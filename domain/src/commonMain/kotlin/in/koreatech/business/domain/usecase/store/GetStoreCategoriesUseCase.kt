package `in`.koreatech.business.domain.usecase.store

import `in`.koreatech.business.domain.model.StoreCategory
import `in`.koreatech.business.domain.repository.StoreRepository
import `in`.koreatech.business.domain.util.runCatchingCancellable

class GetStoreCategoriesUseCase(private val repository: StoreRepository) {
    suspend operator fun invoke(): Result<List<StoreCategory>> = runCatchingCancellable {
        repository.getStoreCategories()
    }
}
