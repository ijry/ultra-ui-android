# UltraUI Android Native Interaction Batch 9A Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [x]`) syntax for tracking.

**Goal:** 在 `net.lingyun.ultraui.android` 中新增 Batch 9A 的 10 个 uview-plus 原生交互与容器组件，保持生成端可直接使用的 `UP*Props`、camelCase 字段、字符串枚举、事件回调和 Compose 原生渲染语义。

**Architecture:** 每个组件提供公开 `UP*Props` 数据类、Props 渲染入口和 direct Compose overload。组件只消费后端生成好的 Kotlin 参数；受控状态通过 `modelValue` / `value` 和事件回调向外同步，不在 Android 内解析 JSON 或运行跨端页面。具备 Android 原生等价能力的弹层、下拉、折叠、刷新和滚动行为使用 Compose `Dialog`、`Popup`、`LazyColumn`、`PullToRefreshBox` 或轻量自绘实现。

**Tech Stack:** Kotlin 2.3.21、Android Gradle Plugin 9.2.1、Jetpack Compose BOM 2025.12.01、Compose Material3/Foundation/UI、JUnit4、Compose UI test、Android screenshot test plugin；验证使用 Android Studio 内置 JBR。

## Global Constraints

- Android library namespace 和 Kotlin 根包必须是 `net.lingyun.ultraui.android`；sample 包必须是 `net.lingyun.ultraui.android.sample`。
- 上游参考固定为 `/Users/admin/Documents/Repos/xyito/open/uview-plus` 的提交 `96f14b06f857aeda011ae5baf9654bb4634dba9a`。
- 后端负责 JSON 解析和跨端源码生成；本库不引入 JSON 解析器、FastView、`.xyfv`、WebView 或运行时代码生成。
- 不安装独立 JDK 17；Gradle 使用 Android Studio 内置 JBR，源码 Java/Kotlin target 保持 17。
- 每个组件都必须接受 `customStyle: UPStyleInput` 和 `UPCompatibilityDiagnostics`，未知枚举安全回退且不抛异常。
- Props 字段保持 uview-plus camelCase；受控字段保留 nullable `modelValue` 与 `value` 兼容别名。
- 事件回调优先发出生成端可序列化的原始值或明确的 `UPRawValue`，组件内部不偷偷替换外部受控状态。
- 动画、定时器、刷新和截图必须可确定；截图使用固定尺寸、短动画或关闭动画。
- 每项生产代码必须先有会失败的测试，再实现最小行为，之后运行单元测试、Compose 行为测试和截图校验。

## Task 1: Establish Batch 9A public contract and failing tests

**Files:**
- Create: `ultra-ui/src/test/kotlin/net/lingyun/ultraui/android/components/UPNativeInteractionPropsTest.kt`
- Create: `ultra-ui/src/androidTest/kotlin/net/lingyun/ultraui/android/components/UPNativeInteractionBehaviorTest.kt`
- Create: `ultra-ui/src/screenshotTest/kotlin/net/lingyun/ultraui/android/components/NativeInteractionScreenshots.kt`
- Modify: `ultra-ui/src/test/kotlin/net/lingyun/ultraui/android/ProjectContractTest.kt`

**Interfaces:**
- Contract tests require these public classes: `UPAlertProps`, `UPActionSheetProps`, `UPNotifyProps`, `UPBackTopProps`, `UPCardProps`, `UPCollapseProps`, `UPCollapseItemProps`, `UPDropdownProps`, `UPDropdownItemProps`, `UPNoticeBarProps`.
- Behavior tests require semantic tags `up-alert`, `up-action-sheet`, `up-notify`, `up-back-top`, `up-card`, `up-collapse`, `up-collapse-item`, `up-dropdown`, `up-dropdown-item`, and `up-notice-bar`.
- Tests cover controlled visibility, callback payloads, safe enum fallback, model/value aliases, expand/collapse, selected dropdown item, refresh callback, and notice close/click actions.

- [x] **Step 1: Write failing Props contract tests.** Construct all 10 Props with defaults and non-default values; assert camelCase fields, `customStyle`, aliases, defaults and enum strings are preserved. Assert the project contract lists all 48 public Props classes after this batch.
- [x] **Step 2: Run the focused unit tests and confirm missing-API failure.**

```bash
./gradlew :ultra-ui:testDebugUnitTest --tests 'net.lingyun.ultraui.android.components.UPNativeInteractionPropsTest' --tests 'net.lingyun.ultraui.android.ProjectContractTest' --console=plain
```

Expected: compilation failure naming missing Batch 9A Props classes and renderer APIs.
- [x] **Step 3: Write failing Compose behavior tests and screenshot declarations.** Exercise the public APIs without internal helpers; include a 10-component deterministic gallery and interaction assertions.
- [x] **Step 4: Run the focused Android tests before implementations.**

```bash
./gradlew :ultra-ui:connectedDebugAndroidTest --tests '*UPNativeInteractionBehaviorTest' --console=plain
```

Expected: compilation failure because the requested composables and Props classes do not exist.

