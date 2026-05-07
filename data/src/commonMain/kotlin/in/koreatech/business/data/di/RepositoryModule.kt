package `in`.koreatech.business.data.di

import `in`.koreatech.business.data.repository.ActiveStoreRepositoryImpl
import `in`.koreatech.business.data.repository.AppPreferencesRepositoryImpl
import `in`.koreatech.business.data.repository.AuthRepositoryImpl
import `in`.koreatech.business.data.repository.OwnerRepositoryImpl
import `in`.koreatech.business.data.repository.StoreRepositoryImpl
import `in`.koreatech.business.data.repository.TokenRepositoryImpl
import `in`.koreatech.business.domain.repository.ActiveStoreRepository
import `in`.koreatech.business.domain.repository.AppPreferencesRepository
import `in`.koreatech.business.domain.repository.AuthRepository
import `in`.koreatech.business.domain.repository.OwnerRepository
import `in`.koreatech.business.domain.repository.StoreRepository
import `in`.koreatech.business.domain.repository.TokenRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
    single<StoreRepository> { StoreRepositoryImpl(get()) }
    single<OwnerRepository> { OwnerRepositoryImpl(get()) }
    single<TokenRepository> { TokenRepositoryImpl(get()) }
    single<AppPreferencesRepository> { AppPreferencesRepositoryImpl(get()) }
    single<ActiveStoreRepository> { ActiveStoreRepositoryImpl(get()) }
}
