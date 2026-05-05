package `in`.koreatech.business

import android.app.Application
import `in`.koreatech.business.platform.AndroidContextHolder
import `in`.koreatech.business.platform.AppVersionHolder
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class BusinessApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        Napier.base(DebugAntilog())

        instance = this
        AndroidContextHolder.applicationContext = applicationContext
        AppVersionHolder.value = BuildConfig.VERSION_NAME

        startKoin(
            businessAppDeclaration {
                androidContext(this@BusinessApplication)
            }
        )
    }

    companion object {
        lateinit var instance: BusinessApplication
            private set
    }
}
