package net.lingyun.ultraui.android

import net.lingyun.ultraui.android.core.UPConfig
import org.junit.Assert.assertEquals
import org.junit.Test

class ProjectContractTest {
    @Test
    fun libraryUsesThePublishedRootPackage() {
        assertEquals("net.lingyun.ultraui.android.core", UPConfig::class.java.packageName)
    }
}
