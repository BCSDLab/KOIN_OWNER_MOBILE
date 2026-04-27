package `in`.koreatech.business

import org.koin.core.KoinApplication

internal actual fun KoinApplication.configurePlatformContext() {
    // no platform context needed on JVM desktop
}
