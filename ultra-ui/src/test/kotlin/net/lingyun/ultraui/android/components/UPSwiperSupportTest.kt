package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPRawValue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UPSwiperSupportTest {
    @Test
    fun swiperPropsMirrorUpstreamDefaults() {
        val props = UPSwiperProps()
        assertEquals(emptyList<UPRawValue>(), props.list)
        assertFalse(props.indicator)
        assertEquals("#FFFFFF", props.indicatorActiveColor)
        assertEquals("rgba(255, 255, 255, 0.35)", props.indicatorInactiveColor)
        assertEquals("line", props.indicatorMode)
        assertTrue(props.autoplay)
        assertEquals(0, props.current)
        assertEquals("", props.currentItemId)
        assertEquals(3000, props.interval)
        assertEquals(300, props.duration)
        assertFalse(props.circular)
        assertFalse(props.vertical)
        assertEquals(0, props.previousMargin)
        assertEquals(0, props.nextMargin)
        assertFalse(props.acceleration)
        assertEquals(1, props.displayMultipleItems)
        assertEquals("default", props.easingFunction)
        assertEquals("url", props.keyName)
        assertEquals("aspectFill", props.imgMode)
        assertEquals(130, props.height)
        assertEquals("#f3f4f6", props.bgColor)
        assertEquals(4, props.radius)
        assertFalse(props.loading)
        assertFalse(props.showTitle)
    }

    @Test
    fun imageProbeIgnoresTheQueryString() {
        assertTrue(upSwiperIsImage("https://cdn.test/a.png"))
        assertTrue(upSwiperIsImage("https://cdn.test/a.PNG?x=1&y=2"))
        assertTrue(upSwiperIsImage("/sdcard/photo.jpeg"))
        assertTrue(upSwiperIsImage("https://cdn.test/a.webp#frag"))
        assertFalse(upSwiperIsImage("https://cdn.test/cover?ext=.png"))
        assertFalse(upSwiperIsImage("第一页"))
        assertFalse(upSwiperIsImage(""))
    }

    @Test
    fun videoProbeKeepsTheQueryStringLikeUpstream() {
        assertTrue(upSwiperIsVideo("https://cdn.test/clip.mp4"))
        assertTrue(upSwiperIsVideo("https://cdn.test/live.m3u8?token=abc"))
        assertTrue(upSwiperIsVideo("https://cdn.test/stream?src=.mov"))
        assertFalse(upSwiperIsVideo("https://cdn.test/a.png"))
    }

    @Test
    fun sourceReadsStringsDirectlyAndObjectsThroughKeyName() {
        assertEquals("a.png", upSwiperSource("a.png", "url"))
        assertEquals("a.png", upSwiperSource(mapOf("url" to "a.png"), "url"))
        assertEquals("b.png", upSwiperSource(mapOf("src" to "b.png"), "src"))
        assertEquals("", upSwiperSource(mapOf("url" to "a.png"), ""))
        assertEquals("", upSwiperSource(mapOf("other" to "a.png"), "url"))
        assertEquals("", upSwiperSource(emptyMap<String, UPRawValue>(), "url"))
        assertEquals("", upSwiperSource(null, "url"))
    }

    @Test
    fun itemTypeFollowsUpstreamGetItemType() {
        assertEquals("image", upSwiperItemType("a.png", "url"))
        assertEquals("video", upSwiperItemType("a.mp4", "url"))
        assertEquals("image", upSwiperItemType("plain-label", "url"))
        assertEquals("video", upSwiperItemType(mapOf("url" to "a.mp4"), "url"))
        assertEquals("image", upSwiperItemType(mapOf("url" to "a.mp4", "type" to "image"), "url"))
        assertEquals("video", upSwiperItemType(mapOf("url" to "a.png", "type" to "video"), "url"))
        assertEquals("image", upSwiperItemType(mapOf("url" to "a.png", "type" to "unknown"), "url"))
        assertEquals("", upSwiperItemType(mapOf("url" to "a.png"), ""))
        assertEquals("", upSwiperItemType(null, "url"))
    }

    @Test
    fun renderKindKeepsPlainLabelsAsTextAndUrlsAsImages() {
        assertEquals("text", upSwiperRenderKind("第一页", "url"))
        assertEquals("text", upSwiperRenderKind("", "url"))
        assertEquals("image", upSwiperRenderKind("https://cdn.test/a.png", "url"))
        assertEquals("image", upSwiperRenderKind("https://cdn.test/cover", "url"))
        assertEquals("image", upSwiperRenderKind("/sdcard/a.bin", "url"))
        assertEquals("image", upSwiperRenderKind(mapOf("url" to "banner", "type" to "image"), "url"))
        assertEquals("video", upSwiperRenderKind(mapOf("url" to "a.mp4"), "url"))
        assertEquals("text", upSwiperRenderKind(mapOf("url" to "a.png"), ""))
    }

    @Test
    fun titleBarOnlyCoversImageSlidesThatCarryATitle() {
        val titled = mapOf("url" to "https://cdn.test/a.png", "title" to "标题")
        assertEquals("标题", upSwiperTitle(titled))
        assertEquals("", upSwiperTitle("a.png"))
        assertTrue(upSwiperShouldShowTitle(titled, "url", showTitle = true))
        assertFalse(upSwiperShouldShowTitle(titled, "url", showTitle = false))
        assertFalse(upSwiperShouldShowTitle(mapOf("url" to "https://cdn.test/a.png"), "url", showTitle = true))
        assertFalse(upSwiperShouldShowTitle(mapOf("url" to "a.mp4", "title" to "视频"), "url", showTitle = true))
        assertFalse(upSwiperShouldShowTitle("https://cdn.test/a.png", "url", showTitle = true))
    }

    @Test
    fun posterIsReadFromObjectSlidesOnly() {
        assertEquals("p.png", upSwiperPoster(mapOf("url" to "a.mp4", "poster" to "p.png")))
        assertEquals("", upSwiperPoster(mapOf("url" to "a.mp4")))
        assertEquals("", upSwiperPoster("a.mp4"))
    }

    @Test
    fun indicatorIsHiddenWhileLoadingOrShowingTitles() {
        assertTrue(upSwiperShouldShowIndicator(loading = false, indicator = true, showTitle = false))
        assertFalse(upSwiperShouldShowIndicator(loading = true, indicator = true, showTitle = false))
        assertFalse(upSwiperShouldShowIndicator(loading = false, indicator = true, showTitle = true))
        assertFalse(upSwiperShouldShowIndicator(loading = false, indicator = false, showTitle = false))
    }

    @Test
    fun neighbourSlidesShrinkOnlyWhenBothMarginsRevealThem() {
        assertEquals(0.92f, upSwiperItemScale(20f, 20f, isCurrent = false), 0f)
        assertEquals(1f, upSwiperItemScale(20f, 20f, isCurrent = true), 0f)
        assertEquals(1f, upSwiperItemScale(20f, 0f, isCurrent = false), 0f)
        assertEquals(1f, upSwiperItemScale(0f, 20f, isCurrent = false), 0f)
    }

    @Test
    fun displayCountClampsToTheSlideCount() {
        assertEquals(0, upSwiperDisplayCount(3, 0))
        assertEquals(1, upSwiperDisplayCount(1, 3))
        assertEquals(2, upSwiperDisplayCount(2, 3))
        assertEquals(3, upSwiperDisplayCount(9, 3))
        assertEquals(1, upSwiperDisplayCount("not-a-number", 3))
        assertEquals(1, upSwiperDisplayCount(0, 3))
    }

    @Test
    fun stepsWrapOnlyWhenCircularIsSet() {
        assertEquals(1, upSwiperNextIndex(0, 2, circular = false))
        assertEquals(null, upSwiperNextIndex(2, 2, circular = false))
        assertEquals(0, upSwiperNextIndex(2, 2, circular = true))
        assertEquals(null, upSwiperNextIndex(0, 0, circular = true))
        assertEquals(1, upSwiperPreviousIndex(2, 2, circular = false))
        assertEquals(null, upSwiperPreviousIndex(0, 2, circular = false))
        assertEquals(2, upSwiperPreviousIndex(0, 2, circular = true))
        assertEquals(null, upSwiperPreviousIndex(0, 0, circular = true))
    }

    @Test
    fun currentItemIdAddressesSlidesByIdentityAndWinsOverCurrent() {
        val list: List<UPRawValue> = listOf(
            mapOf("id" to "a", "url" to "a.png"),
            mapOf("id" to "b", "url" to "b.png"),
            mapOf("id" to "c", "url" to "c.png"),
        )
        assertEquals(1, upSwiperIndexForItemId(list, "b"))
        assertEquals(-1, upSwiperIndexForItemId(list, "zz"))
        assertEquals(-1, upSwiperIndexForItemId(list, ""))
        assertEquals(2, upSwiperIndexForItemId(listOf("x", "y", "z"), "z"))

        assertEquals(1, upSwiperResolveIndex(list, 0, "b"))
        assertEquals(0, upSwiperResolveIndex(list, 0, ""))
        assertEquals(2, upSwiperResolveIndex(list, 2, ""))
        assertEquals(2, upSwiperResolveIndex(list, 2, "unmatched"))
        assertEquals(0, upSwiperResolveIndex(list, -5, ""))
    }
}
