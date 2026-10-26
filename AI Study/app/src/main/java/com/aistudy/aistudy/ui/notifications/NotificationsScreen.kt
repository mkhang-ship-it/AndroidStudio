package com.aistudy.aistudy.ui.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.viewmodel.initializer
import com.aistudy.aistudy.R
import com.aistudy.aistudy.ui.common.EmptyState
import com.aistudy.aistudy.ui.common.LoadingBox
import com.aistudy.aistudy.ui.common.PaperSheet
import com.aistudy.aistudy.ui.common.ScreenHeader
import com.aistudy.aistudy.ui.common.StickerBadge
import com.aistudy.aistudy.ui.notifications.NotificationsUiState
import com.aistudy.aistudy.ui.notifications.NotificationsViewModel
import com.aistudy.aistudy.ui.root.AppDependencies
import com.aistudy.aistudy.ui.label
import com.aistudy.aistudy.ui.theme.Spacing

@Composable
fun NotificationsScreen(
    deps: AppDependencies,
    onBack: () -> Unit,
    onOpenNotification: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val vm: NotificationsViewModel = viewModel(
        factory = viewModelFactory {
            initializer { NotificationsViewModel(deps.notificationRepository) }
        }
    )
    val state by vm.state.collectAsStateWithLifecycle()

    val sessionUserId by deps.authRepository.sessionUserId.collectAsStateWithLifecycle(initialValue = null)
    androidx.compose.runtime.LaunchedEffect(sessionUserId) {
        sessionUserId?.let { vm.load(it) }
    }

    NotificationsContent(
        state = state,
        onBack = onBack,
        onMarkAllRead = { sessionUserId?.let { vm.markAllRead(it) } },
        onOpenNotification = { notification ->
            onOpenNotification(notification.id)
            vm.markRead(notification.id)
        },
        modifier = modifier
    )
}

@Composable
private fun NotificationsContent(
    state: NotificationsUiState,
    onBack: () -> Unit,
    onMarkAllRead: () -> Unit,
    onOpenNotification: (com.aistudy.aistudy.domain.model.entity.Notification) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        PaperSheet(
            modifier = Modifier.padding(horizontal = Spacing.lg, vertical = Spacing.lg)
        ) {
            ScreenHeader(
                titleRes = R.string.notifications_title
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
                        message = state.errorMessage ?: "",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Spacing.lg)
                    )
                    Spacer(modifier = Modifier.width(Spacing.sm))
                    Button(onClick = onBack) {
                        Text(stringResource(R.string.notifications_back))
                    }
                }
            }
            state.notifications.isEmpty() -> EmptyState(
                titleRes = R.string.notifications_empty_title,
                bodyRes = R.string.notifications_empty_body,
                modifier = Modifier.fillMaxSize(),
                action = {
                    Button(onClick = onBack) {
                        Text(stringResource(R.string.notifications_back))
                    }
                }
            )
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(Spacing.lg)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StickerBadge(
                            text = if (state.unreadCount > 0) {
                                stringResource(
                                    R.string.notifications_unread_count,
                                    state.unreadCount
                                )
                            } else {
                                stringResource(R.string.notifications_all_read)
                            },
                            icon = {
                                Icon(
                                    imageVector = if (state.unreadCount > 0) {
                                        Icons.Filled.Notifications
                                    } else {
                                        Icons.Filled.CheckCircle
                                    },
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                            }
                        )
                        TextButton(onClick = onMarkAllRead) {
                            Text(stringResource(R.string.notifications_mark_all_read))
                        }
                    }

                    Spacer(modifier = Modifier.size(Spacing.sm))

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(Spacing.sm)
                    ) {
                        items(state.notifications) { notification ->
                            NotificationCard(
                                title = notification.title,
                                body = notification.body,
                                typeLabel = notification.type.label(),
                                isUnread = !notification.isRead,
                                onClick = { onOpenNotification(notification) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.size(Spacing.md))

                    TextButton(
                        onClick = onBack,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.notifications_back))
                    }
                }
            }
        }
        }
    }
}

@Composable
private fun NotificationCard(
    title: String,
    body: String,
    typeLabel: String,
    isUnread: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isUnread) {
                MaterialTheme.colorScheme.surfaceContainerHigh
            } else {
                MaterialTheme.colorScheme.surfaceContainerLow
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.lg),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .padding(top = Spacing.xs)
                    .size(8.dp)
                    .background(
                        color = if (isUnread) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        shape = CircleShape
                    )
            )
            Spacer(modifier = Modifier.width(Spacing.md))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = if (isUnread) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = body,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = typeLabel,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = stringResource(R.string.notifications_tap_detail),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
