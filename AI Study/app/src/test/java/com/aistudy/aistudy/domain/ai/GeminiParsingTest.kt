package com.aistudy.aistudy.domain.ai

import com.aistudy.aistudy.domain.model.enums.EducationLevel
import com.aistudy.aistudy.domain.model.enums.QuestionType
import com.aistudy.aistudy.domain.model.enums.Subject
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Parse JSON cua Gemini la noi de sinh loi vi no phu thuoc vao chuoi model
 * tra ve. Cac ca day khoa cac thay doi nho cua prompt.
 */
class GeminiParsingTest {

    private val client = GeminiAiClient(apiKey = "test-key")

    private val fullAnswerJson = """
        {
          "subject": "science",
          "steps": [
            {
              "title": "Xác định dữ kiện",
              "body": "Đề cho **lực** và khối lượng.",
              "expr": "F = m \cdot a"
            },
            {
              "body": "Thay số vào công thức."
            }
          ],
          "finalAnswer": "Gia tốc là 3 m/s²."
        }
    """.trimIndent()

    private val expectedMarkdown = """
        ### Bước 1: Xác định dữ kiện

        Đề cho **lực** và khối lượng.

        $$
        F = m \cdot a
        $$

        ### Bước 2

        Thay số vào công thức.

        ---

        ## Đáp án

        Gia tốc là 3 m/s².
    """.trimIndent()

    @Test
    fun `doc duoc schema steps va finalAnswer`() {
        val answer = client.parseAnswer(
            text = "```json\n$fullAnswerJson\n```",
            fallbackSubject = Subject.MATHEMATICS,
            questionText = "Một vật khối lượng 2 kg chịu lực 6 N. Tính gia tốc."
        )

        assertEquals(Subject.SCIENCE, answer.subject)
        assertNull("schema moi khong con difficulty", answer.difficulty)
        assertEquals(expectedMarkdown, answer.answerText)
    }

    @Test
    fun `doc duoc JSON khong co fences va co text bao quanh`() {
        val answer = client.parseAnswer(
            text = "Day la loi giai cua ban:\n$fullAnswerJson\n\nHet.",
            fallbackSubject = Subject.MATHEMATICS,
            questionText = "Một vật khối lượng 2 kg chịu lực 6 N. Tính gia tốc."
        )
        assertEquals(expectedMarkdown, answer.answerText)
    }

    @Test
    fun `cau hoi tieng Anh dung nhan tieng Anh`() {
        val answer = client.parseAnswer(
            text = fullAnswerJson,
            fallbackSubject = Subject.MATHEMATICS,
            questionText = "A 2 kg object is pushed with 6 N. Find its acceleration."
        )

        assertTrue(answer.answerText.contains("### Step 1: "))
        assertTrue(answer.answerText.contains("## Final answer"))
        assertTrue(!answer.answerText.contains("## Đáp án"))
    }

    @Test
    fun `thieu ca steps va finalAnswer phai nem Malformed`() {
        val error = runCatching {
            client.parseAnswer("""{"subject":"math"}""", Subject.MATHEMATICS)
        }.exceptionOrNull()

        assertTrue("phai la AiException.Malformed", error is AiException.Malformed)
    }

    @Test
    fun `chi co finalAnswer van hop le`() {
        val answer = client.parseAnswer(
            """{"finalAnswer":"42"}""",
            Subject.MATHEMATICS
        )

        assertEquals("## Đáp án\n\n42", answer.answerText)
    }

    @Test
    fun `subject la thi fallback ve mon nguoi dung chon`() {
        val answer = client.parseAnswer(
            """{"steps":[{"body":"x"}],"subject":"other"}""",
            Subject.PROGRAMMING
        )

        assertEquals(
            "subject la optional nen phai dung gia tri fallback",
            Subject.PROGRAMMING,
            answer.subject
        )
    }

    @Test
    fun `subject code duoc map sang PROGRAMMING`() {
        val answer = client.parseAnswer(
            """{"steps":[{"body":"x"}],"subject":"code"}""",
            Subject.MATHEMATICS
        )

        assertEquals(Subject.PROGRAMMING, answer.subject)
    }

    @Test
    fun `subject math duoc map sang MATHEMATICS`() {
        val answer = client.parseAnswer(
            """{"steps":[{"body":"x"}],"subject":"math"}""",
            Subject.HISTORY
        )

        assertEquals(Subject.MATHEMATICS, answer.subject)
    }

    @Test
    fun `expr co san dau dollar khong bi nhan doi`() {
        val answer = client.parseAnswer(
            """{"steps":[{"body":"Tinh","expr":"${'$'}x^2${'$'}"}],"finalAnswer":"x"}""",
            Subject.MATHEMATICS
        )

        assertTrue(answer.answerText.contains("$$\nx^2\n$$"))
        assertTrue("khong duoc co ba dau dollar", !answer.answerText.contains("$$$"))
    }

    @Test
    fun `nhan dien ngon ngu cau hoi`() {
        assertTrue(answerLanguageIsVietnamese("Giải phương trình bậc hai"))
        assertTrue(answerLanguageIsVietnamese("Giai phuong trinh 2x + 3 = 7"))
        assertTrue(answerLanguageIsVietnamese("2 + 2 = ?"))
        assertTrue(answerLanguageIsVietnamese("AND khác với ARN như thế nào"))

        assertTrue(!answerLanguageIsVietnamese("Solve 2x + 3 = 7"))
        assertTrue(!answerLanguageIsVietnamese("What is the difference between DNA and RNA?"))
    }

