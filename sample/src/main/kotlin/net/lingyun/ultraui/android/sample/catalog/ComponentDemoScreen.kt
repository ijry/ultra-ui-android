package net.lingyun.ultraui.android.sample.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.sample.SampleScaffold

/** 单个组件的独立 demo 页：顶部返回栏 + 事件反馈条 + 该组件的演示内容。 */
@Composable
public fun ComponentDemoScreen(
    id: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val entry = demoEntryById[id]
    val title = entry?.title ?: id
    var eventText by remember(id) { mutableStateOf("等待「$title」交互") }

    SampleScaffold(title = title, onBack = onBack, modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = eventText,
                color = UPTheme.Content,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .background(Color.White, RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 10.dp),
            )
            ComponentDemoContent(id = id, onEvent = { eventText = it })
        }
    }
}
