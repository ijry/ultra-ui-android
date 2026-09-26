package net.lingyun.ultraui.android.core

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Cross-platform glass options. iOS renders real Liquid Glass; Android renders
 * the dependency-free approximation below; the other platforms accept it as a
 * no-op. Field names/defaults are identical across all 8 platforms.
 */
public data class UPFlexGlass(
    val enabled: Boolean = false,
    val variant: String = "regular",
    val tint: String? = null,
    val interactive: Boolean = false,
    val cornerRadius: UPRawValue = null,
)

internal fun upGlassBackgroundAlpha(variant: String): Float =
    if (variant == "clear") 0.35f else 0.6f

/**
 * Approximate frosted-glass surface: translucent tint clipped to [cornerRadius]
 * plus a hairline highlight. This is NOT a true backdrop blur (that needs an
 * API 31+ RenderEffect on a captured backdrop or a blur library — deferred).
 */
public fun Modifier.upGlass(glass: UPFlexGlass, cornerRadius: Dp): Modifier {
    if (!glass.enabled) return this
    val shape = RoundedCornerShape(cornerRadius)
    val tint = UPColor.parseOrNull(glass.tint) ?: Color.White
    return this
        .clip(shape)
        .background(tint.copy(alpha = upGlassBackgroundAlpha(glass.variant)))
        .border(1.dp, Color.White.copy(alpha = 0.4f), shape)
}
