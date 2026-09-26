package net.lingyun.ultraui.android.sample.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.lingyun.ultraui.android.components.UPButton
import net.lingyun.ultraui.android.components.UPButtonProps
import net.lingyun.ultraui.android.components.UPCircleProgress
import net.lingyun.ultraui.android.components.UPCountDown
import net.lingyun.ultraui.android.components.UPCountDownProps
import net.lingyun.ultraui.android.components.UPCountTo
import net.lingyun.ultraui.android.components.UPCountToProps
import net.lingyun.ultraui.android.components.UPLineProgress
import net.lingyun.ultraui.android.components.UPList
import net.lingyun.ultraui.android.components.UPListItem
import net.lingyun.ultraui.android.components.UPListProps
import net.lingyun.ultraui.android.components.UPTable
import net.lingyun.ultraui.android.components.UPTableProps
import net.lingyun.ultraui.android.components.UPTable2
import net.lingyun.ultraui.android.components.UPTable2Props
import net.lingyun.ultraui.android.components.UPTd
import net.lingyun.ultraui.android.components.UPTh
import net.lingyun.ultraui.android.components.UPTr
import net.lingyun.ultraui.android.components.UPVirtualList
import net.lingyun.ultraui.android.components.UPVirtualListProps
import net.lingyun.ultraui.android.components.rememberUPCountDownController
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.sample.DemoSection

/** 数据组件分组的逐组件 demo；未命中返回 false 交回主分发器。 */
@Composable
internal fun renderDataGroup(id: String, onEvent: (String) -> Unit): Boolean {
    when (id) {
        "list" -> ListDemo()
        "virtual-list" -> VirtualListDemo()
        "progress" -> ProgressDemo(onEvent)
        "table" -> TableDemo()
        "table2" -> Table2Demo(onEvent)
        "count-down" -> CountDownDemo()
        "count-to" -> CountToDemo()
        else -> return false
    }
    return true
}

@Composable
private fun ListDemo() {
    DemoSection(title = "列表") {
        UPList(props = UPListProps(height = 180)) {
            (1..12).forEach { i ->
                UPListItem {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 14.dp),
                    ) { BasicText("列表项 $i") }
                }
            }
        }
    }
}

@Composable
private fun VirtualListDemo() {
    DemoSection(title = "虚拟列表") {
        UPVirtualList(
            props = UPVirtualListProps(
                listData = (1..200).map { mapOf("id" to it, "label" to "虚拟行 $it") },
                itemHeight = 44,
                height = "180",
            ),
        ) { item, _ ->
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 12.dp)) {
                BasicText((item["label"] ?: "").toString())
            }
        }
    }
}

@Composable
private fun ProgressDemo(onEvent: (String) -> Unit) {
    var percentage by remember { mutableIntStateOf(45) }
    Column {
        DemoSection(title = "线性进度") {
            UPLineProgress(percentage = percentage, showText = true, height = 18)
            UPButton(props = UPButtonProps(text = "增加进度", type = "primary", size = "small"), onClick = {
                percentage = (percentage + 15).let { if (it > 100) 0 else it }
                onEvent("线性进度：$percentage%")
            })
        }
        DemoSection(title = "环形进度") {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                UPCircleProgress(percentage = percentage)
            }
        }
    }
}

@Composable
private fun TableDemo() {
    val rows = listOf(
        listOf("张三", "28", "北京"),
        listOf("李四", "34", "上海"),
        listOf("王五", "22", "广州"),
    )
    DemoSection(title = "表格") {
        UPTable(props = UPTableProps()) {
            UPTr {
                UPTh(text = "姓名")
                UPTh(text = "年龄")
                UPTh(text = "城市")
            }
            rows.forEach { r ->
                UPTr {
                    r.forEach { c -> UPTd(text = c) }
                }
            }
        }
    }
}

@Composable
private fun Table2Demo(onEvent: (String) -> Unit) {
    DemoSection(title = "表格2") {
        UPTable2(
            props = UPTable2Props(
                data = listOf(
                    mapOf("id" to 1, "name" to "张三", "age" to 28, "city" to "北京"),
                    mapOf("id" to 2, "name" to "李四", "age" to 34, "city" to "上海"),
                    mapOf("id" to 3, "name" to "王五", "age" to 22, "city" to "广州"),
                ),
                columns = listOf(
                    mapOf("type" to "selection", "width" to "48px"),
                    mapOf("key" to "name", "title" to "姓名", "width" to "90px"),
                    mapOf("key" to "age", "title" to "年龄", "width" to "80px", "sortable" to true, "align" to "center"),
                    mapOf("key" to "city", "title" to "城市", "width" to "90px"),
                ),
                stripe = true,
                border = true,
                highlightCurrentRow = true,
            ),
            onCurrentChange = { onEvent("表格2：选中 ${it["name"]}") },
            onSortChange = { k, o -> onEvent("表格2：排序 $k $o") },
            onSelectionChange = { onEvent("表格2：勾选 ${it.size} 行") },
        )
    }
}

@Composable
private fun CountDownDemo() {
    val countDown = rememberUPCountDownController()
    DemoSection(title = "倒计时") {
        UPCountDown(UPCountDownProps(time = 61000, autoStart = false), controller = countDown)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            UPButton(props = UPButtonProps(text = "开始", type = "primary", size = "mini"), onClick = { countDown.start() })
            UPButton(props = UPButtonProps(text = "暂停", size = "mini"), onClick = { countDown.pause() })
            UPButton(props = UPButtonProps(text = "重设", size = "mini"), onClick = { countDown.reset() })
        }
    }
}

@Composable
private fun CountToDemo() {
    DemoSection(title = "数字滚动") {
        UPCountTo(UPCountToProps(startVal = 0, endVal = 128, autoplay = true))
    }
}
