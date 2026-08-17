package net.lingyun.ultraui.android.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import net.lingyun.ultraui.android.core.UPColor
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPConfig
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.core.upClampPercentage
import net.lingyun.ultraui.android.core.upTestTag

private const val CircleProgressComponentName = "UPCircleProgress"

/** Native Compose counterpart of uview-plus `u-circle-progress`. */
@Composable
public fun UPCircleProgress(
    props: UPCircleProgressProps = UPCircleProgressProps(),
    modifier: Modifier = Modifier,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    val percentage = upClampPercentage(props.percentage, diagnostics, CircleProgressComponentName)
    val style = rememberUPResolvedStyle(props.customStyle, diagnostics, CircleProgressComponentName)
    val activeColor = UPTheme.Success
    val inactiveColor = UPColor.parse("rgb(200, 200, 200)", UPTheme.Border)
    val root = modifier
        .size(100.dp)
        .applyUPResolvedStyle(style)
        .semantics {
            progressBarRangeInfo = ProgressBarRangeInfo(percentage / 100f, 0f..1f, 0)
            contentDescription = "u-circle-progress: ${formatUPPercentage(percentage)}"
        }
        .upTestTag("circle-progress")

    Canvas(modifier = root) {
        val strokeWidth = 5.dp.toPx()
        val diameter = (size.minDimension - strokeWidth).coerceAtLeast(0f)
        val topLeft = Offset(
            x = (size.width - diameter) / 2f,
            y = (size.height - diameter) / 2f,
        )
        val arcSize = androidx.compose.ui.geometry.Size(diameter, diameter)
        drawArc(
            color = inactiveColor,
            startAngle = -90f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
        )
        if (percentage > 0f) {
            drawArc(
                color = activeColor,
                startAngle = -90f,
                sweepAngle = percentage * 3.6f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
            )
        }
    }
}

/** Direct argument form for generated Android source. */
@Composable
public fun UPCircleProgress(
    percentage: UPRawValue = UPConfig.circleProgress.percentage,
    customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
    modifier: Modifier = Modifier,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    UPCircleProgress(
        props = UPCircleProgressProps(
            percentage = percentage,
            customStyle = customStyle,
        ),
        modifier = modifier,
        diagnostics = diagnostics,
    )
}
