package `in`.koreatech.business.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import `in`.koreatech.business.AppSideEffect
import `in`.koreatech.business.AppViewModel
import `in`.koreatech.business.ForceUpdateRouteScreen
import `in`.koreatech.business.LoadingRouteScreen
import `in`.koreatech.business.feature.auth.navigation.AuthNavigation
import `in`.koreatech.business.feature.store.navigation.StoreNavigation
import kotlinx.serialization.Serializable
import org.orbitmvi.orbit.compose.collectSideEffect

@Serializable
sealed class AppRoute {
    @Serializable
    data object Loading : AppRoute()

    @Serializable
    data object ForceUpdate : AppRoute()

    @Serializable
    data object Auth : AppRoute()

    @Serializable
    data class Store(val startAtInsertStore: Boolean = false) : AppRoute()
}

@Composable
internal fun AppNavigation(
    appViewModel: AppViewModel
) {
    val rootNavController = rememberNavController()

    appViewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            AppSideEffect.ToLoading -> navigateToRoot(rootNavController, AppRoute.Loading)
            AppSideEffect.ToForceUpdate -> navigateToRoot(rootNavController, AppRoute.ForceUpdate)
            AppSideEffect.ToSignIn,
            AppSideEffect.ToSignUp,
            AppSideEffect.ToFindPassword -> navigateToRoot(rootNavController, AppRoute.Auth)
            AppSideEffect.ToStoreMain -> navigateToRoot(rootNavController, AppRoute.Store())
            AppSideEffect.ToStoreRegister -> navigateToRoot(
                rootNavController,
                AppRoute.Store(startAtInsertStore = true)
            )
            is AppSideEffect.ShowError -> Unit
        }
    }

    NavHost(
        navController = rootNavController,
        startDestination = AppRoute.Loading
    ) {
        composable<AppRoute.Loading> {
            LoadingRouteScreen()
        }

        composable<AppRoute.ForceUpdate> {
            ForceUpdateRouteScreen()
        }

        composable<AppRoute.Auth> {
            AuthNavigation(
                onSignedInToStoreMain = { appViewModel.navigateToStoreMainAfterSignIn() },
                onSignedInToStoreRegister = { appViewModel.navigateToStoreRegisterAfterSignIn() }
            )
        }

        composable<AppRoute.Store> { backStackEntry ->
            val route = backStackEntry.toRoute<AppRoute.Store>()
            StoreNavigation(
                appViewModel = appViewModel,
                onSignOut = appViewModel::signOut,
                onDeleteAccount = appViewModel::deleteAccount,
                startAtInsertStore = route.startAtInsertStore
            )
        }
    }
}

fun navigateToRoot(
    rootNavController: NavController,
    route: AppRoute
) {
    rootNavController.navigate(route) {
        popUpTo<AppRoute.Loading> { inclusive = true }
        launchSingleTop = true
    }
}
