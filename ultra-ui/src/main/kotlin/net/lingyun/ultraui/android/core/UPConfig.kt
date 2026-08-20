package net.lingyun.ultraui.android.core

/** Immutable uview-plus defaults used by the generated Android Props contracts. */
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
    public val badge: UPBadgeDefaults = UPBadgeDefaults()
    public val divider: UPDividerDefaults = UPDividerDefaults()
    public val gap: UPGapDefaults = UPGapDefaults()
    public val line: UPLineDefaults = UPLineDefaults()
    public val link: UPLinkDefaults = UPLinkDefaults()
    public val text: UPTextDefaults = UPTextDefaults()
    public val title: UPTitleDefaults = UPTitleDefaults()
    public val image: UPImageDefaults = UPImageDefaults()
    public val avatar: UPAvatarDefaults = UPAvatarDefaults()
    public val avatarGroup: UPAvatarGroupDefaults = UPAvatarGroupDefaults()
    public val empty: UPEmptyDefaults = UPEmptyDefaults()
    public val loadingPage: UPLoadingPageDefaults = UPLoadingPageDefaults()
    public val loadmore: UPLoadmoreDefaults = UPLoadmoreDefaults()
    public val input: UPInputDefaults = UPInputDefaults()
    public val textarea: UPTextareaDefaults = UPTextareaDefaults()
    public val search: UPSearchDefaults = UPSearchDefaults()
    public val codeInput: UPCodeInputDefaults = UPCodeInputDefaults()
    public val switch: UPSwitchDefaults = UPSwitchDefaults()
    public val rate: UPRateDefaults = UPRateDefaults()
    public val numberBox: UPNumberBoxDefaults = UPNumberBoxDefaults()
    public val checkbox: UPCheckboxDefaults = UPCheckboxDefaults()
    public val checkboxGroup: UPCheckboxGroupDefaults = UPCheckboxGroupDefaults()
    public val radio: UPRadioDefaults = UPRadioDefaults()
    public val radioGroup: UPRadioGroupDefaults = UPRadioGroupDefaults()
    public val row: UPRowDefaults = UPRowDefaults()
    public val col: UPColDefaults = UPColDefaults()
    public val grid: UPGridDefaults = UPGridDefaults()
    public val gridItem: UPGridItemDefaults = UPGridItemDefaults()
    public val lineProgress: UPLineProgressDefaults = UPLineProgressDefaults()
    public val circleProgress: UPCircleProgressDefaults = UPCircleProgressDefaults()
    public val alert: UPAlertDefaults = UPAlertDefaults()
    public val actionSheet: UPActionSheetDefaults = UPActionSheetDefaults()
    public val notify: UPNotifyDefaults = UPNotifyDefaults()
    public val backTop: UPBackTopDefaults = UPBackTopDefaults()
    public val card: UPCardDefaults = UPCardDefaults()
    public val collapse: UPCollapseDefaults = UPCollapseDefaults()
    public val collapseItem: UPCollapseItemDefaults = UPCollapseItemDefaults()
    /** Defaults shared by the Batch 9B native wrappers. */
    public val batch9b: UPBatch9BDefaults = UPBatch9BDefaults()
}

public data class UPBatch9BDefaults(
    val navbarSafeAreaInsetTop: Boolean = true,
    val navbarHeight: UPRawValue = "44px",
    val tabsDuration: UPRawValue = 300,
    val tabsCurrent: UPRawValue = 0,
    val stepsDirection: String = "row",
    val listScrollable: Boolean = true,
    val popoverPlacement: String = "top",
    val tooltipTriggerMode: String = "longpress",
    val swiperAutoplay: Boolean = true,
    val skeletonLoading: Boolean = true,
    val countToDuration: UPRawValue = 2000,
    val countDownFormat: String = "HH:mm:ss",
    val pickerPopupMode: String = "bottom",
    val paginationPageSize: UPRawValue = 10,
    val selectKeyName: String = "id",
)

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
)

