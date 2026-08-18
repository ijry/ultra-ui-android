package net.lingyun.ultraui.android.sample.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.unit.dp
import net.lingyun.ultraui.android.components.UPBadge
import net.lingyun.ultraui.android.components.UPBadgeProps
import net.lingyun.ultraui.android.components.UPButton
import net.lingyun.ultraui.android.components.UPButtonProps
import net.lingyun.ultraui.android.components.UPDivider
import net.lingyun.ultraui.android.components.UPDividerProps
import net.lingyun.ultraui.android.components.UPGap
import net.lingyun.ultraui.android.components.UPGapProps
import net.lingyun.ultraui.android.components.UPLine
import net.lingyun.ultraui.android.components.UPLineProps
import net.lingyun.ultraui.android.components.UPLink
import net.lingyun.ultraui.android.components.UPLinkProps
import net.lingyun.ultraui.android.components.UPTag
import net.lingyun.ultraui.android.components.UPTagProps
import net.lingyun.ultraui.android.components.UPText
import net.lingyun.ultraui.android.components.UPTextProps
import net.lingyun.ultraui.android.components.UPTitle
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.sample.DemoSection
import net.lingyun.ultraui.android.sample.SampleScaffold

/** Public API demos for uview-plus basic display components. */
@Composable
public fun FoundationDemoPage(onBack: () -> Unit, modifier: Modifier = Modifier) {
    var eventText by remember { mutableStateOf("等待基础组件交互") }

    SampleScaffold(title = "基础展示", onBack = onBack, modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            DemoEventText(eventText)

            DemoSection(title = "按钮") {
                UPButton(
                    props = UPButtonProps(text = "主要按钮", type = "primary", shape = "circle"),
                    onClick = { eventText = "按钮：点击 primary" },
                )
            }

            DemoSection(title = "标签") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    UPTag(
                        props = UPTagProps(text = "可关闭标签", type = "success", closable = true, name = "tag-demo"),
                        onClick = { eventText = "标签：点击 $it" },
                        onClose = { eventText = "标签：关闭 $it" },
                    )
                    UPTag(props = UPTagProps(text = "plain", type = "primary", plain = true))
                }
            }

            DemoSection(title = "徽标") {
                UPBadge(
                    props = UPBadgeProps(value = 8, type = "error"),
                    content = {
                        Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(UPTheme.Light, RoundedCornerShape(8.dp))
                            .border(0.5.dp, UPTheme.Border, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("消息", color = UPTheme.Content)
                        }
                    },
                )
            }

            DemoSection(title = "分割线") {
                UPDivider(props = UPDividerProps(text = "分割线", textPosition = "center"))
            }

            DemoSection(title = "间隔") {
                Text("上方内容", color = UPTheme.Content)
                UPGap(props = UPGapProps(height = "12px", bgColor = "#f4f4f5"))
                Text("下方内容", color = UPTheme.Content)
            }

            DemoSection(title = "线条") {
                UPLine(props = UPLineProps(color = "#2979ff", length = "100%", margin = "8px"))
            }

            DemoSection(title = "链接") {
                UPLink(
                    props = UPLinkProps(text = "打开链接", href = "https://example.com", color = "#2979ff"),
                    onOpen = { eventText = "链接：$it" },
                )
            }

            DemoSection(title = "文本") {
                UPText(
                    props = UPTextProps(text = "可点击文本", type = "primary", bold = true),
                    onClick = { eventText = "文本：点击" },
                )
            }

            DemoSection(title = "标题") {
                UPTitle(text = "u-title 标题")
            }
        }
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
