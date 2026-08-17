package net.lingyun.ultraui.android.components

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.HorizontalAlignmentLine
import androidx.compose.ui.layout.Measured
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.layout.VerticalAlignmentLine
import androidx.compose.ui.unit.Density
import net.lingyun.ultraui.android.core.UPMainAxisAlignment
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPCrossAxisAlignment
import net.lingyun.ultraui.android.core.upSafeEnum

@Immutable
internal data class UPColParentData(
    val span: Int,
    val offset: Int,
)

internal class UPColParentDataModifier(
    private val span: Int,
    private val offset: Int,
) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?): Any = UPColParentData(span, offset)
}

@Immutable
internal data class UPGridItemParentData(
    val index: Int,
)

internal class UPGridItemParentDataModifier(
    private val index: Int,
) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?): Any = UPGridItemParentData(index)
}

@Immutable
internal data class UPRowLayoutContext(
    val gutter: androidx.compose.ui.unit.Dp,
)

internal val LocalUPRowLayoutContext = compositionLocalOf<UPRowLayoutContext?> { null }

/** Composition-local state shared by a grid and its item children. */
internal class UPGridLayoutContext(
    val columns: Int,
    val gap: androidx.compose.ui.unit.Dp,
    val border: Boolean,
) {
    private var nextIndex: Int = 0

    fun beginComposition() {
        nextIndex = 0
    }

    fun allocateIndex(): Int = nextIndex++
}

internal val LocalUPGridLayoutContext = compositionLocalOf<UPGridLayoutContext?> { null }

/**
 * Public row content is typed as RowScope by the source-generation contract.
 * A custom Layout cannot expose Compose's private RowScope instance, so this
 * adapter preserves the receiver API while leaving ordinary child modifiers
 * unchanged.
 */
internal object UPRowScopeAdapter : RowScope {
    override fun Modifier.weight(weight: Float, fill: Boolean): Modifier = this

    override fun Modifier.align(alignment: Alignment.Vertical): Modifier = this

    override fun Modifier.alignBy(alignmentLine: HorizontalAlignmentLine): Modifier = this

    override fun Modifier.alignByBaseline(): Modifier = this

    override fun Modifier.alignBy(alignmentLineBlock: (Measured) -> Int): Modifier = this
}

/** Same adapter pattern for the ColumnScope content exposed by UPCol/GridItem. */
internal object UPColumnLayoutScopeAdapter : ColumnScope {
    override fun Modifier.weight(weight: Float, fill: Boolean): Modifier = this

    override fun Modifier.align(alignment: Alignment.Horizontal): Modifier = this

    override fun Modifier.alignBy(alignmentLine: VerticalAlignmentLine): Modifier = this

    override fun Modifier.alignBy(alignmentLineBlock: (Measured) -> Int): Modifier = this
}

@androidx.compose.runtime.Composable
internal fun UPRawValue.upNonNegativeDp(default: androidx.compose.ui.unit.Dp): androidx.compose.ui.unit.Dp =
    upRawDp(this, default).coerceAtLeast(androidx.compose.ui.unit.Dp.Hairline)

internal fun resolveUPMainAxis(
    value: String,
    fallback: String,
    diagnostics: net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics,
    component: String,
    property: String,
): UPMainAxisAlignment {
    val normalized = upSafeEnum(
        value = value,
        allowed = setOf(
            "start",
            "flex-start",
            "end",
            "flex-end",
            "center",
            "around",
            "space-around",
            "between",
            "space-between",
            "space-evenly",
        ),
        fallback = fallback,
        diagnostics = diagnostics,
        component = component,
        property = property,
    )
    return when (normalized) {
        "end", "flex-end" -> UPMainAxisAlignment.End
        "center" -> UPMainAxisAlignment.Center
        "around", "space-around" -> UPMainAxisAlignment.SpaceAround
        "between", "space-between" -> UPMainAxisAlignment.SpaceBetween
        "space-evenly" -> UPMainAxisAlignment.SpaceEvenly
        else -> UPMainAxisAlignment.Start
    }
}

internal fun resolveUPCrossAxis(
    value: String,
    fallback: String,
    diagnostics: net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics,
    component: String,
    property: String,
): UPCrossAxisAlignment {
    val normalized = upSafeEnum(
        value = value,
        allowed = setOf("top", "bottom", "start", "end", "center", "stretch"),
        fallback = fallback,
        diagnostics = diagnostics,
        component = component,
        property = property,
    )
    return when (normalized) {
        "top", "start" -> UPCrossAxisAlignment.Start
        "bottom", "end" -> UPCrossAxisAlignment.End
        "stretch" -> UPCrossAxisAlignment.Stretch
        else -> UPCrossAxisAlignment.Center
    }
}

internal fun resolveUPTextAlign(
    value: String,
    diagnostics: net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics,
    component: String,
): String = upSafeEnum(
    value = value,
    allowed = setOf("left", "center", "right", "justify"),
    fallback = "left",
    diagnostics = diagnostics,
    component = component,
    property = "textAlign",
)


internal fun formatUPPercentage(value: Float): String {
    val rounded = value.toInt().toFloat()
    return if (value == rounded) {
        "${rounded.toInt()}%"
    } else {
        "${("%.2f".format(java.util.Locale.US, value).trimEnd('0').trimEnd('.'))}%"
    }
}
