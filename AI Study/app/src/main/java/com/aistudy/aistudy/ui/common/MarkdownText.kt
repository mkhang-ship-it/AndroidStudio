package com.aistudy.aistudy.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.withStyle
import com.aistudy.aistudy.ui.theme.Spacing

/**
 * Renderer markdown nhe cho cau tra loi AI - khong them dependency.
 *
 * Ho tro:
 * - tieu de `#`, `##`, `###`
 * - dam `**...**`, nghieng `*...*`, gach ngang `~~...~~`
 * - danh sach `- `, `1. `, trich dan `> `
 * - khoi code ``` ... ``` va code inline `` `...` ``
 * - cong thuc `$...$`, `$$...$$`, `\(...\)`, `\[...\]`
 *
 * Cong thuc duoc doi sang Unicode; rieng `\frac{}{}` duoc ve thanh phan so
 * xep tang bang inline content cua Compose (khong can WebView hay MathJax).
 */
@Composable
fun MarkdownText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodyLarge,
    color: Color = MaterialTheme.colorScheme.onSurface
) {
    val normalised = remember(text) { normaliseMathDelimiters(text) }
    val blocks = remember(normalised) { parseMarkdownBlocks(normalised) }
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Spacing.xs)
    ) {
        blocks.forEach { block ->
            MarkdownBlockView(block = block, style = style, color = color)
        }
    }
}

@Composable
private fun MarkdownBlockView(
    block: MarkdownBlock,
    style: TextStyle,
    color: Color
) {
    when (block) {
        is MarkdownBlock.Paragraph -> InlineMarkdownText(
            text = block.text,
            style = style,
            color = color
        )

        is MarkdownBlock.Heading -> {
            val headingStyle = when (block.level) {
                1 -> MaterialTheme.typography.titleLarge
                2 -> MaterialTheme.typography.titleMedium
                else -> MaterialTheme.typography.titleSmall
            }
            InlineMarkdownText(
                text = block.text,
                style = headingStyle.copy(fontWeight = FontWeight.Bold),
                color = color
            )
        }

        is MarkdownBlock.Bullet -> Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = "\u2022",
                style = style,
                color = MaterialTheme.colorScheme.primary
            )
            InlineMarkdownText(
                text = block.text,
                style = style,
                color = color,
                modifier = Modifier.weight(1f)
            )
        }

        is MarkdownBlock.Ordered -> Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = block.marker,
                style = style.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.primary
            )
            InlineMarkdownText(
                text = block.text,
                style = style,
                color = color,
                modifier = Modifier.weight(1f)
            )
        }

        is MarkdownBlock.Code -> Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surfaceContainerHighest,
            shape = MaterialTheme.shapes.small
        ) {
            Text(
                text = block.code,
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(Spacing.md),
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = FontFamily.Monospace,
                    lineHeight = 18.sp
                ),
                color = color
            )
        }

        is MarkdownBlock.Quote -> Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.6f))
            )
            InlineMarkdownText(
                text = block.text,
                style = style,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f)
            )
        }

        is MarkdownBlock.Formula -> Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            InlineMarkdownText(
                text = "\\[" + block.latex + "\\]",
                style = style.copy(fontSize = style.fontSize * 1.05f),
                color = color,
                textAlign = TextAlign.Center
            )
        }

        MarkdownBlock.Rule -> HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant
        )
    }
}

@Composable
private fun InlineMarkdownText(
    text: String,
    style: TextStyle,
    color: Color,
    modifier: Modifier = Modifier,
    textAlign: TextAlign? = null
) {
    val measurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val rendered = remember(text, style, color, measurer, density) {
        buildInlineMarkdown(
            source = text,
            style = style,
            color = color,
            measurer = measurer,
            density = density
        )
    }
    Text(
        text = rendered.annotated,
        modifier = modifier,
        style = style,
        color = color,
        textAlign = textAlign,
        inlineContent = rendered.inlineContent
    )
}

private class RenderedInline(
    val annotated: AnnotatedString,
    val inlineContent: Map<String, InlineTextContent>
)

