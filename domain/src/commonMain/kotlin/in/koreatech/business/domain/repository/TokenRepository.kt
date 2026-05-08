package `in`.koreatech.business.domain.repository

import kotlinx.coroutines.flow.Flow

interface TokenRepository {
    suspend fun getAccessToken(): String

    fun observeAccessToken(): Flow<String>

    suspend fun saveAccessToken(accessToken: String)

    suspend fun getRefreshToken(): String

    suspend fun saveRefreshToken(refreshToken: String)
}
