package `in`.koreatech.business.data.di

import `in`.koreatech.business.data.source.local.TokenLocalDataSource
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val dataSourceModule =
    module {
        singleOf(::TokenLocalDataSource)
    }
