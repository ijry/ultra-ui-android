# UltraUI Android First Milestone Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Deliver a testable Jetpack Compose library and Android demo that reproduce the selected uview-plus 3.x component contracts and Chinese demo pages for generated Android source.

**Architecture:** Create an `ultra-ui` Android library module with a stable `UP*Props` code-generation contract and Compose renderers that resolve uview-plus strings and raw number-or-string values internally. Create a `sample` Compose application that depends only on the library’s public API, organizes pages as Components A/B/C, and mirrors the pinned upstream scenarios. Keep all JSON parsing, FastView runtime behavior, WebView rendering, and source generation outside this repository.

**Tech Stack:** Android Gradle Plugin 9.2.1 built-in Kotlin with Kotlin Compose Compiler 2.3.21, Android Studio bundled JBR 21 to run Gradle while compiling with Java/Kotlin target 17, Gradle 9.5.1, Compose BOM 2025.12.01, AndroidX Compose Material 3 primitives without Material visual defaults, Compose Navigation, AndroidX Compose UI tests, and Compose Preview Screenshot Testing.

## Global Constraints

- Kotlin root package and library namespace are exactly `net.lingyun.ultraui.android`; the sample application id is exactly `net.lingyun.ultraui.android.sample`.
- Implement Compose-only public APIs in this milestone; do not publish XML/View wrappers.
- Treat `/Users/admin/Documents/Repos/xyito/open/uview-plus` at branch `3.x`, commit `96f14b06f857aeda011ae5baf9654bb4634dba9a`, as the visual and behavioral source of truth.
- Do not add FastView, `.xyfv`, JSON parsing, a JSON renderer, a backend generator, or a WebView runtime.
- Each completed component exposes a complete `UP*Props` data class using uview-plus camelCase field names and a concise direct Compose overload.
- Preserve cross-platform-only fields in public Props contracts so generated Kotlin compiles; ignore them on Android and report use only through opt-in diagnostics.
- Unknown enum strings, malformed color/unit strings, and unsupported style properties must fall back safely and must not crash a screen.
- Resolve `rpx` as `availableScreenWidthDp / 750f`; resolve numeric values, `px`, and `dp` one-to-one to Compose `Dp`.
- Copy the pinned upstream `components/u-icon/upicon.ttf` into `ultra-ui/src/main/res/font/upicon.ttf`; generate the glyph catalog from the pinned upstream `icons.js` file rather than manually maintaining a partial list.
- Replicate the Chinese labels, section grouping, cases, and interaction states from the nine selected demo pages. Replace upstream network-only decoration with deterministic local placeholders.
- Each component batch must include API-contract tests, Compose behavior tests, deterministic screenshot tests, and sample-navigation coverage before its commit.

---

## Planned File Structure

```text
.
├── .gitignore
├── build.gradle.kts
├── gradle.properties
├── gradle/libs.versions.toml
├── gradlew
├── gradlew.bat
├── settings.gradle.kts
├── scripts/
│   └── verify-toolchain.sh
├── tools/
│   └── generate_upicon_catalog.py
├── ultra-ui/
│   ├── build.gradle.kts
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   ├── res/font/upicon.ttf
│       │   └── kotlin/net/lingyun/ultraui/android/
│       │       ├── core/
│       │       │   ├── UPCompatibilityDiagnostics.kt
│       │       │   ├── UPConfig.kt
│       │       │   ├── UPColor.kt
│       │       │   ├── UPStyle.kt
│       │       │   ├── UPTheme.kt
│       │       │   ├── UPUnit.kt
│       │       │   └── UPValue.kt
│       │       └── components/
│       │           ├── UPButton.kt
│       │           ├── UPButtonProps.kt
│       │           ├── UPCell.kt
│       │           ├── UPCellGroup.kt
│       │           ├── UPCellProps.kt
│       │           ├── UPIcon.kt
│       │           ├── UPIconGlyphs.kt
│       │           ├── UPIconProps.kt
│       │           ├── UPLoadingIcon.kt
│       │           ├── UPLoadingIconProps.kt
│       │           ├── UPModal.kt
│       │           ├── UPModalProps.kt
│       │           ├── UPOverlay.kt
│       │           ├── UPOverlayProps.kt
│       │           ├── UPPopup.kt
│       │           ├── UPPopupProps.kt
│       │           ├── UPTag.kt
│       │           ├── UPTagProps.kt
│       │           ├── UPToast.kt
│       │           └── UPToastController.kt
│       ├── test/kotlin/net/lingyun/ultraui/android/
│       │   ├── core/
│       │   └── components/
│       ├── androidTest/kotlin/net/lingyun/ultraui/android/components/
│       └── screenshotTest/kotlin/net/lingyun/ultraui/android/components/
└── sample/
    ├── build.gradle.kts
    └── src/
        ├── main/kotlin/net/lingyun/ultraui/android/sample/
        │   ├── MainActivity.kt
        │   ├── SampleApp.kt
        │   ├── SampleCatalog.kt
        │   ├── SampleScaffold.kt
        │   └── pages/
        │       ├── ButtonDemoPage.kt
        │       ├── CellDemoPage.kt
        │       ├── IconDemoPage.kt
        │       ├── LoadingIconDemoPage.kt
        │       ├── ModalDemoPage.kt
        │       ├── OverlayDemoPage.kt
        │       ├── PopupDemoPage.kt
        │       ├── TagDemoPage.kt
        │       └── ToastDemoPage.kt
        └── androidTest/kotlin/net/lingyun/ultraui/android/sample/
```

### Task 1: Bootstrap the reproducible Android project

**Files:**
- Create: `.gitignore`
- Create: `settings.gradle.kts`
- Create: `build.gradle.kts`
- Create: `gradle.properties`
- Create: `gradle/libs.versions.toml`
- Create: `scripts/verify-toolchain.sh`
- Create: `ultra-ui/build.gradle.kts`
- Create: `ultra-ui/src/main/AndroidManifest.xml`
- Create: `sample/build.gradle.kts`
- Create: `sample/src/main/AndroidManifest.xml`
- Create: `ultra-ui/src/test/kotlin/net/lingyun/ultraui/android/ProjectContractTest.kt`

**Interfaces:**
- Consumes: the fixed namespace, package identities, and Compose-only scope from the approved design.
- Produces: Gradle modules `:ultra-ui` and `:sample`; a unit-test task named `:ultra-ui:testDebugUnitTest`; an installable sample debug variant.

- [ ] **Step 1: Write the failing library identity test**

```kotlin
package net.lingyun.ultraui.android

import kotlin.test.Test
import kotlin.test.assertEquals

class ProjectContractTest {
    @Test
    fun libraryUsesThePublishedRootPackage() {
        assertEquals("net.lingyun.ultraui.android.core", UPConfig::class.java.packageName)
    }
}
```

