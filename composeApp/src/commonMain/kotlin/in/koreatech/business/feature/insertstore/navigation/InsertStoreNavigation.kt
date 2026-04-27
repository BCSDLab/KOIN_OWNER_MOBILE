@file:OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class)

package `in`.koreatech.business.feature.insertstore.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import `in`.koreatech.business.feature.insertstore.BasicInfoStep
import `in`.koreatech.business.feature.insertstore.CompleteStep
import `in`.koreatech.business.feature.insertstore.DetailInfoStep
import `in`.koreatech.business.feature.insertstore.FinalCheckStep
import `in`.koreatech.business.feature.insertstore.InsertStoreStep
import `in`.koreatech.business.feature.insertstore.InsertStoreViewModel
import `in`.koreatech.business.feature.insertstore.SelectCategoryStep
import `in`.koreatech.business.feature.insertstore.StartStep
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState

@Composable
internal fun InsertStoreNavigation(
    onNavigateBack: () -> Unit,
    onNavigateToStoreMain: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: InsertStoreViewModel = koinViewModel()
) {
    val uiState by viewModel.collectAsState()
    val navController = rememberNavController()

    LaunchedEffect(navController, uiState.step) {
        syncInsertStoreRoute(
            navController = navController,
            route = uiState.step.toRoute()
        )
    }

    BackHandler {
        val navigated = viewModel.navigateBack()
        if (!navigated) onNavigateBack()
    }

    NavHost(
        navController = navController,
        startDestination = InsertStoreRoute.Start
    ) {
        composable<InsertStoreRoute.Start> {
            StartStep(
                onNext = viewModel::navigateNext,
                onClose = onNavigateBack
            )
        }

        composable<InsertStoreRoute.SelectCategory> {
            SelectCategoryStep(
                uiState = uiState,
                onCategorySelected = viewModel::onCategorySelected,
                onNext = viewModel::navigateNext,
                onBack = { viewModel.navigateBack() }
            )
        }

        composable<InsertStoreRoute.BasicInfo> {
            BasicInfoStep(
                uiState = uiState,
                onNameChanged = viewModel::onNameChanged,
                onAddressChanged = viewModel::onAddressChanged,
                onPhoneChanged = viewModel::onPhoneChanged,
                onAddCoverImage = viewModel::addCoverImage,
                onRemoveCoverImage = viewModel::removeCoverImage,
                onNext = viewModel::navigateNext,
                onBack = { viewModel.navigateBack() }
            )
        }

        composable<InsertStoreRoute.DetailInfo> {
            DetailInfoStep(
                uiState = uiState,
                onToggleCard = viewModel::onToggleCard,
                onToggleBank = viewModel::onToggleBank,
                onToggleDelivery = viewModel::onToggleDelivery,
                onDeliveryPriceChanged = viewModel::onDeliveryPriceChanged,
                onDescriptionChanged = viewModel::onDescriptionChanged,
                onOperatingTimeToggle = viewModel::onOperatingTimeToggle,
                onOpenTimeChanged = viewModel::onOperatingOpenTimeChanged,
                onCloseTimeChanged = viewModel::onOperatingCloseTimeChanged,
                onNext = viewModel::navigateNext,
                onBack = { viewModel.navigateBack() }
            )
        }

        composable<InsertStoreRoute.FinalCheck> {
            FinalCheckStep(
                uiState = uiState,
                onSubmit = viewModel::navigateNext,
                onBack = { viewModel.navigateBack() }
            )
        }

        composable<InsertStoreRoute.Complete> {
            CompleteStep(
                onNavigateToStoreMain = onNavigateToStoreMain
            )
        }
    }
}

@Serializable
internal sealed class InsertStoreRoute {
    @Serializable
    data object Start : InsertStoreRoute()

    @Serializable
    data object SelectCategory : InsertStoreRoute()

    @Serializable
    data object BasicInfo : InsertStoreRoute()

    @Serializable
    data object DetailInfo : InsertStoreRoute()

    @Serializable
    data object FinalCheck : InsertStoreRoute()

    @Serializable
    data object Complete : InsertStoreRoute()
}

private fun InsertStoreStep.toRoute(): InsertStoreRoute = when (this) {
    InsertStoreStep.Start -> InsertStoreRoute.Start
    InsertStoreStep.SelectCategory -> InsertStoreRoute.SelectCategory
    InsertStoreStep.BasicInfo -> InsertStoreRoute.BasicInfo
    InsertStoreStep.DetailInfo -> InsertStoreRoute.DetailInfo
    InsertStoreStep.FinalCheck -> InsertStoreRoute.FinalCheck
    InsertStoreStep.Complete -> InsertStoreRoute.Complete
}

private suspend fun syncInsertStoreRoute(
    navController: NavHostController,
    route: InsertStoreRoute
) {
    val currentRoute = runCatching {
        navController.currentBackStackEntry?.toRoute<InsertStoreRoute>()
    }.getOrNull()
    if (currentRoute == route) return

    val popped = navController.popBackStack(route, inclusive = false)
    if (!popped) {
        navController.navigate(route) {
            launchSingleTop = true
        }
    }
}
