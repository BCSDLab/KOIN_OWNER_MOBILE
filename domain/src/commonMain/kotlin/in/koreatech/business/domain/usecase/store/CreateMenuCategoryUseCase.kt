package `in`.koreatech.business.domain.usecase.store

import `in`.koreatech.business.domain.repository.StoreRepository

class CreateMenuCategoryUseCase(private val repository: StoreRepository) {
    suspend operator fun invoke(storeId: String, name: String) =
        repository.createMenuCategory(storeId, name)
}
