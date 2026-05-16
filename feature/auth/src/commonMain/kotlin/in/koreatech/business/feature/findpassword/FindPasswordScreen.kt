package `in`.koreatech.business.feature.findpassword

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import `in`.koreatech.business.ui.component.FilledActionButton
import `in`.koreatech.business.ui.component.KoinButton
import `in`.koreatech.business.ui.component.KoinButtonVariant
import `in`.koreatech.business.ui.component.KoinTextField
import `in`.koreatech.business.ui.component.KoinTextFieldAlert
import `in`.koreatech.business.ui.component.KoinTextFieldAlertType
import `in`.koreatech.business.ui.component.PhoneVisualTransformation
import `in`.koreatech.business.ui.theme.KoinTheme
import koreatech.business.designsystem.resources.Res
import koreatech.business.designsystem.resources.change_password_button
import koreatech.business.designsystem.resources.confirm
import koreatech.business.designsystem.resources.field_new_password
import koreatech.business.designsystem.resources.field_new_password_confirm
import koreatech.business.designsystem.resources.field_phone
import koreatech.business.designsystem.resources.field_sms_code
import koreatech.business.designsystem.resources.find_password_new_hint
import koreatech.business.designsystem.resources.find_password_phone_hint
import koreatech.business.designsystem.resources.go_to_login
import koreatech.business.designsystem.resources.login_with_new_password
import koreatech.business.designsystem.resources.password_changed
import koreatech.business.designsystem.resources.password_hide
import koreatech.business.designsystem.resources.password_show
import koreatech.business.designsystem.resources.ph_new_password
import koreatech.business.designsystem.resources.ph_password_confirm
import koreatech.business.designsystem.resources.ph_sms_code
import koreatech.business.designsystem.resources.resend_sms
import koreatech.business.designsystem.resources.send_sms
import koreatech.business.designsystem.resources.sms_sent_to_phone_findpw
import org.jetbrains.compose.resources.stringResource

@Composable
fun PhoneInputStep(
    uiState: FindPasswordState,
    onPhoneChanged: (String) -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(KoinTheme.colors.neutral50)
            .imePadding()
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

        val phoneErrorMessage = uiState.phoneErrorRes?.let { stringResource(it) } ?: uiState.phoneError
        if (phoneErrorMessage.isNotEmpty()) {
            KoinTextFieldAlert(
                message = phoneErrorMessage,
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
fun SmsVerifyStep(
    uiState: FindPasswordState,
    onSmsCodeChanged: (String) -> Unit,
    onNext: () -> Unit,
    onResendSms: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(KoinTheme.colors.neutral50)
            .imePadding()
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

        val smsErrorMessage = uiState.smsErrorRes?.let { stringResource(it) } ?: uiState.smsError
        if (smsErrorMessage.isNotEmpty()) {
            KoinTextFieldAlert(
                message = smsErrorMessage,
                type = KoinTextFieldAlertType.Error
            )
        } else {
            Spacer(modifier = Modifier.height(6.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))

        FilledActionButton(
            text = stringResource(Res.string.confirm),
            onClick = onNext,
            isLoading = uiState.isLoading,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        KoinButton(
            text = stringResource(Res.string.resend_sms),
            onClick = onResendSms,
            variant = KoinButtonVariant.Outlined,
            isLoading = uiState.isLoading,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun NewPasswordStep(
    uiState: FindPasswordState,
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
            .imePadding()
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

        val passwordErrorMessage = uiState.passwordErrorRes?.let { stringResource(it) } ?: uiState.passwordError
        if (passwordErrorMessage.isNotEmpty()) {
            KoinTextFieldAlert(
                message = passwordErrorMessage,
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
fun CompleteStep(
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
