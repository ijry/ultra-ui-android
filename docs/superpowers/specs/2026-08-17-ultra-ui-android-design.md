# UltraUI Android uview-plus Compatibility Design

**Status:** Approved by the repository owner on August 17, 2026.

## Objective

Create a native Android Jetpack Compose component library whose public `UP*` APIs, defaults, string enum values, styling, model-state semantics, slots, and events follow uview-plus closely enough that a backend can transform one canonical component JSON description into equivalent uni-app, SwiftUI, and Android Compose source.

The Android library is **not** a JSON renderer, FastView runtime, WebView wrapper, or backend code generator. The backend owns JSON parsing and platform-source generation. This library owns the Android-native component contract and behavior that the generated code calls.

## Repository and package identity

- Repository: `ultra-ui-android`.
- Kotlin root package and Android library namespace: `net.lingyun.ultraui.android`.
- The sample application package is `net.lingyun.ultraui.android.sample`, so it cannot collide with the library package.
- The public library is a Jetpack Compose Android library. It does not expose a View-system compatibility layer in the first milestone.

## Reference baseline

The behavioral and visual reference is the local uni-app uview-plus demo checkout at:

```text
/Users/admin/Documents/Repos/xyito/open/uview-plus
```

The first implementation baseline is commit `96f14b06` on its `3.x` branch. The reference is pinned so a later upstream demo change does not silently change the Android acceptance target.

For every implemented component, the Android implementation must be compared with both:

1. its upstream uview-plus component source and documented default props; and
2. its corresponding demo page under `src/pages` in the pinned checkout.

## Non-goals

The following are intentionally out of scope for the first milestone:

- Parsing, storing, or rendering a FastView / `.xyfv` component tree at runtime.
- Implementing a backend JSON-to-Kotlin, JSON-to-SwiftUI, or JSON-to-uni-app compiler.
- Running uni-app inside a WebView.
- Providing a traditional XML/View-system public API.
- Claiming Android implementations for components outside the completed and tested component list.

These exclusions keep the native library focused while preserving the actual cross-platform requirement: generated Android source calls the same semantic props as generated uni-app and iOS source.

## Architecture

The project has two Gradle modules:

```text
ultra-ui-android/
├── ultra-ui/                    # Compose Android library
│   └── src/main/kotlin/net/lingyun/ultraui/android/
│       ├── core/                # Theme, config, units, color/style parsing, compatibility diagnostics
│       └── components/          # UP* components and their Props contracts
└── sample/                      # Android demo application
```

`ultra-ui` contains no demo-only logic. `sample` depends only on the public library API and demonstrates the same composition patterns generated Android code will use.

### Core layer

The core layer provides stable primitives shared by every component:

- `UPConfig`: uview-plus-aligned defaults, grouped by component.
- `UPTheme`: semantic colors, typography, border, radius, and disabled-state tokens.
- `UPColor`: safe parsing for named theme values and supported color strings such as hexadecimal RGB/RGBA forms.
- `UPUnit`: safe dimensions for numeric values and `rpx`, `px`, and `dp` strings. `rpx` resolves to `availableScreenWidthDp / 750f`, so a generated uni-app value retains its responsive intent on Android; `px` and `dp` resolve one-to-one to Compose `Dp`.
- `UPStyle`: supported `customStyle` properties such as size, spacing, color, background, border, radius, typography, alignment, and opacity. Unsupported web-only style declarations are safely ignored and can produce a debug compatibility diagnostic.
- `UPCompatibilityDiagnostics`: opt-in debug reporting for intentionally accepted but Android-inapplicable props, unknown enum strings, and unsupported style keys.

Unknown enum values and malformed colors/units must fall back to the same component-safe default rather than throw or crash a generated screen.

### Component layer

Each component is a concrete public Compose API in `net.lingyun.ultraui.android.components`, named with the existing cross-platform `UP` convention:

```kotlin
@Composable
fun UPButton(
    props: UPButtonProps = UPButtonProps(),
    onClick: (() -> Unit)? = null,
)
```

Every completed component also offers a concise direct-call overload for hand-written Compose. The complete `Props` contract is the stable code-generation target; its field names preserve uview-plus camelCase spellings such as `loadingText`, `closeOnClickOverlay`, `showCancelButton`, `customStyle`, and `zIndex`.