public data class UPBadgeDefaults(
    val isDot: Boolean = false,
    val value: UPRawValue = "",
    val show: Boolean = true,
    val max: Int = 999,
    val type: String = "error",
    val showZero: Boolean = false,
    val bgColor: String? = null,
    val color: String? = null,
    val shape: String = "circle",
    val numberType: String = "overflow",
    val offset: List<UPRawValue> = emptyList(),
    val inverted: Boolean = false,
    val absolute: Boolean = false,
)

public data class UPDividerDefaults(
    val dashed: Boolean = false,
    val hairline: Boolean = true,
    val dot: Boolean = false,
    val textPosition: String = "center",
    val text: String = "",
    val textSize: UPRawValue = 14,
    val textColor: String = "#909399",
    val lineColor: String = "#dcdfe6",
)

public data class UPGapDefaults(
    val bgColor: String = "transparent",
    val height: UPRawValue = 20,
    val marginTop: UPRawValue = 0,
    val marginBottom: UPRawValue = 0,
)

public data class UPLineDefaults(
    val color: String = "#d6d7d9",
    val length: UPRawValue = "100%",
    val direction: String = "row",
    val hairline: Boolean = true,
    val margin: UPRawValue = 0,
    val dashed: Boolean = false,
)

public data class UPLinkDefaults(
    val color: String = "#2979ff",
    val fontSize: UPRawValue = 15,
    val underLine: Boolean = false,
    val href: String = "",
    val mpTips: String = "链接已复制",
    val lineColor: String = "",
    val text: String = "",
)

public data class UPTextDefaults(
    val type: String = "",
    val show: Boolean = true,
    val text: UPRawValue = "",
    val prefixIcon: String = "",
    val suffixIcon: String = "",
    val mode: String = "",
    val href: String = "",
    val format: UPRawValue = null,
    val call: Boolean = false,
    val openType: String = "",
    val bold: Boolean = false,
    val block: Boolean = false,
    val lines: UPRawValue = 1,
    val color: String = "",
    val size: UPRawValue = 15,
    val iconStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
    val decoration: String = "none",
    val margin: UPRawValue = 0,
    val lineHeight: UPRawValue = "normal",
    val align: String = "left",
    val wordWrap: String = "normal",
    val flex1: Boolean = false,
)

public data class UPTitleDefaults(
    val customStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
)

public data class UPImageDefaults(
    val src: String = "",
    val mode: String = "aspectFill",
    val width: UPRawValue = 300,
    val height: UPRawValue = 225,
    val shape: String = "square",
    val radius: UPRawValue = 0,
    val lazyLoad: Boolean = true,
    val showMenuByLongpress: Boolean = false,
    val loadingIcon: String = "photo",
    val errorIcon: String = "error-circle",
    val showLoading: Boolean = true,
    val showError: Boolean = true,
    val fade: Boolean = true,
    val webp: Boolean = false,
    val duration: UPRawValue = 500,
    val bgColor: String = "#f3f4f6",
)

public data class UPAvatarDefaults(
    val src: String = "",
    val shape: String = "circle",
    val size: UPRawValue = 40,
    val mode: String = "scaleToFill",
    val text: UPRawValue = "",
    val bgColor: String = "#c0c4cc",
    val color: String = "#ffffff",
    val fontSize: UPRawValue = 18,
    val icon: String = "",
    val mpAvatar: Boolean = false,
    val randomBgColor: Boolean = false,
    val defaultUrl: String = "",
    val colorIndex: UPRawValue = 0,
    val name: UPRawValue = "",
)

public data class UPAvatarGroupDefaults(
    val urls: List<UPRawValue> = emptyList(),
    val maxCount: Int = 5,
    val shape: String = "circle",
    val mode: String = "scaleToFill",
    val showMore: Boolean = true,
    val size: UPRawValue = 40,
    val keyName: String = "",
    val gap: UPRawValue = 0.5,
    val extraValue: UPRawValue = 0,
)

