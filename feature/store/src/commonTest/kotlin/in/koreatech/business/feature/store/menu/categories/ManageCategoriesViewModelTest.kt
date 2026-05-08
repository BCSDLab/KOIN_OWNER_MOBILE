package `in`.koreatech.business.feature.store.menu.categories

import `in`.koreatech.business.domain.error.DomainError
import `in`.koreatech.business.domain.model.MenuCategory
import `in`.koreatech.business.domain.usecase.store.CreateMenuCategoryUseCase
import `in`.koreatech.business.domain.usecase.store.DeleteMenuCategoryUseCase
import `in`.koreatech.business.domain.usecase.store.GetMenuCategoriesUseCase
import `in`.koreatech.business.domain.usecase.store.GetStoreMenusUseCase
import `in`.koreatech.business.domain.usecase.store.RenameMenuCategoryUseCase
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
class ManageCategoriesViewModelTest {
    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun newViewModel(
        categories: List<MenuCategory> = emptyList(),
        getCategoriesError: Throwable? = null,
        createError: Throwable? = null,
        renameError: Throwable? = null,
        deleteError: Throwable? = null
    ): Pair<ManageCategoriesViewModel, FakeStoreRepository> {
        val repo = FakeStoreRepository(
            menuCategories = categories,
            storeMenus = categories,
            getMenuCategoriesError = getCategoriesError,
            createMenuCategoryError = createError,
            renameMenuCategoryError = renameError,
            deleteMenuCategoryError = deleteError
        )
        val vm = ManageCategoriesViewModel(
            getMenuCategoriesUseCase = GetMenuCategoriesUseCase(repo),
            getStoreMenusUseCase = GetStoreMenusUseCase(repo),
            createMenuCategoryUseCase = CreateMenuCategoryUseCase(repo),
            renameMenuCategoryUseCase = RenameMenuCategoryUseCase(repo),
            deleteMenuCategoryUseCase = DeleteMenuCategoryUseCase(repo)
        )
        return vm to repo
    }

    @Test
    fun loadEmitsCategoriesOnSuccess() = runTest {
        val sample = listOf(MenuCategory(id = 1, name = "한식", menus = emptyList()))
        val (vm, _) = newViewModel(categories = sample)
        vm.test(this, ManageCategoriesUiState()) {
            containerHost.load("storeA")
            expectState { copy(storeId = "storeA", isLoading = true) }
            expectState { copy(isLoading = false, categories = sample) }
        }
    }

    @Test
    fun loadFailureSurfacesErrorMessage() = runTest {
        val (vm, _) = newViewModel(getCategoriesError = DomainError.Network("로드 실패"))
        vm.test(this, ManageCategoriesUiState()) {
            containerHost.load("storeA")
            expectState { copy(storeId = "storeA", isLoading = true) }
            expectState { copy(isLoading = false, errorMessage = "로드 실패") }
        }
    }

    @Test
    fun addCategoryReloadsAfterCreate() = runTest {
        val (vm, repo) = newViewModel(categories = emptyList())
        vm.test(this, ManageCategoriesUiState(storeId = "storeA")) {
            containerHost.addCategory("디저트")
            expectState { copy(isLoading = true) }
            expectState { copy(isLoading = false, categories = emptyList()) }
            assertTrue(repo.createMenuCategoryCalls.contains("storeA" to "디저트"))
        }
    }

    @Test
    fun addCategoryWithBlankNameSilentlyDoesNothing() = runTest {
        val (vm, _) = newViewModel()
        vm.test(this, ManageCategoriesUiState(storeId = "storeA")) {
            containerHost.addCategory("   ")
            // no state change expected
        }
    }

    @Test
    fun renameCategoryReloadsAfterRename() = runTest {
        val (vm, repo) = newViewModel(categories = emptyList())
        vm.test(this, ManageCategoriesUiState(storeId = "storeA")) {
            containerHost.renameCategory(categoryId = 5, name = "새이름")
            expectState { copy(isLoading = true) }
            expectState { copy(isLoading = false, categories = emptyList()) }
            assertTrue(repo.renameMenuCategoryCalls.contains(5 to "새이름"))
        }
    }

    @Test
    fun deleteCategoryFailureSurfacesBlockDeleteCategory() = runTest {
        val cat = MenuCategory(id = 5, name = "한식", menus = emptyList())
        val (vm, _) = newViewModel(deleteError = DomainError.Network(""))
        vm.test(this, ManageCategoriesUiState(storeId = "storeA", categories = listOf(cat))) {
            containerHost.deleteCategory(5)
            expectState { copy(isLoading = true) }
            expectState { copy(isLoading = false, blockDeleteCategory = cat, errorMessage = "") }
        }
    }
}
