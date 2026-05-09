package `in`.koreatech.business.domain.usecase.auth

import `in`.koreatech.business.domain.repository.AuthRepository
import `in`.koreatech.business.domain.util.runCatchingCancellable

class VerifySignupSmsUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(phoneNumber: String, code: String): Result<String> = runCatchingCancellable {
        repository.verifySmsCode(phoneNumber, code)
    }
}
