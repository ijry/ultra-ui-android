# ultra-ui-android

uview-plus 的 Android 原生 Jetpack Compose 组件库，根包名为 `net.lingyun.ultraui.android`。项目目标是在尽量保持 uview-plus Props、字符串枚举、默认值和事件语义一致的前提下，用 Android 原生能力封装可复用组件；例如 `up-button` 的 `type = "primary"`、`shape = "circle"` 等配置在 uni-app、iOS 与 Android 生成结果中保持同名可用。

## 项目定位

- **跨端同源配置**：后端程序负责把同一份 JSON 转换成 uni-app、iOS、Android 代码；Android 组件只消费已经生成好的 Kotlin `UP*Props` 或 direct overload 参数。
- **原生实现优先**：按钮、输入框、开关、弹窗、进度、布局等能力使用 Jetpack Compose / Android 原生能力实现，不内置页面宿主或网页容器。
- **接口稳定优先**：公开 API 保留 uview-plus 风格 camelCase 字段、字符串枚举和默认值，未知枚举回退到安全默认并通过诊断路径报告。
- **可测试优先**：组件覆盖 Props 契约、Compose 行为测试和确定性截图测试；示例工程仅调用公开 `UP*` API。

## 包名

```kotlin
import net.lingyun.ultraui.android.components.UPButton
import net.lingyun.ultraui.android.components.UPButtonProps
```

- Library namespace / Kotlin 根包：`net.lingyun.ultraui.android`
- Sample 包：`net.lingyun.ultraui.android.sample`

## 环境配置

不要单独安装 JDK 17，直接使用 Android Studio 内置 JBR：

```bash
export ANDROID_HOME="$HOME/Library/Android/sdk"
export ANDROID_SDK_ROOT="$ANDROID_HOME"
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
export PATH="$ANDROID_HOME/platform-tools:$ANDROID_HOME/emulator:$PATH"
```

常用验证命令：

```bash
./scripts/verify-toolchain.sh
./gradlew :ultra-ui:testDebugUnitTest :sample:assembleDebug --console=plain
```

核查脚本在字段与像素层面对照上游，有问题时以退出码 1 失败：

```bash
python3 tools/find_unread_props.py               # 声明了但组件从不读取的字段（当前 0 个）
python3 tools/compare_uview_defaults.py          # 默认值漂移（当前 0 处未解释漂移）
python3 tools/audit_status_claims.py             # 进度文档的标注是否有证据支撑（当前 0 行偏乐观）
python3 tools/find_missing_upstream_props.py     # 上游有、Android 未建模且未在文档点名的 props（当前 0 个）
python3 tools/inspect_screenshot.py --list       # 截图参考图的配色直方图 / 字形字符画
```

## 示例导航

完整的上游组件全集、Android 映射、复刻进度、接口兼容性和下一批优先级见：[uview-plus Android 组件复刻进度](docs/uview-plus-android-component-progress.md)。当前 140 个组件已建立 Props/API（全部基本完成，其中 u-section 为上游无模板下的推定实现），所有声明字段都已被读取或登记为按设计不生效。

示例工程 1:1 复刻 uview-plus 演示工程（`src/pages/example/components`）的结构：首页是一个可搜索的**组件索引**，分组名、组件标题与顺序均取自上游 `components.config.js`（7 个分组、105 个条目），点任一条目进入该组件的**独立 demo 页**。

