package `in`.koreatech.business.domain.repository

import `in`.koreatech.business.domain.model.MenuCategory
import `in`.koreatech.business.domain.model.MenuOptionPrice
import `in`.koreatech.business.domain.model.OperatingTime
import `in`.koreatech.business.domain.model.StoreCategory
import `in`.koreatech.business.domain.model.StoreDetail
import `in`.koreatech.business.domain.model.StoreEvent

interface StoreRepository {
    suspend fun getStoreDetail(storeId: String): StoreDetail

    suspend fun getStoreMenus(storeId: String): List<MenuCategory>

    suspend fun getMenuCategories(storeId: String): List<MenuCategory>

    suspend fun getStoreEvents(storeId: String): List<StoreEvent>

    suspend fun deleteMenu(storeId: String, menuId: String)

    suspend fun registerMenu(
        storeId: String,
        name: String,
        price: Int?,
        description: String,
        imageUrls: List<String>,
        optionPrices: List<MenuOptionPrice>,
        categoryIds: List<Int>
    )

    suspend fun updateMenu(
        storeId: String,
        menuId: String,
        name: String,
        price: Int?,
        description: String,
        imageUrls: List<String>,
        optionPrices: List<MenuOptionPrice>,
        categoryIds: List<Int>
    )

    suspend fun registerEvent(
        storeId: String,
        title: String,
        content: String,
        imageUrls: List<String>,
        startDate: String,
        endDate: String
    )

    suspend fun deleteEvent(storeId: String, eventId: String)

    suspend fun updateEvent(
        storeId: String,
        eventId: String,
        title: String,
        content: String,
        imageUrls: List<String>,
        startDate: String,
        endDate: String
    )

    suspend fun updateStoreInfo(
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
    )

    suspend fun createMenuCategory(storeId: String, name: String)

    suspend fun renameMenuCategory(categoryId: Int, name: String)

    suspend fun deleteMenuCategory(categoryId: Int)

    suspend fun getStoreCategories(): List<StoreCategory>

    suspend fun registerStore(
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
    )
}
