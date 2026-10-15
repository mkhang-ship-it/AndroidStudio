package com.aistudy.aistudy.domain.rules

object AbuseFilter {
    private val blockedWords = setOf(
        "spam", "scam", "fraud", "hate",
        "drugs", "weapons", "phishing", "casino",
        "xxx", "porn", "kill", "bomb",
        "attack", "steal", "hack"
    )

    private const val MAX_LENGTH = 2000

    fun isBlocked(text: String): Boolean {
        if (text.isBlank()) return false
        if (text.length > MAX_LENGTH) return true
        val lower = text.lowercase()
        if (lower.contains("http://") || lower.contains("https://")) return true
        for (word in blockedWords) {
            if (lower.contains(word)) return true
        }
        return false
    }
}