private fun buildInlineMarkdown(
    source: String,
    style: TextStyle,
    color: Color,
    measurer: TextMeasurer,
    density: androidx.compose.ui.unit.Density
): RenderedInline {
    val builder = AnnotatedString.Builder()
    val inlineContent = mutableMapOf<String, InlineTextContent>()
    var fractionId = 0

    fun appendMath(latex: String) {
        splitLatexPieces(latex).forEach { piece ->
            when (piece) {
                is LatexPiece.Text -> builder.append(piece.value)
                is LatexPiece.Fraction -> {
                    val numerator = latexToUnicode(piece.numerator)
                    val denominator = latexToUnicode(piece.denominator)
                    val numeratorLayout = measurer.measure(
                        text = AnnotatedString(numerator),
                        style = style,
                        softWrap = false,
                        maxLines = 1
                    )
                    val denominatorLayout = measurer.measure(
                        text = AnnotatedString(denominator),
                        style = style,
                        softWrap = false,
                        maxLines = 1
                    )
                    val extraWidth = with(density) { 8.dp.roundToPx() }
                    val extraHeight = with(density) { 5.dp.roundToPx() }
                    val width = maxOf(
                        numeratorLayout.size.width,
                        denominatorLayout.size.width
                    ) + extraWidth
                    val height = numeratorLayout.size.height +
                        denominatorLayout.size.height +
                        with(density) { 1.dp.roundToPx() } +
                        extraHeight

                    val id = "latex-fraction-${fractionId++}"
                    builder.appendInlineContent(
                        id,
                        alternateText = "($numerator)/($denominator)"
                    )
                    val spPerPx = 1f / (density.density * density.fontScale)
                    inlineContent[id] = InlineTextContent(
                        Placeholder(
                            width = (width * spPerPx).sp,
                            height = (height * spPerPx).sp,
                            placeholderVerticalAlign = PlaceholderVerticalAlign.Center
                        )
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = numerator,
                                style = style,
                                color = color,
                                maxLines = 1,
                                softWrap = false
                            )
                            HorizontalDivider(
                                modifier = Modifier.fillMaxWidth(),
                                thickness = 1.dp,
                                color = color.copy(alpha = 0.7f)
                            )
                            Text(
                                text = denominator,
                                style = style,
                                color = color,
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }
                }
            }
        }
    }

    fun appendTokens(tokens: List<InlineToken>) {
        tokens.forEach { token ->
            when (token) {
                is InlineToken.Text -> builder.append(token.value)
                is InlineToken.Math -> appendMath(token.latex)
                is InlineToken.Bold -> builder.withStyle(
                    SpanStyle(fontWeight = FontWeight.Bold)
                ) {
                    appendTokens(token.children)
                }
                is InlineToken.Italic -> builder.withStyle(
                    SpanStyle(fontStyle = FontStyle.Italic)
                ) {
                    appendTokens(token.children)
                }
                is InlineToken.Strike -> builder.withStyle(
                    SpanStyle(textDecoration = TextDecoration.LineThrough)
                ) {
                    appendTokens(token.children)
                }
                is InlineToken.Code -> builder.withStyle(
                    SpanStyle(
                        fontFamily = FontFamily.Monospace,
                        background = color.copy(alpha = 0.10f)
                    )
                ) {
                    builder.append(token.value)
                }
                is InlineToken.Link -> builder.withStyle(
                    SpanStyle(textDecoration = TextDecoration.Underline)
                ) {
                    builder.append(token.label)
                }
            }
        }
    }

    appendTokens(tokenizeInline(source))
    return RenderedInline(builder.toAnnotatedString(), inlineContent)
}

// ---------------------------------------------------------------- block parser

internal sealed interface MarkdownBlock {
    data class Paragraph(val text: String) : MarkdownBlock
    data class Heading(val level: Int, val text: String) : MarkdownBlock
    data class Bullet(val text: String) : MarkdownBlock
    data class Ordered(val marker: String, val text: String) : MarkdownBlock
    data class Code(val language: String, val code: String) : MarkdownBlock
    data class Quote(val text: String) : MarkdownBlock
    data class Formula(val latex: String) : MarkdownBlock
    data object Rule : MarkdownBlock
}

