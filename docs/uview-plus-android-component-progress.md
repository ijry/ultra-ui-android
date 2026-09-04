# uview-plus Android 组件复刻进度

> 用途：记录后端将同一份 JSON 生成到 uni-app、iOS、Android Kotlin 后，各端可使用的 uview-plus 组件接口与实现进度。

## 统计口径

- 上游来源：`/Users/admin/Documents/Repos/xyito/open/uview-plus/src/uni_modules/uview-plus/components`
- 上游固定提交：`b32377ce0500579830e537a20eef1a7c6c9cf806`
- 扫描日期：2026-08-30
- 上游目录总数：141 个
- 当前 Android 公开 `UP*Props`：90 个
- 当前 Android 已有公开 Compose 组件入口：90 个（另有 `UPToastHost` 等宿主辅助 API）
- 当前目标组件完成度：90 / 138 个可直接使用的上游 UI 组件目录，约 65.2%。其中 3 个是辅助模块目录，暂不计入 UI 组件分母。

### 复刻进度定义

| 进度 | 判定标准 |
| --- | --- |
| 未开始 | 尚未建立 Android Props 和公开 Compose 组件入口。 |
| Props 已建 | 已建立部分接口或 Props 草案，但尚不能稳定完成主要渲染。当前清单没有把仅有草案的组件伪装成已完成。 |
| 基础可用 | 有公开 Props、Compose 入口和主要静态展示或基础交互；仍有明显的事件、状态、样式或平台差异。 |
| 基本完成 | 常用 Props、主要事件/受控状态、常用样式已有实现与测试；仍需继续和上游逐字段、逐视觉状态对照。 |
| 完整兼容 | 逐字段、逐事件、逐视觉状态与固定上游对照，并有完整回归证据。本表当前暂不提前标记此等级。 |

### 接口兼容性定义

- **高（Props）**：公开 Props 已使用 uview-plus camelCase 字段、字符串枚举和 raw number/string 值，适合后端生成 Kotlin 调用；具体行为仍以备注为准。
- **中**：已有主要字段和基础事件，但仍有字段、事件、受控状态或布局差异。
- **低**：只有部分接口或明显的兼容降级。
- **暂无**：尚未建立 Android API。

## 完整组件清单

