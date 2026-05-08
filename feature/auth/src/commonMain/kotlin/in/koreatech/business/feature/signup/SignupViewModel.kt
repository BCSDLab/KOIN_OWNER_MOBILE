package `in`.koreatech.business.feature.signup

import androidx.lifecycle.ViewModel
import `in`.koreatech.business.domain.model.signup.ShopSearchResult
import `in`.koreatech.business.domain.usecase.auth.CheckPhoneExistsUseCase
import `in`.koreatech.business.domain.usecase.auth.RegisterUseCase
import `in`.koreatech.business.domain.usecase.auth.SendSignupSmsUseCase
import `in`.koreatech.business.domain.usecase.auth.SignOutUseCase
import `in`.koreatech.business.domain.usecase.auth.VerifySignupSmsUseCase
import `in`.koreatech.business.domain.usecase.owner.SearchShopsUseCase
import `in`.koreatech.business.domain.usecase.owner.UploadFileUseCase
import `in`.koreatech.business.platform.PlatformFile
import `in`.koreatech.business.ui.util.BusinessFormatters
import `in`.koreatech.business.ui.util.BusinessValidators
import koreatech.business.designsystem.resources.Res
import koreatech.business.designsystem.resources.error_password_invalid
import koreatech.business.designsystem.resources.error_password_mismatch
import koreatech.business.designsystem.resources.error_phone_invalid
import koreatech.business.designsystem.resources.error_sms_code_invalid
import koreatech.business.designsystem.resources.error_sms_code_required
import koreatech.business.designsystem.resources.error_sms_send_failed
import koreatech.business.designsystem.resources.signup_error_attach_required
import koreatech.business.designsystem.resources.signup_error_business_number_invalid
import koreatech.business.designsystem.resources.signup_error_name_required
import koreatech.business.designsystem.resources.signup_error_phone_already_registered
import koreatech.business.designsystem.resources.signup_error_register_failed
import koreatech.business.designsystem.resources.signup_error_search_failed
import koreatech.business.designsystem.resources.signup_error_store_name_required
import koreatech.business.designsystem.resources.signup_term_privacy_label
import koreatech.business.designsystem.resources.signup_term_service_label
import org.jetbrains.compose.resources.StringResource
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.Syntax
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

    val route: String get() = when (this) {
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
    val titleRes: StringResource,
    val content: String,
    val isRequired: Boolean,
    val isAgreed: Boolean = false,
    val isExpanded: Boolean = false
)

data class SignupUiState(
    val step: SignupStep = SignupStep.Terms,
    val terms: List<TermItem> = listOf(
        TermItem("service", Res.string.signup_term_service_label, "", isRequired = true),
        TermItem("privacy", Res.string.signup_term_privacy_label, "", isRequired = true)
    ),
    val phoneNumber: String = "",
    val phoneError: String = "",
    val phoneErrorRes: StringResource? = null,
    val smsCode: String = "",
    val smsToken: String = "",
    val smsError: String = "",
    val smsErrorRes: StringResource? = null,
    val name: String = "",
    val password: String = "",
    val passwordConfirm: String = "",
    val isPasswordVisible: Boolean = false,
    val isPasswordConfirmVisible: Boolean = false,
    val passwordError: String = "",
    val passwordErrorRes: StringResource? = null,
    val businessNumber: String = "",
    val businessNumberError: String = "",
    val businessNumberErrorRes: StringResource? = null,
    val storeName: String = "",
    val storeNameError: String = "",
    val storeNameErrorRes: StringResource? = null,
    val searchResults: List<ShopSearchResult> = emptyList(),
    val selectedShopId: Int? = null,
    val selectedShopName: String = "",
    val shopPhoneNumber: String = "",
    val attachedFiles: List<PlatformFile> = emptyList(),
    val attachFileError: String = "",
    val attachFileErrorRes: StringResource? = null,
    val isLoading: Boolean = false
) {
    val allTermsAgreed: Boolean get() = terms.all { it.isAgreed }
    val requiredTermsAgreed: Boolean get() = terms.filter { it.isRequired }.all { it.isAgreed }
}