internal fun parseMarkdownBlocks(text: String): List<MarkdownBlock> {
    val blocks = mutableListOf<MarkdownBlock>()
    val lines = text.replace("\r\n", "\n").replace('\r', '\n').split('\n')
    val paragraph = StringBuilder()

    fun flushParagraph() {
        val content = paragraph.toString().trim('\n', ' ')
        if (content.isNotBlank()) blocks += MarkdownBlock.Paragraph(content)
        paragraph.clear()
    }

    var index = 0
    while (index < lines.size) {
        val rawLine = lines[index]
        val line = rawLine.trim()
        when {
            line.startsWith("```") -> {
                flushParagraph()
                val language = line.removePrefix("```").trim()
                val code = StringBuilder()
                index++
                while (index < lines.size && !lines[index].trim().startsWith("```")) {
                    if (code.isNotEmpty()) code.append('\n')
                    code.append(lines[index])
                    index++
                }
                blocks += MarkdownBlock.Code(language, code.toString())
            }

            line.isEmpty() -> flushParagraph()

            isHorizontalRule(line) -> {
                flushParagraph()
                blocks += MarkdownBlock.Rule
            }

            line.startsWith("#") -> {
                val level = line.takeWhile { it == '#' }.length.coerceIn(1, 3)
                val content = line.drop(level).trim()
                if (content.isNotEmpty()) {
                    flushParagraph()
                    blocks += MarkdownBlock.Heading(level, content)
                } else {
                    flushParagraph()
                    blocks += MarkdownBlock.Paragraph(line)
                }
            }

            line.startsWith("$$") -> {
                flushParagraph()
                val formula = StringBuilder(line.removePrefix("$$"))
                if (formula.endsWith("$$")) {
                    blocks += MarkdownBlock.Formula(formula.removeSuffix("$$").toString().trim())
                } else {
                    index++
                    while (index < lines.size) {
                        val next = lines[index].trim()
                        if (next.endsWith("$$")) {
                            formula.append('\n').append(next.removeSuffix("$$"))
                            break
                        }
                        formula.append('\n').append(next)
                        index++
                    }
                    blocks += MarkdownBlock.Formula(formula.toString().trim())
                }
            }

            line.startsWith("\\[") && line.endsWith("\\]") && line.length > 4 -> {
                flushParagraph()
                blocks += MarkdownBlock.Formula(
                    line.removePrefix("\\[").removeSuffix("\\]").trim()
                )
            }

            BULLET_REGEX.matches(line) -> {
                flushParagraph()
                blocks += MarkdownBlock.Bullet(
                    BULLET_REGEX.matchEntire(line)!!.groupValues[1].trim()
                )
            }

            ORDERED_REGEX.matches(line) -> {
                flushParagraph()
                val match = ORDERED_REGEX.matchEntire(line)!!
                blocks += MarkdownBlock.Ordered(
                    marker = match.groupValues[1],
                    text = match.groupValues[2].trim()
                )
            }

            line.startsWith(">") -> {
                flushParagraph()
                blocks += MarkdownBlock.Quote(line.removePrefix(">").trim())
            }

            else -> {
                if (paragraph.isNotEmpty()) paragraph.append('\n')
                paragraph.append(line)
            }
        }
        index++
    }
    flushParagraph()
    return blocks
}

private fun isHorizontalRule(line: String): Boolean {
    if (line.length < 3) return false
    val char = line[0]
    if (char != '-' && char != '*' && char != '_') return false
    return line.all { it == char }
}

// --------------------------------------------------------------- inline parser

internal sealed interface InlineToken {
    data class Text(val value: String) : InlineToken
    data class Bold(val children: List<InlineToken>) : InlineToken
    data class Italic(val children: List<InlineToken>) : InlineToken
    data class Strike(val children: List<InlineToken>) : InlineToken
    data class Code(val value: String) : InlineToken
    data class Math(val latex: String) : InlineToken
    data class Link(val label: String, val url: String) : InlineToken
}

