package `in`.koreatech.business.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import `in`.koreatech.business.feature.findpassword.navigation.navigateToPasswordReset
import `in`.koreatech.business.feature.findpassword.navigation.passwordResetGraph
import `in`.koreatech.business.feature.insertstore.navigation.InsertStoreGraph
import `in`.koreatech.business.feature.insertstore.navigation.insertStoreGraph
import `in`.koreatech.business.feature.settings.navigation.navigateToOSSLicenses
import `in`.koreatech.business.feature.settings.navigation.navigateToPrivacyPolicy
import `in`.koreatech.business.feature.settings.navigation.navigateToServiceTerms
import `in`.koreatech.business.feature.settings.navigation.settingsGraph
import `in`.koreatech.business.feature.store.navigation.StoreGraph
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
    // Bearer refresh가 영구 실패하면 AppViewModel이 sessionExpired를 emit한다.
    // 어느 그래프에 있든 즉시 AuthGraph로 교체해 재로그인을 유도.
    LaunchedEffect(Unit) {
        appViewModel.sessionExpired.collect {
            rootNavController.replaceRoot(AuthGraph)
        }
    }

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
            // clearSession/deleteAccount은 토큰을 비우고, 그 결과 observeAccessToken
            // 흐름이 빈 값을 emit한다 → AppViewModel이 sessionExpired를 발사 →
            // 위쪽 LaunchedEffect가 replaceRoot(AuthGraph) 한 번만 수행한다.
            // 콜백에서 replaceRoot를 별도로 호출하면 AuthGraph가 재생성돼
            // Auth 화면 상태/ViewModel이 리셋되므로 호출하지 않는다.
            onSignOut = { appViewModel.clearSession() },
            onDeleteAccount = { appViewModel.deleteAccount() },
            onNavigateToInsertStore = { rootNavController.navigate(InsertStoreGraph) },
            onNavigateToPasswordReset = { rootNavController.navigateToPasswordReset() },
            onNavigateToPrivacyPolicy = { rootNavController.navigateToPrivacyPolicy() },
            onNavigateToServiceTerms = { rootNavController.navigateToServiceTerms() },
            onNavigateToOSSLicenses = { rootNavController.navigateToOSSLicenses() }
        )

        insertStoreGraph(
            navController = rootNavController,
            onNavigateBack = {
                if (!rootNavController.popBackStack()) {
                    rootNavController.replaceRoot(StoreGraph)
                }
            },
            onNavigateToStoreMain = { rootNavController.replaceRoot(StoreGraph) }
        )

        passwordResetGraph(navController = rootNavController)

        settingsGraph(navController = rootNavController)
    }
}

private fun NavController.replaceRoot(route: Any) {
    navigate(route) {
        popUpTo(graph.id) { inclusive = true }
        launchSingleTop = true
    }
}

private fun NavController.navigateToStoreForRegister() {
    navigate(StoreGraph) {
        popUpTo(graph.id) { inclusive = true }
        launchSingleTop = true
    }
    navigate(InsertStoreGraph)
}
