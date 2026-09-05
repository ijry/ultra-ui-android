package net.lingyun.ultraui.android.components

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput
import net.lingyun.ultraui.android.core.asFiniteFloatOrNull
import net.lingyun.ultraui.android.core.upIntOrDefault
import net.lingyun.ultraui.android.core.upSafeEnum
import net.lingyun.ultraui.android.core.upStringOrDefault
import kotlin.math.ceil

/**
 * Pure helpers behind `u-tabs`, `u-pagination` and `u-image`. Each function mirrors one
 * upstream computed property or SCSS rule so the composables stay declarative and the
 * behaviour stays unit-testable on the JVM.
 */

/** `shapeMode` values `u-tabs` turns into a `u-tabs--shape-*` class. */
internal val UPTabsShapeModes: Set<String> = setOf("capsule", "card", "pill-arrow", "tag")

/** Shape modes whose own active background replaces the sliding line. */
private val UPTabsLinelessShapeModes: Set<String> = setOf("capsule", "pill-arrow", "tag")

/** `background-size` keywords `u-tabs` forwards to the line element. */
internal val UPTabsLineBgSizes: Set<String> = setOf("cover", "contain", "auto")

/** uview's default `activeStyle.color`; a different value means the caller overrode it. */
internal const val UPTabsDefaultActiveColor: String = "#303133"

/** uview's default `inactiveStyle.color`, used for the same "was it overridden" check. */
internal const val UPTabsDefaultInactiveColor: String = "#606266"

/**
 * `shapeMode` is optional upstream (`shapeModeClass` stays empty when it is blank), so a
 * blank value must not be reported as an unsupported enum.
 */
internal fun upTabsShapeMode(
    value: String,
    diagnostics: UPCompatibilityDiagnostics,
    component: String,
): String {
    if (value.isBlank()) return ""
    return upSafeEnum(value, UPTabsShapeModes, "", diagnostics, component, "shapeMode")
        .takeIf { it.isNotEmpty() } ?: ""
}

/**
 * `showLine = lineShow && !['capsule','pill-arrow','tag'].includes(shapeMode)`. The card
 * mode keeps its slider, every other decorated mode paints the active item instead.
 */
internal fun upTabsShowLine(shapeMode: String): Boolean = shapeMode !in UPTabsLinelessShapeModes

/**
 * `itemComputedStyle` only merges a shape-specific height when the caller did not pass
 * `itemStyle`; Compose has no `hasProp`, so a non-default value stands in for it.
 */
internal fun upTabsItemStyleDeclared(itemStyle: UPStyleInput): Boolean = when (itemStyle) {
    null -> false
    is String -> itemStyle.isNotBlank()
    is Map<*, *> -> itemStyle.isNotEmpty() && itemStyle != mapOf("height" to "44px")
    is Collection<*> -> itemStyle.isNotEmpty()
    else -> true
}

/** Heights `itemComputedStyle` deep-merges per shape mode; `null` keeps the caller's style. */
internal fun upTabsShapeItemHeight(shapeMode: String): Dp? = when (shapeMode) {
    "capsule" -> 30.dp
    "card" -> 34.dp
    "pill-arrow" -> 32.dp
    "tag" -> 28.dp
    else -> null
}

/** Horizontal item padding from the shape SCSS; the plain nav keeps `padding: 0 11px`. */
internal fun upTabsShapeItemPadding(shapeMode: String): Dp = when (shapeMode) {
    "capsule" -> 14.dp
    "card" -> 0.dp
    "pill-arrow" -> 12.dp
    "tag" -> 14.dp
    else -> 11.dp
}

/** Item corner radius per shape mode; `card` rounds only its top corners. */
internal fun upTabsShapeItemRadius(shapeMode: String): Dp = when (shapeMode) {
    "capsule" -> 999.dp
    "card" -> 10.dp
    "tag" -> 999.dp
    "pill-arrow" -> 8.dp
    else -> 0.dp
}

/** Gap the `pill-arrow`/`tag` items keep through `margin-right: 8px`. */
internal fun upTabsShapeItemSpacing(shapeMode: String): Dp =
    if (shapeMode == "pill-arrow" || shapeMode == "tag") 8.dp else 0.dp

/** `__scroll-view-wrapper` padding; only `capsule` insets its pills. */
internal fun upTabsShapeWrapperPadding(shapeMode: String): Dp = if (shapeMode == "capsule") 3.dp else 0.dp

/** `__nav` vertical padding as `top to bottom`, which is where the arrow modes reserve room. */
internal fun upTabsShapeNavPadding(shapeMode: String): Pair<Dp, Dp> = when (shapeMode) {
    "pill-arrow" -> 0.dp to 6.dp
    "tag" -> 2.dp to 2.dp
    else -> 0.dp to 0.dp
}

