package net.lingyun.ultraui.android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.core.upClickable
import net.lingyun.ultraui.android.core.upTestTag

/** `areaList`: the 36 Chinese province plate prefixes, in upstream order. */
internal val upCarKeyboardAreaKeys: List<String> = listOf(
    "京", "沪", "粤", "津", "冀", "豫", "云", "辽", "黑", "湘",
    "皖", "鲁", "苏", "浙", "赣", "鄂", "桂", "甘", "晋", "陕",
    "蒙", "吉", "闽", "贵", "渝", "川", "青", "琼", "宁", "挂",
    "藏", "港", "澳", "新", "使", "学",
)

/** `engKeyBoardList`: digits then A–Z (upstream QWERTY-ish order). */
internal val upCarKeyboardEngKeys: List<String> = listOf(
    "1", "2", "3", "4", "5", "6", "7", "8", "9", "0",
    "Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P",
    "A", "S", "D", "F", "G", "H", "J", "K", "L",
    "Z", "X", "C", "V", "B", "N", "M",
)

/** Splits a flat key list into the 4 rows upstream slices: [0,10) [10,20) [20,30) [30,36). */
internal fun upCarKeyboardRows(keys: List<String>): List<List<String>> = listOf(
    keys.subList(0, minOf(10, keys.size)),
    keys.subList(minOf(10, keys.size), minOf(20, keys.size)),
    keys.subList(minOf(20, keys.size), minOf(30, keys.size)),
    keys.subList(minOf(30, keys.size), keys.size),
)

/**
 * Native Compose counterpart of uview-plus `u-car-keyboard`.
 *
 * A licence-plate keyboard toggling between the Chinese province prefixes (`areaList`) and the
 * English/number plate (`engKeyBoardList`). The fourth row carries a 中/英 mode toggle on the
 * left and a backspace on the right. `random` shuffles the active list; `autoChange` flips from
 * Chinese to English after a single Chinese key (upstream `sleep(200)`), reproduced with a
 * coroutine delay. `change` reports the tapped key, `backspace` the delete key.
 *
 * Difference: upstream repeats `backspace` every 250ms while held; the port fires once per tap.
 */
@Composable
public fun UPCarKeyboard(
    props: UPCarKeyboardProps = UPCarKeyboardProps(),
    modifier: Modifier = Modifier,
    onChange: ((String) -> Unit)? = null,
    onBackspace: (() -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    val style = rememberUPResolvedStyle(props.customStyle, diagnostics, "UPCarKeyboard")
    var abc by remember { mutableStateOf(false) }
    var autoSwitchToken by remember { mutableStateOf(0) }

    LaunchedEffect(autoSwitchToken) {
        if (autoSwitchToken > 0) {
            delay(200)
            abc = true
        }
    }

    val activeKeys = remember(abc, props.random) {
        val base = if (abc) upCarKeyboardEngKeys else upCarKeyboardAreaKeys
        if (props.random) base.shuffled() else base
    }
    val rows = remember(activeKeys) { upCarKeyboardRows(activeKeys) }
    val keyShape = RoundedCornerShape(4.dp)

    @Composable
    fun Key(scope: androidx.compose.foundation.layout.RowScope, label: String, tag: String, onClick: () -> Unit) {
        with(scope) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .padding(horizontal = 3.dp, vertical = 4.dp)
                    .clip(keyShape)
                    .background(Color.White, keyShape)
                    .upTestTag(tag)
                    .upClickable(onClick = onClick),
                contentAlignment = Alignment.Center,
            ) {
                BasicText(label, style = TextStyle(color = UPTheme.Main, fontSize = 16.sp))
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFFE0E4E6))
            .padding(horizontal = 5.dp, vertical = 6.dp)
            .applyUPResolvedStyle(style)
            .upTestTag("car-keyboard"),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        rows.forEachIndexed { rowIndex, row ->
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                // The last row leads with the 中/英 toggle.
                if (rowIndex == 3) {
                    Box(
                        modifier = Modifier
                            .width(60.dp)
                            .height(40.dp)
                            .padding(horizontal = 3.dp, vertical = 4.dp)
                            .clip(keyShape)
                            .background(UPTheme.Border, keyShape)
                            .upTestTag("car-keyboard-mode")
                            .upClickable(onClick = { abc = !abc }),
                        contentAlignment = Alignment.Center,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            BasicText("中", style = TextStyle(color = if (!abc) UPTheme.Primary else UPTheme.Main, fontSize = 16.sp))
                            BasicText("/", style = TextStyle(color = UPTheme.Main, fontSize = 15.sp), modifier = Modifier.padding(horizontal = 1.dp))
                            BasicText("英", style = TextStyle(color = if (abc) UPTheme.Primary else UPTheme.Main, fontSize = 16.sp))
                        }
                    }
                }
                row.forEach { label ->
                    Key(this, label, "car-keyboard-key-$label") {
                        onChange?.invoke(label)
                        if (!abc && props.autoChange) autoSwitchToken += 1
                    }
                }
                if (rowIndex == 3) {
                    Box(
                        modifier = Modifier
                            .width(60.dp)
                            .height(40.dp)
                            .padding(horizontal = 3.dp, vertical = 4.dp)
                            .clip(keyShape)
                            .background(UPTheme.Border, keyShape)
                            .upTestTag("car-keyboard-backspace")
                            .upClickable(onClick = { onBackspace?.invoke() }),
                        contentAlignment = Alignment.Center,
                    ) {
                        UPIcon(UPIconProps(name = "backspace", color = "#303133", size = 28), diagnostics = diagnostics)
                    }
                }
            }
        }
    }
}
