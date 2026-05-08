package `in`.koreatech.business.feature.signup

import `in`.koreatech.business.domain.error.DomainError
import `in`.koreatech.business.domain.model.owner.OwnerProfile
import `in`.koreatech.business.domain.model.owner.OwnerStore
import `in`.koreatech.business.domain.model.signup.ShopSearchResult
import `in`.koreatech.business.domain.repository.AuthRepository
import `in`.koreatech.business.domain.repository.OwnerRepository
import `in`.koreatech.business.domain.usecase.auth.CheckPhoneExistsUseCase
import `in`.koreatech.business.domain.usecase.auth.RegisterUseCase
import `in`.koreatech.business.domain.usecase.auth.SendSignupSmsUseCase
import `in`.koreatech.business.domain.usecase.auth.SignOutUseCase
import `in`.koreatech.business.domain.usecase.auth.VerifySignupSmsUseCase
import `in`.koreatech.business.domain.usecase.owner.SearchShopsUseCase
import `in`.koreatech.business.domain.usecase.owner.UploadFileUseCase
import koreatech.business.designsystem.resources.Res
import koreatech.business.designsystem.resources.error_password_invalid
import koreatech.business.designsystem.resources.error_password_mismatch
import koreatech.business.designsystem.resources.error_phone_invalid
import koreatech.business.designsystem.resources.error_sms_code_required
import koreatech.business.designsystem.resources.signup_error_attach_required
import koreatech.business.designsystem.resources.signup_error_business_number_invalid
import koreatech.business.designsystem.resources.signup_error_name_required
import koreatech.business.designsystem.resources.signup_error_phone_already_registered
import koreatech.business.designsystem.resources.signup_error_register_failed
import koreatech.business.designsystem.resources.signup_error_store_name_required
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.orbitmvi.orbit.test.test

@OptIn(ExperimentalCoroutinesApi::class)
class SignupViewModelTest {
    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun newViewModel(
        phoneAlreadyExists: Boolean = false,
        registerError: DomainError? = null
    ): Pair<SignupViewModel, FakeSignupAuthRepository> {
        val authRepo = FakeSignupAuthRepository(
            phoneAlreadyExists = phoneAlreadyExists,
            registerError = registerError
        )
        val ownerRepo = FakeSignupOwnerRepository()
        val vm = SignupViewModel(
            checkPhoneExistsUseCase = CheckPhoneExistsUseCase(authRepo),
            sendSignupSmsUseCase = SendSignupSmsUseCase(authRepo),
            verifySignupSmsUseCase = VerifySignupSmsUseCase(authRepo),
            registerUseCase = RegisterUseCase(authRepo),
            signOutUseCase = SignOutUseCase(authRepo),
            searchShopsUseCase = SearchShopsUseCase(ownerRepo),
            uploadFileUseCase = UploadFileUseCase(ownerRepo)
        )
        return vm to authRepo
    }

    @Test
    fun submitPhoneInvalidEmitsPhoneInvalidRes() = runTest {
        val (vm, _) = newViewModel()
        vm.test(this, SignupUiState(phoneNumber = "12345")) {
            containerHost.submitPhone()
            expectState { copy(phoneError = "", phoneErrorRes = Res.string.error_phone_invalid) }
        }
    }

    @Test
    fun submitPhoneAlreadyRegisteredEmitsAlreadyRegisteredRes() = runTest {
        val (vm, _) = newViewModel(phoneAlreadyExists = true)
        vm.test(this, SignupUiState(phoneNumber = "01011113333")) {
            containerHost.submitPhone()
            expectState { copy(isLoading = true, phoneError = "", phoneErrorRes = null) }
            expectState {
                copy(
                    isLoading = false,
                    phoneError = "",
                    phoneErrorRes = Res.string.signup_error_phone_already_registered
                )
            }
        }
    }

    @Test
    fun submitSmsTooShortEmitsCodeRequiredRes() = runTest {
        val (vm, _) = newViewModel()
        vm.test(this, SignupUiState(smsCode = "12")) {
            containerHost.submitSms()
            expectState { copy(smsError = "", smsErrorRes = Res.string.error_sms_code_required) }
        }
    }

    @Test
    fun submitPasswordWithBlankNameEmitsNameRequiredRes() = runTest {
        val (vm, _) = newViewModel()
        vm.test(this, SignupUiState(name = "", password = "abc12!@", passwordConfirm = "abc12!@")) {
            containerHost.submitPassword()
            expectState {
                copy(passwordError = "", passwordErrorRes = Res.string.signup_error_name_required)
            }
        }
    }

