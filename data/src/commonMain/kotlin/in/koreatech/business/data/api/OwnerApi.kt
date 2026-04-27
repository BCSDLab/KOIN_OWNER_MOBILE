package `in`.koreatech.business.data.api

import `in`.koreatech.business.data.model.owner.response.OwnerProfileResponse
import `in`.koreatech.business.data.model.owner.response.OwnerStoreListResponse
import `in`.koreatech.business.data.model.signup.request.UploadUrlRequest
import `in`.koreatech.business.data.model.signup.response.UploadUrlResponse
import `in`.koreatech.business.data.model.store.request.CreateMenuCategoryRequest
import `in`.koreatech.business.data.model.store.request.ModifyMenuCategoryRequest
import `in`.koreatech.business.data.model.store.request.RegisterEventRequest
import `in`.koreatech.business.data.model.store.request.RegisterMenuRequest
import `in`.koreatech.business.data.model.store.request.RegisterStoreRequest
import `in`.koreatech.business.data.model.store.request.UpdateStoreInfoRequest
import `in`.koreatech.business.data.model.store.response.MenuCategoriesResponse
import `in`.koreatech.business.data.model.store.response.StoreDetailResponse
import `in`.koreatech.business.data.model.store.response.StoreEventsResponse
import `in`.koreatech.business.data.model.store.response.StoreMenusResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders

class OwnerApi(private val httpClient: HttpClient) {
    suspend fun getOwnerProfile(): OwnerProfileResponse = httpClient.get("/owner").body()

    suspend fun getMyShopList(): OwnerStoreListResponse = httpClient.get("/owner/shops").body()

    suspend fun getUploadUrl(request: UploadUrlRequest): UploadUrlResponse =
        httpClient.post("/market/upload/url") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(request)
        }.body()

    suspend fun getStoreDetail(storeId: String): StoreDetailResponse =
        httpClient.get("/owner/shops/$storeId").body()

    suspend fun getStoreMenus(storeId: String): StoreMenusResponse =
        httpClient.get("/owner/shops/menus") {
            parameter("shopId", storeId)
        }.body()

    suspend fun getMenuCategories(storeId: String): MenuCategoriesResponse =
        httpClient.get("/owner/shops/menus/categories") {
            parameter("shopId", storeId)
        }.body()

    suspend fun getStoreEvents(storeId: String): StoreEventsResponse =
        httpClient.get("/owner/shops/$storeId/event").body()

    suspend fun deleteMenu(menuId: String) =
        httpClient.delete("/owner/shops/menus/$menuId")

    suspend fun registerMenu(storeId: String, request: RegisterMenuRequest) =
        httpClient.post("/owner/shops/$storeId/menus") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(request)
        }

    suspend fun updateMenu(menuId: String, request: RegisterMenuRequest) =
        httpClient.put("/owner/shops/menus/$menuId") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(request)
        }

    suspend fun registerEvent(storeId: String, request: RegisterEventRequest) =
        httpClient.post("/owner/shops/$storeId/event") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(request)
        }

    suspend fun deleteEvent(storeId: String, eventId: String) =
        httpClient.delete("/owner/shops/$storeId/events/$eventId")

    suspend fun updateEvent(storeId: String, eventId: String, request: RegisterEventRequest) =
        httpClient.put("/owner/shops/$storeId/events/$eventId") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(request)
        }

    suspend fun updateStoreInfo(storeId: String, request: UpdateStoreInfoRequest) =
        httpClient.put("/owner/shops/$storeId") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(request)
        }

    suspend fun registerStore(request: RegisterStoreRequest) =
        httpClient.post("/owner/shops") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(request)
        }

    suspend fun createMenuCategory(storeId: String, name: String) =
        httpClient.post("/owner/shops/$storeId/menus/categories") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(CreateMenuCategoryRequest(name = name))
        }

    suspend fun renameMenuCategory(categoryId: Int, name: String) =
        httpClient.put("/owner/shops/menus/categories/$categoryId") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(ModifyMenuCategoryRequest(id = categoryId, name = name))
        }

    suspend fun deleteMenuCategory(categoryId: Int) =
        httpClient.delete("/owner/shops/menus/categories/$categoryId")
}