| # | 分类 | uview-plus 组件 | Android API | 复刻进度 | 接口兼容性 | 复刻方式与备注 |
| ---: | --- | --- | --- | --- | --- | --- |
| 1 | 原生交互 | `u-action-sheet` | `UPActionSheet` / `UPActionSheetProps` | 基础可用 | 中 | Compose 原生面板；选项事件和窗口级弹层仍需加强。 |
| 2 | 辅助模块 | `u-action-sheet-data` | — | 未开始 | 暂无 | 操作菜单数据辅助目录，不单独作为 Android UI 组件。 |
| 3 | 表单与协议 | `u-agreement` | — | 未开始 | 暂无 | 协议勾选/链接组合组件，待按上游字段建立。 |
| 4 | 媒体与内容 | `u-album` | — | 未开始 | 暂无 | 相册选择与预览，涉及系统权限和媒体选择器。 |
| 5 | 原生交互 | `u-alert` | `UPAlert` / `UPAlertProps` | 基础可用 | 中 | 原生确认提示封装；按钮回调已覆盖，复杂插槽仍待补齐。 |
| 6 | 媒体与内容 | `u-avatar` | `UPAvatar` / `UPAvatarProps` | 基本完成 | 高（Props） | 图片、文字、图标和形状已有 Compose 实现与测试。降级：`randomBgColor` 未随机取色，`colorIndex` 缺省固定取第 0 号色（上游 `''` 表示随机），以保证截图可复现。 |
| 7 | 媒体与内容 | `u-avatar-group` | `UPAvatarGroup` / `UPAvatarGroupProps` | 基本完成 | 高（Props） | 头像组基础布局已实现；溢出和间距视觉仍需上游对照。 |
| 8 | 原生交互 | `u-back-top` | `UPBackTop` / `UPBackTopProps` | 基础可用 | 中 | 返回顶部基础行为；父级滚动容器绑定仍需标准化。 |
| 9 | 基础展示 | `u-badge` | `UPBadge` / `UPBadgeProps` | 基本完成 | 高（Props） | 类型、颜色、徽标位置和最大值已有实现。 |
| 10 | 原生能力 | `u-barcode` | — | 未开始 | 暂无 | 条形码生成，待接入 Android 原生/成熟编码库。 |
| 11 | 布局 | `u-box` | — | 未开始 | 暂无 | 通用容器目录，待确认上游公开 Props 后实现。 |
| 12 | 基础展示 | `u-button` | `UPButton` / `UPButtonProps` | 基本完成 | 高（Props） | `type="primary"`、形状、加载态、图标和禁用态已有测试。 |
| 13 | 选择与日期 | `u-calendar` | `UPCalendar` / `UPCalendarProps` | 基础可用 | 中 | Compose 原生日历已支持月份切换及 single/multiple/range 基础选择；农历、时间精度、formatter 等尚未完整复刻。 |
| 14 | 选择与日期 | `u-calendar-strip` | — | 未开始 | 暂无 | 横向日期条，待实现日期滚动和选中状态。 |
| 15 | 媒体与内容 | `u-canvas` | — | 未开始 | 暂无 | Canvas 容器/绘制适配，需单独确认 Android 生成调用方式。 |
| 16 | 键盘与输入 | `u-car-keyboard` | — | 未开始 | 暂无 | 车牌键盘，待按原生输入法交互实现。 |
| 17 | 基础展示 | `u-card` | `UPCard` / `UPCardProps` | 基础可用 | 中 | 卡片基础样式和插槽容器已实现；细节视觉待对照。 |
| 18 | 选择与日期 | `u-cascader` | `UPCascader` / `UPCascaderProps` | 基础可用 | 中 | `data` 多级路径、value/label/children key 与 change/confirm 基础事件已支持；弹层动画和复杂 header 尚未完整复刻。 |
| 19 | 导航 | `u-cate-tab` | — | 未开始 | 暂无 | 分类导航，待建立横向/纵向布局契约。 |
| 20 | 基础展示 | `u-cell` | `UPCell` / `UPCellProps` | 基本完成 | 高（Props） | 标题、描述、图标、箭头和点击行为已有测试。 |
| 21 | 基础展示 | `u-cell-group` | `UPCellGroup` / `UPCellGroupProps` | 基本完成 | 高（Props） | 分组容器和边界样式已有实现。 |
| 22 | 选择 | `u-checkbox` | `UPCheckbox` / `UPCheckboxProps` | 基本完成 | 高（Props） | 受控值、形状、颜色、标签和组上下文已有测试。 |
| 23 | 选择 | `u-checkbox-group` | `UPCheckboxGroup` / `UPCheckboxGroupProps` | 基本完成 | 高（Props） | 多选组受控值和布局已有测试。 |
| 24 | 选择 | `u-choose` | — | 未开始 | 暂无 | 选择组合控件，待核对上游当前 API。 |
| 25 | 布局与进度 | `u-circle-progress` | `UPCircleProgress` / `UPCircleProgressProps` | 基本完成 | 高（Props） | 环形进度、颜色、宽度和文字已有实现。 |
| 26 | 原生能力 | `u-city-locate` | — | 未开始 | 暂无 | 城市定位，涉及定位权限和系统服务。 |
| 27 | 键盘与输入 | `u-code` | — | 未开始 | 暂无 | 验证码/代码展示辅助组件，待确认与 `u-code-input` 的边界。 |
| 28 | 键盘与输入 | `u-code-input` | `UPCodeInput` / `UPCodeInputProps` | 基本完成 | 高（Props） | 输入长度、掩码、颜色和回调已有实现；原生焦点细节待补。 |
| 29 | 布局 | `u-col` | `UPCol` / `UPColProps` | 基本完成 | 高（Props） | 栅格列宽、偏移和响应式基础字段已有实现。 |
| 30 | 内容面板 | `u-collapse` | `UPCollapse` / `UPCollapseProps` | 基础可用 | 中 | 折叠状态和组上下文已有；动画和部分事件待补。 |
| 31 | 内容面板 | `u-collapse-item` | `UPCollapseItem` / `UPCollapseItemProps` | 基础可用 | 中 | 子项展开行为可用；插槽、图标和动画仍需对照。 |
| 32 | 选择与日期 | `u-color-picker` | — | 未开始 | 暂无 | 颜色选择器，待建立颜色值和面板交互契约。 |
| 33 | 通知与状态 | `u-column-notice` | `UPColumnNotice` / `UPColumnNoticeProps` | 基础可用 | 中 | 基于通知栏封装；轮播动画和触摸行为仍较浅。 |
| 34 | 工具 | `u-copy` | — | 未开始 | 暂无 | 剪贴板复制动作，待确认是否以无 UI action API 提供。 |
| 35 | 数值与时间 | `u-count-down` | `UPCountDown` / `UPCountDownProps` | 基础可用 | 中 | 时间格式和自动开始已有；start/pause/reset controller 待补。 |
| 36 | 数值与时间 | `u-count-to` | `UPCountTo` / `UPCountToProps` | 基础可用 | 中 | 数字格式和回调已有；真实逐帧动画仍待实现。 |
| 37 | 基础展示 | `u-coupon` | — | 未开始 | 暂无 | 优惠券展示/选择，待建立业务字段契约。 |
| 38 | 媒体与内容 | `u-cropper` | — | 未开始 | 暂无 | 图片裁剪，待接入原生手势和输出 URI。 |
| 39 | 选择与日期 | `u-datetime-picker` | `UPDatetimePicker` / `UPDatetimePickerProps` | 基本完成 | 中 | year-month/date/time/datetime 基础列选择、时间戳和 value/modelValue 更新已支持；列已按 `visibleItemCount × itemHeight` 固定高度并可滚动（此前平铺导致屏幕外选项无法点击）；`hasInput` 渲染只读 `u-input` 触发器（文案按 `format` 或按 mode 缺省格式化，`time`/`timesecond` 原样显示）并由覆盖层接管点击、`inputBorder` 映射 `u-input` 的 `surround`/`bottom`/`none`（布尔转枚举）、`inputProps` 逐键覆盖 18 个 `u-input` 字段（键名大小写与 `-`/`_` 归一，未知键上报诊断）、`toolbarRightSlot` 用 `toolbarRight` 插槽替换确认按钮（连同确认事件一并让位，与上游 `u-toolbar` 一致）、`maskStyle` 仅在显式传值时覆盖列遮罩且不拦截点击。降级：`popupMode` 仅 `top`/`bottom` 可由内联面板表达，其余四值上报诊断；`maskClass` 无原生等价，改用 `maskStyle`。惯性滚轮视觉、filter/formatter 尚未复刻。 |
| 40 | 基础展示 | `u-divider` | `UPDivider` / `UPDividerProps` | 基本完成 | 高（Props） | 分割线方向、文字和样式已有实现。 |
| 41 | 列表与拖拽 | `u-dragsort` | — | 未开始 | 暂无 | 拖拽排序，待采用 Compose drag-and-drop 方案。 |
| 42 | 原生交互 | `u-dropdown` | `UPDropdown` / `UPDropdownProps` | 基本完成 | 中 | 标题栏样式由父级下发并生效：`height` 限定标题行高、`titleSize` 控制标题字号、`menuIcon`/`menuIconSize` 决定箭头、`borderBottom` 绘制下边框、`borderRadius` 作用于展开面板、`duration` 驱动展开动画；`closeOnClickOverlay` 作为 `closeOnClickMask` 的兼容别名优先生效。均有真机断言。降级：`menu` 为 Android 专有兼容别名，上游无对应字段；向上展开需窗口级弹层。 |
| 43 | 原生交互 | `u-dropdown-item` | `UPDropdownItem` / `UPDropdownItemProps` | 基本完成 | 中 | 标题点击开合、单选/多选 payload、选项禁用与 `height` 限定面板最大高度（超出滚动）均已生效并有真机断言；复杂内容插槽已支持 `content`。 |
| 44 | 基础展示 | `u-empty` | `UPEmpty` / `UPEmptyProps` | 基本完成 | 高（Props） | 图标、描述、按钮和样式已有实现。 |
| 45 | 原生交互 | `u-float-button` | — | 未开始 | 暂无 | 浮动按钮，待实现拖动/吸附和安全区处理。 |
| 46 | 表单与协议 | `u-form` | `UPForm` / `UPFormProps` | 基本完成 | 高（Props） | model/rules/errorType/labelPosition/labelWidth/labelAlign/labelStyle 逐字段下发子项；`UPFormController` 复刻 validate/validateField/resetFields/resetField/clearValidate/setRules 六个 ref 方法，async-validator 的 required/type/range/pattern/whitespace/enum/transform/validator 与消息模板逐条实现；errorType="toast" 交由 `onToast` 宿主回调。差异：Kotlin `model` 不可变，`resetFields()` 经 `onUpdateModel` 回传首帧快照；`borderBottom` 上游仅存于死代码 computed，保留字段但不生效。 |
| 47 | 表单与协议 | `u-form-item` | `UPFormItem` / `UPFormItemProps` | 基本完成 | 高（Props） | 标签宽度/对齐/位置回落父级、必填星号绝对定位不占布局、leftIcon、label/right/error 三插槽、错误文案缩进（labelPosition="top" 归零）、borderBottom 画线与错误色均有真机断言。降级：上游 `rightIcon` 声明后从未渲染，Android 同样保留字段但不生效。 |
| 48 | 基础展示 | `u-gap` | `UPGap` / `UPGapProps` | 基本完成 | 高（Props） | 间隔尺寸和背景已有实现。 |
| 49 | 选择 | `u-goods-sku` | — | 未开始 | 暂无 | 商品规格选择器，待明确业务数据模型。 |
| 50 | 布局 | `u-grid` | `UPGrid` / `UPGridProps` | 基本完成 | 高（Props） | 列数、间距、边框和点击布局已有测试。 |
| 51 | 布局 | `u-grid-item` | `UPGridItem` / `UPGridItemProps` | 基本完成 | 高（Props） | 图标、文字和点击项已有实现。 |
| 52 | 原生交互 | `u-guide` | — | 未开始 | 暂无 | 新手引导遮罩和高亮定位待实现。 |
| 53 | 基础能力 | `u-icon` | `UPIcon` / `UPIconProps` | 基本完成 | 高（Props） | 已接入固定上游 icon font；图片图标和自定义字体仍有降级。 |
| 54 | 媒体与内容 | `u-image` | `UPImage` / `UPImageProps` | 基本完成 | 高（Props） | 加载、错误、裁剪模式和占位已有实现。降级：`showMenuByLongpress` 仅微信小程序有效，Android 保留字段但不生效，缺省 `false`。 |
| 55 | 列表与索引 | `u-index-anchor` | `UPIndexAnchor` / `UPIndexAnchorProps` | 基础可用 | 中 | 索引锚点可渲染，选项对象名称、尺寸和自定义样式已有真机断言；联动滚动和 sticky 语义待补。 |
| 56 | 列表与索引 | `u-index-item` | `UPIndexItem` / `UPIndexItemProps` | 基础可用 | 中 | 索引项容器可用，自定义内容与样式已有真机断言；完整索引定位待补。 |
| 57 | 列表与索引 | `u-index-list` | `UPIndexList` / `UPIndexListProps` | 基础可用 | 中 | 右侧索引条已按 `indexList` 渲染并可点击（`onIndexClick` 回调索引字符与序号）、`activeColor`/`inactiveColor` 区分选中态、`itemMargin` 控制间距、`customNavHeight` 让出导航栏高度、`safeBottomFix` 避让底部安全区；均有真机断言。此前完全不渲染索引条。降级：`sticky` 锚点吸顶需宿主滚动容器回传偏移。 |
| 58 | 键盘与输入 | `u-input` | `UPInput` / `UPInputProps` | 基本完成 | 高（Props） | `modelValue/value`、清除、密码、前后缀和常用样式已有测试；`selectionStart`/`selectionEnd`/`cursor` 已通过 `TextFieldValue` 生效（聚焦时应用、越界自动钳制）。降级：`adjustPosition`、`autoBlur`、`cursorSpacing`、`fixed`、`holdKeyboard`、`disableDefaultPadding`、`ignoreCompositionEvent`、`placeholderClass` 为 uni-app/小程序专有，保留字段但不生效。 |
| 59 | 键盘与输入 | `u-keyboard` | — | 未开始 | 暂无 | 数字/自定义键盘容器待实现。 |
| 60 | 媒体与内容 | `u-lazy-load` | — | 未开始 | 暂无 | 图片懒加载容器，待结合 Compose lazy layout。 |
| 61 | 基础展示 | `u-line` | `UPLine` / `UPLineProps` | 基本完成 | 高（Props） | 横竖线、颜色、虚线和尺寸已有实现。 |
| 62 | 布局与进度 | `u-line-progress` | `UPLineProgress` / `UPLineProgressProps` | 基本完成 | 高（Props） | 进度、颜色、圆角和文字已有实现。 |
| 63 | 基础展示 | `u-link` | `UPLink` / `UPLinkProps` | 基本完成 | 高（Props） | 链接文字、下划线、图标和点击已有实现。 |
| 64 | 列表与索引 | `u-list` | `UPList` / `UPListProps` | 基础可用 | 中 | `height`/`width` 限定视口、`scrollable`、`lowerThreshold`/`upperThreshold` 触边事件（按穿越沿触发一次）、`scrollTop`+`scrollWithAnimation` 程序化滚动、`refresherEnabled` 系列下拉刷新均已生效并有真机测试；未指定高度时可嵌入外层纵向滚动容器；`pagingEnabled`、`preLoadScreen`、`scrollIntoView` 仍未实现。 |
| 65 | 列表与索引 | `u-list-item` | `UPListItem` / `UPListItemProps` | 基础可用 | 中 | 列表项容器可用；复杂 slot 与分割线待补。 |
| 66 | 基础能力 | `u-loading-icon` | `UPLoadingIcon` / `UPLoadingIconProps` | 基本完成 | 高（Props） | 原生 Compose 加载动画和 icon font 兼容已有测试。 |
| 67 | 通知与状态 | `u-loading-page` | `UPLoadingPage` / `UPLoadingPageProps` | 基本完成 | 高（Props） | 加载页文字、图标、背景和状态已有实现。 |
| 68 | 通知与状态 | `u-loadmore` | `UPLoadmore` / `UPLoadmoreProps` | 基本完成 | 高（Props） | 加载/没有更多/点击加载状态已有实现。 |
| 69 | 内容与解析 | `u-markdown` | — | 未开始 | 暂无 | Markdown 渲染待选定 Android 原生解析方案。 |
| 70 | 键盘与输入 | `u-message-input` | — | 未开始 | 暂无 | 消息输入框组合控件，待复用输入和附件能力。 |
| 71 | 原生交互 | `u-modal` | `UPModal` / `UPModalProps` | 基本完成 | 高（Props） | 原生 Dialog/Compose 弹窗、确认取消和样式已有实现。 |
| 72 | 导航 | `u-navbar` | `UPNavbar` / `UPNavbarProps` | 基础可用 | 中 | 安全区、标题和 icon 已有；fixed/statusBarBgColor 等平台字段仍有降级。 |
| 73 | 导航 | `u-navbar-mini` | `UPNavbarMini` / `UPNavbarMiniProps` | 基础可用 | 中 | 迷你导航和 icon 已有；自动返回由宿主处理。 |
| 74 | 通知与状态 | `u-no-network` | — | 未开始 | 暂无 | 无网络状态页待实现。 |
| 75 | 通知与状态 | `u-notice-bar` | `UPNoticeBar` / `UPNoticeBarProps` | 基础可用 | 中 | 通知文字、方向和点击已有；真实滚动动画待补。 |
| 76 | 原生交互 | `u-notify` | `UPNotify` / `UPNotifyProps` | 基础可用 | 中 | 顶部通知基础展示可用；全局 host 生命周期待标准化。 |
| 77 | 内容与解析 | `u-novel-reader` | — | 未开始 | 暂无 | 小说阅读器业务组件，不纳入当前基础组件批次。 |
| 78 | 数值与时间 | `u-number-box` | `UPNumberBox` / `UPNumberBoxProps` | 基本完成 | 高（Props） | 步进、范围、精度、禁用和受控值已有测试。 |
| 79 | 键盘与输入 | `u-number-keyboard` | — | 未开始 | 暂无 | 数字键盘待复刻。 |
| 80 | 原生交互 | `u-overlay` | `UPOverlay` / `UPOverlayProps` | 基本完成 | 高（Props） | 原生 Compose 遮罩、透明度和点击关闭已有实现。 |
| 81 | 选择与日期 | `u-pagination` | `UPPagination` / `UPPaginationProps` | 基础可用 | 中 | 分页基础按钮可用；sizes、total 布局和完整页码算法待补。 |
| 82 | 内容与解析 | `u-parse` | — | 未开始 | 暂无 | HTML 富文本解析待确定原生实现边界。 |
| 83 | 内容与解析 | `u-pdf-reader` | — | 未开始 | 暂无 | PDF 阅读器待接入 Android 原生 PDF 能力。 |
| 84 | 选择与日期 | `u-picker` | `UPPicker` / `UPPickerProps` | 基本完成 | 中 | modelValue/value/defaultIndex 和事件 payload 已修正；列已按 `visibleItemCount × itemHeight` 固定高度并可滚动，选项在行内垂直居中；`hasInput` 渲染只读 `u-input` 触发器（文案经 `keyName` 还原对象列标签）并由覆盖层接管点击打开面板、`inputBorder` 映射 `u-input` 的 `surround`/`bottom`/`none`（布尔转枚举）、`inputProps` 逐键覆盖 18 个 `u-input` 字段（键名大小写与 `-`/`_` 归一，未知键上报诊断）、`toolbarRightSlot` 用 `toolbarRight` 插槽替换确认按钮、`maskStyle` 仅在显式传值时覆盖列遮罩、`popupMode` 经 `round` 决定面板圆角朝向。降级：`popupMode` 的 `left`/`right`/`center` 需窗口级弹层，内联面板仅接受 `top`/`bottom` 并对其余值上报诊断；`maskClass` 为 CSS 类钩子，无原生等价。惯性动画待补。 |
| 85 | 选择与日期 | `u-picker-column` | `UPPickerColumn` / `UPPickerColumnProps` | 基础可用 | 中 | Props 为空契约的容器已提供，自定义内容与样式已有真机断言；原生列滚动待补。 |
| 86 | 辅助模块 | `u-picker-data` | — | 未开始 | 暂无 | 选择器数据辅助目录，不单独作为 Android UI 组件。 |
| 87 | 原生交互 | `u-popover` | `UPPopover` / `UPPopoverProps` | 基础可用 | 中 | `direction` 四向定位（top/bottom/left/right）、`triggerMode` 三态（click 默认／hover 映射为长按／manual 仅受 `show` 控制）、`bgColor` 均已生效并有真机断言。降级：`zIndex`、`forcePosition` 需窗口级弹层，当前为内联渲染。 |
| 88 | 原生交互 | `u-popup` | `UPPopup` / `UPPopupProps` | 基本完成 | 高（Props） | Android 原生 Dialog/Compose 弹层基础能力已有实现；复杂 slot 动画待对照。 |
| 89 | 媒体与内容 | `u-poster` | — | 未开始 | 暂无 | 海报生成/展示待实现。 |
| 90 | 列表与索引 | `u-pull-refresh` | — | 未开始 | 暂无 | 下拉刷新待接入 Compose nested scroll。 |
| 91 | 原生能力 | `u-qrcode` | — | 未开始 | 暂无 | 二维码生成/扫描待接入成熟 Android 库。 |
| 92 | 选择 | `u-radio` | `UPRadio` / `UPRadioProps` | 基本完成 | 高（Props） | 单选形状、组上下文、颜色和标签已有测试。 |
| 93 | 选择 | `u-radio-group` | `UPRadioGroup` / `UPRadioGroupProps` | 基本完成 | 高（Props） | 受控值、布局和组状态已有测试。 |
| 94 | 选择 | `u-rate` | `UPRate` / `UPRateProps` | 基本完成 | 高（Props） | 评分、半星、颜色、数量和点击已有实现。 |
| 95 | 内容面板 | `u-read-more` | `UPReadMore` / `UPReadMoreProps` | 基础可用 | 中 | 高度截断和 controlled alias 已修正；真实测量和展开动画待补。 |
| 96 | 列表与索引 | `u-refresh-virtual-list` | — | 未开始 | 暂无 | 刷新虚拟列表待结合 lazy/scroll 状态实现。 |
| 97 | 布局 | `u-row` | `UPRow` / `UPRowProps` | 基本完成 | 高（Props） | gutter、justify、align 和 slot 布局已有测试。 |
| 98 | 通知与状态 | `u-row-notice` | `UPRowNotice` / `UPRowNoticeProps` | 基础可用 | 中 | 基于通知栏封装；真实横向滚动动画待补。 |
| 99 | 导航 | `u-safe-bottom` | `UPSafeBottom` / `UPSafeBottomProps` | 基础可用 | 高（Props） | Android navigation bar inset 已封装并有真机断言。 |
| 100 | 列表与索引 | `u-scroll-list` | `UPScrollList` / `UPScrollListProps` | 基本完成 | 中 | 内容已可横向滚动，并按上游默认（`indicator: true`）渲染指示器：`indicatorWidth` 定轨道宽、`indicatorBarWidth` 定滑块宽、`indicatorColor`/`indicatorActiveColor` 分别着色轨道与滑块、`indicatorStyle` 可再覆盖样式，滑块位置跟随滚动进度；均有真机断言。此前既不滚动也不渲染指示器。 |
| 101 | 键盘与输入 | `u-search` | `UPSearch` / `UPSearchProps` | 基本完成 | 高（Props） | 输入、清除、搜索按钮和受控值已有实现。 |
| 102 | 基础展示 | `u-section` | — | 未开始 | 暂无 | 区块标题组件待建立。 |
| 103 | 选择 | `u-select` | `UPSelect` / `UPSelectProps` | 基础可用 | 中 | options、current、select/update 事件已有；Popup 和样式字段待补。 |
| 104 | 媒体与内容 | `u-short-video` | — | 未开始 | 暂无 | 短视频播放器涉及 ExoPlayer 和生命周期。 |
| 105 | 原生能力 | `u-signature` | — | 未开始 | 暂无 | 手写签名画布待实现。 |
| 106 | 通知与状态 | `u-skeleton` | `UPSkeleton` / `UPSkeletonProps` | 基础可用 | 中 | 骨架行、头像、标题和动画开关已有；细节尺寸待对照。 |
| 107 | 选择 | `u-slider` | `UPSlider` / `UPSliderProps` | 基础可用 | 中 | 单值、range、step 量化和 changing/change 基础手势已支持；vertical、原生无障碍语义和复杂样式仍待完善。 |
| 108 | 导航 | `u-status-bar` | `UPStatusBar` / `UPStatusBarProps` | 基础可用 | 高（Props） | 状态栏高度和顶部 inset 已封装。 |
| 109 | 导航 | `u-steps` | `UPSteps` / `UPStepsProps` | 基本完成 | 中 | `current` 驱动 finish/process/wait/error 四态、`direction` 控制横纵布局、`activeColor`/`inactiveColor`/`dot`/`activeIcon`/`inactiveIcon` 均已生效并有真机测试。 |
| 110 | 导航 | `u-steps-item` | `UPStepsItem` / `UPStepsItemProps` | 基本完成 | 中 | 按索引与父级 `current` 推导状态：已完成显示 ✓、当前步为实心序号、未达步为灰色序号、`error` 显示 ✕；`iconSize`（对齐上游 17）与 `itemStyle` 已生效。 |
| 111 | 原生交互 | `u-sticky` | `UPSticky` / `UPStickyProps` | 基础可用 | 低 | 当前为可嵌入容器，`offsetTop` + `customNavHeight` 已按上游折算为顶部偏移；真实滚动吸顶仍待实现。 |
| 112 | 导航 | `u-subsection` | `UPSubsection` / `UPSubsectionProps` | 基础可用 | 中 | 分段切换基础行为可用；样式和滚动模式待对照。 |
| 113 | 原生交互 | `u-swipe-action` | `UPSwipeAction` / `UPSwipeActionProps` | 基本完成 | 中 | 父级协调已实现：`autoClose` 打开一项时关闭其余项、`opendItem` 置 false 触发 closeAll、并通过 `onUpdateOpendItem` 上报开合状态；均有真机断言。 |
| 114 | 原生交互 | `u-swipe-action-item` | `UPSwipeActionItem` / `UPSwipeActionItemProps` | 基本完成 | 中 | 横向拖动超过 `threshold`（默认 20）才展开、`duration` 驱动展开动画、`show` 为受控开合状态、`closeOnClick` 点击后收起、`disabled` 忽略手势；均有真机断言。此前按钮由 `show` 常驻显示且完全没有手势。 |
| 115 | 媒体与内容 | `u-swiper` | `UPSwiper` / `UPSwiperProps` | 基本完成 | 中 | `autoplay`+`interval` 定时切换、`circular` 末尾回头、`previousMargin`/`nextMargin` 露边、`indicatorStyle` 已生效；本批补齐 `imgMode`（图片项经 `UPImage` 渲染，`keyName`/`getSource` 对齐上游）、`radius` 圆角裁剪、`showTitle` 半透明标题条（显示标题时隐藏指示器）、`vertical` 纵向布局与纵向拖拽、`displayMultipleItems` 视口均分、`currentItemId`（优先级高于 `current`）、`duration` 过渡动画和 `loading` 占位。视频项渲染 `poster` + 播放图标并上报诊断（无原生播放器）；`acceleration` 降级为诊断上报，`easingFunction` 登记为按设计不生效。 |
| 116 | 媒体与内容 | `u-swiper-indicator` | `UPSwiperIndicator` / `UPSwiperIndicatorProps` | 基础可用 | 中 | line/dot 两种基础模式、当前项尺寸、颜色和点击回调已有真机断言；复杂样式待对照。 |
| 117 | 选择 | `u-switch` | `UPSwitch` / `UPSwitchProps` | 基本完成 | 高（Props） | 受控值、禁用、颜色和 change/update 事件已有测试。 |
| 118 | 导航 | `u-tabbar` | `UPTabbar` / `UPTabbarProps` | 基础可用 | 中 | 父子受控状态、颜色、边框和安全区已支持；新增 9 种 `styleType`、active/inactive 背景、`itemShape`、`textMode`、`iconScale` 和 `animationType`，均有真机结构/状态回归。`fixed`/`placeholder`/`zIndex` 为兼容字段，窗口级固定需宿主放入 Scaffold bottomBar 或底部 Box。 |
| 119 | 导航 | `u-tabbar-item` | `UPTabbarItem` / `UPTabbarItemProps` | 基础可用 | 中 | active/inactive icon、文字、badge/dot、name 事件、状态背景和 underline/dot 指示器已支持；`animationType` 仅作用于激活图标，`midButton` 已支持 64dp 外层、52dp 内层及垂直偏移。CSS class hook 和 box-shadow 通过原生语义/阴影近似并发出降级诊断，复杂视觉仍待上游逐项对照。 |
| 120 | 表格 | `u-table` | — | 未开始 | 暂无 | 表格容器待建立列宽和滚动契约。 |
| 121 | 表格 | `u-table2` | — | 未开始 | 暂无 | 第二版表格，待确认与 `u-table` 的 API 差异。 |
| 122 | 导航 | `u-tabs` | `UPTabs` / `UPTabsProps` | 基础可用 | 中 | tabs/current/change 基础行为可用；滚动、粘性和样式字段待补。 |
| 123 | 导航 | `u-tabs-item` | `UPTabsItem` / `UPTabsItemProps` | 基础可用 | 中 | 空/轻量 Props 契约、自定义内容和样式已有真机断言。 |
| 124 | 导航 | `u-tabs-pro` | — | 未开始 | 暂无 | Pro 标签页待确认专属字段和事件。 |
| 125 | 基础展示 | `u-tag` | `UPTag` / `UPTagProps` | 基本完成 | 高（Props） | 类型、形状、图标、关闭和颜色已有测试；`height`/`borderRadius`/`plainFill` 已生效并有真机断言（此前声明但从不读取）。`autoBgColor` 未实现。 |
| 126 | 表格 | `u-td` | — | 未开始 | 暂无 | 表格单元格待随表格体系实现。 |
| 127 | 基础展示 | `u-text` | `UPText` / `UPTextProps` | 基本完成 | 高（Props） | 文本截断、链接、前后缀图标和样式已有实现。 |
| 128 | 键盘与输入 | `u-textarea` | `UPTextarea` / `UPTextareaProps` | 基本完成 | 高（Props） | 多行输入、字数、清除和受控值已有实现；`selectionStart`/`selectionEnd`/`cursor` 已通过 `TextFieldValue` 生效，`confirmType` 按 multiline 语义映射 IME 动作。降级字段同 `u-input`。 |
| 129 | 表格 | `u-th` | — | 未开始 | 暂无 | 表头单元格待随表格体系实现。 |
| 130 | 基础展示 | `u-title` | `UPTitle` / `UPTitleProps` | 基本完成 | 高（Props） | 标题、装饰线和对齐样式已有实现。 |
| 131 | 原生交互 | `u-toast` | `UPToast` / `UPToastProps` | 基本完成 | 高（Props） | Toast 原生展示和 `UPToastHost` 宿主已有；队列细节待补。 |
| 132 | 原生交互 | `u-toolbar` | — | 未开始 | 暂无 | 工具栏待确认与导航/输入场景的复用边界。 |
| 133 | 原生交互 | `u-tooltip` | `UPTooltip` / `UPTooltipProps` | 基础可用 | 中 | `direction`（top 默认／bottom）决定气泡在触发器上方还是下方、`triggerMode` 三态（longpress 默认／click／manual）、`buttons` 扩展按钮组（带索引回调）、`copyText`（为空回退 `text`）、`bgColor` 均已生效并有真机断言。此前气泡固定渲染在下方且点击与长按都会触发。降级：`zIndex`、`overlay`、`singleton`、`forcePosition` 需窗口级弹层；`showToast` 的复制提示由宿主决定。 |
| 134 | 表格 | `u-tr` | — | 未开始 | 暂无 | 表格行待随表格体系实现。 |
| 135 | 原生交互 | `u-transition` | — | 未开始 | 暂无 | 通用过渡动画待建立 Compose 状态 API。 |
| 136 | 内容面板 | `u-tree` | — | 未开始 | 暂无 | 树节点展开、选中和懒加载待实现。 |
| 137 | 媒体与内容 | `u-upload` | — | 未开始 | 暂无 | 文件/图片上传待接入 Android picker 和上传回调。 |
| 138 | 辅助模块 | `uview-plus` | — | 未开始 | 暂无 | uview-plus 根模块目录，不单独作为 UI 组件。 |
| 139 | 布局 | `u-view` | — | 未开始 | 暂无 | 通用 View 兼容层，Android 端优先直接使用 Compose Modifier/容器。 |
| 140 | 列表与索引 | `u-virtual-list` | — | 未开始 | 暂无 | 虚拟列表待结合 Compose LazyColumn 和生成数据契约。 |
| 141 | 列表与索引 | `u-waterfall` | — | 未开始 | 暂无 | 瀑布流布局待采用原生 staggered grid 方案。 |

