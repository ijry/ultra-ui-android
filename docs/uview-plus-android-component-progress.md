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
| 1 | 原生交互 | `u-action-sheet` | `UPActionSheet` / `UPActionSheetProps` | 基础可用 | 中 | Compose 原生面板；`safeAreaInsetBottom` 决定面板是否避让底部导航栏，有真机断言。降级：`openType` 是微信开放能力按钮（getUserInfo/contact/launchApp 等），Android 无对应物；`index` 是 Android 兼容别名，上游按位置解析选项。窗口级弹层仍需加强。 |
| 2 | 辅助模块 | `u-action-sheet-data` | — | 未开始 | 暂无 | 操作菜单数据辅助目录，不单独作为 Android UI 组件。 |
| 3 | 表单与协议 | `u-agreement` | — | 未开始 | 暂无 | 协议勾选/链接组合组件，待按上游字段建立。 |
| 4 | 媒体与内容 | `u-album` | — | 未开始 | 暂无 | 相册选择与预览，涉及系统权限和媒体选择器。 |
| 5 | 原生交互 | `u-alert` | `UPAlert` / `UPAlertProps` | 基础可用 | 中 | 原生确认提示封装；按钮回调已覆盖，复杂插槽仍待补齐。 |
| 6 | 媒体与内容 | `u-avatar` | `UPAvatar` / `UPAvatarProps` | 基本完成 | 高（Props） | 图片、文字、图标和形状已有 Compose 实现与测试。降级：`randomBgColor` 未随机取色，`colorIndex` 缺省固定取第 0 号色（上游 `''` 表示随机），以保证截图可复现。 |
| 7 | 媒体与内容 | `u-avatar-group` | `UPAvatarGroup` / `UPAvatarGroupProps` | 基本完成 | 高（Props） | 头像组基础布局已实现；溢出和间距视觉仍需上游对照。 |
| 8 | 原生交互 | `u-back-top` | `UPBackTop` / `UPBackTopProps` | 基础可用 | 高（Props） | 返回顶部基础行为；`duration` 按上游 `uni.pageScrollTo` 的时长语义解析后随新增的 `onScrollToTop(durationMillis)` 回调交给宿主，有真机断言。父级滚动容器绑定仍需标准化——上游由 `uni` 全局 API 滚动页面，Android 的滚动容器归宿主所有。 |
| 9 | 基础展示 | `u-badge` | `UPBadge` / `UPBadgeProps` | 基本完成 | 高（Props） | 类型、颜色、徽标位置和最大值已有实现；`offset` 在 `absolute=true` 时按 `[top, right]` 内推徽标（只给一个值时两边同用），有真机断言。 |
| 10 | 原生能力 | `u-barcode` | — | 未开始 | 暂无 | 条形码生成，待接入 Android 原生/成熟编码库。 |
| 11 | 布局 | `u-box` | — | 未开始 | 暂无 | 通用容器目录，待确认上游公开 Props 后实现。 |
| 12 | 基础展示 | `u-button` | `UPButton` / `UPButtonProps` | 基本完成 | 高（Props） | `type="primary"`、形状、加载态、图标和禁用态已有测试。 |
| 13 | 选择与日期 | `u-calendar` | `UPCalendar` / `UPCalendarProps` | 基础可用 | 中 | Compose 原生日历已支持月份切换及 single/multiple/range 基础选择；农历、时间精度、formatter 等尚未完整复刻。 |
| 14 | 选择与日期 | `u-calendar-strip` | — | 未开始 | 暂无 | 横向日期条，待实现日期滚动和选中状态。 |
| 15 | 媒体与内容 | `u-canvas` | — | 未开始 | 暂无 | Canvas 容器/绘制适配，需单独确认 Android 生成调用方式。 |
| 16 | 键盘与输入 | `u-car-keyboard` | — | 未开始 | 暂无 | 车牌键盘，待按原生输入法交互实现。 |
| 17 | 基础展示 | `u-card` | `UPCard` / `UPCardProps` | 基础可用 | 中 | 卡片基础样式和插槽容器已实现；细节视觉待对照。 |
| 18 | 选择与日期 | `u-cascader` | `UPCascader` / `UPCascaderProps` | 基本完成 | 高（Props） | `data` 多级路径、value/label/children key 与 change/confirm 事件之外，`headerDirection="column"` 把并排的层级改为纵向堆叠（对应上游换用 `u-steps` 的长标签排版）、`maskCloseAble` 与 `closeOnClickOverlay` 共同决定点击面板空白处是否取消并关闭，均有真机断言。弹层动画仍需窗口级弹层。 |
| 19 | 导航 | `u-cate-tab` | — | 未开始 | 暂无 | 分类导航，待建立横向/纵向布局契约。 |
| 20 | 基础展示 | `u-cell` | `UPCell` / `UPCellProps` | 基本完成 | 高（Props） | 标题、描述、图标、箭头和点击行为已有测试；`iconStyle`/`rightIconStyle` 分别作用于左图标与右侧箭头，并按 `size="large"` 切换 22/18 与 18/16 两档字号，禁用态右图标改用禁用色，均有真机断言。 |
| 21 | 基础展示 | `u-cell-group` | `UPCellGroup` / `UPCellGroupProps` | 基本完成 | 高（Props） | 分组容器和边界样式已有实现。 |
| 22 | 选择 | `u-checkbox` | `UPCheckbox` / `UPCheckboxProps` | 基本完成 | 高（Props） | 受控值、形状、颜色、标签和组上下文已有测试。 |
| 23 | 选择 | `u-checkbox-group` | `UPCheckboxGroup` / `UPCheckboxGroupProps` | 基本完成 | 高（Props） | 多选组受控值和布局已有测试。降级：`name` 上游 `u-checkbox-group.vue` 自身从不读取（`change` 事件回传的是子项的 `name`），Android 同样保留字段但不生效。 |
| 24 | 选择 | `u-choose` | — | 未开始 | 暂无 | 选择组合控件，待核对上游当前 API。 |
| 25 | 布局与进度 | `u-circle-progress` | `UPCircleProgress` / `UPCircleProgressProps` | 基本完成 | 高（Props） | 环形进度、颜色、宽度和文字已有实现。 |
| 26 | 原生能力 | `u-city-locate` | — | 未开始 | 暂无 | 城市定位，涉及定位权限和系统服务。 |
| 27 | 键盘与输入 | `u-code` | — | 未开始 | 暂无 | 验证码/代码展示辅助组件，待确认与 `u-code-input` 的边界。 |
| 28 | 键盘与输入 | `u-code-input` | `UPCodeInput` / `UPCodeInputProps` | 基本完成 | 高（Props） | 输入长度、掩码、颜色和回调已有实现；原生焦点细节待补。 |
| 29 | 布局 | `u-col` | `UPCol` / `UPColProps` | 基本完成 | 高（Props） | 栅格列宽、偏移和响应式基础字段已有实现。 |
| 30 | 内容面板 | `u-collapse` | `UPCollapse` / `UPCollapseProps` | 基础可用 | 中 | 折叠状态和组上下文已有；动画和部分事件待补。 |
| 31 | 内容面板 | `u-collapse-item` | `UPCollapseItem` / `UPCollapseItemProps` | 基本完成 | 高（Props） | 子项展开行为、插槽与图标之外，`duration` 驱动面板在 0 与测得高度之间的展开/收起动画（收起过程中面板仍在树内，动画结束才移除，下方兄弟节点随之滑动），`cellCustomStyle` 作用于标题行、`customStyle` 作用于外层，均有真机断言。刻意差异：上游 `<view class="u-collapse-item">` 从不绑定 `customStyle`（mixin 声明了但模板未用），Android 让它在外层生效。降级：`cellCustomClass` 是 CSS 类名，无 Compose 等价物。 |
| 32 | 选择与日期 | `u-color-picker` | — | 未开始 | 暂无 | 颜色选择器，待建立颜色值和面板交互契约。 |
| 33 | 通知与状态 | `u-column-notice` | `UPColumnNotice` / `UPColumnNoticeProps` | 基本完成 | 中 | 基于通知栏封装，随 `u-notice-bar` 一并补齐：`duration` 作为轮播间隔逐条切换并循环、`disableTouch=false` 时可上下拖动翻页，均有真机断言。 |
| 34 | 工具 | `u-copy` | — | 未开始 | 暂无 | 剪贴板复制动作，待确认是否以无 UI action API 提供。 |
| 35 | 数值与时间 | `u-count-down` | `UPCountDown` / `UPCountDownProps` | 基本完成 | 高（Props） | 新增 `UPCountDownController` 提供上游 ref 上的 `start()`/`pause()`/`reset()`（`start()` 在运行中直接返回，`pause()` 保留余量，`reset()` 复位后按 `autoStart` 决定是否重开）；计时改为上游的截止时间基准（`endTime = 此刻 + 剩余`，每拍重新读时钟）而非固定递减，`millisecond` 在 30ms 宏拍与 50ms 微拍之间切换且宏拍按 `isSameSecond` 抑制同秒重绘；`format` 复刻 `parseFormat` 的降级链（缺 `DD` 则天折进小时，缺 `HH` 折进分钟，依此类推，`SSS` 补三位且只替换首个匹配），单测逐条对照 node 跑出的上游读数，另有 8 项真机断言与作用域插槽。 |
| 36 | 数值与时间 | `u-count-to` | `UPCountTo` / `UPCountToProps` | 基本完成 | 高（Props） | 数字格式和回调之外，改为真实逐帧动画：`withFrameMillis` 每帧推进并回调 `onChange`，`useEasing` 在上游 ease-out-expo 曲线（`(c·(-2^(-10t/d)+1)·1024)/1023+b`）与线性斜坡之间切换，向上/向下计数都在 `endVal` 处收敛，`duration<=0` 直接跳到终值；单测逐点对照上游公式，另有真机断言。 |
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
| 54 | 媒体与内容 | `u-image` | `UPImage` / `UPImageProps` | 基本完成 | 高（Props） | 加载、错误、裁剪模式和占位已有实现；`loadingIcon` 决定加载态图标、`fade` 与 `duration` 共同驱动加载完成后的淡入（`fade=false` 时时长归零），均有真机断言。刻意差异：上游 transition 时长写死 1000ms 且 `duration` 在模板中被注释掉，Android 让 `duration` 真实生效。降级：`showMenuByLongpress` 仅微信小程序有效，Android 保留字段但不生效，缺省 `false`；`errorIcon` 之外的自定义错误插槽待补。 |
| 55 | 列表与索引 | `u-index-anchor` | `UPIndexAnchor` / `UPIndexAnchorProps` | 基础可用 | 中 | 索引锚点可渲染，选项对象名称、尺寸和自定义样式已有真机断言；联动滚动和 sticky 语义待补。 |
| 56 | 列表与索引 | `u-index-item` | `UPIndexItem` / `UPIndexItemProps` | 基础可用 | 中 | 索引项容器可用，自定义内容与样式已有真机断言；完整索引定位待补。 |
| 57 | 列表与索引 | `u-index-list` | `UPIndexList` / `UPIndexListProps` | 基础可用 | 中 | 右侧索引条已按 `indexList` 渲染并可点击（`onIndexClick` 回调索引字符与序号）、`activeColor`/`inactiveColor` 区分选中态、`itemMargin` 控制间距、`customNavHeight` 让出导航栏高度、`safeBottomFix` 避让底部安全区；均有真机断言。此前完全不渲染索引条。降级：`sticky` 锚点吸顶需宿主滚动容器回传偏移。 |
| 58 | 键盘与输入 | `u-input` | `UPInput` / `UPInputProps` | 基本完成 | 高（Props） | `modelValue/value`、清除、密码、前后缀和常用样式已有测试；`selectionStart`/`selectionEnd`/`cursor` 已通过 `TextFieldValue` 生效（聚焦时应用、越界自动钳制）。降级：`adjustPosition`、`autoBlur`、`cursorSpacing`、`fixed`、`holdKeyboard`、`disableDefaultPadding`、`ignoreCompositionEvent`、`placeholderClass` 为 uni-app/小程序专有，保留字段但不生效。 |
| 59 | 键盘与输入 | `u-keyboard` | — | 未开始 | 暂无 | 数字/自定义键盘容器待实现。 |
| 60 | 媒体与内容 | `u-lazy-load` | — | 未开始 | 暂无 | 图片懒加载容器，待结合 Compose lazy layout。 |
| 61 | 基础展示 | `u-line` | `UPLine` / `UPLineProps` | 基本完成 | 高（Props） | 横竖线、颜色、虚线和尺寸已有实现。 |
| 62 | 布局与进度 | `u-line-progress` | `UPLineProgress` / `UPLineProgressProps` | 基本完成 | 高（Props） | 进度、颜色、圆角和文字已有实现。 |
| 63 | 基础展示 | `u-link` | `UPLink` / `UPLinkProps` | 基本完成 | 高（Props） | 链接文字、下划线、图标和点击已有实现。 |
| 64 | 列表与索引 | `u-list` | `UPList` / `UPListProps` | 基本完成 | 高（Props） | `height`/`width` 限定视口、`scrollable`、`lowerThreshold`/`upperThreshold` 触边事件（按穿越沿触发一次）、`scrollTop`+`scrollWithAnimation` 程序化滚动、`refresherEnabled` 系列下拉刷新均已生效并有真机测试；`scrollIntoView` 按子项上报的 `anchor` 偏移滚动到对应位置（`scrollWithAnimation` 决定是否带动画），有真机断言；未指定高度时可嵌入外层纵向滚动容器。降级：`pagingEnabled` 上游 `u-list.vue` 自身从不读取；`preLoadScreen` 上游只在 `u-list-item.vue` 算出一个 `show` 标记却从不绑定到模板，因此实际不会跳过任何项。 |
| 65 | 列表与索引 | `u-list-item` | `UPListItem` / `UPListItemProps` | 基本完成 | 高（Props） | 列表项容器可用；`anchor` 向父级 `UPList` 上报自身在滚动列内的偏移，供 `scrollIntoView` 定位，同时把 testTag 收敛为 `up-list-item-<anchor>`（无 anchor 时保持 `up-list-item`），有真机断言。复杂 slot 与分割线待补。 |
| 66 | 基础能力 | `u-loading-icon` | `UPLoadingIcon` / `UPLoadingIconProps` | 基本完成 | 高（Props） | 原生 Compose 加载动画和 icon font 兼容已有测试。 |
| 67 | 通知与状态 | `u-loading-page` | `UPLoadingPage` / `UPLoadingPageProps` | 基本完成 | 高（Props） | 加载页文字、图标、背景和状态已有实现。 |
| 68 | 通知与状态 | `u-loadmore` | `UPLoadmore` / `UPLoadmoreProps` | 基本完成 | 高（Props） | 加载/没有更多/点击加载状态已有实现。 |
| 69 | 内容与解析 | `u-markdown` | — | 未开始 | 暂无 | Markdown 渲染待选定 Android 原生解析方案。 |
| 70 | 键盘与输入 | `u-message-input` | — | 未开始 | 暂无 | 消息输入框组合控件，待复用输入和附件能力。 |
| 71 | 原生交互 | `u-modal` | `UPModal` / `UPModalProps` | 基本完成 | 高（Props） | 原生 Dialog/Compose 弹窗、确认取消和样式已有实现；`negativeTop` 以负 margin 上移弹窗避让键盘、`duration` 驱动淡入（`zoom=true` 时叠加 0.8→1 缩放），均有真机断言。差异：上游把 `duration` 透传给 `u-popup` 的过渡，Android 的 popup 无自带过渡，改由 modal 面板自身承载。 |
| 72 | 导航 | `u-navbar` | `UPNavbar` / `UPNavbarProps` | 基本完成 | 高（Props） | 安全区、标题和 icon 之外，`border` 画 0.5px 下边框、`fixed` 提升到 `zIndex=11`（对应 `.u-navbar--fixed`）、`autoBack` 决定左键是否触发新增的 `onBack` 宿主回调，均有真机断言。降级：`position: fixed` 的实际定位由宿主布局决定；`statusBarBgColor` 上游注明仅为兼容保留（状态栏统一用 `bgColor`），Android 显式上报诊断而非静默丢弃。 |
| 73 | 导航 | `u-navbar-mini` | `UPNavbarMini` / `UPNavbarMiniProps` | 基本完成 | 高（Props） | 迷你导航和 icon 之外，`fixed` 按 `.u-navbar-mini--fixed` 偏移 `left: 20px; top: 10px` 并提升到 `zIndex=11`、`autoBack` 在 `leftClick` 之后触发新增的 `onBack` 宿主回调（与上游先 emit 再 `navigateBack` 的顺序一致），均有真机断言。降级：`position: fixed` 的实际定位由宿主布局决定。 |
| 74 | 通知与状态 | `u-no-network` | — | 未开始 | 暂无 | 无网络状态页待实现。 |
| 75 | 通知与状态 | `u-notice-bar` | `UPNoticeBar` / `UPNoticeBarProps` | 基本完成 | 高（Props） | 通知文字、方向和点击之外：`direction="row"` 按 `t = s / v` 实现真实横向跑马灯（文字从右边缘进、越过左边缘出，`speed` 为每秒像素数，宽度按实测值算）；`direction="column"` 或 `step=true` 走逐条轮播，`duration` 作为切换间隔并循环；`disableTouch=false` 时可上下拖动翻页，均有真机断言。 |
| 76 | 原生交互 | `u-notify` | `UPNotify` / `UPNotifyProps` | 基础可用 | 中 | 顶部通知基础展示可用；全局 host 生命周期待标准化。 |
| 77 | 内容与解析 | `u-novel-reader` | — | 未开始 | 暂无 | 小说阅读器业务组件，不纳入当前基础组件批次。 |
| 78 | 数值与时间 | `u-number-box` | `UPNumberBox` / `UPNumberBoxProps` | 基本完成 | 高（Props） | 步进、范围、精度、禁用和受控值已有测试；`longPress` 按上游时序实现长按连续加减（按住 600ms 进入长按、之后每 250ms 步进一次，松手时 `@tap` 仍照常再走一次），有真机断言。降级：`cursorSpacing` 是 uni-app 的键盘避让提示，Android 由 `windowSoftInputMode` 处理，记为按设计不生效。 |
| 79 | 键盘与输入 | `u-number-keyboard` | — | 未开始 | 暂无 | 数字键盘待复刻。 |
| 80 | 原生交互 | `u-overlay` | `UPOverlay` / `UPOverlayProps` | 基本完成 | 高（Props） | 原生 Compose 遮罩、透明度和点击关闭已有实现；`duration` 驱动 `opacity` 从 0 淡入（`duration=0` 首帧即最终不透明度），有真机断言。 |
| 81 | 选择与日期 | `u-pagination` | `UPPagination` / `UPPaginationProps` | 基本完成 | 高（Props） | `layout` 按 `total, prev, pager, next, sizes` 逐段解析并按序渲染，`pageSize`/`total` 驱动完整页码算法（`pagerCount` 限定窗口、省略号补位）、`buttonBgColor`/`buttonBorderColor` 作用于前后翻页按钮、`hideOnSinglePage` 单页时整体隐藏、`pageSizes` 提供每页条数轮转并回传 `onUpdatePageSize`/`onSizeChange`，均有真机断言。刻意差异：`hideOnSinglePage` 在上游只声明未被使用，Android 按字段语义真实实现；`sizes` 段上游是 `<select>` 下拉，Android 改为点击轮转候选值。 |
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
| 92 | 选择 | `u-radio` | `UPRadio` / `UPRadioProps` | 基本完成 | 高（Props） | 单选形状、组上下文、颜色和标签已有测试。降级：`color` 上游 `u-radio.vue` 自身从不读取（勾选色走 `iconColor`/`activeColor`），Android 同样保留字段但不生效。 |
| 93 | 选择 | `u-radio-group` | `UPRadioGroup` / `UPRadioGroupProps` | 基本完成 | 高（Props） | 受控值、布局和组状态已有测试。降级：`label`/`name` 上游 `u-radio-group.vue` 自身从不读取，也不在子项拉取的 `parentData` 键里，Android 同样保留字段但不生效。 |
| 94 | 选择 | `u-rate` | `UPRate` / `UPRateProps` | 基本完成 | 高（Props） | 评分、半星、颜色、数量和点击已有实现。 |
| 95 | 内容面板 | `u-read-more` | `UPReadMore` / `UPReadMoreProps` | 基本完成 | 高（Props） | 高度截断和 controlled alias 已修正；`textIndent` 按 `em`/`px`/裸数字解析后缩进正文（`em` 对 `fontSize` 求值）、`shadowStyle` 仅在收起态作用于展开按钮区（展开后按上游 `innerShadowStyle` 归空），均有真机断言。真实测量与展开动画待补。 |
| 96 | 列表与索引 | `u-refresh-virtual-list` | — | 未开始 | 暂无 | 刷新虚拟列表待结合 lazy/scroll 状态实现。 |
| 97 | 布局 | `u-row` | `UPRow` / `UPRowProps` | 基本完成 | 高（Props） | gutter、justify、align 和 slot 布局已有测试。 |
| 98 | 通知与状态 | `u-row-notice` | `UPRowNotice` / `UPRowNoticeProps` | 基本完成 | 中 | 基于通知栏封装，随 `u-notice-bar` 一并补齐真实横向跑马灯：`speed` 为每秒像素数，一个循环覆盖「容器宽 + 文字宽」，有真机断言。 |
| 99 | 导航 | `u-safe-bottom` | `UPSafeBottom` / `UPSafeBottomProps` | 基础可用 | 高（Props） | Android navigation bar inset 已封装并有真机断言。 |
| 100 | 列表与索引 | `u-scroll-list` | `UPScrollList` / `UPScrollListProps` | 基本完成 | 中 | 内容已可横向滚动，并按上游默认（`indicator: true`）渲染指示器：`indicatorWidth` 定轨道宽、`indicatorBarWidth` 定滑块宽、`indicatorColor`/`indicatorActiveColor` 分别着色轨道与滑块、`indicatorStyle` 可再覆盖样式，滑块位置跟随滚动进度；均有真机断言。此前既不滚动也不渲染指示器。 |
| 101 | 键盘与输入 | `u-search` | `UPSearch` / `UPSearchProps` | 基本完成 | 高（Props） | 输入、清除、搜索按钮和受控值已有实现。 |
| 102 | 基础展示 | `u-section` | — | 未开始 | 暂无 | 区块标题组件待建立。 |
| 103 | 选择 | `u-select` | `UPSelect` / `UPSelectProps` | 基础可用 | 高（Props） | options、current、select/update 事件已有；`showOptionsLabel` 决定触发器印选中项标签还是静态 `label`、`optionsWidth` 按 `normalizedOptionsWidth` 语义（空值交回父级、数字当 px）限定选项面板宽度，均有真机断言。窗口级 Popup 与其余样式字段待补。 |
| 104 | 媒体与内容 | `u-short-video` | — | 未开始 | 暂无 | 短视频播放器涉及 ExoPlayer 和生命周期。 |
| 105 | 原生能力 | `u-signature` | — | 未开始 | 暂无 | 手写签名画布待实现。 |
| 106 | 通知与状态 | `u-skeleton` | `UPSkeleton` / `UPSkeletonProps` | 基本完成 | 高（Props） | 骨架行、头像、标题和动画开关之外，`rowsWidth` 复刻 `rowsArray` 的取值链——数组按行取值、越界或未给时首几行 100%、末行固定 70%，百分比换算为可用宽度的比例、px 走绝对宽度；`avatarShape` 在 circle/square 之间切换头像圆角并对未知值回落诊断，均有真机断言。 |
| 107 | 选择 | `u-slider` | `UPSlider` / `UPSliderProps` | 基本完成 | 高（Props） | 单值、range、step 量化和 changing/change 手势之外：`height` 按上游 `sizeLocal` 语义覆盖 `size` 作为轨道厚度、`length` 限定轨道自身轴向长度（`auto` 交回父级测量）、`innerStyle` 作用于轨道行且行高按 `blockSize`（range 且 `showValue` 时 +24）计算、`blockStyle` 作用于滑块并可改其尺寸圆角，均有真机断言。降级：`useNative` 要求 uni-app 平台 `<slider>`，本库只依赖 Compose foundation，回落到自绘轨道并上报诊断（上游对 range 同样不用原生控件）。原生无障碍语义仍待完善。 |
| 108 | 导航 | `u-status-bar` | `UPStatusBar` / `UPStatusBarProps` | 基础可用 | 高（Props） | 状态栏高度和顶部 inset 已封装。 |
| 109 | 导航 | `u-steps` | `UPSteps` / `UPStepsProps` | 基本完成 | 中 | `current` 驱动 finish/process/wait/error 四态、`direction` 控制横纵布局、`activeColor`/`inactiveColor`/`dot`/`activeIcon`/`inactiveIcon` 均已生效并有真机测试。 |
| 110 | 导航 | `u-steps-item` | `UPStepsItem` / `UPStepsItemProps` | 基本完成 | 中 | 按索引与父级 `current` 推导状态：已完成显示 ✓、当前步为实心序号、未达步为灰色序号、`error` 显示 ✕；`iconSize`（对齐上游 17）与 `itemStyle` 已生效。 |
| 111 | 原生交互 | `u-sticky` | `UPSticky` / `UPStickyProps` | 基础可用 | 中 | 当前为可嵌入容器，`offsetTop` + `customNavHeight` 已按上游折算为顶部偏移，`zIndex` 缺省取上游 `zIndex.sticky`（970）、`disabled` 同时撤掉顶部偏移与层级提升，均有真机断言。降级：`index` 上游仅作调用方标识、自身从不读取。真实滚动吸顶仍待实现（需宿主滚动容器回传偏移）。 |
| 112 | 导航 | `u-subsection` | `UPSubsection` / `UPSubsectionProps` | 基本完成 | 高（Props） | `mode` 复刻两种形态：`button` 在灰底轨道内滑动白色药丸（34px 高、3px 内边距），`subsection` 给每项描 1px 边框并让 `activeColor` 滑块托住白色文字（32px 高）；滑块按测得的项宽平移并以 300ms 过渡，`bold`/`fontSize` 作用于激活项文字，`activeColorKeyName`/`inactiveColorKeyName` 从列表项对象读取逐项颜色覆盖（优先级高于 `activeColor`/`inactiveColor`），`disabled` 拦截点击并整体切换到禁用色，均有真机断言。 |
| 113 | 原生交互 | `u-swipe-action` | `UPSwipeAction` / `UPSwipeActionProps` | 基本完成 | 中 | 父级协调已实现：`autoClose` 打开一项时关闭其余项、`opendItem` 置 false 触发 closeAll、并通过 `onUpdateOpendItem` 上报开合状态；均有真机断言。 |
| 114 | 原生交互 | `u-swipe-action-item` | `UPSwipeActionItem` / `UPSwipeActionItemProps` | 基本完成 | 中 | 横向拖动超过 `threshold`（默认 20）才展开、`duration` 驱动展开动画、`show` 为受控开合状态、`closeOnClick` 点击后收起、`disabled` 忽略手势；均有真机断言。此前按钮由 `show` 常驻显示且完全没有手势。 |
| 115 | 媒体与内容 | `u-swiper` | `UPSwiper` / `UPSwiperProps` | 基本完成 | 中 | `autoplay`+`interval` 定时切换、`circular` 末尾回头、`previousMargin`/`nextMargin` 露边、`indicatorStyle` 已生效；本批补齐 `imgMode`（图片项经 `UPImage` 渲染，`keyName`/`getSource` 对齐上游）、`radius` 圆角裁剪、`showTitle` 半透明标题条（显示标题时隐藏指示器）、`vertical` 纵向布局与纵向拖拽、`displayMultipleItems` 视口均分、`currentItemId`（优先级高于 `current`）、`duration` 过渡动画和 `loading` 占位。视频项渲染 `poster` + 播放图标并上报诊断（无原生播放器）；`acceleration` 降级为诊断上报，`easingFunction` 登记为按设计不生效。 |
| 116 | 媒体与内容 | `u-swiper-indicator` | `UPSwiperIndicator` / `UPSwiperIndicatorProps` | 基础可用 | 中 | line/dot 两种基础模式、当前项尺寸、颜色和点击回调已有真机断言；复杂样式待对照。 |
| 117 | 选择 | `u-switch` | `UPSwitch` / `UPSwitchProps` | 基本完成 | 高（Props） | 受控值、禁用、颜色和 change/update 事件已有测试。 |
| 118 | 导航 | `u-tabbar` | `UPTabbar` / `UPTabbarProps` | 基础可用 | 中 | 父子受控状态、颜色、边框和安全区已支持；新增 9 种 `styleType`、active/inactive 背景、`itemShape`、`textMode`、`iconScale` 和 `animationType`，均有真机结构/状态回归。`fixed`/`placeholder`/`zIndex` 为兼容字段，窗口级固定需宿主放入 Scaffold bottomBar 或底部 Box。 |
| 119 | 导航 | `u-tabbar-item` | `UPTabbarItem` / `UPTabbarItemProps` | 基础可用 | 中 | active/inactive icon、文字、badge/dot、name 事件、状态背景和 underline/dot 指示器已支持；`animationType` 仅作用于激活图标，`midButton` 已支持 64dp 外层、52dp 内层及垂直偏移。CSS class hook 和 box-shadow 通过原生语义/阴影近似并发出降级诊断，复杂视觉仍待上游逐项对照。 |
| 120 | 表格 | `u-table` | — | 未开始 | 暂无 | 表格容器待建立列宽和滚动契约。 |
| 121 | 表格 | `u-table2` | — | 未开始 | 暂无 | 第二版表格，待确认与 `u-table` 的 API 差异。 |
| 122 | 导航 | `u-tabs` | `UPTabs` / `UPTabsProps` | 基本完成 | 高（Props） | tabs/current/change 之外，`shapeMode` 复刻 line/capsule/card/pill-arrow/tag 五种形态（card 斜切四边形、pill-arrow 箭头由 Canvas 绘制）、`activeStyle`/`inactiveStyle`/`itemStyle` 逐项应用、`lineBgSize` 区分 cover/contain/auto 下划线宽度、`duration` 驱动下划线位移动画、`iconStyle` 作用于选项图标，均有真机断言。降级：粘性吸顶需宿主滚动容器回传偏移。 |
| 123 | 导航 | `u-tabs-item` | `UPTabsItem` / `UPTabsItemProps` | 基础可用 | 中 | 空/轻量 Props 契约、自定义内容和样式已有真机断言。 |
| 124 | 导航 | `u-tabs-pro` | — | 未开始 | 暂无 | Pro 标签页待确认专属字段和事件。 |
| 125 | 基础展示 | `u-tag` | `UPTag` / `UPTagProps` | 基本完成 | 高（Props） | 类型、形状、图标、关闭和颜色已有测试；`height`/`borderRadius`/`plainFill` 已生效并有真机断言（此前声明但从不读取）。`autoBgColor > 0 && color` 时按上游 `genLightColor` 的 RGB→HSL→亮度封顶 95%→HEX 流程推导同色系浅色背景（优先级高于 `bgColor` 与类型色），单测逐值对照上游输出；上游只解析 hex 与 `rgb()`/`rgba()` 并对其余格式抛错，Android 改为上报诊断并保留原背景。 |
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
| 基本完成 | 69 |
| 基础可用 | 21 |
| Props 已建 | 0 |
| 未开始（含辅助模块） | 51 |
| 完整兼容 | 0 |

