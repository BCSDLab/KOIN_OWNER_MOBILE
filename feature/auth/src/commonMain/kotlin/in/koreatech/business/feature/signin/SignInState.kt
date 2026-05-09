package `in`.koreatech.business.feature.signin

import org.jetbrains.compose.resources.StringResource

data class SignInState(
    val phoneNumber: String = "",
    val password: String = "",
    val errorMessageRes: StringResource? = null,
    val errorMessage: String = "",
    val isLoading: Boolean = false,
    val notValidateField: Boolean = false,
    val isPasswordVisible: Boolean = false
) {
    val phoneDigits: String get() = phoneNumber
}
