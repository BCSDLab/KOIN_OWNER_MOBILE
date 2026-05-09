package `in`.koreatech.business.feature.findpassword

import `in`.koreatech.business.domain.error.DomainError
import `in`.koreatech.business.domain.model.owner.OwnerProfile
import `in`.koreatech.business.domain.model.owner.OwnerStore
import `in`.koreatech.business.domain.model.signup.ShopSearchResult
import `in`.koreatech.business.domain.repository.AuthRepository
import `in`.koreatech.business.domain.usecase.auth.ChangePasswordBySmsUseCase
import `in`.koreatech.business.domain.usecase.auth.SendFindPasswordSmsUseCase
import `in`.koreatech.business.domain.usecase.auth.VerifyFindPasswordSmsUseCase
import koreatech.business.designsystem.resources.Res
import koreatech.business.designsystem.resources.error_password_invalid
import koreatech.business.designsystem.resources.error_password_mismatch
import koreatech.business.designsystem.resources.error_phone_invalid
import koreatech.business.designsystem.resources.error_sms_code_required
import koreatech.business.designsystem.resources.error_sms_send_failed
import koreatech.business.designsystem.resources.find_password_error_change_failed
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.orbitmvi.orbit.test.test

@OptIn(ExperimentalCoroutinesApi::class)
class FindPasswordViewModelTest {
    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun newViewModel(
        sendSmsError: DomainError? = null,
        verifySmsError: DomainError? = null,
        changePasswordError: DomainError? = null
    ): FindPasswordViewModel {
        val repo = FakeAuthRepository(sendSmsError, verifySmsError, changePasswordError)
        return FindPasswordViewModel(
            sendFindPasswordSmsUseCase = SendFindPasswordSmsUseCase(repo),
            verifyFindPasswordSmsUseCase = VerifyFindPasswordSmsUseCase(repo),
            changePasswordBySmsUseCase = ChangePasswordBySmsUseCase(repo)
        )
    }

    @Test
    fun submitPhoneInvalidEmitsErrorPhoneInvalid() = runTest {
        newViewModel().test(this, FindPasswordState(phoneNumber = "12345")) {
            runOnCreate()
            containerHost.submitPhone()
            expectState { copy(phoneError = "", phoneErrorRes = Res.string.error_phone_invalid) }
        }
    }

    @Test
    fun submitPhoneAdvancesToSmsVerifyOnSuccess() = runTest {
        newViewModel().test(this, FindPasswordState(phoneNumber = "01011113333")) {
            runOnCreate()
            containerHost.submitPhone()
            expectState { copy(isLoading = true, phoneError = "", phoneErrorRes = null) }
            expectState { copy(isLoading = false, step = FindPasswordStep.SmsVerify) }
        }
    }

    @Test
    fun submitPhoneFailureWithEmptyMessageFallsBackToSendFailedRes() = runTest {
        newViewModel(sendSmsError = DomainError.Network(""))
            .test(this, FindPasswordState(phoneNumber = "01011113333")) {
                runOnCreate()
                containerHost.submitPhone()
                expectState { copy(isLoading = true, phoneError = "", phoneErrorRes = null) }
                expectState {
                    copy(
                        phoneError = "",
                        phoneErrorRes = Res.string.error_sms_send_failed,
                        isLoading = false
                    )
                }
            }
    }

    @Test
    fun submitSmsTooShortEmitsCodeRequiredRes() = runTest {
        newViewModel().test(
            this,
            FindPasswordState(step = FindPasswordStep.SmsVerify, smsCode = "12")
        ) {
            runOnCreate()
            containerHost.submitSms()
            expectState { copy(smsError = "", smsErrorRes = Res.string.error_sms_code_required) }
        }
    }

    @Test
    fun submitSmsAdvancesToNewPasswordOnSuccess() = runTest {
        newViewModel().test(
            this,
            FindPasswordState(
                step = FindPasswordStep.SmsVerify,
                phoneNumber = "01011113333",
                smsCode = "123456"
            )
        ) {
            runOnCreate()
            containerHost.submitSms()
            expectState { copy(isLoading = true, smsError = "", smsErrorRes = null) }
            expectState { copy(isLoading = false, step = FindPasswordStep.NewPassword) }
        }
    }

