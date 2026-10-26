package com.aistudy.aistudy.ui.screens.history

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.viewmodel.initializer
import com.aistudy.aistudy.R
import com.aistudy.aistudy.domain.model.entity.QuestionAnswer
import com.aistudy.aistudy.ui.common.ConfirmDialog
import com.aistudy.aistudy.ui.common.MarkdownText
import com.aistudy.aistudy.ui.common.EmptyState
import com.aistudy.aistudy.ui.common.ErrorBanner
import com.aistudy.aistudy.ui.common.LoadingBox
import com.aistudy.aistudy.ui.common.MarkerChip
import com.aistudy.aistudy.ui.common.PaperSheet
import com.aistudy.aistudy.ui.common.ScreenHeader
import com.aistudy.aistudy.ui.common.SubjectChipRow
import com.aistudy.aistudy.ui.history.HistoryUiState
import com.aistudy.aistudy.ui.history.HistoryViewModel
import com.aistudy.aistudy.ui.root.AppDependencies
import com.aistudy.aistudy.ui.label
import com.aistudy.aistudy.ui.theme.Spacing

@Composable
fun HistoryScreen(
    deps: AppDependencies,
    onOpenQuestion: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val vm: HistoryViewModel = viewModel(
        factory = viewModelFactory {
            initializer { HistoryViewModel(deps.questionRepository) }
        }
    )
    val state by vm.state.collectAsStateWithLifecycle()

    val sessionUserId by deps.authRepository.sessionUserId.collectAsStateWithLifecycle(initialValue = null)
    androidx.compose.runtime.LaunchedEffect(sessionUserId) {
        sessionUserId?.let { vm.load(it) }
    }

    HistoryContent(
        state = state,
        onOpenQuestion = onOpenQuestion,
        onSearch = vm::setSearch,
        onToggleFilter = vm::setFilterBookmarked,
        onToggleSubject = vm::toggleSubject,
        onClickAll = vm::clearSubjects,
        onToggleBookmark = { id, value -> vm.toggleBookmark(id, value) },
        onToggleSelectionMode = vm::setSelectionMode,
        onToggleSelect = vm::toggleSelected,
        onSelectAll = vm::selectAll,
        onDeleteSelected = vm::deleteSelected,
        onAskAgain = vm::askAgain,
        onDeleteOne = vm::deleteOne,
        onDismissReask = vm::dismissReask,
        modifier = modifier
    )
}

