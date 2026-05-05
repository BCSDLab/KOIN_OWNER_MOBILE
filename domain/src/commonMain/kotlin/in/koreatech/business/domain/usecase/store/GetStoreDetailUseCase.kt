package `in`.koreatech.business.domain.usecase.store

import `in`.koreatech.business.domain.model.StoreDetail
import `in`.koreatech.business.domain.repository.StoreRepository

class GetStoreDetailUseCase(private val repository: StoreRepository) {
    suspend operator fun invoke(storeId: String): StoreDetail = repository.getStoreDetail(storeId)
}
