package net.lingyun.ultraui.android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.lingyun.ultraui.android.core.UPColor
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.core.upTestTag

private val BLOCK_TAGS = setOf(
    "p", "div", "section", "article", "header", "footer", "nav", "aside", "main", "figure",
    "figcaption", "address", "center", "ul", "ol", "li", "blockquote", "pre", "hr", "dl", "dt", "dd",
    "h1", "h2", "h3", "h4", "h5", "h6", "table", "thead", "tbody", "tfoot", "tr", "td", "th", "img",
)

private val HEADING_SCALE = mapOf(
    "h1" to 2.0f, "h2" to 1.5f, "h3" to 1.17f, "h4" to 1.0f, "h5" to 0.83f, "h6" to 0.67f,
)

// Upstream default per-tag styles (mp-html config.tagStyle) relevant to inline rendering.
private val DEFAULT_TAG_STYLE = mapOf(
    "s" to "text-decoration:line-through",
    "strike" to "text-decoration:line-through",
    "u" to "text-decoration:underline",
    "mark" to "background-color:yellow",
    "small" to "font-size:0.8em",
    "big" to "font-size:1.2em",
    "cite" to "font-style:italic",
    "address" to "font-style:italic",
)

/** Resolves an HTML [url] against [domain] the way mp-html does for relative links/images. */
internal fun upParseResolveUrl(domain: String, url: String): String {
    val u = url.trim()
    if (u.isEmpty()) return u
    val lower = u.lowercase()
    if (lower.startsWith("http://") || lower.startsWith("https://") ||
        lower.startsWith("data:") || lower.startsWith("file://")
    ) {
        return u
    }
    if (u.startsWith("//")) return "https:$u"
    val d = domain.trim().trimEnd('/')
    if (d.isEmpty()) return u
    return if (u.startsWith("/")) "$d$u" else "$d/$u"
}

/** Flattens a node list to its concatenated visible text (used for tests and plain fallbacks). */
internal fun upParseInlineText(nodes: List<UPParseNode>): String {
    val sb = StringBuilder()
    fun walk(list: List<UPParseNode>) {
        for (node in list) when (node) {
            is UPParseText -> sb.append(node.text)
            is UPParseElement -> if (node.name == "br") sb.append('\n') else walk(node.children)
        }
    }
    walk(nodes)
    return sb.toString()
}

/** Parses a CSS declaration string ("a:b;c:d") into a lowercase-keyed map. */
internal fun upParseStyleMap(style: String): Map<String, String> {
    if (style.isBlank()) return emptyMap()
    val out = LinkedHashMap<String, String>()
    for (decl in style.split(';')) {
        val idx = decl.indexOf(':')
        if (idx <= 0) continue
        val key = decl.substring(0, idx).trim().lowercase()
        val value = decl.substring(idx + 1).trim()
        if (key.isNotEmpty() && value.isNotEmpty()) out[key] = value
    }
    return out
}

/** Computes the [SpanStyle] for an inline element given the merged [tagStyle] table, or null. */
internal fun upParseInlineSpan(el: UPParseElement, tagStyle: Map<String, String>): SpanStyle? {
    var weight: FontWeight? = null
    var style: FontStyle? = null
    var decoration: TextDecoration? = null
    var color: Color? = null
    var background: Color? = null
    var baseline: BaselineShift? = null
    var fontFamily: FontFamily? = null

    when (el.name) {
        "b", "strong" -> weight = FontWeight.Bold
        "i", "em" -> style = FontStyle.Italic
        "u", "ins" -> decoration = TextDecoration.Underline
        "s", "del", "strike" -> decoration = TextDecoration.LineThrough
        "code" -> { fontFamily = FontFamily.Monospace; background = Color(0xFFF2F2F2) }
        "sub" -> baseline = BaselineShift.Subscript
        "sup" -> baseline = BaselineShift.Superscript
        "a" -> { color = UPTheme.Primary; decoration = TextDecoration.Underline }
    }

    val merged = LinkedHashMap<String, String>()
    tagStyle[el.name]?.let { merged.putAll(upParseStyleMap(it)) }
    merged.putAll(upParseStyleMap(el.attrs["style"] ?: ""))
    when (merged["font-weight"]) {
        "bold", "bolder", "700", "800", "900" -> weight = FontWeight.Bold
    }
    when (merged["font-style"]) { "italic", "oblique" -> style = FontStyle.Italic }
    when (merged["text-decoration"]) {
        "underline" -> decoration = TextDecoration.Underline
        "line-through" -> decoration = TextDecoration.LineThrough
    }
    merged["color"]?.let { color = UPColor.parseOrNull(it) ?: color }
    merged["background-color"]?.let { background = UPColor.parseOrNull(it) ?: background }

    if (weight == null && style == null && decoration == null && color == null &&
        background == null && baseline == null && fontFamily == null
    ) {
        return null
    }
    return SpanStyle(
        color = color ?: Color.Unspecified,
        fontWeight = weight,
        fontStyle = style,
        textDecoration = decoration,
        background = background ?: Color.Unspecified,
        baselineShift = baseline,
        fontFamily = fontFamily,
    )
}

