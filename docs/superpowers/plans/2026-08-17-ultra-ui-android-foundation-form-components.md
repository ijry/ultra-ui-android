# UltraUI Android Foundation and Form Components Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 在 `net.lingyun.ultraui.android` 中实现下一批 38 个 uview-plus 原生 Compose 组件，使后端从同一份 JSON 生成的 Android 源码可以直接使用 camelCase `UP*Props`、uview-plus 字符串枚举、默认值和事件回调。

**Architecture:** 每个组件由公开的 `UP*Props` 数据类、一个 Props 渲染入口和一个简洁的 direct Compose overload 组成。组件只消费已经生成好的 Kotlin 参数，不解析 JSON、不启动 FastView、不依赖 WebView；Android 具备等价能力的按钮、弹窗、输入框、开关和布局直接使用 Compose/Android 原生实现。跨端没有完全等价能力的字段仍保留在 Props 中，并通过 `UPCompatibilityDiagnostics` 发出非致命降级事件。

**Tech Stack:** Kotlin 2.3.21、Android Gradle Plugin 9.2.1、Jetpack Compose BOM 2025.12.01、Compose Foundation/UI、Android Studio 内置 JBR 21（源码 Java/Kotlin target 17）、JUnit4、Compose UI test、Android screenshot test plugin。

## Global Constraints

- Android library namespace 和 Kotlin 根包必须是 `net.lingyun.ultraui.android`；sample 包必须是 `net.lingyun.ultraui.android.sample`。
- 固定上游参考是 `/Users/admin/Documents/Repos/xyito/open/uview-plus` 的 `3.x` 分支、提交 `96f14b06f857aeda011ae5baf9654bb4634dba9a`；实现和测试必须对照对应 `props.js`、默认配置 JS 和 `src/pages` 下的 `.nvue` 演示页。
- 后端负责 JSON 解析和跨端源码生成；本库不引入 JSON 解析器、FastView、`.xyfv`、WebView 或运行时代码生成。
- 不安装独立 JDK 17；Gradle 验证使用 `/Applications/Android Studio.app/Contents/jbr/Contents/Home`，源码 target 保持 Java/Kotlin 17。
- 每个组件必须有 `UP*Props`、Props 渲染入口、direct Compose overload、`customStyle: UPStyleInput`、降级诊断路径、Props 契约单测、Compose 行为测试和确定性截图测试。
- Props 字段使用上游 camelCase 拼写；`type = "primary"`、`mode = "bottom"`、`shape = "circle"` 等字符串值直接可用，未知值回退到安全默认并报告诊断，不得抛异常。
- `modelValue` 和 `value` 双别名同时保留；nullable 的 `modelValue` 优先，`value` 作为兼容默认值，通过 `resolveUPModelValue(modelValue, value)` 统一解析。
- `u-loading-page.iconSize` 固定采用上游 `props.js` 未传值时实际生效的 `19`，并用测试锁定；`u-checkbox.checked` 固定归一为 `false`，修正上游 `checkbox`/`checked` 拼写缺陷但保持 Vue 未选中语义。
- 图片组件不增加 Coil 依赖；通过可注入 `UPImageLoader` 支持测试、本地资源和 Android 原生加载，截图只使用内存或本地资源，不依赖网络。
- 所有异步、动画、定时器和截图输入必须可确定：测试使用固定时钟/短动画或关闭动画，默认图片加载器失败时显示 uview 风格占位/错误图标。
- 每个任务先写一个会因 API 缺失或行为未实现而失败的测试，再写最小实现、运行通过测试、运行该任务的 Compose/screenshot 检查，最后单独提交一个语义明确的 commit。

## Component Contract Matrix

以下字段是每个 `UP*Props` 的公开生成目标；除列出的字段外，每个 Props 末尾都包含 `customStyle: UPStyleInput = emptyMap<String, UPRawValue>()`。默认值来自固定上游配置，表中的特别值不能被 Android Material 默认值替换。

### Batch A: 基础展示与动作

| Component | Props fields | Defaults that must be fixed |
|---|---|---|
| `u-button` | `hairline`, `type`, `size`, `shape`, `plain`, `disabled`, `loading`, `loadingText`, `loadingMode`, `loadingSize`, `openType`, `formType`, `appParameter`, `hoverStopPropagation`, `lang`, `sessionFrom`, `sendMessageTitle`, `sendMessagePath`, `sendMessageImg`, `showMessageCard`, `dataName`, `throttleTime`, `hoverStartTime`, `hoverStayTime`, `text`, `icon`, `iconColor`, `color`, `stop` | `type="info"`, `size=""`, `shape="square"`, `loadingMode="circle"`, `loadingSize=15`, `stop=true`, `throttleTime=0` |
| `u-tag` | `type`, `disabled`, `size`, `shape`, `text`, `bgColor`, `color`, `borderColor`, `closeColor`, `name`, `plainFill`, `plain`, `closable`, `show`, `icon`, `iconColor`, `textSize`, `height`, `padding`, `borderRadius`, `autoBgColor` | `type="primary"`, `size="medium"`, `shape="square"`, `closeColor="#C6C7CB"`, `show=true` |
| `u-badge` | `isDot`, `value`, `modelValue`, `show`, `max`, `type`, `showZero`, `bgColor`, `color`, `shape`, `numberType`, `offset`, `inverted`, `absolute` | `max=999`, `type="error"`, `shape="circle"`, `numberType="overflow"`, `show=true` |
| `u-divider` | `dashed`, `hairline`, `dot`, `textPosition`, `text`, `textSize`, `textColor`, `lineColor` | `hairline=true`, `textPosition="center"`, `textSize=14`, `textColor="#909399"`, `lineColor="#dcdfe6"` |
| `u-gap` | `bgColor`, `height`, `marginTop`, `marginBottom` | `bgColor="transparent"`, `height=20`, margins `0` |
| `u-line` | `color`, `length`, `direction`, `hairline`, `margin`, `dashed` | `color="#d6d7d9"`, `length="100%"`, `direction="row"`, `hairline=true`, `margin=0` |
| `u-link` | `color`, `fontSize`, `underLine`, `href`, `mpTips`, `lineColor`, `text` | primary color, `fontSize=15`, `underLine=false`, `href=""`, `lineColor=""` |
| `u-text` | `type`, `show`, `text`, `prefixIcon`, `suffixIcon`, `mode`, `href`, `format`, `call`, `openType`, `bold`, `block`, `lines`, `color`, `size`, `iconStyle`, `decoration`, `margin`, `lineHeight`, `align`, `wordWrap`, `flex1` | `show=true`, `size=15`, `decoration="none"`, `margin=0`, `align="left"`, `wordWrap="normal"`, `flex1=false` |
| `u-title` | no upstream props; `customStyle` only | prefix bar `4dp x 18dp`, primary color, content slot preserved |

### Batch B: 层级、内容与状态

