package `in`.koreatech.business

import kotlin.test.Test
import kotlin.test.assertEquals

class AppViewModelTest {

    private fun compareVersions(current: String, required: String): Int {
        val c = current.split(".").map { it.toIntOrNull() ?: 0 }
        val r = required.split(".").map { it.toIntOrNull() ?: 0 }
        for (i in 0..2) {
            val diff = (c.getOrElse(i) { 0 }).compareTo(r.getOrElse(i) { 0 })
            if (diff != 0) return diff
        }
        return 0
    }

    @Test
    fun `current older than required returns negative`() {
        assertEquals(-1, compareVersions("1.0.0", "1.0.1"))
    }

    @Test
    fun `current newer than required returns positive`() {
        assertEquals(1, compareVersions("2.0.0", "1.9.9"))
    }

    @Test
    fun `same version returns zero`() {
        assertEquals(0, compareVersions("1.0.0", "1.0.0"))
    }

    @Test
    fun `short version string padded with zero`() {
        assertEquals(0, compareVersions("1", "1.0.0"))
    }
}
