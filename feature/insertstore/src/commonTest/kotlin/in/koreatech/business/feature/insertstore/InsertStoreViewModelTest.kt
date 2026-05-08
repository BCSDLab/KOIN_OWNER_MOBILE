package `in`.koreatech.business.feature.insertstore

import `in`.koreatech.business.domain.error.DomainError
import `in`.koreatech.business.domain.model.MenuCategory
import `in`.koreatech.business.domain.model.MenuOptionPrice
import `in`.koreatech.business.domain.model.OperatingTime
import `in`.koreatech.business.domain.model.StoreCategory
import `in`.koreatech.business.domain.model.StoreDetail
import `in`.koreatech.business.domain.model.StoreEvent
import `in`.koreatech.business.domain.repository.StoreRepository
import `in`.koreatech.business.domain.usecase.owner.UploadFileUseCase
import `in`.koreatech.business.domain.usecase.store.GetStoreCategoriesUseCase
import `in`.koreatech.business.domain.usecase.store.RegisterStoreUseCase
import koreatech.business.designsystem.resources.Res
import koreatech.business.designsystem.resources.category_select_required
import koreatech.business.designsystem.resources.insert_store_error_categories_load_failed
import koreatech.business.designsystem.resources.insert_store_error_description_required
import koreatech.business.designsystem.resources.insert_store_error_name_required
import koreatech.business.designsystem.resources.insert_store_error_register_failed
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
class InsertStoreViewModelTest {
    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun newViewModel(
        categories: List<StoreCategory> = listOf(StoreCategory(id = 2, name = "한식")),
        loadCategoriesError: DomainError? = null,
        registerError: DomainError? = null
    ): Pair<InsertStoreViewModel, FakeStoreRepository> {
        val repo = FakeStoreRepository(
            categories = categories,
            loadCategoriesError = loadCategoriesError,
            registerError = registerError
        )
        val vm = InsertStoreViewModel(
            getStoreCategoriesUseCase = GetStoreCategoriesUseCase(repo),
            registerStoreUseCase = RegisterStoreUseCase(repo),
            uploadFileUseCase = UploadFileUseCase(InsertStoreFakeOwnerRepository())
        )
        return vm to repo
    }

    @Test
    fun categoriesLoadSucceedsOnCreate() = runTest {
        val (vm, _) = newViewModel(categories = listOf(StoreCategory(id = 5, name = "분식")))
        vm.test(this, InsertStoreUiState()) {
            runOnCreate()
            expectState { copy(isLoading = true) }
            expectState { copy(categories = listOf(StoreCategory(5, "분식")), isLoading = false) }
        }
    }

    @Test
    fun categoriesLoadFailureSurfacesErrorRes() = runTest {
        val (vm, _) = newViewModel(loadCategoriesError = DomainError.Network(""))
        vm.test(this, InsertStoreUiState()) {
            runOnCreate()
            expectState { copy(isLoading = true) }
            expectState {
                copy(
                    isLoading = false,
                    errorMessage = "",
                    errorMessageRes = Res.string.insert_store_error_categories_load_failed
                )
            }
        }
    }

    @Test
    fun navigateNextOnSelectCategoryEmitsCategorySelectRequiredWhenUnselected() = runTest {
        val (vm, _) = newViewModel()
        vm.test(this, InsertStoreUiState(step = InsertStoreStep.SelectCategory)) {
            runOnCreate()
            expectState { copy(isLoading = true) }
            expectState {
                copy(categories = listOf(StoreCategory(2, "한식")), isLoading = false)
            }
            containerHost.navigateNext()
            expectState {
                copy(errorMessage = "", errorMessageRes = Res.string.category_select_required)
            }
        }
    }

    @Test
    fun navigateNextOnBasicInfoChecksAllFields() = runTest {
        val (vm, _) = newViewModel()
        // pre-seeded: category selected, but name blank → error_name
        vm.test(
            this,
            InsertStoreUiState(step = InsertStoreStep.BasicInfo, selectedCategoryId = 2)
        ) {
            runOnCreate()
            expectState { copy(isLoading = true) }
            expectState {
                copy(categories = listOf(StoreCategory(2, "한식")), isLoading = false)
            }
            containerHost.navigateNext()
            expectState {
                copy(errorMessage = "", errorMessageRes = Res.string.insert_store_error_name_required)
            }
        }
    }

    @Test
    fun navigateNextDetailInfoEmitsDescriptionRequired() = runTest {
        val (vm, _) = newViewModel()
        vm.test(this, InsertStoreUiState(step = InsertStoreStep.DetailInfo)) {
            runOnCreate()
            expectState { copy(isLoading = true) }
            expectState {
                copy(categories = listOf(StoreCategory(2, "한식")), isLoading = false)
            }
            containerHost.navigateNext()
            expectState {
                copy(errorMessage = "", errorMessageRes = Res.string.insert_store_error_description_required)
            }
        }
    }

