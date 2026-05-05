package `in`.koreatech.business.domain.usecase.store

import `in`.koreatech.business.domain.model.MenuCategory
import `in`.koreatech.business.domain.repository.StoreRepository

class GetStoreMenusUseCase(private val repository: StoreRepository) {
    suspend operator fun invoke(storeId: String): List<MenuCategory> = repository.getStoreMenus(storeId)
}
