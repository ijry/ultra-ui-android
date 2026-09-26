package net.lingyun.ultraui.android.sample.catalog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.lingyun.ultraui.android.components.UPAlbum
import net.lingyun.ultraui.android.components.UPAlbumProps
import net.lingyun.ultraui.android.components.UPButton
import net.lingyun.ultraui.android.components.UPButtonProps
import net.lingyun.ultraui.android.components.UPCalendar
import net.lingyun.ultraui.android.components.UPCalendarProps
import net.lingyun.ultraui.android.components.UPCascader
import net.lingyun.ultraui.android.components.UPCascaderProps
import net.lingyun.ultraui.android.components.UPCheckbox
import net.lingyun.ultraui.android.components.UPCheckboxGroup
import net.lingyun.ultraui.android.components.UPCheckboxGroupProps
import net.lingyun.ultraui.android.components.UPCheckboxProps
import net.lingyun.ultraui.android.components.UPChoose
import net.lingyun.ultraui.android.components.UPChooseProps
import net.lingyun.ultraui.android.components.UPCode
import net.lingyun.ultraui.android.components.UPCodeProps
import net.lingyun.ultraui.android.components.UPDatetimePicker
import net.lingyun.ultraui.android.components.UPDatetimePickerProps
import net.lingyun.ultraui.android.components.UPForm
import net.lingyun.ultraui.android.components.UPFormItem
import net.lingyun.ultraui.android.components.UPFormItemProps
import net.lingyun.ultraui.android.components.UPFormProps
import net.lingyun.ultraui.android.components.UPFormRule
import net.lingyun.ultraui.android.components.UPInput
import net.lingyun.ultraui.android.components.UPInputProps
import net.lingyun.ultraui.android.components.UPKeyboard
import net.lingyun.ultraui.android.components.UPKeyboardProps
import net.lingyun.ultraui.android.components.UPNumberBox
import net.lingyun.ultraui.android.components.UPNumberBoxProps
import net.lingyun.ultraui.android.components.UPPicker
import net.lingyun.ultraui.android.components.UPPickerProps
import net.lingyun.ultraui.android.components.UPRadio
import net.lingyun.ultraui.android.components.UPRadioGroup
import net.lingyun.ultraui.android.components.UPRadioGroupProps
import net.lingyun.ultraui.android.components.UPRadioProps
import net.lingyun.ultraui.android.components.UPRate
import net.lingyun.ultraui.android.components.UPRateProps
import net.lingyun.ultraui.android.components.UPSearch
import net.lingyun.ultraui.android.components.UPSearchProps
import net.lingyun.ultraui.android.components.UPSelect
import net.lingyun.ultraui.android.components.UPSelectProps
import net.lingyun.ultraui.android.components.UPSlider
import net.lingyun.ultraui.android.components.UPSliderProps
import net.lingyun.ultraui.android.components.UPSwitch
import net.lingyun.ultraui.android.components.UPTextarea
import net.lingyun.ultraui.android.components.UPTextareaProps
import net.lingyun.ultraui.android.components.UPUpload
import net.lingyun.ultraui.android.components.UPUploadProps
import net.lingyun.ultraui.android.components.rememberUPCodeController
import net.lingyun.ultraui.android.components.rememberUPFormController
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.sample.DemoSection

/** 表单组件分组的逐组件 demo；未命中返回 false 交回主分发器。 */
@Composable
internal fun renderFormGroup(id: String, onEvent: (String) -> Unit): Boolean {
    when (id) {
        "form" -> FormDemo(onEvent)
        "calendar" -> CalendarDemo()
        "keyboard" -> KeyboardDemo(onEvent)
        "picker" -> PickerDemo()
        "select" -> SelectDemo(onEvent)
        "cascader" -> CascaderDemo(onEvent)
        "choose" -> ChooseDemo(onEvent)
        "datetime-picker" -> DatetimePickerDemo()
        "rate" -> RateDemo(onEvent)
        "search" -> SearchDemo(onEvent)
        "number-box" -> NumberBoxDemo(onEvent)
        "upload" -> UploadDemo(onEvent)
        "code" -> CodeDemo()
        "input" -> InputDemo(onEvent)
        "textarea" -> TextareaDemo(onEvent)
        "checkbox" -> CheckboxDemo(onEvent)
        "radio" -> RadioDemo(onEvent)
        "switch" -> SwitchDemo(onEvent)
        "slider" -> SliderDemo()
        "album" -> AlbumDemo(onEvent)
        else -> return false
    }
    return true
}

@Composable
private fun InputDemo(onEvent: (String) -> Unit) {
    var value by remember { mutableStateOf("UltraUI") }
    DemoSection(title = "输入框") {
        UPInput(
            props = UPInputProps(modelValue = value, placeholder = "请输入内容", clearable = true),
            onInput = { value = it; onEvent("输入框：$it") },
            onClear = { onEvent("输入框：清空") },
        )
    }
}