- [ ] **Step 2: Run the test before scaffolding the project**

Run: `./gradlew :ultra-ui:testDebugUnitTest --tests net.lingyun.ultraui.android.ProjectContractTest`

Expected: FAIL because the wrapper, `:ultra-ui` module, and `UPConfig` do not exist yet.

- [ ] **Step 3: Add the Gradle modules, pinned toolchain, and minimum `UPConfig` declaration**

```kotlin
// settings.gradle.kts
pluginManagement { repositories { google(); mavenCentral(); gradlePluginPortal() } }
dependencyResolutionManagement { repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS); repositories { google(); mavenCentral() } }
rootProject.name = "ultra-ui-android"
include(":ultra-ui", ":sample")

// ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/core/UPConfig.kt
package net.lingyun.ultraui.android.core

public object UPConfig
```

Configure `ultra-ui` with namespace `net.lingyun.ultraui.android`, `minSdk = 24`, `compileSdk = 36`, Java target 17 (the AGP built-in Kotlin compiler follows it), Compose enabled with the Kotlin Compose Compiler plugin, the Compose BOM, Compose UI test dependencies, and AGP native screenshot-test support. Run Gradle with Android Studio bundled JBR (the current local JBR is Java 21); do not install a separate JDK 17. Configure `sample` with application id `net.lingyun.ultraui.android.sample` and `implementation(project(":ultra-ui"))`. Make `scripts/verify-toolchain.sh` fail with a precise message unless a Java runtime at least 17 and Android SDK platform `android-36` are available, and print the selected runtime path/version.

- [ ] **Step 4: Run the bootstrap checks**

Run: `./scripts/verify-toolchain.sh && ./gradlew :ultra-ui:testDebugUnitTest :sample:assembleDebug`

Expected: PASS; the toolchain check confirms Android Studio bundled JBR (Java 21 or newer) and `android-36`, the package identity test passes, and the sample APK assembles.

- [ ] **Step 5: Commit the reproducible project skeleton**

```bash
git add .gitignore build.gradle.kts gradle.properties gradle settings.gradle.kts scripts ultra-ui sample
git commit -m "build: scaffold Compose library and sample app"
```

### Task 2: Implement core compatibility parsing, defaults, theme, and diagnostics

**Files:**
- Modify: `ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/core/UPConfig.kt`
- Create: `ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/core/UPValue.kt`
- Create: `ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/core/UPTheme.kt`
- Create: `ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/core/UPColor.kt`
- Create: `ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/core/UPUnit.kt`
- Create: `ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/core/UPStyle.kt`
- Create: `ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/core/UPCompatibilityDiagnostics.kt`
- Create: `ultra-ui/src/test/kotlin/net/lingyun/ultraui/android/core/UPColorTest.kt`
- Create: `ultra-ui/src/test/kotlin/net/lingyun/ultraui/android/core/UPUnitTest.kt`
- Create: `ultra-ui/src/test/kotlin/net/lingyun/ultraui/android/core/UPStyleTest.kt`
- Create: `ultra-ui/src/test/kotlin/net/lingyun/ultraui/android/core/UPConfigContractTest.kt`

**Interfaces:**
- Consumes: raw generator values that can be booleans, strings, numbers, or maps.
- Produces: `UPRawValue`, `UPStyleInput`, `UPTheme`, `UPColor.parse`, `UPUnit.toDp`, `UPStyle.resolve`, `UPCompatibilityDiagnostics`, and component-default groups in `UPConfig`.

- [ ] **Step 1: Write core parser and fallback tests first**

```kotlin
@Test fun parsesNamedAndRgbaColorsWithoutThrowing() {
    assertEquals(UPTheme.Primary, UPColor.parse("primary", UPTheme.Info))
    assertEquals(Color(0x80336699), UPColor.parse("#80336699", UPTheme.Info))
    assertEquals(Color(0x80336699), UPColor.parse("rgba(51,102,153,0.5)", UPTheme.Info))
    assertEquals(UPTheme.Info, UPColor.parse("bad-color", UPTheme.Info))
}

@Test fun convertsRpxAndFallsBackForMalformedUnits() {
    assertEquals(375.dp, UPUnit.toDp("750rpx", 375.dp, 1.dp))
    assertEquals(16.dp, UPUnit.toDp("16px", 375.dp, 1.dp))
    assertEquals(9.dp, UPUnit.toDp("broken", 375.dp, 9.dp))
}

@Test fun reportsUnsupportedStyleKeysOnlyWhenDiagnosticsAreEnabled() {
    val events = mutableListOf<UPCompatibilityEvent>()
    val style = UPStyle.resolve(mapOf("marginTop" to "8px", "boxShadow" to "0 1px"), 375.dp, UPCompatibilityDiagnostics { events += it })
    assertEquals(8.dp, style.marginTop)
    assertEquals(listOf("boxShadow"), events.map { it.property })
}
```

- [ ] **Step 2: Run the focused tests and confirm they fail**

Run: `./gradlew :ultra-ui:testDebugUnitTest --tests '*UPColorTest' --tests '*UPUnitTest' --tests '*UPStyleTest' --tests '*UPConfigContractTest'`

Expected: FAIL because the core compatibility API has not been implemented.

- [ ] **Step 3: Implement the stable core contract**

```kotlin
package net.lingyun.ultraui.android.core

typealias UPRawValue = Any?
typealias UPStyleInput = Any?

public data class UPCompatibilityEvent(
    val component: String,
    val property: String,
    val value: UPRawValue,
    val reason: String,
)

public fun interface UPCompatibilityDiagnostics {
    public fun report(event: UPCompatibilityEvent)

    public companion object {
        public val None: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics { }
    }
}
```

Populate `UPTheme` from the pinned uview-plus colors: primary `#2979ff`, warning `#ff9900`, success `#19be6b`, error `#fa3534`, info `#909399`, main `#303133`, content `#606266`, tips `#909399`, and light `#c0c4cc`. Give `UPConfig` immutable defaults for every first-milestone Props field. Implement `UPColor` for theme keys, `#RGB`, `#RGBA`, `#RRGGBB`, `#RRGGBBAA`, `rgb()`, and `rgba()`; parse a two-color `linear-gradient(to right, ...)` only for button backgrounds. Implement `UPStyle` for widths/heights, margins/paddings, color/background/backgroundColor, border/borderColor/borderWidth, borderRadius, fontSize/fontWeight, textAlign, opacity, and alignment. All unsupported CSS keys emit one diagnostic and are ignored.

- [ ] **Step 4: Run the core contract suite and screenshot-plugin discovery task**

Run: `./gradlew :ultra-ui:testDebugUnitTest :ultra-ui:validateScreenshotTest`

Expected: PASS; malformed inputs return fallbacks, the exact first-milestone defaults are covered by tests, and the screenshot-test source set is recognized.