Example generated Android source:

```kotlin
UPButton(
    props = UPButtonProps(
        text = "主要按钮",
        type = "primary",
        shape = "circle",
        plain = false,
        loading = submitting,
    ),
    onClick = ::submit,
)
```

The component implementation resolves these compatibility values into internal Kotlin/Compose types. The generated source therefore stays close to the original JSON and does not need to know the native rendering details.

### Full prop acceptance and platform downgrade

A completed component's `Props` class accepts the upstream fields that the backend can emit for that component, including fields with no Android equivalent. This ensures the generated Android source remains compilable when the canonical JSON contains shared cross-platform props.

Examples of accepted but Android-inapplicable fields include mini-program and web integration settings such as `openType`, `formType`, `sessionFrom`, and `appParameter`. These fields have no visual or interaction effect on Android, remain part of the public contract, and emit a debug-only diagnostic when used. They never cause a crash or silently alter an applicable prop.

## State, events, and slots

### Model/state props

uview-plus model fields retain their names. Compose uses an explicit current value and update callback instead of Vue's `v-model`:

```kotlin
UPPopup(
    show = popupVisible,
    onShowChange = { popupVisible = it },
    mode = "bottom",
    closeOnClickOverlay = true,
)
```

Input-style components outside the first milestone use the same pattern when added: `modelValue` plus `onModelValueChange` when the uview-plus prop is `modelValue`.

### Events

uview-plus events map to typed Kotlin callbacks with conventional `on` prefixes:

- `@click` → `onClick`
- `@change` → `onChange`
- `@confirm` → `onConfirm`
- `@cancel` → `onCancel`
- `@close` → `onClose`

Callbacks retain relevant uview-plus payload semantics in typed event data classes where an event carries more than a simple click.

### Slots

Default and named slots map to Compose content lambdas. Named slots use the same semantic names in Kotlin camelCase, for example `icon`, `title`, `value`, `confirmButton`, and `popupBottom`. The direct Compose API does not require a JSON node or generic slot registry.

## Native implementation policy

The public behavior remains uview-plus-oriented while implementation uses Android-native Compose primitives wherever they offer the correct lifecycle and accessibility behavior:

- `UPButton`: Compose `Surface`/click interaction, with uview color, plain, hairline, loading, disabled, shape, and throttle behavior layered above it.
- `UPIcon`: a bundled copy of the pinned uview-plus `components/u-icon/upicon.ttf` asset with a maintained name-to-glyph catalog; icon name, color, size, label, and placement follow the uview contract.
- `UPLoadingIcon`: Compose animation and text layout matching the documented modes and vertical/horizontal behavior.
- `UPOverlay`: composable overlay layer with uview opacity, z-index, show, and click-close semantics.
- `UPPopup`: Compose `Popup` and dialog-window primitives according to `mode`, with controlled visibility, safe-area handling, overlay behavior, and slot content.
- `UPModal`: Android-native dialog window behavior wrapped in uview title/content/cancel/confirm/async-close/close-on-overlay semantics.
- `UPToast`: a library-provided Compose host and imperative controller; it does not delegate visual rendering to the system Android Toast because system Toast cannot preserve the uview layout or lifecycle contract.
- `UPTag`: native Compose layout with uview type, size, shape, plain, closable, and color behavior.
- `UPCellGroup` and `UPCell`: Compose list rows and dividers with uview title, label, value, arrow, icon, border, click, and slot semantics.

Native Android behavior must never leak Material defaults into the public visual contract when they differ from the corresponding uview-plus sample.

## First milestone component scope

The first milestone contains the foundation and the components needed to reproduce the selected uview-plus demo pages:

1. Core: `UPConfig`, `UPTheme`, `UPColor`, `UPUnit`, `UPStyle`, diagnostics, and shared demo layout primitives.
2. Display and action: `UPButton`, `UPIcon`, `UPLoadingIcon`, `UPTag`.
3. Layer and feedback: `UPOverlay`, `UPPopup`, `UPModal`, `UPToast` and its host/controller.
4. List/content: `UPCellGroup`, `UPCell`.

