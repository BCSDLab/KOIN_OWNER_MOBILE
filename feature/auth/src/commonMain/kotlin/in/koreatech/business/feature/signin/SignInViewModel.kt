package `in`.koreatech.business.feature.signin

import androidx.lifecycle.ViewModel
import `in`.koreatech.business.domain.usecase.auth.SignInUseCase
import `in`.koreatech.business.domain.usecase.owner.GetShopListUseCase
import koreatech.business.designsystem.resources.Res
import koreatech.business.designsystem.resources.error_generic
import koreatech.business.designsystem.resources.error_password_required
import koreatech.business.designsystem.resources.error_phone_required
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

class SignInViewModel(
    private val signInUseCase: SignInUseCase,
    private val getShopListUseCase: GetShopListUseCase
) : ViewModel(),
    ContainerHost<SignInState, SignInSideEffect> {
    override val container = container<SignInState, SignInSideEffect>(SignInState())

    fun onPhoneNumberChanged(phoneNumber: String) = intent {
        reduce {
            state.copy(
                phoneNumber = phoneNumber.filter(Char::isDigit).take(11),
                errorMessageRes = null,
                errorMessage = ""
            )
        }
    }

    fun onPasswordChanged(password: String) = intent {
        reduce { state.copy(password = password, errorMessageRes = null, errorMessage = "") }
    }

    fun togglePasswordVisibility() = intent {
        reduce { state.copy(isPasswordVisible = !state.isPasswordVisible) }
    }

    fun submit() {
        intent {
            val phoneDigits = state.phoneDigits
            val password = state.password.trim()

            if (phoneDigits.isEmpty()) {
                reduce {
                    state.copy(
                        notValidateField = true,
                        errorMessageRes = Res.string.error_phone_required,
                        errorMessage = ""
                    )
                }
                return@intent
            }
            if (password.isEmpty()) {
                reduce {
                    state.copy(
                        notValidateField = true,
                        errorMessageRes = Res.string.error_password_required,
                        errorMessage = ""
                    )
                }
                return@intent
            }

            reduce {
                state.copy(
                    isLoading = true,
                    notValidateField = false,
                    errorMessageRes = null,
                    errorMessage = ""
                )
            }
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
                val serverMessage = exception.message.orEmpty()
                reduce {
                    state.copy(
                        isLoading = false,
                        notValidateField = true,
                        errorMessageRes = if (serverMessage.isBlank()) Res.string.error_generic else null,
                        errorMessage = serverMessage
                    )
                }
            }
        }
    }
}
