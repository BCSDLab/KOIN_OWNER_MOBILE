package `in`.koreatech.business.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import `in`.koreatech.business.feature.store.navigation.StoreDestination
import `in`.koreatech.business.ui.theme.KoinTheme
import `in`.koreatech.business.ui.theme.WindowSizeClass
import koreatech.business.designsystem.resources.*
import koreatech.business.designsystem.resources.Res
import org.jetbrains.compose.resources.stringResource

private val SIDEBAR_WIDTH = 260.dp

data class StoreSidebarActions(
    val onNavigateToDashboard: (() -> Unit)? = null,
    val onNavigateToMenu: (() -> Unit)? = null,
    val onNavigateToCategories: (() -> Unit)? = null,
    val onNavigateToEvents: (() -> Unit)? = null,
    val onNavigateToStoreInfo: (() -> Unit)? = null,
    val onNavigateToTheme: (() -> Unit)? = null,
    val onNavigateToTerms: (() -> Unit)? = null,
    val onNavigateToPrivacy: (() -> Unit)? = null,
    val onNavigateToOssLicenses: (() -> Unit)? = null,
    val onNavigateToPasswordReset: (() -> Unit)? = null,
    val onSignOut: (() -> Unit)? = null,
    val onDeleteAccount: (() -> Unit)? = null,
    val storeName: String? = null,
    val ownerName: String? = null
)

@Composable
fun StoreSubScreenLayout(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    activeSection: StoreDestination = StoreDestination.None,
    sidebarActions: StoreSidebarActions = StoreSidebarActions(),
    content: @Composable () -> Unit
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val sizeClass = WindowSizeClass.of(maxWidth)
        if (sizeClass == WindowSizeClass.Compact) {
            content()
            return@BoxWithConstraints
        }

        Row(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .width(SIDEBAR_WIDTH)
                    .fillMaxHeight()
                    .background(KoinTheme.colors.neutral50)
            ) {
                Column(modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        KoinLogo(colored = true, modifier = Modifier.size(28.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "KOIN",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = KoinTheme.colors.neutral800
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = stringResource(Res.string.greeting),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = KoinTheme.colors.neutral500
                        )
                    }
                    if (!sidebarActions.storeName.isNullOrBlank()) {
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = sidebarActions.storeName,
                            style = MaterialTheme.typography.bodySmall,
                            color = KoinTheme.colors.neutral500,
                            modifier = Modifier.padding(start = 2.dp)
                        )
                    }
                }

                HorizontalDivider(color = KoinTheme.colors.neutral400)

                Column(modifier = Modifier.padding(vertical = 8.dp, horizontal = 8.dp)) {
                    SidebarNavItem(
                        label = stringResource(Res.string.sidebar_dashboard),
                        icon = Icons.Default.Home,
                        selected = activeSection == StoreDestination.TopTab.Dashboard,
                        onClick = sidebarActions.onNavigateToDashboard
                    )
                    Spacer(Modifier.height(4.dp))
                    SidebarSectionLabel(stringResource(Res.string.manage_tab))
                    SidebarNavItem(
                        label = stringResource(Res.string.manage_menus_title),
                        icon = Icons.Default.Store,
                        selected = activeSection == StoreDestination.TopTab.Menu,
                        onClick = sidebarActions.onNavigateToMenu
                    )
                    SidebarSubItem(
                        label = stringResource(Res.string.field_category),
                        selected = activeSection == StoreDestination.SubSection.Categories,
                        onClick = sidebarActions.onNavigateToCategories
                    )
                    SidebarNavItem(
                        label = stringResource(Res.string.event_management),
                        icon = Icons.Default.LocalOffer,
                        selected = activeSection == StoreDestination.TopTab.Events,
                        onClick = sidebarActions.onNavigateToEvents
                    )

                    SidebarNavItem(
                        label = stringResource(Res.string.modify_store_info_title),
                        icon = Icons.Default.Edit,
                        selected = activeSection == StoreDestination.SubSection.StoreInfo,
                        onClick = sidebarActions.onNavigateToStoreInfo
                    )

                    Spacer(Modifier.height(8.dp))
                    SidebarSectionLabel(stringResource(Res.string.section_app))
                    SidebarNavItem(
                        label = stringResource(Res.string.theme),
                        icon = Icons.Outlined.DarkMode,
                        selected = activeSection == StoreDestination.SubSection.Theme,
                        onClick = sidebarActions.onNavigateToTheme
                    )

                    Spacer(Modifier.height(8.dp))
                    SidebarSectionLabel(stringResource(Res.string.section_terms_policy))
                    SidebarNavItem(
                        label = stringResource(Res.string.service_terms),
                        icon = Icons.Default.Description,
                        selected = activeSection == StoreDestination.SubSection.Terms,
                        onClick = sidebarActions.onNavigateToTerms
                    )
                    SidebarNavItem(
                        label = stringResource(Res.string.privacy_policy),
                        icon = Icons.Default.Lock,
                        selected = activeSection == StoreDestination.SubSection.Privacy,
                        onClick = sidebarActions.onNavigateToPrivacy
                    )
                    SidebarNavItem(
                        label = stringResource(Res.string.oss_licenses),
                        icon = Icons.Default.Info,
                        selected = activeSection == StoreDestination.SubSection.OssLicenses,
                        onClick = sidebarActions.onNavigateToOssLicenses
                    )
                }

                Spacer(Modifier.weight(1f))

                HorizontalDivider(color = KoinTheme.colors.neutral400)
                // Account card with menu (비밀번호/로그아웃/탈퇴)
                SidebarAccountCard(
                    ownerName = sidebarActions.ownerName ?: stringResource(Res.string.greeting),
                    onChangePassword = sidebarActions.onNavigateToPasswordReset,
                    onLogout = sidebarActions.onSignOut,
                    onDeleteAccount = sidebarActions.onDeleteAccount
                )
            }

            Box(
                modifier = Modifier.fillMaxHeight().width(1.dp).background(KoinTheme.colors.neutral400)
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(KoinTheme.colors.neutral50)
            ) {
                content()
            }
        }
    }
}

