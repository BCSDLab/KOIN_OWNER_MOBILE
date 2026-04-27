package `in`.koreatech.business.data.model.store.response

import `in`.koreatech.business.domain.model.OperatingTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OperatingTimeResponse(
    @SerialName("day_of_week") val dayOfWeek: String? = null,
    @SerialName("open_time") val openTime: String? = null,
    @SerialName("close_time") val closeTime: String? = null,
    @SerialName("closed") val closed: Boolean? = null
) {
    fun toDomain(): OperatingTime = OperatingTime(
        dayOfWeek = dayOfWeek.orEmpty(),
        openTime = openTime,
        closeTime = closeTime,
        isClosed = closed ?: false
    )
}
