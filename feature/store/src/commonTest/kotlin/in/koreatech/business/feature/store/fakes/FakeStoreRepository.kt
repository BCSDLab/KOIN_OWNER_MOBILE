package `in`.koreatech.business.feature.store.fakes

import `in`.koreatech.business.domain.model.MenuCategory
import `in`.koreatech.business.domain.model.MenuOptionPrice
import `in`.koreatech.business.domain.model.OperatingTime
import `in`.koreatech.business.domain.model.StoreCategory
import `in`.koreatech.business.domain.model.StoreDetail
import `in`.koreatech.business.domain.model.StoreEvent
import `in`.koreatech.business.domain.repository.StoreRepository

internal class FakeStoreRepository(
    var storeDetail: StoreDetail = STORE_DETAIL,
    var storeMenus: List<MenuCategory> = emptyList(),
    var menuCategories: List<MenuCategory> = emptyList(),
    var storeEvents: List<StoreEvent> = emptyList(),
    var storeCategories: List<StoreCategory> = emptyList(),
    var getStoreDetailError: Throwable? = null,
    var getStoreMenusError: Throwable? = null,
    var getMenuCategoriesError: Throwable? = null,
    var getStoreEventsError: Throwable? = null,
    var deleteMenuError: Throwable? = null,
    var registerMenuError: Throwable? = null,
    var updateMenuError: Throwable? = null,
    var registerEventError: Throwable? = null,
    var deleteEventError: Throwable? = null,
    var updateEventError: Throwable? = null,
    var updateStoreInfoError: Throwable? = null,
    var createMenuCategoryError: Throwable? = null,
    var renameMenuCategoryError: Throwable? = null,
    var deleteMenuCategoryError: Throwable? = null
) : StoreRepository {
    val deleteMenuCalls: MutableList<Pair<String, String>> = mutableListOf()
    val deleteEventCalls: MutableList<Pair<String, String>> = mutableListOf()
    val registerMenuCalls: MutableList<String> = mutableListOf()
    val updateMenuCalls: MutableList<String> = mutableListOf()
    val registerEventCalls: MutableList<String> = mutableListOf()
    val updateEventCalls: MutableList<String> = mutableListOf()
    val updateStoreInfoCalls: MutableList<String> = mutableListOf()
    val createMenuCategoryCalls: MutableList<Pair<String, String>> = mutableListOf()
    val renameMenuCategoryCalls: MutableList<Pair<Int, String>> = mutableListOf()
    val deleteMenuCategoryCalls: MutableList<Int> = mutableListOf()

    override suspend fun getStoreDetail(storeId: String): StoreDetail {
        getStoreDetailError?.let { throw it }
        return storeDetail
    }

    override suspend fun getStoreMenus(storeId: String): List<MenuCategory> {
        getStoreMenusError?.let { throw it }
        return storeMenus
    }

    override suspend fun getMenuCategories(storeId: String): List<MenuCategory> {
        getMenuCategoriesError?.let { throw it }
        return menuCategories
    }

    override suspend fun getStoreEvents(storeId: String): List<StoreEvent> {
        getStoreEventsError?.let { throw it }
        return storeEvents
    }

    override suspend fun deleteMenu(storeId: String, menuId: String) {
        deleteMenuError?.let { throw it }
        deleteMenuCalls += storeId to menuId
    }

    override suspend fun registerMenu(
        storeId: String,
        name: String,
        price: Int?,
        description: String,
        imageUrls: List<String>,
        optionPrices: List<MenuOptionPrice>,
        categoryIds: List<Int>
    ) {
        registerMenuError?.let { throw it }
        registerMenuCalls += storeId
    }

    override suspend fun updateMenu(
        storeId: String,
        menuId: String,
        name: String,
        price: Int?,
        description: String,
        imageUrls: List<String>,
        optionPrices: List<MenuOptionPrice>,
        categoryIds: List<Int>
    ) {
        updateMenuError?.let { throw it }
        updateMenuCalls += menuId
    }

    override suspend fun registerEvent(
        storeId: String,
        title: String,
        content: String,
        imageUrls: List<String>,
        startDate: String,
        endDate: String
    ) {
        registerEventError?.let { throw it }
        registerEventCalls += storeId
    }

    override suspend fun deleteEvent(storeId: String, eventId: String) {
        deleteEventError?.let { throw it }
        deleteEventCalls += storeId to eventId
    }

    override suspend fun updateEvent(
        storeId: String,
        eventId: String,
        title: String,
        content: String,
        imageUrls: List<String>,
        startDate: String,
        endDate: String
    ) {
        updateEventError?.let { throw it }
        updateEventCalls += eventId
    }

    override suspend fun updateStoreInfo(
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
    ) {
        updateStoreInfoError?.let { throw it }
        updateStoreInfoCalls += storeId
    }

    override suspend fun createMenuCategory(storeId: String, name: String) {
        createMenuCategoryError?.let { throw it }
        createMenuCategoryCalls += storeId to name
    }

    override suspend fun renameMenuCategory(categoryId: Int, name: String) {
        renameMenuCategoryError?.let { throw it }
        renameMenuCategoryCalls += categoryId to name
    }

    override suspend fun deleteMenuCategory(categoryId: Int) {
        deleteMenuCategoryError?.let { throw it }
        deleteMenuCategoryCalls += categoryId
    }

    override suspend fun getStoreCategories(): List<StoreCategory> = storeCategories

    override suspend fun registerStore(
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
    ) = Unit

    companion object {
        val STORE_DETAIL: StoreDetail = StoreDetail(
            id = 1,
            name = "테스트 매장",
            phone = "01000000000",
            address = "서울",
            description = "설명",
            openTime = "09:00",
            closeTime = "21:00",
            isEvent = false,
            categoryIds = listOf(1, 2),
            isDelivery = false,
            isCard = true,
            isBank = false
        )
    }
}
