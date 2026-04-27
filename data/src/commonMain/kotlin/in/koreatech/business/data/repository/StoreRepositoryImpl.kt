package `in`.koreatech.business.data.repository

import `in`.koreatech.business.data.model.store.request.MenuOptionPriceRequest
import `in`.koreatech.business.data.model.store.request.OperatingTimeRequest
import `in`.koreatech.business.data.model.store.request.RegisterEventRequest
import `in`.koreatech.business.data.model.store.request.RegisterMenuRequest
import `in`.koreatech.business.data.model.store.request.RegisterStoreRequest
import `in`.koreatech.business.data.model.store.request.UpdateStoreInfoRequest
import `in`.koreatech.business.data.model.store.response.toDomainList as categoryToDomainList
import `in`.koreatech.business.data.source.remote.OwnerRemoteDataSource
import `in`.koreatech.business.data.utils.toUserMessage
import `in`.koreatech.business.domain.model.MenuCategory
import `in`.koreatech.business.domain.model.MenuOptionPrice
import `in`.koreatech.business.domain.model.OperatingTime
import `in`.koreatech.business.domain.model.StoreCategory
import `in`.koreatech.business.domain.model.StoreDetail
import `in`.koreatech.business.domain.model.StoreEvent
import `in`.koreatech.business.domain.repository.StoreRepository

