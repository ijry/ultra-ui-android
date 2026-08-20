package net.lingyun.ultraui.android.sample.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.lingyun.ultraui.android.components.UPColumnNotice
import net.lingyun.ultraui.android.components.UPColumnNoticeProps
import net.lingyun.ultraui.android.components.UPCountDown
import net.lingyun.ultraui.android.components.UPCountDownProps
import net.lingyun.ultraui.android.components.UPCountTo
import net.lingyun.ultraui.android.components.UPCountToProps
import net.lingyun.ultraui.android.components.UPIndexAnchor
import net.lingyun.ultraui.android.components.UPIndexAnchorProps
import net.lingyun.ultraui.android.components.UPIndexItem
import net.lingyun.ultraui.android.components.UPList
import net.lingyun.ultraui.android.components.UPListItem
import net.lingyun.ultraui.android.components.UPNavbar
import net.lingyun.ultraui.android.components.UPNavbarMini
import net.lingyun.ultraui.android.components.UPNavbarMiniProps
import net.lingyun.ultraui.android.components.UPNavbarProps
import net.lingyun.ultraui.android.components.UPPagination
import net.lingyun.ultraui.android.components.UPPaginationProps
import net.lingyun.ultraui.android.components.UPPicker
import net.lingyun.ultraui.android.components.UPPickerColumn
import net.lingyun.ultraui.android.components.UPPickerProps
import net.lingyun.ultraui.android.components.UPPopover
import net.lingyun.ultraui.android.components.UPPopoverProps
import net.lingyun.ultraui.android.components.UPReadMore
import net.lingyun.ultraui.android.components.UPReadMoreProps
import net.lingyun.ultraui.android.components.UPRowNotice
import net.lingyun.ultraui.android.components.UPRowNoticeProps
import net.lingyun.ultraui.android.components.UPSafeBottom
import net.lingyun.ultraui.android.components.UPSelect
import net.lingyun.ultraui.android.components.UPSelectProps
import net.lingyun.ultraui.android.components.UPSkeleton
import net.lingyun.ultraui.android.components.UPSkeletonProps
import net.lingyun.ultraui.android.components.UPSticky
import net.lingyun.ultraui.android.components.UPStatusBar
import net.lingyun.ultraui.android.components.UPSteps
import net.lingyun.ultraui.android.components.UPStepsItem
import net.lingyun.ultraui.android.components.UPStepsItemProps
import net.lingyun.ultraui.android.components.UPSubsection
import net.lingyun.ultraui.android.components.UPSubsectionProps
import net.lingyun.ultraui.android.components.UPSwipeAction
import net.lingyun.ultraui.android.components.UPSwipeActionItem
import net.lingyun.ultraui.android.components.UPSwipeActionItemProps
import net.lingyun.ultraui.android.components.UPSwiper
import net.lingyun.ultraui.android.components.UPSwiperIndicator
import net.lingyun.ultraui.android.components.UPSwiperIndicatorProps
import net.lingyun.ultraui.android.components.UPSwiperProps
import net.lingyun.ultraui.android.components.UPTabs
import net.lingyun.ultraui.android.components.UPTabsItem
import net.lingyun.ultraui.android.components.UPTabsProps
import net.lingyun.ultraui.android.components.UPTooltip
import net.lingyun.ultraui.android.components.UPTooltipProps
import net.lingyun.ultraui.android.components.UPScrollList
import net.lingyun.ultraui.android.components.UPIndexList
import net.lingyun.ultraui.android.components.UPIndexListProps
import net.lingyun.ultraui.android.components.UPStatusBarProps
import net.lingyun.ultraui.android.sample.DemoSection
import net.lingyun.ultraui.android.sample.SampleScaffold

