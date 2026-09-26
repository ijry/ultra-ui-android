package net.lingyun.ultraui.android.sample.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import net.lingyun.ultraui.android.components.UPAvatar
import net.lingyun.ultraui.android.components.UPAvatarProps
import net.lingyun.ultraui.android.components.UPBarcode
import net.lingyun.ultraui.android.components.UPBarcodeProps
import net.lingyun.ultraui.android.components.UPButton
import net.lingyun.ultraui.android.components.UPButtonProps
import net.lingyun.ultraui.android.components.UPCityLocate
import net.lingyun.ultraui.android.components.UPCityLocateProps
import net.lingyun.ultraui.android.components.UPCodeInput
import net.lingyun.ultraui.android.components.UPCodeInputProps
import net.lingyun.ultraui.android.components.UPColorPicker
import net.lingyun.ultraui.android.components.UPColorPickerProps
import net.lingyun.ultraui.android.components.UPCoupon
import net.lingyun.ultraui.android.components.UPCouponProps
import net.lingyun.ultraui.android.components.UPCropper
import net.lingyun.ultraui.android.components.UPCropperProps
import net.lingyun.ultraui.android.components.UPDragsort
import net.lingyun.ultraui.android.components.UPDragsortProps
import net.lingyun.ultraui.android.components.UPGap
import net.lingyun.ultraui.android.components.UPGapProps
import net.lingyun.ultraui.android.components.UPGoodsSku
import net.lingyun.ultraui.android.components.UPGoodsSkuProps
import net.lingyun.ultraui.android.components.UPLazyLoad
import net.lingyun.ultraui.android.components.UPLazyLoadProps
import net.lingyun.ultraui.android.components.UPLink
import net.lingyun.ultraui.android.components.UPLinkProps
import net.lingyun.ultraui.android.components.UPLoadmore
import net.lingyun.ultraui.android.components.UPLoadmoreProps
import net.lingyun.ultraui.android.components.UPMarkdown
import net.lingyun.ultraui.android.components.UPMarkdownProps
import net.lingyun.ultraui.android.components.UPNovelReader
import net.lingyun.ultraui.android.components.UPNovelReaderProps
import net.lingyun.ultraui.android.components.UPParse
import net.lingyun.ultraui.android.components.UPParseProps
import net.lingyun.ultraui.android.components.UPPdfReader
import net.lingyun.ultraui.android.components.UPPdfReaderProps
import net.lingyun.ultraui.android.components.UPPoster
import net.lingyun.ultraui.android.components.UPPosterProps
import net.lingyun.ultraui.android.components.UPQrcode
import net.lingyun.ultraui.android.components.UPQrcodeProps
import net.lingyun.ultraui.android.components.UPReadMore
import net.lingyun.ultraui.android.components.UPReadMoreProps
import net.lingyun.ultraui.android.components.UPTransition
import net.lingyun.ultraui.android.components.UPTransitionProps
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.sample.DemoSection

/** 其他组件分组的逐组件 demo；未命中返回 false 交回主分发器。 */
@Composable
internal fun renderOtherGroup(id: String, onEvent: (String) -> Unit): Boolean {
    when (id) {
        "parse" -> ParseDemo()
        "markdown" -> MarkdownDemo(onEvent)
        "code-input" -> CodeInputDemo(onEvent)
        "dragsort" -> DragsortDemo(onEvent)
        "cropper" -> CropperDemo(onEvent)
        "loadmore" -> LoadmoreDemo(onEvent)
        "read-more" -> ReadMoreDemo()
        "lazy-load" -> LazyLoadDemo(onEvent)
        "gap" -> GapDemo()
        "avatar" -> AvatarDemo(onEvent)
        "link" -> LinkDemo(onEvent)
        "transition" -> TransitionDemo(onEvent)
        "qrcode" -> QrcodeDemo(onEvent)
        "coupon" -> CouponDemo(onEvent)
        "barcode" -> BarcodeDemo(onEvent)
        "color-picker" -> ColorPickerDemo(onEvent)
        "poster" -> PosterDemo()
        "goods-sku" -> GoodsSkuDemo(onEvent)
        "city-locate" -> CityLocateDemo()
        "pdf-reader" -> PdfReaderDemo()
        "novel-reader" -> NovelReaderDemo()
        else -> return false
    }
    return true
}

@Composable
private fun DemoBox(height: Int, content: @Composable () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().height(height.dp)) { content() }
}

@Composable
private fun ParseDemo() {
    DemoSection(title = "富文本解析器") {
        UPParse(
            props = UPParseProps(
                content = "<h3>Ultra UI</h3>" +
                    "<p>支持 <b>加粗</b>、<i>斜体</i>、<u>下划线</u> 与 <a href=\"https://uview-plus.jiangruyi.com\">链接</a>。</p>" +
                    "<ul><li>列表项一</li><li>列表项二</li></ul>" +
                    "<blockquote>引用文本块</blockquote>",
                selectable = true,
            ),
        )
    }
}

