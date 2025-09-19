package `in`.koreatech.business.data.network

import `in`.koreatech.business.data.BASE_URL_PRODUCTION
import `in`.koreatech.business.data.BASE_URL_STAGE
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
