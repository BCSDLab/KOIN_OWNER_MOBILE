package `in`.koreatech.business.domain.usecase.store

import `in`.koreatech.business.domain.model.StoreEvent
import `in`.koreatech.business.domain.repository.StoreRepository
import `in`.koreatech.business.domain.util.runCatchingCancellable

class GetStoreEventsUseCase(private val repository: StoreRepository) {
    suspend operator fun invoke(storeId: String): Result<List<StoreEvent>> = runCatchingCancellable {
        repository.getStoreEvents(storeId)
    }
}
