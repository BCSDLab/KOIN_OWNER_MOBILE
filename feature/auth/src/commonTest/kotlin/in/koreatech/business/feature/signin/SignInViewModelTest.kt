package `in`.koreatech.business.feature.signin

import `in`.koreatech.business.domain.model.owner.OwnerProfile
import `in`.koreatech.business.domain.model.owner.OwnerStore
import `in`.koreatech.business.domain.model.signup.ShopSearchResult
import `in`.koreatech.business.domain.repository.AuthRepository
import `in`.koreatech.business.domain.repository.OwnerRepository
import `in`.koreatech.business.domain.usecase.auth.SignInUseCase
import `in`.koreatech.business.domain.usecase.owner.GetShopListUseCase
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
class SignInViewModelTest {
    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun newViewModel(): SignInViewModel = SignInViewModel(
        signInUseCase = SignInUseCase(FakeAuthRepository()),
        getShopListUseCase = GetShopListUseCase(FakeOwnerRepository())
    )

    @Test
    fun phoneNumberStripsNonDigitsAndCapsAtElevenChars() = runTest {
        newViewModel().test(this, SignInUiState()) {
            runOnCreate()
            containerHost.onPhoneNumberChanged("010-1111-3333abc99")
            expectState { copy(phoneNumber = "01011113333", errorMessage = "") }
        }
    }

    @Test
    fun submitWithEmptyPhoneEmitsValidationError() = runTest {
        newViewModel().test(this, SignInUiState()) {
            runOnCreate()
            containerHost.submit()
            expectState {
                copy(
                    notValidateField = true,
                    errorMessage = "전화번호를 입력해주세요."
                )
            }
        }
    }

    @Test
    fun submitWithEmptyPasswordEmitsValidationError() = runTest {
        newViewModel().test(
            this,
            SignInUiState(phoneNumber = "01011113333")
        ) {
            runOnCreate()
            containerHost.submit()
            expectState {
                copy(
                    notValidateField = true,
                    errorMessage = "비밀번호를 입력해주세요."
                )
            }
        }
    }
}

private class FakeAuthRepository : AuthRepository {
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

    override suspend fun sendFindPasswordSms(phoneNumber: String) = Unit
    override suspend fun verifyFindPasswordSms(phoneNumber: String, code: String) = Unit
    override suspend fun changePasswordBySms(phoneNumber: String, password: String) = Unit
}

private class FakeOwnerRepository : OwnerRepository {
    override suspend fun getShopList(): List<OwnerStore> = emptyList()
    override suspend fun getOwnerProfile(): OwnerProfile = throw UnsupportedOperationException("not used in this test")

    override suspend fun getRequiredVersion(): String = ""
    override suspend fun uploadFile(fileName: String, mimeType: String, bytes: ByteArray): String = ""
    override suspend fun searchShops(query: String): List<ShopSearchResult> = emptyList()
}