## 当前已实现组件分批

| 批次 | 组件范围 | 数量 | 当前判断 |
| --- | --- | ---: | --- |
| 基础组件 | button、tag、badge、divider、gap、line、link、text、title、overlay、popup、modal、toast、cell、cell-group、image、avatar、avatar-group、empty、loading-page、loadmore、input、textarea、search、code-input、switch、rate、number-box、checkbox、checkbox-group、radio、radio-group、row、col、grid、grid-item、line-progress、circle-progress | 38 | 基本完成；仍需逐字段视觉回归。 |
| 基础能力 | icon、loading-icon | 2 | 基本完成；自定义图片/字体能力存在平台降级。 |
| Batch 9A 原生交互 | alert、action-sheet、notify、back-top、card、collapse、collapse-item、dropdown、dropdown-item、notice-bar | 10 | 混合；`collapse-item`、`notice-bar`、`dropdown` 系列已到「基本完成」，全局弹层与滚动语义仍需加强。 |
| Batch 9B 导航与更多 | navbar、navbar-mini、status-bar、safe-bottom、tabs、tabs-item、subsection、steps、steps-item、list、list-item、index-list、index-item、index-anchor、scroll-list、popover、tooltip、sticky、swipe-action、swipe-action-item、swiper、swiper-indicator、skeleton、read-more、column-notice、row-notice、count-to、count-down、picker、picker-column、pagination、select | 32 | 混合；`navbar`、`tabs`、`subsection`、`list`、`skeleton`、`read-more`、`count-to`、`count-down`、`pagination` 等已到「基本完成」，`sticky`/`select` 等仍受窗口级弹层与宿主滚动限制。 |
| Batch 10 选择与底部导航 | calendar、datetime-picker、cascader、slider、tabbar、tabbar-item | 6 | 混合；`cascader`、`slider`、`picker` 系列已到「基本完成」，滚轮视觉与窗口级固定仍需加强。 |
| Batch 11 表单校验 | form、form-item | 2 | 基本完成；上游 async-validator 规则、六个 ref 方法与标签/错误布局均有真机断言。 |
| Batch 12 字段补齐 | 跨批次：tabs、pagination、image、cell、modal、navbar、navbar-mini、number-box、overlay、badge、tag、subsection、notice-bar、collapse-item、sticky、action-sheet、slider、list、list-item、count-to、back-top、skeleton、select、read-more、cascader | 25 | 不新增组件，专门消化「声明了但从不读取」的字段。未读字段从 90 一路降到 0；期间发现的组件级缺陷（`u-subsection` 只有一排文字、`u-notice-bar` 从不滚动、`u-collapse-item` 无动画）已一并修复。 |

