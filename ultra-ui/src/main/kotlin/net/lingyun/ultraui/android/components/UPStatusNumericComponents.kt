package net.lingyun.ultraui.android.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import net.lingyun.ultraui.android.core.UPColor
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPImageLoader
import net.lingyun.ultraui.android.core.UPImageLoaders
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.core.report
import net.lingyun.ultraui.android.core.upClickable
import net.lingyun.ultraui.android.core.upDoubleOrDefault
import net.lingyun.ultraui.android.core.upIntOrDefault
import net.lingyun.ultraui.android.core.upSafeEnum
import net.lingyun.ultraui.android.core.upTestTag

@Composable
public fun UPSwiper(props: UPSwiperProps = UPSwiperProps(), modifier: Modifier = Modifier, loader: UPImageLoader = UPImageLoaders.Android, onChange: ((Int) -> Unit)? = null, onClick: ((Int) -> Unit)? = null, onUpdateCurrent: ((Int) -> Unit)? = null, diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None) {
    val list = props.list
    // uview documents `currentItemId` and `current` as mutually exclusive; addressing a slide by
    // identity wins whenever the id actually matches an entry of `list`.
    var current by remember { mutableStateOf(upSwiperResolveIndex(list, props.current, props.currentItemId)) }
    LaunchedEffect(props.current, props.currentItemId, list) { current = upSwiperResolveIndex(list, props.current, props.currentItemId) }
    val style = rememberUPResolvedStyle(props.customStyle, diagnostics, UPSwiperComponentName)
    val background = UPColor.parse(props.bgColor, Color(0xFFF3F4F6))
    val height = net.lingyun.ultraui.android.core.upDimension(props.height, 130.dp)
    val shape = RoundedCornerShape(net.lingyun.ultraui.android.core.upDimension(props.radius, 4.dp).coerceAtLeast(0.dp))
    // While `loading` is set upstream renders a centered loading icon instead of the swiper.
    if (props.loading) {
        Box(modifier.fillMaxWidth().height(height).clip(shape).background(background).applyUPResolvedStyle(style).upTestTag("swiper"), contentAlignment = Alignment.Center) {
            Box(Modifier.upTestTag("swiper-loading"), contentAlignment = Alignment.Center) { UPLoadingIcon(UPLoadingIconProps(show = true, mode = "circle", size = 28), diagnostics = diagnostics) }
        }
        return
    }
    if (list.isEmpty()) return
    // Clamp for rendering only. Assigning to `current` during composition would feed a
    // state write back into the same pass, which Compose treats as an unstable read.
    val index = current.coerceIn(0, list.lastIndex)

    // uview defaults `autoplay` to true and advances every `interval` ms, wrapping past
    // the final slide only when `circular` is set.
    val interval = props.interval.upIntOrDefault(3000).toLong().coerceAtLeast(1L)
    if (props.autoplay && list.size > 1) {
        LaunchedEffect(props.autoplay, interval, props.circular, list.size, index) {
            delay(interval)
            val next = upSwiperNextIndex(index, list.lastIndex, props.circular) ?: return@LaunchedEffect
            current = next
            onChange?.invoke(next)
            onUpdateCurrent?.invoke(next)
        }
    }
    // `acceleration` only tunes uni-app's multi-screen inertia, which has no Compose counterpart.
    if (props.acceleration) LaunchedEffect(props.acceleration) { diagnostics.report(UPSwiperComponentName, "acceleration", props.acceleration, "uni-app 惯性滑动参数，Compose 手势没有对应实现") }

    // `duration` drives the slide transition. Zero keeps the jump instant so callers (and tests)
    // that pause the clock never wait on an animation.
    val durationMillis = props.duration.upIntOrDefault(300).coerceAtLeast(0)
    val slideOffset = remember { Animatable(index.toFloat()) }
    LaunchedEffect(index, durationMillis) {
        val distance = kotlin.math.abs(slideOffset.value - index.toFloat())
        if (durationMillis > 0 && distance > 0f && distance <= 1f) slideOffset.animateTo(index.toFloat(), tween(durationMillis))
        else slideOffset.snapTo(index.toFloat())
    }
    val previousMargin = upRawDp(props.previousMargin, 0.dp).coerceAtLeast(0.dp)
    val nextMargin = upRawDp(props.nextMargin, 0.dp).coerceAtLeast(0.dp)
    val displayCount = upSwiperDisplayCount(props.displayMultipleItems, list.size).coerceAtLeast(1)
    val moveTo: (Int?) -> Unit = { target -> if (target != null) { current = target; onChange?.invoke(target); onUpdateCurrent?.invoke(target) } }
    Column(modifier.fillMaxWidth().applyUPResolvedStyle(style).upTestTag("swiper")) {
        BoxWithConstraints(
            Modifier.fillMaxWidth()
                .height(height)
                .clip(shape)
                .background(background)
                // `vertical` swaps the scroll axis, so the drag detector follows it.
                .pointerInput(index, list.size, props.circular, props.vertical) {
                    val threshold = 24.dp.toPx()
                    var travelled = 0f
                    val settle: () -> Unit = {
                        if (travelled <= -threshold) moveTo(upSwiperNextIndex(index, list.lastIndex, props.circular))
                        else if (travelled >= threshold) moveTo(upSwiperPreviousIndex(index, list.lastIndex, props.circular))
                    }
                    if (props.vertical) detectVerticalDragGestures(onDragStart = { travelled = 0f }, onDragEnd = settle) { _, amount -> travelled += amount }
                    else detectHorizontalDragGestures(onDragStart = { travelled = 0f }, onDragEnd = settle) { _, amount -> travelled += amount }
                },
        ) {
            // previousMargin/nextMargin shrink each slide so the neighbouring ones peek through.
            // Hoisted because @LayoutScopeMarker hides this scope inside the nested Row/Column.
            val boxWidth = maxWidth
            val boxHeight = maxHeight
            val span = (((if (props.vertical) boxHeight else boxWidth) - previousMargin - nextMargin) / displayCount).coerceAtLeast(1.dp)
            val lead = previousMargin - span * slideOffset.value
            if (props.vertical) {
                Column(Modifier.fillMaxWidth().requiredHeight(span * list.size).offset(y = lead)) {
                    list.forEachIndexed { position, slide ->
                        UPSwiperSlide(item = slide, position = position, keyName = props.keyName, imgMode = props.imgMode, radius = props.radius, bgColor = props.bgColor, showTitle = props.showTitle, scale = upSwiperItemScale(previousMargin.value, nextMargin.value, position == index), slideWidth = boxWidth, slideHeight = span, shape = shape, loader = loader, diagnostics = diagnostics, onClick = onClick)
                    }
                }
            } else {
                Row(Modifier.fillMaxHeight().requiredWidth(span * list.size).offset(x = lead)) {
                    list.forEachIndexed { position, slide ->
                        UPSwiperSlide(item = slide, position = position, keyName = props.keyName, imgMode = props.imgMode, radius = props.radius, bgColor = props.bgColor, showTitle = props.showTitle, scale = upSwiperItemScale(previousMargin.value, nextMargin.value, position == index), slideWidth = span, slideHeight = boxHeight, shape = shape, loader = loader, diagnostics = diagnostics, onClick = onClick)
                    }
                }
            }
            // Upstream hides the indicator while a title bar is shown, and while loading.
            if (upSwiperShouldShowIndicator(props.loading, props.indicator, props.showTitle)) {
                UPSwiperIndicator(UPSwiperIndicatorProps(length = list.size, current = index, indicatorActiveColor = props.indicatorActiveColor, indicatorInactiveColor = props.indicatorInactiveColor, indicatorMode = props.indicatorMode, customStyle = props.indicatorStyle), Modifier.align(Alignment.BottomCenter), onClick = { next -> moveTo(next) }, diagnostics = diagnostics)
            }
        }
        val backward = upSwiperPreviousIndex(index, list.lastIndex, props.circular)
        val forward = upSwiperNextIndex(index, list.lastIndex, props.circular)
        if (backward != null || forward != null) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                if (backward != null) Box(Modifier.upClickable(onClick = { moveTo(backward) }).padding(12.dp).upTestTag("swiper-previous")) { UPIcon(UPIconProps(name = if (props.vertical) "arrow-up" else "arrow-left", color = "#606266", size = 20), diagnostics = diagnostics) }
                if (forward != null) Box(Modifier.upClickable(onClick = { moveTo(forward) }).padding(12.dp).upTestTag("swiper-next")) { UPIcon(UPIconProps(name = if (props.vertical) "arrow-down" else "arrow-right", color = "#606266", size = 20), diagnostics = diagnostics) }
            }
        }
    }
}

