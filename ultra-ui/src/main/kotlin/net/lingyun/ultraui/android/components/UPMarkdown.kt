package net.lingyun.ultraui.android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.core.upTestTag

/**
 * Bounded Markdown-to-HTML converter for uview-plus `u-markdown`. This is not a port of the full
 * `marked` engine; it covers the common block and inline constructs (headings, emphasis, inline and
 * fenced code, links, images, lists, blockquotes, rules, paragraphs) and emits HTML that [UPParse]
 * renders. When [showLineNumber] is set, fenced code lines are prefixed with their line number.
 */
internal object UPMarkdown {
    private fun escapeHtml(text: String): String =
        text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")

    private fun inline(textRaw: String): String {
        var t = escapeHtml(textRaw)
        t = Regex("`([^`]+)`").replace(t) { "<code>${it.groupValues[1]}</code>" }
        t = Regex("!\\[([^\\]]*)]\\(([^)\\s]+)\\)").replace(t) { "<img src=\"${it.groupValues[2]}\" alt=\"${it.groupValues[1]}\">" }
        t = Regex("\\[([^\\]]+)]\\(([^)\\s]+)\\)").replace(t) { "<a href=\"${it.groupValues[2]}\">${it.groupValues[1]}</a>" }
        t = Regex("\\*\\*([^*]+)\\*\\*").replace(t) { "<strong>${it.groupValues[1]}</strong>" }
        t = Regex("__([^_]+)__").replace(t) { "<strong>${it.groupValues[1]}</strong>" }
        t = Regex("\\*([^*]+)\\*").replace(t) { "<em>${it.groupValues[1]}</em>" }
        t = Regex("(?<![A-Za-z0-9])_([^_]+)_(?![A-Za-z0-9])").replace(t) { "<em>${it.groupValues[1]}</em>" }
        return t
    }

    private val HR = Regex("^ {0,3}([-*_])( *\\1){2,} *$")
    private val HEADING = Regex("^ {0,3}(#{1,6})\\s+(.*)$")
    private val UL_ITEM = Regex("^ {0,3}[-*+]\\s+(.*)$")
    private val OL_ITEM = Regex("^ {0,3}\\d+\\.\\s+(.*)$")
    private val FENCE = Regex("^ {0,3}```\\s*([A-Za-z0-9]*)\\s*$")
    private val QUOTE = Regex("^ {0,3}>\\s?(.*)$")

    /** Converts Markdown [md] to an HTML string; [showLineNumber] numbers fenced code lines. */
    fun toHtml(md: String, showLineNumber: Boolean): String {
        if (md.isEmpty()) return ""
        val lines = md.replace("\r\n", "\n").replace("\r", "\n").split("\n")
        val out = StringBuilder()
        var i = 0
        val paragraph = ArrayList<String>()

        fun flushParagraph() {
            if (paragraph.isEmpty()) return
            out.append("<p>").append(inline(paragraph.joinToString(" "))).append("</p>")
            paragraph.clear()
        }

        while (i < lines.size) {
            val line = lines[i]
            val fence = FENCE.find(line)
            if (fence != null) {
                flushParagraph()
                val lang = fence.groupValues[1]
                val code = ArrayList<String>()
                i++
                while (i < lines.size && FENCE.find(lines[i]) == null) {
                    code.add(lines[i]); i++
                }
                if (i < lines.size) i++ // consume closing fence
                val body = if (showLineNumber) {
                    code.mapIndexed { idx, l -> "${idx + 1} $l" }.joinToString("\n")
                } else {
                    code.joinToString("\n")
                }
                val cls = if (lang.isNotEmpty()) " class=\"language-$lang\"" else ""
                out.append("<pre><code$cls>").append(escapeHtml(body)).append("</code></pre>")
                continue
            }
            if (line.isBlank()) {
                flushParagraph()
                i++
                continue
            }
            if (HR.matches(line)) {
                flushParagraph(); out.append("<hr>"); i++; continue
            }
            val heading = HEADING.find(line)
            if (heading != null) {
                flushParagraph()
                val level = heading.groupValues[1].length
                out.append("<h$level>").append(inline(heading.groupValues[2].trim())).append("</h$level>")
                i++
                continue
            }
            if (QUOTE.matches(line)) {
                flushParagraph()
                val quote = ArrayList<String>()
                while (i < lines.size && QUOTE.matches(lines[i])) {
                    quote.add(QUOTE.find(lines[i])!!.groupValues[1]); i++
                }
                out.append("<blockquote>").append(inline(quote.joinToString(" "))).append("</blockquote>")
                continue
            }
            if (UL_ITEM.matches(line)) {
                flushParagraph()
                out.append("<ul>")
                while (i < lines.size && UL_ITEM.matches(lines[i])) {
                    out.append("<li>").append(inline(UL_ITEM.find(lines[i])!!.groupValues[1])).append("</li>"); i++
                }
                out.append("</ul>")
                continue
            }
            if (OL_ITEM.matches(line)) {
                flushParagraph()
                out.append("<ol>")
                while (i < lines.size && OL_ITEM.matches(lines[i])) {
                    out.append("<li>").append(inline(OL_ITEM.find(lines[i])!!.groupValues[1])).append("</li>"); i++
                }
                out.append("</ol>")
                continue
            }
            paragraph.add(line.trim())
            i++
        }
        flushParagraph()
        return out.toString()
    }
}

/** Converts Markdown [md] to HTML for [UPMarkdown]/[UPParse]. */
internal fun upMarkdownToHtml(md: String, showLineNumber: Boolean = false): String =
    UPMarkdown.toHtml(md, showLineNumber)

/**
 * Native Compose counterpart of uview-plus `u-markdown`.
 *
 * Converts [`UPMarkdownProps.content`] to HTML with [upMarkdownToHtml] (mirroring upstream's
 * marked -> html -> u-parse pipeline) and renders it through [UPParse], forwarding `previewImg`,
 * `copyLink` and `domain`. `showLineNumber` numbers fenced code lines; `theme` (`light`/`dark`)
 * switches the content colour and background, matching the upstream `.dark` styles.
 */
@Composable
public fun UPMarkdown(
    props: UPMarkdownProps = UPMarkdownProps(),
    modifier: Modifier = Modifier,
    onLinkTap: ((String) -> Unit)? = null,
    onImgTap: ((String) -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    val html = remember(props.content, props.showLineNumber) { upMarkdownToHtml(props.content, props.showLineNumber) }
    val dark = props.theme == "dark"
    val contentColor = if (dark) Color(0xFFCCCCCC) else UPTheme.Content
    val background = if (dark) Color(0xFF1E1E1E) else Color.Transparent

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(background)
            .padding(16.dp)
            .upTestTag("markdown"),
    ) {
        UPParse(
            props = UPParseProps(
                content = html,
                previewImg = props.previewImg,
                copyLink = props.copyLink,
                domain = props.domain,
                customStyle = props.customStyle,
            ),
            onLinkTap = onLinkTap,
            onImgTap = onImgTap,
            contentColor = contentColor,
            diagnostics = diagnostics,
        )
    }
}
