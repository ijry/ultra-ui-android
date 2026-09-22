package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/**
 * Props for [UPCityLocate], mirroring uview-plus `u-city-locate`.
 *
 * A city picker built on `u-index-list`: a located-city header, then `cityList` grouped by the
 * `indexList` rail (index 0 is the hot-city grid). `nameKey` reads each city's display name;
 * `currentCity` seeds the header's located-city text. `locationType` is the geo coordinate system
 * requested upstream (`uni.getLocation`); Android delegates the actual lookup to the host.
 */
public data class UPCityLocateProps(
    val indexList: List<UPRawValue> = listOf("🔥"),
    val cityList: List<UPRawValue> = emptyList(),
    val locationType: String = "wgs84",
    val currentCity: String = "",
    val nameKey: String = "name",
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
