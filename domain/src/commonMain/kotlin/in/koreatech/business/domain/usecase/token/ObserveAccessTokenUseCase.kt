package `in`.koreatech.business.domain.usecase.token

import `in`.koreatech.business.domain.repository.TokenRepository
import kotlinx.coroutines.flow.Flow

class ObserveAccessTokenUseCase(private val repository: TokenRepository) {
    operator fun invoke(): Flow<String> = repository.observeAccessToken()
}
