package `in`.koreatech.business.feature.auth.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import `in`.koreatech.business.feature.auth.component.AuthBrandSupportingPane
import `in`.koreatech.business.feature.findpassword.navigation.FindPasswordRoute
import `in`.koreatech.business.feature.findpassword.navigation.findPasswordSteps
import `in`.koreatech.business.feature.signin.SignInScreen
import `in`.koreatech.business.feature.signup.navigation.signupSubGraph
import kotlinx.serialization.Serializable

@Serializable
data object AuthGraph

@Serializable
sealed class AuthRoute {
    @Serializable
    data object SignIn : AuthRoute()

    @Serializable
    data object SignUp : AuthRoute()

    @Serializable
    data object FindPassword : AuthRoute()
}

fun NavController.navigateToAuth(navOptions: NavOptionsBuilder.() -> Unit = {}) {
    navigate(AuthGraph, navOptions)
}

/**
 * Adds the auth feature nested graph to the enclosing nav graph block.
 *
 * Inside [AuthGraph] the start destination is [AuthRoute.SignIn]; the [signupSubGraph] and
 * find-password sub-graph live as siblings, sharing the root [navController]'s back stack.
 */
fun NavGraphBuilder.authGraph(
    navController: NavController,
    onSignedInToStoreMain: () -> Unit,
    onSignedInToStoreRegister: () -> Unit,
    modifier: Modifier = Modifier
) {
    val onExitSignup: () -> Unit = {
        navController.popBackStack(AuthRoute.SignUp, inclusive = true)
    }
    val onExitFindPassword: () -> Unit = {
        navController.popBackStack(AuthRoute.FindPassword, inclusive = true)
    }

    navigation<AuthGraph>(startDestination = AuthRoute.SignIn) {
        composable<AuthRoute.SignIn> {
            AuthBrandSupportingPane {
                SignInScreen(
                    onSignedInToStoreMain = onSignedInToStoreMain,
                    onSignedInToStoreRegister = onSignedInToStoreRegister,
                    onNavigateToSignUp = { navController.navigate(AuthRoute.SignUp) },
                    onNavigateToFindPassword = { navController.navigate(AuthRoute.FindPassword) },
                    modifier = modifier
                )
            }
        }
        signupSubGraph(navController, onExitSignup, modifier)
        navigation<AuthRoute.FindPassword>(startDestination = FindPasswordRoute.PhoneInput) {
            findPasswordSteps(
                navController = navController,
                parentRoute = AuthRoute.FindPassword,
                onExitFindPassword = onExitFindPassword,
                modifier = modifier
            )
        }
    }
}