    @Test
    fun submitSucceedsAndAdvancesToComplete() = runTest {
        val (vm, repo) = newViewModel()
        vm.test(
            this,
            InsertStoreUiState(
                step = InsertStoreStep.FinalCheck,
                selectedCategoryId = 2,
                name = "테스트 매장",
                address = "주소",
                phone = "01011113333",
                description = "설명"
            )
        ) {
            runOnCreate()
            expectState { copy(isLoading = true) }
            expectState {
                copy(categories = listOf(StoreCategory(2, "한식")), isLoading = false)
            }
            containerHost.navigateNext()
            // submit() flow: isLoading=true → success → step=Complete
            expectState { copy(isLoading = true, errorMessage = "", errorMessageRes = null) }
            expectState { copy(isLoading = false, step = InsertStoreStep.Complete) }
            assertTrue(repo.registerCalled)
        }
    }

    @Test
    fun submitFailureSurfacesRegisterFailedRes() = runTest {
        val (vm, _) = newViewModel(registerError = DomainError.Network(""))
        vm.test(
            this,
            InsertStoreUiState(
                step = InsertStoreStep.FinalCheck,
                selectedCategoryId = 2,
                name = "이름",
                address = "주소",
                phone = "01011113333",
                description = "설명"
            )
        ) {
            runOnCreate()
            expectState { copy(isLoading = true) }
            expectState {
                copy(categories = listOf(StoreCategory(2, "한식")), isLoading = false)
            }
            containerHost.navigateNext()
            expectState { copy(isLoading = true, errorMessage = "", errorMessageRes = null) }
            expectState {
                copy(
                    isLoading = false,
                    errorMessage = "",
                    errorMessageRes = Res.string.insert_store_error_register_failed
                )
            }
        }
    }
}

private class FakeStoreRepository(
    private val categories: List<StoreCategory>,
    private val loadCategoriesError: DomainError? = null,
    private val registerError: DomainError? = null
) : StoreRepository {
    var registerCalled: Boolean = false
        private set

    override suspend fun getStoreCategories(): List<StoreCategory> {
        loadCategoriesError?.let { throw it }
        return categories
    }

    override suspend fun registerStore(
        name: String,
        address: String,
        mainCategoryId: Int,
        categoryIds: List<Int>,
        phone: String,
        delivery: Boolean,
        deliveryPrice: Int,
        payCard: Boolean,
        payBank: Boolean,
        description: String,
        imageUrls: List<String>,
        operatingTimes: List<OperatingTime>
    ) {
        registerError?.let { throw it }
        registerCalled = true
    }

    // unused interface members
    override suspend fun getStoreDetail(storeId: String): StoreDetail = throw UnsupportedOperationException()
    override suspend fun getStoreMenus(storeId: String): List<MenuCategory> = emptyList()
    override suspend fun getMenuCategories(storeId: String): List<MenuCategory> = emptyList()
    override suspend fun getStoreEvents(storeId: String): List<StoreEvent> = emptyList()
    override suspend fun deleteMenu(storeId: String, menuId: String) = Unit
    override suspend fun registerMenu(
        storeId: String,
        name: String,
        price: Int?,
        description: String,
        imageUrls: List<String>,
        optionPrices: List<MenuOptionPrice>,
        categoryIds: List<Int>
    ) = Unit

    override suspend fun updateMenu(
        storeId: String,
        menuId: String,
        name: String,
        price: Int?,
        description: String,
        imageUrls: List<String>,
        optionPrices: List<MenuOptionPrice>,
        categoryIds: List<Int>
    ) = Unit

    override suspend fun registerEvent(
        storeId: String,
        title: String,
        content: String,
        imageUrls: List<String>,
        startDate: String,
        endDate: String
    ) = Unit

    override suspend fun deleteEvent(storeId: String, eventId: String) = Unit
    override suspend fun updateEvent(
        storeId: String,
        eventId: String,
        title: String,
        content: String,
        imageUrls: List<String>,
        startDate: String,
        endDate: String
    ) = Unit

    override suspend fun updateStoreInfo(
        storeId: String,
        name: String,
        phone: String,
        address: String,
        description: String,
        mainCategoryId: Int,
        categoryIds: List<Int>,
        isDelivery: Boolean,
        deliveryPrice: Int,
        isCard: Boolean,
        isBank: Boolean,
        imageUrls: List<String>,
        operatingTimes: List<OperatingTime>
    ) = Unit

    override suspend fun createMenuCategory(storeId: String, name: String) = Unit
    override suspend fun renameMenuCategory(categoryId: Int, name: String) = Unit
    override suspend fun deleteMenuCategory(categoryId: Int) = Unit
}

private class InsertStoreFakeOwnerRepository : `in`.koreatech.business.domain.repository.OwnerRepository {
    override suspend fun getShopList() = emptyList<`in`.koreatech.business.domain.model.owner.OwnerStore>()
    override suspend fun getOwnerProfile() = throw UnsupportedOperationException()
    override suspend fun getRequiredVersion() = ""
    override suspend fun uploadFile(fileName: String, mimeType: String, bytes: ByteArray) = ""
    override suspend fun searchShops(query: String) = emptyList<`in`.koreatech.business.domain.model.signup.ShopSearchResult>()
}
