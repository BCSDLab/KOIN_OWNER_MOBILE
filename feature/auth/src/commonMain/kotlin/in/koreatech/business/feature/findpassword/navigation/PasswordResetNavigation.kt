package `in`.koreatech.business.feature.findpassword.navigation

import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import `in`.koreatech.business.feature.findpassword.CompleteStep
import `in`.koreatech.business.feature.findpassword.NewPasswordStep
import `in`.koreatech.business.feature.findpassword.PhoneInputStep
import `in`.koreatech.business.feature.findpassword.SmsVerifyStep
import koreatech.business.designsystem.resources.Res
import koreatech.business.designsystem.resources.find_password_step_complete
import koreatech.business.designsystem.resources.find_password_step_new
import koreatech.business.designsystem.resources.find_password_step_sms
import koreatech.business.designsystem.resources.find_password_title
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource

@Serializable
data object PasswordResetGraph

fun NavController.navigateToPasswordReset() {
    navigate(PasswordResetGraph)
}

/**
 * Adds the authenticated-user password-reset destination to the enclosing nav graph.
 *
 * The reset flow reuses the same step composables as the unauthenticated find-password flow,
 * but is wrapped in a self-contained internal NavHost ([PasswordResetSection]) so that
 * [FindPasswordRoute] leaf destinations don't collide with the auth-side authGraph
 * registration on the root NavController.
 */
fun NavGraphBuilder.passwordResetGraph(navController: NavController) {
    composable<PasswordResetGraph> {
        PasswordResetSection(
            onExit = { navController.popBackStack(PasswordResetGraph, inclusive = true) }
        )
    }
}

@Serializable
private data object InternalPasswordResetGraph

@Composable
private fun PasswordResetSection(onExit: () -> Unit) {
    val internalNav = rememberNavController()
    NavHost(
        navController = internalNav,
        startDestination = InternalPasswordResetGraph
    ) {
        navigation<InternalPasswordResetGraph>(startDestination = FindPasswordRoute.PhoneInput) {
            composable<FindPasswordRoute.PhoneInput> { entry ->
                FindPasswordStepHost(internalNav, InternalPasswordResetGraph, entry, onExit) { vm, state ->
                    FindPasswordStepScaffold(
                        title = stringResource(Res.string.find_password_title),
                        showBack = true,
                        onBack = {
                            val handled = vm.navigateBack()
                            if (!handled) onExit()
                        }
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
                FindPasswordStepHost(internalNav, InternalPasswordResetGraph, entry, onExit) { vm, state ->
                    FindPasswordStepScaffold(
                        title = stringResource(Res.string.find_password_step_sms),
                        showBack = true,
                        onBack = {
                            val handled = vm.navigateBack()
                            if (!handled) onExit()
                        }
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
                FindPasswordStepHost(internalNav, InternalPasswordResetGraph, entry, onExit) { vm, state ->
                    FindPasswordStepScaffold(
                        title = stringResource(Res.string.find_password_step_new),
                        showBack = true,
                        onBack = {
                            val handled = vm.navigateBack()
                            if (!handled) onExit()
                        }
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
                FindPasswordStepHost(internalNav, InternalPasswordResetGraph, entry, onExit) { _, _ ->
                    FindPasswordStepScaffold(
                        title = stringResource(Res.string.find_password_step_complete),
                        showBack = false,
                        onBack = {}
                    ) { padding ->
                        FindPasswordStepContent(padding) {
                            CompleteStep(
                                onConfirm = onExit,
                                modifier = Modifier.widthIn(max = 440.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
