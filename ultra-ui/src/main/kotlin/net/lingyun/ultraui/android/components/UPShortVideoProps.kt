package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

/**
 * Props for [UPShortVideo], mirroring uview-plus `u-short-video`.
 *
 * [tabsList] drives the top category tabs ([currentTab]); [videoList] is the vertical feed
 * ([currentVideo] is the active page). Each video item carries `videoUrl`/`poster`, an `author`
 * (`avatar`/`name`/`desc`), the action counts (`likeCount`/`commentCount`/`shareCount`/`collectCount`
 * with `isLiked`/`isCollected`), a bottom `title`/`desc` and a `progress` (0..100).
 */
public data class UPShortVideoProps(
    val tabsList: List<UPRawValue> = listOf(
        mapOf("name" to "推荐"),
        mapOf("name" to "关注"),
        mapOf("name" to "朋友"),
        mapOf("name" to "本地"),
    ),
    val videoList: List<UPRawValue> = emptyList(),
    val currentTab: Int = 0,
    val currentVideo: Int = 0,
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
