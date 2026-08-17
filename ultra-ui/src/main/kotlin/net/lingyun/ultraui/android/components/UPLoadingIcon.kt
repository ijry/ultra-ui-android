package net.lingyun.ultraui.android.components

import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.roundToInt
import net.lingyun.ultraui.android.core.UPColor
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPCompatibilityEvent
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.core.UPUnit

private const val LoadingIconComponentName: String = "UPLoadingIcon"
private const val DefaultLoadingDurationMillis: Int = 1200

private data class UPLoadingIconResolution(
    val mode: String,
    val durationMillis: Int,
    val timingEasing: Easing,
    val diagnostics: List<UPCompatibilityEvent>,
)

/** Native Compose counterpart of uview-plus `u-loading-icon`. */
@Composable
public fun UPLoadingIcon(
    props: UPLoadingIconProps = UPLoadingIconProps(),
    modifier: Modifier = Modifier,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    if (!props.show) return

    val resolution = remember(props) { resolveLoadingIcon(props) }
    val style = rememberUPResolvedStyle(props.customStyle, diagnostics, LoadingIconComponentName)
    val screenWidth = availableLoadingScreenWidth()
    val size = UPUnit.toDp(props.size, screenWidth, 24.dp).coerceAtLeast(0.dp)
    val textSize = UPUnit.toDp(props.textSize, screenWidth, 15.dp).coerceAtLeast(0.dp)
    val activeColor = UPColor.parse(props.color, UPTheme.Tips)
    val textColor = UPColor.parse(props.textColor, UPTheme.Tips)
    val inactiveColor = UPColor.parse(props.inactiveColor, lightenTowardWhite(activeColor, 0.8f))
    val text = props.text.displayLoadingTextOrNull()
    val transition = rememberInfiniteTransition(label = "u-loading-icon")
    val rotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = resolution.durationMillis,
                easing = if (resolution.mode == "spinner") TwelveStepEasing else resolution.timingEasing,
            ),
            repeatMode = RepeatMode.Restart,
        ),
        label = "u-loading-icon-rotation",
    )

    LaunchedEffect(resolution.diagnostics, diagnostics) {
        resolution.diagnostics.forEach(diagnostics::report)
    }

    val rootModifier = modifier
        .applyUPResolvedStyle(style)
        .testTag("up-loading-icon")
        .semantics { contentDescription = "u-loading-icon: ${resolution.mode}" }

    @Composable
    fun Spinner() {
        UPLoadingGlyph(
            mode = resolution.mode,
            size = size,
            rotation = rotation,
            activeColor = activeColor,
            inactiveColor = inactiveColor,
        )
    }

    @Composable
    fun LoadingText() {
        if (text != null) {
            BasicText(
                text = text,
                style = TextStyle(
                    color = textColor,
                    fontSize = textSize.value.sp,
                    lineHeight = textSize.value.sp,
                ),
                modifier = Modifier.testTag("up-loading-icon-text"),
            )
        }
    }

    if (props.vertical) {
        Column(
            modifier = rootModifier,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spinner()
            if (text != null) Spacer(modifier = Modifier.height(6.dp))
            LoadingText()
        }
    } else {
        Row(
            modifier = rootModifier,
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spinner()
            if (text != null) Spacer(modifier = Modifier.width(4.dp))
            LoadingText()
        }
    }
}

/** Direct argument form for source generators that do not construct Props values first. */
@Composable
public fun UPLoadingIcon(
    show: Boolean,
    color: String = "#909399",
    textColor: String = "#909399",
    vertical: Boolean = false,
    mode: String = "spinner",
    size: UPRawValue = 24,
    textSize: UPRawValue = 15,
    text: UPRawValue = "",
    timingFunction: String = "ease-in-out",
    duration: UPRawValue = DefaultLoadingDurationMillis,
    inactiveColor: String = "",
    customStyle: net.lingyun.ultraui.android.core.UPStyleInput = emptyMap<String, UPRawValue>(),
    modifier: Modifier = Modifier,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    UPLoadingIcon(
        props = UPLoadingIconProps(
            show = show,
            color = color,
            textColor = textColor,
            vertical = vertical,
            mode = mode,
            size = size,
            textSize = textSize,
            text = text,
            timingFunction = timingFunction,
            duration = duration,
            inactiveColor = inactiveColor,
            customStyle = customStyle,
        ),
        modifier = modifier,
        diagnostics = diagnostics,
    )
}

