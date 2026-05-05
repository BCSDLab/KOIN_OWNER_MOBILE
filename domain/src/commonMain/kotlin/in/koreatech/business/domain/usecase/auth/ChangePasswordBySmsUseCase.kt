package `in`.koreatech.business.domain.usecase.auth

import `in`.koreatech.business.domain.repository.AuthRepository

class ChangePasswordBySmsUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(phoneNumber: String, password: String) =
        repository.changePasswordBySms(phoneNumber, password)
}