public data class UPEmptyDefaults(
    val icon: String = "",
    val text: String = "",
    val textColor: String = "#c0c4cc",
    val textSize: UPRawValue = 14,
    val iconColor: String = "#c0c4cc",
    val iconSize: UPRawValue = 90,
    val mode: String = "data",
    val width: UPRawValue = 160,
    val height: UPRawValue = 160,
    val show: Boolean = true,
    val marginTop: UPRawValue = 0,
)

public data class UPLoadingPageDefaults(
    val loadingText: String = "正在加载",
    val image: String = "",
    val loadingMode: String = "circle",
    val loading: Boolean = false,
    val bgColor: String = "",
    val color: String = "#C8C8C8",
    val fontSize: UPRawValue = 19,
    // uview-plus props.js accidentally uses fontSize as iconSize's default.
    val iconSize: UPRawValue = 19,
    val loadingColor: String = "#C8C8C8",
    val zIndex: Int = 10,
)

public data class UPLoadmoreDefaults(
    val status: String = "loadmore",
    val bgColor: String = "transparent",
    val icon: Boolean = true,
    val fontSize: UPRawValue = 14,
    val iconSize: UPRawValue = 17,
    val color: String = "#606266",
    val loadingIcon: String = "spinner",
    val loadmoreText: String = "加载更多",
    val loadingText: String = "正在加载...",
    val nomoreText: String = "没有更多了",
    val isDot: Boolean = false,
    val iconColor: String = "#b7b7b7",
    val marginTop: UPRawValue = 10,
    val marginBottom: UPRawValue = 10,
    val height: UPRawValue = "auto",
    val line: Boolean = false,
    val lineColor: String = "#E6E8EB",
    val dashed: Boolean = false,
)

public data class UPInputDefaults(
    val modelValue: UPRawValue? = null,
    val value: UPRawValue = "",
    val type: String = "text",
    val fixed: Boolean = false,
    val disabled: Boolean = false,
    val disabledColor: String = "#c8c9cc",
    val clearable: Boolean = false,
    val onlyClearableOnFocused: Boolean = false,
    val password: Boolean = false,
    val maxlength: Int = 140,
    val placeholder: String = "请输入内容",
    val placeholderClass: String = "",
    val placeholderStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
    val showWordLimit: Boolean = false,
    val confirmType: String = "done",
    val confirmHold: Boolean = false,
    val holdKeyboard: Boolean = false,
    val focus: Boolean = false,
    val autoBlur: Boolean = false,
    val disableDefaultPadding: Boolean = false,
    val cursor: UPRawValue = -1,
    val cursorSpacing: UPRawValue = 0,
    val selectionStart: UPRawValue = -1,
    val selectionEnd: UPRawValue = -1,
    val adjustPosition: Boolean = true,
    val inputAlign: String = "left",
    val fontSize: UPRawValue = 15,
    val color: String = "#303133",
    val prefixIcon: String = "",
    val prefixIconStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
    val suffixIcon: String = "",
    val suffixIconStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
    val border: String = "surround",
    val readonly: Boolean = false,
    val shape: String = "square",
    val formatter: UPRawValue = null,
    val ignoreCompositionEvent: Boolean = false,
    val cursorColor: String = "#53c21d",
    val passwordVisibilityToggle: Boolean = false,
)

