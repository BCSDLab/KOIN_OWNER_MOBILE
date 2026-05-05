@file:OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class)

package `in`.koreatech.business.feature.signup.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import `in`.koreatech.business.feature.auth.navigation.AuthRoute
import `in`.koreatech.business.feature.signup.AccountSetupStep
import `in`.koreatech.business.feature.signup.AttachFileStep
import `in`.koreatech.business.feature.signup.BusinessNumberStep
import `in`.koreatech.business.feature.signup.EnterPasswordStep
import `in`.koreatech.business.feature.signup.SearchStoreStep
import `in`.koreatech.business.feature.signup.SignupCompleteStep
import `in`.koreatech.business.feature.signup.SignupStep
import `in`.koreatech.business.feature.signup.SignupUiState
import `in`.koreatech.business.feature.signup.SignupViewModel
import `in`.koreatech.business.feature.signup.SmsVerifyStep
import `in`.koreatech.business.feature.signup.StoreNameStep
import `in`.koreatech.business.feature.signup.TermsStep
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState

@Serializable
sealed class SignupRoute {
    @Serializable
    data object Terms : SignupRoute()

    @Serializable
    data object AccountSetup : SignupRoute()

    @Serializable
    data object SmsVerify : SignupRoute()

    @Serializable
    data object EnterPassword : SignupRoute()

    @Serializable
    data object BusinessNumber : SignupRoute()

    @Serializable
    data object StoreName : SignupRoute()

    @Serializable
    data object SearchStore : SignupRoute()

    @Serializable
    data object AttachFile : SignupRoute()

    @Serializable
    data object Complete : SignupRoute()
}

fun SignupStep.toRoute(): SignupRoute = when (this) {
    SignupStep.Terms -> SignupRoute.Terms
    SignupStep.AccountSetup -> SignupRoute.AccountSetup
    SignupStep.SmsVerify -> SignupRoute.SmsVerify
    SignupStep.EnterPassword -> SignupRoute.EnterPassword
    SignupStep.BusinessNumber -> SignupRoute.BusinessNumber
    SignupStep.StoreName -> SignupRoute.StoreName
    SignupStep.SearchStore -> SignupRoute.SearchStore
    SignupStep.AttachFile -> SignupRoute.AttachFile
    SignupStep.Complete -> SignupRoute.Complete
}

/**
 * Adds the signup nested graph to the enclosing nav graph block. Uses [AuthRoute.SignUp] as
 * the graph route so the shared [SignupViewModel] can be scoped to the parent entry.
 */
