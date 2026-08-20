package net.lingyun.ultraui.android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import net.lingyun.ultraui.android.core.UPColor
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.core.upClickable
import net.lingyun.ultraui.android.core.upDoubleOrDefault
import net.lingyun.ultraui.android.core.upIntOrDefault
import net.lingyun.ultraui.android.core.upTestTag

@Composable
public fun UPSwiper(props: UPSwiperProps = UPSwiperProps(), modifier: Modifier = Modifier, onChange: ((Int) -> Unit)? = null, onClick: ((Int) -> Unit)? = null, onUpdateCurrent: ((Int) -> Unit)? = null, diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None) {
    var current by remember { mutableStateOf(props.current.upIntOrDefault(0).coerceAtLeast(0)) }
    LaunchedEffect(props.current) { current = props.current.upIntOrDefault(0).coerceAtLeast(0) }
    val list = props.list
    if (list.isEmpty()) return
    current = current.coerceIn(0, list.lastIndex)
    val item = list[current]
    val label = actionOrOptionText(item, props.keyName, item.upStringValueOrEmpty())
    Column(modifier.fillMaxWidth().background(UPColor.parse(props.bgColor, Color(0xFFF3F4F6))).applyUPResolvedStyle(rememberUPResolvedStyle(props.customStyle, diagnostics, "UPSwiper")).upTestTag("swiper")) {
        Box(Modifier.fillMaxWidth().height(net.lingyun.ultraui.android.core.upDimension(props.height, 130.dp)).upClickable(onClick = { onClick?.invoke(current) }), contentAlignment = Alignment.Center) { BasicText(label, style = TextStyle(color = UPTheme.Main)) }
        if (props.indicator) UPSwiperIndicator(UPSwiperIndicatorProps(length = list.size, current = current, indicatorActiveColor = props.indicatorActiveColor, indicatorInactiveColor = props.indicatorInactiveColor, indicatorMode = props.indicatorMode), onClick = { next -> current = next; onChange?.invoke(next); onUpdateCurrent?.invoke(next) })
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            if (current > 0) BasicText("‹", modifier = Modifier.upClickable(onClick = { current--; onChange?.invoke(current); onUpdateCurrent?.invoke(current) }).padding(12.dp), style = TextStyle(fontSize = 22.sp))
            if (current < list.lastIndex) BasicText("›", modifier = Modifier.upClickable(onClick = { current++; onChange?.invoke(current); onUpdateCurrent?.invoke(current) }).padding(12.dp), style = TextStyle(fontSize = 22.sp))
        }
    }
}

@Composable
public fun UPSwiper(list: List<UPRawValue>, modifier: Modifier = Modifier, current: Int = 0, onChange: ((Int) -> Unit)? = null) = UPSwiper(UPSwiperProps(list = list, current = current), modifier, onChange)

@Composable
public fun UPSwiperIndicator(props: UPSwiperIndicatorProps = UPSwiperIndicatorProps(), modifier: Modifier = Modifier, onClick: ((Int) -> Unit)? = null, diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None) {
    val count = props.length.upIntOrDefault(0).coerceAtLeast(0)
    val current = props.current.upIntOrDefault(0).coerceIn(0, (count - 1).coerceAtLeast(0))
    Row(modifier.fillMaxWidth().padding(6.dp).upTestTag("swiper-indicator"), horizontalArrangement = Arrangement.Center) {
        repeat(count) { index ->
            Box(Modifier.padding(3.dp).size(if (props.indicatorMode == "line") 18.dp else 7.dp, 7.dp).background(UPColor.parse(if (index == current) props.indicatorActiveColor else props.indicatorInactiveColor, if (index == current) Color.White else Color.LightGray)).upClickable(onClick = { onClick?.invoke(index) }))
        }
    }
}

@Composable
public fun UPSkeleton(props: UPSkeletonProps = UPSkeletonProps(), modifier: Modifier = Modifier, diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None, content: @Composable () -> Unit = {}) {
    if (!props.loading) { Box(modifier.upTestTag("skeleton-content")) { content() }; return }
    val rows = props.rows.upIntOrDefault(0).coerceAtLeast(0)
    Column(modifier.fillMaxWidth().applyUPResolvedStyle(rememberUPResolvedStyle(props.customStyle, diagnostics, "UPSkeleton")).upTestTag("skeleton"), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
            if (props.avatar) Box(Modifier.size(net.lingyun.ultraui.android.core.upDimension(props.avatarSize, 32.dp)).background(Color(0xFFE6E8EB)))
            if (props.title) Box(Modifier.size(net.lingyun.ultraui.android.core.upDimension(props.titleWidth, 120.dp), net.lingyun.ultraui.android.core.upDimension(props.titleHeight, 18.dp)).background(Color(0xFFE6E8EB)))
        }
        repeat(if (rows == 0) 3 else rows) { index -> Box(Modifier.fillMaxWidth(if (index == rows - 1 && rows > 1) .7f else 1f).height(net.lingyun.ultraui.android.core.upDimension(props.rowsHeight, 18.dp)).background(Color(0xFFE6E8EB)).alpha(if (props.animate) .8f else 1f)) }
    }
}

