package net.lingyun.ultraui.android.core

import org.junit.Assert.assertEquals
import org.junit.Test

class UPValueTest {
    @Test
    fun resolvesRawValuesWithoutChangingTheirOriginalTypes() {
        val raw: UPRawValue = 12.5
        assertEquals(raw, resolveUPModelValue(null, raw))
        assertEquals("12", resolveUPModelValue("12", raw))
    }
}
