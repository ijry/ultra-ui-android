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

private val cityColumns = listOf(listOf(mapOf("text" to "北京", "value" to "bj"), mapOf("text" to "上海", "value" to "sh")))

@PreviewTest
@Preview(name = "picker has input triggers", showBackground = true, backgroundColor = 0xFFFFFFFF, widthDp = 360, heightDp = 260)
@Composable
fun UPPickerInputTriggerScreenshot() {
    Column(Modifier.fillMaxSize().background(Color.White).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        BasicText("默认边框")
        UPPicker(UPPickerProps(hasInput = true, columns = cityColumns, modelValue = listOf("sh")))
        BasicText("下边框 + inputProps 覆盖")
        UPPicker(
            UPPickerProps(
                hasInput = true,
                columns = cityColumns,
                inputBorder = "bottom",
                inputProps = mapOf("placeholder" to "请选择出发城市", "inputAlign" to "right", "suffixIcon" to "arrow-down"),
            ),
        )
        BasicText("禁用态")
        UPPicker(UPPickerProps(hasInput = true, columns = cityColumns, disabled = true, disabledColor = "#f5f7fa"))
    }
}

@PreviewTest
@Preview(name = "picker toolbar right slot and mask", showBackground = true, backgroundColor = 0xFFFFFFFF, widthDp = 360, heightDp = 320)
@Composable
fun UPPickerToolbarRightScreenshot() {
    Column(Modifier.fillMaxSize().background(Color.White).padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        UPPicker(
            props = UPPickerProps(
                show = true,
                title = "选择城市",
                round = 12,
                columns = cityColumns,
                toolbarRightSlot = true,
                visibleItemCount = 3,
                maskStyle = mapOf("backgroundColor" to "rgba(0, 0, 0, 0.06)"),
            ),
            toolbarRight = { UPButton(UPButtonProps(text = "重置", size = "mini", type = "primary", plain = true)) },
        )
    }
}

@PreviewTest
@Preview(name = "datetime picker has input trigger", showBackground = true, backgroundColor = 0xFFFFFFFF, widthDp = 360, heightDp = 200)
@Composable
fun UPDatetimePickerInputTriggerScreenshot() {
    Column(Modifier.fillMaxSize().background(Color.White).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        BasicText("日期模式")
        UPDatetimePicker(UPDatetimePickerProps(hasInput = true, mode = "date", value = "2026-08-20", format = "YYYY年MM月DD日"))
        BasicText("时间模式")
        UPDatetimePicker(UPDatetimePickerProps(hasInput = true, mode = "time", value = "09:30", inputBorder = "bottom"))
    }
}
