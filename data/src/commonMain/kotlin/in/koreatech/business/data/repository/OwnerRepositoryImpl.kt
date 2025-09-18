package `in`.koreatech.business.data.repository

import `in`.koreatech.business.data.source.remote.OwnerRemoteDataSource
import `in`.koreatech.business.domain.repository.OwnerRepository

class OwnerRepositoryImpl(
    private val ownerRemoteDataSource: OwnerRemoteDataSource
): OwnerRepository