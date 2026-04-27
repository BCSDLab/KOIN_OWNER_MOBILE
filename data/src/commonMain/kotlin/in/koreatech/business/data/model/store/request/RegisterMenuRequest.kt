package `in`.koreatech.business.data.model.store.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegisterMenuRequest(
    @SerialName("name") val name: String,
    @SerialName("is_single") val isSingle: Boolean,
    @SerialName("single_price") val singlePrice: Int? = null,
    @SerialName("option_prices") val optionPrices: List<MenuOptionPriceRequest>? = null,
    @SerialName("description") val description: String,
    @SerialName("image_urls") val imageUrls: List<String>,
    @SerialName("category_ids") val categoryIds: List<Int>
)

@Serializable
data class MenuOptionPriceRequest(
    @SerialName("option") val option: String,
    @SerialName("price") val price: Int
)
