package `in`.koreatech.business.feature.store.fakes

import `in`.koreatech.business.domain.model.owner.OwnerProfile
import `in`.koreatech.business.domain.model.owner.OwnerStore
import `in`.koreatech.business.domain.model.signup.ShopSearchResult
import `in`.koreatech.business.domain.repository.OwnerRepository

internal class FakeOwnerRepository(
    var shops: List<OwnerStore> = emptyList(),
    var profile: OwnerProfile = OwnerProfile(name = "사장님", email = "owner@example.com", companyNumber = null),
    var profileError: Throwable? = null,
    var shopListError: Throwable? = null
) : OwnerRepository {
    override suspend fun getShopList(): List<OwnerStore> {
        shopListError?.let { throw it }
        return shops
    }

    override suspend fun getOwnerProfile(): OwnerProfile {
        profileError?.let { throw it }
        return profile
    }

    override suspend fun getRequiredVersion(): String = ""
    override suspend fun uploadFile(fileName: String, mimeType: String, bytes: ByteArray): String = "https://example.com/$fileName"
    override suspend fun searchShops(query: String): List<ShopSearchResult> = emptyList()
}