@Composable
private fun SidebarSectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = KoinTheme.colors.neutral500,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 6.dp)
    )
}

@Composable
private fun SidebarNavItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: (() -> Unit)?,
    tint: Color = KoinTheme.colors.neutral800
) {
    val activeColor = KoinTheme.colors.primary500
    val activeBg = KoinTheme.colors.primary100
    val resolvedTint = if (selected) activeColor else tint
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 2.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (selected) activeBg else Color.Transparent)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = resolvedTint,
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            color = resolvedTint,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun SidebarSubItem(
    label: String,
    selected: Boolean,
    onClick: (() -> Unit)?
) {
    val activeColor = KoinTheme.colors.primary500
    val activeBg = KoinTheme.colors.primary100
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 32.dp, end = 4.dp, top = 1.dp, bottom = 1.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (selected) activeBg else Color.Transparent)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (selected) activeColor else KoinTheme.colors.neutral800Variant
        )
    }
}

@Composable
private fun SidebarAccountCard(
    ownerName: String,
    onChangePassword: (() -> Unit)?,
    onLogout: (() -> Unit)?,
    onDeleteAccount: (() -> Unit)?
) {
    var menuExpanded by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Box {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clip(RoundedCornerShape(10.dp))
                .clickable { menuExpanded = true }
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(KoinTheme.colors.primary500),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = ownerName.take(1).ifBlank { "-" },
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    style = MaterialTheme.typography.labelMedium
                )
            }
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(Res.string.name_honorific, ownerName),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = KoinTheme.colors.neutral800
                )
                Text(
                    text = stringResource(Res.string.sidebar_profile_settings),
                    style = MaterialTheme.typography.labelSmall,
                    color = KoinTheme.colors.neutral500
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = KoinTheme.colors.neutral500,
                modifier = Modifier.size(16.dp)
            )
        }
        androidx.compose.material3.DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = { menuExpanded = false }
        ) {
            if (onChangePassword != null) {
                androidx.compose.material3.DropdownMenuItem(
                    text = { Text(stringResource(Res.string.change_password)) },
                    onClick = {
                        menuExpanded = false
                        onChangePassword()
                    }
                )
            }
            if (onLogout != null) {
                androidx.compose.material3.DropdownMenuItem(
                    text = { Text(stringResource(Res.string.logout)) },
                    onClick = {
                        menuExpanded = false
                        showLogoutDialog = true
                    }
                )
            }
            if (onDeleteAccount != null) {
                androidx.compose.material3.DropdownMenuItem(
                    text = {
                        Text(
                            stringResource(Res.string.delete_account),
                            color = KoinTheme.colors.danger600
                        )
                    },
                    onClick = {
                        menuExpanded = false
                        showDeleteDialog = true
                    }
                )
            }
        }
    }

    if (showLogoutDialog && onLogout != null) {
        KoinConfirmDialog(
            title = stringResource(Res.string.logout),
            message = stringResource(Res.string.logout_confirm),
            confirmLabel = stringResource(Res.string.logout),
            danger = false,
            onConfirm = {
                showLogoutDialog = false
                onLogout()
            },
            onDismiss = { showLogoutDialog = false },
            confirmTestTag = "logout_confirm"
        )
    }
    if (showDeleteDialog && onDeleteAccount != null) {
        KoinConfirmDialog(
            title = stringResource(Res.string.delete_account),
            message = stringResource(Res.string.delete_account_confirm),
            confirmLabel = stringResource(Res.string.delete_account),
            danger = true,
            onConfirm = {
                showDeleteDialog = false
                onDeleteAccount()
            },
            onDismiss = { showDeleteDialog = false },
            confirmTestTag = "delete_account_confirm"
        )
    }
}
