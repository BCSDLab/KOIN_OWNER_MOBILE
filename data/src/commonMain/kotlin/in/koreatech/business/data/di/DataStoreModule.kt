package `in`.koreatech.business.data.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import org.koin.core.scope.Scope

expect class DataStoreModule() {
    fun provideDataStore(scope: Scope): DataStore<Preferences>
}

expect class EncryptedDataStore(scope: Scope) {
    fun createData(key: String, value: String)
    fun readData(key: String): String?
    fun deleteData(key: String)
}
