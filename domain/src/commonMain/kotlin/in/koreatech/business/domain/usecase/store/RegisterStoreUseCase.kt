package `in`.koreatech.business.domain.usecase.store

import `in`.koreatech.business.domain.model.OperatingTime
import `in`.koreatech.business.domain.repository.StoreRepository

class RegisterStoreUseCase(private val repository: StoreRepository) {
    suspend operator fun invoke(
        name: String,
        address: String,
        mainCategoryId: Int,
        categoryIds: List<Int>,
        phone: String,
        delivery: Boolean,
        deliveryPrice: Int,
        payCard: Boolean,
        payBank: Boolean,
        description: String,
        imageUrls: List<String>,
        operatingTimes: List<OperatingTime>
    ) = repository.registerStore(
        name, address, mainCategoryId, categoryIds, phone, delivery, deliveryPrice,
        payCard, payBank, description, imageUrls, operatingTimes
    )
}
