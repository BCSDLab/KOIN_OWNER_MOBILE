package `in`.koreatech.business.data.model.store.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegisterStoreRequest(
    @SerialName("name") val name: String,
    @SerialName("address") val address: String,
    @SerialName("main_category_id") val mainCategoryId: Int,
    @SerialName("category_ids") val categoryIds: List<Int>,
    @SerialName("phone") val phone: String,
    @SerialName("delivery") val delivery: Boolean,
    @SerialName("delivery_price") val deliveryPrice: Int,
    @SerialName("pay_card") val payCard: Boolean,
    @SerialName("pay_bank") val payBank: Boolean,
    @SerialName("description") val description: String,
    @SerialName("image_urls") val imageUrls: List<String> = emptyList(),
    @SerialName("open") val open: List<OperatingTimeRequest>
)
