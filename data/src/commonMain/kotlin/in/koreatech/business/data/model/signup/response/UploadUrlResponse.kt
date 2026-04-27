package `in`.koreatech.business.data.model.signup.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UploadUrlResponse(
    @SerialName("pre_signed_url")
    val preSignedUrl: String,
    @SerialName("file_url")
    val fileUrl: String,
)
