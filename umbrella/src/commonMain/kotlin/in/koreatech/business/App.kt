package `in`.koreatech.business

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.network.ktor3.KtorNetworkFetcherFactory
import `in`.koreatech.business.data.di.dataSourceModule
import `in`.koreatech.business.data.di.networkModule
import `in`.koreatech.business.data.di.repositoryModule
import `in`.koreatech.business.di.appModule
import `in`.koreatech.business.di.dataStoreModule
import `in`.koreatech.business.di.encryptedDataStoreModule
import `in`.koreatech.business.di.useCaseModule
import `in`.koreatech.business.domain.model.ThemeMode
import `in`.koreatech.business.feature.auth.di.authModule
import `in`.koreatech.business.feature.insertstore.di.insertStoreModule
import `in`.koreatech.business.feature.settings.di.settingsModule
import `in`.koreatech.business.feature.store.di.storeModule
import `in`.koreatech.business.navigation.AppNavigation
import `in`.koreatech.business.ui.component.FilledActionButton
import `in`.koreatech.business.ui.component.KoinLogo
import `in`.koreatech.business.ui.theme.KoinTheme
import koreatech.business.designsystem.resources.Res
import koreatech.business.designsystem.resources.checking_login
import koreatech.business.designsystem.resources.force_update_desc
import koreatech.business.designsystem.resources.force_update_title
import koreatech.business.designsystem.resources.loading_service
import koreatech.business.designsystem.resources.update_action
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplication as ComposeKoinApplication
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.KoinApplication
import org.koin.dsl.KoinAppDeclaration
import org.orbitmvi.orbit.compose.collectAsState

@Composable
@Preview
fun App() {
    val koinDeclaration = remember { businessAppDeclaration { configurePlatformContext() } }
    LaunchedEffect(Unit) {
        SingletonImageLoader.setSafe(::newImageLoader)
    }
    ComposeKoinApplication(application = koinDeclaration) {
        val appViewModel: AppViewModel = koinViewModel()
        val appUiState by appViewModel.collectAsState()
        val darkTheme = when (appUiState.themeMode) {
            ThemeMode.Light -> false
            ThemeMode.Dark -> true
            ThemeMode.System -> isSystemInDarkTheme()
        }
        SyncSystemBarsAppearance(darkTheme)
        val rootNavController = rememberNavController()
        KoinTheme(darkTheme = darkTheme) {
            Surface(
                color = KoinTheme.colors.neutral50,
                modifier = Modifier.fillMaxSize().rootTestSemantics()
            ) {
                AppNavigation(
                    rootNavController = rootNavController,
                    appViewModel = appViewModel
                )
            }
        }
    }
}

private fun newImageLoader(context: PlatformContext): ImageLoader = ImageLoader.Builder(context)
    .components { add(KtorNetworkFetcherFactory()) }
    .build()

@Composable
fun LoadingRouteScreen(
    launchState: LaunchState,
    onResolved: (LaunchState) -> Unit
) {
    LaunchedEffect(launchState) {
        if (launchState != LaunchState.Loading) onResolved(launchState)
    }
    Surface(color = KoinTheme.colors.neutral50) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeContentPadding()
                .padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            KoinLogo(modifier = Modifier.size(72.dp))
            Spacer(modifier = Modifier.height(24.dp))
            CircularProgressIndicator(color = KoinTheme.colors.primary500)
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = stringResource(Res.string.loading_service),
                style = KoinTheme.typography.bold18,
                color = KoinTheme.colors.neutral800
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(Res.string.checking_login),
                style = KoinTheme.typography.regular14,
                color = KoinTheme.colors.neutral800Variant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ForceUpdateRouteScreen() {
    val uriHandler = LocalUriHandler.current
    val playStoreUrl = "https://play.google.com/store/apps/details?id=in.koreatech.business"

    Surface(color = KoinTheme.colors.neutral50) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeContentPadding()
                .padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                KoinLogo(modifier = Modifier.size(72.dp))
            }
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = stringResource(Res.string.force_update_title),
                style = KoinTheme.typography.bold20,
                color = KoinTheme.colors.neutral800,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(Res.string.force_update_desc),
                style = KoinTheme.typography.regular14,
                color = KoinTheme.colors.neutral800Variant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))
            FilledActionButton(
                text = stringResource(Res.string.update_action),
                onClick = { uriHandler.openUri(playStoreUrl) }
            )
        }
    }
}

fun businessAppDeclaration(additionalDeclaration: KoinApplication.() -> Unit = {}): KoinAppDeclaration = {
    modules(
        dataStoreModule,
        encryptedDataStoreModule,
        networkModule,
        dataSourceModule,
        repositoryModule,
        useCaseModule,
        appModule,
        authModule,
        storeModule,
        settingsModule,
        insertStoreModule
    )
    additionalDeclaration()
}
