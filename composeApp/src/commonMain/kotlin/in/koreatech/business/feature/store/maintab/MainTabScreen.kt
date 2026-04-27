package `in`.koreatech.business.feature.store.maintab

import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.Store
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import `in`.koreatech.business.feature.store.shared.ActiveStoreContext
import `in`.koreatech.business.ui.theme.KoinTheme

@OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class)
@Composable
fun MainTabScreen(
    activeStoreContext: ActiveStoreContext?,
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
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    // 뒤로가기 시 탭 0이 아니면 탭 0으로 복귀
    BackHandler(enabled = selectedTab != 0) {
        selectedTab = 0
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isDesktop = maxWidth >= 700.dp
        if (isDesktop) {
            // 데스크탑: 사이드바가 SubScreenLayout에서 처리, Dashboard만 렌더
            TabDashboardContent(
                activeStoreContext = activeStoreContext,
                onNavigateToInsertStore = onNavigateToInsertStore
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(KoinTheme.colors.neutral50)
            ) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    when (selectedTab) {
                        0 -> TabDashboardContent(
                            activeStoreContext = activeStoreContext,
                            onNavigateToInsertStore = onNavigateToInsertStore
                        )
                        1 -> TabMenuContent(
                            activeStoreContext = activeStoreContext,
                            onNavigateToMenuEditor = onNavigateToMenuEditor,
                            onNavigateToCategories = onNavigateToCategories
                        )
                        2 -> TabEventContent(
                            activeStoreContext = activeStoreContext,
                            onNavigateToEventEditor = onNavigateToEventEditor,
                            onNavigateToEditEvent = onNavigateToEventEdit
                        )
                        3 -> TabMoreContent(
                            activeStoreContext = activeStoreContext,
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
                BottomTabBar(
                    selectedIndex = selectedTab,
                    onSelect = { selectedTab = it }
                )
            }
        }
    }
}

@Composable
private fun BottomTabBar(
    selectedIndex: Int,
    onSelect: (Int) -> Unit
) {
    val items = listOf(
        TabItem("대시보드", Icons.Filled.Home, Icons.Outlined.Home),
        TabItem("메뉴", Icons.Filled.Store, Icons.Outlined.Store),
        TabItem("이벤트", Icons.Filled.LocalOffer, Icons.Outlined.LocalOffer),
        TabItem("더보기", Icons.Filled.MoreHoriz, Icons.Outlined.MoreHoriz)
    )

    Column(modifier = Modifier.fillMaxWidth().background(KoinTheme.colors.neutral0)) {
        HorizontalDivider(thickness = 1.dp, color = KoinTheme.colors.neutral400)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, item ->
                val selected = index == selectedIndex
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .clickable { onSelect(index) },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // 상단 인디케이터 바
                    Box(
                        modifier = Modifier
                            .height(3.dp)
                            .size(width = 24.dp, height = 3.dp)
                            .clip(RoundedCornerShape(bottomStart = 3.dp, bottomEnd = 3.dp))
                            .background(
                                if (selected) KoinTheme.colors.primary500 else androidx.compose.ui.graphics.Color.Transparent
                            )
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        imageVector = if (selected) item.iconFilled else item.iconOutlined,
                        contentDescription = item.label,
                        tint = if (selected) KoinTheme.colors.primary500 else KoinTheme.colors.neutral500,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = item.label,
                        fontSize = 11.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                        color = if (selected) KoinTheme.colors.primary500 else KoinTheme.colors.neutral500
                    )
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
        Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
    }
}

private data class TabItem(
    val label: String,
    val iconFilled: ImageVector,
    val iconOutlined: ImageVector
)
