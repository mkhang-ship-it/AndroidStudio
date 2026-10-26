package com.aistudy.aistudy.domain.ai

import android.util.Base64
import android.util.Log
import com.aistudy.aistudy.BuildConfig
import com.aistudy.aistudy.domain.model.entity.QuestionAnswer
import com.aistudy.aistudy.domain.model.enums.EducationLevel
import com.aistudy.aistudy.domain.model.enums.ExplanationStyle
import com.aistudy.aistudy.domain.model.enums.QuestionType
import com.aistudy.aistudy.domain.model.enums.Subject
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

/**
 * FR4 + FR7 - goi Gemini qua REST.
 *
 * Khong dung Retrofit/OkHttp theo quyet dinh D-5 trong design doc: build khong
 * can mang de tai dependency, va khong xung dot version voi AGP 9.3.3. Chi can
 * `HttpURLConnection` + `org.json` (co san trong Android) la du.
 *
 * Model duoc yeu cau tra ve JSON. Gemini hay boc JSON trong ```json fences,
 * nen [extractJson] cat phan giuaa hai dau ``` truoc khi parse.
 */
class GeminiAiClient(
    private val apiKey: String,
    private val model: String = DEFAULT_MODEL
) : AiClient {

    override val isConfigured: Boolean = apiKey.isNotBlank()

    override suspend fun answer(request: AiRequest): Result<AiAnswer> = withContext(Dispatchers.IO) {
        runCatching {
            val body = JSONObject().apply {
                put(
                    "contents",
                    JSONArray().put(
                        JSONObject().apply {
                            put("role", "user")
                            put(
                                "parts",
                                JSONArray().apply {
                                    put(JSONObject().put("text", answerPrompt(request)))
                                    request.imageBytes
                                        ?.let { bytes ->
                                            put(
                                                JSONObject().put(
                                                    "inline_data",
                                                    JSONObject()
                                                        .put("mime_type", "image/jpeg")
                                                        .put(
                                                            "data",
                                                            Base64.encodeToString(bytes, Base64.NO_WRAP)
                                                        )
                                                )
                                            )
                                        }
                                }
                            )
                        }
                    )
                )
                put(
                    "generationConfig",
                    JSONObject()
                        .put("responseMimeType", "application/json")
                        .put("temperature", 0.3)
                        .put("maxOutputTokens", 4096)
                )
            }

            val root = post(body)
            val text = extractText(root)
            val (inputTokens, outputTokens) = usageTokens(root)
            parseAnswer(
                text = text,
                fallbackSubject = request.subject,
                questionText = request.questionText,
                inputTokens = inputTokens,
                outputTokens = outputTokens
            )
        }.recoverCatching { error -> throw error.toAiError() }
    }

    override suspend fun generateQuiz(
        source: QuestionAnswer,
        count: Int,
        level: EducationLevel,
        style: ExplanationStyle
    ): Result<List<QuizDraft>> = withContext(Dispatchers.IO) {
        runCatching {
            val body = JSONObject().apply {
                put(
                    "contents",
                    JSONArray().put(
                        JSONObject().apply {
                            put("role", "user")
                            put(
                                "parts",
                                JSONArray().put(
                                    JSONObject().put("text", quizPrompt(source, count, level, style))
                                )
                            )
                        }
                    )
                )
                put(
                    "generationConfig",
                    JSONObject()
                        .put("responseMimeType", "application/json")
                        .put("temperature", 0.7)
                        .put("maxOutputTokens", 4096)
                )
            }

            val root = post(body)
            parseQuizDrafts(extractText(root))
        }.recoverCatching { error -> throw error.toAiError() }
    }

    private fun post(body: JSONObject): JSONObject {
        val maxRetries = 2
        var delayMs = 1000L
        val requestBody = body.toString()
        val endpoint = "$BASE_URL/models/$model:generateContent"

        for (attempt in 0..maxRetries) {
            val connection = (URL(endpoint).openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = TIMEOUT_MS
                readTimeout = TIMEOUT_MS
                doOutput = true
                setRequestProperty("Content-Type", "application/json; charset=utf-8")
                setRequestProperty("x-goog-api-key", apiKey)
            }

            try {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "POST $endpoint (attempt ${attempt + 1})")
                    logLong("→ REQUEST BODY", sanitizeRequestBody(requestBody))
                }
                connection.outputStream.use { it.write(requestBody.toByteArray(Charsets.UTF_8)) }

                val code = connection.responseCode
                val stream = if (code in 200..299) connection.inputStream else connection.errorStream
                val raw = stream?.bufferedReader()?.use { it.readText() }.orEmpty()

                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "← HTTP $code responseLen=${raw.length}")
                    logLong("← RESPONSE BODY", raw)
                }

                if (code in 200..299) {
                    return JSONObject(raw)
                }

                // Temporary server busy or rate limit error (503, 429, 500, 502, 504) -> retry with exponential delay
                if ((code == 503 || code == 429 || code in 500..504) && attempt < maxRetries) {
                    try {
                        Thread.sleep(delayMs)
                    } catch (_: InterruptedException) {}
                    delayMs *= 2
                    continue
                }

                val msg = extractError(raw)
                val userFriendlyMessage = when (code) {
                    503 -> "Máy chủ AI hiện đang quá tải (High Demand). Vui lòng thử lại sau vài giây."
                    429 -> "Đã vượt quá giới hạn yêu cầu (Rate Limit). Vui lòng thử lại sau."
                    else -> "Gemini HTTP $code - $msg"
                }
                throw AiException.Provider(userFriendlyMessage)
            } finally {
                connection.disconnect()
            }
        }

        throw AiException.Provider("Máy chủ AI hiện đang quá tải (HTTP 503). Vui lòng thử lại sau.")
    }

    private fun extractText(root: JSONObject): String {
        val candidates = root.optJSONArray("candidates")
            ?: throw AiException.Malformed("Gemini tra ve khong co candidates")
        val first = candidates.optJSONObject(0)
            ?: throw AiException.Malformed("Gemini tra ve candidates rong")
        if (first.optString("finishReason").uppercase() == SAFETY_FINISH_REASON) {
            throw AiException.Provider("Yeu cau bi chan boi bo loc an toan cua Gemini")
        }
        val parts = first.optJSONObject("content")?.optJSONArray("parts")
            ?: throw AiException.Malformed("Gemini tra ve khong co parts")
        val text = (0 until parts.length())
            .mapNotNull { parts.optJSONObject(it)?.optString("text") }
            .joinToString("\n")
        if (text.isBlank()) throw AiException.Malformed("Gemini tra ve noi dung rong")
        if (BuildConfig.DEBUG) {
            Log.d(
                LOG_TAG,
                "textLen=${text.length} finish=${first.optString("finishReason")} " +
                    "tail=${text.takeLast(160)}"
            )
        }
        return text
    }

    private fun extractError(raw: String): String = runCatching {
        JSONObject(raw).optJSONObject("error")?.optString("message").orEmpty()
    }.getOrNull()?.takeIf { it.isNotBlank() } ?: raw.take(200)

    private fun answerPrompt(request: AiRequest): String {
        val levelText = when (request.level) {
            EducationLevel.MIDDLE -> "lower secondary school"
            EducationLevel.HIGH -> "upper secondary school"
            EducationLevel.UNIVERSITY -> "undergraduate"
        }
        val styleText = when (request.style) {
            ExplanationStyle.SHORT ->
                "Keep the explanation short: at most five lines in total."
            ExplanationStyle.DETAILED ->
                "Explain in detail and add a worked numeric example when useful."
            ExplanationStyle.STEP_BY_STEP ->
                "Explain step by step, one idea per step."
        }
        val imageNote = if (request.imageBytes != null) {
            " The question includes an attached photo of the exercise."
        } else {
            ""
        }
        val subjectHint = when (request.subject) {
            Subject.MATHEMATICS -> "math"
            Subject.SCIENCE -> "science"
            Subject.PROGRAMMING -> "code"
            Subject.HISTORY -> "history"
            Subject.LANGUAGE -> "language"
        }

        return """
            You are AI Study Mentor, a study tutor for a student at $levelText level.
            $styleText
            Always answer in the same language as the question.
            If the question is Vietnamese, write Vietnamese with full diacritics in every field (title, body, finalAnswer), even when the question is typed without diacritics.
            Put math formulas in the "expr" field of the matching step.
            Classify the subject correctly (math/science/code/history/language/other). The current hint is "$subjectHint"; keep it unless the question clearly belongs elsewhere.
            Questions about functions, methods, syntax or behaviour in a programming language (Java, C#, C++, Python, Kotlin, Go...) are "code", even when the Vietnamese word "hàm" appears.$imageNote

            Return ONLY valid JSON (no markdown) with this structure:
            {
              "subject": "math|science|code|history|language|other",
              "steps": [{"title": "optional", "body": "required", "expr": "optional"}],
              "finalAnswer": "string"
            }

            Question: ${request.questionText}
        """.trimIndent()
    }

    private fun quizPrompt(
        source: QuestionAnswer,
        count: Int,
        level: EducationLevel,
        style: ExplanationStyle
    ): String = """
        Tao $count cau hoi luyen tap tu bai hoc duoi day. Mon ${source.subjectCode.name}.
        Trinh do ${level.name}, phong cach ${style.name}.

        Tra ve DUY NHAT mot JSON, phan tuoi la mang chua "questions":
        {
          "questions": [
            {
              "questionType": "MULTIPLE_CHOICE" | "SHORT_ANSWER" | "FILL_BLANK",
              "prompt": "noi dung cau hoi",
              "correctAnswer": "dap an dung",
              "optionA": "chi voi MULTIPLE_CHOICE",
              "optionB": "chi voi MULTIPLE_CHOICE",
              "optionC": "chi voi MULTIPLE_CHOICE",
              "optionD": "chi voi MULTIPLE_CHOICE",
              "explanation": "giai thich ngan gon"
            }
          ]
        }

        Bai hoc:
        ${source.questionText}
        Loi giai co san: ${source.answerText.take(600)}
    """.trimIndent()

    internal fun parseAnswer(
        text: String,
        fallbackSubject: Subject,
        questionText: String = "",
        inputTokens: Int = 0,
        outputTokens: Int = 0
    ): AiAnswer {
        val obj = jsonCandidates(text).firstNotNullOfOrNull { candidate ->
            runCatching { JSONObject(candidate) }
                .recoverCatching { JSONObject(escapeInnerControlChars(candidate)) }
                .getOrNull()
                ?.takeIf { it.hasAnswerPayload() }
        } ?: throw AiException.Malformed("Khong parse duoc JSON tu Gemini")

        val answerText = stepsToMarkdown(
            obj = obj,
            vietnamese = answerLanguageIsVietnamese(questionText)
        )
        if (answerText.isBlank()) {
            throw AiException.Malformed("Gemini tra ve steps/finalAnswer rong")
        }

        return AiAnswer(
            answerText = answerText,
            subject = subjectFromCode(obj.optString("subject")) ?: fallbackSubject,
            // Schema moi khong con difficulty; UI tu an chip khi null.
            difficulty = null,
            inputTokens = inputTokens,
            outputTokens = outputTokens
        )
    }

    internal fun parseQuizDrafts(text: String): List<QuizDraft> {
        // Gemini hoac tra mang thang, hoac tra object boc mang trong "questions".
        // Thu object truoc: text xung quanh JSON that co the chua ngoac cua
        // vi du code, khien nhan mang thang nham.
        val array = jsonCandidates(text).firstNotNullOfOrNull { candidate ->
            quizArrayFrom(candidate) ?: quizArrayFrom(escapeInnerControlChars(candidate))
        } ?: throw AiException.Malformed("Khong parse duoc danh sach cau hoi tu Gemini")

        if (array.length() == 0) {
            throw AiException.Malformed("Gemini tra ve khong cau hoi nao")
        }

        return (0 until array.length()).map { index ->
            val item = array.optJSONObject(index)
                ?: throw AiException.Malformed("Cau hoi thu $index khong phai object")
            val prompt = item.optString("prompt").trim()
            val correct = item.optString("correctAnswer").trim()
            if (prompt.isBlank() || correct.isBlank()) {
                throw AiException.Malformed("Cau hoi thu $index thieu prompt hoac correctAnswer")
            }
            QuizDraft(
                questionType = item.optString("questionType")
                    .enumOrNull<QuestionType>() ?: QuestionType.SHORT_ANSWER,
                prompt = prompt,
                correctAnswer = correct,
                optionA = item.optStringOrNull("optionA"),
                optionB = item.optStringOrNull("optionB"),
                optionC = item.optStringOrNull("optionC"),
                optionD = item.optStringOrNull("optionD"),
                explanation = item.optStringOrNull("explanation")
            )
        }
    }

    /**
     * Boc tach mang cau hoi: uu tien object co key "questions", roi moi thu
     * mang thang. Thu tu nay quan trong vi ngoac cua object bao ngoai luon
     * dung ngay ca khi text xung quanh co ngoac cua vi du code.
     */
    private fun quizArrayFrom(json: String): JSONArray? {
        runCatching { JSONObject(json).optJSONArray(KEY_QUESTIONS) }
            .getOrNull()?.let { return it }
        return runCatching { JSONArray(json) }.getOrNull()
    }

    private companion object {
        const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta"
        const val DEFAULT_MODEL = "gemini-3.8-flash"
        const val TIMEOUT_MS = 20_000
        const val SAFETY_FINISH_REASON = "SAFETY"
        const val KEY_QUESTIONS = "questions"    }
}

