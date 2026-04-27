package `in`.koreatech.business.data.model.store.response

import `in`.koreatech.business.domain.model.StoreEvent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StoreEventsResponse(
    @SerialName("events") val events: List<StoreEventItemResponse>? = null
) {
    fun toDomainList(): List<StoreEvent> = events.orEmpty().mapNotNull { it.toDomainOrNull() }
}

@Serializable
data class StoreEventItemResponse(
    @SerialName("event_id") val id: Int? = null,
    @SerialName("shop_id") val shopId: Int? = null,
    @SerialName("shop_name") val shopName: String? = null,
    @SerialName("title") val title: String? = null,
    @SerialName("content") val content: String? = null,
    @SerialName("thumbnail_images") val thumbnailImages: List<String>? = null,
    @SerialName("start_date") val startDate: String? = null,
    @SerialName("end_date") val endDate: String? = null
) {
    fun toDomainOrNull(): StoreEvent? {
        val id = id ?: return null
        return StoreEvent(
            id = id,
            shopId = shopId ?: 0,
            title = title.orEmpty(),
            content = content.orEmpty(),
            thumbnailUrls = thumbnailImages.orEmpty(),
            startDate = startDate.orEmpty(),
            endDate = endDate.orEmpty()
        )
    }
}