/** Builds an [AnnotatedString] for a run of inline nodes, tagging link spans with a "URL" annotation. */
internal fun upParseBuildParagraph(nodes: List<UPParseNode>, tagStyle: Map<String, String>): AnnotatedString =
    buildAnnotatedString {
        fun appendNode(node: UPParseNode) {
            when (node) {
                is UPParseText -> append(node.text)
                is UPParseElement -> {
                    if (node.name == "br") {
                        append("\n")
                        return
                    }
                    val span = upParseInlineSpan(node, tagStyle)
                    val href = if (node.name == "a") node.attrs["href"] else null
                    if (href != null) pushStringAnnotation("URL", href)
                    if (span != null) withStyle(span) { node.children.forEach { appendNode(it) } }
                    else node.children.forEach { appendNode(it) }
                    if (href != null) pop()
                }
            }
        }
        nodes.forEach { appendNode(it) }
    }

private class UPParseContext(
    val props: UPParseProps,
    val tagStyle: Map<String, String>,
    val lazyLoad: Boolean,
    val onLinkTap: (String) -> Unit,
    val onImgTap: (String) -> Unit,
)

@Composable
private fun UPParseBlocks(nodes: List<UPParseNode>, ctx: UPParseContext) {
    val flushed = ArrayList<@Composable () -> Unit>()
    val inlineBuffer = ArrayList<UPParseNode>()

    fun flushInline() {
        if (inlineBuffer.isEmpty()) return
        val snapshot = inlineBuffer.toList()
        inlineBuffer.clear()
        if (upParseInlineText(snapshot).isBlank()) return
        flushed.add { UPParseParagraph(snapshot, ctx, TextStyle(color = UPTheme.Content, fontSize = 14.sp)) }
    }

    for (node in nodes) {
        if (node is UPParseElement && node.name in BLOCK_TAGS) {
            flushInline()
            flushed.add { UPParseBlockElement(node, ctx) }
        } else {
            inlineBuffer.add(node)
        }
    }
    flushInline()

    Column(modifier = Modifier.fillMaxWidth()) {
        for (block in flushed) block()
    }
}

@Composable
private fun UPParseBlockElement(el: UPParseElement, ctx: UPParseContext) {
    when (el.name) {
        "hr" -> Box(Modifier.fillMaxWidth().padding(vertical = 6.dp).height(1.dp).background(UPTheme.Border))
        "img" -> UPParseImage(el, ctx)
        "br" -> {}
        "h1", "h2", "h3", "h4", "h5", "h6" -> {
            val scale = HEADING_SCALE[el.name] ?: 1f
            UPParseParagraph(
                el.children, ctx,
                TextStyle(color = UPTheme.Main, fontSize = (14f * scale).sp, fontWeight = FontWeight.Bold),
                Modifier.padding(vertical = 4.dp),
            )
        }
        "blockquote" -> Box(
            Modifier.fillMaxWidth().padding(vertical = 4.dp)
                .background(Color(0xFFF7F7F7))
                .padding(start = 10.dp, top = 6.dp, bottom = 6.dp, end = 6.dp),
        ) { UPParseBlocks(el.children, ctx) }
        "pre" -> UPParseParagraph(
            el.children, ctx,
            TextStyle(color = UPTheme.Content, fontSize = 13.sp, fontFamily = FontFamily.Monospace),
            Modifier.fillMaxWidth().padding(vertical = 4.dp).background(Color(0xFFF2F2F2)).padding(8.dp),
        )
        "ul", "ol" -> {
            var index = 0
            Column(Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
                for (child in el.children) {
                    if (child is UPParseElement && child.name == "li") {
                        index++
                        val marker = if (el.name == "ol") "$index. " else "\u2022 "
                        Row(Modifier.fillMaxWidth().padding(vertical = 1.dp)) {
                            UPParseParagraph(
                                listOf(UPParseText(marker)), ctx,
                                TextStyle(color = UPTheme.Content, fontSize = 14.sp),
                            )
                            Box(Modifier.weight(1f)) { UPParseBlocks(child.children, ctx) }
                        }
                    }
                }
            }
        }
        "table", "thead", "tbody", "tfoot" -> Column(Modifier.fillMaxWidth()) { UPParseTableRows(el, ctx) }
        "tr" -> Row(Modifier.fillMaxWidth()) {
            for (cell in el.children) if (cell is UPParseElement && (cell.name == "td" || cell.name == "th")) {
                Box(Modifier.weight(1f).padding(4.dp)) { UPParseBlocks(cell.children, ctx) }
            }
        }
        "td", "th" -> Box(Modifier.padding(4.dp)) { UPParseBlocks(el.children, ctx) }
        else -> UPParseBlocks(el.children, ctx) // p, div, section, ... containers
    }
}

