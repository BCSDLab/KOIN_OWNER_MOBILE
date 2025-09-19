package `in`.koreatech.business.data.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
expect class DataStoreModule() {
    @Single
    fun provideDataStore(scope: org.koin.core.scope.Scope): DataStorePlatformModule
}

interface DataStorePlatformModule {
    fun provideDataStore(): DataStore<Preferences>
}
