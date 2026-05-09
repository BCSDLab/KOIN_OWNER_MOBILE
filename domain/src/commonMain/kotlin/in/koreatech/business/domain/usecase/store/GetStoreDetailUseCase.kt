package `in`.koreatech.business.domain.usecase.store

import `in`.koreatech.business.domain.model.StoreDetail
import `in`.koreatech.business.domain.repository.StoreRepository
import `in`.koreatech.business.domain.util.runCatchingCancellable

class GetStoreDetailUseCase(private val repository: StoreRepository) {
    suspend operator fun invoke(storeId: String): Result<StoreDetail> = runCatchingCancellable {
        repository.getStoreDetail(storeId)
    }
}
