package `in`.koreatech.business.data.model.owner.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChangePasswordRequest(
    @SerialName("phone_number") val phoneNumber: String,
    @SerialName("password") val password: String
)
