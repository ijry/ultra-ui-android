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

## 示例导航

示例工程复刻 uview-plus 演示工程的分组体验，并保留已有独立示例：

| 路由标题 | 覆盖组件 |
| --- | --- |
| 基础展示 | 按钮、标签、徽标、分割线、间隔、线条、链接、文本、标题 |
| 弹层与内容 | 遮罩、弹窗、模态框、轻提示、单元格、单元格组、图片、头像、头像组、空状态、加载页、加载更多 |
| 输入与选择 | 输入框、文本域、搜索框、验证码输入、开关、评分、步进器、复选框、复选框组、单选框、单选框组 |
| 布局与进度 | 行布局、列布局、栅格、栅格项、线性进度、环形进度 |
| 图标 | 图标 |
| 加载中图标 | 加载中图标 |

## 38 个组件目录

| uview-plus 标签 | Android Props |
| --- | --- |
| `up-button` | `UPButtonProps` |
| `up-tag` | `UPTagProps` |
| `up-badge` | `UPBadgeProps` |
| `up-divider` | `UPDividerProps` |
| `up-gap` | `UPGapProps` |
| `up-line` | `UPLineProps` |
| `up-link` | `UPLinkProps` |
| `up-text` | `UPTextProps` |
| `up-title` | `UPTitleProps` |
| `up-overlay` | `UPOverlayProps` |
| `up-popup` | `UPPopupProps` |
| `up-modal` | `UPModalProps` |
| `up-toast` | `UPToastProps` |
| `up-cell` | `UPCellProps` |
| `up-cell-group` | `UPCellGroupProps` |
| `up-image` | `UPImageProps` |
| `up-avatar` | `UPAvatarProps` |
| `up-avatar-group` | `UPAvatarGroupProps` |
| `up-empty` | `UPEmptyProps` |
| `up-loading-page` | `UPLoadingPageProps` |
| `up-loadmore` | `UPLoadmoreProps` |
| `up-input` | `UPInputProps` |
| `up-textarea` | `UPTextareaProps` |
| `up-search` | `UPSearchProps` |
| `up-code-input` | `UPCodeInputProps` |
| `up-switch` | `UPSwitchProps` |
| `up-rate` | `UPRateProps` |
| `up-number-box` | `UPNumberBoxProps` |
| `up-checkbox` | `UPCheckboxProps` |
| `up-checkbox-group` | `UPCheckboxGroupProps` |
| `up-radio` | `UPRadioProps` |
| `up-radio-group` | `UPRadioGroupProps` |
| `up-row` | `UPRowProps` |
| `up-col` | `UPColProps` |
| `up-grid` | `UPGridProps` |
| `up-grid-item` | `UPGridItemProps` |
| `up-line-progress` | `UPLineProgressProps` |
| `up-circle-progress` | `UPCircleProgressProps` |

> `UPIconProps` 与 `UPLoadingIconProps` 作为独立基础能力保留在库内和示例页中，不计入本批 38 个生成组件目录。

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
