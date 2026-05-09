package `in`.koreatech.business.feature.store.menu.editor

import androidx.lifecycle.ViewModel
import `in`.koreatech.business.domain.model.MenuOptionPrice
import `in`.koreatech.business.domain.usecase.owner.UploadFileUseCase
import `in`.koreatech.business.domain.usecase.store.DeleteMenuUseCase
import `in`.koreatech.business.domain.usecase.store.GetMenuCategoriesUseCase
import `in`.koreatech.business.domain.usecase.store.GetStoreMenusUseCase
import `in`.koreatech.business.domain.usecase.store.RegisterMenuUseCase
import `in`.koreatech.business.domain.usecase.store.UpdateMenuUseCase
import `in`.koreatech.business.platform.PlatformFile
import `in`.koreatech.business.ui.util.BusinessFormatters
import koreatech.business.designsystem.resources.Res
import koreatech.business.designsystem.resources.category_select_required
import koreatech.business.designsystem.resources.menu_editor_error_categories_empty
import koreatech.business.designsystem.resources.menu_editor_error_name_required
import koreatech.business.designsystem.resources.menu_editor_error_option_required
import koreatech.business.designsystem.resources.menu_editor_error_price_required
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

class MenuEditorViewModel(
    private val getMenuCategoriesUseCase: GetMenuCategoriesUseCase,
    private val getStoreMenusUseCase: GetStoreMenusUseCase,
    private val registerMenuUseCase: RegisterMenuUseCase,
    private val updateMenuUseCase: UpdateMenuUseCase,
    private val deleteMenuUseCase: DeleteMenuUseCase,
    private val uploadFileUseCase: UploadFileUseCase
) : ViewModel(),
    ContainerHost<MenuEditorState, MenuEditorSideEffect> {
    override val container = container<MenuEditorState, MenuEditorSideEffect>(MenuEditorState())

    fun init(storeId: String, menuId: String?) {
        intent(registerIdling = false) {
            reduce { state.copy(storeId = storeId, menuId = menuId, isEditMode = menuId != null) }
        }
        loadMenuAndCategories(storeId, menuId)
    }

    private fun loadMenuAndCategories(storeId: String, menuId: String?) {
        intent {
            reduce { state.copy(isLoading = true) }
            try {
                val allCategories = getMenuCategoriesUseCase(storeId)
                if (menuId == null) {
                    reduce { state.copy(isLoading = false, menuCategories = allCategories) }
                    return@intent
                }
                val categories = getStoreMenusUseCase(storeId)
                val menu = categories.flatMap { it.menus }.firstOrNull { it.id.toString() == menuId }
                val restoredCategoryIds = categories.filter { c -> c.menus.any { it.id.toString() == menuId } }.map { it.id }
                if (menu != null) {
                    reduce {
                        state.copy(
                            isLoading = false,
                            menuCategories = allCategories,
                            selectedCategoryIds = restoredCategoryIds,
                            name = menu.name,
                            description = menu.description,
                            existingImageUrls = menu.imageUrls,
                            singlePrice = if (menu.optionPrices.size == 1 && menu.optionPrices.first().option.isBlank()) {
                                menu.optionPrices.first().price.toString()
                            } else {
                                ""
                            },
                            optionPrices = if (menu.optionPrices.isEmpty() || (menu.optionPrices.size == 1 && menu.optionPrices.first().option.isBlank())) {
                                emptyList()
                            } else {
                                menu.optionPrices.map { op -> MenuOptionPriceDraft(option = op.option, price = op.price.toString()) }
                            }
                        )
                    }
                } else {
                    reduce { state.copy(isLoading = false, menuCategories = allCategories) }
                }
            } catch (exception: Exception) {
                reduce { state.copy(isLoading = false, errorMessage = exception.message.orEmpty()) }
            }
        }
    }

    fun onNameChanged(value: String) = intent { reduce { state.copy(name = value) } }
    fun onDescriptionChanged(value: String) = intent { reduce { state.copy(description = value) } }

    fun onCategoryToggled(categoryId: Int) = intent {
        val updated = state.selectedCategoryIds.toMutableList()
        if (categoryId in updated) updated.remove(categoryId) else updated.add(categoryId)
        reduce { state.copy(selectedCategoryIds = updated) }
    }

    fun onSinglePriceChanged(value: String) = intent {
        reduce { state.copy(singlePrice = BusinessFormatters.digitsOnly(value), singlePriceError = "") }
    }

    fun onOptionNameChanged(index: Int, value: String) = intent {
        val updated = state.optionPrices.toMutableList()
        if (index in updated.indices) updated[index] = updated[index].copy(option = value)
        reduce { state.copy(optionPrices = updated) }
    }

    fun onPriceChanged(index: Int, value: String) = intent {
        val updated = state.optionPrices.toMutableList()
        if (index in updated.indices) updated[index] = updated[index].copy(price = BusinessFormatters.digitsOnly(value))
        reduce { state.copy(optionPrices = updated) }
    }

    fun addOptionPrice() {
        intent(registerIdling = false) {
            reduce { state.copy(optionPrices = state.optionPrices + MenuOptionPriceDraft(), singlePrice = "") }
        }
    }

    fun removeOptionPrice(index: Int) {
        intent(registerIdling = false) {
            val updated = state.optionPrices.toMutableList()
            if (index in updated.indices) updated.removeAt(index)
            reduce { state.copy(optionPrices = updated) }
        }
    }

    fun addImage(file: PlatformFile) {
        intent(registerIdling = false) { reduce { state.copy(pendingImages = state.pendingImages + file) } }
    }

    fun removeExistingImage(index: Int) {
        intent(registerIdling = false) {
            val updated = state.existingImageUrls.toMutableList()
            if (index in updated.indices) updated.removeAt(index)
            reduce { state.copy(existingImageUrls = updated) }
        }
    }

    fun removePendingImage(index: Int) {
        intent(registerIdling = false) {
            val updated = state.pendingImages.toMutableList()
            if (index in updated.indices) updated.removeAt(index)
            reduce { state.copy(pendingImages = updated) }
        }
    }

    fun submit() {
        intent {
            if (state.name.isBlank()) {
                reduce { state.copy(errorMessageRes = Res.string.menu_editor_error_name_required, errorMessage = "") }
                return@intent
            }
            val hasOptions = state.optionPrices.isNotEmpty()
            if (!hasOptions && state.singlePrice.isBlank()) {
                reduce { state.copy(errorMessageRes = Res.string.menu_editor_error_price_required, errorMessage = "") }
                return@intent
            }
            if (hasOptions && state.optionPrices.any { it.option.isBlank() || it.price.isBlank() }) {
                reduce { state.copy(errorMessageRes = Res.string.menu_editor_error_option_required, errorMessage = "") }
                return@intent
            }
            if (state.menuCategories.isEmpty()) {
                reduce { state.copy(errorMessageRes = Res.string.menu_editor_error_categories_empty, errorMessage = "") }
                return@intent
            }
            if (state.selectedCategoryIds.isEmpty()) {
                reduce { state.copy(errorMessageRes = Res.string.category_select_required, errorMessage = "") }
                return@intent
            }

            reduce { state.copy(isLoading = true, errorMessageRes = null, errorMessage = "") }
            try {
                val storeId = state.storeId ?: return@intent
                val uploadedUrls = state.pendingImages.map { uploadFileUseCase(it.name, it.mimeType, it.bytes) }
                val allImageUrls = state.existingImageUrls + uploadedUrls
                val submittedPrice = if (hasOptions) null else state.singlePrice.toIntOrNull()
                val domainOptionPrices = if (hasOptions) {
                    state.optionPrices.map { MenuOptionPrice(option = it.option, price = it.price.toIntOrNull() ?: 0) }
                } else {
                    emptyList()
                }

                if (state.isEditMode && state.menuId != null) {
                    updateMenuUseCase(
                        storeId = storeId,
                        menuId = state.menuId!!,
                        name = state.name,
                        price = submittedPrice,
                        description = state.description,
                        imageUrls = allImageUrls,
                        optionPrices = domainOptionPrices,
                        categoryIds = state.selectedCategoryIds
                    )
                } else {
                    registerMenuUseCase(
                        storeId = storeId,
                        name = state.name,
                        price = submittedPrice,
                        description = state.description,
                        imageUrls = allImageUrls,
                        optionPrices = domainOptionPrices,
                        categoryIds = state.selectedCategoryIds
                    )
                }
                reduce { state.copy(isLoading = false) }
                postSideEffect(MenuEditorSideEffect.NavigateBack)
            } catch (exception: Exception) {
                reduce { state.copy(isLoading = false, errorMessage = exception.message.orEmpty(), errorMessageRes = null) }
            }
        }
    }

    fun clearError() {
        intent(registerIdling = false) { reduce { state.copy(errorMessage = "", errorMessageRes = null) } }
    }

    fun deleteMenu() {
        intent {
            val storeId = state.storeId ?: return@intent
            val menuId = state.menuId ?: return@intent
            reduce { state.copy(isLoading = true, errorMessage = "") }
            try {
                deleteMenuUseCase(storeId, menuId)
                reduce { state.copy(isLoading = false) }
                postSideEffect(MenuEditorSideEffect.NavigateBack)
            } catch (exception: Exception) {
                reduce { state.copy(isLoading = false, errorMessage = exception.message.orEmpty()) }
            }
        }
    }
}
