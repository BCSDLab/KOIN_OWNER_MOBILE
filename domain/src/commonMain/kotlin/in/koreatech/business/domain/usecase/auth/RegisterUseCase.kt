package `in`.koreatech.business.domain.usecase.auth

import `in`.koreatech.business.domain.repository.AuthRepository

class RegisterUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(
        phoneNumber: String,
        password: String,
        name: String,
        companyNumber: String,
        shopNumber: String,
        shopId: Int?,
        shopName: String,
        attachmentUrls: List<String>
    ) = repository.register(
        phoneNumber = phoneNumber,
        password = password,
        name = name,
        companyNumber = companyNumber,
        shopNumber = shopNumber,
        shopId = shopId,
        shopName = shopName,
        attachmentUrls = attachmentUrls
    )
}
