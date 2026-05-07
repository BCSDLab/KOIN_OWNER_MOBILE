package `in`.koreatech.business.feature.store.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import `in`.koreatech.business.feature.store.event.editor.WriteEventScreen
import `in`.koreatech.business.feature.store.maintab.EventListScreen
import `in`.koreatech.business.feature.store.maintab.MainTabScreen
import `in`.koreatech.business.feature.store.maintab.MenuListScreen
import `in`.koreatech.business.feature.store.menu.categories.ManageCategoriesScreen
import `in`.koreatech.business.feature.store.menu.editor.MenuEditorScreen
import `in`.koreatech.business.feature.store.storeinfoedit.ModifyStoreInfoScreen
import kotlinx.serialization.Serializable

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
}

fun NavController.navigateToStore(navOptions: NavOptionsBuilder.() -> Unit = {}) {
    navigate(StoreGraph, navOptions)
}

fun NavGraphBuilder.storeGraph(
    navController: NavController,
    onSignOut: () -> Unit,
    onDeleteAccount: () -> Unit,
    onNavigateToInsertStore: () -> Unit,
    onNavigateToPasswordReset: () -> Unit,
    onNavigateToPrivacyPolicy: () -> Unit,
    onNavigateToServiceTerms: () -> Unit,
    onNavigateToOSSLicenses: () -> Unit
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
                onNavigateToInsertStore = onNavigateToInsertStore,
                onNavigateToPasswordReset = onNavigateToPasswordReset,
                onNavigateToPrivacyPolicy = onNavigateToPrivacyPolicy,
                onNavigateToServiceTerms = onNavigateToServiceTerms,
                onNavigateToOSSLicenses = onNavigateToOSSLicenses,
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
    }
}
