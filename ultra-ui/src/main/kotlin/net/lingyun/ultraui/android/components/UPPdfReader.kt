package net.lingyun.ultraui.android.components

import android.annotation.SuppressLint
import android.webkit.WebView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import java.net.URLEncoder
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.upDimension
import net.lingyun.ultraui.android.core.upTestTag

/**
 * `viewerUrl`: `<baseUrl>/static/pdfjs/web/viewer.html?file=<encoded src>`, matching upstream.
 */
internal fun upPdfReaderViewerUrl(baseUrl: String, src: String): String =
    "$baseUrl/static/pdfjs/web/viewer.html?file=" + URLEncoder.encode(src, "UTF-8")

/**
 * Native Compose counterpart of uview-plus `u-pdf-reader`.
 *
 * Upstream renders a `web-view` pointing at the bundled pdf.js viewer; the Android port hosts an
 * `android.webkit.WebView` (via `AndroidView`) loading the same `viewer.html?file=<src>` URL, sized
 * to `height`. `baseUrl` selects the pdf.js asset host.
 *
 * Note: this loads the remote pdf.js viewer and the PDF at `src` over the network, so the host app
 * must hold the `INTERNET` permission. JavaScript is enabled because pdf.js requires it.
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
public fun UPPdfReader(
    props: UPPdfReaderProps = UPPdfReaderProps(),
    modifier: Modifier = Modifier,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    val style = rememberUPResolvedStyle(props.customStyle, diagnostics, "UPPdfReader")
    val url = upPdfReaderViewerUrl(props.baseUrl, props.src)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(upDimension(props.height, 500.dp))
            .applyUPResolvedStyle(style)
            .upTestTag("pdf-reader"),
    ) {
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    settings.javaScriptEnabled = true
                    settings.allowFileAccess = true
                    loadUrl(url)
                }
            },
            update = { it.loadUrl(url) },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