/**
 * Doc token usage tu response Gemini. Tach rieng de test duoc ma khong can
 * goi mang that — BUG-08 truoc day hardcode `inputTokens = 0`.
 */
internal fun usageTokens(root: JSONObject): Pair<Int, Int> {
    val usage = root.optJSONObject("usageMetadata") ?: return 0 to 0
    return usage.optInt("promptTokenCount", 0) to
        usage.optInt("candidatesTokenCount", 0)
}

/**
 * Gemini doi khi tra JSON chua escape dung cac lenh LaTeX. Vi du `\frac`
 * trong JSON hop le la escape `\f` (form feed) + "rac"; `\to` la tab + "o".
 * Neu khong sua, UI nhan duoc ky tu dieu khien thay vi lenh LaTeX.
 *
 * Ham nay chi sua ben trong chuoi JSON: gap backslash + ten lenh LaTeX da biet
 * thi them mot backslash de JSON tra ve dung `\frac`, `\to`...; cac escape
 * that nhu `\n`, `\t` (khi khong tao thanh lenh LaTeX) duoc giu nguyen.
 */
internal fun repairLatexEscapes(rawJson: String): String {
    val json = repairLatexControlCharEscapes(rawJson)
    val out = StringBuilder(json.length + 32)
    var inString = false
    var i = 0
    while (i < json.length) {
        val ch = json[i]
        if (ch == '"') {
            inString = !inString
            out.append(ch)
            i++
            continue
        }
        if (!inString || ch != '\\') {
            out.append(ch)
            i++
            continue
        }
        if (i + 1 >= json.length) {
            out.append(ch)
            i++
            continue
        }

        val next = json[i + 1]
        if (next == '\\' || next == '"' || next == '/') {
            out.append(ch)
            i++
            continue
        }

        if (next == 'u') {
            val hex = json.substring(i + 2, minOf(i + 6, json.length))
            val validUnicode = hex.length == 4 &&
                hex.all { it.isDigit() || it.lowercaseChar() in 'a'..'f' }
            if (validUnicode) {
                out.append(ch)
                i++
            } else {
                out.append("\\\\")
                i++
            }
            continue
        }

        if (next in "bfnrt") {
            var end = i + 2
            while (end < json.length && json[end].isLetter()) end++
            val word = json.substring(i + 1, end)
            if (word in LATEX_JSON_COMMANDS) {
                out.append("\\\\").append(word)
                i = end
            } else {
                out.append(ch)
                i++
            }
            continue
        }

        // Escape khong hop le trong JSON (vi du \{, \%, \alpha): them backslash.
        out.append("\\\\")
        i++
    }
    return out.toString()
}

