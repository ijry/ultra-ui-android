# uview-plus Android 组件复刻进度

> 用途：记录后端将同一份 JSON 生成到 uni-app、iOS、Android Kotlin 后，各端可使用的 uview-plus 组件接口与实现进度。

## 统计口径

- 上游来源：`/Users/admin/Documents/Repos/xyito/open/uview-plus/src/uni_modules/uview-plus/components`
- 上游固定提交：`b32377ce0500579830e537a20eef1a7c6c9cf806`
- 扫描日期：2026-08-30
- 上游目录总数：141 个
- 当前 Android 公开 `UP*Props`：101 个
- 当前 Android 已有公开 Compose 组件入口：101 个（另有 `UPToastHost` 等宿主辅助 API）
- 当前目标组件完成度：101 / 138 个可直接使用的上游 UI 组件目录，约 73.2%。其中 3 个是辅助模块目录，暂不计入 UI 组件分母。

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
| 1 | 原生交互 | `u-action-sheet` | `UPActionSheet` / `UPActionSheetProps` | 基本完成 | 高（Props） | `safeAreaInsetBottom`、`round`、`wrapMaxHeight`、`closeOnClickAction`、`nameKey`/`subnameKey` 与逐项 `color`/`fontSize` 之外，本轮补齐结构与可达性规则：`title` 非空才生成 header（含右上角 17px 关闭图标，走 `cancel()` 因而不受 `closeOnClickOverlay` 影响）、标题按 `.u-line-1` 加粗单行、`description` 的上边距在有标题时折叠为 0 并在其下画一条 `u-line`、分隔线只出现在**选项之间**（末项没有）、逐项 `loading` 用 18px circle 图标顶替标签且不可选（`!item.disabled && !item.loading`，此前只当作 disabled 一并变暗）、首项在无 header 且 `round > 0` 时继承面板圆角、取消行前用 `u-gap height=6` 分隔、`closeOnClickOverlay=false` 时遮罩吞掉点击而取消按钮照样关闭、自定义内容按 `v-if/v-else` **整体替换**选项列表且点击时按 `closeOnClickAction` 关闭（此前是追加在列表之后）。降级：`openType` 是微信开放能力按钮，Android 无对应物；`index` 是兼容别名，上游按位置解析。刻意差异：面板**不**搬进窗口级 `Popup`——它本就铺满宿主给的空间，没有需要跨越的裁剪边界，而截图渲染器不绘制 popup 窗口，搬过去会让本组件的参考图变成全白、毁掉像素证据。有 6 项单测与 7 项真机断言。 |
| 2 | 辅助模块 | `u-action-sheet-data` | — | 未开始 | 暂无 | 操作菜单数据辅助目录，不单独作为 Android UI 组件。 |
| 3 | 表单与协议 | `up-agreement` | `UPAgreement` / `UPAgreementProps` | 基本完成 | 高（Props） | 隐私/用户协议门：封装 `up-modal`（带取消按钮、确认文案 `阅读并同意`），默认正文是上游样板段落 + 两个主题色内链（`用户协议`/`隐私政策`）；新增 `UPAgreementController` 复刻 ref 上的 `showModal()`（另加 `hide()`），`show` 内部持有；点击内链按上游 `uni.navigateTo` 语义经 `onNavigate` 回传 `urlProtocol`/`urlPrivacy`（路由归宿主），确认经 `onConfirm`（上游 `emit('confirm', 1)`）并关闭。降级：上游取消路径调 `window.close()`/`plus.runtime.quit()` 退出应用，Android 无可移植等价物，改为关闭弹窗并经 `onClose` 交宿主决定（如 finish Activity）。有 1 项默认值单测与 2 项真机断言（`showModal` 打开且确认关闭并回调、取消经 `onClose` 关闭）。 |
| 4 | 媒体与内容 | `u-album` | — | 未开始 | 暂无 | 相册选择与预览，涉及系统权限和媒体选择器。 |
| 5 | 原生交互 | `u-alert` | `UPAlert` / `UPAlertProps` | 基本完成 | 高（Props） | 类型/主题/图标/关闭按钮与 `duration` 自动关闭之外，`transitionMode` 不再只做校验：按 `u-transition` 的十一种模式表真正驱动入场与离场（`fade*` 系插值不透明度、`zoom`/`fade-zoom` 取 `scale(0.95)`、`slide-*` 与 `fade-*` 按 `translate3d` 的整屏偏移起步），关闭时面板留在树内播完离场动画才移除，因而 `close` 先于 `closed`。均有真机断言，模式表有单测逐条对照。刻意差异：上游 `u-transition` 的 watcher 是 `immediate: true`，挂载即可见的横幅也会淡入；Android 侧首帧直接给稳定态，只有后续 `show` 切换才播动画——否则截图参考图（捕获第 0 帧）会变成全白，反而毁掉「类型配色确实被绘制」的像素证据。 |
| 6 | 媒体与内容 | `u-avatar` | `UPAvatar` / `UPAvatarProps` | 基本完成 | 高（Props） | 图片、文字、图标和形状已有 Compose 实现与测试。降级：`randomBgColor` 未随机取色，`colorIndex` 缺省固定取第 0 号色（上游 `''` 表示随机），以保证截图可复现。 |
| 7 | 媒体与内容 | `u-avatar-group` | `UPAvatarGroup` / `UPAvatarGroupProps` | 基本完成 | 高（Props） | 头像组基础布局已实现；溢出和间距视觉仍需上游对照。 |
| 8 | 原生交互 | `u-back-top` | `UPBackTop` / `UPBackTopProps` | 基本完成 | 高（Props） | 新增可选的 `scrollState` 参数后**不再需要宿主代劳**：传入宿主的 `ScrollState`，组件自己读偏移判显隐（`getPx(scrollTop) > getPx(top)`，相等仍隐藏）、自己执行 `animateScrollTo(0, tween(duration))`——即上游的 `uni.pageScrollTo`；不传则保留 `scrollTop` + `onScrollToTop(durationMillis)` 的旧路径，供滚动容器不是 Compose `ScrollState` 的宿主使用。同时修正三处偏离：底色改用 `.u-back-top` 自己的 `#E1E1E1`（此前是白色，在白底页面上完全看不见）、按钮固定 40×40 且文字按 `.u-back-top__tips` 12px + `scale(0.8)` 排在图标**下方**（此前是横向排列且尺寸随内容变化）、`bottom`/`right` 改为从宿主锚点向内的偏移（此前当成自身内边距，把按钮撑到 140dp 并被裁剪——`position: fixed` 的偏移不参与布局）。另按 `<u-transition mode="fade">` 淡入淡出，离场动画播完才移出树。有 6 项真机断言与像素级断言。 |
| 9 | 基础展示 | `u-badge` | `UPBadge` / `UPBadgeProps` | 基本完成 | 高（Props） | 类型、颜色、徽标位置和最大值已有实现；`offset` 在 `absolute=true` 时按 `[top, right]` 内推徽标（只给一个值时两边同用），有真机断言。 |
| 10 | 原生能力 | `u-barcode` | — | 未开始 | 暂无 | 条形码生成，待接入 Android 原生/成熟编码库。 |
| 11 | 布局 | `up-box` | `UPBox` / `UPBoxProps` | 基本完成 | 高（Props） | 首页特色盒子：左侧一整块 + 右侧上下两等高块的三分栏（左 `flex:1`、`gap` 定宽间隔、右列两块 `flex:1` 间夹 `gap` 高 spacer），每块按 `borderRadius` 圆角裁剪并填 `bgColors[0..2]`（数组不足时按索引回落默认三色）；`height`/`borderRadius`/`gap` 走 CSS 长度、`customStyle` 覆盖，三个区域各支持 `left`/`rightTop`/`rightBottom` 插槽，缺省时渲染 36rpx 图标 + 区域标题（左 16px、右两块 15px）；顶层 `click` 事件按上游 emits 契约以 `onClick` 暴露（上游模板虽声明但未真正绑定，Android 补上）。有 1 项默认值/回落单测与 2 项真机断言（三分栏 + 默认标题 + 固定高、插槽替换默认内容并触发点击）。 |
| 12 | 基础展示 | `u-button` | `UPButton` / `UPButtonProps` | 基本完成 | 高（Props） | `type="primary"`、形状、加载态、图标和禁用态已有测试。 |
| 13 | 选择与日期 | `u-calendar` | `UPCalendar` / `UPCalendarProps` | 基本完成 | 高（Props） | 月份切换、single/multiple/range、农历、时间精度与 `maxRange`/`rangePrompt`/`forbidDays`/`customList` 之外，新增 `formatter(config)`：回调收到上游同形状的 `{ day, week, month, date, disabled, bottomInfo, dot }`，返回值逐键覆盖（未返回的键保留内部计算值），可按天改写 `bottomInfo`/`dot` 或直接 `disabled` 掉某天并因此拦住点击；不可调用或抛异常的 formatter 走诊断降级且当天照常渲染。`duration` 驱动面板淡入、`zIndex` 决定同级覆盖顺序、`safeAreaInsetTop`/`safeAreaInsetBottom` 让出系统栏，均已生效。未完成：`overlay`/`overlayStyle`/`overlayOpacity`/`closeOnClickOverlay` 依赖全屏遮罩，而本组件仍是内联渲染——这是未做而非做不到，`u-tooltip` 已用 `androidx.compose.ui.window.Popup` 证明该层可用，把日历面板搬进去即可解开这四个字段。 |
| 14 | 选择与日期 | `u-calendar-strip` | — | 未开始 | 暂无 | 横向日期条，待实现日期滚动和选中状态。 |
| 15 | 媒体与内容 | `u-canvas` | — | 未开始 | 暂无 | Canvas 容器/绘制适配，需单独确认 Android 生成调用方式。 |
| 16 | 键盘与输入 | `u-car-keyboard` | `UPCarKeyboard` / `UPCarKeyboardProps` | 基本完成 | 高（Props） | 车牌键盘：在中文省份简称（`areaList` 36 项）与英文/数字车牌（`engKeyBoardList` 数字 + A–Z 共 36 项）间切换，均按上游切成 10/10/10/6 四行；第四行左侧 中/英 切换、右侧退格；`random` 打乱当前键集，`autoChange` 在输入一个中文后经 200ms 协程延迟自动切到英文（复刻 `sleep(200)`）。`change` 回报键值、`backspace` 退格。键集与行切分逐条对照上游 computed。降级：上游按住退格每 250ms 重复，Android 每次点击一次。有 1 项键集/行切分单测与 2 项真机断言（中文起始并切英文、退格触发）。 |
| 17 | 基础展示 | `u-card` | `UPCard` / `UPCardProps` | 基本完成 | 高（Props） | 三段结构、缩略图、圆角、阴影、逐段内边距与全部样式钩子之外，本轮对齐了四处此前偏离上游的行为：`head-click`/`body-click`/`foot-click` 三个分区事件（与 `click` 一样回传 `index`）；`showHead`/`showFoot` 只看自身开关而不再要求插槽或标题非空（上游是纯 `v-if`）；空脚部按 `$slots.foot ? padding : 0` 不占内边距；`headBorderBottom`/`footBorderTop` 改为按 `.u-border-bottom`/`.u-border-top` 只画一条 hairline（此前误用整框描边）；标题与副标题按 `.u-line-1` 单行省略。有真机断言与像素级断言。 |
| 18 | 选择与日期 | `u-cascader` | `UPCascader` / `UPCascaderProps` | 基本完成 | 高（Props） | `data` 多级路径、value/label/children key 与 change/confirm 事件之外，`headerDirection="column"` 把并排的层级改为纵向堆叠（对应上游换用 `u-steps` 的长标签排版）、`maskCloseAble` 与 `closeOnClickOverlay` 共同决定点击面板空白处是否取消并关闭，均有真机断言。另补齐 `closeable`（按 `.u-popup__content__close { position: absolute }` 让关闭图标浮在右上角、不占布局高度因而工具条不位移）与 `zIndex`（`uZIndex()` 缺省取 `zIndex.popup` = 10075，`0` 视为未设置）。弹层动画仍待补：面板尚未搬进窗口级 `Popup`。 |
| 19 | 导航 | `u-cate-tab` | — | 未开始 | 暂无 | 分类导航，待建立横向/纵向布局契约。 |
| 20 | 基础展示 | `u-cell` | `UPCell` / `UPCellProps` | 基本完成 | 高（Props） | 标题、描述、图标、箭头和点击行为已有测试；`iconStyle`/`rightIconStyle` 分别作用于左图标与右侧箭头，并按 `size="large"` 切换 22/18 与 18/16 两档字号，禁用态右图标改用禁用色，均有真机断言。 |
| 21 | 基础展示 | `u-cell-group` | `UPCellGroup` / `UPCellGroupProps` | 基本完成 | 高（Props） | 分组容器与标题已有实现；`border` 按上游 `<view class="u-cell-group__wrapper"><u-line v-if="border">` 在首个单元格上方画一条 hairline（不是给整组描框），有真机断言。 |
| 22 | 选择 | `u-checkbox` | `UPCheckbox` / `UPCheckboxProps` | 基本完成 | 高（Props） | 受控值、形状、颜色、标签和组上下文已有测试。 |
| 23 | 选择 | `u-checkbox-group` | `UPCheckboxGroup` / `UPCheckboxGroupProps` | 基本完成 | 高（Props） | 多选组受控值和布局已有测试。降级：`name` 上游 `u-checkbox-group.vue` 自身从不读取（`change` 事件回传的是子项的 `name`），Android 同样保留字段但不生效。 |
| 24 | 选择 | `u-choose` | — | 未开始 | 暂无 | 选择组合控件，待核对上游当前 API。 |
| 25 | 布局与进度 | `u-circle-progress` | `UPCircleProgress` / `UPCircleProgressProps` | 基本完成 | 高（Props） | 环形进度、颜色、宽度和文字已有实现。 |
| 26 | 原生能力 | `u-city-locate` | — | 未开始 | 暂无 | 城市定位，涉及定位权限和系统服务。 |
| 27 | 键盘与输入 | `u-code` | `UPCode` / `UPCodeProps` | 基本完成 | 高（Props） | 验证码倒计时（headless，上游 `display:none` + 作用域插槽透传文案）：新增 `UPCodeController` 提供 ref 上的 `start()`/`reset()` 与只读 `canGetCode`（运行中为 false）；`start()` 复刻 `secNum=seconds`、`$emit('start')` 与首拍 `change(secNum)`，随后 `setInterval(1000)` 的 `if(--secNum)` 语义每秒回调 `change` 直到归零；文案按 `startText`（未开始）→ `changeText`（运行中，首个 `X` 换成当前秒）→ `endText`（结束）三段切换，有插槽时透传文案否则 `BasicText` 兜底渲染。补全上游本快照留空的两处：`setTimeText()`/`getText()` 是空实现（`codeText` 从不赋值）、声明的 `end` 事件从不 `$emit`——按属性/事件契约重建，归零时触发 `onEnd`。降级：`keepRunning`/`uniqueKey` 是 H5 刷新续跑的本地存储持久化，Compose 重组树无对应物，两者均上报诊断而非静默忽略。有 1 项默认值/文案单测与 2 项真机断言（start→逐秒 change→endText、reset 复位并停摆）。 |
| 28 | 键盘与输入 | `u-code-input` | `UPCodeInput` / `UPCodeInputProps` | 基本完成 | 高（Props） | 输入长度、掩码、颜色和回调已有实现；原生焦点细节待补。 |
| 29 | 布局 | `u-col` | `UPCol` / `UPColProps` | 基本完成 | 高（Props） | 栅格列宽、偏移和响应式基础字段已有实现。 |
| 30 | 内容面板 | `u-collapse` | `UPCollapse` / `UPCollapseProps` | 基本完成 | 高（Props） | 折叠状态、手风琴模式、组上下文与 `change`/`open`/`close` 事件之外，`border` 按上游 `<view class="u-collapse"><u-line v-if="border">` 在组首画一条 hairline，并经上下文传给子项：每个面板尾部再补一条 `u-line`，展开的面板额外显示标题行下划线（上游用 10ms/290ms 延时避免下划线与面板抢跑，Android 侧按展开比例同序渲染）。子项动画在 `u-collapse-item` 行。有真机断言与像素级断言。 |
| 31 | 内容面板 | `u-collapse-item` | `UPCollapseItem` / `UPCollapseItemProps` | 基本完成 | 高（Props） | 子项展开行为、插槽与图标之外，`duration` 驱动面板在 0 与测得高度之间的展开/收起动画（收起过程中面板仍在树内，动画结束才移除，下方兄弟节点随之滑动），`cellCustomStyle` 作用于标题行、`customStyle` 作用于外层，均有真机断言。刻意差异：上游 `<view class="u-collapse-item">` 从不绑定 `customStyle`（mixin 声明了但模板未用），Android 让它在外层生效。`border` 与父级 `border` 同时为真时，标题行在展开过程中显示下划线、面板尾部固定一条分隔线（均为 `u-line`，颜色取其默认 `#d6d7d9` 而非 `.u-border` 的 `#e4e7ed`——两者在上游本就是不同的值）。降级：`cellCustomClass` 是 CSS 类名，无 Compose 等价物。 |
| 32 | 选择与日期 | `u-color-picker` | — | 未开始 | 暂无 | 颜色选择器，待建立颜色值和面板交互契约。 |
| 33 | 通知与状态 | `u-column-notice` | `UPColumnNotice` / `UPColumnNoticeProps` | 基本完成 | 中 | 基于通知栏封装，随 `u-notice-bar` 一并补齐：`duration` 作为轮播间隔逐条切换并循环、`disableTouch=false` 时可上下拖动翻页，均有真机断言。 |
| 34 | 工具 | `up-copy` | `UPCopy` / `UPCopyProps` | 基本完成 | 高（Props） | 点击复制：把 `content` 写入 `LocalClipboardManager`（复刻 `uni.setClipboardData`），空 `content` 跳过复制并回报「暂无」结果（上游弹 `暂无` toast），成功回报 `notice`（默认 `复制成功`）并触发 `success`；默认插槽渲染「复制」文案，可传自定义子节点。降级：上游按 `alertStyle` 用 `uni.showToast`/`uni.showModal` 自行提示，Android 无常驻 toast 宿主，改由 `onResult(result, notice, alertStyle)` 把结果/文案/样式交宿主呈现，`success` 映射 `onSuccess`。有 1 项默认值单测与 2 项真机断言（成功回报 notice + success、空内容回报 empty 且不触发 success）。 |
| 35 | 数值与时间 | `u-count-down` | `UPCountDown` / `UPCountDownProps` | 基本完成 | 高（Props） | 新增 `UPCountDownController` 提供上游 ref 上的 `start()`/`pause()`/`reset()`（`start()` 在运行中直接返回，`pause()` 保留余量，`reset()` 复位后按 `autoStart` 决定是否重开）；计时改为上游的截止时间基准（`endTime = 此刻 + 剩余`，每拍重新读时钟）而非固定递减，`millisecond` 在 30ms 宏拍与 50ms 微拍之间切换且宏拍按 `isSameSecond` 抑制同秒重绘；`format` 复刻 `parseFormat` 的降级链（缺 `DD` 则天折进小时，缺 `HH` 折进分钟，依此类推，`SSS` 补三位且只替换首个匹配），单测逐条对照 node 跑出的上游读数，另有 8 项真机断言与作用域插槽。 |
| 36 | 数值与时间 | `u-count-to` | `UPCountTo` / `UPCountToProps` | 基本完成 | 高（Props） | 数字格式和回调之外，改为真实逐帧动画：`withFrameMillis` 每帧推进并回调 `onChange`，`useEasing` 在上游 ease-out-expo 曲线（`(c·(-2^(-10t/d)+1)·1024)/1023+b`）与线性斜坡之间切换，向上/向下计数都在 `endVal` 处收敛，`duration<=0` 直接跳到终值；单测逐点对照上游公式，另有真机断言。 |
| 37 | 基础展示 | `u-coupon` | — | 未开始 | 暂无 | 优惠券展示/选择，待建立业务字段契约。 |
| 38 | 媒体与内容 | `u-cropper` | — | 未开始 | 暂无 | 图片裁剪，待接入原生手势和输出 URI。 |
| 39 | 选择与日期 | `u-datetime-picker` | `UPDatetimePicker` / `UPDatetimePickerProps` | 基本完成 | 中 | year-month/date/time/datetime 基础列选择、时间戳和 value/modelValue 更新已支持；列已按 `visibleItemCount × itemHeight` 固定高度并可滚动（此前平铺导致屏幕外选项无法点击）；`hasInput` 渲染只读 `u-input` 触发器（文案按 `format` 或按 mode 缺省格式化，`time`/`timesecond` 原样显示）并由覆盖层接管点击、`inputBorder` 映射 `u-input` 的 `surround`/`bottom`/`none`（布尔转枚举）、`inputProps` 逐键覆盖 18 个 `u-input` 字段（键名大小写与 `-`/`_` 归一，未知键上报诊断）、`toolbarRightSlot` 用 `toolbarRight` 插槽替换确认按钮（连同确认事件一并让位，与上游 `u-toolbar` 一致）、`maskStyle` 仅在显式传值时覆盖列遮罩且不拦截点击。降级：`popupMode` 仅 `top`/`bottom` 可由内联面板表达，其余四值上报诊断；`maskClass` 无原生等价，改用 `maskStyle`。惯性滚轮视觉、filter/formatter 尚未复刻。 另补齐 `filter(type, values)`（按列过滤，过滤后为空则照上游日志语义回退整列不过滤）与 `formatter(type, value)`（逐项改写标签，缺省按上游对非年份补零），不可调用或抛异常均走诊断降级。降级：`defaultIndex` 与 `loading` 上游自身从不透传给 `u-picker`（模板只用内部的 `innerDefaultIndex`），因此保留字段但不生效。 |
| 40 | 基础展示 | `u-divider` | `UPDivider` / `UPDividerProps` | 基本完成 | 高（Props） | 分割线方向、文字和样式已有实现。 |
| 41 | 列表与拖拽 | `u-dragsort` | — | 未开始 | 暂无 | 拖拽排序，待采用 Compose drag-and-drop 方案。 |
| 42 | 原生交互 | `u-dropdown` | `UPDropdown` / `UPDropdownProps` | 基本完成 | 中 | 标题栏样式由父级下发并生效：`height` 限定标题行高、`titleSize` 控制标题字号、`menuIcon`/`menuIconSize` 决定箭头、`borderBottom` 绘制下边框、`borderRadius` 作用于展开面板、`duration` 驱动展开动画；`closeOnClickOverlay` 作为 `closeOnClickMask` 的兼容别名优先生效。均有真机断言。降级：`menu` 为 Android 专有兼容别名，上游无对应字段。未完成：向上展开需把面板搬进窗口级 `Popup`。 |
| 43 | 原生交互 | `u-dropdown-item` | `UPDropdownItem` / `UPDropdownItemProps` | 基本完成 | 中 | 标题点击开合、单选/多选 payload、选项禁用与 `height` 限定面板最大高度（超出滚动）均已生效并有真机断言；复杂内容插槽已支持 `content`。 |
| 44 | 基础展示 | `u-empty` | `UPEmpty` / `UPEmptyProps` | 基本完成 | 高（Props） | 图标、描述、按钮和样式已有实现。 |
| 45 | 原生交互 | `up-float-button` | `UPFloatButton` / `UPFloatButtonProps` | 基本完成 | 高（Props） | 悬浮圆形按钮：`backgroundColor`/`color`/`borderColor`/`width`/`height` 定外观，`isMenu=true` 时点击展开 `list` 菜单（每项 icon-only 圆形，可用 `backgroundColor`/`color`/`borderColor` 覆盖，菜单在主按钮上方 `bottom:height` 处竖排、间距 5px），主按钮 plus 图标在展开时旋转 45°（`.show-list{transform:rotate(45deg)}`）；`click` 主按钮、`item-click` 列表项（payload `{...item, index}`）分别经 `onClick`/`onItemClick` 暴露，`showList` 作用域插槽与列表插槽均支持。降级：上游 `position:fixed`，Android 无固定定位，改由本组件在所给空间的角落锚定（默认右下，`top` 非空时右上），`right`/`top`/`bottom` 作为从该角向内的 offset（同 `u-back-top` 的换算）。有 1 项默认值单测与 2 项真机断言（普通点击不开列表、菜单开合与 item-click 携带 index）。 |
| 46 | 表单与协议 | `u-form` | `UPForm` / `UPFormProps` | 基本完成 | 高（Props） | model/rules/errorType/labelPosition/labelWidth/labelAlign/labelStyle 逐字段下发子项；`UPFormController` 复刻 validate/validateField/resetFields/resetField/clearValidate/setRules 六个 ref 方法，async-validator 的 required/type/range/pattern/whitespace/enum/transform/validator 与消息模板逐条实现；errorType="toast" 交由 `onToast` 宿主回调。差异：Kotlin `model` 不可变，`resetFields()` 经 `onUpdateModel` 回传首帧快照；`borderBottom` 上游仅存于死代码 computed，保留字段但不生效。 |
| 47 | 表单与协议 | `u-form-item` | `UPFormItem` / `UPFormItemProps` | 基本完成 | 高（Props） | 标签宽度/对齐/位置回落父级、必填星号绝对定位不占布局、leftIcon、label/right/error 三插槽、错误文案缩进（labelPosition="top" 归零）、borderBottom 画线与错误色均有真机断言。降级：上游 `rightIcon` 声明后从未渲染，Android 同样保留字段但不生效。 |
| 48 | 基础展示 | `u-gap` | `UPGap` / `UPGapProps` | 基本完成 | 高（Props） | 间隔尺寸和背景已有实现。 |
| 49 | 选择 | `u-goods-sku` | — | 未开始 | 暂无 | 商品规格选择器，待明确业务数据模型。 |
| 50 | 布局 | `u-grid` | `UPGrid` / `UPGridProps` | 基本完成 | 高（Props） | 列数、间距、边框和点击布局已有测试。 |
| 51 | 布局 | `u-grid-item` | `UPGridItem` / `UPGridItemProps` | 基本完成 | 高（Props） | 图标、文字和点击项已有实现。 |
| 52 | 原生交互 | `u-guide` | — | 未开始 | 暂无 | 新手引导遮罩和高亮定位待实现。 |
| 53 | 基础能力 | `u-icon` | `UPIcon` / `UPIconProps` | 基本完成 | 高（Props） | 已接入固定上游 icon font；`isImg`（name 含 `/`）按上游改渲 `<image>`，`imgMode` 透传成 `u-image` 的 mode、`width`/`height` 按 `imgStyle` 语义生效（留空时两边都回落 `size`），均有真机断言，图片加载走可注入的 `UPImageLoader`。降级：自定义图标字体（`customPrefix != "uicon"`）无法在内置字体里查形，仍走诊断。 |
| 54 | 媒体与内容 | `u-image` | `UPImage` / `UPImageProps` | 基本完成 | 高（Props） | 加载、错误、裁剪模式和占位已有实现；`loadingIcon` 决定加载态图标、`fade` 与 `duration` 共同驱动加载完成后的淡入（`fade=false` 时时长归零），均有真机断言。刻意差异：上游 transition 时长写死 1000ms 且 `duration` 在模板中被注释掉，Android 让 `duration` 真实生效。降级：`showMenuByLongpress` 仅微信小程序有效，Android 保留字段但不生效，缺省 `false`；`errorIcon` 之外的自定义错误插槽待补。 |
| 55 | 列表与索引 | `u-index-anchor` | `UPIndexAnchor` / `UPIndexAnchorProps` | 基本完成 | 高（Props） | 文本（含选项对象的 `name`）、颜色、字号、背景与高度之外，锚点现在按 `indexList.anchors.push(this)` 的语义向父级上报自身偏移，索引条拖动即可滚到它；`parentSticky`（`indexList ? indexList.sticky : true`，独立使用时为真）以标记形式暴露在树上，宿主据此决定是否用自己的 sticky header API 固定。均有真机断言。降级：Compose 没有 CSS `position: sticky`，固定动作本身仍归宿主。 |
| 56 | 列表与索引 | `u-index-item` | `UPIndexItem` / `UPIndexItemProps` | 基本完成 | 高（Props） | 上游该组件的 props 契约本身为空（只有 `customStyle`），职责是包裹锚点与内容并让父级测得偏移——定位逻辑现由内部的 `u-index-anchor` 上报完成（见第 55 行），因此本组件已无缺口。自定义内容与样式有真机断言。 |
| 57 | 列表与索引 | `u-index-list` | `UPIndexList` / `UPIndexListProps` | 基本完成 | 高（Props） | 右侧索引条本轮从「可点击的一列文字」升级为完整交互：`indexList` 为空时按上游 `uIndexList()` 生成 A–Z 兜底（此前会整条不渲染）；整条索引条是**一个手势目标**而非 26 个按钮，按 `getIndexListLetter(pageY)` 换算触点落在哪个字母、两端越界各自钳到首/末字母、`setValueForTouch` 的同字母去抖照样生效；选中即按 `u-index-item-${charCodeAt(0)}` 找到对应锚点并滚动过去；`.u-index-list__indicator` 的 50×50 旋转气泡在按住期间放大显示当前字母、松手后按上游 `sleep(300)` 延时隐藏；激活字母按 `--active` 填 `activeColor` 圆片 + 白字。`activeColor`/`inactiveColor`/`itemMargin`/`customNavHeight`/`safeBottomFix`/`sticky` 全部生效。换算部分有 6 项单测逐条对照，交互有真机断言。 |
| 58 | 键盘与输入 | `u-input` | `UPInput` / `UPInputProps` | 基本完成 | 高（Props） | `modelValue/value`、清除、密码、前后缀和常用样式已有测试；`selectionStart`/`selectionEnd`/`cursor` 已通过 `TextFieldValue` 生效（聚焦时应用、越界自动钳制）。降级：`adjustPosition`、`autoBlur`、`cursorSpacing`、`fixed`、`holdKeyboard`、`disableDefaultPadding`、`ignoreCompositionEvent`、`placeholderClass` 为 uni-app/小程序专有，保留字段但不生效。 |
| 59 | 键盘与输入 | `u-keyboard` | `UPKeyboard` / `UPKeyboardProps` | 基本完成 | 高（Props） | 键盘弹层容器：底部 `u-popup` 承载 `mode=number/card` 的数字键盘或 `car` 的车牌键盘，`tooltip` 顶部工具条含 `showCancel`/`showConfirm` 按钮与 `showTips` 中间提示（`tips` 为空时按 mode 缺省 `数字键盘`/`身份证键盘`/`车牌号键盘`）；`overlay`/`closeOnClickOverlay`/`safeAreaInsetBottom`/`zIndex` 透传 popup，`random`/`dotDisabled`/`autoChange` 透传内层键盘；`change`/`backspace` 由内层冒泡、`cancel`/`confirm` 出自工具条、`close` 在 popup 关闭时触发。有 1 项默认值/缺省提示单测与 3 项真机断言（number 工具条 + change/confirm/cancel 冒泡、car 模式承载车牌键盘、`show=false` 不渲染）。 |
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
| 70 | 键盘与输入 | `u-message-input` | `UPMessageInput` / `UPMessageInputProps` | 基本完成 | 高（Props） | 验证码输入框（非 `u-code-input`）：`maxlength` 决定格子数、一个透明 number 字段承接输入、`mode` 三态装饰（`box` 描边并高亮当前格取 `activeColor`、`bottomLine`/`middleLine` 画 0.8×格宽的横线且 `bold` 决定 2px/4px 粗细）、填充格按 `dotFill` 显示 `●` 或数字并用 `inactiveColor`/`fontSize`(rpx)/`bold` 排版、格宽高同为 `width`(rpx)、当前格在非 `middleLine` 模式下画 0.5×格高的占位竖线；`modelValue` 按上游 watcher 先字符串化再截到 `maxlength`，`change` 每次编辑触发、`finish` 在长度达 `maxlength` 时触发。`breathe` 复刻上游 `@keyframes breathe`（透明度 0.3→1→0.3、2s 无限交替）用无限过渡驱动当前格呼吸，`breathe=false` 时当前格保持全不透明。有 1 项默认值/截断单测与 2 项真机断言（逐格渲染 + change/finish、非数字过滤并截到 maxlength）。 |
| 71 | 原生交互 | `u-modal` | `UPModal` / `UPModalProps` | 基本完成 | 高（Props） | 原生 Dialog/Compose 弹窗、确认取消和样式已有实现；`negativeTop` 以负 margin 上移弹窗避让键盘、`duration` 驱动淡入（`zoom=true` 时叠加 0.8→1 缩放），均有真机断言。差异：上游把 `duration` 透传给 `u-popup` 的过渡，Android 的 popup 无自带过渡，改由 modal 面板自身承载。 |
| 72 | 导航 | `u-navbar` | `UPNavbar` / `UPNavbarProps` | 基本完成 | 高（Props） | 安全区、标题和 icon 之外，`border` 画 0.5px 下边框、`fixed` 提升到 `zIndex=11`（对应 `.u-navbar--fixed`）、`autoBack` 决定左键是否触发新增的 `onBack` 宿主回调，均有真机断言。降级：`position: fixed` 的实际定位由宿主布局决定；`statusBarBgColor` 上游注明仅为兼容保留（状态栏统一用 `bgColor`），Android 显式上报诊断而非静默丢弃。 |
| 73 | 导航 | `u-navbar-mini` | `UPNavbarMini` / `UPNavbarMiniProps` | 基本完成 | 高（Props） | 迷你导航和 icon 之外，`fixed` 按 `.u-navbar-mini--fixed` 偏移 `left: 20px; top: 10px` 并提升到 `zIndex=11`、`autoBack` 在 `leftClick` 之后触发新增的 `onBack` 宿主回调（与上游先 emit 再 `navigateBack` 的顺序一致），均有真机断言。降级：`position: fixed` 的实际定位由宿主布局决定。 |
| 74 | 通知与状态 | `u-no-network` | `UPNoNetwork` / `UPNoNetworkProps` | 基本完成 | 中 | `connected=false` 时以白底 `u-overlay` 铺满宿主并居中堆叠：`image` 非空才用 `u-icon` 按 150px/`widthFit` 画图（上游默认是内置 base64 PNG，Android 改取 drawable 路径或 URL）、`tips` 按 `.u-no-network__tips`（14px tips 色、上方 15px）、`.u-no-network` 的 `margin-top:-100px` 用底部 100dp 内边距等价上移、`重试` 按钮按 `.u-no-network__retry` 用 mini plain primary 按钮并回调 `onRetry`；`connected=true` 时整体不渲染（复刻 `:show="!isConnected"`）。降级：上游 `isConnected` 由 `uni.onNetworkStatusChange` 内部维护，Android 的连通性观测需宿主持有的 `ConnectivityManager`，故把 `isConnected` 提升为 `connected` 参数、`retry()` 经 `onRetry` 回调让宿主复检并翻转；`APP-PLUS` 的「前往设置」行调用 `plus.*` 仅 App 运行时可编译，无 Android 对应物，刻意省略。有 1 项默认值单测与 2 项真机断言（断网显示提示与重试、连通不渲染）。 |
| 75 | 通知与状态 | `u-notice-bar` | `UPNoticeBar` / `UPNoticeBarProps` | 基本完成 | 高（Props） | 通知文字、方向和点击之外：`direction="row"` 按 `t = s / v` 实现真实横向跑马灯（文字从右边缘进、越过左边缘出，`speed` 为每秒像素数，宽度按实测值算）；`direction="column"` 或 `step=true` 走逐条轮播，`duration` 作为切换间隔并循环；`disableTouch=false` 时可上下拖动翻页，均有真机断言。 |
| 76 | 原生交互 | `u-notify` | `UPNotify` / `UPNotifyProps` | 基本完成 | 高（Props） | 顶部通知本轮补齐三处与上游的差距：新增 `UPNotifyController` + `UPNotifyHost` 复刻 ref 调用方式（`show(options)` 每次从默认值重新合并，因而不会继承上次调用的覆盖项——对应上游「避免多次调用造成混乱」的注释，另有 `primary`/`success`/`warning`/`error` 四个只收 message 的快捷方法与 `close()`）；`icon()` 的图标表按主题生效（`primary` 无图标），字号按 `1.3 × fontSize` 放大；`safeAreaInsetTop` 的状态栏占位放在**着色横幅内部**（上游 `<u-status-bar>` 就在 `.u-notify` 里），因而底色延伸到状态栏后面；文案按 `.u-notify__warpper` 居中，内边距对齐 `8px 10px`，并按 `<u-transition mode="slide-down">` 从上方滑入。有单测与真机断言。 |
| 77 | 内容与解析 | `u-novel-reader` | — | 未开始 | 暂无 | 小说阅读器业务组件，不纳入当前基础组件批次。 |
| 78 | 数值与时间 | `u-number-box` | `UPNumberBox` / `UPNumberBoxProps` | 基本完成 | 高（Props） | 步进、范围、精度、禁用和受控值已有测试；`longPress` 按上游时序实现长按连续加减（按住 600ms 进入长按、之后每 250ms 步进一次，松手时 `@tap` 仍照常再走一次），有真机断言。降级：`cursorSpacing` 是 uni-app 的键盘避让提示，Android 由 `windowSoftInputMode` 处理，记为按设计不生效。 另补齐 `name` 与 `iconStyle`：`change` 事件改为上游的 `{ value, name, type }` 形状（`type` 只在点击加减时有值，手动输入为空，对应上游「手动输入不支持」），新增 `onFocus`/`onBlur` 也带上 `name`，失焦时空输入按上游强制回到 `min`；`iconStyle` 同时作用于加号与减号图标。均有真机断言。 |
| 79 | 键盘与输入 | `u-number-keyboard` | `UPNumberKeyboard` / `UPNumberKeyboardProps` | 基本完成 | 高（Props） | 三列数字键盘：`mode` 选 `number`（数字，可选点）或 `card`（数字 + 身份证 `X`），`dotDisabled` 在数字模式隐藏 `.` 键并让 `0` 跨两列（`width:464rpx`），`random` 打乱键序；末位退格键灰底，`change` 回报点击值（数字模式启用点时纯数字转 Int，`.`/`X` 保持字符串）、`backspace` 在删除键触发。键集与转换逐条对照上游 `numList`/`keyboardClick`。降级：上游按住退格每 250ms 重复触发，Android 每次点击触发一次，长按重复可由宿主叠加。有 1 项键集/转换单测与 2 项真机断言（number 模式回报数字与退格、card 模式暴露 `X` 键并保持字符串）。 |
| 80 | 原生交互 | `u-overlay` | `UPOverlay` / `UPOverlayProps` | 基本完成 | 高（Props） | 原生 Compose 遮罩、透明度和点击关闭已有实现；`duration` 驱动 `opacity` 从 0 淡入（`duration=0` 首帧即最终不透明度），有真机断言。 |
| 81 | 选择与日期 | `u-pagination` | `UPPagination` / `UPPaginationProps` | 基本完成 | 高（Props） | `layout` 按 `total, prev, pager, next, sizes` 逐段解析并按序渲染，`pageSize`/`total` 驱动完整页码算法（`pagerCount` 限定窗口、省略号补位）、`buttonBgColor`/`buttonBorderColor` 作用于前后翻页按钮、`hideOnSinglePage` 单页时整体隐藏、`pageSizes` 提供每页条数轮转并回传 `onUpdatePageSize`/`onSizeChange`，均有真机断言。刻意差异：`hideOnSinglePage` 在上游只声明未被使用，Android 按字段语义真实实现；`sizes` 段上游是 `<select>` 下拉，Android 改为点击轮转候选值。 |
| 82 | 内容与解析 | `u-parse` | — | 未开始 | 暂无 | HTML 富文本解析待确定原生实现边界。 |
| 83 | 内容与解析 | `u-pdf-reader` | — | 未开始 | 暂无 | PDF 阅读器待接入 Android 原生 PDF 能力。 |
| 84 | 选择与日期 | `u-picker` | `UPPicker` / `UPPickerProps` | 基本完成 | 中 | modelValue/value/defaultIndex 和事件 payload 已修正；列已按 `visibleItemCount × itemHeight` 固定高度并可滚动，选项在行内垂直居中；`hasInput` 渲染只读 `u-input` 触发器（文案经 `keyName` 还原对象列标签）并由覆盖层接管点击打开面板、`inputBorder` 映射 `u-input` 的 `surround`/`bottom`/`none`（布尔转枚举）、`inputProps` 逐键覆盖 18 个 `u-input` 字段（键名大小写与 `-`/`_` 归一，未知键上报诊断）、`toolbarRightSlot` 用 `toolbarRight` 插槽替换确认按钮、`maskStyle` 仅在显式传值时覆盖列遮罩、`popupMode` 经 `round` 决定面板圆角朝向。另补齐 `loading`（按上游 `.u-picker--loading` 用不透明面板 + circle 加载图标整列盖住，期间选项节点不存在因而不可点）、`duration`（面板淡入）与 `zIndex`（同级覆盖顺序）。降级：`maskClass` 为 CSS 类钩子，无原生等价。未完成：`popupMode` 的 `left`/`right`/`center` 与 `closeOnClickOverlay`/`overlayOpacity` 都依赖窗口级弹层，内联面板仅接受 `top`/`bottom` 并对其余值上报诊断——该层已证明可用（见 `u-tooltip`），搬迁是本组件自身的后续工作。滚轮已改为带吸附的列表（见第 85 行 `u-picker-column`）。 |
| 85 | 选择与日期 | `u-picker-column` | `UPPickerColumn` / `UPPickerColumnProps` | 基本完成 | 高（Props） | 上游该组件是 `<picker-view-column>` 的空壳（props 契约为空，只有 `customStyle`），真正的滚轮行为属于父级 `picker-view`——本轮已在 `u-picker` 侧实现：列改为带吸附的 `LazyColumn`，两端各留半个视口的内边距（这是首/末项能进入中央选中带的前提）、`indicatorStyle` 的等高选中带用双 hairline 画出、滚动停下即为 `change`（上游 `picker-view` 由滚动而非点击上报，并按索引数组差分找出变化的那一列）、选中项按 `fontWeight: bold` 加粗、列内 `disabled` 项按 0.35 透明度变暗且不可选、标签按 `.u-line-1` 单行省略。滚轮几何有 4 项单测、行为有 5 项真机断言。 |
| 86 | 辅助模块 | `u-picker-data` | — | 未开始 | 暂无 | 选择器数据辅助目录，不单独作为 Android UI 组件。 |
| 87 | 原生交互 | `u-popover` | `UPPopover` / `UPPopoverProps` | 基本完成 | 高（Props） | 改为按上游结构实现——`u-popover` 本身就是「填了 content 插槽、去掉复制按钮的 `up-tooltip`」，因此这里直接把 `text`/颜色三项/`direction`/`placement`/`triggerMode`/`show`/`zIndex`/`forcePosition` 转发给 `UPTooltip`（此前是各自独立的一套内联布局）。`direction` 是 tooltip 真正读取的字段，`placement` 只在 `direction` 为空时才起作用；`hover` 在 Android 无对应物，按既有约定映射为长按。随窗口级弹层落地，`zIndex` 与 `forcePosition` 均已生效。有真机断言。 |
| 88 | 原生交互 | `u-popup` | `UPPopup` / `UPPopupProps` | 基本完成 | 高（Props） | Compose 弹层基础能力之外，本轮补齐入场过渡与底部手势：`position()` 计算表逐项复刻（仅 `center` 读 `zoom`，给出 `fade-zoom`/`fade`，其余方向给 `slide-up`/`slide-down`/`slide-left`/`slide-right`，`pageInline` 一律 `none`），`duration` 驱动位移与淡入（淡入只属于两种 fade，滑入按 `translate3d` 的整屏偏移起步，缩放取上游 `scale(0.95)`）；`touchable` 仅在 `mode="bottom"` 时生成 100×5 指示条的拖拽区，拖动在 `[minHeight, maxHeight]`（缺省 200px 与窗口 80%）内改高、越界保留原高不做 clamp，松手按上游阈值（位移 > 100px，或 > 30px 且速度 > 0.5px/ms）关闭；`safeAreaInsetTop` 让出状态栏。计算部分有单测逐条对照，行为有真机断言。 |
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
| 99 | 导航 | `u-safe-bottom` | `UPSafeBottom` / `UPSafeBottomProps` | 基本完成 | 高（Props） | 上游契约本就只有 `customStyle` 与底部安全区占位（`u-safe-area-inset-bottom` = `padding-bottom: env(safe-area-inset-bottom)`，nvue 下改用 `getWindowInfo().safeAreaInsets.bottom` 显式填高）。Android 用 `navigationBarsPadding()` 作语义等价填充，`safeAreaInsetBottom=false` 时不占位，`customStyle` 逐字段覆盖，已逐项与上游对齐、无字段缺口。有真机断言验证占位高度等于导航栏 inset。 |
| 100 | 列表与索引 | `u-scroll-list` | `UPScrollList` / `UPScrollListProps` | 基本完成 | 中 | 内容已可横向滚动，并按上游默认（`indicator: true`）渲染指示器：`indicatorWidth` 定轨道宽、`indicatorBarWidth` 定滑块宽、`indicatorColor`/`indicatorActiveColor` 分别着色轨道与滑块、`indicatorStyle` 可再覆盖样式，滑块位置跟随滚动进度；均有真机断言。此前既不滚动也不渲染指示器。 |
| 101 | 键盘与输入 | `u-search` | `UPSearch` / `UPSearchProps` | 基本完成 | 高（Props） | 输入、清除、搜索按钮和受控值已有实现。 |
| 102 | 基础展示 | `u-section` | — | 未开始 | 暂无 | 区块标题组件待建立。 |
| 103 | 选择 | `u-select` | `UPSelect` / `UPSelectProps` | 基本完成 | 高（Props） | options、current、select/update 事件与 `showOptionsLabel`/`optionsWidth` 之外，本轮补齐面板自身的全部字段：`maxHeight` 支持 `90vh` 视口单位（按窗口高度折算，另接受 px/rpx/数值）并配 `overflowY: auto` 滚动、`zIndex` 按上游 `zIndex + 1` 抬高选项层、`itemColor`/`iconColor`/`iconSize` 分别作用于选项文字与触发器箭头、`border` 给触发器 1px 描边 + 4px 圆角 + 36dp 最小高度、`duration` 驱动面板淡入（上游把它交给遮罩，内联无遮罩故由面板承载）。未完成：`overlay`/`overlayStyle`/`overlayOpacity` 与「面板浮在后续内容之上」都依赖把选项面板搬进窗口级 `Popup`（该层已证明可用，见 `u-tooltip`）；当前内联渲染会把后续内容顶下去。 |
| 104 | 媒体与内容 | `u-short-video` | — | 未开始 | 暂无 | 短视频播放器涉及 ExoPlayer 和生命周期。 |
| 105 | 原生能力 | `u-signature` | — | 未开始 | 暂无 | 手写签名画布待实现。 |
| 106 | 通知与状态 | `u-skeleton` | `UPSkeleton` / `UPSkeletonProps` | 基本完成 | 高（Props） | 骨架行、头像、标题和动画开关之外，`rowsWidth` 复刻 `rowsArray` 的取值链——数组按行取值、越界或未给时首几行 100%、末行固定 70%，百分比换算为可用宽度的比例、px 走绝对宽度；`avatarShape` 在 circle/square 之间切换头像圆角并对未知值回落诊断，均有真机断言。 |
| 107 | 选择 | `u-slider` | `UPSlider` / `UPSliderProps` | 基本完成 | 高（Props） | 单值、range、step 量化和 changing/change 手势之外：`height` 按上游 `sizeLocal` 语义覆盖 `size` 作为轨道厚度、`length` 限定轨道自身轴向长度（`auto` 交回父级测量）、`innerStyle` 作用于轨道行且行高按 `blockSize`（range 且 `showValue` 时 +24）计算、`blockStyle` 作用于滑块并可改其尺寸圆角，均有真机断言。降级：`useNative` 要求 uni-app 平台 `<slider>`，本库只依赖 Compose foundation，回落到自绘轨道并上报诊断（上游对 range 同样不用原生控件）。原生无障碍语义仍待完善。 |
| 108 | 导航 | `u-status-bar` | `UPStatusBar` / `UPStatusBarProps` | 基本完成 | 高（Props） | `bgColor` 背景与 `height` 高度之外，本轮补齐两处上游语义：其一 `emits: ['update:height']`——上游 `style()` 里读 `getWindowInfo().statusBarHeight` 并回传，Android 侧新增 `onUpdateHeight: ((Dp) -> Unit)?`，`height=0`（默认）时回传 `WindowInsets.statusBars` 实测 inset，显式高度则回传该值；其二默认 `<slot />`——新增 `content` 插槽，此前是空 `Box` 无从放子节点。`height=0` 落到 `statusBarsPadding()` 顶部安全区、非零走固定高度，`customStyle` 逐字段覆盖。两项各有真机断言（回传插入高度并渲染插槽内容、显式高度回传该高度）。 |
| 109 | 导航 | `u-steps` | `UPSteps` / `UPStepsProps` | 基本完成 | 中 | `current` 驱动 finish/process/wait/error 四态、`direction` 控制横纵布局、`activeColor`/`inactiveColor`/`dot`/`activeIcon`/`inactiveIcon` 均已生效并有真机测试。 |
| 110 | 导航 | `u-steps-item` | `UPStepsItem` / `UPStepsItemProps` | 基本完成 | 中 | 按索引与父级 `current` 推导状态：已完成显示 ✓、当前步为实心序号、未达步为灰色序号、`error` 显示 ✕；`iconSize`（对齐上游 17）与 `itemStyle` 已生效。 |
| 111 | 原生交互 | `u-sticky` | `UPSticky` / `UPStickyProps` | 基本完成 | 高（Props） | 真实滚动吸顶已实现，且**不需要宿主回传偏移**——此前的判断是错的：吸顶带自己就能读到自身在窗口中的位置（`onGloballyPositioned` + `boundsInWindow`），这正是上游 IntersectionObserver 观察的同一个量。据此复刻上游的 JS 分支：`setFixed(top) { fixed = top <= stickyTop }` 判定吸顶，超出量用 `translationY` 补回去，因而内容视觉上钉在 `stickyTop`、而布局槽位仍被占住（对应上游用记录高度实现的「防塌陷」）；新增 `onFixed`/`onUnfixed` 两个事件，只在状态翻转时触发并回传 `index`（上游文档里 `index` 就是「自定义标识，用于区分是哪一个组件」，此前被登记为不生效）。`offsetTop` + `customNavHeight` 折算 `stickyTop`、`zIndex` 缺省取 `zIndex.sticky`（970）、`disabled` 回落到 `position: static` 因而永不吸顶，均有真机断言。**修正了一处此前的偏离**：旧实现把 `stickyTop` 当作无条件的顶部内边距，静止时就把整页往下推，而 `position: sticky` 在页面滚动前不移动任何东西。 |
| 112 | 导航 | `u-subsection` | `UPSubsection` / `UPSubsectionProps` | 基本完成 | 高（Props） | `mode` 复刻两种形态：`button` 在灰底轨道内滑动白色药丸（34px 高、3px 内边距），`subsection` 给每项描 1px 边框并让 `activeColor` 滑块托住白色文字（32px 高）；滑块按测得的项宽平移并以 300ms 过渡，`bold`/`fontSize` 作用于激活项文字，`activeColorKeyName`/`inactiveColorKeyName` 从列表项对象读取逐项颜色覆盖（优先级高于 `activeColor`/`inactiveColor`），`disabled` 拦截点击并整体切换到禁用色，均有真机断言。 |
| 113 | 原生交互 | `u-swipe-action` | `UPSwipeAction` / `UPSwipeActionProps` | 基本完成 | 中 | 父级协调已实现：`autoClose` 打开一项时关闭其余项、`opendItem` 置 false 触发 closeAll、并通过 `onUpdateOpendItem` 上报开合状态；均有真机断言。 |
| 114 | 原生交互 | `u-swipe-action-item` | `UPSwipeActionItem` / `UPSwipeActionItemProps` | 基本完成 | 中 | 横向拖动超过 `threshold`（默认 20）才展开、`duration` 驱动展开动画、`show` 为受控开合状态、`closeOnClick` 点击后收起、`disabled` 忽略手势；均有真机断言。此前按钮由 `show` 常驻显示且完全没有手势。 |
| 115 | 媒体与内容 | `u-swiper` | `UPSwiper` / `UPSwiperProps` | 基本完成 | 中 | `autoplay`+`interval` 定时切换、`circular` 末尾回头、`previousMargin`/`nextMargin` 露边、`indicatorStyle` 已生效；本批补齐 `imgMode`（图片项经 `UPImage` 渲染，`keyName`/`getSource` 对齐上游）、`radius` 圆角裁剪、`showTitle` 半透明标题条（显示标题时隐藏指示器）、`vertical` 纵向布局与纵向拖拽、`displayMultipleItems` 视口均分、`currentItemId`（优先级高于 `current`）、`duration` 过渡动画和 `loading` 占位。视频项渲染 `poster` + 播放图标并上报诊断（无原生播放器）；`acceleration` 降级为诊断上报，`easingFunction` 登记为按设计不生效。 |
| 116 | 媒体与内容 | `u-swiper-indicator` | `UPSwiperIndicator` / `UPSwiperIndicatorProps` | 基本完成 | 高（Props） | line/dot 两种模式、当前项尺寸（`--active` 把点从 5px 拉宽到 12px）、颜色与点击回调之外，本轮对齐三处细节：line 模式的滑块按 `transition: transform 0.3s` 平移而非跳变；dot 之间的间距改为每点 `margin: 0 4px`（外侧同样内缩，此前用 8dp 的整体间隔、外缘没有留白）；`indicatorMode` 未知值按上游两个并列 `v-if` 的语义**什么都不渲染**并上报诊断（此前会静默回落到 line）。有真机断言与像素级断言。 |
| 117 | 选择 | `u-switch` | `UPSwitch` / `UPSwitchProps` | 基本完成 | 高（Props） | 受控值、禁用、颜色和 change/update 事件已有测试。 |
| 118 | 导航 | `u-tabbar` | `UPTabbar` / `UPTabbarProps` | 基本完成 | 高（Props） | 父子受控状态、颜色、边框、安全区、9 种 `styleType`、active/inactive 背景、`itemShape`、`textMode`、`iconScale`、`animationType` 之外，`placeholder` 按上游 `setPlaceholderHeight()` 的守卫（`fixed` 与 `placeholder` 同时为真才生成）用实测栏高撑出等高占位、`zIndex` 决定导航栏覆盖同级兄弟的顺序，均有真机断言。降级：`fixed` 的窗口级固定定位归宿主（放入 Scaffold bottomBar 或底部 Box），Android 侧只保留它对占位元素的开关语义。 |
| 119 | 导航 | `u-tabbar-item` | `UPTabbarItem` / `UPTabbarItemProps` | 基本完成 | 高（Props） | active/inactive icon、文字、badge/dot、name 事件、状态背景和 underline/dot 指示器已支持；`animationType` 仅作用于激活图标，`midButton` 已支持 64dp 外层、52dp 内层及垂直偏移。CSS class hook 和 box-shadow 通过原生语义/阴影近似并发出降级诊断，复杂视觉仍待上游逐项对照。 本轮再对齐三处：`badge` 的绝对偏移按 `[0, dot ? '34rpx' : badge > 9 ? '14rpx' : '20rpx']` 随徽标宽度变化（此前固定用默认角位）；`textMode="active"` 的未选中文字按 `.u-tabbar-item__text--muted` 同时降到 0.68 不透明度并缩到 0.94（此前只降透明度、且改的是颜色而非图层），过渡取 0.22s；`underline`/`dot` 指示器按 `34rpx`/`10rpx` 的实际尺寸居中绘制（此前下划线撑满整格）。有真机断言与像素级断言。 |
| 120 | 表格 | `u-table` | — | 未开始 | 暂无 | 表格容器待建立列宽和滚动契约。 |
| 121 | 表格 | `u-table2` | — | 未开始 | 暂无 | 第二版表格，待确认与 `u-table` 的 API 差异。 |
| 122 | 导航 | `u-tabs` | `UPTabs` / `UPTabsProps` | 基本完成 | 高（Props） | tabs/current/change 之外，`shapeMode` 复刻 line/capsule/card/pill-arrow/tag 五种形态（card 斜切四边形、pill-arrow 箭头由 Canvas 绘制）、`activeStyle`/`inactiveStyle`/`itemStyle` 逐项应用、`lineBgSize` 区分 cover/contain/auto 下划线宽度、`duration` 驱动下划线位移动画、`iconStyle` 作用于选项图标，均有真机断言。未完成：粘性吸顶待补——`u-sticky` 已证明吸顶带自己就能观测位置，把同一套做法套到 tabs 上即可。 |
| 123 | 导航 | `u-tabs-item` | `UPTabsItem` / `UPTabsItemProps` | 基本完成 | 高（Props） | 上游是 `<swiper-item><slot /></swiper-item>` 纯壳，props 契约为空，且全仓库只有自身文件引用它。Android 侧 `content` 插槽透传子节点、`customStyle` 逐字段覆盖，已与上游空契约逐项对齐、无字段缺口。有真机断言验证插槽内容与 `customStyle` 定高生效。 |
| 124 | 导航 | `u-tabs-pro` | — | 未开始 | 暂无 | Pro 标签页待确认专属字段和事件。 |
| 125 | 基础展示 | `u-tag` | `UPTag` / `UPTagProps` | 基本完成 | 高（Props） | 类型、形状、图标、关闭和颜色已有测试；`height`/`borderRadius`/`plainFill` 已生效并有真机断言（此前声明但从不读取）。`autoBgColor > 0 && color` 时按上游 `genLightColor` 的 RGB→HSL→亮度封顶 95%→HEX 流程推导同色系浅色背景（优先级高于 `bgColor` 与类型色），单测逐值对照上游输出；上游只解析 hex 与 `rgb()`/`rgba()` 并对其余格式抛错，Android 改为上报诊断并保留原背景。 |
| 126 | 表格 | `u-td` | — | 未开始 | 暂无 | 表格单元格待随表格体系实现。 |
| 127 | 基础展示 | `u-text` | `UPText` / `UPTextProps` | 基本完成 | 高（Props） | 文本截断、链接、前后缀图标和样式已有实现。 |
| 128 | 键盘与输入 | `u-textarea` | `UPTextarea` / `UPTextareaProps` | 基本完成 | 高（Props） | 多行输入、字数、清除和受控值已有实现；`selectionStart`/`selectionEnd`/`cursor` 已通过 `TextFieldValue` 生效，`confirmType` 按 multiline 语义映射 IME 动作。降级字段同 `u-input`。 |
| 129 | 表格 | `u-th` | — | 未开始 | 暂无 | 表头单元格待随表格体系实现。 |
| 130 | 基础展示 | `u-title` | `UPTitle` / `UPTitleProps` | 基本完成 | 高（Props） | 标题、装饰线和对齐样式已有实现。 |
| 131 | 原生交互 | `u-toast` | `UPToast` / `UPToastProps` | 基本完成 | 高（Props） | Toast 原生展示和 `UPToastHost` 宿主已有；队列细节待补。 |
| 132 | 原生交互 | `u-toolbar` | `UPToolbar` / `UPToolbarProps` | 基本完成 | 高（Props） | `show`（false 时整体不渲染，复刻外层 `v-if="show"`）、`cancelText`/`confirmText`、`cancelColor`（默认 `#909193`）、`confirmColor`（上游默认空串→标签落到主题 primary）、`title`（非空才渲染，按 `.u-line-1` 加粗 16px 居中并 `flex:1`）、`rightSlot`（true 时右侧让位 `right` 插槽、确认按钮及其点击一并撤下，复刻 `v-if="!rightSlot"`）均已实现；42px 行高、`cancel`/`confirm` 事件与上游一致。有 1 项默认值单测与 3 项真机断言（取消/确认触发与标题、右插槽替换确认、`show=false` 不渲染）。 |
| 133 | 原生交互 | `u-tooltip` | `UPTooltip` / `UPTooltipProps` | 基本完成 | 高（Props） | 气泡改由 Compose 的窗口级 `Popup` 承载，因而不再被触发器裁剪、层级也真正高于页面——这一步同时解掉了此前记为「需窗口级弹层」的四个字段。`getTooltipStyle()` 的几何逐条复刻：`direction` 支持 top/bottom/left/right 四向（上游注释只写两向，实现里是四向）、气泡挤到屏幕边缘时按 `screenGap: 12` 钳在屏内而非居中、三角指示器随之重算位置以继续指向触发器、上下方向按 `translateY(±100%)` + `marginTop: -10px` 让开触发器、左右方向按触发器高度垂直居中；`forcePosition` 按 `{...style, ...forcePosition}` 逐边覆盖；`overlay` 渲染全透明遮罩阻断穿透并点击关闭（`overlay=false` 时完全不生成，触摸照常穿透）；`singleton` 用进程级注册表复刻模块作用域的 `activeSingletonTooltip`，开新气泡即关旧气泡，组件销毁时释放槽位；`showToast` 经新增的 `onToast` 回调回传「复制成功／失败」文案；复制动作按上游占据 `click` 的 0 号槽位、扩展按钮依次后移。首帧未测得尺寸前气泡保持透明（对应上游 `tooltipTop: -10000` 的离屏测量两遍法）。几何有 10 项单测逐条对照，行为有真机断言。 |
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
| 基本完成 | 101 |
| 基础可用 | 0 |
| Props 已建 | 0 |
| 未开始（含辅助模块） | 51 |
| 完整兼容 | 0 |