    @Test
    fun submitNewPasswordInvalidEmitsPasswordInvalidRes() = runTest {
        newViewModel().test(
            this,
            FindPasswordState(step = FindPasswordStep.NewPassword, newPassword = "abc")
        ) {
            runOnCreate()
            containerHost.submitNewPassword()
            expectState {
                copy(passwordError = "", passwordErrorRes = Res.string.error_password_invalid)
            }
        }
    }

    @Test
    fun submitNewPasswordMismatchEmitsMismatchRes() = runTest {
        newViewModel().test(
            this,
            FindPasswordState(
                step = FindPasswordStep.NewPassword,
                newPassword = "abc12!@",
                newPasswordConfirm = "different"
            )
        ) {
            runOnCreate()
            containerHost.submitNewPassword()
            expectState {
                copy(passwordError = "", passwordErrorRes = Res.string.error_password_mismatch)
            }
        }
    }

    @Test
    fun submitNewPasswordAdvancesToCompleteOnSuccess() = runTest {
        newViewModel().test(
            this,
            FindPasswordState(
                step = FindPasswordStep.NewPassword,
                phoneNumber = "01011113333",
                newPassword = "abc12!@",
                newPasswordConfirm = "abc12!@"
            )
        ) {
            runOnCreate()
            containerHost.submitNewPassword()
            expectState { copy(isLoading = true, passwordError = "", passwordErrorRes = null) }
            expectState { copy(isLoading = false, step = FindPasswordStep.Complete) }
        }
    }

    @Test
    fun submitNewPasswordFailureFallsBackToChangeFailedRes() = runTest {
        newViewModel(changePasswordError = DomainError.Network("")).test(
            this,
            FindPasswordState(
                step = FindPasswordStep.NewPassword,
                phoneNumber = "01011113333",
                newPassword = "abc12!@",
                newPasswordConfirm = "abc12!@"
            )
        ) {
            runOnCreate()
            containerHost.submitNewPassword()
            expectState { copy(isLoading = true, passwordError = "", passwordErrorRes = null) }
            expectState {
                copy(
                    passwordError = "",
                    passwordErrorRes = Res.string.find_password_error_change_failed,
                    isLoading = false
                )
            }
        }
    }
}

private class FakeAuthRepository(
    private val sendSmsError: DomainError? = null,
    private val verifySmsError: DomainError? = null,
    private val changePasswordError: DomainError? = null
) : AuthRepository {
    override suspend fun signIn(phoneNumber: String, password: String) = Unit
    override suspend fun signOut() = Unit
    override suspend fun deleteAccount() = Unit
    override suspend fun checkPhoneExists(phoneNumber: String): Boolean = false
    override suspend fun sendSignupSms(phoneNumber: String) = Unit
    override suspend fun verifySmsCode(phoneNumber: String, code: String): String = ""
    override suspend fun register(
        phoneNumber: String,
        password: String,
        name: String,
        companyNumber: String,
        shopNumber: String,
        shopId: Int?,
        shopName: String,
        attachmentUrls: List<String>
    ) = Unit

    override suspend fun sendFindPasswordSms(phoneNumber: String) {
        sendSmsError?.let { throw it }
    }

    override suspend fun verifyFindPasswordSms(phoneNumber: String, code: String) {
        verifySmsError?.let { throw it }
    }

    override suspend fun changePasswordBySms(phoneNumber: String, password: String) {
        changePasswordError?.let { throw it }
    }
}

@Suppress("unused")
private class FakeOwnerRepositoryUnused : `in`.koreatech.business.domain.repository.OwnerRepository {
    override suspend fun getShopList(): List<OwnerStore> = emptyList()
    override suspend fun getOwnerProfile(): OwnerProfile = throw UnsupportedOperationException()
    override suspend fun getRequiredVersion(): String = ""
    override suspend fun uploadFile(fileName: String, mimeType: String, bytes: ByteArray): String = ""
    override suspend fun searchShops(query: String): List<ShopSearchResult> = emptyList()
}
