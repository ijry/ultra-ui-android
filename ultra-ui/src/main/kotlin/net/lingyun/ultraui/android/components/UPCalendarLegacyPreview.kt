package net.lingyun.ultraui.android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.lingyun.ultraui.android.core.UPColor
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.core.upClickable
import net.lingyun.ultraui.android.core.upTestTag
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
internal fun UPCalendarLegacyPreview(
    props: UPCalendarProps,
    modifier: Modifier = Modifier,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    if (!props.show && !props.pageInline) return

    val initial = remember(props.defaultDate) { legacyCalendarInitialDates(props.defaultDate) }
    var selected by remember(props.defaultDate, props.mode) { mutableStateOf(initial) }
    var month by remember(props.defaultDate) { mutableStateOf(legacyCalendarForDate(initial.firstOrNull())) }
    LaunchedEffect(props.defaultDate, props.mode) { selected = initial }
    val primary = UPColor.parse(props.color, UPTheme.Primary)
    val background = UPColor.parse(props.bgColor, Color.White)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(background, RoundedCornerShape(props.round.rawFloat(0f).dp))
            .applyUPResolvedStyle(rememberUPResolvedStyle(props.customStyle, diagnostics, "UPCalendar"))
            .upTestTag("calendar")
            .padding(12.dp),
    ) {
        if (props.showTitle) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                BasicText(
                    props.title,
                    style = TextStyle(color = UPTheme.Main, fontSize = 18.sp, fontWeight = FontWeight.Bold),
                )
                BasicText(
                    "×",
                    modifier = Modifier.upClickable { }.padding(8.dp).upTestTag("calendar-close"),
                    style = TextStyle(fontSize = 20.sp),
                )
            }
        }
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BasicText(
                "‹",
                modifier = Modifier
                    .upClickable { month = legacyCalendarShiftMonth(month, -1) }
                    .padding(10.dp)
                    .upTestTag("calendar-prev"),
            )
            if (props.showSubtitle) {
                BasicText(
                    "${month.get(Calendar.YEAR)}年${month.get(Calendar.MONTH) + 1}月",
                    style = TextStyle(color = UPTheme.Main, fontWeight = FontWeight.Bold),
                )
            }
            BasicText(
                "›",
                modifier = Modifier
                    .upClickable { month = legacyCalendarShiftMonth(month, 1) }
                    .padding(10.dp)
                    .upTestTag("calendar-next"),
            )
        }
        Row(Modifier.fillMaxWidth()) {
            props.weekText.take(7).forEach { week ->
                Box(
                    Modifier.weight(1f).padding(6.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    BasicText(week, style = TextStyle(color = UPTheme.Tips, fontSize = 12.sp))
                }
            }
        }

        val days = legacyCalendarPreviewCells(month.get(Calendar.YEAR), month.get(Calendar.MONTH) + 1)
        days.chunked(7).forEachIndexed { rowIndex, week ->
            Row(Modifier.fillMaxWidth().upTestTag("calendar-legacy-row-$rowIndex")) {
                week.forEach { day ->
                    if (day == null) {
                        Box(Modifier.weight(1f).padding(vertical = 8.dp))
                    } else {
                        val date = "%04d-%02d-%02d".format(
                            Locale.ROOT,
                            month.get(Calendar.YEAR),
                            month.get(Calendar.MONTH) + 1,
                            day,
                        )
                        val active = legacyCalendarDateSelected(props.mode, selected, date)
                        val forbidden = !calendarDateAllowed(props, date) || props.forbidDays.any { it.toString() == date }
                        Box(
                            Modifier.weight(1f).padding(2.dp)
                                .background(if (active) primary else Color.Transparent, RoundedCornerShape(50))
                                .upClickable(enabled = !props.readonly && !forbidden) {
                                    selected = updateCalendarSelection(
                                        props.mode,
                                        selected,
                                        date,
                                        props.maxCount.rawInt(Int.MAX_VALUE),
                                        props.allowSameDay,
                                    )
                                }
                                .padding(vertical = 9.dp)
                                .upTestTag("calendar-day-$date"),
                            contentAlignment = Alignment.Center,
                        ) {
                            BasicText(
                                day.toString(),
                                style = TextStyle(
                                    color = if (active) Color.White else if (forbidden) UPTheme.Disabled else UPTheme.Main,
                                    fontSize = 14.sp,
                                ),
                            )
                        }
                    }
                }
            }
        }
        if (props.showConfirm) {
            val enabled = selected.isNotEmpty() && (props.mode != "range" || selected.size == 2)
            Box(
                Modifier.fillMaxWidth().padding(top = 10.dp)
                    .background(if (enabled) primary else UPTheme.Disabled, RoundedCornerShape(20.dp))
                    .upClickable(enabled = enabled) { }
                    .padding(11.dp)
                    .upTestTag("calendar-confirm"),
                contentAlignment = Alignment.Center,
            ) {
                BasicText(
                    if (enabled) props.confirmText else props.confirmDisabledText,
                    style = TextStyle(color = Color.White),
                )
            }
        }
    }
}

internal fun legacyCalendarPreviewCells(year: Int, month: Int): List<Int?> {
    val first = Calendar.getInstance().apply {
        clear()
        set(year, month - 1, 1)
    }
    val offset = first.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY
    val count = first.getActualMaximum(Calendar.DAY_OF_MONTH)
    return List(42) { index ->
        (index - offset + 1).takeIf { it in 1..count }
    }
}

private fun legacyCalendarInitialDates(value: UPRawValue): List<String> = when (value) {
    is List<*> -> value.mapNotNull { it?.toString()?.takeIf(String::isNotBlank) }
    is Number -> listOf(SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).format(Date(value.toLong())))
    is String -> value.takeIf(String::isNotBlank)?.let(::listOf) ?: emptyList()
    else -> emptyList()
}

private fun legacyCalendarForDate(value: String?): Calendar = Calendar.getInstance().apply {
    value?.let {
        runCatching {
            time = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).apply { isLenient = false }.parse(it)!!
        }
    }
    set(Calendar.DAY_OF_MONTH, 1)
}

private fun legacyCalendarShiftMonth(month: Calendar, amount: Int): Calendar =
    (month.clone() as Calendar).apply {
        add(Calendar.MONTH, amount)
        set(Calendar.DAY_OF_MONTH, 1)
    }

private fun legacyCalendarDateSelected(mode: String, selected: List<String>, date: String): Boolean = when (mode) {
    "range" -> selected.size == 2 && date >= selected[0] && date <= selected[1] || date in selected
    else -> date in selected
}
