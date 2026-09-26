package net.lingyun.ultraui.android.sample.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.lingyun.ultraui.android.core.UPTheme

/**
 * 组件索引首页：1:1 复刻 uview-plus 演示工程 `pages/example/components` 的搜索框 + 分组列表体验。
 * 顶部搜索按标题过滤，点条目进入对应组件的独立 demo 页。
 */
@Composable
public fun ComponentIndexPage(
    onOpen: (String) -> Unit,
    onOpenLegacy: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var query by remember { mutableStateOf("") }
    val keyword = query.trim()
    val filtered = remember(keyword) {
        if (keyword.isEmpty()) {
            demoGroups
        } else {
            demoGroups
                .map { group ->
                    group.copy(entries = group.entries.filter { it.title.contains(keyword, ignoreCase = true) })
                }
                .filter { it.entries.isNotEmpty() }
        }
    }

    Column(modifier = modifier.fillMaxSize().background(UPTheme.Background)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 12.dp),
        ) {
            TextField(
                value = query,
                onValueChange = { query = it },
                singleLine = true,
                placeholder = { Text("搜索组件", color = UPTheme.Tips) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = UPTheme.Background,
                    unfocusedContainerColor = UPTheme.Background,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
            )
        }
        HorizontalDivider(color = UPTheme.Border)
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item(key = "legacy-grouped") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .clickable(role = Role.Button, onClick = onOpenLegacy)
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(text = "查看旧版分组示例", color = UPTheme.Primary)
                    Text(text = "›", color = UPTheme.Tips, fontSize = 18.sp)
                }
                HorizontalDivider(color = UPTheme.Border)
            }
            filtered.forEach { group ->
                item(key = "group-" + group.name) {
                    Text(
                        text = group.name,
                        color = UPTheme.Content,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                    )
                }
                items(group.entries, key = { it.id }) { entry ->
                    ComponentIndexRow(entry = entry, onOpen = onOpen)
                    HorizontalDivider(color = UPTheme.Border, modifier = Modifier.padding(start = 16.dp))
                }
            }
        }
    }
}

@Composable
private fun ComponentIndexRow(entry: DemoEntry, onOpen: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .then(
                if (entry.available) {
                    Modifier.clickable(role = Role.Button) { onOpen(entry.id) }
                } else {
                    Modifier
                }
            )
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = entry.title,
            color = if (entry.available) UPTheme.Main else UPTheme.Tips,
        )
        Text(
            text = if (entry.available) "›" else "暂无",
            color = UPTheme.Tips,
            fontSize = if (entry.available) 18.sp else 13.sp,
        )
    }
}
