package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.upIntOrDefault

internal const val UPSwiperComponentName: String = "UPSwiper"

private val UPSwiperImageExtensions =
    Regex("\\.(jpeg|jpg|gif|png|svg|webp|jfif|bmp|dpg)", RegexOption.IGNORE_CASE)
private val UPSwiperVideoExtensions =
    Regex("\\.(mp4|mpg|mpeg|dat|asf|avi|rm|rmvb|mov|wmv|flv|mkv|m3u8)", RegexOption.IGNORE_CASE)

/** Mirrors uview-plus `test.image()`: the query string is dropped before probing the extension. */
internal fun upSwiperIsImage(source: String): Boolean =
    UPSwiperImageExtensions.containsMatchIn(source.substringBefore('?'))

/** Mirrors uview-plus `test.video()`, which upstream runs on the raw source, query string included. */
internal fun upSwiperIsVideo(source: String): Boolean = UPSwiperVideoExtensions.containsMatchIn(source)

/** Mirrors `getSource()`: strings are used as-is, objects are read through `keyName`. */
internal fun upSwiperSource(item: UPRawValue, keyName: String): String = when {
    item is String -> item
    item is Map<*, *> && keyName.isNotEmpty() -> item.upStringKeyMapOrEmpty()[keyName].upStringValueOrEmpty()
    else -> ""
}

/** Mirrors `getItemType()`. Objects without a usable `keyName` return an empty type, as upstream does. */
internal fun upSwiperItemType(item: UPRawValue, keyName: String): String = when {
    item is String -> if (upSwiperIsVideo(item)) "video" else "image"
    item is Map<*, *> && keyName.isNotEmpty() -> {
        val declared = item.upStringKeyMapOrEmpty()["type"].upStringValueOrEmpty().trim().lowercase()
        when {
            declared.isEmpty() -> if (upSwiperIsVideo(upSwiperSource(item, keyName))) "video" else "image"
            declared == "video" -> "video"
            else -> "image"
        }
    }
    else -> ""
}

private fun upSwiperLooksLikeUri(source: String): Boolean =
    source.contains("://") || source.startsWith("/") || source.startsWith("data:")

/**
 * Renderer-level classification. Upstream hands every non-video string to `<image>`, which shows a
 * broken image for plain labels; Android keeps a text fallback instead so label-only lists stay
 * readable. Sources that look like an image, declare `type: 'image'` or carry a URI scheme still
 * render through [UPImage].
 */
internal fun upSwiperRenderKind(item: UPRawValue, keyName: String): String {
    val source = upSwiperSource(item, keyName)
    if (source.isEmpty()) return "text"
    val declared = item.upStringKeyMapOrEmpty()["type"].upStringValueOrEmpty().trim().lowercase()
    return when {
        upSwiperItemType(item, keyName) == "video" -> "video"
        upSwiperIsImage(source) || declared == "image" || upSwiperLooksLikeUri(source) -> "image"
        else -> "text"
    }
}

internal fun upSwiperTitle(item: UPRawValue): String =
    if (item is Map<*, *>) item.upStringKeyMapOrEmpty()["title"].upStringValueOrEmpty() else ""

internal fun upSwiperPoster(item: UPRawValue): String =
    if (item is Map<*, *>) item.upStringKeyMapOrEmpty()["poster"].upStringValueOrEmpty() else ""

/** Upstream only overlays the title bar on image slides that carry a `title` field. */
internal fun upSwiperShouldShowTitle(item: UPRawValue, keyName: String, showTitle: Boolean): Boolean =
    showTitle && upSwiperTitle(item).isNotEmpty() && upSwiperRenderKind(item, keyName) == "image"

/** Upstream renders the indicator only when `!loading && indicator && !showTitle`. */
internal fun upSwiperShouldShowIndicator(loading: Boolean, indicator: Boolean, showTitle: Boolean): Boolean =
    !loading && indicator && !showTitle

/** Upstream shrinks the neighbouring slides only when both margins reveal them. */
internal fun upSwiperItemScale(previousMargin: Float, nextMargin: Float, isCurrent: Boolean): Float =
    if (!isCurrent && previousMargin > 0f && nextMargin > 0f) 0.92f else 1f

/** Upstream passes `list.length > 0 ? displayMultipleItems : 0` down to the native swiper. */
internal fun upSwiperDisplayCount(displayMultipleItems: UPRawValue, listSize: Int): Int =
    if (listSize <= 0) 0 else displayMultipleItems.upIntOrDefault(1).coerceIn(1, listSize)

internal fun upSwiperNextIndex(index: Int, lastIndex: Int, circular: Boolean): Int? = when {
    lastIndex <= 0 -> null
    index < lastIndex -> index + 1
    circular -> 0
    else -> null
}

internal fun upSwiperPreviousIndex(index: Int, lastIndex: Int, circular: Boolean): Int? = when {
    lastIndex <= 0 -> null
    index > 0 -> index - 1
    circular -> lastIndex
    else -> null
}

/**
 * `currentItemId` addresses a slide by identity instead of position. Object slides are matched on
 * their `id` field, plain strings on their own value. Returns `-1` when nothing matches.
 */
internal fun upSwiperIndexForItemId(list: List<UPRawValue>, currentItemId: UPRawValue): Int {
    val target = currentItemId.upStringValueOrEmpty()
    if (target.isEmpty()) return -1
    return list.indexOfFirst { item ->
        val map = item.upStringKeyMapOrEmpty()
        if (map.isNotEmpty()) map["id"].upStringValueOrEmpty() == target else item.upStringValueOrEmpty() == target
    }
}

/** `currentItemId` wins over `current`, which upstream documents as mutually exclusive. */
internal fun upSwiperResolveIndex(list: List<UPRawValue>, current: UPRawValue, currentItemId: UPRawValue): Int {
    val byItemId = upSwiperIndexForItemId(list, currentItemId)
    return if (byItemId >= 0) byItemId else current.upIntOrDefault(0).coerceAtLeast(0)
}
