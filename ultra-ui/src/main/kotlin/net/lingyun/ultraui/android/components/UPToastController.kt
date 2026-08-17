package net.lingyun.ultraui.android.components

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import net.lingyun.ultraui.android.core.UPRawValue

/** Host-side controller for the imperative `u-toast` API without a JSON/runtime host. */
public class UPToastController(initial: UPToastProps? = null) {
    private val _current = mutableStateOf(initial)

    public val current: State<UPToastProps?> get() = _current

    public fun show(options: UPToastProps = UPToastProps(show = true)) {
        _current.value = options.copy(show = true)
    }

    public fun show(
        message: String,
        type: String = "default",
        duration: UPRawValue = 2000,
    ) {
        show(UPToastProps(show = true, message = message, type = type, duration = duration))
    }

    public fun hide() {
        _current.value = _current.value?.copy(show = false)
    }

    public fun clear() {
        _current.value = null
    }
}
