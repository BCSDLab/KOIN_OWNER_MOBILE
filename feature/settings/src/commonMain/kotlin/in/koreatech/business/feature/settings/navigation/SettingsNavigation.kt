package `in`.koreatech.business.feature.settings.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import `in`.koreatech.business.feature.settings.OSSLicensesScreen
import `in`.koreatech.business.feature.settings.PrivacyPolicyScreen
import `in`.koreatech.business.feature.settings.ServiceTermsScreen
import kotlinx.serialization.Serializable

@Serializable
sealed class SettingsRoute {
    @Serializable
    data object PrivacyPolicy : SettingsRoute()

    @Serializable
    data object ServiceTerms : SettingsRoute()

    @Serializable
    data object OSSLicenses : SettingsRoute()
}

fun NavController.navigateToPrivacyPolicy() = navigate(SettingsRoute.PrivacyPolicy)
fun NavController.navigateToServiceTerms() = navigate(SettingsRoute.ServiceTerms)
fun NavController.navigateToOSSLicenses() = navigate(SettingsRoute.OSSLicenses)

fun NavGraphBuilder.settingsGraph(navController: NavController) {
    composable<SettingsRoute.PrivacyPolicy> {
        PrivacyPolicyScreen(onNavigateBack = { navController.popBackStack() })
    }
    composable<SettingsRoute.ServiceTerms> {
        ServiceTermsScreen(onNavigateBack = { navController.popBackStack() })
    }
    composable<SettingsRoute.OSSLicenses> {
        OSSLicensesScreen(onNavigateBack = { navController.popBackStack() })
    }
}
