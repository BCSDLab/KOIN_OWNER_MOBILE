package `in`.koreatech.business.domain.usecase.store

import `in`.koreatech.business.domain.repository.StoreRepository
import `in`.koreatech.business.domain.util.runCatchingCancellable

class RegisterEventUseCase(private val repository: StoreRepository) {
    suspend operator fun invoke(
        storeId: String,
        title: String,
        content: String,
        imageUrls: List<String>,
        startDate: String,
        endDate: String
    ): Result<Unit> = runCatchingCancellable {
        repository.registerEvent(storeId, title, content, imageUrls, startDate, endDate)
    }
}
