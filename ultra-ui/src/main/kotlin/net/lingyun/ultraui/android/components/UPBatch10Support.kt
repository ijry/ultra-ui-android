package net.lingyun.ultraui.android.components

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import net.lingyun.ultraui.android.core.UPRawValue
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.round

internal data class UPDatetimeSelection(val values: List<Int>)

internal fun updateCalendarSelection(
    mode: String,
    selected: List<String>,
    date: String,
    maxCount: Int,
    allowSameDay: Boolean,
): List<String> = when (mode) {
    "multiple" -> if (date in selected) selected - date else (selected + date).take(maxCount.coerceAtLeast(1))
    "range" -> when {
        selected.size != 1 -> listOf(date)
        date < selected.first() -> listOf(date)
        date == selected.first() && !allowSameDay -> selected
        else -> listOf(selected.first(), date)
    }
    else -> listOf(date)
}

internal fun calendarDateAllowed(props: UPCalendarProps, date: String): Boolean {
    val minDate = normalizedCalendarBoundary(props.minDate)
    val maxDate = normalizedCalendarBoundary(props.maxDate)
    val forbidden = props.forbidDays.any { value ->
        value?.toString()?.trim() == date || normalizedCalendarBoundary(value) == date
    }
    return !forbidden && (minDate == null || date >= minDate) && (maxDate == null || date <= maxDate)
}

internal fun resolveDatetimeSelection(props: UPDatetimePickerProps): UPDatetimeSelection {
    val controlled = props.modelValue ?: props.value
    val timeParts = (controlled as? String)
        ?.takeIf { props.mode == "time" || props.mode == "timesecond" }
        ?.split(':')
        ?.mapNotNull(String::toIntOrNull)
        .orEmpty()
    val minHour = props.minHour.rawInt(0)
    val maxHour = props.maxHour.rawInt(23).coerceAtLeast(minHour)
    val minMinute = props.minMinute.rawInt(0)
    val maxMinute = props.maxMinute.rawInt(59).coerceAtLeast(minMinute)
    val minSecond = props.minSecond.rawInt(0)
    val maxSecond = props.maxSecond.rawInt(59).coerceAtLeast(minSecond)
    val timestamp = when (controlled) {
        is Number -> controlled.toLong()
        is String -> controlled.toLongOrNull() ?: parseDateString(controlled)
        else -> null
    } ?: props.minDate.rawLong(System.currentTimeMillis())
    val calendar = Calendar.getInstance().apply {
        timeInMillis = timestamp.coerceIn(
            props.minDate.rawLong(Long.MIN_VALUE),
            props.maxDate.rawLong(Long.MAX_VALUE).coerceAtLeast(props.minDate.rawLong(Long.MIN_VALUE)),
        )
        if (props.mode == "time" || props.mode == "timesecond") {
            set(Calendar.HOUR_OF_DAY, timeParts.getOrNull(0)?.coerceIn(minHour, maxHour) ?: minHour)
            set(Calendar.MINUTE, timeParts.getOrNull(1)?.coerceIn(minMinute, maxMinute) ?: minMinute)
            set(Calendar.SECOND, if (props.mode == "timesecond") timeParts.getOrNull(2)?.coerceIn(minSecond, maxSecond) ?: minSecond else 0)
        }
    }
    return UPDatetimeSelection(
        listOf(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH) + 1,
            calendar.get(Calendar.DAY_OF_MONTH),
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            calendar.get(Calendar.SECOND),
        ),
    )
}

