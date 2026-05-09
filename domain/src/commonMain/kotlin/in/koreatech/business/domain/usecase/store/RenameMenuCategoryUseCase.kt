package `in`.koreatech.business.domain.usecase.store

import `in`.koreatech.business.domain.repository.StoreRepository
import `in`.koreatech.business.domain.util.runCatchingCancellable

class RenameMenuCategoryUseCase(private val repository: StoreRepository) {
    suspend operator fun invoke(categoryId: Int, name: String): Result<Unit> = runCatchingCancellable {
        repository.renameMenuCategory(categoryId, name)
    }
}
