package `in`.koreatech.business

import androidx.compose.ui.Modifier

/**
 * 루트 컴포저블에 적용되는 플랫폼별 시맨틱스 보정.
 *
 * Android: Compose의 testTag을 Android 네이티브 view의 resource-id로 노출시켜
 * Maestro 같은 Android 자동화 도구가 testTag으로 요소를 찾을 수 있게 한다.
 * iOS / Desktop: 동작 없음 (identity).
 */
internal expect fun Modifier.rootTestSemantics(): Modifier
