package com.aistudy.aistudy.ui.screens.ask

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.aistudy.aistudy.ui.theme.Spacing
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.aistudy.aistudy.R
import com.aistudy.aistudy.data.repository.AuthRepository
import com.aistudy.aistudy.data.repository.ProfileRepository
import com.aistudy.aistudy.data.repository.QuestionRepository
import com.aistudy.aistudy.domain.ai.AiException
import com.aistudy.aistudy.domain.model.entity.QuestionAnswer
import com.aistudy.aistudy.domain.rules.QuestionSimilarity
import com.aistudy.aistudy.platform.ServiceLocator
import com.aistudy.aistudy.ui.common.ErrorBanner
import com.aistudy.aistudy.ui.common.DuoButton
import com.aistudy.aistudy.ui.common.MascotMood
import com.aistudy.aistudy.ui.common.PaperSheet
import com.aistudy.aistudy.ui.common.ScreenHeader
import com.aistudy.aistudy.ui.common.StudyMascot
import com.aistudy.aistudy.ui.common.ThinkingDots
import com.aistudy.aistudy.ui.common.TypewriterMarkdown
import com.aistudy.aistudy.ui.label
import com.aistudy.aistudy.ui.root.AppDependencies
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class AskUiState(
    val questionText: String = "",
    val selectedImageUri: Uri? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val lastResult: QuestionAnswer? = null
)

