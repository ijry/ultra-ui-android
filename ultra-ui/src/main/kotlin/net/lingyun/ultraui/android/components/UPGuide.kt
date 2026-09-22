package net.lingyun.ultraui.android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import net.lingyun.ultraui.android.core.UPColor
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.core.asFiniteFloatOrNull
import net.lingyun.ultraui.android.core.upClickable
import net.lingyun.ultraui.android.core.upTestTag

/**
 * Native Compose counterpart of uview-plus `up-guide`.
 *
 * A full-screen onboarding overlay. Each `list` page shows its image (or a placeholder), a title
 * and a description over `backgroundColor ?: bgColor`. Dots (`indicator`) track the page; the
 * primary button advances (`nextText`) until the last page then finishes (`finishText`, emitting
 * `finish` + closing), and `skip` (`showSkip`) closes early. Page changes emit `change`, closes
 * emit `update:show`.
 *
 * Difference: upstream remembers a dismissed guide across launches via local storage
 * (`once`/`storageKey`); persistence belongs to the host on Android, so the port keeps the flags
 * in the contract, drives visibility from `show`, and reports through the callbacks for the host
 * to persist. `zIndex` orders it above sibling content within the same parent.
 */
@Composable
public fun UPGuide(
    props: UPGuideProps = UPGuideProps(),
    modifier: Modifier = Modifier,
    onUpdateShow: ((Boolean) -> Unit)? = null,
    onChange: ((Int) -> Unit)? = null,
    onSkip: (() -> Unit)? = null,
    onFinish: (() -> Unit)? = null,
    onClose: (() -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    if (!props.show || props.list.isEmpty()) return
    val style = rememberUPResolvedStyle(props.customStyle, diagnostics, "UPGuide")
    var current by remember { mutableIntStateOf(0) }
    val last = props.list.size - 1
    val page = props.list[current.coerceIn(0, last)].upStringKeyMapOrEmpty()

    fun close() {
        onUpdateShow?.invoke(false)
        onClose?.invoke()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .zIndex(props.zIndex.asFiniteFloatOrNull() ?: 10075f)
            .background(UPColor.parse(page["backgroundColor"].upStringValueOrEmpty().ifEmpty { props.bgColor }, Color(0xFF111111)))
            .applyUPResolvedStyle(style)
            .upTestTag("guide"),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().weight(1f).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            val image = page["image"].upStringValueOrEmpty()
            if (image.isNotEmpty()) {
                UPImage(UPImageProps(src = image, mode = "aspectFit", width = "200", height = "200"), diagnostics = diagnostics)
            } else {
                BasicText("暂无引导图", modifier = Modifier.upTestTag("guide-placeholder"), style = TextStyle(color = Color.White, fontSize = 14.sp))
            }
            page["title"].upStringValueOrEmpty().takeIf { it.isNotEmpty() }?.let {
                BasicText(it, modifier = Modifier.padding(top = 20.dp), style = TextStyle(color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center))
            }
            page["desc"].upStringValueOrEmpty().takeIf { it.isNotEmpty() }?.let {
                BasicText(it, modifier = Modifier.padding(top = 12.dp), style = TextStyle(color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp, textAlign = TextAlign.Center))
            }
        }

        Column(modifier = Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            if (props.indicator) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(bottom = 16.dp).upTestTag("guide-dots")) {
                    props.list.indices.forEach { i ->
                        Box(
                            modifier = Modifier
                                .size(if (i == current) 16.dp else 8.dp, 8.dp)
                                .clip(CircleShape)
                                .background(if (i == current) UPTheme.Primary else Color.White.copy(alpha = 0.4f)),
                        )
                    }
                }
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                if (props.showSkip) {
                    Box(
                        modifier = Modifier.weight(1f).clip(RoundedCornerShape(8.dp)).background(Color.White.copy(alpha = 0.15f)).padding(vertical = 12.dp).upTestTag("guide-skip").upClickable(onClick = { onSkip?.invoke(); close() }),
                        contentAlignment = Alignment.Center,
                    ) {
                        BasicText(props.skipText, style = TextStyle(color = Color.White, fontSize = 15.sp))
                    }
                }
                Box(
                    modifier = Modifier.weight(1f).clip(RoundedCornerShape(8.dp)).background(UPTheme.Primary).padding(vertical = 12.dp).upTestTag("guide-primary").upClickable(onClick = {
                        if (current >= last) {
                            onFinish?.invoke()
                            close()
                        } else {
                            current += 1
                            onChange?.invoke(current)
                        }
                    }),
                    contentAlignment = Alignment.Center,
                ) {
                    BasicText(if (current >= last) props.finishText else props.nextText, style = TextStyle(color = Color.White, fontSize = 15.sp))
                }
            }
        }
    }
}
