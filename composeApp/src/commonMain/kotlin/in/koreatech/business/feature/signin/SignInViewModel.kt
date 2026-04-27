package `in`.koreatech.business.feature.signin

import androidx.lifecycle.ViewModel
import `in`.koreatech.business.domain.repository.AuthRepository
import `in`.koreatech.business.domain.repository.OwnerRepository
import `in`.koreatech.business.ui.util.blockingIntent
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

class SignInViewModel(
    private val authRepository: AuthRepository,
    private val ownerRepository: OwnerRepository
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
        reduce {
            state.copy(
                password = password,
                errorMessage = ""
            )
        }
    }

    fun togglePasswordVisibility() = blockingIntent {
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
                        errorMessage = "전화번호를 입력해주세요."
                    )
                }
                return@intent
            }

            if (password.isEmpty()) {
                reduce {
                    state.copy(
                        notValidateField = true,
                        errorMessage = "비밀번호를 입력해주세요."
                    )
                }
                return@intent
            }

            reduce {
                state.copy(
                    isLoading = true,
                    notValidateField = false,
                    errorMessage = ""
                )
            }

            try {
                authRepository.signIn(phoneNumber = phoneDigits, password = password)
                val stores = ownerRepository.getShopList()
                reduce {
                    state.copy(
                        isLoading = false
                    )
                }
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

    companion object {
        private fun formatPhoneNumber(phoneNumber: String): String {
            val digits = phoneNumber.filter(Char::isDigit).take(MAX_PHONE_DIGITS)
            return when {
                digits.length <= PHONE_PREFIX_LENGTH -> digits
                digits.length <= PHONE_MIDDLE_MAX_LENGTH -> {
                    "${digits.take(PHONE_PREFIX_LENGTH)}-${digits.drop(PHONE_PREFIX_LENGTH)}"
                }

                else -> {
                    "${digits.take(PHONE_PREFIX_LENGTH)}-${digits.substring(PHONE_PREFIX_LENGTH, PHONE_MIDDLE_MAX_LENGTH)}-${digits.drop(PHONE_MIDDLE_MAX_LENGTH)}"
                }
            }
        }

        private const val MAX_PHONE_DIGITS = 11
        private const val PHONE_PREFIX_LENGTH = 3
        private const val PHONE_MIDDLE_MAX_LENGTH = 7
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
    val phoneDigits: String
        get() = phoneNumber
}
