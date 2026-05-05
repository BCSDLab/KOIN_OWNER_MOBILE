package `in`.koreatech.business.domain.usecase.store

import `in`.koreatech.business.domain.repository.StoreRepository

class DeleteMenuUseCase(private val repository: StoreRepository) {
    suspend operator fun invoke(storeId: String, menuId: String) =
        repository.deleteMenu(storeId, menuId)
}