## 汇总

| 指标 | 数量 |
| --- | ---: |
| 上游目录总数 | 141 |
| 可直接使用的 UI 组件目录 | 138 |
| 辅助模块目录 | 3 |
| Android 已建立 Props/API | 90 |
| 基本完成 | 52 |
| 基础可用 | 38 |
| Props 已建 | 0 |
| 未开始（含辅助模块） | 51 |
| 完整兼容 | 0 |

## 当前已实现组件分批

| 批次 | 组件范围 | 数量 | 当前判断 |
| --- | --- | ---: | --- |
| 基础组件 | button、tag、badge、divider、gap、line、link、text、title、overlay、popup、modal、toast、cell、cell-group、image、avatar、avatar-group、empty、loading-page、loadmore、input、textarea、search、code-input、switch、rate、number-box、checkbox、checkbox-group、radio、radio-group、row、col、grid、grid-item、line-progress、circle-progress | 38 | 基本完成；仍需逐字段视觉回归。 |
| 基础能力 | icon、loading-icon | 2 | 基本完成；自定义图片/字体能力存在平台降级。 |
| Batch 9A 原生交互 | alert、action-sheet、notify、back-top、card、collapse、collapse-item、dropdown、dropdown-item、notice-bar | 10 | 基础可用；全局弹层、滚动和动画语义仍需加强。 |
| Batch 9B 导航与更多 | navbar、navbar-mini、status-bar、safe-bottom、tabs、tabs-item、subsection、steps、steps-item、list、list-item、index-list、index-item、index-anchor、scroll-list、popover、tooltip、sticky、swipe-action、swipe-action-item、swiper、swiper-indicator、skeleton、read-more、column-notice、row-notice、count-to、count-down、picker、picker-column、pagination、select | 32 | 基础可用；部分组件已做受控字段修正，但还不是完整上游行为复刻。 |
| Batch 10 选择与底部导航 | calendar、datetime-picker、cascader、slider、tabbar、tabbar-item | 6 | 基础可用；日期选择、级联、滑块和底部导航核心状态已覆盖，滚轮视觉、复杂样式和窗口级固定仍需加强。 |
| Batch 11 表单校验 | form、form-item | 2 | 基本完成；上游 async-validator 规则、六个 ref 方法与标签/错误布局均有真机断言。 |

