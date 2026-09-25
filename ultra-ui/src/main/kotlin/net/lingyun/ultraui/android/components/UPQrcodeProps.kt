package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/**
 * Props for [UPQrcode], mirroring uview-plus `u-qrcode`.
 *
 * The core is [`val`] (the payload) rendered as a QR matrix by [upQrcodeMatrix] at error-correction
 * level [lv] (0=L, 1=M, 2=Q, 3=H), sized `size` in `unit`, coloured by [foreground]/[background] with
 * the three finder-pattern centres tinted [pdground]. [icon]/[iconSize] overlay a centre image and
 * [allowPreview] enables an enlarged tap-to-preview overlay.
 *
 * H5/App canvas-only knobs ([cid], [onval], [loadMake], [usingComponents], [showLoading],
 * [loadingText]) have no Compose equivalent and are inert; see the docs downgrade notes.
 */
public data class UPQrcodeProps(
    val cid: String = "",
    val size: Int = 200,
    val unit: String = "px",
    val show: Boolean = true,
    val `val`: String = "",
    val background: String = "#ffffff",
    val foreground: String = "#000000",
    val pdground: String = "#000000",
    val icon: String = "",
    val iconSize: Int = 40,
    val lv: Int = 3,
    val quietZone: Int = 0,
    val onval: Boolean = true,
    val loadMake: Boolean = true,
    val usingComponents: Boolean = true,
    val showLoading: Boolean = true,
    val loadingText: String = "生成中",
    val allowPreview: Boolean = false,
    val useRootHeightAndWidth: Boolean = false,
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
