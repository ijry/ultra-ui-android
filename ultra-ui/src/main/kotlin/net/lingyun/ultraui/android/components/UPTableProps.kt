package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/**
 * Props for [UPTable], mirroring uview-plus `u-table`.
 *
 * The table shares its cell styling with the `u-td`/`u-th` children through a composition local:
 * `borderColor`, `align`, `padding`, `fontSize`, `color` and the header-only `thStyle`. `bgColor`
 * fills the table background.
 */
public data class UPTableProps(
    val borderColor: String = "#e4e7ed",
    val align: String = "center",
    val padding: String = "5px 3px",
    val fontSize: String = "14px",
    val color: String = "#606266",
    val thStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
    val bgColor: String = "#ffffff",
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)

/** Props for [UPTd], mirroring `u-td`. `width` fixes the cell width (else it flexes). */
public data class UPTdProps(
    val width: String = "auto",
    val textAlign: String = "",
    val fontSize: String = "",
    val borderColor: String = "",
    val color: String = "",
)

/** Props for [UPTh], mirroring `u-th`. `width` fixes the header cell width (else it flexes). */
public data class UPThProps(
    val width: String = "",
)
