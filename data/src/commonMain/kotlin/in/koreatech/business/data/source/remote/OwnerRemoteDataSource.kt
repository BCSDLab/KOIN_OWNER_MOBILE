package `in`.koreatech.business.data.source.remote

import `in`.koreatech.business.data.api.OwnerApi
import `in`.koreatech.business.data.api.PublicApi
import `in`.koreatech.business.data.api.auth.OwnerAuthApi
import `in`.koreatech.business.data.model.owner.request.ChangePasswordRequest
import `in`.koreatech.business.data.model.owner.request.FindPasswordSendSmsRequest
import `in`.koreatech.business.data.model.owner.request.FindPasswordVerifySmsRequest
import `in`.koreatech.business.data.model.owner.request.OwnerLoginRequest
import `in`.koreatech.business.data.model.owner.response.OwnerLoginResponse
import `in`.koreatech.business.data.model.owner.response.OwnerProfileResponse
import `in`.koreatech.business.data.model.owner.response.OwnerStoreListResponse
import `in`.koreatech.business.data.model.shop.response.ShopSearchResponse
import `in`.koreatech.business.data.model.signup.request.OwnerRegisterRequest
import `in`.koreatech.business.data.model.signup.request.SendSignupSmsRequest
import `in`.koreatech.business.data.model.signup.request.UploadUrlRequest
import `in`.koreatech.business.data.model.signup.request.VerifySmsRequest
import `in`.koreatech.business.data.model.signup.response.VerifySmsResponse
import `in`.koreatech.business.data.model.store.request.RegisterEventRequest
import `in`.koreatech.business.data.model.store.request.RegisterMenuRequest
import `in`.koreatech.business.data.model.store.request.RegisterStoreRequest
import `in`.koreatech.business.data.model.store.request.UpdateStoreInfoRequest
import `in`.koreatech.business.data.model.store.response.StoreDetailResponse
import `in`.koreatech.business.data.model.store.response.StoreEventsResponse
import `in`.koreatech.business.data.model.store.response.StoreMenusResponse
import `in`.koreatech.business.data.model.version.VersionResponse

class OwnerRemoteDataSource(
    private val ownerApi: OwnerApi,
    private val ownerAuthApi: OwnerAuthApi,
    private val publicApi: PublicApi
) {
    suspend fun signIn(request: OwnerLoginRequest): OwnerLoginResponse = ownerAuthApi.login(request)

    suspend fun refreshToken(refreshToken: String): OwnerLoginResponse =
        ownerAuthApi.refreshToken(refreshToken)

    suspend fun getMyShopList(): OwnerStoreListResponse = ownerApi.getMyShopList()

    suspend fun getOwnerProfile(): OwnerProfileResponse = ownerApi.getOwnerProfile()

    suspend fun checkPhoneExists(phoneNumber: String): Boolean =
        ownerAuthApi.checkPhoneExists(phoneNumber)

    suspend fun sendSignupSms(request: SendSignupSmsRequest) = ownerAuthApi.sendSignupSms(request)

    suspend fun verifySmsCode(request: VerifySmsRequest): VerifySmsResponse =
        ownerAuthApi.verifySmsCode(request)

    suspend fun searchShops(query: String): ShopSearchResponse = publicApi.searchShops(query)

    suspend fun register(request: OwnerRegisterRequest) = ownerAuthApi.register(request)

    suspend fun deleteAccount() = ownerAuthApi.deleteAccount()

    suspend fun uploadFile(fileName: String, mimeType: String, bytes: ByteArray): String {
        val response = ownerApi.getUploadUrl(
            UploadUrlRequest(
                contentLength = bytes.size.toLong(),
                contentType = mimeType,
                fileName = fileName,
            )
        )
        publicApi.uploadToS3(
            preSignedUrl = response.preSignedUrl,
            bytes = bytes,
            mimeType = mimeType,
        )
        return response.fileUrl
    }

    suspend fun getStoreDetail(storeId: String): StoreDetailResponse =
        ownerApi.getStoreDetail(storeId)

    suspend fun getStoreMenus(storeId: String): StoreMenusResponse =
        ownerApi.getStoreMenus(storeId)

    suspend fun getMenuCategories(storeId: String): `in`.koreatech.business.data.model.store.response.MenuCategoriesResponse =
        ownerApi.getMenuCategories(storeId)

    suspend fun getStoreEvents(storeId: String): StoreEventsResponse =
        ownerApi.getStoreEvents(storeId)

    suspend fun deleteMenu(menuId: String) =
        ownerApi.deleteMenu(menuId)

    suspend fun registerMenu(storeId: String, request: RegisterMenuRequest) =
        ownerApi.registerMenu(storeId, request)

    suspend fun updateMenu(menuId: String, request: RegisterMenuRequest) =
        ownerApi.updateMenu(menuId, request)

    suspend fun registerEvent(storeId: String, request: RegisterEventRequest) =
        ownerApi.registerEvent(storeId, request)

    suspend fun deleteEvent(storeId: String, eventId: String) =
        ownerApi.deleteEvent(storeId, eventId)

    suspend fun updateEvent(storeId: String, eventId: String, request: RegisterEventRequest) =
        ownerApi.updateEvent(storeId, eventId, request)

    suspend fun updateStoreInfo(storeId: String, request: UpdateStoreInfoRequest) =
        ownerApi.updateStoreInfo(storeId, request)

    suspend fun sendFindPasswordSms(phoneNumber: String) =
        ownerAuthApi.sendFindPasswordSms(FindPasswordSendSmsRequest(phoneNumber))

    suspend fun verifyFindPasswordSms(phoneNumber: String, code: String) =
        ownerAuthApi.verifyFindPasswordSms(FindPasswordVerifySmsRequest(phoneNumber, code))

    suspend fun changePasswordBySms(phoneNumber: String, password: String) =
        ownerAuthApi.changePasswordBySms(ChangePasswordRequest(phoneNumber, password))

    suspend fun getStoreCategories() = publicApi.getStoreCategories()

    suspend fun registerStore(request: RegisterStoreRequest) = ownerApi.registerStore(request)

    suspend fun getRequiredVersion(type: String): VersionResponse =
        publicApi.getRequiredVersion(type)

    suspend fun createMenuCategory(storeId: String, name: String) =
        ownerApi.createMenuCategory(storeId, name)

    suspend fun renameMenuCategory(categoryId: Int, name: String) =
        ownerApi.renameMenuCategory(categoryId, name)

    suspend fun deleteMenuCategory(categoryId: Int) =
        ownerApi.deleteMenuCategory(categoryId)
}
