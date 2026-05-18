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
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpResponseValidator
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
            // 토큰을 항상 선반영한다. 미설정 시 첫 요청이 무인증으로 나가
            // 401 → loadTokens → 재시도하는 비결정적 경로를 타므로,
            // 만료 토큰의 refresh 트리거를 결정론적으로 만들기 위해 필요.
            sendWithoutRequest { true }

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
                    Napier.w("Refresh skipped: refresh token is blank — clearing session", tag = "Auth")
                    tokenLocalDataSource.saveAccessToken("")
                    tokenLocalDataSource.saveRefreshToken("")
                    return@refreshTokens null
                }

                Napier.i("Access token expired — attempting refresh", tag = "Auth")
                runCatching {
                    ownerAuthApi.refreshToken(currentRefreshToken)
                }.fold(
                    onSuccess = { response ->
                        Napier.i("Token refresh succeeded", tag = "Auth")
                        tokenLocalDataSource.saveAccessToken(response.token)
                        tokenLocalDataSource.saveRefreshToken(response.refreshToken)
                        BearerTokens(response.token, response.refreshToken)
                    },
                    onFailure = { error ->
                        Napier.w("Token refresh failed: ${error.message} — clearing session", tag = "Auth")
                        tokenLocalDataSource.saveAccessToken("")
                        tokenLocalDataSource.saveRefreshToken("")
                        null
                    }
                )
            }
        }
    }

    // refreshTokens가 실행되지 않는 401(서버가 WWW-Authenticate 헤더를
    // 내려주지 않는 경우 등)에도 세션을 확실히 종료시키는 안전망.
    // refresh로 복구되지 않은 최종 401에서만 토큰을 비우며, 토큰이 비면
    // observeAccessToken → AppViewModel.sessionExpired →
    // AppNavigation.replaceRoot(AuthGraph) 체인이 동작해 로그인 화면으로 이동한다.
    // 예외는 그대로 전파돼 호출부의 DomainError.Auth 매핑은 유지된다.
    // (refresh 호출은 noAuth 클라이언트라 이 validator와 무관 → 재귀 없음)
    HttpResponseValidator {
        handleResponseExceptionWithRequest { cause, _ ->
            if (cause is ClientRequestException && cause.response.status.value == 401) {
                Napier.w("Unrecovered 401 — clearing session", tag = "Auth")
                tokenLocalDataSource.saveAccessToken("")
                tokenLocalDataSource.saveRefreshToken("")
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
