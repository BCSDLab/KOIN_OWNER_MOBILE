@file:OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class, androidx.compose.material3.ExperimentalMaterial3Api::class)

package `in`.koreatech.business.feature.findpassword.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import `in`.koreatech.business.feature.findpassword.CompleteStep
import `in`.koreatech.business.feature.findpassword.FindPasswordStep
import `in`.koreatech.business.feature.findpassword.FindPasswordUiState
import `in`.koreatech.business.feature.findpassword.FindPasswordViewModel
import `in`.koreatech.business.feature.findpassword.NewPasswordStep
import `in`.koreatech.business.feature.findpassword.PhoneInputStep
import `in`.koreatech.business.feature.findpassword.SmsVerifyStep
import `in`.koreatech.business.ui.theme.KoinTheme
import koreatech.business.designsystem.resources.Res
import koreatech.business.designsystem.resources.back_navigation
import koreatech.business.designsystem.resources.find_password_step_complete
import koreatech.business.designsystem.resources.find_password_step_new
import koreatech.business.designsystem.resources.find_password_step_sms
import koreatech.business.designsystem.resources.find_password_title
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState

@Serializable
sealed class FindPasswordRoute {
    @Serializable
    data object PhoneInput : FindPasswordRoute()

    @Serializable
    data object SmsVerify : FindPasswordRoute()

    @Serializable
    data object NewPassword : FindPasswordRoute()

    @Serializable
    data object Complete : FindPasswordRoute()
}

fun FindPasswordStep.toRoute(): FindPasswordRoute = when (this) {
    FindPasswordStep.PhoneInput -> FindPasswordRoute.PhoneInput
    FindPasswordStep.SmsVerify -> FindPasswordRoute.SmsVerify
    FindPasswordStep.NewPassword -> FindPasswordRoute.NewPassword
    FindPasswordStep.Complete -> FindPasswordRoute.Complete
}

/**
 * Registers the four FindPassword step composables into the enclosing nav graph block.
 *
 * The caller is responsible for wrapping these composables in a `navigation<T>(...)` block
 * (using whatever route serves as the parent graph). [parentRoute] is used to resolve the
 * shared [FindPasswordViewModel] from the parent back stack entry.
 */
fun NavGraphBuilder.findPasswordSteps(
    navController: NavController,
    parentRoute: Any,
    onExitFindPassword: () -> Unit,
    modifier: Modifier = Modifier
) {
    composable<FindPasswordRoute.PhoneInput> { entry ->
        FindPasswordStepHost(navController, parentRoute, entry, onExitFindPassword) { vm, state ->
            FindPasswordStepScaffold(
                title = stringResource(Res.string.find_password_title),
                showBack = true,
                onBack = {
                    val handled = vm.navigateBack()
                    if (!handled) onExitFindPassword()
                },
                modifier = modifier
            ) { padding ->
                FindPasswordStepContent(padding) {
                    PhoneInputStep(
                        uiState = state,
                        onPhoneChanged = vm::onPhoneNumberChanged,
                        onNext = vm::submitPhone,
                        modifier = Modifier.widthIn(max = 440.dp)
                    )
                }
            }
        }
    }
    composable<FindPasswordRoute.SmsVerify> { entry ->
        FindPasswordStepHost(navController, parentRoute, entry, onExitFindPassword) { vm, state ->
            FindPasswordStepScaffold(
                title = stringResource(Res.string.find_password_step_sms),
                showBack = true,
                onBack = {
                    val handled = vm.navigateBack()
                    if (!handled) onExitFindPassword()
                },
                modifier = modifier
            ) { padding ->
                FindPasswordStepContent(padding) {
                    SmsVerifyStep(
                        uiState = state,
                        onSmsCodeChanged = vm::onSmsCodeChanged,
                        onNext = vm::submitSms,
                        onResendSms = vm::resendSms,
                        modifier = Modifier.widthIn(max = 440.dp)
                    )
                }
            }
        }
    }
    composable<FindPasswordRoute.NewPassword> { entry ->
        FindPasswordStepHost(navController, parentRoute, entry, onExitFindPassword) { vm, state ->
            FindPasswordStepScaffold(
                title = stringResource(Res.string.find_password_step_new),
                showBack = true,
                onBack = {
                    val handled = vm.navigateBack()
                    if (!handled) onExitFindPassword()
                },
                modifier = modifier
            ) { padding ->
                FindPasswordStepContent(padding) {
                    NewPasswordStep(
                        uiState = state,
                        onNewPasswordChanged = vm::onNewPasswordChanged,
                        onNewPasswordConfirmChanged = vm::onNewPasswordConfirmChanged,
                        onTogglePasswordVisibility = vm::onTogglePasswordVisibility,
                        onTogglePasswordConfirmVisibility = vm::onTogglePasswordConfirmVisibility,
                        onNext = vm::submitNewPassword,
                        modifier = Modifier.widthIn(max = 440.dp)
                    )
                }
            }
        }
    }
    composable<FindPasswordRoute.Complete> { entry ->
        FindPasswordStepHost(navController, parentRoute, entry, onExitFindPassword) { _, _ ->
            FindPasswordStepScaffold(
                title = stringResource(Res.string.find_password_step_complete),
                showBack = false,
                onBack = {},
                modifier = modifier
            ) { padding ->
                FindPasswordStepContent(padding) {
                    CompleteStep(
                        onConfirm = onExitFindPassword,
                        modifier = Modifier.widthIn(max = 440.dp)
                    )
                }
            }
        }
    }
}

/**
 * Wraps a single FindPassword step composable. Scopes the [FindPasswordViewModel] to the
 * parent identified by [parentRoute] so all four steps share the same VM.
 */
@Composable
fun FindPasswordStepHost(
    navController: NavController,
    parentRoute: Any,
    entry: NavBackStackEntry,
    onExitFindPassword: () -> Unit,
    content: @Composable (viewModel: FindPasswordViewModel, uiState: FindPasswordUiState) -> Unit
) {
    val parentEntry = remember(entry) {
        navController.getBackStackEntry(parentRoute)
    }
    val viewModel: FindPasswordViewModel = koinViewModel(viewModelStoreOwner = parentEntry)
    val uiState by viewModel.collectAsState()
    SyncFindPasswordStepWithNav(navController, uiState.step)
    BackHandler {
        val handled = viewModel.navigateBack()
        if (!handled) onExitFindPassword()
    }
    content(viewModel, uiState)
}

@Composable
private fun SyncFindPasswordStepWithNav(
    navController: NavController,
    step: FindPasswordStep
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

@Composable
fun FindPasswordStepScaffold(
    title: String,
    showBack: Boolean,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(text = title, style = KoinTheme.typography.bold18)
                },
                navigationIcon = {
                    if (showBack) {
                        IconButton(onClick = onBack) {
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
        containerColor = KoinTheme.colors.neutral50,
        content = content
    )
}

@Composable
fun FindPasswordStepContent(
    padding: PaddingValues,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        contentAlignment = Alignment.TopCenter
    ) {
        content()
    }
}