| Component | Props fields | Defaults that must be fixed |
|---|---|---|
| `u-overlay` | `show`, `zIndex`, `duration`, `opacity` | `show=false`, `zIndex=10070`, `duration=300`, `opacity=0.5` |
| `u-popup` | `show`, `overlay`, `mode`, `duration`, `closeable`, `overlayStyle`, `closeOnClickOverlay`, `zIndex`, `safeAreaInsetBottom`, `safeAreaInsetTop`, `closeIconPos`, `round`, `zoom`, `bgColor`, `overlayOpacity`, `pageInline`, `touchable`, `minHeight`, `maxHeight` | `mode="bottom"`, `zIndex=10075`, `round="20px"`, `minHeight="200px"`, `maxHeight="600px"`, `overlay=true`, `closeOnClickOverlay=true` |
| `u-modal` | `show`, `title`, `content`, `confirmText`, `cancelText`, `showConfirmButton`, `showCancelButton`, `confirmColor`, `cancelColor`, `buttonReverse`, `zoom`, `asyncClose`, `closeOnClickOverlay`, `negativeTop`, `width`, `confirmButtonShape`, `duration`, `contentTextAlign`, `asyncCloseTip`, `asyncCancelClose`, `contentStyle` | `confirmText="确认"`, `cancelText="取消"`, `width="650rpx"`, `confirmColor="#2979ff"`, `cancelColor="#606266"`, `showConfirmButton=true`, `showCancelButton=false` |
| `u-toast` | `zIndex`, `loading`, `message`, `icon`, `type`, `loadingMode`, `show`, `overlay`, `position`, `params`, `duration`, `isTab`, `url`, `callback`, `back` | `zIndex=10090`, `position="center"`, `duration=2000`, `show` empty raw value, `overlay=false` |
| `u-cell` | `title`, `label`, `value`, `icon`, `disabled`, `border`, `center`, `url`, `linkType`, `clickable`, `isLink`, `required`, `rightIcon`, `arrowDirection`, `iconStyle`, `rightIconStyle`, `titleStyle`, `size`, `stop`, `name` | preserve upstream empty strings, `border=true`, `clickable=false`, `isLink=false`, `required=false`, `rightIcon="arrow-right"`, `size=""` |
| `u-cell-group` | `title`, `border` | `title=""`, `border=true` |
| `u-image` | `src`, `mode`, `width`, `height`, `shape`, `radius`, `lazyLoad`, `showMenuByLongpress`, `loadingIcon`, `errorIcon`, `showLoading`, `showError`, `fade`, `webp`, `duration`, `bgColor` | `mode="aspectFill"`, `width=300`, `height=225`, `loadingIcon="photo"`, `errorIcon="error-circle"`, `bgColor="#f3f4f6"`, `lazyLoad=true` |
| `u-avatar` | `src`, `shape`, `size`, `mode`, `text`, `bgColor`, `color`, `fontSize`, `icon`, `mpAvatar`, `randomBgColor`, `defaultUrl`, `colorIndex`, `name` | `shape="circle"`, `size=40`, `mode="scaleToFill"`, `bgColor="#c0c4cc"`, `color="#ffffff"`, `fontSize=18` |
| `u-avatar-group` | `urls`, `maxCount`, `shape`, `mode`, `showMore`, `size`, `keyName`, `gap`, `extraValue` | `maxCount=5`, `shape="circle"`, `mode="scaleToFill"`, `showMore=true`, `size=40`, `keyName=""`, `gap=0.5`, `extraValue=0` |
| `u-empty` | `icon`, `text`, `textColor`, `textSize`, `iconColor`, `iconSize`, `mode`, `width`, `height`, `show`, `marginTop` | `icon=""`, `text=""`, `textColor="#c0c4cc"`, `textSize=14`, `iconColor="#c0c4cc"`, `iconSize=90`, `mode="data"`, `width=160`, `height=160`, `show=true`, `marginTop=0` |
| `u-loading-page` | `loadingText`, `image`, `loadingMode`, `loading`, `bgColor`, `color`, `fontSize`, `iconSize`, `loadingColor`, `zIndex` | `loadingText="本地 i18n 文案"`, `loadingMode="circle"`, `loading=false`, `bgColor=""`, `fontSize=19`, effective `iconSize=19`, `zIndex=10` |
| `u-loadmore` | `status`, `bgColor`, `icon`, `fontSize`, `iconSize`, `color`, `loadingIcon`, `loadmoreText`, `loadingText`, `nomoreText`, `isDot`, `iconColor`, `marginTop`, `marginBottom`, `height`, `line`, `lineColor`, `dashed` | `status="loadmore"`, `fontSize=14`, `iconSize=17`, `color="#606266"`, `loadingIcon="spinner"`, `loadmoreText="本地 i18n 文案"`, `loadingText="本地 i18n 文案 + ..."`, `nomoreText="本地 i18n 文案"`, `isDot=false`, `line=false` |

### Batch C: 输入与选择

| Component | Props fields | Defaults that must be fixed |
|---|---|---|
| `u-input` | `modelValue`, `value`, `type`, `fixed`, `disabled`, `disabledColor`, `clearable`, `onlyClearableOnFocused`, `password`, `maxlength`, `placeholder`, `placeholderClass`, `placeholderStyle`, `showWordLimit`, `confirmType`, `confirmHold`, `holdKeyboard`, `focus`, `autoBlur`, `disableDefaultPadding`, `cursor`, `cursorSpacing`, `selectionStart`, `selectionEnd`, `adjustPosition`, `inputAlign`, `fontSize`, `color`, `prefixIcon`, `prefixIconStyle`, `suffixIcon`, `suffixIconStyle`, `border`, `readonly`, `shape`, `formatter`, `ignoreCompositionEvent`, `cursorColor`, `passwordVisibilityToggle` | `type="text"`, `maxlength=140`, `border="surround"`, `shape="square"`, `cursorColor="#53c21d"`, empty model value, `disabled=false`, `readonly=false` |
| `u-textarea` | `value`, `modelValue`, `placeholder`, `placeholderClass`, `placeholderStyle`, `height`, `confirmType`, `disabled`, `count`, `focus`, `autoHeight`, `fixed`, `cursorSpacing`, `cursor`, `showConfirmBar`, `selectionStart`, `selectionEnd`, `adjustPosition`, `disableDefaultPadding`, `holdKeyboard`, `maxlength`, `border`, `formatter`, `ignoreCompositionEvent` | `height=70`, `border="surround"`, `maxlength=140`, `count=false`, `autoHeight=false`, `showConfirmBar=true` |
| `u-search` | `modelValue`, `value`, `shape`, `bgColor`, `placeholder`, `clearabled`, `onlyClearableOnFocused`, `focus`, `showAction`, `actionText`, `label`, `inputAlign`, `disabled`, `animation`, `borderColor`, `searchIconColor`, `searchIconSize`, `color`, `placeholderColor`, `searchIcon`, `margin`, `iconPosition`, `maxlength`, `height`, `adjustPosition`, `autoBlur`, `inputStyle`, `actionStyle` | `shape="round"`, `clearabled=true`, `showAction=true`, `searchIcon="search"`, `height=32`, `actionText="搜索"`, `animation=false`, `iconPosition="left"` |
| `u-code-input` | `adjustPosition`, `maxlength`, `dot`, `mode`, `hairline`, `space`, `modelValue`, `value`, `focus`, `bold`, `color`, `fontSize`, `size`, `disabledKeyboard`, `borderColor`, `disabledDot` | `maxlength=6`, `mode="box"`, `space=10`, `size=35`, `borderColor="#c9cacc"`, `disabledDot=true` |
| `u-switch` | `loading`, `disabled`, `size`, `activeColor`, `inactiveColor`, `dotActiveColor`, `dotInactiveColor`, `modelValue`, `value`, `activeValue`, `inactiveValue`, `asyncChange`, `space` | `size=25`, `activeColor="#2979ff"`, `inactiveColor="#ffffff"`, `activeValue=true`, `inactiveValue=false`, `space=0` |
| `u-rate` | `modelValue`, `value`, `count`, `disabled`, `readonly`, `size`, `inactiveColor`, `activeColor`, `gutter`, `minCount`, `allowHalf`, `activeIcon`, `inactiveIcon`, `touchable` | `value=1`, `count=5`, `gutter=4`, `minCount=1`, `activeIcon="star-fill"`, `inactiveIcon="star"`, `allowHalf=false` |
| `u-number-box` | `name`, `value`, `modelValue`, `min`, `max`, `step`, `integer`, `disabled`, `disabledInput`, `asyncChange`, `inputWidth`, `showMinus`, `showPlus`, `decimalLength`, `longPress`, `color`, `buttonWidth`, `buttonSize`, `buttonRadius`, `bgColor`, `disabledBgColor`, `inputBgColor`, `cursorSpacing`, `disablePlus`, `disableMinus`, `iconStyle`, `miniMode` | `value=0`, `min=1`, `max=Number.MAX_SAFE_INTEGER`, `step=1`, `integer=false`, `showMinus=true`, `showPlus=true`, `longPress=true` |
| `u-checkbox` | `name`, `shape`, `size`, `checked`, `disabled`, `activeColor`, `inactiveColor`, `iconSize`, `iconColor`, `label`, `labelSize`, `labelColor`, `labelDisabled`, `usedAlone` | `checked=false`, `disabled=false`, `activeColor="#2979ff"`, `inactiveColor="#c8c9cc"`, `size=18`, `usedAlone=false` |
| `u-checkbox-group` | `name`, `modelValue`, `value`, `shape`, `disabled`, `activeColor`, `inactiveColor`, `size`, `placement`, `labelSize`, `labelColor`, `labelDisabled`, `iconColor`, `iconSize`, `iconPlacement`, `borderBottom` | `shape="square"`, `activeColor="#2979ff"`, `inactiveColor="#c8c9cc"`, `size=18`, `placement="row"`, `value=[]` |
| `u-radio` | `name`, `shape`, `disabled`, `labelDisabled`, `activeColor`, `inactiveColor`, `iconSize`, `labelSize`, `label`, `size`, `color`, `labelColor`, `iconColor` | preserve group-derived empty values; standalone safe defaults `shape="circle"`, `size=18`, `activeColor="#2979ff"`, `inactiveColor="#c8c9cc"` |
| `u-radio-group` | `modelValue`, `value`, `disabled`, `shape`, `activeColor`, `inactiveColor`, `name`, `size`, `placement`, `label`, `labelColor`, `labelSize`, `labelDisabled`, `iconColor`, `iconSize`, `borderBottom`, `iconPlacement`, `gap` | `shape="circle"`, `activeColor="#2979ff"`, `inactiveColor="#c8c9cc"`, `size=18`, `placement="row"`, `gap="10px"`, `value=""` |

