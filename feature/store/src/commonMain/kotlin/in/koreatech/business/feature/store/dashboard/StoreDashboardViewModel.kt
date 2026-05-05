package `in`.koreatech.business.feature.store.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import `in`.koreatech.business.domain.model.MenuCategory
import `in`.koreatech.business.domain.model.StoreDetail
import `in`.koreatech.business.domain.model.StoreEvent
import `in`.koreatech.business.domain.model.owner.OwnerStore
import `in`.koreatech.business.domain.usecase.owner.GetOwnerProfileUseCase
import `in`.koreatech.business.domain.usecase.owner.GetShopListUseCase
import `in`.koreatech.business.domain.usecase.store.DeleteEventUseCase
import `in`.koreatech.business.domain.usecase.store.GetStoreDetailUseCase
import `in`.koreatech.business.domain.usecase.store.GetStoreEventsUseCase
import `in`.koreatech.business.domain.usecase.store.GetStoreMenusUseCase
import `in`.koreatech.business.domain.usecase.store.ObserveActiveStoreIdUseCase
import `in`.koreatech.business.domain.usecase.store.SetActiveStoreIdUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

class StoreDashboardViewModel(
    private val getShopListUseCase: GetShopListUseCase,
    private val getOwnerProfileUseCase: GetOwnerProfileUseCase,
    private val getStoreDetailUseCase: GetStoreDetailUseCase,
    private val getStoreMenusUseCase: GetStoreMenusUseCase,
    private val getStoreEventsUseCase: GetStoreEventsUseCase,
    private val deleteEventUseCase: DeleteEventUseCase,
    private val setActiveStoreIdUseCase: SetActiveStoreIdUseCase,
    private val observeActiveStoreIdUseCase: ObserveActiveStoreIdUseCase,
) : ViewModel(), ContainerHost<StoreDashboardUiState, StoreDashboardSideEffect> {
    override val container = container<StoreDashboardUiState, StoreDashboardSideEffect>(StoreDashboardUiState())

    init {
        observeActiveStoreIdUseCase()
            .distinctUntilChanged()
            .onEach { id -> load(id) }
            .launchIn(viewModelScope)
    }

    fun load(initialStoreId: String?) {
        intent {
            reduce { state.copy(isLoading = true, errorMessage = "") }
            try {
                val (stores, profile) = coroutineScope {
                    val storesDeferred = async { getShopListUseCase() }
                    val profileDeferred = async { runCatching { getOwnerProfileUseCase() }.getOrNull() }
                    storesDeferred.await() to profileDeferred.await()
                }
                if (profile != null) reduce { state.copy(ownerName = profile.name) }
                if (stores.isEmpty()) {
                    setActiveStoreIdUseCase("")
                    reduce { state.copy(isLoading = false, stores = emptyList()) }
                    return@intent
                }
                val activeStore = initialStoreId?.let { id ->
                    stores.firstOrNull { it.uid.toString() == id }
                } ?: stores.first()
                if (activeStore.uid.toString() != initialStoreId) {
                    setActiveStoreIdUseCase(activeStore.uid.toString())
                }
                loadStoreData(activeStore, stores)
            } catch (exception: Exception) {
                val message = exception.message.orEmpty()
                reduce { state.copy(isLoading = false, errorMessage = message) }
                postSideEffect(StoreDashboardSideEffect(message))
            }
        }
    }

    fun selectStore(store: OwnerStore) {
        intent {
            reduce { state.copy(isLoading = true, errorMessage = "") }
            setActiveStoreIdUseCase(store.uid.toString())
            try {
                loadStoreData(store, state.stores)
            } catch (exception: Exception) {
                val message = exception.message.orEmpty()
                reduce { state.copy(isLoading = false, errorMessage = message) }
                postSideEffect(StoreDashboardSideEffect(message))
            }
        }
    }

    fun refresh() { load(container.stateFlow.value.activeStore?.uid?.toString()) }

    fun toggleEventEditMode() {
        intent(registerIdling = false) {
            reduce { state.copy(isEventEditMode = !state.isEventEditMode, selectedEventIds = emptySet()) }
        }
    }

    fun toggleEventSelection(eventId: Int) {
        intent(registerIdling = false) {
            val updated = state.selectedEventIds.toMutableSet()
            if (eventId in updated) updated.remove(eventId) else updated.add(eventId)
            reduce { state.copy(selectedEventIds = updated) }
        }
    }

    fun toggleAllEventSelection() {
        intent(registerIdling = false) {
            val allIds = state.events.map { it.id }.toSet()
            reduce { state.copy(selectedEventIds = if (state.selectedEventIds == allIds) emptySet() else allIds) }
        }
    }

    fun toggleEventExpanded(eventId: Int) {
        intent(registerIdling = false) {
            val updated = state.expandedEventIds.toMutableSet()
            if (eventId in updated) updated.remove(eventId) else updated.add(eventId)
            reduce { state.copy(expandedEventIds = updated) }
        }
    }

    fun deleteSelectedEvents() {
        intent {
            val activeStore = state.activeStore ?: return@intent
            val storeId = activeStore.uid.toString()
            val ids = state.selectedEventIds.toList()
            if (ids.isEmpty()) return@intent
            reduce { state.copy(isLoading = true, errorMessage = "") }
            try {
                ids.forEach { deleteEventUseCase(storeId, it.toString()) }
                loadStoreData(activeStore, state.stores)
                reduce { state.copy(selectedEventIds = emptySet(), isEventEditMode = false) }
            } catch (exception: Exception) {
                val message = exception.message.orEmpty()
                reduce { state.copy(isLoading = false, errorMessage = message) }
                postSideEffect(StoreDashboardSideEffect(message))
            }
        }
    }

    fun deleteSingleEvent(eventId: Int) {
        intent {
            val activeStore = state.activeStore ?: return@intent
            val storeId = activeStore.uid.toString()
            reduce { state.copy(isLoading = true, errorMessage = "") }
            try {
                deleteEventUseCase(storeId, eventId.toString())
                loadStoreData(activeStore, state.stores)
            } catch (exception: Exception) {
                val message = exception.message.orEmpty()
                reduce { state.copy(isLoading = false, errorMessage = message) }
                postSideEffect(StoreDashboardSideEffect(message))
            }
        }
    }

    fun clearError() {
        intent(registerIdling = false) { reduce { state.copy(errorMessage = "") } }
    }

    private suspend fun org.orbitmvi.orbit.syntax.Syntax<StoreDashboardUiState, StoreDashboardSideEffect>.loadStoreData(
        activeStore: OwnerStore,
        stores: List<OwnerStore>
    ) {
        val storeId = activeStore.uid.toString()
        val (detail, menus, events) = coroutineScope {
            val detailDeferred = async { getStoreDetailUseCase(storeId) }
            val menusDeferred = async { getStoreMenusUseCase(storeId) }
            val eventsDeferred = async { getStoreEventsUseCase(storeId) }
            Triple(detailDeferred.await(), menusDeferred.await(), eventsDeferred.await())
        }
        reduce { state.copy(isLoading = false, stores = stores, activeStore = activeStore, storeDetail = detail, menus = menus, events = events) }
    }
}

data class StoreDashboardUiState(
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
