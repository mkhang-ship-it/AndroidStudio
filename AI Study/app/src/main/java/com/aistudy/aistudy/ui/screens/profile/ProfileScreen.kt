package com.aistudy.aistudy.ui.screens.profile

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import com.aistudy.aistudy.ui.common.PaperSheet
import com.aistudy.aistudy.ui.common.StickerBadge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.viewmodel.initializer
import com.aistudy.aistudy.R
import com.aistudy.aistudy.ui.common.ConfirmDialog
import com.aistudy.aistudy.ui.common.LoadingBox
import com.aistudy.aistudy.ui.common.ScreenHeader
import com.aistudy.aistudy.ui.profile.ProfileUiState
import com.aistudy.aistudy.ui.profile.ProfileViewModel
import com.aistudy.aistudy.ui.root.AppDependencies
import com.aistudy.aistudy.ui.label
import com.aistudy.aistudy.ui.theme.Spacing

@Composable
fun ProfileScreen(
    deps: AppDependencies,
    onEditPreferences: () -> Unit,
    onOpenNotifications: () -> Unit,
    modifier: Modifier = Modifier
) {
    val vm: ProfileViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                ProfileViewModel(
                    authRepository = deps.authRepository,
                    profileRepository = deps.profileRepository,
                    gamificationRepository = deps.gamificationRepository
                )
            }
        }
    )
    val state by vm.state.collectAsStateWithLifecycle()

    val sessionUserId by deps.authRepository.sessionUserId.collectAsStateWithLifecycle(initialValue = null)
    androidx.compose.runtime.LaunchedEffect(sessionUserId) {
        sessionUserId?.let { vm.load(it) }
    }

    var showLogoutConfirm by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    ProfileContent(
        state = state,
        onEditPreferences = onEditPreferences,
        onOpenNotifications = onOpenNotifications,
        onLogoutRequested = { showLogoutConfirm = true },
        onDeleteRequested = { showDeleteConfirm = true },
        modifier = modifier
    )

    if (showLogoutConfirm) {
        ConfirmDialog(
            title = stringResource(R.string.auth_logout_confirm_title),
            message = stringResource(R.string.auth_logout_confirm_body),
            confirmLabelRes = R.string.auth_logout,
            onConfirm = {
                showLogoutConfirm = false
                vm.logout()
            },
            onDismiss = { showLogoutConfirm = false }
        )
    }

    if (showDeleteConfirm) {
        ConfirmDialog(
            title = stringResource(R.string.privacy_delete_confirm_title),
            message = stringResource(R.string.privacy_delete_confirm_body),
            confirmLabelRes = R.string.common_delete,
            onConfirm = {
                showDeleteConfirm = false
                vm.deleteAccount()
            },
            onDismiss = { showDeleteConfirm = false }
        )
    }
}

@Composable
private fun ProfileContent(
    state: ProfileUiState,
    onEditPreferences: () -> Unit,
    onOpenNotifications: () -> Unit,
    onLogoutRequested: () -> Unit,
    onDeleteRequested: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        PaperSheet {
            ScreenHeader(
                titleRes = R.string.profile_title,
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
                    Spacer(modifier = Modifier.height(Spacing.sm))
                    Button(onClick = onEditPreferences) {
                        Text(stringResource(R.string.profile_edit_preferences))
                    }
                }
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(Spacing.lg),
                    verticalArrangement = Arrangement.spacedBy(Spacing.lg)
                ) {
                    ProfileHeader(
                        displayName = state.displayName,
                        level = state.level
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                    ) {
                        StatTile(
                            text = stringResource(R.string.gamification_xp_total, state.totalXp),
                            modifier = Modifier.weight(1f)
                        )
                        StatTile(
                            text = stringResource(R.string.gamification_streak, state.streak),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                        Text(
                            text = stringResource(R.string.gamification_badges_title),
                            style = MaterialTheme.typography.titleMedium
                        )
                        if (state.badges.isNotEmpty()) {
                            state.badges.forEach { badge ->
                                StickerBadge(
                                    text = badge.badgeCode.label(),
                                    icon = { Icon(Icons.Filled.Star, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary) }
                                )
                            }
                        } else {
                            Text(
                                text = stringResource(R.string.gamification_badges_empty),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                        OutlinedButton(
                            onClick = onEditPreferences,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Filled.Edit, contentDescription = null)
                            Spacer(modifier = Modifier.width(Spacing.sm))
                            Text(stringResource(R.string.profile_edit_preferences))
                        }
                        OutlinedButton(
                            onClick = onOpenNotifications,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Filled.Notifications, contentDescription = null)
                            Spacer(modifier = Modifier.width(Spacing.sm))
                            Text(stringResource(R.string.profile_open_notifications))
                        }
                        OutlinedButton(
                            onClick = onLogoutRequested,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(stringResource(R.string.auth_logout))
                        }
                    }

                    HorizontalDivider()

                    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                        Text(
                            text = stringResource(R.string.privacy_section_title),
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(
                            text = stringResource(R.string.privacy_local_only_title),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = stringResource(R.string.privacy_local_only_body),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    TextButton(
                        onClick = onDeleteRequested,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.auth_delete_account))
                    }
                }
            }
        }
        }
    }
}

@Composable
private fun ProfileHeader(
    displayName: String,
    level: Int,
    modifier: Modifier = Modifier
) {
    val initial = displayName.trim().firstOrNull()?.toString()?.uppercase() ?: "?"

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initial,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        Spacer(modifier = Modifier.width(Spacing.md))
        Column {
            Text(
                text = displayName,
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = stringResource(R.string.profile_level_label) + ": " + level,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun StatTile(
    text: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 88.dp)
                .padding(Spacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
        }
    }
}
