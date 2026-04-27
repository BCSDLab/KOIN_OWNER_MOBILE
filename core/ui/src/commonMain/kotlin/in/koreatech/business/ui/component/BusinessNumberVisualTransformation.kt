package `in`.koreatech.business.ui.component

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

/**
 * Formats raw digit string into business registration number for display only.
 *
 * Format: XXX-XX-XXXXX (max 10 digits)
 * State stores only raw digits; hyphens are added visually.
 */
object BusinessNumberVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text
        val formatted = formatDisplay(digits)

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                var digitCount = 0
                formatted.forEachIndexed { index, ch ->
                    if (digitCount == offset) return index
                    if (ch != '-') digitCount++
                }
                return formatted.length
            }

            override fun transformedToOriginal(offset: Int): Int {
                var digitCount = 0
                for (i in 0 until minOf(offset, formatted.length)) {
                    if (formatted[i] != '-') digitCount++
                }
                return digitCount
            }
        }

        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }

    private fun formatDisplay(digits: String): String {
        val d = digits.take(10)
        return when {
            d.length <= 3 -> d
            d.length <= 5 -> "${d.take(3)}-${d.drop(3)}"
            else -> "${d.take(3)}-${d.substring(3, 5)}-${d.drop(5)}"
        }
    }
}
