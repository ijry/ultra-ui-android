package net.lingyun.ultraui.android.components

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.resolveUPModelValue
import net.lingyun.ultraui.android.core.upTestTag

private const val CollapseComponentName = "UPCollapse"

internal class UPCollapseContext(
    val openNames: List<UPRawValue>,
    val accordion: Boolean,
    val border: Boolean,
    val parentDisabled: Boolean,
    val parentClickable: Boolean,
    val arrow: Boolean,
    val allocateIndex: () -> Int,
    val registerName: (UPRawValue) -> Unit,
    val unregisterName: (UPRawValue) -> Unit,
    val toggle: (UPRawValue, Boolean, Boolean, ((UPRawValue) -> Unit)?) -> Unit,
)

internal val LocalUPCollapseContext = staticCompositionLocalOf<UPCollapseContext?> { null }

private fun collapseNames(value: UPRawValue, accordion: Boolean): List<UPRawValue> {
    if (accordion) return if (value == null || value.toString().isEmpty()) emptyList() else listOf(value)
    return when {
        value == null -> emptyList()
        value is List<*> -> value as List<UPRawValue>
        value is Array<*> -> value.toList() as List<UPRawValue>
        else -> listOf(value)
    }
}

private fun collapsePayload(names: List<UPRawValue>, openNames: List<UPRawValue>): List<UPRawValue> =
    names.map { name ->
        mapOf(
            "name" to name,
            "status" to if (openNames.any { it.upLooseEquals(name) }) "open" else "close",
        )
    }

/** Native Compose counterpart of uview-plus `u-collapse`. */
@Composable
public fun UPCollapse(
    props: UPCollapseProps = UPCollapseProps(),
    modifier: Modifier = Modifier,
    onChange: ((UPRawValue) -> Unit)? = null,
    onUpdateModelValue: ((UPRawValue) -> Unit)? = null,
    onUpdateValue: ((UPRawValue) -> Unit)? = null,
    onOpen: ((UPRawValue) -> Unit)? = null,
    onClose: ((UPRawValue) -> Unit)? = null,
    onItemClick: ((UPRawValue) -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
    content: @Composable () -> Unit,
) {
    val requestedValue = resolveUPModelValue(props.modelValue, props.value)
    var openNames by remember(props.accordion) {
        mutableStateOf(collapseNames(requestedValue, props.accordion))
    }
    LaunchedEffect(props.modelValue, props.value, props.accordion) {
        openNames = collapseNames(resolveUPModelValue(props.modelValue, props.value), props.accordion)
    }

    val knownNames = remember { mutableStateListOf<UPRawValue>() }
    var nextIndex by remember { mutableStateOf(0) }

    fun registerName(name: UPRawValue) {
        if (knownNames.none { it.upLooseEquals(name) }) knownNames += name
    }

    fun unregisterName(name: UPRawValue) {
        knownNames.removeAll { it.upLooseEquals(name) }
    }

    fun toggle(
        name: UPRawValue,
        itemDisabled: Boolean,
        itemClickable: Boolean,
        itemOnClick: ((UPRawValue) -> Unit)?,
    ) {
        if (props.disabled || !props.clickable || itemDisabled || !itemClickable) return
        itemOnClick?.invoke(name)
        onItemClick?.invoke(name)
        val wasOpen = openNames.any { it.upLooseEquals(name) }
        val next = if (props.accordion) {
            if (wasOpen) emptyList() else listOf(name)
        } else {
            if (wasOpen) openNames.filterNot { it.upLooseEquals(name) } else openNames + name
        }
        openNames = next
        val payloadNames = (knownNames + name).distinctBy { it?.toString() }
        val payload = collapsePayload(payloadNames, next)
        onChange?.invoke(payload)
        val modelValue: UPRawValue = if (props.accordion) next.firstOrNull() else next
        onUpdateModelValue?.invoke(modelValue)
        onUpdateValue?.invoke(modelValue)
        if (next.any { it.upLooseEquals(name) }) onOpen?.invoke(name) else onClose?.invoke(name)
    }

    val context = UPCollapseContext(
        openNames = openNames,
        accordion = props.accordion,
        border = props.border,
        parentDisabled = props.disabled,
        parentClickable = props.clickable,
        arrow = props.arrow,
        allocateIndex = { nextIndex++ },
        registerName = ::registerName,
        unregisterName = ::unregisterName,
        toggle = ::toggle,
    )

    Column(modifier = modifier.upTestTag("collapse")) {
        CompositionLocalProvider(LocalUPCollapseContext provides context) {
            content()
        }
    }
}

/** Direct argument form for generated source. */
@Composable
public fun UPCollapse(
    value: UPRawValue = null,
    accordion: Boolean = false,
    border: Boolean = true,
    modifier: Modifier = Modifier,
    onChange: ((UPRawValue) -> Unit)? = null,
    onUpdateModelValue: ((UPRawValue) -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
    content: @Composable () -> Unit,
) {
    UPCollapse(
        props = UPCollapseProps(value = value, accordion = accordion, border = border),
        modifier = modifier,
        onChange = onChange,
        onUpdateModelValue = onUpdateModelValue,
        diagnostics = diagnostics,
        content = content,
    )
}
