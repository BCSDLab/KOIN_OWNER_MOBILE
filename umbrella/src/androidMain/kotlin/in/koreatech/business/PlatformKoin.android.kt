package `in`.koreatech.business

import `in`.koreatech.business.platform.AndroidContextHolder
import org.koin.android.ext.koin.androidContext
import org.koin.core.KoinApplication

internal actual fun KoinApplication.configurePlatformContext() {
    AndroidContextHolder.applicationContext?.let { androidContext(it) }
}
