package net.lingyun.ultraui.android.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.core.upClickable
import net.lingyun.ultraui.android.core.upTestTag

/** Result of a [UPCopy] tap, so the host can announce it the way `alertStyle` describes. */
public enum class UPCopyResult { Success, Empty }

/**
 * Native Compose counterpart of uview-plus `up-copy`.
 *
 * Wraps its content in a click target that writes [UPCopyProps.content] to the clipboard. An
 * empty `content` skips the copy and reports [UPCopyResult.Empty] (upstream shows a `暂无` toast);
 * a successful copy reports [UPCopyResult.Success] with the resolved `notice`.
 *
 * Difference: upstream announces the result itself via `uni.showToast` / `uni.showModal` per
 * `alertStyle`; Android has no ambient toast host here, so [onResult] hands the host the outcome,
 * the resolved notice and the `alertStyle` so it can present a toast or modal. `success` maps to
 * [onSuccess], matching the declared emit.
 */
@Composable
public fun UPCopy(
    props: UPCopyProps = UPCopyProps(),
    modifier: Modifier = Modifier,
    onSuccess: (() -> Unit)? = null,
    onResult: ((result: UPCopyResult, notice: String, alertStyle: String) -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
    content: (@Composable () -> Unit)? = null,
) {
    val clipboard = LocalClipboardManager.current
    val style = rememberUPResolvedStyle(props.customStyle, diagnostics, "UPCopy")

    fun handleClick() {
        if (props.content.isEmpty()) {
            onResult?.invoke(UPCopyResult.Empty, "暂无", props.alertStyle)
            return
        }
        clipboard.setText(AnnotatedString(props.content))
        onResult?.invoke(UPCopyResult.Success, props.notice, props.alertStyle)
        onSuccess?.invoke()
    }

    Box(
        modifier = modifier
            .applyUPResolvedStyle(style)
            .upTestTag("copy")
            .upClickable(onClick = ::handleClick),
    ) {
        if (content != null) {
            content()
        } else {
            BasicText("复制", modifier = Modifier.upTestTag("copy-text"), style = TextStyle(color = UPTheme.Content, fontSize = 15.sp))
        }
    }
}
