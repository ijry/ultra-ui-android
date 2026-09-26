package net.lingyun.ultraui.android.sample.catalog

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import net.lingyun.ultraui.android.components.UPBox
import net.lingyun.ultraui.android.components.UPBoxProps
import net.lingyun.ultraui.android.components.UPCard
import net.lingyun.ultraui.android.components.UPCardProps
import net.lingyun.ultraui.android.components.UPCateTab
import net.lingyun.ultraui.android.components.UPCateTabProps
import net.lingyun.ultraui.android.components.UPDivider
import net.lingyun.ultraui.android.components.UPDividerProps
import net.lingyun.ultraui.android.components.UPGrid
import net.lingyun.ultraui.android.components.UPGridItem
import net.lingyun.ultraui.android.components.UPLine
import net.lingyun.ultraui.android.components.UPLineProps
import net.lingyun.ultraui.android.components.UPNoNetwork
import net.lingyun.ultraui.android.components.UPNoNetworkProps
import net.lingyun.ultraui.android.components.UPOverlay
import net.lingyun.ultraui.android.components.UPOverlayProps
import net.lingyun.ultraui.android.components.UPScrollList
import net.lingyun.ultraui.android.components.UPScrollListProps
import net.lingyun.ultraui.android.components.UPShortVideo
import net.lingyun.ultraui.android.components.UPShortVideoProps
import net.lingyun.ultraui.android.components.UPSkeleton
import net.lingyun.ultraui.android.components.UPSkeletonProps
import net.lingyun.ultraui.android.components.UPSticky
import net.lingyun.ultraui.android.components.UPSwiper
import net.lingyun.ultraui.android.components.UPSwiperProps
import net.lingyun.ultraui.android.components.UPTitle
import net.lingyun.ultraui.android.components.UPWaterfall
import net.lingyun.ultraui.android.components.UPWaterfallProps
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.sample.DemoSection

/** 布局组件分组的逐组件 demo；未命中返回 false 交回主分发器。 */
@Composable
internal fun renderLayoutGroup(id: String, onEvent: (String) -> Unit): Boolean {
    when (id) {
        "scroll-list" -> ScrollListDemo()
        "line" -> LineDemo()
        "card" -> CardDemo(onEvent)
        "overlay" -> OverlayDemo(onEvent)
        "no-network" -> NoNetworkDemo(onEvent)
        "grid" -> GridDemo(onEvent)
        "swiper" -> SwiperDemo()
        "skeleton" -> SkeletonDemo()
        "sticky" -> StickyDemo()
        "waterfall" -> WaterfallDemo()
        "divider" -> DividerDemo()
        "box" -> BoxDemo(onEvent)
        "cate-tab" -> CateTabDemo()
        "title" -> TitleDemo()
        "short-video" -> ShortVideoDemo(onEvent)
        else -> return false
    }
    return true
}

@Composable
private fun DemoBox(height: Int, content: @Composable () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().height(height.dp)) { content() }
}

@Composable
private fun DemoTile(text: String) {
    Box(
        modifier = Modifier
            .width(88.dp)
            .height(64.dp)
            .background(UPTheme.Light, RoundedCornerShape(6.dp)),
        contentAlignment = Alignment.Center,
    ) { Text(text, color = UPTheme.Content) }
}

@Composable
private fun ScrollListDemo() {
    DemoSection(title = "横向滚动列表") {
        UPScrollList(props = UPScrollListProps()) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                (1..8).forEach { DemoTile("卡片 $it") }
            }
        }
    }
}

@Composable
private fun LineDemo() {
    DemoSection(title = "线条") {
        UPLine(props = UPLineProps(color = "#2979ff", length = "100%", margin = "8px"))
    }
}

@Composable
private fun DividerDemo() {
    DemoSection(title = "分割线") {
        UPDivider(props = UPDividerProps(text = "分割线", textPosition = "center"))
    }
}

@Composable
private fun TitleDemo() {
    DemoSection(title = "标题") { UPTitle(text = "u-title 标题") }
}

@Composable
private fun CardDemo(onEvent: (String) -> Unit) {
    DemoSection(title = "卡片") {
        UPCard(
            props = UPCardProps(title = "订单信息", subTitle = "今天 12:30", index = 1),
            onClick = { onEvent("卡片：点击 $it") },
        ) {
            Text("卡片主体内容", color = UPTheme.Content)
        }
    }
}

