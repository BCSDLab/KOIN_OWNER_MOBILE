package `in`.koreatech.business.ui.testing

import androidx.compose.ui.Modifier

/**
 * 적용된 서브트리에서 Compose testTag을 플랫폼 네이티브 식별자로 노출시킨다.
 *
 * Android: Compose의 testTag을 Android view의 resource-id로 노출시켜
 * Maestro 같은 자동화 도구가 testTag으로 요소를 찾을 수 있게 한다.
 * 반드시 새 Compose Window의 root에 적용해야 한다 — 자식 Window(Dialog, Popup 등)는
 * 별도 ViewRootImpl을 갖기 때문에 부모 Window의 semantics를 상속받지 않는다.
 *
 * iOS / Desktop: 동작 없음 (identity).
 */
expect fun Modifier.exposeTestTags(): Modifier