## 下一批推荐顺序

未读字段已归零，下一阶段的瓶颈从「字段是否接上」变成「行为是否对得上」，因此建议按下列顺序推进：

1. **真机执行现有断言**：库内 253 项 androidTest 目前只有编译级证据。先在真机上跑一遍，把编译级证据升级为运行级证据，这比新增组件更能暴露问题。
2. **视觉回归的可用性**：已经解决。参考图一直都在画文本与填色，此前"渲染环境不画文本"的判断是错的（见下文《截图内容核查》）。现有 30 项像素级断言**逐组件覆盖全部 28 张参考图**的关键颜色与几何，另有一项遍历全部参考图做非空校验。下一步可做的是把断言从"颜色在不在、比例对不对"推进到与上游真机截图的像素对照。
3. **仍标「基础可用」的 21 行**：这些行的未读字段已为 0，剩下的差距集中在窗口级弹层（`u-popover`、`u-tooltip`、`u-select`）、宿主滚动回传（`u-sticky`、`u-index-list`）与滚轮视觉（`u-picker` 系列）三类，需要先补基础设施再逐个收口。`u-count-down` 属于第四类——缺的是命令式 ref 方法而非基础设施——已在本轮补齐并升到「基本完成」，同类可先挑出来单独收口。
4. **表单体系**：`u-agreement`、`u-upload`、`u-album`。需要先确定 Android 回调 payload 和权限/文件 URI 边界（`u-form`、`u-form-item` 已在 Batch 11 完成）。
5. **列表与数据展示**：`u-pull-refresh`、`u-virtual-list`、`u-refresh-virtual-list`、`u-waterfall`、`u-table`、`u-td`、`u-th`、`u-tr`。
6. **原生能力**：`u-qrcode`、`u-barcode`、`u-signature`、`u-copy`、`u-city-locate`、`u-short-video`、`u-pdf-reader`。
7. **内容解析与复杂业务**：`u-markdown`、`u-parse`、`u-tree`、`u-goods-sku`、`u-novel-reader`、`u-tabs-pro`。
8. **选择增强**：`u-calendar-strip`、`u-keyboard`、`u-number-keyboard`、`u-car-keyboard`。

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