public data class UPTextareaDefaults(
    val value: UPRawValue = "",
    val modelValue: UPRawValue? = null,
    val placeholder: String = "请输入内容",
    val placeholderClass: String = "",
    val placeholderStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
    val height: UPRawValue = 70,
    val confirmType: String = "return",
    val disabled: Boolean = false,
    val count: Boolean = false,
    val focus: Boolean = false,
    val autoHeight: Boolean = false,
    val fixed: Boolean = false,
    val cursorSpacing: UPRawValue = 0,
    val cursor: UPRawValue = -1,
    val showConfirmBar: Boolean = true,
    val selectionStart: UPRawValue = -1,
    val selectionEnd: UPRawValue = -1,
    val adjustPosition: Boolean = true,
    val disableDefaultPadding: Boolean = false,
    val holdKeyboard: Boolean = false,
    val maxlength: Int = 140,
    val border: String = "surround",
    val formatter: UPRawValue = null,
    val ignoreCompositionEvent: Boolean = false,
)

public data class UPSearchDefaults(
    val modelValue: UPRawValue? = null,
    val value: UPRawValue = "",
    val shape: String = "round",
    val bgColor: String = "",
    val placeholder: String = "请输入关键词",
    val clearabled: Boolean = true,
    val onlyClearableOnFocused: Boolean = false,
    val focus: Boolean = false,
    val showAction: Boolean = true,
    val actionText: String = "搜索",
    val label: UPRawValue = null,
    val inputAlign: String = "left",
    val disabled: Boolean = false,
    val animation: Boolean = false,
    val borderColor: String = "transparent",
    val searchIconColor: String = "#909399",
    val searchIconSize: UPRawValue = 22,
    val color: String = "",
    val placeholderColor: String = "",
    val searchIcon: String = "search",
    val margin: UPRawValue = "0",
    val iconPosition: String = "left",
    val maxlength: UPRawValue = "-1",
    val height: UPRawValue = 32,
    val adjustPosition: Boolean = true,
    val autoBlur: Boolean = true,
)

public data class UPCodeInputDefaults(
    val adjustPosition: Boolean = true,
    val maxlength: Int = 6,
    val dot: Boolean = false,
    val mode: String = "box",
    val hairline: Boolean = false,
    val space: UPRawValue = 10,
    val modelValue: UPRawValue? = null,
    val value: UPRawValue = "",
    val focus: Boolean = false,
    val bold: Boolean = false,
    val color: String = "#606266",
    val fontSize: UPRawValue = 18,
    val size: UPRawValue = 35,
    val disabledKeyboard: Boolean = false,
    val borderColor: String = "#c9cacc",
    val disabledDot: Boolean = true,
)

public data class UPSwitchDefaults(
    val loading: Boolean = false,
    val disabled: Boolean = false,
    val size: UPRawValue = 25,
    val activeColor: String = "#2979ff",
    val inactiveColor: String = "#ffffff",
    val dotActiveColor: String = "#ffffff",
    val dotInactiveColor: String = "#ffffff",
    val modelValue: UPRawValue? = null,
    val value: UPRawValue = false,
    val activeValue: UPRawValue = true,
    val inactiveValue: UPRawValue = false,
    val asyncChange: Boolean = false,
    val space: UPRawValue = 0,
)

public data class UPRateDefaults(
    val modelValue: UPRawValue? = null,
    val value: UPRawValue = 1,
    val count: Int = 5,
    val disabled: Boolean = false,
    val readonly: Boolean = false,
    val size: UPRawValue = 18,
    val inactiveColor: String = "",
    val activeColor: String = "",
    val gutter: UPRawValue = 4,
    val minCount: UPRawValue = 1,
    val allowHalf: Boolean = false,
    val activeIcon: String = "star-fill",
    val inactiveIcon: String = "star",
    val touchable: Boolean = true,
)

