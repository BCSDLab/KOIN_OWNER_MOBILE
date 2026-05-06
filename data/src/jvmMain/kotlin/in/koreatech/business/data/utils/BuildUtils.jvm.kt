package `in`.koreatech.business.data.utils

actual fun isDebug(): Boolean = System.getProperty("app.debug", "false").toBoolean()
