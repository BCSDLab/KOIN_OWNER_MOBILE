package `in`.koreatech.business.domain.usecase.auth

import `in`.koreatech.business.domain.repository.AuthRepository

class CheckPhoneExistsUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(phoneNumber: String): Boolean =
        repository.checkPhoneExists(phoneNumber)
}
