package net.lingyun.ultraui.android.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.lingyun.ultraui.android.core.UPColor
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.core.report
import net.lingyun.ultraui.android.core.upClickable
import net.lingyun.ultraui.android.core.upDimension
import net.lingyun.ultraui.android.core.upIntOrDefault
import net.lingyun.ultraui.android.core.upSafeEnum
import net.lingyun.ultraui.android.core.upTestTag

private const val TabsComponentName: String = "UPTabs"

private const val SubsectionComponentName: String = "UPSubsection"

/** `skewX(25deg)` from the `card` corner decoration, expressed as its horizontal shear. */
private const val TabsCardCornerSkew: Float = 0.4663f

/**
 * Native Compose counterpart of uview-plus `u-tabs`.
 *
 * Keeps uview's nav structure: a wrapper carrying the shape background, a horizontally
 * scrollable nav whose items size to their content, and one sliding line placed from the
 * measured item widths instead of a line per item.
 */
@Composable
public fun UPTabs(props: UPTabsProps = UPTabsProps(), modifier: Modifier = Modifier, onChange: ((Int) -> Unit)? = null, onClick: ((Int) -> Unit)? = null, onUpdateCurrent: ((UPRawValue) -> Unit)? = null, diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None) {
    var current by remember { mutableIntStateOf(props.current.upIntOrDefault(0).coerceAtLeast(0)) }
    LaunchedEffect(props.current) { current = props.current.upIntOrDefault(0).coerceAtLeast(0) }
    val style = rememberUPResolvedStyle(props.customStyle, diagnostics, TabsComponentName)
    val shapeMode = upTabsShapeMode(props.shapeMode, diagnostics, TabsComponentName)
    // `lineBgSize` scales a background image. The native line paints a solid color, so the
    // keyword only becomes an actual downgrade when `lineColor` is not a plain color.
    val lineBgSize = upSafeEnum(props.lineBgSize, UPTabsLineBgSizes, "cover", diagnostics, TabsComponentName, "lineBgSize")
    LaunchedEffect(lineBgSize, props.lineColor, diagnostics) {
        if (props.lineColor.isNotEmpty() && UPColor.parseOrNull(props.lineColor) == null) {
            diagnostics.report(TabsComponentName, "lineBgSize", lineBgSize, "background-size only scales background images; the native line paints a solid color.")
        }
    }

    val density = LocalDensity.current
    val itemWidths = remember(props.list.size) { mutableStateListOf<Float>().apply { repeat(props.list.size) { add(0f) } } }
    val lineWidth = upDimension(props.lineWidth, 20.dp)
    // `setLineLeft` animates the offset with the `duration` prop, so the line is a single
    // node that slides rather than one line per item.
    val lineOffset by animateDpAsState(
        targetValue = upTabsLineOffset(itemWidths, current, lineWidth.value).dp,
        animationSpec = tween(durationMillis = props.duration.upIntOrDefault(300).coerceAtLeast(0)),
        label = "up-tabs-line",
    )
    val wrapperColor = upTabsShapeWrapperColor(shapeMode)
    val wrapperShape = RoundedCornerShape(upTabsShapeWrapperRadius(shapeMode))
    val navPadding = upTabsShapeNavPadding(shapeMode)
    val scrollState = rememberScrollState()

    Row(modifier.fillMaxWidth().applyUPResolvedStyle(style).upTestTag("tabs"), verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier
                .weight(1f)
                .then(if (wrapperColor != null) Modifier.background(UPColor.parse(wrapperColor, Color.Transparent), wrapperShape) else Modifier)
                .padding(upTabsShapeWrapperPadding(shapeMode)),
        ) {
            Box(if (props.scrollable) Modifier.horizontalScroll(scrollState) else Modifier.fillMaxWidth()) {
                Box(Modifier.padding(top = navPadding.first, bottom = navPadding.second).upTestTag("tabs-nav")) {
                    Row(
                        modifier = if (props.scrollable) Modifier else Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(upTabsShapeItemSpacing(shapeMode)),
                    ) {
                        props.list.forEachIndexed { index, item ->
                            val fields = item.upStringKeyMapOrEmpty()
                            val title = actionOrOptionText(item, props.keyName, item.upStringValueOrEmpty())
                            val disabled = fields["disabled"].upBooleanValue(false)
                            val isActive = index == current
                            val badge = fields["badge"].upStringKeyMapOrEmpty()
                            val itemRadius = upTabsShapeItemRadius(shapeMode)
                            // `card` rounds only its top corners so the item reads as a tab.
                            val itemShape = if (shapeMode == "card") RoundedCornerShape(topStart = itemRadius, topEnd = itemRadius) else RoundedCornerShape(itemRadius)
                            val activeColors = upTabsShapeActiveColors(shapeMode).map { UPColor.parse(it, Color.Transparent) }
                            val itemColors = if (isActive) activeColors else listOfNotNull(upTabsShapeItemColor(shapeMode)?.let { UPColor.parse(it, Color.Transparent) })
                            val textStyle = rememberUPResolvedStyle(if (isActive) props.activeStyle else props.inactiveStyle, diagnostics, TabsComponentName)
                            val textColor = UPColor.parse(upTabsTextColorHex(shapeMode, isActive, disabled, props.activeStyle, props.inactiveStyle), UPTheme.Content)
                            // `itemComputedStyle` only merges the shape height when the caller left `itemStyle` alone.
                            val shapeHeight = upTabsShapeItemHeight(shapeMode).takeIf { !upTabsItemStyleDeclared(props.itemStyle) }
                            Box(
                                modifier = Modifier
                                    .then(if (props.scrollable) Modifier else Modifier.weight(1f))
                                    .onGloballyPositioned { coordinates ->
                                        val measured = with(density) { coordinates.size.width.toDp() }.value
                                        if (index < itemWidths.size && itemWidths[index] != measured) itemWidths[index] = measured
                                    }
                                    .then(if (shapeHeight != null) Modifier.height(shapeHeight) else Modifier)
                                    .applyUPResolvedStyle(rememberUPResolvedStyle(props.itemStyle, diagnostics, TabsComponentName))
                                    .then(
                                        when {
                                            itemColors.isEmpty() -> Modifier
                                            itemColors.size == 1 -> Modifier.background(itemColors[0], itemShape)
                                            else -> Modifier.background(Brush.horizontalGradient(itemColors), itemShape)
                                        },
                                    )
                                    // uview emits `click` before the disabled guard, and skips
                                    // `update:current`/`change` when the tab is already active.
                                    .upClickable(
                                        onClick = {
                                            onClick?.invoke(index)
                                            if (disabled || index == current) return@upClickable
                                            current = index
                                            onUpdateCurrent?.invoke(index)
                                            onChange?.invoke(index)
                                        },
                                    )
                                    .padding(horizontal = upTabsShapeItemPadding(shapeMode))
                                    .upTestTag("tabs-item-$index"),
                                contentAlignment = Alignment.Center,
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                    val icon = fields["icon"].upStringValueOrEmpty()
                                    if (icon.isNotEmpty()) UPIcon(props = UPIconProps(name = icon, customStyle = props.iconStyle), diagnostics = diagnostics)
                                    val label: @Composable () -> Unit = {
                                        BasicText(
                                            title,
                                            modifier = Modifier.applyUPResolvedStyle(textStyle),
                                            style = TextStyle(color = textColor, fontSize = (textStyle.fontSize ?: 15.dp).value.sp, fontWeight = textStyle.fontWeight),
                                            maxLines = 1,
                                            softWrap = false,
                                        )
                                    }
                                    if (upTabsBadgeVisible(badge)) UPBadge(props = upTabsItemBadgeProps(badge), content = label, diagnostics = diagnostics) else label()
                                }
                                if (isActive && shapeMode == "pill-arrow") {
                                    // `__active-arrow`: a CSS border triangle hanging 6px below the pill.
                                    Canvas(Modifier.align(Alignment.BottomCenter).offset(y = 6.dp).width(12.dp).height(6.dp)) {
                                        drawPath(
                                            Path().apply {
                                                moveTo(0f, 0f)
                                                lineTo(size.width, 0f)
                                                lineTo(size.width / 2f, size.height)
                                                close()
                                            },
                                            activeColors.lastOrNull() ?: UPTheme.Primary,
                                        )
                                    }
                                }
                                if (isActive && shapeMode == "card" && index < props.list.lastIndex) {
                                    // `__card-corner`: a skewed strip overlapping the next card.
                                    Canvas(Modifier.align(Alignment.CenterEnd).offset(x = 10.dp).width(20.dp).fillMaxHeight()) {
                                        val shear = size.height / 2f * TabsCardCornerSkew
                                        drawPath(
                                            Path().apply {
                                                moveTo(shear, 0f)
                                                lineTo(size.width + shear, 0f)
                                                lineTo(size.width - shear, size.height)
                                                lineTo(-shear, size.height)
                                                close()
                                            },
                                            activeColors.firstOrNull() ?: Color.Transparent,
                                        )
                                    }
                                }
                            }
                        }
                    }
                    if (upTabsShowLine(shapeMode)) {
                        Box(
                            Modifier
                                .align(Alignment.BottomStart)
                                .offset(x = lineOffset, y = (-2).dp)
                                .width(lineWidth)
                                .height(upDimension(props.lineHeight, 3.dp))
                                .background(UPColor.parse(props.lineColor, UPTheme.Primary), RoundedCornerShape(100.dp))
                                .upTestTag("tabs-line"),
                        )
                    }
                }
            }
        }
    }
}

