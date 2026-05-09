package `in`.koreatech.business.domain.usecase.store

import `in`.koreatech.business.domain.repository.StoreRepository
import `in`.koreatech.business.domain.util.runCatchingCancellable

class DeleteMenuUseCase(private val repository: StoreRepository) {
    suspend operator fun invoke(storeId: String, menuId: String): Result<Unit> = runCatchingCancellable {
        repository.deleteMenu(storeId, menuId)
    }
}