- [ ] **Step 5: Commit the reusable compatibility core**

```bash
git add ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/core ultra-ui/src/test/kotlin/net/lingyun/ultraui/android/core ultra-ui/build.gradle.kts
git commit -m "feat: add uview compatibility core"
```

### Task 3: Add the deterministic sample catalog shell and local demo assets

**Files:**
- Create: `sample/src/main/kotlin/net/lingyun/ultraui/android/sample/MainActivity.kt`
- Create: `sample/src/main/kotlin/net/lingyun/ultraui/android/sample/SampleApp.kt`
- Create: `sample/src/main/kotlin/net/lingyun/ultraui/android/sample/SampleCatalog.kt`
- Create: `sample/src/main/kotlin/net/lingyun/ultraui/android/sample/SampleScaffold.kt`
- Create: `sample/src/main/res/drawable/demo_placeholder.xml`
- Create: `sample/src/androidTest/kotlin/net/lingyun/ultraui/android/sample/SampleCatalogTest.kt`

**Interfaces:**
- Consumes: public `ultra-ui` composables only.
- Produces: Compose Navigation routes `button`, `icon`, `loading-icon`, `overlay`, `popup`, `cell`, `toast`, `tag`, and `modal`; a reusable Chinese demo section container; a local placeholder painter.

- [ ] **Step 1: Write the catalog navigation test**

```kotlin
@Test fun catalogShowsOnlyCompletedComponentRoutes() {
    composeRule.setContent { SampleApp() }
    composeRule.onNodeWithText("Components A").assertExists()
    composeRule.onNodeWithText("按钮").performClick()
    composeRule.onNodeWithText("按钮类型").assertExists()
}
```

- [ ] **Step 2: Run the instrumentation test before creating the sample shell**

Run: `./gradlew :sample:connectedDebugAndroidTest --tests net.lingyun.ultraui.android.sample.SampleCatalogTest`

Expected: FAIL because `SampleApp` and its navigation routes do not exist.

- [ ] **Step 3: Implement the empty catalog and shared page layout**

```kotlin
public data class SampleDestination(
    val route: String,
    val group: String,
    val title: String,
)

public val sampleDestinations: List<SampleDestination> = emptyList()

@Composable
public fun DemoSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column { Text(title); content() }
}
```

Add destinations only inside the component tasks that also add the corresponding library component and page. Use a white page background, the uview main/content/tips colors, and deterministic `demo_placeholder.xml` in every place where upstream uses an HTTP image.

- [ ] **Step 4: Run the sample app and its initial navigation test**

Run: `./gradlew :sample:assembleDebug :sample:connectedDebugAndroidTest --tests net.lingyun.ultraui.android.sample.SampleCatalogTest`

Expected: PASS after the first completed page is registered; the catalog must not list an unimplemented page.

- [ ] **Step 5: Commit the sample shell**

```bash
git add sample/src/main sample/src/androidTest
git commit -m "feat: add uview-style sample catalog shell"
```

### Task 4: Implement icon and loading-icon contracts, rendering, tests, and pages

**Files:**
- Create: `tools/generate_upicon_catalog.py`
- Create: `ultra-ui/src/main/res/font/upicon.ttf`
- Create: `ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/components/UPIconProps.kt`
- Create: `ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/components/UPIconGlyphs.kt`
- Create: `ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/components/UPIcon.kt`
- Create: `ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/components/UPLoadingIconProps.kt`
- Create: `ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/components/UPLoadingIcon.kt`
- Create: `ultra-ui/src/test/kotlin/net/lingyun/ultraui/android/components/UPIconPropsTest.kt`
- Create: `ultra-ui/src/test/kotlin/net/lingyun/ultraui/android/components/UPLoadingIconPropsTest.kt`
- Create: `ultra-ui/src/androidTest/kotlin/net/lingyun/ultraui/android/components/UPIconBehaviorTest.kt`
- Create: `ultra-ui/src/androidTest/kotlin/net/lingyun/ultraui/android/components/UPLoadingIconBehaviorTest.kt`
- Create: `ultra-ui/src/screenshotTest/kotlin/net/lingyun/ultraui/android/components/IconLoadingScreenshots.kt`
- Create: `sample/src/main/kotlin/net/lingyun/ultraui/android/sample/pages/IconDemoPage.kt`
- Create: `sample/src/main/kotlin/net/lingyun/ultraui/android/sample/pages/LoadingIconDemoPage.kt`
- Modify: `sample/src/main/kotlin/net/lingyun/ultraui/android/sample/SampleCatalog.kt`
- Modify: `sample/src/main/kotlin/net/lingyun/ultraui/android/sample/SampleApp.kt`

**Interfaces:**
- Produces `data class UPIconProps(name, color, size, bold, index, hoverClass, customPrefix, label, labelPos, labelSize, labelColor, space, imgMode, width, height, top, stop, customStyle)` with upstream defaults.
- Produces `data class UPLoadingIconProps(show, color, textColor, vertical, mode, size, textSize, text, timingFunction, duration, inactiveColor, customStyle)` with upstream defaults.
- Produces `UPIcon(props, modifier)` / direct `UPIcon(name, ...)` and `UPLoadingIcon(props, modifier)` / direct `UPLoadingIcon(show, mode, ...)`.

- [ ] **Step 1: Write failing prop and behavior tests**

```kotlin
@Test fun iconPropsPreserveUviewDefaultsAndKnownGlyphNames() {
    assertEquals("", UPIconProps().name)
    assertEquals("16px", UPIconProps().size)
    assertEquals('\ue61d', UPIconGlyphs.codePoint("map"))
}

@Test fun loadingIconHidesWhenShowIsFalse() {
    composeRule.setContent { UPLoadingIcon(UPLoadingIconProps(show = false)) }
    composeRule.onNodeWithTag("up-loading-icon").assertDoesNotExist()
}
```

- [ ] **Step 2: Run the icon/loading tests and confirm they fail**

Run: `./gradlew :ultra-ui:testDebugUnitTest --tests '*IconPropsTest' --tests '*LoadingIconPropsTest' && ./gradlew :ultra-ui:connectedDebugAndroidTest --tests '*IconBehaviorTest' --tests '*LoadingIconBehaviorTest'`

Expected: FAIL because the font asset, glyph catalog, props, and composables are absent.

- [ ] **Step 3: Implement catalog generation and native renderers**

```python
# tools/generate_upicon_catalog.py
# Parse entries such as "'uicon-map': '\\ue61d'" from pinned icons.js,
# strip exactly one "uicon-" prefix, sort by key, and emit UPIconGlyphs.kt.
```

