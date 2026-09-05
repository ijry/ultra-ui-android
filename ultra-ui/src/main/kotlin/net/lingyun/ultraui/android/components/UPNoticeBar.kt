package net.lingyun.ultraui.android.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.lingyun.ultraui.android.core.UPColor
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.core.upClickable
import net.lingyun.ultraui.android.core.upSafeEnum
import net.lingyun.ultraui.android.core.upTestTag
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

private const val NoticeBarComponentName: String = "UPNoticeBar"
private val NoticeBarDirections: Set<String> = setOf("row", "column")
private val NoticeBarModes: Set<String> = setOf("", "link", "closable")
private val NoticeBarJustifyContent: Set<String> = setOf(
    "flex-start",
    "center",
    "flex-end",
    "space-between",
    "space-around",
)

private fun noticeTextItems(value: UPRawValue): List<String> = when (value) {
    is List<*> -> value.map { it.upStringValueOrEmpty() }
    is Array<*> -> value.map { it.upStringValueOrEmpty() }
    null -> emptyList()
    else -> listOf(value.upStringValueOrEmpty())
}.filter(String::isNotEmpty)

private fun noticeContentAlignment(justifyContent: String): Alignment = when (justifyContent) {
    "center" -> Alignment.Center
    "flex-end" -> Alignment.CenterEnd
    else -> Alignment.CenterStart
}

