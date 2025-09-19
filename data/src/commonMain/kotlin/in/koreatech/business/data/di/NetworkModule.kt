package `in`.koreatech.business.data.di

import `in`.koreatech.business.data.BASE_URL_PRODUCTION
import `in`.koreatech.business.data.BASE_URL_STAGE
import `in`.koreatech.business.data.api.OwnerApi
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
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.ALL
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
        tokenLocalDataSource: TokenLocalDataSource
    ): HttpClient = HttpClient(httpClientEngine()) {
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.ALL
        }

        install(Auth) {
            bearer {
                loadTokens {
                    val accessToken = tokenLocalDataSource.getAccessToken()
                    val refreshToken = tokenLocalDataSource.getRefreshToken()
                    BearerTokens(accessToken, refreshToken)
                }

                refreshTokens {
                    // TODO: Implement refresh logic
                    null
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

    @Named("noAuth")
    @Single
    fun provideOwnerApi(httpClient: HttpClient): OwnerApi = OwnerApi(httpClient)

    @Named("auth")
    @Single
    fun provideOwnerAuthApi(httpClient: HttpClient): OwnerAuthApi = OwnerAuthApi(httpClient)
}
