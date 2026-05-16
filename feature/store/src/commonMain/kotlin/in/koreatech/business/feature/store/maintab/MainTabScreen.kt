package `in`.koreatech.business.feature.store.maintab

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.WideNavigationRail
import androidx.compose.material3.WideNavigationRailItem
import androidx.compose.material3.WideNavigationRailItemDefaults
import androidx.compose.material3.WideNavigationRailValue
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.rememberWideNavigationRailState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import `in`.koreatech.business.feature.store.navigation.StoreDestination
import `in`.koreatech.business.ui.theme.KoinTheme
import `in`.koreatech.business.ui.theme.WindowSizeClass
import koreatech.business.designsystem.resources.*
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@OptIn(
    androidx.compose.ui.ExperimentalComposeUiApi::class,
    ExperimentalMaterial3ExpressiveApi::class
)
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
    var selectedTab by rememberSaveable { mutableStateOf(StoreDestination.TopTab.Dashboard) }

    val railState = rememberWideNavigationRailState(
        initialValue = WideNavigationRailValue.Collapsed
    )
    val scope = rememberCoroutineScope()

    val dashboardScrollState = rememberScrollState()
    val moreScrollState = rememberScrollState()
    val menuListState = rememberLazyListState()
    val eventsListState = rememberLazyListState()

    BackHandler(enabled = selectedTab != StoreDestination.TopTab.Dashboard) {
        selectedTab = StoreDestination.TopTab.Dashboard
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
            unselectedTextColor = unselectedColor
        ),
        navigationRailItemColors = NavigationRailItemDefaults.colors(
            selectedIconColor = selectedColor,
            selectedTextColor = selectedColor,
            indicatorColor = indicatorColor,
            unselectedIconColor = unselectedColor,
            unselectedTextColor = unselectedColor
        ),
        navigationDrawerItemColors = NavigationDrawerItemDefaults.colors(
            selectedIconColor = selectedColor,
            selectedTextColor = selectedColor,
            unselectedIconColor = unselectedColor,
            unselectedTextColor = unselectedColor
        )
    )

    val currentOnNavigateToMenuEditor by rememberUpdatedState(onNavigateToMenuEditor)
    val currentOnNavigateToCategories by rememberUpdatedState(onNavigateToCategories)
    val currentOnNavigateToEventEditor by rememberUpdatedState(onNavigateToEventEditor)
    val currentOnNavigateToEditEvent by rememberUpdatedState(onNavigateToEventEdit)
    val currentOnNavigateToStoreInfoEdit by rememberUpdatedState(onNavigateToStoreInfoEdit)
    val currentOnNavigateToInsertStore by rememberUpdatedState(onNavigateToInsertStore)
    val currentOnNavigateToPasswordReset by rememberUpdatedState(onNavigateToPasswordReset)
    val currentOnNavigateToPrivacyPolicy by rememberUpdatedState(onNavigateToPrivacyPolicy)
    val currentOnNavigateToServiceTerms by rememberUpdatedState(onNavigateToServiceTerms)
    val currentOnNavigateToOSSLicenses by rememberUpdatedState(onNavigateToOSSLicenses)
    val currentOnSignOut by rememberUpdatedState(onSignOut)
    val currentOnDeleteAccount by rememberUpdatedState(onDeleteAccount)

    val tabContent = remember {
        movableContentOf { tab: StoreDestination.TopTab ->
            when (tab) {
                StoreDestination.TopTab.Dashboard -> TabDashboardContent(
                    onNavigateToInsertStore = currentOnNavigateToInsertStore,
                    scrollState = dashboardScrollState
                )
                StoreDestination.TopTab.Menu -> TabMenuContent(
                    onNavigateToMenuEditor = currentOnNavigateToMenuEditor,
                    onNavigateToCategories = currentOnNavigateToCategories,
                    listState = menuListState
                )
                StoreDestination.TopTab.Events -> TabEventContent(
                    onNavigateToEventEditor = currentOnNavigateToEventEditor,
                    onNavigateToEditEvent = currentOnNavigateToEditEvent,
                    listState = eventsListState
                )
                StoreDestination.TopTab.More -> TabMoreContent(
                    onNavigateToStoreInfoEdit = currentOnNavigateToStoreInfoEdit,
                    onNavigateToInsertStore = currentOnNavigateToInsertStore,
                    onNavigateToPasswordReset = currentOnNavigateToPasswordReset,
                    onNavigateToPrivacyPolicy = currentOnNavigateToPrivacyPolicy,
                    onNavigateToServiceTerms = currentOnNavigateToServiceTerms,
                    onNavigateToOSSLicenses = currentOnNavigateToOSSLicenses,
                    onSignOut = currentOnSignOut,
                    onDeleteAccount = currentOnDeleteAccount,
                    scrollState = moreScrollState
                )
            }
        }
    }

    val windowSizeClass = KoinTheme.windowSizeClass

    if (windowSizeClass is WindowSizeClass.Expanded) {
        // ── 확장(데스크탑) 폭: WideNavigationRail ──────────────────────
        // WindowSizeClass.Expanded 기준으로 분기하는 것은 이 프로젝트의
        // 어댑티브 내비게이션 시리즈(#12/#14/#15)와 일치하는 의도된 설계 결정이다.
        val railExpanded = railState.targetValue == WideNavigationRailValue.Expanded
        val hamburgerContentDesc = if (railExpanded) {
            stringResource(Res.string.collapse) // "접기" — 현재 할 수 있는 액션
        } else {
            stringResource(Res.string.expand) // "펼치기" — 현재 할 수 있는 액션
        }

        Row(modifier = modifier.fillMaxSize()) {
            WideNavigationRail(
                state = railState,
                header = {
                    IconButton(onClick = { scope.launch { railState.toggle() } }) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = hamburgerContentDesc // "펼치기" / "접기"
                        )
                    }
                },
                arrangement = Arrangement.Center,
                modifier = Modifier.fillMaxHeight()
            ) {
                StoreDestination.TopTab.entries.forEach { dest ->
                    val isSelected = dest == selectedTab
                    WideNavigationRailItem(
                        selected = isSelected,
                        onClick = { selectedTab = dest },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) dest.iconFilled else dest.iconOutlined,
                                contentDescription = null // 가시적 label이 있으므로 중복 TalkBack 방지
                            )
                        },
                        label = { Text(stringResource(dest.labelRes)) },
                        railExpanded = railExpanded,
                        colors = WideNavigationRailItemDefaults.colors(
                            selectedIconColor = selectedColor,
                            selectedTextColor = selectedColor,
                            selectedIndicatorColor = indicatorColor,
                            unselectedIconColor = unselectedColor,
                            unselectedTextColor = unselectedColor,
                            disabledIconColor = unselectedColor,
                            disabledTextColor = unselectedColor
                        )
                    )
                }
            }
            Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                tabContent(selectedTab)
            }
        }
    } else {
        // ── compact / medium 폭: 기존 NavigationSuiteScaffold ──────────
        // NavigationSuiteScaffold의 기본 어댑티브 동작을 변경 없이 그대로 위임한다.
        // medium 폭에서 어떤 레이아웃이 선택될지는 NavigationSuiteScaffold의
        // 내부 calculateFromAdaptiveInfo 로직에 맡긴다.
        // NavigationSuiteScaffold에는 itemColors= 인자를 추가하지 않는다.
        // 색상은 각 item(...) 블록 내부의 colors = itemColors로 지정한다 (현재 파일과 동일).
        NavigationSuiteScaffold(
            modifier = modifier,
            navigationSuiteItems = {
                StoreDestination.TopTab.entries.forEach { dest ->
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
            tabContent(selectedTab)
        }
    }
}
