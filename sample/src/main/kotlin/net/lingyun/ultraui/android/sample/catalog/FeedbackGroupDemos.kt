package net.lingyun.ultraui.android.sample.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import net.lingyun.ultraui.android.components.UPActionSheet
import net.lingyun.ultraui.android.components.UPActionSheetProps
import net.lingyun.ultraui.android.components.UPAgreement
import net.lingyun.ultraui.android.components.UPAlert
import net.lingyun.ultraui.android.components.UPAlertProps
import net.lingyun.ultraui.android.components.UPButton
import net.lingyun.ultraui.android.components.UPButtonProps
import net.lingyun.ultraui.android.components.UPCollapse
import net.lingyun.ultraui.android.components.UPCollapseItem
import net.lingyun.ultraui.android.components.UPCollapseItemProps
import net.lingyun.ultraui.android.components.UPCollapseProps
import net.lingyun.ultraui.android.components.UPCopy
import net.lingyun.ultraui.android.components.UPCopyProps
import net.lingyun.ultraui.android.components.UPFloatButton
import net.lingyun.ultraui.android.components.UPFloatButtonProps
import net.lingyun.ultraui.android.components.UPGuide
import net.lingyun.ultraui.android.components.UPGuideProps
import net.lingyun.ultraui.android.components.UPModal
import net.lingyun.ultraui.android.components.UPModalProps
import net.lingyun.ultraui.android.components.UPNoticeBar
import net.lingyun.ultraui.android.components.UPNoticeBarProps
import net.lingyun.ultraui.android.components.UPNotify
import net.lingyun.ultraui.android.components.UPNotifyHost
import net.lingyun.ultraui.android.components.UPNotifyProps
import net.lingyun.ultraui.android.components.UPPopover
import net.lingyun.ultraui.android.components.UPPopoverProps
import net.lingyun.ultraui.android.components.UPPopup
import net.lingyun.ultraui.android.components.UPPopupProps
import net.lingyun.ultraui.android.components.UPPullRefresh
import net.lingyun.ultraui.android.components.UPPullRefreshProps
import net.lingyun.ultraui.android.components.UPSignature
import net.lingyun.ultraui.android.components.UPSignatureProps
import net.lingyun.ultraui.android.components.UPSwipeAction
import net.lingyun.ultraui.android.components.UPSwipeActionItem
import net.lingyun.ultraui.android.components.UPSwipeActionItemProps
import net.lingyun.ultraui.android.components.UPToastController
import net.lingyun.ultraui.android.components.UPToastHost
import net.lingyun.ultraui.android.components.UPToastProps
import net.lingyun.ultraui.android.components.UPTooltip
import net.lingyun.ultraui.android.components.UPTooltipProps
import net.lingyun.ultraui.android.components.rememberUPAgreementController
import net.lingyun.ultraui.android.components.rememberUPNotifyController
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.sample.DemoSection

/** 反馈组件分组的逐组件 demo；未命中返回 false 交回主分发器。 */
@Composable
internal fun renderFeedbackGroup(id: String, onEvent: (String) -> Unit): Boolean {
    when (id) {
        "tooltip" -> TooltipDemo()
        "guide" -> GuideDemo(onEvent)
        "popover" -> PopoverDemo()
        "action-sheet" -> ActionSheetDemo(onEvent)
        "alert" -> AlertDemo(onEvent)
        "toast" -> ToastDemo(onEvent)
        "notice-bar" -> NoticeBarDemo(onEvent)
        "notify" -> NotifyDemo(onEvent)
        "swipe-action" -> SwipeActionDemo()
        "collapse" -> CollapseDemo(onEvent)
        "popup" -> PopupDemo(onEvent)
        "modal" -> ModalDemo(onEvent)
        "copy" -> CopyDemo(onEvent)
        "float-button" -> FloatButtonDemo(onEvent)
        "pull-refresh" -> PullRefreshDemo(onEvent)
        "signature" -> SignatureDemo(onEvent)
        "agreement" -> AgreementDemo(onEvent)
        else -> return false
    }
    return true
}

@Composable
private fun DemoBox(height: Int, content: @Composable () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().height(height.dp)) { content() }
}

@Composable
private fun TooltipDemo() {
    DemoSection(title = "文字提示") { UPTooltip(UPTooltipProps(text = "提示内容", triggerMode = "click")) }
}

@Composable
private fun PopoverDemo() {
    DemoSection(title = "气泡弹出") { UPPopover(UPPopoverProps(text = "气泡内容")) }
}

