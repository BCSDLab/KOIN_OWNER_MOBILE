package `in`.koreatech.business.platform

import platform.Foundation.NSBundle

actual fun getAppVersion(): String = NSBundle.mainBundle.infoDictionary?.get("CFBundleShortVersionString") as? String ?: "99.99.99"
