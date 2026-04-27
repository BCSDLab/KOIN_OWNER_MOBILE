package `in`.koreatech.business.feature.auth.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import `in`.koreatech.business.feature.findpassword.navigation.FindPasswordNavigation
import `in`.koreatech.business.feature.signin.SignInScreen
import `in`.koreatech.business.feature.signup.navigation.SignupNavigation
import `in`.koreatech.business.ui.component.DesktopAppShell
import kotlinx.serialization.Serializable

@Composable
fun AuthNavigation(
    onSignedInToStoreMain: () -> Unit,
    onSignedInToStoreRegister: () -> Unit,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AuthRoute.SignIn
    ) {
        composable<AuthRoute.SignIn> {
            DesktopAppShell {
                SignInScreen(
                    onSignedInToStoreMain = onSignedInToStoreMain,
                    onSignedInToStoreRegister = onSignedInToStoreRegister,
                    onNavigateToSignUp = {
                        navController.navigate(AuthRoute.SignUp)
                    },
                    onNavigateToFindPassword = {
                        navController.navigate(AuthRoute.FindPassword)
                    },
                    modifier = modifier
                )
            }
        }

        composable<AuthRoute.SignUp> {
            DesktopAppShell {
                SignupNavigation(
                    onNavigateBack = { navController.popBackStack() },
                    modifier = modifier
                )
            }
        }

        composable<AuthRoute.FindPassword> {
            DesktopAppShell {
                FindPasswordNavigation(
                    onNavigateBack = { navController.popBackStack() },
                    modifier = modifier
                )
            }
        }
    }
}

@Serializable
sealed class AuthRoute {
    @Serializable
    data object SignIn : AuthRoute()

    @Serializable
    data object SignUp : AuthRoute()

    @Serializable
    data object FindPassword : AuthRoute()
}
