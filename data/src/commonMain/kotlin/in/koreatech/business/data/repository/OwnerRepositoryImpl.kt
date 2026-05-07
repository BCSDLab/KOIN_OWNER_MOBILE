package `in`.koreatech.business.data.repository

import `in`.koreatech.business.data.model.owner.response.toDomain as ownerProfileToDomain
import `in`.koreatech.business.data.model.shop.response.toDomain
import `in`.koreatech.business.data.source.remote.OwnerRemoteDataSource
import `in`.koreatech.business.data.utils.toUserMessage
import `in`.koreatech.business.domain.model.owner.OwnerProfile
import `in`.koreatech.business.domain.model.owner.OwnerStore
import `in`.koreatech.business.domain.model.signup.ShopSearchResult
import `in`.koreatech.business.domain.repository.OwnerRepository

class OwnerRepositoryImpl(
    private val ownerRemoteDataSource: OwnerRemoteDataSource
) : OwnerRepository {
    override suspend fun getShopList(): List<OwnerStore> {
        try {
            return ownerRemoteDataSource.getMyShopList().toDomainList()
        } catch (exception: Exception) {
            throw IllegalStateException(exception.toUserMessage(), exception)
        }
    }

    override suspend fun getOwnerProfile(): OwnerProfile {
        try {
            return ownerRemoteDataSource.getOwnerProfile().ownerProfileToDomain()
        } catch (exception: Exception) {
            throw IllegalStateException(exception.toUserMessage(), exception)
        }
    }

    override suspend fun getRequiredVersion(): String = ownerRemoteDataSource.getRequiredVersion("android_owner").version

    override suspend fun uploadFile(fileName: String, mimeType: String, bytes: ByteArray): String {
        try {
            return ownerRemoteDataSource.uploadFile(fileName, mimeType, bytes)
        } catch (exception: Exception) {
            throw IllegalStateException(exception.toUserMessage(), exception)
        }
    }

    override suspend fun searchShops(query: String): List<ShopSearchResult> {
        try {
            return ownerRemoteDataSource.searchShops(query).shops.map { it.toDomain() }
        } catch (exception: Exception) {
            throw IllegalStateException(exception.toUserMessage(), exception)
        }
    }
}