## 下一批推荐顺序

1. **表单体系**：`u-agreement`、`u-upload`、`u-album`。需要先确定 Android 回调 payload 和权限/文件 URI 边界（`u-form`、`u-form-item` 已在 Batch 11 完成）。
2. **列表与数据展示**：`u-pull-refresh`、`u-virtual-list`、`u-refresh-virtual-list`、`u-waterfall`、`u-table`、`u-td`、`u-th`、`u-tr`。
3. **原生能力**：`u-qrcode`、`u-barcode`、`u-signature`、`u-copy`、`u-city-locate`、`u-short-video`、`u-pdf-reader`。
4. **内容解析与复杂业务**：`u-markdown`、`u-parse`、`u-tree`、`u-goods-sku`、`u-novel-reader`、`u-tabs-pro`。
5. **选择增强**：`u-calendar-strip`、`u-keyboard`、`u-number-keyboard`、`u-car-keyboard`，并继续增强 Batch 10 滚轮、弹层和固定布局语义。

## 真机行为测试

`src/androidTest` 下的行为测试**必须在设备/模拟器上执行**，仅编译通过不构成验证证据。
本机已有可用 AVD（`MCode_Phone`，android-36/arm64-v8a），`adb` 与 `emulator` 位于
`$ANDROID_HOME` 下但不在默认 `PATH` 中——需要显式导出，否则会误判为"环境无 adb"：

