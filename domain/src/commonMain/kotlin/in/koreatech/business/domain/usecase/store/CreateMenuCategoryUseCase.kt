package `in`.koreatech.business.domain.usecase.store

import `in`.koreatech.business.domain.repository.StoreRepository
import `in`.koreatech.business.domain.util.runCatchingCancellable

class CreateMenuCategoryUseCase(private val repository: StoreRepository) {
    suspend operator fun invoke(storeId: String, name: String): Result<Unit> = runCatchingCancellable {
        repository.createMenuCategory(storeId, name)
    }
}