当前状态：库内共 **253 个行为测试**（`ultra-ui/src/androidTest` 的 `@Test` 静态计数；上一次
`connectedDebugAndroidTest` 在 162 项时报告的 `Starting 162 tests` 与静态计数一致）。最近一批为
`u-count-down` 新增 8 项回归（`UPCountDownBehaviorTest`），覆盖 `autoStart` 两态、
`pause()` 冻结余量而非让截止时间在后台继续跑、运行中重复 `start()` 不重置截止时间、
`reset()` 复位后按 `autoStart` 决定是否重开、宏拍每秒一次重绘对比微拍连续重绘、
到零时 `onFinish` 只触发且计时停在 0，以及 `mm:ss` 把 25 小时折成 1501 分钟与作用域插槽
拿到拆分后的时间。全部经 `mainClock.autoAdvance = false` 手动推进，不含真实等待。
再往前一批为收尾字段批新增 14 项回归（`UPTailFieldBehaviorTest`），覆盖 `u-count-to` 的逐帧推进与
ease-out-expo 领先线性斜坡、`autoplay=false` 停在起始值，`u-back-top` 把滚动时长交给宿主，
`u-skeleton` 的逐行宽度与末行 70% 回落、未知 `avatarShape` 诊断，`u-select` 的
`showOptionsLabel` 与 `optionsWidth`，`u-read-more` 的 `textIndent` 与仅收起态生效的
`shadowStyle`，以及 `u-cascader` 的 `headerDirection="column"` 纵向堆叠与 `maskCloseAble`
两态。再往前一批为动效与几何补齐批新增 19 项回归（`UPMotionParityBehaviorTest`），覆盖 `u-notice-bar` 逐条轮播的定时
切换与循环、单条不切换、`step` 走轮播、横向跑马灯真实位移，`u-column-notice`/`u-row-notice`
两个封装各自转发间隔与速度，`u-collapse-item` 收起过程中面板仍在树内、`customStyle` 落在外层、
零时长即时开合、标题行仍可开合，以及 `u-sticky` 的 `disabled` 撤掉偏移与 `u-action-sheet` 的
`safeAreaInsetBottom`，以及 `u-slider` 的 `height`/`length`/`blockStyle` 几何、`useNative`
降级诊断与 range 对它的免疫，和 `u-list` 的 `scrollIntoView` 锚点定位。这些新增断言同样
**只有编译级证据**（`compileDebugAndroidTestKotlin` 通过），按用户要求未在设备/模拟器上执行。再往前一批为
`u-subsection` 新增 8 项回归（`UPSubsectionBehaviorTest`），覆盖两种 `mode` 的高度差异、
滑块随点击与受控 `current` 平移、单项/空列表边界、`keyName` 与逐项颜色键、`disabled` 拦截点击、
未知 `mode` 回落诊断。再往前一批为
字段补齐批新增 10 项回归（`UPFieldParityBehaviorTest`），覆盖 `u-cell` 两个图标样式钩子、
`u-modal` 的 `negativeTop` 上移、`u-navbar`/`u-navbar-mini` 的 `border`/`autoBack`/
`statusBarBgColor` 诊断、`u-number-box` 的 `longPress` 关闭路径、`u-badge` 的绝对 `offset`，
以及 `u-tag` 的 `autoBgColor` 推导与不可解析色诊断。这 10 项同样**只有编译级证据**
（`compileDebugAndroidTestKotlin` 通过），按用户要求未在设备/模拟器上执行。再往前一批为
`u-tabs`/`u-pagination`/`u-image` 新增 8 项回归（`UPTabsPaginationImageBehaviorTest`），覆盖四种
`shapeMode` 的结构差异与下划线显隐、`activeStyle`/`itemStyle` 应用、分页 `layout` 分段渲染顺序、
`hideOnSinglePage` 单页隐藏、`pageSizes` 点击轮转回传，以及 `loadingIcon` 在加载态的图标名。
再往前一批为 `u-picker`/`u-datetime-picker` 的 `hasInput` 触发器与工具条插槽新增 12 项回归
（`UPPickerInputBehaviorTest`），覆盖触发器只读与覆盖层开合、`inputProps` 覆盖生效、
`toolbarRightSlot` 替换确认按钮、`maskStyle` 仅在显式传值时出现、`popupMode`/`maskClass`
降级诊断，以及 datetime 触发器文案按 `format` 格式化。再往前一批为
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