Copy the pinned TTF, run the generator against the pinned `icons.js`, and commit the generated Kotlin catalog. Render known glyphs with `FontFamily(Font(R.font.upicon))`; render labels at `labelPos` values `left`, `right`, `top`, and `bottom`; report unknown names and unsupported image sources through diagnostics without crashing. Implement `spinner`, `semicircle`, and `circle` loading modes with Compose animation, `duration`, `timingFunction`, color, text, text size, and vertical layout. Add `testTag` and content descriptions for UI tests.

- [ ] **Step 4: Add the two exact sample pages and run their full test matrix**

Run: `./gradlew :ultra-ui:testDebugUnitTest :ultra-ui:connectedDebugAndroidTest :ultra-ui:updateDebugScreenshotTest :sample:connectedDebugAndroidTest`

Expected: PASS; `图标` mirrors the icon grid/name/color/size/label cases in `icon.nvue`, `加载中图标` mirrors `基本案列`, `半圆loading`, `圆形loading`, `自定义动画`, `自定义颜色`, and `自定义文字`, and screenshots are written from deterministic fixtures.

- [ ] **Step 5: Commit icon and loading-icon support**

```bash
git add tools ultra-ui sample
git commit -m "feat: add icon and loading icon components"
```

### Task 5: Implement button compatibility, throttle behavior, screenshot cases, and page

**Files:**
- Create: `ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/components/UPButtonProps.kt`
- Create: `ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/components/UPButton.kt`
- Create: `ultra-ui/src/test/kotlin/net/lingyun/ultraui/android/components/UPButtonPropsTest.kt`
- Create: `ultra-ui/src/androidTest/kotlin/net/lingyun/ultraui/android/components/UPButtonBehaviorTest.kt`
- Create: `ultra-ui/src/screenshotTest/kotlin/net/lingyun/ultraui/android/components/ButtonScreenshots.kt`
- Create: `sample/src/main/kotlin/net/lingyun/ultraui/android/sample/pages/ButtonDemoPage.kt`
- Modify: `sample/src/main/kotlin/net/lingyun/ultraui/android/sample/SampleCatalog.kt`
- Modify: `sample/src/main/kotlin/net/lingyun/ultraui/android/sample/SampleApp.kt`

**Interfaces:**
- Produces `UPButtonProps` with fields `hairline`, `type`, `size`, `shape`, `plain`, `disabled`, `loading`, `loadingText`, `loadingMode`, `loadingSize`, `openType`, `formType`, `appParameter`, `hoverStopPropagation`, `lang`, `sessionFrom`, `sendMessageTitle`, `sendMessagePath`, `sendMessageImg`, `showMessageCard`, `dataName`, `throttleTime`, `hoverStartTime`, `hoverStayTime`, `text`, `icon`, `iconColor`, `color`, `stop`, and `customStyle`.
- Produces `UPButton(props, modifier, onClick)` and direct `UPButton(text, type, size, shape, plain, disabled, loading, onClick, modifier)`.

- [ ] **Step 1: Write failing contract and click tests**

```kotlin
@Test fun buttonAcceptsPlatformOnlyPropsWithUviewDefaults() {
    val props = UPButtonProps(openType = "contact", formType = "submit", sessionFrom = "support")
    assertEquals("contact", props.openType)
    assertEquals("info", UPButtonProps().type)
    assertEquals("normal", UPButtonProps().size)
}

@Test fun loadingDisabledAndThrottleSuppressClicks() {
    var clicks = 0
    composeRule.setContent { UPButton(UPButtonProps(text = "提交", throttleTime = 500), onClick = { clicks++ }) }
    composeRule.onNodeWithText("提交").performClick().performClick()
    assertEquals(1, clicks)
}
```

- [ ] **Step 2: Run the focused button suite before implementation**

Run: `./gradlew :ultra-ui:testDebugUnitTest --tests '*UPButtonPropsTest' && ./gradlew :ultra-ui:connectedDebugAndroidTest --tests '*UPButtonBehaviorTest'`

Expected: FAIL because `UPButtonProps` and `UPButton` are missing.

- [ ] **Step 3: Implement the button renderer and compatibility downgrade**

```kotlin
@Composable
public fun UPButton(
    props: UPButtonProps = UPButtonProps(),
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) { /* resolve type/size/shape and compose native Surface semantics */ }
```

Resolve `type` values `primary`, `success`, `info`, `error`, and `warning`, falling back to `info`; resolve `size` values `large`, `normal`, `small`, and `mini`, falling back to `normal`; resolve `shape` values `circle` and `square`, falling back to `square`. Implement filled/plain/hairline colors, disabled opacity, solid and two-stop horizontal gradient `color`, leading `UPIcon`, and loading text/icon. Drive throttling from an injectable monotonic clock so behavior tests do not sleep. Suppress events while disabled or loading. For each platform-only field, call diagnostics once when its value differs from its default and otherwise render nothing.

- [ ] **Step 4: Recreate the Button demo and run API, behavior, screenshot, and navigation checks**

Run: `./gradlew :ultra-ui:testDebugUnitTest :ultra-ui:connectedDebugAndroidTest :ultra-ui:updateDebugScreenshotTest :sample:connectedDebugAndroidTest`

Expected: PASS; the `按钮` page contains the eight Chinese sections from `button.nvue`, including type/plain/hairline/disabled/loading/icon-shape/custom-color/custom-size cases. Screenshots cover one filled primary button, one hairline warning button, a disabled error button, a loading success button, and a gradient button.

- [ ] **Step 5: Commit the button milestone**

```bash
git add ultra-ui sample
git commit -m "feat: add uview-compatible button"
```

### Task 6: Implement tag behavior, name payloads, screenshot cases, and page

**Files:**
- Create: `ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/components/UPTagProps.kt`
- Create: `ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/components/UPTag.kt`
- Create: `ultra-ui/src/test/kotlin/net/lingyun/ultraui/android/components/UPTagPropsTest.kt`
- Create: `ultra-ui/src/androidTest/kotlin/net/lingyun/ultraui/android/components/UPTagBehaviorTest.kt`
- Create: `ultra-ui/src/screenshotTest/kotlin/net/lingyun/ultraui/android/components/TagScreenshots.kt`
- Create: `sample/src/main/kotlin/net/lingyun/ultraui/android/sample/pages/TagDemoPage.kt`
- Modify: `sample/src/main/kotlin/net/lingyun/ultraui/android/sample/SampleCatalog.kt`
- Modify: `sample/src/main/kotlin/net/lingyun/ultraui/android/sample/SampleApp.kt`

**Interfaces:**
- Produces `UPTagProps` with fields `type`, `disabled`, `size`, `shape`, `text`, `bgColor`, `color`, `borderColor`, `closeColor`, `name`, `plainFill`, `plain`, `closable`, `show`, `icon`, `iconColor`, `textSize`, `height`, `padding`, `borderRadius`, `autoBgColor`, and `customStyle`.
- Produces `UPTag(props, modifier, onClick: ((UPRawValue) -> Unit)?, onClose: ((UPRawValue) -> Unit)?)` and a concise direct overload.

