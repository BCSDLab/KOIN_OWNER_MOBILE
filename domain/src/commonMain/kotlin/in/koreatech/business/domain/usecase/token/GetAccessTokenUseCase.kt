package `in`.koreatech.business.domain.usecase.token

import `in`.koreatech.business.domain.repository.TokenRepository
import `in`.koreatech.business.domain.util.runCatchingCancellable

class GetAccessTokenUseCase(private val repository: TokenRepository) {
    suspend operator fun invoke(): Result<String> = runCatchingCancellable {
        repository.getAccessToken()
    }
}