> 教训：`animateFloatAsState` / `animateDpAsState` 的初值就是首帧的目标值，直接写
> `targetValue = 最终值` 会让动画无从插值、`duration` 静默失效。`u-overlay` 与 `u-modal`
> 都需要先 `remember { mutableStateOf(false) }` 一个 `entered` 标记，在 `LaunchedEffect(Unit)`
> 里翻转，才能真正从 0 过渡到目标值。
>
> 教训：验证时长类字段必须关掉自动推进时钟（`composeRule.mainClock.autoAdvance = false`）
> 再手动 `advanceTimeBy`，否则 `waitForIdle` 会一次性跑完动画，中间态无从断言。
>
> 教训：跨端色彩算法要按 `Double` 而不是 `Float` 复刻。上游 `genLightColor` 对 `#2979ff`
> 在 95% 亮度下会算出 229.49999999999997 这种正好压在四舍五入边界上的通道值，用 `Float`
> 精度会把某个通道多进一位，得到 `#e6efff` 而不是上游的 `#e5efff`。
>
> 教训：不要用"看图"当作视觉证据。本文档曾连续多轮写着"渲染环境不绘制文本与大部分填色"，
> 逐像素解码后发现文字、填色、圆角、抗锯齿一应俱全——错判来自查看图片时的降采样，
> 945px 宽的参考图被压缩到 12sp 文字彻底消失。**结论应当来自可复算的读数，不是缩略图。**

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

