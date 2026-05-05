package `in`.koreatech.business.domain.usecase.auth

import `in`.koreatech.business.domain.repository.AuthRepository

class SignOutUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke() = repository.signOut()
}
