@file:OptIn(kotlin.time.ExperimentalTime::class)

package `in`.koreatech.business.feature.store.event.editor

import androidx.lifecycle.ViewModel
import `in`.koreatech.business.domain.model.StoreEvent
import `in`.koreatech.business.domain.usecase.owner.UploadFileUseCase
import `in`.koreatech.business.domain.usecase.store.DeleteEventUseCase
import `in`.koreatech.business.domain.usecase.store.GetStoreEventsUseCase
import `in`.koreatech.business.domain.usecase.store.RegisterEventUseCase
import `in`.koreatech.business.domain.usecase.store.UpdateEventUseCase
import `in`.koreatech.business.platform.PlatformFile
import `in`.koreatech.business.ui.util.BusinessValidators
import koreatech.business.designsystem.resources.Res
import koreatech.business.designsystem.resources.event_editor_error_content_required
import koreatech.business.designsystem.resources.event_editor_error_not_found
import koreatech.business.designsystem.resources.event_editor_error_period_invalid
import koreatech.business.designsystem.resources.event_editor_error_period_required
import koreatech.business.designsystem.resources.event_editor_error_title_required
import kotlin.time.Clock
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.blockingIntent
import org.orbitmvi.orbit.viewmodel.container

class WriteEventViewModel(
    private val getStoreEventsUseCase: GetStoreEventsUseCase,
    private val registerEventUseCase: RegisterEventUseCase,
    private val updateEventUseCase: UpdateEventUseCase,
    private val deleteEventUseCase: DeleteEventUseCase,
    private val uploadFileUseCase: UploadFileUseCase
) : ViewModel(),
    ContainerHost<WriteEventState, WriteEventSideEffect> {
    override val container = container<WriteEventState, WriteEventSideEffect>(WriteEventState())

    fun init(storeId: String, eventId: String? = null) {
        intent {
            reduce { state.copy(storeId = storeId, eventId = eventId, isEditMode = eventId != null) }
            if (eventId == null) return@intent
            reduce { state.copy(isLoading = true, errorMessage = "", errorMessageRes = null) }
            getStoreEventsUseCase(storeId)
                .onSuccess { events -> applyLoadedEvent(events, eventId) }
                .onFailure { showLoadError(it.message.orEmpty()) }
        }
    }

    private fun applyLoadedEvent(events: List<StoreEvent>, eventId: String) = intent {
        val target = events.firstOrNull { it.id.toString() == eventId }
        if (target != null) {
            reduce {
                state.copy(
                    isLoading = false,
                    title = target.title,
                    content = target.content,
                    startDate = target.startDate.take(10),
                    endDate = target.endDate.take(10),
                    existingImageUrls = target.thumbnailUrls
                )
            }
        } else {
            reduce {
                state.copy(
                    isLoading = false,
                    errorMessage = "",
                    errorMessageRes = Res.string.event_editor_error_not_found
                )
            }
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

    fun onTitleChanged(value: String) = blockingIntent { reduce { state.copy(title = value) } }
    fun onContentChanged(value: String) = blockingIntent { reduce { state.copy(content = value) } }
    fun onStartDateChanged(value: String) = blockingIntent { reduce { state.copy(startDate = value) } }
    fun onEndDateChanged(value: String) = blockingIntent { reduce { state.copy(endDate = value) } }

    fun addImage(file: PlatformFile) {
        intent(registerIdling = false) { reduce { state.copy(images = state.images + file) } }
    }

    fun removeImage(index: Int) {
        intent(registerIdling = false) {
            val updated = state.images.toMutableList()
            if (index in updated.indices) updated.removeAt(index)
            reduce { state.copy(images = updated) }
        }
    }

    fun removeExistingImage(index: Int) {
        intent(registerIdling = false) {
            val updated = state.existingImageUrls.toMutableList()
            if (index in updated.indices) updated.removeAt(index)
            reduce { state.copy(existingImageUrls = updated) }
        }
    }

    fun submit() {
        intent {
            if (state.title.isBlank()) {
                reduce {
                    state.copy(errorMessage = "", errorMessageRes = Res.string.event_editor_error_title_required)
                }
                return@intent
            }
            if (state.content.isBlank()) {
                reduce {
                    state.copy(errorMessage = "", errorMessageRes = Res.string.event_editor_error_content_required)
                }
                return@intent
            }
            if (state.startDate.isBlank() || state.endDate.isBlank()) {
                reduce {
                    state.copy(errorMessage = "", errorMessageRes = Res.string.event_editor_error_period_required)
                }
                return@intent
            }
            if (!BusinessValidators.isValidDate(state.startDate) || !BusinessValidators.isValidDate(state.endDate)) {
                reduce {
                    state.copy(errorMessage = "", errorMessageRes = Res.string.event_editor_error_period_invalid)
                }
                return@intent
            }
            reduce { state.copy(isLoading = true, errorMessage = "", errorMessageRes = null) }
            uploadImagesAndSubmit()
        }
    }

    private fun uploadImagesAndSubmit() = intent {
        val storeId = state.storeId ?: return@intent
        val uploadedUrls = mutableListOf<String>()
        for (img in state.images) {
            val uploadResult = uploadFileUseCase(img.name, img.mimeType, img.bytes)
            uploadResult.fold(
                onSuccess = { uploadedUrls.add(it) },
                onFailure = {
                    showSubmitError(it.message.orEmpty())
                    return@intent
                }
            )
        }
        val allImageUrls = state.existingImageUrls + uploadedUrls
        val eventId = state.eventId
        val saveResult = if (state.isEditMode && eventId != null) {
            updateEventUseCase(storeId, eventId, state.title, state.content, allImageUrls, state.startDate, state.endDate)
        } else {
            registerEventUseCase(storeId, state.title, state.content, allImageUrls, state.startDate, state.endDate)
        }
        saveResult
            .onSuccess { completeSubmit() }
            .onFailure { showSubmitError(it.message.orEmpty()) }
    }

    private fun completeSubmit() = intent {
        reduce { state.copy(isLoading = false) }
        postSideEffect(WriteEventSideEffect.NavigateBack)
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

    fun deleteEvent() {
        intent {
            val storeId = state.storeId ?: return@intent
            val eventId = state.eventId ?: return@intent
            reduce { state.copy(isLoading = true, errorMessage = "", errorMessageRes = null) }
            deleteEventUseCase(storeId, eventId)
                .onSuccess { completeSubmit() }
                .onFailure { showSubmitError(it.message.orEmpty()) }
        }
    }

    fun applyDurationPreset(days: Int) = intent(registerIdling = false) {
        val base = state.startDate.ifBlank { todayIsoString() }
        reduce { state.copy(startDate = base, endDate = addDaysToIsoDate(base, days)) }
    }

    fun clearError() {
        intent(registerIdling = false) { reduce { state.copy(errorMessage = "", errorMessageRes = null) } }
    }
}

private fun todayIsoString(): String {
    val epochDay = (Clock.System.now().toEpochMilliseconds() / 86_400_000L).toInt()
    return epochDayToIso(epochDay)
}

private fun addDaysToIsoDate(isoDate: String, days: Int): String {
    val epochDay = isoToEpochDay(isoDate) ?: (Clock.System.now().toEpochMilliseconds() / 86_400_000L).toInt()
    return epochDayToIso(epochDay + days)
}

private fun isoToEpochDay(iso: String): Int? {
    val parts = iso.split("-")
    if (parts.size != 3) return null
    val y = parts[0].toIntOrNull() ?: return null
    val m = parts[1].toIntOrNull() ?: return null
    val d = parts[2].toIntOrNull() ?: return null
    return dateToEpochDay(y, m, d)
}

private fun dateToEpochDay(year: Int, month: Int, day: Int): Int {
    var y = year
    var m = month
    if (m <= 2) {
        y -= 1
        m += 9
    } else {
        m -= 3
    }
    val era = if (y >= 0) y / 400 else (y - 399) / 400
    val yoe = y - era * 400
    val doy = (153 * m + 2) / 5 + day - 1
    val doe = yoe * 365 + yoe / 4 - yoe / 100 + doy
    return era * 146097 + doe - 719468
}

private fun epochDayToIso(epochDay: Int): String {
    var z = epochDay + 719468
    val era = if (z >= 0) z / 146097 else (z - 146096) / 146097
    val doe = z - era * 146097
    val yoe = (doe - doe / 1460 + doe / 36524 - doe / 146096) / 365
    val y = yoe + era * 400
    val doy = doe - (365 * yoe + yoe / 4 - yoe / 100)
    val mp = (5 * doy + 2) / 153
    val d = doy - (153 * mp + 2) / 5 + 1
    val m = if (mp < 10) mp + 3 else mp - 9
    val finalY = if (m <= 2) y + 1 else y
    return finalY.toString().padStart(4, '0') + "-" + m.toString().padStart(2, '0') + "-" + d.toString().padStart(2, '0')
}
