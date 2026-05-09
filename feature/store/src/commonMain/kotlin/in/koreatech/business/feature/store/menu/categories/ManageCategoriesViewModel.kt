package `in`.koreatech.business.feature.store.menu.categories

import androidx.lifecycle.ViewModel
import `in`.koreatech.business.domain.model.MenuCategory
import `in`.koreatech.business.domain.usecase.store.CreateMenuCategoryUseCase
import `in`.koreatech.business.domain.usecase.store.DeleteMenuCategoryUseCase
import `in`.koreatech.business.domain.usecase.store.GetMenuCategoriesUseCase
import `in`.koreatech.business.domain.usecase.store.GetStoreMenusUseCase
import `in`.koreatech.business.domain.usecase.store.RenameMenuCategoryUseCase
import `in`.koreatech.business.domain.util.runCatchingCancellable
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

class ManageCategoriesViewModel(
    private val getMenuCategoriesUseCase: GetMenuCategoriesUseCase,
    private val getStoreMenusUseCase: GetStoreMenusUseCase,
    private val createMenuCategoryUseCase: CreateMenuCategoryUseCase,
    private val renameMenuCategoryUseCase: RenameMenuCategoryUseCase,
    private val deleteMenuCategoryUseCase: DeleteMenuCategoryUseCase
) : ViewModel(),
    ContainerHost<ManageCategoriesState, ManageCategoriesSideEffect> {
    override val container = container<ManageCategoriesState, ManageCategoriesSideEffect>(ManageCategoriesState())

    fun load(storeId: String) = intent {
        reduce { state.copy(storeId = storeId, isLoading = true) }
        loadCategoriesWithMenus(storeId)
            .onSuccess { applyCategories(it) }
            .onFailure { showError(it.message.orEmpty()) }
    }

    fun addCategory(name: String) = intent {
        val storeId = state.storeId ?: return@intent
        if (name.isBlank()) return@intent
        reduce { state.copy(isLoading = true) }
        createMenuCategoryUseCase(storeId, name.trim())
            .onSuccess { reloadAfterMutation(storeId) }
            .onFailure { showError(it.message.orEmpty()) }
    }

    fun renameCategory(categoryId: Int, name: String) = intent {
        val storeId = state.storeId ?: return@intent
        if (name.isBlank()) return@intent
        reduce { state.copy(isLoading = true) }
        renameMenuCategoryUseCase(categoryId, name.trim())
            .onSuccess { reloadAfterMutation(storeId) }
            .onFailure { showError(it.message.orEmpty()) }
    }

    fun deleteCategory(categoryId: Int) = intent {
        val storeId = state.storeId ?: return@intent
        val cat = state.categories.find { it.id == categoryId } ?: return@intent
        reduce { state.copy(isLoading = true) }
        deleteMenuCategoryUseCase(categoryId)
            .onSuccess { reloadAfterMutation(storeId) }
            .onFailure { showBlockDelete(cat) }
    }

    private fun reloadAfterMutation(storeId: String) = intent {
        loadCategoriesWithMenus(storeId)
            .onSuccess { applyCategories(it) }
            .onFailure { showError(it.message.orEmpty()) }
    }

    private fun applyCategories(categories: List<MenuCategory>) = intent {
        reduce { state.copy(isLoading = false, categories = categories) }
    }

    private fun showError(message: String) = intent {
        reduce { state.copy(isLoading = false, errorMessage = message) }
    }

    private fun showBlockDelete(cat: MenuCategory) = intent {
        reduce { state.copy(isLoading = false, blockDeleteCategory = cat, errorMessage = "") }
    }

    private suspend fun loadCategoriesWithMenus(storeId: String): Result<List<MenuCategory>> = runCatchingCancellable {
        val allCategories = getMenuCategoriesUseCase(storeId).getOrThrow()
        val categoriesWithMenus = getStoreMenusUseCase(storeId).getOrDefault(emptyList())
        val menusByCategoryId = categoriesWithMenus.associateBy { it.id }
        allCategories.map { cat -> cat.copy(menus = menusByCategoryId[cat.id]?.menus.orEmpty()) }
    }

    fun clearBlockDelete() = intent(registerIdling = false) {
        reduce { state.copy(blockDeleteCategory = null) }
    }

    fun clearError() = intent(registerIdling = false) {
        reduce { state.copy(errorMessage = "") }
    }
}

sealed class ManageCategoriesSideEffect
