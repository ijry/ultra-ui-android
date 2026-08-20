# UltraUI Android Component Batch 9B Implementation Plan

> Batch continuation for the Android-native uview-plus compatibility library.

## Goal

Add the next 32 real uview-plus components under `net.lingyun.ultraui.android`, preserving camelCase Props names, raw number/string values, defaults, controlled aliases where applicable, and serializable callback payloads. The Android implementation consumes generated Kotlin Props; it does not parse JSON or run a cross-platform runtime.

## Scope

- Navigation/safe-area: `navbar`, `navbar-mini`, `status-bar`, `safe-bottom`
- Tabs/steps: `tabs`, `tabs-item`, `subsection`, `steps`, `steps-item`
- Lists/indexes: `list`, `list-item`, `index-list`, `index-item`, `index-anchor`, `scroll-list`
- Native popup/gesture: `popover`, `tooltip`, `sticky`, `swipe-action`, `swipe-action-item`
- Paging/status: `swiper`, `swiper-indicator`, `skeleton`, `read-more`, `column-notice`, `row-notice`
- Numeric/time: `count-to`, `count-down`
- Native picker: `picker`, `picker-column`
- Lightweight controls: `pagination`, `select`

`tabs-item`, `steps-item`, `index-item`, `picker-column` have no upstream public props in the pinned version beyond their documented empty contract; no speculative fields are added.

## Design

- Public `UP*Props` data classes use uview-plus field names and preserve raw generated values as `UPRawValue`.
- Compose renderers use foundation/UI primitives and the existing `UPStyle`, `UPUnit`, `UPColor`, icon, and test-tag helpers.
- Native Android equivalents are used where available: Compose foundation text/click/scroll primitives, inline popover and tooltip panels for deterministic embedding, `Dialog`-free inline picker panels, and gesture state for swipe actions. The API remains embeddable in generated screens rather than imposing a window-level overlay.
- Unsupported enums fall back through `upSafeEnum`; malformed dimensions/colors are non-fatal.
- Controlled state is emitted through `onUpdate*` callbacks and never silently replaced inside caller state.

## Verification checklist

1. Add Props contract tests, behavior tests, screenshot declarations, and project/sample catalog contract updates; run focused tests before implementation and record the expected red failure.
2. Implement the public Props and Compose APIs with shared native primitives.
3. Add a sample `导航与更多` route using only public APIs.
4. Generate/update screenshot references and review deterministic output.
5. Run toolchain check, unit tests, connected library tests, screenshot validation, sample build/instrumentation, and forbidden-boundary scan.
6. Commit the batch independently.

## Completion record

- Implemented all 32 components in this batch, including public Props contracts, Compose renderers, sample catalog entries, and the `导航与更多` sample route.
- Added Props and behavior coverage. The picker contract now resolves controlled `modelValue` before the legacy `value` alias and emits uview-compatible `value`, `values`, `indexs`, `index`, and `columnIndex` fields.
- Corrected `read-more` controlled-state semantics: `toggle` controls whether the close action remains visible after expansion; it does not mean initially expanded.
- Navigation icons use the bundled uview icon font through `UPIcon`; the sample uses explicit component imports.
- Android Studio JBR toolchain verification passed (`openjdk 21.0.10`, Android SDK platform 36); Java/Kotlin bytecode targets remain 17.
- Fresh full JVM unit tests and `:sample:assembleDebug` passed after the final compatibility fixes.
- Screenshot references were refreshed after adopting the uview icon font, and fresh screenshot validation passed.
- The forbidden-boundary scan found no FastView, `.xyfv`, WebView, or JSON runtime/dependency usage.
- Connected Android tests could not run because `adb devices` reported no connected devices.
