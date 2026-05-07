package `in`.koreatech.business.feature.store.menu.categories

import androidx.lifecycle.ViewModel
import `in`.koreatech.business.domain.model.MenuCategory
import `in`.koreatech.business.domain.usecase.store.CreateMenuCategoryUseCase
import `in`.koreatech.business.domain.usecase.store.DeleteMenuCategoryUseCase
import `in`.koreatech.business.domain.usecase.store.GetMenuCategoriesUseCase
import `in`.koreatech.business.domain.usecase.store.GetStoreMenusUseCase
import `in`.koreatech.business.domain.usecase.store.RenameMenuCategoryUseCase
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

class ManageCategoriesViewModel(
    private val getMenuCategoriesUseCase: GetMenuCategoriesUseCase,
    private val getStoreMenusUseCase: GetStoreMenusUseCase,
    private val createMenuCategoryUseCase: CreateMenuCategoryUseCase,
    private val renameMenuCategoryUseCase: RenameMenuCategoryUseCase,
    private val deleteMenuCategoryUseCase: DeleteMenuCategoryUseCase
) : ViewModel(),
    ContainerHost<ManageCategoriesUiState, ManageCategoriesSideEffect> {
    override val container = container<ManageCategoriesUiState, ManageCategoriesSideEffect>(ManageCategoriesUiState())

    fun load(storeId: String) = intent {
        reduce { state.copy(storeId = storeId, isLoading = true) }
        try {
            val updated = loadCategoriesWithMenus(storeId)
            reduce { state.copy(isLoading = false, categories = updated) }
        } catch (e: Exception) {
            reduce { state.copy(isLoading = false, errorMessage = e.message.orEmpty()) }
        }
    }

    fun addCategory(name: String) = intent {
        val storeId = state.storeId ?: return@intent
        if (name.isBlank()) return@intent
        reduce { state.copy(isLoading = true) }
        try {
            createMenuCategoryUseCase(storeId, name.trim())
            val updated = loadCategoriesWithMenus(storeId)
            reduce { state.copy(isLoading = false, categories = updated) }
        } catch (e: Exception) {
            reduce { state.copy(isLoading = false, errorMessage = e.message.orEmpty()) }
        }
    }

    fun renameCategory(categoryId: Int, name: String) = intent {
        val storeId = state.storeId ?: return@intent
        if (name.isBlank()) return@intent
        reduce { state.copy(isLoading = true) }
        try {
            renameMenuCategoryUseCase(categoryId, name.trim())
            val updated = loadCategoriesWithMenus(storeId)
            reduce { state.copy(isLoading = false, categories = updated) }
        } catch (e: Exception) {
            reduce { state.copy(isLoading = false, errorMessage = e.message.orEmpty()) }
        }
    }

    fun deleteCategory(categoryId: Int) = intent {
        val storeId = state.storeId ?: return@intent
        val cat = state.categories.find { it.id == categoryId } ?: return@intent
        reduce { state.copy(isLoading = true) }
        try {
            deleteMenuCategoryUseCase(categoryId)
            val updated = loadCategoriesWithMenus(storeId)
            reduce { state.copy(isLoading = false, categories = updated) }
        } catch (e: Exception) {
            reduce { state.copy(isLoading = false, blockDeleteCategory = cat, errorMessage = "") }
        }
    }

    private suspend fun loadCategoriesWithMenus(storeId: String): List<MenuCategory> {
        val allCategories = getMenuCategoriesUseCase(storeId)
        val categoriesWithMenus = runCatching { getStoreMenusUseCase(storeId) }.getOrDefault(emptyList())
        val menusByCategoryId = categoriesWithMenus.associateBy { it.id }
        return allCategories.map { cat -> cat.copy(menus = menusByCategoryId[cat.id]?.menus.orEmpty()) }
    }

    fun clearBlockDelete() = intent(registerIdling = false) {
        reduce { state.copy(blockDeleteCategory = null) }
    }

    fun clearError() = intent(registerIdling = false) {
        reduce { state.copy(errorMessage = "") }
    }
}

data class ManageCategoriesUiState(
    val isLoading: Boolean = false,
    val storeId: String? = null,
    val categories: List<MenuCategory> = emptyList(),
    val blockDeleteCategory: MenuCategory? = null,
    val errorMessage: String = ""
)

sealed class ManageCategoriesSideEffect
