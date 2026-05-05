package `in`.koreatech.business.feature.store.navigation

import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import `in`.koreatech.business.feature.findpassword.CompleteStep
import `in`.koreatech.business.feature.findpassword.NewPasswordStep
import `in`.koreatech.business.feature.findpassword.PhoneInputStep
import `in`.koreatech.business.feature.findpassword.SmsVerifyStep
import `in`.koreatech.business.feature.findpassword.navigation.FindPasswordRoute
import `in`.koreatech.business.feature.findpassword.navigation.FindPasswordStepContent
import `in`.koreatech.business.feature.findpassword.navigation.FindPasswordStepHost
import `in`.koreatech.business.feature.findpassword.navigation.FindPasswordStepScaffold
import `in`.koreatech.business.feature.insertstore.navigation.InsertStoreGraph
import `in`.koreatech.business.feature.insertstore.navigation.insertStoreGraph
import `in`.koreatech.business.feature.settings.OSSLicensesScreen
import `in`.koreatech.business.feature.settings.PrivacyPolicyScreen
import `in`.koreatech.business.feature.settings.ServiceTermsScreen
import `in`.koreatech.business.feature.store.event.editor.WriteEventScreen
import `in`.koreatech.business.feature.store.maintab.EventListScreen
import `in`.koreatech.business.feature.store.maintab.MainTabScreen
import `in`.koreatech.business.feature.store.maintab.MenuListScreen
import `in`.koreatech.business.feature.store.menu.categories.ManageCategoriesScreen
import `in`.koreatech.business.feature.store.menu.editor.MenuEditorScreen
import `in`.koreatech.business.feature.store.storeinfoedit.ModifyStoreInfoScreen
import koreatech.business.designsystem.resources.Res
import koreatech.business.designsystem.resources.find_password_step_complete
import koreatech.business.designsystem.resources.find_password_step_new
import koreatech.business.designsystem.resources.find_password_step_sms
import koreatech.business.designsystem.resources.find_password_title
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource

@Serializable
data object StoreGraph

@Serializable
sealed class StoreRoute {
    @Serializable
    data object Dashboard : StoreRoute()

    @Serializable
    data class ManageMenus(val storeId: String) : StoreRoute()

    @Serializable
    data class MenuCreate(val storeId: String) : StoreRoute()

    @Serializable
    data class MenuEdit(val storeId: String, val menuId: String) : StoreRoute()

    @Serializable
    data class Events(val storeId: String) : StoreRoute()

    @Serializable
    data class WriteEvent(val storeId: String, val eventId: String? = null) : StoreRoute()

    @Serializable
    data class StoreInfoEdit(val storeId: String) : StoreRoute()

    @Serializable
    data class ManageCategories(val storeId: String) : StoreRoute()

    @Serializable
    data object PasswordReset : StoreRoute()

    @Serializable
    data object PrivacyPolicy : StoreRoute()

    @Serializable
    data object ServiceTerms : StoreRoute()

    @Serializable
    data object OSSLicenses : StoreRoute()
}

fun NavController.navigateToStore(navOptions: NavOptionsBuilder.() -> Unit = {}) {
    navigate(StoreGraph, navOptions)
}

/**
 * Lands on the store dashboard with [InsertStoreGraph] pushed on top — used after sign-up
 * completion when the new owner needs to register their first store.
 */
fun NavController.navigateToStoreForRegister() {
    navigate(StoreGraph) {
        popUpTo(graph.id) { inclusive = true }
        launchSingleTop = true
    }
    navigate(InsertStoreGraph)
}

/**
 * Adds the store feature nested graph to the enclosing nav graph block.
 *
 * The store-side password reset flow is intentionally wrapped in a self-contained
 * composable+NavHost ([PasswordResetSection]) instead of being flattened — that avoids
 * registering [FindPasswordRoute]'s leaf destinations twice on the root NavController
 * (the auth-side find-password sub-graph also registers them).
 */
