package `in`.koreatech.business.di

import `in`.koreatech.business.data.di.DataSourceModule
import `in`.koreatech.business.data.di.DataStoreModule
import `in`.koreatech.business.data.di.EncryptedDataStoreModule
import `in`.koreatech.business.data.di.NetworkModule
import `in`.koreatech.business.data.di.RepositoryModule
import `in`.koreatech.business.data.repository.ActiveStoreRepositoryImpl
import `in`.koreatech.business.domain.repository.ActiveStoreRepository
import org.koin.core.qualifier.named
import org.koin.dsl.module

val dataStoreModule = module {
    single { DataStoreModule().provideDataStore(this) }
}

val encryptedDataStoreModule = module {
    single { EncryptedDataStoreModule().provideEncryptedDataStore(this) }
}

val networkDslModule = module {
    val nm = NetworkModule()
    single(named("noAuth")) { nm.provideNoAuthHttpClient() }
    single(named("s3")) { nm.provideS3HttpClient() }
    single { nm.provideOwnerAuthApi(get(named("noAuth"))) }
    single(named("auth")) { nm.provideAuthHttpClient(get(), get()) }
    single { nm.provideOwnerApi(get(named("auth"))) }
    single { nm.providePublicApi(get(named("noAuth")), get(named("s3"))) }
}

val dataSourceDslModule = module {
    val dm = DataSourceModule()
    single { dm.provideOwnerRemoteDataSource(get(), get(), get()) }
    single { dm.provideTokenLocalDataSource(get()) }
}

val repositoryDslModule = module {
    val rm = RepositoryModule()
    single { rm.provideAuthRepository(get(), get()) }
    single { rm.provideStoreRepository(get()) }
    single { rm.provideOwnerRepository(get()) }
    single { rm.provideTokenRepository(get()) }
    single { rm.provideAppPreferencesRepository(get()) }
    single<ActiveStoreRepository> { ActiveStoreRepositoryImpl(get()) }
}
