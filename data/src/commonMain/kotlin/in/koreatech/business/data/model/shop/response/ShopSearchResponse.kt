package `in`.koreatech.business.data.model.shop.response

import `in`.koreatech.business.domain.model.signup.ShopSearchResult
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ShopSearchResponse(
    @SerialName("shops")
    val shops: List<ShopItem> = emptyList()
)

@Serializable
data class ShopItem(
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String,
    @SerialName("phone")
    val phone: String? = null,
    @SerialName("address")
    val address: String? = null
)

fun ShopItem.toDomain() = ShopSearchResult(
    id = id,
    name = name,
    phone = phone.orEmpty(),
    address = address.orEmpty()
)
