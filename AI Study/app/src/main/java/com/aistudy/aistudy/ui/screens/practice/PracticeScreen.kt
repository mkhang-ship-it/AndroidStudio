package com.aistudy.aistudy.ui.screens.practice

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.aistudy.aistudy.R
import com.aistudy.aistudy.domain.model.enums.QuestionType
import com.aistudy.aistudy.ui.common.DuoButton
import com.aistudy.aistudy.ui.common.DuoProgress
import com.aistudy.aistudy.ui.common.EmptyState
import com.aistudy.aistudy.ui.common.ErrorBanner
import com.aistudy.aistudy.ui.common.MascotMood
import com.aistudy.aistudy.ui.common.MarkdownText
import com.aistudy.aistudy.ui.common.MarkerChip
import com.aistudy.aistudy.ui.common.PaperSheet
import com.aistudy.aistudy.ui.common.ScreenHeader
import com.aistudy.aistudy.ui.common.StickerBadge
import com.aistudy.aistudy.ui.common.StudyMascot
import com.aistudy.aistudy.ui.label
import com.aistudy.aistudy.ui.practice.PracticeUiState
import com.aistudy.aistudy.ui.practice.PracticeViewModel
import com.aistudy.aistudy.ui.practice.answerOptions
import com.aistudy.aistudy.ui.root.AppDependencies
import com.aistudy.aistudy.ui.theme.Spacing
import com.aistudy.aistudy.ui.theme.AppMotion

@Composable
fun PracticeScreen(
    deps: AppDependencies,
    modifier: Modifier = Modifier
) {
    val viewModel: PracticeViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                PracticeViewModel(
                    quizRepository = deps.quizRepository,
                    questionRepository = deps.questionRepository,
                    gamificationRepository = deps.gamificationRepository
                )
            }
        }
    )
    val state by viewModel.state.collectAsStateWithLifecycle()
    val sessionUserId by deps.authRepository.sessionUserId
        .collectAsStateWithLifecycle(initialValue = null)

    LaunchedEffect(sessionUserId) {
        sessionUserId?.let(viewModel::load)
    }

    PracticeContent(
        state = state,
        onGenerate = { sessionUserId?.let { viewModel.generatePractice(it) } },
        onSourceSelected = viewModel::selectSource,
        onCountSelected = viewModel::setCount,
        onAnswerChange = viewModel::setUserAnswer,
        onSubmit = viewModel::submitAnswer,
        onNext = viewModel::nextQuestion,
        onRestart = viewModel::restart,
        modifier = modifier
    )
}

@Composable
private fun PracticeContent(
    state: PracticeUiState,
    onGenerate: () -> Unit,
    onSourceSelected: (Long?) -> Unit,
    onCountSelected: (Int) -> Unit,
    onAnswerChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onNext: () -> Unit,
    onRestart: () -> Unit,
    modifier: Modifier = Modifier
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
                ScreenHeader(titleRes = R.string.practice_title)

                Spacer(modifier = Modifier.height(Spacing.lg))

                when {
                    state.isLoading -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = Spacing.xl),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator()
                            Spacer(Modifier.height(Spacing.md))
                            Text(
                                text = stringResource(R.string.practice_generating),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    state.hasError -> ErrorBanner(
                        message = state.errorMessage?.takeIf { it.isNotBlank() }
                            ?: stringResource(R.string.common_error_generic),
                        onRetry = onGenerate,
                        modifier = Modifier.fillMaxWidth()
                    )

                    state.isFinished -> ResultState(state = state, onRestart = onRestart)

                    state.currentItem != null -> QuestionState(
                        state = state,
                        onAnswerChange = onAnswerChange,
                        onSubmit = onSubmit,
                        onNext = onNext
                    )

                    else -> GenerateState(
                        state = state,
                        onGenerate = onGenerate,
                        onSourceSelected = onSourceSelected,
                        onCountSelected = onCountSelected
                    )
                }
            }
        }
    }
}

