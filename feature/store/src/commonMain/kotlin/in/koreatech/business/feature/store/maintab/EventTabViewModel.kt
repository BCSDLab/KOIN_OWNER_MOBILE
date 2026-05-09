package `in`.koreatech.business.feature.store.maintab

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import `in`.koreatech.business.domain.model.StoreEvent
import `in`.koreatech.business.domain.usecase.store.DeleteEventUseCase
import `in`.koreatech.business.domain.usecase.store.GetStoreEventsUseCase
import `in`.koreatech.business.domain.usecase.store.ObserveActiveStoreIdUseCase
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

class EventTabViewModel(
    private val getStoreEventsUseCase: GetStoreEventsUseCase,
    private val deleteEventUseCase: DeleteEventUseCase,
    private val observeActiveStoreIdUseCase: ObserveActiveStoreIdUseCase
) : ViewModel(),
    ContainerHost<EventTabState, Nothing> {
    override val container = container<EventTabState, Nothing>(
        initialState = EventTabState(),
        onCreate = {
            observeActiveStoreIdUseCase()
                .distinctUntilChanged()
                .onEach { id -> if (!id.isNullOrBlank()) load(id) }
                .launchIn(viewModelScope)
        }
    )

    fun load(storeId: String) {
        intent {
            reduce { state.copy(storeId = storeId, isLoading = true, errorMessage = "") }
            getStoreEventsUseCase(storeId)
                .onSuccess { events -> applyEvents(events) }
                .onFailure { showError(it.message.orEmpty()) }
        }
    }

    private fun applyEvents(events: List<StoreEvent>) = intent {
        reduce { state.copy(isLoading = false, events = events) }
    }

    private fun showError(message: String) = intent {
        reduce { state.copy(isLoading = false, errorMessage = message) }
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
            reduce { state.copy(isEditMode = !state.isEditMode, selectedEventIds = emptySet()) }
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
            reduce { state.copy(selectedEventIds = if (state.selectedEventIds == allIds) emptySet() else allIds) }
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
            for (id in ids) {
                val deleteResult = deleteEventUseCase(storeId, id.toString())
                if (deleteResult.isFailure) {
                    showError(deleteResult.exceptionOrNull()?.message.orEmpty())
                    return@intent
                }
            }
            getStoreEventsUseCase(storeId)
                .onSuccess { events -> applyEventsAfterDelete(events) }
                .onFailure { showError(it.message.orEmpty()) }
        }
    }

    private fun applyEventsAfterDelete(events: List<StoreEvent>) = intent {
        reduce { state.copy(isLoading = false, events = events, selectedEventIds = emptySet(), isEditMode = false) }
    }

    fun clearError() {
        intent(registerIdling = false) { reduce { state.copy(errorMessage = "") } }
    }
}
