package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPRawValue
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

internal data class UPCalendarMonth(
    val year: Int,
    val month: Int,
)

internal data class UPCalendarDayMetadata(
    val date: String,
    val topInfo: String = "",
    val bottomInfo: String = "",
    val dot: Boolean = false,
    val disabled: Boolean = false,
)

internal data class UPCalendarSelectionOutcome(
    val selected: List<String>,
    val prompt: String? = null,
)

internal data class UPCalendarTime(
    val hour: Int = 0,
    val minute: Int = 0,
    val second: Int = 0,
)

internal fun calendarMonthSequence(
    anchorDate: String?,
    monthNum: Int,
    monthSwitch: Boolean,
): List<UPCalendarMonth> {
    val anchor = Calendar.getInstance().apply {
        anchorDate?.let { value ->
            runCatching {
                time = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).apply { isLenient = false }.parse(value)!!
            }
        }
        set(Calendar.DAY_OF_MONTH, 1)
    }
    val count = if (monthSwitch) 1 else monthNum.coerceAtLeast(1)
    return List(count) { index ->
        val month = (anchor.clone() as Calendar).apply { add(Calendar.MONTH, index) }
        UPCalendarMonth(month.get(Calendar.YEAR), month.get(Calendar.MONTH) + 1)
    }
}

internal fun calendarMonthTitle(month: UPCalendarMonth, monthFormat: String): String {
    val template = monthFormat.ifBlank { "YYYY年MM月" }
    return template
        .replace("YYYY", month.year.toString().padStart(4, '0'))
        .replace("MM", month.month.toString().padStart(2, '0'))
        .replace("M", month.month.toString())
}

internal fun calendarCustomMetadata(customList: List<UPRawValue>): Map<String, UPCalendarDayMetadata> =
    customList.mapNotNull { value ->
        val item = value.rawMap()
        val date = item["date"]?.toString()?.takeIf(String::isNotBlank) ?: return@mapNotNull null
        date to UPCalendarDayMetadata(
            date = date,
            topInfo = item["topInfo"]?.toString().orEmpty(),
            bottomInfo = item["bottomInfo"]?.toString().orEmpty(),
            dot = item["dot"].calendarBoolean(),
            disabled = item["disabled"].calendarBoolean(),
        )
    }.toMap()

internal fun resolveCalendarSelection(
    props: UPCalendarProps,
    selected: List<String>,
    date: String,
): UPCalendarSelectionOutcome {
    if (props.mode != "range") {
        val maxCount = props.maxCount.rawLong(Int.MAX_VALUE.toLong()).coerceIn(1L, Int.MAX_VALUE.toLong()).toInt()
        return UPCalendarSelectionOutcome(
            updateCalendarSelection(props.mode, selected, date, maxCount, props.allowSameDay),
        )
    }
    if (selected.size != 1) return UPCalendarSelectionOutcome(listOf(date))
    val start = selected.first()
    if (date < start) return UPCalendarSelectionOutcome(listOf(date))
    if (date == start && !props.allowSameDay) return UPCalendarSelectionOutcome(selected)
    if (date == start) return UPCalendarSelectionOutcome(listOf(start, date))

    val maxRange = props.maxRange.rawLong(Long.MAX_VALUE).coerceAtLeast(0L)
    val distance = calendarDateDistance(start, date)
    if (props.showRangePrompt && distance != null && distance > maxRange) {
        val prompt = props.rangePrompt.ifBlank { "选择天数不能超过${maxRange}天" }
        return UPCalendarSelectionOutcome(selected, prompt)
    }
    return UPCalendarSelectionOutcome(listOf(start, date))
}

internal fun calendarResultDates(
    mode: String,
    selected: List<String>,
    rangeResultMode: String,
): List<String> {
    if (mode != "range" || selected.size < 2) return selected
    val boundaries = listOf(selected.first(), selected.last())
    return if (rangeResultMode == "boundary") boundaries else calendarDatesBetween(boundaries[0], boundaries[1])
}

internal fun calendarForbiddenPrompt(props: UPCalendarProps, date: String): String? {
    if (props.mode == "range") return null
    return props.forbidDays
        .firstOrNull { it?.toString() == date }
        ?.let { props.forbidDaysToast }
}

internal fun calendarInitialTimes(props: UPCalendarProps): List<UPCalendarTime> {
    if (!props.enableTime) return emptyList()
    if (props.mode == "single") return listOf(calendarTimeValue(props.defaultTime))
    if (props.mode == "range" && props.rangeResultMode == "boundary") {
        val value = calendarTimeValue(props.defaultTime)
        return listOf(value, value)
    }
    return emptyList()
}

internal fun calendarTimeParts(time: UPCalendarTime, precision: String): List<String> = when (precision) {
    "hour" -> listOf(time.hour.calendarTimeText())
    "second" -> listOf(time.hour.calendarTimeText(), time.minute.calendarTimeText(), time.second.calendarTimeText())
    else -> listOf(time.hour.calendarTimeText(), time.minute.calendarTimeText())
}

