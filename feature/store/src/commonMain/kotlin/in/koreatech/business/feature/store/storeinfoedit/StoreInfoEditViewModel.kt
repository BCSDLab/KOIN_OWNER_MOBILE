package `in`.koreatech.business.feature.store.storeinfoedit

import androidx.lifecycle.ViewModel
import `in`.koreatech.business.domain.model.OperatingTime
import `in`.koreatech.business.domain.model.defaultOperatingTimes
import `in`.koreatech.business.domain.usecase.owner.UploadFileUseCase
import `in`.koreatech.business.domain.usecase.store.GetStoreDetailUseCase
import `in`.koreatech.business.domain.usecase.store.UpdateStoreInfoUseCase
import `in`.koreatech.business.platform.PlatformFile
import `in`.koreatech.business.ui.util.BusinessFormatters
import `in`.koreatech.business.ui.util.BusinessValidators
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

class StoreInfoEditViewModel(
    private val getStoreDetailUseCase: GetStoreDetailUseCase,
    private val updateStoreInfoUseCase: UpdateStoreInfoUseCase,
    private val uploadFileUseCase: UploadFileUseCase
) : ViewModel(),
    ContainerHost<StoreInfoEditUiState, StoreInfoEditSideEffect> {
    override val container = container<StoreInfoEditUiState, StoreInfoEditSideEffect>(StoreInfoEditUiState())

    fun load(storeId: String) {
        intent {
            reduce { state.copy(storeId = storeId, isLoading = true, errorMessage = "") }
            try {
                val detail = getStoreDetailUseCase(storeId)
                val derivedMainCategoryId = detail.categoryIds.firstOrNull { it != ROOT_CATEGORY_ID } ?: ROOT_CATEGORY_ID
                reduce {
                    state.copy(
                        isLoading = false,
                        name = detail.name,
                        phone = BusinessFormatters.digitsOnly(detail.phone, 11),
                        address = detail.address,
                        description = detail.description,
                        openTime = detail.openTime,
                        closeTime = detail.closeTime,
                        mainCategoryId = derivedMainCategoryId,
                        selectedCategoryIds = detail.categoryIds.ifEmpty { listOf(ROOT_CATEGORY_ID) },
                        isDelivery = detail.isDelivery,
                        isCard = detail.isCard,
                        isBank = detail.isBank,
                        existingImageUrls = detail.imageUrls,
                        operatingTimes = detail.operatingTimes
                    )
                }
            } catch (exception: Exception) {
                reduce { state.copy(isLoading = false, errorMessage = exception.message.orEmpty()) }
            }
        }
    }

    fun onPhoneChanged(value: String) = intent {
        reduce { state.copy(phone = BusinessFormatters.digitsOnly(value, 11)) }
    }

    fun onAddressChanged(value: String) = intent { reduce { state.copy(address = value) } }
    fun onDescriptionChanged(value: String) = intent { reduce { state.copy(description = value) } }
    fun onOpenTimeChanged(value: String) = intent { reduce { state.copy(openTime = value) } }
    fun onCloseTimeChanged(value: String) = intent { reduce { state.copy(closeTime = value) } }
    fun onToggleDelivery() = intent { reduce { state.copy(isDelivery = !state.isDelivery) } }
    fun onToggleCard() = intent { reduce { state.copy(isCard = !state.isCard) } }
    fun onToggleBank() = intent { reduce { state.copy(isBank = !state.isBank) } }

    fun onOperatingTimeToggle(index: Int) = intent {
        val times = state.operatingTimes.toMutableList()
        if (index in times.indices) times[index] = times[index].copy(isClosed = !times[index].isClosed)
        reduce { state.copy(operatingTimes = times) }
    }

    fun onOperatingOpenTimeChanged(index: Int, value: String) = intent {
        val times = state.operatingTimes.toMutableList()
        if (index in times.indices) times[index] = times[index].copy(openTime = BusinessFormatters.normalizeTime(value))
        reduce { state.copy(operatingTimes = times) }
    }

    fun onOperatingCloseTimeChanged(index: Int, value: String) = intent {
        val times = state.operatingTimes.toMutableList()
        if (index in times.indices) times[index] = times[index].copy(closeTime = BusinessFormatters.normalizeTime(value))
        reduce { state.copy(operatingTimes = times) }
    }

    fun submit() {
        intent {
            val storeId = state.storeId ?: return@intent
            if (!BusinessValidators.isValidPhone(state.phone)) {
                reduce { state.copy(errorMessage = "올바른 전화번호를 입력해주세요.") }
                return@intent
            }
            if (state.operatingTimes.any { !it.isClosed && (!BusinessValidators.isValidTime(it.openTime.orEmpty()) || !BusinessValidators.isValidTime(it.closeTime.orEmpty())) }) {
                reduce { state.copy(errorMessage = "운영 시간을 올바르게 선택해주세요.") }
                return@intent
            }
            reduce { state.copy(isLoading = true, errorMessage = "") }
            try {
                val uploadedUrls = state.pendingImages.map { uploadFileUseCase(it.name, it.mimeType, it.bytes) }
                val mainId = state.mainCategoryId.takeIf { it > 0 }
                    ?: state.selectedCategoryIds.firstOrNull { it != ROOT_CATEGORY_ID }
                    ?: ROOT_CATEGORY_ID
                val finalCategoryIds = (listOf(ROOT_CATEGORY_ID, mainId) + state.selectedCategoryIds).distinct()
                updateStoreInfoUseCase(
                    storeId = storeId, name = state.name, phone = BusinessFormatters.formatPhone(state.phone),
                    address = state.address, description = state.description,
                    mainCategoryId = mainId, categoryIds = finalCategoryIds,
                    isDelivery = state.isDelivery, deliveryPrice = state.deliveryPrice,
                    isCard = state.isCard, isBank = state.isBank,
                    imageUrls = state.existingImageUrls + uploadedUrls,
                    operatingTimes = state.operatingTimes
                )
                reduce { state.copy(isLoading = false) }
                postSideEffect(StoreInfoEditSideEffect.NavigateBack)
            } catch (exception: Exception) {
                reduce { state.copy(isLoading = false, errorMessage = exception.message.orEmpty()) }
            }
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

    fun clearError() {
        intent(registerIdling = false) { reduce { state.copy(errorMessage = "") } }
    }

    companion object {
        private const val ROOT_CATEGORY_ID = 1
    }
}

data class StoreInfoEditUiState(
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
    val errorMessage: String = ""
)