- [ ] **Step 1: Write failing tag tests**

```kotlin
@Test fun tagDefaultsMatchUviewAndCanCarryNamePayload() {
    assertEquals("primary", UPTagProps().type)
    assertEquals("medium", UPTagProps().size)
    assertEquals(false, UPTagProps().closable)
}

@Test fun closeEmitsNameAndDisabledTagEmitsNothing() {
    var closed: UPRawValue = null
    composeRule.setContent { UPTag(UPTagProps(text = "标签", name = 8, closable = true), onClose = { closed = it }) }
    composeRule.onNodeWithContentDescription("关闭 标签").performClick()
    assertEquals(8, closed)
}
```

- [ ] **Step 2: Run the tag tests and confirm they fail**

Run: `./gradlew :ultra-ui:testDebugUnitTest --tests '*UPTagPropsTest' && ./gradlew :ultra-ui:connectedDebugAndroidTest --tests '*UPTagBehaviorTest'`

Expected: FAIL because `UPTagProps` and `UPTag` are missing.

- [ ] **Step 3: Implement tag rendering and close/click semantics**

```kotlin
@Composable
public fun UPTag(
    props: UPTagProps = UPTagProps(),
    modifier: Modifier = Modifier,
    onClick: ((UPRawValue) -> Unit)? = null,
    onClose: ((UPRawValue) -> Unit)? = null,
)
```

Return without composition when `show` is false. Resolve type/color, `small`/`medium`/`mini` size, `square`/`circle` shape, plain/plainFill backgrounds, custom dimensions, icon, and close button. Emit `name` for both click and close, exactly as upstream does. Do not emit either event when disabled. Use `UPIcon` for named icon strings and local placeholder treatment for remote image strings.

- [ ] **Step 4: Recreate all Tag page groups and verify deterministic output**

Run: `./gradlew :ultra-ui:testDebugUnitTest :ultra-ui:connectedDebugAndroidTest :ultra-ui:updateDebugScreenshotTest :sample:connectedDebugAndroidTest`

Expected: PASS; `标签` includes `基础功能`, `自定义主题`, `圆形标签`, `镂空标签`, `镂空带背景色`, `自定义尺寸`, `可关闭标签`, `带图片和图标`, `单选标签`, and `多选标签`; close, single-select, and multi-select state changes are covered by instrumentation tests.

- [ ] **Step 5: Commit tag support**

```bash
git add ultra-ui sample
git commit -m "feat: add uview-compatible tag"
```

### Task 7: Implement overlay and popup controlled visibility, layers, slots, tests, and pages

**Files:**
- Create: `ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/components/UPOverlayProps.kt`
- Create: `ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/components/UPOverlay.kt`
- Create: `ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/components/UPPopupProps.kt`
- Create: `ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/components/UPPopup.kt`
- Create: `ultra-ui/src/test/kotlin/net/lingyun/ultraui/android/components/UPOverlayPropsTest.kt`
- Create: `ultra-ui/src/test/kotlin/net/lingyun/ultraui/android/components/UPPopupPropsTest.kt`
- Create: `ultra-ui/src/androidTest/kotlin/net/lingyun/ultraui/android/components/UPOverlayPopupBehaviorTest.kt`
- Create: `ultra-ui/src/screenshotTest/kotlin/net/lingyun/ultraui/android/components/OverlayPopupScreenshots.kt`
- Create: `sample/src/main/kotlin/net/lingyun/ultraui/android/sample/pages/OverlayDemoPage.kt`
- Create: `sample/src/main/kotlin/net/lingyun/ultraui/android/sample/pages/PopupDemoPage.kt`
- Modify: `sample/src/main/kotlin/net/lingyun/ultraui/android/sample/SampleCatalog.kt`
- Modify: `sample/src/main/kotlin/net/lingyun/ultraui/android/sample/SampleApp.kt`

**Interfaces:**
- Produces `UPOverlayProps(show, zIndex, duration, opacity, customStyle)` and `UPOverlay(props, modifier, onClick, content)`.
- Produces `UPPopupProps(show, overlay, mode, duration, closeable, overlayStyle, closeOnClickOverlay, zIndex, safeAreaInsetBottom, safeAreaInsetTop, closeIconPos, round, zoom, bgColor, overlayOpacity, pageInline, touchable, minHeight, maxHeight, customStyle)`.
- Produces `UPPopup(props, modifier, onShowChange, onOpen, onClose, onClick, popupBottom, content)` and direct `UPPopup(show, onShowChange, mode, ...)`.

- [ ] **Step 1: Write failing controlled-state and slot tests**

```kotlin
@Test fun overlayClickIsDeliveredOnlyWhenShown() {
    var clicks = 0
    composeRule.setContent { UPOverlay(UPOverlayProps(show = true), onClick = { clicks++ }) }
    composeRule.onNodeWithTag("up-overlay").performClick()
    assertEquals(1, clicks)
}

@Test fun popupOverlayCloseRequestsStateChangeAndCloseEvent() {
    var shown = true
    var closeCount = 0
    composeRule.setContent { UPPopup(UPPopupProps(show = shown), onShowChange = { shown = it }, onClose = { closeCount++ }) }
    composeRule.onNodeWithTag("up-popup-overlay").performClick()
    assertFalse(shown)
    assertEquals(1, closeCount)
}
```

- [ ] **Step 2: Run the layer behavior tests before implementation**

Run: `./gradlew :ultra-ui:testDebugUnitTest --tests '*OverlayPropsTest' --tests '*PopupPropsTest' && ./gradlew :ultra-ui:connectedDebugAndroidTest --tests '*OverlayPopupBehaviorTest'`

Expected: FAIL because the controlled overlay and popup APIs are absent.

- [ ] **Step 3: Implement native overlay and popup primitives**

```kotlin
@Composable
public fun UPPopup(
    props: UPPopupProps = UPPopupProps(),
    modifier: Modifier = Modifier,
    onShowChange: (Boolean) -> Unit,
    onOpen: (() -> Unit)? = null,
    onClose: (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    popupBottom: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit = {},
)
```

Use a full-window Compose overlay layer and placement-specific popup surfaces for `top`, `bottom`, `left`, `right`, and `center`; fall back to `bottom`. Overlay click requests `onShowChange(false)` then invokes `onClose` only when `closeOnClickOverlay` is true. Close icon follows `top-left`, `top-right`, `bottom-left`, or `bottom-right` and always requests state change. Invoke `onOpen` only after first composition of a shown popup. Respect overlay visibility, opacity, safe-area insets, round/background color, z-index ordering, content clicks, and `popupBottom`. Emit diagnostics for page-inline and drag/touch parameters that cannot be represented identically by Compose.

