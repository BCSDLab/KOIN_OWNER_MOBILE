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
    @OptIn(ExperimentalForeignApi::class)
    actual fun provideDataStore(scope: Scope): DataStore<Preferences> = createDataStore(
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

actual class EncryptedDataStore actual constructor(scope: Scope) {
    actual fun createData(key: String, value: String) {
        iosKeyChainProvider?.createData(key, value)
    }

    actual fun readData(key: String): String? = iosKeyChainProvider?.readData(key)

    actual fun deleteData(key: String) {
        iosKeyChainProvider?.deleteData(key)
    }
}

interface IOSKeyChainProvider {
    fun createData(key: String, value: String)
    fun readData(key: String): String?
    fun deleteData(key: String)
}

private var iosKeyChainProvider: IOSKeyChainProvider? = null

fun setIOSKeyChainProvider(provider: IOSKeyChainProvider) {
    iosKeyChainProvider = provider
}
