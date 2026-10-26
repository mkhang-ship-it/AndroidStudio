package com.aistudy.aistudy.ui.common

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.aistudy.aistudy.R
import com.aistudy.aistudy.domain.model.enums.Subject
import com.aistudy.aistudy.ui.label
import com.aistudy.aistudy.ui.theme.Spacing

/**
 * Tieu de man hinh dung chung: mot dong tieu de dam + dong phu tuy chon. Thay
 * cho viec man nao cung tu ve mot Text headline roi lech co chu/padding.
 */
@Composable
fun ScreenHeader(
    @StringRes titleRes: Int,
    modifier: Modifier = Modifier,
    @StringRes subtitleRes: Int? = null
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(titleRes),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        if (subtitleRes != null) {
            Spacer(modifier = Modifier.height(Spacing.xs))
            Text(
                text = stringResource(subtitleRes),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Trang thai cho - dung chung cho ca 12 man hinh de khong viet lai Box + spinner.
 */
@Composable
fun LoadingBox(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

/**
 * [message] da duoc ViewModel doi san thanh chuoi doc duoc - man hinh khong
 * duoc hien thi exception tho.
 */
@Composable
fun ErrorBanner(
    message: String,
    modifier: Modifier = Modifier,
    onRetry: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(Spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center
        )
        if (onRetry != null) {
            TextButton(onClick = onRetry) {
                Text(stringResource(R.string.common_retry))
            }
        }
    }
}

@Composable
fun EmptyState(
    @StringRes titleRes: Int,
    @StringRes bodyRes: Int,
    modifier: Modifier = Modifier,
    action: (@Composable () -> Unit)? = null,
    icon: ImageVector? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(Spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .padding(bottom = Spacing.lg)
                    .size(64.dp)
            )
        }
        Text(
            text = stringResource(titleRes),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
        Text(
            text = stringResource(bodyRes),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = Spacing.sm)
        )
        if (action != null) {
            Box(modifier = Modifier.padding(top = Spacing.lg)) { action() }
        }
    }
}

/**
 * Bo cuc chuan cho mot man hinh: card PaperSheet chua ScreenHeader o dinh +
 * noi dung cuon ben trong. Nhat quan giua cac tab (Mau A).
 */
@Composable
fun ScreenPager(
    @StringRes titleRes: Int,
    modifier: Modifier = Modifier,
    @StringRes subtitleRes: Int? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = Spacing.lg, vertical = Spacing.lg)
    ) {
        PaperSheet(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                ScreenHeader(titleRes = titleRes, subtitleRes = subtitleRes)
                Spacer(modifier = Modifier.height(Spacing.lg))
                content()
            }
        }
    }
}

/**
 * Subject co 5 gia tri nen 1 hang chip se tran tren man hinh hep - cho phep
 * cuon ngang thay vi Wrap hay luoi.
 */
@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun SubjectChipRow(
    subjects: List<Subject>,
    selected: Set<Subject>,
    onToggle: (Subject) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
    ) {
        subjects.forEach { subject ->
            FilterChip(
                selected = subject in selected,
                onClick = { onToggle(subject) },
                label = { Text(subject.label()) }
            )
        }
    }
}