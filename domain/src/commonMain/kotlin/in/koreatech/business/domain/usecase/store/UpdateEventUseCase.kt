package `in`.koreatech.business.domain.usecase.store

import `in`.koreatech.business.domain.repository.StoreRepository

class UpdateEventUseCase(private val repository: StoreRepository) {
    suspend operator fun invoke(
        storeId: String,
        eventId: String,
        title: String,
        content: String,
        imageUrls: List<String>,
        startDate: String,
        endDate: String
    ) = repository.updateEvent(storeId, eventId, title, content, imageUrls, startDate, endDate)
}
