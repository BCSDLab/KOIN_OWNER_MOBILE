@file:OptIn(kotlin.time.ExperimentalTime::class)

package `in`.koreatech.business.feature.store.event.editor

import androidx.lifecycle.ViewModel
import `in`.koreatech.business.domain.repository.OwnerRepository
import `in`.koreatech.business.domain.repository.StoreRepository
import `in`.koreatech.business.platform.PlatformFile
import `in`.koreatech.business.ui.util.BusinessValidators
import `in`.koreatech.business.ui.util.blockingIntent
import kotlin.time.Clock
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

class WriteEventViewModel(
    private val storeRepository: StoreRepository,
    private val ownerRepository: OwnerRepository
) : ViewModel(),
    ContainerHost<WriteEventUiState, WriteEventSideEffect> {
    override val container = container<WriteEventUiState, WriteEventSideEffect>(WriteEventUiState())

    fun init(storeId: String, eventId: String? = null) {
        intent {
            reduce { state.copy(storeId = storeId, eventId = eventId, isEditMode = eventId != null) }
            if (eventId == null) return@intent
            reduce { state.copy(isLoading = true, errorMessage = "") }
            try {
                val events = storeRepository.getStoreEvents(storeId)
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
                    reduce { state.copy(isLoading = false, errorMessage = "이벤트를 찾을 수 없습니다.") }
                }
            } catch (e: Exception) {
                reduce { state.copy(isLoading = false, errorMessage = e.message.orEmpty()) }
            }
        }
    }

    fun onTitleChanged(value: String) = blockingIntent {
        reduce { state.copy(title = value) }
    }

    fun onContentChanged(value: String) = blockingIntent {
        reduce { state.copy(content = value) }
    }

    fun onStartDateChanged(value: String) = blockingIntent {
        reduce { state.copy(startDate = value) }
    }

    fun onEndDateChanged(value: String) = blockingIntent {
        reduce { state.copy(endDate = value) }
    }

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
                reduce { state.copy(errorMessage = "제목을 입력해주세요.") }
                return@intent
            }
            if (state.content.isBlank()) {
                reduce { state.copy(errorMessage = "내용을 입력해주세요.") }
                return@intent
            }
            if (state.startDate.isBlank() || state.endDate.isBlank()) {
                reduce { state.copy(errorMessage = "이벤트 기간을 입력해주세요.") }
                return@intent
            }
            if (!BusinessValidators.isValidDate(state.startDate) || !BusinessValidators.isValidDate(state.endDate)) {
                reduce { state.copy(errorMessage = "이벤트 기간을 올바르게 선택해주세요.") }
                return@intent
            }

            reduce { state.copy(isLoading = true, errorMessage = "") }
            try {
                val uploadedUrls = state.images.map { file ->
                    ownerRepository.uploadFile(file.name, file.mimeType, file.bytes)
                }
                val allImageUrls = state.existingImageUrls + uploadedUrls
                val storeId = state.storeId ?: return@intent
                val eventId = state.eventId
                if (state.isEditMode && eventId != null) {
                    storeRepository.updateEvent(
                        storeId = storeId,
                        eventId = eventId,
                        title = state.title,
                        content = state.content,
                        imageUrls = allImageUrls,
                        startDate = state.startDate,
                        endDate = state.endDate
                    )
                } else {
                    storeRepository.registerEvent(
                        storeId = storeId,
                        title = state.title,
                        content = state.content,
                        imageUrls = allImageUrls,
                        startDate = state.startDate,
                        endDate = state.endDate
                    )
                }
                reduce { state.copy(isLoading = false) }
                postSideEffect(WriteEventSideEffect.NavigateBack)
            } catch (exception: Exception) {
                reduce {
                    state.copy(isLoading = false, errorMessage = exception.message.orEmpty())
                }
            }
        }
    }

    fun deleteEvent() {
        intent {
            val storeId = state.storeId ?: return@intent
            val eventId = state.eventId ?: return@intent
            reduce { state.copy(isLoading = true, errorMessage = "") }
            try {
                storeRepository.deleteEvent(storeId, eventId)
                reduce { state.copy(isLoading = false) }
                postSideEffect(WriteEventSideEffect.NavigateBack)
            } catch (exception: Exception) {
                reduce { state.copy(isLoading = false, errorMessage = exception.message.orEmpty()) }
            }
        }
    }

    fun applyDurationPreset(days: Int) = intent(registerIdling = false) {
        val base = state.startDate.ifBlank { todayIsoString() }
        val endDate = addDaysToIsoDate(base, days)
        reduce { state.copy(endDate = endDate) }
    }

    fun clearError() {
        intent(registerIdling = false) { reduce { state.copy(errorMessage = "") } }
    }
}

// ---------------------------------------------------------------------------
// Pure-Kotlin KMP date helpers — no java.time, no kotlinx-datetime dependency
// ---------------------------------------------------------------------------

/** Returns today's date as "yyyy-MM-dd" using epoch-day arithmetic. */
private fun todayIsoString(): String {
    // currentTimeMillis() is available in common Kotlin
    val epochDay = (Clock.System.now().toEpochMilliseconds() / 86_400_000L).toInt()
    return epochDayToIso(epochDay)
}

/**
 * Adds [days] to an ISO date string "yyyy-MM-dd" and returns the result in the same format.
 * Falls back to today if [isoDate] is malformed.
 */
private fun addDaysToIsoDate(isoDate: String, days: Int): String {
    val epochDay = isoToEpochDay(isoDate) ?: (Clock.System.now().toEpochMilliseconds() / 86_400_000L).toInt()
    return epochDayToIso(epochDay + days)
}

/** Converts "yyyy-MM-dd" to days since Unix epoch (1970-01-01). Returns null on parse failure. */
private fun isoToEpochDay(iso: String): Int? {
    val parts = iso.split("-")
    if (parts.size != 3) return null
    val y = parts[0].toIntOrNull() ?: return null
    val m = parts[1].toIntOrNull() ?: return null
    val d = parts[2].toIntOrNull() ?: return null
    return dateToEpochDay(y, m, d)
}

/** Converts a proleptic Gregorian date to days since Unix epoch (1970-01-01). */
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

/** Converts days since Unix epoch (1970-01-01) back to "yyyy-MM-dd". */
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

data class WriteEventUiState(
    val isLoading: Boolean = false,
    val storeId: String? = null,
    val eventId: String? = null,
    val isEditMode: Boolean = false,
    val title: String = "",
    val content: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val existingImageUrls: List<String> = emptyList(),
    val images: List<PlatformFile> = emptyList(),
    val errorMessage: String = ""
)
