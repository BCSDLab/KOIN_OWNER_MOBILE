package `in`.koreatech.business.ui.component

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import `in`.koreatech.business.ui.theme.KoinTheme

@Composable
fun KoinTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String = "",
    isError: Boolean = false,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    minLines: Int = 1,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: (@Composable () -> Unit)? = null
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = if (label != null) {
            { Text(text = label, style = KoinTheme.typography.medium14) }
        } else {
            null
        },
        placeholder = {
            Text(
                text = placeholder,
                style = KoinTheme.typography.regular14,
                color = KoinTheme.colors.neutral500
            )
        },
        isError = isError,
        enabled = enabled,
        readOnly = readOnly,
        singleLine = singleLine,
        minLines = minLines,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        visualTransformation = visualTransformation,
        trailingIcon = trailingIcon,
        shape = RoundedCornerShape(12.dp),
        textStyle = KoinTheme.typography.regular16.copy(color = KoinTheme.colors.neutral800),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = KoinTheme.colors.neutral300,
            unfocusedContainerColor = KoinTheme.colors.neutral300,
            disabledContainerColor = KoinTheme.colors.neutral300,
            errorContainerColor = KoinTheme.colors.neutral300,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent,
            focusedTextColor = KoinTheme.colors.neutral800,
            unfocusedTextColor = KoinTheme.colors.neutral800,
            disabledTextColor = KoinTheme.colors.neutral500,
            errorTextColor = KoinTheme.colors.neutral800,
            focusedPlaceholderColor = KoinTheme.colors.neutral500,
            unfocusedPlaceholderColor = KoinTheme.colors.neutral500,
            focusedLabelColor = KoinTheme.colors.neutral500,
            unfocusedLabelColor = KoinTheme.colors.neutral500,
            errorLabelColor = KoinTheme.colors.danger600
        )
    )
}