    @Test
    fun `khong co JSON nao trong text phai nem Malformed`() {
        val error = runCatching {
            client.parseAnswer("Xin loi, toi khong the tra loi cau nay.", Subject.MATHEMATICS)
        }.exceptionOrNull()

        assertTrue(error is AiException.Malformed)
    }

    @Test
    fun `parseQuizDrafts doc duoc object boc mang trong questions`() {
        val drafts = client.parseQuizDrafts(
            """
            ```json
            {"questions":[
              {"questionType":"MULTIPLE_CHOICE","prompt":"2+2=?","correctAnswer":"A",
               "optionA":"4","optionB":"5","optionC":"6","optionD":"7","explanation":"Cong don"},
              {"questionType":"FILL_BLANK","prompt":"Dat vao cho trong","correctAnswer":"luc"}
            ]}
            ```
            """.trimIndent()
        )

        assertEquals(2, drafts.size)
        assertEquals(QuestionType.MULTIPLE_CHOICE, drafts[0].questionType)
        assertEquals("A", drafts[0].correctAnswer)
        assertEquals("4", drafts[0].optionA)
        assertEquals("7", drafts[0].optionD)
        assertNull("SHORT_ANSWER khong co option", drafts[1].optionA)
        assertEquals(QuestionType.FILL_BLANK, drafts[1].questionType)
    }

    @Test
    fun `parseQuizDrafts doc duoc mang thang`() {
        val drafts = client.parseQuizDrafts(
            """[{"questionType":"SHORT_ANSWER","prompt":"Giai thich","correctAnswer":"Vi co x"}]"""
        )
        assertEquals(1, drafts.size)
        assertEquals(QuestionType.SHORT_ANSWER, drafts[0].questionType)
    }

    @Test
    fun `mang rong phai nem Malformed thay vi tra list rong`() {
        val error = runCatching { client.parseQuizDrafts("""{"questions":[]}""") }.exceptionOrNull()
        assertTrue(error is AiException.Malformed)
    }

    @Test
    fun `cau thieu correctAnswer phai nem Malformed`() {
        val error = runCatching {
            client.parseQuizDrafts("""[{"questionType":"SHORT_ANSWER","prompt":"p"}]""")
        }.exceptionOrNull()
        assertTrue(error is AiException.Malformed)
    }

    @Test
    fun `questionType la khoa deu thi fallback sang SHORT_ANSWER`() {
        val drafts = client.parseQuizDrafts(
            """[{"questionType":"TU_KHON_BIET","prompt":"p","correctAnswer":"c"}]"""
        )
        assertEquals(QuestionType.SHORT_ANSWER, drafts[0].questionType)
    }

    @Test
    fun `isConfigured true khi co api key`() {
        assertTrue(GeminiAiClient(apiKey = "abc").isConfigured)
    }

    @Test
    fun `extractJson uu tien noi dung trong fences`() {
        val extracted = extractJson("```json\n{\"a\":1}\n```")
        assertEquals("""{"a":1}""", extracted)
    }

    @Test
    fun `AiRequest so sanh theo noi dung anh, khong theo dia chi mang`() {
        val a = AiRequest(
            questionText = "q",
            imagePath = "p",
            imageBytes = byteArrayOf(1, 2, 3),
            level = EducationLevel.HIGH
        )
        val b = a.copy(imageBytes = byteArrayOf(1, 2, 3))
        val c = a.copy(imageBytes = byteArrayOf(3, 2, 1))

        assertEquals(a, b)
        assertEquals(a.hashCode(), b.hashCode())
        assertTrue("anh khac thi phai khac request", a != c)
    }

    @Test
    fun `usageTokens doc prompt va candidates tu response`() {
        val root = JSONObject(
            """{"usageMetadata":{"promptTokenCount":321,"candidatesTokenCount":123}}"""
        )

        assertEquals(321 to 123, usageTokens(root))
    }

    @Test
    fun `usageTokens thieu metadata tra ve 0 khong crash`() {
        assertEquals(0 to 0, usageTokens(JSONObject("""{"candidates":[]}""")))
    }

    @Test
    fun `parseAnswer giu token usage de luu xuong DB`() {
        val answer = client.parseAnswer(
            text = fullAnswerJson,
            fallbackSubject = Subject.MATHEMATICS,
            inputTokens = 321,
            outputTokens = 123
        )

        assertEquals(321, answer.inputTokens)
        assertEquals(123, answer.outputTokens)
    }

    @Test
    fun `repairLatexEscapes giu lenh frac va to bi JSON an`() {
        val raw = """{"answerText":"${'$'}\frac{1}{2}${'$'} va ${'$'}x \to 0${'$'}"}"""

        val parsed = JSONObject(repairLatexEscapes(raw))

        assertEquals(
            "\$\\frac{1}{2}\$ va \$x \\to 0\$",
            parsed.optString("answerText")
        )
    }

    @Test
    fun `repairLatexEscapes khong pha escape xuong dong that`() {
        val raw = """{"answerText":"line1\nline2"}"""

        val parsed = JSONObject(repairLatexEscapes(raw))

        assertEquals("line1\nline2", parsed.optString("answerText"))
    }

    @Test
    fun `parseAnswer sua form feed do JSON parse sai`() {
        val raw = "{\"steps\":[{\"body\":\"x = \u000Crac{1}{2}\"}],\"finalAnswer\":\"x\"}"

        val answer = client.parseAnswer(raw, Subject.MATHEMATICS, questionText = "Tính x")

        assertTrue(answer.answerText.contains("x = \\frac{1}{2}"))
    }
}
