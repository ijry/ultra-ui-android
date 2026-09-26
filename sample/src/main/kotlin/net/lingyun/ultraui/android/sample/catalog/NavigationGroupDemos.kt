package net.lingyun.ultraui.android.sample.catalog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.lingyun.ultraui.android.components.UPBackTop
import net.lingyun.ultraui.android.components.UPBackTopProps
import net.lingyun.ultraui.android.components.UPDropdown
import net.lingyun.ultraui.android.components.UPDropdownItem
import net.lingyun.ultraui.android.components.UPDropdownItemProps
import net.lingyun.ultraui.android.components.UPDropdownProps
import net.lingyun.ultraui.android.components.UPEmpty
import net.lingyun.ultraui.android.components.UPEmptyProps
import net.lingyun.ultraui.android.components.UPIndexAnchor
import net.lingyun.ultraui.android.components.UPIndexAnchorProps
import net.lingyun.ultraui.android.components.UPIndexItem
import net.lingyun.ultraui.android.components.UPIndexList
import net.lingyun.ultraui.android.components.UPNavbar
import net.lingyun.ultraui.android.components.UPNavbarMini
import net.lingyun.ultraui.android.components.UPNavbarMiniProps
import net.lingyun.ultraui.android.components.UPNavbarProps
import net.lingyun.ultraui.android.components.UPPagination
import net.lingyun.ultraui.android.components.UPPaginationProps
import net.lingyun.ultraui.android.components.UPSteps
import net.lingyun.ultraui.android.components.UPStepsItem
import net.lingyun.ultraui.android.components.UPStepsItemProps
import net.lingyun.ultraui.android.components.UPSubsection
import net.lingyun.ultraui.android.components.UPSubsectionProps
import net.lingyun.ultraui.android.components.UPTabbar
import net.lingyun.ultraui.android.components.UPTabbarItem
import net.lingyun.ultraui.android.components.UPTabbarItemProps
import net.lingyun.ultraui.android.components.UPTabbarProps
import net.lingyun.ultraui.android.components.UPTabs
import net.lingyun.ultraui.android.components.UPTabsProps
import net.lingyun.ultraui.android.components.UPTree
import net.lingyun.ultraui.android.components.UPTreeProps
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.sample.DemoSection

/** 导航组件分组的逐组件 demo；未命中返回 false 交回主分发器。 */
@Composable
internal fun renderNavigationGroup(id: String, onEvent: (String) -> Unit): Boolean {
    when (id) {
        "dropdown" -> DropdownDemo(onEvent)
        "tabbar" -> TabbarDemo(onEvent)
        "backtop" -> BackTopDemo(onEvent)
        "navbar" -> NavbarDemo()
        "navbar-mini" -> NavbarMiniDemo()
        "tabs" -> TabsDemo(onEvent)
        "subsection" -> SubsectionDemo(onEvent)
        "index-list" -> IndexListDemo()
        "steps" -> StepsDemo()
        "empty" -> EmptyDemo()
        "pagination" -> PaginationDemo()
        "tree" -> TreeDemo(onEvent)
        else -> return false
    }
    return true
}

@Composable
private fun DropdownDemo(onEvent: (String) -> Unit) {
    var value: UPRawValue by remember { mutableStateOf("all") }
    DemoSection(title = "下拉菜单") {
        UPDropdown(
            props = UPDropdownProps(),
            onOpen = { onEvent("下拉菜单：打开") },
            onClose = { onEvent("下拉菜单：关闭") },
        ) {
            UPDropdownItem(
                props = UPDropdownItemProps(
                    title = "状态",
                    options = listOf(mapOf("label" to "全部", "value" to "all"), mapOf("label" to "已完成", "value" to "done")),
                    modelValue = value,
                ),
                onUpdateModelValue = { value = it; onEvent("下拉菜单：选择 $it") },
            )
        }
    }
}

