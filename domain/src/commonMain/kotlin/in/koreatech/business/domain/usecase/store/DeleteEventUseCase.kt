package `in`.koreatech.business.domain.usecase.store

import `in`.koreatech.business.domain.repository.StoreRepository
import `in`.koreatech.business.domain.util.runCatchingCancellable

class DeleteEventUseCase(private val repository: StoreRepository) {
    suspend operator fun invoke(storeId: String, eventId: String): Result<Unit> = runCatchingCancellable {
        repository.deleteEvent(storeId, eventId)
    }
}
