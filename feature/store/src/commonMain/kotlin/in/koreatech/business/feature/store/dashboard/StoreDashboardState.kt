package `in`.koreatech.business.feature.store.dashboard

import `in`.koreatech.business.domain.model.MenuCategory
import `in`.koreatech.business.domain.model.StoreDetail
import `in`.koreatech.business.domain.model.StoreEvent
import `in`.koreatech.business.domain.model.owner.OwnerStore

data class StoreDashboardState(
    val isLoading: Boolean = false,
    val ownerName: String = "",
    val stores: List<OwnerStore> = emptyList(),
    val activeStore: OwnerStore? = null,
    val storeDetail: StoreDetail? = null,
    val menus: List<MenuCategory> = emptyList(),
    val events: List<StoreEvent> = emptyList(),
    val isEventEditMode: Boolean = false,
    val selectedEventIds: Set<Int> = emptySet(),
    val expandedEventIds: Set<Int> = emptySet(),
    val errorMessage: String = ""
)
