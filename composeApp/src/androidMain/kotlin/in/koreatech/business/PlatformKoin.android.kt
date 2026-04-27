package `in`.koreatech.business

import org.koin.android.ext.koin.androidContext
import org.koin.core.KoinApplication

internal actual fun KoinApplication.configurePlatformContext() {
    androidContext(BusinessApplication.instance)
}