@Composable
private fun AlertDemo(onEvent: (String) -> Unit) {
    var visible by remember { mutableStateOf(true) }
    DemoSection(title = "警告提示") {
        UPAlert(
            props = UPAlertProps(
                title = "系统提示",
                description = "这是原生 Compose 的 u-alert",
                type = "warning",
                showIcon = true,
                closable = true,
                modelValue = visible,
            ),
            onUpdateModelValue = { visible = it; onEvent("警告提示：${if (it) "打开" else "关闭"}") },
            onClick = { onEvent("警告提示：点击") },
        )
    }
}

@Composable
private fun NoticeBarDemo(onEvent: (String) -> Unit) {
    DemoSection(title = "滚动通知") {
        UPNoticeBar(
            props = UPNoticeBarProps(text = listOf("系统将于今晚维护", "请提前保存数据"), mode = "closable"),
            onClick = { onEvent("滚动通知：点击第 $it 条") },
            onClose = { onEvent("滚动通知：关闭") },
        )
    }
}

@Composable
private fun CollapseDemo(onEvent: (String) -> Unit) {
    var value: UPRawValue by remember { mutableStateOf(listOf<UPRawValue>("one")) }
    DemoSection(title = "折叠面板") {
        UPCollapse(
            props = UPCollapseProps(modelValue = value, accordion = true),
            onUpdateModelValue = { value = it; onEvent("折叠面板：更新 $it") },
        ) {
            UPCollapseItem(UPCollapseItemProps(name = "one", title = "第一项")) { Text("第一项内容", color = UPTheme.Content) }
            UPCollapseItem(UPCollapseItemProps(name = "two", title = "第二项")) { Text("第二项内容", color = UPTheme.Content) }
        }
    }
}

@Composable
private fun ActionSheetDemo(onEvent: (String) -> Unit) {
    var show by remember { mutableStateOf(false) }
    DemoSection(title = "上拉菜单") {
        DemoActionButton("打开操作菜单") { show = true; onEvent("操作菜单：打开") }
        DemoBox(240) {
            UPActionSheet(
                props = UPActionSheetProps(
                    show = show,
                    title = "选择操作",
                    actions = listOf(mapOf("name" to "拍照"), mapOf("name" to "从相册选择")),
                    cancelText = "取消",
                ),
                onUpdateShow = { show = it; onEvent("操作菜单：${if (it) "打开" else "关闭"}") },
                onSelect = { onEvent("操作菜单：选择 $it") },
                onCancel = { onEvent("操作菜单：取消") },
            )
        }
    }
}

@Composable
private fun NotifyDemo(onEvent: (String) -> Unit) {
    val controller = rememberUPNotifyController()
    var visible by remember { mutableStateOf(true) }
    DemoSection(title = "消息提示") {
        DemoBox(80) {
            if (visible) {
                UPNotify(
                    props = UPNotifyProps(message = "保存成功", duration = -1),
                    onClick = { onEvent("通知：点击") },
                    onClose = { visible = false; onEvent("通知：关闭") },
                )
            }
        }
        UPNotifyHost(controller, onClose = { onEvent("通知：关闭") })
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DemoActionButton("成功") { controller.success("已保存") }
            DemoActionButton("警告") { controller.warning("请检查网络") }
            DemoActionButton("失败") { controller.error("保存失败") }
        }
    }
}

@Composable
private fun ToastDemo(onEvent: (String) -> Unit) {
    val toast = remember { UPToastController() }
    DemoSection(title = "消息提示 Toast") {
        UPButton(props = UPButtonProps(text = "显示 toast", type = "success", size = "small"), onClick = {
            onEvent("轻提示：show")
            toast.show(UPToastProps(message = "操作成功", type = "success", duration = 1200, position = "center"))
        })
        DemoBox(200) { UPToastHost(controller = toast, modifier = Modifier.fillMaxWidth().height(200.dp)) }
    }
}

@Composable
private fun SwipeActionDemo() {
    DemoSection(title = "滑动单元格") {
        UPSwipeAction {
            UPSwipeActionItem(UPSwipeActionItemProps(show = true, options = listOf(mapOf("text" to "删除")))) {
                BasicText("向左滑动", Modifier.padding(12.dp))
            }
        }
    }
}

