package net.lingyun.ultraui.android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.core.upClickable
import net.lingyun.ultraui.android.core.upTestTag

private const val NumberKeyboardBackspace = "backspace"

/**
 * `numList`: the ordered key labels for a keyboard configuration. `card` swaps the dot for `X`;
 * number mode drops the dot when `dotDisabled`. Matches the upstream computed exactly.
 */
internal fun upNumberKeyboardKeys(mode: String, dotDisabled: Boolean): List<String> = when {
    mode == "card" -> listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "X", "0")
    dotDisabled -> listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")
    else -> listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", ".", "0")
}

/**
 * `keyboardClick`: number mode with the dot enabled coerces plain digits to numbers; the dot and
 * the card `X` stay strings. Returns the value handed to the `change` event.
 */
internal fun upNumberKeyboardChangeValue(mode: String, dotDisabled: Boolean, key: String): Any =
    if (!dotDisabled && key != "." && key != "X") key.toInt() else key

private fun keyTag(label: String): String =
    if (label == NumberKeyboardBackspace) "number-keyboard-backspace" else "number-keyboard-key-$label"

/**
 * Native Compose counterpart of uview-plus `u-number-keyboard`.
 *
 * A three-column numeric keypad. `mode`/`dotDisabled` decide the key set (see
 * [upNumberKeyboardKeys]); `random` shuffles it once per composition. The trailing backspace key
 * is tinted grey. `change` reports the tapped value (digits become numbers unless the dot is
 * disabled), `backspace` fires on the delete key. When the dot is disabled in number mode the
 * `0` key spans two columns (`width: 464rpx`).
 *
 * Difference: upstream repeats `backspace` every 250ms while the key is held; Compose here fires
 * a single `backspace` per tap. The long-press repeat can be layered on by the host if needed.
 */
@Composable
public fun UPNumberKeyboard(
    props: UPNumberKeyboardProps = UPNumberKeyboardProps(),
    modifier: Modifier = Modifier,
    onChange: ((Any) -> Unit)? = null,
    onBackspace: (() -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    val style = rememberUPResolvedStyle(props.customStyle, diagnostics, "UPNumberKeyboard")
    val keys = remember(props.mode, props.dotDisabled, props.random) {
        val base = upNumberKeyboardKeys(props.mode, props.dotDisabled)
        if (props.random) base.shuffled() else base
    }
    val keyShape = RoundedCornerShape(4.dp)
    val cells = keys + NumberKeyboardBackspace
    val wideZero = props.mode == "number" && props.dotDisabled

    // Pack the cells into rows of three column-units; a wide `0` consumes two units.
    val rows = remember(cells, wideZero) {
        val out = mutableListOf<MutableList<Pair<String, Int>>>()
        var row = mutableListOf<Pair<String, Int>>()
        var used = 0
        for (label in cells) {
            val span = if (wideZero && label == "0") 2 else 1
            if (used + span > 3) {
                out += row
                row = mutableListOf()
                used = 0
            }
            row += label to span
            used += span
        }
        if (row.isNotEmpty()) out += row
        out
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFFE0E4E6))
            .padding(horizontal = 5.dp, vertical = 8.dp)
            .applyUPResolvedStyle(style)
            .upTestTag("number-keyboard"),
    ) {
        rows.forEach { row ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                row.forEach { (label, span) ->
                    val gray = label == NumberKeyboardBackspace
                    Box(
                        modifier = Modifier
                            .weight(span.toFloat())
                            .height(45.dp)
                            .padding(horizontal = 6.dp, vertical = 4.dp)
                            .clip(keyShape)
                            .background(if (gray) UPTheme.Border else Color.White, keyShape)
                            .upTestTag(keyTag(label))
                            .upClickable(onClick = {
                                if (label == NumberKeyboardBackspace) {
                                    onBackspace?.invoke()
                                } else {
                                    onChange?.invoke(upNumberKeyboardChangeValue(props.mode, props.dotDisabled, label))
                                }
                            }),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (label == NumberKeyboardBackspace) {
                            UPIcon(UPIconProps(name = "backspace", color = "#303133", size = 28), diagnostics = diagnostics)
                        } else {
                            BasicText(label, style = TextStyle(color = UPTheme.Main, fontSize = 20.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center))
                        }
                    }
                }
            }
        }
    }
}
