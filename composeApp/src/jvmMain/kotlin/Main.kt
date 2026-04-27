import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import `in`.koreatech.business.App

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "KOIN 사장님"
    ) {
        App()
    }
}
