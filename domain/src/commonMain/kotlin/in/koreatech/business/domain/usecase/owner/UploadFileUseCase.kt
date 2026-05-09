package `in`.koreatech.business.domain.usecase.owner

import `in`.koreatech.business.domain.repository.OwnerRepository
import `in`.koreatech.business.domain.util.runCatchingCancellable

class UploadFileUseCase(private val repository: OwnerRepository) {
    suspend operator fun invoke(fileName: String, mimeType: String, bytes: ByteArray): Result<String> = runCatchingCancellable {
        repository.uploadFile(fileName, mimeType, bytes)
    }
}