| 分组 | 条目数 | 说明 |
| --- | --- | --- |
| 基础组件 | 11 | Color、Icon、Image、Button、Text、Layout、Cell、Badge、Tag、Loading、Loading page |
| 表单组件 | 20 | Form、Calendar、Keyboard、Picker、Select、Cascader、Choose、DatetimePicker、Rate、Search、NumberBox、Upload、Code、Input、Textarea、Checkbox、Radio、Switch、Slider、Album |
| 数据组件 | 7 | List、VirtualList、Progress、Table、Table2、CountDown、CountTo |
| 反馈组件 | 18 | Tooltip、Guide、Popover、ActionSheet、Alert、Toast、NoticeBar、Notify、SwipeAction、Collapse、Popup、Modal、Copy、FloatButton、PullRefresh、Signature、Agreement、fullScreen（上游暂无） |
| 布局组件 | 15 | ScrollList、Line、Card、Overlay、NoNetwork、Grid、Swiper、Skeleton、Sticky、Waterfall、Divider、Box、CateTab、Title、ShortVideo |
| 导航组件 | 13 | Dropdown、Tabbar、BackTop、Navbar、NavbarMini、Tabs、TabsSwiper（上游暂无）、Subsection、IndexList、Steps、Empty、Pagination、Tree |
| 其他组件 | 21 | Parse、Markdown、CodeInput、Dragsort、Cropper、Loadmore、ReadMore、LazyLoad、Gap、Avatar、Link、Transition、Qrcode、Coupon、Barcode、ColorPicker、Poster、GoodsSku、CityLocate、PdfReader、NovelReader |

其中 `fullScreen`（压窗屏）与 `TabsSwiper`（全屏选项卡）在上游即标注「暂无」，索引中保留但不可点。其余 103 个组件均有可交互的独立 demo 页，页顶提供事件反馈条。Android 端仍只接收后端生成的 Kotlin `UP*Props`；同一份后端 JSON 可分别生成 uni-app、iOS 和 Android 调用，不由 Android 运行时自行解析。

## 140 个组件目录

