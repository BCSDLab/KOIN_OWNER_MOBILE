package `in`.koreatech.business.data.model.store.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateStoreInfoRequest(
    @SerialName("name") val name: String,
    @SerialName("phone") val phone: String,
    @SerialName("address") val address: String,
    @SerialName("description") val description: String,
    @SerialName("main_category_id") val mainCategoryId: Int,
    @SerialName("category_ids") val categoryIds: List<Int>,
    @SerialName("delivery") val delivery: Boolean,
    @SerialName("delivery_price") val deliveryPrice: Int,
    @SerialName("pay_card") val payCard: Boolean,
    @SerialName("pay_bank") val payBank: Boolean,
    @SerialName("image_urls") val imageUrls: List<String>,
    @SerialName("open") val open: List<OperatingTimeRequest>,
    @SerialName("bank") val bank: String? = null,
    @SerialName("account_number") val accountNumber: String? = null
)
