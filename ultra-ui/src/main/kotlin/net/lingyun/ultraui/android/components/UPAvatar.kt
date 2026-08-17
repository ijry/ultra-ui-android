package net.lingyun.ultraui.android.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.lingyun.ultraui.android.core.UPColor
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPImageLoaders
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.core.asFiniteFloatOrNull
import net.lingyun.ultraui.android.core.upClickable
import net.lingyun.ultraui.android.core.upSafeEnum
import net.lingyun.ultraui.android.core.upSize
import net.lingyun.ultraui.android.core.upTestTag

private const val AvatarComponentName = "UPAvatar"
private val AvatarColors = listOf(
    "#ffb34b", "#f2bba9", "#f7a196", "#f18080", "#88a867", "#bfbf39", "#89c152", "#94d554",
    "#f19ec2", "#afaae4", "#e1b0df", "#c38cc1", "#72dcdc", "#9acdcb", "#77b1cc", "#448aca",
    "#86cefa", "#98d1ee", "#73d1f1", "#80a7dc",
)

/** Native Compose counterpart of uview-plus `u-avatar`. */
@Composable
public fun UPAvatar(
    props: UPAvatarProps = UPAvatarProps(),
    modifier: Modifier = Modifier,
    onClick: ((UPRawValue) -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    val shapeName = upSafeEnum(props.shape, setOf("circle", "square"), "circle", diagnostics, AvatarComponentName, "shape")
    val shape = if (shapeName == "circle") androidx.compose.foundation.shape.RoundedCornerShape(percent = 50) else androidx.compose.foundation.shape.RoundedCornerShape(4.dp)
    val size = upRawDp(props.size, 40.dp).coerceAtLeast(0.dp)
    val fontSize = upRawDp(props.fontSize, 18.dp).coerceAtLeast(0.dp)
    val index = props.colorIndex.asFiniteFloatOrNull()?.toInt()?.coerceIn(0, AvatarColors.lastIndex) ?: 0
    val background = if (props.randomBgColor && (props.text.toString().isNotEmpty() || props.icon.isNotEmpty())) {
        UPColor.parse(AvatarColors[index], UPTheme.Light)
    } else UPColor.parse(props.bgColor, UPTheme.Light)
    val src = props.src.ifEmpty { props.defaultUrl }
    val image by produceState<ImageBitmap?>(initialValue = null, key1 = src) {
        value = if (src.isEmpty()) null else runCatching { UPImageLoaders.Android.load(src) }.getOrNull()
    }
    val style = rememberUPResolvedStyle(props.customStyle, diagnostics, AvatarComponentName)
    val hasText = props.text.toString().isNotEmpty()
    val root = modifier
        .upSize(size, size)
        .clip(shape)
        .background(if (props.icon.isNotEmpty() || hasText || image == null) background else androidx.compose.ui.graphics.Color.Transparent, shape)
        .applyUPResolvedStyle(style)
        .upTestTag("avatar")
        .upClickable(enabled = onClick != null, onClick = { onClick?.invoke(props.name) })

    Box(root, contentAlignment = Alignment.Center) {
        // uview-plus gives icon and text priority over the image fallback.
        when {
            props.icon.isNotEmpty() -> UPIcon(UPIconProps(name = props.icon, size = fontSize.value, color = props.color), diagnostics = diagnostics)
            hasText -> BasicText(
                props.text.toString(),
                style = TextStyle(color = UPColor.parse(props.color, UPTheme.Primary), fontSize = fontSize.value.sp),
            )
            image != null -> Image(
                bitmap = image!!,
                contentDescription = null,
                contentScale = props.mode.toAvatarContentScale(),
                modifier = Modifier.fillMaxSize(),
            )
            else -> BasicText("?", style = TextStyle(color = UPColor.parse(props.color, androidx.compose.ui.graphics.Color.White), fontSize = fontSize.value.sp))
        }
    }
}

private fun String.toAvatarContentScale(): ContentScale = when (lowercase()) {
    "aspectfit" -> ContentScale.Fit
    "aspectfill" -> ContentScale.Crop
    "center" -> ContentScale.None
    else -> ContentScale.FillBounds
}
