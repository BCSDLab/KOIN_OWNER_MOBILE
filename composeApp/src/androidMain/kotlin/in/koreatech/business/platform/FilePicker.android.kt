package `in`.koreatech.business.platform

import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun rememberFilePicker(onFilePicked: (PlatformFile) -> Unit): () -> Unit {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                ?: return@rememberLauncherForActivityResult

            // Get the actual display name (e.g., "IMG_20250328.jpg")
            val displayName = context.contentResolver.query(
                uri,
                arrayOf(OpenableColumns.DISPLAY_NAME),
                null,
                null,
                null
            )?.use { cursor ->
                if (cursor.moveToFirst()) cursor.getString(0) else null
            }

            val name = displayName ?: "image.jpg"
            val ext = name.substringAfterLast('.', "jpg").lowercase()

            val mimeType = when (ext) {
                "png" -> "image/png"
                "webp" -> "image/webp"
                "heic", "heif" -> "image/heic"
                "pdf" -> "application/pdf"
                else -> "image/jpeg"
            }

            onFilePicked(PlatformFile(name = name, mimeType = mimeType, bytes = bytes))
        }
    }
    return { launcher.launch("image/*") }
}