- [ ] **Step 4: Mirror Overlay and Popup demo interactions and run the matrix**

Run: `./gradlew :ultra-ui:testDebugUnitTest :ultra-ui:connectedDebugAndroidTest :ultra-ui:updateDebugScreenshotTest :sample:connectedDebugAndroidTest`

Expected: PASS; `遮罩层` has basic, embedded-content, opacity, and deterministic QR-placeholder cases. `弹窗` has top/right/bottom/left/center, rounded, overlay-close-disabled, close-icon, and gesture-labelled cases; screenshots cover each placement and one closed state.

- [ ] **Step 5: Commit overlay and popup support**

```bash
git add ultra-ui sample
git commit -m "feat: add overlay and popup components"
```

### Task 8: Implement modal state semantics, async close, named slots, tests, and page

**Files:**
- Create: `ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/components/UPModalProps.kt`
- Create: `ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/components/UPModal.kt`
- Create: `ultra-ui/src/test/kotlin/net/lingyun/ultraui/android/components/UPModalPropsTest.kt`
- Create: `ultra-ui/src/androidTest/kotlin/net/lingyun/ultraui/android/components/UPModalBehaviorTest.kt`
- Create: `ultra-ui/src/screenshotTest/kotlin/net/lingyun/ultraui/android/components/ModalScreenshots.kt`
- Create: `sample/src/main/kotlin/net/lingyun/ultraui/android/sample/pages/ModalDemoPage.kt`
- Modify: `sample/src/main/kotlin/net/lingyun/ultraui/android/sample/SampleCatalog.kt`
- Modify: `sample/src/main/kotlin/net/lingyun/ultraui/android/sample/SampleApp.kt`

**Interfaces:**
- Produces `UPModalProps(show, title, content, confirmText, cancelText, showConfirmButton, showCancelButton, confirmColor, cancelColor, buttonReverse, zoom, asyncClose, closeOnClickOverlay, negativeTop, width, confirmButtonShape, duration, contentTextAlign, asyncCloseTip, asyncCancelClose, contentStyle, customStyle)`.
- Produces `UPModal(props, modifier, onShowChange, onConfirm, onCancel, onClose, onCancelOnAsync, title, confirmButton, popupBottom, content)` and direct `UPModal(show, onShowChange, title, content, ...)`.

- [ ] **Step 1: Write failing modal state tests**

```kotlin
@Test fun normalConfirmRequestsCloseThenEmitsConfirm() {
    var shown = true
    var confirms = 0
    composeRule.setContent { UPModal(UPModalProps(show = shown), onShowChange = { shown = it }, onConfirm = { confirms++ }) }
    composeRule.onNodeWithText("确认").performClick()
    assertFalse(shown)
    assertEquals(1, confirms)
}

@Test fun asyncConfirmLeavesStateOpenUntilOwnerChangesIt() {
    var shown = true
    composeRule.setContent { UPModal(UPModalProps(show = shown, asyncClose = true), onShowChange = { shown = it }) }
    composeRule.onNodeWithText("确认").performClick()
    assertTrue(shown)
}
```

- [ ] **Step 2: Run modal contract and behavior tests before implementation**

Run: `./gradlew :ultra-ui:testDebugUnitTest --tests '*UPModalPropsTest' && ./gradlew :ultra-ui:connectedDebugAndroidTest --tests '*UPModalBehaviorTest'`

Expected: FAIL because `UPModalProps` and `UPModal` are absent.

- [ ] **Step 3: Implement the modal as a uview-oriented native dialog wrapper**

```kotlin
@Composable
public fun UPModal(
    props: UPModalProps = UPModalProps(),
    modifier: Modifier = Modifier,
    onShowChange: (Boolean) -> Unit,
    onConfirm: (() -> Unit)? = null,
    onCancel: (() -> Unit)? = null,
    onClose: (() -> Unit)? = null,
    onCancelOnAsync: (() -> Unit)? = null,
    title: (@Composable () -> Unit)? = null,
    confirmButton: (@Composable () -> Unit)? = null,
    popupBottom: (@Composable () -> Unit)? = null,
    content: (@Composable () -> Unit)? = null,
)
```

Use `UPPopup` center-mode layering without exposing Material dialog defaults. In normal confirm/cancel flows request `onShowChange(false)` before the corresponding event. For `asyncClose`, retain local confirm-loading state and do not request close after confirm; while loading, cancel calls `onCancelOnAsync` and displays `asyncCloseTip` through the toast host. Honor `asyncCancelClose`, `buttonReverse`, `showConfirmButton`, `showCancelButton`, `confirmButtonShape`, custom title/content/confirm/popup-bottom slots, width, negative top, zoom, overlay-close, and content alignment. Make all modal ownership remain controlled by `show` and `onShowChange`.

- [ ] **Step 4: Add all ten Modal demo cases and run the complete verification set**

Run: `./gradlew :ultra-ui:testDebugUnitTest :ultra-ui:connectedDebugAndroidTest :ultra-ui:updateDebugScreenshotTest :sample:connectedDebugAndroidTest`

Expected: PASS; `模态框` offers basic/no-title/cancel/async/reversed/overlay-close/slot/custom-button/fade/popup-bottom cases. Instrumentation covers confirm, cancel, overlay-close, async confirm, and the owner-driven close after asynchronous completion.

- [ ] **Step 5: Commit modal support**

```bash
git add ultra-ui sample
git commit -m "feat: add uview-compatible modal"
```

### Task 9: Implement toast host/controller behavior, screenshot cases, and page

**Files:**
- Create: `ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/components/UPToastController.kt`
- Create: `ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/components/UPToast.kt`
- Create: `ultra-ui/src/test/kotlin/net/lingyun/ultraui/android/components/UPToastControllerTest.kt`
- Create: `ultra-ui/src/androidTest/kotlin/net/lingyun/ultraui/android/components/UPToastBehaviorTest.kt`
- Create: `ultra-ui/src/screenshotTest/kotlin/net/lingyun/ultraui/android/components/ToastScreenshots.kt`
- Create: `sample/src/main/kotlin/net/lingyun/ultraui/android/sample/pages/ToastDemoPage.kt`
- Modify: `sample/src/main/kotlin/net/lingyun/ultraui/android/sample/SampleApp.kt`
- Modify: `sample/src/main/kotlin/net/lingyun/ultraui/android/sample/SampleCatalog.kt`

**Interfaces:**
- Produces `data class UPToastOptions(message, type, zIndex, duration, icon, position, complete, overlay, loading, loadingMode, customStyle)`.
- Produces `interface UPToastController { fun show(options: UPToastOptions); fun hide() }`, `rememberUPToastController()`, `UPToastHost(controller, modifier)`, and `UPToast(options, onDismiss, modifier)`.

