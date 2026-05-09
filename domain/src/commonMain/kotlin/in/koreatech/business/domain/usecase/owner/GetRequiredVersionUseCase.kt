package `in`.koreatech.business.domain.usecase.owner

import `in`.koreatech.business.domain.repository.OwnerRepository
import `in`.koreatech.business.domain.util.runCatchingCancellable

class GetRequiredVersionUseCase(private val repository: OwnerRepository) {
    suspend operator fun invoke(): Result<String> = runCatchingCancellable {
        repository.getRequiredVersion()
    }
}
