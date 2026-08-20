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
@Preview(name = "batch 9b navigation and status", showBackground = true, backgroundColor = 0xFFFFFFFF, widthDp = 360, heightDp = 360)
@Composable
fun UPBatch9BNavigationScreenshot() {
    Column(Modifier.fillMaxSize().background(Color.White).padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        UPNavbar(UPNavbarProps(title = "消息中心", leftText = "返回", rightText = "完成"))
        UPStatusBar(UPStatusBarProps(bgColor = "#f3f4f6", height = 8))
        UPTabs(UPTabsProps(list = listOf("全部", "未读", "已读")))
        UPSubsection(UPSubsectionProps(list = listOf("日", "周", "月"), current = 1))
        UPSteps(UPStepsProps(current = 1)) {
            UPStepsItem(UPStepsItemProps(title = "提交", desc = "已完成"))
            UPStepsItem(UPStepsItemProps(title = "审核", desc = "处理中"))
        }
    }
}

@PreviewTest
@Preview(name = "batch 9b status and picker", showBackground = true, backgroundColor = 0xFFFFFFFF, widthDp = 360, heightDp = 420)
@Composable
fun UPBatch9BStatusScreenshot() {
    Column(Modifier.fillMaxSize().background(Color.White).padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        UPSkeleton(UPSkeletonProps(rows = 3, avatar = true))
        UPColumnNotice(UPColumnNoticeProps(text = listOf("第一条通知", "第二条通知")))
        UPRowNotice(UPRowNoticeProps(text = "系统维护通知", mode = "link"))
        UPPicker(UPPickerProps(show = true, title = "选择城市", columns = listOf(listOf("北京", "上海"))))
        BasicText("组件 9B")
    }
}