private fun repairLatexControlCharEscapes(json: String): String {
    var result = json
    LATEX_CONTROL_ESCAPES.forEach { (broken, tail) ->
        val replacement = "\\\\" + tail
        // Truong hop da co san mot backslash truoc ky tu dieu khien.
        result = result.replace("\\$broken", replacement)
        result = result.replace(broken, replacement)
    }
    return result
}

/** Ban sua cho chuoi da parse (de luu DB / hien thi lai). */
private fun repairStoredLatex(value: String): String {
    var result = value
    LATEX_CONTROL_ESCAPES.forEach { (broken, tail) ->
        val fixed = "\\" + tail
        result = result.replace("\\$broken", fixed)
        result = result.replace(broken, fixed)
    }
    return result
}

/**
 * Cac cap (ky tu dieu khien + phan con lai) do JSON parser tao ra khi model
 * khong escape LaTeX. Thu tu dai -> ngan de khong cat nham.
 */
private val LATEX_CONTROL_ESCAPES = listOf(
    "\u000Dightarrow" to "rightarrow",
    "\u000Dight" to "right",
    "\u000Crac" to "frac",
    "\u000Corall" to "forall",
    "\u0009heta" to "theta",
    "\u0009imes" to "times",
    "\u0009ext" to "text",
    "\u0009an" to "tan",
    "\u0009au" to "tau",
    "\u0009o" to "to",
    "\u0008egin" to "begin",
    "\u0008eta" to "beta",
    "\u0008inom" to "binom",
    "\u0008ar" to "bar",
    "\u000Aabla" to "nabla",
    "\u000Aotin" to "notin",
    "\u000Aeq" to "neq",
    "\u000Dho" to "rho"
)

