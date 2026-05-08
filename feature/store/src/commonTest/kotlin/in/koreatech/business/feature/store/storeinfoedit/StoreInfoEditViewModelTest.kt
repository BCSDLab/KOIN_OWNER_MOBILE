package `in`.koreatech.business.feature.store.storeinfoedit

import `in`.koreatech.business.domain.error.DomainError
import `in`.koreatech.business.domain.model.defaultOperatingTimes
import `in`.koreatech.business.domain.usecase.owner.UploadFileUseCase
import `in`.koreatech.business.domain.usecase.store.GetStoreDetailUseCase
import `in`.koreatech.business.domain.usecase.store.UpdateStoreInfoUseCase
import `in`.koreatech.business.feature.store.fakes.FakeOwnerRepository
import `in`.koreatech.business.feature.store.fakes.FakeStoreRepository
import koreatech.business.designsystem.resources.Res
import koreatech.business.designsystem.resources.error_phone_invalid
import koreatech.business.designsystem.resources.store_info_error_operating_time_invalid
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.orbitmvi.orbit.test.test

@OptIn(ExperimentalCoroutinesApi::class)
class StoreInfoEditViewModelTest {
    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun newViewModel(
        getDetailError: Throwable? = null,
        updateError: Throwable? = null
    ): Pair<StoreInfoEditViewModel, FakeStoreRepository> {
        val store = FakeStoreRepository(
            getStoreDetailError = getDetailError,
            updateStoreInfoError = updateError
        )
        val vm = StoreInfoEditViewModel(
            getStoreDetailUseCase = GetStoreDetailUseCase(store),
            updateStoreInfoUseCase = UpdateStoreInfoUseCase(store),
            uploadFileUseCase = UploadFileUseCase(FakeOwnerRepository())
        )
        return vm to store
    }

    @Test
    fun loadPopulatesStateFromDetail() = runTest {
        val (vm, _) = newViewModel()
        vm.test(this, StoreInfoEditUiState()) {
            containerHost.load("storeA")
            expectState {
                copy(storeId = "storeA", isLoading = true, errorMessage = "", errorMessageRes = null)
            }
            expectState {
                copy(
                    isLoading = false,
                    name = "테스트 매장",
                    phone = "01000000000",
                    address = "서울",
                    description = "설명",
                    openTime = "09:00",
                    closeTime = "21:00",
                    mainCategoryId = 2,
                    selectedCategoryIds = listOf(1, 2),
                    isDelivery = false,
                    isCard = true,
                    isBank = false,
                    existingImageUrls = emptyList(),
                    operatingTimes = defaultOperatingTimes
                )
            }
        }
    }

    @Test
    fun submitInvalidPhoneEmitsPhoneInvalidRes() = runTest {
        val (vm, _) = newViewModel()
        vm.test(
            this,
            StoreInfoEditUiState(storeId = "storeA", phone = "abc")
        ) {
            containerHost.submit()
            expectState { copy(errorMessage = "", errorMessageRes = Res.string.error_phone_invalid) }
        }
    }

    @Test
    fun submitInvalidOperatingTimeEmitsOperatingInvalidRes() = runTest {
        val (vm, _) = newViewModel()
        val invalidTimes = defaultOperatingTimes.toMutableList()
        invalidTimes[0] = invalidTimes[0].copy(openTime = "25:00")
        vm.test(
            this,
            StoreInfoEditUiState(
                storeId = "storeA",
                phone = "01011113333",
                operatingTimes = invalidTimes
            )
        ) {
            containerHost.submit()
            expectState {
                copy(
                    errorMessage = "",
                    errorMessageRes = Res.string.store_info_error_operating_time_invalid
                )
            }
        }
    }

    @Test
    fun submitSucceedsAndEmitsNavigateBack() = runTest {
        val (vm, repo) = newViewModel()
        vm.test(
            this,
            StoreInfoEditUiState(
                storeId = "storeA",
                phone = "01011113333",
                name = "이름",
                operatingTimes = defaultOperatingTimes
            )
        ) {
            containerHost.submit()
            expectState { copy(isLoading = true, errorMessage = "", errorMessageRes = null) }
            expectState { copy(isLoading = false) }
            expectSideEffect(StoreInfoEditSideEffect.NavigateBack)
            assertTrue(repo.updateStoreInfoCalls.contains("storeA"))
        }
    }

    @Test
    fun submitFailureSurfacesServerErrorMessage() = runTest {
        val (vm, _) = newViewModel(updateError = DomainError.Network("저장 실패"))
        vm.test(
            this,
            StoreInfoEditUiState(
                storeId = "storeA",
                phone = "01011113333",
                operatingTimes = defaultOperatingTimes
            )
        ) {
            containerHost.submit()
            expectState { copy(isLoading = true, errorMessage = "", errorMessageRes = null) }
            expectState { copy(isLoading = false, errorMessage = "저장 실패", errorMessageRes = null) }
        }
    }

    @Test
    fun toggleDeliveryCardBank() = runTest {
        val (vm, _) = newViewModel()
        vm.test(this, StoreInfoEditUiState(isDelivery = false, isCard = false, isBank = false)) {
            containerHost.onToggleDelivery()
            expectState { copy(isDelivery = true) }
            containerHost.onToggleCard()
            expectState { copy(isCard = true) }
            containerHost.onToggleBank()
            expectState { copy(isBank = true) }
        }
    }

    @Test
    fun operatingTimeToggleClosesAndReopens() = runTest {
        val (vm, _) = newViewModel()
        val initialTimes = defaultOperatingTimes
        vm.test(this, StoreInfoEditUiState(operatingTimes = initialTimes)) {
            containerHost.onOperatingTimeToggle(0)
            val toggled = initialTimes.toMutableList().also {
                it[0] = it[0].copy(isClosed = true)
            }
            expectState { copy(operatingTimes = toggled.toList()) }
        }
    }
}
