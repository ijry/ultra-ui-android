package net.lingyun.ultraui.android.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import net.lingyun.ultraui.android.core.UPColor
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.core.asFiniteFloatOrNull
import net.lingyun.ultraui.android.core.upClickable
import net.lingyun.ultraui.android.core.upIntOrDefault
import net.lingyun.ultraui.android.core.upStringOrDefault
import net.lingyun.ultraui.android.core.report
import net.lingyun.ultraui.android.core.upTestTag

private const val PickerComponentName = "UPPicker"

/** Popup modes an inline panel can still express through its corner rounding. */
private val PickerInlinePopupModes = setOf("top", "bottom")

private const val PaginationComponentName = "UPPagination"

/** `.u-pagination` sets `font-size: 14px` and `color: $u-content-color` for every part. */
private val PaginationTextStyle = TextStyle(color = UPTheme.Content, fontSize = 14.sp)

/** `.u-pagination__pager__item--active` paints its own blue instead of the theme primary. */
private const val PaginationActivePageColor = "#409eff"

@Composable
public fun UPPicker(props: UPPickerProps = UPPickerProps(), modifier: Modifier = Modifier, onUpdateModelValue: ((List<UPRawValue>) -> Unit)? = null, onUpdateShow: ((Boolean) -> Unit)? = null, onChange: ((UPPickerEvent) -> Unit)? = null, onConfirm: ((UPPickerEvent) -> Unit)? = null, onCancel: (() -> Unit)? = null, onClose: (() -> Unit)? = null, toolbarRight: @Composable () -> Unit = {}, diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None) {
    // uview keeps the `hasInput` trigger mounted and opens the wheel from its own flag, so
    // the component stays on screen while the popup itself is hidden.
    var showByClickInput by remember { mutableStateOf(false) }
    val visible = props.show || props.pageInline || (props.hasInput && showByClickInput)
    if (!props.hasInput && !visible) return
    val selected = remember(props) { mutableStateListOf<Int>().apply { addAll(resolvePickerIndexes(props)) } }
    val popupMode = upPickerPopupMode(props.popupMode, diagnostics, PickerComponentName)
    LaunchedEffect(props.maskClass, diagnostics) {
        if (props.maskClass.isNotBlank()) diagnostics.report(PickerComponentName, "maskClass", props.maskClass, "CSS class hooks have no native Android equivalent; the wheel mask is styled through maskStyle instead.")
    }
    LaunchedEffect(popupMode, diagnostics) {
        if (popupMode !in PickerInlinePopupModes) diagnostics.report(PickerComponentName, "popupMode", props.popupMode, "Inline render; sliding in from $popupMode needs a window-level overlay.")
    }
    // `<u-popup :duration :zIndex>` wraps the wheel upstream. Inline there is no scrim, but
    // the panel still owns its entry fade and its stacking. `entered` starts at the current
    // visibility so a panel that is already open renders opaque immediately; the fade plays
    // when the wheel actually opens, which is also the only case a user can perceive.
    var entered by remember { mutableStateOf(visible) }
    LaunchedEffect(visible) { entered = visible }
    Column(modifier.fillMaxWidth().upTestTag("picker-wrapper")) {
        if (props.hasInput) Box(Modifier.fillMaxWidth().upTestTag("picker-input")) {
            UPInput(
                props = upPickerInputProps(
                    label = upPickerInputLabel(props.columns, props.modelValue.ifEmpty { props.value }, props.keyName, props.valueName),
                    inputBorder = props.inputBorder,
                    placeholder = props.placeholder,
                    disabled = props.disabled,
                    disabledColor = props.disabledColor,
                    inputProps = props.inputProps,
                    diagnostics = diagnostics,
                    component = PickerComponentName,
                ),
                diagnostics = diagnostics,
            )
            // uview lays a <cover-view> over the readonly field so the tap opens the wheel
            // instead of focusing the input.
            Box(Modifier.matchParentSize().upClickable(enabled = !props.disabled) { showByClickInput = !showByClickInput }.upTestTag("picker-input-cover"))
        }
        if (!visible) return@Column
        val panelShape = upPickerPopupShape(popupMode, upRawDp(props.round, 0.dp).coerceAtLeast(0.dp))
        val panelAlpha by animateFloatAsState(
            targetValue = if (entered) 1f else 0f,
            animationSpec = tween(upPopupTransitionDuration(props.duration)),
            label = "up-picker-fade",
        )
        Column(Modifier.fillMaxWidth().zIndex(props.zIndex.asFiniteFloatOrNull() ?: 10076f).alpha(panelAlpha).clip(panelShape).background(UPColor.parse(props.bgColor, Color.White), panelShape).applyUPResolvedStyle(rememberUPResolvedStyle(props.customStyle, diagnostics, PickerComponentName)).upTestTag("picker")) {
            if (props.showToolbar) Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                BasicText(props.cancelText, modifier = Modifier.upClickable(onClick = { showByClickInput = false; onCancel?.invoke(); onUpdateShow?.invoke(false); onClose?.invoke() }).upTestTag("picker-cancel"), style = TextStyle(color = UPColor.parse(props.cancelColor, UPTheme.Tips)))
                BasicText(props.title, style = TextStyle(color = UPTheme.Main))
                // `toolbarRightSlot` replaces the confirm label upstream, so the confirm tap
                // disappears with it and the slot owns that corner.
                if (upPickerToolbarShowsConfirm(props.toolbarRightSlot)) {
                    BasicText(props.confirmText, modifier = Modifier.upClickable(onClick = { val event = pickerEvent(props, selected); showByClickInput = false; onConfirm?.invoke(event); onUpdateModelValue?.invoke(pickerModelValues(props, selected)); onUpdateShow?.invoke(false); onClose?.invoke() }).upTestTag("picker-confirm"), style = TextStyle(color = UPColor.parse(props.confirmColor, UPTheme.Primary)))
                } else {
                    Box(Modifier.upTestTag("picker-toolbar-right")) { toolbarRight() }
                }
            }
            // Each column is a `visibleItemCount * itemHeight` wheel that scrolls internally,
            // as upstream does. A flat column pushes later options outside the root bounds,
            // where they measure zero-height and cannot be tapped.
            val itemHeight = upRawDp(props.itemHeight, 44.dp).coerceAtLeast(16.dp)
            val visibleCount = props.visibleItemCount.upIntOrDefault(5).coerceIn(1, 20)
            val maskStyle = rememberUPResolvedStyle(props.maskStyle, diagnostics, "$PickerComponentName.maskStyle")
            val maskDeclared = upPickerMaskStyleDeclared(props.maskStyle)
            props.columns.forEachIndexed { columnIndex, rawColumn ->
                val column = rawColumn.upItemsOrEmpty()
                Box(Modifier.fillMaxWidth().height(itemHeight * visibleCount)) {
                    // `.u-picker--loading` covers the whole wheel with an opaque panel and
                    // a circle spinner, so no option is reachable while it is up.
                    if (props.loading) {
                        Box(Modifier.matchParentSize().background(Color.White).upTestTag("picker-loading-$columnIndex"), contentAlignment = Alignment.Center) {
                            UPLoadingIcon(UPLoadingIconProps(show = true, mode = "circle"), diagnostics = diagnostics)
                        }
                        return@Box
                    }
                    Column(
                        Modifier.fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .upTestTag("picker-column-$columnIndex"),
                    ) {
                        column.forEachIndexed { optionIndex, option ->
                            val label = actionOrOptionText(option, props.keyName, option.toString())
                            // uview sets lineHeight = itemHeight, so the label sits centred in its row.
                            Box(
                                Modifier.fillMaxWidth().height(itemHeight)
                                    .background(if (selected.getOrElse(columnIndex) { 0 } == optionIndex) Color(0xFFEAF3FF) else Color.Transparent)
                                    .upClickable(onClick = { selected[columnIndex] = optionIndex; if (props.immediateChange) onChange?.invoke(pickerEvent(props, selected, columnIndex, optionIndex)) })
                                    .padding(horizontal = 12.dp),
                                contentAlignment = Alignment.CenterStart,
                            ) {
                                BasicText(label, style = TextStyle(color = UPTheme.Main, fontSize = 14.sp))
                            }
                        }
                    }
                    // uview hands `mask-style` to picker-view, which paints it over the wheel.
                    // A plain Box takes no pointer input, so the options underneath stay tappable.
                    if (maskDeclared) Box(Modifier.matchParentSize().applyUPResolvedStyle(maskStyle).upTestTag("picker-mask-$columnIndex"))
                }
            }
        }
    }
}

