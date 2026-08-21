package net.lingyun.ultraui.android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.lingyun.ultraui.android.core.UPColor
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.core.upTestTag
import kotlin.math.abs

@Composable
public fun UPSlider(
    props: UPSliderProps = UPSliderProps(),
    modifier: Modifier = Modifier,
    onUpdateValue: ((UPRawValue) -> Unit)? = null,
    onUpdateModelValue: ((UPRawValue) -> Unit)? = null,
    onUpdateRangeValue: ((List<UPRawValue>) -> Unit)? = null,
    onChanging: ((UPSliderEvent) -> Unit)? = null,
    onChange: ((UPSliderEvent) -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    val min = props.min.rawFloat(0f)
    val max = props.max.rawFloat(100f).coerceAtLeast(min)
    val step = props.step.rawFloat(1f)
    val range = props.isRange
    var values by remember(props.value, props.modelValue, props.isRange, props.rangeValue, min, max, step) {
        mutableStateOf(resolveSliderValues(props, min, max, step))
    }
    var layoutSize by remember { mutableStateOf(IntSize.Zero) }
    LaunchedEffect(props.value, props.modelValue, props.isRange, props.rangeValue, min, max, step) {
        values = resolveSliderValues(props, min, max, step)
    }

    fun update(position: Offset, finished: Boolean) {
        if (props.disabled || layoutSize == IntSize.Zero) return
        val fraction = sliderPositionFraction(position, layoutSize, props.vertical)
        val next = quantizeSliderValue(min + fraction * (max - min), min, max, step)
        values = if (range) {
            val index = if (abs(next - values[0]) <= abs(next - values[1])) 0 else 1
            values.toMutableList().also { it[index] = next }.sorted()
        } else listOf(next)
        val event = sliderEvent(values, range)
        if (range) {
            onUpdateRangeValue?.invoke(values)
            onUpdateModelValue?.invoke(values)
        } else {
            onUpdateValue?.invoke(values[0])
            onUpdateModelValue?.invoke(values[0])
        }
        onChanging?.invoke(event)
        if (finished) onChange?.invoke(event)
    }

    val thumbStartFraction = ((values.first() - min) / ((max - min).takeIf { it > 0f } ?: 1f)).coerceIn(0f, 1f)
    val thumbEndFraction = ((values.last() - min) / ((max - min).takeIf { it > 0f } ?: 1f)).coerceIn(0f, 1f)
    val (trackStartFraction, trackEndFraction) = sliderTrackFractions(values, min, max, range)
    val active = UPColor.parse(props.activeColor, UPTheme.Primary)
    val inactive = UPColor.parse(props.inactiveColor, UPTheme.Border)
    val block = UPColor.parse(props.blockColor, Color.White)
    val blockSize = props.blockSize.rawFloat(18f).dp
    val trackHeight = props.size.rawFloat(2f).coerceAtLeast(1f).dp
    val vertical = props.vertical

    BoxWithConstraints(
        modifier = modifier
            .then(if (vertical) Modifier.width(blockSize + 20.dp).height(220.dp) else Modifier.fillMaxWidth().height(40.dp))
            .onSizeChanged { layoutSize = it }
            .pointerInput(props.disabled, min, max, step, range) { detectTapGestures { update(it, true) } }
            .pointerInput(props.disabled, min, max, step, range) {
                detectDragGestures(
                    onDragEnd = { if (values.isNotEmpty()) onChange?.invoke(sliderEvent(values, range)) },
                    onDrag = { change, _ -> update(change.position, false) },
                )
            }
            .applyUPResolvedStyle(rememberUPResolvedStyle(props.customStyle, diagnostics, "UPSlider"))
            .upTestTag("slider"),
        contentAlignment = if (vertical) Alignment.BottomCenter else Alignment.CenterStart,
    ) {
        val trackLength = if (vertical) 220.dp else maxWidth
        Box(
            (if (vertical) Modifier.fillMaxHeight().width(trackHeight) else Modifier.fillMaxWidth().height(trackHeight))
                .background(inactive),
        )
        Box(
            if (vertical) {
                Modifier.offset(y = sliderAxisOffset(trackEndFraction, trackLength.value, vertical = true).dp)
                    .width(trackHeight)
                    .height(trackLength * (trackEndFraction - trackStartFraction))
                    .align(Alignment.TopCenter)
            } else {
                Modifier.offset(x = maxWidth * trackStartFraction)
                    .width(maxWidth * (trackEndFraction - trackStartFraction))
                    .height(trackHeight)
                    .align(Alignment.CenterStart)
            }
                .background(active),
        )
        SliderThumb(thumbStartFraction, trackLength, blockSize, block, values.first(), props.showValue, "slider-thumb-start", vertical)
        if (range) SliderThumb(thumbEndFraction, trackLength, blockSize, block, values.last(), props.showValue, "slider-thumb-end", vertical)
    }
}

@Composable
private fun androidx.compose.foundation.layout.BoxScope.SliderThumb(
    fraction: Float,
    trackWidth: androidx.compose.ui.unit.Dp,
    size: androidx.compose.ui.unit.Dp,
    color: Color,
    value: Float,
    showValue: Boolean,
    tag: String,
    vertical: Boolean,
) {
    Box(
        Modifier.then(
            if (vertical) {
                Modifier.align(Alignment.TopCenter)
                    .offset(
                        y = (sliderAxisOffset(fraction, trackWidth.value, vertical = true).dp - size / 2)
                            .coerceIn(0.dp, (trackWidth - size).coerceAtLeast(0.dp)),
                    )
            } else {
                Modifier.align(Alignment.CenterStart)
                    .offset(
                        x = (sliderAxisOffset(fraction, trackWidth.value, vertical = false).dp - size / 2)
                            .coerceIn(0.dp, (trackWidth - size).coerceAtLeast(0.dp)),
                    )
            },
        )
            .size(size).background(color, CircleShape).upTestTag(tag),
        contentAlignment = Alignment.Center,
    ) {
        if (showValue) BasicText(value.toString().removeSuffix(".0"), style = TextStyle(color = UPTheme.Main, fontSize = 9.sp))
    }
}

private fun sliderEvent(values: List<Float>, range: Boolean): UPSliderEvent =
    if (range) UPSliderEvent(values, values) else UPSliderEvent(values.firstOrNull() ?: 0f)
