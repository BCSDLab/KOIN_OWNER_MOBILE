package `in`.koreatech.business.data.utils

import kotlin.experimental.ExperimentalNativeApi

@OptIn(ExperimentalNativeApi::class)
actual fun isDebug(): Boolean = Platform.isDebugBinary