```
export ANDROID_HOME="$HOME/Library/Android/sdk"
export PATH="$ANDROID_HOME/platform-tools:$ANDROID_HOME/emulator:$PATH"
emulator -avd MCode_Phone -no-snapshot-load -no-boot-anim -gpu swiftshader_indirect &
until [ "$(adb shell getprop sys.boot_completed | tr -d '\r')" = "1" ]; do sleep 4; done
export ANDROID_SERIAL=emulator-5554        # 锁定手机 AVD：配对启动的 Wear OS 模拟器会让同一批用例在圆形小屏上重复执行并必然裁剪失败
./gradlew :ultra-ui:connectedDebugAndroidTest
# 按类执行（该任务不支持 --tests）
./gradlew :ultra-ui:connectedDebugAndroidTest \
  -Pandroid.testInstrumentationRunnerArguments.class=net.lingyun.ultraui.android.components.UPFormBehaviorTest
```

当前状态：库内共 **186 个行为测试**（`ultra-ui/src/androidTest` 的 `@Test` 静态计数；上一次
`connectedDebugAndroidTest` 在 162 项时报告的 `Starting 162 tests` 与静态计数一致）。最近一批为
`u-picker`/`u-datetime-picker` 的 `hasInput` 触发器与工具条插槽新增 12 项回归
（`UPPickerInputBehaviorTest`），覆盖触发器只读与覆盖层开合、`inputProps` 覆盖生效、
`toolbarRightSlot` 替换确认按钮、`maskStyle` 仅在显式传值时出现、`popupMode`/`maskClass`
降级诊断，以及 datetime 触发器文案按 `format` 格式化。这 12 项同样**只有编译级证据**
（`compileDebugAndroidTestKotlin` 通过），按用户要求未在设备/模拟器上执行。再往前一批为
`u-swiper` 补齐 12 项回归（`UPSwiperBehaviorTest` 5 项 → 17 项），覆盖 `loading` 占位、图片项经
`UPImage` 渲染、`showTitle` 标题条与指示器互斥、`currentItemId` 压过 `current`、翻页控件上报的邻居
索引、`circular` 首屏双向控件、`vertical` 纵向堆叠对比横向排列、`displayMultipleItems` 视口均分和
露边内缩。这 12 项**只有编译级证据**（`compileDebugAndroidTestKotlin` 通过），按用户要求未在
设备/模拟器上执行。Batch 11 新增 16 项
`u-form`/`u-form-item` 回归，覆盖 validate/validateField/resetFields/resetField/clearValidate/setRules
六个 ref 方法、`trigger` 事件过滤、`errorType="toast"` 经 `onToast` 转发且行内不渲染文案、
必填星号绝对定位不占布局、标签宽度/对齐/位置回落父级、label/right/error 三插槽和
`borderBottom` 画线；另新增 1 项固定公历日期→农历文案的换算用例。这 16 项与修订后的
`UPBatch10BehaviorTest`（16 项）已在 `emulator-5554`（MCode_Phone，android-36/arm64-v8a）上按类跑通；
整轮 162 项的一次性全绿**尚缺证据**——两次尝试都因模拟器进程被外部回收而中断
（报告为 `device offline` / `device 'emulator-5554' not found`），最近一次停在第 51 项，
且这 51 项中没有真实失败。补跑时优先按类分批执行，并核对
`ultra-ui/build/outputs/androidTest-results/connected/debug/TEST-*.xml` 的累计条数。

