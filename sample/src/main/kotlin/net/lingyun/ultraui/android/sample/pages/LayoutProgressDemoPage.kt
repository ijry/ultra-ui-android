package net.lingyun.ultraui.android.sample.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import net.lingyun.ultraui.android.components.UPButton
import net.lingyun.ultraui.android.components.UPButtonProps
import net.lingyun.ultraui.android.components.UPCircleProgress
import net.lingyun.ultraui.android.components.UPCol
import net.lingyun.ultraui.android.components.UPTree
import net.lingyun.ultraui.android.components.UPTreeProps
import net.lingyun.ultraui.android.components.UPVirtualList
import net.lingyun.ultraui.android.components.UPVirtualListProps
import net.lingyun.ultraui.android.components.UPWaterfall
import net.lingyun.ultraui.android.components.UPWaterfallProps
import net.lingyun.ultraui.android.components.UPDragsort
import net.lingyun.ultraui.android.components.UPDragsortProps
import net.lingyun.ultraui.android.components.UPView
import net.lingyun.ultraui.android.components.UPViewProps
import net.lingyun.ultraui.android.components.UPFloatButton
import net.lingyun.ultraui.android.components.UPFloatButtonProps
import net.lingyun.ultraui.android.components.UPBox
import net.lingyun.ultraui.android.components.UPBoxProps
import net.lingyun.ultraui.android.components.UPGrid
import net.lingyun.ultraui.android.components.UPGridItem
import net.lingyun.ultraui.android.components.UPLineProgress
import net.lingyun.ultraui.android.components.UPRow
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.sample.DemoSection
import net.lingyun.ultraui.android.sample.SampleScaffold

/** Public API demos for layout and progress components. */
@Composable
public fun LayoutProgressDemoPage(onBack: () -> Unit, modifier: Modifier = Modifier) {
    var eventText by remember { mutableStateOf("等待布局与进度交互") }
    var percentage by remember { mutableStateOf(45) }

    SampleScaffold(title = "布局与进度", onBack = onBack, modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            DemoEventText(eventText)

            DemoSection(title = "行布局") {
                UPRow(gutter = 8, justify = "between", onClick = { eventText = "行布局：点击" }) {
                    UPCol(span = 4) { DemoTile("span 4") }
                    UPCol(span = 4) { DemoTile("span 4") }
                    UPCol(span = 4) { DemoTile("span 4") }
                }
            }

            DemoSection(title = "列布局") {
                UPRow(gutter = 8) {
                    UPCol(span = 6, onClick = { eventText = "列布局：左列" }) { DemoTile("左列") }
                    UPCol(span = 6, onClick = { eventText = "列布局：右列" }) { DemoTile("右列") }
                }
            }

            DemoSection(title = "栅格") {
                UPGrid(col = 3, border = true, gap = 8) {
                    repeat(3) { index ->
                        UPGridItem(name = "grid-$index", onClick = {
                            eventText = "栅格：$it"
                        }) {
                            DemoTile("宫格 ${index + 1}")
                        }
                    }
                }
            }

            DemoSection(title = "栅格项") {
                UPGrid(col = 2, border = true, gap = 8) {
                    UPGridItem(name = "single-grid-item", bgColor = "#f4f4f5", onClick = {
                        eventText = "栅格项：$it"
                    }) {
                        DemoTile("可点击项")
                    }
                    UPGridItem(name = "disabled-demo") { DemoTile("展示项") }
                }
            }

            DemoSection(title = "盒子") {
                UPBox(
                    props = UPBoxProps(leftTitle = "推荐", rightTopTitle = "热门", rightBottomTitle = "新品"),
                    onClick = { eventText = "盒子：点击" },
                )
            }

            DemoSection(title = "树形控件") {
                UPTree(
                    props = UPTreeProps(
                        defaultExpandAll = false,
                        data = listOf(
                            mapOf(
                                "id" to "a", "label" to "一级 A",
                                "children" to listOf(
                                    mapOf("id" to "a1", "label" to "二级 A-1"),
                                    mapOf("id" to "a2", "label" to "二级 A-2"),
                                ),
                            ),
                            mapOf("id" to "b", "label" to "一级 B"),
                        ),
                    ),
                    onNodeClick = { eventText = "树：${it["label"]}" },
                )
            }

            DemoSection(title = "虚拟列表") {
                UPVirtualList(
                    props = UPVirtualListProps(
                        listData = (1..200).map { mapOf("id" to it, "label" to "虚拟行 $it") },
                        itemHeight = 44,
                        height = "180",
                    ),
                    modifier = Modifier.fillMaxWidth().height(180.dp),
                ) { item, _ ->
                    androidx.compose.foundation.text.BasicText(
                        item["label"].toString(),
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                    )
                }
            }

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
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFEFF3FF)),
                        contentAlignment = androidx.compose.ui.Alignment.Center,
                    ) { androidx.compose.foundation.text.BasicText(item["label"].toString()) }
                }
            }

            DemoSection(title = "拖拽排序") {
                UPDragsort(
                    props = UPDragsortProps(
                        initialList = listOf(
                            mapOf("id" to 1, "label" to "拖动我 · 一"),
                            mapOf("id" to 2, "label" to "拖动我 · 二"),
                            mapOf("id" to 3, "label" to "拖动我 · 三"),
                        ),
                    ),
                    onDragEnd = { eventText = "拖拽排序：${it.size} 项" },
                )
            }

            DemoSection(title = "视图容器") {
                UPView(
                    props = UPViewProps(backgroundColor = "#f4f4f5", height = "60px", padding = "12px"),
                    onClick = { eventText = "视图容器：点击" },
                ) {
                    androidx.compose.foundation.text.BasicText("通用样式容器")
                }
            }

            DemoSection(title = "悬浮按钮") {
                Box(modifier = Modifier.fillMaxWidth().height(180.dp)) {
                    UPFloatButton(
                        props = UPFloatButtonProps(
                            isMenu = true,
                            bottom = "12px",
                            list = listOf(mapOf("name" to "star"), mapOf("name" to "heart")),
                        ),
                        onClick = { eventText = "悬浮按钮：点击" },
                        onItemClick = { item, index -> eventText = "悬浮按钮项：${item["name"]} @$index" },
                    )
                }
            }

            DemoSection(title = "线性进度") {
                UPLineProgress(percentage = percentage, showText = true, height = 18)
                UPButton(props = UPButtonProps(text = "增加进度", type = "primary", size = "small"), onClick = {
                    percentage = (percentage + 15).let { if (it > 100) 0 else it }
                    eventText = "线性进度：$percentage%"
                })
            }

            DemoSection(title = "环形进度") {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    UPCircleProgress(percentage = percentage)
                }
            }
        }
    }
}

@Composable
private fun DemoTile(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .background(UPTheme.Light, RoundedCornerShape(6.dp))
            .padding(8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = text, color = UPTheme.Content, textAlign = TextAlign.Center)
    }
}

@Composable
private fun DemoEventText(text: String) {
    Text(
        text = text,
        color = UPTheme.Content,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(Color.White, RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
    )
}
