package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/**
 * Props for [UPView], mirroring uview-plus `up-view`.
 *
 * A thin styled container: the CSS-like fields feed straight into the wrapper's style, and a tap
 * emits `click`. All fields default to empty (unset), matching upstream.
 */
public data class UPViewProps(
    val backgroundColor: String = "",
    val color: String = "",
    val flexDirection: String = "",
    val justifyContent: String = "",
    val alignItems: String = "",
    val flex1: String = "",
    val width: UPRawValue = "",
    val height: UPRawValue = "",
    val padding: UPRawValue = "",
    val margin: UPRawValue = "",
    val borderColor: String = "",
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