当前状态：90 个已实现组件行中，**90 行证据齐备、0 行标注超出证据**，脚本首次全绿。最后 10 行
（`u-cell`、`u-modal`、`u-navbar`、`u-navbar-mini`、`u-number-box`、`u-radio`、`u-radio-group`、
`u-checkbox-group`、`u-overlay`、`u-badge`、`u-tag`）在本轮补齐了实现或登记为按设计不生效，
其中 `u-navbar`、`u-navbar-mini` 由「基础可用」升级为「基本完成」；再往前一批的 `u-tabs`、
`u-pagination`、`u-image`，以及更早的 `u-picker`、`u-datetime-picker`、`u-form`、`u-form-item`、
`u-swiper` 也都按同样标准移出过清单。

需要强调这个「0」的边界：脚本核验的是**标注是否有证据支撑**（未读字段为 0、且有真机或截图
语料），不是「组件行为与上游完全一致」。仍有 22 行标注「基础可用」，其未读字段清单见下一节；
另外真机测试目前只有编译级证据；视觉一侧现已有 7 项像素级断言（见《截图内容核查》），
但覆盖面还只到本轮改动的几个组件。

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
再往前一批为 `u-picker`/`u-datetime-picker` 的 `hasInput` 触发器、`toolbarRightSlot` 插槽与 `maskStyle`
遮罩新增 12 项行为断言（编译级）与 3 张截图参考图，参考图总数 18 → 21。再往前一批为 `u-tabs` 四种
`shapeMode` 与 `u-pagination` 完整 `layout` 新增 1 张截图参考图（`UPBatch9BTabsShapeScreenshot`），
参考图总数 21 → 22。再往前一批为字段补齐批新增 10 项行为断言（编译级）与 2 张截图参考图
（`FieldParityScreenshots`：cell/navbar/tag 与 badge 偏移/number-box），参考图总数 22 → 24。
再往前一批为 `u-subsection` 两种形态新增 1 张参考图，参考图总数 24 → 25。再往前一批为通知栏、
折叠面板与吸顶容器新增 1 张参考图，参考图总数 25 → 26。再往前一批为 `u-slider` 的四种几何组合
新增 1 张参考图，参考图总数 26 → 27。最近一批为骨架屏逐行宽度、选择器面板与阅读更多缩进
新增 1 张参考图，参考图总数 27 → 28。
**此前对这些参考图的判断是错的，需要更正**：文档一直写着"本仓库的 screenshotTest 渲染环境
不绘制文本与大部分填色，因此参考图只能作为不崩溃与布局占位证据"。逐像素解码后发现，
文本、填色、圆角、抗锯齿边缘**全都在图里**——错判的原因是查看图片的通道会把 945px 宽的
参考图降采样到无法分辨 12sp 文字，于是把"看不见"当成了"没画"。详见下一节。

## 截图内容核查

