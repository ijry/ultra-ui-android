package net.lingyun.ultraui.android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.tools.screenshot.PreviewTest

@PreviewTest
@Preview(
    name = "u-row col and grid",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF,
    widthDp = 360,
    heightDp = 360,
)
@Composable
public fun UPLayoutRowColGridScreenshot(): Unit = LayoutProgressScreenshotSurface {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        BasicText("栅格布局", style = ScreenshotTitleStyle)
        UPRow(
            props = UPRowProps(gutter = "8px", justify = "between", align = "center"),
        ) {
            UPCol(props = UPColProps(span = 4)) {
                LayoutCell(label = "4", color = Color(0xFF2979FF))
            }
            UPCol(props = UPColProps(span = 4)) {
                LayoutCell(label = "4", color = Color(0xFF19BE6B))
            }
            UPCol(props = UPColProps(span = 4)) {
                LayoutCell(label = "4", color = Color(0xFFFF9900))
            }
        }
        BasicText("带间距与边框的网格", style = ScreenshotTitleStyle)
        UPGrid(props = UPGridProps(col = 3, gap = "8px", border = true)) {
            listOf(
                "首页" to "#2979ff",
                "消息" to "#19be6b",
                "设置" to "#ff9900",
                "帮助" to "#fa3534",
                "收藏" to "#6739b6",
                "关于" to "#909399",
            ).forEach { (label, color) ->
                UPGridItem(props = UPGridItemProps(bgColor = color)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        BasicText(label, style = TextStyle(color = Color.White, fontSize = 13.sp))
                    }
                }
            }
        }
    }
}

@PreviewTest
@Preview(
    name = "u-line and circle progress",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF,
    widthDp = 360,
    heightDp = 350,
)
@Composable
public fun UPLayoutProgressScreenshot(): Unit = LayoutProgressScreenshotSurface {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        BasicText("线性进度", style = ScreenshotTitleStyle)
        ProgressRow(label = "0%", percentage = 0)
        ProgressRow(label = "50%", percentage = 50)
        ProgressRow(label = "100%", percentage = 100)
        ProgressRow(label = "从右侧", percentage = 65, fromRight = true)
        BasicText("环形进度", style = ScreenshotTitleStyle)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(28.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            UPCircleProgress(props = UPCircleProgressProps(percentage = 30))
            UPCircleProgress(props = UPCircleProgressProps(percentage = 100))
        }
    }
}

@Composable
private fun ProgressRow(
    label: String,
    percentage: Int,
    fromRight: Boolean = false,
): Unit {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        BasicText(label, style = TextStyle(color = Color(0xFF606266), fontSize = 12.sp))
        UPLineProgress(
            props = UPLineProgressProps(
                percentage = percentage,
                showText = true,
                fromRight = fromRight,
                activeColor = "#2979ff",
                inactiveColor = "#ebeef5",
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp),
        )
    }
}

@Composable
private fun LayoutCell(label: String, color: Color): Unit {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(color, RoundedCornerShape(4.dp)),
        contentAlignment = Alignment.Center,
    ) {
        BasicText(label, style = TextStyle(color = Color.White, fontSize = 14.sp))
    }
}

@Composable
private fun LayoutProgressScreenshotSurface(content: @Composable () -> Unit): Unit {
    Box(
        modifier = Modifier.background(Color.White),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

private val ScreenshotTitleStyle = TextStyle(
    color = Color(0xFF303133),
    fontSize = 15.sp,
)
