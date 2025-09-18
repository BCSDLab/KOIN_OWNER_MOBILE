package `in`.koreatech.business.data.source.remote

import `in`.koreatech.business.data.api.OwnerApi
import `in`.koreatech.business.data.api.auth.OwnerAuthApi

class OwnerRemoteDataSource(
    private val ownerApi: OwnerApi,
    private val ownerAuthApi: OwnerAuthApi
)