@Composable
private fun ResultState(state: PracticeUiState, onRestart: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.practice_result_title),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(Spacing.xl))

        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                progress = { state.accuracy / 100f },
                modifier = Modifier.size(160.dp),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 12.dp,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${state.accuracy}%",
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = stringResource(
                        R.string.practice_result_score,
                        state.correctCount,
                        state.progressTotal
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(Modifier.height(Spacing.xl))

        StickerBadge(
            text = stringResource(R.string.practice_result_xp, state.xpGained),
            icon = {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary
                )
            }
        )

        Spacer(Modifier.height(Spacing.xl))

        Button(
            onClick = onRestart,
            modifier = Modifier
                .fillMaxWidth()
                .sizeIn(minHeight = 48.dp)
        ) {
            Text(stringResource(R.string.practice_result_done))
        }
    }
}

@Composable
private fun QuestionState(
    state: PracticeUiState,
    onAnswerChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onNext: () -> Unit
) {
    val item = state.currentItem ?: return
    val isCorrect = state.userAnswer.trim().equals(item.correctAnswer.trim(), ignoreCase = true)

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(
                    R.string.practice_progress,
                    state.progressIndex,
                    state.progressTotal
                ),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            TypeChip(text = item.questionType.label())
        }
        Spacer(Modifier.height(Spacing.sm))
        DuoProgress(
            progress = {
                state.progressIndex.toFloat() / state.progressTotal.coerceAtLeast(1).toFloat()
            },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(Spacing.lg))

        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
            ),
            shape = MaterialTheme.shapes.large,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(Spacing.lg)) {
                MarkdownText(
                    text = item.prompt,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
        Spacer(Modifier.height(Spacing.lg))

        val options = item.answerOptions
        if (item.questionType == QuestionType.MULTIPLE_CHOICE && options.isNotEmpty()) {
            options.forEach { (index, optionText) ->
                OptionCard(
                    letter = stringResource(OPTION_LETTER_RES[index]),
                    text = optionText,
                    isChosen = state.userAnswer.trim().equals(optionText.trim(), ignoreCase = true),
                    isCorrectOption = optionText.trim().equals(item.correctAnswer.trim(), ignoreCase = true),
                    submitted = state.submitted,
                    onClick = { onAnswerChange(optionText) }
                )
                Spacer(Modifier.height(Spacing.sm))
            }
        } else {
            OutlinedTextField(
                value = state.userAnswer,
                onValueChange = onAnswerChange,
                enabled = !state.submitted,
                label = { Text(stringResource(R.string.practice_your_answer)) },
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(Modifier.height(Spacing.md))

        if (!state.submitted) {
            DuoButton(
                text = stringResource(R.string.practice_submit_answer),
                onClick = onSubmit,
                modifier = Modifier
                    .fillMaxWidth()
                    .sizeIn(minHeight = 48.dp),
                enabled = state.userAnswer.isNotBlank()
            )
            if (state.userAnswer.isBlank()) {
                Spacer(Modifier.height(Spacing.xs))
                Text(
                    text = stringResource(R.string.practice_answer_required),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            FeedbackCard(
                isCorrect = isCorrect,
                correctAnswer = item.correctAnswer,
                explanation = item.explanation
            )
            Spacer(Modifier.height(Spacing.lg))
            DuoButton(
                text = stringResource(R.string.practice_next),
                onClick = onNext,
                modifier = Modifier
                    .fillMaxWidth()
                    .sizeIn(minHeight = 48.dp)
            )
        }
    }
}

@Composable
private fun OptionCard(
    letter: String,
    text: String,
    isChosen: Boolean,
    isCorrectOption: Boolean,
    submitted: Boolean,
    onClick: () -> Unit
) {
    val container = when {
        submitted && isCorrectOption -> MaterialTheme.colorScheme.primaryContainer
        submitted && isChosen -> MaterialTheme.colorScheme.errorContainer
        isChosen -> MaterialTheme.colorScheme.secondaryContainer
        else -> MaterialTheme.colorScheme.surface
    }
    val borderColor = when {
        submitted && isCorrectOption -> MaterialTheme.colorScheme.primary
        submitted && isChosen -> MaterialTheme.colorScheme.error
        isChosen -> MaterialTheme.colorScheme.secondary
        else -> MaterialTheme.colorScheme.outlineVariant
    }
    val content = when {
        submitted && isCorrectOption -> MaterialTheme.colorScheme.onPrimaryContainer
        submitted && isChosen -> MaterialTheme.colorScheme.onErrorContainer
        isChosen -> MaterialTheme.colorScheme.onSecondaryContainer
        else -> MaterialTheme.colorScheme.onSurface
    }
    val badgeColor = when {
        submitted && isCorrectOption -> MaterialTheme.colorScheme.primary
        submitted && isChosen -> MaterialTheme.colorScheme.error
        isChosen -> MaterialTheme.colorScheme.secondary
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    val onBadgeColor = when {
        submitted && isCorrectOption -> MaterialTheme.colorScheme.onPrimary
        submitted && isChosen -> MaterialTheme.colorScheme.onError
        isChosen -> MaterialTheme.colorScheme.onSecondary
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        onClick = onClick,
        enabled = !submitted,
        colors = CardDefaults.cardColors(containerColor = container),
        border = BorderStroke(width = 1.dp, color = borderColor),
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .fillMaxWidth()
            .sizeIn(minHeight = 56.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.md, vertical = Spacing.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(color = badgeColor, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = letter,
                    style = MaterialTheme.typography.labelLarge,
                    color = onBadgeColor
                )
            }
            Spacer(Modifier.width(Spacing.md))
            MarkdownText(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                color = content,
                modifier = Modifier.weight(1f)
            )
            when {
                submitted && isCorrectOption -> Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                submitted && isChosen -> Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun FeedbackCard(
    isCorrect: Boolean,
    correctAnswer: String,
    explanation: String?
) {
    val containerColor = if (isCorrect) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.errorContainer
    }
    val onContainerColor = if (isCorrect) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onErrorContainer
    }
    val hasBack = !explanation.isNullOrBlank()
    var flipped by remember(isCorrect, correctAnswer, explanation) { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (flipped) 180f else 0f,
        animationSpec = AppMotion.bouncyFlip(),
        label = "feedbackFlip"
    )
    Card(
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f
            }
            .then(if (hasBack) Modifier.clickable { flipped = !flipped } else Modifier)
    ) {
        if (rotation <= 90f) {
            Column(modifier = Modifier.padding(Spacing.lg)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isCorrect) Icons.Filled.CheckCircle else Icons.Filled.Warning,
                        contentDescription = null,
                        tint = onContainerColor
                    )
                    Spacer(Modifier.width(Spacing.sm))
                    Text(
                        text = stringResource(
                            if (isCorrect) R.string.practice_correct else R.string.practice_incorrect
                        ),
                        style = MaterialTheme.typography.titleMedium,
                        color = onContainerColor,
                        modifier = Modifier.weight(1f)
                    )
                    StudyMascot(
                        mood = if (isCorrect) MascotMood.CELEBRATING else MascotMood.THINKING,
                        modifier = Modifier.size(64.dp)
                    )
                }
                if (!isCorrect) {
                    Spacer(Modifier.height(Spacing.xs))
                    MarkdownText(
                        text = stringResource(R.string.practice_correct_answer, correctAnswer),
                        style = MaterialTheme.typography.bodyMedium,
                        color = onContainerColor
                    )
                }
                if (hasBack) {
                    Spacer(Modifier.height(Spacing.xs))
                    Text(
                        text = stringResource(R.string.practice_tap_for_explanation),
                        style = MaterialTheme.typography.bodySmall,
                        color = onContainerColor.copy(alpha = 0.7f)
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .graphicsLayer { rotationY = 180f }
                    .padding(Spacing.lg)
            ) {
                Text(
                    text = stringResource(R.string.practice_explanation),
                    style = MaterialTheme.typography.labelMedium,
                    color = onContainerColor
                )
                MarkdownText(
                    text = explanation.orEmpty(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = onContainerColor
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GenerateState(
    state: PracticeUiState,
    onGenerate: () -> Unit,
    onSourceSelected: (Long?) -> Unit,
    onCountSelected: (Int) -> Unit
) {
    if (state.sources.isEmpty()) {
        EmptyState(
            titleRes = R.string.practice_empty_title,
            bodyRes = R.string.practice_pick_source_empty
        )
        return
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.height(Spacing.lg))

        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
            ),
            shape = MaterialTheme.shapes.large,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(Spacing.lg)) {
                SourcePicker(state = state, onSourceSelected = onSourceSelected)

                Spacer(Modifier.height(Spacing.lg))

                CountPicker(state = state, onCountSelected = onCountSelected)
            }
        }

        Spacer(Modifier.height(Spacing.xl))

        Button(
            onClick = onGenerate,
            modifier = Modifier
                .fillMaxWidth()
                .sizeIn(minHeight = 48.dp)
        ) {
            Icon(Icons.Filled.PlayArrow, contentDescription = null)
            Spacer(Modifier.width(Spacing.sm))
            Text(stringResource(R.string.practice_generate))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SourcePicker(
    state: PracticeUiState,
    onSourceSelected: (Long?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selected = state.sources.firstOrNull { it.id == state.sourceId }
    val placeholder = stringResource(R.string.practice_pick_source)

    Column {
        Text(
            text = placeholder,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(Spacing.xs))
        Box {
            OutlinedButton(
                onClick = { expanded = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .sizeIn(minHeight = 48.dp)
            ) {
                Text(
                    text = selected?.questionText?.take(MAX_SOURCE_LABEL) ?: placeholder,
                    maxLines = 1
                )
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text(placeholder, maxLines = 1) },
                    onClick = {
                        onSourceSelected(null)
                        expanded = false
                    }
                )
                state.sources.forEach { source ->
                    DropdownMenuItem(
                        text = { Text(source.questionText.take(MAX_SOURCE_LABEL), maxLines = 1) },
                        onClick = {
                            onSourceSelected(source.id)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CountPicker(state: PracticeUiState, onCountSelected: (Int) -> Unit) {
    var countText by remember(state.count) { mutableStateOf(state.count.toString()) }

    Column {
        Text(
            text = stringResource(R.string.practice_count_label),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(Spacing.xs))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = { onCountSelected(state.count - 1) },
                enabled = state.count > PracticeUiState.MIN_COUNT,
                modifier = Modifier.sizeIn(minWidth = 52.dp, minHeight = 52.dp)
            ) {
                Text("\u2212", style = MaterialTheme.typography.titleLarge)
            }

            OutlinedTextField(
                value = countText,
                onValueChange = { raw ->
                    val digits = raw.filter { it.isDigit() }.take(2)
                    countText = digits
                    digits.toIntOrNull()?.let { value ->
                        if (value in PracticeUiState.MIN_COUNT..PracticeUiState.MAX_COUNT) {
                            onCountSelected(value)
                        }
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                textStyle = MaterialTheme.typography.titleMedium.copy(textAlign = TextAlign.Center),
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.width(88.dp)
            )

            OutlinedButton(
                onClick = { onCountSelected(state.count + 1) },
                enabled = state.count < PracticeUiState.MAX_COUNT,
                modifier = Modifier.sizeIn(minWidth = 52.dp, minHeight = 52.dp)
            ) {
                Text("+", style = MaterialTheme.typography.titleLarge)
            }

            Text(
                text = stringResource(R.string.practice_count_unit),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(Modifier.height(Spacing.xs))
        Text(
            text = stringResource(
                R.string.practice_count_hint,
                PracticeUiState.MIN_COUNT,
                PracticeUiState.MAX_COUNT
            ),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(Spacing.sm))
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            COUNT_OPTIONS.forEach { option ->
                MarkerChip(
                    label = option.toString(),
                    selected = option == state.count,
                    onClick = { onCountSelected(option) }
                )
            }
        }
    }
}

@Composable
private fun TypeChip(text: String) {
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = Spacing.sm, vertical = Spacing.xs)
        )
    }
}

private val OPTION_LETTER_RES = listOf(
    R.string.practice_option_a,
    R.string.practice_option_b,
    R.string.practice_option_c,
    R.string.practice_option_d
)

private const val MAX_SOURCE_LABEL = 60
private val COUNT_OPTIONS = listOf(3, 5, 10)