private val LATEX_JSON_COMMANDS = setOf(
    "frac", "dfrac", "tfrac", "sqrt", "lim", "to", "infty",
    "Delta", "delta", "alpha", "beta", "gamma", "theta", "lambda",
    "mu", "nu", "pi", "rho", "sigma", "omega", "sum", "prod", "int",
    "partial", "nabla", "cdot", "times", "div", "pm", "mp", "le", "leq",
    "ge", "geq", "ne", "neq", "approx", "equiv", "sim", "propto",
    "left", "right", "text", "mathrm", "mathit", "mathbf", "mathbb",
    "operatorname", "begin", "end", "overline", "vec", "hat", "bar",
    "quad", "qquad", "log", "ln", "sin", "cos", "tan", "cot", "sec",
    "csc", "binom", "angle", "degree", "prime", "ldots", "cdots",
    "vdots", "ddots", "subset", "supset", "subseteq", "supseteq",
    "in", "notin", "cup", "cap", "forall", "exists", "emptyset",
    "Rightarrow", "Leftarrow", "leftrightarrow", "Leftrightarrow",
    "mapsto", "ast", "star", "bullet", "cong", "parallel", "perp",
    "therefore", "because", "neg", "land", "lor", "oplus", "otimes"
)

// ---------------------------------------------------------------- debug logging

