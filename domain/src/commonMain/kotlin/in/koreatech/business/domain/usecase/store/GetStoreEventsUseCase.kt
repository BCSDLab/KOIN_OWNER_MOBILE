package `in`.koreatech.business.domain.usecase.store

import `in`.koreatech.business.domain.model.StoreEvent
import `in`.koreatech.business.domain.repository.StoreRepository

class GetStoreEventsUseCase(private val repository: StoreRepository) {
    suspend operator fun invoke(storeId: String): List<StoreEvent> = repository.getStoreEvents(storeId)
}
