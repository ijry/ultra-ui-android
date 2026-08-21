package net.lingyun.ultraui.android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.lingyun.ultraui.android.core.UPColor
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.core.upClickable
import net.lingyun.ultraui.android.core.upSafeEnum
import net.lingyun.ultraui.android.core.upTestTag

@Immutable
private data class UPTabbarContext(
    val selected: UPRawValue,
    val activeColor: String,
    val inactiveColor: String,
    val textMode: String,
    val nextIndex: () -> Int,
    val select: (UPRawValue, Int) -> Unit,
)

private val LocalUPTabbar = staticCompositionLocalOf<UPTabbarContext?> { null }

@Composable
public fun UPTabbar(
    props: UPTabbarProps = UPTabbarProps(),
    modifier: Modifier = Modifier,
    onChange: ((UPTabbarChangeEvent) -> Unit)? = null,
    onUpdateValue: ((UPRawValue) -> Unit)? = null,
    onUpdateModelValue: ((UPRawValue) -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
    content: @Composable RowScope.() -> Unit,
) {
    var selected by remember(props.value, props.modelValue, props.current) { mutableStateOf(resolveTabbarValue(props)) }
    LaunchedEffect(props.value, props.modelValue, props.current) { selected = resolveTabbarValue(props) }
    var nextIndex = 0
    val borderColor = UPColor.parse(props.borderColor, UPTheme.Border)
    val background = UPColor.parse(props.backgroundColor, Color.White)
    val context = UPTabbarContext(
        selected = selected,
        activeColor = props.activeColor,
        inactiveColor = props.inactiveColor,
        textMode = upSafeEnum(props.textMode, setOf("always", "active"), "always", diagnostics, "UPTabbar", "textMode"),
        nextIndex = { nextIndex++ },
        select = { value, index ->
            tabbarChangeEvent(selected, value, index)?.let { event ->
                selected = value
                onUpdateValue?.invoke(value)
                onUpdateModelValue?.invoke(value)
                onChange?.invoke(event)
            }
        },
    )
    Row(
        modifier = modifier.fillMaxWidth().background(background)
            .then(if (props.border) Modifier.border(0.5.dp, borderColor) else Modifier)
            .then(if (props.safeAreaInsetBottom) Modifier.navigationBarsPadding() else Modifier)
            .applyUPResolvedStyle(rememberUPResolvedStyle(props.customStyle, diagnostics, "UPTabbar"))
            .upTestTag("tabbar"),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CompositionLocalProvider(LocalUPTabbar provides context) { content() }
    }
}

@Composable
public fun RowScope.UPTabbarItem(
    props: UPTabbarItemProps = UPTabbarItemProps(),
    modifier: Modifier = Modifier,
    onClick: ((UPRawValue) -> Unit)? = null,
) {
    val parent = LocalUPTabbar.current
    val index = parent?.nextIndex?.invoke() ?: 0
    val value = tabbarItemValue(props.name, index)
    val active = parent?.selected.rawEquals(value)
    val color = UPColor.parse(if (active) parent?.activeColor else parent?.inactiveColor, if (active) UPTheme.Primary else UPTheme.Content)
    val icon = when {
        active && props.activeIcon.toString().isNotBlank() -> props.activeIcon
        !active && props.inactiveIcon.toString().isNotBlank() -> props.inactiveIcon
        else -> props.icon
    }.toString()
    val suffix = value.toString()
    val isMiddleButton = isTabbarMiddleButton(props.mode)
    val shape = if (isMiddleButton) RoundedCornerShape(50) else RoundedCornerShape(0.dp)
    Column(
        modifier = modifier.weight(1f)
            .background(if (isMiddleButton) UPColor.parse(props.midButtonBgColor, Color.White) else Color.Transparent, shape)
            .upClickable {
                parent?.select?.invoke(value, index)
                onClick?.invoke(value)
            }
            .padding(horizontal = 8.dp, vertical = 7.dp)
            .upTestTag("tabbar-item-$suffix"),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (icon.isNotBlank()) {
            UPBadge(
                props = UPBadgeProps(
                    isDot = props.dot,
                    value = props.badge ?: 0,
                    show = props.badge?.let { tabbarBadgeIsVisible(props.dot, it) } ?: props.dot,
                    showZero = props.dot,
                    absolute = true,
                    customStyle = props.badgeStyle,
                ),
                content = {
                    UPIcon(UPIconProps(name = icon, color = color.toHexString(), size = tabbarIconSize(props.mode, props.midButtonIconSize)))
                },
            )
        }
        // uview keeps the label mounted in every text mode and only mutes the
        // inactive copy (`u-tabbar-item__text--muted`: opacity .68, scale .94).
        val muted = parent?.textMode == "active" && !active
        BasicText(
            props.text.toString(),
            style = TextStyle(color = if (muted) color.copy(alpha = 0.68f) else color, fontSize = 12.sp),
            modifier = Modifier.padding(top = 2.dp),
        )
    }
}

private fun Color.toHexString(): String = "#%02x%02x%02x".format(
    (red * 255).toInt().coerceIn(0, 255),
    (green * 255).toInt().coerceIn(0, 255),
    (blue * 255).toInt().coerceIn(0, 255),
)