## 当前已实现组件分批

| 批次 | 组件范围 | 数量 | 当前判断 |
| --- | --- | ---: | --- |
| 基础组件 | button、tag、badge、divider、gap、line、link、text、title、overlay、popup、modal、toast、cell、cell-group、image、avatar、avatar-group、empty、loading-page、loadmore、input、textarea、search、code-input、switch、rate、number-box、checkbox、checkbox-group、radio、radio-group、row、col、grid、grid-item、line-progress、circle-progress | 38 | 基本完成；仍需逐字段视觉回归。 |
| 基础能力 | icon、loading-icon | 2 | 基本完成；自定义图片/字体能力存在平台降级。 |
| Batch 9A 原生交互 | alert、action-sheet、notify、back-top、card、collapse、collapse-item、dropdown、dropdown-item、notice-bar | 10 | 混合；`collapse-item`、`notice-bar`、`dropdown` 系列已到「基本完成」，全局弹层与滚动语义仍需加强。 |
| Batch 9B 导航与更多 | navbar、navbar-mini、status-bar、safe-bottom、tabs、tabs-item、subsection、steps、steps-item、list、list-item、index-list、index-item、index-anchor、scroll-list、popover、tooltip、sticky、swipe-action、swipe-action-item、swiper、swiper-indicator、skeleton、read-more、column-notice、row-notice、count-to、count-down、picker、picker-column、pagination、select | 32 | 混合；`navbar`、`tabs`、`subsection`、`list`、`skeleton`、`read-more`、`count-to`、`count-down`、`pagination` 等已到「基本完成」，`popover`/`tooltip` 已改用窗口级 `Popup`，`sticky` 已实现真实吸顶；`select`/`picker-column` 等仍受内联渲染与滚轮视觉限制。 |
| Batch 10 选择与底部导航 | calendar、datetime-picker、cascader、slider、tabbar、tabbar-item | 6 | 混合；`cascader`、`slider`、`picker` 系列已到「基本完成」，滚轮视觉与窗口级固定仍需加强。 |
| Batch 11 表单校验 | form、form-item | 2 | 基本完成；上游 async-validator 规则、六个 ref 方法与标签/错误布局均有真机断言。 |
| Batch 12 字段补齐 | 跨批次：tabs、pagination、image、cell、modal、navbar、navbar-mini、number-box、overlay、badge、tag、subsection、notice-bar、collapse-item、sticky、action-sheet、slider、list、list-item、count-to、back-top、skeleton、select、read-more、cascader | 25 | 不新增组件，专门消化「声明了但从不读取」的字段。未读字段从 90 一路降到 0；期间发现的组件级缺陷（`u-subsection` 只有一排文字、`u-notice-bar` 从不滚动、`u-collapse-item` 无动画）已一并修复。 |
| Batch 13 空契约收口与新组件 | status-bar、safe-bottom、tabs-item、toolbar、no-network、code、message-input、box、agreement、copy、float-button、number-keyboard、car-keyboard、keyboard | 14 | `status-bar` 补 `update:height` 回调与 `<slot />`、`safe-bottom`/`tabs-item` 与上游空契约逐项对齐，三者由「基础可用」升到「基本完成」，清单再无该档；新增 `u-toolbar`（取消/确认/标题/右插槽/`show` 语义齐备）、`u-no-network`（白底 overlay + 提示 + 重试，连通性提升为 `connected` 参数）、`u-code`（headless 验证码倒计时，`UPCodeController` 提供 `start()`/`reset()`）、`u-message-input`（验证码输入格，三态装饰 + `change`/`finish`）、`up-box`（首页特色三分栏盒子）、`up-agreement`（隐私协议门，`UPAgreementController` 提供 `showModal()`）、`up-copy`（点击复制到剪贴板）、`up-float-button`（悬浮按钮 + 展开菜单）、`u-number-keyboard`（三列数字/身份证键盘）、`u-car-keyboard`（车牌中英键盘）与 `u-keyboard`（键盘弹层容器）。 |