private const val LOG_TAG = "GeminiAPI"
private const val LOG_CHUNK_SIZE = 3_000

/** Logcat cat message ~4KB, nen chia body thanh tung chunk de khong mat du lieu. */
private fun logLong(label: String, value: String) {
    if (value.length <= LOG_CHUNK_SIZE) {
        Log.d(LOG_TAG, "$label\n$value")
        return
    }
    var index = 0
    var part = 1
    while (index < value.length) {
        val end = minOf(index + LOG_CHUNK_SIZE, value.length)
        Log.d(LOG_TAG, "$label [part $part]\n${value.substring(index, end)}")
        index = end
        part++
    }
}

/** Khong log base64 anh (co the vai tram KB) nhung van giu lai do dai. */
private fun sanitizeRequestBody(body: String): String =
    body.replace(
        Regex("\"data\"\\s*:\\s*\"[A-Za-z0-9+/=\\r\\n]{64,}\""),
        "\"data\":\"<base64 image hidden>\""
    )

/**
 * Cat JSON ra khoi text. Gemini hay boc ket qua trong ```json ... ``` va
 * thuong them mot cau mo dan ngoai, nen can ca `first {` den `last }`.
 */
internal fun extractJson(text: String): String {
    val fenced = FENCE.find(text)?.groupValues?.getOrNull(1)
    if (fenced != null && fenced.isNotBlank()) return fenced.trim()

    // Van ban co the open bang `{` hoac `[`. Phai chon dau ngoac mo ra truoc
    // thoi - neu uu tien `{` thi payload `[{...}]` se bi cat mat vo `[` `]`.
    val objectStart = text.indexOf('{')
    val arrayStart = text.indexOf('[')
    val start = when {
        objectStart < 0 -> arrayStart
        arrayStart < 0 -> objectStart
        else -> minOf(objectStart, arrayStart)
    }
    if (start < 0) throw AiException.Malformed("Khong tim thay JSON trong phan tra loi cua Gemini")

    val end = maxOf(text.lastIndexOf('}'), text.lastIndexOf(']'))
    if (end <= start) throw AiException.Malformed("JSON trong phan tra loi cua Gemini khong dong")

    return text.substring(start, end + 1)
}