`validateDebugScreenshotTest` 只回答"渲染结果和上次 `update` 相比有没有变"，它无法回答
"到底画了什么"——一张从来就没有文字的参考图会永远通过校验。为此新增两条互补的手段：

```
python3 tools/inspect_screenshot.py --list                        # 列出全部参考图
python3 tools/inspect_screenshot.py "subsection modes"            # 尺寸、配色直方图、抗锯齿占比
python3 tools/inspect_screenshot.py "subsection modes" --expect 3c9cff eeeeef   # 缺色时退出码 1
python3 tools/inspect_screenshot.py "tabs shapes" --ascii 656 690 44 118        # 亮度字符画
```

脚本只用标准库 `zlib` 手工反滤波 PNG（本机没有 Pillow），可以直接查证某张参考图里
究竟有哪些颜色、各占多少、深色像素落在哪几行。`--ascii` 会把指定矩形渲染成亮度字符画，
用来肉眼确认字形——上面「共 100 条」的"共"字与三个数字轮廓清晰可辨，正是据此推翻了
"渲染环境不画文本"的旧判断。

同时新增 `UPScreenshotContentTest`（**30 项 JVM 单测，逐组件覆盖全部 28 张参考图**，走
`javax.imageio` 读回已提交的 PNG），把视觉断言变成可以 gate 提交的证据，而不是靠人眼：

- 抗锯齿混合色占比 > 0.1%，证明文字真的被绘制（纯色块布局做不出这么多一次性混合色）
- `u-subsection` 两种形态各自的调色板：button 轨道 `#eeeeef`、subsection 滑块 `#3c9cff`、
  禁用滑块 `#f5f5f5`，且 button 轨道恰好两段（启用 + 禁用）、subsection 滑块恰好两段
- 滑块随 `current` 前进整整一格，宽度约为控件的三分之一
- `u-tag` 的 `autoBgColor` 推导色 `#e5efff` 与来源文字色 `#2979ff` 同时在图内，且推导色
  覆盖面积 > 1000 像素
- `u-tabs` 四形态那张图配色数 > 1000、抗锯齿 > 0.5%、未激活标签色跨 4 段行区间
- `u-slider` 四种几何：`#c0c4cc` 轨道恰好四段，且 `height = 8` 的第二条比 2dp 默认值厚 2 倍以上
- `u-skeleton` 的 `rowsWidth = ["100%", "80%", "40%"]` 三行实测占可用宽度的比例逐行收窄，
  误差 < 6%（这是把百分比换算成实际像素的唯一像素级证据）
- `u-badge` 的 `offset = [12, 20]` 按 dp 换算后下移约 12dp、从右边缘内推约 20dp，
  误差 < 3dp（两个徽标 y 区间重叠，必须按各自列范围分别扫描才能量到偏移）
- `u-notice-bar` 两条通知各自铺 `#fdf6ec` 背景、文字取 `#f9ae3d`，`u-sticky` 的
  `#f3f4f6` 恰好一段且位于两条通知之下
- `u-col` 的 `span = 4` 三列各占内容宽度的 1/3（误差 < 4%）且自左向右不重叠，
  `u-grid` 的 `col = 3` 六格里三种主题色与栅格行共用、另三种只在网格出现
- `u-action-sheet` 的 45% 遮罩解析为 `#8c8c8c`，从顶边开始、在面板处**停止**
  （若遮罩盖住面板就说明层级反了），紧邻的下一行必须是面板白
- `u-number-box` 的禁用态同时重绘两个按钮与输入框：启用底 `#ebecee` 两段、
  禁用底 `#f7f8fa` 两段，且各自都拆成 minus/field/plus 三列
- `u-swiper` 的 `loading = true` 占位页高度按 `height = 120` 换算（误差 < 5%），
  占位字形完整落在页内且中心处于页面中间三分之一区域
- `u-line-progress` 四条进度按百分比精确填充：0% 只剩满宽凹槽、50% 与凹槽各半、
  100% 无凹槽、`fromRight = true` 的 65% 贴右边缘且左侧留空
- `u-circle-progress` 两环：30% 保留 `#c8c8c8` 余量弧、100% 完全被 `#19be6b` 填满，
  且余量弧只出现在左边那一环
- `u-switch` 只有 `modelValue = true` 的那个铺 `activeColor`，且它在右侧；
  `u-rate` 三个控件共 15 个星形字形，0 分全灰、5 分全亮、2.5 分那个第 3 星
  同时含两色且亮色部分约占该字形一半（误差 < 8%）
- `u-checkbox`/`u-radio` 只给勾选项填 `activeColor`，未勾选项只描 `inactiveColor` 边
- `u-icon` 三个彩色图标各自保留自己的颜色（图标字体加载失败会只剩标签文字）
- `u-picker` 的 `maskStyle = rgba(0,0,0,0.06)` 把整列（含选中行高亮）都压暗 6%：
  `#eaf3ff` 高亮变成 `#dce4f0`、白底变成 `#f0f0f0`，且**原始 `#eaf3ff` 一个像素都不剩**
  —— 这一条恰好证明遮罩层在选项之上，而不是被选项盖住
- `u-status-bar` 的 `height = 8` 按密度换算后高度吻合（误差 < 15%），且下方
  subsection 轨道与它不相接（说明两者没有合并成一块）
- `u-calendar` 的 `defaultDate` 选中日填 activeColor，与其下 `u-slider` 的填充同色但
  分成两段；日格是小方块、滑块横跨内容宽度（宽度差 > 5 倍），且 65% 不触右边缘
- `u-cascader` 的 `modelValue` 两级路径在两列各高亮一项，两列宽度均等；`u-tabbar-item`
  的 `dot` 红点位于级联面板之下
- `u-alert` 的 `type = "warning"` 用 warningLight 底 + warning 前景，`u-notify` 在其下方
  用 primary 底，两者都是满宽横幅而非行内小块
- `u-card`/`u-collapse` 的边框色分多段出现且都在底部通知栏之前（布局塌陷会让通知消失）
- `u-loading-icon` 三种 mode 各保留自己的 `color`（共用一色说明 `color` 没送到字形），
  两个 `vertical = true` 的字形严格位于标签之上，三者自左向右按声明顺序排列
- `u-swiper` 的 `previousMargin`/`nextMargin` 让页宽收窄，无边距那张恰好满宽；
  `showTitle` 的标题条紧贴页面下缘并横跨整页宽
- `u-picker` 的 `hasInput` 触发器渲染成描边输入框且**滚轮高亮一个像素都没有**（面板未展开），
  与之相对，内联那张 picker 的 `#eaf3ff` 高亮 > 1000 像素且几乎没有被压暗的痕迹
  —— 一对互为反证的断言
- 「非空」这一项覆盖**全部 28 张**参考图（不只本轮新增的 7 张），空白参考图会被拦下

其余限制仍然存在：这些断言核对的是"该画的颜色在不在、几何比例对不对"，不是与上游
uview-plus 的像素级同像；后者需要上游真机截图作基线。另有两条来自实践的经验：抗锯齿会
让极少量像素落在"本不该出现"的颜色上（内联 picker 里就有 3 个 `#dce4f0`），因此互斥类
断言要比量级而不是要求精确为 0；字形墨迹盒受字体 side bearing 影响永远不会正好居中，
位置断言应写成"完全落在容器内 + 中心处于中间三分之一"。

## 未生效字段核查

