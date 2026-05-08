package `in`.koreatech.business.data.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondError
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class TokenRefreshTest {

    @Serializable
    private data class FakeRefreshRequest(@SerialName("refresh_token") val refreshToken: String)

    @Serializable
    private data class FakeRefreshResponse(
        @SerialName("token") val token: String,
        @SerialName("refresh_token") val refreshToken: String
    )

    private class InMemoryTokenStore(var access: String, var refresh: String)

    private fun jsonHeaders() = headersOf(HttpHeaders.ContentType, "application/json")

    /** 401에 대해 bearer 플러그인이 refreshTokens를 호출하고 retry가 성공하는 경로. */
    @Test
    fun expiredAccessTokenTriggersRefreshAndRetriesRequest() = runTest {
        val store = InMemoryTokenStore(access = "expired-access", refresh = "valid-refresh")
        var protectedCalls = 0
        val engine = MockEngine { request ->
            when (request.url.encodedPath) {
                "/user/refresh" -> respond(
                    content = """{"token":"new-access","refresh_token":"new-refresh"}""",
                    status = HttpStatusCode.OK,
                    headers = jsonHeaders()
                )
                "/owners/me" -> {
                    protectedCalls++
                    val auth = request.headers[HttpHeaders.Authorization]
                    if (auth == "Bearer new-access") {
                        respond(
                            content = """{"name":"홍길동"}""",
                            status = HttpStatusCode.OK,
                            headers = jsonHeaders()
                        )
                    } else {
                        respondError(HttpStatusCode.Unauthorized)
                    }
                }
                else -> error("Unexpected request: ${request.url}")
            }
        }
        val client = bearerClient(engine, store)

        val body = client.get("/owners/me").bodyAsText()

        assertTrue(body.contains("홍길동"))
        assertEquals("new-access", store.access)
        assertEquals("new-refresh", store.refresh)
        // 첫 401 호출 + retry 성공
        assertEquals(2, protectedCalls)
    }

    /** refresh API 자체가 401을 반환하면 토큰을 모두 비우고 원 호출을 실패시킨다. */
    @Test
    fun refreshFailureClearsTokens() = runTest {
        val store = InMemoryTokenStore(access = "expired-access", refresh = "stale-refresh")
        val engine = MockEngine { request ->
            when (request.url.encodedPath) {
                "/user/refresh" -> respondError(HttpStatusCode.Unauthorized)
                "/owners/me" -> respondError(HttpStatusCode.Unauthorized)
                else -> error("Unexpected request: ${request.url}")
            }
        }
        val client = bearerClient(engine, store)

        try {
            client.get("/owners/me")
            fail("Expected ClientRequestException for unauthorized request")
        } catch (_: ClientRequestException) {
            // expected
        }

        assertEquals("", store.access)
        assertEquals("", store.refresh)
    }

    /** refresh 토큰 자체가 비어있으면 refresh 시도 없이 즉시 세션을 정리한다. */
    @Test
    fun blankRefreshTokenSkipsRefreshAndClearsSession() = runTest {
        val store = InMemoryTokenStore(access = "expired-access", refresh = "")
        var refreshCalls = 0
        val engine = MockEngine { request ->
            when (request.url.encodedPath) {
                "/user/refresh" -> {
                    refreshCalls++
                    respondError(HttpStatusCode.Unauthorized)
                }
                "/owners/me" -> respondError(HttpStatusCode.Unauthorized)
                else -> error("Unexpected request: ${request.url}")
            }
        }
        val client = bearerClient(engine, store)

        try {
            client.get("/owners/me")
            fail("Expected ClientRequestException for unauthorized request")
        } catch (_: ClientRequestException) {
            // expected
        }

        // refresh API는 호출되면 안 된다
        assertEquals(0, refreshCalls)
        assertEquals("", store.access)
        assertEquals("", store.refresh)
    }

    /**
     * 프로덕션 NetworkModule.createAuthHttpClient의 bearer 블록과 동일한 동작 재현.
     * 테스트 전용 client builder.
     */
    private fun bearerClient(engine: MockEngine, store: InMemoryTokenStore): HttpClient {
        // 같은 엔진을 공유하는 noAuth 클라이언트 — refresh API 호출용
        val noAuthClient = HttpClient(engine) {
            expectSuccess = true
            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        }
        return HttpClient(engine) {
            expectSuccess = true
            install(Auth) {
                bearer {
                    loadTokens {
                        if (store.access.isBlank()) {
                            null
                        } else {
                            BearerTokens(store.access, store.refresh)
                        }
                    }
                    refreshTokens {
                        if (store.refresh.isBlank()) {
                            store.access = ""
                            store.refresh = ""
                            return@refreshTokens null
                        }
                        runCatching {
                            noAuthClient.post("/user/refresh") {
                                header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                                setBody(FakeRefreshRequest(refreshToken = store.refresh))
                            }
                        }.fold(
                            onSuccess = { httpResponse ->
                                val text = httpResponse.bodyAsText()
                                val parsed = Json { ignoreUnknownKeys = true }
                                    .decodeFromString(FakeRefreshResponse.serializer(), text)
                                store.access = parsed.token
                                store.refresh = parsed.refreshToken
                                BearerTokens(parsed.token, parsed.refreshToken)
                            },
                            onFailure = {
                                store.access = ""
                                store.refresh = ""
                                null
                            }
                        )
                    }
                }
            }
            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
            install(DefaultRequest) {
                url("https://api.test/")
            }
        }
    }
}
