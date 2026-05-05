package `in`.koreatech.business.domain.usecase.auth

import `in`.koreatech.business.domain.repository.AuthRepository

class VerifySignupSmsUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(phoneNumber: String, code: String): String =
        repository.verifySmsCode(phoneNumber, code)
}
