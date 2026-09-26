package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/**
 * Props for [UPNovelReader], mirroring uview-plus `u-novel-reader`.
 *
 * [currentChapter] (`{ id, index, title, content }`) is rendered as themed, scrollable paragraphs;
 * [chapters] feeds the catalog. [loading]/[error] show the loading/error states. [defaultSettings]
 * merged with [settings] drive `theme`(day/paper/green/night/dark)/`fontSize`/`lineHeight`/
 * `paragraphSpacing`/`contentWidth`/`fontFamily`/`fontWeight`. [mode] selects scroll vs page,
 * [showBack]/[backIcon] the back control, [safeAreaInsetTop]/[safeAreaInsetBottom] the insets and
 * [controlsAutoHide] the ms after which the toolbars auto-hide (0 = never).
 *
 * Persistence, prefetch and bookmark storage are host concerns and inert: [bookId], [storageKey],
 * [persist], [initialProgress], [progress], [initialBookmarks], [bookmarks], [preloadThreshold],
 * [pageAnimation] and [autoBack]; see docs.
 */
public data class UPNovelReaderProps(
    val chapters: List<UPRawValue> = emptyList(),
    val currentChapter: Map<String, UPRawValue> = emptyMap(),
    val loading: Boolean = false,
    val error: Map<String, UPRawValue> = emptyMap(),
    val bookId: UPRawValue = "",
    val storageKey: String = "",
    val persist: Boolean = true,
    val initialProgress: Map<String, UPRawValue> = emptyMap(),
    val progress: Map<String, UPRawValue> = emptyMap(),
    val initialBookmarks: List<UPRawValue> = emptyList(),
    val bookmarks: List<UPRawValue> = emptyList(),
    val defaultSettings: Map<String, UPRawValue> = mapOf(
        "theme" to "day",
        "fontSize" to 18,
        "lineHeight" to 1.8,
        "paragraphSpacing" to 16,
        "contentWidth" to "92%",
        "fontFamily" to "system",
        "fontWeight" to 400,
        "animation" to true,
    ),
    val settings: Map<String, UPRawValue> = emptyMap(),
    val mode: String = "scroll",
    val showBack: Boolean = true,
    val autoBack: Boolean = false,
    val backIcon: String = "arrow-left",
    val safeAreaInsetTop: Boolean = true,
    val safeAreaInsetBottom: Boolean = true,
    val preloadThreshold: Int = 2,
    val pageAnimation: Boolean = true,
    val controlsAutoHide: Int = 0,
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