@Composable
private fun UPSwiperSlide(item: UPRawValue, position: Int, keyName: String, imgMode: String, radius: UPRawValue, bgColor: String, showTitle: Boolean, scale: Float, slideWidth: Dp, slideHeight: Dp, shape: RoundedCornerShape, loader: UPImageLoader, diagnostics: UPCompatibilityDiagnostics, onClick: ((Int) -> Unit)?) {
    val source = upSwiperSource(item, keyName)
    val title = if (upSwiperShouldShowTitle(item, keyName, showTitle)) upSwiperTitle(item) else ""
    Box(
        Modifier.size(slideWidth, slideHeight)
            // uview scales the neighbouring slides down when both margins reveal them.
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clip(shape)
            .upTestTag("swiper-item-$position")
            .upClickable(onClick = { onClick?.invoke(position) }),
        contentAlignment = Alignment.Center,
    ) {
        when (upSwiperRenderKind(item, keyName)) {
            "image" -> UPImage(props = UPImageProps(src = source, mode = imgMode, width = slideWidth, height = slideHeight, radius = radius, showLoading = false, bgColor = bgColor), loader = loader, diagnostics = diagnostics)
            "video" -> {
                // Compose ships no video surface, so the poster plus a play badge stands in.
                LaunchedEffect(source) { diagnostics.report(UPSwiperComponentName, "list", source, "视频项渲染为封面加播放标记，Compose 没有内置播放器") }
                Box(Modifier.fillMaxSize().background(Color.Black), contentAlignment = Alignment.Center) {
                    val poster = upSwiperPoster(item)
                    if (poster.isNotEmpty()) UPImage(props = UPImageProps(src = poster, mode = imgMode, width = slideWidth, height = slideHeight, radius = radius, showLoading = false, showError = false, bgColor = "#000000"), loader = loader, diagnostics = diagnostics)
                    UPIcon(UPIconProps(name = "play-right-fill", color = "#FFFFFF", size = 34), diagnostics = diagnostics)
                }
            }
            // Plain labels stay text: upstream would hand them to `<image>` and show a broken icon.
            else -> BasicText(actionOrOptionText(item, keyName, item.upStringValueOrEmpty()), style = TextStyle(color = UPTheme.Main))
        }
        if (title.isNotEmpty()) {
            Box(Modifier.align(Alignment.BottomCenter).fillMaxWidth().background(Color(0x4D000000)).padding(horizontal = 12.dp, vertical = 6.dp)) {
                BasicText(title, style = TextStyle(color = Color.White, fontSize = 14.sp), maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@Composable
public fun UPSwiper(list: List<UPRawValue>, modifier: Modifier = Modifier, current: Int = 0, onChange: ((Int) -> Unit)? = null) = UPSwiper(UPSwiperProps(list = list, current = current), modifier, onChange = onChange)


@Composable
public fun UPSwiperIndicator(props: UPSwiperIndicatorProps = UPSwiperIndicatorProps(), modifier: Modifier = Modifier, onClick: ((Int) -> Unit)? = null, diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None) {
    val count = props.length.upIntOrDefault(0).coerceAtLeast(0)
    val current = props.current.upIntOrDefault(0).coerceIn(0, (count - 1).coerceAtLeast(0))
    val activeColor = UPColor.parse(props.indicatorActiveColor, Color.White)
    val inactiveColor = UPColor.parse(props.indicatorInactiveColor, Color.LightGray)
    val indicatorShape = RoundedCornerShape(100.dp)
    Row(modifier.fillMaxWidth().padding(6.dp).applyUPResolvedStyle(rememberUPResolvedStyle(props.customStyle, diagnostics, "UPSwiperIndicator")).upTestTag("swiper-indicator"), horizontalArrangement = Arrangement.Center) {
        if (props.indicatorMode == "dot") {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                repeat(count) { index ->
                    Box(
                        Modifier
                            .width(if (index == current) 12.dp else 5.dp)
                            .height(5.dp)
                            .background(if (index == current) activeColor else inactiveColor, indicatorShape)
                            .upClickable(onClick = { onClick?.invoke(index) })
                            .upTestTag("swiper-indicator-dot-$index"),
                    )
                }
            }
        } else {
            val lineWidth = 22.dp
            Box(
                Modifier
                    .width(lineWidth * count)
                    .height(4.dp)
                    .background(inactiveColor, indicatorShape)
                    .upTestTag("swiper-indicator-line-track"),
            ) {
                Box(
                    Modifier
                        .offset(x = lineWidth * current)
                        .width(lineWidth)
                        .fillMaxHeight()
                        .background(activeColor, indicatorShape)
                        .upTestTag("swiper-indicator-line-bar"),
                )
                if (onClick != null) {
                    Row(Modifier.fillMaxWidth().fillMaxHeight()) {
                        repeat(count) { index ->
                            Spacer(
                                Modifier
                                    .width(lineWidth)
                                    .fillMaxHeight()
                                    .upClickable(onClick = { onClick(index) }),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
public fun UPSkeleton(props: UPSkeletonProps = UPSkeletonProps(), modifier: Modifier = Modifier, diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None, content: @Composable () -> Unit = {}) {
    if (!props.loading) { Box(modifier.upTestTag("skeleton-content")) { content() }; return }
    val rows = props.rows.upIntOrDefault(0).coerceAtLeast(0)
    // `.u-skeleton__wrapper__avatar--${avatarShape}` rounds the avatar placeholder.
    val avatarShape = upSafeEnum(props.avatarShape, UPSkeletonAvatarShapes, "circle", diagnostics, "UPSkeleton", "avatarShape")
    val avatarCorner = if (avatarShape == "square") RoundedCornerShape(4.dp) else RoundedCornerShape(percent = 50)
    Column(modifier.fillMaxWidth().applyUPResolvedStyle(rememberUPResolvedStyle(props.customStyle, diagnostics, "UPSkeleton")).upTestTag("skeleton"), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
            if (props.avatar) Box(Modifier.size(net.lingyun.ultraui.android.core.upDimension(props.avatarSize, 32.dp)).background(Color(0xFFE6E8EB), avatarCorner).upTestTag("skeleton-avatar"))
            if (props.title) Box(Modifier.size(net.lingyun.ultraui.android.core.upDimension(props.titleWidth, 120.dp), net.lingyun.ultraui.android.core.upDimension(props.titleHeight, 18.dp)).background(Color(0xFFE6E8EB)))
        }
        val rowCount = if (rows == 0) 3 else rows
        repeat(rowCount) { index ->
            // `rowsArray`: a per-row width array wins, the last row falls back to 70%,
            // and a percentage becomes a fraction of the available width.
            val width = upSkeletonRowWidth(props.rowsWidth, index, rowCount)
            val fraction = upSkeletonWidthFractionOrNull(width)
            val absolute = if (fraction == null) net.lingyun.ultraui.android.core.upDimension(width, 0.dp).takeIf { it > 0.dp } else null
            Box(
                Modifier
                    .then(
                        when {
                            absolute != null -> Modifier.width(absolute)
                            else -> Modifier.fillMaxWidth(fraction ?: 1f)
                        },
                    )
                    .height(net.lingyun.ultraui.android.core.upDimension(props.rowsHeight, 18.dp))
                    .background(Color(0xFFE6E8EB))
                    .alpha(if (props.animate) .8f else 1f)
                    .upTestTag("skeleton-row-$index"),
            )
        }
    }
}

@Composable
public fun UPReadMore(props: UPReadMoreProps = UPReadMoreProps(), modifier: Modifier = Modifier, onOpen: ((UPRawValue) -> Unit)? = null, onClose: ((UPRawValue) -> Unit)? = null, onUpdateModelValue: ((Boolean) -> Unit)? = null, diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None, content: @Composable () -> Unit) {
    var open by remember(props) { mutableStateOf(resolveReadMoreOpen(props)) }
    LaunchedEffect(props.modelValue, props.value, props.toggle) { open = resolveReadMoreOpen(props) }
    val fontSize = net.lingyun.ultraui.android.core.upDimension(props.fontSize, 14.dp).value
    // `textIndent: '2em'` indents only the first line; `em` resolves against the text size.
    val indent = upTextIndentPx(props.textIndent, fontSize)
    // `innerShadowStyle`: the fade only covers the collapsed state.
    val shadowStyle = rememberUPResolvedStyle(props.shadowStyle, diagnostics, "UPReadMore.shadowStyle")
    Column(modifier.fillMaxWidth().applyUPResolvedStyle(rememberUPResolvedStyle(props.customStyle, diagnostics, "UPReadMore")).upTestTag("read-more")) {
        Box(
            (if (open) Modifier else Modifier.height(net.lingyun.ultraui.android.core.upDimension(props.showHeight, 240.dp)))
                .then(if (indent == null) Modifier else Modifier.padding(start = indent.dp))
                .upTestTag("read-more-content"),
        ) { content() }
        if (shouldShowReadMoreControl(open, props.toggle)) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .then(if (open) Modifier else Modifier.applyUPResolvedStyle(shadowStyle))
                    .upTestTag("read-more-toggle"),
            ) {
                BasicText(if (open) props.openText else props.closeText, modifier = Modifier.upClickable(onClick = { open = !open; onUpdateModelValue?.invoke(open); if (open) onOpen?.invoke(props.name) else onClose?.invoke(props.name) }).padding(vertical = 8.dp), style = TextStyle(color = UPColor.parse(props.color, UPTheme.Primary), fontSize = fontSize.sp))
            }
        }
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
    var value by remember(props) { mutableStateOf(start) }
    // `count(timestamp)` steps the printed value each frame; `useEasing` chooses between
    // upstream's ease-out-expo curve and a linear ramp, and `end` is emitted once on
    // arrival. A non-positive duration jumps straight there, as it does upstream.
    LaunchedEffect(props) {
        if (!props.autoplay) return@LaunchedEffect
        val duration = props.duration.upIntOrDefault(0).toDouble()
        if (duration <= 0.0) {
            value = end
            onChange?.invoke(end)
            onFinished?.invoke()
            return@LaunchedEffect
        }
        val startedAt = withFrameMillis { it }
        var progress = 0.0
        while (progress < duration) {
            progress = (withFrameMillis { it } - startedAt).toDouble()
            value = upCountToValueAt(progress, duration, start, end, props.useEasing)
            onChange?.invoke(value)
        }
        value = end
        onFinished?.invoke()
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
