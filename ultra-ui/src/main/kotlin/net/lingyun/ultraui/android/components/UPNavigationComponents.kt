package net.lingyun.ultraui.android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import net.lingyun.ultraui.android.core.UPColor
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.core.report
import net.lingyun.ultraui.android.core.upClickable
import net.lingyun.ultraui.android.core.upDimension
import net.lingyun.ultraui.android.core.upTestTag

@Composable
public fun UPNavbar(
    props: UPNavbarProps = UPNavbarProps(),
    modifier: Modifier = Modifier,
    onLeftClick: (() -> Unit)? = null,
    onRightClick: (() -> Unit)? = null,
    onBack: (() -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    val height = upDimension(props.height, 44.dp)
    val background = UPColor.parse(props.bgColor, Color.White)
    val titleColor = UPColor.parse(props.titleColor, UPTheme.Main)
    val iconColor = UPColor.parse(props.leftIconColor, UPTheme.Main)
    val style = rememberUPResolvedStyle(props.customStyle, diagnostics, "UPNavbar")
    val titleStyle = rememberUPResolvedStyle(props.titleStyle, diagnostics, "UPNavbar.titleStyle")
    // `statusBarBgColor` is documented upstream as kept for compatibility only: the bar
    // paints `bgColor` across the status area as well. Report it so callers relying on a
    // separate status colour learn it has no effect rather than silently losing it.
    LaunchedEffect(props.statusBarBgColor, diagnostics) {
        if (props.statusBarBgColor.isNotEmpty()) {
            diagnostics.report(
                "UPNavbar",
                "statusBarBgColor",
                props.statusBarBgColor,
                "Upstream keeps this for compatibility only; bgColor paints the status area.",
            )
        }
    }
    // `autoBack` calls `uni.navigateBack()` upstream. Routing belongs to the host on
    // Android, so the flag decides whether the host's back action runs, the same way
    // `u-form`'s toast errorType defers to `onToast`.
    val leftClick: (() -> Unit)? = if (onLeftClick == null && !(props.autoBack && onBack != null)) {
        null
    } else {
        {
            onLeftClick?.invoke()
            if (props.autoBack) onBack?.invoke()
        }
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            // `.u-navbar--fixed` lifts the bar to `z-index: 11`; the host still owns the
            // actual `position: fixed` placement in its own layout.
            .then(if (props.fixed) Modifier.zIndex(UPNavbarFixedZIndex) else Modifier)
            .then(if (props.safeAreaInsetTop) Modifier.statusBarsPadding() else Modifier)
            .height(height)
            .background(background)
            // `:class="[border && 'u-border-bottom']"` on the content row.
            .then(if (props.border) Modifier.upBottomHairline() else Modifier)
            .applyUPResolvedStyle(style)
            .upTestTag("navbar"),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier
                .width(96.dp)
                .upTestTag("navbar-left")
                .upClickable(enabled = leftClick != null, onClick = { leftClick?.invoke() })
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            if (props.leftIcon.isNotEmpty()) UPIcon(UPIconProps(name = props.leftIcon, color = iconColor.toHex(), size = props.leftIconSize), diagnostics = diagnostics)
            if (props.leftText.isNotEmpty()) BasicText(props.leftText, style = TextStyle(color = iconColor))
        }
        Box(modifier = Modifier.weight(1f).width(upDimension(props.titleWidth, 200.dp)), contentAlignment = Alignment.Center) {
            BasicText(props.title, style = TextStyle(color = titleStyle.color ?: titleColor, fontSize = titleStyle.fontSize?.value?.sp ?: TextStyle.Default.fontSize))
        }
        Row(
            modifier = Modifier
                .width(96.dp)
                .upTestTag("navbar-right")
                .upClickable(enabled = onRightClick != null, onClick = { onRightClick?.invoke() })
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End,
        ) {
            if (props.rightText.isNotEmpty()) BasicText(props.rightText, style = TextStyle(color = UPTheme.Main))
            if (props.rightIcon.isNotEmpty()) UPIcon(UPIconProps(name = props.rightIcon, color = UPTheme.Main.toHex(), size = props.leftIconSize), diagnostics = diagnostics)
        }
    }
    if (props.placeholder) Spacer(modifier = Modifier.height(height))
}

