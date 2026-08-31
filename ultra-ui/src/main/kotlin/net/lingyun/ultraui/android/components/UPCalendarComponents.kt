package net.lingyun.ultraui.android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
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
public fun UPCalendar(
    props: UPCalendarProps = UPCalendarProps(),
    modifier: Modifier = Modifier,
    onChange: ((UPCalendarEvent) -> Unit)? = null,
    onConfirm: ((UPCalendarEvent) -> Unit)? = null,
    onClose: (() -> Unit)? = null,
    onUpdateShow: ((Boolean) -> Unit)? = null,
    onMonthChange: ((Int, Int) -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    if (!props.show && !props.pageInline) return

    val initial = remember(props.defaultDate) { calendarInitialDates(props.defaultDate) }
    val monthCount = props.monthNum.rawInt(3).coerceAtLeast(1)
    val months = remember(initial.firstOrNull(), monthCount) {
        calendarMonthSequence(initial.firstOrNull(), monthCount, monthSwitch = false)
    }
    val initialTimes = remember(
        props.enableTime,
        props.mode,
        props.rangeResultMode,
        props.defaultTime,
        props.timePrecision,
    ) { calendarInitialTimes(props) }
    val customMetadata = remember(props.customList) { calendarCustomMetadata(props.customList) }
    val todayDate = remember { formatCalendarDate(Calendar.getInstance()) }
    val todayMonthIndex = months.indexOfFirst { it.key == todayDate.substringBeforeLast('-') }
    val primary = UPColor.parse(props.color, UPTheme.Primary)
    val todayColor = UPColor.parse(props.todayColor, primary)
    val background = UPColor.parse(props.bgColor, Color.White)
    val cellHeight = calendarCellHeight(props.rowHeight)
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current
    val monthScrollOffset = with(density) {
        (cellHeight * 6f + 58.dp).toPx().toInt().coerceAtLeast(1)
    }

    var selected by remember(props.defaultDate, props.mode) { mutableStateOf(initial) }
    var monthIndex by remember(props.defaultDate, props.mode, monthCount, props.monthSwitch) { mutableStateOf(0) }
    var prompt by remember(props.defaultDate, props.mode) { mutableStateOf<String?>(null) }
    var times by remember(
        props.enableTime,
        props.mode,
        props.rangeResultMode,
        props.defaultTime,
        props.timePrecision,
    ) { mutableStateOf(initialTimes) }

    LaunchedEffect(props.defaultDate, props.mode, monthCount, props.monthSwitch) {
        selected = initial
        monthIndex = 0
        prompt = null
    }
    LaunchedEffect(props.enableTime, props.mode, props.rangeResultMode, props.defaultTime, props.timePrecision) {
        times = initialTimes
    }

    fun switchToMonth(index: Int) {
        val nextIndex = index.coerceIn(0, months.lastIndex)
        if (nextIndex == monthIndex) return
        monthIndex = nextIndex
        months.getOrNull(nextIndex)?.let { onMonthChange?.invoke(it.year, it.month) }
    }

    fun scrollToMonth(index: Int) {
        val target = index.coerceIn(0, months.lastIndex)
        if (props.monthSwitch) {
            switchToMonth(target)
        } else {
            coroutineScope.launch {
                scrollState.animateScrollTo((target * monthScrollOffset).coerceIn(0, scrollState.maxValue))
            }
        }
    }

    val visibleMonths = if (props.monthSwitch) {
        listOf(months[monthIndex.coerceIn(0, months.lastIndex)])
    } else {
        months
    }

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
                    modifier = Modifier.upTestTag("calendar-title"),
                    style = TextStyle(color = UPTheme.Main, fontSize = 18.sp, fontWeight = FontWeight.Bold),
                )
                BasicText(
                    "×",
                    modifier = Modifier
                        .upClickable { onUpdateShow?.invoke(false); onClose?.invoke() }
                        .padding(8.dp)
                        .upTestTag("calendar-close"),
                    style = TextStyle(fontSize = 20.sp),
                )
            }
        }

        if (props.monthSwitch) {
            val currentMonth = months[monthIndex.coerceIn(0, months.lastIndex)]
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                BasicText(
                    "‹",
                    modifier = Modifier
                        .upClickable { switchToMonth(monthIndex - 1) }
                        .padding(10.dp)
                        .upTestTag("calendar-prev"),
                    style = TextStyle(color = UPTheme.Main, fontSize = 20.sp),
                )
                if (props.showSubtitle) {
                    BasicText(
                        calendarMonthTitle(currentMonth, props.monthFormat),
                        modifier = Modifier.upTestTag("calendar-switch-title"),
                        style = TextStyle(color = UPTheme.Main, fontWeight = FontWeight.Bold),
                    )
                }
                BasicText(
                    "›",
                    modifier = Modifier
                        .upClickable { switchToMonth(monthIndex + 1) }
                        .padding(10.dp)
                        .upTestTag("calendar-next"),
                    style = TextStyle(color = UPTheme.Main, fontSize = 20.sp),
                )
            }
        }

        if (props.showToday) {
            BasicText(
                "今天",
                modifier = Modifier
                    .upClickable { if (todayMonthIndex >= 0) scrollToMonth(todayMonthIndex) }
                    .padding(vertical = 6.dp)
                    .upTestTag("calendar-today"),
                style = TextStyle(color = todayColor, fontSize = 13.sp),
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (!props.monthSwitch && months.size > 1) {
                        Modifier.height(420.dp).verticalScroll(scrollState)
                    } else {
                        Modifier
                    },
                )
                .upTestTag("calendar-month-scroll"),
        ) {
            visibleMonths.forEachIndexed { visibleIndex, month ->
                CalendarMonth(
                    props = props,
                    month = month,
                    visibleIndex = visibleIndex,
                    selected = selected,
                    customMetadata = customMetadata,
                    todayDate = todayDate,
                    primary = primary,
                    todayColor = todayColor,
                    cellHeight = cellHeight,
                    onDateClick = { date ->
                        val metadata = customMetadata[date] ?: UPCalendarDayMetadata(date)
                        val forbiddenPrompt = calendarForbiddenPrompt(props, date)
                        val allowed = calendarDateAllowed(props, date) && !metadata.disabled
                        if (forbiddenPrompt != null) {
                            prompt = forbiddenPrompt.takeIf(String::isNotBlank)
                        } else if (allowed) {
                            val outcome = resolveCalendarSelection(props, selected, date)
                            prompt = outcome.prompt
                            if (outcome.prompt == null) {
                                selected = outcome.selected
                                onChange?.invoke(calendarEvent(props, selected, date, times))
                            }
                        }
                    },
                )
                if (visibleIndex != visibleMonths.lastIndex) Spacer(Modifier.height(8.dp))
            }
        }

        if (calendarShowsTimePanel(props)) {
            CalendarTimePanel(
                props = props,
                selected = selected,
                times = times,
                onUpdate = { timeIndex, partIndex, value ->
                    val updatedTimes = times.toMutableList()
                    while (updatedTimes.size <= timeIndex) updatedTimes += UPCalendarTime()
                    updatedTimes[timeIndex] = updateCalendarTime(updatedTimes[timeIndex], partIndex, value)
                    times = updatedTimes
                },
            )
        }

        prompt?.let { message ->
            BasicText(
                message,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .upTestTag("calendar-prompt"),
                style = TextStyle(color = UPTheme.Error, fontSize = 13.sp),
            )
        }

        if (props.showConfirm) {
            val enabled = selected.isNotEmpty() && (props.mode != "range" || selected.size == 2)
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
                    .background(if (enabled) primary else UPTheme.Disabled, RoundedCornerShape(20.dp))
                    .upClickable(enabled = enabled) {
                        if (!calendarSameDayRangeTimeAllowed(props, selected, times)) {
                            prompt = "结束时间不能早于开始时间"
                        } else {
                            onConfirm?.invoke(calendarEvent(props, selected, selected.last(), times))
                            onUpdateShow?.invoke(false)
                            onClose?.invoke()
                        }
                    }
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

@Composable
private fun CalendarMonth(
    props: UPCalendarProps,
    month: UPCalendarMonth,
    visibleIndex: Int,
    selected: List<String>,
    customMetadata: Map<String, UPCalendarDayMetadata>,
    todayDate: String,
    primary: Color,
    todayColor: Color,
    cellHeight: Dp,
    onDateClick: (String) -> Unit,
) {
    val monthKey = month.key
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .upTestTag("calendar-month-$monthKey"),
    ) {
        if ((props.showSubtitle && !props.monthSwitch) || props.showMark) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (props.showSubtitle && !props.monthSwitch) {
                    BasicText(
                        calendarMonthTitle(month, props.monthFormat),
                        modifier = Modifier.upTestTag("calendar-month-title-$monthKey"),
                        style = TextStyle(color = UPTheme.Main, fontWeight = FontWeight.Bold),
                    )
                } else {
                    Spacer(Modifier)
                }
                if (props.showMark) {
                    BasicText(
                        month.month.toString(),
                        modifier = Modifier.upTestTag("calendar-month-$monthKey-mark"),
                        style = TextStyle(color = UPTheme.Tips, fontSize = 12.sp),
                    )
                }
            }
        }

        CalendarWeekHeader(props.weekText, monthKey)
        val grid = calendarMonthGrid(month)
        grid.chunked(7).forEachIndexed { rowIndex, week ->
            Row(Modifier.fillMaxWidth().upTestTag("calendar-month-$monthKey-row-$rowIndex")) {
                week.forEach { cell ->
                    CalendarDayCell(
                        props = props,
                        cell = cell,
                        selected = selected,
                        customMetadata = customMetadata,
                        todayDate = todayDate,
                        primary = primary,
                        todayColor = todayColor,
                        cellHeight = cellHeight,
                        onDateClick = onDateClick,
                    )
                }
            }
        }
    }
}

