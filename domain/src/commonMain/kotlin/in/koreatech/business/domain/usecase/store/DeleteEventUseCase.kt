package `in`.koreatech.business.domain.usecase.store

import `in`.koreatech.business.domain.repository.StoreRepository

class DeleteEventUseCase(private val repository: StoreRepository) {
    suspend operator fun invoke(storeId: String, eventId: String) = repository.deleteEvent(storeId, eventId)
}