@Composable
public fun UPNavbar(
    title: String = "",
    leftIcon: String = "arrow-left",
    rightText: String = "",
    modifier: Modifier = Modifier,
    onLeftClick: (() -> Unit)? = null,
    onRightClick: (() -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) = UPNavbar(
    props = UPNavbarProps(title = title, leftIcon = leftIcon, rightText = rightText),
    modifier = modifier,
    onLeftClick = onLeftClick,
    onRightClick = onRightClick,
    diagnostics = diagnostics,
)

@Composable
public fun UPNavbarMini(
    props: UPNavbarMiniProps = UPNavbarMiniProps(),
    modifier: Modifier = Modifier,
    onLeftClick: (() -> Unit)? = null,
    onHomeClick: (() -> Unit)? = null,
    onBack: (() -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    val style = rememberUPResolvedStyle(props.customStyle, diagnostics, "UPNavbarMini")
    Row(
        modifier = modifier
            .fillMaxWidth()
            // `.u-navbar-mini--fixed` floats the pill at `left: 20px; top: 10px` with
            // `z-index: 11`; the host still owns the actual fixed placement.
            .then(
                if (props.fixed) {
                    Modifier
                        .zIndex(UPNavbarFixedZIndex)
                        .offset(
                            x = UPNavbarMiniFixedStartOffsetDp.dp,
                            y = UPNavbarMiniFixedTopOffsetDp.dp,
                        )
                } else {
                    Modifier
                },
            )
            .then(if (props.safeAreaInsetTop) Modifier.statusBarsPadding() else Modifier)
            .height(upDimension(props.height, 32.dp))
            .background(UPColor.parse(props.bgColor, Color.Transparent))
            .applyUPResolvedStyle(style)
            .upTestTag("navbar-mini"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        UPIcon(
            UPIconProps(name = props.leftIcon, color = props.iconColor, size = props.iconSize),
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .upClickable(
                    onClick = {
                        onLeftClick?.invoke()
                        // `leftClick()` emits first, then calls `uni.navigateBack()`.
                        if (props.autoBack) onBack?.invoke()
                    },
                ),
            diagnostics = diagnostics,
        )
        if (props.homeUrl.isNotEmpty()) UPIcon(UPIconProps(name = "home", color = props.iconColor, size = props.iconSize), modifier = Modifier.upClickable(onClick = { onHomeClick?.invoke() }).padding(horizontal = 12.dp), diagnostics = diagnostics) else Spacer(Modifier.width(1.dp))
    }
}

@Composable
public fun UPStatusBar(props: UPStatusBarProps = UPStatusBarProps(), modifier: Modifier = Modifier, diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .then(if (props.height.toString() == "0") Modifier.statusBarsPadding() else Modifier.height(upDimension(props.height, 0.dp)))
            .background(UPColor.parse(props.bgColor, Color.Transparent))
            .applyUPResolvedStyle(rememberUPResolvedStyle(props.customStyle, diagnostics, "UPStatusBar"))
            .upTestTag("status-bar"),
    )
}

@Composable
public fun UPSafeBottom(props: UPSafeBottomProps = UPSafeBottomProps(), modifier: Modifier = Modifier, diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None) {
    Spacer(
        modifier = modifier
            .fillMaxWidth()
            .then(if (props.safeAreaInsetBottom) Modifier.navigationBarsPadding() else Modifier)
            .applyUPResolvedStyle(rememberUPResolvedStyle(props.customStyle, diagnostics, "UPSafeBottom"))
            .upTestTag("safe-bottom"),
    )
}

/** `.u-border-bottom`: a 0.5px hairline in the shared border colour. */
private fun Modifier.upBottomHairline(): Modifier = drawBehind {
    val stroke = 0.5.dp.toPx()
    drawLine(
        color = UPTheme.Border,
        start = Offset(0f, size.height - stroke / 2f),
        end = Offset(size.width, size.height - stroke / 2f),
        strokeWidth = stroke,
    )
}

private fun colorStyle(color: String): TextStyle = TextStyle(color = UPColor.parse(color, Color.White))

private fun Color.toHex(): String = "#%08X".format(toArgb())
