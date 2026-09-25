package net.lingyun.ultraui.android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.lingyun.ultraui.android.core.UPColor
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.core.upClickable
import net.lingyun.ultraui.android.core.upTestTag

/**
 * Native Compose counterpart of uview-plus `u-color-picker` (solid-colour mode).
 *
 * A bottom-sheet HSL picker: a saturation/lightness square (drag to set S from x and L from y), a
 * rainbow hue slider (drag to set H), a live preview swatch and the `commonColors` presets. The
 * chosen colour is fed back as `#rrggbb` through `update:modelValue` ([onUpdateModelValue]) and,
 * on confirm, `confirm` ([onConfirm]). `modelValue` seeds the initial HSL (via [upColorHexToHsl]).
 *
 * Difference: upstream also has a gradient tab (multi-stop editor); the port ships the solid-colour
 * picker here and leaves gradient authoring to a future/host surface.
 */
@Composable
public fun UPColorPicker(
    props: UPColorPickerProps = UPColorPickerProps(),
    modifier: Modifier = Modifier,
    onUpdateModelValue: ((String) -> Unit)? = null,
    onConfirm: ((String) -> Unit)? = null,
    onUpdateShow: ((Boolean) -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    val style = rememberUPResolvedStyle(props.customStyle, diagnostics, "UPColorPicker")
    val seed = remember(props.modelValue) { upColorHexToHsl(props.modelValue) }
    var hue by remember(props.modelValue) { mutableFloatStateOf(seed.h) }
    var sat by remember(props.modelValue) { mutableFloatStateOf(seed.s) }
    var light by remember(props.modelValue) { mutableFloatStateOf(seed.l) }
    var squareSize by remember { mutableStateOf(Offset(1f, 1f)) }
    var hueWidth by remember { mutableFloatStateOf(1f) }

    val current = upColorHslToHex(hue, sat, light)

    fun emit() { onUpdateModelValue?.invoke(upColorHslToHex(hue, sat, light)) }

    UPPopup(
        props = UPPopupProps(show = props.show, mode = "bottom", round = 10),
        modifier = modifier,
        onUpdateShow = onUpdateShow,
    ) {
        Column(Modifier.fillMaxWidth().padding(20.dp).applyUPResolvedStyle(style).upTestTag("color-picker")) {
            BasicText("选择颜色", modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp), style = TextStyle(color = UPTheme.Main, fontSize = 15.sp))

            // Saturation / lightness square.
            val hueColor = Color(android.graphics.Color.parseColor(upColorHslToHex(hue, 100f, 50f)))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Brush.horizontalGradient(listOf(Color.White, hueColor)))
                    .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black)))
                    .onGloballyPositioned { squareSize = Offset(it.size.width.toFloat(), it.size.height.toFloat()) }
                    .upTestTag("color-picker-square")
                    .pointerInput(Unit) {
                        detectTapGestures { pos ->
                            sat = (pos.x / squareSize.x * 100f).coerceIn(0f, 100f)
                            light = (100f - pos.y / squareSize.y * 100f).coerceIn(0f, 100f)
                            emit()
                        }
                    }
                    .pointerInput(Unit) {
                        detectDragGestures { change, _ ->
                            change.consume()
                            sat = (change.position.x / squareSize.x * 100f).coerceIn(0f, 100f)
                            light = (100f - change.position.y / squareSize.y * 100f).coerceIn(0f, 100f)
                            emit()
                        }
                    },
            )

            // Hue slider.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp)
                    .padding(top = 16.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.horizontalGradient(
                            (0..6).map { Color(android.graphics.Color.parseColor(upColorHslToHex(it * 60f, 100f, 50f))) },
                        ),
                    )
                    .onGloballyPositioned { hueWidth = it.size.width.toFloat() }
                    .upTestTag("color-picker-hue")
                    .pointerInput(Unit) {
                        detectTapGestures { pos -> hue = (pos.x / hueWidth * 360f).coerceIn(0f, 360f); emit() }
                    }
                    .pointerInput(Unit) {
                        detectDragGestures { change, _ ->
                            change.consume(); hue = (change.position.x / hueWidth * 360f).coerceIn(0f, 360f); emit()
                        }
                    },
            )

            // Preview + common colours.
            Row(modifier = Modifier.fillMaxWidth().padding(top = 16.dp), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Box(Modifier.size(32.dp).clip(RoundedCornerShape(6.dp)).background(Color(android.graphics.Color.parseColor(current))).border(0.5.dp, UPTheme.Border, RoundedCornerShape(6.dp)).upTestTag("color-picker-preview"))
                BasicText(current, modifier = Modifier.padding(start = 12.dp).upTestTag("color-picker-value"), style = TextStyle(color = UPTheme.Content, fontSize = 14.sp))
            }

            if (props.commonColors.isNotEmpty()) {
                Row(modifier = Modifier.fillMaxWidth().padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    props.commonColors.forEachIndexed { index, raw ->
                        val hex = raw.upStringValueOrEmpty()
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(UPColor.parse(hex, Color.Transparent))
                                .border(0.5.dp, UPTheme.Border, RoundedCornerShape(4.dp))
                                .upTestTag("color-picker-common-$index")
                                .upClickable(onClick = {
                                    val hsl = upColorHexToHsl(hex)
                                    hue = hsl.h; sat = hsl.s; light = hsl.l
                                    emit()
                                }),
                        )
                    }
                }
            }

            UPButton(
                UPButtonProps(text = "确认", type = "primary"),
                onClick = { onConfirm?.invoke(upColorHslToHex(hue, sat, light)) },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp).upTestTag("color-picker-confirm"),
                diagnostics = diagnostics,
            )
        }
    }
}
