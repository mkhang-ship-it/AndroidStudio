package com.aistudy.aistudy.ui.common

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle

/**
 * Lite markdown renderer (pure Compose, no extra dependency).
 * - `**bold**` -> Bold (same text color)
 * - line starting with `- ` -> bullet `• `
 * - line starting with `## ` -> titleSmall Bold
 * - `$...$` -> strip `$`, keep inner content
 */
@Composable
fun MarkdownText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodyLarge,
    color: Color = MaterialTheme.colorScheme.onSurface
) {
    val titleStyle = MaterialTheme.typography.titleSmall
    val annotated = remember(text) { parseLiteMarkdown(text, titleStyle) }
    Text(
        text = annotated,
        modifier = modifier,
        style = style,
        color = color
    )
}

private val boldRegex = Regex("\\*\\*(.+?)\\*\\*")

private fun parseLiteMarkdown(text: String, titleStyle: TextStyle): AnnotatedString {
    // Strip `$` wrapping formulas, keep inner content.
    val noDollars = text.replace("$", "")
    val lines = noDollars.split("\n")
    return buildAnnotatedString {
        lines.forEachIndexed { index, rawLine ->
            if (index > 0) append("\n")
            val trimmed = rawLine.trimStart()
            when {
                trimmed.startsWith("## ") -> {
                    val content = trimmed.removePrefix("## ")
                    withStyle(
                        SpanStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = titleStyle.fontSize
                        )
                    ) {
                        appendInlineBold(content)
                    }
                }
                trimmed.startsWith("- ") -> {
                    val content = trimmed.removePrefix("- ")
                    append("• ")
                    appendInlineBold(content)
                }
                else -> appendInlineBold(rawLine)
            }
        }
    }
}

private fun AnnotatedString.Builder.appendInlineBold(line: String) {
    var lastIndex = 0
    for (match in boldRegex.findAll(line)) {
        if (match.range.first > lastIndex) {
            append(line.substring(lastIndex, match.range.first))
        }
        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
            append(match.groupValues[1])
        }
        lastIndex = match.range.last + 1
    }
    if (lastIndex < line.length) {
        append(line.substring(lastIndex))
    }
}
