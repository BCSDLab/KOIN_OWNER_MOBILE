package `in`.koreatech.business.data.model.store.response

import `in`.koreatech.business.domain.model.MenuCategory
import `in`.koreatech.business.domain.model.MenuItem
import `in`.koreatech.business.domain.model.MenuOptionPrice
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StoreMenusResponse(
    @SerialName("count") val count: Int? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
    @SerialName("menu_categories") val categories: List<MenuCategoryResponse>? = null
) {
    fun toDomainList(): List<MenuCategory> = categories.orEmpty().map { it.toDomain() }
}

@Serializable
data class MenuCategoryResponse(
    @SerialName("id") val id: Int? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("menus") val menus: List<MenuItemResponse>? = null
) {
    fun toDomain(): MenuCategory = MenuCategory(
        id = id ?: 0,
        name = name.orEmpty(),
        menus = menus.orEmpty().map { it.toDomain() }
    )
}

@Serializable
data class MenuItemResponse(
    @SerialName("id") val id: Int? = null,
    @SerialName("shop_id") val shopId: Int? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("description") val description: String? = null,
    @SerialName("image_urls") val imageUrls: List<String>? = null,
    @SerialName("is_single") val isSingle: Boolean? = null,
    @SerialName("single_price") val singlePrice: Int? = null,
    @SerialName("option_prices") val optionPrices: List<MenuOptionPriceResponse>? = null,
    @SerialName("category_ids") val categoryIds: List<Int>? = null,
    @SerialName("is_hidden") val isHidden: Boolean? = null
) {
    fun toDomain(): MenuItem {
        val resolvedOptionPrices = if (isSingle == true && singlePrice != null) {
            listOf(MenuOptionPrice(option = "", price = singlePrice))
        } else {
            optionPrices.orEmpty().map { it.toDomain() }
        }
        return MenuItem(
            id = id ?: 0,
            shopId = shopId ?: 0,
            name = name.orEmpty(),
            description = description.orEmpty(),
            imageUrls = imageUrls.orEmpty(),
            isSingle = isSingle ?: (optionPrices.orEmpty().size <= 1),
            singlePrice = singlePrice,
            optionPrices = resolvedOptionPrices,
            categoryIds = categoryIds.orEmpty(),
            isHidden = isHidden ?: false
        )
    }
}

@Serializable
data class MenuOptionPriceResponse(
    @SerialName("option") val option: String? = null,
    @SerialName("price") val price: Int? = null
) {
    fun toDomain(): MenuOptionPrice = MenuOptionPrice(
        option = option.orEmpty(),
        price = price ?: 0
    )
}
