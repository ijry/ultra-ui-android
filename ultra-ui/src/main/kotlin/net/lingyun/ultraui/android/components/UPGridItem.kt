package net.lingyun.ultraui.android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.lingyun.ultraui.android.core.UPColor
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPConfig
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.core.upClickable
import net.lingyun.ultraui.android.core.upTestTag

private const val GridItemComponentName = "UPGridItem"

/** Native Compose counterpart of uview-plus `u-grid-item`. */
@Composable
public fun UPGridItem(
    props: UPGridItemProps = UPGridItemProps(),
    modifier: Modifier = Modifier,
    onClick: ((UPRawValue) -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
    content: @Composable ColumnScope.() -> Unit,
) {
    val context = LocalUPGridLayoutContext.current
    val index = context?.allocateIndex() ?: 0
    val payload = props.name ?: index
    val backgroundColor = UPColor.parse(props.bgColor, androidx.compose.ui.graphics.Color.Transparent)
    val style = rememberUPResolvedStyle(props.customStyle, diagnostics, GridItemComponentName)
    var root = modifier
        .fillMaxWidth()
        .then(UPGridItemParentDataModifier(index))
        .background(backgroundColor, RoundedCornerShape(0.dp))
    if (context?.border == true) {
        root = root.border(0.5.dp, UPTheme.Border, RoundedCornerShape(0.dp))
    }
    root = root
        .applyUPResolvedStyle(style)
        .upTestTag("grid-item-$index")
    if (onClick != null) {
        root = root.upClickable(onClick = { onClick(payload) })
    }

    Column(
        modifier = root,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        with(UPColumnLayoutScopeAdapter) { content() }
    }
}

/** Direct argument form for generated Android source. */
@Composable
public fun UPGridItem(
    name: UPRawValue = UPConfig.gridItem.name,
    bgColor: String = UPConfig.gridItem.bgColor,
    customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
    modifier: Modifier = Modifier,
    onClick: ((UPRawValue) -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
    content: @Composable ColumnScope.() -> Unit,
) {
    UPGridItem(
        props = UPGridItemProps(
            name = name,
            bgColor = bgColor,
            customStyle = customStyle,
        ),
        modifier = modifier,
        onClick = onClick,
        diagnostics = diagnostics,
        content = content,
    )
}
