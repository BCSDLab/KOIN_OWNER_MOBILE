package `in`.koreatech.business.domain.usecase.owner

import `in`.koreatech.business.domain.model.owner.OwnerProfile
import `in`.koreatech.business.domain.repository.OwnerRepository
import `in`.koreatech.business.domain.util.runCatchingCancellable

class GetOwnerProfileUseCase(private val repository: OwnerRepository) {
    suspend operator fun invoke(): Result<OwnerProfile> = runCatchingCancellable {
        repository.getOwnerProfile()
    }
}