fun NavGraphBuilder.storeGraph(
    navController: NavController,
    onSignOut: () -> Unit,
    onDeleteAccount: () -> Unit,
    onNavigateToStoreMain: () -> Unit
) {
    navigation<StoreGraph>(startDestination = StoreRoute.Dashboard) {
        composable<StoreRoute.Dashboard> {
            MainTabScreen(
                onNavigateToMenuEditor = { sid, menuId ->
                    if (menuId != null) {
                        navController.navigate(StoreRoute.MenuEdit(sid, menuId))
                    } else {
                        navController.navigate(StoreRoute.MenuCreate(sid))
                    }
                },
                onNavigateToCategories = { sid ->
                    navController.navigate(StoreRoute.ManageCategories(sid))
                },
                onNavigateToEventEditor = { sid ->
                    navController.navigate(StoreRoute.WriteEvent(sid))
                },
                onNavigateToEventEdit = { sid, eid ->
                    navController.navigate(StoreRoute.WriteEvent(sid, eid))
                },
                onNavigateToStoreInfoEdit = { sid ->
                    navController.navigate(StoreRoute.StoreInfoEdit(sid))
                },
                onNavigateToInsertStore = {
                    navController.navigate(InsertStoreGraph)
                },
                onNavigateToPasswordReset = {
                    navController.navigate(StoreRoute.PasswordReset)
                },
                onNavigateToPrivacyPolicy = {
                    navController.navigate(StoreRoute.PrivacyPolicy)
                },
                onNavigateToServiceTerms = {
                    navController.navigate(StoreRoute.ServiceTerms)
                },
                onNavigateToOSSLicenses = {
                    navController.navigate(StoreRoute.OSSLicenses)
                },
                onSignOut = onSignOut,
                onDeleteAccount = onDeleteAccount
            )
        }

        composable<StoreRoute.ManageMenus> { backStackEntry ->
            val route = backStackEntry.toRoute<StoreRoute.ManageMenus>()
            MenuListScreen(
                storeId = route.storeId,
                onNavigateToMenuEditor = { sid, menuId ->
                    if (menuId != null) {
                        navController.navigate(StoreRoute.MenuEdit(sid, menuId))
                    } else {
                        navController.navigate(StoreRoute.MenuCreate(sid))
                    }
                },
                onNavigateToCategories = { sid ->
                    navController.navigate(StoreRoute.ManageCategories(sid))
                }
            )
        }

        composable<StoreRoute.ManageCategories> { backStackEntry ->
            val route = backStackEntry.toRoute<StoreRoute.ManageCategories>()
            ManageCategoriesScreen(
                storeId = route.storeId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable<StoreRoute.MenuCreate> { backStackEntry ->
            val route = backStackEntry.toRoute<StoreRoute.MenuCreate>()
            MenuEditorScreen(
                storeId = route.storeId,
                menuId = null,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable<StoreRoute.MenuEdit> { backStackEntry ->
            val route = backStackEntry.toRoute<StoreRoute.MenuEdit>()
            MenuEditorScreen(
                storeId = route.storeId,
                menuId = route.menuId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable<StoreRoute.Events> { backStackEntry ->
            val route = backStackEntry.toRoute<StoreRoute.Events>()
            EventListScreen(
                storeId = route.storeId,
                onNavigateToEventEditor = { sid ->
                    navController.navigate(StoreRoute.WriteEvent(sid))
                },
                onNavigateToEditEvent = { sid, eid ->
                    navController.navigate(StoreRoute.WriteEvent(sid, eid))
                }
            )
        }

        composable<StoreRoute.WriteEvent> { backStackEntry ->
            val route = backStackEntry.toRoute<StoreRoute.WriteEvent>()
            WriteEventScreen(
                storeId = route.storeId,
                eventId = route.eventId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable<StoreRoute.StoreInfoEdit> { backStackEntry ->
            val route = backStackEntry.toRoute<StoreRoute.StoreInfoEdit>()
            ModifyStoreInfoScreen(
                storeId = route.storeId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable<StoreRoute.PrivacyPolicy> {
            PrivacyPolicyScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable<StoreRoute.ServiceTerms> {
            ServiceTermsScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable<StoreRoute.OSSLicenses> {
            OSSLicensesScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable<StoreRoute.PasswordReset> {
            PasswordResetSection(
                onExit = { navController.popBackStack(StoreRoute.PasswordReset, inclusive = true) }
            )
        }

        insertStoreGraph(
            navController = navController,
            onNavigateBack = {
                if (!navController.popBackStack()) {
                    onNavigateToStoreMain()
                }
            },
            onNavigateToStoreMain = onNavigateToStoreMain
        )
    }
}

/**
 * Internal NavHost wrapper around the find-password step graph. Keeping a separate
 * NavController here means [FindPasswordRoute]'s leaf destinations don't collide with
 * the auth-side registration on the root NavController.
 */
@Serializable
private data object PasswordResetGraph

@Composable
private fun PasswordResetSection(onExit: () -> Unit) {
    val internalNav = rememberNavController()
    NavHost(
        navController = internalNav,
        startDestination = PasswordResetGraph
    ) {
        navigation<PasswordResetGraph>(startDestination = FindPasswordRoute.PhoneInput) {
            composable<FindPasswordRoute.PhoneInput> { entry ->
                FindPasswordStepHost(internalNav, PasswordResetGraph, entry, onExit) { vm, state ->
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
                FindPasswordStepHost(internalNav, PasswordResetGraph, entry, onExit) { vm, state ->
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
                FindPasswordStepHost(internalNav, PasswordResetGraph, entry, onExit) { vm, state ->
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
                FindPasswordStepHost(internalNav, PasswordResetGraph, entry, onExit) { _, _ ->
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
