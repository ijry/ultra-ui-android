package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/**
 * Props for [UPGuide], mirroring uview-plus `up-guide`.
 *
 * A full-screen onboarding overlay paging through `list` (each `{ image, title, desc,
 * backgroundColor? }`). `showSkip` shows the skip button, `indicator` the dots, and the primary
 * button reads `nextText` until the last page then `finishText`. `once`/`storageKey` drive the
 * "show only once" memory upstream via local storage; see [UPGuide] for the Android handling.
 */
public data class UPGuideProps(
    val show: Boolean = false,
    val list: List<UPRawValue> = emptyList(),
    val storageKey: String = "up-guide-default",
    val once: Boolean = true,
    val showSkip: Boolean = true,
    val skipText: String = "跳过",
    val nextText: String = "下一步",
    val finishText: String = "立即体验",
    val indicator: Boolean = true,
    val bgColor: String = "#111111",
    val zIndex: UPRawValue = 10075,
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
