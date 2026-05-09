package `in`.koreatech.business.domain.usecase.owner

import `in`.koreatech.business.domain.model.owner.OwnerStore
import `in`.koreatech.business.domain.repository.OwnerRepository
import `in`.koreatech.business.domain.util.runCatchingCancellable

class GetShopListUseCase(private val repository: OwnerRepository) {
    suspend operator fun invoke(): Result<List<OwnerStore>> = runCatchingCancellable {
        repository.getShopList()
    }
}