### Batch D: 布局与进度

| Component | Props fields | Defaults that must be fixed |
|---|---|---|
| `u-row` | `gutter`, `justify`, `align` | `gutter=0`, `justify="start"`, `align="center"` |
| `u-col` | `span`, `offset`, `justify`, `align`, `textAlign` | `span=12`, `offset=0`, `justify="start"`, `align="stretch"`, `textAlign="left"` |
| `u-grid` | `col`, `border`, `align`, `gap` | `col=3`, `border=false`, `align="left"`, `gap="0px"` |
| `u-grid-item` | `name`, `bgColor` | `name=null`, `bgColor="transparent"` |
| `u-line-progress` | `activeColor`, `inactiveColor`, `percentage`, `showText`, `height`, `fromRight` | `activeColor="#19be6b"`, `inactiveColor="#ececec"`, `percentage=0`, `showText=true`, `height=12`, `fromRight=false` |
| `u-circle-progress` | `percentage` | `percentage=30` |

## Planned File Structure

### Core support

- Modify `ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/core/UPConfig.kt` to add immutable defaults for all 38 components without removing existing icon/loading-icon/button/overlay/popup/toast/tag/modal/cell defaults.
- Modify `ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/core/UPValue.kt` to add safe numeric/string/list conversion and the nullable model alias resolver:

```kotlin
internal fun resolveUPModelValue(modelValue: UPRawValue?, value: UPRawValue): UPRawValue =
    modelValue ?: value
```

- Create `ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/core/UPComponentSupport.kt` for safe enum fallback, finite percentage clamping, event diagnostics, semantic test tags, and `Modifier` click/throttle helpers.
- Create `ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/core/UPImageLoader.kt` for the injectable `UPImageLoader` contract and local Android fallback; no network is required for the public component API.
- Modify `ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/components/UPComponentStyle.kt` only where shared style application needs a deterministic border/shape or safe-area modifier; preserve the existing supported `customStyle` subset.
- Create `ultra-ui/src/test/kotlin/net/lingyun/ultraui/android/core/UPComponentSupportTest.kt` and extend `UPConfigContractTest.kt` and `UPValueTest.kt` with defaults, alias, percentage, and invalid-value cases.

### Public component files

For every pair below, the Props file contains the public data class and `effectiveValue` helpers; the component file contains the Props entry point and the direct overload. All public APIs remain under `net.lingyun.ultraui.android.components`.

- Create `UPButton.kt`, `UPButtonProps.kt`, `UPTag.kt`, `UPTagProps.kt`, `UPBadge.kt`, `UPBadgeProps.kt`, `UPDivider.kt`, `UPDividerProps.kt`, `UPGap.kt`, `UPGapProps.kt`, `UPLine.kt`, `UPLineProps.kt`, `UPLink.kt`, `UPLinkProps.kt`, `UPText.kt`, `UPTextProps.kt`, `UPTitle.kt`, `UPTitleProps.kt`.
- Create `UPOverlay.kt`, `UPOverlayProps.kt`, `UPPopup.kt`, `UPPopupProps.kt`, `UPModal.kt`, `UPModalProps.kt`, `UPToast.kt`, `UPToastProps.kt`, `UPToastController.kt`, `UPCell.kt`, `UPCellProps.kt`, `UPCellGroup.kt`, `UPCellGroupProps.kt`.
- Create `UPImage.kt`, `UPImageProps.kt`, `UPAvatar.kt`, `UPAvatarProps.kt`, `UPAvatarGroup.kt`, `UPAvatarGroupProps.kt`, `UPEmpty.kt`, `UPEmptyProps.kt`, `UPLoadingPage.kt`, `UPLoadingPageProps.kt`, `UPLoadmore.kt`, `UPLoadmoreProps.kt`.
- Create `UPInput.kt`, `UPInputProps.kt`, `UPTextarea.kt`, `UPTextareaProps.kt`, `UPSearch.kt`, `UPSearchProps.kt`, `UPCodeInput.kt`, `UPCodeInputProps.kt`.
- Create `UPSwitch.kt`, `UPSwitchProps.kt`, `UPRate.kt`, `UPRateProps.kt`, `UPNumberBox.kt`, `UPNumberBoxProps.kt`, `UPCheckbox.kt`, `UPCheckboxProps.kt`, `UPCheckboxGroup.kt`, `UPCheckboxGroupProps.kt`, `UPRadio.kt`, `UPRadioProps.kt`, `UPRadioGroup.kt`, `UPRadioGroupProps.kt`.
- Create `UPRow.kt`, `UPRowProps.kt`, `UPCol.kt`, `UPColProps.kt`, `UPGrid.kt`, `UPGridProps.kt`, `UPGridItem.kt`, `UPGridItemProps.kt`, `UPLineProgress.kt`, `UPLineProgressProps.kt`, `UPCircleProgress.kt`, `UPCircleProgressProps.kt`.

### Tests and sample

