package com.aistudy.aistudy.ui.common

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aistudy.aistudy.ui.theme.RuleLineDark
import com.aistudy.aistudy.ui.theme.RuleLineLight
import com.aistudy.aistudy.ui.theme.Spacing

// ==================== PaperSheet ====================

@Composable
fun PaperSheet(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceContainerLowest,
        tonalElevation = 0.dp
    ) {
        Box(modifier = Modifier.padding(Spacing.lg)) {
            // Column bat buoc: content() cua caller thuong co NHIEU con
            // (tieu de + spacer + o nhap + chips). Dung Box tran sexe chung
            // len nhau (da tung xay ra, thay bang anh chup thiet bi that).
            Column {
                content()
            }
        }
    }
}

// ==================== MarkerChip ====================

@Composable
fun MarkerChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    // animateColorAsState song trong androidx.compose.animation (artifact animation,
    // repo chua co dependency nay) nen dung animateFloatAsState (animation-core,
    // da chung minh compile duoc) + lerp mau bang spring — hieu ung tuong duong.
    val selection by animateFloatAsState(
        targetValue = if (selected) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = 400f),
        label = "markerSelection"
    )
    val containerColor = lerp(
        MaterialTheme.colorScheme.surfaceContainerLow,
        MaterialTheme.colorScheme.tertiaryContainer,
        selection
    )
    val labelColor = lerp(
        MaterialTheme.colorScheme.onSurfaceVariant,
        MaterialTheme.colorScheme.onTertiaryContainer,
        selection
    )
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(
                text = label,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
            )
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = containerColor,
            selectedLabelColor = labelColor,
            containerColor = containerColor,
            labelColor = labelColor
        )
    )
}

// ==================== StickerBadge ====================

@Composable
fun StickerBadge(
    text: String,
    icon: @Composable () -> Unit
) {
    var shown by remember(Unit) { mutableStateOf(false) }
    LaunchedEffect(Unit) { shown = true }
    val scale by animateFloatAsState(
        targetValue = if (shown) 1f else 0.8f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 300f),
        label = "stickerScale"
    )
    AssistChip(
        modifier = Modifier.graphicsLayer(scaleX = scale, scaleY = scale),
        onClick = { },
        label = { Text(text = text) },
        leadingIcon = icon,
        colors = AssistChipDefaults.assistChipColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            labelColor = MaterialTheme.colorScheme.onTertiaryContainer
        )
    )
}

// ==================== RuledDivider ====================

@Composable
fun RuledDivider() {
    val ruleLine = if (isSystemInDarkTheme()) RuleLineDark else RuleLineLight

    HorizontalDivider(color = ruleLine)
}

// ==================== Previews ====================

@Preview
@Composable
private fun PaperSheetPreview() {
    MaterialTheme {
        PaperSheet {
            Text(text = "Noi dung trang vo")
        }
    }
}

@Preview
@Composable
private fun MarkerChipPreview() {
    MaterialTheme {
        Row(modifier = Modifier.padding(Spacing.sm)) {
            MarkerChip(label = "Toan", selected = true, onClick = {})
            Spacer(modifier = Modifier.width(Spacing.xs))
            MarkerChip(label = "Van", selected = false, onClick = {})
        }
    }
}

@Preview
@Composable
private fun StickerBadgePreview() {
    MaterialTheme {
        Row(modifier = Modifier.padding(Spacing.sm)) {
            StickerBadge(text = "+50 XP") {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary
                )
            }
        }
    }
}

@Preview
@Composable
private fun RuledDividerPreview() {
    MaterialTheme {
        RuledDivider()
    }
}
