package net.lingyun.ultraui.android.components

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.report
import net.lingyun.ultraui.android.core.upSafeEnum
import net.lingyun.ultraui.android.core.upTestTag

/**
 * Native Compose counterpart of uview-plus `u-form`.
 *
 * The upstream component is a plain wrapper that owns the validation state of
 * its `u-form-item` children, so the Android port keeps the same split: layout
 * lives here, every child registers itself through [LocalUPFormContext], and
 * the imperative `ref` methods are reached through [controller].
 *
 * `model` stays an immutable map, therefore `resetFields()` / `resetField()`
 * cannot mutate it the way Vue reactivity does; they publish the restored map
 * through [onUpdateModel] instead. [onToast] receives the first error message
 * when `errorType` is `toast`, replacing the upstream global toast call.
 */
@Composable
public fun UPForm(
    props: UPFormProps = UPFormProps(),
    modifier: Modifier = Modifier,
    controller: UPFormController? = null,
    onUpdateModel: ((Map<String, UPRawValue>) -> Unit)? = null,
    onToast: ((String) -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
    content: @Composable () -> Unit,
) {
    val children = remember { mutableStateListOf<UPFormItemRegistration>() }
    // Mirrors the `immediate` model watcher that stores `deepClone(model)` once:
    // the first observed map is the snapshot `resetFields()` restores. Maps are
    // read only on this platform, so no defensive copy is required.
    val originalModel = remember { props.model }
    var formRules by remember { mutableStateOf(props.rules) }

    val errorType = upSafeEnum(
        props.errorType,
        setOf("message", "none", "toast", "border-bottom"),
        "message",
        diagnostics,
        UPFormComponentName,
        "errorType",
    )
    val labelPosition = upSafeEnum(
        props.labelPosition,
        setOf("left", "top"),
        "left",
        diagnostics,
        UPFormComponentName,
        "labelPosition",
    )
    val labelAlign = upSafeEnum(
        props.labelAlign,
        setOf("left", "center", "right"),
        "left",
        diagnostics,
        UPFormComponentName,
        "labelAlign",
    )

    fun setRules(rules: Map<String, UPRawValue>) {
        if (rules.isEmpty()) return
        // Upstream aborts the assignment in development when `model` is empty.
        // There is no development flag here, so the mismatch is reported and the
        // rules are still applied, matching the upstream production build.
        if (props.model.isEmpty()) {
            diagnostics.report(
                UPFormComponentName,
                "rules",
                rules.keys.toList(),
                "`model` is empty, so no rule can read a value.",
            )
        }
        formRules = rules
    }

    LaunchedEffect(props.rules) { setRules(props.rules) }

    fun validateField(
        fields: List<String>,
        event: String?,
        showErrorMsg: Boolean,
    ): List<UPFormError> {
        val errors = mutableListOf<UPFormError>()
        children.forEach { child ->
            if (child.prop !in fields) return@forEach
            val value = upFormGetProperty(props.model, child.prop)
            val propertyName = child.prop.split(".").last()
            val itemRules = upFormNormalizeRules(child.itemRules())
            // Item level rules win over the form level entry, and a missing
            // entry skips the child instead of feeding the validator a null.
            val rules = if (itemRules.isNotEmpty()) {
                itemRules
            } else {
                upFormNormalizeRules(formRules[child.prop] ?: return@forEach)
            }
            val childErrors = upFormRunRules(propertyName, value, rules, event, child.prop, diagnostics)
            // The message is only rewritten when at least one rule ran, because
            // upstream assigns it inside the per rule validator callback.
            val executed = rules.any { upFormRuleRunsOn(it, event) }
            if (showErrorMsg && executed) {
                child.message.value = childErrors.firstOrNull()?.message ?: ""
            }
            errors += childErrors
        }
        return errors
    }

    fun validate(showErrorMsg: Boolean): List<UPFormError> {
        // Upstream bails out in development when no form level rule exists, but
        // item level rules alone are a valid setup, so this only reports.
        if (formRules.isEmpty() && children.none { it.itemRules().isNotEmpty() }) {
            diagnostics.report(
                UPFormComponentName,
                "rules",
                props.rules,
                "No rule is configured, so validate() can never fail.",
            )
        }
        val errors = validateField(children.map { it.prop }, null, showErrorMsg)
        if (errors.isNotEmpty() && errorType == "toast") onToast?.invoke(errors.first().message)
        return errors
    }

    fun resetFields() {
        // `resetFields()` delegates to `resetModel()` upstream, which restores
        // the snapshot without touching `message`; only `u-form-item`'s own
        // `resetField()` clears the error text. The asymmetry is kept on purpose.
        var next = props.model
        children.forEach { child ->
            next = upFormSetProperty(next, child.prop, upFormGetProperty(originalModel, child.prop))
        }
        onUpdateModel?.invoke(next)
    }

    fun resetField(prop: String) {
        onUpdateModel?.invoke(upFormSetProperty(props.model, prop, upFormGetProperty(originalModel, prop)))
        children.forEach { child -> if (child.prop == prop) child.message.value = "" }
    }

    fun clearValidate(fields: List<String>) {
        children.forEach { child ->
            if (fields.isEmpty() || child.prop in fields) child.message.value = ""
        }
    }

    fun register(registration: UPFormItemRegistration) {
        if (children.none { it === registration }) children += registration
    }

    fun unregister(registration: UPFormItemRegistration) {
        children.removeAll { it === registration }
    }

    if (controller != null) {
        val binding = UPFormController.Binding(
            validate = ::validate,
            validateField = ::validateField,
            resetFields = ::resetFields,
            resetField = ::resetField,
            clearValidate = ::clearValidate,
            setRules = ::setRules,
        )
        SideEffect { controller.attach(binding) }
        DisposableEffect(controller) { onDispose { controller.detach() } }
    }

    val context = UPFormContext(
        errorType = errorType,
        labelPosition = labelPosition,
        labelAlign = labelAlign,
        labelWidth = props.labelWidth,
        labelStyle = props.labelStyle,
        register = ::register,
        unregister = ::unregister,
        validateField = ::validateField,
    )

    Column(
        modifier = modifier
            .applyUPResolvedStyle(rememberUPResolvedStyle(props.customStyle, diagnostics, UPFormComponentName))
            .upTestTag("form"),
    ) {
        CompositionLocalProvider(LocalUPFormContext provides context) {
            content()
        }
    }
}

/** Direct argument form for generated source. */
@Composable
public fun UPForm(
    model: Map<String, UPRawValue> = emptyMap(),
    rules: Map<String, UPRawValue> = emptyMap(),
    errorType: String = "message",
    labelPosition: String = "left",
    labelWidth: UPRawValue = 45,
    modifier: Modifier = Modifier,
    controller: UPFormController? = null,
    onUpdateModel: ((Map<String, UPRawValue>) -> Unit)? = null,
    onToast: ((String) -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
    content: @Composable () -> Unit,
) {
    UPForm(
        props = UPFormProps(
            model = model,
            rules = rules,
            errorType = errorType,
            labelPosition = labelPosition,
            labelWidth = labelWidth,
        ),
        modifier = modifier,
        controller = controller,
        onUpdateModel = onUpdateModel,
        onToast = onToast,
        diagnostics = diagnostics,
        content = content,
    )
}
