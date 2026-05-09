package `in`.koreatech.business

import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId

internal actual fun Modifier.rootTestSemantics(): Modifier = semantics { testTagsAsResourceId = true }
