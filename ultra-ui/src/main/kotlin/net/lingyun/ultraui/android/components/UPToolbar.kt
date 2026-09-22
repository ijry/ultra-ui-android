package net.lingyun.ultraui.android.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.lingyun.ultraui.android.core.UPColor
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.core.upClickable
import net.lingyun.ultraui.android.core.upTestTag

/**
 * Native Compose counterpart of uview-plus `u-toolbar`.
 *
 * A 42px-tall row that pins a cancel label to the left, an optional bold centred title, and a
 * confirm label to the right. When [UPToolbarProps.rightSlot] is set the confirm button gives
 * way to the caller-provided [right] slot, matching the upstream `v-if="!rightSlot"` branch, so
 * the confirm tap is only wired while the built-in button is shown. `show = false` renders
 * nothing, exactly like the template's outer `v-if="show"`.
 */
@Composable
public fun UPToolbar(
    props: UPToolbarProps = UPToolbarProps(),
    modifier: Modifier = Modifier,
    onConfirm: (() -> Unit)? = null,
    onCancel: (() -> Unit)? = null,
    right: @Composable () -> Unit = {},
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    if (!props.show) return
    val style = rememberUPResolvedStyle(props.customStyle, diagnostics, "UPToolbar")
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(42.dp)
            .applyUPResolvedStyle(style)
            .upTestTag("toolbar"),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // `.u-toolbar__wrapper__cancel`: 15px, tips colour, 0 15px padding.
        BasicText(
            props.cancelText,
            modifier = Modifier
                .upClickable(onClick = { onCancel?.invoke() })
                .padding(horizontal = 15.dp)
                .upTestTag("toolbar-cancel"),
            style = TextStyle(color = UPColor.parse(props.cancelColor, UPTheme.Tips), fontSize = 15.sp),
        )
        // `.u-toolbar__title.u-line-1`: bold 16px main colour, centred, `flex: 1`.
        if (props.title.isNotEmpty()) {
            BasicText(
                props.title,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 30.dp)
                    .upTestTag("toolbar-title"),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = TextStyle(
                    color = UPTheme.Main,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                ),
            )
        }
        Box(Modifier.upTestTag("toolbar-right"), contentAlignment = Alignment.Center) {
            if (props.rightSlot) {
                right()
            } else {
                // `.u-toolbar__wrapper__confirm`: 15px, empty colour falls back to primary.
                BasicText(
                    props.confirmText,
                    modifier = Modifier
                        .upClickable(onClick = { onConfirm?.invoke() })
                        .padding(horizontal = 15.dp)
                        .upTestTag("toolbar-confirm"),
                    style = TextStyle(color = UPColor.parse(props.confirmColor, UPTheme.Primary), fontSize = 15.sp),
                )
            }
        }
    }
}
