package net.lingyun.ultraui.android.sample.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.lingyun.ultraui.android.components.UPLoadingIcon
import net.lingyun.ultraui.android.components.UPLoadingIconProps
import net.lingyun.ultraui.android.sample.DemoSection
import net.lingyun.ultraui.android.sample.SampleScaffold

/** Exact demo cases from pinned uview-plus componentsA/loading-icon/loading-icon.nvue. */
@Composable
public fun LoadingIconDemoPage(onBack: () -> Unit, modifier: Modifier = Modifier) {
    SampleScaffold(title = "加载中图标", onBack = onBack, modifier = modifier) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(items = loadingIconDemoCases, key = LoadingIconDemoCase::title) { demoCase ->
                DemoSection(title = demoCase.title) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 5.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        UPLoadingIcon(props = demoCase.props)
                    }
                }
            }
        }
    }
}

private data class LoadingIconDemoCase(
    val title: String,
    val props: UPLoadingIconProps,
)

private val loadingIconDemoCases: List<LoadingIconDemoCase> = listOf(
    LoadingIconDemoCase("基本案列", UPLoadingIconProps()),
    LoadingIconDemoCase("半圆loading", UPLoadingIconProps(mode = "semicircle")),
    LoadingIconDemoCase("圆形loading", UPLoadingIconProps(mode = "circle")),
    LoadingIconDemoCase("自定义动画", UPLoadingIconProps(mode = "circle", timingFunction = "linear")),
    LoadingIconDemoCase("自定义颜色", UPLoadingIconProps(color = "#19be6b")),
    LoadingIconDemoCase("自定义文字", UPLoadingIconProps(vertical = true, text = "加载中")),
)
