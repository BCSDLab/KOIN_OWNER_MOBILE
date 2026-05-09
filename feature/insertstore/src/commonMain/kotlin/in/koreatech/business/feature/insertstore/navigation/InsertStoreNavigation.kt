@file:OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class)

package `in`.koreatech.business.feature.insertstore.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.backhandler.BackHandler
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import `in`.koreatech.business.feature.insertstore.BasicInfoStep
import `in`.koreatech.business.feature.insertstore.CompleteStep
import `in`.koreatech.business.feature.insertstore.DetailInfoStep
import `in`.koreatech.business.feature.insertstore.FinalCheckStep
import `in`.koreatech.business.feature.insertstore.InsertStoreState
import `in`.koreatech.business.feature.insertstore.InsertStoreStep
import `in`.koreatech.business.feature.insertstore.InsertStoreViewModel
import `in`.koreatech.business.feature.insertstore.SelectCategoryStep
import `in`.koreatech.business.feature.insertstore.StartStep
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState

@Serializable
data object InsertStoreGraph

@Serializable
sealed class InsertStoreRoute {
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

fun NavController.navigateToInsertStore(navOptions: NavOptionsBuilder.() -> Unit = {}) {
    navigate(InsertStoreGraph, navOptions)
}

/**
 * Adds the insert-store nested graph to the enclosing nav graph block.
 *
 * The shared [InsertStoreViewModel] is scoped to the [InsertStoreGraph] parent entry, so all
 * six steps observe and drive the same VM.
 */
fun NavGraphBuilder.insertStoreGraph(
    navController: NavController,
    onNavigateBack: () -> Unit,
    onNavigateToStoreMain: () -> Unit
) {
    navigation<InsertStoreGraph>(startDestination = InsertStoreRoute.Start) {
        composable<InsertStoreRoute.Start> { entry ->
            InsertStoreStepHost(navController, entry, onNavigateBack) { vm, _ ->
                StartStep(
                    onNext = vm::navigateNext,
                    onClose = onNavigateBack
                )
            }
        }
        composable<InsertStoreRoute.SelectCategory> { entry ->
            InsertStoreStepHost(navController, entry, onNavigateBack) { vm, state ->
                SelectCategoryStep(
                    uiState = state,
                    onCategorySelected = vm::onCategorySelected,
                    onNext = vm::navigateNext,
                    onBack = { vm.navigateBack() }
                )
            }
        }
        composable<InsertStoreRoute.BasicInfo> { entry ->
            InsertStoreStepHost(navController, entry, onNavigateBack) { vm, state ->
                BasicInfoStep(
                    uiState = state,
                    onNameChanged = vm::onNameChanged,
                    onAddressChanged = vm::onAddressChanged,
                    onPhoneChanged = vm::onPhoneChanged,
                    onAddCoverImage = vm::addCoverImage,
                    onRemoveCoverImage = vm::removeCoverImage,
                    onNext = vm::navigateNext,
                    onBack = { vm.navigateBack() }
                )
            }
        }
        composable<InsertStoreRoute.DetailInfo> { entry ->
            InsertStoreStepHost(navController, entry, onNavigateBack) { vm, state ->
                DetailInfoStep(
                    uiState = state,
                    onToggleCard = vm::onToggleCard,
                    onToggleBank = vm::onToggleBank,
                    onToggleDelivery = vm::onToggleDelivery,
                    onDeliveryPriceChanged = vm::onDeliveryPriceChanged,
                    onDescriptionChanged = vm::onDescriptionChanged,
                    onOperatingTimeToggle = vm::onOperatingTimeToggle,
                    onOpenTimeChanged = vm::onOperatingOpenTimeChanged,
                    onCloseTimeChanged = vm::onOperatingCloseTimeChanged,
                    onNext = vm::navigateNext,
                    onBack = { vm.navigateBack() }
                )
            }
        }
        composable<InsertStoreRoute.FinalCheck> { entry ->
            InsertStoreStepHost(navController, entry, onNavigateBack) { vm, state ->
                FinalCheckStep(
                    uiState = state,
                    onSubmit = vm::navigateNext,
                    onBack = { vm.navigateBack() }
                )
            }
        }
        composable<InsertStoreRoute.Complete> { entry ->
            InsertStoreStepHost(navController, entry, onNavigateBack) { _, _ ->
                CompleteStep(
                    onNavigateToStoreMain = onNavigateToStoreMain
                )
            }
        }
    }
}

@Composable
private fun InsertStoreStepHost(
    navController: NavController,
    entry: NavBackStackEntry,
    onNavigateBack: () -> Unit,
    content: @Composable (viewModel: InsertStoreViewModel, uiState: InsertStoreState) -> Unit
) {
    val parentEntry = remember(entry) {
        navController.getBackStackEntry<InsertStoreGraph>()
    }
    val viewModel: InsertStoreViewModel = koinViewModel(viewModelStoreOwner = parentEntry)
    val uiState by viewModel.collectAsState()
    SyncInsertStoreWithStep(navController, uiState.step)
    BackHandler {
        val handled = viewModel.navigateBack()
        if (!handled) onNavigateBack()
    }
    content(viewModel, uiState)
}

private fun InsertStoreStep.toRoute(): InsertStoreRoute = when (this) {
    InsertStoreStep.Start -> InsertStoreRoute.Start
    InsertStoreStep.SelectCategory -> InsertStoreRoute.SelectCategory
    InsertStoreStep.BasicInfo -> InsertStoreRoute.BasicInfo
    InsertStoreStep.DetailInfo -> InsertStoreRoute.DetailInfo
    InsertStoreStep.FinalCheck -> InsertStoreRoute.FinalCheck
    InsertStoreStep.Complete -> InsertStoreRoute.Complete
}

@Composable
private fun SyncInsertStoreWithStep(
    navController: NavController,
    step: InsertStoreStep
) {
    LaunchedEffect(step) {
        val target = step.toRoute()
        val currentDestination = navController.currentBackStackEntry?.destination ?: return@LaunchedEffect
        if (currentDestination.hasRoute(target::class)) return@LaunchedEffect

        val isInBackStack = runCatching { navController.getBackStackEntry(target) }.isSuccess
        if (isInBackStack) {
            navController.popBackStack(target, inclusive = false)
        } else {
            navController.navigate(target) { launchSingleTop = true }
        }
    }
}
