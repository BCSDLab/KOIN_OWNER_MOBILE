package `in`.koreatech.business.feature.store.maintab

import `in`.koreatech.business.domain.error.DomainError
import `in`.koreatech.business.domain.model.StoreEvent
import `in`.koreatech.business.domain.usecase.store.DeleteEventUseCase
import `in`.koreatech.business.domain.usecase.store.GetStoreEventsUseCase
import `in`.koreatech.business.domain.usecase.store.ObserveActiveStoreIdUseCase
import `in`.koreatech.business.feature.store.fakes.FakeActiveStoreRepository
import `in`.koreatech.business.feature.store.fakes.FakeStoreRepository
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.orbitmvi.orbit.test.test

@OptIn(ExperimentalCoroutinesApi::class)
class EventTabViewModelTest {
    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun newViewModel(
        events: List<StoreEvent> = emptyList(),
        getEventsError: Throwable? = null,
        deleteEventError: Throwable? = null
    ): Pair<EventTabViewModel, FakeStoreRepository> {
        val store = FakeStoreRepository(
            storeEvents = events,
            getStoreEventsError = getEventsError,
            deleteEventError = deleteEventError
        )
        val active = FakeActiveStoreRepository()
        val vm = EventTabViewModel(
            getStoreEventsUseCase = GetStoreEventsUseCase(store),
            deleteEventUseCase = DeleteEventUseCase(store),
            observeActiveStoreIdUseCase = ObserveActiveStoreIdUseCase(active)
        )
        return vm to store
    }

    @Test
    fun loadEmitsEventsOnSuccess() = runTest {
        val sample = listOf(
            StoreEvent(id = 1, shopId = 100, title = "할인", content = "20%", thumbnailUrls = emptyList(), startDate = "2026-01-01", endDate = "2026-01-31")
        )
        val (vm, _) = newViewModel(events = sample)
        vm.test(this, EventTabUiState()) {
            containerHost.load("storeA")
            expectState { copy(storeId = "storeA", isLoading = true, errorMessage = "") }
            expectState { copy(isLoading = false, events = sample) }
        }
    }

    @Test
    fun loadFailureSurfacesErrorMessage() = runTest {
        val (vm, _) = newViewModel(getEventsError = DomainError.Network("이벤트 로드 실패"))
        vm.test(this, EventTabUiState()) {
            containerHost.load("storeA")
            expectState { copy(storeId = "storeA", isLoading = true, errorMessage = "") }
            expectState { copy(isLoading = false, errorMessage = "이벤트 로드 실패") }
        }
    }

    @Test
    fun setFilterUpdatesState() = runTest {
        val (vm, _) = newViewModel()
        vm.test(this, EventTabUiState()) {
            containerHost.setFilter(EventFilter.Live)
            expectState { copy(filter = EventFilter.Live) }
        }
    }

    @Test
    fun toggleEditModeFlipsAndClearsSelection() = runTest {
        val (vm, _) = newViewModel()
        vm.test(this, EventTabUiState(selectedEventIds = setOf(1, 2))) {
            containerHost.toggleEditMode()
            expectState { copy(isEditMode = true, selectedEventIds = emptySet()) }
        }
    }

    @Test
    fun toggleSelectionAddsThenRemoves() = runTest {
        val (vm, _) = newViewModel()
        vm.test(this, EventTabUiState()) {
            containerHost.toggleSelection(1)
            expectState { copy(selectedEventIds = setOf(1)) }
            containerHost.toggleSelection(1)
            expectState { copy(selectedEventIds = emptySet()) }
        }
    }

    @Test
    fun deleteSelectedRemovesAndReloads() = runTest {
        val sample = listOf(
            StoreEvent(id = 1, shopId = 100, title = "이벤트1", content = "", thumbnailUrls = emptyList(), startDate = "", endDate = "")
        )
        val (vm, repo) = newViewModel(events = sample)
        vm.test(
            this,
            EventTabUiState(storeId = "storeA", selectedEventIds = setOf(1), events = sample)
        ) {
            containerHost.deleteSelected()
            expectState { copy(isLoading = true, errorMessage = "") }
            expectState {
                copy(
                    isLoading = false,
                    events = sample,
                    selectedEventIds = emptySet(),
                    isEditMode = false
                )
            }
            assertTrue(repo.deleteEventCalls.contains("storeA" to "1"))
        }
    }
}
