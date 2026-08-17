package net.lingyun.ultraui.android.sample

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import net.lingyun.ultraui.android.core.UPTheme

/** A completed sample page that can be shown from the catalog. */
public data class SampleDestination(
    val route: String,
    val group: String,
    val title: String,
)

/** Stable route identifiers reserved for the first UltraUI Android milestone. */
public object SampleRoutes {
    public const val Catalog: String = "catalog"
    public const val Button: String = "button"
    public const val Icon: String = "icon"
    public const val LoadingIcon: String = "loading-icon"
    public const val Overlay: String = "overlay"
    public const val Popup: String = "popup"
    public const val Cell: String = "cell"
    public const val Toast: String = "toast"
    public const val Tag: String = "tag"
    public const val Modal: String = "modal"
}

/** Completed destinations from the current Android uview-plus compatibility milestone. */
public val sampleDestinations: List<SampleDestination> = listOf(
    SampleDestination(route = SampleRoutes.Icon, group = "Components A", title = "图标"),
    SampleDestination(route = SampleRoutes.LoadingIcon, group = "Components A", title = "加载中图标"),
)

private val sampleGroups: List<String> = listOf("Components A", "Components B", "Components C")

/** The home page shared by all deterministic UltraUI component demos. */
@Composable
public fun SampleCatalog(
    destinations: List<SampleDestination> = sampleDestinations,
    onDestinationClick: (SampleDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.background(UPTheme.Background),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        sampleGroups.forEach { group ->
            item(key = "header-$group") {
                Text(
                    text = group,
                    color = UPTheme.Tips,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
            val groupedDestinations = destinations.filter { it.group == group }
            if (groupedDestinations.isEmpty()) {
                item(key = "empty-$group") {
                    Text(
                        text = "暂无已完成组件",
                        color = UPTheme.Light,
                        modifier = Modifier.padding(horizontal = 16.dp),
                    )
                }
            } else {
                items(items = groupedDestinations, key = SampleDestination::route) { destination ->
                    SampleDestinationRow(destination = destination, onClick = { onDestinationClick(destination) })
                }
            }
        }
    }
}

@Composable
private fun SampleDestinationRow(destination: SampleDestination, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(UPTheme.Background)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 16.dp),
    ) {
        Text(
            text = destination.title,
            color = UPTheme.Main,
            modifier = Modifier.padding(vertical = 16.dp),
        )
        HorizontalDivider(color = UPTheme.Border)
    }
}