@Composable
private fun HistoryContent(
    state: HistoryUiState,
    onOpenQuestion: (Long) -> Unit,
    onSearch: (String) -> Unit,
    onToggleFilter: (Boolean) -> Unit,
    onToggleSubject: (com.aistudy.aistudy.domain.model.enums.Subject) -> Unit,
    onClickAll: () -> Unit,
    onToggleBookmark: (Long, Boolean) -> Unit,
    onToggleSelectionMode: (Boolean) -> Unit,
    onToggleSelect: (Long) -> Unit,
    onSelectAll: () -> Unit,
    onDeleteSelected: () -> Unit,
    onAskAgain: (QuestionAnswer) -> Unit,
    onDeleteOne: (Long) -> Unit,
    onDismissReask: () -> Unit,
    modifier: Modifier = Modifier
) {
    val subjects = state.subjects
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = Spacing.lg, vertical = Spacing.lg)
    ) {
        PaperSheet {
            ScreenHeader(titleRes = R.string.history_title)

            Spacer(modifier = Modifier.height(Spacing.lg))

            OutlinedTextField(
                value = state.searchKeyword,
                onValueChange = onSearch,
                label = { Text(stringResource(R.string.history_search_hint)) },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                singleLine = true,
                shape = MaterialTheme.shapes.large,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(Spacing.md))

            // Bộ lọc: Tất cả / Đã lưu (giữ nguyên) + các môn có dữ liệu.
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                MarkerChip(
                    label = stringResource(R.string.history_filter_all),
                    selected = !state.filterBookmarked && state.selectedSubjects.isEmpty(),
                    onClick = onClickAll
                )
                MarkerChip(
                    label = stringResource(R.string.history_filter_bookmarked),
                    selected = state.filterBookmarked,
                    onClick = { onToggleFilter(!state.filterBookmarked) }
                )
            }

            if (subjects.isNotEmpty()) {
                Spacer(modifier = Modifier.height(Spacing.sm))
                SubjectChipRow(
                    subjects = subjects,
                    selected = state.selectedSubjects,
                    onToggle = onToggleSubject
                )
            }
        }

        Spacer(modifier = Modifier.height(Spacing.sm))

        if (state.selectionMode) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                ) {
                    val allSelected = state.selectedIds.size == state.items.size && state.items.isNotEmpty()
                    TextButton(onClick = onSelectAll) {
                        Text(
                            stringResource(
                                if (allSelected) R.string.history_deselect_all else R.string.history_select_all
                            )
                        )
                    }
                    Text(
                        text = stringResource(R.string.history_selected_count, state.selectedIds.size),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                    TextButton(onClick = { showDeleteConfirm = true }) {
                        Text(
                            stringResource(R.string.common_delete),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    TextButton(onClick = { onToggleSelectionMode(false) }) {
                        Text(stringResource(R.string.common_cancel))
                    }
                }
            }
            Spacer(modifier = Modifier.height(Spacing.sm))
        } else if (state.items.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.history_result_count, state.items.size),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                TextButton(onClick = { onToggleSelectionMode(true) }) {
                    Text(stringResource(R.string.history_select))
                }
            }
            Spacer(modifier = Modifier.height(Spacing.sm))
        }

        when {
            state.isLoading -> LoadingBox(modifier = Modifier.fillMaxSize())
            state.hasError -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    ErrorBanner(
                        message = state.errorMessage?.takeIf { it.isNotBlank() }
                            ?: stringResource(R.string.common_error_generic),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Spacing.lg)
                    )
                }
            }
            state.items.isEmpty() -> {
                val emptyTitle = if (state.filterBookmarked) R.string.history_empty_bookmark_title else R.string.history_empty_title
                val emptyBody = if (state.filterBookmarked) R.string.history_empty_bookmark_body else (if (state.searchKeyword.isNotEmpty()) R.string.history_empty_search_body else R.string.history_empty_body)
                EmptyState(
                    titleRes = emptyTitle,
                    bodyRes = emptyBody,
                    modifier = Modifier.fillMaxSize()
                )
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (state.selectedSubjects.size == 1) {
                        // Dang loc 1 mon - hien phang, khong group (truong header mon).
                        items(state.items, key = { it.id }) { item ->
                            HistoryRow(
                                item = item,
                                selectionMode = state.selectionMode,
                                selected = item.id in state.selectedIds,
                                reasking = item.id == state.reaskingId,
                                onOpenQuestion = { onOpenQuestion(item.id) },
                                onToggleBookmark = { value -> onToggleBookmark(item.id, value) },
                                onToggleSelect = { onToggleSelect(item.id) },
                                onAskAgain = { onAskAgain(item) },
                                onDeleteOne = { onDeleteOne(item.id) },
                                modifier = Modifier.animateItem()
                            )
                            Spacer(modifier = Modifier.height(Spacing.sm))
                        }
                    } else state.subjects.forEach { subject ->
                        val group = state.items.filter { it.subjectCode == subject }
                        if (group.isNotEmpty()) {
                            item(key = "header_$subject") {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = Spacing.sm),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = subject.label(),
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.width(Spacing.sm))
                                    Text(
                                        text = stringResource(R.string.history_result_count, group.size),
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                        items(group, key = { it.id }) { item ->
                            HistoryRow(
                                item = item,
                                selectionMode = state.selectionMode,
                                selected = item.id in state.selectedIds,
                                reasking = item.id == state.reaskingId,
                                onOpenQuestion = { onOpenQuestion(item.id) },
                                onToggleBookmark = { value -> onToggleBookmark(item.id, value) },
                                onToggleSelect = { onToggleSelect(item.id) },
                                onAskAgain = { onAskAgain(item) },
                                onDeleteOne = { onDeleteOne(item.id) },
                                modifier = Modifier.animateItem()
                            )
                            Spacer(modifier = Modifier.height(Spacing.sm))
                        }
                    }
                }
            }
        }
    }

    if (showDeleteConfirm) {
        ConfirmDialog(
            title = stringResource(R.string.history_delete_confirm_title),
            message = stringResource(R.string.history_delete_confirm_body, state.selectedIds.size),
            confirmLabelRes = R.string.common_delete,
            onConfirm = {
                showDeleteConfirm = false
                onDeleteSelected()
            },
            onDismiss = { showDeleteConfirm = false }
        )
    }

    val reaskResult = state.reaskResult
    if (reaskResult != null) {
        ReaskDialog(
            result = reaskResult,
            onDismiss = onDismissReask
        )
    } else if (state.reaskError != null) {
        ReaskDialog(
            error = state.reaskError,
            onDismiss = onDismissReask
        )
    }
}

@Composable
private fun HistoryRow(
    item: QuestionAnswer,
    selectionMode: Boolean,
    selected: Boolean,
    reasking: Boolean,
    onOpenQuestion: () -> Unit,
    onToggleBookmark: (Boolean) -> Unit,
    onToggleSelect: () -> Unit,
    onAskAgain: () -> Unit,
    onDeleteOne: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = if (selectionMode) onToggleSelect else onOpenQuestion),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) MaterialTheme.colorScheme.tertiaryContainer
            else MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = Spacing.md, end = Spacing.xs, top = Spacing.xs, bottom = Spacing.xs),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (selectionMode) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = if (selected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.outlineVariant
                )
                Spacer(modifier = Modifier.width(Spacing.sm))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.questionText,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (!selectionMode) {
                IconButton(onClick = { onToggleBookmark(!item.isBookmarked) }) {
                    Icon(
                        imageVector = if (item.isBookmarked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = stringResource(
                            if (item.isBookmarked) R.string.history_bookmark_remove else R.string.history_bookmark_add
                        ),
                        tint = if (item.isBookmarked) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
                if (item.questionText.isNotBlank()) {
                    IconButton(onClick = onAskAgain, enabled = !reasking) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = stringResource(R.string.history_ask_again),
                            tint = if (reasking) MaterialTheme.colorScheme.outlineVariant
                            else MaterialTheme.colorScheme.primary
                        )
                    }
                }
                IconButton(onClick = onDeleteOne) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = stringResource(R.string.history_delete_one),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
private fun ReaskDialog(
    result: QuestionAnswer? = null,
    error: String? = null,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.common_close))
            }
        },
        title = { Text(stringResource(R.string.history_reask_title)) },
        text = {
            if (error != null) {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error
                )
            } else if (result != null) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AssistChip(
                            onClick = {},
                            label = { Text(result.subjectCode.label()) }
                        )
                        Spacer(modifier = Modifier.width(Spacing.sm))
                        Text(
                            text = stringResource(R.string.ask_saved_hint),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(Spacing.md))
                    Box(contentAlignment = Alignment.CenterStart) {
                        MarkdownText(text = result.answerText)
                    }
                }
            }
        }
    )
}