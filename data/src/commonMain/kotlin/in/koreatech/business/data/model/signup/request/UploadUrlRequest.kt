package `in`.koreatech.business.data.model.signup.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UploadUrlRequest(
    @SerialName("content_length")
    val contentLength: Long,
    @SerialName("content_type")
    val contentType: String,
    @SerialName("file_name")
    val fileName: String,
)
