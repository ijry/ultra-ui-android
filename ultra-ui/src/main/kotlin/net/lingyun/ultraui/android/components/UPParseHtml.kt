package net.lingyun.ultraui.android.components

/**
 * A parsed HTML node for [UPParse]: either raw [UPParseText] or an [UPParseElement] with a lowercase
 * tag name, its attributes and children.
 */
internal sealed interface UPParseNode

internal data class UPParseText(val text: String) : UPParseNode

internal data class UPParseElement(
    val name: String,
    val attrs: Map<String, String>,
    val children: List<UPParseNode>,
) : UPParseNode

/**
 * Bounded HTML parser for uview-plus `u-parse` (mp-html). This is not a line-for-line port of the
 * full mp-html engine; it is a focused, well-formed-tree parser that honours the same tag
 * classification tables (void / ignore / raw-text) and HTML entity set so common rich text renders
 * faithfully. Unsupported/exotic constructs degrade to plain text rather than throwing.
 */
internal object UPParseHtml {
    // Self-closing tags: never take children.
    private val VOID_TAGS = setOf(
        "area", "base", "br", "col", "circle", "ellipse", "embed", "frame", "hr", "img", "input",
        "line", "link", "meta", "param", "path", "polygon", "rect", "source", "track", "use", "wbr",
    )

    // Tags removed together with their (raw) content.
    private val RAW_TEXT_TAGS = setOf("script", "style", "textarea", "title")

    // Tags dropped entirely (their children are discarded).
    private val IGNORE_TAGS = setOf(
        "area", "base", "canvas", "embed", "frame", "head", "iframe", "input", "link", "map",
        "meta", "param", "rp", "source", "track", "wbr",
    )

    private val NAMED_ENTITIES = mapOf(
        "lt" to "<", "gt" to ">", "quot" to "\"", "apos" to "'", "amp" to "&",
        "ensp" to "\u2002", "emsp" to "\u2003", "nbsp" to "\u00A0", "semi" to ";",
        "ndash" to "\u2013", "mdash" to "\u2014", "middot" to "\u00B7",
        "lsquo" to "\u2018", "rsquo" to "\u2019", "ldquo" to "\u201C", "rdquo" to "\u201D",
        "bull" to "\u2022", "hellip" to "\u2026",
        "larr" to "\u2190", "uarr" to "\u2191", "rarr" to "\u2192", "darr" to "\u2193",
        "copy" to "\u00A9", "reg" to "\u00AE", "times" to "\u00D7", "divide" to "\u00F7",
    )

    /** Decodes HTML character references (named + decimal + hex) in [text]. */
    fun decodeEntities(text: String): String {
        if (text.indexOf('&') < 0) return text
        val sb = StringBuilder(text.length)
        var i = 0
        while (i < text.length) {
            val c = text[i]
            if (c != '&') {
                sb.append(c)
                i++
                continue
            }
            val semi = text.indexOf(';', i + 1)
            if (semi < 0 || semi - i > 12) {
                sb.append(c)
                i++
                continue
            }
            val body = text.substring(i + 1, semi)
            val decoded = when {
                body.startsWith("#x") || body.startsWith("#X") ->
                    body.substring(2).toIntOrNull(16)?.let { cp -> runCatching { String(Character.toChars(cp)) }.getOrNull() }
                body.startsWith("#") ->
                    body.substring(1).toIntOrNull()?.let { cp -> runCatching { String(Character.toChars(cp)) }.getOrNull() }
                else -> NAMED_ENTITIES[body]
            }
            if (decoded != null) {
                sb.append(decoded)
                i = semi + 1
            } else {
                sb.append(c)
                i++
            }
        }
        return sb.toString()
    }

    private data class Frame(val name: String, val attrs: Map<String, String>, val children: MutableList<UPParseNode>)

