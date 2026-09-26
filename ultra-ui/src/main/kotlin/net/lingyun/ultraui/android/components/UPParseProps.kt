package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/**
 * Props for [UPParse], mirroring uview-plus `u-parse` (mp-html).
 *
 * [content] is the HTML string to render; [containerStyle] styles the outer container; [domain]
 * prefixes protocol-relative/root-relative image and link URLs; [selectable] enables long-press text
 * selection; [previewImg] opens an enlarged overlay on image tap; [copyLink] copies an external link
 * to the clipboard on tap; [tagStyle] supplies default per-tag inline styles; [lazyLoad] is forwarded
 * to the image host.
 *
 * H5/WeChat-only or host-owned knobs ([errorImg], [loadingImg], [pauseVideo], [setTitle],
 * [showImgMenu], [useAnchor], [scrollTable]) are inert; see the docs downgrade notes.
 */
public data class UPParseProps(
    val containerStyle: String = "",
    val content: String = "",
    val copyLink: Boolean = true,
    val domain: String = "",
    val errorImg: String = "",
    val lazyLoad: Boolean = false,
    val loadingImg: String = "",
    val pauseVideo: Boolean = true,
    val previewImg: Boolean = true,
    val scrollTable: Boolean = false,
    val selectable: Boolean = false,
    val setTitle: Boolean = true,
    val showImgMenu: Boolean = true,
    val tagStyle: Map<String, String> = emptyMap(),
    val useAnchor: Boolean = false,
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
