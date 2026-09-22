package net.lingyun.ultraui.android.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.core.upClickable
import net.lingyun.ultraui.android.core.upListOrEmpty
import net.lingyun.ultraui.android.core.upTestTag

/**
 * Native Compose counterpart of uview-plus `u-city-locate`.
 *
 * A city picker over `u-index-list`: a header showing the located city, then `cityList` grouped
 * under the `indexList` rail — group 0 renders as a hot-city grid, the rest as a divided list.
 * Tapping the located-city header calls [onLocate] (upstream `uni.getLocation`); tapping a city
 * updates the header and emits `select-city` (surfaced as [onSelectCity] with the city map). The
 * `location-success` event is left to the host, which owns the actual geolocation lookup.
 */
@Composable
public fun UPCityLocate(
    props: UPCityLocateProps = UPCityLocateProps(),
    modifier: Modifier = Modifier,
    onSelectCity: ((Map<String, UPRawValue>) -> Unit)? = null,
    onLocate: (() -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    val style = rememberUPResolvedStyle(props.customStyle, diagnostics, "UPCityLocate")
    var locationCity by remember(props.currentCity) { mutableStateOf(props.currentCity.ifEmpty { "定位中...." }) }

    Column(modifier.fillMaxWidth().applyUPResolvedStyle(style).upTestTag("city-locate")) {
        UPIndexList(props = UPIndexListProps(indexList = props.indexList), diagnostics = diagnostics) {
            // Header: located city.
            Column(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp).upTestTag("city-locate-header")) {
                BasicText("定位城市", style = TextStyle(color = UPTheme.Tips, fontSize = 12.sp))
                BasicText(
                    locationCity,
                    modifier = Modifier.padding(top = 6.dp).upTestTag("city-locate-current").upClickable(onClick = { onLocate?.invoke() }),
                    style = TextStyle(color = UPTheme.Main, fontSize = 15.sp, fontWeight = FontWeight.Medium),
                )
            }

            props.cityList.forEachIndexed { index, group ->
                UPIndexItem {
                    UPIndexAnchor(UPIndexAnchorProps(text = props.indexList.getOrNull(index) ?: ""), diagnostics = diagnostics)
                    val cities = group.upListOrEmpty()
                    if (index == 0) {
                        // Hot-city grid.
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp).upTestTag("city-locate-hot"),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            cities.forEach { c ->
                                val map = c.upStringKeyMapOrEmpty()
                                BasicText(
                                    map[props.nameKey].upStringValueOrEmpty(),
                                    modifier = Modifier.upClickable(onClick = {
                                        locationCity = map[props.nameKey].upStringValueOrEmpty()
                                        onSelectCity?.invoke(map)
                                    }),
                                    style = TextStyle(color = UPTheme.Content, fontSize = 13.sp),
                                )
                            }
                        }
                    } else {
                        cities.forEach { c ->
                            val map = c.upStringKeyMapOrEmpty()
                            Column {
                                BasicText(
                                    map[props.nameKey].upStringValueOrEmpty(),
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 12.dp).upClickable(onClick = {
                                        locationCity = map[props.nameKey].upStringValueOrEmpty()
                                        onSelectCity?.invoke(map)
                                    }),
                                    style = TextStyle(color = UPTheme.Main, fontSize = 14.sp),
                                )
                                UPLine()
                            }
                        }
                    }
                }
            }
        }
    }
}
