package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/**
 * Props for [UPPdfReader], mirroring uview-plus `u-pdf-reader`.
 *
 * A PDF viewer backed by the bundled pdf.js web viewer. `src` is the PDF URL, `height` the
 * component height, and `baseUrl` the pdf.js asset host.
 */
public data class UPPdfReaderProps(
    val src: String = "",
    val height: UPRawValue = "500px",
    val baseUrl: String = "https://uview-plus.jiangruyi.com/h5",
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
