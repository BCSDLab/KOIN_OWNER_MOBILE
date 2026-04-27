package `in`.koreatech.business.feature.signup

import androidx.lifecycle.ViewModel
import `in`.koreatech.business.domain.model.signup.ShopSearchResult
import `in`.koreatech.business.domain.repository.AuthRepository
import `in`.koreatech.business.domain.repository.OwnerRepository
import `in`.koreatech.business.platform.PlatformFile
import `in`.koreatech.business.ui.util.BusinessFormatters
import `in`.koreatech.business.ui.util.BusinessValidators
import `in`.koreatech.business.ui.util.blockingIntent
import koin_owner_mobile.composeapp.generated.resources.Res
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

enum class SignupStep {
    Terms,
    AccountSetup,
    SmsVerify,
    EnterPassword,
    BusinessNumber,
    StoreName,
    SearchStore,
    AttachFile,
    Complete;

    val route: String
        get() = when (this) {
            Terms -> "terms"
            AccountSetup -> "account"
            SmsVerify -> "sms-verify"
            EnterPassword -> "enter-password"
            BusinessNumber -> "business-number"
            StoreName -> "store-name"
            SearchStore -> "search-store"
            AttachFile -> "attach-file"
            Complete -> "complete"
        }
}

data class TermItem(
    val id: String,
    val title: String,
    val content: String,
    val isRequired: Boolean,
    val isAgreed: Boolean = false,
    val isExpanded: Boolean = false
)

data class SignupUiState(
    val step: SignupStep = SignupStep.Terms,
    val terms: List<TermItem> = listOf(
        TermItem("service", "서비스 이용약관 (필수)", "", isRequired = true),
        TermItem("privacy", "개인정보 처리방침 (필수)", "", isRequired = true)
    ),
    val phoneNumber: String = "",
    val phoneError: String = "",
    val smsCode: String = "",
    val smsToken: String = "",
    val smsError: String = "",
    val name: String = "",
    val password: String = "",
    val passwordConfirm: String = "",
    val isPasswordVisible: Boolean = false,
    val isPasswordConfirmVisible: Boolean = false,
    val passwordError: String = "",
    val businessNumber: String = "",
    val businessNumberError: String = "",
    val storeName: String = "",
    val storeNameError: String = "",
    val searchResults: List<ShopSearchResult> = emptyList(),
    val selectedShopId: Int? = null,
    val selectedShopName: String = "",
    val shopPhoneNumber: String = "",
    val attachedFiles: List<PlatformFile> = emptyList(),
    val attachFileError: String = "",
    val isLoading: Boolean = false
) {
    val allTermsAgreed: Boolean
        get() = terms.all { it.isAgreed }

    val requiredTermsAgreed: Boolean
        get() = terms.filter { it.isRequired }.all { it.isAgreed }
}

