package `in`.koreatech.business.feature.insertstore

import androidx.lifecycle.ViewModel
import `in`.koreatech.business.domain.model.OperatingTime
import `in`.koreatech.business.domain.model.StoreCategory
import `in`.koreatech.business.domain.model.defaultOperatingTimes
import `in`.koreatech.business.domain.usecase.owner.UploadFileUseCase
import `in`.koreatech.business.domain.usecase.store.GetStoreCategoriesUseCase
import `in`.koreatech.business.domain.usecase.store.RegisterStoreUseCase
import `in`.koreatech.business.platform.PlatformFile
import `in`.koreatech.business.ui.util.BusinessFormatters
import `in`.koreatech.business.ui.util.BusinessValidators
import `in`.koreatech.business.ui.util.blockingIntent
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

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

data class InsertStoreUiState(
    val step: InsertStoreStep = InsertStoreStep.Start,
    val isLoading: Boolean = false,
    val errorMessage: String = "",
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

class InsertStoreViewModel(
    private val getStoreCategoriesUseCase: GetStoreCategoriesUseCase,
    private val registerStoreUseCase: RegisterStoreUseCase,
    private val uploadFileUseCase: UploadFileUseCase
) : ViewModel(),
    ContainerHost<InsertStoreUiState, Nothing> {
    override val container = container<InsertStoreUiState, Nothing>(InsertStoreUiState())

    init {
        loadCategories()
    }

    private fun loadCategories() = intent {
        reduce { state.copy(isLoading = true) }
        try {
            val categories = getStoreCategoriesUseCase()
            reduce { state.copy(categories = categories, isLoading = false) }
        } catch (e: Exception) {
            reduce { state.copy(isLoading = false, errorMessage = e.message ?: "카테고리를 불러오지 못했습니다.") }
        }
    }

    fun navigateNext() = intent {
        val next = when (state.step) {
            InsertStoreStep.Start -> InsertStoreStep.SelectCategory
            InsertStoreStep.SelectCategory -> {
                if (state.selectedCategoryId == -1) {
                    reduce { state.copy(errorMessage = "카테고리를 선택해주세요.") }
                    return@intent
                }
                InsertStoreStep.BasicInfo
            }
            InsertStoreStep.BasicInfo -> {
                when {
                    state.name.isBlank() -> {
                        reduce { state.copy(errorMessage = "매장명을 입력해주세요.") }
                        return@intent
                    }
                    state.address.isBlank() -> {
                        reduce { state.copy(errorMessage = "주소를 입력해주세요.") }
                        return@intent
                    }
                    state.phone.isBlank() -> {
                        reduce { state.copy(errorMessage = "전화번호를 입력해주세요.") }
                        return@intent
                    }
                    !BusinessValidators.isValidPhone(state.phone) -> {
                        reduce { state.copy(errorMessage = "올바른 전화번호를 입력해주세요.") }
                        return@intent
                    }
                }
                InsertStoreStep.DetailInfo
            }
            InsertStoreStep.DetailInfo -> {
                if (state.description.isBlank()) {
                    reduce { state.copy(errorMessage = "소개글을 입력해주세요.") }
                    return@intent
                }
                InsertStoreStep.FinalCheck
            }
            InsertStoreStep.FinalCheck -> {
                submit()
                return@intent
            }
            InsertStoreStep.Complete -> InsertStoreStep.Complete
        }
        reduce { state.copy(step = next, errorMessage = "") }
    }

    fun navigateBack(): Boolean {
        val previousStep = when (container.stateFlow.value.step) {
            InsertStoreStep.Start -> return false
            InsertStoreStep.SelectCategory -> InsertStoreStep.Start
            InsertStoreStep.BasicInfo -> InsertStoreStep.SelectCategory
            InsertStoreStep.DetailInfo -> InsertStoreStep.BasicInfo
            InsertStoreStep.FinalCheck -> InsertStoreStep.DetailInfo
            InsertStoreStep.Complete -> return false
        }
        intent(registerIdling = false) { reduce { state.copy(step = previousStep, errorMessage = "") } }
        return true
    }

    fun onCategorySelected(id: Int, name: String) = blockingIntent {
        reduce { state.copy(selectedCategoryId = id, selectedCategoryName = name, errorMessage = "") }
    }

    fun onNameChanged(value: String) = blockingIntent { reduce { state.copy(name = value, errorMessage = "") } }
    fun onAddressChanged(value: String) = blockingIntent { reduce { state.copy(address = value, errorMessage = "") } }
    fun onPhoneChanged(value: String) = blockingIntent {
        reduce { state.copy(phone = BusinessFormatters.digitsOnly(value, 11), errorMessage = "") }
    }
    fun onToggleCard() = blockingIntent { reduce { state.copy(isCardOk = !state.isCardOk) } }
    fun onToggleBank() = blockingIntent { reduce { state.copy(isBankOk = !state.isBankOk) } }
    fun onToggleDelivery() = blockingIntent { reduce { state.copy(isDeliveryOk = !state.isDeliveryOk) } }
    fun onDeliveryPriceChanged(value: String) = blockingIntent {
        reduce { state.copy(deliveryPrice = BusinessFormatters.digitsOnly(value)) }
    }
    fun onDescriptionChanged(value: String) = blockingIntent { reduce { state.copy(description = value, errorMessage = "") } }

    fun onOperatingTimeToggle(index: Int) = blockingIntent {
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

    fun addCoverImage(file: PlatformFile) = intent {
        reduce { state.copy(coverImages = state.coverImages + file) }
    }

    fun removeCoverImage(index: Int) = intent(registerIdling = false) {
        val updated = state.coverImages.toMutableList()
        if (index in updated.indices) updated.removeAt(index)
        reduce { state.copy(coverImages = updated) }
    }

    private fun submit() = intent {
        reduce { state.copy(isLoading = true, errorMessage = "") }
        try {
            val imageUrls = state.coverImages.map { uploadFileUseCase(it.name, it.mimeType, it.bytes) }
            registerStoreUseCase(
                name = state.name, address = state.address,
                mainCategoryId = state.mainCategoryId, categoryIds = state.categoryIds,
                phone = BusinessFormatters.formatPhone(state.phone), delivery = state.isDeliveryOk,
                deliveryPrice = state.deliveryPrice.toIntOrNull() ?: 0,
                payCard = state.isCardOk, payBank = state.isBankOk,
                description = state.description, imageUrls = imageUrls,
                operatingTimes = state.operatingTimes
            )
            reduce { state.copy(isLoading = false, step = InsertStoreStep.Complete) }
        } catch (e: Exception) {
            reduce { state.copy(isLoading = false, errorMessage = e.message ?: "매장 등록에 실패했습니다.") }
        }
    }

    fun clearError() = intent(registerIdling = false) { reduce { state.copy(errorMessage = "") } }
}
