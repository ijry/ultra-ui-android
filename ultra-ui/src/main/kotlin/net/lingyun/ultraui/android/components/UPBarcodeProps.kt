package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/**
 * Props for [UPBarcode], mirroring uview-plus `u-barcode`.
 *
 * [value] is encoded by [upBarcodeEncode] in [format] (default `auto` = CODE128) and painted as a
 * bar/space grid `width` x `height` px with [lineColor] on [background]. [displayValue] toggles the
 * human-readable [text] (defaults to [value]) placed per [textPosition]/[textAlign] with [fontSize]/
 * [textMargin]/[font]. [margin] sets all sides; [marginTop]/[marginBottom]/[marginLeft]/[marginRight]
 * override individually (null = fall back to [margin]).
 *
 * `fontOptions` (upstream stores but never applies to the canvas) and `useCanvas` (Android always
 * paints to a Compose canvas; the image-file branch needs host export) are inert; see docs.
 */
public data class UPBarcodeProps(
    val value: String = "",
    val format: String = "auto",
    val width: Int = 200,
    val height: Int = 80,
    val displayValue: Boolean = true,
    val text: String? = null,
    val fontOptions: String = "",
    val font: String = "monospace",
    val textAlign: String = "center",
    val textPosition: String = "bottom",
    val textMargin: Int = 2,
    val fontSize: Int = 14,
    val background: String = "#ffffff",
    val lineColor: String = "#000000",
    val margin: Int = 10,
    val marginTop: Int? = null,
    val marginBottom: Int? = null,
    val marginLeft: Int? = null,
    val marginRight: Int? = null,
    val useCanvas: Boolean = true,
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
