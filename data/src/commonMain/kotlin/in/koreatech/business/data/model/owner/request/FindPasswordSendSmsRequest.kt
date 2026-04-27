package `in`.koreatech.business.data.model.owner.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FindPasswordSendSmsRequest(
    @SerialName("phone_number") val phoneNumber: String
)
