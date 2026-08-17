package net.lingyun.ultraui.android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.asFiniteFloatOrNull
import net.lingyun.ultraui.android.core.upSafeEnum
import net.lingyun.ultraui.android.core.upClickable
import net.lingyun.ultraui.android.core.upTestTag
import kotlin.math.roundToInt

private const val AvatarGroupComponentName = "UPAvatarGroup"

/** Native Compose counterpart of uview-plus `u-avatar-group`. */
@Composable
public fun UPAvatarGroup(
    props: UPAvatarGroupProps = UPAvatarGroupProps(),
    modifier: Modifier = Modifier,
    onShowMore: (() -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    val shape = upSafeEnum(props.shape, setOf("circle", "square"), "circle", diagnostics, AvatarGroupComponentName, "shape")
    val size = upRawDp(props.size, 40.dp).coerceAtLeast(0.dp)
    val gap = (props.gap.asFiniteFloatOrNull() ?: 0.5f).coerceIn(0f, 1f)
    val maxCount = (props.maxCount.asFiniteFloatOrNull()?.toInt() ?: 5).coerceAtLeast(0)
    val shown = props.urls.take(maxCount)
    val hasMore = props.urls.size > shown.size || (props.extraValue.asFiniteFloatOrNull() ?: 0f) > 0f
    val extra = props.extraValue.asFiniteFloatOrNull()?.toInt()?.takeIf { it > 0 } ?: (props.urls.size - shown.size)
    Row(modifier = modifier.upTestTag("avatar-group"), verticalAlignment = Alignment.CenterVertically) {
        shown.forEachIndexed { index, raw ->
            val source = avatarGroupSource(raw, props.keyName)
            Box(
                modifier = Modifier
                    .then(if (index == 0) Modifier else Modifier.offset { IntOffset((-size.toPxSafe() * gap).roundToInt(), 0) })
                    .size(size)
                    .upTestTag("avatar-group-item-$index"),
            ) {
                UPAvatar(
                    props = UPAvatarProps(src = source, shape = shape, mode = props.mode, size = size.value),
                    diagnostics = diagnostics,
                )
                if (props.showMore && hasMore && index == shown.lastIndex) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.35f), androidx.compose.foundation.shape.RoundedCornerShape(percent = if (shape == "circle") 50 else 0))
                            .upTestTag("avatar-more")
                            .upClickable(enabled = true, onClick = { onShowMore?.invoke() }),
                        contentAlignment = Alignment.Center,
                    ) {
                        BasicText(
                            text = "+$extra",
                            style = TextStyle(color = Color.White, fontSize = (size.value * 0.4f).sp),
                        )
                    }
                }
            }
        }
    }
}

private fun avatarGroupSource(raw: UPRawValue, keyName: String): String = when (raw) {
    is String -> raw
    is Map<*, *> -> {
        val key = keyName.takeIf { it.isNotEmpty() } ?: "url"
        (raw[key] ?: raw[keyName])?.toString().orEmpty()
    }
    else -> raw?.toString().orEmpty()
}

private fun androidx.compose.ui.unit.Dp.toPxSafe(): Float = value
