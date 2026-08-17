package net.lingyun.ultraui.android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.lingyun.ultraui.android.core.UPColor
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPConfig
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.core.upClampPercentage
import net.lingyun.ultraui.android.core.upTestTag

private const val LineProgressComponentName = "UPLineProgress"

/** Native Compose counterpart of uview-plus `u-line-progress`. */
@Composable
public fun UPLineProgress(
    props: UPLineProgressProps = UPLineProgressProps(),
    modifier: Modifier = Modifier,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    val percentage = upClampPercentage(props.percentage, diagnostics, LineProgressComponentName)
    val fraction = percentage / 100f
    val height = upRawDp(props.height, 12.dp).coerceAtLeast(1.dp)
    val activeColor = UPColor.parse(props.activeColor, UPTheme.Success)
    val inactiveColor = UPColor.parse(props.inactiveColor, UPTheme.Border)
    val shape = RoundedCornerShape(percent = 50)
    val style = rememberUPResolvedStyle(props.customStyle, diagnostics, LineProgressComponentName)
    val root = modifier
        .fillMaxWidth()
        .height(height)
        .applyUPResolvedStyle(style)
        .semantics {
            progressBarRangeInfo = ProgressBarRangeInfo(fraction, 0f..1f, 0)
        }
        .upTestTag("line-progress")

    Box(modifier = root) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .clip(shape)
                .background(inactiveColor)
                .upTestTag("line-progress-inactive"),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth(fraction)
                .fillMaxHeight()
                .align(if (props.fromRight) Alignment.CenterEnd else Alignment.CenterStart)
                .clip(shape)
                .background(activeColor)
                .upTestTag("line-progress-active"),
            contentAlignment = Alignment.CenterEnd,
        ) {
            if (props.showText && percentage >= 10f) {
                BasicText(
                    text = formatUPPercentage(percentage),
                    modifier = Modifier
                        .padding(end = 5.dp)
                        .upTestTag("line-progress-text"),
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 10.sp,
                        lineHeight = 10.sp,
                    ),
                )
            }
        }
    }
}

/** Direct argument form for generated Android source. */
@Composable
public fun UPLineProgress(
    activeColor: String = UPConfig.lineProgress.activeColor,
    inactiveColor: String = UPConfig.lineProgress.inactiveColor,
    percentage: UPRawValue = UPConfig.lineProgress.percentage,
    showText: Boolean = UPConfig.lineProgress.showText,
    height: UPRawValue = UPConfig.lineProgress.height,
    fromRight: Boolean = UPConfig.lineProgress.fromRight,
    customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
    modifier: Modifier = Modifier,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    UPLineProgress(
        props = UPLineProgressProps(
            activeColor = activeColor,
            inactiveColor = inactiveColor,
            percentage = percentage,
            showText = showText,
            height = height,
            fromRight = fromRight,
            customStyle = customStyle,
        ),
        modifier = modifier,
        diagnostics = diagnostics,
    )
}
