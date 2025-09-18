package `in`.koreatech.business.data.di

import `in`.koreatech.business.data.repository.TokenRepositoryImpl
import `in`.koreatech.business.domain.repository.TokenRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val repositoryModule =
    module {
        singleOf(::TokenRepositoryImpl) { bind<TokenRepository>() }
    }
