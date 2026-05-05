package `in`.koreatech.business.domain.usecase.owner

import `in`.koreatech.business.domain.model.owner.OwnerStore
import `in`.koreatech.business.domain.repository.OwnerRepository

class GetShopListUseCase(private val repository: OwnerRepository) {
    suspend operator fun invoke(): List<OwnerStore> = repository.getShopList()
}
