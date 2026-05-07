package `in`.koreatech.business.domain.model

data class StoreDetail(
    val id: Int,
    val name: String,
    val phone: String,
    val address: String,
    val description: String,
    val openTime: String,
    val closeTime: String,
    val isEvent: Boolean,
    val categoryIds: List<Int>,
    val isDelivery: Boolean = false,
    val isCard: Boolean = false,
    val isBank: Boolean = false,
    val operatingTimes: List<OperatingTime> = defaultOperatingTimes,
    val imageUrls: List<String> = emptyList()
)
