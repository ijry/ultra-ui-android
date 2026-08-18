package net.lingyun.ultraui.android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.lingyun.ultraui.android.core.UPColor
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.core.upClickable
import net.lingyun.ultraui.android.core.upTestTag

private const val ActionSheetComponentName: String = "UPActionSheet"

/** Native Compose counterpart of uview-plus `u-action-sheet`. */
@Composable
public fun UPActionSheet(
    props: UPActionSheetProps = UPActionSheetProps(),
    modifier: Modifier = Modifier,
    onUpdateShow: ((Boolean) -> Unit)? = null,
    onClose: (() -> Unit)? = null,
    onSelect: ((UPRawValue) -> Unit)? = null,
    onCancel: (() -> Unit)? = null,
    content: (@Composable ColumnScope.() -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    if (!props.show) return

    val maxHeight = upRawDp(props.wrapMaxHeight, 600.dp).coerceAtLeast(120.dp)
    val round = upRawDp(props.round, 0.dp).coerceAtLeast(0.dp)
    val panelShape = RoundedCornerShape(topStart = round, topEnd = round)

    fun closeSheet(callback: (() -> Unit)? = onClose) {
        onUpdateShow?.invoke(false)
        callback?.invoke()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .upTestTag("action-sheet"),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.45f))
                .upTestTag("action-sheet-overlay")
                .upClickable(enabled = props.closeOnClickOverlay, onClick = { closeSheet() }),
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .heightIn(max = maxHeight)
                .background(Color.White, panelShape)
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .upTestTag("action-sheet-panel"),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (props.title.isNotEmpty() || props.description.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    if (props.title.isNotEmpty()) {
                        BasicText(
                            props.title,
                            style = TextStyle(
                                color = UPTheme.Main,
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center,
                            ),
                        )
                    }
                    if (props.description.isNotEmpty()) {
                        BasicText(
                            props.description,
                            style = TextStyle(
                                color = UPTheme.Tips,
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center,
                            ),
                        )
                    }
                }
            }
            props.actions.forEachIndexed { index, action ->
                val map = action.upStringKeyMapOrEmpty()
                val disabled = map["disabled"].upBooleanValue(false) || map["loading"].upBooleanValue(false)
                val itemColor = when {
                    disabled -> UPTheme.Light
                    map["color"].upStringValueOrEmpty().isNotEmpty() -> UPColor.parse(map["color"].upStringValueOrEmpty(), UPTheme.Main)
                    else -> UPTheme.Main
                }
                val itemSize = map["fontSize"].upTextUnitOr(16.sp)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .upTestTag("action-sheet-item-$index")
                        .upClickable(enabled = !disabled, onClick = {
                            onSelect?.invoke(action)
                            if (props.closeOnClickAction) closeSheet()
                        })
                        .padding(horizontal = 16.dp, vertical = 15.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                ) {
                    BasicText(
                        actionOrOptionText(action, props.nameKey),
                        style = TextStyle(color = itemColor, fontSize = itemSize, textAlign = TextAlign.Center),
                    )
                    val subname = actionOrOptionText(action, props.subnameKey)
                    if (subname.isNotEmpty()) {
                        BasicText(
                            subname,
                            style = TextStyle(color = if (disabled) UPTheme.Light else UPTheme.Tips, fontSize = 13.sp, textAlign = TextAlign.Center),
                        )
                    }
                }
            }
            content?.invoke(this)
            if (props.cancelText.isNotEmpty()) {
                Spacer(Modifier.padding(top = 6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF7F7F7))
                        .upTestTag("action-sheet-cancel")
                        .upClickable(onClick = {
                            closeSheet(onCancel)
                        })
                        .padding(vertical = 15.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    BasicText(props.cancelText, style = TextStyle(color = UPTheme.Main, fontSize = 16.sp))
                }
            }
        }
    }
}

/** Convenience overload for generated code that supplies only a title and actions. */
@Composable
public fun UPActionSheet(
    show: Boolean,
    actions: List<UPRawValue> = emptyList(),
    title: String = "",
    onSelect: ((UPRawValue) -> Unit)? = null,
    onUpdateShow: ((Boolean) -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    UPActionSheet(
        props = UPActionSheetProps(show = show, actions = actions, title = title),
        onSelect = onSelect,
        onUpdateShow = onUpdateShow,
        diagnostics = diagnostics,
    )
}