@Composable
private fun PopupDemo(onEvent: (String) -> Unit) {
    var show by remember { mutableStateOf(true) }
    DemoSection(title = "弹出层") {
        DemoActionButton("打开弹出层") { show = true }
        DemoBox(220) {
            UPPopup(
                props = UPPopupProps(show = show, pageInline = true, overlay = false, mode = "center", round = 8),
                onUpdateShow = { show = it },
                onClick = { onEvent("弹出层：内容点击") },
            ) {
                Text("内联 popup 内容", color = UPTheme.Main, modifier = Modifier.padding(16.dp))
            }
        }
    }
}

@Composable
private fun ModalDemo(onEvent: (String) -> Unit) {
    var show by remember { mutableStateOf(false) }
    DemoSection(title = "模态框") {
        DemoActionButton("打开模态框") { show = true }
        DemoBox(220) {
            UPModal(
                props = UPModalProps(show = show, title = "提示", content = "这是 Android 原生 Compose 模态框", showCancelButton = true),
                onUpdateShow = { show = it },
                onConfirm = { onEvent("模态框：确认"); show = false },
                onCancel = { onEvent("模态框：取消"); show = false },
            )
        }
    }
}

@Composable
private fun CopyDemo(onEvent: (String) -> Unit) {
    DemoSection(title = "复制") {
        UPCopy(
            props = UPCopyProps(content = "ultra-ui", notice = "已复制到剪贴板"),
            onResult = { _, notice, _ -> onEvent("复制：$notice") },
        ) { UPButton(props = UPButtonProps(text = "复制文本", type = "primary", size = "mini")) }
    }
}

@Composable
private fun FloatButtonDemo(onEvent: (String) -> Unit) {
    DemoSection(title = "悬浮按钮") {
        DemoBox(220) {
            UPFloatButton(
                props = UPFloatButtonProps(
                    isMenu = true,
                    bottom = "12px",
                    list = listOf(mapOf("name" to "star"), mapOf("name" to "heart")),
                ),
                onClick = { onEvent("悬浮按钮：点击") },
                onItemClick = { item, index -> onEvent("悬浮按钮项：${item["name"]} @$index") },
            )
        }
    }
}

@Composable
private fun PullRefreshDemo(onEvent: (String) -> Unit) {
    var refreshing by remember { mutableStateOf(false) }
    DemoSection(title = "下拉刷新") {
        DemoBox(180) {
            UPPullRefresh(
                props = UPPullRefreshProps(refreshing = refreshing),
                onRefresh = { refreshing = true; onEvent("下拉刷新：触发") },
            ) {
                Text("下拉此区域触发刷新", color = UPTheme.Content, modifier = Modifier.padding(16.dp))
            }
        }
        UPButton(props = UPButtonProps(text = "结束刷新", size = "mini"), onClick = { refreshing = false })
    }
}

@Composable
private fun SignatureDemo(onEvent: (String) -> Unit) {
    DemoSection(title = "签名签字") {
        UPSignature(
            props = UPSignatureProps(height = 160),
            onClear = { onEvent("签名：已清空") },
            onConfirm = { onEvent("签名：已确认") },
            onError = { onEvent("签名：请先签名") },
        )
    }
}

@Composable
private fun GuideDemo(onEvent: (String) -> Unit) {
    var show by remember { mutableStateOf(false) }
    DemoSection(title = "首屏引导") {
        DemoActionButton("开始引导") { show = true }
        DemoBox(320) {
            UPGuide(
                props = UPGuideProps(
                    show = show,
                    list = listOf(
                        mapOf("title" to "欢迎", "desc" to "这是首屏引导第一页"),
                        mapOf("title" to "开始", "desc" to "点击立即体验完成引导"),
                    ),
                ),
                onUpdateShow = { show = it },
                onFinish = { onEvent("引导：完成") },
                onSkip = { onEvent("引导：跳过") },
            )
        }
    }
}

@Composable
private fun AgreementDemo(onEvent: (String) -> Unit) {
    val agreement = rememberUPAgreementController()
    DemoSection(title = "弹窗协议") {
        UPButton(props = UPButtonProps(text = "弹出协议", type = "primary", size = "small"), onClick = {
            agreement.showModal(); onEvent("隐私协议：弹出")
        })
        DemoBox(20) {
            UPAgreement(
                controller = agreement,
                onConfirm = { onEvent("隐私协议：已同意") },
                onClose = { onEvent("隐私协议：已关闭") },
                onNavigate = { onEvent("隐私协议：跳转 $it") },
            )
        }
    }
}

@Composable
private fun DemoActionButton(text: String, onClick: () -> Unit) {
    Text(
        text = text,
        color = UPTheme.Primary,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(6.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp)
            .clickable(onClick = onClick),
    )
}