@Composable
private fun MarkdownDemo(onEvent: (String) -> Unit) {
    DemoSection(title = "Markdown 解析器") {
        UPMarkdown(
            props = UPMarkdownProps(
                content = "# Ultra UI\n\n支持 **加粗**、*斜体* 与 `code`。\n\n- 列表一\n- 列表二\n\n> 引用块\n\n```kotlin\nval x = 1\n```",
                showLineNumber = true,
            ),
            onLinkTap = { onEvent("Markdown：链接 $it") },
        )
    }
}

@Composable
private fun CodeInputDemo(onEvent: (String) -> Unit) {
    var value by remember { mutableStateOf("12") }
    DemoSection(title = "验证码输入") {
        UPCodeInput(
            props = UPCodeInputProps(modelValue = value, maxlength = 4, mode = "box"),
            onInput = { value = it; onEvent("验证码输入：$it") },
            onFinish = { onEvent("验证码输入：完成 $it") },
        )
    }
}

@Composable
private fun DragsortDemo(onEvent: (String) -> Unit) {
    DemoSection(title = "拖动排序") {
        UPDragsort(
            props = UPDragsortProps(
                initialList = listOf(
                    mapOf("id" to 1, "label" to "拖动我 · 一"),
                    mapOf("id" to 2, "label" to "拖动我 · 二"),
                    mapOf("id" to 3, "label" to "拖动我 · 三"),
                ),
            ),
            onDragEnd = { onEvent("拖拽排序：${it.size} 项") },
        )
    }
}

@Composable
private fun CropperDemo(onEvent: (String) -> Unit) {
    DemoSection(title = "图片裁剪") {
        UPCropper(
            src = "/sdcard/sample.jpg",
            props = UPCropperProps(noTab = false, areaWidth = "220px", areaHeight = "180px", fillColor = "#1a1a1a"),
            onConfirm = { onEvent("裁剪：缩放 ${(it.scale * 100).toInt()}% 旋转 ${it.rotation.toInt()}°") },
            onCancel = { onEvent("裁剪：取消") },
        )
    }
}

@Composable
private fun LoadmoreDemo(onEvent: (String) -> Unit) {
    var status by remember { mutableStateOf("loadmore") }
    DemoSection(title = "加载更多") {
        UPLoadmore(
            props = UPLoadmoreProps(status = status, line = true),
            onLoadmore = {
                status = if (status == "loadmore") "loading" else "loadmore"
                onEvent("加载更多：$status")
            },
        )
    }
}

@Composable
private fun ReadMoreDemo() {
    DemoSection(title = "展开阅读更多") {
        UPReadMore(UPReadMoreProps(showHeight = 48)) {
            BasicText("这是一段较长的内容，用于展示展开阅读组件在原生页面中的截断与展开行为。")
        }
    }
}

@Composable
private fun LazyLoadDemo(onEvent: (String) -> Unit) {
    DemoSection(title = "懒加载") {
        UPLazyLoad(props = UPLazyLoadProps(image = "", height = "120"), onClick = { onEvent("懒加载：点击") })
    }
}

@Composable
private fun GapDemo() {
    DemoSection(title = "间隔槽") {
        BasicText("上方内容")
        UPGap(props = UPGapProps(height = "12px", bgColor = "#f4f4f5"))
        BasicText("下方内容")
    }
}

@Composable
private fun AvatarDemo(onEvent: (String) -> Unit) {
    DemoSection(title = "头像") {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            UPAvatar(props = UPAvatarProps(text = "U", randomBgColor = true, name = "avatar-u"), onClick = { onEvent("头像：$it") })
            UPAvatar(props = UPAvatarProps(text = "A", shape = "square", bgColor = "#2979ff", color = "#ffffff"))
        }
    }
}

@Composable
private fun LinkDemo(onEvent: (String) -> Unit) {
    DemoSection(title = "超链接") {
        UPLink(props = UPLinkProps(text = "打开链接", href = "https://example.com", color = "#2979ff"), onOpen = { onEvent("链接：$it") })
    }
}

@Composable
private fun TransitionDemo(onEvent: (String) -> Unit) {
    var show by remember { mutableStateOf(true) }
    DemoSection(title = "动画") {
        UPButton(props = UPButtonProps(text = if (show) "隐藏" else "显示", type = "primary", size = "small"), onClick = { show = !show })
        DemoBox(110) {
            UPTransition(
                props = UPTransitionProps(show = show, mode = "fade-up", duration = 300),
                onAfterEnter = { onEvent("过渡：进入完成") },
                onAfterLeave = { onEvent("过渡：离开完成") },
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(90.dp).background(UPTheme.Primary, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center,
                ) { Text("fade-up 过渡内容", color = Color.White) }
            }
        }
    }
}

@Composable
private fun QrcodeDemo(onEvent: (String) -> Unit) {
    DemoSection(title = "二维码") {
        UPQrcode(
            props = UPQrcodeProps(`val` = "https://uview-plus.jiangruyi.com", size = 180, foreground = "#3c9cff", allowPreview = true),
            onPreview = { onEvent("二维码：预览") },
        )
    }
}

