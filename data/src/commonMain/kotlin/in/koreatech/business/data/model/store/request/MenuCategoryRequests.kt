package `in`.koreatech.business.data.model.store.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateMenuCategoryRequest(
    @SerialName("name") val name: String
)

@Serializable
data class ModifyMenuCategoryRequest(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String
)