fun NavGraphBuilder.signupSubGraph(
    navController: NavController,
    onExitSignup: () -> Unit,
    modifier: Modifier = Modifier
) {
    navigation<AuthRoute.SignUp>(startDestination = SignupRoute.Terms) {
        composable<SignupRoute.Terms> { entry ->
            SignupStepHost(navController, entry, onExitSignup) { vm, state ->
                TermsStep(
                    uiState = state,
                    onBack = onExitSignup,
                    onToggleAll = vm::onToggleAllTerms,
                    onToggleTerm = vm::onToggleTerm,
                    onToggleExpand = vm::onToggleTermExpand,
                    onNext = vm::submitTerms,
                    modifier = modifier
                )
            }
        }
        composable<SignupRoute.AccountSetup> { entry ->
            SignupStepHost(navController, entry, onExitSignup) { vm, state ->
                AccountSetupStep(
                    uiState = state,
                    onBack = { vm.navigateBack() },
                    onPhoneChanged = vm::onPhoneNumberChanged,
                    onNext = vm::submitPhone,
                    modifier = modifier
                )
            }
        }
        composable<SignupRoute.SmsVerify> { entry ->
            SignupStepHost(navController, entry, onExitSignup) { vm, state ->
                SmsVerifyStep(
                    uiState = state,
                    onBack = { vm.navigateBack() },
                    onCodeChanged = vm::onSmsCodeChanged,
                    onNext = vm::submitSms,
                    modifier = modifier
                )
            }
        }
        composable<SignupRoute.EnterPassword> { entry ->
            SignupStepHost(navController, entry, onExitSignup) { vm, state ->
                EnterPasswordStep(
                    uiState = state,
                    onBack = { vm.navigateBack() },
                    onNameChanged = vm::onNameChanged,
                    onPasswordChanged = vm::onPasswordChanged,
                    onPasswordConfirmChanged = vm::onPasswordConfirmChanged,
                    onTogglePasswordVisibility = vm::onTogglePasswordVisibility,
                    onTogglePasswordConfirmVisibility = vm::onTogglePasswordConfirmVisibility,
                    onNext = vm::submitPassword,
                    modifier = modifier
                )
            }
        }
        composable<SignupRoute.BusinessNumber> { entry ->
            SignupStepHost(navController, entry, onExitSignup) { vm, state ->
                BusinessNumberStep(
                    uiState = state,
                    onBack = { vm.navigateBack() },
                    onBusinessNumberChanged = vm::onBusinessNumberChanged,
                    onNext = vm::submitBusinessNumber,
                    modifier = modifier
                )
            }
        }
        composable<SignupRoute.StoreName> { entry ->
            SignupStepHost(navController, entry, onExitSignup) { vm, state ->
                StoreNameStep(
                    uiState = state,
                    onBack = { vm.navigateBack() },
                    onStoreNameChanged = vm::onStoreNameChanged,
                    onSearch = vm::submitStoreName,
                    onEnterManually = vm::onEnterShopManually,
                    modifier = modifier
                )
            }
        }
        composable<SignupRoute.SearchStore> { entry ->
            SignupStepHost(navController, entry, onExitSignup) { vm, state ->
                SearchStoreStep(
                    uiState = state,
                    onBack = { vm.navigateBack() },
                    onSelectShop = vm::onSelectShop,
                    onEnterManually = vm::onEnterShopManually,
                    modifier = modifier
                )
            }
        }
        composable<SignupRoute.AttachFile> { entry ->
            SignupStepHost(navController, entry, onExitSignup) { vm, state ->
                AttachFileStep(
                    uiState = state,
                    onBack = { vm.navigateBack() },
                    onShopPhoneChanged = vm::onShopPhoneNumberChanged,
                    onAddFile = vm::onAddFile,
                    onRemoveFile = vm::onRemoveFile,
                    onNext = vm::submitAttachFile,
                    modifier = modifier
                )
            }
        }
        composable<SignupRoute.Complete> { entry ->
            SignupStepHost(navController, entry, onExitSignup) { _, _ ->
                SignupCompleteStep(
                    onGoToSignIn = onExitSignup,
                    modifier = modifier
                )
            }
        }
    }
}

@Composable
fun SignupStepHost(
    navController: NavController,
    entry: NavBackStackEntry,
    onExitSignup: () -> Unit,
    content: @Composable (viewModel: SignupViewModel, uiState: SignupUiState) -> Unit
) {
    val parentEntry = remember(entry) {
        navController.getBackStackEntry<AuthRoute.SignUp>()
    }
    val viewModel: SignupViewModel = koinViewModel(viewModelStoreOwner = parentEntry)
    val uiState by viewModel.collectAsState()
    SyncSignupStepWithNav(navController, uiState.step)
    BackHandler {
        val handled = viewModel.navigateBack()
        if (!handled) onExitSignup()
    }
    content(viewModel, uiState)
}

@Composable
private fun SyncSignupStepWithNav(
    navController: NavController,
    step: SignupStep
) {
    LaunchedEffect(step) {
        val target = step.toRoute()
        val currentDestination = navController.currentBackStackEntry?.destination ?: return@LaunchedEffect
        if (currentDestination.hasRoute(target::class)) return@LaunchedEffect

        val isInBackStack = runCatching { navController.getBackStackEntry(target) }.isSuccess
        if (isInBackStack) {
            navController.popBackStack(target, inclusive = false)
        } else {
            navController.navigate(target) { launchSingleTop = true }
        }
    }
}
