package `in`.koreatech.business.data.model.version

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VersionResponse(
    val id: Int,
    val version: String,
    val type: String,
    val title: String? = null,
    val content: String? = null,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String
)