- [ ] **Step 1: Write failing controller lifecycle tests**

```kotlin
@Test fun negativeDurationKeepsToastVisibleUntilHide() {
    val controller = TestUPToastController()
    controller.show(UPToastOptions(message = "加载中", duration = -1))
    assertEquals("加载中", controller.current.value?.message)
    controller.hide()
    assertNull(controller.current.value)
}

@Test fun toastCompleteRunsExactlyOnceAfterTimedDismissal() {
    var completed = 0
    composeRule.mainClock.autoAdvance = false
    composeRule.setContent { ToastHarness(onComplete = { completed++ }) }
    composeRule.mainClock.advanceTimeBy(2_000)
    assertEquals(1, completed)
}
```

- [ ] **Step 2: Run toast tests before implementation**

Run: `./gradlew :ultra-ui:testDebugUnitTest --tests '*UPToastControllerTest' && ./gradlew :ultra-ui:connectedDebugAndroidTest --tests '*UPToastBehaviorTest'`

Expected: FAIL because the toast controller and host do not exist.

- [ ] **Step 3: Implement library-owned toast rendering and timing**

```kotlin
@Stable
public interface UPToastController {
    public fun show(options: UPToastOptions)
    public fun hide()
}

@Composable
public fun UPToastHost(controller: UPToastController, modifier: Modifier = Modifier)
```

Render through the host rather than Android system Toast. Support types `primary`, `success`, `error`, `warning`, `default`, and `loading`; use `top`, `center`, and `bottom` positions and fall back to `center`. Use default duration `2000`, do not auto-dismiss `-1`, replace an active toast on `show`, honor transparent interaction-blocking overlay, emit completion once after timed dismissal, and render icon booleans, `none`, known glyphs, and local placeholder image paths safely.

- [ ] **Step 4: Mirror every Toast demo item and run behavior and visual tests**

Run: `./gradlew :ultra-ui:testDebugUnitTest :ultra-ui:connectedDebugAndroidTest :ultra-ui:updateDebugScreenshotTest :sample:connectedDebugAndroidTest`

Expected: PASS; `提示消息` contains default, error-without-icon, success, top-position, loading, completion-navigation, custom-icon, and image-placeholder cases. Tests assert overlay input blocking and no auto-dismiss for duration `-1`.

- [ ] **Step 5: Commit toast support**

```bash
git add ultra-ui sample
git commit -m "feat: add uview-compatible toast host"
```

### Task 10: Implement cell group and cell contracts, slots, events, tests, and page

**Files:**
- Create: `ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/components/UPCellProps.kt`
- Create: `ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/components/UPCellGroup.kt`
- Create: `ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/components/UPCell.kt`
- Create: `ultra-ui/src/test/kotlin/net/lingyun/ultraui/android/components/UPCellPropsTest.kt`
- Create: `ultra-ui/src/androidTest/kotlin/net/lingyun/ultraui/android/components/UPCellBehaviorTest.kt`
- Create: `ultra-ui/src/screenshotTest/kotlin/net/lingyun/ultraui/android/components/CellScreenshots.kt`
- Create: `sample/src/main/kotlin/net/lingyun/ultraui/android/sample/pages/CellDemoPage.kt`
- Modify: `sample/src/main/kotlin/net/lingyun/ultraui/android/sample/SampleApp.kt`
- Modify: `sample/src/main/kotlin/net/lingyun/ultraui/android/sample/SampleCatalog.kt`

**Interfaces:**
- Produces `UPCellGroupProps(title, border, customStyle)` and `UPCellGroup(props, modifier, title, content)`.
- Produces `UPCellProps(customClass, title, label, value, icon, disabled, border, center, url, linkType, clickable, isLink, required, arrowDirection, iconStyle, rightIconStyle, rightIcon, titleStyle, size, stop, name, customStyle)`.
- Produces `data class UPCellClickEvent(val name: UPRawValue)` plus `UPCell(props, modifier, onClick, icon, title, value, rightIcon)`.

- [ ] **Step 1: Write failing props, event-payload, and slot tests**

```kotlin
@Test fun cellPropsKeepRouteFieldsWhileDeliveringNamePayload() {
    val props = UPCellProps(title = "单元格", url = "/pages/componentsB/tag/tag", name = "tag")
    assertEquals("navigateTo", props.linkType)
    assertEquals("tag", props.name)
}

@Test fun disabledCellDoesNotClickAndEnabledCellReturnsName() {
    var event: UPCellClickEvent? = null
    composeRule.setContent { UPCell(UPCellProps(title = "单元格", name = 3, isLink = true), onClick = { event = it }) }
    composeRule.onNodeWithText("单元格").performClick()
    assertEquals(3, event?.name)
}
```

- [ ] **Step 2: Run the focused cell tests before implementation**

Run: `./gradlew :ultra-ui:testDebugUnitTest --tests '*UPCellPropsTest' && ./gradlew :ultra-ui:connectedDebugAndroidTest --tests '*UPCellBehaviorTest'`

Expected: FAIL because the cell group, cell Props, and click-event type are absent.

- [ ] **Step 3: Implement cell layout, dividers, slots, and safe route downgrade**

```kotlin
@Composable
public fun UPCell(
    props: UPCellProps = UPCellProps(),
    modifier: Modifier = Modifier,
    onClick: ((UPCellClickEvent) -> Unit)? = null,
    icon: (@Composable () -> Unit)? = null,
    title: (@Composable () -> Unit)? = null,
    value: (@Composable () -> Unit)? = null,
    rightIcon: (@Composable () -> Unit)? = null,
)
```

Render title/label/value, required asterisk, `large` sizing, left/right icons, `isLink` arrow direction `left`/`up`/`down`/default right, per-cell and group borders, center alignment, and title/icon/right-icon styles. Emit `UPCellClickEvent(name)` for enabled rows. Do not navigate from the library when `url` and `linkType` are supplied; preserve them for generated-source compatibility and report their Android no-op status through diagnostics unless an application provides its own `onClick` routing.

- [ ] **Step 4: Recreate the Cell demo and run tests/screenshots**

Run: `./gradlew :ultra-ui:testDebugUnitTest :ultra-ui:connectedDebugAndroidTest :ultra-ui:updateDebugScreenshotTest :sample:connectedDebugAndroidTest`

Expected: PASS; `单元格` includes all seven source groups: basic, custom icon/image placeholder, large size, arrows, route-labelled rows, vertical center, and title/value/right-icon slots. Screenshots cover labels, large rows, required state, and custom slots.

- [ ] **Step 5: Commit cell support**

```bash
git add ultra-ui sample
git commit -m "feat: add uview-compatible cells"
```

### Task 11: Add complete API compatibility coverage and guarded sample integration tests

