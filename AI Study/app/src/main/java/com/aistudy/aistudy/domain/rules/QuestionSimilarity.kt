package com.aistudy.aistudy.domain.rules

import java.text.Normalizer
import java.security.MessageDigest
import java.util.Locale

/**
 * Chuẩn hoá văn bản + hash + đo độ giống nhau — phần lõi của cơ chế
 * **AI Cost Management** mà brief bắt buộc ("Reduce repeated AI calls through
 * caching", "Detect similar questions and reuse answers").
 *
 * Thuần Kotlin, không phụ thuộc Android ⇒ test được bằng JUnit thuần.
 * Ba ca kiểm thử quan trọng nhất của cả dự án chạy ở đây: TC-ASK-02/03/04.
 */
object QuestionSimilarity {

    /**
     * Ngưỡng **bao phủ** để coi là cùng một câu hỏi.
     *
     * Cao quá thì miss nhiều câu nên lãng phí tiền; thấp quá thì trả nhầm câu trả
     * lời của câu hỏi khác — rủi ro nghiêm trọng hơn nhiều với app học tập.
     */
    const val SIMILARITY_THRESHOLD = 0.85

    /**
     * Ngưỡng chặn kép bên dưới.
     *
     * Vì sao cần: [containment] = |A ∩ B| / min(|A|,|B|) rất dễ cho điểm cao khi
     * câu ngắn nằm trọn trong câu dài. Một câu hỏi 3 từ nằm trong một câu 50 từ sẽ
     * ra 1.0 — sai. Vì vậy bắt buộc [jaccard] cũng phải đạt ngưỡng riêng, nếu không
     * app sẽ trả nhầm câu trả lời.
     */
    const val MIN_JACCARD_GUARD = 0.6

    const val MAX_QUESTION_LENGTH = 2000

    /**
     * Từ dừng, đã **bỏ dấu** (vì [normalise] cũng bỏ dấu).
     *
     * Bắt buộc phải có tiếng Việt: app phục vụ học sinh Việt Nam, mà nếu thiếu
     * "cua" thì hai cách hỏi cùng một bài chỉ khớp được ~0.8 — tức là **tốn tiền
     * gọi AI oan** cho câu đã hỏi rồi.
     *
     * Cố ý KHÔNG đưa vào các từ mang nội dung như "tinh" (tính), "ham" (hàm),
     * "phuong" (phương) — đó là từ khoá toán, bỏ đi thì phép so khớp hỏng.
     */
    private val STOP_WORDS = setOf(
        // ----- tiếng Anh -----
        "a", "an", "the", "is", "are", "was", "were", "be", "been", "being",
        "of", "in", "on", "at", "to", "for", "with", "and", "or", "but", "if",
        "what", "how", "why", "when", "where", "which", "who", "do", "does",
        "did", "can", "could", "should", "would", "will", "this", "that", "it",
        // ----- tiếng Việt (đã bỏ dấu) -----
        "cua", "cho", "voi", "va", "hoac", "nhung", "la", "co", "khong", "duoc",
        "cac", "mot", "nay", "do", "khi", "nao", "tai", "ve", "tu", "den",
        "de", "nhu", "thi", "ma", "ai", "gi", "bao", "nhieu", "gio", "vi", "nen",
        "da", "dang", "se", "hay", "hon", "nhat", "rat", "qua", "cung", "chi",
        "chinh", "ca", "ra", "vao", "len", "xuong", "lai", "nua", "them",
        "moi", "mo", "tat", "cu", "the", "du", "thuoc", "trong", "ngoai",
        "giup", "tim", "xem", "noi", "biet", "ban", "minh", "chung", "toan"
    )

    /** Giữ lại vài token 1 ký tự vì chúng là tên ngôn ngữ (C, R). */
    private val SINGLE_CHAR_TOKENS = setOf("c", "r")

    /**
     * Token quyết định ngữ cảnh: con số và tên ngôn ngữ lập trình.
     * "hàm số trong Java" khác "hàm số trong C#", "x^2" khác "x^3" — không được
     * tái dùng đáp án chỉ vì phần chữ giống nhau.
     */
    private val KEY_TOKENS = setOf(
        "java", "csharp", "cpp", "fsharp", "python", "kotlin", "javascript",
        "typescript", "swift", "sql", "html", "css", "react", "flutter",
        "golang", "go", "rust", "php", "ruby", "scala", "dotnet", "nodejs",
        "c", "r"
    )

    /** Đổi tên ngôn ngữ có ký hiệu thành token chữ trước khi bỏ dấu câu. */
    private val LANGUAGE_ALIASES = listOf(
        Regex("(?<![\\p{L}\\p{N}])c\\s*#") to "csharp",
        Regex("(?<![\\p{L}\\p{N}])c\\s*\\+\\+") to "cpp",
        Regex("(?<![\\p{L}\\p{N}])f\\s*#") to "fsharp",
        Regex("\\.net") to "dotnet"
    )

    private fun keyTokens(text: String): Set<String> = tokenize(text)
        .filterTo(mutableSetOf()) { token ->
            token in KEY_TOKENS || token.any { ch -> ch.isDigit() }
        }