- Create unit contract suites: `UPFoundationPropsTest.kt`, `UPLayerPropsTest.kt`, `UPContentPropsTest.kt`, `UPInputPropsTest.kt`, `UPSelectionPropsTest.kt`, `UPLayoutProgressPropsTest.kt`.
- Create Android Compose behavior suites: `UPFoundationBehaviorTest.kt`, `UPLayerBehaviorTest.kt`, `UPContentBehaviorTest.kt`, `UPInputBehaviorTest.kt`, `UPSelectionBehaviorTest.kt`, `UPLayoutProgressBehaviorTest.kt`.
- Create screenshot suites: `FoundationScreenshots.kt`, `LayerScreenshots.kt`, `ContentScreenshots.kt`, `InputScreenshots.kt`, `SelectionScreenshots.kt`, `LayoutProgressScreenshots.kt`; create debug reference images with `updateDebugScreenshotTest` only after implementation and review the rendered PNGs.
- Create `sample/src/main/kotlin/net/lingyun/ultraui/android/sample/pages/FoundationDemoPage.kt`, `LayerContentDemoPage.kt`, `InputSelectionDemoPage.kt`, and `LayoutProgressDemoPage.kt`; modify `SampleCatalog.kt`, `SampleApp.kt`, `SampleCatalogTest.kt`, and `README.md` with Chinese navigation entries and public-API-only examples.

---

### Task 1: Extend shared defaults, aliases, diagnostics, and image loading

**Files:**
- Modify: `ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/core/UPConfig.kt`
- Modify: `ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/core/UPValue.kt`
- Create: `ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/core/UPComponentSupport.kt`
- Create: `ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/core/UPImageLoader.kt`
- Modify: `ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/components/UPComponentStyle.kt`
- Test: `ultra-ui/src/test/kotlin/net/lingyun/ultraui/android/core/UPComponentSupportTest.kt`
- Test: `ultra-ui/src/test/kotlin/net/lingyun/ultraui/android/core/UPConfigContractTest.kt`
- Test: `ultra-ui/src/test/kotlin/net/lingyun/ultraui/android/core/UPValueTest.kt`

**Interfaces:**
- Consumes: existing `UPRawValue`, `UPStyleInput`, `UPColor`, `UPUnit`, `UPCompatibilityDiagnostics`.
- Produces:

```kotlin
internal fun resolveUPModelValue(modelValue: UPRawValue?, value: UPRawValue): UPRawValue
internal fun UPRawValue.upIntOrDefault(default: Int): Int
internal fun UPRawValue.upFloatOrDefault(default: Float): Float
internal fun UPRawValue.upBooleanOrDefault(default: Boolean): Boolean
internal fun upClampPercentage(value: UPRawValue, diagnostics: UPCompatibilityDiagnostics, component: String): Float
internal fun Modifier.upTestTag(component: String): Modifier
public fun interface UPImageLoader {
    public suspend fun load(source: String): androidx.compose.ui.graphics.ImageBitmap?
}
public object UPImageLoaders {
    public val Empty: UPImageLoader
    public val Android: UPImageLoader
}
```

- [ ] **Step 1: Write failing shared-contract tests.** Assert that nullable `modelValue` wins over `value`, null model falls back to value, malformed numbers use the supplied fallback, percentages clamp to `0f..100f` and report one diagnostic, unknown enum reporting retains component/property/value, and `UPConfig` exposes all 38 default groups.
- [ ] **Step 2: Run the focused tests and verify they fail for missing APIs.**

Run:

```bash
export ANDROID_HOME="$HOME/Library/Android/sdk"
export ANDROID_SDK_ROOT="$ANDROID_HOME"
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
./gradlew :ultra-ui:testDebugUnitTest --tests 'net.lingyun.ultraui.android.core.UPComponentSupportTest' --tests 'net.lingyun.ultraui.android.core.UPConfigContractTest' --tests 'net.lingyun.ultraui.android.core.UPValueTest' --console=plain
```

Expected: compilation failure naming the new helpers/default groups, not an unrelated Gradle or JDK failure.
- [ ] **Step 3: Implement the minimum shared support.** Add every default data class named by the Props matrix, make malformed raw values non-throwing, keep diagnostics silent by default, and implement `UPImageLoaders.Empty` without network or external image dependencies. `UPImageLoaders.Android` must return null for empty/unsupported sources and decode only Android-resolvable local sources.
- [ ] **Step 4: Re-run focused tests and compile the library.**

Run:

```bash
./gradlew :ultra-ui:testDebugUnitTest --tests 'net.lingyun.ultraui.android.core.UPComponentSupportTest' --tests 'net.lingyun.ultraui.android.core.UPConfigContractTest' --tests 'net.lingyun.ultraui.android.core.UPValueTest' :ultra-ui:compileDebugKotlin --console=plain
```

Expected: PASS and `:ultra-ui:compileDebugKotlin` successful.
- [ ] **Step 5: Commit the shared contract.**

```bash
git add ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/core ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/components/UPComponentStyle.kt ultra-ui/src/test/kotlin/net/lingyun/ultraui/android/core
git commit -m "feat: extend uview compatibility primitives"
```

---

### Task 2: Implement Batch A foundation, display, and action components

**Components:** `u-button`, `u-tag`, `u-badge`, `u-divider`, `u-gap`, `u-line`, `u-link`, `u-text`, `u-title`.

**Files:**
- Create the 18 public files listed under Batch A in the planned file structure.
- Test: `ultra-ui/src/test/kotlin/net/lingyun/ultraui/android/components/UPFoundationPropsTest.kt`
- Test: `ultra-ui/src/androidTest/kotlin/net/lingyun/ultraui/android/components/UPFoundationBehaviorTest.kt`
- Test: `ultra-ui/src/screenshotTest/kotlin/net/lingyun/ultraui/android/components/FoundationScreenshots.kt`

**Interfaces:**
- Consumes: Task 1 defaults, style resolver, diagnostics, `UPIcon`, `UPLoadingIcon`, and `UPRawValue` converters.
- Produces these public entry points:

```kotlin
@Composable fun UPButton(props: UPButtonProps = UPButtonProps(), onClick: (() -> Unit)? = null, content: (@Composable RowScope.() -> Unit)? = null, diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None)
@Composable fun UPButton(text: String = "", type: String = "info", size: String = "normal", onClick: (() -> Unit)? = null)
@Composable fun UPTag(props: UPTagProps = UPTagProps(), onClick: ((UPRawValue) -> Unit)? = null, onClose: ((UPRawValue) -> Unit)? = null, diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None)
@Composable fun UPBadge(props: UPBadgeProps = UPBadgeProps(), content: @Composable () -> Unit = {}, diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None)
@Composable fun UPDivider(props: UPDividerProps = UPDividerProps(), diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None)
@Composable fun UPGap(props: UPGapProps = UPGapProps(), diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None)
@Composable fun UPLine(props: UPLineProps = UPLineProps(), diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None)
@Composable fun UPLink(props: UPLinkProps = UPLinkProps(), onClick: (() -> Unit)? = null, diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None)
@Composable fun UPText(props: UPTextProps = UPTextProps(), onClick: (() -> Unit)? = null, diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None)
@Composable fun UPTitle(props: UPTitleProps = UPTitleProps(), prefix: (@Composable () -> Unit)? = null, content: @Composable () -> Unit, diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None)
```

- [ ] **Step 1: Write failing Props contract tests for all nine components.** Construct each Props with defaults and non-default values, assert every field remains unchanged, assert `customStyle` accepts both a map and a CSS-like string, assert `UPButtonProps(type="primary")`, `UPTagProps(textSize=12)`, and `UPTextProps(flex1=true)` compile and preserve values, and assert invalid button/tag/badge enums report a downgrade only when rendered.
- [ ] **Step 2: Run the unit tests before creating implementations.**

