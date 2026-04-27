package `in`.koreatech.business.data.model.store.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegisterEventRequest(
    @SerialName("title") val title: String,
    @SerialName("content") val content: String,
    @SerialName("thumbnail_images") val thumbnailImages: List<String>,
    @SerialName("start_date") val startDate: String,
    @SerialName("end_date") val endDate: String
)
