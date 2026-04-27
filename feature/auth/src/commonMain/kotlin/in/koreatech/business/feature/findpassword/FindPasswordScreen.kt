package `in`.koreatech.business.feature.findpassword

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import `in`.koreatech.business.ui.component.FilledActionButton
import `in`.koreatech.business.ui.component.KoinTextField
import `in`.koreatech.business.ui.component.KoinTextFieldAlert
import `in`.koreatech.business.ui.component.KoinTextFieldAlertType
import `in`.koreatech.business.ui.component.PhoneVisualTransformation
import `in`.koreatech.business.ui.theme.KoinTheme
import koreatech.business.designsystem.resources.*
import koreatech.business.designsystem.resources.Res
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun FindPasswordScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FindPasswordViewModel = koinViewModel()
) {
    val uiState by viewModel.collectAsState()
    val navController = rememberNavController()

    LaunchedEffect(navController, uiState.step) {
        syncFindPasswordRoute(
            navController = navController,
            route = uiState.step.route
        )
    }

    BackHandler {
        val navigated = viewModel.navigateBack()
        if (!navigated) onNavigateBack()
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when (uiState.step) {
                            FindPasswordStep.PhoneInput -> stringResource(Res.string.find_password_title)
                            FindPasswordStep.SmsVerify -> stringResource(Res.string.find_password_step_sms)
                            FindPasswordStep.NewPassword -> stringResource(Res.string.find_password_step_new)
                            FindPasswordStep.Complete -> stringResource(Res.string.find_password_step_complete)
                        },
                        style = KoinTheme.typography.bold18
                    )
                },
                navigationIcon = {
                    if (uiState.step != FindPasswordStep.Complete) {
                        IconButton(onClick = {
                            val navigated = viewModel.navigateBack()
                            if (!navigated) onNavigateBack()
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(Res.string.back_navigation)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = KoinTheme.colors.neutral50
                )
            )
        },
        containerColor = KoinTheme.colors.neutral50
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = FindPasswordStep.PhoneInput.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(FindPasswordStep.PhoneInput.route) {
                PhoneInputStep(
                    uiState = uiState,
                    onPhoneChanged = viewModel::onPhoneNumberChanged,
                    onNext = viewModel::submitPhone
                )
            }
            composable(FindPasswordStep.SmsVerify.route) {
                SmsVerifyStep(
                    uiState = uiState,
                    onSmsCodeChanged = viewModel::onSmsCodeChanged,
                    onNext = viewModel::submitSms
                )
            }
            composable(FindPasswordStep.NewPassword.route) {
                NewPasswordStep(
                    uiState = uiState,
                    onNewPasswordChanged = viewModel::onNewPasswordChanged,
                    onNewPasswordConfirmChanged = viewModel::onNewPasswordConfirmChanged,
                    onTogglePasswordVisibility = viewModel::onTogglePasswordVisibility,
                    onTogglePasswordConfirmVisibility = viewModel::onTogglePasswordConfirmVisibility,
                    onNext = viewModel::submitNewPassword
                )
            }
            composable(FindPasswordStep.Complete.route) {
                CompleteStep(
                    onConfirm = onNavigateBack
                )
            }
        }
    }
}

private suspend fun syncFindPasswordRoute(
    navController: NavHostController,
    route: String
) {
    val currentRoute = navController.currentBackStackEntry?.destination?.route
    if (currentRoute == route) return

    val popped = navController.popBackStack(route, inclusive = false)
    if (!popped) {
        navController.navigate(route) {
            launchSingleTop = true
        }
    }
}

