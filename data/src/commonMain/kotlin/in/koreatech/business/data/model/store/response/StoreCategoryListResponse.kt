package `in`.koreatech.business.data.model.store.response

import `in`.koreatech.business.domain.model.StoreCategory
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StoreCategoryListResponse(
    @SerialName("shop_categories") val shopCategories: List<StoreCategoryItemResponse> = emptyList()
)

@Serializable
data class StoreCategoryItemResponse(
    @SerialName("id") val id: Int? = null,
    @SerialName("name") val name: String? = null
) {
    fun toDomain(): StoreCategory? {
        if (id == null || name == null) return null
        return StoreCategory(id = id, name = name)
    }
}

fun StoreCategoryListResponse.toDomainList(): List<StoreCategory> = shopCategories.mapNotNull { it.toDomain() }
