package `in`.koreatech.business.domain.model

data class OperatingTime(
    val dayOfWeek: String,
    val openTime: String?,
    val closeTime: String?,
    val isClosed: Boolean = false
)

val defaultOperatingTimes: List<OperatingTime> = listOf(
    OperatingTime(dayOfWeek = "MONDAY", openTime = "00:00", closeTime = "00:00"),
    OperatingTime(dayOfWeek = "TUESDAY", openTime = "00:00", closeTime = "00:00"),
    OperatingTime(dayOfWeek = "WEDNESDAY", openTime = "00:00", closeTime = "00:00"),
    OperatingTime(dayOfWeek = "THURSDAY", openTime = "00:00", closeTime = "00:00"),
    OperatingTime(dayOfWeek = "FRIDAY", openTime = "00:00", closeTime = "00:00"),
    OperatingTime(dayOfWeek = "SATURDAY", openTime = "00:00", closeTime = "00:00"),
    OperatingTime(dayOfWeek = "SUNDAY", openTime = "00:00", closeTime = "00:00"),
)

val dayOfWeekLabels: Map<String, String> = mapOf(
    "MONDAY" to "월",
    "TUESDAY" to "화",
    "WEDNESDAY" to "수",
    "THURSDAY" to "목",
    "FRIDAY" to "금",
    "SATURDAY" to "토",
    "SUNDAY" to "일",
)
