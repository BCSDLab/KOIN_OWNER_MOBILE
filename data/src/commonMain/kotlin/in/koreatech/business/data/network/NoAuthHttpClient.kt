package `in`.koreatech.business.data.network

import `in`.koreatech.business.data.BASE_URL_PRODUCTION
import `in`.koreatech.business.data.BASE_URL_STAGE
import `in`.koreatech.business.data.utils.isDebug
import io.ktor.client.HttpClient
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

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
