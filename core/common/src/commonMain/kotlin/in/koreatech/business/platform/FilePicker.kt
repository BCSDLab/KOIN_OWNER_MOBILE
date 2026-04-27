package `in`.koreatech.business.platform

import androidx.compose.runtime.Composable

/**
 * Returns a lambda that, when invoked, opens the platform image picker.
 * The picked file is delivered via [onFilePicked] as a [PlatformFile] containing
 * the file name, MIME type, and raw bytes — ready for upload.
 */
@Composable
expect fun rememberFilePicker(onFilePicked: (PlatformFile) -> Unit): () -> Unit