/** Mirrors `u-popup`'s rounding: only the edges facing the screen centre are rounded. */
internal fun upPickerPopupShape(mode: String, radius: Dp): RoundedCornerShape = when {
    radius <= 0.dp -> RoundedCornerShape(0.dp)
    mode == "top" -> RoundedCornerShape(bottomStart = radius, bottomEnd = radius)
    mode == "bottom" -> RoundedCornerShape(topStart = radius, topEnd = radius)
    mode == "left" -> RoundedCornerShape(topEnd = radius, bottomEnd = radius)
    mode == "right" -> RoundedCornerShape(topStart = radius, bottomStart = radius)
    else -> RoundedCornerShape(radius)
}

@Composable
public fun UPPickerColumn(props: UPPickerColumnProps = UPPickerColumnProps(), modifier: Modifier = Modifier, content: @Composable () -> Unit = {}) { Box(modifier.applyUPResolvedStyle(rememberUPResolvedStyle(props.customStyle, UPCompatibilityDiagnostics.None, "UPPickerColumn")).upTestTag("picker-column")) { content() } }

internal fun pickerEvent(
    props: UPPickerProps,
    indexes: List<Int>,
    columnIndex: Int = 0,
    index: Int = indexes.getOrElse(columnIndex) { 0 },
): UPPickerEvent {
    val values = props.columns.mapIndexed { columnIndex, rawColumn -> rawColumn.upItemsOrEmpty().getOrNull(indexes.getOrElse(columnIndex) { 0 }) ?: "" }
    return UPPickerEvent(
        value = values,
        indexs = indexes.toList(),
        index = index,
        values = props.columns,
        columnIndex = columnIndex,
    )
}

