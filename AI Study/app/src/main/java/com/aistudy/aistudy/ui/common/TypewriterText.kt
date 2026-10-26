package com.aistudy.aistudy.ui.common

import android.os.SystemClock
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

/**
 * Hien thi cau tra loi theo kieu "chay chu" de giam cam giac cho doi.
 *
 * Khong phai streaming that (Gemini tra JSON mot lan), nhung giu dung trai
 * nghiem: chu hien dan, cham vao bat ky dau de hien toan bo.
 *
 * Thoi luong duoc gioi han 0.65s - 2.2s de cau tra loi dai khong bat nguoi
 * dung cho qua lau.
 */
@Composable
fun TypewriterMarkdown(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodyLarge,
    color: Color = MaterialTheme.colorScheme.onSurface,
    animate: Boolean = true
) {
    var visibleCount by remember(text, animate) {
        mutableIntStateOf(if (animate) 0 else text.length)
    }

    LaunchedEffect(text, animate) {
        if (!animate || text.isEmpty()) {
            visibleCount = text.length
            return@LaunchedEffect
        }
        visibleCount = 0
        val durationMs = (text.length * 3L).coerceIn(650L, 2_200L)
        val startedAt = SystemClock.elapsedRealtime()
        while (true) {
            val elapsed = SystemClock.elapsedRealtime() - startedAt
            val fraction = (elapsed.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f)
            val next = (text.length * fraction).toInt()
                .coerceAtLeast(1)
                .coerceAtMost(text.length)
            visibleCount = next
            if (next >= text.length) break
            delay(24)
        }
    }

    val interactionSource = remember { MutableInteractionSource() }
    val visibleText = if (visibleCount >= text.length) {
        text
    } else {
        text.take(visibleCount) + "\u258C"
    }

    Box(
        modifier = modifier.clickable(
            interactionSource = interactionSource,
            indication = null
        ) {
            visibleCount = text.length
        }
    ) {
        MarkdownText(
            text = visibleText,
            style = style,
            color = color
        )
    }
}

/** Ba cham nhap nhay - dung cho trang thai AI dang xu ly. */
@Composable
fun ThinkingDots(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary
) {
    val transition = rememberInfiniteTransition(label = "thinking-dots")
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(3) { index ->
            val alpha by transition.animateFloat(
                initialValue = 0.2f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 520,
                        delayMillis = index * 170,
                        easing = LinearEasing
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "thinking-dot-$index"
            )
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = alpha))
            )
        }
    }
}
