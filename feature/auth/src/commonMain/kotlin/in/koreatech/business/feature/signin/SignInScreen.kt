package `in`.koreatech.business.feature.signin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import `in`.koreatech.business.ui.component.GradientActionButton
import `in`.koreatech.business.ui.component.KoinLogo
import `in`.koreatech.business.ui.component.KoinTextField
import `in`.koreatech.business.ui.component.KoinTextFieldAlert
import `in`.koreatech.business.ui.component.KoinTextFieldAlertType
import `in`.koreatech.business.ui.component.PhoneVisualTransformation
import `in`.koreatech.business.ui.theme.KoinTheme
import `in`.koreatech.business.ui.theme.WindowSizeClass
import koreatech.business.designsystem.resources.*
import koreatech.business.designsystem.resources.Res
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun SignInScreen(
    onSignedInToStoreMain: () -> Unit,
    onSignedInToStoreRegister: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    onNavigateToFindPassword: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SignInViewModel = koinViewModel()
) {
    val uiState by viewModel.collectAsState()

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            SignInSideEffect.NavigateToStoreMain -> onSignedInToStoreMain()
            SignInSideEffect.NavigateToStoreRegister -> onSignedInToStoreRegister()
        }
    }

    SignInScreenImpl(
        state = uiState,
        onPhoneNumberChanged = viewModel::onPhoneNumberChanged,
        onPasswordChanged = viewModel::onPasswordChanged,
        onTogglePasswordVisibility = viewModel::togglePasswordVisibility,
        onSubmit = viewModel::submit,
        onNavigateToSignUp = onNavigateToSignUp,
        onNavigateToFindPassword = onNavigateToFindPassword,
        modifier = modifier
    )
}

@Composable
internal fun SignInScreenImpl(
    state: SignInState,
    onPhoneNumberChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onSubmit: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    onNavigateToFindPassword: () -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(KoinTheme.colors.neutral50)
            .imePadding()
            .verticalScroll(rememberScrollState())
    ) {
        val sizeClass = WindowSizeClass.of(maxWidth)
        val formMaxWidth = (maxWidth * 0.6f).coerceIn(360.dp, 560.dp)
        val columnModifier = if (sizeClass !is WindowSizeClass.Compact) {
            Modifier
                .widthIn(max = formMaxWidth)
                .align(Alignment.Center)
                .padding(vertical = 32.dp)
        } else {
            Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(horizontal = 32.dp, vertical = 24.dp)
        }

        Column(modifier = columnModifier) {
            if (sizeClass is WindowSizeClass.Compact) {
                // Top spacer (mobile only)
                Spacer(modifier = Modifier.height(32.dp))
            }

            // Logo
            KoinLogo(colored = true)

            Spacer(modifier = Modifier.height(20.dp))

            // Title
            Text(
                text = stringResource(Res.string.sign_in_title),
                style = KoinTheme.typography.bold28,
                color = KoinTheme.colors.neutral800
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle
            Text(
                text = stringResource(Res.string.sign_in_subtitle),
                style = KoinTheme.typography.regular14,
                color = KoinTheme.colors.neutral700
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Phone field label
            Text(
                text = stringResource(Res.string.field_phone),
                style = KoinTheme.typography.medium14,
                color = KoinTheme.colors.neutral700
            )
            Spacer(modifier = Modifier.height(6.dp))
            KoinTextField(
                value = state.phoneNumber,
                onValueChange = onPhoneNumberChanged,
                modifier = Modifier.fillMaxWidth(),
                placeholder = "010-0000-0000",
                visualTransformation = PhoneVisualTransformation,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password field label
            Text(
                text = stringResource(Res.string.field_password),
                style = KoinTheme.typography.medium14,
                color = KoinTheme.colors.neutral700
            )
            Spacer(modifier = Modifier.height(6.dp))
            KoinTextField(
                value = state.password,
                onValueChange = onPasswordChanged,
                modifier = Modifier.fillMaxWidth(),
                placeholder = stringResource(Res.string.ph_password),
                visualTransformation = if (state.isPasswordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                trailingIcon = {
                    IconButton(onClick = onTogglePasswordVisibility) {
                        Icon(
                            imageVector = if (state.isPasswordVisible) {
                                Icons.Outlined.Visibility
                            } else {
                                Icons.Outlined.VisibilityOff
                            },
                            contentDescription = if (state.isPasswordVisible) stringResource(Res.string.password_hide) else stringResource(Res.string.password_show),
                            modifier = Modifier.size(20.dp),
                            tint = KoinTheme.colors.neutral500
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { onSubmit() })
            )

            // Error message
            val errorMessage = state.signInErrorMessage()
            if (errorMessage != null) {
                KoinTextFieldAlert(
                    message = errorMessage,
                    type = KoinTextFieldAlertType.Error
                )
            } else {
                Spacer(modifier = Modifier.height(6.dp))
            }

            // Find password link
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.weight(1f))
                TextButton(onClick = onNavigateToFindPassword) {
                    Text(
                        text = stringResource(Res.string.find_password),
                        style = KoinTheme.typography.medium14,
                        color = KoinTheme.colors.primary500
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Login button
            GradientActionButton(
                text = stringResource(Res.string.sign_in_button),
                onClick = onSubmit,
                isLoading = state.isLoading
            )

            Spacer(modifier = Modifier.height(32.dp))

            // OR divider
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = KoinTheme.colors.neutral400)
                Text(
                    text = "OR",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = KoinTheme.typography.medium12,
                    color = KoinTheme.colors.neutral400
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = KoinTheme.colors.neutral400)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Sign up button
            OutlinedButton(
                onClick = onNavigateToSignUp,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, KoinTheme.colors.neutral400),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = KoinTheme.colors.primary500
                )
            ) {
                Text(
                    text = stringResource(Res.string.sign_up_button),
                    style = KoinTheme.typography.bold16
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Footer
            Text(
                text = "ⓒ BCSD Lab. All rights reserved.",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                textAlign = TextAlign.Center,
                style = KoinTheme.typography.regular12,
                color = KoinTheme.colors.neutral500
            )
        }
    }
}

@Composable
fun AuthEntryPlaceholderScreen(
    title: String,
    description: String,
    buttonText: String,
    onButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(KoinTheme.colors.neutral50)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = title,
            style = KoinTheme.typography.bold20,
            color = KoinTheme.colors.neutral800,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = description,
            fontSize = 14.sp,
            color = KoinTheme.colors.neutral700,
            textAlign = TextAlign.Center,
            lineHeight = 19.6.sp
        )
        Spacer(modifier = Modifier.height(48.dp))
        GradientActionButton(
            text = buttonText,
            onClick = onButtonClick,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun SignInState.signInErrorMessage(): String? {
    if (!notValidateField) return null
    errorMessageRes?.let { return stringResource(it) }
    return errorMessage.takeIf { it.isNotEmpty() }
}
