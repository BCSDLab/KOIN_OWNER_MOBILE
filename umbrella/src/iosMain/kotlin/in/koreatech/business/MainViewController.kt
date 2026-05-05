package `in`.koreatech.business

import androidx.compose.ui.window.ComposeUIViewController
import `in`.koreatech.business.platform.IosViewControllerHolder
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

fun MainViewController() = run {
    Napier.base(DebugAntilog())
    ComposeUIViewController { App() }.also { viewController ->
        IosViewControllerHolder.rootViewController = viewController
    }
}
