package net.lingyun.ultraui.android.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.core.report
import net.lingyun.ultraui.android.core.upIntOrDefault
import net.lingyun.ultraui.android.core.upTestTag

/**
 * Imperative handle for the `start()` / `reset()` methods `u-code` exposes on its ref.
 *
 * Stays inert until it reaches a composed [UPCode]; calls made beforehand are no-ops.
 */
public class UPCodeController {
    internal class Binding(
        val start: () -> Unit,
        val reset: () -> Unit,
        val canGetCode: () -> Boolean,
    )

    private var binding: Binding? = null

    internal fun attach(binding: Binding) {
        this.binding = binding
    }

    internal fun detach() {
        binding = null
    }

    /** `start()`: begins the countdown; upstream re-arms `secNum` and emits `start` + `change`. */
    public fun start() {
        binding?.start?.invoke()
    }

    /** `reset()`: stops the timer and re-enables the button (`canGetCode = true`). */
    public fun reset() {
        binding?.reset?.invoke()
    }

    /** Reflects `canGetCode`; false while a countdown is running. */
    public val canGetCode: Boolean
        get() = binding?.canGetCode?.invoke() ?: true
}

/** Remembers a [UPCodeController] across recompositions. */
@Composable
public fun rememberUPCodeController(): UPCodeController = remember { UPCodeController() }

/**
 * Native Compose counterpart of uview-plus `u-code`.
 *
 * Upstream renders nothing itself (`<view style="display: none;"><slot :text="codeText" /></view>`)
 * and drives a verification-code countdown through a ref's `start()` / `reset()`, emitting
 * `start` once, then `change(secNum)` every second. The port keeps that headless shape when a
 * [content] slot is supplied — it receives the current prompt text — and otherwise renders the
 * text with [BasicText] so a bare `UPCode()` is still visible.
 *
 * The prompt text follows the documented `startText` / `changeText` / `endText` contract, with
 * `changeText`'s literal `X` replaced by the current second. This upstream snapshot stubs out
 * `setTimeText()` / `getText()` (so `codeText` is never actually assigned) and never `$emit`s the
 * declared `end` event; the port reconstructs both from the documented property/event contract,
 * firing [onEnd] when the countdown reaches zero.
 *
 * `keepRunning` / `uniqueKey` persist an in-flight countdown across an H5 refresh via local
 * storage; there is no equivalent for a recomposing Android tree, so both are surfaced as
 * diagnostics rather than silently ignored.
 */
@Composable
public fun UPCode(
    props: UPCodeProps = UPCodeProps(),
    modifier: Modifier = Modifier,
    controller: UPCodeController? = null,
    onChange: ((Int) -> Unit)? = null,
    onStart: (() -> Unit)? = null,
    onEnd: (() -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
    content: (@Composable (String) -> Unit)? = null,
) {
    val total = props.seconds.upIntOrDefault(60).coerceAtLeast(0)
    var secNum by remember { mutableIntStateOf(total) }
    var running by remember { mutableStateOf(false) }
    var started by remember { mutableStateOf(false) }
    var runToken by remember { mutableIntStateOf(0) }

    LaunchedEffect(props.keepRunning, props.uniqueKey, diagnostics) {
        if (props.keepRunning) {
            diagnostics.report("UPCode", "keepRunning", props.keepRunning.toString(), "H5-only local-storage resume; no Android equivalent.")
        }
        if (props.uniqueKey.isNotEmpty()) {
            diagnostics.report("UPCode", "uniqueKey", props.uniqueKey, "Only meaningful with keepRunning's H5 persistence; inert on Android.")
        }
    }

    fun start() {
        if (running) return
        onStart?.invoke()
        running = true
        started = true
        secNum = total
        onChange?.invoke(secNum)
        runToken += 1
    }

    fun reset() {
        running = false
        started = false
        secNum = total
    }

    if (controller != null) {
        val binding = UPCodeController.Binding(start = ::start, reset = ::reset, canGetCode = { !running })
        SideEffect { controller.attach(binding) }
        DisposableEffect(controller) { onDispose { controller.detach() } }
    }

    // `setInterval(..., 1000)` with `if (--secNum)`: decrement first, emit `change` while
    // positive, and stop (re-enabling the button) when it reaches zero.
    LaunchedEffect(running, runToken) {
        if (!running) return@LaunchedEffect
        while (running) {
            delay(1_000L)
            if (!running) break
            val next = secNum - 1
            secNum = next
            if (next > 0) {
                onChange?.invoke(next)
            } else {
                running = false
                onEnd?.invoke()
                break
            }
        }
    }

    val text = upCodeText(props, running = running, started = started, secNum = secNum)

    Box(modifier.applyUPResolvedStyle(rememberUPResolvedStyle(props.customStyle, diagnostics, "UPCode")).upTestTag("code")) {
        if (content != null) {
            content(text)
        } else {
            BasicText(text, modifier = Modifier.upTestTag("code-text"), style = TextStyle(color = UPTheme.Content, fontSize = 15.sp))
        }
    }
}

/**
 * `getText()`-equivalent: the prompt text for the current phase. `changeText`'s first `X`
 * is swapped for the current second, matching the documented `X秒重新获取`.
 */
internal fun upCodeText(props: UPCodeProps, running: Boolean, started: Boolean, secNum: Int): String = when {
    running -> props.changeText.replaceFirst("X", secNum.toString())
    started -> props.endText
    else -> props.startText
}
