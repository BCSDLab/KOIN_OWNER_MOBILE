package `in`.koreatech.business.data.di

import `in`.koreatech.business.data.api.OwnerApi
import `in`.koreatech.business.data.api.auth.OwnerAuthApi
import `in`.koreatech.business.data.network.provideAuthHttpClient
import `in`.koreatech.business.data.network.provideNoAuthHttpClient
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val networkModule =
    module {
        singleOf(::OwnerApi) { named("noAuth") }
        singleOf(::OwnerAuthApi) { named("auth") }

        singleOf(::provideAuthHttpClient) { named("auth") }
        singleOf(::provideNoAuthHttpClient) { named("noAuth") }
    }
