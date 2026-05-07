package `in`.koreatech.business.domain.usecase.owner

import `in`.koreatech.business.domain.repository.OwnerRepository

class UploadFileUseCase(private val repository: OwnerRepository) {
    suspend operator fun invoke(fileName: String, mimeType: String, bytes: ByteArray): String = repository.uploadFile(fileName, mimeType, bytes)
}
