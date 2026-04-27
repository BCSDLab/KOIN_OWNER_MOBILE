package `in`.koreatech.business.data.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import `in`.koreatech.business.data.api.OwnerApi
import `in`.koreatech.business.data.api.PublicApi
import `in`.koreatech.business.data.api.auth.OwnerAuthApi
import `in`.koreatech.business.data.source.local.TokenLocalDataSource
import `in`.koreatech.business.data.source.remote.OwnerRemoteDataSource
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
class DataSourceModule {
    @Single
    fun provideOwnerRemoteDataSource(ownerApi: OwnerApi, ownerAuthApi: OwnerAuthApi, publicApi: PublicApi): OwnerRemoteDataSource = OwnerRemoteDataSource(ownerApi, ownerAuthApi, publicApi)

    @Single
    fun provideTokenLocalDataSource(dataStore: DataStore<Preferences>): TokenLocalDataSource = TokenLocalDataSource(dataStore)
}
