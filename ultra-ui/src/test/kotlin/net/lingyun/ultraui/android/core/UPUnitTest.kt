package net.lingyun.ultraui.android.core

import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Test

class UPUnitTest {
    @Test
    fun convertsRpxNumericAndPixelValuesAndFallsBackForMalformedUnits() {
        assertEquals(375.dp, UPUnit.toDp("750rpx", 375.dp, 1.dp))
        assertEquals(16.dp, UPUnit.toDp("16px", 375.dp, 1.dp))
        assertEquals(12.5.dp, UPUnit.toDp(12.5, 375.dp, 1.dp))
        assertEquals(9.dp, UPUnit.toDp("broken", 375.dp, 9.dp))
        assertEquals(9.dp, UPUnit.toDp("20%", 375.dp, 9.dp))
    }
}