internal fun datetimeColumns(props: UPDatetimePickerProps, state: UPDatetimeSelection): List<List<Int>> {
    val minTimestamp = props.minDate.rawLong(Long.MIN_VALUE)
    val maxTimestamp = props.maxDate.rawLong(Long.MAX_VALUE).coerceAtLeast(minTimestamp)
    val minDate = dateCalendar(minTimestamp)
    val maxDate = dateCalendar(maxTimestamp)
    val minYear = minDate.get(Calendar.YEAR)
    val maxYear = maxDate.get(Calendar.YEAR)
    val year = state.values[0].coerceIn(minYear, maxYear)
    val monthRange = (
        if (year == minYear) minDate.get(Calendar.MONTH) + 1 else 1
        )..(
        if (year == maxYear) maxDate.get(Calendar.MONTH) + 1 else 12
        )
    val month = state.values[1].coerceIn(monthRange.first, monthRange.last)
    val days = Calendar.getInstance().apply { set(year, month - 1, 1) }.getActualMaximum(Calendar.DAY_OF_MONTH)
    val dayRange = (
        if (year == minYear && month == minDate.get(Calendar.MONTH) + 1) minDate.get(Calendar.DAY_OF_MONTH) else 1
        )..(
        if (year == maxYear && month == maxDate.get(Calendar.MONTH) + 1) maxDate.get(Calendar.DAY_OF_MONTH) else days
        )
    val day = state.values[2].coerceIn(dayRange.first, dayRange.last)
    val atMinDate = year == minYear && month == minDate.get(Calendar.MONTH) + 1 && day == minDate.get(Calendar.DAY_OF_MONTH)
    val atMaxDate = year == maxYear && month == maxDate.get(Calendar.MONTH) + 1 && day == maxDate.get(Calendar.DAY_OF_MONTH)
    val dateHourRange = (if (atMinDate) minDate.get(Calendar.HOUR_OF_DAY) else 0)..(if (atMaxDate) maxDate.get(Calendar.HOUR_OF_DAY) else 23)
    val hour = state.values[3].coerceIn(dateHourRange.first, dateHourRange.last)
    val atMinHour = atMinDate && hour == minDate.get(Calendar.HOUR_OF_DAY)
    val atMaxHour = atMaxDate && hour == maxDate.get(Calendar.HOUR_OF_DAY)
    val dateMinuteRange = (if (atMinHour) minDate.get(Calendar.MINUTE) else 0)..(if (atMaxHour) maxDate.get(Calendar.MINUTE) else 59)
    val minute = state.values[4].coerceIn(dateMinuteRange.first, dateMinuteRange.last)
    val atMinMinute = atMinHour && minute == minDate.get(Calendar.MINUTE)
    val atMaxMinute = atMaxHour && minute == maxDate.get(Calendar.MINUTE)
    val dateSecondRange = (if (atMinMinute) minDate.get(Calendar.SECOND) else 0)..(if (atMaxMinute) maxDate.get(Calendar.SECOND) else 59)
    val timeHourRange = props.minHour.rawInt(0)..props.maxHour.rawInt(23).coerceAtLeast(props.minHour.rawInt(0))
    val timeMinuteRange = props.minMinute.rawInt(0)..props.maxMinute.rawInt(59).coerceAtLeast(props.minMinute.rawInt(0))
    val timeSecondRange = props.minSecond.rawInt(0)..props.maxSecond.rawInt(59).coerceAtLeast(props.minSecond.rawInt(0))
    return when (props.mode) {
        "year-month" -> listOf((minYear..maxYear).toList(), monthRange.toList())
        "date" -> listOf((minYear..maxYear).toList(), monthRange.toList(), dayRange.toList())
        "datehour" -> listOf((minYear..maxYear).toList(), monthRange.toList(), dayRange.toList(), dateHourRange.toList())
        "time" -> listOf(timeHourRange.toList(), timeMinuteRange.toList())
        "timesecond" -> listOf(timeHourRange.toList(), timeMinuteRange.toList(), timeSecondRange.toList())
        "datetimesecond" -> listOf((minYear..maxYear).toList(), monthRange.toList(), dayRange.toList(), dateHourRange.toList(), dateMinuteRange.toList(), dateSecondRange.toList())
        else -> listOf((minYear..maxYear).toList(), monthRange.toList(), dayRange.toList(), dateHourRange.toList(), dateMinuteRange.toList())
    }
}

