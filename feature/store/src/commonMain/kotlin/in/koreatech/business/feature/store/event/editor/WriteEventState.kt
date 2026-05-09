package `in`.koreatech.business.feature.store.event.editor

import `in`.koreatech.business.platform.PlatformFile
import org.jetbrains.compose.resources.StringResource

data class WriteEventState(
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
    val errorMessage: String = "",
    val errorMessageRes: StringResource? = null
)