## 下一批推荐顺序

未读字段已归零，下一阶段的瓶颈从「字段是否接上」变成「行为是否对得上」，因此建议按下列顺序推进：

1. **真机执行现有断言**：库内 345 项 androidTest 目前只有编译级证据。先在真机上跑一遍，把编译级证据升级为运行级证据，这比新增组件更能暴露问题。
2. **视觉回归的可用性**：已经解决。参考图一直都在画文本与填色，此前"渲染环境不画文本"的判断是错的（见下文《截图内容核查》）。现有 30 项像素级断言**逐组件覆盖全部 28 张参考图**的关键颜色与几何，另有一项遍历全部参考图做非空校验。下一步可做的是把断言从"颜色在不在、比例对不对"推进到与上游真机截图的像素对照。
3. **原「基础可用」的 3 行已收口（本轮）**：`u-safe-bottom`、`u-status-bar`、`u-tabs-item` 三者上游 props 契约本就近乎为空，此前记为「没有可复刻字段」。本轮把它们逐一对齐到上游全部语义后升到「基本完成」：`u-status-bar` 补 `emits: ['update:height']`（新增 `onUpdateHeight` 回传实测状态栏 inset 或显式高度）与默认 `<slot />`（新增 `content` 插槽）；`u-safe-bottom` 以 `navigationBarsPadding()` 作 `env(safe-area-inset-bottom)` 的语义等价、`customStyle` 逐字段覆盖；`u-tabs-item` 是 `<swiper-item><slot /></swiper-item>` 纯壳、`content` 插槽透传即等价。各补真机断言。至此清单再无「基础可用」行。**此前归纳的三大类「基础设施缺失」全部证伪**：其一，「窗口级弹层」——Compose 自带 `androidx.compose.ui.window.Popup`，`u-tooltip`/`u-popover` 已据此落地；其二，「宿主滚动回传」——吸顶带自己就能读到自身在窗口中的位置，`u-sticky` 已据此实现真实吸顶；其三，「滚轮视觉」——`LazyColumn` + `rememberSnapFlingBehavior` 就是滚轮，`u-picker` 已据此实现吸附选中。三次都是把「还没做」误当成了「做不到」，**判断某件事做不到之前，先去查平台到底提供了什么**。`u-count-down`（缺命令式 ref 方法）、`u-calendar`/`u-select`/`u-tabbar`（缺自身字段）、`u-alert`/`u-collapse`（缺过渡与分隔线）、`u-index-list` 系列（缺手势换算而非滚动基础设施——索引条自己就是滚动容器）、`u-card`（缺分区事件与 hairline 语义）、`u-notify`（缺 ref 调用与图标表）、`u-swiper-indicator`/`u-tabbar-item`（缺过渡与尺寸语义）、`u-tooltip`/`u-popover`（缺窗口级弹层）、`u-sticky`（缺自身位置观测）、`u-picker-column`（缺吸附滚轮）、`u-action-sheet`（缺结构与可达性规则）、`u-back-top`（缺一个 `scrollState` 参数）本轮已补齐并升到「基本完成」——**先把这类「不依赖基础设施」的行挑出来单独收口，是性价比最高的推进方式**。
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