Run:

```bash
./gradlew :ultra-ui:testDebugUnitTest --tests 'net.lingyun.ultraui.android.components.UPFoundationPropsTest' --console=plain
```

Expected: compilation failure because the nine Props classes do not exist.
- [ ] **Step 3: Implement the nine Props data classes and renderers.**
  - Button maps `info|primary|error|warning|success`, `large|normal|small|mini`, and `circle|square`; disabled/loading state blocks clicks; `throttleTime` uses monotonic time; unsupported mini-program-only fields remain in Props and report once; `loadingMode` delegates to `UPLoadingIcon`.
  - Tag supports plain/filled colors, `show`, icon, close affordance, `click(name)` and `close(name)` payloads, with `textSize` and `autoBgColor` applied deterministically.
  - Badge resolves `modelValue` before `value`, supports dot/overflow/ellipsis/number display, `showZero`, offset, inverted and absolute placement.
  - Divider renders left/center/right text, dot, dashed and hairline rules; Gap and Line resolve numeric/rpx dimensions and safe margins; Link and Text preserve text, color, decoration, line limits, prefix/suffix icon and click behavior; Title preserves prefix/content slots.
  - Every root uses the `up-*` test tag and `rememberUPResolvedStyle`, and every unsupported platform field calls diagnostics with the exact property name.
- [ ] **Step 4: Run unit tests, compile, and run Compose behavior tests.**

```bash
./gradlew :ultra-ui:testDebugUnitTest :ultra-ui:compileDebugKotlin :ultra-ui:connectedDebugAndroidTest --console=plain
```

Expected behavior assertions include: button click is suppressed while loading/disabled and emitted once after throttle; tag emits its `name`; badge hides zero by default; divider text position changes bounds; link/text click semantics exist; line direction changes measured orientation; title prefix is visible.
- [ ] **Step 5: Add and render deterministic screenshots.** Use fixed 320dp/360dp surfaces and no animation. Include one named preview for each of `button`, `tag`, `badge`, `divider`, `gap`, `line`, `link`, `text`, and `title`, with Chinese labels matching the upstream demo intent. Run:

```bash
./gradlew :ultra-ui:updateDebugScreenshotTest :ultra-ui:validateDebugScreenshotTest --console=plain
```

Review every generated PNG under `ultra-ui/src/screenshotTestDebug/reference` and retain only deterministic references.
- [ ] **Step 6: Commit Batch A.**

```bash
git add ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/components/UP*.kt ultra-ui/src/test/kotlin/net/lingyun/ultraui/android/components/UPFoundationPropsTest.kt ultra-ui/src/androidTest/kotlin/net/lingyun/ultraui/android/components/UPFoundationBehaviorTest.kt ultra-ui/src/screenshotTest ultra-ui/src/screenshotTestDebug/reference
git commit -m "feat: add uview foundation components"
```

---

### Task 3: Implement native overlay, popup, modal, toast, and cell components

**Components:** `u-overlay`, `u-popup`, `u-modal`, `u-toast`, `u-cell`, `u-cell-group`.

**Files:**
- Create/modify the 12 public files listed for these components, including `UPToastProps.kt` and `UPToastController.kt`.
- Test: `ultra-ui/src/test/kotlin/net/lingyun/ultraui/android/components/UPLayerPropsTest.kt`
- Test: `ultra-ui/src/androidTest/kotlin/net/lingyun/ultraui/android/components/UPLayerBehaviorTest.kt`
- Test: `ultra-ui/src/screenshotTest/kotlin/net/lingyun/ultraui/android/components/LayerScreenshots.kt`

**Interfaces:**
- Consumes: Task 1 style/diagnostics and Task 2 button/icon/text primitives.
- Produces:

```kotlin
@Composable fun UPOverlay(props: UPOverlayProps = UPOverlayProps(), onClick: (() -> Unit)? = null, content: @Composable BoxScope.() -> Unit = {}, diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None)
@Composable fun UPPopup(props: UPPopupProps = UPPopupProps(), onUpdateShow: ((Boolean) -> Unit)? = null, onOpen: (() -> Unit)? = null, onClose: (() -> Unit)? = null, onClick: (() -> Unit)? = null, content: @Composable ColumnScope.() -> Unit, diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None)
@Composable fun UPModal(props: UPModalProps = UPModalProps(), onUpdateShow: ((Boolean) -> Unit)? = null, onConfirm: (() -> Unit)? = null, onCancel: (() -> Unit)? = null, onClose: (() -> Unit)? = null, onCancelOnAsync: (() -> Unit)? = null, content: (@Composable ColumnScope.() -> Unit)? = null, diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None)
data class UPToastProps(...)
typealias UPToastOptions = UPToastProps
@Composable fun UPToastHost(controller: UPToastController, modifier: Modifier = Modifier, diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None)
@Composable fun UPToast(props: UPToastProps = UPToastProps(), onComplete: (() -> Unit)? = null, diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None)
@Composable fun UPCell(props: UPCellProps = UPCellProps(), onClick: ((UPRawValue) -> Unit)? = null, left: (@Composable RowScope.() -> Unit)? = null, right: (@Composable RowScope.() -> Unit)? = null, diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None)
@Composable fun UPCellGroup(props: UPCellGroupProps = UPCellGroupProps(), content: @Composable ColumnScope.() -> Unit, diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None)
```

- [ ] **Step 1: Write failing tests for controlled visibility and event contracts.** Assert overlay default hidden/z-index/opacity, popup mode and min/max dimensions, modal text/button defaults and alias fields, `UPToastProps` existence with `UPToastOptions` alias, cell name payload and group border/title defaults.
- [ ] **Step 2: Run focused tests and verify the expected missing-API failure.**

```bash
./gradlew :ultra-ui:testDebugUnitTest --tests 'net.lingyun.ultraui.android.components.UPLayerPropsTest' --console=plain
```

Expected: compilation failure at the new Props names.
- [ ] **Step 3: Implement native Compose layers.** Use `Dialog`/`Popup`/`AnimatedVisibility` with Compose z-order and safe drawing insets; do not create a WebView or JSON host. `show=false` emits no layer. Overlay click closes only when configured. Popup supports `bottom|top|left|right|center`, rounded corners, close icon positions and content slots. Modal supports confirm/cancel buttons, `asyncClose`, `asyncCloseTip`, `asyncCancelClose`, reverse order, width, and `contentStyle`. Toast uses a controller/host with a monotonic-duration state machine, `duration=-1` persistence, callback, and `UPToastProps` as the canonical generated contract. Cell/cell-group preserve title/label/value/icon/right icon/required/arrow/link semantics and emit `click({ name })` through a raw-value payload.
- [ ] **Step 4: Run unit, compile, and connected Compose behavior tests.**

```bash
./gradlew :ultra-ui:testDebugUnitTest :ultra-ui:compileDebugKotlin :ultra-ui:connectedDebugAndroidTest --console=plain
```

The behavior suite must verify overlay dismissal, popup `update:show`/open/close order, modal confirm/cancel and async cancellation, toast show/hide, cell click payload, and that hidden layers have no semantics node.
- [ ] **Step 5: Add screenshots and validate.** Render a centered overlay, each popup mode in separate previews, modal with both buttons, toast positions/types, and a cell group. Disable transition duration in screenshot Props while retaining runtime defaults in contract tests.

```bash
./gradlew :ultra-ui:updateDebugScreenshotTest :ultra-ui:validateDebugScreenshotTest --console=plain
```

