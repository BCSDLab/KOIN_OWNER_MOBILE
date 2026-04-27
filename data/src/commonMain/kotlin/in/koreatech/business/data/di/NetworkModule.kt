package `in`.koreatech.business.data.di

import `in`.koreatech.business.data.BASE_URL_PRODUCTION
import `in`.koreatech.business.data.BASE_URL_STAGE
import `in`.koreatech.business.data.api.OwnerApi
import `in`.koreatech.business.data.api.PublicApi
import `in`.koreatech.business.data.api.auth.OwnerAuthApi
import `in`.koreatech.business.data.network.httpClientEngine
import `in`.koreatech.business.data.source.local.TokenLocalDataSource
import `in`.koreatech.business.data.utils.isDebug
import io.ktor.client.HttpClient
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Module
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

@Module
class NetworkModule {
    @Named("noAuth")
    @Single
    fun provideNoAuthHttpClient(): HttpClient = HttpClient(httpClientEngine()) {
        expectSuccess = true

        install(Logging) {
            logger = Logger.DEFAULT
            level = if (isDebug()) LogLevel.ALL else LogLevel.NONE
        }

        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                }
            )
        }

        install(DefaultRequest) {
            url(if (isDebug()) BASE_URL_STAGE else BASE_URL_PRODUCTION)
        }
    }

    @Named("auth")
    @Single
    fun provideAuthHttpClient(
        tokenLocalDataSource: TokenLocalDataSource,
        ownerAuthApi: OwnerAuthApi
    ): HttpClient = HttpClient(httpClientEngine()) {
        expectSuccess = true

        install(Logging) {
            logger = Logger.DEFAULT
            level = if (isDebug()) LogLevel.ALL else LogLevel.NONE
        }

        install(Auth) {
            bearer {
                loadTokens {
                    val accessToken = tokenLocalDataSource.getAccessToken()
                    val refreshToken = tokenLocalDataSource.getRefreshToken()
                    if (accessToken.isBlank() || refreshToken.isBlank()) {
                        null
                    } else {
                        BearerTokens(accessToken, refreshToken)
                    }
                }

                refreshTokens {
                    val currentRefreshToken = tokenLocalDataSource.getRefreshToken()
                    if (currentRefreshToken.isBlank()) {
                        tokenLocalDataSource.saveAccessToken("")
                        tokenLocalDataSource.saveRefreshToken("")
                        return@refreshTokens null
                    }

                    runCatching {
                        ownerAuthApi.refreshToken(currentRefreshToken)
                    }.fold(
                        onSuccess = { response ->
                            tokenLocalDataSource.saveAccessToken(response.token)
                            tokenLocalDataSource.saveRefreshToken(response.refreshToken)
                            BearerTokens(response.token, response.refreshToken)
                        },
                        onFailure = {
                            tokenLocalDataSource.saveAccessToken("")
                            tokenLocalDataSource.saveRefreshToken("")
                            null
                        }
                    )
                }
            }
        }

        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                }
            )
        }

        install(DefaultRequest) {
            url(if (isDebug()) BASE_URL_STAGE else BASE_URL_PRODUCTION)
        }
    }

    @Named("s3")
    @Single
    fun provideS3HttpClient(): HttpClient = HttpClient(httpClientEngine()) {
        expectSuccess = true
    }

    @Single
    fun provideOwnerApi(@Named("auth") httpClient: HttpClient): OwnerApi = OwnerApi(httpClient)

    @Single
    fun provideOwnerAuthApi(@Named("noAuth") httpClient: HttpClient): OwnerAuthApi = OwnerAuthApi(httpClient)

    @Single
    fun providePublicApi(
        @Named("noAuth") httpClient: HttpClient,
        @Named("s3") s3HttpClient: HttpClient,
    ): PublicApi = PublicApi(httpClient, s3HttpClient)
}