当前状态：库内共 **345 个行为测试**（`ultra-ui/src/androidTest` 的 `@Test` 静态计数；上一次
`connectedDebugAndroidTest` 在 162 项时报告的 `Starting 162 tests` 与静态计数一致）。最近一批为
`u-keyboard` 新增 3 项回归（`UPKeyboardBehaviorTest`），覆盖 number 工具条 + change/confirm/cancel 冒泡、car 模式承载车牌键盘、`show=false` 不渲染。再往前一批为
`u-car-keyboard` 新增 2 项回归（`UPCarKeyboardBehaviorTest`），覆盖中文起始并切英文、退格触发。再往前一批为
`u-number-keyboard` 新增 2 项回归（`UPNumberKeyboardBehaviorTest`），覆盖 number 模式回报数字与退格、card 模式暴露 `X` 键并保持字符串。再往前一批为
`up-float-button` 新增 2 项回归（`UPFloatButtonBehaviorTest`），覆盖普通点击不开列表、菜单开合与 item-click 携带 index。再往前一批为
`up-copy` 新增 2 项回归（`UPCopyBehaviorTest`），覆盖成功回报 notice + success、空内容回报 empty 且不触发 success。再往前一批为
`up-agreement` 新增 2 项回归（`UPAgreementBehaviorTest`），覆盖 `showModal()` 打开且确认关闭并回调、取消经 `onClose` 关闭。再往前一批为
`up-box` 新增 2 项回归（`UPBoxBehaviorTest`），覆盖三分栏 + 默认标题 + 固定高、插槽替换默认内容并触发点击。再往前一批为
`u-message-input` 新增 2 项回归（`UPMessageInputBehaviorTest`），覆盖逐格渲染 + `change`/`finish`、非数字过滤并截到 `maxlength`。再往前一批为
`u-code` 新增 2 项回归（`UPCodeBehaviorTest`），覆盖 `start()` 后逐秒 `change` 至 `endText`、`reset()` 复位并停摆。再往前一批为
`u-no-network` 新增 2 项回归（`UPStructuralComponentBehaviorTest`），覆盖断网时显示提示并回调重试、连通时整体不渲染。再往前一批为
`u-toolbar` 新增 3 项回归（`UPStructuralComponentBehaviorTest`），覆盖取消/确认事件与标题、
`rightSlot` 撤下确认按钮换上右插槽、`show=false` 整体不渲染。再往前一批为
`u-status-bar` 新增 2 项回归（`UPStructuralComponentBehaviorTest`），覆盖 `onUpdateHeight`
回传实测 inset 并渲染 `content` 插槽、显式高度回传该高度。再往前一批为
`u-back-top` 新增 6 项回归（`UPBackTopBehaviorTest`），覆盖固定 40×40 尺寸、`scrollState`
同时驱动显隐与点击后的滚动、不传时时长随回调、离场动画播完才移出树、阈值是严格大于、
`bottom`/`right` 只偏移不撑大。再往前一批为
`u-action-sheet` 新增 7 项回归（`UPActionSheetBehaviorTest`），覆盖 header 关闭按钮走
`cancel`、无标题则整个 header 不存在、分隔线数量随 `description` 变化、`loading` 项显示
spinner 且不可选、`closeOnClickOverlay=false` 时遮罩不关而取消按钮关、自定义内容整体替换
选项列表并按 `closeOnClickAction` 关闭、取消行前的 6dp 间隔。再往前一批为
`u-picker` 的吸附滚轮新增 5 项回归（`UPPickerWheelBehaviorTest`），覆盖选中带恰为一个
`itemHeight` 且居于滚轮正中、首项起始即落在选中带内（证明两端留白生效）、滑动改变选中并
上报 `change`、`disabled` 项不可选、`immediateChange=false` 时滑动静默但确认照样上报。再往前一批为
`u-sticky` 的真实吸顶重写了 3 项回归，覆盖静止时吸顶带只占内容高度（不再把整页推下去）、
滚过阈值后内容被钉住且 `fixed`/`unfixed` 各回传一次 `index`、`disabled` 永不吸顶。再往前一批为
`u-tooltip`/`u-popover` 的窗口级弹层新增 11 项回归，覆盖透明遮罩阻断穿透并点击关闭、
`overlay=false` 时不生成遮罩、气泡挣脱 60dp 容器的裁剪、贴边时仍留在屏内、
`singleton` 两态各自的开启数量、`forcePosition` 覆盖计算位置、复制动作占 0 号槽位、
`showToast=false` 静默、扩展按钮索引后移、popover 没有自己的复制按钮。再往前一批为
`u-tabbar-item` 新增 2 项回归，覆盖 `textMode="active"` 只让未选中项进入 muted 态、
`underline` 指示器按 34rpx 居中而非撑满。再往前一批为
`u-swiper-indicator` 新增 2 项回归，覆盖 line 滑块在 300ms 内被抓到中途位置（而非跳变）、
未知 `indicatorMode` 两种指示器都不渲染且上报诊断。再往前一批为
`u-notify` 新增 3 项回归，覆盖四主题图标表（`primary` 无图标）、`safeAreaInsetTop` 的状态栏
占位落在横幅内部、`UPNotifyController` 驱动 `UPNotifyHost` 并自行关闭。再往前一批为
`u-card` 新增 3 项回归，覆盖三个分区事件各自回传 `index`、`showHead`/`showFoot` 只看开关、
空脚部不占内边距。再往前一批为
`u-index-list` 的索引条交互新增 5 项、移除 1 项过时用例（`UPIndexScrollBehaviorTest` 6→10 项），
覆盖空 `indexList` 生成 A–Z 兜底、拖动索引条依序走过每个字母且去抖后各报一次、
放大气泡在按住时出现并在松手 300ms 后消失、`sticky` 两态各自的锚点标记、
独立使用的锚点默认按吸顶态渲染。再往前一批为
`u-alert` 的过渡与 `u-collapse` 的分隔线新增 3 项回归（并入 `UPInertFieldBehaviorTest`），
覆盖 `transitionMode="slide-up"` 真实上滑、关闭后面板留在树内播完离场动画才移除、
`border` 在展开 + 收起两项时生成四条 hairline 而 `border=false` 时一条都没有。再往前一批为
「声明了但从不读取」的二轮清理新增 18 项回归（`UPInertFieldBehaviorTest`），覆盖
`u-popup` 的 `touchable` 手势条只在底部弹层出现、`duration` 驱动滑入、`safeAreaInsetTop`
让出状态栏，`u-select` 的 `border` 描边与 36dp 下限、`maxHeight` 封顶、`iconSize` 箭头与
选项仍可点，`u-tabbar` 的 `placeholder` 占位等高且 `fixed=false` 时消失，`u-picker` 的
`loading` 盖住整列且期间无选项节点，`u-cell-group` 的 `border` 只画一条 hairline，
`u-icon` 的图片分支（`width`/`height` 生效、留空回落 `size`），`u-number-box` 的
`{ value, name, type }` 事件与 `iconStyle`，`u-cascader` 的 `closeable` 浮层不位移工具条，
以及 `u-calendar` 的 `formatter` 可禁用某天并拦住点击、`u-datetime-picker` 的 `filter`
删项与 `formatter` 改标签、不可调用的 formatter 走诊断。再往前一批为
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

