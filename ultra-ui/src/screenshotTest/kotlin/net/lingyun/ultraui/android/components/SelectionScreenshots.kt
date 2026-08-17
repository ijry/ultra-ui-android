package net.lingyun.ultraui.android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.tools.screenshot.PreviewTest

@PreviewTest
@Preview(
    name = "u-selection switch and rate",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF,
    widthDp = 360,
    heightDp = 190,
)
@Composable
public fun UPSelectionSwitchRateScreenshot(): Unit = SelectionScreenshotSurface {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            UPSwitch(
                props = UPSwitchProps(modelValue = false),
            )
            UPSwitch(
                props = UPSwitchProps(modelValue = true),
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            UPRate(props = UPRateProps(modelValue = 0, count = 5))
            UPRate(props = UPRateProps(modelValue = 2.5, count = 5, allowHalf = true))
            UPRate(props = UPRateProps(modelValue = 5, count = 5))
        }
    }
}

@PreviewTest
@Preview(
    name = "u-number-box limits",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF,
    widthDp = 360,
    heightDp = 170,
)
@Composable
public fun UPNumberBoxLimitsScreenshot(): Unit = SelectionScreenshotSurface {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        UPNumberBox(
            props = UPNumberBoxProps(modelValue = 1, min = 1, max = 5),
        )
        UPNumberBox(
            props = UPNumberBoxProps(modelValue = 3.5, min = 1, max = 5, decimalLength = 1),
        )
        UPNumberBox(
            props = UPNumberBoxProps(modelValue = 5, min = 1, max = 5, disabled = true),
        )
    }
}

@PreviewTest
@Preview(
    name = "u-checkbox and radio placements",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF,
    widthDp = 360,
    heightDp = 280,
)
@Composable
public fun UPCheckboxRadioPlacementsScreenshot(): Unit = SelectionScreenshotSurface {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        UPCheckboxGroup(
            props = UPCheckboxGroupProps(
                modelValue = listOf("group-circle"),
                placement = "row",
                shape = "circle",
            ),
        ) {
            UPCheckbox(UPCheckboxProps(name = "group-circle", label = "继承圆形"))
            UPCheckbox(UPCheckboxProps(name = "leaf-square", label = "子项方形", shape = "square"))
        }
        UPCheckboxGroup(
            props = UPCheckboxGroupProps(
                modelValue = listOf("column"),
                placement = "column",
                borderBottom = true,
            ),
        ) {
            UPCheckbox(UPCheckboxProps(name = "column", label = "纵向已选", shape = "circle"))
            UPCheckbox(UPCheckboxProps(name = "column-off", label = "纵向未选", shape = "square"))
        }
        UPRadioGroup(
            props = UPRadioGroupProps(
                modelValue = "radio-square",
                placement = "row",
                gap = "8px",
                shape = "square",
            ),
        ) {
            UPRadio(UPRadioProps(name = "radio-square", label = "继承方形"))
            UPRadio(UPRadioProps(name = "radio-circle", label = "子项圆形", shape = "circle"))
        }
        UPRadioGroup(
            props = UPRadioGroupProps(
                modelValue = "radio-column",
                placement = "column",
                borderBottom = true,
                iconPlacement = "right",
            ),
        ) {
            UPRadio(UPRadioProps(name = "radio-column", label = "纵向单选"))
            UPRadio(UPRadioProps(name = "radio-column-off", label = "未选项"))
        }
    }
}

@Composable
private fun SelectionScreenshotSurface(content: @Composable () -> Unit): Unit {
    androidx.compose.foundation.layout.Box(
        modifier = Modifier.background(Color.White),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}