/** Wrapper background painted by the shape SCSS, or `null` for the plain nav. */
internal fun upTabsShapeWrapperColor(shapeMode: String): String? = when (shapeMode) {
    "capsule" -> "#edf0f5"
    "card" -> "#9ccde5"
    else -> null
}

/** Wrapper corner radius that pairs with [upTabsShapeWrapperColor]. */
internal fun upTabsShapeWrapperRadius(shapeMode: String): Dp = when (shapeMode) {
    "capsule" -> 999.dp
    "card" -> 10.dp
    else -> 0.dp
}

/** Inactive item background from the shape SCSS, or `null` when the item stays transparent. */
internal fun upTabsShapeItemColor(shapeMode: String): String? = when (shapeMode) {
    "pill-arrow" -> "#e8e8e8"
    "tag" -> "#f3f4f6"
    else -> null
}

/**
 * Active item background per shape mode. `pill-arrow` uses a `90deg` gradient upstream,
 * which is expressed here as its two stops because `UPColor` only parses `to right`.
 */
internal fun upTabsShapeActiveColors(shapeMode: String): List<String> = when (shapeMode) {
    "capsule" -> listOf("#ffffff")
    "card" -> listOf("#f6f8fb")
    "pill-arrow" -> listOf("#ff6c57", "#ff3b30")
    "tag" -> listOf("#2a6bf6")
    else -> emptyList()
}

/**
 * Mirrors `textStyle(index)`: the disabled color wins outright, then the caller's
 * `activeStyle`/`inactiveStyle` color, and finally the per-shape default.
 */
internal fun upTabsTextColorHex(
    shapeMode: String,
    isActive: Boolean,
    disabled: Boolean,
    activeStyle: UPStyleInput,
    inactiveStyle: UPStyleInput,
): String {
    if (disabled) return "#c8c9cc"
    val style = if (isActive) activeStyle else inactiveStyle
    val custom = upStyleColorOrNull(style)
    val defaultColor = if (isActive) UPTabsDefaultActiveColor else UPTabsDefaultInactiveColor
    if (custom != null && !custom.equals(defaultColor, ignoreCase = true)) return custom
    if (isActive) return if (shapeMode == "pill-arrow" || shapeMode == "tag") "#ffffff" else UPTabsDefaultActiveColor
    return UPTabsDefaultInactiveColor
}

/** Reads a non-blank `color` declaration out of a map or declaration-string style. */
internal fun upStyleColorOrNull(style: UPStyleInput): String? = when (style) {
    is Map<*, *> -> style.entries
        .firstOrNull { (key, _) -> (key as? String)?.trim()?.lowercase() == "color" }
        ?.value
        .upStringOrDefault()
        .trim()
        .takeIf { it.isNotEmpty() }
    is String -> style.split(';')
        .map(String::trim)
        .firstOrNull { it.substringBefore(':').trim().lowercase() == "color" }
        ?.substringAfter(':')
        ?.trim()
        ?.takeIf { it.isNotEmpty() }
    else -> null
}

/**
 * `setLineLeft` centers the line inside the active item after summing the widths of the
 * items before it, so the offset only depends on measured widths and the line width.
 */
internal fun upTabsLineOffset(itemWidths: List<Float>, index: Int, lineWidth: Float): Float {
    val safeIndex = index.coerceIn(0, (itemWidths.size - 1).coerceAtLeast(0))
    val activeWidth = itemWidths.getOrElse(safeIndex) { 0f }
    // uview only calls `setLineLeft` once `getRect` reported a width, so an unmeasured nav
    // keeps the line at the start instead of centering it around a zero-width item.
    if (activeWidth <= 0f) return 0f
    val leading = itemWidths.take(safeIndex).sum()
    return leading + (activeWidth - lineWidth) / 2f
}

/**
 * Maps `item.badge` onto `UPBadgeProps` with uview's `item.badge.X || propsBadge.X` fallback
 * chain, including the `margin-left: 4px` the template hardcodes.
 */
