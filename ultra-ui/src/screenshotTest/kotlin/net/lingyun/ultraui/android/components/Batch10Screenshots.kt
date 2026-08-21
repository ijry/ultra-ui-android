package net.lingyun.ultraui.android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.tools.screenshot.PreviewTest

@PreviewTest
@Preview(name = "batch 10 calendar and slider", showBackground = true, backgroundColor = 0xFFFFFFFF, widthDp = 360, heightDp = 420)
@Composable
public fun UPBatch10CalendarSliderScreenshot() {
    Column(
        Modifier.fillMaxSize().background(Color.White).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        UPCalendar(UPCalendarProps(pageInline = true, defaultDate = "2026-08-20"))
        UPSlider(UPSliderProps(value = 65, step = 5, showValue = true))
    }
}

@PreviewTest
@Preview(name = "batch 10 cascader and tabbar", showBackground = true, backgroundColor = 0xFFFFFFFF, widthDp = 360, heightDp = 360)
@Composable
public fun UPBatch10CascaderTabbarScreenshot() {
    val data = listOf(
        mapOf("value" to "zhejiang", "label" to "浙江", "children" to listOf(mapOf("value" to "hangzhou", "label" to "杭州"))),
        mapOf("value" to "jiangsu", "label" to "江苏", "children" to listOf(mapOf("value" to "nanjing", "label" to "南京"))),
    )
    Column(
        Modifier.fillMaxSize().background(Color.White).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        UPCascader(UPCascaderProps(show = true, data = data, modelValue = listOf("zhejiang", "hangzhou")))
        UPTabbar(UPTabbarProps(value = "home", fixed = false, placeholder = false)) {
            UPTabbarItem(UPTabbarItemProps(name = "home", icon = "home", text = "首页"))
            UPTabbarItem(UPTabbarItemProps(name = "mine", icon = "account", text = "我的", dot = true))
        }
        BasicText("Batch 10")
    }
}
