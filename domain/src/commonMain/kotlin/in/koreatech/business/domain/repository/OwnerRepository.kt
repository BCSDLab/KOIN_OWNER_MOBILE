package `in`.koreatech.business.domain.repository

import `in`.koreatech.business.domain.model.owner.OwnerProfile
import `in`.koreatech.business.domain.model.owner.OwnerStore
import `in`.koreatech.business.domain.model.signup.ShopSearchResult

interface OwnerRepository {
    suspend fun getShopList(): List<OwnerStore>

    suspend fun getOwnerProfile(): OwnerProfile

    suspend fun getRequiredVersion(): String

    suspend fun uploadFile(fileName: String, mimeType: String, bytes: ByteArray): String

    suspend fun searchShops(query: String): List<ShopSearchResult>
}
