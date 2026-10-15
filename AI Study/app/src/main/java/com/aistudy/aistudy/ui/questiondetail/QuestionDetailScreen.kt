package com.aistudy.aistudy.ui.questiondetail

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import com.aistudy.aistudy.ui.theme.Spacing
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.aistudy.aistudy.R
import com.aistudy.aistudy.data.repository.ImprovementKind
import com.aistudy.aistudy.data.repository.QuestionRepository
import com.aistudy.aistudy.domain.model.entity.QuestionAnswer
import com.aistudy.aistudy.ui.common.DuoButton
import com.aistudy.aistudy.ui.common.DuoButtonColors
import com.aistudy.aistudy.ui.common.ErrorBanner
import com.aistudy.aistudy.ui.common.LoadingBox
import com.aistudy.aistudy.ui.common.MarkdownText
import com.aistudy.aistudy.ui.label
import com.aistudy.aistudy.ui.root.AppDependencies
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class QuestionDetailUiState(
    val question: QuestionAnswer? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val generatingKinds: Set<ImprovementKind> = emptySet()
)

class QuestionDetailViewModel(
    private val questionRepository: QuestionRepository,
    private val questionId: Long
) : ViewModel() {

    private val _state = MutableStateFlow(QuestionDetailUiState())
    val state: StateFlow<QuestionDetailUiState> = _state.asStateFlow()

    init {
        loadQuestion()
    }

    fun loadQuestion() {
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            val q = questionRepository.getQuestion(questionId)
            if (q != null) {
                _state.update { it.copy(question = q, isLoading = false) }
            } else {
                _state.update { it.copy(isLoading = false, errorMessage = "Question not found") }
            }
        }
    }

    fun toggleBookmark() {
        val q = _state.value.question ?: return
        val newValue = !q.isBookmarked
        _state.update { it.copy(question = q.copy(isBookmarked = newValue)) }
        viewModelScope.launch {
            questionRepository.setBookmarked(questionId, newValue)
        }
    }

    fun generateImprovement(kind: ImprovementKind) {
        val currentGenerating = _state.value.generatingKinds
        if (currentGenerating.contains(kind)) return

        _state.update { it.copy(generatingKinds = currentGenerating + kind) }

        viewModelScope.launch {
            val result = questionRepository.improve(questionId, kind)
            result.onSuccess { text ->
                val updatedQ = questionRepository.getQuestion(questionId)
                _state.update { s ->
                    s.copy(
                        question = updatedQ ?: s.question,
                        generatingKinds = s.generatingKinds - kind
                    )
                }
            }.onFailure { ex ->
                _state.update { s ->
                    s.copy(
                        generatingKinds = s.generatingKinds - kind,
                        errorMessage = ex.message ?: "Failed to generate content"
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionDetailScreen(
    deps: AppDependencies,
    questionId: Long,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val vm: QuestionDetailViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                QuestionDetailViewModel(
                    questionRepository = deps.questionRepository,
                    questionId = questionId
                )
            }
        }
    )
    val state by vm.state.collectAsStateWithLifecycle()

    // Track review time when screen is resumed and submit once on dispose
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()
    DisposableEffect(lifecycleOwner, questionId) {
        var startTime = System.currentTimeMillis()
        var accumulatedSeconds = 0L

        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    startTime = System.currentTimeMillis()
                }
                Lifecycle.Event.ON_PAUSE -> {
                    if (startTime > 0) {
                        accumulatedSeconds += (System.currentTimeMillis() - startTime) / 1000
                        startTime = 0
                    }
                }
                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            if (startTime > 0) {
                accumulatedSeconds += (System.currentTimeMillis() - startTime) / 1000
            }
            if (accumulatedSeconds > 0) {
                val secondsToAdd = accumulatedSeconds.toInt()
                coroutineScope.launch {
                    deps.questionRepository.addReviewTime(questionId, secondsToAdd)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(
                    stringResource(R.string.detail_title),
                    style = MaterialTheme.typography.headlineSmall
                ) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.common_back)
                        )
                    }
                },
                actions = {
                    val q = state.question
                    if (q != null) {
                        IconButton(onClick = vm::toggleBookmark) {
                            Icon(
                                imageVector = if (q.isBookmarked) {
                                    Icons.Filled.Star
                                } else {
                                    Icons.Outlined.Star
                                },
                                contentDescription = stringResource(
                                    if (q.isBookmarked) {
                                        R.string.history_bookmark_remove
                                    } else {
                                        R.string.history_bookmark_add
                                    }
                                ),
                                tint = if (q.isBookmarked) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                        }
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                state.isLoading -> {
                    LoadingBox()
                }
                state.question == null -> {
                    ErrorBanner(
                        message = state.errorMessage ?: stringResource(R.string.detail_not_found),
                        onRetry = vm::loadQuestion
                    )
                }
                else -> {
                    val q = state.question!!
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(Spacing.lg)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = q.subjectCode.label(),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                            val formattedDate = remember(q.createdAt) {
                                SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(q.createdAt))
                            }
                            Text(
                                text = stringResource(R.string.detail_asked_at, formattedDate),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(Spacing.lg))

                        // Question section
                        Text(
                            text = stringResource(R.string.detail_question_label),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(Spacing.xs))
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                            ),
                            shape = MaterialTheme.shapes.large,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = q.questionText.ifBlank { stringResource(R.string.ask_image_attached) },
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(Spacing.lg)
                            )
                        }

                        Spacer(modifier = Modifier.height(Spacing.lg))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        Spacer(modifier = Modifier.height(Spacing.lg))

                        // Answer section
                        Text(
                            text = stringResource(R.string.detail_answer_label),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(Spacing.xs))
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
                            ),
                            shape = MaterialTheme.shapes.large,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            MarkdownText(
                                text = q.answerText,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(Spacing.lg)
                            )
                        }

                        Spacer(modifier = Modifier.height(Spacing.lg))

                        val reviewMinutes = (q.reviewTimeSeconds / 60)
                        Text(
                            text = stringResource(R.string.detail_time_spent, "$reviewMinutes min"),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(Spacing.lg))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        Spacer(modifier = Modifier.height(Spacing.lg))

                        // FR5: 5 Improvement Blocks
                        Text(
                            text = stringResource(R.string.improve_title),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(Spacing.md))

                        ImprovementBlockItem(
                            kind = ImprovementKind.SIMPLIFIED_EXPLANATION,
                            content = q.simplifiedExplanation,
                            isGenerating = state.generatingKinds.contains(ImprovementKind.SIMPLIFIED_EXPLANATION),
                            onGenerate = { vm.generateImprovement(ImprovementKind.SIMPLIFIED_EXPLANATION) }
                        )

                        ImprovementBlockItem(
                            kind = ImprovementKind.ALTERNATIVE_APPROACH,
                            content = q.alternativeApproach,
                            isGenerating = state.generatingKinds.contains(ImprovementKind.ALTERNATIVE_APPROACH),
                            onGenerate = { vm.generateImprovement(ImprovementKind.ALTERNATIVE_APPROACH) }
                        )

                        ImprovementBlockItem(
                            kind = ImprovementKind.KEY_CONCEPTS,
                            content = q.keyConcepts,
                            isGenerating = state.generatingKinds.contains(ImprovementKind.KEY_CONCEPTS),
                            onGenerate = { vm.generateImprovement(ImprovementKind.KEY_CONCEPTS) }
                        )

                        ImprovementBlockItem(
                            kind = ImprovementKind.COMMON_MISTAKES,
                            content = q.commonMistakes,
                            isGenerating = state.generatingKinds.contains(ImprovementKind.COMMON_MISTAKES),
                            onGenerate = { vm.generateImprovement(ImprovementKind.COMMON_MISTAKES) }
                        )

                        ImprovementBlockItem(
                            kind = ImprovementKind.FOLLOW_UP_QUESTIONS,
                            content = q.followUpQuestions,
                            isGenerating = state.generatingKinds.contains(ImprovementKind.FOLLOW_UP_QUESTIONS),
                            onGenerate = { vm.generateImprovement(ImprovementKind.FOLLOW_UP_QUESTIONS) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ImprovementBlockItem(
    kind: ImprovementKind,
    content: String?,
    isGenerating: Boolean,
    onGenerate: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.xs)
    ) {
        Column(modifier = Modifier.padding(Spacing.lg)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = kind.label(),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (isGenerating) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(Spacing.xs))
                        Text(
                            text = stringResource(R.string.improve_loading),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(Spacing.sm))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(Spacing.sm))

            if (!content.isNullOrBlank()) {
                MarkdownText(
                    text = content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(Spacing.sm))
                DuoButton(
                    text = stringResource(R.string.improve_regenerate),
                    onClick = onGenerate,
                    modifier = Modifier.align(Alignment.End),
                    enabled = !isGenerating,
                    colors = DuoButtonColors.Secondary
                )
            } else {
                Text(
                    text = stringResource(R.string.improve_empty),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(Spacing.sm))
                DuoButton(
                    text = stringResource(R.string.improve_generate),
                    onClick = onGenerate,
                    modifier = Modifier.align(Alignment.End),
                    enabled = !isGenerating,
                    colors = DuoButtonColors.Primary
                )
            }
        }
    }
}