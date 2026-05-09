package `in`.koreatech.business.feature.store.storeinfoedit

import `in`.koreatech.business.domain.model.OperatingTime
import `in`.koreatech.business.domain.model.defaultOperatingTimes
import `in`.koreatech.business.platform.PlatformFile
import org.jetbrains.compose.resources.StringResource

data class StoreInfoEditState(
    val isLoading: Boolean = false,
    val storeId: String? = null,
    val name: String = "",
    val phone: String = "",
    val address: String = "",
    val description: String = "",
    val openTime: String = "",
    val closeTime: String = "",
    val mainCategoryId: Int = 0,
    val selectedCategoryIds: List<Int> = emptyList(),
    val isDelivery: Boolean = false,
    val deliveryPrice: Int = 0,
    val isCard: Boolean = false,
    val isBank: Boolean = false,
    val existingImageUrls: List<String> = emptyList(),
    val pendingImages: List<PlatformFile> = emptyList(),
    val operatingTimes: List<OperatingTime> = defaultOperatingTimes,
    val errorMessage: String = "",
    val errorMessageRes: StringResource? = null
)
