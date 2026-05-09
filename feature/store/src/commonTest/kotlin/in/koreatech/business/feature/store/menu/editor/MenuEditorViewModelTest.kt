package `in`.koreatech.business.feature.store.menu.editor

import `in`.koreatech.business.domain.error.DomainError
import `in`.koreatech.business.domain.model.MenuCategory
import `in`.koreatech.business.domain.usecase.owner.UploadFileUseCase
import `in`.koreatech.business.domain.usecase.store.DeleteMenuUseCase
import `in`.koreatech.business.domain.usecase.store.GetMenuCategoriesUseCase
import `in`.koreatech.business.domain.usecase.store.GetStoreMenusUseCase
import `in`.koreatech.business.domain.usecase.store.RegisterMenuUseCase
import `in`.koreatech.business.domain.usecase.store.UpdateMenuUseCase
import `in`.koreatech.business.feature.store.fakes.FakeOwnerRepository
import `in`.koreatech.business.feature.store.fakes.FakeStoreRepository
import koreatech.business.designsystem.resources.Res
import koreatech.business.designsystem.resources.category_select_required
import koreatech.business.designsystem.resources.menu_editor_error_categories_empty
import koreatech.business.designsystem.resources.menu_editor_error_name_required
import koreatech.business.designsystem.resources.menu_editor_error_option_required
import koreatech.business.designsystem.resources.menu_editor_error_price_required
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
class MenuEditorViewModelTest {
    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun newViewModel(
        categories: List<MenuCategory> = listOf(MenuCategory(id = 1, name = "한식", menus = emptyList())),
        registerError: Throwable? = null,
        updateError: Throwable? = null,
        deleteError: Throwable? = null
    ): Pair<MenuEditorViewModel, FakeStoreRepository> {
        val repo = FakeStoreRepository(
            menuCategories = categories,
            registerMenuError = registerError,
            updateMenuError = updateError,
            deleteMenuError = deleteError
        )
        val vm = MenuEditorViewModel(
            getMenuCategoriesUseCase = GetMenuCategoriesUseCase(repo),
            getStoreMenusUseCase = GetStoreMenusUseCase(repo),
            registerMenuUseCase = RegisterMenuUseCase(repo),
            updateMenuUseCase = UpdateMenuUseCase(repo),
            deleteMenuUseCase = DeleteMenuUseCase(repo),
            uploadFileUseCase = UploadFileUseCase(FakeOwnerRepository())
        )
        return vm to repo
    }

    @Test
    fun submitWithBlankNameEmitsNameRequiredRes() = runTest {
        val (vm, _) = newViewModel()
        vm.test(this, MenuEditorState(storeId = "storeA", name = "")) {
            containerHost.submit()
            expectState {
                copy(errorMessageRes = Res.string.menu_editor_error_name_required, errorMessage = "")
            }
        }
    }

    @Test
    fun submitWithSinglePriceEmptyEmitsPriceRequiredRes() = runTest {
        val (vm, _) = newViewModel()
        vm.test(
            this,
            MenuEditorState(storeId = "storeA", name = "메뉴", singlePrice = "")
        ) {
            containerHost.submit()
            expectState {
                copy(errorMessageRes = Res.string.menu_editor_error_price_required, errorMessage = "")
            }
        }
    }

    @Test
    fun submitWithIncompleteOptionEmitsOptionRequiredRes() = runTest {
        val (vm, _) = newViewModel()
        vm.test(
            this,
            MenuEditorState(
                storeId = "storeA",
                name = "메뉴",
                optionPrices = listOf(MenuOptionPriceDraft(option = "", price = "5000"))
            )
        ) {
            containerHost.submit()
            expectState {
                copy(errorMessageRes = Res.string.menu_editor_error_option_required, errorMessage = "")
            }
        }
    }