@Composable
public fun UPReadMore(props: UPReadMoreProps = UPReadMoreProps(), modifier: Modifier = Modifier, onOpen: ((UPRawValue) -> Unit)? = null, onClose: ((UPRawValue) -> Unit)? = null, onUpdateModelValue: ((Boolean) -> Unit)? = null, diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None, content: @Composable () -> Unit) {
    var open by remember(props) { mutableStateOf(resolveReadMoreOpen(props)) }
    LaunchedEffect(props.modelValue, props.value, props.toggle) { open = resolveReadMoreOpen(props) }
    Column(modifier.fillMaxWidth().applyUPResolvedStyle(rememberUPResolvedStyle(props.customStyle, diagnostics, "UPReadMore")).upTestTag("read-more")) {
        Box(if (open) Modifier else Modifier.height(net.lingyun.ultraui.android.core.upDimension(props.showHeight, 240.dp))) { content() }
        if (shouldShowReadMoreControl(open, props.toggle)) BasicText(if (open) props.openText else props.closeText, modifier = Modifier.upClickable(onClick = { open = !open; onUpdateModelValue?.invoke(open); if (open) onOpen?.invoke(props.name) else onClose?.invoke(props.name) }).padding(vertical = 8.dp), style = TextStyle(color = UPColor.parse(props.color, UPTheme.Primary), fontSize = net.lingyun.ultraui.android.core.upDimension(props.fontSize, 14.dp).value.sp))
    }
}

internal fun resolveReadMoreOpen(props: UPReadMoreProps): Boolean = props.modelValue ?: props.value

internal fun shouldShowReadMoreControl(open: Boolean, toggle: Boolean): Boolean = !open || toggle

@Composable
public fun UPColumnNotice(props: UPColumnNoticeProps = UPColumnNoticeProps(), modifier: Modifier = Modifier, onClick: ((Int) -> Unit)? = null, diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None) = UPNoticeBar(UPNoticeBarProps(text = props.text, direction = "column", step = props.step, icon = props.icon, mode = props.mode, color = props.color, bgColor = props.bgColor, fontSize = props.fontSize, speed = props.speed, duration = props.duration, disableTouch = props.disableTouch, justifyContent = props.justifyContent, customStyle = props.customStyle), modifier, onClick = onClick, diagnostics = diagnostics)

@Composable
public fun UPRowNotice(props: UPRowNoticeProps = UPRowNoticeProps(), modifier: Modifier = Modifier, onClick: ((Int) -> Unit)? = null, diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None) = UPNoticeBar(UPNoticeBarProps(text = props.text, direction = "row", icon = props.icon, mode = props.mode, color = props.color, bgColor = props.bgColor, fontSize = props.fontSize, speed = props.speed, customStyle = props.customStyle), modifier, onClick = onClick, diagnostics = diagnostics)

private fun formatNumber(value: Double, decimals: Int, decimal: String, separator: String): String {
    val fixed = "% .${decimals.coerceAtLeast(0)}f".format(java.util.Locale.US, value).trim()
    val pieces = fixed.split('.')
    val whole = if (separator.isEmpty()) pieces[0] else pieces[0].reversed().chunked(3).joinToString(separator).reversed()
    return if (decimals > 0) whole + decimal + pieces.getOrElse(1) { "0" } else whole
}

@Composable
public fun UPCountTo(props: UPCountToProps = UPCountToProps(), modifier: Modifier = Modifier, onChange: ((Double) -> Unit)? = null, onFinished: (() -> Unit)? = null, diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None) {
    val start = props.startVal.upDoubleOrDefault(0.0)
    val end = props.endVal.upDoubleOrDefault(0.0)
    var value by remember(props) { mutableStateOf(if (props.autoplay) start else start) }
    LaunchedEffect(props) {
        if (props.autoplay) { if (props.duration.upIntOrDefault(0) <= 0) value = end else { value = end; onChange?.invoke(end); onFinished?.invoke() } }
    }
    BasicText(formatNumber(value, props.decimals.upIntOrDefault(0), props.decimal, props.separator), modifier = modifier.applyUPResolvedStyle(rememberUPResolvedStyle(props.customStyle, diagnostics, "UPCountTo")).upTestTag("count-to"), style = TextStyle(color = UPColor.parse(props.color, UPTheme.Content), fontSize = net.lingyun.ultraui.android.core.upDimension(props.fontSize, 22.dp).value.sp, fontWeight = if (props.bold) FontWeight.Bold else FontWeight.Normal))
}

@Composable
public fun UPCountDown(props: UPCountDownProps = UPCountDownProps(), modifier: Modifier = Modifier, onChange: ((UPCountDownTime) -> Unit)? = null, onFinish: (() -> Unit)? = null, diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None) {
    var remaining by remember(props) { mutableLongStateOf(props.time.upLongOrDefault(0L).coerceAtLeast(0L)) }
    LaunchedEffect(props, props.autoStart) { while (props.autoStart && remaining > 0L) { delay(if (props.millisecond) 10L else 1000L); remaining = (remaining - if (props.millisecond) 10 else 1000).coerceAtLeast(0); onChange?.invoke(countdownTime(remaining)); if (remaining == 0L) onFinish?.invoke() } }
    BasicText(formatCountdown(remaining, props.format, props.millisecond), modifier = modifier.applyUPResolvedStyle(rememberUPResolvedStyle(props.customStyle, diagnostics, "UPCountDown")).upTestTag("count-down"))
}

private fun countdownTime(ms: Long) = UPCountDownTime((ms / 86_400_000).toInt(), (ms / 3_600_000 % 24).toInt(), (ms / 60_000 % 60).toInt(), (ms / 1_000 % 60).toInt(), (ms % 1000).toInt())
private fun formatCountdown(ms: Long, format: String, millisecond: Boolean): String { val t = countdownTime(ms); return format.replace("DD", "%02d".format(t.days)).replace("HH", "%02d".format(t.hours + t.days * 24)).replace("mm", "%02d".format(t.minutes)).replace("ss", "%02d".format(t.seconds)).replace("SSS", "%03d".format(t.milliseconds)) }
