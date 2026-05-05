package `in`.koreatech.business.domain.usecase.store

import `in`.koreatech.business.domain.repository.StoreRepository

class DeleteMenuCategoryUseCase(private val repository: StoreRepository) {
    suspend operator fun invoke(categoryId: Int) =
        repository.deleteMenuCategory(categoryId)
}
