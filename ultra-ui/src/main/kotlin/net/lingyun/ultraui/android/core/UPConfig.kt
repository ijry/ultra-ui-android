package net.lingyun.ultraui.android.core

/** Immutable uview-plus defaults used by every first-milestone Props contract. */
public object UPConfig {
    public val button: UPButtonDefaults = UPButtonDefaults()
    public val icon: UPIconDefaults = UPIconDefaults()
    public val loadingIcon: UPLoadingIconDefaults = UPLoadingIconDefaults()
    public val overlay: UPOverlayDefaults = UPOverlayDefaults()
    public val popup: UPPopupDefaults = UPPopupDefaults()
    public val toast: UPToastDefaults = UPToastDefaults()
    public val tag: UPTagDefaults = UPTagDefaults()
    public val modal: UPModalDefaults = UPModalDefaults()
    public val cell: UPCellDefaults = UPCellDefaults()
    public val cellGroup: UPCellGroupDefaults = UPCellGroupDefaults()
}

public data class UPButtonDefaults(
    val hairline: Boolean = false,
    val type: String = "info",
    val size: String = "normal",
    val shape: String = "square",
    val plain: Boolean = false,
    val disabled: Boolean = false,
    val loading: Boolean = false,
    val loadingText: String = "",
    val loadingMode: String = "spinner",
    val loadingSize: UPRawValue = 15,
    val openType: String = "",
    val formType: String = "",
    val appParameter: String = "",
    val hoverStopPropagation: Boolean = true,
    val lang: String = "en",
    val sessionFrom: String = "",
    val sendMessageTitle: String = "",
    val sendMessagePath: String = "",
    val sendMessageImg: String = "",
    val showMessageCard: Boolean = false,
    val dataName: String = "",
    val throttleTime: UPRawValue = 0,
    val hoverStartTime: UPRawValue = 0,
    val hoverStayTime: UPRawValue = 200,
    val text: String = "",
    val icon: String = "",
    val iconColor: String = "",
    val color: String = "",
    val stop: Boolean = true,
)

public data class UPIconDefaults(
    val name: String = "",
    val color: String = "#606266",
    val size: UPRawValue = "16px",
    val bold: Boolean = false,
    val index: UPRawValue = "",
    val hoverClass: String = "",
    val customPrefix: String = "uicon",
    val label: String = "",
    val labelPos: String = "right",
    val labelSize: UPRawValue = "15px",
    val labelColor: String = "#606266",
    val space: UPRawValue = "3px",
    val imgMode: String = "",
    val width: UPRawValue = "",
    val height: UPRawValue = "",
    val top: UPRawValue = 0,
    val stop: Boolean = false,
)

public data class UPLoadingIconDefaults(
    val show: Boolean = true,
    val color: String = "#909399",
    val textColor: String = "#909399",
    val vertical: Boolean = false,
    val mode: String = "spinner",
    val size: UPRawValue = 24,
    val textSize: UPRawValue = 15,
    val text: String = "",
    val timingFunction: String = "ease-in-out",
    val duration: UPRawValue = 1200,
    val inactiveColor: String = "",
)

public data class UPOverlayDefaults(
    val show: Boolean = false,
    val zIndex: Int = 10070,
    val duration: UPRawValue = 300,
    val opacity: Float = 0.5f,
)

public data class UPPopupDefaults(
    val show: Boolean = false,
    val overlay: Boolean = true,
    val mode: String = "bottom",
    val duration: UPRawValue = 300,
    val closeable: Boolean = false,
    val overlayStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
    val closeOnClickOverlay: Boolean = true,
    val zIndex: Int = 10075,
    val safeAreaInsetBottom: Boolean = true,
    val safeAreaInsetTop: Boolean = false,
    val closeIconPos: String = "top-right",
    val round: UPRawValue = "20px",
    val zoom: Boolean = true,
    val bgColor: String = "",
    val overlayOpacity: Float = 0.5f,
    val pageInline: Boolean = false,
    val touchable: Boolean = false,
    val minHeight: UPRawValue = "200px",
    val maxHeight: UPRawValue = "600px",
)

public data class UPToastDefaults(
    val zIndex: Int = 10090,
    val loading: Boolean = false,
    val message: String = "",
    val icon: String = "",
    val type: String = "",
    val loadingMode: String = "",
    val show: UPRawValue = "",
    val overlay: Boolean = false,
    val position: String = "center",
    val params: Map<String, UPRawValue> = emptyMap(),
    val duration: UPRawValue = 2000,
    val isTab: Boolean = false,
    val url: String = "",
    val callback: UPRawValue = null,
    val back: Boolean = false,
)

public data class UPTagDefaults(
    val type: String = "primary",
    val disabled: Boolean = false,
    val size: String = "medium",
    val shape: String = "square",
    val text: String = "",
    val bgColor: String = "",
    val color: String = "",
    val borderColor: String = "",
    val closeColor: String = "#C6C7CB",
    val name: UPRawValue = "",
    val plainFill: Boolean = false,
    val plain: Boolean = false,
    val closable: Boolean = false,
    val show: Boolean = true,
    val icon: String = "",
    val iconColor: String = "",
    val textSize: UPRawValue = "",
    val height: UPRawValue = "",
    val padding: UPRawValue = "",
    val borderRadius: UPRawValue = "",
    val autoBgColor: UPRawValue = 0,
)

public data class UPModalDefaults(
    val show: Boolean = false,
    val title: String = "",
    val content: String = "",
    val confirmText: String = "确认",
    val cancelText: String = "取消",
    val showConfirmButton: Boolean = true,
    val showCancelButton: Boolean = false,
    val confirmColor: String = "#2979ff",
    val cancelColor: String = "#606266",
    val buttonReverse: Boolean = false,
    val zoom: Boolean = true,
    val asyncClose: Boolean = false,
    val closeOnClickOverlay: Boolean = false,
    val negativeTop: UPRawValue = 0,
    val width: UPRawValue = "650rpx",
    val confirmButtonShape: String = "",
    val duration: UPRawValue = 400,
    val contentTextAlign: String = "left",
    val asyncCloseTip: String = "操作中...",
    val asyncCancelClose: Boolean = false,
    val contentStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)

public data class UPCellDefaults(
    val customClass: String = "",
    val title: UPRawValue = "",
    val label: UPRawValue = "",
    val value: UPRawValue = "",
    val icon: String = "",
    val disabled: Boolean = false,
    val border: Boolean = true,
    val center: Boolean = false,
    val url: String = "",
    val linkType: String = "navigateTo",
    val clickable: Boolean = false,
    val isLink: Boolean = false,
    val required: Boolean = false,
    val arrowDirection: String = "",
    val iconStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
    val rightIconStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
    val rightIcon: String = "arrow-right",
    val titleStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
    val size: String = "",
    val stop: Boolean = true,
    val name: UPRawValue = "",
)

public data class UPCellGroupDefaults(
    val title: String = "",
    val border: Boolean = true,
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)