@Composable
private fun UPLoadingGlyph(
    mode: String,
    size: androidx.compose.ui.unit.Dp,
    rotation: Float,
    activeColor: Color,
    inactiveColor: Color,
) {
    Canvas(
        modifier = Modifier
            .size(size)
            .testTag("up-loading-icon-spinner"),
    ) {
        when (mode) {
            "circle" -> drawCircleLoading(rotation, activeColor, inactiveColor)
            "semicircle" -> drawSemicircleLoading(rotation, activeColor)
            else -> drawSpinnerLoading(rotation, activeColor)
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawSpinnerLoading(rotation: Float, color: Color) {
    val radius = min(size.width, size.height) / 2f
    val stroke = (radius * 0.22f).coerceAtLeast(1f)
    rotate(degrees = rotation, pivot = center) {
        repeat(12) { index ->
            rotate(degrees = index * 30f, pivot = center) {
                drawLine(
                    color = color.copy(alpha = (1f - 0.0625f * index).coerceIn(0.25f, 1f)),
                    start = Offset(center.x, center.y - radius * 0.82f),
                    end = Offset(center.x, center.y - radius * 0.48f),
                    strokeWidth = stroke,
                    cap = StrokeCap.Round,
                )
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawSemicircleLoading(rotation: Float, color: Color) {
    val stroke = 2.dp.toPx()
    rotate(degrees = rotation, pivot = center) {
        drawArc(
            color = color,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            style = Stroke(width = stroke, cap = StrokeCap.Round),
        )
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawCircleLoading(
    rotation: Float,
    activeColor: Color,
    inactiveColor: Color,
) {
    val stroke = 2.dp.toPx()
    drawArc(
        color = inactiveColor,
        startAngle = 0f,
        sweepAngle = 360f,
        useCenter = false,
        style = Stroke(width = stroke),
    )
    rotate(degrees = rotation, pivot = center) {
        drawArc(
            color = activeColor,
            startAngle = -90f,
            sweepAngle = 90f,
            useCenter = false,
            style = Stroke(width = stroke, cap = StrokeCap.Round),
        )
    }
}

private fun resolveLoadingIcon(props: UPLoadingIconProps): UPLoadingIconResolution {
    val events = mutableListOf<UPCompatibilityEvent>()
    val mode = props.mode.trim().lowercase().let { requested ->
        if (requested in LoadingIconModes) {
            requested
        } else {
            events += UPCompatibilityEvent(
                component = LoadingIconComponentName,
                property = "mode",
                value = props.mode,
                reason = "Unsupported loading mode; using spinner.",
            )
            "spinner"
        }
    }
    val durationMillis = props.duration.durationMillisOrNull()
    val resolvedDuration = if (durationMillis == null) {
        events += UPCompatibilityEvent(
            component = LoadingIconComponentName,
            property = "duration",
            value = props.duration,
            reason = "Malformed duration; using 1200ms.",
        )
        DefaultLoadingDurationMillis
    } else {
        durationMillis
    }
    val timingEasing = resolveTimingEasing(props.timingFunction).also { result ->
        if (!result.supported) {
            events += UPCompatibilityEvent(
                component = LoadingIconComponentName,
                property = "timingFunction",
                value = props.timingFunction,
                reason = "Unsupported timingFunction; using ease-in-out.",
            )
        }
    }.easing
    return UPLoadingIconResolution(
        mode = mode,
        durationMillis = resolvedDuration,
        timingEasing = timingEasing,
        diagnostics = events,
    )
}

private data class TimingEasingResolution(val easing: Easing, val supported: Boolean)

private fun resolveTimingEasing(value: String): TimingEasingResolution = when (value.trim().lowercase()) {
    "linear" -> TimingEasingResolution(LinearEasing, supported = true)
    "ease-in", "easein" -> TimingEasingResolution(FastOutLinearInEasing, supported = true)
    "ease-out", "easeout" -> TimingEasingResolution(LinearOutSlowInEasing, supported = true)
    "ease-in-out", "easeinout", "" -> TimingEasingResolution(FastOutSlowInEasing, supported = true)
    else -> TimingEasingResolution(FastOutSlowInEasing, supported = false)
}

private fun UPRawValue.durationMillisOrNull(): Int? {
    val value = when (this) {
        is Number -> toDouble()
        is String -> trim().toDoubleOrNull()
        else -> null
    } ?: return null
    return value.takeIf { it.isFinite() && it > 0.0 }
        ?.coerceAtMost(Int.MAX_VALUE.toDouble())
        ?.roundToInt()
}

private fun UPRawValue.displayLoadingTextOrNull(): String? = when (this) {
    null -> null
    else -> toString().takeIf(String::isNotEmpty)
}

private fun lightenTowardWhite(color: Color, amount: Float): Color {
    val progress = amount.coerceIn(0f, 1f)
    return Color(
        red = color.red + (1f - color.red) * progress,
        green = color.green + (1f - color.green) * progress,
        blue = color.blue + (1f - color.blue) * progress,
        alpha = color.alpha,
    )
}

@Composable
private fun availableLoadingScreenWidth(): androidx.compose.ui.unit.Dp = androidx.compose.ui.platform.LocalConfiguration.current.screenWidthDp
    .takeIf { it > 0 }
    ?.dp
    ?: 750.dp

private val LoadingIconModes: Set<String> = setOf("spinner", "semicircle", "circle")
private val TwelveStepEasing: Easing = Easing { fraction -> floor(fraction.coerceIn(0f, 1f) * 12f) / 12f }
