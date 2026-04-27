package `in`.koreatech.business.domain.repository

interface AuthRepository {
    suspend fun signIn(phoneNumber: String, password: String)

    suspend fun signOut()

    suspend fun deleteAccount()

    suspend fun checkPhoneExists(phoneNumber: String): Boolean

    suspend fun sendSignupSms(phoneNumber: String)

    suspend fun verifySmsCode(phoneNumber: String, code: String): String

    suspend fun register(
        phoneNumber: String,
        password: String,
        name: String,
        companyNumber: String,
        shopNumber: String,
        shopId: Int?,
        shopName: String,
        attachmentUrls: List<String>
    )

    suspend fun sendFindPasswordSms(phoneNumber: String)

    suspend fun verifyFindPasswordSms(phoneNumber: String, code: String)

    suspend fun changePasswordBySms(phoneNumber: String, password: String)
}
