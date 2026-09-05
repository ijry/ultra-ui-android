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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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

    // Deliberately *not* lifted into a window-level `Popup`, unlike `u-tooltip`. Two reasons:
    // the sheet already spans whatever the host gives it, so it gains no layering it needs;
    // and the screenshot renderer does not draw popup windows at all, so moving it there
    // would blank this component's reference image and throw away the pixel evidence that
    // its fills and text are painted. A bubble has to escape its trigger's clip; a bottom
    // sheet does not.
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
                // `closeHandler()` only forwards `close` when `closeOnClickOverlay` is set,
                // so with the flag off the scrim swallows the tap instead of dismissing.
                .upClickable(enabled = upActionSheetOverlayCloses(props.closeOnClickOverlay), onClick = { closeSheet() }),
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .heightIn(max = maxHeight)
                .background(Color.White, panelShape)
                // `<u-popup :safeAreaInsetBottom>`; false lets the sheet sit flush with the
                // screen edge instead of clearing the navigation bar.
                .then(if (props.safeAreaInsetBottom) Modifier.navigationBarsPadding() else Modifier)
                .verticalScroll(rememberScrollState())
                // uview styles the sheet panel, not the fullscreen overlay.
                .applyUPResolvedStyle(rememberUPResolvedStyle(props.customStyle, diagnostics, "UPActionSheet"))
                .upTestTag("action-sheet-panel"),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // `.u-action-sheet__header`: 12px 30px padding, a bold centred title, and an
            // absolutely positioned close button 15px in from the top-right corner.
            if (props.title.isNotEmpty()) {
                Box(Modifier.fillMaxWidth().upTestTag("action-sheet-header")) {
                    BasicText(
                        props.title,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 30.dp, vertical = 12.dp),
                        // `.u-line-1`.
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = TextStyle(
                            color = UPTheme.Main,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                        ),
                    )
                    UPIcon(
                        UPIconProps(name = "close", size = 17, bold = true, color = "#606266"),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(end = 15.dp, top = 15.dp)
                            // `@tap.stop="cancel"` — the header close button is `cancel`, not
                            // the overlay path, so it fires regardless of the overlay flag.
                            .upClickable { closeSheet(onCancel) }
                            .upTestTag("action-sheet-close"),
                        diagnostics = diagnostics,
                    )
                }
            }
            if (props.description.isNotEmpty()) {
                BasicText(
                    props.description,
                    modifier = Modifier
                        .fillMaxWidth()
                        // `margin: 18px 15px`, with the top margin collapsing under a title.
                        .padding(
                            top = upActionSheetDescriptionTopMarginPx(props.title, props.description).dp,
                            bottom = 18.dp,
                            start = 15.dp,
                            end = 15.dp,
                        )
                        .upTestTag("action-sheet-description"),
                    style = TextStyle(
                        color = UPTheme.Tips,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                    ),
                )
                // `<u-line v-if="description">` separates the description from the items.
                UPLine(diagnostics = diagnostics)
            }
            // Custom content replaces the action list entirely upstream (`v-if="$slots.default"`
            // with the list under `v-else`), and tapping it closes the sheet when
            // `closeOnClickAction` is set — the same rule the items follow.
            if (content != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .upClickable(onClick = { if (upActionSheetSlotCloses(props.closeOnClickAction)) closeSheet() })
                        .upTestTag("action-sheet-slot"),
                ) { content() }
            } else {
                props.actions.forEachIndexed { index, action ->
                    val map = action.upStringKeyMapOrEmpty()
                    // `!item.disabled && !item.loading`: a loading item is as unreachable as a
                    // disabled one, and only `disabled` dims the text.
                    val enabled = upActionSheetItemEnabled(action)
                    val loading = upActionSheetItemLoading(action)
                    val dimmed = map["disabled"].upBooleanValue(false)
                    val itemColor = when {
                        dimmed -> UPTheme.Light
                        map["color"].upStringValueOrEmpty().isNotEmpty() -> UPColor.parse(map["color"].upStringValueOrEmpty(), UPTheme.Main)
                        else -> UPTheme.Main
                    }
                    val itemSize = map["fontSize"].upTextUnitOr(16.sp)
                    // `getItemHoverStyle(0)`: the first item owns the sheet's rounded top
                    // corners, but only when no header sits above it.
                    val itemShape = if (upActionSheetItemRoundsTop(index, round.value, props.title, props.description)) {
                        RoundedCornerShape(topStart = round, topEnd = round)
                    } else {
                        RoundedCornerShape(0.dp)
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(itemShape)
                            .upTestTag("action-sheet-item-$index")
                            .upClickable(enabled = enabled, onClick = {
                                onSelect?.invoke(action)
                                if (props.closeOnClickAction) closeSheet()
                            })
                            // `.u-action-sheet__item-wrap__item { padding: 17px }`.
                            .padding(17.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        if (loading) {
                            // `<u-loading-icon v-else size="18" mode="circle">` replaces the
                            // whole label while an item is loading.
                            UPLoadingIcon(
                                UPLoadingIconProps(show = true, mode = "circle", size = 18),
                                modifier = Modifier.upTestTag("action-sheet-item-$index-loading"),
                                diagnostics = diagnostics,
                            )
                        } else {
                            BasicText(
                                actionOrOptionText(action, props.nameKey),
                                style = TextStyle(color = itemColor, fontSize = itemSize, textAlign = TextAlign.Center),
                            )
                            val subname = actionOrOptionText(action, props.subnameKey)
                            if (subname.isNotEmpty()) {
                                BasicText(
                                    subname,
                                    modifier = Modifier.padding(top = 10.dp),
                                    style = TextStyle(color = if (dimmed) UPTheme.Light else UPTheme.Tips, fontSize = 13.sp, textAlign = TextAlign.Center),
                                )
                            }
                        }
                    }
                    // `<u-line v-if="index !== actions.length - 1">`: dividers sit between
                    // items, so the last one has none.
                    if (upActionSheetItemHasDivider(index, props.actions.size)) {
                        UPLine(diagnostics = diagnostics)
                    }
                }
            }
            if (props.cancelText.isNotEmpty()) {
                // `<u-gap :bgColor="cancelGapColor" height="6">` separates the cancel row.
                UPGap(UPGapProps(height = 6, bgColor = "#eaeaec"), diagnostics = diagnostics)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .upTestTag("action-sheet-cancel")
                        // `cancel()` is unconditional, so the button works even with
                        // `closeOnClickOverlay` off.
                        .upClickable(onClick = { closeSheet(onCancel) })
                        .padding(17.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    // `.u-action-sheet__cancel-text` is declared twice upstream; the later
                    // 15px declaration wins.
                    BasicText(props.cancelText, style = TextStyle(color = UPTheme.Main, fontSize = 15.sp))
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
