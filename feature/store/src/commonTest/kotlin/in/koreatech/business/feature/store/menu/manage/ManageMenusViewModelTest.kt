package `in`.koreatech.business.feature.store.menu.manage

import `in`.koreatech.business.domain.error.DomainError
import `in`.koreatech.business.domain.model.MenuCategory
import `in`.koreatech.business.domain.usecase.store.DeleteMenuUseCase
import `in`.koreatech.business.domain.usecase.store.GetStoreMenusUseCase
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
class ManageMenusViewModelTest {
    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun newViewModel(
        activeStoreId: String? = null,
        menus: List<MenuCategory> = emptyList(),
        getStoreMenusError: Throwable? = null,
        deleteMenuError: Throwable? = null
    ): Triple<ManageMenusViewModel, FakeStoreRepository, FakeActiveStoreRepository> {
        val store = FakeStoreRepository(
            storeMenus = menus,
            getStoreMenusError = getStoreMenusError,
            deleteMenuError = deleteMenuError
        )
        val active = FakeActiveStoreRepository(initial = activeStoreId)
        val vm = ManageMenusViewModel(
            getStoreMenusUseCase = GetStoreMenusUseCase(store),
            deleteMenuUseCase = DeleteMenuUseCase(store),
            observeActiveStoreIdUseCase = ObserveActiveStoreIdUseCase(active)
        )
        return Triple(vm, store, active)
    }

    @Test
    fun loadCallEmitsMenusOnSuccess() = runTest {
        val sample = listOf(MenuCategory(id = 1, name = "한식", menus = emptyList()))
        val (vm, _, _) = newViewModel(menus = sample)
        vm.test(this, ManageMenusUiState()) {
            containerHost.load("storeA")
            expectState {
                copy(storeId = "storeA", isLoading = true, errorMessage = "")
            }
            expectState {
                copy(isLoading = false, categories = sample, deletingMenuId = null)
            }
        }
    }

    @Test
    fun loadFailureSurfacesErrorMessage() = runTest {
        val (vm, _, _) = newViewModel(getStoreMenusError = DomainError.Network("메뉴 로드 실패"))
        vm.test(this, ManageMenusUiState()) {
            containerHost.load("storeA")
            expectState {
                copy(storeId = "storeA", isLoading = true, errorMessage = "")
            }
            expectState { copy(isLoading = false, errorMessage = "메뉴 로드 실패") }
        }
    }

    @Test
    fun deleteMenuRefetchesAndClearsErrorOnSuccess() = runTest {
        val initial = listOf(MenuCategory(id = 1, name = "한식", menus = emptyList()))
        val (vm, repo, _) = newViewModel(menus = initial)
        vm.test(this, ManageMenusUiState(storeId = "storeA", categories = initial)) {
            containerHost.deleteMenu("M1")
            expectState { copy(deletingMenuId = "M1") }
            expectState { copy(categories = initial, deletingMenuId = null, errorMessage = "") }
            assertTrue(repo.deleteMenuCalls.contains("storeA" to "M1"))
        }
    }

    @Test
    fun deleteMenuFailureRetainsCategoriesAndSurfacesError() = runTest {
        val (vm, _, _) = newViewModel(deleteMenuError = DomainError.Network("삭제 실패"))
        vm.test(this, ManageMenusUiState(storeId = "storeA")) {
            containerHost.deleteMenu("M1")
            expectState { copy(deletingMenuId = "M1") }
            expectState { copy(deletingMenuId = null, errorMessage = "삭제 실패") }
        }
    }
}
