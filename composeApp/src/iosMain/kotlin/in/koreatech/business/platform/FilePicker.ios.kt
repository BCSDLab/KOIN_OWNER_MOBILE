@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package `in`.koreatech.business.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.NSURL
import platform.Foundation.lastPathComponent
import platform.UIKit.UIDocumentPickerDelegateProtocol
import platform.UIKit.UIDocumentPickerViewController
import platform.UIKit.UIViewController
import platform.UniformTypeIdentifiers.UTTypeImage
import platform.UniformTypeIdentifiers.UTTypePDF
import platform.posix.memcpy

@Composable
actual fun rememberFilePicker(onFilePicked: (PlatformFile) -> Unit): () -> Unit {
    val delegate = remember(onFilePicked) { DocumentPickerDelegate(onFilePicked) }
    return remember(delegate) {
        fun openPicker() {
            val rootViewController = IosViewControllerHolder.rootViewController ?: return
            FilePickerDelegateStore.current = delegate

            val picker = UIDocumentPickerViewController(
                forOpeningContentTypes = listOf(UTTypeImage, UTTypePDF),
                asCopy = true
            )
            picker.delegate = delegate
            topMostViewController(rootViewController).presentViewController(
                viewControllerToPresent = picker,
                animated = true,
                completion = null
            )
        }
        ::openPicker
    }
}

private object FilePickerDelegateStore {
    var current: UIDocumentPickerDelegateProtocol? = null
}

private class DocumentPickerDelegate(
    private val onFilePicked: (PlatformFile) -> Unit
) : platform.Foundation.NSObject(),
    UIDocumentPickerDelegateProtocol {

    override fun documentPicker(
        controller: UIDocumentPickerViewController,
        didPickDocumentsAtURLs: List<*>
    ) {
        val url = didPickDocumentsAtURLs.firstOrNull() as? NSURL ?: return
        val accessed = url.startAccessingSecurityScopedResource()
        try {
            val data = NSData.dataWithContentsOfURL(url) ?: return
            val size = data.length.toInt()
            val bytes = ByteArray(size)
            bytes.usePinned { pinned ->
                memcpy(pinned.addressOf(0), data.bytes, data.length)
            }
            val name = url.lastPathComponent ?: "attachment"
            val extension = name.substringAfterLast('.', "").lowercase()
            val mimeType = when (extension) {
                "pdf" -> "application/pdf"
                "png" -> "image/png"
                "webp" -> "image/webp"
                "heic", "heif" -> "image/heic"
                else -> "image/jpeg"
            }
            onFilePicked(PlatformFile(name = name, mimeType = mimeType, bytes = bytes))
        } finally {
            if (accessed) url.stopAccessingSecurityScopedResource()
        }
    }
}

private fun topMostViewController(root: UIViewController): UIViewController {
    var current = root
    while (current.presentedViewController != null) {
        current = current.presentedViewController!!
    }
    return current
}
