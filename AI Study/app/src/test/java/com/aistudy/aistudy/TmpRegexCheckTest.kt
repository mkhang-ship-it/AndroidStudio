package com.aistudy.aistudy

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class TmpRegexCheckTest {
    private val inlineMathRegex = Regex("\\$([^\\$]+?)\\$")

    private fun stripMathDelimiters(text: String): String {
        var result = text.replace("$$", "")
        result = inlineMathRegex.replace(result, "$1")
        result = result
            .replace("\\(", "")
            .replace("\\)", "")
            .replace("\\[", "")
            .replace("\\]", "")
        return result
    }

    @Test
    fun stripsBlockMathDollarPairs() {
        val sample = "Theo dinh nghia:\n\$\$3 \\times 7 = 7 + 7 + 7 = 21\$\$\n\nHoac ket qua la 21."
        val out = stripMathDelimiters(sample)
        println("OUTPUT: $out")
        assertFalse("Con dau $ trong output", out.contains("$"))
        assertEquals("Theo dinh nghia:\n3 \\times 7 = 7 + 7 + 7 = 21\n\nHoac ket qua la 21.", out)
    }

    @Test
    fun stripsInlineMathDollarPairs() {
        val sample = "Ta co \$x^2 - 4x + 3 = 0\$ va nghiem la 1, 3."
        val out = stripMathDelimiters(sample)
        println("OUTPUT: $out")
        assertFalse("Con dau $ trong output", out.contains("$"))
    }

    @Test
    fun stripsLatexBracketDelimiters() {
        val sample = "\\(a + b\\) va \\[c + d\\] deu la cong thuc."
        val out = stripMathDelimiters(sample)
        println("OUTPUT: $out")
        assertFalse(out.contains("\\("))
        assertFalse(out.contains("\\)"))
        assertFalse(out.contains("\\["))
        assertFalse(out.contains("\\]"))
    }

    @Test
    fun keepsCurrencyLikeTextWithoutPair() {
        val out = stripMathDelimiters("Gia 5$ khong phai cong thuc.")
        assertEquals("Gia 5$ khong phai cong thuc.", out)
    }
}
