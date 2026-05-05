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
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.graphics.vector.ImageVector

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
                            contentDescription = dest.label
                        )
                    },
                    label = { Text(dest.label) }
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
    val label: String,
    val iconFilled: ImageVector,
    val iconOutlined: ImageVector
) {
    Dashboard("대시보드", Icons.Filled.Home, Icons.Outlined.Home),
    Menu("메뉴", Icons.Filled.Store, Icons.Outlined.Store),
    Events("이벤트", Icons.Filled.LocalOffer, Icons.Outlined.LocalOffer),
    More("더보기", Icons.Filled.MoreHoriz, Icons.Outlined.MoreHoriz)
}