@Composable
private fun CalendarWeekHeader(weekText: List<String>, monthKey: String) {
    val source = weekText.take(7)
    val fallback = listOf("日", "一", "二", "三", "四", "五", "六")
    val mondayFirst = (0 until 7).map { index ->
        source.getOrElse((index + 1) % 7) { fallback[(index + 1) % 7] }
    }
    Row(Modifier.fillMaxWidth().upTestTag("calendar-month-$monthKey-week-header")) {
        mondayFirst.forEachIndexed { index, week ->
            Box(
                Modifier
                    .weight(1f)
                    .padding(vertical = 4.dp)
                    .upTestTag("calendar-week-$monthKey-$index"),
                contentAlignment = Alignment.Center,
            ) {
                BasicText(week, style = TextStyle(color = UPTheme.Tips, fontSize = 12.sp))
            }
        }
    }
}

@Composable
private fun RowScope.CalendarDayCell(
    props: UPCalendarProps,
    cell: UPCalendarGridCell,
    selected: List<String>,
    customMetadata: Map<String, UPCalendarDayMetadata>,
    todayDate: String,
    primary: Color,
    todayColor: Color,
    cellHeight: Dp,
    onDateClick: (String) -> Unit,
) {
    val date = cell.date
    val metadata = customMetadata[date] ?: UPCalendarDayMetadata(date)
    val active = calendarDateSelected(props.mode, selected, date)
    val rangeMiddle = props.mode == "range" && selected.size >= 2 && date > selected.first() && date < selected.last()
    val forbiddenPrompt = calendarForbiddenPrompt(props, date)
    val allowed = calendarDateAllowed(props, date) && !metadata.disabled
    val forbidden = !allowed
    val clickable = !props.readonly && !metadata.disabled && (allowed || forbiddenPrompt != null)
    val showToday = props.showToday && !cell.adjacent && date == todayDate
    val dateTag = "calendar-day-$date${if (cell.adjacent) "-adjacent" else ""}"
    val bottomInfo = calendarBottomInfo(props, selected, date, metadata.bottomInfo)
    val lunar = if (props.showLunar) calendarLunarLabel(cell.year, cell.month, cell.day) else ""
    val fill = when {
        rangeMiddle -> primary.copy(alpha = 0.14f)
        active -> primary
        else -> Color.Transparent
    }
    val numberColor = when {
        active && !rangeMiddle -> Color.White
        forbidden -> UPTheme.Disabled
        showToday -> todayColor
        else -> UPTheme.Main
    }

    Box(
        modifier = Modifier
            .weight(1f)
            .height(cellHeight)
            .upClickable(enabled = clickable) { onDateClick(date) }
            .upTestTag(dateTag),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(2.dp)
                .background(fill, RoundedCornerShape(50))
                .then(
                    if (rangeMiddle && !cell.adjacent) {
                        Modifier.upTestTag("$dateTag-range-middle")
                    } else {
                        Modifier
                    },
                ),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                if (metadata.topInfo.isNotBlank()) {
                    BasicText(
                        metadata.topInfo,
                        modifier = Modifier.upTestTag("$dateTag-top-info"),
                        style = TextStyle(color = if (forbidden) UPTheme.Disabled else UPTheme.Content, fontSize = 9.sp),
                    )
                }
                if (showToday) {
                    Box(Modifier.upTestTag("$dateTag-today")) {
                        Box(Modifier.upTestTag("$dateTag-today-color-${calendarColorTag(props.todayColor)}")) {
                            BasicText(
                                cell.day.toString(),
                                style = TextStyle(color = numberColor, fontSize = 14.sp),
                            )
                        }
                    }
                } else {
                    BasicText(
                        cell.day.toString(),
                        style = TextStyle(color = numberColor, fontSize = 14.sp),
                    )
                }
                if (bottomInfo.isNotBlank()) {
                    BasicText(
                        bottomInfo,
                        modifier = Modifier.upTestTag("$dateTag-bottom-info"),
                        style = TextStyle(color = if (active && !rangeMiddle) Color.White else UPTheme.Content, fontSize = 9.sp),
                    )
                }
                if (lunar.isNotBlank() && !cell.adjacent) {
                    BasicText(
                        lunar,
                        modifier = Modifier.upTestTag("$dateTag-lunar"),
                        style = TextStyle(color = if (active && !rangeMiddle) Color.White else UPTheme.Tips, fontSize = 8.sp),
                    )
                }
                if (metadata.dot) {
                    BasicText(
                        "•",
                        modifier = Modifier.upTestTag("$dateTag-dot"),
                        style = TextStyle(color = primary, fontSize = 10.sp, lineHeight = 8.sp),
                    )
                }
                if (metadata.disabled && !cell.adjacent) {
                    BasicText(
                        "",
                        modifier = Modifier.upTestTag("$dateTag-disabled"),
                    )
                }
            }
        }
    }
}

