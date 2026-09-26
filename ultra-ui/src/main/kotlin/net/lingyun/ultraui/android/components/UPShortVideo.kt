package net.lingyun.ultraui.android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.upTestTag

/**
 * Native Compose counterpart of uview-plus `u-short-video`.
 *
 * Builds the vertical short-video feed shell: a top [`UPShortVideoProps.tabsList`] tab bar
 * ([onTabChange]), a swipeable [VerticalPager] over [videoList] ([onVideoChange]) whose pages show a
 * poster + centre play/pause overlay ([onVideoPlay]/[onVideoPause]), the author block, the right
 * action rail (like/comment/share/collect with counts, [onLike]/[onComment]/[onShare]/[onCollect])
 * and a bottom progress [UPSlider] ([onProgressChanging]/[onProgressChange]).
 *
 * Downgrade: actual video decoding/playback needs a native player (ExoPlayer/VideoView) and is a host
 * concern; each page shows the item's `poster` with a play overlay instead.
 */
@Composable
public fun UPShortVideo(
    props: UPShortVideoProps = UPShortVideoProps(),
    modifier: Modifier = Modifier,
    onTabChange: ((Int) -> Unit)? = null,
    onVideoChange: ((Int) -> Unit)? = null,
    onLike: ((Map<String, UPRawValue>, Int) -> Unit)? = null,
    onComment: ((Map<String, UPRawValue>, Int) -> Unit)? = null,
    onShare: ((Map<String, UPRawValue>, Int) -> Unit)? = null,
    onCollect: ((Map<String, UPRawValue>, Int) -> Unit)? = null,
    onProgressChanging: ((Int, Float) -> Unit)? = null,
    onProgressChange: ((Int, Float) -> Unit)? = null,
    onVideoPlay: ((Int) -> Unit)? = null,
    onVideoPause: ((Int) -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    val style = rememberUPResolvedStyle(props.customStyle, diagnostics, "UPShortVideo")
    val videos = props.videoList
    val pausedMap = remember { mutableStateMapOf<Int, Boolean>() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF000000))
            .applyUPResolvedStyle(style)
            .upTestTag("short-video"),
    ) {
        UPTabs(
            list = props.tabsList,
            current = props.currentTab,
            onChange = { onTabChange?.invoke(it) },
        )

        if (videos.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                BasicText(text = "暂无视频", style = TextStyle(color = Color(0xFF888888), fontSize = 14.sp))
            }
            return@Column
        }

        val pagerState = rememberPagerState(
            initialPage = props.currentVideo.coerceIn(0, (videos.size - 1).coerceAtLeast(0)),
        ) { videos.size }

        LaunchedEffect(pagerState) {
            snapshotFlow { pagerState.currentPage }.collect { onVideoChange?.invoke(it) }
        }

        VerticalPager(state = pagerState, modifier = Modifier.weight(1f).fillMaxWidth()) { page ->
            val item = videos[page].upStringKeyMapOrEmpty()
            val paused = pausedMap[page] ?: false
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF111111))
                    .pointerInput(page) {
                        detectTapGestures {
                            val nowPaused = !(pausedMap[page] ?: false)
                            pausedMap[page] = nowPaused
                            if (nowPaused) onVideoPause?.invoke(page) else onVideoPlay?.invoke(page)
                        }
                    }
                    .upTestTag("short-video-item-$page"),
            ) {
                val poster = item["poster"].upStringValueOrEmpty().ifEmpty { item["cover"].upStringValueOrEmpty() }
                if (poster.isNotEmpty()) {
                    UPImage(props = UPImageProps(src = poster, width = "100%", height = "100%", mode = "aspectFill"))
                }
                if (paused) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        UPIcon(props = UPIconProps(name = "play-right-fill", color = "#eeeeee", size = "60px"))
                    }
                }

                // Author block (bottom-left).
                val author = item["author"].upStringKeyMapOrEmpty()
                Column(
                    modifier = Modifier.align(Alignment.BottomStart).padding(12.dp).fillMaxWidth(0.7f),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        UPAvatar(props = UPAvatarProps(src = author["avatar"].upStringValueOrEmpty(), size = "40px"))
                        BasicText(
                            text = author["name"].upStringValueOrEmpty(),
                            style = TextStyle(color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold),
                        )
                    }
                    val desc = item["title"].upStringValueOrEmpty().ifEmpty { author["desc"].upStringValueOrEmpty() }
                    if (desc.isNotEmpty()) {
                        BasicText(text = desc, style = TextStyle(color = Color(0xFFEEEEEE), fontSize = 13.sp))
                    }
                }

                // Action rail (right).
                Column(
                    modifier = Modifier.align(Alignment.BottomEnd).padding(end = 10.dp, bottom = 20.dp).width(56.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    ActionItem(if (item["isLiked"].upBooleanValue()) "thumb-up-fill" else "thumb-up", item["likeCount"].upStringValueOrEmpty(), page, "like") { onLike?.invoke(item, page) }
                    ActionItem("chat", item["commentCount"].upStringValueOrEmpty(), page, "comment") { onComment?.invoke(item, page) }
                    ActionItem("share", item["shareCount"].upStringValueOrEmpty(), page, "share") { onShare?.invoke(item, page) }
                    ActionItem(if (item["isCollected"].upBooleanValue()) "star-fill" else "star", item["collectCount"].upStringValueOrEmpty(), page, "collect") { onCollect?.invoke(item, page) }
                }
            }
        }

        // Progress slider for the current page.
        val current = pagerState.currentPage
        val progress = videos.getOrNull(current).upStringKeyMapOrEmpty()["progress"] ?: 0
        UPSlider(
            props = UPSliderProps(value = progress, min = 0, max = 100, showValue = false, activeColor = "#ffffff", inactiveColor = "#66ffffff", blockSize = 6),
            onChanging = { onProgressChanging?.invoke(current, (it.value as? Number)?.toFloat() ?: 0f) },
            onChange = { onProgressChange?.invoke(current, (it.value as? Number)?.toFloat() ?: 0f) },
        )
    }
}

@Composable
private fun ActionItem(icon: String, count: String, page: Int, key: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .pointerInput(page, key) { detectTapGestures { onClick() } }
            .upTestTag("short-video-$key-$page"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        UPIcon(props = UPIconProps(name = icon, color = "#eeeeee", size = "30px"))
        if (count.isNotEmpty()) {
            BasicText(text = count, style = TextStyle(color = Color(0xFFEEEEEE), fontSize = 12.sp))
        }
    }
}
