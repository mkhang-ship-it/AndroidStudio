package com.aistudy.aistudy.domain.rules

import com.aistudy.aistudy.domain.model.enums.Subject

/**
 * Nhan dien mon hoc tu noi dung cau hoi (FR3).
 *
 * Dung chung cho repository (doan khi luu) va UI (quyet dinh co hien chip
 * mon o History khong). Pure Kotlin, khong android import.
 *
 * Quy tac khop:
 * - Van ban da chuan hoa (bo dau, viet thuong) roi tach theo tu.
 * - Tu don chi khop nguyen tu ("sinh" khong duoc khop "hoc sinh",
 *   "hoa" khong duoc khop "van hoa", "cu" khong duoc khop "cua").
 * - Cum nhieu tu khop dang contains ("phuong trinh", "quang hop").
 * - Mon nao nhieu diem nhat thang; hoa diem hoac 0 diem -> null (khong ro).
 */
object SubjectDetector {

    fun detect(text: String): Subject? {
        val normalised = QuestionSimilarity.normalise(text)
        if (normalised.isBlank()) return null
        val tokens = normalised.split(' ').filter { it.isNotBlank() }.toSet()
        if (tokens.isEmpty()) return null

        var best: Subject? = null
        var bestScore = 0
        for ((subject, keywords) in SUBJECT_KEYWORDS) {
            var score = 0
            for (kw in keywords) {
                if (' ' in kw) {
                    if (normalised.contains(kw)) score += 2
                } else if (kw in tokens) {
                    score += 2
                }
            }
            if (score > bestScore) {
                bestScore = score
                best = subject
            } else if (score == bestScore && score > 0) {
                best = null
            }
        }
        return best
    }

    val SUBJECT_KEYWORDS: Map<Subject, List<String>> = mapOf(
        Subject.MATHEMATICS to listOf(
            "toan", "phuong trinh", "he phuong trinh", "bat phuong trinh",
            "dao ham", "tich phan", "nguyen ham", "gioi han", "logarit",
            "hinh hoc", "dai so", "giai tich", "xac suat", "thong ke",
            "phan so", "phan tram", "dien tich", "the tich", "chu vi",
            "tam giac", "hinh tron", "hinh vuong", "hinh chu nhat",
            "can bac", "to hop", "chinh hop", "cap so", "day so",
            "bieu thuc", "rut gon", "chung minh", "tim x",
            "goc vuong", "duong tron", "hinh thang", "hinh binh hanh"
        ),
        Subject.SCIENCE to listOf(
            "vat ly", "hoa hoc", "sinh hoc", "khoa hoc",
            "nguyen tu", "phan tu", "te bao", "adn", "arn",
            "nhiem sac the", "di truyen", "tien hoa", "quang hop",
            "he tieu hoa", "he tuan hoan", "he ho hap",
            "luc hap dan", "luc day", "luc keo", "luc ma sat",
            "van toc", "khoi luong", "nang luong", "hat nhan",
            "dong dien", "mach dien", "dien truong",
            "phan ung", "axit", "bazo", "chat hoa hoc",
            "am thanh", "anh sang", "nhiet do", "ap suat",
            "protein", "vitamin", "co the nguoi", "moi truong"
        ),
        Subject.PROGRAMMING to listOf(
            "lap trinh", "code", "coding", "thuat toan",
            "python", "java", "javascript", "typescript", "kotlin",
            "swift", "sql", "html", "css", "react", "flutter",
            "android studio", "github", "api", "json", "bug",
            "debug", "bien so", "vong lap", "chuong trinh",
            "backend", "frontend", "co so du lieu", "he quan tri",
            "csharp", "cpp", "fsharp", "golang", "go", "rust", "php",
            "ruby", "scala", "dotnet", "nodejs"
        ),
        Subject.HISTORY to listOf(
            "lich su", "chien tranh", "the chien", "cach mang",
            "khoi nghia", "chien dich", "trieu dai", "phong kien",
            "thuc dan", "thoi dai", "su kien", "van minh",
            "co dai", "trung dai", "hien dai",
            "vua hung", "nha ly", "nha tran", "nha nguyen"
        ),
        Subject.LANGUAGE to listOf(
            "ngu phap", "tu vung", "chinh ta", "phat am",
            "van hoc", "tac pham", "bai tho", "viet van",
            "luyen viet", "doc hieu", "ngu nghia",
            "thanh ngu", "tuc ngu", "cau truc cau",
            "hien tai don", "qua khu don", "tuong lai don",
            "tieng anh", "tieng viet", "ngoai ngu",
            "cam nhan", "nghi luan"
        )
    )
}