@Composable
internal fun PhoneInputStep(
    uiState: FindPasswordUiState,
    onPhoneChanged: (String) -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(KoinTheme.colors.neutral50)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Text(
            text = stringResource(Res.string.find_password_phone_hint),
            style = KoinTheme.typography.regular14,
            color = KoinTheme.colors.neutral700
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(Res.string.field_phone),
            style = KoinTheme.typography.medium14,
            color = KoinTheme.colors.neutral700
        )
        Spacer(modifier = Modifier.height(6.dp))
        KoinTextField(
            value = uiState.phoneNumber,
            onValueChange = onPhoneChanged,
            modifier = Modifier.fillMaxWidth(),
            placeholder = "010-0000-0000",
            visualTransformation = PhoneVisualTransformation,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { onNext() })
        )

        if (uiState.phoneError.isNotEmpty()) {
            KoinTextFieldAlert(
                message = uiState.phoneError,
                type = KoinTextFieldAlertType.Error
            )
        } else {
            Spacer(modifier = Modifier.height(6.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        FilledActionButton(
            text = stringResource(Res.string.send_sms),
            onClick = onNext,
            isLoading = uiState.isLoading,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
internal fun SmsVerifyStep(
    uiState: FindPasswordUiState,
    onSmsCodeChanged: (String) -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(KoinTheme.colors.neutral50)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Text(
            text = stringResource(Res.string.sms_sent_to_phone_findpw, uiState.phoneNumber),
            style = KoinTheme.typography.regular14,
            color = KoinTheme.colors.neutral700
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(Res.string.field_sms_code),
            style = KoinTheme.typography.medium14,
            color = KoinTheme.colors.neutral700
        )
        Spacer(modifier = Modifier.height(6.dp))
        KoinTextField(
            value = uiState.smsCode,
            onValueChange = onSmsCodeChanged,
            modifier = Modifier.fillMaxWidth(),
            placeholder = stringResource(Res.string.ph_sms_code),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { onNext() })
        )

        if (uiState.smsError.isNotEmpty()) {
            KoinTextFieldAlert(
                message = uiState.smsError,
                type = KoinTextFieldAlertType.Error
            )
        } else {
            Spacer(modifier = Modifier.height(6.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        FilledActionButton(
            text = stringResource(Res.string.confirm),
            onClick = onNext,
            isLoading = uiState.isLoading,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
internal fun NewPasswordStep(
    uiState: FindPasswordUiState,
    onNewPasswordChanged: (String) -> Unit,
    onNewPasswordConfirmChanged: (String) -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onTogglePasswordConfirmVisibility: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(KoinTheme.colors.neutral50)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Text(
            text = stringResource(Res.string.find_password_new_hint),
            style = KoinTheme.typography.regular14,
            color = KoinTheme.colors.neutral700
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(Res.string.field_new_password),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = KoinTheme.colors.neutral700
        )
        Spacer(modifier = Modifier.height(6.dp))
        KoinTextField(
            value = uiState.newPassword,
            onValueChange = onNewPasswordChanged,
            modifier = Modifier.fillMaxWidth(),
            placeholder = stringResource(Res.string.ph_new_password),
            visualTransformation = if (uiState.isPasswordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            trailingIcon = {
                IconButton(onClick = onTogglePasswordVisibility) {
                    Icon(
                        imageVector = if (uiState.isPasswordVisible) {
                            Icons.Outlined.Visibility
                        } else {
                            Icons.Outlined.VisibilityOff
                        },
                        contentDescription = if (uiState.isPasswordVisible) stringResource(Res.string.password_hide) else stringResource(Res.string.password_show),
                        modifier = Modifier.size(20.dp),
                        tint = KoinTheme.colors.neutral500
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(Res.string.field_new_password_confirm),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = KoinTheme.colors.neutral700
        )
        Spacer(modifier = Modifier.height(6.dp))
        KoinTextField(
            value = uiState.newPasswordConfirm,
            onValueChange = onNewPasswordConfirmChanged,
            modifier = Modifier.fillMaxWidth(),
            placeholder = stringResource(Res.string.ph_password_confirm),
            visualTransformation = if (uiState.isPasswordConfirmVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            trailingIcon = {
                IconButton(onClick = onTogglePasswordConfirmVisibility) {
                    Icon(
                        imageVector = if (uiState.isPasswordConfirmVisible) {
                            Icons.Outlined.Visibility
                        } else {
                            Icons.Outlined.VisibilityOff
                        },
                        contentDescription = if (uiState.isPasswordConfirmVisible) stringResource(Res.string.password_hide) else stringResource(Res.string.password_show),
                        modifier = Modifier.size(20.dp),
                        tint = KoinTheme.colors.neutral500
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { onNext() })
        )

        if (uiState.passwordError.isNotEmpty()) {
            KoinTextFieldAlert(
                message = uiState.passwordError,
                type = KoinTextFieldAlertType.Error
            )
        } else {
            Spacer(modifier = Modifier.height(6.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        FilledActionButton(
            text = stringResource(Res.string.change_password_button),
            onClick = onNext,
            isLoading = uiState.isLoading,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
internal fun CompleteStep(
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(KoinTheme.colors.neutral50)
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = stringResource(Res.string.password_changed),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = KoinTheme.colors.neutral800
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(Res.string.login_with_new_password),
            fontSize = 14.sp,
            color = KoinTheme.colors.neutral700
        )

        Spacer(modifier = Modifier.height(40.dp))

        FilledActionButton(
            text = stringResource(Res.string.go_to_login),
            onClick = onConfirm,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