internal fun updateCalendarTime(time: UPCalendarTime, index: Int, value: Int): UPCalendarTime = when (index) {
    0 -> time.copy(hour = value.coerceIn(0, 23))
    1 -> time.copy(minute = value.coerceIn(0, 59))
    2 -> time.copy(second = value.coerceIn(0, 59))
    else -> time
}

internal fun calendarResultValues(
    props: UPCalendarProps,
    selected: List<String>,
    times: List<UPCalendarTime>,
): List<String> {
    val dates = calendarResultDates(props.mode, selected, props.rangeResultMode)
    if (!calendarShowsTimePanel(props) || dates.isEmpty()) return dates
    return when {
        props.mode == "single" -> listOf(
            calendarAppendTime(dates.first(), times.firstOrNull() ?: UPCalendarTime(), props.timePrecision),
        )
        props.mode == "range" && props.rangeResultMode == "boundary" && dates.size >= 2 -> listOf(
            calendarAppendTime(dates[0], times.getOrNull(0) ?: UPCalendarTime(), props.timePrecision),
            calendarAppendTime(dates[1], times.getOrNull(1) ?: times.firstOrNull() ?: UPCalendarTime(), props.timePrecision),
        )
        else -> dates
    }
}

internal fun calendarShowsTimePanel(props: UPCalendarProps): Boolean =
    props.enableTime && (props.mode == "single" || (props.mode == "range" && props.rangeResultMode == "boundary"))

internal fun calendarSameDayRangeTimeAllowed(
    props: UPCalendarProps,
    selected: List<String>,
    times: List<UPCalendarTime>,
): Boolean {
    if (!calendarShowsTimePanel(props) || props.mode != "range" || props.rangeResultMode != "boundary") return true
    if (selected.size < 2 || selected.first() != selected.last()) return true
    val start = times.getOrNull(0) ?: UPCalendarTime()
    val end = times.getOrNull(1) ?: UPCalendarTime()
    val startSeconds = calendarTimeSeconds(start, props.timePrecision)
    val endSeconds = calendarTimeSeconds(end, props.timePrecision)
    return endSeconds >= startSeconds
}

private fun calendarTimeValue(value: UPRawValue): UPCalendarTime {
    val parts = value?.toString()?.trim()?.takeIf(String::isNotEmpty)?.split(':').orEmpty()
    return UPCalendarTime(
        hour = parts.getOrNull(0).calendarTimeNumber(23),
        minute = parts.getOrNull(1).calendarTimeNumber(59),
        second = parts.getOrNull(2).calendarTimeNumber(59),
    )
}

private fun String?.calendarTimeNumber(max: Int): Int =
    this?.toIntOrNull()?.coerceIn(0, max) ?: 0

private fun Int.calendarTimeText(): String = toString().padStart(2, '0')

private fun calendarAppendTime(date: String, time: UPCalendarTime, precision: String): String =
    "$date ${calendarTimeParts(time, precision).joinToString(":")}"

private fun calendarTimeSeconds(time: UPCalendarTime, precision: String): Int = when (precision) {
    "hour" -> time.hour * 3600
    "second" -> time.hour * 3600 + time.minute * 60 + time.second
    else -> time.hour * 3600 + time.minute * 60
}

private fun UPRawValue.calendarBoolean(): Boolean = when (this) {
    is Boolean -> this
    is Number -> toInt() != 0
    is String -> equals("true", ignoreCase = true) || this == "1"
    else -> false
}

private fun calendarDateDistance(start: String, end: String): Long? {
    val startDate = parseCalendarIsoDate(start) ?: return null
    val endDate = parseCalendarIsoDate(end) ?: return null
    return (endDate.timeInMillis - startDate.timeInMillis) / MILLIS_PER_DAY
}

private fun calendarDatesBetween(start: String, end: String): List<String> {
    val current = parseCalendarIsoDate(start) ?: return listOf(start, end).distinct()
    val last = parseCalendarIsoDate(end) ?: return listOf(start, end).distinct()
    if (current.after(last)) return listOf(start, end).distinct()
    return buildList {
        while (!current.after(last)) {
            add(formatCalendarIsoDate(current))
            current.add(Calendar.DAY_OF_MONTH, 1)
        }
    }
}

private fun parseCalendarIsoDate(value: String): Calendar? = runCatching {
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).apply {
        isLenient = false
        timeZone = CALENDAR_TIME_ZONE
    }
    Calendar.getInstance(CALENDAR_TIME_ZONE, Locale.ROOT).apply {
        time = formatter.parse(value)!!
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
}.getOrNull()

private fun formatCalendarIsoDate(value: Calendar): String =
    SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).apply { timeZone = CALENDAR_TIME_ZONE }.format(value.time)

private const val MILLIS_PER_DAY: Long = 86_400_000L
private val CALENDAR_TIME_ZONE: TimeZone = TimeZone.getTimeZone("UTC")
