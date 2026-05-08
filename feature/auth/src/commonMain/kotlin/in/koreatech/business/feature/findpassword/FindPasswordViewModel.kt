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
import org.jetbrains.compose.resources.StringResource
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

enum class FindPasswordStep {
    PhoneInput,
    SmsVerify,
    NewPassword,
    Complete;

    val route: String get() = when (this) {
        PhoneInput -> "phone-input"
        SmsVerify -> "sms-verify"
        NewPassword -> "new-password"
        Complete -> "complete"
    }
}

data class FindPasswordUiState(
    val step: FindPasswordStep = FindPasswordStep.PhoneInput,
    val phoneNumber: String = "",
    val phoneError: String = "",
    val phoneErrorRes: StringResource? = null,
    val smsCode: String = "",
    val smsError: String = "",
    val smsErrorRes: StringResource? = null,
    val newPassword: String = "",
    val newPasswordConfirm: String = "",
    val isPasswordVisible: Boolean = false,
    val isPasswordConfirmVisible: Boolean = false,
    val passwordError: String = "",
    val passwordErrorRes: StringResource? = null,
    val isLoading: Boolean = false
)

class FindPasswordViewModel(
    private val sendFindPasswordSmsUseCase: SendFindPasswordSmsUseCase,
    private val verifyFindPasswordSmsUseCase: VerifyFindPasswordSmsUseCase,
    private val changePasswordBySmsUseCase: ChangePasswordBySmsUseCase
) : ViewModel(),
    ContainerHost<FindPasswordUiState, Nothing> {
    override val container = container<FindPasswordUiState, Nothing>(FindPasswordUiState())

    fun onPhoneNumberChanged(value: String) = intent {
        reduce {
            state.copy(
                phoneNumber = BusinessFormatters.digitsOnly(value, 11),
                phoneError = "",
                phoneErrorRes = null
            )
        }
    }

    fun onSmsCodeChanged(value: String) = intent {
        reduce {
            state.copy(
                smsCode = BusinessFormatters.digitsOnly(value, 6),
                smsError = "",
                smsErrorRes = null
            )
        }
    }

    fun onNewPasswordChanged(value: String) = intent {
        reduce { state.copy(newPassword = value, passwordError = "", passwordErrorRes = null) }
    }

    fun onNewPasswordConfirmChanged(value: String) = intent {
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
        try {
            sendFindPasswordSmsUseCase(phone)
            reduce { state.copy(isLoading = false, step = FindPasswordStep.SmsVerify) }
        } catch (e: Exception) {
            val msg = e.message.orEmpty()
            reduce {
                state.copy(
                    phoneError = msg,
                    phoneErrorRes = if (msg.isEmpty()) Res.string.error_sms_send_failed else null,
                    isLoading = false
                )
            }
        }
    }

    fun resendSms() = intent {
        val phone = state.phoneNumber
        reduce { state.copy(isLoading = true, smsError = "", smsErrorRes = null) }
        try {
            sendFindPasswordSmsUseCase(phone)
            reduce { state.copy(isLoading = false, smsCode = "") }
        } catch (e: Exception) {
            val msg = e.message.orEmpty()
            reduce {
                state.copy(
                    smsError = msg,
                    smsErrorRes = if (msg.isEmpty()) Res.string.error_sms_resend_failed else null,
                    isLoading = false
                )
            }
        }
    }

    fun submitSms() = intent {
        if (state.smsCode.length < 6) {
            reduce { state.copy(smsError = "", smsErrorRes = Res.string.error_sms_code_required) }
            return@intent
        }
        reduce { state.copy(isLoading = true, smsError = "", smsErrorRes = null) }
        try {
            verifyFindPasswordSmsUseCase(state.phoneNumber, state.smsCode)
            reduce { state.copy(isLoading = false, step = FindPasswordStep.NewPassword) }
        } catch (e: Exception) {
            val msg = e.message.orEmpty()
            reduce {
                state.copy(
                    smsError = msg,
                    smsErrorRes = if (msg.isEmpty()) Res.string.error_sms_code_invalid else null,
                    isLoading = false
                )
            }
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
        try {
            changePasswordBySmsUseCase(state.phoneNumber, state.newPassword)
            reduce { state.copy(isLoading = false, step = FindPasswordStep.Complete) }
        } catch (e: Exception) {
            val msg = e.message.orEmpty()
            reduce {
                state.copy(
                    passwordError = msg,
                    passwordErrorRes = if (msg.isEmpty()) Res.string.find_password_error_change_failed else null,
                    isLoading = false
                )
            }
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
