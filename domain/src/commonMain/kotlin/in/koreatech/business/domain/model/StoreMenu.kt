package `in`.koreatech.business.domain.model

data class MenuCategory(
    val id: Int,
    val name: String,
    val menus: List<MenuItem>
)

data class MenuItem(
    val id: Int,
    val shopId: Int = 0,
    val name: String,
    val description: String,
    val imageUrls: List<String>,
    val isSingle: Boolean,
    val singlePrice: Int? = null,
    val optionPrices: List<MenuOptionPrice>,
    val categoryIds: List<Int> = emptyList()
)

data class MenuOptionPrice(
    val option: String,
    val price: Int
)