// ---------------------------------------------------------------- answer schema

/** Schema moi: steps + finalAnswer. Thieu ca hai coi nhu model tra loi hong. */
private fun JSONObject.hasAnswerPayload(): Boolean =
    (optJSONArray("steps")?.length() ?: 0) > 0 || optString("finalAnswer").isNotBlank()

/**
 * Gop steps thanh markdown de luu vao `answerText` (khong doi schema Room).
 * `expr` duoc dua vao block `$$...$$` de MarkdownText render thanh cong thuc
 * can giua, tach khoi phan chu cua buoc.
 */
private fun stepsToMarkdown(obj: JSONObject, vietnamese: Boolean): String {
    val out = StringBuilder()
    var rendered = 0
    val steps = obj.optJSONArray("steps")

    if (steps != null) {
        for (index in 0 until steps.length()) {
            val step = steps.optJSONObject(index) ?: continue
            val body = repairStoredLatex(step.optString("body").trim())
            val expr = stripMathDelimiters(
                repairStoredLatex(step.optString("expr").trim())
            )
            val title = repairStoredLatex(step.optString("title").trim())
            if (body.isBlank() && expr.isBlank()) continue

            rendered++
            if (out.isNotEmpty()) out.append("\n\n")
            val defaultLabel = if (vietnamese) "Bước $rendered" else "Step $rendered"
            val heading = when {
                title.isBlank() -> defaultLabel
                title.startsWith("Bước", ignoreCase = true) ||
                    title.startsWith("Step", ignoreCase = true) ||
                    title.startsWith("$rendered.") -> title

                else -> "$defaultLabel: $title"
            }
            out.append("### ").append(heading)
            if (body.isNotBlank()) out.append("\n\n").append(body)
            if (expr.isNotBlank()) {
                if (looksLikeCode(expr)) {
                    out.append("\n\n```\n").append(expr).append("\n```")
                } else {
                    out.append("\n\n$$\n").append(expr).append("\n$$")
                }
            }
        }
    }

    val finalAnswer = repairStoredLatex(obj.optString("finalAnswer").trim())
    if (finalAnswer.isNotBlank()) {
        if (out.isNotEmpty()) out.append("\n\n---\n\n")
        out.append("## ").append(if (vietnamese) "Đáp án" else "Final answer")
        out.append("\n\n").append(finalAnswer)
    }
    return out.toString().trim()
}

