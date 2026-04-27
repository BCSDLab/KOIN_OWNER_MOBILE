package `in`.koreatech.business.data.model.owner.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FindPasswordVerifySmsRequest(
    @SerialName("phone_number") val phoneNumber: String,
    @SerialName("certification_code") val certificationCode: String
)
