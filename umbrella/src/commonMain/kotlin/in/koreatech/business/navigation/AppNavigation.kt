package `in`.koreatech.business.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import `in`.koreatech.business.AppViewModel
import `in`.koreatech.business.ForceUpdateRouteScreen
import `in`.koreatech.business.LaunchState
import `in`.koreatech.business.LoadingRouteScreen
import `in`.koreatech.business.feature.auth.navigation.AuthGraph
import `in`.koreatech.business.feature.auth.navigation.authGraph
import `in`.koreatech.business.feature.store.navigation.StoreGraph
import `in`.koreatech.business.feature.store.navigation.navigateToStoreForRegister
import `in`.koreatech.business.feature.store.navigation.storeGraph
import kotlinx.serialization.Serializable

@Serializable
sealed class AppRoute {
    @Serializable
    data object Loading : AppRoute()

    @Serializable
    data object ForceUpdate : AppRoute()
}

private const val NAV_ANIM_DURATION_MS = 280

@Composable
internal fun AppNavigation(
    rootNavController: NavHostController,
    appViewModel: AppViewModel
) {
    NavHost(
        navController = rootNavController,
        startDestination = AppRoute.Loading,
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(NAV_ANIM_DURATION_MS)
            ) + fadeIn(tween(NAV_ANIM_DURATION_MS))
        },
        exitTransition = {
            fadeOut(tween(NAV_ANIM_DURATION_MS))
        },
        popEnterTransition = {
            fadeIn(tween(NAV_ANIM_DURATION_MS))
        },
        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = tween(NAV_ANIM_DURATION_MS)
            ) + fadeOut(tween(NAV_ANIM_DURATION_MS))
        }
    ) {
        composable<AppRoute.Loading> {
            val launchState by appViewModel.launchState.collectAsStateWithLifecycle()
            LoadingRouteScreen(
                launchState = launchState,
                onResolved = { state ->
                    when (state) {
                        LaunchState.RequiresUpdate -> rootNavController.replaceRoot(AppRoute.ForceUpdate)
                        LaunchState.Authenticated -> rootNavController.replaceRoot(StoreGraph)
                        LaunchState.Unauthenticated -> rootNavController.replaceRoot(AuthGraph)
                        LaunchState.Loading -> Unit
                    }
                }
            )
        }

        composable<AppRoute.ForceUpdate> {
            ForceUpdateRouteScreen()
        }

        authGraph(
            navController = rootNavController,
            onSignedInToStoreMain = { rootNavController.replaceRoot(StoreGraph) },
            onSignedInToStoreRegister = { rootNavController.navigateToStoreForRegister() }
        )

        storeGraph(
            navController = rootNavController,
            onSignOut = {
                appViewModel.clearSession()
                rootNavController.replaceRoot(AuthGraph)
            },
            onDeleteAccount = {
                appViewModel.deleteAccount()
                rootNavController.replaceRoot(AuthGraph)
            },
            onNavigateToStoreMain = { rootNavController.replaceRoot(StoreGraph) }
        )
    }
}

private fun NavController.replaceRoot(route: Any) {
    navigate(route) {
        popUpTo(graph.id) { inclusive = true }
        launchSingleTop = true
    }
}
