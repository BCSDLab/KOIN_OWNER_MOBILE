import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import `in`.koreatech.business.App
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

fun main() {
    Napier.base(DebugAntilog())
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "KOIN 사장님"
        ) {
            App()
        }
    }
}
