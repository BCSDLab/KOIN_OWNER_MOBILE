package `in`.koreatech.business.ui.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import `in`.koreatech.business.ui.testing.exposeTestTags
import `in`.koreatech.business.ui.theme.KoinTheme
import koreatech.business.designsystem.resources.Res
import koreatech.business.designsystem.resources.cancel
import org.jetbrains.compose.resources.stringResource

/**
 * 표준 확인/취소 다이얼로그.
 *
 * confirmTestTag을 지정하면 confirm 버튼에 testTag을 부여하고 그 위에
 * exposeTestTags()를 적용해 AlertDialog의 별도 Window 안에서도
 * Android resource-id로 노출되도록 한다 (Maestro 같은 자동화 도구가 인식 가능).
 */
@Composable
fun KoinConfirmDialog(
    title: String,
    message: String,
    confirmLabel: String,
    danger: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    confirmTestTag: String? = null
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium
            )
        },
        text = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                modifier = if (confirmTestTag != null) {
                    Modifier
                        .exposeTestTags()
                        .semantics { testTag = confirmTestTag }
                } else {
                    Modifier
                }
            ) {
                Text(
                    text = confirmLabel,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (danger) KoinTheme.colors.danger700 else KoinTheme.colors.primary500
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(Res.string.cancel),
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    )
}
