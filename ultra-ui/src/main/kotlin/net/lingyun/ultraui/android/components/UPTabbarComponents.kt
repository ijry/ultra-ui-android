package net.lingyun.ultraui.android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import net.lingyun.ultraui.android.core.UPColor
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.core.asFiniteFloatOrNull
import net.lingyun.ultraui.android.core.report
import net.lingyun.ultraui.android.core.upClickable
import net.lingyun.ultraui.android.core.upSafeEnum
import net.lingyun.ultraui.android.core.upTestTag

private const val TabbarComponentName = "UPTabbar"
private const val TabbarItemComponentName = "UPTabbarItem"
private const val DefaultIconScale = 1.1f
private val TabbarStyles = setOf("default", "minimal", "pill", "card", "glow", "lift", "underline", "dot", "convex")
private val TabbarAnimations = setOf("none", "scale", "lift", "swing", "pulse")
private val TabbarItemShapes = setOf("default", "round", "square")

@Immutable
private data class UPTabbarContext(
    val selected: UPRawValue,
    val activeColor: Color,
    val inactiveColor: Color,
    val activeBackgroundColor: Color,
    val inactiveBackgroundColor: Color,
    val styleType: String,
    val animationType: String,
    val itemShape: String,
    val iconScale: Float,
    val textMode: String,
    val border: Boolean,
    val borderColor: Color,
    val diagnostics: UPCompatibilityDiagnostics,
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

    val styleType = upSafeEnum(props.styleType, TabbarStyles, "default", diagnostics, TabbarComponentName, "styleType")
    val animationType = upSafeEnum(props.animationType, TabbarAnimations, "none", diagnostics, TabbarComponentName, "animationType")
    val itemShape = upSafeEnum(props.itemShape, TabbarItemShapes, "default", diagnostics, TabbarComponentName, "itemShape")
    val textMode = upSafeEnum(props.textMode, setOf("always", "active"), "always", diagnostics, TabbarComponentName, "textMode")
    val iconScale = resolveTabbarIconScale(props.iconScale, diagnostics)
    val borderColor = UPColor.parse(props.borderColor, UPTheme.Border)
    val background = UPColor.parse(props.backgroundColor, Color.White)
    val activeColor = UPColor.parse(props.activeColor, UPTheme.Primary)
    val inactiveColor = UPColor.parse(props.inactiveColor, UPTheme.Content)
    val activeBackgroundColor = UPColor.parse(props.activeBackgroundColor, Color.Transparent)
    val inactiveBackgroundColor = UPColor.parse(props.inactiveBackgroundColor, Color.Transparent)
    var nextIndex = 0
    val context = UPTabbarContext(
        selected = selected,
        activeColor = activeColor,
        inactiveColor = inactiveColor,
        activeBackgroundColor = activeBackgroundColor,
        inactiveBackgroundColor = inactiveBackgroundColor,
        styleType = styleType,
        animationType = animationType,
        itemShape = itemShape,
        iconScale = iconScale,
        textMode = textMode,
        border = props.border,
        borderColor = borderColor,
        diagnostics = diagnostics,
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
    val style = rememberUPResolvedStyle(props.customStyle, diagnostics, TabbarComponentName)
    val wrapperShape = tabbarWrapperShape(styleType)
    val wrapperElevation = tabbarWrapperElevation(styleType)
    val wrapperModifier = Modifier
        .fillMaxWidth()
        .then(if (wrapperElevation > 0.dp) Modifier.shadow(wrapperElevation, wrapperShape, clip = false) else Modifier)
        .background(background, wrapperShape)
        .then(if (props.border) Modifier.border(0.5.dp, borderColor, wrapperShape) else Modifier)
        .then(if (styleType == "pill" || styleType == "glow") Modifier.padding(horizontal = 12.dp) else Modifier)
        .then(if (props.safeAreaInsetBottom) Modifier.navigationBarsPadding() else Modifier)
        .upTestTag("tabbar-style-$styleType")

    Column(
        modifier = modifier
            .fillMaxWidth()
            .applyUPResolvedStyle(style)
            .upTestTag("tabbar"),
    ) {
        Row(
            modifier = wrapperModifier,
            horizontalArrangement = if (styleType == "pill" || styleType == "card" || styleType == "glow" || styleType == "convex") {
                Arrangement.spacedBy(4.dp)
            } else {
                Arrangement.SpaceEvenly
            },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CompositionLocalProvider(LocalUPTabbar provides context) { content() }
        }
    }
}

@Composable
public fun RowScope.UPTabbarItem(
    props: UPTabbarItemProps = UPTabbarItemProps(),
    modifier: Modifier = Modifier,
    onClick: ((UPRawValue) -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    val parent = LocalUPTabbar.current
    val effectiveDiagnostics = parent?.diagnostics ?: diagnostics
    val index = parent?.nextIndex?.invoke() ?: 0
    val value = tabbarItemValue(props.name, index)
    val active = parent?.selected.rawEquals(value) == true
    val state = if (active) "active" else "inactive"
    val styleType = parent?.styleType ?: "default"
    val animationType = parent?.animationType ?: "none"
    val itemShapeName = parent?.itemShape ?: "default"
    val activeColor = parent?.activeColor ?: UPTheme.Primary
    val inactiveColor = parent?.inactiveColor ?: UPTheme.Content
    val color = if (active) activeColor else inactiveColor
    val icon = when {
        active && props.activeIcon.toString().isNotBlank() -> props.activeIcon
        !active && props.inactiveIcon.toString().isNotBlank() -> props.inactiveIcon
        else -> props.icon
    }.toString()
    val suffix = value.toString()
    val isMiddleButton = isTabbarMiddleButton(props.mode)
    val itemStyle = rememberUPResolvedStyle(props.customStyle, effectiveDiagnostics, TabbarItemComponentName)
    val itemShape = tabbarItemShape(styleType, itemShapeName)
    val iconScale = parent?.iconScale ?: DefaultIconScale
    val iconAnimation = if (active) animationType else "none"
    val midButtonOffset = upRawDp(props.midButtonOffsetY, (-10).dp)
    val outerShadow = tabbarShadowElevation(props.midButtonBoxShadow)
    val innerShadow = props.midButtonInnerBoxShadow.isNotBlank()
    val rootTag = "tabbar-item-$suffix"

    LaunchedEffect(active, props.activeClass, props.inactiveClass, effectiveDiagnostics) {
        val className = if (active) props.activeClass else props.inactiveClass
        val property = if (active) "activeClass" else "inactiveClass"
        if (className.isNotBlank()) {
            effectiveDiagnostics.report(
                TabbarItemComponentName,
                property,
                className,
                "CSS class hooks have no native Android equivalent; state remains exposed through native semantics.",
            )
        }
    }
    LaunchedEffect(isMiddleButton, props.midButtonBoxShadow, props.midButtonInnerBoxShadow, effectiveDiagnostics) {
        if (isMiddleButton && props.midButtonBoxShadow.isNotBlank()) {
            effectiveDiagnostics.report(
                TabbarItemComponentName,
                "midButtonBoxShadow",
                props.midButtonBoxShadow,
                "CSS box-shadow is approximated by native elevation on Android.",
            )
        }
        if (isMiddleButton && props.midButtonInnerBoxShadow.isNotBlank()) {
            effectiveDiagnostics.report(
                TabbarItemComponentName,
                "midButtonInnerBoxShadow",
                props.midButtonInnerBoxShadow,
                "CSS inset box-shadow is approximated by a native inner border on Android.",
            )
        }
    }

    val rootModifier = modifier
        .weight(1f)
        .zIndex(if (isMiddleButton) 2f else 0f)
        .applyUPResolvedStyle(itemStyle)
        .upClickable {
            parent?.select?.invoke(value, index)
            onClick?.invoke(value)
        }
        .upTestTag(rootTag)

    Column(
        modifier = rootModifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    if (isMiddleButton) Color.Transparent else if (active) {
                        parent?.activeBackgroundColor ?: Color.Transparent
                    } else {
                        parent?.inactiveBackgroundColor ?: Color.Transparent
                    },
                    itemShape,
                )
                .upTestTag("$rootTag-content-$state"),
        ) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(itemShape)
                    .upTestTag("$rootTag-bubble"),
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(itemShape)
                    .upTestTag("$rootTag-bubble-shape-$itemShapeName"),
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 8.dp,
                        vertical = if (styleType == "underline" || styleType == "dot") 5.dp else 7.dp,
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (isMiddleButton) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .offset(y = midButtonOffset)
                            .shadow(outerShadow, CircleShape, clip = false)
                            .background(Color.White, CircleShape)
                            .then(
                                if (parent?.border == true) {
                                    Modifier.border(1.dp, parent.borderColor, CircleShape)
                                } else {
                                    Modifier
                                },
                            )
                            .upTestTag("$rootTag-mid-button"),
                        contentAlignment = Alignment.Center,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .background(UPColor.parse(props.midButtonBgColor, Color.White), CircleShape)
                                .then(
                                    if (innerShadow) Modifier.border(1.dp, Color.Black.copy(alpha = 0.08f), CircleShape) else Modifier,
                                )
                                .upTestTag("$rootTag-mid-button-inner"),
                        )
                        TabbarIcon(
                            icon = icon,
                            color = UPColor.parse(props.midButtonIconColor, Color(0xFF3C9CFF)),
                            size = tabbarIconSize(props.mode, props.midButtonIconSize),
                            iconTag = "$rootTag-icon",
                            animation = if (active) animationType else "none",
                            iconScale = iconScale,
                            modifier = Modifier.size(52.dp).zIndex(2f),
                            badge = props,
                            diagnostics = effectiveDiagnostics,
                        )
                    }
                } else {
                    TabbarIcon(
                        icon = icon,
                        color = color,
                        size = tabbarIconSize(props.mode, props.midButtonIconSize),
                        iconTag = "$rootTag-icon",
                        animation = iconAnimation,
                        iconScale = iconScale,
                        modifier = Modifier.fillMaxWidth().height(24.dp),
                        badge = props,
                        diagnostics = effectiveDiagnostics,
                    )
                }
                BasicText(
                    props.text.toString(),
                    style = TextStyle(
                        color = if (parent?.textMode == "active" && !active) color.copy(alpha = 0.68f) else color,
                        fontSize = 12.sp,
                    ),
                        modifier = Modifier
                        .padding(top = 2.dp)
                        .then(if (isMiddleButton) Modifier.offset(y = (-4).dp) else Modifier)
                        .upTestTag("$rootTag-text-$state"),
                )
            }
        }
        if (active && styleType == "underline") {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .background(color, RoundedCornerShape(50))
                    .upTestTag("$rootTag-underline"),
            )
        }
        if (active && styleType == "dot") {
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .background(color, CircleShape)
                    .upTestTag("$rootTag-dot"),
            )
        }
    }
}

