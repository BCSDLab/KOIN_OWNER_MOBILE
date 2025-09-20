package `in`.koreatech.business.domain.repository

interface TokenRepository {
    suspend fun getAccessToken(): String

    suspend fun saveAccessToken(accessToken: String)

    suspend fun getRefreshToken(): String

    suspend fun saveRefreshToken(refreshToken: String)
}
