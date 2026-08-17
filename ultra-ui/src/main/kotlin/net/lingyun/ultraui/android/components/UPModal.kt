package net.lingyun.ultraui.android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.lingyun.ultraui.android.core.UPColor
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.core.upSafeEnum
import net.lingyun.ultraui.android.core.upTestTag

private const val ModalComponentName = "UPModal"

/** Native Compose counterpart of uview-plus `u-modal`. */
@Composable
public fun UPModal(
    props: UPModalProps = UPModalProps(),
    modifier: Modifier = Modifier,
    onUpdateShow: ((Boolean) -> Unit)? = null,
    onConfirm: (() -> Unit)? = null,
    onCancel: (() -> Unit)? = null,
    onClose: (() -> Unit)? = null,
    onCancelOnAsync: (() -> Unit)? = null,
    content: (@Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    if (!props.show) return

    var confirming by remember { mutableStateOf(false) }
    LaunchedEffect(props.show) {
        if (props.show) confirming = false
    }
    val contentAlign = when (upSafeEnum(
        props.contentTextAlign,
        setOf("left", "center", "right"),
        "center",
        diagnostics,
        ModalComponentName,
        "contentTextAlign",
    )) {
        "left" -> TextAlign.Left
        "right" -> TextAlign.Right
        else -> TextAlign.Center
    }
    val width = upRawDp(props.width, 325.dp).coerceAtLeast(0.dp)
    val contentStyle = rememberUPResolvedStyle(props.contentStyle, diagnostics, "$ModalComponentName.contentStyle")
    val style = rememberUPResolvedStyle(props.customStyle, diagnostics, ModalComponentName)
    val confirmColor = UPColor.parse(props.confirmColor, UPTheme.Primary)
    val cancelColor = UPColor.parse(props.cancelColor, UPTheme.Content)
    val title = props.title
    val message = props.content

    UPPopup(
        props = UPPopupProps(
            show = true,
            mode = "center",
            overlay = true,
            closeOnClickOverlay = props.closeOnClickOverlay,
            round = 6,
            zoom = props.zoom,
            customStyle = style,
        ),
        modifier = modifier,
        onUpdateShow = onUpdateShow,
        onClose = onClose,
        content = {
            Column(
                modifier = Modifier
                    .widthIn(min = width, max = width)
                    .background(Color.White)
                    .applyUPResolvedStyle(contentStyle)
                    .upTestTag("modal"),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (title.isNotEmpty()) {
                    BasicText(
                        title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp, start = 24.dp, end = 24.dp)
                            .upTestTag("modal-title"),
                        style = TextStyle(
                            color = UPTheme.Content,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                        ),
                    )
                }
                if (message.isNotEmpty() || content != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 18.dp)
                            .upTestTag("modal-content"),
                        horizontalAlignment = when (contentAlign) {
                            TextAlign.Left -> Alignment.Start
                            TextAlign.Right -> Alignment.End
                            else -> Alignment.CenterHorizontally
                        },
                    ) {
                        if (message.isNotEmpty()) {
                            BasicText(
                                message,
                                modifier = Modifier.fillMaxWidth(),
                                style = TextStyle(
                                    color = UPTheme.Content,
                                    fontSize = 14.sp,
                                    textAlign = contentAlign,
                                ),
                            )
                        }
                        content?.invoke(this)
                    }
                }
                if (confirming && props.asyncCloseTip.isNotEmpty()) {
                    BasicText(
                        props.asyncCloseTip,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 4.dp)
                            .upTestTag("modal-async-tip"),
                        style = TextStyle(color = UPTheme.Tips, fontSize = 12.sp, textAlign = TextAlign.Center),
                    )
                }
                val buttons = buildList {
                    if (props.showCancelButton) add("cancel")
                    if (props.showConfirmButton) add("confirm")
                }
                if (buttons.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        buttons.asReversedIf(props.buttonReverse).forEach { kind ->
                            if (kind == "cancel") {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .upTestTag("modal-cancel"),
                                ) {
                                    UPButton(
                                        props = UPButtonProps(
                                            text = props.cancelText,
                                            type = "info",
                                            plain = true,
                                            color = cancelColor.toHexString(),
                                            shape = props.confirmButtonShape,
                                            customStyle = mapOf("width" to "100%"),
                                        ),
                                        modifier = Modifier.fillMaxWidth(),
                                        onClick = {
                                            if (props.asyncClose && confirming) {
                                                onCancelOnAsync?.invoke()
                                            } else {
                                                if (!props.asyncCancelClose) onUpdateShow?.invoke(false)
                                                onCancel?.invoke()
                                            }
                                        },
                                    )
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .upTestTag("modal-confirm"),
                                ) {
                                    UPButton(
                                        props = UPButtonProps(
                                            text = props.confirmText,
                                            type = "primary",
                                            color = confirmColor.toHexString(),
                                            shape = props.confirmButtonShape,
                                            customStyle = mapOf("width" to "100%"),
                                        ),
                                        modifier = Modifier.fillMaxWidth(),
                                        onClick = {
                                            if (props.asyncClose) confirming = true
                                            else onUpdateShow?.invoke(false)
                                            onConfirm?.invoke()
                                        },
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        diagnostics = diagnostics,
    )
}

private fun List<String>.asReversedIf(reverse: Boolean): List<String> = if (reverse) reversed() else this

private fun Color.toHexString(): String = "#%08X".format(toArgb())

/** Convenience overload for a simple title/message modal. */
@Composable
public fun UPModal(
    show: Boolean,
    title: String = "",
    content: String = "",
    onConfirm: (() -> Unit)? = null,
    onCancel: (() -> Unit)? = null,
    onUpdateShow: ((Boolean) -> Unit)? = null,
) {
    UPModal(
        props = UPModalProps(show = show, title = title, content = content),
        onUpdateShow = onUpdateShow,
        onConfirm = onConfirm,
        onCancel = onCancel,
    )
}
