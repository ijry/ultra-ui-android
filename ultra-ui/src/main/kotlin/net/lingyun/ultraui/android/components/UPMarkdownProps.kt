package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/**
 * Props for [UPMarkdown], mirroring uview-plus `u-markdown`.
 *
 * [content] is the Markdown source. It is converted to HTML and rendered by [UPParse], so
 * [previewImg]/[copyLink]/[domain] are forwarded there. [showLineNumber] prefixes fenced code lines
 * with line numbers and [theme] (`light`/`dark`) switches the text/background colour scheme.
 */
public data class UPMarkdownProps(
    val content: String = "",
    val previewImg: Boolean = true,
    val copyLink: Boolean = true,
    val domain: String = "",
    val showLineNumber: Boolean = false,
    val theme: String = "light",
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
