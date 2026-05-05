@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package `in`.koreatech.business.feature.store.maintab

import androidx.compose.foundation.background
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.unit.sp
import `in`.koreatech.business.domain.model.ThemeMode
import `in`.koreatech.business.feature.settings.SettingsViewModel
import `in`.koreatech.business.feature.store.dashboard.StoreDashboardViewModel
import `in`.koreatech.business.platform.getAppVersion
import `in`.koreatech.business.ui.component.KoinCard
import `in`.koreatech.business.ui.theme.KoinTheme
import koreatech.business.designsystem.resources.*
import koreatech.business.designsystem.resources.Res
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun TabMoreContent(
    onNavigateToStoreInfoEdit: (storeId: String) -> Unit,
    onNavigateToInsertStore: () -> Unit,
    onNavigateToPasswordReset: () -> Unit,
    onNavigateToPrivacyPolicy: () -> Unit,
    onNavigateToServiceTerms: () -> Unit,
    onNavigateToOSSLicenses: () -> Unit,
    onSignOut: () -> Unit,
    onDeleteAccount: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel(),
    dashboardViewModel: StoreDashboardViewModel = koinViewModel()
) {
    val uiState by viewModel.collectAsState()
    val dashboardState by dashboardViewModel.collectAsState()
    val storeId = dashboardState.activeStore?.uid?.toString().orEmpty()
    val activeStoreName = dashboardState.activeStore?.name.orEmpty()

    var showLogoutDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(KoinTheme.colors.neutral50)
    ) {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(Res.string.tab_more),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = KoinTheme.colors.neutral800
                )
            },
            actions = {
                if (activeStoreName.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(KoinTheme.colors.neutral200)
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Store,
                            contentDescription = null,
                            tint = KoinTheme.colors.neutral500,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = activeStoreName,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = KoinTheme.colors.neutral800Variant
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = KoinTheme.colors.neutral50
            )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(top = 12.dp, bottom = 24.dp)
        ) {
            // 사장님 헤더 카드
            OwnerHeaderCard(
                initial = uiState.ownerName.take(1).ifBlank { "-" },
                name = uiState.ownerName.ifBlank { "-" },
                email = uiState.ownerEmail.ifBlank { "-" },
                onClick = onNavigateToPasswordReset,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            Spacer(Modifier.height(12.dp))

            // 매장
            MoreSection(title = stringResource(Res.string.section_store)) {
                MoreRow(
                    icon = Icons.Default.Edit,
                    label = stringResource(Res.string.modify_store_info_title),
                    sub = if (storeId.isNotEmpty()) stringResource(Res.string.current_store_info_edit) else null,
                    enabled = storeId.isNotEmpty(),
                    onClick = { if (storeId.isNotEmpty()) onNavigateToStoreInfoEdit(storeId) }
                )
                MoreRow(
                    icon = Icons.Default.Add,
                    label = stringResource(Res.string.add_store_btn),
                    onClick = onNavigateToInsertStore
                )
            }

            // 사장님 정보
            MoreSection(title = stringResource(Res.string.section_owner_info)) {
                MoreRow(
                    icon = Icons.Default.VpnKey,
                    label = stringResource(Res.string.change_password),
                    sub = stringResource(Res.string.change_password_sub),
                    onClick = onNavigateToPasswordReset
                )
            }

            // 앱 설정 — 테마 인라인 picker
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                MoreSectionHeader(title = stringResource(Res.string.section_app_settings))
                KoinCard(
                    modifier = Modifier.fillMaxWidth(),
                    padding = PaddingValues(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconBubble(icon = Icons.Outlined.DarkMode)
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(Res.string.theme),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = KoinTheme.colors.neutral800
                            )
                            Text(
                                text = stringResource(Res.string.theme_desc),
                                style = MaterialTheme.typography.bodySmall,
                                color = KoinTheme.colors.neutral500
                            )
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    ThemePickerInline(value = uiState.themeMode, onChange = viewModel::setThemeMode)
                }
                Spacer(Modifier.height(20.dp))
            }

            // 약관 및 정책
            MoreSection(title = stringResource(Res.string.section_terms_policy)) {
                MoreRow(
                    icon = Icons.Default.Description,
                    label = stringResource(Res.string.service_terms),
                    onClick = onNavigateToServiceTerms
                )
                MoreRow(
                    icon = Icons.Default.Lock,
                    label = stringResource(Res.string.privacy_policy),
                    onClick = onNavigateToPrivacyPolicy
                )
                MoreRow(
                    icon = Icons.Default.Info,
                    label = stringResource(Res.string.oss_licenses),
                    onClick = onNavigateToOSSLicenses
                )
                MoreRow(
                    icon = Icons.Default.Info,
                    label = stringResource(Res.string.app_version),
                    rightText = getAppVersion(),
                    showChevron = false,
                    onClick = {}
                )
            }

            // 계정
            MoreSection(title = stringResource(Res.string.settings_group_account)) {
                MoreRow(
                    icon = Icons.AutoMirrored.Filled.Logout,
                    label = stringResource(Res.string.logout),
                    showChevron = false,
                    onClick = { showLogoutDialog = true }
                )
                MoreRow(
                    icon = Icons.Default.Close,
                    label = stringResource(Res.string.delete_account_mobile),
                    danger = true,
                    showChevron = false,
                    onClick = { showDeleteDialog = true }
                )
            }
        }
    }

    if (showLogoutDialog) {
        ConfirmDialog(
            title = stringResource(Res.string.logout),
            message = stringResource(Res.string.logout_confirm),
            confirmLabel = stringResource(Res.string.logout),
            danger = false,
            onConfirm = {
                showLogoutDialog = false
                onSignOut()
            },
            onDismiss = { showLogoutDialog = false },
            confirmTestTag = "logout_confirm"
        )
    }
    if (showDeleteDialog) {
        ConfirmDialog(
            title = stringResource(Res.string.delete_account),
            message = stringResource(Res.string.delete_account_confirm),
            confirmLabel = stringResource(Res.string.delete_account),
            danger = true,
            onConfirm = {
                showDeleteDialog = false
                onDeleteAccount()
            },
            onDismiss = { showDeleteDialog = false }
        )
    }
}

