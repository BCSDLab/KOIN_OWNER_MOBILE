package `in`.koreatech.business.data.model.owner.response

import `in`.koreatech.business.domain.model.owner.OwnerProfile
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OwnerProfileResponse(
    @SerialName("name") val name: String? = null,
    @SerialName("email") val email: String? = null,
    @SerialName("company_number") val companyNumber: String? = null
)

fun OwnerProfileResponse.toDomain() = OwnerProfile(
    name = name.orEmpty(),
    email = email.orEmpty(),
    companyNumber = companyNumber
)
