package com.aistudy.aistudy.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

enum class DuoButtonColors { Primary, Secondary, Danger }

@Composable
fun DuoButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: DuoButtonColors = DuoButtonColors.Primary
) {
    val dark = isSystemInDarkTheme()
    val topColor = when (colors) {
        DuoButtonColors.Primary -> MaterialTheme.colorScheme.primary
        DuoButtonColors.Secondary -> MaterialTheme.colorScheme.secondary
        DuoButtonColors.Danger -> MaterialTheme.colorScheme.error
    }
    val edgeColor = when (colors) {
        DuoButtonColors.Primary -> if (dark) Color(0xFF46A302) else Color(0xFF58A700)
        DuoButtonColors.Secondary -> if (dark) Color(0xFF1483B7) else Color(0xFF1899D6)
        DuoButtonColors.Danger -> if (dark) Color(0xFFB02A2A) else Color(0xFFD33131)
    }
    val shape = RoundedCornerShape(16.dp)
    Box(
        modifier = modifier
            .background(
                if (enabled) edgeColor else MaterialTheme.colorScheme.outline,
                shape
            )
            .padding(bottom = 4.dp)
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            shape = shape,
            colors = ButtonDefaults.buttonColors(
                containerColor = topColor,
                contentColor = Color.White,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 0.dp,
                pressedElevation = 0.dp,
                focusedElevation = 0.dp,
                hoveredElevation = 0.dp,
                disabledElevation = 0.dp
            ),
            contentPadding = PaddingValues(vertical = 14.dp)
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}

@Composable
fun DuoProgress(
    progress: () -> Float,
    modifier: Modifier = Modifier
) {
    LinearProgressIndicator(
        progress = progress,
        modifier = modifier
            .fillMaxWidth()
            .height(14.dp)
            .clip(RoundedCornerShape(50)),
        color = MaterialTheme.colorScheme.primary,
        trackColor = MaterialTheme.colorScheme.surfaceVariant,
        strokeCap = StrokeCap.Round
    )
}