@Composable
private fun CouponDemo(onEvent: (String) -> Unit) {
    DemoSection(title = "优惠券") {
        UPCoupon(
            props = UPCouponProps(amount = "50", limit = "满199可用", title = "新人专享券", desc = "全场通用", time = "有效期至 2026-12-31"),
            onClick = { onEvent("优惠券：使用") },
        )
    }
}

@Composable
private fun BarcodeDemo(onEvent: (String) -> Unit) {
    DemoSection(title = "条码") {
        UPBarcode(props = UPBarcodeProps(value = "ULTRA-UI-123", format = "CODE128"), onError = { onEvent("条形码：$it") })
        UPBarcode(props = UPBarcodeProps(value = "5901234123457", format = "EAN13"))
    }
}

@Composable
private fun ColorPickerDemo(onEvent: (String) -> Unit) {
    var show by remember { mutableStateOf(false) }
    DemoSection(title = "颜色选择器") {
        UPButton(props = UPButtonProps(text = "选择颜色", type = "primary", size = "small"), onClick = { show = true })
        DemoBox(320) {
            UPColorPicker(
                props = UPColorPickerProps(show = show, modelValue = "#3c9cff", commonColors = listOf("#ff0000", "#00ff00", "#0000ff", "#ffcc00")),
                onUpdateShow = { show = it },
                onConfirm = { show = false; onEvent("颜色：$it") },
            )
        }
    }
}

@Composable
private fun PosterDemo() {
    DemoSection(title = "海报生成") {
        UPPoster(
            props = UPPosterProps(
                json = mapOf(
                    "css" to mapOf("width" to "600rpx", "height" to "760rpx", "background" to "linear-gradient(135deg, #3c9cff, #5ac8fa)", "radius" to "16rpx"),
                    "views" to listOf(
                        mapOf("type" to "text", "text" to "Ultra UI 海报", "css" to mapOf("left" to "40rpx", "top" to "48rpx", "color" to "#ffffff", "fontSize" to "44rpx", "fontWeight" to "bold")),
                        mapOf("type" to "view", "css" to mapOf("left" to "40rpx", "top" to "140rpx", "width" to "520rpx", "height" to "1rpx", "background" to "#ffffff")),
                        mapOf("type" to "text", "text" to "扫码体验原生 Compose 组件库", "css" to mapOf("left" to "40rpx", "top" to "180rpx", "color" to "#eef6ff", "fontSize" to "28rpx")),
                        mapOf("type" to "qrcode", "text" to "https://uview-plus.jiangruyi.com", "css" to mapOf("left" to "200rpx", "top" to "300rpx", "width" to "200rpx", "height" to "200rpx")),
                    ),
                ),
            ),
        )
    }
}

@Composable
private fun GoodsSkuDemo(onEvent: (String) -> Unit) {
    var show by remember { mutableStateOf(false) }
    DemoSection(title = "商品 SKU") {
        UPButton(props = UPButtonProps(text = "选择规格", type = "primary", size = "small"), onClick = { show = true })
        DemoBox(360) {
            UPGoodsSku(
                props = UPGoodsSkuProps(
                    show = show,
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
                onUpdateShow = { show = it },
                onConfirm = { _, num, text -> show = false; onEvent("规格：$text ×$num") },
            )
        }
    }
}

@Composable
private fun CityLocateDemo() {
    DemoSection(title = "城市定位") {
        DemoBox(360) {
            UPCityLocate(
                props = UPCityLocateProps(
                    indexList = listOf("🔥", "A"),
                    currentCity = "上海",
                    cityList = listOf(
                        listOf(mapOf("name" to "北京"), mapOf("name" to "广州")),
                        listOf(mapOf("name" to "安庆")),
                    ),
                ),
                onSelectCity = { },
                onLocate = { },
            )
        }
    }
}

@Composable
private fun PdfReaderDemo() {
    DemoSection(title = "PDF 阅读器") {
        DemoBox(360) {
            UPPdfReader(props = UPPdfReaderProps(src = "https://uview-plus.jiangruyi.com/uview-plus.pdf", height = "360px"))
        }
    }
}

@Composable
private fun NovelReaderDemo() {
    val chapters = listOf(
        mapOf("id" to "c1", "index" to 0, "title" to "第一章 山雨欲来"),
        mapOf("id" to "c2", "index" to 1, "title" to "第二章 风满楼"),
    )
    DemoSection(title = "小说阅读器") {
        DemoBox(420) {
            UPNovelReader(
                props = UPNovelReaderProps(
                    chapters = chapters,
                    currentChapter = mapOf(
                        "id" to "c1", "index" to 0, "title" to "第一章 山雨欲来",
                        "content" to "夜色深沉，风穿过长街。\n他站在屋檐下，望着远处的灯火，久久不语。\n这一夜注定无眠。",
                    ),
                ),
            )
        }
    }
}