internal fun resolvePickerIndexes(props: UPPickerProps): List<Int> {
    val controlledValues = props.modelValue.ifEmpty { props.value }
    return props.columns.mapIndexed { columnIndex, rawColumn ->
        val options = rawColumn.upItemsOrEmpty()
        val controlledValue = controlledValues.getOrNull(columnIndex)
        val controlledIndex = controlledValue?.let { value ->
            options.indexOfFirst { option ->
                val comparable = option.upStringKeyMapOrEmpty()[props.valueName] ?: option
                comparable.upLooseEquals(value)
            }.takeIf { it >= 0 }
        }
        val fallbackIndex = props.defaultIndex.getOrNull(columnIndex).upIntOrDefault(0)
        (controlledIndex ?: fallbackIndex).coerceIn(0, (options.size - 1).coerceAtLeast(0))
    }
}

internal fun pickerModelValues(props: UPPickerProps, indexes: List<Int>): List<UPRawValue> =
    props.columns.mapIndexed { columnIndex, rawColumn ->
        val option = rawColumn.upItemsOrEmpty().getOrNull(indexes.getOrElse(columnIndex) { 0 })
        option.upStringKeyMapOrEmpty()[props.valueName] ?: option
    }

@Composable
public fun UPPagination(props: UPPaginationProps = UPPaginationProps(), modifier: Modifier = Modifier, onUpdateCurrentPage: ((Int) -> Unit)? = null, onCurrentChange: ((Int) -> Unit)? = null, onUpdatePageSize: ((Int) -> Unit)? = null, onSizeChange: ((Int) -> Unit)? = null, diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None) {
    val parts = upPaginationLayoutParts(props.layout)
    val sizeOptions = upNormalizedPageSizes(props.pageSizes)
    val total = props.total.upIntOrDefault(0).coerceAtLeast(0)
    var pageSize by remember(props) { mutableIntStateOf(props.pageSize.upIntOrDefault(10).coerceAtLeast(1)) }
    val totalPages = upPaginationTotalPages(total, pageSize)
    var current by remember(props) { mutableIntStateOf(props.currentPage.upIntOrDefault(1).coerceIn(1, totalPages)) }
    // uview declares `hideOnSinglePage` but never reads it; Android honors the documented meaning.
    if (props.hideOnSinglePage && totalPages <= 1) return
    // `goTo` drops the `'...'` markers, out-of-range pages and re-clicks on the active page.
    val goTo: (UPRawValue) -> Unit = { raw ->
        val page = raw.upIntOrDefault(0)
        if (page in 1..totalPages && page != current) {
            current = page
            onUpdateCurrentPage?.invoke(page)
            onCurrentChange?.invoke(page)
        }
    }
    Row(
        modifier.fillMaxWidth().applyUPResolvedStyle(rememberUPResolvedStyle(props.customStyle, diagnostics, PaginationComponentName)).upTestTag("pagination"),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (parts.contains("total") && total > 0) {
            BasicText("共 $total 条", modifier = Modifier.padding(end = 10.dp).upTestTag("pagination-total"), style = PaginationTextStyle)
        }
        if (parts.contains("prev")) {
            UPPaginationButton(props.prevText, "arrow-left", current > 1, props, "pagination-prev", diagnostics) { goTo(current - 1) }
        }
        if (parts.contains("pager")) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                upPaginationDisplayedPages(totalPages, current).forEachIndexed { index, page ->
                    val label = page.upStringOrDefault()
                    val isActive = page.upIntOrDefault(0) == current
                    BasicText(
                        label,
                        modifier = Modifier
                            .padding(horizontal = 2.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (isActive) UPColor.parse(PaginationActivePageColor, UPTheme.Primary) else Color.Transparent)
                            .upClickable(enabled = label != "...", onClick = { goTo(page) })
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .upTestTag("pagination-page-$index"),
                        style = PaginationTextStyle.copy(color = if (isActive) Color.White else UPTheme.Content),
                    )
                }
            }
        }
        if (parts.contains("next")) {
            UPPaginationButton(props.nextText, "arrow-right", current < totalPages, props, "pagination-next", diagnostics) { goTo(current + 1) }
        }
        if (parts.contains("sizes") && sizeOptions.isNotEmpty()) {
            BasicText(
                upPageSizeLabel(sizeOptions, pageSize),
                modifier = Modifier
                    .padding(start = 10.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(UPColor.parse(props.buttonBgColor, UPTheme.Background))
                    .border(1.dp, UPColor.parse(props.buttonBorderColor, UPTheme.Disabled), RoundedCornerShape(4.dp))
                    .upClickable(
                        onClick = {
                            // `handleSizeChange` reads the picked option, falls back to the first
                            // one and emits nothing for falsy sizes. A native tap cycles instead
                            // of opening a `<select>`, which has no Android counterpart.
                            val next = (upPageSizeIndex(sizeOptions, pageSize) + 1) % sizeOptions.size
                            upPageSizeAt(sizeOptions, next)?.let { picked ->
                                pageSize = picked
                                onUpdatePageSize?.invoke(picked)
                                onSizeChange?.invoke(picked)
                            }
                        },
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .upTestTag("pagination-sizes"),
                style = PaginationTextStyle,
            )
        }
    }
}

