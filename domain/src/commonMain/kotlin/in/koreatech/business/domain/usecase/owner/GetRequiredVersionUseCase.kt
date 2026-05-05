package `in`.koreatech.business.domain.usecase.owner

import `in`.koreatech.business.domain.repository.OwnerRepository

class GetRequiredVersionUseCase(private val repository: OwnerRepository) {
    suspend operator fun invoke(): String = repository.getRequiredVersion()
}
