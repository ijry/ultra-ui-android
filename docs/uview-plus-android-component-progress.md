# uview-plus Android 组件复刻进度

> 用途：记录后端将同一份 JSON 生成到 uni-app、iOS、Android Kotlin 后，各端可使用的 uview-plus 组件接口与实现进度。

## 统计口径

- 上游来源：`/Users/admin/Documents/Repos/xyito/open/uview-plus/src/uni_modules/uview-plus/components`
- 上游固定提交：`b32377ce0500579830e537a20eef1a7c6c9cf806`
- 扫描日期：2026-08-20
- 上游目录总数：141 个
- 当前 Android 公开 `UP*Props`：82 个
- 当前 Android 已有公开 Compose 组件入口：82 个（另有 `UPToastHost` 等宿主辅助 API）
- 当前目标组件完成度：82 / 138 个可直接使用的上游 UI 组件目录，约 59.4%。其中 3 个是辅助模块目录，暂不计入 UI 组件分母。

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
| 6 | 媒体与内容 | `u-avatar` | `UPAvatar` / `UPAvatarProps` | 基本完成 | 高（Props） | 图片、文字、图标和形状已有 Compose 实现与测试。 |
| 7 | 媒体与内容 | `u-avatar-group` | `UPAvatarGroup` / `UPAvatarGroupProps` | 基本完成 | 高（Props） | 头像组基础布局已实现；溢出和间距视觉仍需上游对照。 |
| 8 | 原生交互 | `u-back-top` | `UPBackTop` / `UPBackTopProps` | 基础可用 | 中 | 返回顶部基础行为；父级滚动容器绑定仍需标准化。 |
| 9 | 基础展示 | `u-badge` | `UPBadge` / `UPBadgeProps` | 基本完成 | 高（Props） | 类型、颜色、徽标位置和最大值已有实现。 |
| 10 | 原生能力 | `u-barcode` | — | 未开始 | 暂无 | 条形码生成，待接入 Android 原生/成熟编码库。 |
| 11 | 布局 | `u-box` | — | 未开始 | 暂无 | 通用容器目录，待确认上游公开 Props 后实现。 |
| 12 | 基础展示 | `u-button` | `UPButton` / `UPButtonProps` | 基本完成 | 高（Props） | `type="primary"`、形状、加载态、图标和禁用态已有测试。 |
| 13 | 选择与日期 | `u-calendar` | — | 未开始 | 暂无 | 日历选择器，待建立日期值和范围事件契约。 |
| 14 | 选择与日期 | `u-calendar-strip` | — | 未开始 | 暂无 | 横向日期条，待实现日期滚动和选中状态。 |
| 15 | 媒体与内容 | `u-canvas` | — | 未开始 | 暂无 | Canvas 容器/绘制适配，需单独确认 Android 生成调用方式。 |
| 16 | 键盘与输入 | `u-car-keyboard` | — | 未开始 | 暂无 | 车牌键盘，待按原生输入法交互实现。 |
| 17 | 基础展示 | `u-card` | `UPCard` / `UPCardProps` | 基础可用 | 中 | 卡片基础样式和插槽容器已实现；细节视觉待对照。 |
| 18 | 选择与日期 | `u-cascader` | — | 未开始 | 暂无 | 多级联动选择，待确定生成事件 payload。 |
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
| 39 | 选择与日期 | `u-datetime-picker` | — | 未开始 | 暂无 | 日期时间选择器，待复刻列配置和时间值格式。 |
| 40 | 基础展示 | `u-divider` | `UPDivider` / `UPDividerProps` | 基本完成 | 高（Props） | 分割线方向、文字和样式已有实现。 |
| 41 | 列表与拖拽 | `u-dragsort` | — | 未开始 | 暂无 | 拖拽排序，待采用 Compose drag-and-drop 方案。 |
| 42 | 原生交互 | `u-dropdown` | `UPDropdown` / `UPDropdownProps` | 基础可用 | 中 | 下拉容器和组状态已有；Popup 定位和遮罩仍需加强。 |
| 43 | 原生交互 | `u-dropdown-item` | `UPDropdownItem` / `UPDropdownItemProps` | 基础可用 | 中 | 下拉项选择和图标已有；复杂内容插槽待补。 |
| 44 | 基础展示 | `u-empty` | `UPEmpty` / `UPEmptyProps` | 基本完成 | 高（Props） | 图标、描述、按钮和样式已有实现。 |
| 45 | 原生交互 | `u-float-button` | — | 未开始 | 暂无 | 浮动按钮，待实现拖动/吸附和安全区处理。 |
| 46 | 表单与协议 | `u-form` | — | 未开始 | 暂无 | 表单校验上下文，待建立 Kotlin validator 事件边界。 |
| 47 | 表单与协议 | `u-form-item` | — | 未开始 | 暂无 | 表单项标签、错误和校验展示待实现。 |
| 48 | 基础展示 | `u-gap` | `UPGap` / `UPGapProps` | 基本完成 | 高（Props） | 间隔尺寸和背景已有实现。 |
| 49 | 选择 | `u-goods-sku` | — | 未开始 | 暂无 | 商品规格选择器，待明确业务数据模型。 |
| 50 | 布局 | `u-grid` | `UPGrid` / `UPGridProps` | 基本完成 | 高（Props） | 列数、间距、边框和点击布局已有测试。 |
| 51 | 布局 | `u-grid-item` | `UPGridItem` / `UPGridItemProps` | 基本完成 | 高（Props） | 图标、文字和点击项已有实现。 |
| 52 | 原生交互 | `u-guide` | — | 未开始 | 暂无 | 新手引导遮罩和高亮定位待实现。 |
| 53 | 基础能力 | `u-icon` | `UPIcon` / `UPIconProps` | 基本完成 | 高（Props） | 已接入固定上游 icon font；图片图标和自定义字体仍有降级。 |
| 54 | 媒体与内容 | `u-image` | `UPImage` / `UPImageProps` | 基本完成 | 高（Props） | 加载、错误、裁剪模式和占位已有实现。 |
| 55 | 列表与索引 | `u-index-anchor` | `UPIndexAnchor` / `UPIndexAnchorProps` | 基础可用 | 中 | 索引锚点可渲染；联动滚动和 sticky 语义待补。 |
| 56 | 列表与索引 | `u-index-item` | `UPIndexItem` / `UPIndexItemProps` | 基础可用 | 中 | 索引项容器可用；完整索引定位待补。 |
| 57 | 列表与索引 | `u-index-list` | `UPIndexList` / `UPIndexListProps` | 基础可用 | 中 | 列表容器和锚点上下文已有；右侧索引触摸导航待补。 |
| 58 | 键盘与输入 | `u-input` | `UPInput` / `UPInputProps` | 基本完成 | 高（Props） | `modelValue/value`、清除、密码、前后缀和常用样式已有测试。 |
| 59 | 键盘与输入 | `u-keyboard` | — | 未开始 | 暂无 | 数字/自定义键盘容器待实现。 |
| 60 | 媒体与内容 | `u-lazy-load` | — | 未开始 | 暂无 | 图片懒加载容器，待结合 Compose lazy layout。 |
| 61 | 基础展示 | `u-line` | `UPLine` / `UPLineProps` | 基本完成 | 高（Props） | 横竖线、颜色、虚线和尺寸已有实现。 |
| 62 | 布局与进度 | `u-line-progress` | `UPLineProgress` / `UPLineProgressProps` | 基本完成 | 高（Props） | 进度、颜色、圆角和文字已有实现。 |
| 63 | 基础展示 | `u-link` | `UPLink` / `UPLinkProps` | 基本完成 | 高（Props） | 链接文字、下划线、图标和点击已有实现。 |
| 64 | 列表与索引 | `u-list` | `UPList` / `UPListProps` | 基础可用 | 中 | 列表容器和阈值字段已有；滚动事件需连接真实列表状态。 |
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
| 84 | 选择与日期 | `u-picker` | `UPPicker` / `UPPickerProps` | 基础可用 | 中 | modelValue/value/defaultIndex 和事件 payload 已修正；Popup、列滚动、动画待补。 |
| 85 | 选择与日期 | `u-picker-column` | `UPPickerColumn` / `UPPickerColumnProps` | 基础可用 | 中 | Props 为空契约的容器已提供；原生列滚动待补。 |
| 86 | 辅助模块 | `u-picker-data` | — | 未开始 | 暂无 | 选择器数据辅助目录，不单独作为 Android UI 组件。 |
| 87 | 原生交互 | `u-popover` | `UPPopover` / `UPPopoverProps` | 基础可用 | 中 | 内嵌面板展示可用；Popup 定位、长按触发和遮罩待补。 |
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
| 99 | 导航 | `u-safe-bottom` | `UPSafeBottom` / `UPSafeBottomProps` | 基础可用 | 高（Props） | Android navigation bar inset 已封装。 |
| 100 | 列表与索引 | `u-scroll-list` | `UPScrollList` / `UPScrollListProps` | 基础可用 | 中 | 横向滚动容器基础展示可用；滚动控制事件待补。 |
| 101 | 键盘与输入 | `u-search` | `UPSearch` / `UPSearchProps` | 基本完成 | 高（Props） | 输入、清除、搜索按钮和受控值已有实现。 |
| 102 | 基础展示 | `u-section` | — | 未开始 | 暂无 | 区块标题组件待建立。 |
| 103 | 选择 | `u-select` | `UPSelect` / `UPSelectProps` | 基础可用 | 中 | options、current、select/update 事件已有；Popup 和样式字段待补。 |
| 104 | 媒体与内容 | `u-short-video` | — | 未开始 | 暂无 | 短视频播放器涉及 ExoPlayer 和生命周期。 |
| 105 | 原生能力 | `u-signature` | — | 未开始 | 暂无 | 手写签名画布待实现。 |
| 106 | 通知与状态 | `u-skeleton` | `UPSkeleton` / `UPSkeletonProps` | 基础可用 | 中 | 骨架行、头像、标题和动画开关已有；细节尺寸待对照。 |
| 107 | 选择 | `u-slider` | — | 未开始 | 暂无 | 滑块待建立 range/step/双滑块事件契约。 |
| 108 | 导航 | `u-status-bar` | `UPStatusBar` / `UPStatusBarProps` | 基础可用 | 高（Props） | 状态栏高度和顶部 inset 已封装。 |
| 109 | 导航 | `u-steps` | `UPSteps` / `UPStepsProps` | 基础可用 | 中 | 步骤容器可用；current/direction/activeColor 传递仍需完善。 |
| 110 | 导航 | `u-steps-item` | `UPStepsItem` / `UPStepsItemProps` | 基础可用 | 中 | 空/轻量 Props 契约和内容渲染可用；组状态待补。 |
| 111 | 原生交互 | `u-sticky` | `UPSticky` / `UPStickyProps` | 基础可用 | 低 | 当前为可嵌入容器；真实吸顶和 offset 语义待实现。 |
| 112 | 导航 | `u-subsection` | `UPSubsection` / `UPSubsectionProps` | 基础可用 | 中 | 分段切换基础行为可用；样式和滚动模式待对照。 |
| 113 | 原生交互 | `u-swipe-action` | `UPSwipeAction` / `UPSwipeActionProps` | 基础可用 | 中 | 原生手势基础容器可用；真实滑动距离和动画待补。 |
| 114 | 原生交互 | `u-swipe-action-item` | `UPSwipeActionItem` / `UPSwipeActionItemProps` | 基础可用 | 中 | 操作项和按钮回调可用；完整手势状态待补。 |
| 115 | 媒体与内容 | `u-swiper` | `UPSwiper` / `UPSwiperProps` | 基础可用 | 中 | 列表、索引和 indicator 基础渲染可用；自动播放和手势待补。 |
| 116 | 媒体与内容 | `u-swiper-indicator` | `UPSwiperIndicator` / `UPSwiperIndicatorProps` | 基础可用 | 中 | 指示器静态状态可用；复杂样式待对照。 |
| 117 | 选择 | `u-switch` | `UPSwitch` / `UPSwitchProps` | 基本完成 | 高（Props） | 受控值、禁用、颜色和 change/update 事件已有测试。 |
| 118 | 导航 | `u-tabbar` | — | 未开始 | 暂无 | 底部导航容器待实现。 |
| 119 | 导航 | `u-tabbar-item` | — | 未开始 | 暂无 | 底部导航项待实现。 |
| 120 | 表格 | `u-table` | — | 未开始 | 暂无 | 表格容器待建立列宽和滚动契约。 |
| 121 | 表格 | `u-table2` | — | 未开始 | 暂无 | 第二版表格，待确认与 `u-table` 的 API 差异。 |
| 122 | 导航 | `u-tabs` | `UPTabs` / `UPTabsProps` | 基础可用 | 中 | tabs/current/change 基础行为可用；滚动、粘性和样式字段待补。 |
| 123 | 导航 | `u-tabs-item` | `UPTabsItem` / `UPTabsItemProps` | 基础可用 | 中 | 空/轻量 Props 契约和自定义内容可用。 |
| 124 | 导航 | `u-tabs-pro` | — | 未开始 | 暂无 | Pro 标签页待确认专属字段和事件。 |
| 125 | 基础展示 | `u-tag` | `UPTag` / `UPTagProps` | 基本完成 | 高（Props） | 类型、形状、图标、关闭和颜色已有测试。 |
| 126 | 表格 | `u-td` | — | 未开始 | 暂无 | 表格单元格待随表格体系实现。 |
| 127 | 基础展示 | `u-text` | `UPText` / `UPTextProps` | 基本完成 | 高（Props） | 文本截断、链接、前后缀图标和样式已有实现。 |
| 128 | 键盘与输入 | `u-textarea` | `UPTextarea` / `UPTextareaProps` | 基本完成 | 高（Props） | 多行输入、字数、清除和受控值已有实现。 |
| 129 | 表格 | `u-th` | — | 未开始 | 暂无 | 表头单元格待随表格体系实现。 |
| 130 | 基础展示 | `u-title` | `UPTitle` / `UPTitleProps` | 基本完成 | 高（Props） | 标题、装饰线和对齐样式已有实现。 |
| 131 | 原生交互 | `u-toast` | `UPToast` / `UPToastProps` | 基本完成 | 高（Props） | Toast 原生展示和 `UPToastHost` 宿主已有；队列细节待补。 |
| 132 | 原生交互 | `u-toolbar` | — | 未开始 | 暂无 | 工具栏待确认与导航/输入场景的复用边界。 |
| 133 | 原生交互 | `u-tooltip` | `UPTooltip` / `UPTooltipProps` | 基础可用 | 中 | 内嵌提示和 click 基础行为可用；longpress/定位待补。 |
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
| Android 已建立 Props/API | 82 |
| 基本完成 | 40 |
| 基础可用 | 42 |
| Props 已建 | 0 |
| 未开始（含辅助模块） | 59 |
| 完整兼容 | 0 |