/** `.u-pagination__button` keeps its own background and border colors while disabled fades it. */
@Composable
private fun UPPaginationButton(text: String, icon: String, enabled: Boolean, props: UPPaginationProps, tag: String, diagnostics: UPCompatibilityDiagnostics, onClick: () -> Unit) {
    val shape = RoundedCornerShape(4.dp)
    Box(
        Modifier
            .padding(horizontal = 3.dp)
            .clip(shape)
            .background(UPColor.parse(props.buttonBgColor, UPTheme.Background))
            .border(1.dp, UPColor.parse(props.buttonBorderColor, UPTheme.Disabled), shape)
            .alpha(if (enabled) 1f else 0.5f)
            .upClickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .upTestTag(tag),
        contentAlignment = Alignment.Center,
    ) {
        if (text.isEmpty()) {
            UPIcon(props = UPIconProps(name = icon, color = "#606266", size = 16), diagnostics = diagnostics)
        } else {
            BasicText(text, style = PaginationTextStyle)
        }
    }
}

/**
 * `u-select`. The panel renders inline underneath the trigger rather than in a
 * window-level overlay, so `overlay`/`overlayStyle`/`overlayOpacity` have nothing to
 * apply to and stay documented as inert. Everything the panel itself owns is applied
 * here: its cap (`maxHeight`, including `90vh`), stacking (`zIndex`), colours
 * (`itemColor`, `iconColor`, `iconSize`), the outlined trigger (`border`) and the fade
 * (`duration`, carried by the panel because the overlay that carries it upstream is
 * absent).
 */
