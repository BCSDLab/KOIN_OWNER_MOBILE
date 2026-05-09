package `in`.koreatech.business.feature.store.maintab

import `in`.koreatech.business.domain.model.StoreEvent

enum class EventFilter { All, Live, Planned, Ended }

data class EventTabState(
    val isLoading: Boolean = false,
    val storeId: String? = null,
    val events: List<StoreEvent> = emptyList(),
    val filter: EventFilter = EventFilter.All,
    val isEditMode: Boolean = false,
    val selectedEventIds: Set<Int> = emptySet(),
    val expandedEventIds: Set<Int> = emptySet(),
    val errorMessage: String = ""
)