public data class UPNumberBoxDefaults(
    val name: UPRawValue = "",
    val value: UPRawValue = 0,
    val modelValue: UPRawValue? = null,
    val min: UPRawValue = 1,
    val max: UPRawValue = 9007199254740991L,
    val step: UPRawValue = 1,
    val integer: Boolean = false,
    val disabled: Boolean = false,
    val disabledInput: Boolean = false,
    val asyncChange: Boolean = false,
    val inputWidth: UPRawValue = 35,
    val showMinus: Boolean = true,
    val showPlus: Boolean = true,
    val decimalLength: UPRawValue = null,
    val longPress: Boolean = true,
    val color: String = "",
    val buttonWidth: UPRawValue = 30,
    val buttonSize: UPRawValue = 30,
    val buttonRadius: UPRawValue = "0px",
    val bgColor: String = "",
    val disabledBgColor: String = "",
    val inputBgColor: String = "",
    val cursorSpacing: UPRawValue = 100,
    val disablePlus: Boolean = false,
    val disableMinus: Boolean = false,
    val iconStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
    val miniMode: Boolean = false,
)

public data class UPCheckboxDefaults(
    val name: UPRawValue = "",
    // uview leaves visual props empty on the leaf so checkbox-group can inherit them.
    val shape: String = "",
    val size: UPRawValue = "",
    val checked: Boolean = false,
    val disabled: UPRawValue = "",
    val activeColor: String = "",
    val inactiveColor: String = "",
    val iconSize: UPRawValue = "",
    val iconColor: String = "",
    val label: UPRawValue = "",
    val labelSize: UPRawValue = "",
    val labelColor: String = "",
    val labelDisabled: UPRawValue = "",
    val usedAlone: Boolean = false,
)

public data class UPCheckboxGroupDefaults(
    val name: UPRawValue = "",
    val modelValue: UPRawValue? = null,
    val value: List<UPRawValue> = emptyList(),
    val shape: String = "square",
    val disabled: Boolean = false,
    val activeColor: String = "#2979ff",
    val inactiveColor: String = "#c8c9cc",
    val size: UPRawValue = 18,
    val placement: String = "row",
    val labelSize: UPRawValue = 14,
    val labelColor: String = "#303133",
    val labelDisabled: Boolean = false,
    val iconColor: String = "#ffffff",
    val iconSize: UPRawValue = 12,
    val iconPlacement: String = "left",
    val borderBottom: Boolean = false,
)

public data class UPRadioDefaults(
    val name: UPRawValue = "",
    val shape: String = "",
    val disabled: UPRawValue = "",
    val labelDisabled: UPRawValue = "",
    val activeColor: String = "",
    val inactiveColor: String = "",
    val iconSize: UPRawValue = "",
    val labelSize: UPRawValue = "",
    val label: UPRawValue = "",
    val size: UPRawValue = "",
    val color: String = "",
    val labelColor: String = "",
    val iconColor: String = "",
)

public data class UPRadioGroupDefaults(
    val modelValue: UPRawValue? = null,
    val value: UPRawValue = "",
    val disabled: Boolean = false,
    val shape: String = "circle",
    val activeColor: String = "#2979ff",
    val inactiveColor: String = "#c8c9cc",
    val name: UPRawValue = "",
    val size: UPRawValue = 18,
    val placement: String = "row",
    val label: UPRawValue = "",
    val labelColor: String = "#303133",
    val labelSize: UPRawValue = 14,
    val labelDisabled: Boolean = false,
    val iconColor: String = "#ffffff",
    val iconSize: UPRawValue = 12,
    val borderBottom: Boolean = false,
    val iconPlacement: String = "left",
    val gap: UPRawValue = "10px",
)

public data class UPRowDefaults(
    val gutter: UPRawValue = 0,
    val justify: String = "start",
    val align: String = "center",
)

public data class UPColDefaults(
    val span: UPRawValue = 12,
    val offset: UPRawValue = 0,
    val justify: String = "start",
    val align: String = "stretch",
    val textAlign: String = "left",
)

public data class UPGridDefaults(
    val col: UPRawValue = 3,
    val border: Boolean = false,
    val align: String = "left",
    val gap: UPRawValue = "0px",
)

public data class UPGridItemDefaults(
    val name: UPRawValue = null,
    val bgColor: String = "transparent",
)

