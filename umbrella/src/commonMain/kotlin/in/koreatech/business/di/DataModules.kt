package `in`.koreatech.business.di

import `in`.koreatech.business.data.di.DataStoreModule
import `in`.koreatech.business.data.di.EncryptedDataStore
import org.koin.dsl.module

val dataStoreModule = module {
    single { DataStoreModule().provideDataStore(this) }
}

val encryptedDataStoreModule = module {
    single { EncryptedDataStore(this) }
}
