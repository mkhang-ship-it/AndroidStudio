package com.aistudy.aistudy.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.aistudy.aistudy.R
import com.aistudy.aistudy.data.repository.AuthRepository
import com.aistudy.aistudy.ui.common.DuoButton
import com.aistudy.aistudy.ui.common.DuoButtonColors
import com.aistudy.aistudy.ui.common.ErrorBanner
import com.aistudy.aistudy.ui.common.PaperSheet
import com.aistudy.aistudy.ui.root.AppDependencies
import com.aistudy.aistudy.ui.theme.Spacing
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ==================== WELCOME SCREEN ====================

@Composable
fun WelcomeScreen(
    deps: AppDependencies,
    onSignIn: () -> Unit,
    onSignUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(modifier = modifier) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(Spacing.xl)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (!deps.aiClient.isConfigured) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = Spacing.xl)
                ) {
                    Column(modifier = Modifier.padding(Spacing.lg)) {
                        Text(
                            text = stringResource(R.string.ask_fake_mode_banner),
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(Spacing.xxs))
                        Text(
                            text = stringResource(R.string.ask_fake_mode_body),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            PaperSheet(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(R.string.auth_welcome_title),
                        style = MaterialTheme.typography.displaySmall,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(Spacing.md))
                    Text(
                        text = stringResource(R.string.auth_welcome_subtitle),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.xxl))

            DuoButton(
                text = stringResource(R.string.auth_sign_in),
                onClick = onSignIn,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(Spacing.lg))

            DuoButton(
                text = stringResource(R.string.auth_sign_up),
                onClick = onSignUp,
                modifier = Modifier.fillMaxWidth(),
                colors = DuoButtonColors.Secondary
            )
        }
    }
}

