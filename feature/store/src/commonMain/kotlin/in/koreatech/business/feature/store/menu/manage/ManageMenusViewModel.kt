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
    ContainerHost<ManageMenusState, ManageMenusSideEffect> {
    override val container = container<ManageMenusState, ManageMenusSideEffect>(
        initialState = ManageMenusState(),
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
            getStoreMenusUseCase(storeId)
                .onSuccess { categories -> applyCategories(categories) }
                .onFailure { showLoadError(it.message.orEmpty()) }
        }
    }

    private fun applyCategories(categories: List<MenuCategory>) = intent {
        reduce { state.copy(isLoading = false, categories = categories, deletingMenuId = null) }
    }

    private fun showLoadError(message: String) = intent {
        reduce { state.copy(isLoading = false, errorMessage = message) }
    }

    fun deleteMenu(menuId: String) {
        intent {
            val storeId = state.storeId ?: return@intent
            reduce { state.copy(deletingMenuId = menuId) }
            deleteMenuUseCase(storeId, menuId)
                .onSuccess { reloadAfterDelete(storeId) }
                .onFailure { showDeleteError(it.message.orEmpty()) }
        }
    }

    private fun reloadAfterDelete(storeId: String) = intent {
        getStoreMenusUseCase(storeId)
            .onSuccess { categories -> applyCategoriesAfterDelete(categories) }
            .onFailure { showDeleteError(it.message.orEmpty()) }
    }

    private fun applyCategoriesAfterDelete(categories: List<MenuCategory>) = intent {
        reduce { state.copy(categories = categories, deletingMenuId = null, errorMessage = "") }
    }

    private fun showDeleteError(message: String) = intent {
        reduce { state.copy(deletingMenuId = null, errorMessage = message) }
    }

    fun refresh() {
        val storeId = container.stateFlow.value.storeId ?: return
        load(storeId)
    }

    fun clearError() {
        intent(registerIdling = false) { reduce { state.copy(errorMessage = "") } }
    }
}
