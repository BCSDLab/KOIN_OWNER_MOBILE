package `in`.koreatech.business.data.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import org.koin.core.scope.Scope

@Module
expect class DataStoreModule() {
    @Single
    fun provideDataStore(scope: Scope): DataStore<Preferences>
}

@Module
class EncryptedDataStoreModule {
    @Single
    fun provideEncryptedDataStore(scope: Scope): EncryptedDataStore = EncryptedDataStore(scope)
}

expect class EncryptedDataStore(scope: Scope) {
    fun createData(key: String, value: String)
    fun readData(key: String): String?
    fun deleteData(key: String)
}