@Composable
private fun UPParseTableRows(el: UPParseElement, ctx: UPParseContext) {
    for (child in el.children) {
        if (child is UPParseElement) when (child.name) {
            "tr" -> UPParseBlockElement(child, ctx)
            "thead", "tbody", "tfoot" -> UPParseTableRows(child, ctx)
            else -> {}
        }
    }
}

@Composable
private fun UPParseImage(el: UPParseElement, ctx: UPParseContext) {
    val src = upParseResolveUrl(ctx.props.domain, el.attrs["src"] ?: "")
    if (src.isEmpty()) return
    Box(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .pointerInput(src, ctx.props.previewImg) { detectTapGestures { ctx.onImgTap(src) } },
    ) {
        UPImage(props = UPImageProps(src = src, width = "100%", mode = "widthFix", lazyLoad = ctx.lazyLoad))
    }
}

@Composable
private fun UPParseParagraph(
    nodes: List<UPParseNode>,
    ctx: UPParseContext,
    baseStyle: TextStyle,
    modifier: Modifier = Modifier,
) {
    val annotated = remember(nodes, ctx.tagStyle) { upParseBuildParagraph(nodes, ctx.tagStyle) }
    var layout by remember { mutableStateOf<TextLayoutResult?>(null) }
    BasicText(
        text = annotated,
        style = baseStyle,
        onTextLayout = { layout = it },
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(annotated) {
                detectTapGestures { pos ->
                    val lr = layout ?: return@detectTapGestures
                    val offset = lr.getOffsetForPosition(pos)
                    annotated.getStringAnnotations("URL", offset, offset).firstOrNull()?.let { ctx.onLinkTap(it.item) }
                }
            }
            .upTestTag("parse-text"),
    )
}

/**
 * Native Compose counterpart of uview-plus `u-parse` (mp-html).
 *
 * Parses [`UPParseProps.content`] with [upParseHtml] and renders a bounded but faithful subset of
 * HTML: paragraphs and headings, inline emphasis (b/i/u/s/code/mark/sub/sup + inline `style` and the
 * `tagStyle` table), links (tap fires [onLinkTap] and, when `copyLink`, copies to the clipboard),
 * images (tap fires [onImgTap] and, when `previewImg`, opens an enlarged overlay), lists,
 * blockquotes, `pre`, `hr` and simple tables. `domain` resolves relative URLs; `selectable` wraps
 * the output in a [SelectionContainer].
 *
 * Downgrades (see docs): `errorImg`/`loadingImg` placeholder URLs and `lazyLoad` windowing are the
 * host image loader's concern; `pauseVideo`, `setTitle`, `showImgMenu`, `useAnchor` and
 * `scrollTable` have no Compose/in-tree equivalent and are inert.
 */
@Composable
public fun UPParse(
    props: UPParseProps = UPParseProps(),
    modifier: Modifier = Modifier,
    onLinkTap: ((String) -> Unit)? = null,
    onImgTap: ((String) -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    val style = rememberUPResolvedStyle(props.customStyle, diagnostics, "UPParse")
    val containerStyle = rememberUPResolvedStyle(
        remember(props.containerStyle) { upParseStyleMap(props.containerStyle).mapValues { it.value as UPRawValue } },
        diagnostics,
        "UPParseContainer",
    )
    val nodes = remember(props.content) { upParseHtml(props.content) }
    val clipboard = LocalClipboardManager.current
    var previewSrc by remember { mutableStateOf<String?>(null) }
    val tagStyle = remember(props.tagStyle) { DEFAULT_TAG_STYLE + props.tagStyle }

    val ctx = remember(props, tagStyle, onLinkTap, onImgTap) {
        UPParseContext(
            props = props,
            tagStyle = tagStyle,
            lazyLoad = props.lazyLoad,
            onLinkTap = { href ->
                val resolved = upParseResolveUrl(props.domain, href)
                if (props.copyLink) clipboard.setText(AnnotatedString(resolved))
                onLinkTap?.invoke(resolved)
            },
            onImgTap = { src ->
                if (props.previewImg) previewSrc = src
                onImgTap?.invoke(src)
            },
        )
    }

    val body: @Composable () -> Unit = {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .applyUPResolvedStyle(containerStyle)
                .applyUPResolvedStyle(style)
                .upTestTag("parse"),
        ) {
            UPParseBlocks(nodes, ctx)
        }
    }
    if (props.selectable) SelectionContainer(content = { body() }) else body()

    val preview = previewSrc
    if (preview != null) {
        UPOverlay(
            props = UPOverlayProps(show = true),
            onClick = { previewSrc = null },
            content = {
                Box(Modifier.fillMaxSize().upTestTag("parse-preview"), contentAlignment = Alignment.Center) {
                    UPImage(props = UPImageProps(src = preview, width = "300px", mode = "widthFix"))
                }
            },
        )
    }
}