## 当前已实现组件分批

| 批次 | 组件范围 | 数量 | 当前判断 |
| --- | --- | ---: | --- |
| 基础组件 | button、tag、badge、divider、gap、line、link、text、title、overlay、popup、modal、toast、cell、cell-group、image、avatar、avatar-group、empty、loading-page、loadmore、input、textarea、search、code-input、switch、rate、number-box、checkbox、checkbox-group、radio、radio-group、row、col、grid、grid-item、line-progress、circle-progress | 38 | 基本完成；仍需逐字段视觉回归。 |
| 基础能力 | icon、loading-icon | 2 | 基本完成；自定义图片/字体能力存在平台降级。 |
| Batch 9A 原生交互 | alert、action-sheet、notify、back-top、card、collapse、collapse-item、dropdown、dropdown-item、notice-bar | 10 | 基础可用；全局弹层、滚动和动画语义仍需加强。 |
| Batch 9B 导航与更多 | navbar、navbar-mini、status-bar、safe-bottom、tabs、tabs-item、subsection、steps、steps-item、list、list-item、index-list、index-item、index-anchor、scroll-list、popover、tooltip、sticky、swipe-action、swipe-action-item、swiper、swiper-indicator、skeleton、read-more、column-notice、row-notice、count-to、count-down、picker、picker-column、pagination、select | 32 | 基础可用；部分组件已做受控字段修正，但还不是完整上游行为复刻。 |

