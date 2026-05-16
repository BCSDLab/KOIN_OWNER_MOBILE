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
import org.orbitmvi.orbit.blockingIntent
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.Syntax
import org.orbitmvi.orbit.viewmodel.container

class SignupViewModel(
    private val checkPhoneExistsUseCase: CheckPhoneExistsUseCase,
    private val sendSignupSmsUseCase: SendSignupSmsUseCase,
    private val verifySignupSmsUseCase: VerifySignupSmsUseCase,
    private val registerUseCase: RegisterUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val searchShopsUseCase: SearchShopsUseCase,
    private val uploadFileUseCase: UploadFileUseCase
) : ViewModel(),
    ContainerHost<SignupState, Nothing> {
    override val container = container<SignupState, Nothing>(
        initialState = SignupState(),
        onCreate = { loadTermsContent() }
    )

    private suspend fun Syntax<SignupState, Nothing>.loadTermsContent() {
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

    fun onPhoneNumberChanged(value: String) = blockingIntent {
        reduce {
            state.copy(
                phoneNumber = BusinessFormatters.digitsOnly(value, 11),
                phoneError = "",
                phoneErrorRes = null
            )
        }
    }

    fun onSmsCodeChanged(value: String) = blockingIntent {
        reduce {
            state.copy(
                smsCode = BusinessFormatters.digitsOnly(value, 6),
                smsError = "",
                smsErrorRes = null
            )
        }
    }

    fun onNameChanged(value: String) = blockingIntent {
        reduce { state.copy(name = value, passwordError = "", passwordErrorRes = null) }
    }

    fun onPasswordChanged(value: String) = blockingIntent {
        reduce { state.copy(password = value, passwordError = "", passwordErrorRes = null) }
    }

    fun onPasswordConfirmChanged(value: String) = blockingIntent {
        reduce { state.copy(passwordConfirm = value, passwordError = "", passwordErrorRes = null) }
    }

    fun onTogglePasswordVisibility() = intent {
        reduce { state.copy(isPasswordVisible = !state.isPasswordVisible) }
    }

    fun onTogglePasswordConfirmVisibility() = intent {
        reduce { state.copy(isPasswordConfirmVisible = !state.isPasswordConfirmVisible) }
    }

    fun onBusinessNumberChanged(value: String) = blockingIntent {
        reduce {
            state.copy(
                businessNumber = BusinessFormatters.digitsOnly(value, 10),
                businessNumberError = "",
                businessNumberErrorRes = null
            )
        }
    }

    fun onStoreNameChanged(value: String) = blockingIntent {
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

    fun onShopPhoneNumberChanged(value: String) = blockingIntent {
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
        checkPhoneExistsUseCase(phone)
            .onSuccess { exists -> handlePhoneExistsResult(phone, exists) }
            .onFailure { showPhoneError(it.message.orEmpty()) }
    }

    private fun handlePhoneExistsResult(phone: String, exists: Boolean) = intent {
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
            .onSuccess { advanceAfterPhoneSubmit() }
            .onFailure { showPhoneError(it.message.orEmpty()) }
    }

    private fun advanceAfterPhoneSubmit() = intent {
        reduce { state.copy(isLoading = false) }
        navigateNext()
    }

    private fun showPhoneError(message: String) = intent {
        reduce {
            state.copy(
                phoneError = message,
                phoneErrorRes = if (message.isEmpty()) Res.string.error_sms_send_failed else null,
                isLoading = false
            )
        }
    }

    fun submitSms() = intent {
        if (state.smsCode.length < 6) {
            reduce { state.copy(smsError = "", smsErrorRes = Res.string.error_sms_code_required) }
            return@intent
        }
        reduce { state.copy(isLoading = true, smsError = "", smsErrorRes = null) }
        verifySignupSmsUseCase(state.phoneNumber, state.smsCode)
            .onSuccess { token -> applySmsToken(token) }
            .onFailure { showSmsError(it.message.orEmpty()) }
    }

    private fun applySmsToken(token: String) = intent {
        reduce { state.copy(smsToken = token, isLoading = false) }
        navigateNext()
    }

    private fun showSmsError(message: String) = intent {
        reduce {
            state.copy(
                smsError = message,
                smsErrorRes = if (message.isEmpty()) Res.string.error_sms_code_invalid else null,
                isLoading = false
            )
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
        searchShopsUseCase(name)
            .onSuccess { results -> applySearchResults(results) }
            .onFailure { showStoreNameError(it.message.orEmpty()) }
    }

    private fun applySearchResults(results: List<ShopSearchResult>) = intent {
        reduce { state.copy(searchResults = results, isLoading = false) }
    }

    private fun showStoreNameError(message: String) = intent {
        reduce {
            state.copy(
                storeNameError = message,
                storeNameErrorRes = if (message.isEmpty()) Res.string.signup_error_search_failed else null,
                isLoading = false
            )
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
        uploadAllFilesAndRegister()
    }

    private fun uploadAllFilesAndRegister() = intent {
        val uploadedUrls = mutableListOf<String>()
        for (file in state.attachedFiles) {
            val uploadResult = uploadFileUseCase(fileName = file.name, mimeType = file.mimeType, bytes = file.bytes)
            uploadResult.fold(
                onSuccess = { uploadedUrls.add(it) },
                onFailure = {
                    showAttachFileError(it.message.orEmpty())
                    return@intent
                }
            )
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
            .onSuccess { signOutAndComplete() }
            .onFailure { showAttachFileError(it.message.orEmpty()) }
    }

    private fun signOutAndComplete() = intent {
        signOutUseCase()
        reduce { state.copy(isLoading = false) }
        navigateNext()
    }

    private fun showAttachFileError(message: String) = intent {
        reduce {
            state.copy(
                attachFileError = message,
                attachFileErrorRes = if (message.isEmpty()) Res.string.signup_error_register_failed else null,
                isLoading = false
            )
        }
    }
}
