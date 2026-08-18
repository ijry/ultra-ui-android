package net.lingyun.ultraui.android.sample.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import net.lingyun.ultraui.android.components.UPActionSheet
import net.lingyun.ultraui.android.components.UPActionSheetProps
import net.lingyun.ultraui.android.components.UPAlert
import net.lingyun.ultraui.android.components.UPAlertProps
import net.lingyun.ultraui.android.components.UPBackTop
import net.lingyun.ultraui.android.components.UPBackTopProps
import net.lingyun.ultraui.android.components.UPCard
import net.lingyun.ultraui.android.components.UPCardProps
import net.lingyun.ultraui.android.components.UPCollapse
import net.lingyun.ultraui.android.components.UPCollapseItem
import net.lingyun.ultraui.android.components.UPCollapseItemProps
import net.lingyun.ultraui.android.components.UPCollapseProps
import net.lingyun.ultraui.android.components.UPDropdown
import net.lingyun.ultraui.android.components.UPDropdownItem
import net.lingyun.ultraui.android.components.UPDropdownItemProps
import net.lingyun.ultraui.android.components.UPDropdownProps
import net.lingyun.ultraui.android.components.UPNoticeBar
import net.lingyun.ultraui.android.components.UPNoticeBarProps
import net.lingyun.ultraui.android.components.UPNotify
import net.lingyun.ultraui.android.components.UPNotifyProps
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.sample.DemoSection
import net.lingyun.ultraui.android.sample.SampleScaffold

/** Public API demos for the Batch 9A native interaction components. */
@Composable
public fun NativeInteractionDemoPage(onBack: () -> Unit, modifier: Modifier = Modifier) {
    var eventText by remember { mutableStateOf("等待原生交互") }
    var alertVisible by remember { mutableStateOf(true) }
    var actionSheetVisible by remember { mutableStateOf(false) }
    var notifyVisible by remember { mutableStateOf(true) }
    var collapseValue: UPRawValue by remember { mutableStateOf(listOf<UPRawValue>("one")) }
    var dropdownValue: UPRawValue by remember { mutableStateOf("all") }

    SampleScaffold(title = "原生交互", onBack = onBack, modifier = modifier) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                DemoEventText(eventText)

                DemoSection(title = "警告提示") {
                    UPAlert(
                        props = UPAlertProps(
                            title = "系统提示",
                            description = "这是原生 Compose 的 u-alert",
                            type = "warning",
                            showIcon = true,
                            closable = true,
                            modelValue = alertVisible,
                        ),
                        onUpdateModelValue = {
                            alertVisible = it
                            eventText = "警告提示：${if (it) "打开" else "关闭"}"
                        },
                        onClick = { eventText = "警告提示：点击" },
                    )
                }

                DemoSection(title = "操作菜单") {
                    UPActionSheet(
                        props = UPActionSheetProps(
                            show = actionSheetVisible,
                            title = "选择操作",
                            actions = listOf(
                                mapOf("name" to "拍照"),
                                mapOf("name" to "从相册选择"),
                            ),
                            cancelText = "取消",
                        ),
                        onUpdateShow = {
                            actionSheetVisible = it
                            eventText = "操作菜单：${if (it) "打开" else "关闭"}"
                        },
                        onSelect = { eventText = "操作菜单：选择 ${it.toString()}" },
                        onCancel = { eventText = "操作菜单：取消" },
                    )
                    DemoActionButton(
                        text = "打开操作菜单",
                        onClick = {
                            actionSheetVisible = true
                            eventText = "操作菜单：打开"
                        },
                    )
                }

                DemoSection(title = "通知") {
                    if (notifyVisible) {
                        UPNotify(
                            props = UPNotifyProps(message = "保存成功", duration = -1),
                            onClick = { eventText = "通知：点击" },
                            onClose = {
                                notifyVisible = false
                                eventText = "通知：关闭"
                            },
                        )
                    } else {
                        DemoActionButton(
                            text = "再次显示通知",
                            onClick = {
                                notifyVisible = true
                                eventText = "通知：打开"
                            },
                        )
                    }
                }

                DemoSection(title = "返回顶部") {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        UPBackTop(
                            props = UPBackTopProps(scrollTop = 800, text = "顶部"),
                            onClick = { eventText = "返回顶部：点击" },
                        )
                    }
                }

                DemoSection(title = "卡片") {
                    UPCard(
                        props = UPCardProps(title = "订单信息", subTitle = "今天 12:30", index = 1),
                        onClick = { eventText = "卡片：点击 $it" },
                    ) {
                        Text("卡片主体内容", color = UPTheme.Content)
                    }
                }

                DemoSection(title = "折叠面板") {
                    UPCollapse(
                        props = UPCollapseProps(modelValue = collapseValue, accordion = true),
                        onUpdateModelValue = {
                            collapseValue = it
                            eventText = "折叠面板：更新 $it"
                        },
                        onChange = { eventText = "折叠面板：状态 $it" },
                    ) {
                        UPCollapseItem(UPCollapseItemProps(name = "one", title = "第一项")) {
                            Text("第一项内容", color = UPTheme.Content)
                        }
                        UPCollapseItem(UPCollapseItemProps(name = "two", title = "第二项")) {
                            Text("第二项内容", color = UPTheme.Content)
                        }
                    }
                }

                DemoSection(title = "折叠项") {
                    UPCollapseItem(
                        props = UPCollapseItemProps(
                            name = "standalone",
                            title = "独立折叠项",
                            isOpen = true,
                        ),
                        onClick = { eventText = "折叠项：点击 $it" },
                    ) {
                        Text("独立内容", color = UPTheme.Content)
                    }
                }

                DemoSection(title = "下拉菜单") {
                    UPDropdown(
                        props = UPDropdownProps(),
                        onOpen = { eventText = "下拉菜单：打开" },
                        onClose = { eventText = "下拉菜单：关闭" },
                    ) {
                        UPDropdownItem(
                            props = UPDropdownItemProps(
                                title = "状态",
                                options = listOf(
                                    mapOf("label" to "全部", "value" to "all"),
                                    mapOf("label" to "已完成", "value" to "done"),
                                ),
                                modelValue = dropdownValue,
                            ),
                            onUpdateModelValue = {
                                dropdownValue = it
                                eventText = "下拉菜单：选择 $it"
                            },
                        )
                    }
                }

                DemoSection(title = "下拉项") {
                    Text("下拉项使用上方菜单中的 options 和 modelValue", color = UPTheme.Content)
                }

                DemoSection(title = "滚动通知") {
                    UPNoticeBar(
                        props = UPNoticeBarProps(
                            text = listOf("系统将于今晚维护", "请提前保存数据"),
                            mode = "closable",
                        ),
                        onClick = { eventText = "滚动通知：点击第 $it 条" },
                        onClose = { eventText = "滚动通知：关闭" },
                    )
                }
            }
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
