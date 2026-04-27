package `in`.koreatech.business.data.model.signup.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OwnerRegisterRequest(
    @SerialName("attachment_urls")
    val attachmentUrls: List<AttachmentUrl>,
    @SerialName("company_number")
    val companyNumber: String,
    @SerialName("name")
    val name: String,
    @SerialName("password")
    val password: String,
    @SerialName("phone_number")
    val phoneNumber: String,
    @SerialName("shop_number")
    val shopNumber: String,
    @SerialName("shop_id")
    val shopId: Int?,
    @SerialName("shop_name")
    val shopName: String
)

@Serializable
data class AttachmentUrl(
    @SerialName("file_url")
    val fileUrl: String
)