@Composable
public fun UPSelect(props: UPSelectProps = UPSelectProps(), modifier: Modifier = Modifier, onUpdateCurrent: ((UPRawValue) -> Unit)? = null, onSelect: ((UPRawValue) -> Unit)? = null, diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None) {
    var open by remember { mutableStateOf(false) }
    var current by remember(props) { mutableStateOf(props.current) }
    // `currentLabel` finds the selected option's label; `showOptionsLabel` decides whether
    // the trigger prints it or keeps the static `label` even after a selection.
    val currentLabel = actionOrOptionText(
        props.options.firstOrNull { it.upStringKeyMapOrEmpty()[props.keyName].upLooseEquals(current) },
        props.labelName,
        props.label,
    )
    // `normalizedOptionsWidth` sizes the options panel; empty leaves it to the parent.
    val optionsWidth = upSelectOptionsWidthDp(props.optionsWidth)?.dp
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val maxHeight = upSelectMaxHeightDp(
        props.maxHeight,
        configuration.screenHeightDp.toFloat(),
        configuration.screenWidthDp.toFloat(),
    )?.dp
    // `resolvedItemColor` / `resolvedIconColor` fall back to the theme's main and content
    // colours; the trigger text always uses main.
    val itemColor = UPColor.parse(props.itemColor.ifBlank { null }, UPTheme.Main)
    val iconColor = props.iconColor.ifBlank { "#606266" }
    Column(modifier.fillMaxWidth().applyUPResolvedStyle(rememberUPResolvedStyle(props.customStyle, diagnostics, "UPSelect")).upTestTag("select")) {
        Row(
            // `.u-select__label--border`: 1px outline, 4px radius, 8x10 padding, 36px floor.
            Modifier
                .fillMaxWidth()
                .then(
                    if (props.border) {
                        Modifier
                            .heightIn(min = 36.dp)
                            .border(1.dp, UPTheme.Border, RoundedCornerShape(4.dp))
                            .background(Color.White, RoundedCornerShape(4.dp))
                    } else {
                        Modifier
                    },
                )
                .upClickable(enabled = !props.disabled, onClick = { open = !open })
                .padding(horizontal = if (props.border) 10.dp else 12.dp, vertical = if (props.border) 8.dp else 12.dp)
                .upTestTag("select-trigger"),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            BasicText(
                upSelectTriggerText(props.showOptionsLabel, currentLabel, props.label),
                style = TextStyle(color = UPTheme.Main),
            )
            UPIcon(UPIconProps(name = "arrow-down", size = props.iconSize, color = iconColor), diagnostics = diagnostics)
        }
        if (open) {
            // `<u-overlay :duration="duration + 50">` is the only place upstream spends
            // `duration`; with no overlay inline, the panel itself fades instead.
            var entered by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) { entered = true }
            val opacity by animateFloatAsState(
                targetValue = if (entered) 1f else 0f,
                animationSpec = tween(props.duration.upIntOrDefault(300).coerceAtLeast(0)),
                label = "up-select-fade",
            )
            Column(
                Modifier
                    // `optionsWrapStyle` puts the wrap at `zIndex + 1`, above the overlay.
                    .zIndex(upSelectOptionsZIndex(props.zIndex))
                    .then(if (optionsWidth != null) Modifier.width(optionsWidth) else Modifier.fillMaxWidth())
                    // `.u-select__options` — 1px border, 4px radius, white panel.
                    .border(1.dp, UPTheme.Border, RoundedCornerShape(4.dp))
                    .background(Color.White, RoundedCornerShape(4.dp))
                    .alpha(opacity)
                    // `maxHeight` with `overflowY: auto`; `90vh` by default.
                    .then(if (maxHeight != null) Modifier.heightIn(max = maxHeight) else Modifier)
                    .verticalScroll(rememberScrollState())
                    .upTestTag("select-options"),
            ) {
                props.options.forEach { option ->
                    BasicText(
                        actionOrOptionText(option, props.labelName),
                        modifier = Modifier.fillMaxWidth().upClickable(onClick = { val value = option.upStringKeyMapOrEmpty()[props.keyName]; current = value; open = false; onUpdateCurrent?.invoke(value); onSelect?.invoke(option) }).padding(horizontal = 12.dp, vertical = 10.dp).upTestTag("select-option"),
                        style = TextStyle(color = itemColor),
                    )
                }
            }
        }
    }
}
