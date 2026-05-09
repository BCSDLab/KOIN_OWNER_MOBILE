import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import `in`.koreatech.business.App
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

fun main() {
    Napier.base(DebugAntilog())
    application {
        val state = rememberWindowState(size = DpSize(1280.dp, 840.dp))
        Window(
            onCloseRequest = ::exitApplication,
            state = state,
            title = "KOIN 사장님"
        ) {
            App()
        }
    }
}
