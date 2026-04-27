package `in`.koreatech.business.data.api

import `in`.koreatech.business.data.model.shop.response.ShopSearchResponse
import `in`.koreatech.business.data.model.store.response.StoreCategoryListResponse
import `in`.koreatech.business.data.model.version.VersionResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class PublicApi(
    private val httpClient: HttpClient,
    private val s3HttpClient: HttpClient,
) {
    suspend fun searchShops(query: String): ShopSearchResponse =
        httpClient.get("/v2/shops") {
            parameter("query", query)
        }.body()

    suspend fun getStoreCategories(): StoreCategoryListResponse =
        httpClient.get("/shops/categories").body()

    suspend fun uploadToS3(preSignedUrl: String, bytes: ByteArray, mimeType: String) {
        s3HttpClient.put(preSignedUrl) {
            contentType(ContentType.parse(mimeType))
            setBody(bytes)
        }
    }

    suspend fun getRequiredVersion(type: String): VersionResponse =
        httpClient.get("/version/$type").body()
}
