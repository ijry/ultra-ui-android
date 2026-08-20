package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.UPStyleInput

public data class UPSwiperProps(
    val list: List<UPRawValue> = emptyList(),
    val indicator: Boolean = false,
    val indicatorActiveColor: String = "#FFFFFF",
    val indicatorInactiveColor: String = "rgba(255, 255, 255, 0.35)",
    val indicatorStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
    val indicatorMode: String = "line",
    val autoplay: Boolean = true,
    val current: UPRawValue = 0,
    val currentItemId: UPRawValue = "",
    val interval: UPRawValue = 3000,
    val duration: UPRawValue = 300,
    val circular: Boolean = false,
    val vertical: Boolean = false,
    val previousMargin: UPRawValue = 0,
    val nextMargin: UPRawValue = 0,
    val acceleration: Boolean = false,
    val displayMultipleItems: UPRawValue = 1,
    val easingFunction: String = "default",
    val keyName: String = "url",
    val imgMode: String = "aspectFill",
    val height: UPRawValue = 130,
    val bgColor: String = "#f3f4f6",
    val radius: UPRawValue = 4,
    val loading: Boolean = false,
    val showTitle: Boolean = false,
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)

public data class UPSwiperIndicatorProps(
    val length: UPRawValue = 0,
    val current: UPRawValue = 0,
    val indicatorActiveColor: String = "",
    val indicatorInactiveColor: String = "",
    val indicatorMode: String = "line",
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)

public data class UPSkeletonProps(
    val loading: Boolean = true,
    val animate: Boolean = true,
    val rows: UPRawValue = 0,
    val rowsWidth: UPRawValue = "100%",
    val rowsHeight: UPRawValue = 18,
    val title: Boolean = true,
    val titleWidth: UPRawValue = "50%",
    val titleHeight: UPRawValue = 18,
    val avatar: Boolean = false,
    val avatarSize: UPRawValue = 32,
    val avatarShape: String = "circle",
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)

public data class UPReadMoreProps(
    val showHeight: UPRawValue = 400,
    val toggle: Boolean = false,
    val closeText: String = "展开",
    val openText: String = "收起",
    val color: String = "#2979ff",
    val fontSize: UPRawValue = 14,
    val shadowStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
    val textIndent: UPRawValue = "2em",
    val name: UPRawValue = "",
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
    /** Android controlled aliases retained for generated templates. */
    val modelValue: Boolean? = null,
    val value: Boolean = false,
)

public data class UPColumnNoticeProps(
    val text: List<UPRawValue> = emptyList(),
    val icon: String = "volume",
    val mode: String = "",
    val color: String = "#f9ae3d",
    val bgColor: String = "#fdf6ec",
    val fontSize: UPRawValue = 14,
    val speed: UPRawValue = 80,
    val step: Boolean = false,
    val duration: UPRawValue = 1500,
    val disableTouch: Boolean = true,
    val justifyContent: String = "flex-start",
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)

public data class UPRowNoticeProps(
    val text: UPRawValue = "",
    val icon: String = "volume",
    val mode: String = "",
    val color: String = "#f9ae3d",
    val bgColor: String = "#fdf6ec",
    val fontSize: UPRawValue = 14,
    val speed: UPRawValue = 80,
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)

public data class UPCountToProps(
    val startVal: UPRawValue = 0,
    val endVal: UPRawValue = 0,
    val duration: UPRawValue = 2000,
    val autoplay: Boolean = true,
    val decimals: UPRawValue = 0,
    val useEasing: Boolean = true,
    val decimal: String = ".",
    val color: String = "#606266",
    val fontSize: UPRawValue = 22,
    val bold: Boolean = false,
    val separator: String = "",
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)

public data class UPCountDownProps(
    val time: UPRawValue = 0,
    val format: String = "HH:mm:ss",
    val autoStart: Boolean = true,
    val millisecond: Boolean = false,
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)

public data class UPCountDownTime(
    val days: Int,
    val hours: Int,
    val minutes: Int,
    val seconds: Int,
    val milliseconds: Int,
)
