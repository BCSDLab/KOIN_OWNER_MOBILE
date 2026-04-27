package `in`.koreatech.business.data.model.signup.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VerifySmsRequest(
    @SerialName("phone_number")
    val phoneNumber: String,
    @SerialName("certification_code")
    val certificationCode: String
)
