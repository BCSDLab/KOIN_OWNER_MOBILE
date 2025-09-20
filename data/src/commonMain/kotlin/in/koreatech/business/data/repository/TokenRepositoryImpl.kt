package `in`.koreatech.business.data.repository

import `in`.koreatech.business.data.source.local.TokenLocalDataSource
import `in`.koreatech.business.domain.repository.TokenRepository

class TokenRepositoryImpl(
    private val tokenLocalDataSource: TokenLocalDataSource
) : TokenRepository {
    override suspend fun getAccessToken(): String = tokenLocalDataSource.getAccessToken()

    override suspend fun saveAccessToken(accessToken: String) {
        tokenLocalDataSource.saveAccessToken(accessToken)
    }

    override suspend fun getRefreshToken(): String = tokenLocalDataSource.getRefreshToken()

    override suspend fun saveRefreshToken(refreshToken: String) {
        tokenLocalDataSource.saveRefreshToken(refreshToken)
    }
}
