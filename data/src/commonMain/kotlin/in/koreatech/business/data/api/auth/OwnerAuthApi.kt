package `in`.koreatech.business.data.api.auth

import `in`.koreatech.business.data.model.owner.request.ChangePasswordRequest
import `in`.koreatech.business.data.model.owner.request.FindPasswordSendSmsRequest
import `in`.koreatech.business.data.model.owner.request.FindPasswordVerifySmsRequest
import `in`.koreatech.business.data.model.owner.request.OwnerLoginRequest
import `in`.koreatech.business.data.model.owner.request.RefreshTokenRequest
import `in`.koreatech.business.data.model.owner.response.OwnerLoginResponse
import `in`.koreatech.business.data.model.signup.request.OwnerRegisterRequest
import `in`.koreatech.business.data.model.signup.request.SendSignupSmsRequest
import `in`.koreatech.business.data.model.signup.request.VerifySmsRequest
import `in`.koreatech.business.data.model.signup.response.VerifySmsResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode

class OwnerAuthApi(private val httpClient: HttpClient) {
    suspend fun login(request: OwnerLoginRequest): OwnerLoginResponse = httpClient.post("/owner/login") {
        header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        setBody(request)
    }.body()

    /**
     * GET /owners/exists/account — 계정 존재 여부 확인.
     * @return true 이미 가입된 번호(409). false 아직 가입되지 않은 번호(200).
     */
    suspend fun checkPhoneExists(phoneNumber: String): Boolean {
        return try {
            httpClient.get("/owners/exists/account") {
                parameter("account", phoneNumber)
            }
            false
        } catch (exception: ClientRequestException) {
            if (exception.response.status == HttpStatusCode.Conflict) {
                true
            } else {
                throw exception
            }
        }
    }

    suspend fun sendSignupSms(request: SendSignupSmsRequest) = httpClient.post("/owners/verification/sms") {
        header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        setBody(request)
    }

    suspend fun verifySmsCode(request: VerifySmsRequest): VerifySmsResponse =
        httpClient.post("/owners/verification/code/sms") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(request)
        }.body()

    suspend fun register(request: OwnerRegisterRequest) = httpClient.post("/owners/register/phone") {
        header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        setBody(request)
    }

    suspend fun refreshToken(refreshToken: String): OwnerLoginResponse = httpClient.post("/user/refresh") {
        header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        setBody(RefreshTokenRequest(refreshToken = refreshToken))
    }.body()

    suspend fun deleteAccount() = httpClient.delete("/user")

    suspend fun sendFindPasswordSms(request: FindPasswordSendSmsRequest) =
        httpClient.post("/owners/password/reset/verification/sms") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(request)
        }

    suspend fun verifyFindPasswordSms(request: FindPasswordVerifySmsRequest) =
        httpClient.post("/owners/password/reset/send/sms") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(request)
        }

    suspend fun changePasswordBySms(request: ChangePasswordRequest) =
        httpClient.put("/owners/password/reset/sms") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(request)
        }
}
