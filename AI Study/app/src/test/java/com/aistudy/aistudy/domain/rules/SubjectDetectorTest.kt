package com.aistudy.aistudy.domain.rules

import com.aistudy.aistudy.domain.model.enums.Subject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SubjectDetectorTest {
    @Test fun mathDetected() {
        assertEquals(Subject.MATHEMATICS, SubjectDetector.detect("Giải phương trình x^2 + 1 = 0"))
    }

    @Test fun programmingDetected() {
        assertEquals(Subject.PROGRAMMING, SubjectDetector.detect("Viết code python sắp xếp mảng"))
    }

    @Test fun blankReturnsNull() {
        assertNull(SubjectDetector.detect(""))
        assertNull(SubjectDetector.detect("   "))
    }

    @Test fun unknownReturnsNull() {
        assertNull(SubjectDetector.detect("xyz qqq zzz"))
    }

    @Test fun accentInsensitive() {
        assertEquals(Subject.MATHEMATICS, SubjectDetector.detect("tinh dao ham cua ham so"))
    }

    // ---- ca that tu DB cua user ----
    @Test fun greetingNotScience() {
        assertNull(SubjectDetector.detect("hi"))
    }

    @Test fun biologyDetected() {
        assertEquals(Subject.SCIENCE, SubjectDetector.detect("ADN - ARN là gì"))
    }

    @Test fun hocSinhNotScience() {
        assertNull(SubjectDetector.detect("Học sinh cần chuẩn bị gì"))
    }

    @Test fun areaMath() {
        assertEquals(Subject.MATHEMATICS, SubjectDetector.detect("Tính diện tích hình tròn"))
    }

    @Test fun poemLanguage() {
        assertEquals(Subject.LANGUAGE, SubjectDetector.detect("Phân tích bài thơ Đồng chí"))
    }

    @Test fun warHistory() {
        assertEquals(Subject.HISTORY, SubjectDetector.detect("Chiến tranh thế giới thứ hai"))
    }

    @Test fun velocityScience() {
        assertEquals(Subject.SCIENCE, SubjectDetector.detect("Tính vận tốc của xe"))
    }
}
