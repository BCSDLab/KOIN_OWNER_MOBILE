package `in`.koreatech.business.domain.usecase.store

import `in`.koreatech.business.domain.model.MenuCategory
import `in`.koreatech.business.domain.repository.StoreRepository
import `in`.koreatech.business.domain.util.runCatchingCancellable

class GetStoreMenusUseCase(private val repository: StoreRepository) {
    suspend operator fun invoke(storeId: String): Result<List<MenuCategory>> = runCatchingCancellable {
        repository.getStoreMenus(storeId)
    }
}