@Composable
private fun TextareaDemo(onEvent: (String) -> Unit) {
    var value by remember { mutableStateOf("多行文本") }
    DemoSection(title = "文本域") {
        UPTextarea(
            props = UPTextareaProps(modelValue = value, placeholder = "请输入多行内容", count = true, maxlength = 100),
            onInput = { value = it; onEvent("文本域：$it") },
        )
    }
}

@Composable
private fun SearchDemo(onEvent: (String) -> Unit) {
    var value by remember { mutableStateOf("组件") }
    DemoSection(title = "搜索") {
        UPSearch(
            props = UPSearchProps(modelValue = value, placeholder = "搜索"),
            onChange = { value = it },
            onSearch = { onEvent("搜索：$value") },
            onCustom = { onEvent("搜索：点击搜索按钮") },
        )
    }
}

@Composable
private fun RateDemo(onEvent: (String) -> Unit) {
    var value by remember { mutableFloatStateOf(3f) }
    DemoSection(title = "评分") {
        UPRate(
            props = UPRateProps(modelValue = value, value = value, allowHalf = true),
            onInput = { value = it; onEvent("评分：$it") },
            onChange = { onEvent("评分确认：$it") },
        )
    }
}

@Composable
private fun NumberBoxDemo(onEvent: (String) -> Unit) {
    var value by remember { mutableStateOf<UPRawValue>(2) }
    DemoSection(title = "步进器") {
        UPNumberBox(
            props = UPNumberBoxProps(modelValue = value, value = value, min = 0, max = 9),
            onInput = { value = it; onEvent("步进器：$it") },
            onOverlimit = { onEvent("步进器：超出范围") },
        )
    }
}

@Composable
private fun SwitchDemo(onEvent: (String) -> Unit) {
    var value by remember { mutableStateOf(true) }
    DemoSection(title = "开关") {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            UPSwitch(value = value, onChange = { value = it; onEvent("开关：$it") })
            BasicText("当前：$value")
        }
    }
}

@Composable
private fun CheckboxDemo(onEvent: (String) -> Unit) {
    var single by remember { mutableStateOf(true) }
    var values by remember { mutableStateOf<List<UPRawValue>>(listOf("apple")) }
    Column {
        DemoSection(title = "单个复选框") {
            UPCheckbox(checked = single, name = "single", label = "同意协议", onUpdateChecked = { single = it; onEvent("复选框：$it") })
        }
        DemoSection(title = "复选框组") {
            UPCheckboxGroup(
                props = UPCheckboxGroupProps(modelValue = values, placement = "row"),
                onInput = { values = it; onEvent("复选框组：${it.joinToString()}") },
            ) {
                UPCheckbox(props = UPCheckboxProps(name = "apple", label = "苹果"))
                UPCheckbox(props = UPCheckboxProps(name = "banana", label = "香蕉"))
            }
        }
    }
}

@Composable
private fun RadioDemo(onEvent: (String) -> Unit) {
    var value by remember { mutableStateOf<UPRawValue>("android") }
    DemoSection(title = "单选框") {
        UPRadioGroup(props = UPRadioGroupProps(modelValue = value, placement = "row"), onChange = { value = it; onEvent("单选框：$it") }) {
            UPRadio(props = UPRadioProps(name = "android", label = "Android"))
            UPRadio(props = UPRadioProps(name = "ios", label = "iOS"))
        }
    }
}

@Composable
private fun ChooseDemo(onEvent: (String) -> Unit) {
    var index by remember { mutableStateOf(0) }
    DemoSection(title = "选项选择器") {
        UPChoose(
            props = UPChooseProps(
                options = listOf(
                    mapOf("title" to "北京", "value" to "bj"),
                    mapOf("title" to "上海", "value" to "sh"),
                    mapOf("title" to "广州", "value" to "gz"),
                ),
                modelValue = index,
            ),
            onUpdateModelValue = { index = it; onEvent("选项选择器：$it") },
        )
    }
}

@Composable
private fun KeyboardDemo(onEvent: (String) -> Unit) {
    var show by remember { mutableStateOf(false) }
    DemoSection(title = "键盘") {
        UPButton(props = UPButtonProps(text = "弹出键盘", type = "primary", size = "small"), onClick = { show = true })
        UPKeyboard(
            props = UPKeyboardProps(show = show, mode = "number"),
            onChange = { onEvent("键盘：$it") },
            onConfirm = { show = false; onEvent("键盘：完成") },
            onCancel = { show = false; onEvent("键盘：取消") },
            onClose = { show = false },
            onUpdateShow = { show = it },
        )
    }
}

@Composable
private fun SliderDemo() {
    var value by remember { mutableStateOf<UPRawValue>(35) }
    DemoSection(title = "滑块") {
        UPSlider(props = UPSliderProps(value = value, step = 5, showValue = true), onUpdateValue = { value = it })
        BasicText("当前值：$value")
    }
}

@Composable
private fun CalendarDemo() {
    DemoSection(title = "日历") {
        UPCalendar(UPCalendarProps(pageInline = true, defaultDate = "2026-08-20"))
    }
}

