package `in`.koreatech.business.domain.usecase.auth

import `in`.koreatech.business.domain.repository.AuthRepository

class VerifyFindPasswordSmsUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(phoneNumber: String, code: String) =
        repository.verifyFindPasswordSms(phoneNumber, code)
}
