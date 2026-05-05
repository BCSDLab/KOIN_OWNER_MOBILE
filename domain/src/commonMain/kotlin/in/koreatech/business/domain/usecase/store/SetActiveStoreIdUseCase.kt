package `in`.koreatech.business.domain.usecase.store

import `in`.koreatech.business.domain.repository.ActiveStoreRepository

class SetActiveStoreIdUseCase(
    private val repository: ActiveStoreRepository
) {
    suspend operator fun invoke(id: String?) = repository.setActiveStoreId(id)
}
