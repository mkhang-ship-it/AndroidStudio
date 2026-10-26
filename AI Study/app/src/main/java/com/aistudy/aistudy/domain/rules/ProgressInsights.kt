package com.aistudy.aistudy.domain.rules

import com.aistudy.aistudy.domain.model.enums.Subject

/**
 * FR8 - nhan xet hoc tap dua tren so lieu da co trong DB.
 *
 * Co y khong goi AI: dashboard phai mo duoc offline, khong ton token va khong
 * phu thuoc mang. Day cung la huong BA1 trong roadmap ("AI-insights ban text
 * template truoc").
 */
enum class ProgressInsightKind {
    TOP_SUBJECT,
    LEAST_SUBJECT,
    LOW_ACCURACY,
    PRACTICE_REMAINING,
    REVIEW_TIME
}

data class ProgressInsight(
    val kind: ProgressInsightKind,
    val subject: Subject? = null,
    val value: Int = 0
)

object ProgressInsights {

    const val LOW_ACCURACY_THRESHOLD = 0.6
    const val MIN_ANSWERED_FOR_ACCURACY = 3
    const val MAX_INSIGHTS = 3

    fun build(
        questionsBySubject: Map<Subject, Int>,
        quizAccuracy: Double,
        quizAnswered: Int,
        quizRemaining: Int,
        reviewMinutes: Long
    ): List<ProgressInsight> {
        if (questionsBySubject.values.sum() <= 0) return emptyList()

        val insights = mutableListOf<ProgressInsight>()

        val accuracyLow = quizAnswered >= MIN_ANSWERED_FOR_ACCURACY &&
            quizAccuracy < LOW_ACCURACY_THRESHOLD
        if (accuracyLow) {
            insights += ProgressInsight(
                kind = ProgressInsightKind.LOW_ACCURACY,
                value = (quizAccuracy * 100).toInt().coerceIn(0, 100)
            )
        } else {
            questionsBySubject.maxByOrNull { it.value }?.let { (subject, count) ->
                insights += ProgressInsight(
                    kind = ProgressInsightKind.TOP_SUBJECT,
                    subject = subject,
                    value = count
                )
            }
        }

        if (quizRemaining > 0) {
            insights += ProgressInsight(
                kind = ProgressInsightKind.PRACTICE_REMAINING,
                value = quizRemaining
            )
        }

        if (questionsBySubject.size > 1) {
            questionsBySubject.minByOrNull { it.value }?.let { (subject, count) ->
                insights += ProgressInsight(
                    kind = ProgressInsightKind.LEAST_SUBJECT,
                    subject = subject,
                    value = count
                )
            }
        }

        if (insights.size < MAX_INSIGHTS && reviewMinutes > 0) {
            insights += ProgressInsight(
                kind = ProgressInsightKind.REVIEW_TIME,
                value = reviewMinutes.toInt()
            )
        }

        return insights.take(MAX_INSIGHTS)
    }
}
