package com.aistudy.aistudy.ui.screens.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import com.aistudy.aistudy.ui.common.PaperSheet
import com.aistudy.aistudy.ui.common.StickerBadge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.viewmodel.initializer
import com.aistudy.aistudy.R
import com.aistudy.aistudy.domain.model.enums.Subject
import com.aistudy.aistudy.ui.common.EmptyState
import com.aistudy.aistudy.ui.common.LoadingBox
import com.aistudy.aistudy.ui.common.ScreenHeader
import com.aistudy.aistudy.ui.progress.ProgressUiState
import com.aistudy.aistudy.ui.progress.ProgressViewModel
import com.aistudy.aistudy.ui.root.AppDependencies
import com.aistudy.aistudy.ui.label
import com.aistudy.aistudy.ui.theme.Spacing

@Composable
fun ProgressScreen(
    deps: AppDependencies,
    modifier: Modifier = Modifier
) {
    val vm: ProgressViewModel = viewModel(
        factory = viewModelFactory {
            initializer { ProgressViewModel(deps.progressRepository) }
        }
    )
    val state by vm.state.collectAsStateWithLifecycle()

    val sessionUserId by deps.authRepository.sessionUserId.collectAsStateWithLifecycle(initialValue = null)

    LaunchedEffect(sessionUserId) {
        sessionUserId?.let { vm.load(it) }
    }

    ProgressContent(state = state, modifier = modifier)
}

@Composable
private fun ProgressContent(
    state: ProgressUiState,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        PaperSheet {
            ScreenHeader(
                titleRes = R.string.progress_title,
                modifier = Modifier.padding(horizontal = Spacing.lg, vertical = Spacing.lg)
            )
        }
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            when {
                state.isLoading -> LoadingBox()
            state.hasError -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    com.aistudy.aistudy.ui.common.ErrorBanner(
                        message = state.errorMessage?.takeIf { it.isNotBlank() }
                            ?: stringResource(R.string.common_error_generic),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Spacing.lg)
                    )
                }
            }
            state.summary != null -> {
                val summary = state.summary
                val total = summary.totalQuestions
                if (total == 0) {
                    EmptyState(
                        titleRes = R.string.progress_empty_title,
                        bodyRes = R.string.progress_empty_body,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    val accuracy = (summary.quizAccuracy * 100).toInt()
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(Spacing.lg),
                        verticalArrangement = Arrangement.spacedBy(Spacing.sm)
                    ) {
                        StatCard(
                            label = R.string.progress_questions_total,
                            value = total.toString()
                        )
                        StatCard(
                            label = R.string.progress_review_time,
                            value = stringResource(R.string.progress_review_time_value, summary.reviewMinutes)
                        )
                        StatCard(
                            label = R.string.progress_quiz_accuracy,
                            value = stringResource(R.string.progress_quiz_accuracy_value, accuracy)
                        )
                        StatTextCard(
                            text = stringResource(R.string.progress_quiz_progress, summary.quizAnswered, summary.quizTotal)
                        )
                        StickerBadge(
                            text = stringResource(R.string.progress_xp_week, summary.xpThisWeek),
                            icon = { Icon(Icons.Filled.Star, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary) }
                        )

                        Spacer(modifier = Modifier.height(Spacing.md))

                        Text(
                            text = stringResource(R.string.progress_by_subject),
                            style = MaterialTheme.typography.titleMedium
                        )

                        if (summary.questionsBySubject.isNotEmpty()) {
                            val maxCount = summary.questionsBySubject.values
                                .maxOrNull()
                                ?.coerceAtLeast(1) ?: 1
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(Spacing.md)
                            ) {
                                summary.questionsBySubject.forEach { (subject, count) ->
                                    SubjectBar(
                                        subject = subject,
                                        count = count,
                                        maxCount = maxCount
                                    )
                                }
                            }
                        } else {
                            Text(
                                text = stringResource(R.string.progress_by_subject_empty),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            else -> EmptyState(
                titleRes = R.string.progress_empty_title,
                bodyRes = R.string.progress_empty_body,
                modifier = Modifier.fillMaxSize()
            )
            }
        }
    }
}

@Composable
private fun StatCard(
    @androidx.annotation.StringRes label: Int,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.lg, vertical = Spacing.md),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(label),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun StatTextCard(
    text: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.lg, vertical = Spacing.md)
        )
    }
}

@Composable
private fun SubjectBar(
    subject: Subject,
    count: Int,
    maxCount: Int,
    modifier: Modifier = Modifier
) {
    val ratio = (count.toFloat() / maxCount).coerceIn(0f, 1f)

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = subject.label(),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = stringResource(R.string.progress_subject_count, count),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(Spacing.xs))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(MaterialTheme.shapes.extraSmall)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(ratio)
                    .fillMaxHeight()
                    .clip(MaterialTheme.shapes.extraSmall)
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
    }
}
