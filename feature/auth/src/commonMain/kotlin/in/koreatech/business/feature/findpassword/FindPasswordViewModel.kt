package `in`.koreatech.business.feature.findpassword

import androidx.lifecycle.ViewModel
import `in`.koreatech.business.domain.usecase.auth.ChangePasswordBySmsUseCase
import `in`.koreatech.business.domain.usecase.auth.SendFindPasswordSmsUseCase
import `in`.koreatech.business.domain.usecase.auth.VerifyFindPasswordSmsUseCase
import `in`.koreatech.business.ui.util.BusinessFormatters
import `in`.koreatech.business.ui.util.BusinessValidators
import koreatech.business.designsystem.resources.Res
import koreatech.business.designsystem.resources.error_password_invalid
import koreatech.business.designsystem.resources.error_password_mismatch
import koreatech.business.designsystem.resources.error_phone_invalid
import koreatech.business.designsystem.resources.error_sms_code_invalid
import koreatech.business.designsystem.resources.error_sms_code_required
import koreatech.business.designsystem.resources.error_sms_resend_failed
import koreatech.business.designsystem.resources.error_sms_send_failed
import koreatech.business.designsystem.resources.find_password_error_change_failed
import org.orbitmvi.orbit.blockingIntent
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

class FindPasswordViewModel(
    private val sendFindPasswordSmsUseCase: SendFindPasswordSmsUseCase,
    private val verifyFindPasswordSmsUseCase: VerifyFindPasswordSmsUseCase,
    private val changePasswordBySmsUseCase: ChangePasswordBySmsUseCase
) : ViewModel(),
    ContainerHost<FindPasswordState, Nothing> {
    override val container = container<FindPasswordState, Nothing>(FindPasswordState())

    fun onPhoneNumberChanged(value: String) = blockingIntent {
        reduce {
            state.copy(
                phoneNumber = BusinessFormatters.digitsOnly(value, 11),
                phoneError = "",
                phoneErrorRes = null
            )
        }
    }

    fun onSmsCodeChanged(value: String) = blockingIntent {
        reduce {
            state.copy(
                smsCode = BusinessFormatters.digitsOnly(value, 6),
                smsError = "",
                smsErrorRes = null
            )
        }
    }

    fun onNewPasswordChanged(value: String) = blockingIntent {
        reduce { state.copy(newPassword = value, passwordError = "", passwordErrorRes = null) }
    }

    fun onNewPasswordConfirmChanged(value: String) = blockingIntent {
        reduce { state.copy(newPasswordConfirm = value, passwordError = "", passwordErrorRes = null) }
    }

    fun onTogglePasswordVisibility() = intent {
        reduce { state.copy(isPasswordVisible = !state.isPasswordVisible) }
    }

    fun onTogglePasswordConfirmVisibility() = intent {
        reduce { state.copy(isPasswordConfirmVisible = !state.isPasswordConfirmVisible) }
    }

    fun submitPhone() = intent {
        val phone = state.phoneNumber
        if (!BusinessValidators.isValidPhone(phone)) {
            reduce { state.copy(phoneError = "", phoneErrorRes = Res.string.error_phone_invalid) }
            return@intent
        }
        reduce { state.copy(isLoading = true, phoneError = "", phoneErrorRes = null) }
        sendFindPasswordSmsUseCase(phone)
            .onSuccess { advanceToSmsVerify() }
            .onFailure { showPhoneError(it.message.orEmpty()) }
    }

    private fun advanceToSmsVerify() = intent {
        reduce { state.copy(isLoading = false, step = FindPasswordStep.SmsVerify) }
    }

    private fun showPhoneError(message: String) = intent {
        reduce {
            state.copy(
                phoneError = message,
                phoneErrorRes = if (message.isEmpty()) Res.string.error_sms_send_failed else null,
                isLoading = false
            )
        }
    }

    fun resendSms() = intent {
        val phone = state.phoneNumber
        reduce { state.copy(isLoading = true, smsError = "", smsErrorRes = null) }
        sendFindPasswordSmsUseCase(phone)
            .onSuccess { resetSmsAfterResend() }
            .onFailure { showResendSmsError(it.message.orEmpty()) }
    }

    private fun resetSmsAfterResend() = intent {
        reduce { state.copy(isLoading = false, smsCode = "") }
    }

    private fun showResendSmsError(message: String) = intent {
        reduce {
            state.copy(
                smsError = message,
                smsErrorRes = if (message.isEmpty()) Res.string.error_sms_resend_failed else null,
                isLoading = false
            )
        }
    }

    fun submitSms() = intent {
        if (state.smsCode.length < 6) {
            reduce { state.copy(smsError = "", smsErrorRes = Res.string.error_sms_code_required) }
            return@intent
        }
        reduce { state.copy(isLoading = true, smsError = "", smsErrorRes = null) }
        verifyFindPasswordSmsUseCase(state.phoneNumber, state.smsCode)
            .onSuccess { advanceToNewPassword() }
            .onFailure { showSmsError(it.message.orEmpty()) }
    }

    private fun advanceToNewPassword() = intent {
        reduce { state.copy(isLoading = false, step = FindPasswordStep.NewPassword) }
    }

    private fun showSmsError(message: String) = intent {
        reduce {
            state.copy(
                smsError = message,
                smsErrorRes = if (message.isEmpty()) Res.string.error_sms_code_invalid else null,
                isLoading = false
            )
        }
    }

    fun submitNewPassword() = intent {
        when {
            !BusinessValidators.isValidPassword(state.newPassword) -> {
                reduce {
                    state.copy(passwordError = "", passwordErrorRes = Res.string.error_password_invalid)
                }
                return@intent
            }
            state.newPassword != state.newPasswordConfirm -> {
                reduce {
                    state.copy(passwordError = "", passwordErrorRes = Res.string.error_password_mismatch)
                }
                return@intent
            }
        }
        reduce { state.copy(isLoading = true, passwordError = "", passwordErrorRes = null) }
        changePasswordBySmsUseCase(state.phoneNumber, state.newPassword)
            .onSuccess { advanceToComplete() }
            .onFailure { showPasswordError(it.message.orEmpty()) }
    }

    private fun advanceToComplete() = intent {
        reduce { state.copy(isLoading = false, step = FindPasswordStep.Complete) }
    }

    private fun showPasswordError(message: String) = intent {
        reduce {
            state.copy(
                passwordError = message,
                passwordErrorRes = if (message.isEmpty()) Res.string.find_password_error_change_failed else null,
                isLoading = false
            )
        }
    }

    fun navigateBack(): Boolean {
        val previousStep = when (container.stateFlow.value.step) {
            FindPasswordStep.PhoneInput -> return false
            FindPasswordStep.SmsVerify -> FindPasswordStep.PhoneInput
            FindPasswordStep.NewPassword -> FindPasswordStep.SmsVerify
            FindPasswordStep.Complete -> return false
        }
        intent(registerIdling = false) { reduce { state.copy(step = previousStep) } }
        return true
    }
}