@Composable
private fun OverlayDemo(onEvent: (String) -> Unit) {
    DemoSection(title = "遮罩层") {
        DemoBox(160) {
            Box(modifier = Modifier.fillMaxWidth().height(160.dp).background(UPTheme.Light)) {
                UPOverlay(props = UPOverlayProps(show = true, opacity = 0.35), onClick = { onEvent("遮罩：点击") })
            }
        }
    }
}

@Composable
private fun NoNetworkDemo(onEvent: (String) -> Unit) {
    var connected by remember { mutableStateOf(false) }
    DemoSection(title = "无网络提示") {
        DemoBox(260) {
            UPNoNetwork(
                props = UPNoNetworkProps(tips = "哎呀，网络信号丢失"),
                connected = connected,
                onRetry = { connected = true; onEvent("无网络：点击重试") },
            )
        }
    }
}

@Composable
private fun GridDemo(onEvent: (String) -> Unit) {
    DemoSection(title = "宫格布局") {
        UPGrid(col = 3, border = true, gap = 8) {
            repeat(6) { index ->
                UPGridItem(name = "grid-$index", onClick = { onEvent("宫格：$it") }) {
                    DemoTile("宫格 ${index + 1}")
                }
            }
        }
    }
}

@Composable
private fun SwiperDemo() {
    DemoSection(title = "轮播图") {
        UPSwiper(UPSwiperProps(list = listOf("第一页", "第二页", "第三页"), indicator = true, indicatorMode = "dot", radius = 12, previousMargin = 12, nextMargin = 12))
    }
}

@Composable
private fun SkeletonDemo() {
    DemoSection(title = "骨架屏") { UPSkeleton(UPSkeletonProps(rows = 3, avatar = true)) }
}

@Composable
private fun StickyDemo() {
    DemoSection(title = "吸顶") { UPSticky { BasicText("吸顶内容", Modifier.padding(12.dp)) } }
}

@Composable
private fun WaterfallDemo() {
    DemoSection(title = "瀑布流") {
        UPWaterfall(
            props = UPWaterfallProps(
                modelValue = (1..6).map { mapOf("id" to it, "label" to "瀑布 $it", "h" to (60 + it * 12)) },
                columns = 2,
            ),
            modifier = Modifier.fillMaxWidth(),
        ) { item, _ ->
            val h = (item["h"] as? Int) ?: 80
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(h.dp)
                    .padding(4.dp)
                    .background(UPTheme.Light, RoundedCornerShape(6.dp)),
                contentAlignment = Alignment.Center,
            ) { Text((item["label"] ?: "").toString(), color = UPTheme.Content) }
        }
    }
}

@Composable
private fun BoxDemo(onEvent: (String) -> Unit) {
    DemoSection(title = "盒子") {
        UPBox(
            props = UPBoxProps(leftTitle = "推荐", rightTopTitle = "热门", rightBottomTitle = "新品"),
            onClick = { onEvent("盒子：点击") },
        )
    }
}

@Composable
private fun CateTabDemo() {
    var cate by remember { mutableStateOf(0) }
    DemoSection(title = "垂直 TAB") {
        DemoBox(260) {
            UPCateTab(
                props = UPCateTabProps(
                    mode = "tab",
                    current = cate,
                    tabList = listOf(
                        mapOf("name" to "水果", "children" to listOf(mapOf("name" to "苹果"), mapOf("name" to "香蕉"))),
                        mapOf("name" to "蔬菜", "children" to listOf(mapOf("name" to "白菜"), mapOf("name" to "菠菜"))),
                    ),
                ),
                onUpdateCurrent = { cate = it },
            )
        }
    }
}

@Composable
private fun ShortVideoDemo(onEvent: (String) -> Unit) {
    DemoSection(title = "短视频切换") {
        DemoBox(360) {
            UPShortVideo(
                props = UPShortVideoProps(
                    videoList = listOf(
                        mapOf("poster" to "/sdcard/v1.jpg", "title" to "第一条短视频", "likeCount" to "1.2w", "commentCount" to "320", "shareCount" to "88", "collectCount" to "45", "author" to mapOf("name" to "@作者甲", "desc" to "记录生活")),
                        mapOf("poster" to "/sdcard/v2.jpg", "title" to "第二条短视频", "likeCount" to "8621", "commentCount" to "210", "shareCount" to "33", "collectCount" to "12", "author" to mapOf("name" to "@作者乙", "desc" to "旅行日记")),
                    ),
                ),
                onLike = { _, i -> onEvent("短视频：点赞第 $i 条") },
                onVideoChange = { onEvent("短视频：切到第 $it 条") },
            )
        }
    }
}
