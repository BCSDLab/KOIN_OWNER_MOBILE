package `in`.koreatech.business.data.model.owner.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OwnerLoginRequest(
    @SerialName("account")
    val phoneNumber: String,
    @SerialName("password")
    val password: String
)
