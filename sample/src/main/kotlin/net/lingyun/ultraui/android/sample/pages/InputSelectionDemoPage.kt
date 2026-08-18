package net.lingyun.ultraui.android.sample.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import net.lingyun.ultraui.android.components.UPCheckbox
import net.lingyun.ultraui.android.components.UPCheckboxGroup
import net.lingyun.ultraui.android.components.UPCheckboxGroupProps
import net.lingyun.ultraui.android.components.UPCheckboxProps
import net.lingyun.ultraui.android.components.UPCodeInput
import net.lingyun.ultraui.android.components.UPCodeInputProps
import net.lingyun.ultraui.android.components.UPInput
import net.lingyun.ultraui.android.components.UPInputProps
import net.lingyun.ultraui.android.components.UPNumberBox
import net.lingyun.ultraui.android.components.UPNumberBoxProps
import net.lingyun.ultraui.android.components.UPRadio
import net.lingyun.ultraui.android.components.UPRadioGroup
import net.lingyun.ultraui.android.components.UPRadioGroupProps
import net.lingyun.ultraui.android.components.UPRadioProps
import net.lingyun.ultraui.android.components.UPRate
import net.lingyun.ultraui.android.components.UPRateProps
import net.lingyun.ultraui.android.components.UPSearch
import net.lingyun.ultraui.android.components.UPSearchProps
import net.lingyun.ultraui.android.components.UPSwitch
import net.lingyun.ultraui.android.components.UPTextarea
import net.lingyun.ultraui.android.components.UPTextareaProps
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.sample.DemoSection
import net.lingyun.ultraui.android.sample.SampleScaffold

/** Public API demos for form input and selection components. */
@Composable
public fun InputSelectionDemoPage(onBack: () -> Unit, modifier: Modifier = Modifier) {
    var eventText by remember { mutableStateOf("等待输入与选择交互") }
    var inputValue by remember { mutableStateOf("UltraUI") }
    var textareaValue by remember { mutableStateOf("多行文本") }
    var searchValue by remember { mutableStateOf("组件") }
    var codeValue by remember { mutableStateOf("12") }
    var switchValue by remember { mutableStateOf(true) }
    var rateValue by remember { mutableFloatStateOf(3f) }
    var numberValue by remember { mutableStateOf<UPRawValue>(2) }
    var singleChecked by remember { mutableStateOf(true) }
    var checkboxValues by remember { mutableStateOf<List<UPRawValue>>(listOf("apple")) }
    var radioValue by remember { mutableStateOf<UPRawValue>("android") }

    SampleScaffold(title = "输入与选择", onBack = onBack, modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            DemoEventText(eventText)

            DemoSection(title = "输入框") {
                UPInput(
                    props = UPInputProps(modelValue = inputValue, placeholder = "请输入内容", clearable = true),
                    onInput = {
                        inputValue = it
                        eventText = "输入框：$it"
                    },
                    onClear = { eventText = "输入框：清空" },
                )
            }

            DemoSection(title = "文本域") {
                UPTextarea(
                    props = UPTextareaProps(modelValue = textareaValue, placeholder = "请输入多行文本", count = true, maxlength = 80),
                    onInput = {
                        textareaValue = it
                        eventText = "文本域：${it.length} 字"
                    },
                )
            }

            DemoSection(title = "搜索框") {
                UPSearch(
                    props = UPSearchProps(modelValue = searchValue, placeholder = "搜索组件", showAction = true, actionText = "搜索"),
                    onInput = {
                        searchValue = it
                        eventText = "搜索框：$it"
                    },
                    onSearch = { eventText = "搜索框：提交 $searchValue" },
                    onCustom = { eventText = "搜索框：动作按钮" },
                )
            }

            DemoSection(title = "验证码输入") {
                UPCodeInput(
                    props = UPCodeInputProps(modelValue = codeValue, maxlength = 4, mode = "box"),
                    onInput = {
                        codeValue = it
                        eventText = "验证码输入：$it"
                    },
                    onFinish = { eventText = "验证码输入：完成 $it" },
                )
            }

            DemoSection(title = "开关") {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    UPSwitch(value = switchValue, onChange = {
                        switchValue = it
                        eventText = "开关：$it"
                    })
                    Text("当前：$switchValue", color = UPTheme.Content)
                }
            }

            DemoSection(title = "评分") {
                UPRate(
                    props = UPRateProps(modelValue = rateValue, value = rateValue, allowHalf = true),
                    onInput = {
                        rateValue = it
                        eventText = "评分：$it"
                    },
                    onChange = { eventText = "评分确认：$it" },
                )
            }

            DemoSection(title = "步进器") {
                UPNumberBox(
                    props = UPNumberBoxProps(modelValue = numberValue, value = numberValue, min = 0, max = 9),
                    onInput = {
                        numberValue = it
                        eventText = "步进器：$it"
                    },
                    onOverlimit = { eventText = "步进器：超出范围" },
                )
            }

            DemoSection(title = "复选框") {
                UPCheckbox(
                    checked = singleChecked,
                    name = "single",
                    label = "单个复选框",
                    onUpdateChecked = {
                        singleChecked = it
                        eventText = "复选框：$it"
                    },
                )
            }

            DemoSection(title = "复选框组") {
                UPCheckboxGroup(
                    props = UPCheckboxGroupProps(modelValue = checkboxValues, placement = "row"),
                    onInput = {
                        checkboxValues = it
                        eventText = "复选框组：${it.joinToString()}"
                    },
                ) {
                    UPCheckbox(props = UPCheckboxProps(name = "apple", label = "苹果"))
                    UPCheckbox(props = UPCheckboxProps(name = "banana", label = "香蕉"))
                }
            }

            DemoSection(title = "单选框") {
                UPRadioGroup(props = UPRadioGroupProps(modelValue = radioValue, placement = "row"), onInput = {
                    radioValue = it
                    eventText = "单选框：$it"
                }) {
                    UPRadio(props = UPRadioProps(name = "android", label = "Android"))
                }
            }

            DemoSection(title = "单选框组") {
                UPRadioGroup(props = UPRadioGroupProps(modelValue = radioValue, placement = "row"), onChange = {
                    radioValue = it
                    eventText = "单选框组：$it"
                }) {
                    UPRadio(props = UPRadioProps(name = "android", label = "Android"))
                    UPRadio(props = UPRadioProps(name = "ios", label = "iOS"))
                }
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
