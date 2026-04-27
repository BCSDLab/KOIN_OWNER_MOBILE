package `in`.koreatech.business.data.model.owner.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OwnerLoginResponse(
    @SerialName("token")
    val token: String,
    @SerialName("refresh_token")
    val refreshToken: String
)
