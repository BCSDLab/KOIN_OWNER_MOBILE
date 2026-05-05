package `in`.koreatech.business.domain.usecase.owner

import `in`.koreatech.business.domain.model.signup.ShopSearchResult
import `in`.koreatech.business.domain.repository.OwnerRepository

class SearchShopsUseCase(private val repository: OwnerRepository) {
    suspend operator fun invoke(query: String): List<ShopSearchResult> =
        repository.searchShops(query)
}