@Composable
private fun TabbarIcon(
    icon: String,
    color: Color,
    size: UPRawValue,
    iconTag: String,
    animation: String,
    iconScale: Float,
    modifier: Modifier,
    badge: UPTabbarItemProps,
    diagnostics: UPCompatibilityDiagnostics,
) {
    val transform = tabbarIconTransform(animation, iconScale)
    val transformedModifier = modifier
        .then(if (transform.offsetY != 0.dp) Modifier.offset(y = transform.offsetY) else Modifier)
        .graphicsLayer {
            scaleX = transform.scale
            scaleY = transform.scale
            rotationZ = transform.rotation
        }
    Box(
        modifier = transformedModifier.then(
            if (animation != "none") Modifier.upTestTag("$iconTag-animation-$animation") else Modifier,
        ),
    ) {
        Box(
            modifier = Modifier.fillMaxSize().upTestTag(iconTag),
            contentAlignment = Alignment.Center,
        ) {
            if (icon.isNotBlank()) {
                UPBadge(
                    props = UPBadgeProps(
                        isDot = badge.dot,
                        value = badge.badge ?: 0,
                        show = badge.badge?.let { tabbarBadgeIsVisible(badge.dot, it) } ?: badge.dot,
                        showZero = badge.dot,
                        absolute = true,
                        customStyle = badge.badgeStyle,
                    ),
                    content = {
                        UPIcon(
                            props = UPIconProps(
                                name = icon,
                                color = color.toHexString(),
                                size = size,
                            ),
                            diagnostics = diagnostics,
                        )
                    },
                    diagnostics = diagnostics,
                )
            }
        }
    }
}