private fun calendarBottomInfo(
    props: UPCalendarProps,
    selected: List<String>,
    date: String,
    customBottomInfo: String,
): String = when {
    props.mode != "range" -> customBottomInfo
    selected.isEmpty() -> customBottomInfo
    date == selected.first() -> props.startText
    selected.size >= 2 && date == selected.last() -> props.endText
    else -> customBottomInfo
}

private data class UPCalendarGridCell(
    val date: String,
    val year: Int,
    val month: Int,
    val day: Int,
    val adjacent: Boolean,
)

private fun calendarMonthGrid(month: UPCalendarMonth): List<UPCalendarGridCell> {
    val first = Calendar.getInstance().apply {
        clear()
        set(month.year, month.month - 1, 1, 12, 0, 0)
    }
    val mondayOffset = (first.get(Calendar.DAY_OF_WEEK) + 5) % 7
    return List(42) { index ->
        val date = (first.clone() as Calendar).apply { add(Calendar.DAY_OF_MONTH, index - mondayOffset) }
        UPCalendarGridCell(
            date = formatCalendarDate(date),
            year = date.get(Calendar.YEAR),
            month = date.get(Calendar.MONTH) + 1,
            day = date.get(Calendar.DAY_OF_MONTH),
            adjacent = date.get(Calendar.YEAR) != month.year || date.get(Calendar.MONTH) + 1 != month.month,
        )
    }
}

