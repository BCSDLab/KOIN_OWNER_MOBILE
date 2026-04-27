package `in`.koreatech.business.data.model.store.response

import `in`.koreatech.business.domain.model.MenuCategory
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MenuCategoriesResponse(
    @SerialName("count") val count: Int? = null,
    @SerialName("menu_categories") val categories: List<MenuCategoryItemResponse>? = null
) {
    fun toDomainList(): List<MenuCategory> = categories.orEmpty().map { it.toDomain() }
}

@Serializable
data class MenuCategoryItemResponse(
    @SerialName("id") val id: Int? = null,
    @SerialName("name") val name: String? = null
) {
    fun toDomain(): MenuCategory = MenuCategory(
        id = id ?: 0,
        name = name.orEmpty(),
        menus = emptyList()
    )
}
