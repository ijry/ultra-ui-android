package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/**
 * Props for [UPAgreement], mirroring uview-plus `up-agreement`.
 *
 * `urlProtocol` / `urlPrivacy` are the routes the two inline links navigate to; upstream calls
 * `uni.navigateTo({ url })`, so the Android port forwards them through an `onNavigate` callback
 * since routing belongs to the host.
 */
public data class UPAgreementProps(
    val urlProtocol: String = "/pages/user_agreement/agreement/info?title=用户协议",
    val urlPrivacy: String = "/pages/user_agreement/agreement/info?title=隐私政策",
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
