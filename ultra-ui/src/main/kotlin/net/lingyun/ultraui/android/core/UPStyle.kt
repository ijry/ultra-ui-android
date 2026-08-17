package net.lingyun.ultraui.android.core

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Cross-axis alignment resolved from a CSS-like `alignItems` declaration. */
public enum class UPCrossAxisAlignment {
    Start,
    Center,
    End,
    Stretch,
}

/** Main-axis alignment resolved from a CSS-like `justifyContent` declaration. */
public enum class UPMainAxisAlignment {
    Start,
    Center,
    End,
    SpaceBetween,
    SpaceAround,
    SpaceEvenly,
}

/** The supported, platform-neutral subset of uview-plus `customStyle`. */
public data class UPResolvedStyle(
    val width: Dp? = null,
    val height: Dp? = null,
    val marginTop: Dp = 0.dp,
    val marginEnd: Dp = 0.dp,
    val marginBottom: Dp = 0.dp,
    val marginStart: Dp = 0.dp,
    val paddingTop: Dp = 0.dp,
    val paddingEnd: Dp = 0.dp,
    val paddingBottom: Dp = 0.dp,
    val paddingStart: Dp = 0.dp,
    val color: Color? = null,
    val backgroundColor: Color? = null,
    val backgroundGradient: UPLinearGradient? = null,
    val borderColor: Color? = null,
    val borderWidth: Dp? = null,
    val borderRadius: Dp? = null,
    val fontSize: Dp? = null,
    val fontWeight: FontWeight? = null,
    val textAlign: TextAlign? = null,
    val opacity: Float? = null,
    val alignItems: UPCrossAxisAlignment? = null,
    val justifyContent: UPMainAxisAlignment? = null,
)

/** Resolves uview-compatible map or declaration-string styles without CSS runtime behavior. */
public object UPStyle {
    private const val componentName: String = "UPStyle"

