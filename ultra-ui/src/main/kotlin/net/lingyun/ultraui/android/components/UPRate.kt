package net.lingyun.ultraui.android.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.core.asFiniteFloatOrNull
import net.lingyun.ultraui.android.core.resolveUPModelValue
import net.lingyun.ultraui.android.core.report
import net.lingyun.ultraui.android.core.upClickable
import net.lingyun.ultraui.android.core.upIntOrDefault
import net.lingyun.ultraui.android.core.upTestTag

private const val RateComponentName = "UPRate"

/** Native Compose counterpart of uview-plus `u-rate`. */
@Composable
public fun UPRate(
    props: UPRateProps = UPRateProps(),
    onInput: ((Float) -> Unit)? = null,
    onChange: ((Float) -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
    modifier: Modifier = Modifier,
) {
    val count = props.count.upIntOrDefault(5).coerceIn(0, 100)
    val minimum = props.minCount.asFiniteFloatOrNull()?.coerceAtLeast(0f) ?: 1f
    val rawExternal = resolveUPModelValue(props.modelValue, props.value)
    val external = rawExternal.asFiniteFloatOrNull()
        ?: run {
            diagnostics.report(RateComponentName, "modelValue", rawExternal, "Malformed rating; using minCount.")
            minimum
        }
    var current by remember { mutableStateOf(external) }
    LaunchedEffect(external) { current = external }

    val effectiveMinimum = minimum.coerceIn(0f, count.toFloat())
    val value = current.coerceIn(0f, count.toFloat())
    val size = selectionDimension(props.size, 18.dp)
    val gutter = selectionDimension(props.gutter, 4.dp)
    val activeColor = props.activeColor.takeIf { it.isNotBlank() } ?: "#ff9f0a"
    val inactiveColor = props.inactiveColor.takeIf { it.isNotBlank() } ?: "#c8c9cc"

    Row(
        modifier = modifier.upTestTag("rate"),
    ) {
        repeat(count) { index ->
            val position = index + 1f
            val fraction = (value - index).coerceIn(0f, 1f)
            Box(
                modifier = Modifier
                    .width(size)
                    .clip(RectangleShape)
                    .upTestTag("rate-item-$index")
                    .upClickable(
                        enabled = !props.disabled && !props.readonly && props.touchable,
                        role = androidx.compose.ui.semantics.Role.Button,
                    ) {
                        val next = if (props.allowHalf && fraction > 0f && fraction < 1f) {
                            index + 0.5f
                        } else {
                            position
                        }.coerceAtLeast(effectiveMinimum)
                        if (!props.allowHalf && next % 1f != 0f) return@upClickable
                        current = next
                        onInput?.invoke(next)
                        onChange?.invoke(next)
                    },
            ) {
                RateIcon(
                    name = props.inactiveIcon,
                    color = inactiveColor,
                    size = size,
                    diagnostics = diagnostics,
                    modifier = Modifier.upTestTag("rate-inactive-$index"),
                )
                if (fraction >= 1f || (props.allowHalf && fraction >= 0.5f)) {
                    if (props.allowHalf && fraction < 1f) {
                        Box(
                            modifier = Modifier
                                .width(size * 0.5f)
                                .clip(RectangleShape),
                        ) {
                            RateIcon(
                                name = props.activeIcon,
                                color = activeColor,
                                size = size,
                                diagnostics = diagnostics,
                                modifier = Modifier.upTestTag("rate-active-half-$index"),
                            )
                        }
                    } else {
                        RateIcon(
                            name = props.activeIcon,
                            color = activeColor,
                            size = size,
                            diagnostics = diagnostics,
                            modifier = Modifier.upTestTag("rate-active-$index"),
                        )
                    }
                }
            }
            if (index != count - 1) {
                androidx.compose.foundation.layout.Spacer(Modifier.width(gutter))
            }
        }
    }
}

/** Direct value form for generated Compose source. */
@Composable
public fun UPRate(
    value: Float,
    modifier: Modifier = Modifier,
    onInput: ((Float) -> Unit)? = null,
    onChange: ((Float) -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    UPRate(
        props = UPRateProps(modelValue = value, value = value),
        onInput = onInput,
        onChange = onChange,
        diagnostics = diagnostics,
        modifier = modifier,
    )
}

@Composable
private fun RateIcon(
    name: String,
    color: String,
    size: androidx.compose.ui.unit.Dp,
    diagnostics: UPCompatibilityDiagnostics,
    modifier: Modifier,
) {
    UPIcon(
        props = UPIconProps(name = name, color = color, size = size.value),
        modifier = modifier,
        diagnostics = diagnostics,
    )
}
