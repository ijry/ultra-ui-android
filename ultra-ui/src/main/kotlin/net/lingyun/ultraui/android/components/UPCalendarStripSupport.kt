package net.lingyun.ultraui.android.components

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/** A single day cell in the strip. `week` is 0=Sun..6=Sat (matching dayjs `.day()`). */
internal data class UPCalendarStripDay(
    val day: Int,
    val date: String,
    val week: Int,
    val disabled: Boolean,
    val selected: Boolean,
    val today: Boolean,
)

private fun stripCalendar(): Calendar = Calendar.getInstance()

private fun parseYmd(date: String): Calendar? {
    if (date.isEmpty()) return null
    return try {
        val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply { isLenient = false }
        stripCalendar().apply { time = fmt.parse(date)!! }
    } catch (_: Exception) {
        null
    }
}

internal fun upCalendarStripFormatYmd(cal: Calendar): String =
    SimpleDateFormat("yyyy-MM-dd", Locale.US).format(cal.time)

/** `dayjs(a).isSame(b, 'day')`. */
internal fun upCalendarStripSameDay(a: String, b: String): Boolean {
    val ca = parseYmd(a) ?: return false
    val cb = parseYmd(b) ?: return false
    return ca.get(Calendar.YEAR) == cb.get(Calendar.YEAR) &&
        ca.get(Calendar.DAY_OF_YEAR) == cb.get(Calendar.DAY_OF_YEAR)
}

/** `YYYY-MM` for a `YYYY-MM-DD` date, or "" when unparseable. */
internal fun upCalendarStripMonthOf(date: String): String {
    val c = parseYmd(date) ?: return ""
    return "%04d-%02d".format(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1)
}

/** `monthLabel`: `YYYY年MM月` (zh) unless `monthFormat` overrides with a SimpleDateFormat pattern. */
internal fun upCalendarStripMonthLabel(month: String, monthFormat: String): String {
    val first = parseYmd("$month-01") ?: return ""
    return if (monthFormat.isNotEmpty()) {
        runCatching { SimpleDateFormat(monthFormat, Locale.US).format(first.time) }.getOrElse {
            SimpleDateFormat("yyyy年MM月", Locale.CHINA).format(first.time)
        }
    } else {
        SimpleDateFormat("yyyy年MM月", Locale.CHINA).format(first.time)
    }
}

private fun withinBound(date: String, minDate: String, maxDate: String): Boolean {
    val c = parseYmd(date) ?: return false
    if (minDate.isNotEmpty()) parseYmd(minDate)?.let { if (c.before(it)) return false }
    if (maxDate.isNotEmpty()) parseYmd(maxDate)?.let { if (c.after(it)) return false }
    return true
}

/** `isDateDisabled`: outside [minDate, maxDate]. */
internal fun upCalendarStripDisabled(date: String, minDate: String, maxDate: String): Boolean =
    !withinBound(date, minDate, maxDate)

/**
 * `monthDays`: one entry per day of `month` (YYYY-MM), with disabled/selected/today flags. `today`
 * is supplied so the pure function stays deterministic for tests.
 */
internal fun upCalendarStripMonthDays(
    month: String,
    selectedDate: String,
    todayDate: String,
    minDate: String,
    maxDate: String,
): List<UPCalendarStripDay> {
    val first = parseYmd("$month-01") ?: return emptyList()
    val days = first.getActualMaximum(Calendar.DAY_OF_MONTH)
    return (1..days).map { d ->
        val cal = (first.clone() as Calendar).apply { set(Calendar.DAY_OF_MONTH, d) }
        val date = upCalendarStripFormatYmd(cal)
        UPCalendarStripDay(
            day = d,
            date = date,
            week = cal.get(Calendar.DAY_OF_WEEK) - 1,
            disabled = upCalendarStripDisabled(date, minDate, maxDate),
            selected = upCalendarStripSameDay(date, selectedDate),
            today = upCalendarStripSameDay(date, todayDate),
        )
    }
}

/** `switchMonth`: add `step` months to `YYYY-MM`, returning the new `YYYY-MM`. */
internal fun upCalendarStripShiftMonth(month: String, step: Int): String {
    val first = parseYmd("$month-01") ?: return month
    val shifted = (first.clone() as Calendar).apply { add(Calendar.MONTH, step) }
    return "%04d-%02d".format(shifted.get(Calendar.YEAR), shifted.get(Calendar.MONTH) + 1)
}

/**
 * The day to select after a month switch: keep the same day-of-month when it exists, clamped to the
 * month length, matching `Math.min(selectedDay, target.daysInMonth())`.
 */
internal fun upCalendarStripDayInMonth(month: String, desiredDay: Int): String {
    val first = parseYmd("$month-01") ?: return ""
    val maxDay = first.getActualMaximum(Calendar.DAY_OF_MONTH)
    val d = desiredDay.coerceIn(1, maxDay)
    val cal = (first.clone() as Calendar).apply { set(Calendar.DAY_OF_MONTH, d) }
    return upCalendarStripFormatYmd(cal)
}
