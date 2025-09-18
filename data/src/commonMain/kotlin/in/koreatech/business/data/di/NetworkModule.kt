package `in`.koreatech.business.data.di

import `in`.koreatech.business.data.network.provideAuthHttpClient
import `in`.koreatech.business.data.network.provideNoAuthHttpClient
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val networkModule =
    module {
        singleOf(::provideAuthHttpClient) { named("auth") }
        singleOf(::provideNoAuthHttpClient) { named("noAuth") }
    }