    public fun resolve(
        input: UPStyleInput,
        availableScreenWidth: Dp,
        diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
        component: String = componentName,
    ): UPResolvedStyle {
        val entries = entriesFrom(input, diagnostics, component) ?: return UPResolvedStyle()
        if (entries.isEmpty()) return UPResolvedStyle()

        var width: Dp? = null
        var height: Dp? = null
        var marginTop = 0.dp
        var marginEnd = 0.dp
        var marginBottom = 0.dp
        var marginStart = 0.dp
        var paddingTop = 0.dp
        var paddingEnd = 0.dp
        var paddingBottom = 0.dp
        var paddingStart = 0.dp
        var color: Color? = null
        var backgroundColor: Color? = null
        var backgroundGradient: UPLinearGradient? = null
        var borderColor: Color? = null
        var borderWidth: Dp? = null
        var borderRadius: Dp? = null
        var fontSize: Dp? = null
        var fontWeight: FontWeight? = null
        var textAlign: TextAlign? = null
        var opacity: Float? = null
        var alignItems: UPCrossAxisAlignment? = null
        var justifyContent: UPMainAxisAlignment? = null

        fun reportMalformed(entry: StyleEntry, reason: String) {
            diagnostics.report(component, entry.originalKey, entry.value, reason)
        }

        fun dimension(entry: StyleEntry): Dp? =
            UPUnit.parseOrNull(entry.value, availableScreenWidth)
                ?: run {
                    reportMalformed(entry, "malformed-dimension")
                    null
                }

        fun parsedColor(entry: StyleEntry): Color? =
            UPColor.parseOrNull(entry.value)
                ?: run {
                    reportMalformed(entry, "malformed-color")
                    null
                }

        fun applySpacing(entry: StyleEntry, isMargin: Boolean) {
            val spacing = parseSpacing(entry.value, availableScreenWidth)
            if (spacing == null) {
                reportMalformed(entry, "malformed-dimension")
                return
            }
            val top = spacing[0]
            val end = spacing[1]
            val bottom = spacing[2]
            val start = spacing[3]
            if (isMargin) {
                marginTop = top
                marginEnd = end
                marginBottom = bottom
                marginStart = start
            } else {
                paddingTop = top
                paddingEnd = end
                paddingBottom = bottom
                paddingStart = start
            }
        }

        // CSS shorthands are intentionally applied before individual sides, independent of map iteration order.
        entries.filter { it.normalizedKey == "margin" }.forEach { applySpacing(it, isMargin = true) }
        entries.filter { it.normalizedKey == "padding" }.forEach { applySpacing(it, isMargin = false) }

        entries.filterNot { it.normalizedKey == "margin" || it.normalizedKey == "padding" }.forEach { entry ->
            when (entry.normalizedKey) {
                "width" -> width = dimension(entry)
                "height" -> height = dimension(entry)
                "margintop" -> dimension(entry)?.let { marginTop = it }
                "marginright", "marginend" -> dimension(entry)?.let { marginEnd = it }
                "marginbottom" -> dimension(entry)?.let { marginBottom = it }
                "marginleft", "marginstart" -> dimension(entry)?.let { marginStart = it }
                "paddingtop" -> dimension(entry)?.let { paddingTop = it }
                "paddingright", "paddingend" -> dimension(entry)?.let { paddingEnd = it }
                "paddingbottom" -> dimension(entry)?.let { paddingBottom = it }
                "paddingleft", "paddingstart" -> dimension(entry)?.let { paddingStart = it }
                "color" -> color = parsedColor(entry)
                "backgroundcolor" -> {
                    backgroundColor = parsedColor(entry)
                    backgroundGradient = null
                }
                "background" -> {
                    val gradient = UPColor.parseRightLinearGradient(entry.value)
                    if (gradient != null) {
                        backgroundGradient = gradient
                        backgroundColor = null
                    } else {
                        backgroundColor = parsedColor(entry)
                        backgroundGradient = null
                    }
                }
                "border" -> {
                    parseBorder(entry.value, availableScreenWidth)?.let { border ->
                        border.width?.let { borderWidth = it }
                        border.color?.let { borderColor = it }
                    } ?: reportMalformed(entry, "malformed-border")
                }
                "bordercolor" -> borderColor = parsedColor(entry)
                "borderwidth" -> borderWidth = dimension(entry)
                "borderradius" -> borderRadius = dimension(entry)
                "fontsize" -> fontSize = dimension(entry)
                "fontweight" -> {
                    fontWeight = parseFontWeight(entry.value)
                    if (fontWeight == null) reportMalformed(entry, "malformed-font-weight")
                }
                "textalign" -> {
                    textAlign = parseTextAlign(entry.value)
                    if (textAlign == null) reportMalformed(entry, "unsupported-text-align")
                }
                "opacity" -> {
                    opacity = entry.value.asFiniteFloatOrNull()?.takeIf { it >= 0f }?.coerceAtMost(1f)
                    if (opacity == null) reportMalformed(entry, "malformed-opacity")
                }
                "align", "alignitems" -> {
                    alignItems = parseCrossAxisAlignment(entry.value)
                    if (alignItems == null) reportMalformed(entry, "unsupported-alignment")
                }
                "justifycontent" -> {
                    justifyContent = parseMainAxisAlignment(entry.value)
                    if (justifyContent == null) reportMalformed(entry, "unsupported-alignment")
                }
                else -> diagnostics.report(component, entry.originalKey, entry.value, "unsupported-style-property")
            }
        }

        return UPResolvedStyle(
            width = width,
            height = height,
            marginTop = marginTop,
            marginEnd = marginEnd,
            marginBottom = marginBottom,
            marginStart = marginStart,
            paddingTop = paddingTop,
            paddingEnd = paddingEnd,
            paddingBottom = paddingBottom,
            paddingStart = paddingStart,
            color = color,
            backgroundColor = backgroundColor,
            backgroundGradient = backgroundGradient,
            borderColor = borderColor,
            borderWidth = borderWidth,
            borderRadius = borderRadius,
            fontSize = fontSize,
            fontWeight = fontWeight,
            textAlign = textAlign,
            opacity = opacity,
            alignItems = alignItems,
            justifyContent = justifyContent,
        )
    }

    private data class StyleEntry(
        val originalKey: String,
        val normalizedKey: String,
        val value: UPRawValue,
    )

    private data class ParsedBorder(
        val width: Dp?,
        val color: Color?,
    )

    private fun entriesFrom(
        input: UPStyleInput,
        diagnostics: UPCompatibilityDiagnostics,
        component: String,
    ): List<StyleEntry>? = when (input) {
        null -> emptyList()
        is Map<*, *> -> input.entries.mapNotNull { (key, value) ->
            (key as? String)?.let { StyleEntry(it, normalizeKey(it), value) }
                ?: run {
                    diagnostics.report(component, "customStyle", value, "non-string-style-key")
                    null
                }
        }
        is String -> parseDeclarationString(input, diagnostics, component)
        else -> {
            diagnostics.report(component, "customStyle", input, "unsupported-style-input")
            null
        }
    }

