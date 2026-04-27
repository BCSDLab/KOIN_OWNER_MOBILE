package `in`.koreatech.business.data.model.signup.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VerifySmsResponse(
    @SerialName("token")
    val token: String
)