    @Test
    fun submitWithEmptyMenuCategoriesEmitsCategoriesEmptyRes() = runTest {
        val (vm, _) = newViewModel(categories = emptyList())
        vm.test(
            this,
            MenuEditorState(
                storeId = "storeA",
                name = "메뉴",
                singlePrice = "5000",
                menuCategories = emptyList()
            )
        ) {
            containerHost.submit()
            expectState {
                copy(errorMessageRes = Res.string.menu_editor_error_categories_empty, errorMessage = "")
            }
        }
    }

    @Test
    fun submitWithoutSelectedCategoryEmitsSelectRequiredRes() = runTest {
        val cats = listOf(MenuCategory(id = 1, name = "한식", menus = emptyList()))
        val (vm, _) = newViewModel(categories = cats)
        vm.test(
            this,
            MenuEditorState(
                storeId = "storeA",
                name = "메뉴",
                singlePrice = "5000",
                menuCategories = cats,
                selectedCategoryIds = emptyList()
            )
        ) {
            containerHost.submit()
            expectState {
                copy(errorMessageRes = Res.string.category_select_required, errorMessage = "")
            }
        }
    }

    @Test
    fun submitNewMenuRegistersAndPostsNavigateBack() = runTest {
        val cats = listOf(MenuCategory(id = 1, name = "한식", menus = emptyList()))
        val (vm, repo) = newViewModel(categories = cats)
        vm.test(
            this,
            MenuEditorState(
                storeId = "storeA",
                name = "메뉴",
                singlePrice = "5000",
                menuCategories = cats,
                selectedCategoryIds = listOf(1)
            )
        ) {
            containerHost.submit()
            expectState { copy(isLoading = true, errorMessageRes = null, errorMessage = "") }
            expectState { copy(isLoading = false) }
            expectSideEffect(MenuEditorSideEffect.NavigateBack)
            assertTrue(repo.registerMenuCalls.contains("storeA"))
        }
    }

    @Test
    fun submitEditModeUpdatesMenu() = runTest {
        val cats = listOf(MenuCategory(id = 1, name = "한식", menus = emptyList()))
        val (vm, repo) = newViewModel(categories = cats)
        vm.test(
            this,
            MenuEditorState(
                storeId = "storeA",
                menuId = "M1",
                isEditMode = true,
                name = "메뉴",
                singlePrice = "5000",
                menuCategories = cats,
                selectedCategoryIds = listOf(1)
            )
        ) {
            containerHost.submit()
            expectState { copy(isLoading = true, errorMessageRes = null, errorMessage = "") }
            expectState { copy(isLoading = false) }
            expectSideEffect(MenuEditorSideEffect.NavigateBack)
            assertTrue(repo.updateMenuCalls.contains("M1"))
        }
    }

    @Test
    fun submitFailureSurfacesServerErrorMessage() = runTest {
        val cats = listOf(MenuCategory(id = 1, name = "한식", menus = emptyList()))
        val (vm, _) = newViewModel(categories = cats, registerError = DomainError.Network("등록 실패"))
        vm.test(
            this,
            MenuEditorState(
                storeId = "storeA",
                name = "메뉴",
                singlePrice = "5000",
                menuCategories = cats,
                selectedCategoryIds = listOf(1)
            )
        ) {
            containerHost.submit()
            expectState { copy(isLoading = true, errorMessageRes = null, errorMessage = "") }
            expectState { copy(isLoading = false, errorMessage = "등록 실패", errorMessageRes = null) }
        }
    }

    @Test
    fun deleteMenuPostsNavigateBack() = runTest {
        val (vm, repo) = newViewModel()
        vm.test(this, MenuEditorState(storeId = "storeA", menuId = "M1")) {
            containerHost.deleteMenu()
            expectState { copy(isLoading = true, errorMessage = "") }
            expectState { copy(isLoading = false) }
            expectSideEffect(MenuEditorSideEffect.NavigateBack)
            assertTrue(repo.deleteMenuCalls.contains("storeA" to "M1"))
        }
    }
}
