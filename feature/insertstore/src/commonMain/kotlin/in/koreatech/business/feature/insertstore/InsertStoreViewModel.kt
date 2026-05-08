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
import koreatech.business.designsystem.resources.Res
import koreatech.business.designsystem.resources.category_select_required
import koreatech.business.designsystem.resources.error_phone_invalid
import koreatech.business.designsystem.resources.error_phone_required
import koreatech.business.designsystem.resources.insert_store_error_address_required
import koreatech.business.designsystem.resources.insert_store_error_categories_load_failed
import koreatech.business.designsystem.resources.insert_store_error_description_required
import koreatech.business.designsystem.resources.insert_store_error_name_required
import koreatech.business.designsystem.resources.insert_store_error_register_failed
import org.jetbrains.compose.resources.StringResource
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.Syntax
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

class InsertStoreViewModel(
    private val getStoreCategoriesUseCase: GetStoreCategoriesUseCase,
    private val registerStoreUseCase: RegisterStoreUseCase,
    private val uploadFileUseCase: UploadFileUseCase
) : ViewModel(),
    ContainerHost<InsertStoreUiState, Nothing> {
    override val container = container<InsertStoreUiState, Nothing>(
        initialState = InsertStoreUiState(),
        onCreate = { loadCategories() }
    )

    private suspend fun Syntax<InsertStoreUiState, Nothing>.loadCategories() {
        reduce { state.copy(isLoading = true) }
        try {
            val categories = getStoreCategoriesUseCase()
            reduce { state.copy(categories = categories, isLoading = false) }
        } catch (e: Exception) {
            val msg = e.message.orEmpty()
            reduce {
                state.copy(
                    isLoading = false,
                    errorMessage = msg,
                    errorMessageRes = if (msg.isEmpty()) Res.string.insert_store_error_categories_load_failed else null
                )
            }
        }
    }

    fun navigateNext() = intent {
        val next = when (state.step) {
            InsertStoreStep.Start -> InsertStoreStep.SelectCategory
            InsertStoreStep.SelectCategory -> {
                if (state.selectedCategoryId == -1) {
                    reduce { state.copy(errorMessage = "", errorMessageRes = Res.string.category_select_required) }
                    return@intent
                }
                InsertStoreStep.BasicInfo
            }
            InsertStoreStep.BasicInfo -> {
                when {
                    state.name.isBlank() -> {
                        reduce {
                            state.copy(errorMessage = "", errorMessageRes = Res.string.insert_store_error_name_required)
                        }
                        return@intent
                    }
                    state.address.isBlank() -> {
                        reduce {
                            state.copy(errorMessage = "", errorMessageRes = Res.string.insert_store_error_address_required)
                        }
                        return@intent
                    }
                    state.phone.isBlank() -> {
                        reduce { state.copy(errorMessage = "", errorMessageRes = Res.string.error_phone_required) }
                        return@intent
                    }
                    !BusinessValidators.isValidPhone(state.phone) -> {
                        reduce { state.copy(errorMessage = "", errorMessageRes = Res.string.error_phone_invalid) }
                        return@intent
                    }
                }
                InsertStoreStep.DetailInfo
            }
            InsertStoreStep.DetailInfo -> {
                if (state.description.isBlank()) {
                    reduce {
                        state.copy(errorMessage = "", errorMessageRes = Res.string.insert_store_error_description_required)
                    }
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
        reduce { state.copy(step = next, errorMessage = "", errorMessageRes = null) }
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
        intent(registerIdling = false) {
            reduce { state.copy(step = previousStep, errorMessage = "", errorMessageRes = null) }
        }
        return true
    }

    fun onCategorySelected(id: Int, name: String) = intent {
        reduce {
            state.copy(
                selectedCategoryId = id,
                selectedCategoryName = name,
                errorMessage = "",
                errorMessageRes = null
            )
        }
    }

    fun onNameChanged(value: String) = intent {
        reduce { state.copy(name = value, errorMessage = "", errorMessageRes = null) }
    }
    fun onAddressChanged(value: String) = intent {
        reduce { state.copy(address = value, errorMessage = "", errorMessageRes = null) }
    }
    fun onPhoneChanged(value: String) = intent {
        reduce {
            state.copy(
                phone = BusinessFormatters.digitsOnly(value, 11),
                errorMessage = "",
                errorMessageRes = null
            )
        }
    }
    fun onToggleCard() = intent { reduce { state.copy(isCardOk = !state.isCardOk) } }
    fun onToggleBank() = intent { reduce { state.copy(isBankOk = !state.isBankOk) } }
    fun onToggleDelivery() = intent { reduce { state.copy(isDeliveryOk = !state.isDeliveryOk) } }
    fun onDeliveryPriceChanged(value: String) = intent {
        reduce { state.copy(deliveryPrice = BusinessFormatters.digitsOnly(value)) }
    }
    fun onDescriptionChanged(value: String) = intent {
        reduce { state.copy(description = value, errorMessage = "", errorMessageRes = null) }
    }

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

    fun addCoverImage(file: PlatformFile) = intent {
        reduce { state.copy(coverImages = state.coverImages + file) }
    }

    fun removeCoverImage(index: Int) = intent(registerIdling = false) {
        val updated = state.coverImages.toMutableList()
        if (index in updated.indices) updated.removeAt(index)
        reduce { state.copy(coverImages = updated) }
    }

    private fun submit() = intent {
        reduce { state.copy(isLoading = true, errorMessage = "", errorMessageRes = null) }
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
            val msg = e.message.orEmpty()
            reduce {
                state.copy(
                    isLoading = false,
                    errorMessage = msg,
                    errorMessageRes = if (msg.isEmpty()) Res.string.insert_store_error_register_failed else null
                )
            }
        }
    }

    fun clearError() = intent(registerIdling = false) {
        reduce { state.copy(errorMessage = "", errorMessageRes = null) }
    }
}
