@file:OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class)

package `in`.koreatech.business.feature.signup.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import `in`.koreatech.business.feature.signup.AccountSetupStep
import `in`.koreatech.business.feature.signup.AttachFileStep
import `in`.koreatech.business.feature.signup.BusinessNumberStep
import `in`.koreatech.business.feature.signup.EnterPasswordStep
import `in`.koreatech.business.feature.signup.SearchStoreStep
import `in`.koreatech.business.feature.signup.SignupCompleteStep
import `in`.koreatech.business.feature.signup.SignupStep
import `in`.koreatech.business.feature.signup.SignupViewModel
import `in`.koreatech.business.feature.signup.SmsVerifyStep
import `in`.koreatech.business.feature.signup.StoreNameStep
import `in`.koreatech.business.feature.signup.TermsStep
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState

@Composable
internal fun SignupNavigation(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SignupViewModel = koinViewModel()
) {
    val uiState by viewModel.collectAsState()
    val navController = rememberNavController()

    LaunchedEffect(navController, uiState.step) {
        syncSignupRoute(
            navController = navController,
            route = uiState.step.toRoute()
        )
    }

    BackHandler {
        val navigated = viewModel.navigateBack()
        if (!navigated) onNavigateBack()
    }

    NavHost(
        navController = navController,
        startDestination = SignupRoute.Terms
    ) {
        composable<SignupRoute.Terms> {
            TermsStep(
                uiState = uiState,
                onBack = onNavigateBack,
                onToggleAll = viewModel::onToggleAllTerms,
                onToggleTerm = viewModel::onToggleTerm,
                onToggleExpand = viewModel::onToggleTermExpand,
                onNext = viewModel::submitTerms,
                modifier = modifier
            )
        }

        composable<SignupRoute.AccountSetup> {
            AccountSetupStep(
                uiState = uiState,
                onBack = { viewModel.navigateBack() },
                onPhoneChanged = viewModel::onPhoneNumberChanged,
                onNext = viewModel::submitPhone,
                modifier = modifier
            )
        }

        composable<SignupRoute.SmsVerify> {
            SmsVerifyStep(
                uiState = uiState,
                onBack = { viewModel.navigateBack() },
                onCodeChanged = viewModel::onSmsCodeChanged,
                onNext = viewModel::submitSms,
                modifier = modifier
            )
        }

        composable<SignupRoute.EnterPassword> {
            EnterPasswordStep(
                uiState = uiState,
                onBack = { viewModel.navigateBack() },
                onNameChanged = viewModel::onNameChanged,
                onPasswordChanged = viewModel::onPasswordChanged,
                onPasswordConfirmChanged = viewModel::onPasswordConfirmChanged,
                onTogglePasswordVisibility = viewModel::onTogglePasswordVisibility,
                onTogglePasswordConfirmVisibility = viewModel::onTogglePasswordConfirmVisibility,
                onNext = viewModel::submitPassword,
                modifier = modifier
            )
        }

        composable<SignupRoute.BusinessNumber> {
            BusinessNumberStep(
                uiState = uiState,
                onBack = { viewModel.navigateBack() },
                onBusinessNumberChanged = viewModel::onBusinessNumberChanged,
                onNext = viewModel::submitBusinessNumber,
                modifier = modifier
            )
        }

        composable<SignupRoute.StoreName> {
            StoreNameStep(
                uiState = uiState,
                onBack = { viewModel.navigateBack() },
                onStoreNameChanged = viewModel::onStoreNameChanged,
                onSearch = viewModel::submitStoreName,
                onEnterManually = viewModel::onEnterShopManually,
                modifier = modifier
            )
        }

        composable<SignupRoute.SearchStore> {
            SearchStoreStep(
                uiState = uiState,
                onBack = { viewModel.navigateBack() },
                onSelectShop = viewModel::onSelectShop,
                onEnterManually = viewModel::onEnterShopManually,
                modifier = modifier
            )
        }

        composable<SignupRoute.AttachFile> {
            AttachFileStep(
                uiState = uiState,
                onBack = { viewModel.navigateBack() },
                onShopPhoneChanged = viewModel::onShopPhoneNumberChanged,
                onAddFile = viewModel::onAddFile,
                onRemoveFile = viewModel::onRemoveFile,
                onNext = viewModel::submitAttachFile,
                modifier = modifier
            )
        }

        composable<SignupRoute.Complete> {
            SignupCompleteStep(
                onGoToSignIn = onNavigateBack,
                modifier = modifier
            )
        }
    }
}

@Serializable
internal sealed class SignupRoute {
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

private fun SignupStep.toRoute(): SignupRoute = when (this) {
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

private suspend fun syncSignupRoute(
    navController: NavHostController,
    route: SignupRoute
) {
    val currentRoute = runCatching {
        navController.currentBackStackEntry?.toRoute<SignupRoute>()
    }.getOrNull()
    if (currentRoute == route) return

    val popped = navController.popBackStack(route, inclusive = false)
    if (!popped) {
        navController.navigate(route) {
            launchSingleTop = true
        }
    }
}
