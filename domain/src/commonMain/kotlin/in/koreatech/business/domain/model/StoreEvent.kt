package `in`.koreatech.business.domain.model

data class StoreEvent(
    val id: Int,
    val shopId: Int,
    val title: String,
    val content: String,
    val thumbnailUrls: List<String>,
    val startDate: String,
    val endDate: String
)
