package `in`.koreatech.business

import androidx.lifecycle.ViewModel
import `in`.koreatech.business.data.di.EncryptedDataStore
import `in`.koreatech.business.domain.repository.AuthRepository
import `in`.koreatech.business.domain.repository.OwnerRepository
import `in`.koreatech.business.domain.repository.TokenRepository
import `in`.koreatech.business.feature.store.shared.ActiveStoreContext
import `in`.koreatech.business.feature.store.shared.StoreSelectionSource
import `in`.koreatech.business.platform.getAppVersion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

sealed class AppSideEffect {
    data object ToLoading : AppSideEffect()
    data object ToForceUpdate : AppSideEffect()
    data object ToSignIn : AppSideEffect()
    data object ToSignUp : AppSideEffect()
    data object ToFindPassword : AppSideEffect()
    data object ToStoreMain : AppSideEffect()
    data object ToStoreRegister : AppSideEffect()
    data class ShowError(val message: String) : AppSideEffect()
}

class AppViewModel(
    private val authRepository: AuthRepository,
    private val ownerRepository: OwnerRepository,
    private val tokenRepository: TokenRepository,
    private val encryptedDataStore: EncryptedDataStore
) : ViewModel(),
    ContainerHost<AppUiState, AppSideEffect> {
    override val container = container<AppUiState, AppSideEffect>(AppUiState())

    init {
        refreshLaunchState()
    }

    fun refreshLaunchState() {
        intent {
            postSideEffect(AppSideEffect.ToLoading)

            if (isForceUpdateRequired()) {
                postSideEffect(AppSideEffect.ToForceUpdate)
                return@intent
            }

            if (hasValidOwnerSession()) {
                val activeStoreContext = restoreActiveStoreContext()
                reduce { state.copy(activeStoreContext = activeStoreContext) }
                postSideEffect(AppSideEffect.ToStoreMain)
            } else {
                reduce { state.copy(activeStoreContext = null) }
                postSideEffect(AppSideEffect.ToSignIn)
            }
        }
    }

    fun navigateToSignUp() {
        intent(registerIdling = false) {
            postSideEffect(AppSideEffect.ToSignUp)
        }
    }

    fun navigateToFindPassword() {
        intent(registerIdling = false) {
            postSideEffect(AppSideEffect.ToFindPassword)
        }
    }

    fun navigateBackToSignIn() {
        intent(registerIdling = false) {
            postSideEffect(AppSideEffect.ToSignIn)
        }
    }

    fun signOut() {
        intent {
            runCatching {
                withContext(Dispatchers.Default) {
                    tokenRepository.saveAccessToken("")
                    tokenRepository.saveRefreshToken("")
                    encryptedDataStore.deleteData(LAST_ACTIVE_STORE_ID_KEY)
                }
            }
            reduce { state.copy(activeStoreContext = null) }
            postSideEffect(AppSideEffect.ToSignIn)
        }
    }

    fun deleteAccount() {
        intent {
            try {
                authRepository.deleteAccount()
                withContext(Dispatchers.Default) {
                    tokenRepository.saveAccessToken("")
                    tokenRepository.saveRefreshToken("")
                    encryptedDataStore.deleteData(LAST_ACTIVE_STORE_ID_KEY)
                }
                reduce { state.copy(activeStoreContext = null) }
                postSideEffect(AppSideEffect.ToSignIn)
            } catch (e: Exception) {
                postSideEffect(AppSideEffect.ShowError(e.message ?: "계정 삭제에 실패했습니다."))
            }
        }
    }

    fun navigateToStoreMainAfterSignIn() {
        refreshLaunchState()
    }

    fun navigateToStoreRegisterAfterSignIn() {
        intent(registerIdling = false) {
            reduce { state.copy(activeStoreContext = null) }
            postSideEffect(AppSideEffect.ToStoreRegister)
        }
    }

    private suspend fun hasValidOwnerSession(): Boolean {
        val accessToken = tokenRepository.getAccessToken().trim()
        return accessToken.isNotBlank() && accessToken.lowercase() != "null"
    }

    private fun restoreActiveStoreContext(): ActiveStoreContext {
        val lastActiveStoreId = encryptedDataStore.readData(LAST_ACTIVE_STORE_ID_KEY)
            ?.trim()
            ?.takeIf { it.isNotEmpty() }
        val selectedFrom = if (lastActiveStoreId != null) {
            StoreSelectionSource.RESTORE
        } else {
            StoreSelectionSource.BOOTSTRAP
        }

        return ActiveStoreContext(
            accountId = AUTHENTICATED_ACCOUNT_ID,
            activeStoreId = lastActiveStoreId,
            selectedFrom = selectedFrom
        )
    }

    private suspend fun isForceUpdateRequired(): Boolean = try {
        val required = ownerRepository.getRequiredVersion()
        compareVersions(getAppVersion(), required) < 0
    } catch (e: Exception) {
        false
    }

    internal fun compareVersions(current: String, required: String): Int {
        val c = current.split(".").map { it.toIntOrNull() ?: 0 }
        val r = required.split(".").map { it.toIntOrNull() ?: 0 }
        for (i in 0..2) {
            val diff = (c.getOrElse(i) { 0 }).compareTo(r.getOrElse(i) { 0 })
            if (diff != 0) return diff
        }
        return 0
    }

    companion object {
        private const val AUTHENTICATED_ACCOUNT_ID = "authenticated-owner"
        private const val LAST_ACTIVE_STORE_ID_KEY = "lastActiveStoreId"
    }
}

data class AppUiState(
    val activeStoreContext: ActiveStoreContext? = null
)