@Composable
private fun OwnerHeaderCard(
    initial: String,
    name: String,
    email: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    KoinCard(modifier = modifier.fillMaxWidth(), padding = PaddingValues(0.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(KoinTheme.colors.primary500),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initial,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(Res.string.name_honorific, name),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = KoinTheme.colors.neutral800
                )
                Text(
                    text = email,
                    style = MaterialTheme.typography.bodySmall,
                    color = KoinTheme.colors.neutral500,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = KoinTheme.colors.neutral500,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun MoreSectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = KoinTheme.colors.neutral500,
        letterSpacing = 0.6.sp,
        modifier = Modifier.padding(top = 4.dp, bottom = 8.dp, start = 4.dp)
    )
}

@Composable
private fun MoreSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        MoreSectionHeader(title)
        KoinCard(
            modifier = Modifier.fillMaxWidth(),
            padding = PaddingValues(0.dp),
            spacing = 0.dp
        ) {
            content()
        }
        Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun MoreRow(
    icon: ImageVector,
    label: String,
    sub: String? = null,
    rightText: String? = null,
    showChevron: Boolean = true,
    enabled: Boolean = true,
    danger: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconBubble(icon = icon, danger = danger)
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = when {
                    danger -> KoinTheme.colors.danger600
                    !enabled -> KoinTheme.colors.neutral500
                    else -> KoinTheme.colors.neutral800
                }
            )
            if (sub != null) {
                Text(
                    text = sub,
                    style = MaterialTheme.typography.bodySmall,
                    color = KoinTheme.colors.neutral500,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
        if (rightText != null) {
            Text(
                text = rightText,
                style = MaterialTheme.typography.bodySmall,
                color = KoinTheme.colors.neutral500,
                modifier = Modifier.padding(end = if (showChevron) 4.dp else 0.dp)
            )
        }
        if (showChevron) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = KoinTheme.colors.neutral500,
                modifier = Modifier.size(18.dp)
            )
        }
    }
    HorizontalDivider(
        color = KoinTheme.colors.neutral200,
        thickness = 1.dp,
        modifier = Modifier.padding(horizontal = 16.dp)
    )
}

@Composable
private fun IconBubble(
    icon: ImageVector,
    danger: Boolean = false
) {
    Box(
        modifier = Modifier
            .size(30.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (danger) KoinTheme.colors.danger100 else KoinTheme.colors.primary100),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (danger) KoinTheme.colors.danger600 else KoinTheme.colors.primary500,
            modifier = Modifier.size(15.dp)
        )
    }
}

@Composable
private fun ThemePickerInline(
    value: ThemeMode,
    onChange: (ThemeMode) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ThemeMode.entries.forEach { option ->
            val selected = option == value
            val (icon, label) = when (option) {
                ThemeMode.Light -> Icons.Outlined.LightMode to stringResource(Res.string.theme_light)
                ThemeMode.Dark -> Icons.Outlined.DarkMode to stringResource(Res.string.theme_dark)
                ThemeMode.System -> Icons.Outlined.Refresh to stringResource(Res.string.theme_system)
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (selected) KoinTheme.colors.primary100 else KoinTheme.colors.neutral100
                    )
                    .border(
                        width = if (selected) 2.dp else 0.dp,
                        color = if (selected) KoinTheme.colors.primary500 else Color.Transparent,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .clickable { onChange(option) }
                    .padding(vertical = 12.dp, horizontal = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (selected) KoinTheme.colors.primary500 else KoinTheme.colors.neutral800Variant,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = if (selected) KoinTheme.colors.primary500 else KoinTheme.colors.neutral800Variant
                )
            }
        }
    }
}

@Composable
private fun ConfirmDialog(
    title: String,
    message: String,
    confirmLabel: String,
    danger: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    confirmTestTag: String? = null
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium
            )
        },
        text = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                modifier = if (confirmTestTag != null) Modifier.semantics { testTag = confirmTestTag } else Modifier
            ) {
                Text(
                    text = confirmLabel,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (danger) KoinTheme.colors.danger700 else KoinTheme.colors.primary500
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(Res.string.cancel),
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    )
}
