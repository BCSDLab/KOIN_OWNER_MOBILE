package `in`.koreatech.business.data.di

import `in`.koreatech.business.data.repository.OwnerRepositoryImpl
import `in`.koreatech.business.data.repository.TokenRepositoryImpl
import `in`.koreatech.business.domain.repository.OwnerRepository
import `in`.koreatech.business.domain.repository.TokenRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val repositoryModule =
    module {
        singleOf(::OwnerRepositoryImpl) { bind<OwnerRepository>() }
        singleOf(::TokenRepositoryImpl) { bind<TokenRepository>() }
    }