class StoreRepositoryImpl(
    private val ownerRemoteDataSource: OwnerRemoteDataSource
) : StoreRepository {
    override suspend fun getStoreDetail(storeId: String): StoreDetail {
        try {
            return ownerRemoteDataSource.getStoreDetail(storeId).toDomain()
        } catch (exception: Exception) {
            throw IllegalStateException(exception.toUserMessage(), exception)
        }
    }

    override suspend fun getStoreMenus(storeId: String): List<MenuCategory> {
        try {
            return ownerRemoteDataSource.getStoreMenus(storeId).toDomainList()
        } catch (exception: Exception) {
            throw IllegalStateException(exception.toUserMessage(), exception)
        }
    }

    override suspend fun getMenuCategories(storeId: String): List<MenuCategory> {
        try {
            return ownerRemoteDataSource.getMenuCategories(storeId).toDomainList()
        } catch (exception: Exception) {
            throw IllegalStateException(exception.toUserMessage(), exception)
        }
    }

    override suspend fun getStoreEvents(storeId: String): List<StoreEvent> {
        try {
            return ownerRemoteDataSource.getStoreEvents(storeId).toDomainList()
        } catch (exception: Exception) {
            throw IllegalStateException(exception.toUserMessage(), exception)
        }
    }

    override suspend fun deleteMenu(storeId: String, menuId: String) {
        try {
            ownerRemoteDataSource.deleteMenu(menuId)
        } catch (exception: Exception) {
            throw IllegalStateException(exception.toUserMessage(), exception)
        }
    }

    override suspend fun registerMenu(
        storeId: String,
        name: String,
        price: Int?,
        description: String,
        imageUrls: List<String>,
        optionPrices: List<MenuOptionPrice>,
        categoryIds: List<Int>,
        isHidden: Boolean
    ) {
        try {
            val isSingle = optionPrices.isEmpty()
            val request = if (isSingle) {
                RegisterMenuRequest(
                    name = name,
                    isSingle = true,
                    singlePrice = price,
                    optionPrices = null,
                    description = description,
                    imageUrls = imageUrls,
                    categoryIds = categoryIds
                )
            } else {
                RegisterMenuRequest(
                    name = name,
                    isSingle = false,
                    singlePrice = null,
                    optionPrices = optionPrices.map {
                        MenuOptionPriceRequest(option = it.option, price = it.price)
                    },
                    description = description,
                    imageUrls = imageUrls,
                    categoryIds = categoryIds
                )
            }
            ownerRemoteDataSource.registerMenu(storeId = storeId, request = request)
        } catch (exception: Exception) {
            throw IllegalStateException(exception.toUserMessage(), exception)
        }
    }

    override suspend fun updateMenu(
        storeId: String,
        menuId: String,
        name: String,
        price: Int?,
        description: String,
        imageUrls: List<String>,
        optionPrices: List<MenuOptionPrice>,
        categoryIds: List<Int>,
        isHidden: Boolean
    ) {
        try {
            val isSingle = optionPrices.isEmpty()
            val request = if (isSingle) {
                RegisterMenuRequest(
                    name = name,
                    isSingle = true,
                    singlePrice = price,
                    optionPrices = null,
                    description = description,
                    imageUrls = imageUrls,
                    categoryIds = categoryIds
                )
            } else {
                RegisterMenuRequest(
                    name = name,
                    isSingle = false,
                    singlePrice = null,
                    optionPrices = optionPrices.map {
                        MenuOptionPriceRequest(option = it.option, price = it.price)
                    },
                    description = description,
                    imageUrls = imageUrls,
                    categoryIds = categoryIds
                )
            }
            ownerRemoteDataSource.updateMenu(menuId = menuId, request = request)
        } catch (exception: Exception) {
            throw IllegalStateException(exception.toUserMessage(), exception)
        }
    }

    override suspend fun registerEvent(
        storeId: String,
        title: String,
        content: String,
        imageUrls: List<String>,
        startDate: String,
        endDate: String
    ) {
        try {
            ownerRemoteDataSource.registerEvent(
                storeId = storeId,
                request = RegisterEventRequest(
                    title = title,
                    content = content,
                    thumbnailImages = imageUrls,
                    startDate = startDate,
                    endDate = endDate
                )
            )
        } catch (exception: Exception) {
            throw IllegalStateException(exception.toUserMessage(), exception)
        }
    }

    override suspend fun deleteEvent(storeId: String, eventId: String) {
        try {
            ownerRemoteDataSource.deleteEvent(storeId, eventId)
        } catch (exception: Exception) {
            throw IllegalStateException(exception.toUserMessage(), exception)
        }
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
        try {
            ownerRemoteDataSource.updateEvent(
                storeId = storeId,
                eventId = eventId,
                request = RegisterEventRequest(
                    title = title,
                    content = content,
                    thumbnailImages = imageUrls,
                    startDate = startDate,
                    endDate = endDate
                )
            )
        } catch (exception: Exception) {
            throw IllegalStateException(exception.toUserMessage(), exception)
        }
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
        try {
            ownerRemoteDataSource.updateStoreInfo(
                storeId = storeId,
                request = UpdateStoreInfoRequest(
                    name = name,
                    phone = phone,
                    address = address,
                    description = description,
                    mainCategoryId = mainCategoryId,
                    categoryIds = categoryIds,
                    delivery = isDelivery,
                    deliveryPrice = deliveryPrice,
                    payCard = isCard,
                    payBank = isBank,
                    imageUrls = imageUrls,
                    open = operatingTimes.map { t ->
                        OperatingTimeRequest(
                            dayOfWeek = t.dayOfWeek,
                            openTime = t.openTime ?: "00:00",
                            closeTime = t.closeTime ?: "00:00",
                            closed = t.isClosed
                        )
                    }
                )
            )
        } catch (exception: Exception) {
            throw IllegalStateException(exception.toUserMessage(), exception)
        }
    }

    override suspend fun createMenuCategory(storeId: String, name: String) {
        try {
            ownerRemoteDataSource.createMenuCategory(storeId, name)
        } catch (exception: Exception) {
            throw IllegalStateException(exception.toUserMessage(), exception)
        }
    }

    override suspend fun renameMenuCategory(categoryId: Int, name: String) {
        try {
            ownerRemoteDataSource.renameMenuCategory(categoryId, name)
        } catch (exception: Exception) {
            throw IllegalStateException(exception.toUserMessage(), exception)
        }
    }

    override suspend fun deleteMenuCategory(categoryId: Int) {
        try {
            ownerRemoteDataSource.deleteMenuCategory(categoryId)
        } catch (exception: Exception) {
            throw IllegalStateException(exception.toUserMessage(), exception)
        }
    }

    override suspend fun getStoreCategories(): List<StoreCategory> {
        return try {
            ownerRemoteDataSource.getStoreCategories().categoryToDomainList()
        } catch (exception: Exception) {
            throw IllegalStateException(exception.toUserMessage(), exception)
        }
    }

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
    ) {
        try {
            ownerRemoteDataSource.registerStore(
                RegisterStoreRequest(
                    name = name,
                    address = address,
                    mainCategoryId = mainCategoryId,
                    categoryIds = categoryIds,
                    phone = phone,
                    delivery = delivery,
                    deliveryPrice = deliveryPrice,
                    payCard = payCard,
                    payBank = payBank,
                    description = description,
                    imageUrls = imageUrls,
                    open = operatingTimes.map { t ->
                        OperatingTimeRequest(
                            dayOfWeek = t.dayOfWeek,
                            openTime = t.openTime ?: "00:00",
                            closeTime = t.closeTime ?: "00:00",
                            closed = t.isClosed
                        )
                    }
                )
            )
        } catch (exception: Exception) {
            throw IllegalStateException(exception.toUserMessage(), exception)
        }
    }
}
