package `in`.koreatech.business.feature.signup

import `in`.koreatech.business.domain.model.signup.ShopSearchResult
import `in`.koreatech.business.platform.PlatformFile
import koreatech.business.designsystem.resources.Res
import koreatech.business.designsystem.resources.signup_term_privacy_label
import koreatech.business.designsystem.resources.signup_term_service_label
import org.jetbrains.compose.resources.StringResource

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

data class SignupState(
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
