package net.lingyun.ultraui.android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.upTestTag

/** Slot-preserving native Compose counterpart of uview-plus `u-title`. */
@Composable
public fun UPTitle(
    props: UPTitleProps = UPTitleProps(),
    modifier: Modifier = Modifier,
    prefix: (@Composable () -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
    content: @Composable () -> Unit,
) {
    val style = rememberUPResolvedStyle(props.customStyle, diagnostics, "UPTitle")
    Row(
        modifier = modifier
            .applyUPResolvedStyle(style)
            .upTestTag("title"),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (prefix == null) {
            Box(
                modifier = Modifier
                    .size(width = 4.dp, height = 18.dp)
                    .background(net.lingyun.ultraui.android.core.UPTheme.Primary, RoundedCornerShape(2.dp))
                    .upTestTag("title-prefix"),
            )
        } else {
            Box(modifier = Modifier.upTestTag("title-prefix")) { prefix() }
        }
        content()
    }
}

/** Text convenience form for generated Android source that has no slot content. */
@Composable
public fun UPTitle(
    text: UPRawValue,
    props: UPTitleProps = UPTitleProps(),
    modifier: Modifier = Modifier,
    prefix: (@Composable () -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    UPTitle(
        props = props,
        modifier = modifier,
        prefix = prefix,
        content = {
            BasicText(
                text = upRawText(text),
                style = androidx.compose.ui.text.TextStyle(
                    color = net.lingyun.ultraui.android.core.UPTheme.Main,
                    fontSize = 16.sp,
                ),
            )
        },
        diagnostics = diagnostics,
    )
}
