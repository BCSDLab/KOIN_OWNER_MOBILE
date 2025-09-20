package `in`.koreatech.business.data.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import `in`.koreatech.business.data.utils.createDataStore
import `in`.koreatech.business.data.utils.dataStoreFileName
import kotlinx.cinterop.ExperimentalForeignApi
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import org.koin.core.scope.Scope
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

@Module
actual class DataStoreModule {
    @Single
    actual fun provideDataStore(scope: Scope): DataStorePlatformModule = DataStoreIOSModule()
}

class DataStoreIOSModule : DataStorePlatformModule {
    @OptIn(ExperimentalForeignApi::class)
    override fun provideDataStore(): DataStore<Preferences> = createDataStore(
        producePath = {
            val documentDirectory: NSURL? = NSFileManager.defaultManager.URLForDirectory(
                directory = NSDocumentDirectory,
                inDomain = NSUserDomainMask,
                appropriateForURL = null,
                create = false,
                error = null
            )
            requireNotNull(documentDirectory).path + "/$dataStoreFileName"
        }
    )
}