## Task 2: Implement alert, action sheet, notify, and back-top

**Files:**
- Create: `ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/components/UPAlertProps.kt`
- Create: `ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/components/UPAlert.kt`
- Create: `ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/components/UPActionSheetProps.kt`
- Create: `ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/components/UPActionSheet.kt`
- Create: `ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/components/UPNotifyProps.kt`
- Create: `ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/components/UPNotify.kt`
- Create: `ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/components/UPBackTopProps.kt`
- Create: `ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/components/UPBackTop.kt`

**Interfaces:**
- `UPAlertProps` preserves `title`, `type`, `description`, `closable`, `showIcon`, `effect`, `center`, `fontSize`, `transitionMode`, `duration`, `icon`, `modelValue`, `value`, and `customStyle`.
- `UPActionSheetProps` preserves `show`, `actions`, `title`, `description`, `cancelText`, `closeOnClickAction`, `safeAreaInsetBottom`, `openType`, `closeOnClickOverlay`, `round`, `wrapMaxHeight`, and `customStyle`; action rows emit raw `name` or index payloads.
- `UPNotifyProps` preserves `top`, `type`, `color`, `bgColor`, `message`, `duration`, `fontSize`, `safeAreaInsetTop`, and `customStyle`.
- `UPBackTopProps` preserves the upstream `mode`, `icon`, `text`, `duration`, `scrollTop`, `top`, `bottom`, `right`, `zIndex`, `iconStyle`, and `customStyle`; visibility is derived from `scrollTop > top`, matching the uni-app component, and the direct overload exposes `onClick` plus the Android-friendly `onBackToTop` alias.

- [x] **Step 1: Implement Props defaults and raw-value-compatible action model.** Keep action payloads as `List<UPRawValue>`/`List<UPRawMap>` compatible with generated code and provide a typed convenience constructor only where it does not change the generated contract.
- [x] **Step 2: Implement native renderers.** Alert uses a styled surface with optional icon and close action; action sheet uses `ModalBottomSheet`-equivalent Compose layout with overlay close behavior; notify uses an anchored top banner and deterministic duration; back-top uses a scroll threshold and native click callback.
- [x] **Step 3: Run unit tests and compile.**

```bash
./gradlew :ultra-ui:testDebugUnitTest :ultra-ui:compileDebugKotlin --console=plain
```

- [x] **Step 4: Run focused connected behavior tests.** Assert alert close, action-sheet action payload, notify visibility, and back-top click semantics.

## Task 3: Implement card, collapse, and dropdown families

**Files:**
- Create: `ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/components/UPCardProps.kt`
- Create: `ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/components/UPCard.kt`
- Create: `ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/components/UPCollapseProps.kt`
- Create: `ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/components/UPCollapse.kt`
- Create: `ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/components/UPCollapseItemProps.kt`
- Create: `ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/components/UPCollapseItem.kt`
- Create: `ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/components/UPDropdownProps.kt`
- Create: `ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/components/UPDropdown.kt`
- Create: `ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/components/UPDropdownItemProps.kt`
- Create: `ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/components/UPDropdownItem.kt`

**Interfaces:**
- `UPCardProps` preserves the upstream `full`, `title`, `titleColor`, `titleSize`, `subTitle`, `subTitleColor`, `subTitleSize`, `border`, `index`, `margin`, `borderRadius`, `headStyle`, `bodyStyle`, `footStyle`, `headBorderBottom`, `footBorderTop`, `thumb`, `thumbWidth`, `thumbCircle`, `padding`, `paddingHead`, `paddingBody`, `paddingFoot`, `showHead`, `showFoot`, `boxShadow`, and `customStyle`; `radius`/`shadow` remain appended nullable aliases for older Android generators.
- `UPCollapseProps` preserves the upstream `value`, `accordion`, `border`, and `customStyle`, with appended nullable `modelValue` plus Android interaction aliases `arrow`, `disabled`, and `clickable`; `UPCollapseItemProps` preserves all upstream item fields and appends `isOpen`/`open` for standalone generated renderers.
- `UPDropdownProps` preserves the upstream `activeColor`, `inactiveColor`, `closeOnClickMask`, `closeOnClickSelf`, `duration`, `height`, `borderBottom`, `titleSize`, `borderRadius`, `menuIcon`, `menuIconSize`, and `customStyle`; appended nullable/optional aliases retain `closeOnClickOverlay`, `direction`, and `menu` for older generated Android payloads. `UPDropdownItemProps` preserves the upstream `modelValue`/`value`, `title`, `options`, `disabled`, `height`, `closeOnClickOverlay`, and `customStyle`, with appended `multiple` for generated multi-select payloads.

- [x] **Step 1: Implement card surface and slot layout.** Support title/header/footer/body slots, thumb, border, radius, margin/padding, shadow and custom styles.
- [x] **Step 2: Implement controlled collapse state.** Support accordion and multi-open names, disabled/clickable items, arrow state, `onChange` and item click payloads; content stays in Compose slots.
- [x] **Step 3: Implement dropdown menu and item popup.** Support direction, active colors, disabled options, single/multiple value resolution, overlay close and option callback payloads.
- [x] **Step 4: Run unit, compile and connected behavior tests.**

