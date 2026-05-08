package `in`.koreatech.business.feature.store.menu.manage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import `in`.koreatech.business.domain.model.MenuCategory
import `in`.koreatech.business.domain.usecase.store.DeleteMenuUseCase
import `in`.koreatech.business.domain.usecase.store.GetStoreMenusUseCase
import `in`.koreatech.business.domain.usecase.store.ObserveActiveStoreIdUseCase
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

class ManageMenusViewModel(
    private val getStoreMenusUseCase: GetStoreMenusUseCase,
    private val deleteMenuUseCase: DeleteMenuUseCase,
    private val observeActiveStoreIdUseCase: ObserveActiveStoreIdUseCase
) : ViewModel(),
    ContainerHost<ManageMenusUiState, ManageMenusSideEffect> {
    override val container = container<ManageMenusUiState, ManageMenusSideEffect>(
        initialState = ManageMenusUiState(),
        onCreate = {
            observeActiveStoreIdUseCase()
                .distinctUntilChanged()
                .onEach { id -> if (!id.isNullOrBlank()) load(id) }
                .launchIn(viewModelScope)
        }
    )

    fun load(storeId: String) {
        intent {
            reduce { state.copy(storeId = storeId, isLoading = true, errorMessage = "") }
            try {
                val categories = getStoreMenusUseCase(storeId)
                reduce { state.copy(isLoading = false, categories = categories, deletingMenuId = null) }
            } catch (exception: Exception) {
                reduce { state.copy(isLoading = false, errorMessage = exception.message.orEmpty()) }
            }
        }
    }

    fun deleteMenu(menuId: String) {
        intent {
            val storeId = state.storeId ?: return@intent
            reduce { state.copy(deletingMenuId = menuId) }
            try {
                deleteMenuUseCase(storeId, menuId)
                val categories = getStoreMenusUseCase(storeId)
                reduce { state.copy(categories = categories, deletingMenuId = null, errorMessage = "") }
            } catch (exception: Exception) {
                reduce { state.copy(deletingMenuId = null, errorMessage = exception.message.orEmpty()) }
            }
        }
    }

    fun refresh() {
        val storeId = container.stateFlow.value.storeId ?: return
        load(storeId)
    }

    fun clearError() {
        intent(registerIdling = false) { reduce { state.copy(errorMessage = "") } }
    }
}

data class ManageMenusUiState(
    val isLoading: Boolean = false,
    val storeId: String? = null,
    val categories: List<MenuCategory> = emptyList(),
    val deletingMenuId: String? = null,
    val errorMessage: String = ""
)
