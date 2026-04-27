@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package `in`.koreatech.business.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import `in`.koreatech.business.domain.model.ThemeMode
import `in`.koreatech.business.ui.component.DesktopTopBar
import `in`.koreatech.business.ui.component.KoinCard
import `in`.koreatech.business.ui.theme.KoinTheme
import koreatech.business.designsystem.resources.Res
import koreatech.business.designsystem.resources.back_navigation
import koreatech.business.designsystem.resources.theme
import koreatech.business.designsystem.resources.theme_dark
import koreatech.business.designsystem.resources.theme_desc
import koreatech.business.designsystem.resources.theme_light
import koreatech.business.designsystem.resources.theme_system
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun ThemeSettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel()
) {
    val uiState by viewModel.collectAsState()
    BoxWithConstraints(modifier = Modifier.fillMaxSize().background(KoinTheme.colors.neutral50)) {
        val isDesktop = maxWidth >= 440.dp
        Column(modifier = Modifier.fillMaxSize()) {
            if (isDesktop) {
                DesktopTopBar(
                    title = stringResource(Res.string.theme),
                    subtitle = stringResource(Res.string.theme_desc)
                )
            } else {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(Res.string.theme),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = KoinTheme.colors.neutral800
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(Res.string.back_navigation),
                                tint = KoinTheme.colors.neutral800
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = KoinTheme.colors.neutral0
                    )
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = if (isDesktop) 32.dp else 16.dp, vertical = 16.dp)
            ) {
                KoinCard(modifier = Modifier.fillMaxWidth(), padding = PaddingValues(20.dp)) {
                    Text(
                        text = stringResource(Res.string.theme),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = KoinTheme.colors.neutral800
                    )
                    Text(
                        text = stringResource(Res.string.theme_desc),
                        style = MaterialTheme.typography.bodySmall,
                        color = KoinTheme.colors.neutral500,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Spacer(Modifier.height(16.dp))
                    ThemePicker(value = uiState.themeMode, onChange = viewModel::setThemeMode)
                }
            }
        }
    }
}

@Composable
private fun ThemePicker(
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
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (selected) KoinTheme.colors.primary100 else KoinTheme.colors.neutral100
                    )
                    .border(
                        width = if (selected) 2.dp else 0.dp,
                        color = if (selected) KoinTheme.colors.primary500 else Color.Transparent,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { onChange(option) }
                    .padding(vertical = 14.dp, horizontal = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (selected) KoinTheme.colors.primary500 else KoinTheme.colors.neutral800Variant,
                    modifier = Modifier.size(20.dp)
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