/** `expr` co the kem san `$...$`, `$$...$$` hoac `\[...\]` - bo vo de khong nhan doi. */
private fun stripMathDelimiters(raw: String): String {
    var value = raw.trim()
    value = when {
        value.length > 4 && value.startsWith("$$") && value.endsWith("$$") ->
            value.substring(2, value.length - 2)

        value.length > 4 && value.startsWith("\\[") && value.endsWith("\\]") ->
            value.substring(2, value.length - 2)

        value.length > 2 && value.startsWith("$") && value.endsWith("$") ->
            value.substring(1, value.length - 1)

        else -> value
    }
    return value.trim()
}

/**
 * Model doi khi dat code (Java/C#/Python) vao `expr`. Neu render nhu cong thuc
 * thi dau `_` thanh subscript va code bi meo, nen dua vao code fence.
 * Cong thuc LaTeX co dau `\` nen duoc loai truoc.
 */
private fun looksLikeCode(expr: String): Boolean {
    if (expr.contains(';') || expr.contains("//") || expr.contains('\n')) return true
    if (expr.contains('{') && expr.contains('}') && expr.contains('(') &&
        !expr.contains('\\')
    ) {
        return true
    }
    return CODE_HINT_REGEX.containsMatchIn(expr)
}

private val CODE_HINT_REGEX = Regex(
    "\\b(public|private|protected|static|class|void|return|def|function|fun|val|var|let|const|using|import|include|console|system|print|println)\\b",
    RegexOption.IGNORE_CASE
)

/**
 * Prompt tra ve `math|science|code|history|language|other`; `other` va gia tri
 * la deu roi ve fallback ma nguoi dung da chon luc hoi.
 */
private fun subjectFromCode(raw: String): Subject? = when (raw.trim().lowercase()) {
    "math", "mathematics", "maths", "toan" -> Subject.MATHEMATICS
    "science", "physics", "chemistry", "biology", "khoa_hoc" -> Subject.SCIENCE
    "code", "programming", "computer_science", "cs" -> Subject.PROGRAMMING
    "history", "lich_su" -> Subject.HISTORY
    "language", "english", "literature", "ngon_ngu" -> Subject.LANGUAGE
    else -> null
}

/**
 * Cau tra loi theo ngon ngu cau hoi. Tieng Viet nhan dien qua dau tieng Viet
 * hoac vai tu khoa ASCII thong dung ("giai", "phuong", "trinh"...). Cau hoi
 * khong co chu (vi du "2 + 2 = ?") mac dinh dung nhan tieng Viet vi UI la
 * tieng Viet.
 */
internal fun answerLanguageIsVietnamese(questionText: String): Boolean {
    val text = questionText.trim()
    if (text.isEmpty()) return true
    if (text.any { it in VIETNAMESE_CHARS }) return true

    val letters = text.filter { it.isLetter() }
    if (letters.isEmpty()) return true
    // Chu khong phai Latin ASCII (Trung/Nhat/Han...) -> khong phai tieng Viet.
    if (letters.none { it.code < 128 }) return false

    return VIETNAMESE_ASCII_REGEX.containsMatchIn(text.lowercase())
}

