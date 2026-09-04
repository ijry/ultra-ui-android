package net.lingyun.ultraui.android.sample.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.lingyun.ultraui.android.components.UPButton
import net.lingyun.ultraui.android.components.UPButtonProps
import net.lingyun.ultraui.android.components.UPForm
import net.lingyun.ultraui.android.components.UPFormItem
import net.lingyun.ultraui.android.components.UPFormItemProps
import net.lingyun.ultraui.android.components.UPFormProps
import net.lingyun.ultraui.android.components.UPFormRule
import net.lingyun.ultraui.android.components.UPIcon
import net.lingyun.ultraui.android.components.UPInput
import net.lingyun.ultraui.android.components.UPInputProps
import net.lingyun.ultraui.android.components.UPSwitch
import net.lingyun.ultraui.android.components.rememberUPFormController
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.sample.DemoSection
import net.lingyun.ultraui.android.sample.SampleScaffold

/**
 * Native demonstrations for the `u-form` / `u-form-item` validation contract:
 * async-validator style rules, the six imperative `ref` methods, the `toast`
 * error channel, top labels with the required star and the three slots.
 */
@Composable
public fun FormValidationDemoPage(onBack: () -> Unit, modifier: Modifier = Modifier) {
    val controller = rememberUPFormController()
    val toastController = rememberUPFormController()
    val topController = rememberUPFormController()
    val slotController = rememberUPFormController()

    var model by remember {
        mutableStateOf(mapOf<String, UPRawValue>("name" to "张三", "phone" to "", "agree" to false))
    }
    var toastModel by remember { mutableStateOf(mapOf<String, UPRawValue>("code" to "")) }
    var topModel by remember { mutableStateOf(mapOf<String, UPRawValue>("address" to "")) }
    var slotModel by remember { mutableStateOf(mapOf<String, UPRawValue>("city" to "")) }

    var validateText by remember { mutableStateOf("尚未校验") }
    var toastText by remember { mutableStateOf("尚未收到 toast") }
    var slotText by remember { mutableStateOf("尚未点击") }

    val rules = remember {
        mapOf<String, UPRawValue>(
            "name" to listOf(
                UPFormRule(required = true, message = "请填写姓名", trigger = listOf("blur", "change")),
                UPFormRule(min = 2, message = "姓名至少 2 个字", trigger = listOf("blur")),
            ),
            "phone" to UPFormRule(
                pattern = "^1\\d{10}$",
                message = "请填写 11 位手机号",
                trigger = listOf("blur"),
            ),
            "agree" to UPFormRule(
                type = "boolean",
                message = "请先同意用户协议",
                validator = { _, value -> value == true },
            ),
        )
    }

    SampleScaffold(title = "表单与校验", onBack = onBack, modifier = modifier) {
        Column(
            Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            DemoSection(title = "表单校验") {
                UPForm(
                    props = UPFormProps(model = model, rules = rules, labelWidth = 70),
                    controller = controller,
                    onUpdateModel = { model = it },
                ) {
                    UPFormItem(props = UPFormItemProps(label = "姓名", prop = "name", required = true)) {
                        UPInput(
                            props = UPInputProps(
                                modelValue = model["name"],
                                placeholder = "请填写姓名",
                                border = "none",
                            ),
                            onInput = { model = model + ("name" to it) },
                            onBlur = { controller.validateField(listOf("name"), "blur") },
                        )
                    }
                    UPFormItem(props = UPFormItemProps(label = "手机号", prop = "phone", leftIcon = "phone")) {
                        UPInput(
                            props = UPInputProps(
                                modelValue = model["phone"],
                                type = "number",
                                placeholder = "失焦时按 blur 规则校验",
                                border = "none",
                            ),
                            onInput = { model = model + ("phone" to it) },
                            onBlur = { controller.validateField(listOf("phone"), "blur") },
                        )
                    }
                    UPFormItem(
                        props = UPFormItemProps(label = "用户协议", prop = "agree", borderBottom = false),
                    ) {
                        UPSwitch(value = model["agree"] == true, onChange = { model = model + ("agree" to it) })
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    UPButton(
                        props = UPButtonProps(text = "校验", type = "primary", size = "mini"),
                        onClick = {
                            val errors = controller.validate()
                            validateText = if (errors.isEmpty()) {
                                "校验通过"
                            } else {
                                "首个错误：${errors.first().message}（字段 ${errors.first().field}）"
                            }
                        },
                    )
                    UPButton(
                        props = UPButtonProps(text = "重置", size = "mini"),
                        onClick = {
                            controller.resetFields()
                            controller.clearValidate()
                            validateText = "已恢复首次观测到的 model"
                        },
                    )
                    UPButton(
                        props = UPButtonProps(text = "清除提示", size = "mini"),
                        onClick = {
                            controller.clearValidate()
                            validateText = "已清除错误提示"
                        },
                    )
                }
                BasicText(text = validateText)
            }

            DemoSection(title = "错误提示为 toast") {
                UPForm(
                    props = UPFormProps(
                        model = toastModel,
                        rules = mapOf<String, UPRawValue>(
                            "code" to UPFormRule(required = true, message = "请填写邀请码"),
                        ),
                        errorType = "toast",
                        labelWidth = 70,
                    ),
                    controller = toastController,
                    onUpdateModel = { toastModel = it },
                    onToast = { toastText = "toast：$it" },
                ) {
                    UPFormItem(
                        props = UPFormItemProps(label = "邀请码", prop = "code", borderBottom = false),
                    ) {
                        UPInput(
                            props = UPInputProps(
                                modelValue = toastModel["code"],
                                placeholder = "留空后点击校验",
                                border = "none",
                            ),
                            onInput = { toastModel = toastModel + ("code" to it) },
                        )
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    UPButton(
                        props = UPButtonProps(text = "触发校验", type = "warning", size = "mini"),
                        onClick = { toastController.validate() },
                    )
                }
                BasicText(text = "$toastText（errorType 为 toast 时行内不显示错误文案）")
            }

            DemoSection(title = "标签在上方") {
                UPForm(
                    props = UPFormProps(
                        model = topModel,
                        rules = mapOf<String, UPRawValue>(
                            "address" to UPFormRule(required = true, message = "请填写收货地址"),
                        ),
                        labelPosition = "top",
                        labelWidth = 100,
                    ),
                    controller = topController,
                    onUpdateModel = { topModel = it },
                ) {
                    UPFormItem(
                        props = UPFormItemProps(
                            label = "收货地址",
                            prop = "address",
                            required = true,
                            leftIcon = "map",
                            borderBottom = false,
                        ),
                    ) {
                        UPInput(
                            props = UPInputProps(
                                modelValue = topModel["address"],
                                placeholder = "请填写收货地址",
                                border = "none",
                            ),
                            onInput = { topModel = topModel + ("address" to it) },
                        )
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    UPButton(
                        props = UPButtonProps(text = "校验地址", size = "mini"),
                        onClick = { topController.validate() },
                    )
                    UPButton(
                        props = UPButtonProps(text = "重置该字段", size = "mini"),
                        onClick = { topController.resetField("address") },
                    )
                }
            }

            DemoSection(title = "标签、右侧与错误插槽") {
                UPForm(
                    props = UPFormProps(
                        model = slotModel,
                        rules = mapOf<String, UPRawValue>(
                            "city" to UPFormRule(required = true, message = "请选择城市"),
                        ),
                        labelWidth = 90,
                    ),
                    controller = slotController,
                    onUpdateModel = { slotModel = it },
                ) {
                    UPFormItem(
                        props = UPFormItemProps(prop = "city", borderBottom = false),
                        onClick = { slotText = "整行点击已触发" },
                        labelContent = {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                UPIcon(name = "map", size = "14px")
                                BasicText(text = "城市")
                            }
                        },
                        rightContent = { UPIcon(name = "arrow-right", size = "14px") },
                        errorContent = { message -> BasicText(text = "⚠ $message") },
                    ) {
                        BasicText(text = (slotModel["city"] as? String).orEmpty().ifEmpty { "请选择城市" })
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    UPButton(
                        props = UPButtonProps(text = "校验", type = "primary", size = "mini"),
                        onClick = { slotController.validate() },
                    )
                    UPButton(
                        props = UPButtonProps(text = "填入杭州", size = "mini"),
                        onClick = {
                            slotModel = slotModel + ("city" to "杭州")
                            slotController.clearValidate(listOf("city"))
                        },
                    )
                }
                BasicText(text = slotText)
            }
        }
    }
}
