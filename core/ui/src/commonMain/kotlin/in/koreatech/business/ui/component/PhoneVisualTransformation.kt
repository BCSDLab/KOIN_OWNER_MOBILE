package `in`.koreatech.business.ui.component

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

/**
 * Formats raw digit string into phone number for display only.
 *
 * 01X (mobile): 010-XXXX-XXXX (max 11 digits)
 * 0XX (area):   0XX-XXX-XXXX  (max 10 digits)
 *
 * Original value stays as pure digits; hyphens are added visually.
 */
object PhoneVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text
        val formatted = formatDisplay(digits)

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                // Map digit index → formatted index (skipping inserted hyphens)
                var digitCount = 0
                formatted.forEachIndexed { index, ch ->
                    if (digitCount == offset) return index
                    if (ch != '-') digitCount++
                }
                return formatted.length
            }

            override fun transformedToOriginal(offset: Int): Int {
                // Map formatted index → digit index (ignoring hyphens)
                var digitCount = 0
                for (i in 0 until minOf(offset, formatted.length)) {
                    if (formatted[i] != '-') digitCount++
                }
                return digitCount
            }
        }

        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }

    private fun formatDisplay(digits: String): String = if (digits.startsWith("01")) {
        val d = digits.take(11)
        when {
            d.length <= 3 -> d
            d.length <= 7 -> "${d.take(3)}-${d.drop(3)}"
            else -> "${d.take(3)}-${d.substring(3, 7)}-${d.drop(7)}"
        }
    } else {
        val d = digits.take(10)
        when {
            d.length <= 3 -> d
            d.length <= 6 -> "${d.take(3)}-${d.drop(3)}"
            else -> "${d.take(3)}-${d.substring(3, 6)}-${d.drop(6)}"
        }
    }
}
