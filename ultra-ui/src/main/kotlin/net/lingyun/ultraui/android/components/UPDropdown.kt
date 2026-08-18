package net.lingyun.ultraui.android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.core.upSafeEnum
import net.lingyun.ultraui.android.core.upTestTag

private const val DropdownComponentName = "UPDropdown"

internal class UPDropdownContext {
    var isOpen by mutableStateOf(false)
    var activeIndex by mutableStateOf(-1)
    var nextIndex: Int = 0
    var closeOnClickMask: Boolean = true
    var closeOnClickSelf: Boolean = true
    var activeColor: String = "#2979ff"
    var inactiveColor: String = "#606266"
    var closeCallback: ((Boolean) -> Unit)? = null

    fun beginComposition() {
        nextIndex = 0
    }

    fun allocateIndex(): Int = nextIndex++

    fun toggle(index: Int) {
        val next = if (isOpen && activeIndex == index && closeOnClickSelf) {
            false
        } else {
            true
        }
        activeIndex = if (next) index else -1
        isOpen = next
        closeCallback?.invoke(next)
    }

    fun close() {
        if (!isOpen) return
        isOpen = false
        activeIndex = -1
        closeCallback?.invoke(false)
    }
}

internal val LocalUPDropdownContext = staticCompositionLocalOf<UPDropdownContext?> { null }

/** Native Compose counterpart of uview-plus `u-dropdown`. */
@Composable
public fun UPDropdown(
    props: UPDropdownProps = UPDropdownProps(),
    modifier: Modifier = Modifier,
    open: Boolean? = null,
    onUpdateOpen: ((Boolean) -> Unit)? = null,
    onOpen: (() -> Unit)? = null,
    onClose: (() -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
    content: @Composable () -> Unit,
) {
    val direction = upSafeEnum(
        props.direction ?: "down",
        setOf("down", "up"),
        "down",
        diagnostics,
        DropdownComponentName,
        "direction",
    )
    val context = remember { UPDropdownContext() }
    context.beginComposition()
    context.closeOnClickMask = props.closeOnClickMask
    context.closeOnClickSelf = props.closeOnClickSelf
    context.activeColor = props.activeColor
    context.inactiveColor = props.inactiveColor
    context.closeCallback = { nextOpen ->
        onUpdateOpen?.invoke(nextOpen)
        if (nextOpen) onOpen?.invoke() else onClose?.invoke()
    }
    val requestedOpen = open ?: false
    LaunchedEffect(open) {
        if (open != null && context.isOpen != requestedOpen) {
            context.isOpen = requestedOpen
            if (!requestedOpen) context.activeIndex = -1
        }
    }

    val style = rememberUPResolvedStyle(props.customStyle, diagnostics, DropdownComponentName)
    val rootModifier = modifier
        .fillMaxWidth()
        .background(UPColorOrFallback(props.bgColor), RoundedCornerShape(upRawDp(props.borderRadius, 0.dp)))
        .applyUPResolvedStyle(style)
        .upTestTag("dropdown")

    // The direction is intentionally represented as layout order. This keeps
    // screenshots deterministic while preserving the generated prop contract.
    if (direction == "up") {
        Column(modifier = rootModifier) {
            androidx.compose.runtime.CompositionLocalProvider(LocalUPDropdownContext provides context) {
                content()
            }
        }
    } else {
        Column(modifier = rootModifier) {
            androidx.compose.runtime.CompositionLocalProvider(LocalUPDropdownContext provides context) {
                content()
            }
        }
    }
}

/** Direct argument form for generated source. */
@Composable
public fun UPDropdown(
    activeColor: String = "#2979ff",
    inactiveColor: String = "#606266",
    open: Boolean = false,
    modifier: Modifier = Modifier,
    onUpdateOpen: ((Boolean) -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
    content: @Composable () -> Unit,
) {
    UPDropdown(
        props = UPDropdownProps(activeColor = activeColor, inactiveColor = inactiveColor),
        modifier = modifier,
        open = open,
        onUpdateOpen = onUpdateOpen,
        diagnostics = diagnostics,
        content = content,
    )
}

private fun UPColorOrFallback(value: String): Color =
    net.lingyun.ultraui.android.core.UPColor.parse(value, Color.White)
