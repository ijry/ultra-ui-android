package net.lingyun.ultraui.android.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

/**
 * `u-count-down/utils.js` plus the imperative `ref` methods the component documents
 * as `start` / `pause` / `reset`. The helpers stay pure so the format degradation
 * chain can be pinned by JVM tests without a composition.
 */

private const val UPCountDownSecondMillis = 1_000L
private const val UPCountDownMinuteMillis = 60L * UPCountDownSecondMillis
private const val UPCountDownHourMillis = 60L * UPCountDownMinuteMillis
private const val UPCountDownDayMillis = 24L * UPCountDownHourMillis

/** `padZero(num, targetLength = 2)`. */
private fun upCountDownPadZero(value: Long, targetLength: Int = 2): String =
    value.toString().padStart(targetLength, '0')

/**
 * `parseTimeData(time)`: splits a millisecond budget into calendar-like buckets.
 * Callers clamp negative budgets away, matching `getRemainTime()`'s `Math.max(..., 0)`.
 */
internal fun upCountDownParseTimeData(time: Long): UPCountDownTime {
    val safe = time.coerceAtLeast(0L)
    return UPCountDownTime(
        days = (safe / UPCountDownDayMillis).toInt(),
        hours = ((safe % UPCountDownDayMillis) / UPCountDownHourMillis).toInt(),
        minutes = ((safe % UPCountDownHourMillis) / UPCountDownMinuteMillis).toInt(),
        seconds = ((safe % UPCountDownMinuteMillis) / UPCountDownSecondMillis).toInt(),
        milliseconds = (safe % UPCountDownSecondMillis).toInt(),
    )
}

/**
 * `parseFormat(format, timeData)`: every unit missing from the pattern folds into the
 * next smaller one, so `mm:ss` counts a whole day as 1440 minutes. Upstream relies on
 * `String.prototype.replace` with a string needle, which swaps the first hit only, hence
 * [replaceFirst]. Milliseconds accumulate in a [Long] because a multi-day budget folded
 * all the way down to `SSS` overflows 32 bits.
 */
internal fun upCountDownParseFormat(format: String, timeData: UPCountDownTime): String {
    var pattern = format
    var hours = timeData.hours.toLong()
    var minutes = timeData.minutes.toLong()
    var seconds = timeData.seconds.toLong()
    var milliseconds = timeData.milliseconds.toLong()
    if (!pattern.contains("DD")) {
        hours += timeData.days * 24L
    } else {
        pattern = pattern.replaceFirst("DD", upCountDownPadZero(timeData.days.toLong()))
    }
    if (!pattern.contains("HH")) {
        minutes += hours * 60L
    } else {
        pattern = pattern.replaceFirst("HH", upCountDownPadZero(hours))
    }
    if (!pattern.contains("mm")) {
        seconds += minutes * 60L
    } else {
        pattern = pattern.replaceFirst("mm", upCountDownPadZero(minutes))
    }
    if (!pattern.contains("ss")) {
        milliseconds += seconds * 1_000L
    } else {
        pattern = pattern.replaceFirst("ss", upCountDownPadZero(seconds))
    }
    return pattern.replaceFirst("SSS", upCountDownPadZero(milliseconds, 3))
}

/** `isSameSecond(time1, time2)` — the macro tick only repaints once a second changes. */
internal fun upCountDownIsSameSecond(first: Long, second: Long): Boolean =
    first / UPCountDownSecondMillis == second / UPCountDownSecondMillis

/** `macroTick()` polls every 30ms, `microTick()` every 50ms. */
internal fun upCountDownTickIntervalMillis(millisecond: Boolean): Long = if (millisecond) 50L else 30L

/**
 * Imperative handle for the methods `u-count-down` exposes on its ref.
 *
 * Like [UPFormController] the handle stays inert until it reaches a composed
 * [UPCountDown]; calls made before that are no-ops instead of throwing.
 */
public class UPCountDownController {
    internal class Binding(
        val start: () -> Unit,
        val pause: () -> Unit,
        val reset: () -> Unit,
    )

    private var binding: Binding? = null

    internal fun attach(binding: Binding) {
        this.binding = binding
    }

    internal fun detach() {
        binding = null
    }

    /** `start()`: re-bases the deadline on the remaining time; a running clock is left alone. */
    public fun start() {
        binding?.start?.invoke()
    }

    /** `pause()`: stops the tick and keeps the last published remainder. */
    public fun pause() {
        binding?.pause?.invoke()
    }

    /** `reset()`: restores `time` and starts again when `autoStart` is set. */
    public fun reset() {
        binding?.reset?.invoke()
    }
}

/** Remembers a [UPCountDownController] across recompositions. */
@Composable
public fun rememberUPCountDownController(): UPCountDownController = remember { UPCountDownController() }
