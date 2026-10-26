package com.aistudy.aistudy.ui.notifications

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.aistudy.aistudy.R
import com.aistudy.aistudy.data.repository.NotificationRepository
import com.aistudy.aistudy.domain.model.entity.Notification
import com.aistudy.aistudy.ui.common.DuoButton
import com.aistudy.aistudy.ui.common.ErrorBanner
import com.aistudy.aistudy.ui.common.LoadingBox
import com.aistudy.aistudy.ui.common.MarkdownText
import com.aistudy.aistudy.ui.label
import com.aistudy.aistudy.ui.root.AppDependencies
import com.aistudy.aistudy.ui.theme.Spacing
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class NotificationDetailUiState(
    val isLoading: Boolean = true,
    val notification: Notification? = null,
    val errorMessage: String? = null,
    val hasError: Boolean = false
)

class NotificationDetailViewModel(
    private val notificationRepository: NotificationRepository,
    private val notificationId: Long
) : ViewModel() {

    private val _state = MutableStateFlow(NotificationDetailUiState())
    val state: StateFlow<NotificationDetailUiState> = _state.asStateFlow()

    private var loaded = false

    fun load(userId: Long) {
        if (loaded) return
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val item = notificationRepository.getForUser(userId, notificationId)
                if (item == null) {
                    _state.value = NotificationDetailUiState(
                        isLoading = false,
                        hasError = true,
                        errorMessage = null
                    )
                    return@launch
                }
                // Mo chi tiet la danh dau da doc; danh sach se tu cap nhat khi quay lai.
                runCatching { notificationRepository.markRead(item.id) }
                loaded = true
                _state.value = NotificationDetailUiState(
                    isLoading = false,
                    notification = item.copy(isRead = true)
                )
            } catch (e: Exception) {
                _state.value = NotificationDetailUiState(
                    isLoading = false,
                    hasError = true,
                    errorMessage = e.message
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationDetailScreen(
    deps: AppDependencies,
    notificationId: Long,
    onBack: () -> Unit,
    onOpenDeepLink: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val vm: NotificationDetailViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                NotificationDetailViewModel(
                    notificationRepository = deps.notificationRepository,
                    notificationId = notificationId
                )
            }
        }
    )
    val state by vm.state.collectAsStateWithLifecycle()
    val sessionUserId by deps.authRepository.sessionUserId
        .collectAsStateWithLifecycle(initialValue = null)

    LaunchedEffect(sessionUserId, notificationId) {
        sessionUserId?.let(vm::load)
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.notification_detail_title),
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.common_back)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                state.isLoading -> LoadingBox()
                state.notification == null -> ErrorBanner(
                    message = state.errorMessage
                        ?: stringResource(R.string.notification_detail_not_found),
                    onRetry = { sessionUserId?.let(vm::load) }
                )
                else -> NotificationDetailContent(
                    notification = state.notification!!,
                    onOpenDeepLink = onOpenDeepLink
                )
            }
        }
    }
}

@Composable
private fun NotificationDetailContent(
    notification: Notification,
    onOpenDeepLink: (String) -> Unit
) {
    val date = remember(notification.createdAt) {
        SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            .format(Date(notification.createdAt))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Spacing.lg, vertical = Spacing.lg)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Notifications,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(modifier = Modifier.width(Spacing.md))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = notification.type.label(),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = if (notification.isRead) {
                        stringResource(R.string.notification_detail_read)
                    } else {
                        stringResource(R.string.notification_detail_unread)
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(Spacing.lg))

        Text(
            text = notification.title,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(Spacing.xs))
        Text(
            text = date,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(Spacing.lg))
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        Spacer(modifier = Modifier.height(Spacing.lg))

        Text(
            text = stringResource(R.string.notification_detail_content),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(Spacing.sm))

        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow
            ),
            shape = MaterialTheme.shapes.large,
            modifier = Modifier.fillMaxWidth()
        ) {
            MarkdownText(
                text = notification.body,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(Spacing.lg)
            )
        }

        Spacer(modifier = Modifier.height(Spacing.xl))

        val deepLink = notification.deepLink
        if (!deepLink.isNullOrBlank()) {
            DuoButton(
                text = stringResource(R.string.notification_detail_open_link),
                onClick = { onOpenDeepLink(deepLink) },
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            Surface(
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.notification_detail_no_link),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(Spacing.md)
                )
            }
        }
    }
}
