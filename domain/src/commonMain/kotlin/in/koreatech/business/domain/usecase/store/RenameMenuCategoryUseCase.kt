package `in`.koreatech.business.domain.usecase.store

import `in`.koreatech.business.domain.repository.StoreRepository

class RenameMenuCategoryUseCase(private val repository: StoreRepository) {
    suspend operator fun invoke(categoryId: Int, name: String) =
        repository.renameMenuCategory(categoryId, name)
}
