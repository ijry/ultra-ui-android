package net.lingyun.ultraui.android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.core.upTestTag

/**
 * Trailing-lambda overload matching normal Compose call-site syntax.
 */
@Composable
public fun UPCellGroup(
    props: UPCellGroupProps = UPCellGroupProps(),
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    UPCellGroup(
        props = props,
        modifier = modifier,
        content = content,
        diagnostics = UPCompatibilityDiagnostics.None,
    )
}

/** Native Compose counterpart of uview-plus `u-cell-group`. */
@Composable
public fun UPCellGroup(
    props: UPCellGroupProps = UPCellGroupProps(),
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    val style = rememberUPResolvedStyle(props.customStyle, diagnostics, "UPCellGroup")
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .applyUPResolvedStyle(style)
            .upTestTag("cell-group"),
    ) {
        if (props.title.isNotEmpty()) {
            BasicText(
                props.title,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                style = TextStyle(color = UPTheme.Tips, fontSize = 13.sp),
            )
        }
        content()
    }
}
