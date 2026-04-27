@file:OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class, androidx.compose.material3.ExperimentalMaterial3Api::class)

package `in`.koreatech.business.feature.findpassword.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import `in`.koreatech.business.feature.findpassword.CompleteStep
import `in`.koreatech.business.feature.findpassword.FindPasswordStep
import `in`.koreatech.business.feature.findpassword.FindPasswordViewModel
import `in`.koreatech.business.feature.findpassword.NewPasswordStep
import `in`.koreatech.business.feature.findpassword.PhoneInputStep
import `in`.koreatech.business.feature.findpassword.SmsVerifyStep
import `in`.koreatech.business.ui.theme.KoinTheme
import koin_owner_mobile.composeapp.generated.resources.*
import koin_owner_mobile.composeapp.generated.resources.Res
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState

@Composable
internal fun FindPasswordNavigation(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FindPasswordViewModel = koinViewModel()
) {
    val uiState by viewModel.collectAsState()
    val navController = rememberNavController()

    LaunchedEffect(navController, uiState.step) {
        syncFindPasswordRoute(
            navController = navController,
            route = uiState.step.toRoute()
        )
    }

    BackHandler {
        val navigated = viewModel.navigateBack()
        if (!navigated) onNavigateBack()
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when (uiState.step) {
                            FindPasswordStep.PhoneInput -> stringResource(Res.string.find_password_title)
                            FindPasswordStep.SmsVerify -> stringResource(Res.string.find_password_step_sms)
                            FindPasswordStep.NewPassword -> stringResource(Res.string.find_password_step_new)
                            FindPasswordStep.Complete -> stringResource(Res.string.find_password_step_complete)
                        },
                        style = KoinTheme.typography.bold18
                    )
                },
                navigationIcon = {
                    if (uiState.step != FindPasswordStep.Complete) {
                        IconButton(onClick = {
                            val navigated = viewModel.navigateBack()
                            if (!navigated) onNavigateBack()
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(Res.string.back_navigation)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = KoinTheme.colors.neutral50
                )
            )
        },
        containerColor = KoinTheme.colors.neutral50
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = FindPasswordRoute.PhoneInput,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable<FindPasswordRoute.PhoneInput> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
                    PhoneInputStep(
                        uiState = uiState,
                        onPhoneChanged = viewModel::onPhoneNumberChanged,
                        onNext = viewModel::submitPhone,
                        modifier = Modifier.widthIn(max = 440.dp)
                    )
                }
            }

            composable<FindPasswordRoute.SmsVerify> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
                    SmsVerifyStep(
                        uiState = uiState,
                        onSmsCodeChanged = viewModel::onSmsCodeChanged,
                        onNext = viewModel::submitSms,
                        modifier = Modifier.widthIn(max = 440.dp)
                    )
                }
            }

            composable<FindPasswordRoute.NewPassword> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
                    NewPasswordStep(
                        uiState = uiState,
                        onNewPasswordChanged = viewModel::onNewPasswordChanged,
                        onNewPasswordConfirmChanged = viewModel::onNewPasswordConfirmChanged,
                        onTogglePasswordVisibility = viewModel::onTogglePasswordVisibility,
                        onTogglePasswordConfirmVisibility = viewModel::onTogglePasswordConfirmVisibility,
                        onNext = viewModel::submitNewPassword,
                        modifier = Modifier.widthIn(max = 440.dp)
                    )
                }
            }

            composable<FindPasswordRoute.Complete> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
                    CompleteStep(
                        onConfirm = onNavigateBack,
                        modifier = Modifier.widthIn(max = 440.dp)
                    )
                }
            }
        }
    }
}

@Serializable
internal sealed class FindPasswordRoute {
    @Serializable
    data object PhoneInput : FindPasswordRoute()

    @Serializable
    data object SmsVerify : FindPasswordRoute()

    @Serializable
    data object NewPassword : FindPasswordRoute()

    @Serializable
    data object Complete : FindPasswordRoute()
}

private fun FindPasswordStep.toRoute(): FindPasswordRoute = when (this) {
    FindPasswordStep.PhoneInput -> FindPasswordRoute.PhoneInput
    FindPasswordStep.SmsVerify -> FindPasswordRoute.SmsVerify
    FindPasswordStep.NewPassword -> FindPasswordRoute.NewPassword
    FindPasswordStep.Complete -> FindPasswordRoute.Complete
}

private suspend fun syncFindPasswordRoute(
    navController: NavHostController,
    route: FindPasswordRoute
) {
    val currentRoute = runCatching {
        navController.currentBackStackEntry?.toRoute<FindPasswordRoute>()
    }.getOrNull()
    if (currentRoute == route) return

    val popped = navController.popBackStack(route, inclusive = false)
    if (!popped) {
        navController.navigate(route) {
            launchSingleTop = true
        }
    }
}