@Composable
private fun DatetimePickerDemo() {
    DemoSection(title = "时间选择器") {
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
}

@Composable
private fun PickerDemo() {
    DemoSection(title = "选择器") {
        UPPicker(
            UPPickerProps(
                show = true,
                title = "城市",
                columns = listOf(listOf(mapOf("text" to "北京", "value" to "bj"), mapOf("text" to "上海", "value" to "sh"))),
            ),
        )
    }
}

@Composable
private fun SelectDemo(onEvent: (String) -> Unit) {
    var select by remember { mutableStateOf<UPRawValue>(1) }
    DemoSection(title = "下拉框") {
        UPSelect(
            UPSelectProps(options = listOf(mapOf("id" to 1, "name" to "北京"), mapOf("id" to 2, "name" to "上海")), current = select),
            onUpdateCurrent = { select = it; onEvent("下拉框：$it") },
        )
    }
}

@Composable
private fun CascaderDemo(onEvent: (String) -> Unit) {
    var value by remember { mutableStateOf<List<UPRawValue>>(emptyList()) }
    val data = remember {
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
    DemoSection(title = "级联选择器") {
        UPCascader(
            props = UPCascaderProps(show = true, data = data, modelValue = value),
            onUpdateModelValue = { value = it; onEvent("级联：${it.joinToString()}") },
        )
    }
}

@Composable
private fun CodeDemo() {
    val code = rememberUPCodeController()
    DemoSection(title = "验证码倒计时") {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            UPCode(UPCodeProps(seconds = 60), controller = code) { text -> BasicText(text) }
            UPButton(props = UPButtonProps(text = "获取验证码", type = "primary", size = "small"), onClick = { code.start() })
        }
    }
}

@Composable
private fun UploadDemo(onEvent: (String) -> Unit) {
    var files by remember {
        mutableStateOf(listOf<Map<String, Any?>>(mapOf("url" to "/sdcard/demo1.png"), mapOf("url" to "/sdcard/demo2.png", "status" to "uploading")))
    }
    DemoSection(title = "上传") {
        UPUpload(
            props = UPUploadProps(fileList = files, uploadText = "上传"),
            onAddClick = { onEvent("上传：请在宿主打开选择器") },
            onDelete = { idx -> files = files.filterIndexed { i, _ -> i != idx }; onEvent("上传：删除第 $idx 项") },
        )
    }
}

@Composable
private fun AlbumDemo(onEvent: (String) -> Unit) {
    DemoSection(title = "相册") {
        UPAlbum(
            props = UPAlbumProps(
                urls = listOf("/sdcard/p1.png", "/sdcard/p2.png", "/sdcard/p3.png", "/sdcard/p4.png", "/sdcard/p5.png", "/sdcard/p6.png", "/sdcard/p7.png"),
                maxCount = 6,
            ),
            onClick = { onEvent("相册：点击第 $it 张") },
        )
    }
}

@Composable
private fun FormDemo(onEvent: (String) -> Unit) {
    val controller = rememberUPFormController()
    var model by remember { mutableStateOf(mapOf<String, UPRawValue>("name" to "张三", "phone" to "")) }
    var validateText by remember { mutableStateOf("尚未校验") }
    val rules = remember {
        mapOf<String, UPRawValue>(
            "name" to listOf(
                UPFormRule(required = true, message = "请填写姓名", trigger = listOf("blur", "change")),
                UPFormRule(min = 2, message = "姓名至少 2 个字", trigger = listOf("blur")),
            ),
            "phone" to UPFormRule(pattern = "^1\\d{10}$", message = "请填写 11 位手机号", trigger = listOf("blur")),
        )
    }
    DemoSection(title = "表单校验") {
        UPForm(
            props = UPFormProps(model = model, rules = rules, labelWidth = 70),
            controller = controller,
            onUpdateModel = { model = it },
        ) {
            UPFormItem(props = UPFormItemProps(label = "姓名", prop = "name", required = true)) {
                UPInput(
                    props = UPInputProps(modelValue = model["name"], placeholder = "请填写姓名", border = "none"),
                    onInput = { model = model + ("name" to it) },
                    onBlur = { controller.validateField(listOf("name"), "blur") },
                )
            }
            UPFormItem(props = UPFormItemProps(label = "手机号", prop = "phone", leftIcon = "phone", borderBottom = false)) {
                UPInput(
                    props = UPInputProps(modelValue = model["phone"], type = "number", placeholder = "失焦按 blur 校验", border = "none"),
                    onInput = { model = model + ("phone" to it) },
                    onBlur = { controller.validateField(listOf("phone"), "blur") },
                )
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            UPButton(
                props = UPButtonProps(text = "校验", type = "primary", size = "small"),
                onClick = {
                    val errors = controller.validate()
                    validateText = if (errors.isEmpty()) "校验通过" else "首个错误：${errors.first().message}"
                    onEvent("表单：$validateText")
                },
            )
            BasicText(validateText)
        }
    }
}