```bash
./gradlew :ultra-ui:testDebugUnitTest :ultra-ui:compileDebugKotlin :ultra-ui:connectedDebugAndroidTest --console=plain
```

## Task 4: Implement notice bar and shared sample route

**Files:**
- Create: `ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/components/UPNoticeBarProps.kt`
- Create: `ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/components/UPNoticeBar.kt`
- Create: `sample/src/main/kotlin/net/lingyun/ultraui/android/sample/pages/NativeInteractionDemoPage.kt`
- Modify: `sample/src/main/kotlin/net/lingyun/ultraui/android/sample/SampleCatalog.kt`
- Modify: `sample/src/main/kotlin/net/lingyun/ultraui/android/sample/SampleApp.kt`
- Modify: `sample/src/androidTest/kotlin/net/lingyun/ultraui/android/sample/SampleCatalogTest.kt`
- Modify: `ultra-ui/src/test/kotlin/net/lingyun/ultraui/android/ProjectContractTest.kt`

**Interfaces:**
- `UPNoticeBarProps` preserves the upstream `text`, `direction`, `step`, `icon`, `mode`, `color`, `bgColor`, `speed`, `fontSize`, `duration`, `disableTouch`, `url`, `linkType`, `justifyContent`, and `customStyle`; no speculative legacy fields are added because they are not present in the pinned uview-plus `props.js` contract.
- Add a Chinese sample route `原生交互` containing visible sections and at least one callback interaction for all Batch 9A components; keep all previous routes reachable.

- [x] **Step 1: Implement notice bar.** Support left/right icons, close affordance, mode/type colors, non-scrolling and deterministic scrolling behavior, click and close callbacks.
- [x] **Step 2: Add the sample route and update catalog contract.** Use only public `UP*` APIs and state hoisting; no duplicate component implementation.
- [x] **Step 3: Run sample compile and connected navigation tests.**

## Task 5: Deterministic screenshots, docs, and verification

**Files:**
- Modify: `ultra-ui/src/screenshotTest/kotlin/net/lingyun/ultraui/android/components/NativeInteractionScreenshots.kt`
- Add generated references under: `ultra-ui/src/screenshotTestDebug/reference/...`
- Modify: `README.md`
- Modify: `docs/superpowers/plans/2026-08-18-ultra-ui-android-native-interaction-9a.md`

- [x] **Step 1: Render deterministic screenshots.** Cover alert, action sheet, notify, card/collapse, dropdown, and notice bar with fixed dimensions and disabled/short animations.
- [x] **Step 2: Review and validate screenshot references.**

```bash
./gradlew :ultra-ui:updateDebugScreenshotTest :ultra-ui:validateDebugScreenshotTest --console=plain
```

- [x] **Step 3: Run final verification and boundary scans.**

```bash
export ANDROID_HOME="$HOME/Library/Android/sdk"
export ANDROID_SDK_ROOT="$ANDROID_HOME"
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
./scripts/verify-toolchain.sh
./gradlew :ultra-ui:testDebugUnitTest :ultra-ui:connectedDebugAndroidTest :ultra-ui:validateDebugScreenshotTest :sample:assembleDebug :sample:connectedDebugAndroidTest --console=plain
if rg -n 'FastView|\.xyfv|WebView|org\.json|kotlinx\.serialization|Gson|Moshi' ultra-ui sample README.md; then exit 1; fi
```

- [x] **Step 4: Update README with Batch 9A catalog and commit.**

```bash
git add ultra-ui sample README.md docs/superpowers/plans/2026-08-18-ultra-ui-android-native-interaction-9a.md
git commit -m "feat: add native interaction components batch 9a"
```

## Verification record

- Props/contract unit tests and Kotlin compilation passed for Batch 9A.
- Focused `UPNativeInteractionBehaviorTest`: 6/6 connected tests passed on `MCode_Phone(AVD) - 16`.
- Full `:ultra-ui:connectedDebugAndroidTest`: 40/40 connected tests passed on `MCode_Phone(AVD) - 16`.
- Full `:sample:connectedDebugAndroidTest`: 3/3 connected tests passed; existing icon routes remain reachable.
- `:ultra-ui:updateDebugScreenshotTest` and `:ultra-ui:validateDebugScreenshotTest` passed; three Batch 9A references were reviewed.
- The pinned uview-plus `props.js` audit confirms that `safeAreaInsetBottom`, `ellipsis`, `showLeftIcon`, `showRightIcon`, `closeMode`, and `rightIcon` are not props of the referenced `u-dropdown-item`/`u-notice-bar` version; they were intentionally not added as speculative fields.
- Fresh verification on 2026-08-18: toolchain check passed; unit tests passed; focused native behavior tests passed 6/6; full library instrumentation passed 40/40; screenshot validation passed; sample assemble and instrumentation passed 3/3; boundary scan and staged diff check passed.
- The final aggregate verification command and commit are intentionally performed after the README/catalog and boundary-scan changes in this worktree.
