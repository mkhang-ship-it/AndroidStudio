package com.aistudy.aistudy.domain.rules

import com.aistudy.aistudy.domain.model.enums.Subject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * FR8 - kiem thu generator nhan xet hoc tap (offline, khong goi AI).
 * Anh xa test plan: TC-PRG-04 (AI-insights ban text template).
 */
class ProgressInsightsTest {

    @Test
    fun `khong co cau hoi nao thi khong tra ve nhan xet`() {
        val insights = ProgressInsights.build(
            questionsBySubject = emptyMap(),
            quizAccuracy = 0.0,
            quizAnswered = 0,
            quizRemaining = 0,
            reviewMinutes = 0
        )
        assertTrue(insights.isEmpty())
    }

    @Test
    fun `mon hoi nhieu nhat duoc chon lam top subject`() {
        val insights = ProgressInsights.build(
            questionsBySubject = mapOf(
                Subject.MATHEMATICS to 10,
                Subject.SCIENCE to 4,
                Subject.PROGRAMMING to 2
            ),
            quizAccuracy = 0.8,
            quizAnswered = 10,
            quizRemaining = 5,
            reviewMinutes = 12
        )

        val top = insights.first { it.kind == ProgressInsightKind.TOP_SUBJECT }
        assertEquals(Subject.MATHEMATICS, top.subject)
        assertEquals(10, top.value)
        assertTrue(insights.any { it.kind == ProgressInsightKind.PRACTICE_REMAINING })
        assertTrue(insights.size <= ProgressInsights.MAX_INSIGHTS)
    }

    @Test
    fun `ti le dung thap duoc uu tien hon top subject`() {
        val insights = ProgressInsights.build(
            questionsBySubject = mapOf(Subject.MATHEMATICS to 10),
            quizAccuracy = 0.4,
            quizAnswered = 10,
            quizRemaining = 2,
            reviewMinutes = 0
        )

        val low = insights.first() as? ProgressInsight
        assertEquals(ProgressInsightKind.LOW_ACCURACY, low?.kind)
        assertEquals(40, low?.value)
        assertTrue("khong can top subject khi dang yeu", insights.none { it.kind == ProgressInsightKind.TOP_SUBJECT })
    }

    @Test
    fun `ti le dung thap nhung chua du cau thi khong ket luan`() {
        val insights = ProgressInsights.build(
            questionsBySubject = mapOf(Subject.MATHEMATICS to 10),
            quizAccuracy = 0.0,
            quizAnswered = 1,
            quizRemaining = 9,
            reviewMinutes = 0
        )

        assertTrue(insights.any { it.kind == ProgressInsightKind.TOP_SUBJECT })
        assertTrue(insights.none { it.kind == ProgressInsightKind.LOW_ACCURACY })
    }

    @Test
    fun `mon it cau hoi duoc goi y bo sung`() {
        val insights = ProgressInsights.build(
            questionsBySubject = mapOf(
                Subject.MATHEMATICS to 8,
                Subject.LANGUAGE to 1
            ),
            quizAccuracy = 0.9,
            quizAnswered = 8,
            quizRemaining = 0,
            reviewMinutes = 0
        )

        val least = insights.firstOrNull { it.kind == ProgressInsightKind.LEAST_SUBJECT }
        assertEquals(Subject.LANGUAGE, least?.subject)
        assertEquals(1, least?.value)
    }

    @Test
    fun `thoi gian on tap duoc dua vao khi con cho`() {
        val insights = ProgressInsights.build(
            questionsBySubject = mapOf(Subject.MATHEMATICS to 3),
            quizAccuracy = 0.9,
            quizAnswered = 3,
            quizRemaining = 0,
            reviewMinutes = 15
        )

        val review = insights.firstOrNull { it.kind == ProgressInsightKind.REVIEW_TIME }
        assertEquals(15, review?.value)
    }

    @Test
    fun `toi da ba nhan xet de card khong qua dai`() {
        val insights = ProgressInsights.build(
            questionsBySubject = mapOf(
                Subject.MATHEMATICS to 8,
                Subject.SCIENCE to 4,
                Subject.PROGRAMMING to 1
            ),
            quizAccuracy = 0.9,
            quizAnswered = 9,
            quizRemaining = 4,
            reviewMinutes = 20
        )

        assertEquals(ProgressInsights.MAX_INSIGHTS, insights.size)
    }
}
