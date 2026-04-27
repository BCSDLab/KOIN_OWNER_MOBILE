package `in`.koreatech.business.data.model.signup.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SendSignupSmsRequest(
    @SerialName("phone_number")
    val phoneNumber: String
)
