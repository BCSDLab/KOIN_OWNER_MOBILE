package `in`.koreatech.business.data.repository

import `in`.koreatech.business.data.model.owner.request.OwnerLoginRequest
import `in`.koreatech.business.data.model.signup.request.AttachmentUrl
import `in`.koreatech.business.data.model.signup.request.OwnerRegisterRequest
import `in`.koreatech.business.data.model.signup.request.SendSignupSmsRequest
import `in`.koreatech.business.data.model.signup.request.VerifySmsRequest
import `in`.koreatech.business.data.source.remote.OwnerRemoteDataSource
import `in`.koreatech.business.data.utils.sha256
import `in`.koreatech.business.data.utils.toUserMessage
import `in`.koreatech.business.domain.repository.AuthRepository
import `in`.koreatech.business.domain.repository.TokenRepository

class AuthRepositoryImpl(
    private val ownerRemoteDataSource: OwnerRemoteDataSource,
    private val tokenRepository: TokenRepository
) : AuthRepository {
    override suspend fun signIn(phoneNumber: String, password: String) {
        try {
            val response = ownerRemoteDataSource.signIn(
                request = OwnerLoginRequest(
                    phoneNumber = phoneNumber,
                    password = sha256(password)
                )
            )
            tokenRepository.saveAccessToken(response.token)
            tokenRepository.saveRefreshToken(response.refreshToken)
        } catch (exception: Exception) {
            throw IllegalStateException(exception.toUserMessage(), exception)
        }
    }

    override suspend fun signOut() {
        tokenRepository.saveAccessToken("")
        tokenRepository.saveRefreshToken("")
    }

    override suspend fun deleteAccount() {
        try {
            ownerRemoteDataSource.deleteAccount()
            tokenRepository.saveAccessToken("")
            tokenRepository.saveRefreshToken("")
        } catch (exception: Exception) {
            throw IllegalStateException(exception.toUserMessage(), exception)
        }
    }

    override suspend fun checkPhoneExists(phoneNumber: String): Boolean {
        try {
            return ownerRemoteDataSource.checkPhoneExists(phoneNumber)
        } catch (exception: Exception) {
            throw IllegalStateException(exception.toUserMessage(), exception)
        }
    }

    override suspend fun sendSignupSms(phoneNumber: String) {
        try {
            ownerRemoteDataSource.sendSignupSms(SendSignupSmsRequest(phoneNumber = phoneNumber))
        } catch (exception: Exception) {
            throw IllegalStateException(exception.toUserMessage(), exception)
        }
    }

    override suspend fun verifySmsCode(phoneNumber: String, code: String): String {
        try {
            val token = ownerRemoteDataSource.verifySmsCode(
                VerifySmsRequest(phoneNumber = phoneNumber, certificationCode = code)
            ).token
            tokenRepository.saveAccessToken(token)
            return token
        } catch (exception: Exception) {
            throw IllegalStateException(exception.toUserMessage(), exception)
        }
    }

    override suspend fun register(
        phoneNumber: String,
        password: String,
        name: String,
        companyNumber: String,
        shopNumber: String,
        shopId: Int?,
        shopName: String,
        attachmentUrls: List<String>
    ) {
        try {
            ownerRemoteDataSource.register(
                OwnerRegisterRequest(
                    phoneNumber = phoneNumber,
                    password = sha256(password),
                    name = name,
                    companyNumber = companyNumber,
                    shopNumber = shopNumber,
                    shopId = shopId,
                    shopName = shopName,
                    attachmentUrls = attachmentUrls.map { AttachmentUrl(it) }
                )
            )
        } catch (exception: Exception) {
            throw IllegalStateException(exception.toUserMessage(), exception)
        }
    }

    override suspend fun sendFindPasswordSms(phoneNumber: String) {
        try {
            ownerRemoteDataSource.sendFindPasswordSms(phoneNumber)
        } catch (exception: Exception) {
            throw IllegalStateException(exception.toUserMessage(), exception)
        }
    }

    override suspend fun verifyFindPasswordSms(phoneNumber: String, code: String) {
        try {
            ownerRemoteDataSource.verifyFindPasswordSms(phoneNumber, code)
        } catch (exception: Exception) {
            throw IllegalStateException(exception.toUserMessage(), exception)
        }
    }

    override suspend fun changePasswordBySms(phoneNumber: String, password: String) {
        try {
            ownerRemoteDataSource.changePasswordBySms(phoneNumber, sha256(password))
        } catch (exception: Exception) {
            throw IllegalStateException(exception.toUserMessage(), exception)
        }
    }
}
