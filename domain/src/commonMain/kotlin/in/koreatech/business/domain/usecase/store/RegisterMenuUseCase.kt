package `in`.koreatech.business.domain.usecase.store

import `in`.koreatech.business.domain.model.MenuOptionPrice
import `in`.koreatech.business.domain.repository.StoreRepository
import `in`.koreatech.business.domain.util.runCatchingCancellable

class RegisterMenuUseCase(private val repository: StoreRepository) {
    suspend operator fun invoke(
        storeId: String,
        name: String,
        price: Int?,
        description: String,
        imageUrls: List<String>,
        optionPrices: List<MenuOptionPrice>,
        categoryIds: List<Int>
    ): Result<Unit> = runCatchingCancellable {
        repository.registerMenu(
            storeId,
            name,
            price,
            description,
            imageUrls,
            optionPrices,
            categoryIds
        )
    }
}
