package `in`.koreatech.business.domain.usecase.auth

import `in`.koreatech.business.domain.repository.AuthRepository

class SendFindPasswordSmsUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(phoneNumber: String) =
        repository.sendFindPasswordSms(phoneNumber)
}
