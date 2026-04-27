package `in`.koreatech.business

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class BusinessApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        instance = this

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