private fun calendarCellHeight(rowHeight: UPRawValue): Dp =
    (rowHeight.rawFloat(56f).coerceAtLeast(0f) * (52f / 84f)).dp

private fun calendarColorTag(value: String): String =
    value.trim().removePrefix("#").lowercase(Locale.ROOT)

private fun calendarInitialDates(value: UPRawValue): List<String> = when (value) {
    is List<*> -> value.mapNotNull { it?.toString()?.takeIf(String::isNotBlank) }
    is Number -> listOf(SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).format(Date(value.toLong())))
    is String -> value.takeIf(String::isNotBlank)?.let(::listOf) ?: emptyList()
    else -> emptyList()
}

private fun calendarDateSelected(mode: String, selected: List<String>, date: String): Boolean = when (mode) {
    "range" -> selected.any { it == date } || (
        selected.size >= 2 && date > selected.first() && date < selected.last()
        )
    else -> date in selected
}

@Composable
private fun CalendarTimePanel(
    props: UPCalendarProps,
    selected: List<String>,
    times: List<UPCalendarTime>,
    onUpdate: (timeIndex: Int, partIndex: Int, value: Int) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
            .upTestTag("calendar-time-panel"),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        when (props.mode) {
            "single" -> CalendarTimeEditor(
                prefix = "calendar-time-single",
                dateLabel = selected.firstOrNull() ?: "--",
                time = times.firstOrNull() ?: UPCalendarTime(),
                precision = props.timePrecision,
                readonly = props.readonly,
                onUpdate = { partIndex, value -> onUpdate(0, partIndex, value) },
            )

            "range" -> {
                CalendarTimeEditor(
                    prefix = "calendar-time-range-start",
                    label = props.startText,
                    dateLabel = selected.firstOrNull() ?: "--",
                    time = times.firstOrNull() ?: UPCalendarTime(),
                    precision = props.timePrecision,
                    readonly = props.readonly,
                    onUpdate = { partIndex, value -> onUpdate(0, partIndex, value) },
                )
                CalendarTimeEditor(
                    prefix = "calendar-time-range-end",
                    label = props.endText,
                    dateLabel = selected.getOrNull(1) ?: "--",
                    time = times.getOrNull(1) ?: times.firstOrNull() ?: UPCalendarTime(),
                    precision = props.timePrecision,
                    readonly = props.readonly,
                    onUpdate = { partIndex, value -> onUpdate(1, partIndex, value) },
                )
            }
        }
    }
}