// ==================== LOGIN SCREEN ====================

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class LoginViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private val _state = MutableStateFlow(LoginUiState())
    val state: StateFlow<LoginUiState> = _state.asStateFlow()

    fun onEmailChange(email: String) {
        _state.update { it.copy(email = email, errorMessage = null) }
    }

    fun onPasswordChange(password: String) {
        _state.update { it.copy(password = password, errorMessage = null) }
    }

    fun togglePasswordVisibility() {
        _state.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun login(onSignedIn: () -> Unit) {
        val currentState = _state.value
        val email = currentState.email.trim()
        val password = currentState.password

        if (email.isBlank() || password.isBlank()) {
            return
        }

        _state.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            val result = authRepository.login(email, password)
            result.onSuccess {
                _state.update { s -> s.copy(isLoading = false, password = "") }
                onSignedIn()
            }.onFailure { ex ->
                _state.update { s ->
                    s.copy(
                        isLoading = false,
                        errorMessage = ex.message ?: "Login failed"
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    deps: AppDependencies,
    onBack: () -> Unit,
    onSignedIn: () -> Unit,
    modifier: Modifier = Modifier
) {
    val vm: LoginViewModel = viewModel(
        factory = viewModelFactory { initializer { LoginViewModel(deps.authRepository) } }
    )
    val state by vm.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.auth_sign_in),
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
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(Spacing.xl)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (state.errorMessage != null) {
                ErrorBanner(
                    message = state.errorMessage!!,
                    modifier = Modifier.padding(bottom = Spacing.lg)
                )
            }

            OutlinedTextField(
                value = state.email,
                onValueChange = vm::onEmailChange,
                label = { Text(stringResource(R.string.auth_email)) },
                isError = state.errorMessage != null,
                supportingText = {
                    if (state.errorMessage != null) {
                        Text(
                            text = state.errorMessage!!,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(Spacing.lg))

            OutlinedTextField(
                value = state.password,
                onValueChange = vm::onPasswordChange,
                label = { Text(stringResource(R.string.auth_password)) },
                isError = state.errorMessage != null,
                supportingText = {
                    if (state.errorMessage != null) {
                        Text(
                            text = state.errorMessage!!,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                singleLine = true,
                visualTransformation = if (state.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    TextButton(onClick = vm::togglePasswordVisibility) {
                        Text(
                            text = if (state.isPasswordVisible) "Hide" else "Show",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { vm.login(onSignedIn) }
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(Spacing.xxl))

            DuoButton(
                text = stringResource(R.string.auth_sign_in),
                onClick = { vm.login(onSignedIn) },
                enabled = !state.isLoading && state.email.isNotBlank() && state.password.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// ==================== REGISTER SCREEN ====================

data class RegisterUiState(
    val displayName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val validationErrorRes: Int? = null,
    val apiErrorMessage: String? = null
)

class RegisterViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private val _state = MutableStateFlow(RegisterUiState())
    val state: StateFlow<RegisterUiState> = _state.asStateFlow()

    fun onDisplayNameChange(value: String) {
        _state.update { it.copy(displayName = value, validationErrorRes = null, apiErrorMessage = null) }
    }

    fun onEmailChange(value: String) {
        _state.update { it.copy(email = value, validationErrorRes = null, apiErrorMessage = null) }
    }

    fun onPasswordChange(value: String) {
        _state.update { it.copy(password = value, validationErrorRes = null, apiErrorMessage = null) }
    }

    fun onConfirmPasswordChange(value: String) {
        _state.update { it.copy(confirmPassword = value, validationErrorRes = null, apiErrorMessage = null) }
    }

    fun togglePasswordVisibility() {
        _state.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun register(onRegistered: () -> Unit) {
        val currentState = _state.value
        val displayName = currentState.displayName.trim()
        val email = currentState.email.trim()
        val password = currentState.password
        val confirmPassword = currentState.confirmPassword

        if (displayName.isBlank()) {
            _state.update { it.copy(validationErrorRes = R.string.auth_error_display_name_empty) }
            return
        }
        if (!email.contains("@") || !email.contains(".")) {
            _state.update { it.copy(validationErrorRes = R.string.auth_error_email_invalid) }
            return
        }
        if (password.length < 8) {
            _state.update { it.copy(validationErrorRes = R.string.auth_error_password_short) }
            return
        }
        if (!password.any { it.isLetter() } || !password.any { it.isDigit() }) {
            _state.update { it.copy(validationErrorRes = R.string.auth_error_password_weak) }
            return
        }
        if (password != confirmPassword) {
            _state.update { it.copy(validationErrorRes = R.string.auth_error_password_mismatch) }
            return
        }

        _state.update { it.copy(isLoading = true, validationErrorRes = null, apiErrorMessage = null) }
        viewModelScope.launch {
            val result = authRepository.register(email, password, displayName)
            result.onSuccess {
                // Clear password fields immediately on success
                _state.update { s -> s.copy(isLoading = false, password = "", confirmPassword = "") }
                onRegistered()
            }.onFailure { ex ->
                val errorMsg = if (ex is IllegalStateException) {
                    null
                } else {
                    ex.message
                }
                val errorRes = if (ex is IllegalStateException) {
                    R.string.auth_error_email_taken
                } else {
                    null
                }
                _state.update { s ->
                    s.copy(
                        isLoading = false,
                        validationErrorRes = errorRes,
                        apiErrorMessage = errorMsg ?: if (errorRes == null) "Registration failed" else null
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    deps: AppDependencies,
    onBack: () -> Unit,
    onRegistered: () -> Unit,
    modifier: Modifier = Modifier
) {
    val vm: RegisterViewModel = viewModel(
        factory = viewModelFactory { initializer { RegisterViewModel(deps.authRepository) } }
    )
    val state by vm.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.auth_sign_up),
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
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(Spacing.xl)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val errorText = when {
                state.validationErrorRes != null -> stringResource(state.validationErrorRes!!)
                state.apiErrorMessage != null -> state.apiErrorMessage
                else -> null
            }
            if (errorText != null) {
                ErrorBanner(
                    message = errorText,
                    modifier = Modifier.padding(bottom = Spacing.lg)
                )
            }

            OutlinedTextField(
                value = state.displayName,
                onValueChange = vm::onDisplayNameChange,
                label = { Text(stringResource(R.string.auth_display_name)) },
                isError = state.validationErrorRes == R.string.auth_error_display_name_empty,
                supportingText = {
                    if (state.validationErrorRes == R.string.auth_error_display_name_empty) {
                        Text(
                            text = stringResource(R.string.auth_error_display_name_empty),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(Spacing.lg))

            OutlinedTextField(
                value = state.email,
                onValueChange = vm::onEmailChange,
                label = { Text(stringResource(R.string.auth_email)) },
                isError = state.validationErrorRes == R.string.auth_error_email_invalid || state.validationErrorRes == R.string.auth_error_email_taken || state.apiErrorMessage != null,
                supportingText = {
                    val msg = when {
                        state.validationErrorRes == R.string.auth_error_email_invalid -> stringResource(R.string.auth_error_email_invalid)
                        state.validationErrorRes == R.string.auth_error_email_taken -> stringResource(R.string.auth_error_email_taken)
                        state.apiErrorMessage != null -> state.apiErrorMessage
                        else -> null
                    }
                    if (msg != null) {
                        Text(
                            text = msg,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(Spacing.lg))

            OutlinedTextField(
                value = state.password,
                onValueChange = vm::onPasswordChange,
                label = { Text(stringResource(R.string.auth_password)) },
                isError = state.validationErrorRes == R.string.auth_error_password_short || state.validationErrorRes == R.string.auth_error_password_weak || state.validationErrorRes == R.string.auth_error_password_mismatch,
                supportingText = {
                    val errorMsg = when (state.validationErrorRes) {
                        R.string.auth_error_password_short -> stringResource(R.string.auth_error_password_short)
                        R.string.auth_error_password_weak -> stringResource(R.string.auth_error_password_weak)
                        R.string.auth_error_password_mismatch -> stringResource(R.string.auth_error_password_mismatch)
                        else -> null
                    }
                    if (errorMsg != null) {
                        Text(
                            text = errorMsg,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    } else {
                        Text(stringResource(R.string.auth_password_helper))
                    }
                },
                singleLine = true,
                visualTransformation = if (state.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    TextButton(onClick = vm::togglePasswordVisibility) {
                        Text(
                            text = if (state.isPasswordVisible) "Hide" else "Show",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(Spacing.lg))

            OutlinedTextField(
                value = state.confirmPassword,
                onValueChange = vm::onConfirmPasswordChange,
                label = { Text(stringResource(R.string.auth_confirm_password)) },
                isError = state.validationErrorRes == R.string.auth_error_password_mismatch,
                supportingText = {
                    if (state.validationErrorRes == R.string.auth_error_password_mismatch) {
                        Text(
                            text = stringResource(R.string.auth_error_password_mismatch),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                singleLine = true,
                visualTransformation = if (state.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { vm.register(onRegistered) }
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(Spacing.xxl))

            DuoButton(
                text = stringResource(R.string.auth_sign_up),
                onClick = { vm.register(onRegistered) },
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}