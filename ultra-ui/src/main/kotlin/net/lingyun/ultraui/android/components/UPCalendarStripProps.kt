package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/**
 * Props for [UPCalendarStrip], mirroring uview-plus `u-calendar-strip`.
 *
 * A horizontal month day strip with a month header. `modelValue` is the selected date
 * (`YYYY-MM-DD`); `minDate`/`maxDate` bound selection; `color` tints the selected day; `weekText`
 * labels the weekday row (index 0 = Monday, matching upstream). `showToday` highlights today;
 * `readonly` blocks taps; `monthFormat` overrides the header label.
 */
public data class UPCalendarStripProps(
    val modelValue: UPRawValue = null,
    val minDate: UPRawValue = 0,
    val maxDate: UPRawValue = 0,
    val color: String = "#3c9cff",
    val weekText: List<String> = listOf("一", "二", "三", "四", "五", "六", "日"),
    val readonly: Boolean = false,
    val showToday: Boolean = true,
    val monthFormat: String = "",
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