@Composable
private fun CalendarTimeEditor(
    prefix: String,
    dateLabel: String,
    time: UPCalendarTime,
    precision: String,
    readonly: Boolean,
    onUpdate: (partIndex: Int, value: Int) -> Unit,
    label: String? = null,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 2.dp)
            .upTestTag(prefix),
    ) {
        label?.takeIf(String::isNotBlank)?.let { value ->
            BasicText(
                value,
                modifier = Modifier.upTestTag("$prefix-label"),
                style = TextStyle(color = UPTheme.Content, fontSize = 13.sp),
            )
        }
        BasicText(
            dateLabel,
            modifier = Modifier.upTestTag("$prefix-date"),
            style = TextStyle(color = UPTheme.Tips, fontSize = 12.sp),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 3.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            calendarTimeParts(time, precision).forEachIndexed { partIndex, value ->
                val partName = calendarTimePartName(partIndex)
                val current = calendarTimePartValue(time, partIndex)
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    BasicText(
                        "−",
                        modifier = Modifier
                            .upClickable(enabled = !readonly) { onUpdate(partIndex, current - 1) }
                            .padding(horizontal = 10.dp, vertical = 2.dp)
                            .upTestTag("$prefix-$partName-decrease"),
                        style = TextStyle(color = UPTheme.Tips, fontSize = 14.sp),
                    )
                    BasicText(
                        value,
                        modifier = Modifier
                            .padding(vertical = 3.dp)
                            .upTestTag("$prefix-$partName"),
                        style = TextStyle(color = UPTheme.Main, fontSize = 16.sp, fontWeight = FontWeight.Bold),
                    )
                    BasicText(
                        "+",
                        modifier = Modifier
                            .upClickable(enabled = !readonly) { onUpdate(partIndex, current + 1) }
                            .padding(horizontal = 10.dp, vertical = 2.dp)
                            .upTestTag("$prefix-$partName-increase"),
                        style = TextStyle(color = UPTheme.Primary, fontSize = 14.sp),
                    )
                }
            }
        }
    }
}

private fun calendarTimePartName(index: Int): String = when (index) {
    0 -> "hour"
    1 -> "minute"
    else -> "second"
}

private fun calendarTimePartValue(time: UPCalendarTime, index: Int): Int = when (index) {
    0 -> time.hour
    1 -> time.minute
    else -> time.second
}

private fun calendarEvent(
    props: UPCalendarProps,
    selected: List<String>,
    clicked: String,
    times: List<UPCalendarTime>,
): UPCalendarEvent {
    val parts = clicked.split('-').mapNotNull(String::toIntOrNull)
    val resultValues = calendarResultValues(props, selected, times)
    val value: UPRawValue = when (props.mode) {
        "multiple", "range" -> resultValues
        else -> resultValues.firstOrNull() ?: ""
    }
    return UPCalendarEvent(
        value,
        parts.getOrElse(0) { 0 },
        parts.getOrElse(1) { 0 },
        parts.getOrElse(2) { 0 },
        props.mode,
        selected,
    )
}

private val UPCalendarMonth.key: String
    get() = "%04d-%02d".format(Locale.ROOT, year, month)
