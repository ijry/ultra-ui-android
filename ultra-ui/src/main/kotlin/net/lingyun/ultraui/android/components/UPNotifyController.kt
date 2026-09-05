package net.lingyun.ultraui.android.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.asFiniteFloatOrNull

/**
 * Host-side controller for `u-notify`'s imperative API. Upstream is called through a ref:
 * `show(options)` merges the options over the defaults, `close()` hides the banner, and
 * `created()` batch-generates one shortcut per theme (`primary`, `success`, `warning`,
 * `error`), each taking just a message.
 */
public class UPNotifyController(initial: UPNotifyProps? = null) {
    private val _current = mutableStateOf(initial)

    /** The options currently on screen, or `null` while nothing is shown. */
    public val current: State<UPNotifyProps?> get() = _current

    /**
     * `show(options)`: `deepMerge(this.config, options)` starts from the defaults every
     * time, so one call never inherits the previous call's overrides.
     */
    public fun show(options: UPNotifyProps) {
        _current.value = options
    }

    /** `this[type] = message => this.show({ type, message })` for each of the four themes. */
    public fun primary(message: String): Unit = show(UPNotifyProps(message = message, type = "primary"))

    public fun success(message: String): Unit = show(UPNotifyProps(message = message, type = "success"))

    public fun warning(message: String): Unit = show(UPNotifyProps(message = message, type = "warning"))

    public fun error(message: String): Unit = show(UPNotifyProps(message = message, type = "error"))

    /** `close()` clears the timer and hides the banner. */
    public fun close() {
        _current.value = null
    }
}

/** Remembers a [UPNotifyController] across recompositions. */
@Composable
public fun rememberUPNotifyController(): UPNotifyController = remember { UPNotifyController() }

/** `icon()`: only the three status themes carry a glyph; `primary` has none. */
internal fun upNotifyIconName(type: String): String? = when (type) {
    "success" -> "checkmark-circle"
    "error" -> "close-circle"
    "warning" -> "error-circle"
    else -> null
}

/** `:size="1.3 * tmpConfig.fontSize"` on the type icon. */
internal fun upNotifyIconSize(fontSize: UPRawValue): Float =
    (fontSize.asFiniteFloatOrNull() ?: 15f) * 1.3f