private data class TabbarIconTransform(
    val offsetY: androidx.compose.ui.unit.Dp = 0.dp,
    val scale: Float = 1f,
    val rotation: Float = 0f,
)

private fun tabbarIconTransform(animation: String, iconScale: Float): TabbarIconTransform = when (animation) {
    "scale" -> TabbarIconTransform(scale = iconScale)
    "lift" -> TabbarIconTransform(offsetY = (-6).dp, scale = iconScale)
    "swing" -> TabbarIconTransform(scale = iconScale, rotation = -10f)
    "pulse" -> TabbarIconTransform(scale = iconScale)
    else -> TabbarIconTransform()
}

private fun resolveTabbarIconScale(value: UPRawValue, diagnostics: UPCompatibilityDiagnostics): Float {
    val parsed = value.asFiniteFloatOrNull()?.takeIf { it > 0f }
    if (parsed != null) return parsed
    diagnostics.report(TabbarComponentName, "iconScale", value, "Malformed icon scale; using $DefaultIconScale.")
    return DefaultIconScale
}

private fun tabbarWrapperShape(styleType: String): RoundedCornerShape = when (styleType) {
    "pill", "glow" -> RoundedCornerShape(50)
    "card" -> RoundedCornerShape(12.dp)
    "convex" -> RoundedCornerShape(16.dp)
    else -> RoundedCornerShape(0.dp)
}

