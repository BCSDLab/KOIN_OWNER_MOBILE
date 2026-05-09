package `in`.koreatech.business.feature.store.menu.editor

import `in`.koreatech.business.domain.model.MenuCategory
import `in`.koreatech.business.platform.PlatformFile
import org.jetbrains.compose.resources.StringResource

data class MenuEditorState(
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
    val errorMessage: String = "",
    val errorMessageRes: StringResource? = null
)

data class MenuOptionPriceDraft(val option: String = "", val price: String = "")