| uview-plus 标签 | Android Props |
| --- | --- |
| `u-action-sheet` | `UPActionSheetProps` |
| `u-action-sheet-data` | `UPActionSheetDataProps` |
| `up-agreement` | `UPAgreementProps` |
| `u-album` | `UPAlbumProps` |
| `u-alert` | `UPAlertProps` |
| `u-avatar` | `UPAvatarProps` |
| `u-avatar-group` | `UPAvatarGroupProps` |
| `u-back-top` | `UPBackTopProps` |
| `u-badge` | `UPBadgeProps` |
| `u-barcode` | `UPBarcodeProps` |
| `up-box` | `UPBoxProps` |
| `u-button` | `UPButtonProps` |
| `u-calendar` | `UPCalendarProps` |
| `u-calendar-strip` | `UPCalendarStripProps` |
| `u-canvas` | `UPCanvasProps` |
| `u-car-keyboard` | `UPCarKeyboardProps` |
| `u-card` | `UPCardProps` |
| `u-cascader` | `UPCascaderProps` |
| `up-cate-tab` | `UPCateTabProps` |
| `u-cell` | `UPCellProps` |
| `u-cell-group` | `UPCellGroupProps` |
| `u-checkbox` | `UPCheckboxProps` |
| `u-checkbox-group` | `UPCheckboxGroupProps` |
| `up-choose` | `UPChooseProps` |
| `u-circle-progress` | `UPCircleProgressProps` |
| `u-city-locate` | `UPCityLocateProps` |
| `u-code` | `UPCodeProps` |
| `u-code-input` | `UPCodeInputProps` |
| `u-col` | `UPColProps` |
| `u-collapse` | `UPCollapseProps` |
| `u-collapse-item` | `UPCollapseItemProps` |
| `u-color-picker` | `UPColorPickerProps` |
| `u-column-notice` | `UPColumnNoticeProps` |
| `up-copy` | `UPCopyProps` |
| `u-count-down` | `UPCountDownProps` |
| `u-count-to` | `UPCountToProps` |
| `up-coupon` | `UPCouponProps` |
| `u-cropper` | `UPCropperProps` |
| `u-datetime-picker` | `UPDatetimePickerProps` |
| `u-divider` | `UPDividerProps` |
| `u-dragsort` | `UPDragsortProps` |
| `u-dropdown` | `UPDropdownProps` |
| `u-dropdown-item` | `UPDropdownItemProps` |
| `u-empty` | `UPEmptyProps` |
| `up-float-button` | `UPFloatButtonProps` |
| `u-form` | `UPFormProps` |
| `u-form-item` | `UPFormItemProps` |
| `u-gap` | `UPGapProps` |
| `u-goods-sku` | `UPGoodsSkuProps` |
| `u-grid` | `UPGridProps` |
| `u-grid-item` | `UPGridItemProps` |
| `up-guide` | `UPGuideProps` |
| `u-icon` | `UPIconProps` |
| `u-image` | `UPImageProps` |
| `u-index-anchor` | `UPIndexAnchorProps` |
| `u-index-item` | `UPIndexItemProps` |
| `u-index-list` | `UPIndexListProps` |
| `u-input` | `UPInputProps` |
| `u-keyboard` | `UPKeyboardProps` |
| `u-lazy-load` | `UPLazyLoadProps` |
| `u-line` | `UPLineProps` |
| `u-line-progress` | `UPLineProgressProps` |
| `u-link` | `UPLinkProps` |
| `u-list` | `UPListProps` |
| `u-list-item` | `UPListItemProps` |
| `u-loading-icon` | `UPLoadingIconProps` |
| `u-loading-page` | `UPLoadingPageProps` |
| `u-loadmore` | `UPLoadmoreProps` |
| `u-markdown` | `UPMarkdownProps` |
| `u-message-input` | `UPMessageInputProps` |
| `u-modal` | `UPModalProps` |
| `u-navbar` | `UPNavbarProps` |
| `u-navbar-mini` | `UPNavbarMiniProps` |
| `u-no-network` | `UPNoNetworkProps` |
| `u-notice-bar` | `UPNoticeBarProps` |
| `u-notify` | `UPNotifyProps` |
| `u-novel-reader` | `UPNovelReaderProps` |
| `u-number-box` | `UPNumberBoxProps` |
| `u-number-keyboard` | `UPNumberKeyboardProps` |
| `u-overlay` | `UPOverlayProps` |
| `u-pagination` | `UPPaginationProps` |
| `u-parse` | `UPParseProps` |
| `u-pdf-reader` | `UPPdfReaderProps` |
| `u-picker` | `UPPickerProps` |
| `u-picker-column` | `UPPickerColumnProps` |
| `u-picker-data` | `UPPickerDataProps` |
| `u-popover` | `UPPopoverProps` |
| `u-popup` | `UPPopupProps` |
| `u-poster` | `UPPosterProps` |
| `u-pull-refresh` | `UPPullRefreshProps` |
| `u-qrcode` | `UPQrcodeProps` |
| `u-radio` | `UPRadioProps` |
| `u-radio-group` | `UPRadioGroupProps` |
| `u-rate` | `UPRateProps` |
| `u-read-more` | `UPReadMoreProps` |
| `u-refresh-virtual-list` | `UPRefreshVirtualListProps` |
| `u-row` | `UPRowProps` |
| `u-row-notice` | `UPRowNoticeProps` |
| `u-safe-bottom` | `UPSafeBottomProps` |
| `u-scroll-list` | `UPScrollListProps` |
| `u-search` | `UPSearchProps` |
| `u-section` | `UPSectionProps` |
| `u-select` | `UPSelectProps` |
| `u-short-video` | `UPShortVideoProps` |
| `u-signature` | `UPSignatureProps` |
| `u-skeleton` | `UPSkeletonProps` |
| `u-slider` | `UPSliderProps` |
| `u-status-bar` | `UPStatusBarProps` |
| `u-steps` | `UPStepsProps` |
| `u-steps-item` | `UPStepsItemProps` |
| `u-sticky` | `UPStickyProps` |
| `u-subsection` | `UPSubsectionProps` |
| `u-swipe-action` | `UPSwipeActionProps` |
| `u-swipe-action-item` | `UPSwipeActionItemProps` |
| `u-swiper` | `UPSwiperProps` |
| `u-swiper-indicator` | `UPSwiperIndicatorProps` |
| `u-switch` | `UPSwitchProps` |
| `u-tabbar` | `UPTabbarProps` |
| `u-tabbar-item` | `UPTabbarItemProps` |
| `u-table` | `UPTableProps` |
| `u-table2` | `UPTable2Props` |
| `u-tabs` | `UPTabsProps` |
| `u-tabs-item` | `UPTabsItemProps` |
| `u-tabs-pro` | `UPTabsProProps` |
| `u-tag` | `UPTagProps` |
| `u-td` | `UPTdProps` |
| `u-text` | `UPTextProps` |
| `u-textarea` | `UPTextareaProps` |
| `u-th` | `UPThProps` |
| `u-title` | `UPTitleProps` |
| `u-toast` | `UPToastProps` |
| `u-toolbar` | `UPToolbarProps` |
| `u-tooltip` | `UPTooltipProps` |
| `u-tr` | `UPTr` |
| `u-transition` | `UPTransitionProps` |
| `u-tree` | `UPTreeProps` |
| `u-upload` | `UPUploadProps` |
| `up-view` | `UPViewProps` |
| `u-virtual-list` | `UPVirtualListProps` |
| `u-waterfall` | `UPWaterfallProps` |