    @Test
    fun submitPasswordInvalidEmitsPasswordInvalidRes() = runTest {
        val (vm, _) = newViewModel()
        vm.test(this, SignupUiState(name = "홍길동", password = "abc")) {
            containerHost.submitPassword()
            expectState {
                copy(passwordError = "", passwordErrorRes = Res.string.error_password_invalid)
            }
        }
    }

    @Test
    fun submitPasswordMismatchEmitsMismatchRes() = runTest {
        val (vm, _) = newViewModel()
        vm.test(
            this,
            SignupUiState(
                name = "홍길동",
                password = "abc12!@",
                passwordConfirm = "different"
            )
        ) {
            containerHost.submitPassword()
            expectState {
                copy(passwordError = "", passwordErrorRes = Res.string.error_password_mismatch)
            }
        }
    }

    @Test
    fun submitBusinessNumberInvalidEmitsRes() = runTest {
        val (vm, _) = newViewModel()
        vm.test(this, SignupUiState(businessNumber = "12")) {
            containerHost.submitBusinessNumber()
            expectState {
                copy(
                    businessNumberError = "",
                    businessNumberErrorRes = Res.string.signup_error_business_number_invalid
                )
            }
        }
    }

    @Test
    fun submitStoreNameBlankEmitsRes() = runTest {
        val (vm, _) = newViewModel()
        vm.test(this, SignupUiState(storeName = "")) {
            containerHost.submitStoreName()
            expectState {
                copy(
                    storeNameError = "",
                    storeNameErrorRes = Res.string.signup_error_store_name_required
                )
            }
        }
    }

    @Test
    fun submitAttachFileEmptyEmitsRes() = runTest {
        val (vm, _) = newViewModel()
        vm.test(this, SignupUiState(attachedFiles = emptyList())) {
            containerHost.submitAttachFile()
            expectState {
                copy(
                    attachFileError = "",
                    attachFileErrorRes = Res.string.signup_error_attach_required
                )
            }
        }
    }

    @Test
    fun submitAttachFileFailureFallsBackToRegisterFailedRes() = runTest {
        val (vm, _) = newViewModel(registerError = DomainError.Network(""))
        val fakeFile = `in`.koreatech.business.platform.PlatformFile(
            name = "사업자등록증.jpg",
            mimeType = "image/jpeg",
            bytes = ByteArray(0)
        )
        vm.test(
            this,
            SignupUiState(
                phoneNumber = "01011113333",
                password = "abc12!@",
                name = "홍길동",
                businessNumber = "1234567890",
                selectedShopName = "매장",
                attachedFiles = listOf(fakeFile)
            )
        ) {
            containerHost.submitAttachFile()
            expectState { copy(isLoading = true, attachFileError = "", attachFileErrorRes = null) }
            expectState {
                copy(
                    attachFileError = "",
                    attachFileErrorRes = Res.string.signup_error_register_failed,
                    isLoading = false
                )
            }
        }
    }

    @Test
    fun toggleAllTermsFlipsAllFromUnagreedToAgreed() = runTest {
        val (vm, _) = newViewModel()
        val initial = SignupUiState()
        vm.test(this, initial) {
            containerHost.onToggleAllTerms()
            expectState {
                copy(terms = initial.terms.map { it.copy(isAgreed = true) })
            }
            assertTrue(initial.terms.size == 2)
        }
    }
}

private class FakeSignupAuthRepository(
    private val phoneAlreadyExists: Boolean = false,
    private val registerError: DomainError? = null
) : AuthRepository {
    override suspend fun signIn(phoneNumber: String, password: String) = Unit
    override suspend fun signOut() = Unit
    override suspend fun deleteAccount() = Unit
    override suspend fun checkPhoneExists(phoneNumber: String): Boolean = phoneAlreadyExists
    override suspend fun sendSignupSms(phoneNumber: String) = Unit
    override suspend fun verifySmsCode(phoneNumber: String, code: String): String = "fake-token"
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
        registerError?.let { throw it }
    }

    override suspend fun sendFindPasswordSms(phoneNumber: String) = Unit
    override suspend fun verifyFindPasswordSms(phoneNumber: String, code: String) = Unit
    override suspend fun changePasswordBySms(phoneNumber: String, password: String) = Unit
}

private class FakeSignupOwnerRepository : OwnerRepository {
    override suspend fun getShopList(): List<OwnerStore> = emptyList()
    override suspend fun getOwnerProfile(): OwnerProfile = throw UnsupportedOperationException()
    override suspend fun getRequiredVersion(): String = ""
    override suspend fun uploadFile(fileName: String, mimeType: String, bytes: ByteArray): String = "https://example.com/$fileName"
    override suspend fun searchShops(query: String): List<ShopSearchResult> = emptyList()
}
