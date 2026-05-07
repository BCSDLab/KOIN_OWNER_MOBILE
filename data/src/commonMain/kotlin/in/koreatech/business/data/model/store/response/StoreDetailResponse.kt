package `in`.koreatech.business.data.model.store.response

import `in`.koreatech.business.domain.model.StoreDetail
import `in`.koreatech.business.domain.model.defaultOperatingTimes
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StoreDetailResponse(
    @SerialName("id") val id: Int? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("phone") val phone: String? = null,
    @SerialName("address") val address: String? = null,
    @SerialName("description") val description: String? = null,
    @SerialName("open_time") val openTime: String? = null,
    @SerialName("close_time") val closeTime: String? = null,
    @SerialName("is_event") val isEvent: Boolean? = null,
    @SerialName("category_ids") val categoryIds: List<Int>? = null,
    @SerialName("delivery") val delivery: Boolean? = null,
    @SerialName("pay_card") val payCard: Boolean? = null,
    @SerialName("pay_bank") val payBank: Boolean? = null,
    @SerialName("open") val open: List<OperatingTimeResponse>? = null,
    @SerialName("image_urls") val imageUrls: List<String>? = null
) {
    fun toDomain(): StoreDetail {
        val serverTimes = open.orEmpty()
        val operatingTimes = defaultOperatingTimes.map { default ->
            val serverDay = serverTimes.firstOrNull { it.dayOfWeek == default.dayOfWeek }
            serverDay?.toDomain() ?: default
        }
        return StoreDetail(
            id = id ?: 0,
            name = name.orEmpty(),
            phone = phone.orEmpty(),
            address = address.orEmpty(),
            description = description.orEmpty(),
            openTime = openTime.orEmpty(),
            closeTime = closeTime.orEmpty(),
            isEvent = isEvent ?: false,
            categoryIds = categoryIds.orEmpty(),
            isDelivery = delivery ?: false,
            isCard = payCard ?: false,
            isBank = payBank ?: false,
            operatingTimes = operatingTimes,
            imageUrls = imageUrls.orEmpty()
        )
    }
}
