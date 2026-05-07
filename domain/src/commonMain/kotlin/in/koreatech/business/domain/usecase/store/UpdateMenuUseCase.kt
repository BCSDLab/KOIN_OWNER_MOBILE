package `in`.koreatech.business.domain.usecase.store

import `in`.koreatech.business.domain.model.MenuOptionPrice
import `in`.koreatech.business.domain.repository.StoreRepository

class UpdateMenuUseCase(private val repository: StoreRepository) {
    suspend operator fun invoke(
        storeId: String,
        menuId: String,
        name: String,
        price: Int?,
        description: String,
        imageUrls: List<String>,
        optionPrices: List<MenuOptionPrice>,
        categoryIds: List<Int>
    ) = repository.updateMenu(
        storeId,
        menuId,
        name,
        price,
        description,
        imageUrls,
        optionPrices,
        categoryIds
    )
}
