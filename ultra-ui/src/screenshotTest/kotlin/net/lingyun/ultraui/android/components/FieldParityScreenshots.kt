package net.lingyun.ultraui.android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.tools.screenshot.PreviewTest

/**
 * Layout coverage for the field-parity batch. This repository's screenshot renderer draws
 * neither text nor most fills, so these previews stand as crash-free layout evidence
 * rather than visual regression baselines.
 */
@PreviewTest
@Preview(
    name = "field parity cell navbar and tag",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF,
    widthDp = 360,
    heightDp = 320,
)
@Composable
fun UPFieldParityCellNavbarScreenshot() {
    Column(
        Modifier.fillMaxSize().background(Color.White),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        UPNavbar(UPNavbarProps(title = "订单", leftText = "返回", border = true))
        UPCell(
            props = UPCellProps(
                title = "收货地址",
                value = "北京",
                icon = "map",
                isLink = true,
                iconStyle = mapOf("fontSize" to "30px"),
                rightIconStyle = mapOf("fontSize" to "26px"),
            ),
        )
        UPCell(props = UPCellProps(title = "禁用项", value = "不可点", isLink = true, disabled = true))
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            UPTag(UPTagProps(text = "自动浅色", color = "#2979ff", autoBgColor = 95))
            UPTag(UPTagProps(text = "常规", type = "primary"))
        }
    }
}

@PreviewTest
@Preview(
    name = "field parity badge offset and number box",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF,
    widthDp = 360,
    heightDp = 240,
)
@Composable
fun UPFieldParityBadgeOffsetScreenshot() {
    Column(
        Modifier.fillMaxSize().background(Color.White).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            Box(modifier = Modifier.size(72.dp)) {
                UPBadge(
                    props = UPBadgeProps(value = 9, absolute = true),
                    content = { BasicText("默认角标") },
                )
            }
            Box(modifier = Modifier.size(72.dp)) {
                UPBadge(
                    props = UPBadgeProps(value = 9, absolute = true, offset = listOf(12, 20)),
                    content = { BasicText("偏移角标") },
                )
            }
        }
        UPNumberBox(props = UPNumberBoxProps(modelValue = 3, min = 0, max = 9, longPress = true))
    }
}

@PreviewTest
@Preview(
    name = "field parity subsection modes",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF,
    widthDp = 360,
    heightDp = 240,
)
@Composable
fun UPFieldParitySubsectionScreenshot() {
    Column(
        Modifier.fillMaxSize().background(Color.White).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        UPSubsection(UPSubsectionProps(list = listOf("日", "周", "月"), current = 0))
        UPSubsection(UPSubsectionProps(list = listOf("日", "周", "月"), current = 1, mode = "subsection"))
        UPSubsection(UPSubsectionProps(list = listOf("日", "周", "月"), current = 2, mode = "subsection"))
        UPSubsection(UPSubsectionProps(list = listOf("日", "周", "月"), current = 1, disabled = true))
    }
}

@PreviewTest
@Preview(
    name = "motion parity notice collapse sticky",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF,
    widthDp = 360,
    heightDp = 320,
)
@Composable
fun UPMotionParityScreenshot() {
    Column(
        Modifier.fillMaxSize().background(Color.White),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        UPNoticeBar(UPNoticeBarProps(text = listOf("系统维护通知，请及时保存"), direction = "row"))
        UPNoticeBar(
            UPNoticeBarProps(
                text = listOf("第一条通知", "第二条通知"),
                direction = "column",
                mode = "closable",
            ),
        )
        UPSticky(UPStickyProps(offsetTop = 8, bgColor = "#f3f4f6")) {
            BasicText("吸顶区域")
        }
        UPCollapseItem(props = UPCollapseItemProps(title = "展开的面板", open = true, isLink = true)) {
            BasicText("面板内容")
        }
        UPCollapseItem(props = UPCollapseItemProps(title = "收起的面板", open = false, isLink = true)) {
            BasicText("隐藏内容")
        }
    }
}

@PreviewTest
@Preview(
    name = "motion parity slider geometry",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF,
    widthDp = 360,
    heightDp = 240,
)
@Composable
fun UPMotionParitySliderScreenshot() {
    Column(
        Modifier.fillMaxSize().background(Color.White).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        UPSlider(UPSliderProps(value = 40))
        UPSlider(UPSliderProps(value = 40, height = 8, length = 220))
        UPSlider(
            UPSliderProps(
                value = 60,
                blockStyle = mapOf("width" to "30px", "height" to "30px", "borderRadius" to "6px"),
            ),
        )
        UPSlider(UPSliderProps(isRange = true, rangeValue = listOf(20, 70), showValue = true))
    }
}