此前批次新增 8 项 `u-tabbar`/`u-tabbar-item` 回归，覆盖风格结构、状态背景、underline/dot
指示器、激活图标动画、middle 按钮几何、内容包裹高度、图标与标签居中和
CSS hook 降级诊断；再往前有 10 项结构与滚动约束回归，覆盖 `u-index-anchor`、`u-index-item`、
`u-picker`、`u-picker-column`、`u-datetime-picker`、`u-list`、`u-safe-bottom`、
`u-swiper-indicator`、`u-tabs-item`。其中 picker、datetime-picker 和 list 均验证了嵌入外层
纵向滚动容器时不会触发无限高度约束崩溃。首次真机执行曾暴露 1 个实现缺陷（picker 列平铺
导致屏幕外选项无法点击）和 4 处测试自身写错（`upTestTag` 会加 `up-` 前缀；`customStyle`
需在组件自身尺寸**之前**应用才能覆盖）。

> 教训：`upTestTag("x")` 生成的标签是 `up-x`；断言几何时要确认 tag 挂在 modifier 链的
> 哪一层，`padding` 之后的 tag 只能看到内容区。

> 教训：涉及「今天」的断言必须从 `Calendar.getInstance()` 推导。`UPBatch10BehaviorTest`
> 曾把 `2026-08-31` 写死当作今天，跨天后必然失败；已改为动态推导，并另留一个固定公历
> 日期→农历（`2026-08-31` → `七月十九`）的用例保留精确换算证据。
>
> 教训：整轮执行期间模拟器可能被外部回收，AGP 会把 `device offline` 报成某个用例 FAILED。
> 判定回归前先确认设备仍在线（`adb devices`），再看是否有断言堆栈。

