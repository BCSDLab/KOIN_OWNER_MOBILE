package `in`.koreatech.business.feature.insertstore

import `in`.koreatech.business.domain.model.OperatingTime
import `in`.koreatech.business.domain.model.StoreCategory
import `in`.koreatech.business.domain.model.defaultOperatingTimes
import `in`.koreatech.business.platform.PlatformFile
import org.jetbrains.compose.resources.StringResource

enum class InsertStoreStep {
    Start,
    SelectCategory,
    BasicInfo,
    DetailInfo,
    FinalCheck,
    Complete;

    val route: String get() = when (this) {
        Start -> "start"
        SelectCategory -> "select-category"
        BasicInfo -> "basic-info"
        DetailInfo -> "detail-info"
        FinalCheck -> "final-check"
        Complete -> "complete"
    }
}

data class InsertStoreState(
    val step: InsertStoreStep = InsertStoreStep.Start,
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val errorMessageRes: StringResource? = null,
    val categories: List<StoreCategory> = emptyList(),
    val selectedCategoryId: Int = -1,
    val selectedCategoryName: String = "",
    val name: String = "",
    val address: String = "",
    val phone: String = "",
    val isCardOk: Boolean = false,
    val isBankOk: Boolean = false,
    val isDeliveryOk: Boolean = false,
    val deliveryPrice: String = "",
    val description: String = "",
    val operatingTimes: List<OperatingTime> = defaultOperatingTimes,
    val coverImages: List<PlatformFile> = emptyList()
) {
    val mainCategoryId: Int get() = selectedCategoryId
    val categoryIds: List<Int> get() = listOf(1, selectedCategoryId)
}
