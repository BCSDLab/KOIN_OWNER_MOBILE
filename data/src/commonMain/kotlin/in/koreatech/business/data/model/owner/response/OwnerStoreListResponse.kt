package `in`.koreatech.business.data.model.owner.response

import `in`.koreatech.business.domain.model.owner.OwnerStore
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OwnerStoreListResponse(
    @SerialName("shops")
    val shops: List<OwnerStoreItemResponse> = emptyList()
) {
    fun toDomainList(): List<OwnerStore> = shops.mapNotNull { shop ->
        val uid = shop.uid
        val name = shop.name
        if (uid == null || name == null) {
            null
        } else {
            OwnerStore(
                uid = uid,
                name = name,
                isEvent = shop.isEvent ?: false
            )
        }
    }
}

@Serializable
data class OwnerStoreItemResponse(
    @SerialName("id")
    val uid: Int? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("is_event")
    val isEvent: Boolean? = null
)
