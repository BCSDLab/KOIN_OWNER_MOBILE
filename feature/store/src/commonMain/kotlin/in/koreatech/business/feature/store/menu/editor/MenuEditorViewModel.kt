package `in`.koreatech.business.feature.store.menu.editor

import androidx.lifecycle.ViewModel
import `in`.koreatech.business.domain.model.MenuCategory
import `in`.koreatech.business.domain.model.MenuOptionPrice
import `in`.koreatech.business.domain.usecase.owner.UploadFileUseCase
import `in`.koreatech.business.domain.usecase.store.DeleteMenuUseCase
import `in`.koreatech.business.domain.usecase.store.GetMenuCategoriesUseCase
import `in`.koreatech.business.domain.usecase.store.GetStoreMenusUseCase
import `in`.koreatech.business.domain.usecase.store.RegisterMenuUseCase
import `in`.koreatech.business.domain.usecase.store.UpdateMenuUseCase
import `in`.koreatech.business.platform.PlatformFile
import `in`.koreatech.business.ui.util.BusinessFormatters
import `in`.koreatech.business.ui.util.blockingIntent
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
    ContainerHost<MenuEditorUiState, MenuEditorSideEffect> {
    override val container = container<MenuEditorUiState, MenuEditorSideEffect>(MenuEditorUiState())

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

    fun onNameChanged(value: String) = blockingIntent { reduce { state.copy(name = value) } }
    fun onDescriptionChanged(value: String) = blockingIntent { reduce { state.copy(description = value) } }

    fun onCategoryToggled(categoryId: Int) = blockingIntent {
        val updated = state.selectedCategoryIds.toMutableList()
        if (categoryId in updated) updated.remove(categoryId) else updated.add(categoryId)
        reduce { state.copy(selectedCategoryIds = updated) }
    }

    fun onSinglePriceChanged(value: String) = blockingIntent {
        reduce { state.copy(singlePrice = BusinessFormatters.digitsOnly(value), singlePriceError = "") }
    }

    fun onOptionNameChanged(index: Int, value: String) = blockingIntent {
        val updated = state.optionPrices.toMutableList()
        if (index in updated.indices) updated[index] = updated[index].copy(option = value)
        reduce { state.copy(optionPrices = updated) }
    }

    fun onPriceChanged(index: Int, value: String) = blockingIntent {
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
                reduce { state.copy(errorMessage = "메뉴 이름을 입력해주세요.") }
                return@intent
            }
            val hasOptions = state.optionPrices.isNotEmpty()
            if (!hasOptions && state.singlePrice.isBlank()) {
                reduce { state.copy(errorMessage = "가격을 입력해주세요.") }
                return@intent
            }
            if (hasOptions && state.optionPrices.any { it.option.isBlank() || it.price.isBlank() }) {
                reduce { state.copy(errorMessage = "옵션명과 가격을 모두 입력해주세요.") }
                return@intent
            }
            if (state.menuCategories.isEmpty()) {
                reduce { state.copy(errorMessage = "카테고리를 먼저 생성해주세요.") }
                return@intent
            }
            if (state.selectedCategoryIds.isEmpty()) {
                reduce { state.copy(errorMessage = "카테고리를 선택해주세요.") }
                return@intent
            }

            reduce { state.copy(isLoading = true, errorMessage = "") }
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
                reduce { state.copy(isLoading = false, errorMessage = exception.message.orEmpty()) }
            }
        }
    }

    fun clearError() {
        intent(registerIdling = false) { reduce { state.copy(errorMessage = "") } }
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

data class MenuEditorUiState(
    val isLoading: Boolean = false,
    val storeId: String? = null,
    val menuId: String? = null,
    val isEditMode: Boolean = false,
    val name: String = "",
    val description: String = "",
    val singlePrice: String = "",
    val singlePriceError: String = "",
    val optionPrices: List<MenuOptionPriceDraft> = emptyList(),
    val menuCategories: List<MenuCategory> = emptyList(),
    val selectedCategoryIds: List<Int> = emptyList(),
    val existingImageUrls: List<String> = emptyList(),
    val pendingImages: List<PlatformFile> = emptyList(),
    val errorMessage: String = ""
)

data class MenuOptionPriceDraft(val option: String = "", val price: String = "")