public data class UPLineProgressDefaults(
    val activeColor: String = "#19be6b",
    val inactiveColor: String = "#ececec",
    val percentage: UPRawValue = 0,
    val showText: Boolean = true,
    val height: UPRawValue = 12,
    val fromRight: Boolean = false,
)

public data class UPCircleProgressDefaults(
    val percentage: UPRawValue = 30,
)


public data class UPAlertDefaults(
    val title: String = "",
    val type: String = "warning",
    val description: String = "",
    val closable: Boolean = false,
    val showIcon: Boolean = false,
    val effect: String = "light",
    val center: Boolean = false,
    val fontSize: UPRawValue = 14,
    val transitionMode: String = "fade",
    val duration: UPRawValue = 0,
    val icon: String = "",
    val value: Boolean = true,
)

public data class UPActionSheetDefaults(
    val show: Boolean = false,
    val title: String = "",
    val description: String = "",
    val actions: List<UPRawValue> = emptyList(),
    val nameKey: String = "name",
    val subnameKey: String = "subnameKey",
    val cancelText: String = "",
    val closeOnClickAction: Boolean = true,
    val safeAreaInsetBottom: Boolean = true,
    val openType: String = "",
    val closeOnClickOverlay: Boolean = true,
    val round: UPRawValue = 0,
    val wrapMaxHeight: String = "600px",
    val index: UPRawValue = "",
)

public data class UPNotifyDefaults(
    val top: UPRawValue = 0,
    val type: String = "primary",
    val color: String = "#ffffff",
    val bgColor: String = "",
    val message: String = "",
    val duration: UPRawValue = 3000,
    val fontSize: UPRawValue = 15,
    val safeAreaInsetTop: Boolean = false,
)

public data class UPBackTopDefaults(
    val mode: String = "circle",
    val icon: String = "arrow-upward",
    val text: String = "",
    val duration: UPRawValue = 100,
    val scrollTop: UPRawValue = 0,
    val top: UPRawValue = 400,
    val bottom: UPRawValue = 100,
    val right: UPRawValue = 20,
    val zIndex: UPRawValue = 9,
    val iconStyle: Map<String, UPRawValue> = mapOf("color" to "#909399", "fontSize" to "19px"),
)

public data class UPCardDefaults(
    val full: Boolean = false,
    val title: String = "",
    val titleColor: String = "#303133",
    val titleSize: UPRawValue = "15px",
    val subTitle: String = "",
    val subTitleColor: String = "#909399",
    val subTitleSize: UPRawValue = "13px",
    val border: Boolean = true,
    val index: UPRawValue = "",
    val margin: UPRawValue = "15px",
    val borderRadius: UPRawValue = "8px",
    val headStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
    val bodyStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
    val footStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
    val headBorderBottom: Boolean = true,
    val footBorderTop: Boolean = true,
    val thumb: String = "",
    val thumbWidth: UPRawValue = "30px",
    val thumbCircle: Boolean = false,
    val padding: UPRawValue = "15px",
    val paddingHead: UPRawValue = "",
    val paddingBody: UPRawValue = "",
    val paddingFoot: UPRawValue = "",
    val showHead: Boolean = true,
    val showFoot: Boolean = true,
    val boxShadow: String = "none",
)

public data class UPCollapseDefaults(
    val value: UPRawValue = null,
    val accordion: Boolean = false,
    val border: Boolean = true,
)

public data class UPCollapseItemDefaults(
    val title: String = "",
    val titleStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
    val value: UPRawValue = "",
    val label: String = "",
    val disabled: Boolean = false,
    val isLink: Boolean = true,
    val clickable: Boolean = true,
    val border: Boolean = true,
    val align: String = "left",
    val name: UPRawValue = "",
    val icon: String = "",
    val duration: UPRawValue = 300,
    val showRight: Boolean = true,
    val iconStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
    val rightIconStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
    val cellCustomStyle: UPStyleInput = emptyMap<String, UPRawValue>(),
    val cellCustomClass: String = "",
)
