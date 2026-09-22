package net.lingyun.ultraui.android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.core.upClickable
import net.lingyun.ultraui.android.core.upTestTag

/** The tooltip's default centre tip per mode when `tips` is empty. */
internal fun upKeyboardDefaultTip(mode: String): String = when (mode) {
    "number" -> "数字键盘"
    "card" -> "身份证键盘"
    else -> "车牌号键盘"
}

/**
 * Native Compose counterpart of uview-plus `u-keyboard`.
 *
 * A bottom `u-popup` hosting either the number/card keyboard (`mode` in `number`/`card`) or the
 * car keyboard (any other mode), with an optional tooltip bar carrying a cancel button, a centre
 * tip and a confirm button. `change`/`backspace` bubble up from the inner keyboard; `cancel`,
 * `confirm` come from the tooltip; `close` fires when the popup dismisses.
 */
@Composable
public fun UPKeyboard(
    props: UPKeyboardProps = UPKeyboardProps(),
    modifier: Modifier = Modifier,
    onChange: ((Any) -> Unit)? = null,
    onBackspace: (() -> Unit)? = null,
    onConfirm: (() -> Unit)? = null,
    onCancel: (() -> Unit)? = null,
    onClose: (() -> Unit)? = null,
    onUpdateShow: ((Boolean) -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    val isNumberFamily = props.mode == "number" || props.mode == "card"
    UPPopup(
        props = UPPopupProps(
            show = props.show,
            mode = "bottom",
            overlay = props.overlay,
            closeOnClickOverlay = props.closeOnClickOverlay,
            safeAreaInsetBottom = props.safeAreaInsetBottom,
            zIndex = props.zIndex,
            customStyle = props.customStyle,
        ),
        modifier = modifier,
        onUpdateShow = onUpdateShow,
        onClose = onClose,
    ) {
        if (props.tooltip) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 12.dp, vertical = 14.dp)
                    .upTestTag("keyboard-tooltip"),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                if (props.showCancel) {
                    BasicText(
                        props.cancelText,
                        modifier = Modifier.weight(1f).upTestTag("keyboard-cancel").upClickable(onClick = { onCancel?.invoke() }),
                        style = TextStyle(color = UPTheme.Tips, fontSize = 15.sp, textAlign = TextAlign.Start),
                    )
                } else {
                    BasicText("", modifier = Modifier.weight(1f))
                }
                if (props.showTips) {
                    BasicText(
                        props.tips.ifEmpty { upKeyboardDefaultTip(props.mode) },
                        modifier = Modifier.weight(1f).upTestTag("keyboard-tips"),
                        style = TextStyle(color = UPTheme.Tips, fontSize = 15.sp, textAlign = TextAlign.Center),
                    )
                } else {
                    BasicText("", modifier = Modifier.weight(1f))
                }
                if (props.showConfirm) {
                    BasicText(
                        props.confirmText,
                        modifier = Modifier.weight(1f).upTestTag("keyboard-confirm").upClickable(onClick = { onConfirm?.invoke() }),
                        style = TextStyle(color = UPTheme.Primary, fontSize = 15.sp, textAlign = TextAlign.End),
                    )
                } else {
                    BasicText("", modifier = Modifier.weight(1f))
                }
            }
        }

        if (isNumberFamily) {
            UPNumberKeyboard(
                props = UPNumberKeyboardProps(mode = props.mode, dotDisabled = props.dotDisabled, random = props.random),
                onChange = { onChange?.invoke(it) },
                onBackspace = { onBackspace?.invoke() },
                diagnostics = diagnostics,
            )
        } else {
            UPCarKeyboard(
                props = UPCarKeyboardProps(random = props.random, autoChange = props.autoChange),
                onChange = { onChange?.invoke(it) },
                onBackspace = { onBackspace?.invoke() },
                diagnostics = diagnostics,
            )
        }
    }
}
