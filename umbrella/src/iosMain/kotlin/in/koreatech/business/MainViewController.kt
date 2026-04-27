package `in`.koreatech.business

import androidx.compose.ui.window.ComposeUIViewController
import `in`.koreatech.business.platform.IosViewControllerHolder

fun MainViewController() = ComposeUIViewController {
    App()
}.also { viewController ->
    IosViewControllerHolder.rootViewController = viewController
}
