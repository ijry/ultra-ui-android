package net.lingyun.ultraui.android.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.lingyun.ultraui.android.R
import net.lingyun.ultraui.android.core.UPColor
import net.lingyun.ultraui.android.core.UPConfig
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPCompatibilityEvent
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPResolvedStyle
import net.lingyun.ultraui.android.core.UPUnit
import net.lingyun.ultraui.android.core.UPTheme

private const val IconComponentName: String = "UPIcon"
private val UPIconFontFamily: FontFamily = FontFamily(Font(R.font.upicon))

private data class UPIconResolution(
    val glyph: Char?,
    val labelPosition: String,
    val diagnostics: List<UPCompatibilityEvent>,
)

/**
 * Native Compose counterpart of uview-plus `u-icon`.
 *
 * Built-in `uicon-*` names use the pinned uview font. Image sources and arbitrary
 * custom icon fonts remain visible compatibility downgrades through [diagnostics].
 */
@Composable
public fun UPIcon(
    props: UPIconProps = UPIconProps(),
    modifier: Modifier = Modifier,
    onClick: ((UPRawValue) -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    val resolution = remember(props) { resolveIcon(props) }
    val style = rememberUPResolvedStyle(props.customStyle, diagnostics, IconComponentName)

    LaunchedEffect(resolution.diagnostics, diagnostics) {
        resolution.diagnostics.forEach(diagnostics::report)
    }

    val iconSize = UPUnit.toDp(props.size, availableScreenWidth(), 16.dp).coerceAtLeast(0.dp)
    val labelSize = UPUnit.toDp(props.labelSize, availableScreenWidth(), 15.dp).coerceAtLeast(0.dp)
    val spacing = UPUnit.toDp(props.space, availableScreenWidth(), 3.dp).coerceAtLeast(0.dp)
    val topOffset = UPUnit.toDp(props.top, availableScreenWidth(), 0.dp)
    val label = props.label.displayTextOrNull()
    val rootModifier = modifier
        .defaultMinSize(minWidth = 0.dp, minHeight = 0.dp)
        .testTag("up-icon")
        .semantics { contentDescription = "u-icon: ${props.name}" }
        .then(
            if (onClick == null) {
                Modifier
            } else {
                Modifier.clickable(role = Role.Button) { onClick(props.index) }
            },
        )

    @Composable
    fun Glyph() {
        val glyph = resolution.glyph ?: return
        UPIconGlyph(
            glyph = glyph,
            props = props,
            style = style,
            iconSize = iconSize.value.sp,
            topOffset = topOffset,
        )
    }

    @Composable
    fun Label() {
        if (label != null) {
            BasicText(
                text = label,
                style = TextStyle(
                    color = UPColor.parse(props.labelColor, UPTheme.Content),
                    fontSize = labelSize.value.sp,
                    lineHeight = labelSize.value.sp,
                ),
                modifier = Modifier.testTag("up-icon-label"),
            )
        }
    }

    when (resolution.labelPosition) {
        "left" -> Row(
            modifier = rootModifier,
            horizontalArrangement = Arrangement.spacedBy(spacing),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Label()
            Glyph()
        }

        "top" -> Column(
            modifier = rootModifier,
            verticalArrangement = Arrangement.spacedBy(spacing),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Label()
            Glyph()
        }

        "bottom" -> Column(
            modifier = rootModifier,
            verticalArrangement = Arrangement.spacedBy(spacing),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Glyph()
            Label()
        }

        else -> Row(
            modifier = rootModifier,
            horizontalArrangement = Arrangement.spacedBy(spacing),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Glyph()
            Label()
        }
    }
}

/** Direct argument form for source generators that do not construct Props values first. */
@Composable
public fun UPIcon(
    name: String,
    color: String = UPConfig.icon.color,
    size: UPRawValue = "16px",
    bold: Boolean = false,
    index: UPRawValue = "",
    hoverClass: String = "",
    customPrefix: String = "uicon",
    label: UPRawValue = "",
    labelPos: String = "right",
    labelSize: UPRawValue = "15px",
    labelColor: String = "#606266",
    space: UPRawValue = "3px",
    imgMode: String = "",
    width: UPRawValue = "",
    height: UPRawValue = "",
    top: UPRawValue = 0,
    stop: Boolean = false,
    customStyle: net.lingyun.ultraui.android.core.UPStyleInput = emptyMap<String, UPRawValue>(),
    modifier: Modifier = Modifier,
    onClick: ((UPRawValue) -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    UPIcon(
        props = UPIconProps(
            name = name,
            color = color,
            size = size,
            bold = bold,
            index = index,
            hoverClass = hoverClass,
            customPrefix = customPrefix,
            label = label,
            labelPos = labelPos,
            labelSize = labelSize,
            labelColor = labelColor,
            space = space,
            imgMode = imgMode,
            width = width,
            height = height,
            top = top,
            stop = stop,
            customStyle = customStyle,
        ),
        modifier = modifier,
        onClick = onClick,
        diagnostics = diagnostics,
    )
}

@Composable
private fun UPIconGlyph(
    glyph: Char,
    props: UPIconProps,
    style: UPResolvedStyle,
    iconSize: androidx.compose.ui.unit.TextUnit,
    topOffset: androidx.compose.ui.unit.Dp,
) {
    val fontSize = style.fontSize?.value?.sp ?: iconSize
    val color = style.color ?: UPColor.parse(props.color, UPTheme.Content)
    val fontWeight = style.fontWeight ?: if (props.bold) FontWeight.Bold else FontWeight.Normal
    val styleModifier = Modifier
        .applyUPResolvedStyle(style)
        .offset(y = topOffset)
        .testTag("up-icon-glyph")

    BasicText(
        text = glyph.toString(),
        style = TextStyle(
            color = color,
            fontFamily = UPIconFontFamily,
            fontSize = fontSize,
            lineHeight = fontSize,
            fontWeight = fontWeight,
            textAlign = style.textAlign ?: TextAlign.Start,
        ),
        modifier = styleModifier,
    )
}

private fun resolveIcon(props: UPIconProps): UPIconResolution {
    val diagnostics = buildList {
        if (props.labelPos !in IconLabelPositions) {
            add(
                UPCompatibilityEvent(
                    component = IconComponentName,
                    property = "labelPos",
                    value = props.labelPos,
                    reason = "Unsupported labelPos; using right.",
                ),
            )
        }
        if (props.hoverClass.isNotEmpty()) {
            add(
                UPCompatibilityEvent(
                    component = IconComponentName,
                    property = "hoverClass",
                    value = props.hoverClass,
                    reason = "hoverClass is not supported by native Compose icons.",
                ),
            )
        }
    }
    val labelPosition = props.labelPos.takeIf { it in IconLabelPositions } ?: "right"
    if (props.name.isEmpty()) return UPIconResolution(glyph = null, labelPosition = labelPosition, diagnostics = diagnostics)

    if (props.name.contains('/')) {
        return UPIconResolution(
            glyph = null,
            labelPosition = labelPosition,
            diagnostics = diagnostics + UPCompatibilityEvent(
                component = IconComponentName,
                property = "name",
                value = props.name,
                reason = "Image icon sources are not supported without an Android image loader.",
            ),
        )
    }
    if (props.customPrefix != "uicon") {
        return UPIconResolution(
            glyph = null,
            labelPosition = labelPosition,
            diagnostics = diagnostics + UPCompatibilityEvent(
                component = IconComponentName,
                property = "customPrefix",
                value = props.customPrefix,
                reason = "Custom icon fonts are not supported by the bundled uview font.",
            ),
        )
    }

    val glyph = UPIconGlyphs.codePoint(props.name) ?: props.name.privateUseGlyphOrNull()
    if (glyph == null) {
        return UPIconResolution(
            glyph = null,
            labelPosition = labelPosition,
            diagnostics = diagnostics + UPCompatibilityEvent(
                component = IconComponentName,
                property = "name",
                value = props.name,
                reason = "Unknown uview-plus icon glyph.",
            ),
        )
    }
    return UPIconResolution(glyph = glyph, labelPosition = labelPosition, diagnostics = diagnostics)
}

private fun String.privateUseGlyphOrNull(): Char? = singleOrNull()?.takeIf { it.code in 0xE000..0xF8FF }

private fun UPRawValue.displayTextOrNull(): String? = when (this) {
    null -> null
    else -> toString().takeIf(String::isNotEmpty)
}

private val IconLabelPositions: Set<String> = setOf("left", "right", "top", "bottom")