- [ ] **Step 6: Commit the layer batch.**

```bash
git add ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/components/UPOverlay* ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/components/UPPopup* ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/components/UPModal* ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/components/UPToast* ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/components/UPCell* ultra-ui/src/test ultra-ui/src/androidTest ultra-ui/src/screenshotTest ultra-ui/src/screenshotTestDebug/reference
git commit -m "feat: add native uview layers and cells"
```

---

### Task 4: Implement image, avatar, empty, loading-page, and loadmore content/status components

**Components:** `u-image`, `u-avatar`, `u-avatar-group`, `u-empty`, `u-loading-page`, `u-loadmore`.

**Files:**
- Create the 12 public files listed for these components.
- Test: `ultra-ui/src/test/kotlin/net/lingyun/ultraui/android/components/UPContentPropsTest.kt`
- Test: `ultra-ui/src/androidTest/kotlin/net/lingyun/ultraui/android/components/UPContentBehaviorTest.kt`
- Test: `ultra-ui/src/screenshotTest/kotlin/net/lingyun/ultraui/android/components/ContentScreenshots.kt`

**Interfaces:**
- Consumes: `UPImageLoader`, `UPIcon`, `UPLoadingIcon`, `UPText`, style and diagnostics support.
- Produces:

```kotlin
@Composable fun UPImage(props: UPImageProps = UPImageProps(), loader: UPImageLoader = UPImageLoaders.Android, onClick: (() -> Unit)? = null, onLoad: (() -> Unit)? = null, onError: ((Throwable?) -> Unit)? = null, diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None)
@Composable fun UPAvatar(props: UPAvatarProps = UPAvatarProps(), onClick: ((UPRawValue) -> Unit)? = null, diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None)
@Composable fun UPAvatarGroup(props: UPAvatarGroupProps = UPAvatarGroupProps(), onShowMore: (() -> Unit)? = null, diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None)
@Composable fun UPEmpty(props: UPEmptyProps = UPEmptyProps(), content: (@Composable ColumnScope.() -> Unit)? = null, diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None)
@Composable fun UPLoadingPage(props: UPLoadingPageProps = UPLoadingPageProps(), diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None)
@Composable fun UPLoadmore(props: UPLoadmoreProps = UPLoadmoreProps(), onLoadmore: (() -> Unit)? = null, diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None)
```

- [ ] **Step 1: Write failing Props and loader tests.** Assert image/avatars/status defaults, `extraValue`, loading-page effective icon size `19`, local loader injection, and that a null loader result displays the error state only when `showError=true`.
- [ ] **Step 2: Run the focused unit tests and verify missing API failures.**

```bash
./gradlew :ultra-ui:testDebugUnitTest --tests 'net.lingyun.ultraui.android.components.UPContentPropsTest' --console=plain
```

- [ ] **Step 3: Implement content/status renderers.** `UPImage` uses `produceState` with the injected loader, supports loading/error transitions, shape/radius/mode/bgColor, click/load/error callbacks and deterministic fallback icons. `UPAvatar` chooses image, text or icon in that order, supports circle/square, random color deterministically from `colorIndex`, and emits `name`. `UPAvatarGroup` clips/overlaps up to `maxCount`, shows `extraValue` in the overflow avatar and emits `showMore`. `UPEmpty` renders icon/text/mode/size/margins. `UPLoadingPage` covers the screen with native loading icon or image while `loading=false`. `UPLoadmore` renders `loadmore|loading|nomore`, optional line/dot and emits loadmore only in the actionable state.
- [ ] **Step 4: Run unit, compile, and behavior tests.**

```bash
./gradlew :ultra-ui:testDebugUnitTest :ultra-ui:compileDebugKotlin :ultra-ui:connectedDebugAndroidTest --console=plain
```

The behavior suite must assert image callbacks for injected success/failure, avatar fallback ordering, group overflow, empty visibility, loading-page visibility and loadmore event gating.
- [ ] **Step 5: Add deterministic screenshots.** Use `UPImageLoaders.Empty` or an in-memory loader returning a fixed `ImageBitmap`; cover image success/loading/error, avatar variants, group overflow, empty modes, loading page, and all loadmore statuses.

```bash
./gradlew :ultra-ui:updateDebugScreenshotTest :ultra-ui:validateDebugScreenshotTest --console=plain
```

- [ ] **Step 6: Commit the content batch.**

```bash
git add ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/components/UPImage* ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/components/UPAvatar* ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/components/UPEmpty* ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/components/UPLoadingPage* ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/components/UPLoadmore* ultra-ui/src/test ultra-ui/src/androidTest ultra-ui/src/screenshotTest ultra-ui/src/screenshotTestDebug/reference
git commit -m "feat: add uview content and status components"
```

---

### Task 5: Implement text input, textarea, search, and code-input components

**Components:** `u-input`, `u-textarea`, `u-search`, `u-code-input`.

**Files:**
- Create the eight public files listed for these components.
- Test: `ultra-ui/src/test/kotlin/net/lingyun/ultraui/android/components/UPInputPropsTest.kt`
- Test: `ultra-ui/src/androidTest/kotlin/net/lingyun/ultraui/android/components/UPInputBehaviorTest.kt`
- Test: `ultra-ui/src/screenshotTest/kotlin/net/lingyun/ultraui/android/components/InputScreenshots.kt`

**Interfaces:**
- Consumes: model alias resolver, `TextField`/`BasicTextField`, `UPIcon`, style and diagnostics support.
- Produces:

```kotlin
@Composable fun UPInput(props: UPInputProps = UPInputProps(), onInput: ((String) -> Unit)? = null, onChange: ((String) -> Unit)? = null, onFocus: (() -> Unit)? = null, onBlur: (() -> Unit)? = null, onConfirm: (() -> Unit)? = null, onClear: (() -> Unit)? = null, diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None)
@Composable fun UPTextarea(props: UPTextareaProps = UPTextareaProps(), onInput: ((String) -> Unit)? = null, onChange: ((String) -> Unit)? = null, onFocus: (() -> Unit)? = null, onBlur: (() -> Unit)? = null, onConfirm: (() -> Unit)? = null, diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None)
@Composable fun UPSearch(props: UPSearchProps = UPSearchProps(), onInput: ((String) -> Unit)? = null, onChange: ((String) -> Unit)? = null, onClear: (() -> Unit)? = null, onSearch: (() -> Unit)? = null, onCustom: (() -> Unit)? = null, onFocus: (() -> Unit)? = null, onBlur: (() -> Unit)? = null, onClick: (() -> Unit)? = null, onClickIcon: (() -> Unit)? = null, diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None)
@Composable fun UPCodeInput(props: UPCodeInputProps = UPCodeInputProps(), onInput: ((String) -> Unit)? = null, onChange: ((String) -> Unit)? = null, onFinish: ((String) -> Unit)? = null, diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None)
```

- [ ] **Step 1: Write failing input contract tests.** Assert all listed camelCase fields and defaults, `modelValue` precedence, max-length normalization, `u-code-input.adjustPosition`, `disabledDot`, `u-search.clearabled`, and `UPInputProps(cursorColor="#53c21d")`.
- [ ] **Step 2: Run focused tests and verify missing-API failure.**

```bash
./gradlew :ultra-ui:testDebugUnitTest --tests 'net.lingyun.ultraui.android.components.UPInputPropsTest' --console=plain
```

