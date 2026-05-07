package `in`.koreatech.business.platform

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

actual fun getCurrentDateString(): String = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).format(Date())