当前状态：101 个已实现组件行中，**101 行证据齐备、0 行标注超出证据**，脚本保持全绿。最后 10 行
（`u-cell`、`u-modal`、`u-navbar`、`u-navbar-mini`、`u-number-box`、`u-radio`、`u-radio-group`、
`u-checkbox-group`、`u-overlay`、`u-badge`、`u-tag`）在本轮补齐了实现或登记为按设计不生效，
其中 `u-navbar`、`u-navbar-mini` 由「基础可用」升级为「基本完成」；再往前一批的 `u-tabs`、
`u-pagination`、`u-image`，以及更早的 `u-picker`、`u-datetime-picker`、`u-form`、`u-form-item`、
`u-swiper` 也都按同样标准移出过清单。

需要强调这个「0」的边界：脚本核验的是**标注是否有证据支撑**（未读字段为 0、且有真机或截图
语料），不是「组件行为与上游完全一致」。原有 3 行「基础可用」已在本轮补齐语义并升到「基本完成」，清单再无该档；
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
当前扫描结果为空，`u-tabbar`、`u-tabbar-item`、`u-swiper`、`u-picker`、`u-datetime-picker`、
`u-tabs`、`u-pagination`、`u-image`、`u-cell`、`u-cell-group`、`u-modal`、`u-navbar`、
`u-navbar-mini`、`u-number-box`、`u-overlay`、`u-badge`、`u-tag`、`u-subsection`、
`u-notice-bar`、`u-collapse-item`、`u-sticky`、`u-action-sheet`、`u-slider`、`u-list`、
`u-list-item`、`u-count-to`、`u-count-down`、`u-back-top`、`u-skeleton`、`u-select`、
`u-read-more`、`u-cascader`、`u-popup`、`u-icon` 与 `u-calendar` 均已消化完毕；但字段被读取
只是最低门槛，读得对不对仍需真机与视觉证据，不能仅以 Props 声明或编译通过替代行为证据。

