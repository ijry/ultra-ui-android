package net.lingyun.ultraui.android.components

import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.core.upTestTag

/**
 * Imperative handle for the `showModal()` method `up-agreement` exposes on its ref.
 *
 * Stays inert until it reaches a composed [UPAgreement]; calls made beforehand are no-ops.
 */
public class UPAgreementController {
    internal class Binding(val show: () -> Unit, val hide: () -> Unit)

    private var binding: Binding? = null

    internal fun attach(binding: Binding) {
        this.binding = binding
    }

    internal fun detach() {
        binding = null
    }

    /** `showModal()`: opens the agreement modal. */
    public fun showModal() {
        binding?.show?.invoke()
    }

    /** Closes the modal without emitting `confirm` (mirrors the internal `show = false`). */
    public fun hide() {
        binding?.hide?.invoke()
    }
}

/** Remembers a [UPAgreementController] across recompositions. */
@Composable
public fun rememberUPAgreementController(): UPAgreementController = remember { UPAgreementController() }

/**
 * Native Compose counterpart of uview-plus `up-agreement`.
 *
 * A privacy/user-agreement gate: a [UPModal] with a cancel button and the confirm label
 * `阅读并同意`. Its default body is the upstream boilerplate paragraph with two inline links,
 * `用户协议` and `隐私政策`, tinted with the theme primary. Tapping a link navigates to
 * `urlProtocol` / `urlPrivacy`; upstream calls `uni.navigateTo`, so the port forwards the target
 * through [onNavigate] because routing belongs to the host. Confirm emits `confirm(1)` upstream,
 * surfaced here as [onConfirm] plus the modal closing.
 *
 * Difference: the upstream cancel path calls `window.close()` / `plus.runtime.quit()` to leave
 * the app; there is no portable Android equivalent, so cancel closes the modal and reports
 * through [onClose] for the host to decide (e.g. finish the activity).
 */
@Composable
public fun UPAgreement(
    props: UPAgreementProps = UPAgreementProps(),
    modifier: Modifier = Modifier,
    controller: UPAgreementController? = null,
    onConfirm: (() -> Unit)? = null,
    onClose: (() -> Unit)? = null,
    onNavigate: ((String) -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
    content: (@Composable () -> Unit)? = null,
) {
    var show by remember { mutableStateOf(false) }

    if (controller != null) {
        val binding = UPAgreementController.Binding(show = { show = true }, hide = { show = false })
        androidx.compose.runtime.SideEffect { controller.attach(binding) }
        androidx.compose.runtime.DisposableEffect(controller) { onDispose { controller.detach() } }
    }

    UPModal(
        props = UPModalProps(
            show = show,
            showCancelButton = true,
            confirmText = "阅读并同意",
            customStyle = props.customStyle,
        ),
        modifier = modifier,
        onConfirm = {
            show = false
            onConfirm?.invoke()
        },
        onCancel = {
            show = false
            onClose?.invoke()
        },
        onUpdateShow = { show = it },
        content = {
            if (content != null) {
                content()
            } else {
                val linkColor = UPTheme.Primary
                val body = buildAnnotatedString {
                    append("我们非常重视您的个人信息和隐私保护。为了更好地保障您的个人权益，在您使用我们的产品前，请务必审慎阅读《")
                    pushStringAnnotation(tag = "protocol", annotation = props.urlProtocol)
                    withStyle(SpanStyle(color = linkColor)) { append("用户协议") }
                    pop()
                    append("》和《")
                    pushStringAnnotation(tag = "privacy", annotation = props.urlPrivacy)
                    withStyle(SpanStyle(color = linkColor)) { append("隐私政策") }
                    pop()
                    append("》内的所有条款。如您对以上协议有任何疑问，请先不要同意，您点击“阅读并同意”的行为即表示您已阅读完毕并同意以上协议的全部内容。")
                }
                ClickableText(
                    text = body,
                    modifier = Modifier.upTestTag("agreement-content"),
                    style = TextStyle(color = UPTheme.Main, fontSize = 14.sp),
                    onClick = { offset ->
                        body.getStringAnnotations(tag = "protocol", start = offset, end = offset).firstOrNull()?.let { onNavigate?.invoke(it.item) }
                        body.getStringAnnotations(tag = "privacy", start = offset, end = offset).firstOrNull()?.let { onNavigate?.invoke(it.item) }
                    },
                )
            }
        },
        diagnostics = diagnostics,
    )
}
