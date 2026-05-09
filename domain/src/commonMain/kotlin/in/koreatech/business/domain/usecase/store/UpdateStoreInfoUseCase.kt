package `in`.koreatech.business.domain.usecase.store

import `in`.koreatech.business.domain.model.OperatingTime
import `in`.koreatech.business.domain.repository.StoreRepository
import `in`.koreatech.business.domain.util.runCatchingCancellable

class UpdateStoreInfoUseCase(private val repository: StoreRepository) {
    suspend operator fun invoke(
        storeId: String,
        name: String,
        phone: String,
        address: String,
        description: String,
        mainCategoryId: Int,
        categoryIds: List<Int>,
        isDelivery: Boolean,
        deliveryPrice: Int,
        isCard: Boolean,
        isBank: Boolean,
        imageUrls: List<String>,
        operatingTimes: List<OperatingTime>
    ): Result<Unit> = runCatchingCancellable {
        repository.updateStoreInfo(
            storeId, name, phone, address, description, mainCategoryId, categoryIds,
            isDelivery, deliveryPrice, isCard, isBank, imageUrls, operatingTimes
        )
    }
}
