package `in`.koreatech.business.domain.usecase.owner

import `in`.koreatech.business.domain.model.signup.ShopSearchResult
import `in`.koreatech.business.domain.repository.OwnerRepository
import `in`.koreatech.business.domain.util.runCatchingCancellable

class SearchShopsUseCase(private val repository: OwnerRepository) {
    suspend operator fun invoke(query: String): Result<List<ShopSearchResult>> = runCatchingCancellable {
        repository.searchShops(query)
    }
}
