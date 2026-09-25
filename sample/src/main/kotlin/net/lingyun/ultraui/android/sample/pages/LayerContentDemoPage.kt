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
import net.lingyun.ultraui.android.components.UPGuide
import net.lingyun.ultraui.android.components.UPGuideProps
import net.lingyun.ultraui.android.components.UPLazyLoad
import net.lingyun.ultraui.android.components.UPLazyLoadProps
import net.lingyun.ultraui.android.components.UPCoupon
import net.lingyun.ultraui.android.components.UPCouponProps
import net.lingyun.ultraui.android.components.UPNoNetwork
import net.lingyun.ultraui.android.components.UPNoNetworkProps
import net.lingyun.ultraui.android.components.UPImage
import net.lingyun.ultraui.android.components.UPImageProps
import net.lingyun.ultraui.android.components.UPLoadingPage
import net.lingyun.ultraui.android.components.UPLoadingPageProps
import net.lingyun.ultraui.android.components.UPColorPicker
import net.lingyun.ultraui.android.components.UPColorPickerProps
import net.lingyun.ultraui.android.components.UPSignature
import net.lingyun.ultraui.android.components.UPSignatureProps
import net.lingyun.ultraui.android.components.UPGoodsSku
import net.lingyun.ultraui.android.components.UPGoodsSkuProps
import net.lingyun.ultraui.android.components.UPPullRefresh
import net.lingyun.ultraui.android.components.UPPullRefreshProps
import net.lingyun.ultraui.android.components.UPLoadmore
import net.lingyun.ultraui.android.components.UPLoadmoreProps
import net.lingyun.ultraui.android.components.UPCopy
import net.lingyun.ultraui.android.components.UPCopyProps
import net.lingyun.ultraui.android.components.UPAgreement
import net.lingyun.ultraui.android.components.rememberUPAgreementController
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
    var networkConnected by remember { mutableStateOf(true) }
    var pullRefreshing by remember { mutableStateOf(false) }
    var skuShow by remember { mutableStateOf(false) }
    var colorShow by remember { mutableStateOf(false) }
    var guideShow by remember { mutableStateOf(false) }
    val agreement = rememberUPAgreementController()
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

                DemoSection(title = "复制") {
                    UPCopy(
                        props = UPCopyProps(content = "ultra-ui", notice = "已复制到剪贴板"),
                        onResult = { _, notice, _ -> eventText = "复制：$notice" },
                    ) { UPButton(props = UPButtonProps(text = "复制文本", type = "primary", size = "mini")) }
                }

                DemoSection(title = "隐私协议") {
                    UPButton(props = UPButtonProps(text = "弹出协议", type = "primary", size = "small"), onClick = {
                        agreement.showModal()
                        eventText = "隐私协议：弹出"
                    })
                    UPAgreement(
                        controller = agreement,
                        onConfirm = { eventText = "隐私协议：已同意" },
                        onClose = { eventText = "隐私协议：已关闭" },
                        onNavigate = { eventText = "隐私协议：跳转 $it" },
                    )
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

                DemoSection(title = "首屏引导") {
                    UPButton(props = UPButtonProps(text = "开始引导", type = "primary", size = "small"), onClick = { guideShow = true })
                    Box(modifier = Modifier.fillMaxWidth().height(if (guideShow) 320.dp else 0.dp)) {
                        UPGuide(
                            props = UPGuideProps(
                                show = guideShow,
                                list = listOf(
                                    mapOf("title" to "欢迎", "desc" to "这是首屏引导第一页"),
                                    mapOf("title" to "开始", "desc" to "点击立即体验完成引导"),
                                ),
                            ),
                            onUpdateShow = { guideShow = it },
                            onFinish = { eventText = "引导：完成" },
                            onSkip = { eventText = "引导：跳过" },
                        )
                    }
                }

                DemoSection(title = "懒加载图片") {
                    UPLazyLoad(
                        props = UPLazyLoadProps(image = "", height = "120"),
                        onClick = { eventText = "懒加载：点击" },
                    )
                }

                DemoSection(title = "优惠券") {
                    UPCoupon(
                        props = UPCouponProps(amount = "50", limit = "满199可用", title = "新人专享券", desc = "全场通用", time = "有效期至 2026-12-31"),
                        onClick = { eventText = "优惠券：使用" },
                    )
                }

                DemoSection(title = "空状态") {
                    UPEmpty(props = UPEmptyProps(mode = "data", text = "暂无数据"))
                }

                DemoSection(title = "无网络") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White),
                    ) {
                        UPButton(
                            UPButtonProps(text = if (networkConnected) "模拟断网" else "已断网", size = "small"),
                            onClick = { networkConnected = false },
                        )
                        UPNoNetwork(
                            props = UPNoNetworkProps(tips = "哎呀，网络信号丢失"),
                            connected = networkConnected,
                            onRetry = { networkConnected = true; eventText = "无网络：点击重试" },
                        )
                    }
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

                DemoSection(title = "颜色选择器") {
                    UPButton(props = UPButtonProps(text = "选择颜色", type = "primary", size = "small"), onClick = { colorShow = true })
                    UPColorPicker(
                        props = UPColorPickerProps(show = colorShow, modelValue = "#3c9cff", commonColors = listOf("#ff0000", "#00ff00", "#0000ff", "#ffcc00")),
                        onUpdateShow = { colorShow = it },
                        onConfirm = { colorShow = false; eventText = "颜色：$it" },
                    )
                }

                DemoSection(title = "手写签名") {
                    UPSignature(
                        props = UPSignatureProps(height = 160),
                        onClear = { eventText = "签名：已清空" },
                        onConfirm = { eventText = "签名：已确认" },
                        onError = { eventText = "签名：请先签名" },
                    )
                }

                DemoSection(title = "商品规格") {
                    UPButton(props = UPButtonProps(text = "选择规格", type = "primary", size = "small"), onClick = { skuShow = true })
                    UPGoodsSku(
                        props = UPGoodsSkuProps(
                            show = skuShow,
                            goodsInfo = mapOf("price" to 99, "stock" to 20),
                            skuTree = listOf(
                                mapOf("label" to "颜色", "name" to "color", "children" to listOf(mapOf("id" to "r", "name" to "红色"), mapOf("id" to "b", "name" to "蓝色"))),
                                mapOf("label" to "尺寸", "name" to "size", "children" to listOf(mapOf("id" to "s", "name" to "S"), mapOf("id" to "m", "name" to "M"))),
                            ),
                            skuList = listOf(
                                mapOf("color" to "r", "size" to "s", "price" to 88, "stock" to 5),
                                mapOf("color" to "b", "size" to "m", "price" to 96, "stock" to 3),
                            ),
                        ),
                        onUpdateShow = { skuShow = it },
                        onConfirm = { _, num, text -> skuShow = false; eventText = "规格：$text ×$num" },
                    )
                }

                DemoSection(title = "下拉刷新") {
                    UPPullRefresh(
                        props = UPPullRefreshProps(refreshing = pullRefreshing),
                        onRefresh = { pullRefreshing = true; eventText = "下拉刷新：触发" },
                    ) {
                        Text("下拉此区域触发刷新", color = UPTheme.Content, modifier = Modifier.padding(16.dp))
                    }
                    UPButton(props = UPButtonProps(text = "结束刷新", size = "mini"), onClick = { pullRefreshing = false })
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