Each component ships only after its public props, defaults, events, UI behavior, source demo page, and tests are complete.

## Sample application

The Sample application is a functional Android recreation of the uview-plus demo rather than an independently designed Material showcase. It preserves the reference project's Chinese page titles, grouping, examples, and interaction states. Native Compose Navigation replaces uni-app route mechanics only; it does not change component demonstrations.

The first milestone mirrors these pages from the pinned reference tree:

| Reference uni-app page | Android sample screen | Required cases |
| --- | --- | --- |
| `componentsA/button/button.nvue` | 按钮 | Button types, plain, hairline, disabled, sizes, shapes, loading, click/throttle states. |
| `componentsA/icon/icon.nvue` | 图标 | Icon names, colors, sizes, labels, and placement. |
| `componentsA/loading-icon/loading-icon.nvue` | 加载中图标 | Loading modes, colors, text, sizes, and orientations. |
| `componentsA/overlay/overlay.nvue` | 遮罩层 | Visibility, opacity, click-close, z-index, and content layering. |
| `componentsA/popup/popup.nvue` | 弹窗 | Top/bottom/left/right/center modes, overlay, radius, close behavior, and content. |
| `componentsA/cell/cell.nvue` | 单元格 | Groups, title/label/value, icons, arrows, borders, and click responses. |
| `componentsB/toast/toast.nvue` | 提示消息 | Default/success/error/loading states, positions, duration, and overlay. |
| `componentsB/tag/tag.nvue` | 标签 | Types, plain, sizes, shapes, custom colors, and close behavior. |
| `componentsC/modal/modal.nvue` | 模态框 | Title/no-title, cancel, reverse buttons, asynchronous close, overlay close, custom slots, and custom buttons. |

The catalog layout uses the same Components A/B/C grouping for completed pages. A page is added to the navigation catalog only when its library component is implemented and tested; the sample does not pretend unsupported components are complete.

Images used solely as remote demo decoration are replaced with bundled, deterministic Android assets or a stable local placeholder, so the visual test target does not depend on network availability.

## Error handling and compatibility rules

- Never crash because a backend-generated string enum is unknown, a style value is malformed, or a platform-only prop is present.
- Resolve unknown `type`, `size`, `shape`, `mode`, and `position` values to documented component defaults.
- Keep state ownership explicit: dismissing a popup or modal calls its supplied state callback; it does not mutate an inaccessible internal copy of caller state.
- `asyncClose` keeps the modal open until the callback owner changes `show`; it never guesses when a network operation is complete.
- Suppress click and state events consistently when the component is disabled or loading, matching the corresponding uview-plus behavior.
- Preserve accessibility semantics and touch target requirements while matching uview-plus visual metrics.

## Testing strategy

Every component milestone includes all of the following evidence:

1. **Core unit tests**
   - Defaults, color/unit parsing, enum fallback, `rpx` conversion, `customStyle` mapping, and compatibility diagnostics.
2. **API-contract tests**
   - Props field coverage, default values, accepted compatibility-only fields, and event/model-state contracts used by generated code.
3. **Compose behavior tests**
   - Click, disabled, loading, throttle, `show` state updates, overlay dismissal, modal confirm/cancel/close, asynchronous close, and tag close events.
4. **Visual regression tests**
   - Deterministic golden scenarios based on each replicated uni-app page; colors, spacing, borders, radii, typography, loading state, overlay opacity, and popup/modal placement are checked.
5. **Sample integration tests**
   - Navigation reaches every completed sample page and every page exercises the public library API rather than demo-only implementation hooks.

No component is called complete solely because it renders. It must pass contract, behavior, visual, and demo integration coverage.

## Delivery sequence

1. Scaffold the Compose library and sample app using the fixed namespace.
2. Implement and verify the shared core layer and sample demo scaffolding.
3. Implement display/action components and their corresponding Components A/B sample pages.
4. Implement overlay/popup/modal/toast native wrappers and their corresponding pages.
5. Implement cell group/cell and its corresponding Components A page.
6. After each component batch, run contract, behavior, visual, and sample tests; compare against the pinned uni-app source before advancing.

This sequence deliberately keeps the component API contract and the uni-app demo replica advancing together, so backend-generated code always targets proven Android behavior.
