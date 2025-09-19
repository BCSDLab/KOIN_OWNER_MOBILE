package `in`.koreatech.business.data.di

import `in`.koreatech.business.data.repository.OwnerRepositoryImpl
import `in`.koreatech.business.data.repository.TokenRepositoryImpl
import `in`.koreatech.business.data.source.local.TokenLocalDataSource
import `in`.koreatech.business.data.source.remote.OwnerRemoteDataSource
import `in`.koreatech.business.domain.repository.OwnerRepository
import `in`.koreatech.business.domain.repository.TokenRepository
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
class RepositoryModule {
    @Single
    fun provideOwnerRepository(
        ownerRemoteDataSource: OwnerRemoteDataSource
    ): OwnerRepository = OwnerRepositoryImpl(ownerRemoteDataSource)

    @Single
    fun provideTokenRepository(
        tokenLocalDataSource: TokenLocalDataSource
    ): TokenRepository = TokenRepositoryImpl(tokenLocalDataSource)
}