private const val VIETNAMESE_CHARS =
    "ăâđêôơưàáảãạằắẳẵặầấẩẫậèéẻẽẹềếểễệìíỉĩịòóỏõọồốổỗộờớởỡợùúủũụừứửữựỳýỷỹỵ"

private val VIETNAMESE_ASCII_WORDS = setOf(
    "khong", "duoc", "giup", "giai", "tinh", "phuong", "trinh", "cau", "hoi",
    "bai", "nhu", "nao", "nay", "khi", "voi", "mot", "trong", "cua", "gi"
)

private val VIETNAMESE_ASCII_REGEX =
    Regex("\\b(${VIETNAMESE_ASCII_WORDS.joinToString("|")})\\b")

/**
 * Thu thap tat ca cac doan ung cu cho JSON cua Gemini: moi code fence rieng
 * le (vi du ```javascript hay ```json), roi den khoang tu dau ngoac dau tien
 * den ngoac cuoi cung cua toan van ban. Nhanh nay quan trong vi Gemini hay
 * tra code mau ```javascript ben trai va JSON ben phai - uu tien fence cuoi
 * chua nut cua JSON bang cach thu tung fence qua get key.
 */
internal fun jsonCandidates(text: String): List<String> {
    val fenced = FENCE.findAll(text)
        .map { it.groupValues[1].trim() }
        .filter { it.isNotBlank() }
        .toList()
    val objectStart = text.indexOf('{')
    val arrayStart = text.indexOf('[')
    val start = when {
        objectStart < 0 -> arrayStart
        arrayStart < 0 -> objectStart
        else -> minOf(objectStart, arrayStart)
    }
    val end = maxOf(text.lastIndexOf('}'), text.lastIndexOf(']'))
    val whole = if (start in 0 until end) listOf(text.substring(start, end + 1)) else emptyList()
    return (fenced + whole).distinct().map(::repairLatexEscapes)
}

private val FENCE = Regex("```(?:json)?\\s*([\\s\\S]*?)```", RegexOption.IGNORE_CASE)

/**
 * Gemini hay de nguyen dau xuong dong / tab ben trong gia tri chuoi JSON, do
 * la khong hop le theo dac ta. Ham nay doi cac ky tu dieu khien do thanh
 * escape tuong ung, chi khi nam trong chuoi, de `JSONObject` parse duoc.
 */
internal fun escapeInnerControlChars(json: String): String {
    val out = StringBuilder(json.length)
    var inString = false
    var escaped = false
    for (ch in json) {
        when {
            escaped -> { out.append(ch); escaped = false }
            inString && ch == '\\' -> { out.append(ch); escaped = true }
            ch == '"' -> { inString = !inString; out.append(ch) }
            inString && ch == '\n' -> out.append("\\n")
            inString && ch == '\r' -> Unit
            inString && ch == '\t' -> out.append("\\t")
            else -> out.append(ch)
        }
    }
    return out.toString()
}

private inline fun <reified T : Enum<T>> String.enumOrNull(): T? =
    if (isBlank()) null else runCatching { enumValueOf<T>(trim().uppercase()) }.getOrNull()

/** `optString` tra ve chuoi rong cho ca khi JSON thieu field - phai phan biet. */
private fun JSONObject.optStringOrNull(key: String): String? =
    if (isNull(key)) null else optString(key).trim().takeIf { it.isNotEmpty() }

private fun Throwable.toAiError(): Throwable = when (this) {
    is AiException -> this
    is java.net.UnknownHostException,
    is java.net.ConnectException,
    is java.net.SocketTimeoutException -> AiException.Network(message ?: "Khong ket noi duoc toi may chu AI")

    else -> AiException.Provider(message ?: "Loi khong mong doi: ${this::class.simpleName}")
}
