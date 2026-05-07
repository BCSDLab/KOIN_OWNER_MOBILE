package `in`.koreatech.business.feature.signin

import androidx.lifecycle.ViewModel
import `in`.koreatech.business.domain.usecase.auth.SignInUseCase
import `in`.koreatech.business.domain.usecase.owner.GetShopListUseCase
import `in`.koreatech.business.ui.util.blockingIntent
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

class SignInViewModel(
    private val signInUseCase: SignInUseCase,
    private val getShopListUseCase: GetShopListUseCase
) : ViewModel(),
    ContainerHost<SignInUiState, SignInSideEffect> {
    override val container = container<SignInUiState, SignInSideEffect>(SignInUiState())

    fun onPhoneNumberChanged(phoneNumber: String) = blockingIntent {
        reduce {
            state.copy(
                phoneNumber = phoneNumber.filter(Char::isDigit).take(11),
                errorMessage = ""
            )
        }
    }

    fun onPasswordChanged(password: String) = blockingIntent {
        reduce { state.copy(password = password, errorMessage = "") }
    }

    fun togglePasswordVisibility() = blockingIntent {
        reduce { state.copy(isPasswordVisible = !state.isPasswordVisible) }
    }

    fun submit() {
        intent {
            val phoneDigits = state.phoneDigits
            val password = state.password.trim()

            if (phoneDigits.isEmpty()) {
                reduce { state.copy(notValidateField = true, errorMessage = "전화번호를 입력해주세요.") }
                return@intent
            }
            if (password.isEmpty()) {
                reduce { state.copy(notValidateField = true, errorMessage = "비밀번호를 입력해주세요.") }
                return@intent
            }

            reduce { state.copy(isLoading = true, notValidateField = false, errorMessage = "") }
            try {
                signInUseCase(phoneNumber = phoneDigits, password = password)
                val stores = getShopListUseCase()
                reduce { state.copy(isLoading = false) }
                postSideEffect(
                    if (stores.isEmpty()) {
                        SignInSideEffect.NavigateToStoreRegister
                    } else {
                        SignInSideEffect.NavigateToStoreMain
                    }
                )
            } catch (exception: Exception) {
                reduce {
                    state.copy(
                        isLoading = false,
                        notValidateField = true,
                        errorMessage = exception.message ?: "오류가 발생했습니다. 잠시 후 다시 시도해주세요."
                    )
                }
            }
        }
    }
}

data class SignInUiState(
    val phoneNumber: String = "",
    val password: String = "",
    val errorMessage: String = "",
    val isLoading: Boolean = false,
    val notValidateField: Boolean = false,
    val isPasswordVisible: Boolean = false
) {
    val phoneDigits: String get() = phoneNumber
}
