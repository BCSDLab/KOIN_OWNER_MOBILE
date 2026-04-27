package `in`.koreatech.business.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import java.awt.FileDialog
import java.awt.Frame
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
actual fun rememberFilePicker(onFilePicked: (PlatformFile) -> Unit): () -> Unit {
    val scope = rememberCoroutineScope()
    return {
        scope.launch(Dispatchers.IO) {
            val dialog = FileDialog(null as Frame?, "파일 선택", FileDialog.LOAD)
            withContext(Dispatchers.Main) { dialog.isVisible = true }
            val file = dialog.files.firstOrNull() ?: return@launch
            val bytes = file.readBytes()
            val name = file.name
            val ext = name.substringAfterLast('.', "").lowercase()
            val mimeType = when (ext) {
                "pdf" -> "application/pdf"
                "png" -> "image/png"
                "webp" -> "image/webp"
                "heic", "heif" -> "image/heic"
                else -> "image/jpeg"
            }
            withContext(Dispatchers.Main) {
                onFilePicked(PlatformFile(name = name, mimeType = mimeType, bytes = bytes))
            }
        }
    }
}
