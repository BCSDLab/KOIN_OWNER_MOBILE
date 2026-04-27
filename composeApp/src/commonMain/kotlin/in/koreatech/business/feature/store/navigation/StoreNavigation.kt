package `in`.koreatech.business.feature.store.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import `in`.koreatech.business.AppViewModel
import `in`.koreatech.business.feature.findpassword.navigation.FindPasswordNavigation
import `in`.koreatech.business.feature.insertstore.navigation.InsertStoreNavigation
import `in`.koreatech.business.feature.settings.OSSLicensesScreen
import `in`.koreatech.business.feature.settings.PrivacyPolicyScreen
import `in`.koreatech.business.feature.settings.ServiceTermsScreen
import `in`.koreatech.business.feature.settings.ThemeSettingsScreen
import `in`.koreatech.business.feature.store.event.editor.WriteEventScreen
import `in`.koreatech.business.feature.store.maintab.EventListScreen
import `in`.koreatech.business.feature.store.maintab.MainTabScreen
import `in`.koreatech.business.feature.store.maintab.MenuListScreen
import `in`.koreatech.business.feature.store.menu.categories.ManageCategoriesScreen
import `in`.koreatech.business.feature.store.menu.editor.MenuEditorScreen
import `in`.koreatech.business.feature.store.storeinfoedit.ModifyStoreInfoScreen
import `in`.koreatech.business.ui.component.StoreNavSection
import `in`.koreatech.business.ui.component.StoreSidebarActions
import `in`.koreatech.business.ui.component.StoreSubScreenLayout
import kotlinx.serialization.Serializable
import org.orbitmvi.orbit.compose.collectAsState

@Composable
internal fun StoreNavigation(
    appViewModel: AppViewModel,
    onSignOut: () -> Unit,
    onDeleteAccount: () -> Unit,
    startAtInsertStore: Boolean = false,
    modifier: Modifier = Modifier
) {
    val appUiState by appViewModel.collectAsState()
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val destination = backStackEntry?.destination

    LaunchedEffect(startAtInsertStore) {
        if (startAtInsertStore) {
            navController.navigate(StoreRoute.InsertStore) {
                launchSingleTop = true
            }
        }
    }

    val storeId = appUiState.activeStoreContext?.activeStoreId.orEmpty()

    val activeSection = when {
        destination?.hasRoute<StoreRoute.Dashboard>() == true -> StoreNavSection.Dashboard
        destination?.hasRoute<StoreRoute.ManageMenus>() == true -> StoreNavSection.Menu
        destination?.hasRoute<StoreRoute.ManageCategories>() == true -> StoreNavSection.Categories
        destination?.hasRoute<StoreRoute.Events>() == true -> StoreNavSection.Events
        destination?.hasRoute<StoreRoute.WriteEvent>() == true -> StoreNavSection.Events
        destination?.hasRoute<StoreRoute.StoreInfoEdit>() == true -> StoreNavSection.StoreInfo
        destination?.hasRoute<StoreRoute.ThemeSettings>() == true -> StoreNavSection.Theme
        destination?.hasRoute<StoreRoute.ServiceTerms>() == true -> StoreNavSection.Terms
        destination?.hasRoute<StoreRoute.PrivacyPolicy>() == true -> StoreNavSection.Privacy
        destination?.hasRoute<StoreRoute.OSSLicenses>() == true -> StoreNavSection.OssLicenses
        else -> StoreNavSection.None
    }

    val sidebarActions = rememberNavSidebarActions(
        storeId = storeId,
        navController = navController,
        activeSection = activeSection,
        onSignOut = onSignOut,
        onDeleteAccount = onDeleteAccount
    )

    StoreSubScreenLayout(
        onNavigateBack = { navController.popBackStack() },
        activeSection = activeSection,
        sidebarActions = sidebarActions,
        modifier = modifier
    ) {
        NavHost(
            navController = navController,
            startDestination = StoreRoute.Dashboard
        ) {
            composable<StoreRoute.Dashboard> {
                MainTabScreen(
                    activeStoreContext = appUiState.activeStoreContext,
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
                        navController.navigate(StoreRoute.InsertStore)
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

            composable<StoreRoute.InsertStore> {
                InsertStoreNavigation(
                    onNavigateBack = {
                        if (!navController.popBackStack()) {
                            appViewModel.navigateToStoreMainAfterSignIn()
                        }
                    },
                    onNavigateToStoreMain = {
                        appViewModel.navigateToStoreMainAfterSignIn()
                    }
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
                FindPasswordNavigation(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable<StoreRoute.ThemeSettings> {
                ThemeSettingsScreen(onNavigateBack = { navController.popBackStack() })
            }
        }
    }
}

@Composable
private fun rememberNavSidebarActions(
    storeId: String,
    navController: NavController,
    activeSection: StoreNavSection,
    onSignOut: () -> Unit,
    onDeleteAccount: () -> Unit
): StoreSidebarActions = remember(storeId, activeSection) {
    StoreSidebarActions(
        onNavigateToDashboard = if (activeSection == StoreNavSection.Dashboard) {
            null
        } else {
            { navController.popBackStack(StoreRoute.Dashboard, inclusive = false) }
        },
        onNavigateToMenu = if (activeSection == StoreNavSection.Menu) {
            null
        } else {
            { navController.navigate(StoreRoute.ManageMenus(storeId)) { popUpTo<StoreRoute.ManageMenus> { inclusive = true } } }
        },
        onNavigateToCategories = if (activeSection == StoreNavSection.Categories) {
            null
        } else {
            { navController.navigate(StoreRoute.ManageCategories(storeId)) }
        },
        onNavigateToEvents = if (activeSection == StoreNavSection.Events) {
            null
        } else {
            { navController.navigate(StoreRoute.Events(storeId)) }
        },
        onNavigateToStoreInfo = if (activeSection == StoreNavSection.StoreInfo) {
            null
        } else {
            { navController.navigate(StoreRoute.StoreInfoEdit(storeId)) }
        },
        onNavigateToTheme = if (activeSection == StoreNavSection.Theme) {
            null
        } else {
            { navController.navigate(StoreRoute.ThemeSettings) }
        },
        onNavigateToTerms = if (activeSection == StoreNavSection.Terms) {
            null
        } else {
            { navController.navigate(StoreRoute.ServiceTerms) }
        },
        onNavigateToPrivacy = if (activeSection == StoreNavSection.Privacy) {
            null
        } else {
            { navController.navigate(StoreRoute.PrivacyPolicy) }
        },
        onNavigateToOssLicenses = if (activeSection == StoreNavSection.OssLicenses) {
            null
        } else {
            { navController.navigate(StoreRoute.OSSLicenses) }
        },
        onNavigateToPasswordReset = { navController.navigate(StoreRoute.PasswordReset) },
        onSignOut = onSignOut,
        onDeleteAccount = onDeleteAccount
    )
}

@Serializable
internal sealed class StoreRoute {
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
    data object InsertStore : StoreRoute()

    @Serializable
    data object PasswordReset : StoreRoute()

    @Serializable
    data object PrivacyPolicy : StoreRoute()

    @Serializable
    data object ServiceTerms : StoreRoute()

    @Serializable
    data object OSSLicenses : StoreRoute()

    @Serializable
    data object ThemeSettings : StoreRoute()
}