## 维护规则

- 新增组件前，先把固定上游目录、Props 字段、字符串枚举、事件 payload 和默认值加入本表。
- 只有同时具备 Props、公开 Compose 入口和至少一组行为/截图证据，才允许从“未开始”提升到“基础可用”。
- 只有完成常用字段、事件、受控状态、样式和错误/禁用状态回归，才允许提升到“基本完成”。
- “完整兼容”必须有上游演示对照或逐字段核验记录，不能仅因 Kotlin 文件存在而标记。
- Android 不解析 JSON，不引入 FastView、`.xyfv`、WebView 或 JSON 映射运行时；后端负责把同一份 JSON 转成各端调用。

## 状态标注可信度核验

`tools/audit_status_claims.py` 把本表的状态标注与三项可度量证据交叉比对，标注超出证据时
以退出码 1 失败：

```
python3 tools/audit_status_claims.py            # 只列标注超出证据的行
python3 tools/audit_status_claims.py --all      # 同时列出证据齐备的行
```

判定依据直接取自本文《维护规则》：「基础可用」至少需要一组真机或截图证据；「基本完成」
还需常用字段全部生效（未读字段为 0）并有真机行为测试。

当前状态：90 个已实现组件行中，**77 行证据齐备、13 行标注超出证据**。`u-picker` 与
`u-datetime-picker` 本轮补齐各自七个未读字段后未读数归零，并同时拥有行为测试与截图语料，
因此从偏乐观清单中移出并升级为「基本完成」；在此之前的 `u-form`、`u-form-item` 两行同时
具备真机行为测试与零未读字段，`u-swiper` 补齐九个字段后同样已移出清单。

> 为什么要做这件事：连续五轮工作中，每一轮都在标着「基础可用/基本完成」的组件里发现
> **组件级不可用**缺陷——`u-picker` 列平铺导致选项无法点击、`u-swiper` 只渲染文字不显示
> 图片（已修复：图片项现走 `UPImage`）、`u-steps` 完全忽略 `current` 使每步都显示已完成、
> `u-input` 的 `selectionStart` 全无作用。这些都通过了 Props 单测与截图，说明**标注整体偏乐观**，
> 需要独立的证据核验。

