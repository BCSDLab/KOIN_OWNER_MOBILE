package `in`.koreatech.business.domain.usecase.store

import `in`.koreatech.business.domain.repository.ActiveStoreRepository
import `in`.koreatech.business.domain.util.runCatchingCancellable

class SetActiveStoreIdUseCase(
    private val repository: ActiveStoreRepository
) {
    suspend operator fun invoke(id: String?): Result<Unit> = runCatchingCancellable {
        repository.setActiveStoreId(id)
    }
}
