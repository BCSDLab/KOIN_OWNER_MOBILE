package `in`.koreatech.business.feature.store.menu.categories

import `in`.koreatech.business.domain.model.MenuCategory

data class ManageCategoriesState(
    val isLoading: Boolean = false,
    val storeId: String? = null,
    val categories: List<MenuCategory> = emptyList(),
    val blockDeleteCategory: MenuCategory? = null,
    val errorMessage: String = ""
)