- [ ] **Step 3: Implement controlled Compose input state.** Keep `modelValue`/`value` compatibility, emit `input` on each accepted edit and `change` on focus loss/IME completion, enforce `maxlength` and `formatter` without throwing, support password visibility, read-only/disabled colors, prefix/suffix icons, border/shape/alignment/style, clear action and word counts. Textarea uses multiline/auto-height and line count. Search composes a native input with search/clear/action controls and emits each named event. Code input renders exactly `maxlength` cells, masks with dots when configured, supports box/line modes, focus and `finish` at max length; `disabledDot=true` rejects `.`.
- [ ] **Step 4: Run unit, compile, and behavior tests.**

```bash
./gradlew :ultra-ui:testDebugUnitTest :ultra-ui:compileDebugKotlin :ultra-ui:connectedDebugAndroidTest --console=plain
```

Behavior tests must cover typing, clear, IME confirm, focus/blur, disabled/read-only suppression, search action/icon events, code-input masking and finish, and alias state precedence.
- [ ] **Step 5: Add screenshots.** Render normal/password/clearable input, multiline count textarea, left/right search action and box/line code inputs with fixed values, no keyboard or time-dependent state.

```bash
./gradlew :ultra-ui:updateDebugScreenshotTest :ultra-ui:validateDebugScreenshotTest --console=plain
```

- [ ] **Step 6: Commit the input batch.**

```bash
git add ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/components/UPInput* ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/components/UPTextarea* ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/components/UPSearch* ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/components/UPCodeInput* ultra-ui/src/test ultra-ui/src/androidTest ultra-ui/src/screenshotTest ultra-ui/src/screenshotTestDebug/reference
git commit -m "feat: add uview text input components"
```

---

### Task 6: Implement switch, rate, number-box, checkbox, checkbox-group, radio, and radio-group

**Components:** `u-switch`, `u-rate`, `u-number-box`, `u-checkbox`, `u-checkbox-group`, `u-radio`, `u-radio-group`.

**Files:**
- Create the 14 public files listed for these components.
- Test: `ultra-ui/src/test/kotlin/net/lingyun/ultraui/android/components/UPSelectionPropsTest.kt`
- Test: `ultra-ui/src/androidTest/kotlin/net/lingyun/ultraui/android/components/UPSelectionBehaviorTest.kt`
- Test: `ultra-ui/src/screenshotTest/kotlin/net/lingyun/ultraui/android/components/SelectionScreenshots.kt`

**Interfaces:**
- Consumes: controlled value resolver, `UPIcon`, `UPButton`, diagnostics and style support.
- Produces:

```kotlin
@Composable fun UPSwitch(props: UPSwitchProps = UPSwitchProps(), onInput: ((UPRawValue) -> Unit)? = null, onChange: ((UPRawValue) -> Unit)? = null, diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None)
@Composable fun UPRate(props: UPRateProps = UPRateProps(), onInput: ((Float) -> Unit)? = null, onChange: ((Float) -> Unit)? = null, diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None)
@Composable fun UPNumberBox(props: UPNumberBoxProps = UPNumberBoxProps(), onInput: ((UPRawValue) -> Unit)? = null, onChange: ((UPRawValue) -> Unit)? = null, onOverlimit: (() -> Unit)? = null, onPlus: (() -> Unit)? = null, onMinus: (() -> Unit)? = null, diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None)
@Composable fun UPCheckbox(props: UPCheckboxProps = UPCheckboxProps(), onChange: ((Boolean, UPRawValue) -> Unit)? = null, onUpdateChecked: ((Boolean) -> Unit)? = null, diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None)
@Composable fun UPCheckboxGroup(props: UPCheckboxGroupProps = UPCheckboxGroupProps(), content: @Composable ColumnScope.() -> Unit, onInput: ((List<UPRawValue>) -> Unit)? = null, onChange: ((List<UPRawValue>) -> Unit)? = null, diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None)
@Composable fun UPRadio(props: UPRadioProps = UPRadioProps(), onChange: ((UPRawValue) -> Unit)? = null, diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None)
@Composable fun UPRadioGroup(props: UPRadioGroupProps = UPRadioGroupProps(), content: @Composable ColumnScope.() -> Unit, onInput: ((UPRawValue) -> Unit)? = null, onChange: ((UPRawValue) -> Unit)? = null, diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None)
```

- [x] **Step 1: Write failing selection contract tests.** Assert switch active/inactive values and colors, rate count/gutter/minimum/icon defaults, number-box min/max/step, checkbox false normalization, group `modelValue`/`value` aliases, radio shape and `gap="10px"`.
- [x] **Step 2: Run focused tests and verify missing-API failure.**

```bash
./gradlew :ultra-ui:testDebugUnitTest --tests 'net.lingyun.ultraui.android.components.UPSelectionPropsTest' --console=plain
```

- [x] **Step 3: Implement controlled selection components.** Switch uses a native Compose toggle visual but emits configured raw active/inactive values and reports async changes without changing state until the generated source updates Props. Rate supports tap/drag-compatible click targets, half values only when `allowHalf`, `minCount`, disabled/readonly and icon names. Number box clamps to min/max, normalizes integer/decimal length, emits plus/minus/overlimit and honors disabled plus/minus/input. Checkbox and radio use group-local composition state, preserve label/icon placement, border-bottom and shape, and emit raw names; groups expose row/column placement and keep their generated value contract.
- [x] **Step 4: Run unit, compile, and behavior tests.**

```bash
./gradlew :ultra-ui:testDebugUnitTest :ultra-ui:compileDebugKotlin :ultra-ui:connectedDebugAndroidTest --console=plain
```

Behavior tests must verify controlled switch async behavior, rate minimum/disabled behavior, number-box clamping and event order, checkbox group multi-selection, radio group single-selection, label-disabled suppression and `gap` measurement.
- [x] **Step 5: Add screenshots.** Cover active/inactive switch, three rate states, number-box limits, checkbox/radio shapes and group placements, using fixed props and no animations.

```bash
./gradlew :ultra-ui:updateDebugScreenshotTest :ultra-ui:validateDebugScreenshotTest --console=plain
```

- [x] **Step 6: Commit the selection batch.**

```bash
git add ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/components/UPSwitch* ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/components/UPRate* ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/components/UPNumberBox* ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/components/UPCheckbox* ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/components/UPRadio* ultra-ui/src/test ultra-ui/src/androidTest ultra-ui/src/screenshotTest ultra-ui/src/screenshotTestDebug/reference
git commit -m "feat: add uview selection components"
```

---

### Task 7: Implement row, column, grid, item, line-progress, and circle-progress

**Components:** `u-row`, `u-col`, `u-grid`, `u-grid-item`, `u-line-progress`, `u-circle-progress`.

**Files:**
- Create the 12 public files listed for these components.
- Test: `ultra-ui/src/test/kotlin/net/lingyun/ultraui/android/components/UPLayoutProgressPropsTest.kt`
- Test: `ultra-ui/src/androidTest/kotlin/net/lingyun/ultraui/android/components/UPLayoutProgressBehaviorTest.kt`
- Test: `ultra-ui/src/screenshotTest/kotlin/net/lingyun/ultraui/android/components/LayoutProgressScreenshots.kt`

**Interfaces:**
- Consumes: style/unit conversion, diagnostics, and controlled click semantics.
- Produces:

```kotlin
@Composable fun UPRow(props: UPRowProps = UPRowProps(), onClick: (() -> Unit)? = null, content: @Composable RowScope.() -> Unit, diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None)
@Composable fun UPCol(props: UPColProps = UPColProps(), onClick: (() -> Unit)? = null, content: @Composable ColumnScope.() -> Unit, diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None)
@Composable fun UPGrid(props: UPGridProps = UPGridProps(), content: @Composable RowScope.() -> Unit, diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None)
@Composable fun UPGridItem(props: UPGridItemProps = UPGridItemProps(), onClick: ((UPRawValue) -> Unit)? = null, content: @Composable ColumnScope.() -> Unit, diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None)
@Composable fun UPLineProgress(props: UPLineProgressProps = UPLineProgressProps(), diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None)
@Composable fun UPCircleProgress(props: UPCircleProgressProps = UPCircleProgressProps(), diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None)
```

