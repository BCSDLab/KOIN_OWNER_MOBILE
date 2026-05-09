package `in`.koreatech.business.feature.store.dashboard

import `in`.koreatech.business.domain.error.DomainError
import `in`.koreatech.business.domain.model.owner.OwnerProfile
import `in`.koreatech.business.domain.model.owner.OwnerStore
import `in`.koreatech.business.domain.usecase.owner.GetOwnerProfileUseCase
import `in`.koreatech.business.domain.usecase.owner.GetShopListUseCase
import `in`.koreatech.business.domain.usecase.store.DeleteEventUseCase
import `in`.koreatech.business.domain.usecase.store.GetStoreDetailUseCase
import `in`.koreatech.business.domain.usecase.store.GetStoreEventsUseCase
import `in`.koreatech.business.domain.usecase.store.GetStoreMenusUseCase
import `in`.koreatech.business.domain.usecase.store.ObserveActiveStoreIdUseCase
import `in`.koreatech.business.domain.usecase.store.SetActiveStoreIdUseCase
import `in`.koreatech.business.feature.store.fakes.FakeActiveStoreRepository
import `in`.koreatech.business.feature.store.fakes.FakeOwnerRepository
import `in`.koreatech.business.feature.store.fakes.FakeStoreRepository
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.orbitmvi.orbit.test.test

@OptIn(ExperimentalCoroutinesApi::class)
class StoreDashboardViewModelTest {
    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun newViewModel(
        shops: List<OwnerStore> = emptyList(),
        profile: OwnerProfile = OwnerProfile(name = "사장님", email = "owner@example.com", companyNumber = null),
        shopListError: Throwable? = null
    ): Triple<StoreDashboardViewModel, FakeStoreRepository, FakeOwnerRepository> {
        val store = FakeStoreRepository()
        val owner = FakeOwnerRepository(shops = shops, profile = profile, shopListError = shopListError)
        val active = FakeActiveStoreRepository()
        val vm = StoreDashboardViewModel(
            getShopListUseCase = GetShopListUseCase(owner),
            getOwnerProfileUseCase = GetOwnerProfileUseCase(owner),
            getStoreDetailUseCase = GetStoreDetailUseCase(store),
            getStoreMenusUseCase = GetStoreMenusUseCase(store),
            getStoreEventsUseCase = GetStoreEventsUseCase(store),
            deleteEventUseCase = DeleteEventUseCase(store),
            setActiveStoreIdUseCase = SetActiveStoreIdUseCase(active),
            observeActiveStoreIdUseCase = ObserveActiveStoreIdUseCase(active)
        )
        return Triple(vm, store, owner)
    }

    @Test
    fun loadEmitsEmptyStoresWhenShopListEmpty() = runTest {
        val (vm, _, _) = newViewModel(shops = emptyList())
        vm.test(this, StoreDashboardState()) {
            containerHost.load(initialStoreId = null)
            expectState { copy(isLoading = true, errorMessage = "") }
            expectState { copy(ownerName = "사장님") }
            expectState { copy(isLoading = false, stores = emptyList()) }
        }
    }

    @Test
    fun loadFailureSurfacesErrorAndPostsSideEffect() = runTest {
        val (vm, _, _) = newViewModel(shopListError = DomainError.Network("매장 로드 실패"))
        vm.test(this, StoreDashboardState()) {
            containerHost.load(initialStoreId = null)
            expectState { copy(isLoading = true, errorMessage = "") }
            expectState { copy(isLoading = false, errorMessage = "매장 로드 실패") }
            expectSideEffect(StoreDashboardSideEffect("매장 로드 실패"))
        }
    }

    @Test
    fun toggleEventEditModeFlipsAndClearsSelection() = runTest {
        val (vm, _, _) = newViewModel()
        vm.test(this, StoreDashboardState(selectedEventIds = setOf(1))) {
            containerHost.toggleEventEditMode()
            expectState { copy(isEventEditMode = true, selectedEventIds = emptySet()) }
        }
    }

    @Test
    fun toggleEventSelectionAddsThenRemoves() = runTest {
        val (vm, _, _) = newViewModel()
        vm.test(this, StoreDashboardState()) {
            containerHost.toggleEventSelection(7)
            expectState { copy(selectedEventIds = setOf(7)) }
            containerHost.toggleEventSelection(7)
            expectState { copy(selectedEventIds = emptySet()) }
        }
    }

    @Test
    fun clearErrorResetsErrorMessage() = runTest {
        val (vm, _, _) = newViewModel()
        vm.test(this, StoreDashboardState(errorMessage = "기존 에러")) {
            containerHost.clearError()
            expectState { copy(errorMessage = "") }
        }
    }
}
