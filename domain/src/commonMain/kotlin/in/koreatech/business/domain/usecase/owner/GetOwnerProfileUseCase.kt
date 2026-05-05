package `in`.koreatech.business.domain.usecase.owner

import `in`.koreatech.business.domain.model.owner.OwnerProfile
import `in`.koreatech.business.domain.repository.OwnerRepository

class GetOwnerProfileUseCase(private val repository: OwnerRepository) {
    suspend operator fun invoke(): OwnerProfile = repository.getOwnerProfile()
}
