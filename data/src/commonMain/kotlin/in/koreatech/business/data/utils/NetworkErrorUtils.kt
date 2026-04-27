package `in`.koreatech.business.data.utils

import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

suspend fun Exception.toUserMessage(): String = when (this) {
    is ClientRequestException -> response.toUserMessage()
    is RedirectResponseException -> response.toUserMessage()
    is ServerResponseException -> response.toUserMessage()
    is ResponseException -> response.toUserMessage()
    else -> message?.takeIf { it.isNotBlank() } ?: DEFAULT_ERROR_MESSAGE
}

private suspend fun io.ktor.client.statement.HttpResponse.toUserMessage(): String {
    val bodyText = runCatching { bodyAsText() }.getOrDefault("")
    val apiMessage = runCatching {
        Json.parseToJsonElement(bodyText)
            .jsonObject["message"]
            ?.jsonPrimitive
            ?.content
            ?.takeIf { it.isNotBlank() }
    }.getOrNull()

    if (apiMessage != null) {
        return apiMessage
    }

    return when (status.value) {
        400, 422 -> "입력 정보를 확인해주세요."
        401 -> "인증이 필요합니다. 다시 로그인해주세요."
        403 -> "접근 권한이 없습니다."
        404 -> "요청한 정보를 찾을 수 없습니다."
        409 -> "이미 존재하는 정보입니다."
        in 500..599 -> "서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요."
        else -> DEFAULT_ERROR_MESSAGE
    }
}

private const val DEFAULT_ERROR_MESSAGE = "오류가 발생했습니다. 잠시 후 다시 시도해주세요."
