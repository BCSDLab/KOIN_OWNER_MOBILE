package `in`.koreatech.business.domain.usecase.store

import `in`.koreatech.business.domain.model.StoreCategory
import `in`.koreatech.business.domain.repository.StoreRepository

class GetStoreCategoriesUseCase(private val repository: StoreRepository) {
    suspend operator fun invoke(): List<StoreCategory> = repository.getStoreCategories()
}