> `UPIconProps` 与 `UPLoadingIconProps` 作为独立基础能力保留在库内和示例页中，不计入本批 140 个生成组件目录。

## 公开 API 示例

Props 入口适合后端生成代码直接落地：

```kotlin
@Composable
fun GeneratedButton() {
    UPButton(
        props = UPButtonProps(
            type = "primary",
            shape = "circle",
            text = "主要按钮",
            loading = false,
        ),
        onClick = { /* emit generated event */ },
    )
}
```

Direct overload 适合手写 Compose 页面或示例页：

```kotlin
@Composable
fun ManualButton() {
    UPButton(
        text = "确定",
        type = "success",
        shape = "circle",
        onClick = { /* handle click */ },
    )
}
```

受控输入类组件保留 `modelValue` / `value` 兼容别名；当 `modelValue` 不为 `null` 时优先使用它，否则回退到 `value`：

```kotlin
UPInput(
    props = UPInputProps(
        modelValue = "已生成内容",
        placeholder = "请输入",
        clearable = true,
    ),
    onChange = { nextValue -> /* send nextValue upstream */ },
)
```

## Props 与 direct overload 契约

- 每个生成组件都有公开 `UP*Props` 数据类、Props 渲染入口和简洁 direct overload。
- Props 字段保持 uview-plus camelCase 命名，例如 `loadingText`、`iconColor`、`modelValue`、`customStyle`。
- 字符串枚举保持跨端同名，例如 `type = "primary"`、`mode = "bottom"`、`shape = "circle"`。
- `customStyle` 接受 map 或 CSS-like 字符串输入，并在渲染时合并为 Compose 可用样式。
- Android 端不解析 JSON，也不执行跨端页面 DSL；JSON 到 Kotlin 的转换属于后端生成步骤。
- 不支持或仅部分支持的平台字段保留在 Props 中，通过兼容诊断报告非致命降级，不抛出运行时异常。

## 架构边界

- 禁止在 Android 库内加入专用跨端运行时宿主、网页容器、库内 JSON 解析器或第三方 JSON 映射层。
- 禁止让示例页复制组件实现；示例页只能调用 `net.lingyun.ultraui.android.components` 下公开 API。
- 禁止把后端生成职责下沉到 Android 运行时；Android 只负责渲染已生成的 Kotlin 参数。
- 禁止用硬编码示例替代 Props 契约；新增字段必须先进入公开 Props，并由测试锁定默认值和行为。