internal fun datetimeTimestamp(props: UPDatetimePickerProps, state: UPDatetimeSelection): Long {
    val values = state.values + List((6 - state.values.size).coerceAtLeast(0)) { 0 }
    val calendar = Calendar.getInstance().apply {
        set(Calendar.YEAR, values[0])
        set(Calendar.MONTH, (values[1] - 1).coerceIn(0, 11))
        set(Calendar.DAY_OF_MONTH, if (props.mode == "year-month") 1 else values[2].coerceAtLeast(1))
        set(Calendar.HOUR_OF_DAY, if (props.mode in setOf("datehour", "datetime", "datetimesecond")) values[3] else 0)
        set(Calendar.MINUTE, if (props.mode in setOf("datetime", "datetimesecond")) values[4] else 0)
        set(Calendar.SECOND, if (props.mode == "datetimesecond") values[5] else 0)
        set(Calendar.MILLISECOND, 0)
    }
    val min = props.minDate.rawLong(Long.MIN_VALUE)
    val max = props.maxDate.rawLong(Long.MAX_VALUE)
    return calendar.timeInMillis.coerceIn(min, max.coerceAtLeast(min))
}

internal fun datetimeValueIndex(mode: String, column: Int): Int = when (mode) {
    "time", "timesecond" -> column + 3
    else -> column
}

internal fun updateDatetimeSelection(
    props: UPDatetimePickerProps,
    state: UPDatetimeSelection,
    column: Int,
    option: Int,
): UPDatetimeSelection {
    val values = state.values.toMutableList()
    values[datetimeValueIndex(props.mode, column)] = option
    val changed = UPDatetimeSelection(values)
    datetimeColumns(props, changed).forEachIndexed { columnIndex, options ->
        if (options.isNotEmpty()) {
            val valueIndex = datetimeValueIndex(props.mode, columnIndex)
            values[valueIndex] = values[valueIndex].coerceIn(options.first(), options.last())
        }
    }
    return UPDatetimeSelection(values)
}

internal fun datetimeEvent(
    props: UPDatetimePickerProps,
    state: UPDatetimeSelection,
    columns: List<List<Int>> = datetimeColumns(props, state),
): UPDatetimePickerEvent {
    val indexes = columns.mapIndexed { columnIndex, values ->
        values.indexOf(state.values[datetimeValueIndex(props.mode, columnIndex)]).coerceAtLeast(0)
    }
    val selected = columns.mapIndexed { columnIndex, _ -> state.values[datetimeValueIndex(props.mode, columnIndex)] }
    val value: UPRawValue = when (props.mode) {
        "time", "timesecond" -> selected.joinToString(":") { it.toString().padStart(2, '0') }
        else -> datetimeTimestamp(props, state)
    }
    return UPDatetimePickerEvent(value, selected, indexes)
}

internal fun cascaderOptionsAt(props: UPCascaderProps, path: List<UPRawValue>, level: Int): List<UPRawValue> {
    var options = props.data
    repeat(level) { pathLevel ->
        val selected = options.firstOrNull { it.rawMap()[props.valueKey].rawEquals(path.getOrNull(pathLevel)) }
        options = selected.rawMap()[props.childrenKey].rawList()
    }
    return options
}

internal fun updateCascaderSelection(
    props: UPCascaderProps,
    path: List<UPRawValue>,
    level: Int,
    option: UPRawValue,
): UPCascaderEvent {
    val next = path.take(level) + option.rawMap()[props.valueKey]
    val selected = next.mapIndexedNotNull { index, value ->
        cascaderOptionsAt(props, next, index).firstOrNull { it.rawMap()[props.valueKey].rawEquals(value) }
    }
    return UPCascaderEvent(next, selected, level)
}

internal fun quantizeSliderValue(value: Float, min: Float, max: Float, step: Float): Float {
    val upper = max.coerceAtLeast(min)
    val safeStep = step.takeIf { it.isFinite() && it > 0f } ?: 1f
    return (min + round((value.coerceIn(min, upper) - min) / safeStep) * safeStep).coerceIn(min, upper)
}

internal fun sliderPositionFraction(position: Offset, size: IntSize, vertical: Boolean): Float =
    if (vertical) {
        1f - position.y / size.height.coerceAtLeast(1)
    } else {
        position.x / size.width.coerceAtLeast(1)
    }.coerceIn(0f, 1f)

internal fun sliderAxisOffset(fraction: Float, trackLength: Float, vertical: Boolean): Float =
    (if (vertical) 1f - fraction else fraction) * trackLength

internal fun isTabbarMiddleButton(mode: String): Boolean = mode == "midButton"