internal fun upTabsItemBadgeProps(badge: Map<String, UPRawValue>): UPBadgeProps {
    val defaults = UPBadgeProps()
    fun text(key: String, fallback: String): String =
        badge[key].upStringOrDefault().takeIf { it.isNotEmpty() } ?: fallback
    return UPBadgeProps(
        isDot = badge["isDot"].upBooleanValue(false) || defaults.isDot,
        value = badge["value"].upStringOrDefault().takeIf { it.isNotEmpty() } ?: defaults.value,
        show = true,
        max = badge["max"].asFiniteFloatOrNull()?.toInt() ?: defaults.max,
        type = text("type", defaults.type),
        showZero = badge["showZero"].upBooleanValue(false) || defaults.showZero,
        bgColor = text("bgColor", defaults.bgColor.orEmpty()).takeIf { it.isNotEmpty() },
        color = text("color", defaults.color.orEmpty()).takeIf { it.isNotEmpty() },
        shape = text("shape", defaults.shape),
        numberType = text("numberType", defaults.numberType),
        inverted = badge["inverted"].upBooleanValue(false) || defaults.inverted,
        customStyle = mapOf("marginLeft" to "4px"),
    )
}

/** `badgeShow`: an item badge renders when it asks to, carries a value, or is a dot. */
internal fun upTabsBadgeVisible(badge: Map<String, UPRawValue>): Boolean {
    if (badge.isEmpty()) return false
    val show = badge["show"].upBooleanValue(false)
    val isDot = badge["isDot"].upBooleanValue(false)
    val value = badge["value"].upStringOrDefault().trim()
    return show || isDot || (value.isNotEmpty() && value != "0" && value != "false")
}

/** `Math.max(1, Math.ceil(total / pageSize))`. */
internal fun upPaginationTotalPages(total: Int, pageSize: Int): Int {
    if (pageSize <= 0) return 1
    return ceil(total.toDouble() / pageSize.toDouble()).toInt().coerceAtLeast(1)
}

/**
 * `displayedPages` keeps the first and last page pinned and collapses the rest into the
 * `'...'` markers, exactly like upstream's four-branch computed property.
 */
internal fun upPaginationDisplayedPages(totalPages: Int, currentPage: Int): List<UPRawValue> {
    val pages = totalPages.coerceAtLeast(1)
    if (pages <= 4) return (1..pages).toList()
    val current = currentPage.coerceIn(1, pages)
    return when {
        current <= 2 -> listOf(1, 2, 3, 4, "...", pages)
        current >= pages - 1 -> listOf(1, "...", pages - 3, pages - 2, pages - 1, pages)
        else -> listOf(1, "...", current - 1, current, current + 1, "...", pages)
    }
}

/** `normalizedPageSizes` accepts numbers, numeric strings and `{ label, value }` objects. */
internal fun upNormalizedPageSizes(pageSizes: List<UPRawValue>): List<Pair<String, Int>> =
    pageSizes.mapNotNull { raw ->
        when (raw) {
            null -> null
            is Map<*, *> -> {
                val map = raw.upStringKeyMapOrEmpty()
                val value = map["value"].asFiniteFloatOrNull()?.toInt() ?: return@mapNotNull null
                val label = map["label"].upStringOrDefault().takeIf { it.isNotEmpty() } ?: "${value}\u6761/\u9875"
                label to value
            }
            else -> {
                val value = raw.asFiniteFloatOrNull()?.toInt() ?: return@mapNotNull null
                "${value}\u6761/\u9875" to value
            }
        }
    }

/** `pageSizeIndex` falls back to the first entry when `pageSize` is not one of the options. */
internal fun upPageSizeIndex(normalized: List<Pair<String, Int>>, pageSize: Int): Int =
    normalized.indexOfFirst { it.second == pageSize }.takeIf { it >= 0 } ?: 0

/** `pageSizeLabel` shows the matching option label, or the raw `pageSize` when there is none. */
internal fun upPageSizeLabel(normalized: List<Pair<String, Int>>, pageSize: UPRawValue): String =
    normalized.firstOrNull { it.second == pageSize.upIntOrDefault(Int.MIN_VALUE) }?.first
        ?: pageSize.upStringOrDefault()

/**
 * `handleSizeChange` reads the picked option, falls back to the first one, and drops
 * falsy sizes without emitting anything.
 */
internal fun upPageSizeAt(normalized: List<Pair<String, Int>>, index: Int): Int? {
    val picked = normalized.getOrNull(index)?.second ?: normalized.firstOrNull()?.second
    return picked?.takeIf { it != 0 }
}

/** `layout.includes(part)` over the comma separated declaration. */
internal fun upPaginationLayoutParts(layout: String): Set<String> =
    layout.split(',').map { it.trim().lowercase() }.filter { it.isNotEmpty() }.toSet()

/**
 * `u-image` wraps itself in `<u-transition mode="fade" :duration="fade ? 1000 : 0">`, but its
 * own `duration` prop is documented as the fade length and only stays unused because the
 * upstream `wrapStyle` block is commented out. Android honors the documented prop.
 */
internal fun upImageFadeDuration(fade: Boolean, duration: UPRawValue): Int =
    if (!fade) 0 else duration.upIntOrDefault(500).coerceAtLeast(0)
