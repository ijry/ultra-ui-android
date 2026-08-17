package net.lingyun.ultraui.android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.lingyun.ultraui.android.core.UPColor
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.upTestTag

@Composable
public fun UPGap(props: UPGapProps = UPGapProps(), diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None) {
    val style = rememberUPResolvedStyle(props.customStyle, diagnostics, "UPGap")
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = upRawDp(props.marginTop, 0.dp), bottom = upRawDp(props.marginBottom, 0.dp))
            .background(UPColor.parse(props.bgColor, androidx.compose.ui.graphics.Color.Transparent))
            .height(upRawDp(props.height, 0.dp))
            .applyUPResolvedStyle(style)
            .upTestTag("gap"),
    )
}