@Composable
private fun TabbarDemo(onEvent: (String) -> Unit) {
    var value by remember { mutableStateOf<UPRawValue>("home") }
    DemoSection(title = "底部导航栏") {
        UPTabbar(
            props = UPTabbarProps(value = value, fixed = false, placeholder = false),
            onUpdateValue = { value = it; onEvent("底部导航：$it") },
        ) {
            UPTabbarItem(UPTabbarItemProps(name = "home", icon = "home", text = "首页"))
            UPTabbarItem(UPTabbarItemProps(name = "message", icon = "chat", text = "消息", badge = 3))
            UPTabbarItem(UPTabbarItemProps(name = "mine", icon = "account", text = "我的"))
        }
    }
}

@Composable
private fun BackTopDemo(onEvent: (String) -> Unit) {
    DemoSection(title = "返回顶部") {
        Box(modifier = Modifier.fillMaxWidth().height(80.dp)) {
            UPBackTop(props = UPBackTopProps(scrollTop = 800, text = "顶部"), onClick = { onEvent("返回顶部：点击") })
        }
    }
}

@Composable
private fun NavbarDemo() {
    DemoSection(title = "导航栏") { UPNavbar(UPNavbarProps(title = "订单详情")) }
}

@Composable
private fun NavbarMiniDemo() {
    DemoSection(title = "迷你导航栏") { UPNavbarMini(UPNavbarMiniProps(homeUrl = "/")) }
}

@Composable
private fun TabsDemo(onEvent: (String) -> Unit) {
    var tab by remember { mutableIntStateOf(0) }
    DemoSection(title = "标签") {
        UPTabs(UPTabsProps(list = listOf("全部", "待支付", "已完成"), current = tab), onChange = { tab = it; onEvent("标签：$it") })
    }
}

@Composable
private fun SubsectionDemo(onEvent: (String) -> Unit) {
    var subsection by remember { mutableIntStateOf(0) }
    DemoSection(title = "分段器") {
        UPSubsection(UPSubsectionProps(list = listOf("日", "周", "月"), current = subsection), onChange = { subsection = it; onEvent("分段器：$it") })
    }
}

@Composable
private fun IndexListDemo() {
    DemoSection(title = "索引列表") {
        UPIndexList {
            UPIndexAnchor(UPIndexAnchorProps(text = "A"))
            UPIndexItem { BasicText("Apple", Modifier.padding(10.dp)) }
            UPIndexAnchor(UPIndexAnchorProps(text = "B"))
            UPIndexItem { BasicText("Banana", Modifier.padding(10.dp)) }
        }
    }
}

@Composable
private fun StepsDemo() {
    DemoSection(title = "步骤条") {
        UPSteps {
            UPStepsItem(UPStepsItemProps(title = "提交订单", desc = "已完成"))
            UPStepsItem(UPStepsItemProps(title = "配送中", desc = "处理中"))
            UPStepsItem(UPStepsItemProps(title = "已签收", desc = "待处理"))
        }
    }
}

@Composable
private fun EmptyDemo() {
    DemoSection(title = "内容为空") { UPEmpty(props = UPEmptyProps(mode = "data", text = "暂无数据")) }
}

@Composable
private fun PaginationDemo() {
    DemoSection(title = "分页器") { UPPagination(UPPaginationProps(total = 42)) }
}

@Composable
private fun TreeDemo(onEvent: (String) -> Unit) {
    DemoSection(title = "树形") {
        UPTree(
            props = UPTreeProps(
                defaultExpandAll = false,
                data = listOf(
                    mapOf(
                        "id" to "a", "label" to "一级 A",
                        "children" to listOf(
                            mapOf("id" to "a1", "label" to "二级 A-1"),
                            mapOf("id" to "a2", "label" to "二级 A-2"),
                        ),
                    ),
                    mapOf("id" to "b", "label" to "一级 B"),
                ),
            ),
            onNodeClick = { onEvent("树：${it["label"]}") },
        )
    }
}
