package `in`.koreatech.business.ui.util

object BusinessValidators {
    private val passwordRegex =
        Regex("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[^A-Za-z\\d])[A-Za-z\\d[^\\s]]{6,18}$")
    private val dateRegex = Regex("^\\d{4}-\\d{2}-\\d{2}$")
    private val timeRegex = Regex("^\\d{2}:\\d{2}$")

    fun isValidPhone(phone: String): Boolean {
        val digits = BusinessFormatters.digitsOnly(phone)
        return digits.length in 10..11
    }

    fun isValidBusinessNumber(number: String): Boolean = BusinessFormatters.digitsOnly(number).length == 10

    fun isValidPassword(password: String): Boolean = passwordRegex.matches(password)

    fun isValidDate(date: String): Boolean = dateRegex.matches(date)

    fun isValidTime(time: String): Boolean {
        if (!timeRegex.matches(time)) return false
        val parts = time.split(":")
        val hour = parts[0].toIntOrNull() ?: return false
        val minute = parts[1].toIntOrNull() ?: return false
        return hour in 0..23 && minute in 0..59
    }
}