class SignupViewModel(
    private val checkPhoneExistsUseCase: CheckPhoneExistsUseCase,
    private val sendSignupSmsUseCase: SendSignupSmsUseCase,
    private val verifySignupSmsUseCase: VerifySignupSmsUseCase,
    private val registerUseCase: RegisterUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val searchShopsUseCase: SearchShopsUseCase,
    private val uploadFileUseCase: UploadFileUseCase
) : ViewModel(),
    ContainerHost<SignupUiState, Nothing> {
    override val container = container<SignupUiState, Nothing>(
        initialState = SignupUiState(),
        onCreate = { loadTermsContent() }
    )

    private suspend fun Syntax<SignupUiState, Nothing>.loadTermsContent() {
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
        } catch (_: Exception) { }
    }

    fun onToggleAllTerms() = intent(registerIdling = false) {
        val allAgreed = state.allTermsAgreed
        reduce { state.copy(terms = state.terms.map { it.copy(isAgreed = !allAgreed) }) }
    }

    fun onToggleTerm(id: String) = intent(registerIdling = false) {
        reduce { state.copy(terms = state.terms.map { if (it.id == id) it.copy(isAgreed = !it.isAgreed) else it }) }
    }

    fun onToggleTermExpand(id: String) = intent(registerIdling = false) {
        reduce { state.copy(terms = state.terms.map { if (it.id == id) it.copy(isExpanded = !it.isExpanded) else it }) }
    }

    fun onPhoneNumberChanged(value: String) = intent {
        reduce {
            state.copy(
                phoneNumber = BusinessFormatters.digitsOnly(value, 11),
                phoneError = "",
                phoneErrorRes = null
            )
        }
    }

    fun onSmsCodeChanged(value: String) = intent {
        reduce {
            state.copy(
                smsCode = BusinessFormatters.digitsOnly(value, 6),
                smsError = "",
                smsErrorRes = null
            )
        }
    }

    fun onNameChanged(value: String) = intent {
        reduce { state.copy(name = value, passwordError = "", passwordErrorRes = null) }
    }

    fun onPasswordChanged(value: String) = intent {
        reduce { state.copy(password = value, passwordError = "", passwordErrorRes = null) }
    }

    fun onPasswordConfirmChanged(value: String) = intent {
        reduce { state.copy(passwordConfirm = value, passwordError = "", passwordErrorRes = null) }
    }

    fun onTogglePasswordVisibility() = intent {
        reduce { state.copy(isPasswordVisible = !state.isPasswordVisible) }
    }

    fun onTogglePasswordConfirmVisibility() = intent {
        reduce { state.copy(isPasswordConfirmVisible = !state.isPasswordConfirmVisible) }
    }

    fun onBusinessNumberChanged(value: String) = intent {
        reduce {
            state.copy(
                businessNumber = BusinessFormatters.digitsOnly(value, 10),
                businessNumberError = "",
                businessNumberErrorRes = null
            )
        }
    }

    fun onStoreNameChanged(value: String) = intent {
        reduce {
            state.copy(
                storeName = value,
                storeNameError = "",
                storeNameErrorRes = null,
                searchResults = emptyList()
            )
        }
    }

    fun onSelectShop(shop: ShopSearchResult) {
        intent(registerIdling = false) { reduce { state.copy(selectedShopId = shop.id, selectedShopName = shop.name) } }
        navigateNext()
    }

    fun onEnterShopManually() = intent(registerIdling = false) {
        reduce { state.copy(selectedShopId = null, selectedShopName = state.storeName, step = SignupStep.AttachFile) }
    }

    fun onShopPhoneNumberChanged(value: String) = intent {
        reduce {
            state.copy(
                shopPhoneNumber = BusinessFormatters.digitsOnly(value, 11),
                attachFileError = "",
                attachFileErrorRes = null
            )
        }
    }

    fun onAddFile(file: PlatformFile) = intent(registerIdling = false) {
        reduce { if (state.attachedFiles.size < 5) state.copy(attachedFiles = state.attachedFiles + file) else state }
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
            reduce { state.copy(phoneError = "", phoneErrorRes = Res.string.error_phone_invalid) }
            return@intent
        }
        reduce { state.copy(isLoading = true, phoneError = "", phoneErrorRes = null) }
        try {
            val exists = checkPhoneExistsUseCase(phone)
            if (exists) {
                reduce {
                    state.copy(
                        isLoading = false,
                        phoneError = "",
                        phoneErrorRes = Res.string.signup_error_phone_already_registered
                    )
                }
                return@intent
            }
            sendSignupSmsUseCase(phone)
            reduce { state.copy(isLoading = false) }
            navigateNext()
        } catch (e: Exception) {
            val msg = e.message.orEmpty()
            reduce {
                state.copy(
                    phoneError = msg,
                    phoneErrorRes = if (msg.isEmpty()) Res.string.error_sms_send_failed else null,
                    isLoading = false
                )
            }
        }
    }

    fun submitSms() = intent {
        if (state.smsCode.length < 6) {
            reduce { state.copy(smsError = "", smsErrorRes = Res.string.error_sms_code_required) }
            return@intent
        }
        reduce { state.copy(isLoading = true, smsError = "", smsErrorRes = null) }
        try {
            val token = verifySignupSmsUseCase(state.phoneNumber, state.smsCode)
            reduce { state.copy(smsToken = token, isLoading = false) }
            navigateNext()
        } catch (e: Exception) {
            val msg = e.message.orEmpty()
            reduce {
                state.copy(
                    smsError = msg,
                    smsErrorRes = if (msg.isEmpty()) Res.string.error_sms_code_invalid else null,
                    isLoading = false
                )
            }
        }
    }

    fun submitPassword() = intent(registerIdling = false) {
        when {
            state.name.isBlank() -> reduce {
                state.copy(passwordError = "", passwordErrorRes = Res.string.signup_error_name_required)
            }
            !BusinessValidators.isValidPassword(state.password) -> reduce {
                state.copy(passwordError = "", passwordErrorRes = Res.string.error_password_invalid)
            }
            state.password != state.passwordConfirm -> reduce {
                state.copy(passwordError = "", passwordErrorRes = Res.string.error_password_mismatch)
            }
            else -> {
                reduce { state.copy(passwordError = "", passwordErrorRes = null) }
                navigateNext()
            }
        }
    }

    fun submitBusinessNumber() = intent(registerIdling = false) {
        if (!BusinessValidators.isValidBusinessNumber(state.businessNumber)) {
            reduce {
                state.copy(
                    businessNumberError = "",
                    businessNumberErrorRes = Res.string.signup_error_business_number_invalid
                )
            }
            return@intent
        }
        reduce { state.copy(businessNumberError = "", businessNumberErrorRes = null) }
        navigateNext()
    }

    fun submitStoreName() = intent {
        val name = state.storeName
        if (name.isBlank()) {
            reduce {
                state.copy(
                    storeNameError = "",
                    storeNameErrorRes = Res.string.signup_error_store_name_required
                )
            }
            return@intent
        }
        reduce {
            state.copy(
                isLoading = true,
                storeNameError = "",
                storeNameErrorRes = null,
                searchResults = emptyList()
            )
        }
        try {
            val results = searchShopsUseCase(name)
            reduce { state.copy(searchResults = results, isLoading = false) }
        } catch (e: Exception) {
            val msg = e.message.orEmpty()
            reduce {
                state.copy(
                    storeNameError = msg,
                    storeNameErrorRes = if (msg.isEmpty()) Res.string.signup_error_search_failed else null,
                    isLoading = false
                )
            }
        }
    }

    fun submitAttachFile() = intent {
        if (state.attachedFiles.isEmpty()) {
            reduce {
                state.copy(
                    attachFileError = "",
                    attachFileErrorRes = Res.string.signup_error_attach_required
                )
            }
            return@intent
        }
        reduce { state.copy(isLoading = true, attachFileError = "", attachFileErrorRes = null) }
        try {
            val uploadedUrls = state.attachedFiles.map { file ->
                uploadFileUseCase(fileName = file.name, mimeType = file.mimeType, bytes = file.bytes)
            }
            registerUseCase(
                phoneNumber = state.phoneNumber,
                password = state.password,
                name = state.name,
                companyNumber = BusinessFormatters.formatBusinessNumber(state.businessNumber),
                shopNumber = state.shopPhoneNumber,
                shopId = state.selectedShopId,
                shopName = state.selectedShopName,
                attachmentUrls = uploadedUrls
            )
            runCatching { signOutUseCase() }
            reduce { state.copy(isLoading = false) }
            navigateNext()
        } catch (e: Exception) {
            val msg = e.message.orEmpty()
            reduce {
                state.copy(
                    attachFileError = msg,
                    attachFileErrorRes = if (msg.isEmpty()) Res.string.signup_error_register_failed else null,
                    isLoading = false
                )
            }
        }
    }
}
