package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.asFiniteFloatOrNull
import net.lingyun.ultraui.android.core.upIntOrDefault
import kotlin.math.roundToInt

/**
 * Pure helpers for the motion-parity batch: `u-notice-bar`'s marquee and carousel timings,
 * `u-collapse-item`'s expand animation and `u-sticky`'s stacking. Each mirrors one upstream
 * computed value so the composables stay declarative and the arithmetic stays testable.
 */

/** `zIndex.sticky` from `libs/config/zIndex.js`. */
internal const val UPStickyDefaultZIndex: Float = 970f

/** `uZindex() { return this.zIndex ? this.zIndex : zIndex.sticky }`. */
internal fun upStickyZIndex(zIndex: UPRawValue): Float =
    zIndex.asFiniteFloatOrNull()?.takeIf { it != 0f } ?: UPStickyDefaultZIndex

/**
 * `<swiper :interval="duration">` in `u-column-notice`: how long each message stays before
 * the carousel advances. Values at or below zero would spin the loop, so they are clamped.
 */
internal fun upNoticeIntervalMillis(duration: UPRawValue): Long =
    duration.upIntOrDefault(2000).toLong().coerceAtLeast(1L)

/**
 * `t = s / v`: the marquee moves at `speed` px per second, so any sweep of `distancePx`
 * lasts `distancePx / speed * 1000` ms. Keeping this per-distance (rather than per-loop)
 * means the first partial sweep runs at the same speed as the full ones.
 */
internal fun upNoticeMarqueeSweepMillis(distancePx: Float, speed: UPRawValue): Int {
    if (distancePx <= 0f) return 0
    val pxPerSecond = speed.asFiniteFloatOrNull()?.takeIf { it > 0f } ?: 80f
    return (distancePx / pxPerSecond * 1000f).roundToInt().coerceAtLeast(1)
}

/**
 * `loopAnimation`: a full loop covers `boxWidth + textWidth`, because the text enters from
 * the right edge and leaves past the left one. Returns 0 when there is nothing to measure.
 */
internal fun upNoticeMarqueeDurationMillis(boxWidthPx: Float, textWidthPx: Float, speed: UPRawValue): Int {
    if (boxWidthPx <= 0f || textWidthPx <= 0f) return 0
    return upNoticeMarqueeSweepMillis(boxWidthPx + textWidthPx, speed)
}

/** `<swiper circular>` wraps around; `direction="row"` with `step` uses the same carousel. */
internal fun upNoticeNextIndex(index: Int, count: Int): Int =
    if (count <= 0) 0 else (index + 1) % count

/** The same carousel, dragged the other way. */
internal fun upNoticePreviousIndex(index: Int, count: Int): Int =
    if (count <= 0) 0 else (index - 1 + count) % count

/** Dragging the vertical carousel is only allowed while `disableTouch` is false. */
internal fun upNoticeTouchEnabled(disableTouch: Boolean, count: Int): Boolean =
    !disableTouch && count > 1

/** `animation.height(height).step({ duration })` on the collapse panel. */
internal fun upCollapseDurationMillis(duration: UPRawValue): Int =
    duration.upIntOrDefault(300).coerceAtLeast(0)

/**
 * `mounted() { sizeLocal = height !== '' ? height : size }`. Upstream lets `height`
 * override `size` for the track thickness, regardless of orientation.
 */
internal fun upSliderTrackThickness(height: UPRawValue, size: UPRawValue): Float {
    val fromHeight = height.asFiniteFloatOrNull() ?: upSliderPxOrNull(height)
    if (fromHeight != null && fromHeight > 0f) return fromHeight
    return (size.asFiniteFloatOrNull() ?: upSliderPxOrNull(size) ?: 2f).coerceAtLeast(1f)
}

/**
 * `innerStyleCpu.height`: the track row is as tall as the thumb, plus 24px of room for the
 * two value labels a range slider prints above it.
 */
internal fun upSliderInnerHeightDp(blockSize: UPRawValue, isRange: Boolean, showValue: Boolean): Float {
    val block = (blockSize.asFiniteFloatOrNull() ?: upSliderPxOrNull(blockSize) ?: 18f).coerceAtLeast(1f)
    return if (isRange && showValue) block + 24f else block
}

/** `length` defaults to `auto`, which leaves the axis to the parent's measurement. */
internal fun upSliderLengthDp(length: UPRawValue): Float? {
    val text = length.upStringValueOrEmpty().trim()
    if (text.isEmpty() || text.equals("auto", ignoreCase = true)) return null
    return (length.asFiniteFloatOrNull() ?: upSliderPxOrNull(length))?.takeIf { it > 0f }
}

/** `<template v-if="!useNative || isRange">`: a range slider never uses the native one. */
internal fun upSliderUsesNative(useNative: Boolean, isRange: Boolean): Boolean = useNative && !isRange

/** `getPx` accepts a bare number or a `px` string; `rpx` is not used by these fields. */
private fun upSliderPxOrNull(value: UPRawValue): Float? {
    val text = value.upStringValueOrEmpty().trim().lowercase()
    if (!text.endsWith("px")) return null
    return text.removeSuffix("px").trim().toFloatOrNull()
}
