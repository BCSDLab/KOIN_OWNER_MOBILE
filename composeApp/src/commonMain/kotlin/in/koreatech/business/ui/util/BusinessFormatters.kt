package `in`.koreatech.business.ui.util

object BusinessFormatters {
    fun digitsOnly(value: String, maxLength: Int? = null): String {
        val digits = value.filter(Char::isDigit)
        return maxLength?.let(digits::take) ?: digits
    }

    fun formatPhone(input: String): String {
        val digits = digitsOnly(input, 11)
        return when {
            digits.startsWith("01") -> when {
                digits.length <= 3 -> digits
                digits.length <= 7 -> "${digits.take(3)}-${digits.drop(3)}"
                else -> "${digits.take(3)}-${digits.substring(3, 7)}-${digits.drop(7)}"
            }

            else -> when {
                digits.length <= 3 -> digits
                digits.length <= 6 -> "${digits.take(3)}-${digits.drop(3)}"
                else -> "${digits.take(3)}-${digits.substring(3, 6)}-${digits.drop(6)}"
            }
        }
    }

    fun formatBusinessNumber(input: String): String {
        val digits = digitsOnly(input, 10)
        return when {
            digits.length <= 3 -> digits
            digits.length <= 5 -> "${digits.take(3)}-${digits.drop(3)}"
            else -> "${digits.take(3)}-${digits.substring(3, 5)}-${digits.drop(5)}"
        }
    }

    fun normalizeTime(input: String): String {
        val digits = digitsOnly(input, 4)
        return when {
            digits.length <= 2 -> digits
            else -> "${digits.take(2)}:${digits.drop(2)}"
        }
    }
}
