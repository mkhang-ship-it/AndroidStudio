package com.aistudy.aistudy.ui.screens.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Icon
import com.aistudy.aistudy.ui.common.MarkerChip
import com.aistudy.aistudy.ui.common.PaperSheet
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.aistudy.aistudy.domain.rules.SubjectDetector
import com.aistudy.aistudy.ui.common.EmptyState
import com.aistudy.aistudy.ui.common.LoadingBox
import com.aistudy.aistudy.ui.common.ScreenHeader
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
        onToggleGroup = vm::setGroupBySubject,
        onToggleBookmark = { id, value -> vm.toggleBookmark(id, value) },
        modifier = modifier
    )
}

@Composable
private fun HistoryContent(
    state: HistoryUiState,
    onOpenQuestion: (Long) -> Unit,
    onSearch: (String) -> Unit,
    onToggleFilter: (Boolean) -> Unit,
    onToggleGroup: (Boolean) -> Unit,
    onToggleBookmark: (Long, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
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

            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                MarkerChip(
                    label = stringResource(R.string.history_filter_all),
                    selected = !state.filterBookmarked,
                    onClick = { onToggleFilter(false) }
                )
                MarkerChip(
                    label = stringResource(R.string.history_filter_bookmarked),
                    selected = state.filterBookmarked,
                    onClick = { onToggleFilter(true) }
                )
                MarkerChip(
                    label = stringResource(R.string.history_group_by_subject),
                    selected = state.groupBySubject,
                    onClick = { onToggleGroup(!state.groupBySubject) }
                )
            }
        }

        Spacer(modifier = Modifier.height(Spacing.sm))

        if (state.items.isNotEmpty()) {
            Text(
                text = stringResource(R.string.history_result_count, state.items.size),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(Spacing.md))

        when {
            state.isLoading -> LoadingBox(modifier = Modifier.fillMaxSize())
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
                val list = if (state.groupBySubject) {
                    state.items.groupBy { it.subjectCode }.map { (subject, items) ->
                        SubjectGroup(subject, items)
                    }
                } else {
                    state.items.map { item -> SubjectGroup(null, listOf(item)) }
                }
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    list.forEach { group ->
                        if (group.subject != null) {
                            Text(
                                text = group.subject.label(),
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(
                                    top = Spacing.sm,
                                    bottom = Spacing.xs
                                )
                            )
                        }
                        group.items.forEach { item ->
                            HistoryRow(
                                item = item,
                                onOpenQuestion = { onOpenQuestion(item.id) },
                                onToggleBookmark = { value -> onToggleBookmark(item.id, value) }
                            )
                            Spacer(modifier = Modifier.height(Spacing.sm))
                        }
                        Spacer(modifier = Modifier.height(Spacing.sm))
                    }
                }
            }
        }
    }
}

private data class SubjectGroup(
    val subject: com.aistudy.aistudy.domain.model.enums.Subject?,
    val items: List<QuestionAnswer>
)

@Composable
private fun HistoryRow(
    item: QuestionAnswer,
    onOpenQuestion: () -> Unit,
    onToggleBookmark: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onOpenQuestion),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = Spacing.md, end = Spacing.xs, top = Spacing.xs, bottom = Spacing.xs),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.questionText,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                if (SubjectDetector.detect(item.questionText) != null) {
                    Spacer(modifier = Modifier.height(Spacing.xxs))
                    AssistChip(
                        onClick = onOpenQuestion,
                        label = {
                            Text(
                                text = item.subjectCode.label(),
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    )
                }
            }
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
        }
    }
}