> **脚本本身曾漏报 48 个字段。** 旧规则是"一旦组件把整个 `props` 转发出去，就退化成全库搜索"，
> 于是 `u-picker` 的 `props.duration` 能替 `u-select` 和 `u-popup` 的同名字段作保，
> `u-cell` 的 `rightIcon` 能替 `u-form-item` 的作保。现规则改为**按类型定界**：只在真正声明了
> `UP<组件>Props` 参数或属性的函数、类、上下文里搜索。转发场景照样成立（`fun foo(props: UPSelectProps)`
> 会被收进范围），但不再从无关组件借证据。收紧后立刻显形 48 个字段，其中 33 个是真缺口，
> 已在本轮全部补齐或按上游事实登记为不生效。**这类"核查工具自己有盲区"的问题，
> 比它要查的缺陷更值得优先修。**

```
python3 tools/find_unread_props.py                # 列出无人读取的字段（有结果时退出码 1）
python3 tools/find_unread_props.py --show-inert    # 同时列出按设计不生效的字段及原因
```

当前状态：101 个 Props 类中有 **0 个字段无人读取**，另有 76 个已记录为按设计不生效
（uni-app / 微信小程序 / nvue 专有开关、DOM 事件语义、内联渲染没有窗口级遮罩可作用的字段，
以及上游自己也从不读取的字段，仅保留接口兼容）。已消化的批次：13 个组件曾声明
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

