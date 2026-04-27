package `in`.koreatech.business.data.model.store.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OperatingTimeRequest(
    @SerialName("day_of_week") val dayOfWeek: String,
    @SerialName("open_time") val openTime: String,
    @SerialName("close_time") val closeTime: String,
    @SerialName("closed") val closed: Boolean
)
