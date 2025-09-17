package `in`.koreatech.business

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class BusinessApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin(
            businessAppDeclaration {
                androidContext(this@BusinessApplication)
            }
        )
    }
}
