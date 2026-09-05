package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.upSafeEnum

/**
 * Pure helpers behind `u-subsection`. The upstream component resolves every colour, weight
 * and radius in `computed`, so mirroring those branches here keeps the composable
 * declarative and lets the JVM tests pin the decision table.
 */

/** `mode` picks between the pill-on-grey button bar and the outlined segmented control. */
internal val UPSubsectionModes: Set<String> = setOf("button", "subsection")

/** `.u-subsection--button__bar` background. */
internal const val UPSubsectionButtonBarColor: String = "#ffffff"

/** `--up-subsection-disabled-bar-color` in light mode. */
internal const val UPSubsectionDisabledBarColor: String = "#f5f5f5"

/** `--up-subsection-disabled-text-color` in light mode. */
internal const val UPSubsectionDisabledTextColor: String = "#c8c9cc"

/** `--up-subsection-disabled-border-color` in light mode. */
internal const val UPSubsectionDisabledBorderColor: String = "#d4d4d4"

/** `mode="subsection"` paints the active label white rather than in `activeColor`. */
internal const val UPSubsectionActiveTextOnBar: String = "#FFFFFF"

/** `.u-subsection--button` is 34px tall, `.u-subsection--subsection` 32px. */
internal fun upSubsectionHeightDp(mode: String): Int = if (mode == "subsection") 32 else 34

/** `.u-subsection--button` insets its bar by `padding: 3px`; the outlined mode does not. */
internal fun upSubsectionWrapperPaddingDp(mode: String): Int = if (mode == "subsection") 0 else 3

/** Only `mode="button"` paints `bgColor` behind the whole control. */
internal fun upSubsectionWrapperColorHex(mode: String, bgColor: String): String? =
    if (mode == "subsection") null else bgColor

internal fun upSubsectionMode(
    value: String,
    diagnostics: UPCompatibilityDiagnostics,
    component: String,
): String = upSafeEnum(value, UPSubsectionModes, "button", diagnostics, component, "mode")

/**
 * `activeColorKeyName` / `inactiveColorKeyName` read a per-item override out of the list
 * entry; upstream only honours it for object entries with a truthy value.
 */
internal fun upSubsectionItemColorOverride(item: UPRawValue, keyName: String): String? {
    if (keyName.isEmpty()) return null
    val map = item.upStringKeyMapOrEmpty()
    if (map.isEmpty()) return null
    return map[keyName].upStringValueOrEmpty().takeIf { it.isNotEmpty() }
}

/** `textStyle(index, item)`: per-item override first, then the mode's own default. */
internal fun upSubsectionTextColorHex(
    mode: String,
    active: Boolean,
    disabled: Boolean,
    activeOverride: String?,
    inactiveOverride: String?,
    activeColor: String,
    inactiveColor: String,
): String {
    if (disabled) return UPSubsectionDisabledTextColor
    return if (active) {
        activeOverride ?: if (mode == "subsection") UPSubsectionActiveTextOnBar else activeColor
    } else {
        inactiveOverride ?: inactiveColor
    }
}

/** `style.fontWeight = bold && innerCurrent === index ? "bold" : "normal"`. */
internal fun upSubsectionBold(bold: Boolean, active: Boolean, disabled: Boolean): Boolean =
    !disabled && bold && active

/** `barStyle.backgroundColor`: the outlined mode rides on `activeColor`, button mode on white. */
internal fun upSubsectionBarColorHex(mode: String, disabled: Boolean, activeColor: String): String = when {
    mode == "subsection" && disabled -> UPSubsectionDisabledBorderColor
    mode == "subsection" -> activeColor
    disabled -> UPSubsectionDisabledBarColor
    else -> UPSubsectionButtonBarColor
}

/** `itemStyle(index).borderColor`; only the outlined mode draws item borders at all. */
internal fun upSubsectionItemBorderColorHex(mode: String, disabled: Boolean, activeColor: String): String? = when {
    mode != "subsection" -> null
    disabled -> UPSubsectionDisabledBorderColor
    else -> activeColor
}

/**
 * `u-subsection__bar--first|center|last` only round the outer edge the bar currently sits
 * against; the button-mode bar is uniformly rounded instead.
 */
internal fun upSubsectionBarPosition(index: Int, count: Int): String = when {
    count <= 1 -> "single"
    index <= 0 -> "first"
    index >= count - 1 -> "last"
    else -> "center"
}

/** `translateX(innerCurrent * itemWidth)` clamped to the rendered item count. */
internal fun upSubsectionBarIndex(current: Int, count: Int): Int =
    if (count <= 0) 0 else current.coerceIn(0, count - 1)

/** `transition-duration: 0.3s` on the bar's transform. */
internal const val UPSubsectionBarDurationMillis: Int = 300
