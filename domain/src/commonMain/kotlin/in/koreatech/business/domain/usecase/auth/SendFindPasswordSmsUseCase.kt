package `in`.koreatech.business.domain.usecase.auth

import `in`.koreatech.business.domain.repository.AuthRepository
import `in`.koreatech.business.domain.util.runCatchingCancellable

class SendFindPasswordSmsUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(phoneNumber: String): Result<Unit> = runCatchingCancellable {
        repository.sendFindPasswordSms(phoneNumber)
    }
}
