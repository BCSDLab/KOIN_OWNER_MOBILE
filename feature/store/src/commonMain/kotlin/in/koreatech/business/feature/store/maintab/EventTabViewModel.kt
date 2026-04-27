package `in`.koreatech.business.feature.store.maintab

import androidx.lifecycle.ViewModel
import `in`.koreatech.business.domain.model.StoreEvent
import `in`.koreatech.business.domain.repository.StoreRepository
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

enum class EventFilter {
    All, Live, Planned, Ended
}

data class EventTabUiState(
    val isLoading: Boolean = false,
    val storeId: String? = null,
    val events: List<StoreEvent> = emptyList(),
    val filter: EventFilter = EventFilter.All,
    val isEditMode: Boolean = false,
    val selectedEventIds: Set<Int> = emptySet(),
    val expandedEventIds: Set<Int> = emptySet(),
    val errorMessage: String = ""
)

class EventTabViewModel(
    private val storeRepository: StoreRepository
) : ViewModel(),
    ContainerHost<EventTabUiState, Nothing> {
    override val container = container<EventTabUiState, Nothing>(EventTabUiState())

    fun load(storeId: String) {
        intent {
            reduce { state.copy(storeId = storeId, isLoading = true, errorMessage = "") }
            try {
                val events = storeRepository.getStoreEvents(storeId)
                reduce { state.copy(isLoading = false, events = events) }
            } catch (e: Exception) {
                reduce { state.copy(isLoading = false, errorMessage = e.message.orEmpty()) }
            }
        }
    }

    fun refresh() {
        val storeId = container.stateFlow.value.storeId ?: return
        load(storeId)
    }

    fun setFilter(filter: EventFilter) {
        intent(registerIdling = false) { reduce { state.copy(filter = filter) } }
    }

    fun toggleEditMode() {
        intent(registerIdling = false) {
            reduce {
                state.copy(
                    isEditMode = !state.isEditMode,
                    selectedEventIds = emptySet()
                )
            }
        }
    }

    fun toggleSelection(eventId: Int) {
        intent(registerIdling = false) {
            val updated = state.selectedEventIds.toMutableSet()
            if (eventId in updated) updated.remove(eventId) else updated.add(eventId)
            reduce { state.copy(selectedEventIds = updated) }
        }
    }

    fun toggleAllSelection() {
        intent(registerIdling = false) {
            val allIds = state.events.map { it.id }.toSet()
            val isAllSelected = state.selectedEventIds == allIds
            reduce { state.copy(selectedEventIds = if (isAllSelected) emptySet() else allIds) }
        }
    }

    fun toggleExpanded(eventId: Int) {
        intent(registerIdling = false) {
            val updated = state.expandedEventIds.toMutableSet()
            if (eventId in updated) updated.remove(eventId) else updated.add(eventId)
            reduce { state.copy(expandedEventIds = updated) }
        }
    }

    fun deleteSelected() {
        intent {
            val storeId = state.storeId ?: return@intent
            val ids = state.selectedEventIds.toList()
            if (ids.isEmpty()) return@intent
            reduce { state.copy(isLoading = true, errorMessage = "") }
            try {
                ids.forEach { id ->
                    storeRepository.deleteEvent(storeId, id.toString())
                }
                val events = storeRepository.getStoreEvents(storeId)
                reduce {
                    state.copy(
                        isLoading = false,
                        events = events,
                        selectedEventIds = emptySet(),
                        isEditMode = false
                    )
                }
            } catch (e: Exception) {
                reduce { state.copy(isLoading = false, errorMessage = e.message.orEmpty()) }
            }
        }
    }

    fun clearError() {
        intent(registerIdling = false) { reduce { state.copy(errorMessage = "") } }
    }
}
