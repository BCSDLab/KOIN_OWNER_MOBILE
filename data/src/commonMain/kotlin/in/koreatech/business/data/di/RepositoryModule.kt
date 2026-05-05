package `in`.koreatech.business.data.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import `in`.koreatech.business.data.repository.ActiveStoreRepositoryImpl
import `in`.koreatech.business.data.repository.AppPreferencesRepositoryImpl
import `in`.koreatech.business.data.repository.AuthRepositoryImpl
import `in`.koreatech.business.data.repository.OwnerRepositoryImpl
import `in`.koreatech.business.data.repository.StoreRepositoryImpl
import `in`.koreatech.business.data.repository.TokenRepositoryImpl
import `in`.koreatech.business.data.source.local.TokenLocalDataSource
import `in`.koreatech.business.data.source.remote.OwnerRemoteDataSource
import `in`.koreatech.business.domain.repository.ActiveStoreRepository
import `in`.koreatech.business.domain.repository.AppPreferencesRepository
import `in`.koreatech.business.domain.repository.AuthRepository
import `in`.koreatech.business.domain.repository.OwnerRepository
import `in`.koreatech.business.domain.repository.StoreRepository
import `in`.koreatech.business.domain.repository.TokenRepository

class RepositoryModule {
    fun provideAuthRepository(
        ownerRemoteDataSource: OwnerRemoteDataSource,
        tokenRepository: TokenRepository
    ): AuthRepository = AuthRepositoryImpl(ownerRemoteDataSource, tokenRepository)

    fun provideStoreRepository(
        ownerRemoteDataSource: OwnerRemoteDataSource
    ): StoreRepository = StoreRepositoryImpl(ownerRemoteDataSource)

    fun provideOwnerRepository(
        ownerRemoteDataSource: OwnerRemoteDataSource
    ): OwnerRepository = OwnerRepositoryImpl(ownerRemoteDataSource)

    fun provideTokenRepository(
        tokenLocalDataSource: TokenLocalDataSource
    ): TokenRepository = TokenRepositoryImpl(tokenLocalDataSource)

    fun provideAppPreferencesRepository(
        dataStore: DataStore<Preferences>
    ): AppPreferencesRepository = AppPreferencesRepositoryImpl(dataStore)

    fun provideActiveStoreRepository(
        encryptedDataStore: EncryptedDataStore
    ): ActiveStoreRepository = ActiveStoreRepositoryImpl(encryptedDataStore)
}