    /** Parses [content] into a node tree. */
    fun parse(content: String): List<UPParseNode> {
        val root = Frame("", emptyMap(), mutableListOf())
        val stack = ArrayDeque<Frame>()
        stack.addLast(root)
        var i = 0
        val n = content.length
        while (i < n) {
            val lt = content.indexOf('<', i)
            if (lt < 0) {
                appendText(stack.last().children, content.substring(i))
                break
            }
            if (lt > i) appendText(stack.last().children, content.substring(i, lt))

            // Comment / doctype / CDATA
            if (content.startsWith("<!--", lt)) {
                val end = content.indexOf("-->", lt + 4)
                i = if (end < 0) n else end + 3
                continue
            }
            if (content.startsWith("<!", lt) || content.startsWith("<?", lt)) {
                val end = content.indexOf('>', lt + 2)
                i = if (end < 0) n else end + 1
                continue
            }

            val gt = content.indexOf('>', lt + 1)
            if (gt < 0) {
                appendText(stack.last().children, content.substring(lt))
                break
            }
            val rawTag = content.substring(lt + 1, gt)

            if (rawTag.startsWith("/")) {
                // Closing tag: pop to the matching open frame if present.
                val name = rawTag.substring(1).trim().lowercase()
                closeTag(stack, name)
                i = gt + 1
                continue
            }

            val selfClose = rawTag.endsWith("/")
            val body = if (selfClose) rawTag.dropLast(1) else rawTag
            val (name, attrs) = parseTag(body)
            if (name.isEmpty()) {
                i = gt + 1
                continue
            }

            if (name in RAW_TEXT_TAGS) {
                // Skip raw content up to the matching close tag.
                val closeIdx = indexOfCloseTag(content, name, gt + 1)
                i = if (closeIdx < 0) n else closeIdx
                continue
            }

            if (name in IGNORE_TAGS) {
                i = gt + 1
                continue
            }

            if (selfClose || name in VOID_TAGS) {
                stack.last().children.add(UPParseElement(name, attrs, emptyList()))
                i = gt + 1
                continue
            }

            stack.addLast(Frame(name, attrs, mutableListOf()))
            i = gt + 1
        }
        // Unwind any unclosed frames.
        while (stack.size > 1) {
            val f = stack.removeLast()
            stack.last().children.add(UPParseElement(f.name, f.attrs, f.children))
        }
        return root.children
    }

    private fun closeTag(stack: ArrayDeque<Frame>, name: String) {
        // Find nearest matching open frame; if none, ignore the stray close tag.
        val idx = stack.indexOfLast { it.name == name }
        if (idx <= 0) return
        while (stack.size - 1 >= idx) {
            val f = stack.removeLast()
            stack.last().children.add(UPParseElement(f.name, f.attrs, f.children))
        }
    }

    private fun appendText(into: MutableList<UPParseNode>, raw: String) {
        if (raw.isEmpty()) return
        into.add(UPParseText(decodeEntities(raw)))
    }

    private fun indexOfCloseTag(content: String, name: String, from: Int): Int {
        var search = from
        while (true) {
            val idx = content.indexOf("</", search, ignoreCase = true)
            if (idx < 0) return -1
            val gt = content.indexOf('>', idx + 2)
            if (gt < 0) return -1
            val closing = content.substring(idx + 2, gt).trim().lowercase()
            if (closing == name) return gt + 1
            search = gt + 1
        }
    }

    private fun parseTag(body: String): Pair<String, Map<String, String>> {
        val trimmed = body.trim()
        if (trimmed.isEmpty()) return "" to emptyMap()
        var j = 0
        while (j < trimmed.length && !trimmed[j].isWhitespace()) j++
        val name = trimmed.substring(0, j).lowercase()
        val attrs = LinkedHashMap<String, String>()
        while (j < trimmed.length) {
            while (j < trimmed.length && trimmed[j].isWhitespace()) j++
            if (j >= trimmed.length) break
            val keyStart = j
            while (j < trimmed.length && trimmed[j] != '=' && !trimmed[j].isWhitespace()) j++
            val key = trimmed.substring(keyStart, j).lowercase()
            if (key.isEmpty()) {
                j++
                continue
            }
            while (j < trimmed.length && trimmed[j].isWhitespace()) j++
            if (j < trimmed.length && trimmed[j] == '=') {
                j++
                while (j < trimmed.length && trimmed[j].isWhitespace()) j++
                var value: String
                if (j < trimmed.length && (trimmed[j] == '"' || trimmed[j] == '\'')) {
                    val quote = trimmed[j]
                    j++
                    val vStart = j
                    while (j < trimmed.length && trimmed[j] != quote) j++
                    value = trimmed.substring(vStart, minOf(j, trimmed.length))
                    j++
                } else {
                    val vStart = j
                    while (j < trimmed.length && !trimmed[j].isWhitespace()) j++
                    value = trimmed.substring(vStart, j)
                }
                attrs[key] = decodeEntities(value)
            } else {
                attrs[key] = ""
            }
        }
        return name to attrs
    }
}

/** Parses [content] into an HTML node tree for [UPParse]. */
internal fun upParseHtml(content: String): List<UPParseNode> = UPParseHtml.parse(content)
