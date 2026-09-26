package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/**
 * Props for [UPSection], mirroring uview-plus `u-section`'s prop config.
 *
 * Note: this snapshot of uview-plus ships only `u-section/section.js` (the prop defaults) with no
 * `.vue` template, so [UPSection] is a reconstruction from those props (a section header row), not a
 * line-for-line port of an upstream template. The prop names and defaults match `section.js`.
 *
 * [title] is the section title styled by [color]/[fontSize]/[bold]; [showLine] shows a left accent
 * bar in [lineColor] (empty = theme primary); [right] shows a right area with [subTitle] in
 * [subColor] and, when [arrow], a trailing arrow that emits the right-click.
 */
public data class UPSectionProps(
    val title: String = "",
    val subTitle: String = "更多",
    val right: Boolean = true,
    val fontSize: UPRawValue = 15,
    val bold: Boolean = true,
    val color: String = "#303133",
    val subColor: String = "#909399",
    val showLine: Boolean = true,
    val lineColor: String = "",
    val arrow: Boolean = true,
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