`tools/find_unread_props.py` 报告「声明了但组件从不读取」的 `UP*Props` 字段。这类字段是静默
空操作：类型检查通过、Props 测试通过、截图也不变，因此既有核查手段都发现不了它。
当前扫描结果中，`u-tabbar`、`u-tabbar-item`、`u-swiper`、`u-picker`、`u-datetime-picker`、
`u-tabs`、`u-pagination`、`u-image`、`u-cell`、`u-modal`、`u-navbar`、`u-navbar-mini`、
`u-number-box`、`u-overlay`、`u-badge`、`u-tag`、`u-subsection`、`u-notice-bar`、
`u-collapse-item`、`u-sticky`、`u-action-sheet`、`u-slider`、`u-list`、`u-list-item`、
`u-count-to`、`u-back-top`、`u-skeleton`、`u-select`、`u-read-more` 与 `u-cascader`
均已消化完毕，清单为空；但字段被读取只是最低门槛，读得对不对仍需真机与视觉证据，
不能仅以 Props 声明或编译通过替代行为证据。Batch 11 的两个表单组件没有新增未读字段：
`u-form` 的 `borderBottom` 已登记为按设计不生效（上游只有一个不再被调用的 `propsChange` computed 引用它，
`u-form-item` 从不读取父级该字段），`u-form-item` 的 `rightIcon` 上游声明后同样从未渲染——后者因为
`UPFormItem` 会把整个 `props` 转发给内部函数，脚本会退化成全库搜索并被 `UPCell` 的同名字段掩盖，
所以只能记录在本文档而不会出现在脚本清单里。

```
python3 tools/find_unread_props.py                # 列出无人读取的字段（有结果时退出码 1）
python3 tools/find_unread_props.py --show-inert    # 同时列出按设计不生效的字段及原因
```

当前状态：90 个 Props 类中有 **0 个字段无人读取**，另有 67 个已记录为按设计不生效
（uni-app / 微信小程序 / nvue 专有开关、DOM 事件语义，以及上游自己也从不读取的字段，
仅保留接口兼容）。已消化的批次：13 个组件曾声明
`customStyle` 却从不应用（`UPSwitch`、`UPRate`、`UPBadge` 等）、`UPSticky` 的
`offsetTop`/`customNavHeight`、`UPPicker`/`UPDatetimePicker` 的 `itemHeight`/`visibleItemCount`、
`u-list` 的 10 个滚动与下拉刷新字段、`u-swiper` 的自动播放与循环，以及 `u-steps` 的
全部 7 个状态字段，以及 `u-input`/`u-textarea` 的 `selectionStart`/`selectionEnd`/`cursor`。
最近一批把 `u-swiper` 剩余 9 个字段（`imgMode`、`radius`、`showTitle`、`vertical`、
`displayMultipleItems`、`currentItemId`、`duration`、`loading`、`acceleration`）全部接上实现或
诊断降级，未读数因此从 90 降到 81；`easingFunction` 早已登记为按设计不生效（上游注明只对微信小程序有效）。
再往前一批把 `u-picker` 与 `u-datetime-picker` 各自的七个同名字段（`hasInput`、`inputBorder`、
`inputProps`、`maskClass`、`maskStyle`、`popupMode`、`toolbarRightSlot`）全部接上实现或诊断降级，
未读数因此从 81 降到 67。最新一批把 `u-tabs` 的 7 个（`shapeMode`、`activeStyle`、`inactiveStyle`、
`itemStyle`、`iconStyle`、`lineBgSize`、`duration`）、`u-pagination` 的 4 个（`pageSizes`、
`buttonBgColor`、`buttonBorderColor`、`hideOnSinglePage`）与 `u-image` 的 3 个（`loadingIcon`、
`fade`、`duration`）全部接上实现，未读数因此从 67 降到 53。最新一批把 `u-cell` 的
`iconStyle`/`rightIconStyle`、`u-modal` 的 `negativeTop`/`duration`、`u-navbar` 的
`border`/`fixed`/`autoBack`、`u-navbar-mini` 的 `fixed`/`autoBack`、`u-number-box` 的
`longPress`、`u-overlay` 的 `duration`、`u-badge` 的 `offset` 与 `u-tag` 的 `autoBgColor`
全部接上实现；另有 5 个字段经核对上游源码后登记为按设计不生效（`u-radio.color`、
`u-radio-group.label`/`name`、`u-checkbox-group.name` 在上游 `.vue` 里也从不被读取，
`u-navbar.statusBarBgColor` 上游注明仅为兼容保留，`u-number-box.cursorSpacing` 属 uni-app
键盘避让提示）。未读数因此从 53 降到 34。最新一批把 `u-subsection` 的 5 个字段
（`mode`、`bold`、`fontSize`、`activeColorKeyName`、`inactiveColorKeyName`）全部接上实现，
未读数降到 29。最新一批把 `u-notice-bar` 的 3 个（`speed`、`duration`、`disableTouch`）、
`u-collapse-item` 的 2 个（`duration`、`customStyle`）、`u-sticky` 的 2 个（`disabled`、`zIndex`）
与 `u-action-sheet` 的 `safeAreaInsetBottom` 接上实现，另有 4 个登记为按设计不生效
（`u-sticky.index`、`u-action-sheet.index`/`openType`、`u-collapse-item.cellCustomClass`），
未读数降到 17。最新一批把 `u-slider` 的 3 个（`height`、`innerStyle`、`blockStyle`，另有
`useNative` 走诊断降级）与 `u-list` 的 `scrollIntoView`、`u-list-item` 的 `anchor` 接上实现，
另有 2 个登记为按设计不生效（`u-list.pagingEnabled` 上游自身不读、`u-list.preLoadScreen`
上游算出 `show` 后从不绑定），未读数降到 10。最后一批把剩下 10 个全部接上实现：
`u-count-to.useEasing`（逐帧 ease-out-expo）、`u-back-top.duration`（随新增
`onScrollToTop` 交给宿主）、`u-skeleton` 的 `rowsWidth`/`avatarShape`、`u-select` 的
`optionsWidth`/`showOptionsLabel`、`u-read-more` 的 `textIndent`/`shadowStyle`、
`u-cascader` 的 `headerDirection`/`maskCloseAble`。**未读数因此归零，脚本首次以退出码 0 通过。**

> 数字为何从 134 涨到 205：脚本原先把**整个文件**当作搜索范围，同文件内的兄弟组件
> （`UPSwiper` 与 `UPCountTo` 同在 `UPStatusNumericComponents.kt`）会互相掩盖——
> `UPCountTo` 的 `props.autoplay` 让 `UPSwiper` 从未生效的 `autoplay` 被误判为已读。
> 另一处 `= props` 正则过宽，把 `current = props.current`（字段读取）误判为
> 「转发整个 props 对象」，从而退化为全库搜索。两处收紧后，此前被掩盖的 ~70 个字段
> 才显形。**205 是更接近真相的数字，不是退步。**

这份清单曾被视为**功能缺口清单**而不是待清理的噪音，因为它里面既有"缺特性"，
也有"组件根本不可用"——`u-picker` 的列平铺、`u-swiper` 只渲染文字不显示图片（已修复）、
`u-steps` 曾完全忽略 `current` 导致每一步都显示为已完成（已修复），都属于后者。
清单现已清空，但**不等于行为与上游一致**：脚本只判断字段是否被读取，读得对不对要靠
默认值比对、真机断言与视觉核对；后续工作应转向这三项，以及仍标「基础可用」的 22 行。

另需说明「按设计不生效」这一档的判定标准：只有当**上游自己也不读取该字段**，或该字段是
uni-app / 微信小程序 / nvue 的平台专有开关、DOM 事件语义在 Compose 中无对应物时，才会登记进
`KNOWN_INERT`。凡 Android 有等价表达手段的字段一律实现，不用「平台差异」当挡箭牌。

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
