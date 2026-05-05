package `in`.koreatech.business.feature.store.maintab

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.Store
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.graphics.vector.ImageVector
import `in`.koreatech.business.ui.theme.KoinTheme
import koreatech.business.designsystem.resources.Res
import koreatech.business.designsystem.resources.*
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class)
@Composable
fun MainTabScreen(
    onNavigateToMenuEditor: (storeId: String, menuId: String?) -> Unit,
    onNavigateToCategories: (storeId: String) -> Unit,
    onNavigateToEventEditor: (storeId: String) -> Unit,
    onNavigateToEventEdit: (storeId: String, eventId: String) -> Unit,
    onNavigateToStoreInfoEdit: (storeId: String) -> Unit,
    onNavigateToInsertStore: () -> Unit,
    onNavigateToPasswordReset: () -> Unit,
    onNavigateToPrivacyPolicy: () -> Unit,
    onNavigateToServiceTerms: () -> Unit,
    onNavigateToOSSLicenses: () -> Unit,
    onSignOut: () -> Unit,
    onDeleteAccount: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by rememberSaveable { mutableStateOf(MainTabDestination.Dashboard) }

    BackHandler(enabled = selectedTab != MainTabDestination.Dashboard) {
        selectedTab = MainTabDestination.Dashboard
    }

    val selectedColor = KoinTheme.colors.primary500
    val unselectedColor = KoinTheme.colors.neutral500
    val indicatorColor = KoinTheme.colors.primary100
    val itemColors = NavigationSuiteDefaults.itemColors(
        navigationBarItemColors = NavigationBarItemDefaults.colors(
            selectedIconColor = selectedColor,
            selectedTextColor = selectedColor,
            indicatorColor = indicatorColor,
            unselectedIconColor = unselectedColor,
            unselectedTextColor = unselectedColor,
        ),
        navigationRailItemColors = NavigationRailItemDefaults.colors(
            selectedIconColor = selectedColor,
            selectedTextColor = selectedColor,
            indicatorColor = indicatorColor,
            unselectedIconColor = unselectedColor,
            unselectedTextColor = unselectedColor,
        ),
        navigationDrawerItemColors = NavigationDrawerItemDefaults.colors(
            selectedIconColor = selectedColor,
            selectedTextColor = selectedColor,
            unselectedIconColor = unselectedColor,
            unselectedTextColor = unselectedColor,
        )
    )

    NavigationSuiteScaffold(
        modifier = modifier,
        navigationSuiteItems = {
            MainTabDestination.entries.forEach { dest ->
                val isSelected = dest == selectedTab
                item(
                    selected = isSelected,
                    onClick = { selectedTab = dest },
                    icon = {
                        Icon(
                            imageVector = if (isSelected) dest.iconFilled else dest.iconOutlined,
                            contentDescription = stringResource(dest.labelRes)
                        )
                    },
                    label = { Text(stringResource(dest.labelRes)) },
                    colors = itemColors
                )
            }
        }
    ) {
        when (selectedTab) {
            MainTabDestination.Dashboard -> TabDashboardContent(
                onNavigateToInsertStore = onNavigateToInsertStore
            )
            MainTabDestination.Menu -> TabMenuContent(
                onNavigateToMenuEditor = onNavigateToMenuEditor,
                onNavigateToCategories = onNavigateToCategories
            )
            MainTabDestination.Events -> TabEventContent(
                onNavigateToEventEditor = onNavigateToEventEditor,
                onNavigateToEditEvent = onNavigateToEventEdit
            )
            MainTabDestination.More -> TabMoreContent(
                onNavigateToStoreInfoEdit = onNavigateToStoreInfoEdit,
                onNavigateToInsertStore = onNavigateToInsertStore,
                onNavigateToPasswordReset = onNavigateToPasswordReset,
                onNavigateToPrivacyPolicy = onNavigateToPrivacyPolicy,
                onNavigateToServiceTerms = onNavigateToServiceTerms,
                onNavigateToOSSLicenses = onNavigateToOSSLicenses,
                onSignOut = onSignOut,
                onDeleteAccount = onDeleteAccount
            )
        }
    }
}

private enum class MainTabDestination(
    val labelRes: StringResource,
    val iconFilled: ImageVector,
    val iconOutlined: ImageVector
) {
    Dashboard(Res.string.sidebar_dashboard, Icons.Filled.Home, Icons.Outlined.Home),
    Menu(Res.string.tab_menu, Icons.Filled.Store, Icons.Outlined.Store),
    Events(Res.string.tab_events, Icons.Filled.LocalOffer, Icons.Outlined.LocalOffer),
    More(Res.string.tab_more, Icons.Filled.MoreHoriz, Icons.Outlined.MoreHoriz)
}