**Files:**
- Create: `ultra-ui/src/test/kotlin/net/lingyun/ultraui/android/components/FirstMilestoneApiContractTest.kt`
- Create: `sample/src/androidTest/kotlin/net/lingyun/ultraui/android/sample/FirstMilestoneNavigationTest.kt`
- Modify: `README.md`

**Interfaces:**
- Consumes: every first-milestone Props class, public composable, Toast controller, sample destination, and Chinese page title.
- Produces: a single regression gate that rejects removed/renamed Props fields, changed defaults, missing sample pages, or library APIs leaked into the sample only.

- [ ] **Step 1: Write the failing complete-contract test**

```kotlin
@Test fun firstMilestonePropsExposeTheCrossPlatformFields() {
    assertEquals("primary", UPTagProps().type)
    assertEquals("bottom", UPPopupProps().mode)
    assertEquals("650rpx", UPModalProps().width)
    assertEquals(10090, UPToastOptions().zIndex)
    assertEquals("arrow-right", UPCellProps().rightIcon)
    assertEquals("spinner", UPLoadingIconProps().mode)
}

@Test fun catalogContainsExactlyTheNineCompletedPages() {
    assertEquals(
        listOf("按钮", "图标", "加载中图标", "遮罩层", "弹窗", "单元格", "提示消息", "标签", "模态框"),
        sampleDestinations.map { it.title },
    )
}
```

- [ ] **Step 2: Run the full contract and navigation tests before wiring all pages**

Run: `./gradlew :ultra-ui:testDebugUnitTest --tests '*FirstMilestoneApiContractTest' && ./gradlew :sample:connectedDebugAndroidTest --tests '*FirstMilestoneNavigationTest'`

Expected: FAIL until every Props class, default, and sample route from Tasks 4 through 10 exists.

- [ ] **Step 3: Implement the final reflection-free contract assertions and README usage example**

```kotlin
UPButton(
    props = UPButtonProps(text = "主要按钮", type = "primary", shape = "circle"),
    onClick = ::submit,
)
```

Document the library/sample module split, package names, baseline commit, no-JSON-runtime boundary, build commands, icon font provenance, `show` plus `onShowChange` state pattern, platform-only prop downgrade, and how to update screenshot references only after comparing to the pinned source demo.

- [ ] **Step 4: Execute the aggregate verification gate**

Run: `./scripts/verify-toolchain.sh && ./gradlew :ultra-ui:testDebugUnitTest :ultra-ui:connectedDebugAndroidTest :ultra-ui:verifyDebugScreenshotTest :sample:assembleDebug :sample:connectedDebugAndroidTest`

Expected: PASS; all nine routes are reachable, every selected demo is library-backed, API defaults match the pinned source, screenshots match checked-in references, and no test depends on network images.

- [ ] **Step 5: Commit compatibility evidence and documentation**

```bash
git add README.md ultra-ui/src/test sample/src/androidTest
git commit -m "test: lock first milestone compatibility contract"
```

### Task 12: Run repository-level quality checks and produce the implementation handoff

**Files:**
- Modify: `README.md`
- Modify: `docs/superpowers/plans/2026-08-17-ultra-ui-android-first-milestone.md`

**Interfaces:**
- Consumes: all build, test, screenshot, and sample artifacts from Tasks 1 through 11.
- Produces: a verified repository state with no accidental JSON runtime and a concise status record for the owner.

- [ ] **Step 1: Write the failing repository-boundary check**

```bash
! grep -R --line-number --exclude-dir=.git --exclude='*.md' \
  -E 'FastView|\.xyfv|WebView|JSONObject|kotlinx\.serialization\.json' \
  ultra-ui/src/main sample/src/main
```

- [ ] **Step 2: Run the boundary check before the final cleanup**

Run: `! grep -R --line-number --exclude-dir=.git --exclude='*.md' -E 'FastView|\.xyfv|WebView|JSONObject|kotlinx\.serialization\.json' ultra-ui/src/main sample/src/main`

Expected: PASS only when the library remains a native Compose contract and demo; any matching runtime/parser code is a failure.

- [ ] **Step 3: Record verified commands and exact implementation outcomes in README**

```markdown
./scripts/verify-toolchain.sh
./gradlew :ultra-ui:testDebugUnitTest :ultra-ui:connectedDebugAndroidTest :ultra-ui:verifyDebugScreenshotTest
./gradlew :sample:assembleDebug :sample:connectedDebugAndroidTest
```

Add a completed-component table listing exactly the nine first-milestone components. Do not list unimplemented uview-plus components as available.

- [ ] **Step 4: Run whitespace, source-boundary, and full verification**

Run: `git diff --check && ! grep -R --line-number --exclude-dir=.git --exclude='*.md' -E 'FastView|\.xyfv|WebView|JSONObject|kotlinx\.serialization\.json' ultra-ui/src/main sample/src/main && ./scripts/verify-toolchain.sh && ./gradlew :ultra-ui:testDebugUnitTest :ultra-ui:connectedDebugAndroidTest :ultra-ui:verifyDebugScreenshotTest :sample:assembleDebug :sample:connectedDebugAndroidTest`

Expected: PASS; the working tree contains no whitespace errors, no prohibited runtime/parser architecture, all contract/behavior/screenshot tests pass, and the demo application builds and tests successfully.

- [ ] **Step 5: Commit the verified milestone handoff**

```bash
git add README.md docs/superpowers/plans/2026-08-17-ultra-ui-android-first-milestone.md
git commit -m "docs: document Android compatibility milestone"
```

## Plan Self-Review

### Spec coverage

- Repository identity, Compose-only library boundary, and the no-JSON-runtime rule are covered by Tasks 1 and 12.
- Color/theme/default/unit/style/diagnostic behavior is covered by Task 2.
- The icon TTF and generated full glyph catalog are covered by Task 4.
- The nine specified library components and their upstream demo pages are covered by Tasks 4 through 10.
- Generated-source Props compatibility, including Android no-op fields, is covered in each component task and consolidated by Task 11.
- Controlled `show` state, events, named slots, disabled/loading suppression, popup/modal close behavior, tag close payload, and async modal behavior have dedicated instrumentation assertions.
- Visual regression coverage uses deterministic local data and Compose screenshot tests in every component batch.
- Sample navigation and public-library-only usage are covered by Tasks 3 and 11.

### Placeholder scan

This plan contains concrete file paths, public signatures, test names, commands, expected outcomes, and commits for every task. It contains no deferred implementation markers.

### Type consistency

All raw generator values use `UPRawValue`; style input uses `UPStyleInput`; controlled layers use `show` plus `onShowChange`; cell clicks use `UPCellClickEvent`; tag callbacks deliver `UPRawValue`; toast is hosted through `UPToastController`. Component names, package names, and sample route titles are consistent across tasks.
