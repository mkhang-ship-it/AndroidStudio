package com.aistudy.aistudy.ui.screens.progress

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.aistudy.aistudy.R
import com.aistudy.aistudy.data.repository.ProgressSummary
import com.aistudy.aistudy.domain.model.enums.Subject
import com.aistudy.aistudy.domain.rules.ProgressInsight
import com.aistudy.aistudy.domain.rules.ProgressInsightKind
import com.aistudy.aistudy.domain.rules.ProgressInsights
import com.aistudy.aistudy.ui.common.EmptyState
import com.aistudy.aistudy.ui.common.LoadingBox
import com.aistudy.aistudy.ui.common.ScreenHeader
import com.aistudy.aistudy.ui.common.ScreenPager
import com.aistudy.aistudy.ui.common.StickerBadge
import com.aistudy.aistudy.ui.label
import com.aistudy.aistudy.ui.progress.ProgressUiState
import com.aistudy.aistudy.ui.progress.ProgressViewModel
import com.aistudy.aistudy.ui.root.AppDependencies
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
    ScreenPager(titleRes = R.string.progress_title) {
        when {
            state.isLoading -> LoadingBox(modifier = Modifier.fillMaxSize())
            state.hasError -> com.aistudy.aistudy.ui.common.ErrorBanner(
                message = state.errorMessage?.takeIf { it.isNotBlank() }
                    ?: stringResource(R.string.common_error_generic),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = Spacing.lg)
            )
            state.summary != null -> {
                val summary = state.summary
                if (summary.totalQuestions == 0) {
                    EmptyState(
                        titleRes = R.string.progress_empty_title,
                        bodyRes = R.string.progress_empty_body,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    ProgressSummaryContent(summary)
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

@Composable
private fun ProgressSummaryContent(summary: ProgressSummary) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    val accuracy = (summary.quizAccuracy * 100).toInt()
    val insights = remember(summary) {
        ProgressInsights.build(
            questionsBySubject = summary.questionsBySubject,
            quizAccuracy = summary.quizAccuracy,
            quizAnswered = summary.quizAnswered,
            quizRemaining = summary.quizRemaining,
            reviewMinutes = summary.reviewMinutes
        )
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(320)) + slideInVertically(tween(320)) { it / 20 }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            StatCard(
                label = R.string.progress_questions_total,
                value = summary.totalQuestions.toString()
            )
            StatCard(
                label = R.string.progress_review_time,
                value = reviewTimeText(summary),
                hint = stringResource(R.string.progress_review_time_hint)
            )
            StatCard(
                label = R.string.progress_quiz_accuracy,
                value = stringResource(R.string.progress_quiz_accuracy_value, accuracy)
            )
            StatTextCard(
                text = stringResource(
                    R.string.progress_quiz_progress,
                    summary.quizAnswered,
                    summary.quizTotal
                )
            )
            StickerBadge(
                text = stringResource(R.string.progress_xp_week_summary, summary.xpThisWeek),
                icon = {
                    Icon(
                        Icons.Filled.Star,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                }
            )

            if (insights.isNotEmpty()) {
                Spacer(modifier = Modifier.height(Spacing.xs))
                InsightsCard(insights)
            }

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

@Composable
private fun InsightsCard(
    insights: List<ProgressInsight>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(Spacing.lg)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Spacer(modifier = Modifier.width(Spacing.sm))
                Text(
                    text = stringResource(R.string.progress_insights_title),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            Spacer(modifier = Modifier.height(Spacing.sm))
            insights.forEach { insight ->
                Text(
                    text = "\u2022 " + insightText(insight),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(vertical = Spacing.xxs)
                )
            }
        }
    }
}

@Composable
private fun insightText(insight: ProgressInsight): String = when (insight.kind) {
    ProgressInsightKind.TOP_SUBJECT -> stringResource(
        R.string.progress_insight_top_subject,
        insight.subject?.label().orEmpty(),
        insight.value
    )

    ProgressInsightKind.LEAST_SUBJECT -> stringResource(
        R.string.progress_insight_least_subject,
        insight.subject?.label().orEmpty(),
        insight.value
    )

    ProgressInsightKind.LOW_ACCURACY -> stringResource(
        R.string.progress_insight_low_accuracy,
        insight.value
    )

    ProgressInsightKind.PRACTICE_REMAINING -> stringResource(
        R.string.progress_insight_practice_remaining,
        insight.value
    )

    ProgressInsightKind.REVIEW_TIME -> stringResource(
        R.string.progress_insight_review_time,
        insight.value
    )
}

@Composable
private fun reviewTimeText(summary: ProgressSummary): String = when {
    summary.totalReviewSeconds <= 0L -> stringResource(R.string.progress_review_time_empty)
    summary.reviewMinutes <= 0L -> stringResource(
        R.string.progress_review_time_seconds,
        summary.totalReviewSeconds
    )

    else -> stringResource(R.string.progress_review_time_value, summary.reviewMinutes)
}

@Composable
private fun StatCard(
    @androidx.annotation.StringRes label: Int,
    value: String,
    modifier: Modifier = Modifier,
    hint: String? = null
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.lg, vertical = Spacing.md)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
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
            if (hint != null) {
                Spacer(modifier = Modifier.height(Spacing.xxs))
                Text(
                    text = hint,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
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
    val animatedRatio = remember(count, maxCount) { Animatable(0f) }
    LaunchedEffect(ratio) {
        animatedRatio.animateTo(
            targetValue = ratio,
            animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
        )
    }

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
                    .fillMaxWidth(animatedRatio.value)
                    .fillMaxHeight()
                    .clip(MaterialTheme.shapes.extraSmall)
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
    }
}
