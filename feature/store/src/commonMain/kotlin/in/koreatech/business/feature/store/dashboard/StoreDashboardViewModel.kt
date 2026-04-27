package `in`.koreatech.business.feature.store.dashboard

import androidx.lifecycle.ViewModel
import `in`.koreatech.business.data.di.EncryptedDataStore
import `in`.koreatech.business.domain.model.MenuCategory
import `in`.koreatech.business.domain.model.StoreDetail
import `in`.koreatech.business.domain.model.StoreEvent
import `in`.koreatech.business.domain.model.owner.OwnerStore
import `in`.koreatech.business.domain.repository.OwnerRepository
import `in`.koreatech.business.domain.repository.StoreRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

class StoreDashboardViewModel(
    private val ownerRepository: OwnerRepository,
    private val storeRepository: StoreRepository,
    private val encryptedDataStore: EncryptedDataStore
) : ViewModel(),
    ContainerHost<StoreDashboardUiState, StoreDashboardSideEffect> {
    override val container = container<StoreDashboardUiState, StoreDashboardSideEffect>(StoreDashboardUiState())

    fun load(initialStoreId: String?) {
        intent {
            reduce { state.copy(isLoading = true, errorMessage = "") }
            try {
                val (stores, profile) = coroutineScope {
                    val storesDeferred = async { ownerRepository.getShopList() }
                    val profileDeferred = async { runCatching { ownerRepository.getOwnerProfile() }.getOrNull() }
                    storesDeferred.await() to profileDeferred.await()
                }
                if (profile != null) {
                    reduce { state.copy(ownerName = profile.name) }
                }
                if (stores.isEmpty()) {
                    reduce { state.copy(isLoading = false, stores = emptyList()) }
                    return@intent
                }

                val activeStore = initialStoreId?.let { id ->
                    stores.firstOrNull { it.uid.toString() == id }
                } ?: stores.first()

                encryptedDataStore.createData(LAST_ACTIVE_STORE_ID_KEY, activeStore.uid.toString())

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
            encryptedDataStore.createData(LAST_ACTIVE_STORE_ID_KEY, store.uid.toString())
            try {
                loadStoreData(store, state.stores)
            } catch (exception: Exception) {
                val message = exception.message.orEmpty()
                reduce { state.copy(isLoading = false, errorMessage = message) }
                postSideEffect(StoreDashboardSideEffect(message))
            }
        }
    }

    fun refresh() {
        load(container.stateFlow.value.activeStore?.uid?.toString())
    }

    fun toggleEventEditMode() {
        intent(registerIdling = false) {
            reduce {
                state.copy(
                    isEventEditMode = !state.isEventEditMode,
                    selectedEventIds = emptySet()
                )
            }
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
            val isAllSelected = state.selectedEventIds == allIds
            reduce { state.copy(selectedEventIds = if (isAllSelected) emptySet() else allIds) }
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
                ids.forEach { eventId ->
                    storeRepository.deleteEvent(storeId, eventId.toString())
                }
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
                storeRepository.deleteEvent(storeId, eventId.toString())
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
            val detailDeferred = async { storeRepository.getStoreDetail(storeId) }
            val menusDeferred = async { storeRepository.getStoreMenus(storeId) }
            val eventsDeferred = async { storeRepository.getStoreEvents(storeId) }
            Triple(detailDeferred.await(), menusDeferred.await(), eventsDeferred.await())
        }
        reduce {
            state.copy(
                isLoading = false,
                stores = stores,
                activeStore = activeStore,
                storeDetail = detail,
                menus = menus,
                events = events
            )
        }
    }

    companion object {
        private const val LAST_ACTIVE_STORE_ID_KEY = "lastActiveStoreId"
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