## 下一批推荐顺序

1. **选择与日期**：`u-calendar`、`u-datetime-picker`、`u-cascader`、`u-slider`、`u-tabbar`。这些组件直接影响后端 JSON 生成后的常用业务表单和导航场景。
2. **表单体系**：`u-form`、`u-form-item`、`u-agreement`、`u-upload`、`u-album`。需要先确定 Android 回调 payload 和权限/文件 URI 边界。
3. **列表与数据展示**：`u-pull-refresh`、`u-virtual-list`、`u-refresh-virtual-list`、`u-waterfall`、`u-table`、`u-td`、`u-th`、`u-tr`。
4. **原生能力**：`u-qrcode`、`u-barcode`、`u-signature`、`u-copy`、`u-city-locate`、`u-short-video`、`u-pdf-reader`。
5. **内容解析与复杂业务**：`u-markdown`、`u-parse`、`u-tree`、`u-goods-sku`、`u-novel-reader`、`u-tabs-pro`。

## 维护规则

- 新增组件前，先把固定上游目录、Props 字段、字符串枚举、事件 payload 和默认值加入本表。
- 只有同时具备 Props、公开 Compose 入口和至少一组行为/截图证据，才允许从“未开始”提升到“基础可用”。
- 只有完成常用字段、事件、受控状态、样式和错误/禁用状态回归，才允许提升到“基本完成”。
- “完整兼容”必须有上游演示对照或逐字段核验记录，不能仅因 Kotlin 文件存在而标记。
- Android 不解析 JSON，不引入 FastView、`.xyfv`、WebView 或 JSON 映射运行时；后端负责把同一份 JSON 转成各端调用。
