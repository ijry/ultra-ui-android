package net.lingyun.ultraui.android.sample.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.lingyun.ultraui.android.components.UPCalendar
import net.lingyun.ultraui.android.components.UPCalendarProps
import net.lingyun.ultraui.android.components.UPCascader
import net.lingyun.ultraui.android.components.UPCascaderProps
import net.lingyun.ultraui.android.components.UPDatetimePicker
import net.lingyun.ultraui.android.components.UPDatetimePickerProps
import net.lingyun.ultraui.android.components.UPSlider
import net.lingyun.ultraui.android.components.UPSliderProps
import net.lingyun.ultraui.android.components.UPTabbar
import net.lingyun.ultraui.android.components.UPTabbarItem
import net.lingyun.ultraui.android.components.UPTabbarItemProps
import net.lingyun.ultraui.android.components.UPTabbarProps
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.sample.DemoSection
import net.lingyun.ultraui.android.sample.SampleScaffold

/** Native demonstrations for calendar, picker, cascader, slider and tabbar contracts. */
@Composable
public fun SelectionNavigationDemoPage(onBack: () -> Unit, modifier: Modifier = Modifier) {
    var sliderValue by remember { mutableStateOf<UPRawValue>(35) }
    var tabbarValue by remember { mutableStateOf<UPRawValue>("home") }
    var cascaderValue by remember { mutableStateOf<List<UPRawValue>>(emptyList()) }
    val cascaderData = remember {
        listOf(
            mapOf(
                "value" to "zhejiang",
                "label" to "浙江",
                "children" to listOf(
                    mapOf("value" to "hangzhou", "label" to "杭州"),
                    mapOf("value" to "ningbo", "label" to "宁波"),
                ),
            ),
            mapOf(
                "value" to "jiangsu",
                "label" to "江苏",
                "children" to listOf(mapOf("value" to "nanjing", "label" to "南京")),
            ),
        )
    }

    SampleScaffold(title = "选择与底部导航", onBack = onBack, modifier = modifier) {
        Column(
            Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            DemoSection("日历") {
                UPCalendar(UPCalendarProps(pageInline = true, defaultDate = "2026-08-20"))
            }
            DemoSection("日期时间选择器") {
                UPDatetimePicker(
                    UPDatetimePickerProps(
                        pageInline = true,
                        mode = "time",
                        minHour = 8,
                        maxHour = 10,
                        minMinute = 0,
                        maxMinute = 2,
                        minSecond = 0,
                        maxSecond = 2,
                    ),
                )
            }
            DemoSection("日期时间输入触发器") {
                UPDatetimePicker(
                    UPDatetimePickerProps(
                        hasInput = true,
                        placeholder = "请选择日期",
                        mode = "date",
                        value = "2026-08-20",
                    ),
                )
            }
            DemoSection("级联选择器") {
                UPCascader(
                    props = UPCascaderProps(show = true, data = cascaderData, modelValue = cascaderValue),
                    onUpdateModelValue = { cascaderValue = it },
                )
            }
            DemoSection("滑块") {
                UPSlider(
                    props = UPSliderProps(value = sliderValue, step = 5, showValue = true),
                    onUpdateValue = { sliderValue = it },
                )
                BasicText("当前值：$sliderValue")
            }
            DemoSection("底部导航 / 底部导航项") {
                UPTabbar(
                    props = UPTabbarProps(value = tabbarValue, fixed = false, placeholder = false),
                    onUpdateValue = { tabbarValue = it },
                ) {
                    UPTabbarItem(UPTabbarItemProps(name = "home", icon = "home", text = "首页"))
                    UPTabbarItem(UPTabbarItemProps(name = "message", icon = "chat", text = "消息", badge = 3))
                    UPTabbarItem(UPTabbarItemProps(name = "mine", icon = "account", text = "我的"))
                }
            }
        }
    }
}
