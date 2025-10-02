package `in`.koreatech.business

import android.app.Application
import com.google.firebase.analytics.FirebaseAnalytics
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class BusinessApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        FirebaseAnalytics.getInstance(this@BusinessApplication)

        startKoin(
            businessAppDeclaration {
                androidContext(this@BusinessApplication)
            }
        )
    }
}
