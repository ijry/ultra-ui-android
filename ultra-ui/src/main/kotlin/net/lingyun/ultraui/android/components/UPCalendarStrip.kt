package net.lingyun.ultraui.android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Calendar
import net.lingyun.ultraui.android.core.UPColor
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.core.upClickable
import net.lingyun.ultraui.android.core.upTestTag

/** `weekText` index for a `day()` value: Sunday(0)→6, else week-1 (Monday-first labels). */
internal fun upCalendarStripWeekLabel(weekText: List<String>, week: Int): String {
    val index = if (week == 0) 6 else week - 1
    return weekText.getOrNull(index) ?: ""
}

private fun stripRawDate(value: UPRawValue): String = when (value) {
    null -> ""
    is String -> value
    else -> ""
}

/**
 * Native Compose counterpart of uview-plus `u-calendar-strip` (strip mode).
 *
 * A month header (‹ title ›) over a horizontally scrollable strip of the month's days, each cell
 * showing the day number and its weekday label. The selected day fills with `color`; today (when
 * `showToday`) gets a tinted ring; out-of-range days (`minDate`/`maxDate`) and, under `readonly`,
 * all days are non-interactive. Tapping a day or switching months selects a date and emits
 * `change`/`confirm` (surfaced as [onChange]) plus `update:modelValue` ([onUpdateModelValue]); a
 * month change also fires [onMonthChange].
 *
 * Difference: upstream's optional `fullCalendar` pull-down panel wraps the full `u-calendar`; the
 * port ships the strip itself here and leaves the expandable full-month panel to a composed
 * [UPCalendar] the host can toggle, so the strip stays a focused, self-contained control.
 */
@Composable
public fun UPCalendarStrip(
    props: UPCalendarStripProps = UPCalendarStripProps(),
    modifier: Modifier = Modifier,
    onUpdateModelValue: ((String) -> Unit)? = null,
    onChange: ((date: String, month: String) -> Unit)? = null,
    onMonthChange: ((String) -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    val style = rememberUPResolvedStyle(props.customStyle, diagnostics, "UPCalendarStrip")
    val today = remember { upCalendarStripFormatYmd(Calendar.getInstance()) }
    val minDate = stripRawDate(props.minDate)
    val maxDate = stripRawDate(props.maxDate)
    val seed = stripRawDate(props.modelValue).ifEmpty { today }

    var selected by remember(props.modelValue) { mutableStateOf(seed) }
    var month by remember(props.modelValue) { mutableStateOf(upCalendarStripMonthOf(seed)) }
    val activeColor = UPColor.parse(props.color, UPTheme.Primary)

    fun select(date: String, scene: String) {
        if (upCalendarStripDisabled(date, minDate, maxDate)) return
        val prevMonth = month
        selected = date
        month = upCalendarStripMonthOf(date)
        onUpdateModelValue?.invoke(date)
        onChange?.invoke(date, month)
        if (prevMonth != month) onMonthChange?.invoke(month)
    }

    fun switchMonth(step: Int) {
        val target = upCalendarStripShiftMonth(month, step)
        val desiredDay = selected.substringAfterLast('-').toIntOrNull() ?: 1
        val next = upCalendarStripDayInMonth(target, desiredDay)
        if (next.isNotEmpty()) select(next, "switch")
    }

    val days = remember(month, selected, today, minDate, maxDate) {
        upCalendarStripMonthDays(month, selected, today, minDate, maxDate)
    }

    Column(modifier.fillMaxWidth().applyUPResolvedStyle(style).upTestTag("calendar-strip")) {
        // Header.
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BasicText("‹", modifier = Modifier.upTestTag("calendar-strip-prev").upClickable(onClick = { switchMonth(-1) }), style = TextStyle(color = UPTheme.Main, fontSize = 20.sp))
            BasicText(upCalendarStripMonthLabel(month, props.monthFormat), modifier = Modifier.upTestTag("calendar-strip-title"), style = TextStyle(color = UPTheme.Main, fontSize = 15.sp, fontWeight = FontWeight.Medium))
            BasicText("›", modifier = Modifier.upTestTag("calendar-strip-next").upClickable(onClick = { switchMonth(1) }), style = TextStyle(color = UPTheme.Main, fontSize = 20.sp))
        }
        // Day strip.
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            days.forEach { d ->
                val bg = when {
                    d.selected -> activeColor
                    d.today && props.showToday -> activeColor.copy(alpha = 0.12f)
                    else -> Color.Transparent
                }
                val fg = if (d.selected) Color.White else if (d.disabled) UPTheme.Light else UPTheme.Main
                Column(
                    modifier = Modifier
                        .size(44.dp, 56.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(bg)
                        .upTestTag("calendar-strip-day-${d.day}")
                        .upClickable(enabled = !props.readonly && !d.disabled, onClick = { select(d.date, "tap") }),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    BasicText(d.day.toString(), style = TextStyle(color = fg, fontSize = 16.sp, fontWeight = FontWeight.Medium))
                    BasicText(upCalendarStripWeekLabel(props.weekText, d.week), style = TextStyle(color = if (d.selected) Color.White else UPTheme.Tips, fontSize = 11.sp))
                }
            }
        }
    }
}
