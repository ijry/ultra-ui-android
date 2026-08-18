package net.lingyun.ultraui.android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.tools.screenshot.PreviewTest

@PreviewTest
@Preview(
    name = "native alert notify backtop",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF,
    widthDp = 360,
    heightDp = 260,
)
@Composable
public fun UPNativeAlertNotifyBackTopScreenshot(): Unit = NativeInteractionSurface {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        UPAlert(
            props = UPAlertProps(
                title = "系统提示",
                description = "请检查网络连接",
                type = "warning",
                showIcon = true,
                closable = true,
            ),
        )
        UPNotify(
            props = UPNotifyProps(message = "保存成功", duration = -1),
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            UPBackTop(props = UPBackTopProps(scrollTop = 800, text = "顶部"))
        }
    }
}

@PreviewTest
@Preview(
    name = "native card collapse dropdown",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF,
    widthDp = 360,
    heightDp = 420,
)
@Composable
public fun UPNativeCardCollapseDropdownScreenshot(): Unit = NativeInteractionSurface {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        UPCard(props = UPCardProps(title = "订单信息", subTitle = "今天 12:30", index = 1)) {
            BasicText("卡片主体内容")
        }
        UPCollapse(props = UPCollapseProps(value = listOf("one"))) {
            UPCollapseItem(UPCollapseItemProps(name = "one", title = "已展开面板")) {
                BasicText("面板内容")
            }
            UPCollapseItem(UPCollapseItemProps(name = "two", title = "未展开面板")) {
                BasicText("更多内容")
            }
        }
        UPDropdown {
            UPDropdownItem(
                UPDropdownItemProps(
                    title = "状态",
                    options = listOf(
                        mapOf("label" to "全部", "value" to "all"),
                        mapOf("label" to "已完成", "value" to "done"),
                    ),
                ),
            )
        }
        UPNoticeBar(props = UPNoticeBarProps(text = listOf("系统将于今晚维护"), mode = "link"))
    }
}

@PreviewTest
@Preview(
    name = "native action sheet",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF,
    widthDp = 360,
    heightDp = 420,
)
@Composable
public fun UPActionSheetScreenshot(): Unit = NativeInteractionSurface {
    UPActionSheet(
        props = UPActionSheetProps(
            show = true,
            title = "请选择操作",
            description = "选择后将立即执行",
            actions = listOf(
                mapOf("name" to "拍照", "subname" to "使用相机"),
                mapOf("name" to "从相册选择"),
            ),
            cancelText = "取消",
        ),
    )
}

@Composable
private fun NativeInteractionSurface(content: @Composable () -> Unit): Unit {
    androidx.compose.foundation.layout.Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.TopCenter,
    ) {
        content()
    }
}
