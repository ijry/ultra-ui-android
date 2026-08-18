package net.lingyun.ultraui.android.sample.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import net.lingyun.ultraui.android.components.UPAvatar
import net.lingyun.ultraui.android.components.UPAvatarGroup
import net.lingyun.ultraui.android.components.UPAvatarGroupProps
import net.lingyun.ultraui.android.components.UPAvatarProps
import net.lingyun.ultraui.android.components.UPButton
import net.lingyun.ultraui.android.components.UPButtonProps
import net.lingyun.ultraui.android.components.UPCell
import net.lingyun.ultraui.android.components.UPCellGroup
import net.lingyun.ultraui.android.components.UPCellGroupProps
import net.lingyun.ultraui.android.components.UPCellProps
import net.lingyun.ultraui.android.components.UPEmpty
import net.lingyun.ultraui.android.components.UPEmptyProps
import net.lingyun.ultraui.android.components.UPImage
import net.lingyun.ultraui.android.components.UPImageProps
import net.lingyun.ultraui.android.components.UPLoadingPage
import net.lingyun.ultraui.android.components.UPLoadingPageProps
import net.lingyun.ultraui.android.components.UPLoadmore
import net.lingyun.ultraui.android.components.UPLoadmoreProps
import net.lingyun.ultraui.android.components.UPModal
import net.lingyun.ultraui.android.components.UPModalProps
import net.lingyun.ultraui.android.components.UPOverlay
import net.lingyun.ultraui.android.components.UPOverlayProps
import net.lingyun.ultraui.android.components.UPPopup
import net.lingyun.ultraui.android.components.UPPopupProps
import net.lingyun.ultraui.android.components.UPToastController
import net.lingyun.ultraui.android.components.UPToastHost
import net.lingyun.ultraui.android.components.UPToastProps
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.sample.DemoPlaceholder
import net.lingyun.ultraui.android.sample.DemoSection
import net.lingyun.ultraui.android.sample.SampleScaffold

/** Public API demos for layer and content components. */
@Composable
public fun LayerContentDemoPage(onBack: () -> Unit, modifier: Modifier = Modifier) {
    var eventText by remember { mutableStateOf("等待弹层与内容交互") }
    var modalVisible by remember { mutableStateOf(false) }
    var popupVisible by remember { mutableStateOf(true) }
    var loadmoreStatus by remember { mutableStateOf("loadmore") }
    val toastController = remember { UPToastController() }

    SampleScaffold(title = "弹层与内容", onBack = onBack, modifier = modifier) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                DemoEventText(eventText)

                DemoSection(title = "遮罩") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(96.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(UPTheme.Light),
                    ) {
                        Text("局部遮罩", color = UPTheme.Content, modifier = Modifier.align(Alignment.Center))
                        UPOverlay(props = UPOverlayProps(show = true, opacity = 0.35), onClick = { eventText = "遮罩：点击" })
                    }
                }

                DemoSection(title = "弹窗") {
                    UPButton(props = UPButtonProps(text = "切换弹窗", type = "primary", size = "small"), onClick = {
                        popupVisible = !popupVisible
                        eventText = "弹窗：${if (popupVisible) "打开" else "关闭"}"
                    })
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .padding(top = 10.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(UPTheme.Light),
                    ) {
                        UPPopup(
                            props = UPPopupProps(show = popupVisible, pageInline = true, overlay = false, mode = "center", round = 8),
                            onUpdateShow = { popupVisible = it },
                            onClick = { eventText = "弹窗：内容点击" },
                        ) {
                            Text("内联 popup 内容", color = UPTheme.Main, modifier = Modifier.padding(16.dp))
                        }
                    }
                }

                DemoSection(title = "模态框") {
                    UPButton(props = UPButtonProps(text = "显示模态框", type = "warning", size = "small"), onClick = {
                        modalVisible = true
                        eventText = "模态框：显示"
                    })
                }

                DemoSection(title = "轻提示") {
                    UPButton(props = UPButtonProps(text = "显示 toast", type = "success", size = "small"), onClick = {
                        eventText = "轻提示：show"
                        toastController.show(UPToastProps(message = "操作成功", type = "success", duration = 1200, position = "center"))
                    })
                }

                DemoSection(title = "单元格") {
                    UPCell(
                        props = UPCellProps(title = "账户资料", label = "公开 UPCellProps", value = "查看", isLink = true, clickable = true),
                        onClick = { eventText = "单元格：$it" },
                    )
                }

                DemoSection(title = "单元格组") {
                    UPCellGroup(props = UPCellGroupProps(title = "基础信息")) {
                        UPCell(props = UPCellProps(title = "昵称", value = "UltraUI"))
                        UPCell(props = UPCellProps(title = "状态", value = "已启用", border = false))
                    }
                }

                DemoSection(title = "图片") {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        UPImage(
                            props = UPImageProps(src = "", width = 72, height = 72, radius = 8, showError = true),
                            onError = { eventText = "图片：本地错误占位" },
                        )
                        DemoPlaceholder(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(0.5.dp, UPTheme.Border, RoundedCornerShape(8.dp)),
                        )
                    }
                }

                DemoSection(title = "头像") {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                        UPAvatar(props = UPAvatarProps(text = "U", randomBgColor = true, name = "avatar-u"), onClick = {
                            eventText = "头像：$it"
                        })
                        UPAvatar(props = UPAvatarProps(text = "A", shape = "square", bgColor = "#2979ff", color = "#ffffff"))
                    }
                }

                DemoSection(title = "头像组") {
                    UPAvatarGroup(
                        props = UPAvatarGroupProps(urls = listOf("", "", ""), maxCount = 2, showMore = true, extraValue = 3),
                        onShowMore = { eventText = "头像组：更多" },
                    )
                }

                DemoSection(title = "空状态") {
                    UPEmpty(props = UPEmptyProps(mode = "data", text = "暂无数据"))
                }

                DemoSection(title = "加载页") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White),
                    ) {
                        UPLoadingPage(
                            props = UPLoadingPageProps(loading = true, loadingText = "加载中", bgColor = "#ffffff"),
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                }

                DemoSection(title = "加载更多") {
                    UPLoadmore(
                        props = UPLoadmoreProps(status = loadmoreStatus, line = true),
                        onLoadmore = {
                            loadmoreStatus = if (loadmoreStatus == "loadmore") "loading" else "loadmore"
                            eventText = "加载更多：$loadmoreStatus"
                        },
                    )
                }
            }

            UPToastHost(controller = toastController, modifier = Modifier.fillMaxSize())
            UPModal(
                props = UPModalProps(show = modalVisible, title = "提示", content = "这是 Android 原生 Compose 模态框", showCancelButton = true),
                onUpdateShow = { modalVisible = it },
                onConfirm = {
                    eventText = "模态框：确认"
                    modalVisible = false
                },
                onCancel = {
                    eventText = "模态框：取消"
                    modalVisible = false
                },
            )
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
