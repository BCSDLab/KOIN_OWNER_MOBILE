package `in`.koreatech.business.feature.store.menu.manage

import `in`.koreatech.business.domain.model.MenuCategory

data class ManageMenusState(
    val isLoading: Boolean = false,
    val storeId: String? = null,
    val categories: List<MenuCategory> = emptyList(),
    val deletingMenuId: String? = null,
    val errorMessage: String = ""
)