@Composable
public fun UPTabs(list: List<UPRawValue>, current: Int = 0, modifier: Modifier = Modifier, onChange: ((Int) -> Unit)? = null) = UPTabs(UPTabsProps(list = list, current = current), modifier, onChange)

@Composable
public fun UPTabsItem(props: UPTabsItemProps = UPTabsItemProps(), modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(modifier.applyUPResolvedStyle(rememberUPResolvedStyle(props.customStyle, UPCompatibilityDiagnostics.None, "UPTabsItem")).upTestTag("tabs-item")) { content() }
}

/**
 * Native Compose counterpart of uview-plus `u-subsection`.
 *
 * `mode` drives two visually distinct controls: `button` floats a white pill inside a grey
 * track, `subsection` outlines every item and slides an `activeColor` bar underneath the
 * labels. The sliding bar is a real sibling of the items, animated over
 * [UPSubsectionBarDurationMillis], so the widths are measured rather than assumed.
 */
@Composable
public fun UPSubsection(
    props: UPSubsectionProps = UPSubsectionProps(),
    modifier: Modifier = Modifier,
    onChange: ((Int) -> Unit)? = null,
    onUpdateCurrent: ((UPRawValue) -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    var current by remember { mutableIntStateOf(props.current.upIntOrDefault(0).coerceAtLeast(0)) }
    LaunchedEffect(props.current) { current = props.current.upIntOrDefault(0).coerceAtLeast(0) }
    val mode = upSubsectionMode(props.mode, diagnostics, SubsectionComponentName)
    val count = props.list.size
    val barIndex = upSubsectionBarIndex(current, count)
    val cornerRadius = 4.dp
    val barColor = UPColor.parse(
        upSubsectionBarColorHex(mode, props.disabled, props.activeColor),
        UPTheme.Primary,
    )
    val itemBorderColor = upSubsectionItemBorderColorHex(mode, props.disabled, props.activeColor)
        ?.let { UPColor.parse(it, UPTheme.Primary) }
    val wrapperColor = upSubsectionWrapperColorHex(mode, props.bgColor)
        ?.let { UPColor.parse(it, Color(0xFFEEEEEF)) }
        ?: Color.Transparent
    val fontSize = upDimension(props.fontSize, 12.dp).value.sp

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(upSubsectionHeightDp(mode).dp)
            .background(wrapperColor, RoundedCornerShape(cornerRadius))
            .applyUPResolvedStyle(rememberUPResolvedStyle(props.customStyle, diagnostics, SubsectionComponentName))
            .upTestTag("subsection")
            .padding(upSubsectionWrapperPaddingDp(mode).dp),
    ) {
        if (count > 0) {
            // `barStyle` sizes the bar to one item and slides it with `translateX`.
            val slotWidth by animateFloatAsState(
                targetValue = barIndex.toFloat(),
                animationSpec = tween(durationMillis = UPSubsectionBarDurationMillis),
                label = "up-subsection-bar",
            )
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val itemWidth = maxWidth / count
                val barShape = when (upSubsectionBarPosition(barIndex, count)) {
                    // The outlined bar only rounds the outer edge it currently touches.
                    "first" -> if (mode == "subsection") {
                        RoundedCornerShape(topStart = cornerRadius, bottomStart = cornerRadius)
                    } else {
                        RoundedCornerShape(cornerRadius)
                    }
                    "last" -> if (mode == "subsection") {
                        RoundedCornerShape(topEnd = cornerRadius, bottomEnd = cornerRadius)
                    } else {
                        RoundedCornerShape(cornerRadius)
                    }
                    "center" -> if (mode == "subsection") RoundedCornerShape(0.dp) else RoundedCornerShape(cornerRadius)
                    else -> RoundedCornerShape(cornerRadius)
                }
                Box(
                    modifier = Modifier
                        .offset(x = itemWidth * slotWidth)
                        .width(itemWidth)
                        .fillMaxHeight()
                        .background(barColor, barShape)
                        .upTestTag("subsection-bar"),
                )
                Row(modifier = Modifier.fillMaxSize()) {
                    props.list.forEachIndexed { index, item ->
                        val label = actionOrOptionText(item, props.keyName, item.upStringValueOrEmpty())
                        val active = current == index
                        val textColor = upSubsectionTextColorHex(
                            mode = mode,
                            active = active,
                            disabled = props.disabled,
                            activeOverride = upSubsectionItemColorOverride(item, props.activeColorKeyName),
                            inactiveOverride = upSubsectionItemColorOverride(item, props.inactiveColorKeyName),
                            activeColor = props.activeColor,
                            inactiveColor = props.inactiveColor,
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                // `u-subsection__item--no-border-right` drops the shared edge so
                                // adjacent items do not double the 1px outline.
                                .then(
                                    if (itemBorderColor == null) {
                                        Modifier
                                    } else {
                                        Modifier.border(
                                            width = 1.dp,
                                            color = itemBorderColor,
                                            shape = when {
                                                count == 1 -> RoundedCornerShape(cornerRadius)
                                                index == 0 -> RoundedCornerShape(topStart = cornerRadius, bottomStart = cornerRadius)
                                                index == count - 1 -> RoundedCornerShape(topEnd = cornerRadius, bottomEnd = cornerRadius)
                                                else -> RoundedCornerShape(0.dp)
                                            },
                                        )
                                    },
                                )
                                .upClickable(
                                    enabled = !props.disabled,
                                    onClick = {
                                        current = index
                                        onUpdateCurrent?.invoke(index)
                                        onChange?.invoke(index)
                                    },
                                )
                                .padding(horizontal = 4.dp)
                                .upTestTag("subsection-item-$index"),
                            contentAlignment = Alignment.Center,
                        ) {
                            BasicText(
                                label,
                                style = TextStyle(
                                    color = UPColor.parse(textColor, UPTheme.Main),
                                    fontSize = fontSize,
                                    fontWeight = if (upSubsectionBold(props.bold, active, props.disabled)) {
                                        FontWeight.Bold
                                    } else {
                                        FontWeight.Normal
                                    },
                                ),
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Parent state each `UPStepsItem` needs. uview passes this down as `parentData`; the
 * items cannot derive their own status without knowing `current` and the sibling count.
 */
@Immutable
private data class UPStepsContext(
    val current: Int,
    val direction: String,
    val activeColor: String,
    val inactiveColor: String,
    val activeIcon: String,
    val inactiveIcon: String,
    val dot: Boolean,
    val nextIndex: () -> Int,
)

private val LocalUPSteps = staticCompositionLocalOf<UPStepsContext?> { null }

/** uview's four step states (`u-steps-item.vue`: statusClass). */
internal fun stepsItemStatus(index: Int, current: Int, error: Boolean): String = when {
    current == index -> if (error) "error" else "process"
    error -> "error"
    current > index -> "finish"
    else -> "wait"
}

@Composable
public fun UPSteps(props: UPStepsProps = UPStepsProps(), modifier: Modifier = Modifier, onClick: ((Int) -> Unit)? = null, diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None, content: @Composable () -> Unit) {
    val direction = upSafeEnum(props.direction, setOf("row", "column"), "row", diagnostics, "UPSteps", "direction")
    var nextIndex = 0
    val context = UPStepsContext(
        current = props.current.upIntOrDefault(0),
        direction = direction,
        activeColor = props.activeColor,
        inactiveColor = props.inactiveColor,
        activeIcon = props.activeIcon,
        inactiveIcon = props.inactiveIcon,
        dot = props.dot,
        nextIndex = { nextIndex++ },
    )
    val root = modifier.fillMaxWidth()
        .applyUPResolvedStyle(rememberUPResolvedStyle(props.customStyle, diagnostics, "UPSteps"))
        .upTestTag("steps")
    // `direction` picks the axis the steps advance along, as it does upstream.
    if (direction == "column") {
        Column(root) { CompositionLocalProvider(LocalUPSteps provides context) { content() } }
    } else {
        Row(root) { CompositionLocalProvider(LocalUPSteps provides context) { content() } }
    }
}

@Composable
public fun UPStepsItem(props: UPStepsItemProps = UPStepsItemProps(), modifier: Modifier = Modifier, onClick: (() -> Unit)? = null, diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None) {
    val parent = LocalUPSteps.current
    val index = parent?.nextIndex?.invoke() ?: 0
    val status = stepsItemStatus(index, parent?.current ?: 0, props.error)
    val activeColor = UPColor.parse(parent?.activeColor, UPTheme.Primary)
    val inactiveColor = UPColor.parse(parent?.inactiveColor, UPTheme.Tips)
    val markerColor = when (status) {
        "finish", "process" -> activeColor
        "error" -> UPTheme.Error
        else -> inactiveColor
    }
    val markerSize = upRawDp(props.iconSize, 17.dp).coerceAtLeast(0.dp)
    val vertical = parent?.direction == "column"
    val row = modifier
        .then(if (vertical) Modifier.fillMaxWidth() else Modifier)
        .upClickable(enabled = onClick != null, onClick = { onClick?.invoke() })
        .applyUPResolvedStyle(rememberUPResolvedStyle(props.customStyle, diagnostics, "UPStepsItem"))
        .padding(12.dp)
        .upTestTag("steps-item-$index-$status")

    Row(row, verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        val marker = Modifier.width(markerSize).height(markerSize)
            .applyUPResolvedStyle(rememberUPResolvedStyle(props.itemStyle, diagnostics, "UPStepsItem.itemStyle"))
        val customIcon = if (index <= (parent?.current ?: 0)) parent?.activeIcon else parent?.inactiveIcon
        if (parent?.dot == true) {
            // Dot mode drops the numbered circle entirely and just tints a disc.
            Box(marker.background(markerColor, RoundedCornerShape(50)).upTestTag("steps-item-$index-dot"))
        } else if (!parent?.activeIcon.isNullOrEmpty() || !parent?.inactiveIcon.isNullOrEmpty()) {
            // uview swaps the circle for `activeIcon`/`inactiveIcon` when either is set,
            // picking by `index <= current` rather than by the four-state status.
            Box(marker.upTestTag("steps-item-$index-icon"), contentAlignment = Alignment.Center) {
                UPIcon(
                    UPIconProps(
                        name = customIcon.orEmpty(),
                        size = props.iconSize,
                        color = if (status == "wait") parent.inactiveColor else parent.activeColor,
                    ),
                )
            }
        } else {
            Box(
                marker
                    .background(if (status == "process") markerColor else Color.Transparent, RoundedCornerShape(50))
                    .border(1.dp, markerColor, RoundedCornerShape(50)),
                contentAlignment = Alignment.Center,
            ) {
                val glyph = when (status) {
                    "finish" -> "✓"
                    "error" -> "✕"
                    else -> "${index + 1}"
                }
                BasicText(
                    glyph,
                    style = TextStyle(
                        color = if (status == "process") Color.White else markerColor,
                        fontSize = (markerSize.value * 0.6f).sp,
                    ),
                )
            }
        }
        Column {
            BasicText(props.title.upStringValueOrEmpty(), style = TextStyle(color = if (status == "wait") inactiveColor else UPTheme.Main))
            BasicText(props.desc.upStringValueOrEmpty(), style = TextStyle(color = UPTheme.Content))
        }
    }
}