class AskViewModel(
    private val questionRepository: QuestionRepository,
    private val authRepository: AuthRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AskUiState())
    val state: StateFlow<AskUiState> = _state.asStateFlow()

    fun onTextChange(text: String) {
        _state.update {
            it.copy(
                questionText = text,
                errorMessage = null
            )
        }
    }

    fun onImagePicked(uri: Uri?) {
        _state.update { it.copy(selectedImageUri = uri, errorMessage = null) }
    }

    fun clearInput() {
        _state.update {
            it.copy(
                questionText = "",
                selectedImageUri = null,
                errorMessage = null
            )
        }
    }

    fun submitQuestion(context: android.content.Context) {
        val s = _state.value
        val text = s.questionText.trim()
        val imageUri = s.selectedImageUri

        if (text.isBlank() && imageUri == null) {
            _state.update { it.copy(errorMessage = context.getString(R.string.ask_error_empty)) }
            return
        }

        if (text.length > QuestionSimilarity.MAX_QUESTION_LENGTH) {
            _state.update {
                it.copy(
                    errorMessage = context.getString(
                        R.string.ask_error_too_long,
                        QuestionSimilarity.MAX_QUESTION_LENGTH
                    )
                )
            }
            return
        }

        val userId = authRepository.sessionUserId.value ?: return

        _state.update { it.copy(isLoading = true, errorMessage = null, lastResult = null) }

        viewModelScope.launch {
            val result = if (imageUri != null) {
                val stored = withContext(Dispatchers.IO) {
                    runCatching { ServiceLocator.imageStore.store(context, imageUri) }.getOrNull()
                }
                questionRepository.askWithImage(
                    userId = userId,
                    text = text,
                    imagePath = stored?.path,
                    imageIv = stored?.iv
                )
            } else {
                questionRepository.ask(userId, text)
            }

            result.onSuccess { qa ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        questionText = "",
                        selectedImageUri = null,
                        lastResult = qa,
                        errorMessage = null
                    )
                }
            }.onFailure { ex ->
                val errorMsg = mapAiExceptionToString(context, ex)
                _state.update { it.copy(isLoading = false, errorMessage = errorMsg) }
            }
        }
    }

    private fun mapAiExceptionToString(context: android.content.Context, ex: Throwable): String {
        return when (ex) {
            is AiException.NotConfigured -> context.getString(R.string.ask_error_not_configured)
            is AiException.Provider -> context.getString(R.string.ask_error_provider, ex.message ?: "")
            is AiException.Network -> context.getString(R.string.ask_error_network)
            is AiException.Malformed -> context.getString(R.string.ask_error_malformed)
            else -> ex.message ?: context.getString(R.string.common_error_generic)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AskScreen(
    deps: AppDependencies,
    modifier: Modifier = Modifier
) {
    val vm: AskViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                AskViewModel(
                    questionRepository = deps.questionRepository,
                    authRepository = deps.authRepository,
                    profileRepository = deps.profileRepository
                )
            }
        }
    )
    val state by vm.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val lifecycleOwner = LocalLifecycleOwner.current
    var isResumed by remember {
        mutableStateOf(
            lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)
        )
    }
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            isResumed = event == Lifecycle.Event.ON_RESUME
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // Ghi nhan thoi gian on tap khi nguoi dung doc cau tra loi. Cong theo tung
    // khoi 15 giay de khong mat du lieu khi roi man hinh dot ngot; dung lai khi
    // app xuong background de so lieu khong bi thoi phong.
    LaunchedEffect(state.lastResult?.id, isResumed) {
        val questionId = state.lastResult?.id ?: return@LaunchedEffect
        if (!isResumed) return@LaunchedEffect
        while (true) {
            delay(15_000)
            deps.questionRepository.addReviewTime(questionId, 15)
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        vm.onImagePicked(uri)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = Spacing.lg, vertical = Spacing.lg)
    ) {
        PaperSheet {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ScreenHeader(
                    titleRes = R.string.ask_title,
                    modifier = Modifier.weight(1f)
                )
                StudyMascot(
                    mood = when {
                        state.isLoading -> MascotMood.THINKING
                        state.lastResult != null -> MascotMood.CELEBRATING
                        else -> MascotMood.IDLE
                    },
                    modifier = Modifier.size(96.dp)
                )
            }

            Spacer(modifier = Modifier.height(Spacing.lg))
            if (!deps.aiClient.isConfigured) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                    ),
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = Spacing.lg)
                ) {
                    Column(modifier = Modifier.padding(Spacing.md)) {
                        Text(
                            text = stringResource(R.string.ask_fake_mode_banner),
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = stringResource(R.string.ask_fake_mode_body),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(Spacing.lg))

            OutlinedTextField(
                value = state.questionText,
                onValueChange = vm::onTextChange,
                label = { Text(stringResource(R.string.ask_input_label)) },
                placeholder = { Text(stringResource(R.string.ask_input_hint)) },
                minLines = 4,
                maxLines = 8,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            )

            if (state.questionText.isBlank() && !state.isLoading && state.lastResult == null) {
                Spacer(modifier = Modifier.height(Spacing.sm))
                AskSuggestionRow(onPick = vm::onTextChange)
            }

            Spacer(modifier = Modifier.height(Spacing.sm))

            if (state.selectedImageUri != null) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                    ),
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = Spacing.md)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Spacing.md),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            ThumbnailPreview(uri = state.selectedImageUri!!)
                            Spacer(modifier = Modifier.width(Spacing.md))
                            Text(
                                text = stringResource(R.string.ask_image_attached),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        TextButton(onClick = { vm.onImagePicked(null) }) {
                            Text(stringResource(R.string.ask_remove_image))
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                OutlinedButton(
                    onClick = {
                        imagePickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        if (state.selectedImageUri == null)
                            stringResource(R.string.ask_pick_image)
                        else
                            stringResource(R.string.ask_change_image)
                    )
                }

                if (state.questionText.isNotEmpty() || state.selectedImageUri != null) {
                    TextButton(onClick = vm::clearInput) {
                        Text(stringResource(R.string.ask_clear))
                    }
                }
            }

            Spacer(modifier = Modifier.height(Spacing.lg))

            if (state.errorMessage != null) {
                ErrorBanner(
                    message = state.errorMessage!!,
                    modifier = Modifier.padding(bottom = Spacing.lg)
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp),
                contentAlignment = Alignment.Center
            ) {
                DuoButton(
                    text = if (state.isLoading) stringResource(R.string.ask_answer_loading)
                    else stringResource(R.string.ask_submit),
                    onClick = { vm.submitQuestion(context) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isLoading && (state.questionText.isNotBlank() || state.selectedImageUri != null)
                )
            }

            AnimatedVisibility(
                visible = state.isLoading,
                enter = fadeIn(tween(220)) + expandVertically(tween(260)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                    ),
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = Spacing.md)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Spacing.md),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ThinkingDots()
                        Spacer(modifier = Modifier.width(Spacing.md))
                        Column {
                            Text(
                                text = stringResource(R.string.ask_thinking_title),
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = stringResource(R.string.ask_thinking_body),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Answer Result Display Section
            AnimatedVisibility(
                visible = state.lastResult != null,
                enter = fadeIn(tween(280)) +
                    expandVertically(tween(320)) +
                    slideInVertically(tween(320)) { it / 4 }
            ) {
                val qa = state.lastResult ?: return@AnimatedVisibility
                Spacer(modifier = Modifier.height(Spacing.xl))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                    ),
                    shape = MaterialTheme.shapes.large,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(Spacing.lg)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = stringResource(R.string.ask_subject_detected_label),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.width(Spacing.xs))
                                AssistChip(onClick = {}, label = { Text(qa.subjectCode.label()) })
                                Spacer(modifier = Modifier.width(Spacing.sm))
                                Text(
                                    text = stringResource(R.string.ask_saved_hint),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            if (qa.servedFromCache) {
                                AssistChip(
                                    onClick = {},
                                    label = { Text(stringResource(R.string.ask_cached_badge)) }
                                )
                            }
                        }

                        if (qa.servedFromCache) {
                            Spacer(modifier = Modifier.height(Spacing.xs))
                            Text(
                                text = stringResource(R.string.ask_matched_note),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(Spacing.sm))

                        if (qa.detectedDifficulty != null) {
                            Text(
                                text = "${stringResource(R.string.ask_difficulty_label)}: ${qa.detectedDifficulty.label()}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Spacer(modifier = Modifier.height(Spacing.sm))
                        }

                        Surface(
                            color = MaterialTheme.colorScheme.surfaceContainerHighest,
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            TypewriterMarkdown(
                                text = qa.answerText,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(Spacing.md)
                            )
                        }
                    }
                }
            }
        }
        }
    }
}

@Composable
private fun AskSuggestionRow(
    onPick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val suggestions = listOf(
        stringResource(R.string.ask_suggestion_math),
        stringResource(R.string.ask_suggestion_derivative),
        stringResource(R.string.ask_suggestion_code)
    )
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.ask_suggestion_title),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(Spacing.xs))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            suggestions.forEach { suggestion ->
                AssistChip(
                    onClick = { onPick(suggestion) },
                    label = {
                        Text(
                            text = suggestion,
                            maxLines = 1
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun ThumbnailPreview(uri: Uri) {
    val context = LocalContext.current
    var bitmap by remember(uri) { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(uri) {
        withContext(Dispatchers.IO) {
            runCatching {
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    BitmapFactory.decodeStream(stream)
                }
            }.getOrNull()?.let { loaded ->
                bitmap = loaded
            }
        }
    }

    if (bitmap != null) {
        Image(
            bitmap = bitmap!!.asImageBitmap(),
            contentDescription = stringResource(R.string.ask_image_attached),
            modifier = Modifier.size(48.dp)
        )
    } else {
        Box(modifier = Modifier.size(48.dp))
    }
}
