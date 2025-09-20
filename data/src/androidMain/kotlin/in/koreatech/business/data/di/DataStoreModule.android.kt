package `in`.koreatech.business.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import `in`.koreatech.business.data.utils.createDataStore
import `in`.koreatech.business.data.utils.dataStoreFileName
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
actual class DataStoreModule {
    @Single
    actual fun provideDataStore(scope: org.koin.core.scope.Scope): DataStorePlatformModule = DataStoreAndroidModule(scope)
}

class DataStoreAndroidModule(scope: org.koin.core.scope.Scope) : DataStorePlatformModule {
    val context: Context = scope.get()
    override fun provideDataStore(): DataStore<Preferences> = createDataStore(
        producePath = { context.filesDir.resolve(dataStoreFileName).absolutePath }
    )
}
