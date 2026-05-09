package `in`.koreatech.business.domain.usecase.auth

import `in`.koreatech.business.domain.repository.AuthRepository
import `in`.koreatech.business.domain.util.runCatchingCancellable

class ChangePasswordBySmsUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(phoneNumber: String, password: String): Result<Unit> = runCatchingCancellable {
        repository.changePasswordBySms(phoneNumber, password)
    }
}
