package `in`.koreatech.business.data.di

import `in`.koreatech.business.data.BASE_URL_PRODUCTION
import `in`.koreatech.business.data.BASE_URL_STAGE
import `in`.koreatech.business.data.api.OwnerApi
import `in`.koreatech.business.data.api.PublicApi
import `in`.koreatech.business.data.api.auth.OwnerAuthApi
import `in`.koreatech.business.data.network.httpClientEngine
import `in`.koreatech.business.data.source.local.TokenLocalDataSource
import `in`.koreatech.business.data.utils.isDebug
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named
import org.koin.dsl.module

val networkModule = module {
    single(named("noAuth")) { createNoAuthHttpClient() }
    single(named("s3")) { createS3HttpClient() }
    single { OwnerAuthApi(get(named("noAuth"))) }
    single(named("auth")) { createAuthHttpClient(get(), get()) }
    single { OwnerApi(get(named("auth"))) }
    single { PublicApi(get(named("noAuth")), get(named("s3"))) }
}

private fun createNoAuthHttpClient(): HttpClient = HttpClient(httpClientEngine()) {
    expectSuccess = true

    install(Logging) {
        logger = object : Logger {
            override fun log(message: String) = Napier.d(message, tag = "HTTP-noAuth")
        }
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

private fun createAuthHttpClient(
    tokenLocalDataSource: TokenLocalDataSource,
    ownerAuthApi: OwnerAuthApi
): HttpClient = HttpClient(httpClientEngine()) {
    expectSuccess = true

    install(Logging) {
        logger = object : Logger {
            override fun log(message: String) = Napier.d(message, tag = "HTTP-auth")
        }
        level = if (isDebug()) LogLevel.ALL else LogLevel.NONE
    }

    install(Auth) {
        bearer {
            loadTokens {
                val accessToken = tokenLocalDataSource.getAccessToken()
                if (accessToken.isBlank()) {
                    null
                } else {
                    BearerTokens(accessToken, tokenLocalDataSource.getRefreshToken())
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

private fun createS3HttpClient(): HttpClient = HttpClient(httpClientEngine()) {
    expectSuccess = true

    install(Logging) {
        logger = object : Logger {
            override fun log(message: String) = Napier.d(message, tag = "HTTP-s3")
        }
        level = if (isDebug()) LogLevel.ALL else LogLevel.NONE
    }
}