- [x] **Step 1: Write failing layout/progress tests.** Assert all defaults and invalid enum fallback, row gutter/justify/align, col span/offset/textAlign, grid gap, item name payload, line-progress clamping/fromRight, and circle progress clamping.
- [x] **Step 2: Run focused tests and verify missing-API failure.**

```bash
./gradlew :ultra-ui:testDebugUnitTest --tests 'net.lingyun.ultraui.android.components.UPLayoutProgressPropsTest' --console=plain
```

- [x] **Step 3: Implement layout/progress renderers.** Row maps `start|end|center|space-around|space-between|space-evenly`, col computes a 12-column width with offset, grid lays out children in `col` columns with optional border and `gap`, and item emits its raw `name`. Line progress paints inactive/active tracks with `fromRight` and optional text; circle progress uses a deterministic `Canvas` arc with clamped percentage and no animation in screenshots.
- [x] **Step 4: Run unit, compile, and connected behavior tests.**

```bash
./gradlew :ultra-ui:testDebugUnitTest :ultra-ui:compileDebugKotlin :ultra-ui:connectedDebugAndroidTest --console=plain
```

Behavior tests must check row/col measured placement, grid item click payload, percentage clamping, right-to-left fill and progress text visibility.
- [x] **Step 5: Add and validate deterministic screenshots.** Render a row with three columns, a bordered/gapped grid, line-progress at 0/50/100 and from-right, and circle-progress at 30/100.

```bash
./gradlew :ultra-ui:updateDebugScreenshotTest :ultra-ui:validateDebugScreenshotTest --console=plain
```

- [x] **Step 6: Commit the layout/progress batch.**

```bash
git add ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/components/UPRow* ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/components/UPCol* ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/components/UPGrid* ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/components/UPLineProgress* ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/components/UPCircleProgress* ultra-ui/src/test ultra-ui/src/androidTest ultra-ui/src/screenshotTest ultra-ui/src/screenshotTestDebug/reference
git commit -m "feat: add uview layout and progress components"
```

---

### Task 8: Add Chinese sample navigation, public API documentation, and complete contract coverage

**Files:**
- Create: `sample/src/main/kotlin/net/lingyun/ultraui/android/sample/pages/FoundationDemoPage.kt`
- Create: `sample/src/main/kotlin/net/lingyun/ultraui/android/sample/pages/LayerContentDemoPage.kt`
- Create: `sample/src/main/kotlin/net/lingyun/ultraui/android/sample/pages/InputSelectionDemoPage.kt`
- Create: `sample/src/main/kotlin/net/lingyun/ultraui/android/sample/pages/LayoutProgressDemoPage.kt`
- Modify: `sample/src/main/kotlin/net/lingyun/ultraui/android/sample/SampleCatalog.kt`
- Modify: `sample/src/main/kotlin/net/lingyun/ultraui/android/sample/SampleApp.kt`
- Modify: `sample/src/androidTest/kotlin/net/lingyun/ultraui/android/sample/SampleCatalogTest.kt`
- Modify: `README.md`
- Test: `ultra-ui/src/test/kotlin/net/lingyun/ultraui/android/ProjectContractTest.kt`

**Interfaces:**
- Consumes every public library API from Tasks 1–7; sample imports no internal symbol and has no duplicate component implementation.
- Produces four reachable Chinese demo routes: `基础展示`, `弹层与内容`, `输入与选择`, `布局与进度`; each route contains a visible section for every component in its batch and at least one interaction that exercises the public event callback.

- [x] **Step 1: Write failing sample navigation/API coverage tests.** Assert all 38 component names occur in the catalog, each route is reachable from `SampleApp`, and the library public package contains every `UP*Props` class.
- [x] **Step 2: Run the tests before adding routes and docs.**

```bash
./gradlew :ultra-ui:testDebugUnitTest --tests 'net.lingyun.ultraui.android.ProjectContractTest' :sample:connectedDebugAndroidTest --console=plain
```

Expected: failure naming missing routes/Props/classes.
- [x] **Step 3: Implement four Chinese pages and route wiring.** Use only public `UP*` APIs, local deterministic assets, and state hoisted in the page. Add catalog titles for all 38 components and keep existing icon/loading-icon pages reachable.
- [x] **Step 4: Run full verification.**

```bash
export ANDROID_HOME="$HOME/Library/Android/sdk"
export ANDROID_SDK_ROOT="$ANDROID_HOME"
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
./scripts/verify-toolchain.sh
./gradlew \
  :ultra-ui:testDebugUnitTest \
  :ultra-ui:connectedDebugAndroidTest \
  :ultra-ui:validateDebugScreenshotTest \
  :sample:assembleDebug \
  :sample:connectedDebugAndroidTest \
  --console=plain
```

If screenshot validation is affected by the local proxy, run only the screenshot command again with `JAVA_TOOL_OPTIONS='-Dhttp.proxyHost= -Dhttps.proxyHost= -DsocksProxyHost='`; do not use that variable for `scripts/verify-toolchain.sh`.
- [x] **Step 5: Review the public API and forbidden-architecture boundaries.** Run:

```bash
# Run the repository's placeholder-marker scan and require zero matches.
rg -n 'FastView|\.xyfv|WebView|org\.json|kotlinx\.serialization|Gson|Moshi' ultra-ui sample README.md
rg -n 'data class UP(Button|Tag|Badge|Divider|Gap|Line|Link|Text|Title|Overlay|Popup|Modal|Toast|Cell|CellGroup|Image|Avatar|AvatarGroup|Empty|LoadingPage|Loadmore|Input|Textarea|Search|CodeInput|Switch|Rate|NumberBox|Checkbox|CheckboxGroup|Radio|RadioGroup|Row|Col|Grid|GridItem|LineProgress|CircleProgress)Props' ultra-ui/src/main/kotlin
```

Expected: the plan scan prints no lines; the forbidden-boundary scan prints no forbidden implementation usage; the Props scan prints all 38 data classes.
- [x] **Step 6: Commit sample/docs and final coverage.**

```bash
git add sample README.md ultra-ui/src/test/kotlin/net/lingyun/ultraui/android/ProjectContractTest.kt
git commit -m "docs: add complete uview component catalog"
```

## Plan Self-Review

- **Spec coverage:** Tasks 1–8 cover all 38 requested components, their exact camelCase Props fields, defaults, direct overloads, custom style, diagnostics, event semantics, unit tests, Compose behavior tests, screenshots, sample navigation, package identity, native Android wrappers, and forbidden runtime boundaries.
- **Placeholder scan:** The repository's placeholder-marker scan in Task 8 must return no lines before this plan is considered executable.
- **Type consistency:** All generated values use `UPRawValue`; model aliases use nullable `modelValue` plus `value`; event payloads use `UPRawValue` or explicitly typed strings/floats/booleans; `UPToastProps` is canonical and `UPToastOptions` is only a typealias; every component accepts `diagnostics` and `customStyle`.
- **Approval carry-forward:** The architecture document is marked approved by the repository owner on August 17, 2026. The user subsequently selected inline execution and repeatedly requested continuation, so implementation proceeds in this checkout without a second design pause or additional worktree.
