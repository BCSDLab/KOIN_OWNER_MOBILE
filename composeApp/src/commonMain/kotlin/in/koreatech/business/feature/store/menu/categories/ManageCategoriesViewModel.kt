package `in`.koreatech.business.feature.store.menu.categories

import androidx.lifecycle.ViewModel
import `in`.koreatech.business.domain.model.MenuCategory
import `in`.koreatech.business.domain.repository.StoreRepository
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

class ManageCategoriesViewModel(
    private val storeRepository: StoreRepository
) : ViewModel(),
    ContainerHost<ManageCategoriesUiState, ManageCategoriesSideEffect> {
    override val container =
        container<ManageCategoriesUiState, ManageCategoriesSideEffect>(ManageCategoriesUiState())

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
            storeRepository.createMenuCategory(storeId, name.trim())
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
            storeRepository.renameMenuCategory(categoryId, name.trim())
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
            storeRepository.deleteMenuCategory(categoryId)
            val updated = loadCategoriesWithMenus(storeId)
            reduce { state.copy(isLoading = false, categories = updated) }
        } catch (e: Exception) {
            reduce {
                state.copy(
                    isLoading = false,
                    blockDeleteCategory = cat,
                    errorMessage = ""
                )
            }
        }
    }

    private suspend fun loadCategoriesWithMenus(storeId: String): List<MenuCategory> {
        val allCategories = storeRepository.getMenuCategories(storeId)
        val categoriesWithMenus = runCatching { storeRepository.getStoreMenus(storeId) }.getOrDefault(emptyList())
        val menusByCategoryId = categoriesWithMenus.associateBy { it.id }
        return allCategories.map { cat ->
            cat.copy(menus = menusByCategoryId[cat.id]?.menus.orEmpty())
        }
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