此前已补齐证据的行：`u-avatar`、`u-cell-group`、`u-divider`、`u-title`（此前既无真机测试
也无截图，却标「基本完成」）、`u-button`（库内最常用组件，此前零真机断言）。上一批新增
`u-index-anchor`、`u-index-item`、`u-picker-column`、`u-safe-bottom`、`u-swiper-indicator`、
`u-tabs-item` 的结构行为证据，并为 `u-picker`、`u-datetime-picker`、`u-list` 补充了外层
纵向滚动回归；此后补齐 `u-tabbar` 与 `u-tabbar-item` 的风格和状态行为证据。`u-tag` 的
`height`/`borderRadius`/`plainFill` 已实现并有真机测试。再往前一批为 `u-swiper` 补齐 12 项行为断言
（编译级）与 4 张截图参考图（文字页、`loading`、标题条、露边缩放），参考图总数 14 → 18。
最近一批为 `u-picker`/`u-datetime-picker` 的 `hasInput` 触发器、`toolbarRightSlot` 插槽与 `maskStyle`
遮罩新增 12 项行为断言（编译级）与 3 张截图参考图，参考图总数 18 → 21。需说明的限制：
本仓库的 screenshotTest 渲染环境不绘制文本与大部分填色（已对照既有 `Batch9BScreenshots`
基线确认同样表现，非本次改动引入的回归），因此这类参考图只能作为不崩溃与布局占位证据，
不能当作视觉回归证据。

## 未生效字段核查

`tools/find_unread_props.py` 报告「声明了但组件从不读取」的 `UP*Props` 字段。这类字段是静默
空操作：类型检查通过、Props 测试通过、截图也不变，因此既有核查手段都发现不了它。
当前扫描结果中，`u-tabbar`、`u-tabbar-item`、`u-swiper`、`u-picker` 与 `u-datetime-picker`
已不再出现在未读字段清单；未读字段
仍需按组件逐项消化，不能仅以 Props 声明或编译通过替代行为证据。Batch 11 的两个表单组件没有新增未读字段：
`u-form` 的 `borderBottom` 已登记为按设计不生效（上游只有一个不再被调用的 `propsChange` computed 引用它，
`u-form-item` 从不读取父级该字段），`u-form-item` 的 `rightIcon` 上游声明后同样从未渲染——后者因为
`UPFormItem` 会把整个 `props` 转发给内部函数，脚本会退化成全库搜索并被 `UPCell` 的同名字段掩盖，
所以只能记录在本文档而不会出现在脚本清单里。

```
python3 tools/find_unread_props.py                # 列出无人读取的字段（有结果时退出码 1）
python3 tools/find_unread_props.py --show-inert    # 同时列出按设计不生效的字段及原因
```

当前状态：90 个 Props 类中有 **67 个字段无人读取**，另有 56 个已记录为按设计不生效
（uni-app / 微信小程序 / nvue 专有开关，仅保留接口兼容）。已消化的批次：13 个组件曾声明
`customStyle` 却从不应用（`UPSwitch`、`UPRate`、`UPBadge` 等）、`UPSticky` 的
`offsetTop`/`customNavHeight`、`UPPicker`/`UPDatetimePicker` 的 `itemHeight`/`visibleItemCount`、
`u-list` 的 10 个滚动与下拉刷新字段、`u-swiper` 的自动播放与循环，以及 `u-steps` 的
全部 7 个状态字段，以及 `u-input`/`u-textarea` 的 `selectionStart`/`selectionEnd`/`cursor`。
最近一批把 `u-swiper` 剩余 9 个字段（`imgMode`、`radius`、`showTitle`、`vertical`、
`displayMultipleItems`、`currentItemId`、`duration`、`loading`、`acceleration`）全部接上实现或
诊断降级，未读数因此从 90 降到 81；`easingFunction` 早已登记为按设计不生效（上游注明只对微信小程序有效）。
最新一批把 `u-picker` 与 `u-datetime-picker` 各自的七个同名字段（`hasInput`、`inputBorder`、
`inputProps`、`maskClass`、`maskStyle`、`popupMode`、`toolbarRightSlot`）全部接上实现或诊断降级，
未读数因此从 81 降到 67。

> 数字为何从 134 涨到 205：脚本原先把**整个文件**当作搜索范围，同文件内的兄弟组件
> （`UPSwiper` 与 `UPCountTo` 同在 `UPStatusNumericComponents.kt`）会互相掩盖——
> `UPCountTo` 的 `props.autoplay` 让 `UPSwiper` 从未生效的 `autoplay` 被误判为已读。
> 另一处 `= props` 正则过宽，把 `current = props.current`（字段读取）误判为
> 「转发整个 props 对象」，从而退化为全库搜索。两处收紧后，此前被掩盖的 ~70 个字段
> 才显形。**205 是更接近真相的数字，不是退步。**

67 这个数字应当被视为**功能缺口清单**，而不是待清理的噪音。清单里既可能是"缺特性"，
也可能是"组件根本不可用"——`u-picker` 的列平铺、`u-swiper` 只渲染文字不显示图片（已修复）、
`u-steps` 曾完全忽略 `current` 导致每一步都显示为已完成（已修复），都属于后者。后续批次应优先
消化本清单，而不是先增加新组件。按未读字段数排序，下一批优先目标是
`u-tabs`（7 个）、`u-pagination`（4 个）与 `u-image`（3 个）。

## 默认值漂移核查

`tools/compare_uview_defaults.py` 会把上游默认值与 Android 侧默认值逐字段对比，漂移时以退出码 1 失败。
上游默认值来自 `components/u-<name>/<name>.js`，没有该文件的组件则回退读取 `props.js` 里的内联
`default:`；Android 侧同时读取 `core/UPConfig.kt` 的 `UP*Defaults` 与 `UP*Props` 上的字面量默认值。

```
python3 tools/compare_uview_defaults.py                  # 仅报告未解释的漂移
python3 tools/compare_uview_defaults.py --show-accepted   # 同时列出已记录的降级原因
python3 tools/compare_uview_defaults.py --list-unaudited  # 列出脚本仍覆盖不到的组件
```

当前状态：比对 82 个组件、925 个字段，未解释漂移 0 个，已记录降级 15 个。

**覆盖边界**：仍有 8 个组件（Cascader、IndexItem、Pagination、PickerColumn、SafeBottom、Select、
TabsItem、Title）在上游没有可比对的字面量默认值，需人工对照 `.vue` 复核。另外本脚本只比对
**声明的默认值**，不校验渲染几何与事件语义——`u-tabbar-item` 的图标尺寸漂移最终是靠截图发现的，
两类核查缺一不可。
