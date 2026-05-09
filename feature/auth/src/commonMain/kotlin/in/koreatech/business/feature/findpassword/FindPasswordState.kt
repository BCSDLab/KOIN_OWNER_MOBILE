package `in`.koreatech.business.feature.findpassword

import org.jetbrains.compose.resources.StringResource

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

data class FindPasswordState(
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
