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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import net.lingyun.ultraui.android.components.UPButton
import net.lingyun.ultraui.android.components.UPButtonProps
import net.lingyun.ultraui.android.components.UPCircleProgress
import net.lingyun.ultraui.android.components.UPCol
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
