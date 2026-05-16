package `in`.koreatech.business.feature.store.storeinfoedit

import androidx.lifecycle.ViewModel
import `in`.koreatech.business.domain.model.StoreDetail
import `in`.koreatech.business.domain.usecase.owner.UploadFileUseCase
import `in`.koreatech.business.domain.usecase.store.GetStoreDetailUseCase
import `in`.koreatech.business.domain.usecase.store.UpdateStoreInfoUseCase
import `in`.koreatech.business.platform.PlatformFile
import `in`.koreatech.business.ui.util.BusinessFormatters
import `in`.koreatech.business.ui.util.BusinessValidators
import koreatech.business.designsystem.resources.Res
import koreatech.business.designsystem.resources.error_phone_invalid
import koreatech.business.designsystem.resources.store_info_error_operating_time_invalid
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.blockingIntent
import org.orbitmvi.orbit.viewmodel.container

class StoreInfoEditViewModel(
    private val getStoreDetailUseCase: GetStoreDetailUseCase,
    private val updateStoreInfoUseCase: UpdateStoreInfoUseCase,
    private val uploadFileUseCase: UploadFileUseCase
) : ViewModel(),
    ContainerHost<StoreInfoEditState, StoreInfoEditSideEffect> {
    override val container = container<StoreInfoEditState, StoreInfoEditSideEffect>(StoreInfoEditState())

    fun load(storeId: String) {
        intent {
            reduce { state.copy(storeId = storeId, isLoading = true, errorMessage = "", errorMessageRes = null) }
            getStoreDetailUseCase(storeId)
                .onSuccess { detail -> applyStoreDetail(detail) }
                .onFailure { showLoadError(it.message.orEmpty()) }
        }
    }

    private fun applyStoreDetail(detail: StoreDetail) = intent {
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
    }

    private fun showLoadError(message: String) = intent {
        reduce {
            state.copy(
                isLoading = false,
                errorMessage = message,
                errorMessageRes = null
            )
        }
    }

    fun onPhoneChanged(value: String) = blockingIntent {
        reduce { state.copy(phone = BusinessFormatters.digitsOnly(value, 11)) }
    }

    fun onAddressChanged(value: String) = blockingIntent { reduce { state.copy(address = value) } }
    fun onDescriptionChanged(value: String) = blockingIntent { reduce { state.copy(description = value) } }
    fun onOpenTimeChanged(value: String) = blockingIntent { reduce { state.copy(openTime = value) } }
    fun onCloseTimeChanged(value: String) = blockingIntent { reduce { state.copy(closeTime = value) } }
    fun onToggleDelivery() = intent { reduce { state.copy(isDelivery = !state.isDelivery) } }
    fun onToggleCard() = intent { reduce { state.copy(isCard = !state.isCard) } }
    fun onToggleBank() = intent { reduce { state.copy(isBank = !state.isBank) } }

    fun onOperatingTimeToggle(index: Int) = intent {
        val times = state.operatingTimes.toMutableList()
        if (index in times.indices) times[index] = times[index].copy(isClosed = !times[index].isClosed)
        reduce { state.copy(operatingTimes = times) }
    }

    fun onOperatingOpenTimeChanged(index: Int, value: String) = blockingIntent {
        val times = state.operatingTimes.toMutableList()
        if (index in times.indices) times[index] = times[index].copy(openTime = BusinessFormatters.normalizeTime(value))
        reduce { state.copy(operatingTimes = times) }
    }

    fun onOperatingCloseTimeChanged(index: Int, value: String) = blockingIntent {
        val times = state.operatingTimes.toMutableList()
        if (index in times.indices) times[index] = times[index].copy(closeTime = BusinessFormatters.normalizeTime(value))
        reduce { state.copy(operatingTimes = times) }
    }

    fun submit() {
        intent {
            val storeId = state.storeId ?: return@intent
            if (!BusinessValidators.isValidPhone(state.phone)) {
                reduce { state.copy(errorMessage = "", errorMessageRes = Res.string.error_phone_invalid) }
                return@intent
            }
            if (state.operatingTimes.any { !it.isClosed && (!BusinessValidators.isValidTime(it.openTime.orEmpty()) || !BusinessValidators.isValidTime(it.closeTime.orEmpty())) }) {
                reduce {
                    state.copy(errorMessage = "", errorMessageRes = Res.string.store_info_error_operating_time_invalid)
                }
                return@intent
            }
            reduce { state.copy(isLoading = true, errorMessage = "", errorMessageRes = null) }
            uploadImagesAndSubmit(storeId)
        }
    }

    private fun uploadImagesAndSubmit(storeId: String) = intent {
        val uploadedUrls = mutableListOf<String>()
        for (img in state.pendingImages) {
            val uploadResult = uploadFileUseCase(img.name, img.mimeType, img.bytes)
            uploadResult.fold(
                onSuccess = { uploadedUrls.add(it) },
                onFailure = {
                    showSubmitError(it.message.orEmpty())
                    return@intent
                }
            )
        }
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
            .onSuccess { completeSubmit() }
            .onFailure { showSubmitError(it.message.orEmpty()) }
    }

    private fun completeSubmit() = intent {
        reduce { state.copy(isLoading = false) }
        postSideEffect(StoreInfoEditSideEffect.NavigateBack)
    }

    private fun showSubmitError(message: String) = intent {
        reduce {
            state.copy(
                isLoading = false,
                errorMessage = message,
                errorMessageRes = null
            )
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
        intent(registerIdling = false) { reduce { state.copy(errorMessage = "", errorMessageRes = null) } }
    }

    companion object {
        private const val ROOT_CATEGORY_ID = 1
    }
}