/**
 * uview sizes tabbar glyphs at a fixed 24 and only defers to `midButtonIconSize`
 * for the raised middle button (`u-tabbar-item.vue`: `isMidButton ? midButtonIconSize : 24`).
 */
internal fun tabbarIconSize(mode: String, midButtonIconSize: UPRawValue): UPRawValue =
    if (isTabbarMiddleButton(mode)) midButtonIconSize else 24

internal fun tabbarBadgeIsVisible(dot: Boolean, badge: UPRawValue): Boolean = dot || badge.rawInt(0) > 0

internal fun sliderTrackFractions(
    values: List<Float>,
    min: Float,
    max: Float,
    range: Boolean,
): Pair<Float, Float> {
    val denominator = (max - min).takeIf { it > 0f } ?: 1f
    val first = (((values.firstOrNull() ?: min) - min) / denominator).coerceIn(0f, 1f)
    val last = (((values.lastOrNull() ?: min) - min) / denominator).coerceIn(0f, 1f)
    return if (range) minOf(first, last) to maxOf(first, last) else 0f to last
}

internal fun resolveSliderValues(props: UPSliderProps, min: Float, max: Float, step: Float): List<Float> =
    if (props.isRange) {
        listOf(
            quantizeSliderValue(props.rangeValue.getOrNull(0).rawFloat(min), min, max, step),
            quantizeSliderValue(props.rangeValue.getOrNull(1).rawFloat(max), min, max, step),
        ).sorted()
    } else {
        listOf(quantizeSliderValue((props.modelValue ?: props.value).rawFloat(min), min, max, step))
    }

internal fun resolveTabbarValue(props: UPTabbarProps): UPRawValue = props.modelValue ?: props.value ?: props.current

internal fun tabbarItemValue(name: UPRawValue, index: Int): UPRawValue = when (name) {
    null -> index
    is String -> name.ifBlank { index }
    is Number -> if (name.toDouble() == 0.0) index else name
    else -> name
}

internal fun tabbarChangeEvent(selected: UPRawValue, value: UPRawValue, index: Int): UPTabbarChangeEvent? =
    if (selected.rawEquals(value)) null else UPTabbarChangeEvent(value, index)

internal fun UPRawValue.rawMap(): Map<String, UPRawValue> = when (this) {
    is Map<*, *> -> entries.filter { it.key is String }.associate { it.key as String to it.value }
    else -> emptyMap()
}

internal fun UPRawValue.rawList(): List<UPRawValue> = (this as? List<*>)?.toList() ?: emptyList()

internal fun UPRawValue.rawEquals(other: UPRawValue): Boolean = this == other ||
    (this is Number && other is Number && toDouble() == other.toDouble())

internal fun UPRawValue.rawInt(fallback: Int): Int = when (this) {
    is Number -> toInt()
    is String -> toDoubleOrNull()?.toInt()
    else -> null
} ?: fallback

internal fun UPRawValue.rawLong(fallback: Long): Long = when (this) {
    is Number -> toLong()
    is String -> toDoubleOrNull()?.toLong()
    else -> null
} ?: fallback

internal fun UPRawValue.rawFloat(fallback: Float): Float = when (this) {
    is Number -> toFloat()
    is String -> toFloatOrNull()
    else -> null
}?.takeIf(Float::isFinite) ?: fallback

internal fun formatCalendarDate(calendar: Calendar): String = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).format(calendar.time)

private fun normalizedCalendarBoundary(value: UPRawValue): String? = when (value) {
    is Number -> value.toLong().takeIf { it != 0L }?.let { SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).format(Date(it)) }
    is String -> value.trim().takeIf(String::isNotEmpty)?.let { text ->
        text.toLongOrNull()?.takeIf { it != 0L }?.let { SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).format(Date(it)) }
            ?: parseDateString(text)?.let { SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).format(Date(it)) }
    }
    else -> null
}

private fun parseDateString(value: String): Long? = listOf("yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM-dd").firstNotNullOfOrNull { pattern ->
    runCatching { SimpleDateFormat(pattern, Locale.ROOT).apply { isLenient = false }.parse(value)?.time }.getOrNull()
}

private fun dateCalendar(value: UPRawValue): Calendar = Calendar.getInstance().apply {
    time = Date(value.rawLong(System.currentTimeMillis()))
}
