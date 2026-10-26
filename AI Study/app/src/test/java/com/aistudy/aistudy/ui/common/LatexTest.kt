package com.aistudy.aistudy.ui.common

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Kiem thu phan chuyen LaTeX -> Unicode va parser markdown.
 *
 * Day la logic thuan Kotlin nam trong `ui/common` nhung khong phu thuoc
 * Android, nen chay duoc bang JUnit tren JVM (khong can emulator).
 */
class LatexTest {

    // ------------------------------------------------------------ latexToUnicode

    @Test
    fun `phan so don gian thanh a tren b`() {
        assertEquals("a/b", latexToUnicode("\\frac{a}{b}"))
    }

    @Test
    fun `phan so co bieu thuc duoc boc ngoac`() {
        assertEquals("(x+1)/2", latexToUnicode("\\frac{x+1}{2}"))
    }

    @Test
    fun `luy thua va chi so duoi dung unicode`() {
        assertEquals("x\u00B2", latexToUnicode("x^2"))
        assertEquals("x\u207F\u207A\u00B9", latexToUnicode("x^{n+1}"))
        assertEquals("f\u2032(x\u2080)", latexToUnicode("f'(x_0)"))
    }

    @Test
    fun `ky hieu hy lap va quan he`() {
        assertEquals("\u03B1 \u2192 \u221E", latexToUnicode("\\alpha \\to \\infty"))
        assertEquals("x \u2264 3", latexToUnicode("x \\le 3"))
    }

    @Test
    fun `lenh thieu dau gach cheo van hien thi duoc`() {
        assertEquals("2 \u00B7 3", latexToUnicode("2 cdot 3"))
        assertEquals("2 \u00B7 3", latexToUnicode("2 \\cdot 3"))
        assertEquals("x \u2192 0", latexToUnicode("x to 0"))
    }

    @Test
    fun `khoi phuc ky tu dieu khien do JSON parse sai`() {
        assertEquals("a/b", latexToUnicode("\u000Crac{a}{b}"))
        assertEquals("( x )", latexToUnicode("\\left( x \u000Dight)"))
    }

    @Test
    fun `can bac hai boc ngoac khi can thiet`() {
        assertEquals("\u221A(x\u00B2+1)", latexToUnicode("\\sqrt{x^2+1}"))
        assertEquals("\u221A9", latexToUnicode("\\sqrt{9}"))
        assertEquals("\u221Bx", latexToUnicode("\\sqrt[3]{x}"))
    }

    @Test
    fun `gioi han co chi so duoi`() {
        val result = latexToUnicode("\\lim_{x \\to 0}")
        assertTrue("phai bat dau bang lim, thuc te: $result", result.startsWith("lim"))
        assertTrue("phai chua mui ten, thuc te: $result", result.contains("\u2192"))
    }

    @Test
    fun `text va ma tran khong bi pha`() {
        assertEquals("speed", latexToUnicode("\\text{speed}"))
        val matrix = latexToUnicode(
            "\\begin{pmatrix} a & b \\\\ c & d \\end{pmatrix}"
        )
        assertEquals("a b\nc d", matrix)
    }

    @Test
    fun `left right va dau phan cach vo hinh duoc bo`() {
        val result = latexToUnicode("\\left( x \\right)")
        assertEquals("( x )", result)
    }

    @Test
    fun `lenh la khong lam crash`() {
        assertEquals("khongbietx", latexToUnicode("\\khongbiet{x}"))
    }

    // ------------------------------------------------------------ splitLatexPieces

    @Test
    fun `tach duoc phan so ra khoi cong thuc`() {
        val pieces = splitLatexPieces("\\frac{1}{2} + x")
        assertTrue("phai co 2 manh, thuc te: $pieces", pieces.size == 2)
        val fraction = pieces[0] as LatexPiece.Fraction
        assertEquals("1", fraction.numerator)
        assertEquals("2", fraction.denominator)
        val text = pieces[1] as LatexPiece.Text
        assertEquals(" + x", text.value)
    }

    @Test
    fun `cong thuc chi co chu thi khong tao phan so`() {
        val pieces = splitLatexPieces("a + b")
        assertEquals(1, pieces.size)
        assertEquals("a + b", (pieces[0] as LatexPiece.Text).value)
    }

    // ------------------------------------------------------------ markdown blocks

    @Test
    fun `parse duoc tieu de gach dau dong va cong thuc hien thi`() {
        val blocks = parseMarkdownBlocks(
            """
            ## Cach giai
            - Buoc 1
            - Buoc 2
            $$
            x = \frac{-b}{2a}
            $$
            """.trimIndent()
        )

        assertTrue(blocks[0] is MarkdownBlock.Heading)
        assertEquals("Cach giai", (blocks[0] as MarkdownBlock.Heading).text)
        assertTrue(blocks[1] is MarkdownBlock.Bullet)
        assertTrue(blocks[2] is MarkdownBlock.Bullet)
        val formula = blocks[3] as MarkdownBlock.Formula
        assertTrue(formula.latex.contains("frac"))
    }

    @Test
    fun `parse duoc khoi code va trich dan`() {
        val blocks = parseMarkdownBlocks(
            """
            ```kotlin
            val x = 1
            ```
            > Ghi chu
            ---
            """.trimIndent()
        )

        assertTrue(blocks[0] is MarkdownBlock.Code)
        assertEquals("kotlin", (blocks[0] as MarkdownBlock.Code).language)
        assertEquals("val x = 1", (blocks[0] as MarkdownBlock.Code).code)
        assertTrue(blocks[1] is MarkdownBlock.Quote)
        assertTrue(blocks[2] is MarkdownBlock.Rule)
    }

    @Test
    fun `doan van nhieu dong duoc gop lai`() {
        val blocks = parseMarkdownBlocks("Dong 1\nDong 2\n\nDoan 2")
        assertEquals(2, blocks.size)
        assertEquals("Dong 1\nDong 2", (blocks[0] as MarkdownBlock.Paragraph).text)
    }

    // ------------------------------------------------------------ inline tokens

    @Test
    fun `tokenize nhan dien dam cong thuc va code`() {
        val tokens = tokenizeInline("**bold** va \$x^2\$ va `code`")

        assertTrue(tokens[0] is InlineToken.Bold)
        assertTrue(tokens[1] is InlineToken.Text)
        assertTrue(tokens[2] is InlineToken.Math)
        assertEquals("x^2", (tokens[2] as InlineToken.Math).latex)
        assertTrue(tokens[3] is InlineToken.Text)
        assertTrue(tokens[4] is InlineToken.Code)
        assertEquals("code", (tokens[4] as InlineToken.Code).value)
    }

    @Test
    fun `tokenize nhan dien link va gach ngang`() {
        val tokens = tokenizeInline("~~cu~~ [mo](https://example.com)")
        assertTrue(tokens[0] is InlineToken.Strike)
        assertTrue(tokens[1] is InlineToken.Text)
        val link = tokens[2] as InlineToken.Link
        assertEquals("mo", link.label)
        assertEquals("https://example.com", link.url)
    }

    @Test
    fun `dollar le khong bi coi la cong thuc`() {
        val tokens = tokenizeInline("Gia 5\$ khong phai cong thuc")
        assertEquals(1, tokens.size)
        assertTrue(tokens[0] is InlineToken.Text)
    }
}
