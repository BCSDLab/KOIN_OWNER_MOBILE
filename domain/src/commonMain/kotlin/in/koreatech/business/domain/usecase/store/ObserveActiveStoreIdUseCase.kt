package `in`.koreatech.business.domain.usecase.store

import `in`.koreatech.business.domain.repository.ActiveStoreRepository
import kotlinx.coroutines.flow.Flow

class ObserveActiveStoreIdUseCase(
    private val repository: ActiveStoreRepository
) {
    operator fun invoke(): Flow<String?> = repository.activeStoreId
}
