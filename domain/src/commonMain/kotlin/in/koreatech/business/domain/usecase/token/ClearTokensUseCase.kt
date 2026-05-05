package `in`.koreatech.business.domain.usecase.token

import `in`.koreatech.business.domain.repository.TokenRepository

class ClearTokensUseCase(private val repository: TokenRepository) {
    suspend operator fun invoke() {
        repository.saveAccessToken("")
        repository.saveRefreshToken("")
    }
}
