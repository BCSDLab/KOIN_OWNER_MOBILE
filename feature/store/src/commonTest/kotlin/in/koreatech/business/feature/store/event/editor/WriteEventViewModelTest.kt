@file:OptIn(kotlin.time.ExperimentalTime::class)

package `in`.koreatech.business.feature.store.event.editor

import `in`.koreatech.business.domain.model.StoreEvent
import `in`.koreatech.business.domain.usecase.owner.UploadFileUseCase
import `in`.koreatech.business.domain.usecase.store.DeleteEventUseCase
import `in`.koreatech.business.domain.usecase.store.GetStoreEventsUseCase
import `in`.koreatech.business.domain.usecase.store.RegisterEventUseCase
import `in`.koreatech.business.domain.usecase.store.UpdateEventUseCase
import `in`.koreatech.business.feature.store.fakes.FakeOwnerRepository
import `in`.koreatech.business.feature.store.fakes.FakeStoreRepository
import koreatech.business.designsystem.resources.Res
import koreatech.business.designsystem.resources.event_editor_error_content_required
import koreatech.business.designsystem.resources.event_editor_error_not_found
import koreatech.business.designsystem.resources.event_editor_error_period_invalid
import koreatech.business.designsystem.resources.event_editor_error_period_required
import koreatech.business.designsystem.resources.event_editor_error_title_required
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
class WriteEventViewModelTest {
    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun newViewModel(
        events: List<StoreEvent> = emptyList(),
        getEventsError: Throwable? = null,
        registerError: Throwable? = null,
        updateError: Throwable? = null,
        deleteError: Throwable? = null
    ): Pair<WriteEventViewModel, FakeStoreRepository> {
        val repo = FakeStoreRepository(
            storeEvents = events,
            getStoreEventsError = getEventsError,
            registerEventError = registerError,
            updateEventError = updateError,
            deleteEventError = deleteError
        )
        val vm = WriteEventViewModel(
            getStoreEventsUseCase = GetStoreEventsUseCase(repo),
            registerEventUseCase = RegisterEventUseCase(repo),
            updateEventUseCase = UpdateEventUseCase(repo),
            deleteEventUseCase = DeleteEventUseCase(repo),
            uploadFileUseCase = UploadFileUseCase(FakeOwnerRepository())
        )
        return vm to repo
    }

    @Test
    fun initLoadsEventForEditMode() = runTest {
        val sample = StoreEvent(
            id = 5,
            shopId = 100,
            title = "할인",
            content = "20% 세일",
            thumbnailUrls = listOf("a.jpg"),
            startDate = "2026-01-01",
            endDate = "2026-01-31"
        )
        val (vm, _) = newViewModel(events = listOf(sample))
        vm.test(this, WriteEventState()) {
            containerHost.init(storeId = "storeA", eventId = "5")
            expectState { copy(storeId = "storeA", eventId = "5", isEditMode = true) }
            expectState { copy(isLoading = true, errorMessage = "", errorMessageRes = null) }
            expectState {
                copy(
                    isLoading = false,
                    title = "할인",
                    content = "20% 세일",
                    startDate = "2026-01-01",
                    endDate = "2026-01-31",
                    existingImageUrls = listOf("a.jpg")
                )
            }
        }
    }

    @Test
    fun initEventNotFoundEmitsNotFoundRes() = runTest {
        val (vm, _) = newViewModel(events = emptyList())
        vm.test(this, WriteEventState()) {
            containerHost.init(storeId = "storeA", eventId = "999")
            expectState { copy(storeId = "storeA", eventId = "999", isEditMode = true) }
            expectState { copy(isLoading = true, errorMessage = "", errorMessageRes = null) }
            expectState {
                copy(
                    isLoading = false,
                    errorMessage = "",
                    errorMessageRes = Res.string.event_editor_error_not_found
                )
            }
        }
    }

    @Test
    fun submitBlankTitleEmitsTitleRequiredRes() = runTest {
        val (vm, _) = newViewModel()
        vm.test(this, WriteEventState(storeId = "storeA")) {
            containerHost.submit()
            expectState {
                copy(errorMessage = "", errorMessageRes = Res.string.event_editor_error_title_required)
            }
        }
    }

    @Test
    fun submitBlankContentEmitsContentRequiredRes() = runTest {
        val (vm, _) = newViewModel()
        vm.test(this, WriteEventState(storeId = "storeA", title = "타이틀")) {
            containerHost.submit()
            expectState {
                copy(errorMessage = "", errorMessageRes = Res.string.event_editor_error_content_required)
            }
        }
    }

    @Test
    fun submitMissingPeriodEmitsPeriodRequiredRes() = runTest {
        val (vm, _) = newViewModel()
        vm.test(this, WriteEventState(storeId = "storeA", title = "T", content = "C")) {
            containerHost.submit()
            expectState {
                copy(errorMessage = "", errorMessageRes = Res.string.event_editor_error_period_required)
            }
        }
    }

    @Test
    fun submitInvalidDateEmitsPeriodInvalidRes() = runTest {
        val (vm, _) = newViewModel()
        vm.test(
            this,
            WriteEventState(
                storeId = "storeA",
                title = "T",
                content = "C",
                startDate = "not-a-date",
                endDate = "also-not"
            )
        ) {
            containerHost.submit()
            expectState {
                copy(errorMessage = "", errorMessageRes = Res.string.event_editor_error_period_invalid)
            }
        }
    }

    @Test
    fun submitRegistersNewEventAndPostsNavigateBack() = runTest {
        val (vm, repo) = newViewModel()
        vm.test(
            this,
            WriteEventState(
                storeId = "storeA",
                title = "이벤트",
                content = "내용",
                startDate = "2026-01-01",
                endDate = "2026-01-31"
            )
        ) {
            containerHost.submit()
            expectState { copy(isLoading = true, errorMessage = "", errorMessageRes = null) }
            expectState { copy(isLoading = false) }
            expectSideEffect(WriteEventSideEffect.NavigateBack)
            assertTrue(repo.registerEventCalls.contains("storeA"))
        }
    }

    @Test
    fun deleteEventPostsNavigateBack() = runTest {
        val (vm, repo) = newViewModel()
        vm.test(this, WriteEventState(storeId = "storeA", eventId = "5")) {
            containerHost.deleteEvent()
            expectState { copy(isLoading = true, errorMessage = "", errorMessageRes = null) }
            expectState { copy(isLoading = false) }
            expectSideEffect(WriteEventSideEffect.NavigateBack)
            assertTrue(repo.deleteEventCalls.contains("storeA" to "5"))
        }
    }
}
