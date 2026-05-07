package `in`.koreatech.business.feature.findpassword

import androidx.lifecycle.ViewModel
import `in`.koreatech.business.domain.usecase.auth.ChangePasswordBySmsUseCase
import `in`.koreatech.business.domain.usecase.auth.SendFindPasswordSmsUseCase
import `in`.koreatech.business.domain.usecase.auth.VerifyFindPasswordSmsUseCase
import `in`.koreatech.business.ui.util.BusinessFormatters
import `in`.koreatech.business.ui.util.BusinessValidators
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
    val smsCode: String = "",
    val smsError: String = "",
    val newPassword: String = "",
    val newPasswordConfirm: String = "",
    val isPasswordVisible: Boolean = false,
    val isPasswordConfirmVisible: Boolean = false,
    val passwordError: String = "",
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
        reduce { state.copy(phoneNumber = BusinessFormatters.digitsOnly(value, 11), phoneError = "") }
    }

    fun onSmsCodeChanged(value: String) = intent {
        reduce { state.copy(smsCode = BusinessFormatters.digitsOnly(value, 6), smsError = "") }
    }

    fun onNewPasswordChanged(value: String) = intent {
        reduce { state.copy(newPassword = value, passwordError = "") }
    }

    fun onNewPasswordConfirmChanged(value: String) = intent {
        reduce { state.copy(newPasswordConfirm = value, passwordError = "") }
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
            reduce { state.copy(phoneError = "올바른 전화번호를 입력해주세요.") }
            return@intent
        }
        reduce { state.copy(isLoading = true, phoneError = "") }
        try {
            sendFindPasswordSmsUseCase(phone)
            reduce { state.copy(isLoading = false, step = FindPasswordStep.SmsVerify) }
        } catch (e: Exception) {
            reduce { state.copy(phoneError = e.message ?: "SMS 발송에 실패했습니다.", isLoading = false) }
        }
    }

    fun resendSms() = intent {
        val phone = state.phoneNumber
        reduce { state.copy(isLoading = true, smsError = "") }
        try {
            sendFindPasswordSmsUseCase(phone)
            reduce { state.copy(isLoading = false, smsCode = "") }
        } catch (e: Exception) {
            reduce { state.copy(smsError = e.message ?: "SMS 재발송에 실패했습니다.", isLoading = false) }
        }
    }

    fun submitSms() = intent {
        if (state.smsCode.length < 6) {
            reduce { state.copy(smsError = "인증번호 6자리를 입력해주세요.") }
            return@intent
        }
        reduce { state.copy(isLoading = true, smsError = "") }
        try {
            verifyFindPasswordSmsUseCase(state.phoneNumber, state.smsCode)
            reduce { state.copy(isLoading = false, step = FindPasswordStep.NewPassword) }
        } catch (e: Exception) {
            reduce { state.copy(smsError = e.message ?: "인증번호가 올바르지 않습니다.", isLoading = false) }
        }
    }

    fun submitNewPassword() = intent {
        when {
            !BusinessValidators.isValidPassword(state.newPassword) -> {
                reduce { state.copy(passwordError = "영문, 숫자, 특수문자를 포함한 6~18자 비밀번호를 입력해주세요.") }
                return@intent
            }
            state.newPassword != state.newPasswordConfirm -> {
                reduce { state.copy(passwordError = "비밀번호가 일치하지 않습니다.") }
                return@intent
            }
        }
        reduce { state.copy(isLoading = true, passwordError = "") }
        try {
            changePasswordBySmsUseCase(state.phoneNumber, state.newPassword)
            reduce { state.copy(isLoading = false, step = FindPasswordStep.Complete) }
        } catch (e: Exception) {
            reduce { state.copy(passwordError = e.message ?: "비밀번호 변경에 실패했습니다.", isLoading = false) }
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
