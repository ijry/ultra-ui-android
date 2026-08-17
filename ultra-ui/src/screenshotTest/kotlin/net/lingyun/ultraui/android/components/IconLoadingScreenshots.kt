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
    name = "u-icon labels",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF,
    widthDp = 320,
    heightDp = 160,
)
@Composable
public fun UPIconLabelsScreenshot(): Unit = ScreenshotSurface {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        UPIcon(
            props = UPIconProps(
                name = "map",
                color = "#2979ff",
                size = "30px",
                label = "地图",
                labelPos = "right",
            ),
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            UPIcon(
                props = UPIconProps(
                    name = "checkmark-circle-fill",
                    color = "#19be6b",
                    size = "28px",
                    label = "左侧",
                    labelPos = "left",
                ),
            )
            UPIcon(
                props = UPIconProps(
                    name = "star-fill",
                    color = "#ff9900",
                    size = "28px",
                    label = "下方",
                    labelPos = "bottom",
                ),
            )
        }
    }
}

@PreviewTest
@Preview(
    name = "u-loading-icon modes",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF,
    widthDp = 320,
    heightDp = 150,
)
@Composable
public fun UPLoadingIconModesScreenshot(): Unit = ScreenshotSurface {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        UPLoadingIcon(
            props = UPLoadingIconProps(
                mode = "spinner",
                color = "#2979ff",
                text = "加载中",
            ),
        )
        UPLoadingIcon(
            props = UPLoadingIconProps(
                mode = "semicircle",
                color = "#19be6b",
                text = "半圆",
                vertical = true,
            ),
        )
        UPLoadingIcon(
            props = UPLoadingIconProps(
                mode = "circle",
                color = "#fa3534",
                text = "圆形",
                vertical = true,
            ),
        )
    }
}

@Composable
private fun ScreenshotSurface(content: @Composable () -> Unit): Unit {
    androidx.compose.foundation.layout.Box(
        modifier = Modifier.background(Color.White),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}
