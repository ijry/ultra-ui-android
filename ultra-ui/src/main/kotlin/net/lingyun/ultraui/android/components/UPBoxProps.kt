package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/**
 * Props for [UPBox], mirroring uview-plus `up-box`.
 *
 * A home-screen feature block: one tall left panel beside a right column of two equal-height
 * panels, each tinted by `bgColors[0..2]`. `height`, `borderRadius` and `gap` are CSS lengths;
 * the per-region `*Icon`/`*Title` feed the default content when a region slot is absent.
 */
public data class UPBoxProps(
    val bgColors: List<String> = listOf("#EEFCFF", "#FCF8FF", "#FDF8F2"),
    val height: UPRawValue = "160px",
    val borderRadius: UPRawValue = "6px",
    val gap: UPRawValue = "15px",
    val leftIcon: String = "",
    val leftTitle: String = "左",
    val rightTopIcon: String = "",
    val rightTopTitle: String = "右上",
    val rightBottomIcon: String = "",
    val rightBottomTitle: String = "右下",
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
