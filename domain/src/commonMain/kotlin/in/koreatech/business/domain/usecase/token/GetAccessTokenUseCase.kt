package `in`.koreatech.business.domain.usecase.token

import `in`.koreatech.business.domain.repository.TokenRepository

class GetAccessTokenUseCase(private val repository: TokenRepository) {
    suspend operator fun invoke(): String = repository.getAccessToken()
}
