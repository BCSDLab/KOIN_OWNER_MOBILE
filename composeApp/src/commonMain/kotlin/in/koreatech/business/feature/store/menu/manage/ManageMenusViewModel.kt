package `in`.koreatech.business.feature.store.menu.manage

import androidx.lifecycle.ViewModel
import `in`.koreatech.business.domain.model.MenuCategory
import `in`.koreatech.business.domain.repository.StoreRepository
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

class ManageMenusViewModel(
    private val storeRepository: StoreRepository
) : ViewModel(),
    ContainerHost<ManageMenusUiState, ManageMenusSideEffect> {
    override val container = container<ManageMenusUiState, ManageMenusSideEffect>(ManageMenusUiState())

    fun load(storeId: String) {
        intent {
            reduce { state.copy(storeId = storeId, isLoading = true, errorMessage = "") }
            try {
                val categories = storeRepository.getStoreMenus(storeId)
                reduce { state.copy(isLoading = false, categories = categories, deletingMenuId = null) }
            } catch (exception: Exception) {
                reduce {
                    state.copy(isLoading = false, errorMessage = exception.message.orEmpty())
                }
            }
        }
    }

    fun deleteMenu(menuId: String) {
        intent {
            val storeId = state.storeId ?: return@intent
            reduce { state.copy(deletingMenuId = menuId) }
            try {
                storeRepository.deleteMenu(storeId, menuId)
                val categories = storeRepository.getStoreMenus(storeId)
                reduce {
                    state.copy(
                        categories = categories,
                        deletingMenuId = null,
                        errorMessage = ""
                    )
                }
            } catch (exception: Exception) {
                reduce {
                    state.copy(
                        deletingMenuId = null,
                        errorMessage = exception.message.orEmpty()
                    )
                }
            }
        }
    }

    fun refresh() {
        val storeId = container.stateFlow.value.storeId ?: return
        load(storeId)
    }

    fun clearError() {
        intent(registerIdling = false) {
            reduce { state.copy(errorMessage = "") }
        }
    }
}

data class ManageMenusUiState(
    val isLoading: Boolean = false,
    val storeId: String? = null,
    val categories: List<MenuCategory> = emptyList(),
    val deletingMenuId: String? = null,
    val errorMessage: String = ""
)