本轮（脚本定界收紧后的二轮清理）把显形的 33 个真缺口全部接上实现：
`u-select` 的 7 个（`maxHeight` 含 `90vh` 视口换算、`zIndex` 按上游 `+1`、`itemColor`、
`iconColor`、`iconSize`、`border`、`duration`）、`u-calendar` 的 5 个（`formatter` 逐键覆盖回调、
`duration`、`zIndex`、`safeAreaInsetTop`/`safeAreaInsetBottom`）、`u-popup` 的 3 个
（`duration` 驱动 `position()` 计算出的过渡、`zoom` 决定 `fade-zoom`/`fade`、`touchable` 的
底部拖拽条含上游三条关闭阈值）、`u-tabbar` 的 3 个（`zIndex`、`placeholder` 按 `fixed && placeholder`
守卫撑出等高占位、`fixed` 保留占位语义）、`u-picker` 的 3 个（`loading` 整列遮罩、`duration`、`zIndex`）、
`u-icon` 的 3 个（`imgMode`/`width`/`height`，`isImg` 分支改渲 `u-image`）、`u-datetime-picker` 的
2 个（`filter`/`formatter`）、`u-cascader` 的 2 个（`closeable`/`zIndex`）、`u-number-box` 的
2 个（`name` 进入 `{ value, name, type }` 事件、`iconStyle` 作用于两个图标）、`u-cell-group` 的
`border`。另有 15 个按上游事实登记为不生效：内联渲染无全屏遮罩的 `overlay`/`overlayStyle`/
`overlayOpacity`/`closeOnClickOverlay`（`u-select`/`u-calendar`/`u-picker`/`u-datetime-picker`）、
上游自身从不读取的 `u-dropdown-item.closeOnClickOverlay`、`u-form-item.rightIcon`、
`u-datetime-picker.defaultIndex`/`loading`，以及 uni-app 键盘语义的
`u-code-input.adjustPosition`、`u-search.adjustPosition`/`autoBlur`。

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
默认值比对、真机断言与视觉核对；后续工作应转向这三项，以及尚未开始的上游组件。

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
