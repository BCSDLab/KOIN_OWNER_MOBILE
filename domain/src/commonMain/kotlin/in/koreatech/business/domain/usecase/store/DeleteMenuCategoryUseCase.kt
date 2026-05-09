package `in`.koreatech.business.domain.usecase.store

import `in`.koreatech.business.domain.repository.StoreRepository
import `in`.koreatech.business.domain.util.runCatchingCancellable

class DeleteMenuCategoryUseCase(private val repository: StoreRepository) {
    suspend operator fun invoke(categoryId: Int): Result<Unit> = runCatchingCancellable {
        repository.deleteMenuCategory(categoryId)
    }
}
