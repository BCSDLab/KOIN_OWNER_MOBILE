package `in`.koreatech.business.domain.usecase.token

import `in`.koreatech.business.domain.repository.TokenRepository
import `in`.koreatech.business.domain.util.runCatchingCancellable

class ClearTokensUseCase(private val repository: TokenRepository) {
    suspend operator fun invoke(): Result<Unit> = runCatchingCancellable {
        repository.saveAccessToken("")
        repository.saveRefreshToken("")
    }
}