    private fun parseDeclarationString(
        value: String,
        diagnostics: UPCompatibilityDiagnostics,
        component: String,
    ): List<StyleEntry> = UPColor.splitTopLevel(value, ';').mapNotNull { declaration ->
        val trimmed = declaration.trim()
        if (trimmed.isEmpty()) return@mapNotNull null
        val separator = indexOfTopLevelColon(trimmed)
        if (separator <= 0) {
            diagnostics.report(component, "customStyle", trimmed, "malformed-style-declaration")
            return@mapNotNull null
        }
        val key = trimmed.substring(0, separator).trim()
        val rawValue = trimmed.substring(separator + 1).trim()
        if (key.isEmpty()) {
            diagnostics.report(component, "customStyle", trimmed, "malformed-style-declaration")
            null
        } else {
            StyleEntry(key, normalizeKey(key), rawValue)
        }
    }

    private fun indexOfTopLevelColon(value: String): Int {
        var depth = 0
        value.forEachIndexed { index, character ->
            when (character) {
                '(' -> depth += 1
                ')' -> if (depth > 0) depth -= 1
                ':' -> if (depth == 0) return index
            }
        }
        return -1
    }

    private fun normalizeKey(value: String): String = value.trim().lowercase().replace("-", "")

    private fun parseSpacing(value: UPRawValue, availableScreenWidth: Dp): List<Dp>? {
        if (value !is String) {
            val resolved = UPUnit.parseOrNull(value, availableScreenWidth) ?: return null
            return listOf(resolved, resolved, resolved, resolved)
        }

        val values = value.trim().split(Regex("\\s+")).filter(String::isNotEmpty)
        if (values.size !in 1..4) return null
        val resolved = values.map { UPUnit.parseOrNull(it, availableScreenWidth) ?: return null }
        return when (resolved.size) {
            1 -> List(4) { resolved[0] }
            2 -> listOf(resolved[0], resolved[1], resolved[0], resolved[1])
            3 -> listOf(resolved[0], resolved[1], resolved[2], resolved[1])
            else -> resolved
        }
    }

    private fun parseBorder(value: UPRawValue, availableScreenWidth: Dp): ParsedBorder? {
        if (value !is String) {
            return UPUnit.parseOrNull(value, availableScreenWidth)?.let { ParsedBorder(width = it, color = null) }
        }

        var width: Dp? = null
        var color: Color? = null
        val tokens = value.trim().split(Regex("\\s+")).filter(String::isNotEmpty)
        tokens.forEach { token ->
            if (width == null) UPUnit.parseOrNull(token, availableScreenWidth)?.let { width = it }
            if (color == null) UPColor.parseOrNull(token)?.let { color = it }
        }
        return ParsedBorder(width = width, color = color).takeIf { it.width != null || it.color != null }
    }

    private fun parseFontWeight(value: UPRawValue): FontWeight? = when (value) {
        is FontWeight -> value
        is Number -> fontWeight(value.toInt())
        is String -> when (value.trim().lowercase()) {
            "thin" -> FontWeight.Thin
            "extralight", "ultralight" -> FontWeight.ExtraLight
            "light" -> FontWeight.Light
            "normal", "regular" -> FontWeight.Normal
            "medium" -> FontWeight.Medium
            "semibold", "demibold" -> FontWeight.SemiBold
            "bold" -> FontWeight.Bold
            "extrabold", "ultrabold" -> FontWeight.ExtraBold
            "black", "heavy" -> FontWeight.Black
            else -> value.trim().toIntOrNull()?.let(::fontWeight)
        }
        else -> null
    }

    private fun fontWeight(value: Int): FontWeight? = value.takeIf { it in 1..1000 }?.let(::FontWeight)

    private fun parseTextAlign(value: UPRawValue): TextAlign? = when ((value as? String)?.trim()?.lowercase()) {
        "start" -> TextAlign.Start
        "end" -> TextAlign.End
        "left" -> TextAlign.Left
        "right" -> TextAlign.Right
        "center" -> TextAlign.Center
        "justify" -> TextAlign.Justify
        else -> null
    }

    private fun parseCrossAxisAlignment(value: UPRawValue): UPCrossAxisAlignment? = when ((value as? String)?.trim()?.lowercase()) {
        "start", "flex-start", "left", "top" -> UPCrossAxisAlignment.Start
        "center" -> UPCrossAxisAlignment.Center
        "end", "flex-end", "right", "bottom" -> UPCrossAxisAlignment.End
        "stretch" -> UPCrossAxisAlignment.Stretch
        else -> null
    }

    private fun parseMainAxisAlignment(value: UPRawValue): UPMainAxisAlignment? = when ((value as? String)?.trim()?.lowercase()) {
        "start", "flex-start" -> UPMainAxisAlignment.Start
        "center" -> UPMainAxisAlignment.Center
        "end", "flex-end" -> UPMainAxisAlignment.End
        "space-between" -> UPMainAxisAlignment.SpaceBetween
        "space-around" -> UPMainAxisAlignment.SpaceAround
        "space-evenly" -> UPMainAxisAlignment.SpaceEvenly
        else -> null
    }
}