/** Native Compose counterpart of uview-plus `u-notice-bar`. */
@Composable
public fun UPNoticeBar(
    props: UPNoticeBarProps = UPNoticeBarProps(),
    modifier: Modifier = Modifier,
    onClick: ((Int) -> Unit)? = null,
    onItemClick: (() -> Unit)? = null,
    onClose: (() -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    var visible by remember { mutableStateOf(true) }
    if (!visible) return

    val direction = upSafeEnum(
        props.direction,
        NoticeBarDirections,
        "row",
        diagnostics,
        NoticeBarComponentName,
        "direction",
    )
    val mode = upSafeEnum(
        props.mode,
        NoticeBarModes,
        "",
        diagnostics,
        NoticeBarComponentName,
        "mode",
    )
    val justifyContent = upSafeEnum(
        props.justifyContent,
        NoticeBarJustifyContent,
        "flex-start",
        diagnostics,
        NoticeBarComponentName,
        "justifyContent",
    )
    val messages = noticeTextItems(props.text)
    // `direction === 'column' || (direction === 'row' && step)` picks `u-column-notice`,
    // which pages through the messages instead of scrolling one long line.
    val usesColumnNotice = direction == "column" || props.step
    var noticeIndex by remember(messages.size) { mutableIntStateOf(0) }
    val safeIndex = if (messages.isEmpty()) 0 else noticeIndex.coerceIn(0, messages.lastIndex)
    val displayText = if (usesColumnNotice) {
        messages.getOrNull(safeIndex).orEmpty()
    } else {
        messages.joinToString(separator = "  ")
    }
    // `<swiper :interval="duration" autoplay circular>`: advance on a timer and wrap.
    if (usesColumnNotice && messages.size > 1) {
        LaunchedEffect(safeIndex, messages.size, props.duration) {
            delay(upNoticeIntervalMillis(props.duration))
            noticeIndex = upNoticeNextIndex(safeIndex, messages.size)
        }
    }
    val textColor = UPColor.parse(props.color, UPTheme.Warning)
    val background = UPColor.parse(props.bgColor, Color(0xFFFDF6EC))
    val style = rememberUPResolvedStyle(props.customStyle, diagnostics, NoticeBarComponentName)

    fun clickNotice() {
        onClick?.invoke(0)
        onItemClick?.invoke()
    }

    fun closeNotice() {
        if (!visible) return
        visible = false
        onClose?.invoke()
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(background)
            .applyUPResolvedStyle(style)
            .padding(horizontal = 12.dp, vertical = 9.dp)
            .upTestTag("notice-bar")
            .upClickable(onClick = ::clickNotice),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (props.icon.isNotEmpty()) {
            UPIcon(
                props = UPIconProps(name = props.icon, color = props.color, size = 19),
                diagnostics = diagnostics,
            )
        }
        Box(
            modifier = Modifier
                .weight(1f)
                // `disable-touch` on the vertical swiper; false lets a drag page the messages.
                .then(
                    if (!upNoticeTouchEnabled(props.disableTouch, messages.size)) {
                        Modifier
                    } else {
                        Modifier.pointerInput(safeIndex, messages.size) {
                            val threshold = 16.dp.toPx()
                            var travelled = 0f
                            detectVerticalDragGestures(
                                onDragStart = { travelled = 0f },
                                onDragEnd = {
                                    noticeIndex = when {
                                        travelled <= -threshold -> upNoticeNextIndex(safeIndex, messages.size)
                                        travelled >= threshold -> upNoticePreviousIndex(safeIndex, messages.size)
                                        else -> safeIndex
                                    }
                                },
                            ) { _, amount -> travelled += amount }
                        }
                    },
                )
                .clipToBounds()
                .upTestTag("notice-bar-content"),
            contentAlignment = noticeContentAlignment(justifyContent),
        ) {
            val textStyle = TextStyle(color = textColor, fontSize = props.fontSize.upTextUnitOr(14.sp))
            if (usesColumnNotice) {
                BasicText(
                    text = displayText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.upTestTag("notice-bar-text"),
                    style = textStyle,
                )
            } else {
                // `direction="row"` marquees one line: the text starts at the right edge and
                // travels until its trailing edge clears the left one, at `speed` px/second.
                var boxWidth by remember { mutableIntStateOf(0) }
                var textWidth by remember { mutableIntStateOf(0) }
                val travel = remember { Animatable(0f) }
                LaunchedEffect(boxWidth, textWidth, props.speed, displayText) {
                    val box = boxWidth.toFloat()
                    val text = textWidth.toFloat()
                    if (box <= 0f || text <= 0f) return@LaunchedEffect
                    val loopMillis = upNoticeMarqueeDurationMillis(box, text, props.speed)
                    if (loopMillis <= 0) return@LaunchedEffect
                    // The first pass only has to cover `box + text` starting from the right
                    // edge; every later loop repeats that same distance.
                    while (true) {
                        travel.snapTo(box)
                        travel.animateTo(-text, tween(durationMillis = loopMillis, easing = LinearEasing))
                    }
                }
                BasicText(
                    text = displayText,
                    maxLines = 1,
                    softWrap = false,
                    modifier = Modifier
                        .onGloballyPositioned { boxWidth = it.parentLayoutCoordinates?.size?.width ?: 0 }
                        .offset { IntOffset(travel.value.roundToInt(), 0) }
                        .onSizeChanged { textWidth = it.width }
                        .upTestTag("notice-bar-text"),
                    style = textStyle,
                )
            }
        }
        when (mode) {
            "link" -> UPIcon(
                props = UPIconProps(name = "arrow-right", color = props.color, size = 17),
                diagnostics = diagnostics,
            )

            "closable" -> UPIcon(
                props = UPIconProps(name = "close", color = props.color, size = 16),
                modifier = Modifier.upTestTag("notice-bar-close"),
                onClick = { closeNotice() },
                diagnostics = diagnostics,
            )
        }
    }
}

/** Direct argument form for generated source. */
@Composable
public fun UPNoticeBar(
    text: UPRawValue,
    direction: String = "row",
    mode: String = "",
    onClick: ((Int) -> Unit)? = null,
    onClose: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    UPNoticeBar(
        props = UPNoticeBarProps(text = text, direction = direction, mode = mode),
        modifier = modifier,
        onClick = onClick,
        onClose = onClose,
        diagnostics = diagnostics,
    )
}