class SignupViewModel(
    private val authRepository: AuthRepository,
    private val ownerRepository: OwnerRepository
) : ViewModel(),
    ContainerHost<SignupUiState, Nothing> {
    override val container = container<SignupUiState, Nothing>(SignupUiState())

    init {
        loadTermsContent()
    }

    private fun loadTermsContent() = intent {
        try {
            val serviceText = Res.readBytes("files/Terms_koin_sign_up.txt").decodeToString()
            val privacyText = Res.readBytes("files/Terms_personal_information.txt").decodeToString()
            reduce {
                state.copy(
                    terms = state.terms.map { term ->
                        when (term.id) {
                            "service" -> term.copy(content = serviceText)
                            "privacy" -> term.copy(content = privacyText)
                            else -> term
                        }
                    }
                )
            }
        } catch (_: Exception) {
            // keep empty content if resource loading fails
        }
    }

    fun onToggleAllTerms() = intent(registerIdling = false) {
        val allAgreed = state.allTermsAgreed
        reduce { state.copy(terms = state.terms.map { it.copy(isAgreed = !allAgreed) }) }
    }

    fun onToggleTerm(id: String) = intent(registerIdling = false) {
        reduce {
            state.copy(
                terms = state.terms.map {
                    if (it.id == id) it.copy(isAgreed = !it.isAgreed) else it
                }
            )
        }
    }

    fun onToggleTermExpand(id: String) = intent(registerIdling = false) {
        reduce {
            state.copy(
                terms = state.terms.map {
                    if (it.id == id) it.copy(isExpanded = !it.isExpanded) else it
                }
            )
        }
    }

    fun onPhoneNumberChanged(value: String) = blockingIntent {
        reduce {
            state.copy(
                phoneNumber = BusinessFormatters.digitsOnly(value, 11),
                phoneError = ""
            )
        }
    }

    fun onSmsCodeChanged(value: String) = blockingIntent {
        reduce { state.copy(smsCode = BusinessFormatters.digitsOnly(value, 6), smsError = "") }
    }

    fun onNameChanged(value: String) = blockingIntent {
        reduce { state.copy(name = value, passwordError = "") }
    }

    fun onPasswordChanged(value: String) = blockingIntent {
        reduce { state.copy(password = value, passwordError = "") }
    }

    fun onPasswordConfirmChanged(value: String) = blockingIntent {
        reduce { state.copy(passwordConfirm = value, passwordError = "") }
    }

    fun onTogglePasswordVisibility() = blockingIntent {
        reduce { state.copy(isPasswordVisible = !state.isPasswordVisible) }
    }

    fun onTogglePasswordConfirmVisibility() = blockingIntent {
        reduce { state.copy(isPasswordConfirmVisible = !state.isPasswordConfirmVisible) }
    }

    fun onBusinessNumberChanged(value: String) = blockingIntent {
        reduce {
            state.copy(
                businessNumber = BusinessFormatters.digitsOnly(value, 10),
                businessNumberError = ""
            )
        }
    }

    fun onStoreNameChanged(value: String) = blockingIntent {
        reduce { state.copy(storeName = value, storeNameError = "", searchResults = emptyList()) }
    }

    fun onSelectShop(shop: ShopSearchResult) {
        intent(registerIdling = false) {
            reduce { state.copy(selectedShopId = shop.id, selectedShopName = shop.name) }
        }
        navigateNext()
    }

    fun onEnterShopManually() = intent(registerIdling = false) {
        reduce { state.copy(selectedShopId = null, selectedShopName = state.storeName) }
        val target = when (state.step) {
            SignupStep.StoreName -> SignupStep.AttachFile
            else -> SignupStep.AttachFile
        }
        reduce { state.copy(step = target) }
    }

    fun onShopPhoneNumberChanged(value: String) = blockingIntent {
        reduce {
            state.copy(
                shopPhoneNumber = BusinessFormatters.digitsOnly(value, 11),
                attachFileError = ""
            )
        }
    }

    fun onAddFile(file: PlatformFile) = intent(registerIdling = false) {
        reduce {
            if (state.attachedFiles.size < 5) {
                state.copy(attachedFiles = state.attachedFiles + file)
            } else {
                state
            }
        }
    }

    fun onRemoveFile(file: PlatformFile) = intent(registerIdling = false) {
        reduce { state.copy(attachedFiles = state.attachedFiles - file) }
    }

    fun navigateNext() = intent(registerIdling = false) {
        val next = when (state.step) {
            SignupStep.Terms -> SignupStep.AccountSetup
            SignupStep.AccountSetup -> SignupStep.SmsVerify
            SignupStep.SmsVerify -> SignupStep.EnterPassword
            SignupStep.EnterPassword -> SignupStep.BusinessNumber
            SignupStep.BusinessNumber -> SignupStep.StoreName
            SignupStep.StoreName -> SignupStep.SearchStore
            SignupStep.SearchStore -> SignupStep.AttachFile
            SignupStep.AttachFile -> SignupStep.Complete
            SignupStep.Complete -> SignupStep.Complete
        }
        reduce { state.copy(step = next) }
    }

    fun navigateBack(): Boolean {
        val prev = when (container.stateFlow.value.step) {
            SignupStep.Terms -> return false
            SignupStep.AccountSetup -> SignupStep.Terms
            SignupStep.SmsVerify -> SignupStep.AccountSetup
            SignupStep.EnterPassword -> SignupStep.SmsVerify
            SignupStep.BusinessNumber -> SignupStep.EnterPassword
            SignupStep.StoreName -> SignupStep.BusinessNumber
            SignupStep.SearchStore -> SignupStep.StoreName
            SignupStep.AttachFile -> SignupStep.SearchStore
            SignupStep.Complete -> SignupStep.AttachFile
        }
        intent(registerIdling = false) { reduce { state.copy(step = prev) } }
        return true
    }

    fun submitTerms() {
        if (container.stateFlow.value.requiredTermsAgreed) navigateNext()
    }

    fun submitPhone() = intent {
        val phone = state.phoneNumber
        if (!BusinessValidators.isValidPhone(phone)) {
            reduce { state.copy(phoneError = "올바른 전화번호를 입력해주세요.") }
            return@intent
        }
        reduce { state.copy(isLoading = true, phoneError = "") }
        try {
            val exists = authRepository.checkPhoneExists(phone)
            if (exists) {
                reduce {
                    state.copy(
                        isLoading = false,
                        phoneError = "이미 가입된 번호입니다."
                    )
                }
                return@intent
            }
            authRepository.sendSignupSms(phone)
            reduce { state.copy(isLoading = false) }
            navigateNext()
        } catch (e: Exception) {
            reduce { state.copy(phoneError = e.message ?: "SMS 발송에 실패했습니다.", isLoading = false) }
        }
    }

    fun submitSms() = intent {
        if (state.smsCode.length < 6) {
            reduce { state.copy(smsError = "인증번호 6자리를 입력해주세요.") }
            return@intent
        }
        reduce { state.copy(isLoading = true, smsError = "") }
        try {
            val token = authRepository.verifySmsCode(state.phoneNumber, state.smsCode)
            reduce { state.copy(smsToken = token, isLoading = false) }
            navigateNext()
        } catch (e: Exception) {
            reduce { state.copy(smsError = e.message ?: "인증번호가 올바르지 않습니다.", isLoading = false) }
        }
    }

    fun submitPassword() = intent(registerIdling = false) {
        when {
            state.name.isBlank() -> reduce { state.copy(passwordError = "이름을 입력해주세요.") }
            !BusinessValidators.isValidPassword(state.password) -> reduce {
                state.copy(passwordError = "영문, 숫자, 특수문자를 포함한 6~18자 비밀번호를 입력해주세요.")
            }
            state.password != state.passwordConfirm -> reduce { state.copy(passwordError = "비밀번호가 일치하지 않습니다.") }
            else -> {
                reduce { state.copy(passwordError = "") }
                navigateNext()
            }
        }
    }

    fun submitBusinessNumber() = intent(registerIdling = false) {
        if (!BusinessValidators.isValidBusinessNumber(state.businessNumber)) {
            reduce { state.copy(businessNumberError = "올바른 사업자번호를 입력해주세요.") }
            return@intent
        }
        reduce { state.copy(businessNumberError = "") }
        navigateNext()
    }

    fun submitStoreName() = intent {
        val name = state.storeName
        if (name.isBlank()) {
            reduce { state.copy(storeNameError = "매장 이름을 입력해주세요.") }
            return@intent
        }
        reduce { state.copy(isLoading = true, storeNameError = "", searchResults = emptyList()) }
        try {
            val results = ownerRepository.searchShops(name)
            reduce { state.copy(searchResults = results, isLoading = false) }
        } catch (e: Exception) {
            reduce { state.copy(storeNameError = e.message ?: "검색 중 오류가 발생했습니다.", isLoading = false) }
        }
    }

    fun submitAttachFile() = intent {
        if (state.attachedFiles.isEmpty()) {
            reduce { state.copy(attachFileError = "사업자 등록증을 첨부해주세요.") }
            return@intent
        }
        reduce { state.copy(isLoading = true, attachFileError = "") }
        try {
            val uploadedUrls = state.attachedFiles.map { file ->
                ownerRepository.uploadFile(
                    fileName = file.name,
                    mimeType = file.mimeType,
                    bytes = file.bytes
                )
            }
            authRepository.register(
                phoneNumber = state.phoneNumber,
                password = state.password,
                name = state.name,
                companyNumber = BusinessFormatters.formatBusinessNumber(state.businessNumber),
                shopNumber = state.shopPhoneNumber,
                shopId = state.selectedShopId,
                shopName = state.selectedShopName,
                attachmentUrls = uploadedUrls
            )
            // SMS 인증 중 발급된 임시 토큰을 폐기해서 자동 로그인 상태가 되지 않도록 한다.
            runCatching { authRepository.signOut() }
            reduce { state.copy(isLoading = false) }
            navigateNext()
        } catch (e: Exception) {
            reduce { state.copy(attachFileError = e.message ?: "회원가입에 실패했습니다.", isLoading = false) }
        }
    }
}