    /**
     * Bỏ dấu tiếng Việt: "Tính đạo hàm" → "Tinh dao ham".
     *
     * Cần thiết vì học sinh gõ trên điện thoại rất dễ bỏ dấu. Nếu giữ dấu thì
     * "tính đạo hàm" và "tinh dao ham" ra hai hash khác nhau ⇒ cache trượt ⇒ mất tiền
     * gọi AI cho câu đã hỏi (TC-ASK-02).
     *
     * Lưu ý: `Normalizer` NFD **không** tách được `đ`/`Đ` — đó là ký tự dựng sẵn
     * mang nét gạch chứ không phải dấu thanh, nên phải thay thẳng. Thiếu bước này
     * thì "đạo hàm" và "dao ham" vẫn ra hai hash khác nhau.
     */
    fun stripDiacritics(text: String): String {
        val preMapped = text.replace('đ', 'd').replace('Đ', 'D')
        val decomposed = Normalizer.normalize(preMapped, Normalizer.Form.NFD)
        val stripped = StringBuilder(decomposed.length)
        for (ch in decomposed) {
            if (Character.getType(ch) != Character.NON_SPACING_MARK.toInt()) {
                stripped.append(ch)
            }
        }
        return Normalizer.normalize(stripped, Normalizer.Form.NFC)
    }

    /**
     * Chuẩn hoá văn bản cho cả hash lẫn so khớp mờ. Thứ tự các bước rất quan trọng:
     * phải bỏ dấu **trước** khi so khớp với STOP_WORDS, vì danh sách từ dừng cũng
     * đã bỏ dấu.
     */
    fun normalise(text: String): String {
        var prepared = stripDiacritics(text).lowercase(Locale.ROOT)
        // "C#"/"C++" bi bo dau cau thanh "c" (1 ky tu) va bi tokenize loai bo,
        // khien cau hoi ve C#/C++ bi coi la trung voi cau hoi Java. Doi ten
        // ngon ngu thanh token on dinh truoc khi bo dau cau.
        LANGUAGE_ALIASES.forEach { (pattern, replacement) ->
            prepared = pattern.replace(prepared, replacement)
        }
        return prepared
            .replace(PUNCTUATION, " ")
            .replace(WHITESPACE, " ")
            .trim()
    }

    fun hashOf(text: String): String {
        val normalised = normalise(text)
        val digest = MessageDigest.getInstance("SHA-256").digest(normalised.toByteArray(Charsets.UTF_8))
        return digest.joinToString(separator = "") { byte -> "%02x".format(byte) }
    }

    fun tokenize(text: String): Set<String> = normalise(text)
        .split(" ")
        .asSequence()
        // Giu token 1 ky tu khi la ten ngon ngu (C/R) hoac chu so (0-9):
        // "x^2 ... = 0" va "= 1" la hai bai khac nhau.
        .filter { it.length > 1 || it in SINGLE_CHAR_TOKENS || it.firstOrNull()?.isDigit() == true }
        .filterNot { it in STOP_WORDS }
        .toSet()

    /**
     * Hệ số Jaccard = |A ∩ B| / |A ∪ B|.
     *
     * Hai chuỗi rỗng được coi là giống hệt (0/0 → 0.5) thay vì 0.0 để tránh
     * một câu hỏi toàn ký hiệu tạo ra "không giống" với chính nó.
     */
    fun jaccard(a: String, b: String): Double {
        val setA = tokenize(a)
        val setB = tokenize(b)
        if (setA.isEmpty() && setB.isEmpty()) return 0.5
        if (setA.isEmpty() || setB.isEmpty()) return 0.0
        val intersection = setA.count { it in setB }.toDouble()
        val union = (setA.size + setB.size - intersection).toDouble()
        return if (union == 0.0) 0.0 else intersection / union
    }

    fun isSimilar(a: String, b: String, threshold: Double = SIMILARITY_THRESHOLD): Boolean =
        containment(a, b) >= threshold &&
            jaccard(a, b) >= MIN_JACCARD_GUARD &&
            keyTokens(a) == keyTokens(b)

    /**
     * Hệ số bao phủ = |A ∩ B| / min(|A|, |B|).
     *
     * Dùng để chịu được câu hỏi có thêm vài từ chức năng: "Tính đạo hàm của hàm số
     * f(x) = x^2" và "Tính đạo hàm hàm số f(x) bằng x^2" chỉ khác một từ "bằng", nên
     * Jaccard thuần chỉ ra 0.8 (dưới ngưỡng ⇒ tốn tiền gọi AI lại), còn hệ số này
     * ra 1.0. Cặp với [jaccard] làm ngưỡng chặn kép tránh trả nhầm.
     */
    fun containment(a: String, b: String): Double {
        val setA = tokenize(a)
        val setB = tokenize(b)
        if (setA.isEmpty() || setB.isEmpty()) return 0.0
        val intersection = setA.count { it in setB }.toDouble()
        val smaller = minOf(setA.size, setB.size).toDouble()
        return intersection / smaller
    }

    /** Chuỗi hợp lệ để gửi cho AI. Rỗng hoặc quá dài đều bị chặn (TC-ASK-07, TC-ASK-08/09). */
    fun isValidQuestionText(text: String): Boolean =
        text.isNotBlank() && text.length <= MAX_QUESTION_LENGTH

    private val PUNCTUATION = Regex("[^\\p{L}\\p{N}]+")
    private val WHITESPACE = Regex("\\s+")
}