/** Native demonstrations for the second interaction batch. */
@Composable
public fun NavigationMoreDemoPage(onBack: () -> Unit, modifier: Modifier = Modifier) {
    var tab by remember { mutableStateOf(0) }
    var subsection by remember { mutableStateOf(0) }
    var select by remember { mutableStateOf<Any?>(null) }
    SampleScaffold(title = "导航与更多", onBack = onBack, modifier = modifier) {
        Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            DemoSection("导航栏") { UPNavbar(UPNavbarProps(title = "订单详情")) }
            DemoSection("迷你导航栏") { UPNavbarMini(UPNavbarMiniProps(homeUrl = "/")) }
            DemoSection("状态栏") { UPStatusBar(); BasicText("状态栏安全区") }
            DemoSection("底部安全区") { BasicText("内容"); UPSafeBottom() }
            DemoSection("标签页") { UPTabs(UPTabsProps(list = listOf("全部", "待支付", "已完成"), current = tab), onChange = { tab = it }) }
            DemoSection("标签项") { UPTabsItem { BasicText("自定义标签项") } }
            DemoSection("分段器") { UPSubsection(UPSubsectionProps(list = listOf("日", "周", "月"), current = subsection), onChange = { subsection = it }) }
            DemoSection("步骤条") { UPSteps { UPStepsItem(UPStepsItemProps(title = "提交订单", desc = "已完成")); UPStepsItem(UPStepsItemProps(title = "配送中", desc = "处理中")) } }
            DemoSection("步骤项") { UPStepsItem(UPStepsItemProps(title = "独立步骤", desc = "可嵌套使用")) }
            DemoSection("列表") { UPList { repeat(3) { UPListItem { BasicText("列表内容 ${it + 1}", Modifier.padding(10.dp)) } } } }
            DemoSection("列表项") { UPListItem { BasicText("列表项内容") } }
            DemoSection("索引列表") { UPIndexList { UPIndexAnchor(UPIndexAnchorProps(text = "A")); UPIndexItem { BasicText("Apple", Modifier.padding(10.dp)) } } }
            DemoSection("索引项") { UPIndexItem { BasicText("索引项内容") } }
            DemoSection("索引锚点") { UPIndexAnchor(UPIndexAnchorProps(text = "B")) }
            DemoSection("滚动列表") { UPScrollList { BasicText("横向滚动列表内容") } }
            DemoSection("气泡弹出") { UPPopover(UPPopoverProps(text = "气泡内容")) }
            DemoSection("文字提示") { UPTooltip(UPTooltipProps(text = "提示内容", triggerMode = "click")) }
            DemoSection("吸顶") { UPSticky { BasicText("吸顶内容") } }
            DemoSection("滑动操作") { UPSwipeAction { UPSwipeActionItem(UPSwipeActionItemProps(show = true, options = listOf(mapOf("text" to "删除")))) { BasicText("向左滑动", Modifier.padding(12.dp)) } } }
            DemoSection("滑动操作项") { UPSwipeActionItem(UPSwipeActionItemProps(show = true, options = listOf(mapOf("text" to "删除")))) { BasicText("操作项") } }
            DemoSection("轮播图") { UPSwiper(UPSwiperProps(list = listOf("第一页", "第二页"), indicator = true)) }
            DemoSection("轮播指示器") { UPSwiperIndicator(UPSwiperIndicatorProps(length = 3, current = 1)) }
            DemoSection("骨架屏") { UPSkeleton(UPSkeletonProps(rows = 3, avatar = true)) }
            DemoSection("展开阅读") { UPReadMore(UPReadMoreProps(showHeight = 48)) { BasicText("这是一段较长的内容，用于展示展开阅读组件在原生页面中的截断与展开行为。") } }
            DemoSection("纵向通知") { UPColumnNotice(UPColumnNoticeProps(text = listOf("第一条通知", "第二条通知"))) }
            DemoSection("横向通知") { UPRowNotice(UPRowNoticeProps(text = "横向滚动通知")) }
            DemoSection("数字滚动") { UPCountTo(UPCountToProps(startVal = 0, endVal = 128, autoplay = false)) }
            DemoSection("倒计时") { UPCountDown(UPCountDownProps(time = 61000, autoStart = false)) }
            DemoSection("选择器") { UPPicker(UPPickerProps(show = true, title = "城市", columns = listOf(listOf(mapOf("text" to "北京", "value" to "bj"))))) }
            DemoSection("选择器列") { UPPickerColumn { BasicText("北京") } }
            DemoSection("分页") { UPPagination(UPPaginationProps(total = 42)) }
            DemoSection("下拉选择") { UPSelect(UPSelectProps(options = listOf(mapOf("id" to 1, "name" to "北京"), mapOf("id" to 2, "name" to "上海")), current = select), onUpdateCurrent = { select = it }) }
        }
    }
}