private fun tabbarWrapperElevation(styleType: String): androidx.compose.ui.unit.Dp = when (styleType) {
    "card" -> 4.dp
    "glow" -> 3.dp
    "convex" -> 2.dp
    else -> 0.dp
}

private fun tabbarItemShape(styleType: String, itemShape: String): RoundedCornerShape = when (itemShape) {
    "round" -> RoundedCornerShape(50)
    "square" -> RoundedCornerShape(8.dp)
    else -> when (styleType) {
        "pill", "glow" -> RoundedCornerShape(50)
        "card" -> RoundedCornerShape(12.dp)
        else -> RoundedCornerShape(0.dp)
    }
}

private fun tabbarShadowElevation(value: String): androidx.compose.ui.unit.Dp {
    if (value.isBlank()) return 0.dp
    val values = Regex("[-+]?(?:\\d+(?:\\.\\d*)?|\\.\\d+)").findAll(value)
        .mapNotNull { it.value.toFloatOrNull() }
        .take(3)
        .toList()
    val vertical = values.getOrNull(1)?.let { kotlin.math.abs(it) } ?: 0f
    val blur = values.getOrNull(2)?.let { kotlin.math.abs(it) } ?: 0f
    return maxOf(vertical, blur / 2f).coerceIn(1f, 24f).dp
}

private fun Color.toHexString(): String = "#%02x%02x%02x".format(
    (red * 255).toInt().coerceIn(0, 255),
    (green * 255).toInt().coerceIn(0, 255),
    (blue * 255).toInt().coerceIn(0, 255),
)