internal fun tokenizeInline(text: String): List<InlineToken> {
    val tokens = mutableListOf<InlineToken>()
    val buffer = StringBuilder()

    fun flushText() {
        if (buffer.isNotEmpty()) {
            tokens += InlineToken.Text(buffer.toString())
            buffer.clear()
        }
    }

    var i = 0
    while (i < text.length) {
        when {
            text.startsWith("$$", i) -> {
                val end = text.indexOf("$$", i + 2)
                if (end > i + 2) {
                    flushText()
                    tokens += InlineToken.Math(text.substring(i + 2, end))
                    i = end + 2
                } else {
                    buffer.append("$$")
                    i += 2
                }
            }

            text[i] == '$' -> {
                val end = text.indexOf('$', i + 1)
                if (end > i + 1) {
                    flushText()
                    tokens += InlineToken.Math(text.substring(i + 1, end))
                    i = end + 1
                } else {
                    buffer.append('$')
                    i++
                }
            }

            text.startsWith("\\(", i) -> {
                val end = text.indexOf("\\)", i + 2)
                if (end > i + 2) {
                    flushText()
                    tokens += InlineToken.Math(text.substring(i + 2, end))
                    i = end + 2
                } else {
                    buffer.append('\\')
                    i++
                }
            }

            text.startsWith("\\[", i) -> {
                val end = text.indexOf("\\]", i + 2)
                if (end > i + 2) {
                    flushText()
                    tokens += InlineToken.Math(text.substring(i + 2, end))
                    i = end + 2
                } else {
                    buffer.append('\\')
                    i++
                }
            }

            text.startsWith("**", i) -> {
                val end = text.indexOf("**", i + 2)
                if (end > i + 2) {
                    flushText()
                    tokens += InlineToken.Bold(
                        tokenizeInline(text.substring(i + 2, end))
                    )
                    i = end + 2
                } else {
                    buffer.append("**")
                    i += 2
                }
            }

            text.startsWith("~~", i) -> {
                val end = text.indexOf("~~", i + 2)
                if (end > i + 2) {
                    flushText()
                    tokens += InlineToken.Strike(
                        tokenizeInline(text.substring(i + 2, end))
                    )
                    i = end + 2
                } else {
                    buffer.append("~~")
                    i += 2
                }
            }

            text[i] == '`' -> {
                val end = text.indexOf('`', i + 1)
                if (end > i + 1) {
                    flushText()
                    tokens += InlineToken.Code(text.substring(i + 1, end))
                    i = end + 1
                } else {
                    buffer.append('`')
                    i++
                }
            }

            text[i] == '*' -> {
                val end = text.indexOf('*', i + 1)
                if (end > i + 1) {
                    flushText()
                    tokens += InlineToken.Italic(
                        tokenizeInline(text.substring(i + 1, end))
                    )
                    i = end + 1
                } else {
                    buffer.append('*')
                    i++
                }
            }

            text[i] == '[' -> {
                val closeLabel = text.indexOf(']', i + 1)
                val openUrl = if (closeLabel > i) text.indexOf('(', closeLabel + 1) else -1
                val closeUrl = if (openUrl == closeLabel + 1) text.indexOf(')', openUrl + 1) else -1
                if (closeLabel > i + 1 && closeUrl > openUrl + 1) {
                    flushText()
                    tokens += InlineToken.Link(
                        label = text.substring(i + 1, closeLabel),
                        url = text.substring(openUrl + 1, closeUrl)
                    )
                    i = closeUrl + 1
                } else {
                    buffer.append('[')
                    i++
                }
            }

            else -> {
                buffer.append(text[i])
                i++
            }
        }
    }
    flushText()
    return tokens
}

private fun normaliseMathDelimiters(text: String): String {
    // Mot so model tra ve dau \\ ( dbl-backslash) do escape JSON; dua ve dang
    // delimiter ma parser hieu duoc.
    return text
        .replace("\\\\(", "\\(")
        .replace("\\\\)", "\\)")
        .replace("\\\\[", "\\[")
        .replace("\\\\]", "\\]")
}

private val BULLET_REGEX = Regex("^[-*+]\\s+(.+)$")
private val ORDERED_REGEX = Regex("^(\\d+[.)])\\s+(.+)$")
