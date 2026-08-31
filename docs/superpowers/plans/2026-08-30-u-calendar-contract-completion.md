# UCalendar Contract Completion Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Make all 19 currently unread `UPCalendarProps` fields affect observable Android behavior while preserving the public Props and event APIs.

**Architecture:** Move calendar-specific calculations out of the Batch 10 utility file into focused pure Kotlin support types, then keep `UPCalendar` responsible only for Compose state and rendering. Model month generation, custom date metadata, range outcomes, result expansion, and time values as independently testable helpers; keep the lunar converter in a separate dependency-free file.

**Tech Stack:** Kotlin, Jetpack Compose, Java `Calendar`/`SimpleDateFormat`, JUnit 4, AndroidX Compose UI tests, and Compose Preview Screenshot Testing.

**Spec:** `docs/uview-plus-android-component-progress.md` plus the approved bounded design in the continuation of session 189.

## Global Constraints

- Do not change the public fields or defaults of `UPCalendarProps` or `UPCalendarEvent`.
- Treat `/Users/admin/Documents/Repos/xyito/open/uview-plus/src/uni_modules/uview-plus/components/u-calendar` and `/Users/admin/Documents/Repos/xyito/open/uview-plus/src/uni_modules/uview-plus/libs/util/calendar.js` as the pinned behavior references.
- Do not add a new dependency for date, time, or lunar conversion.
- Preserve the existing single/multiple/range behavior unless a tested upstream contract requires a change.
- Every production behavior starts with a focused failing JVM or Compose test and an observed RED result.
- Keep existing screenshot goldens unchanged; if new defaults alter the legacy screenshot scene, pin that preview to explicit legacy-compatible props rather than updating its golden.
- Do not commit or create a branch; the current `main` workspace intentionally contains prior uncommitted compatibility work.

---

### Task 1: Add calendar models and month metadata helpers

**Files:**
- Create: `ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/components/UPCalendarSupport.kt`
- Modify: `ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/components/UPBatch10Support.kt:14`
- Modify: `ultra-ui/src/test/kotlin/net/lingyun/ultraui/android/components/UPBatch10PropsTest.kt:143`

**Interfaces:**
- Produces: `UPCalendarMonth`, `UPCalendarDayMetadata`, `calendarMonthSequence`, `calendarCustomMetadata`, `calendarMonthTitle`, and calendar date parsing/formatting helpers.
- Consumes: `UPCalendarProps`, raw `customList` maps, and the existing `rawInt` conversions.

- [ ] **Step 1: Write failing JVM tests for month generation and custom metadata**

Add literal expectations proving that `monthNum=3` generates August through October 2026, `monthSwitch=true` limits the visible sequence to one month, `monthFormat` replaces `YYYY`/`MM`, and `customList` merges `topInfo`, `bottomInfo`, `dot`, and `disabled` for an exact date.

- [ ] **Step 2: Run the focused JVM test and verify RED**

Run:

```bash
./gradlew :ultra-ui:testDebugUnitTest --tests 'net.lingyun.ultraui.android.components.UPBatch10PropsTest' --console=plain
```

Expected: compilation or assertion failure because the new calendar model helpers do not exist.

- [ ] **Step 3: Implement the minimum pure Kotlin helpers**

Create small immutable calendar model types, parse raw maps defensively, normalize month counts to at least one, and format month titles without locale-dependent test output. Move the existing `updateCalendarSelection` and `calendarDateAllowed` declarations from `UPBatch10Support.kt` into the new calendar support file without changing behavior yet.

- [ ] **Step 4: Re-run the focused JVM test and verify GREEN**

Run the Task 1 command and require a clean PASS.

### Task 2: Implement range limits, prompts, and result modes

**Files:**
- Modify: `ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/components/UPCalendarSupport.kt`
- Modify: `ultra-ui/src/test/kotlin/net/lingyun/ultraui/android/components/UPBatch10PropsTest.kt`
- Modify: `ultra-ui/src/androidTest/kotlin/net/lingyun/ultraui/android/components/UPBatch10BehaviorTest.kt:22`

**Interfaces:**
- Produces: `UPCalendarSelectionOutcome`, `resolveCalendarSelection`, `calendarResultDates`, and `calendarForbiddenPrompt`.
- Consumes: selected ISO dates, `maxRange`, `rangePrompt`, `showRangePrompt`, `rangeResultMode`, `forbidDays`, and `forbidDaysToast`.

- [ ] **Step 1: Write failing JVM tests for range decisions**

Cover inclusive maximum-range counting, rejected over-limit selections, default prompt fallback, hidden prompts, `boundary` results, and `all` results expanded to every ISO date in the selected interval.

- [ ] **Step 2: Run the focused JVM test and verify RED**

Use the Task 1 JVM command. Expected: failures naming the missing outcome and result helpers.

- [ ] **Step 3: Implement minimal range and prompt helpers**

Keep selection state unchanged when a range exceeds `maxRange`; return a prompt only when configured to show one. Expand range results only when emitting events, leaving the two selected boundaries as the internal state.

- [ ] **Step 4: Write a failing Compose test for user-visible prompts**

Render a range calendar with a two-day maximum, tap an over-limit end date, and assert the prompt tag exists. Render a forbidden date with `forbidDaysToast`, tap it, and assert the configured message is visible.

- [ ] **Step 5: Run the targeted device test and verify RED**

Run:

```bash
./gradlew :ultra-ui:connectedDebugAndroidTest \
  -Pandroid.testInstrumentationRunnerArguments.class=net.lingyun.ultraui.android.components.UPBatch10BehaviorTest \
  --console=plain
```

Expected: the new prompt assertions fail because `UPCalendar` does not render prompt state.

### Task 3: Implement calendar time values and result formatting

**Files:**
- Modify: `ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/components/UPCalendarSupport.kt`
- Modify: `ultra-ui/src/test/kotlin/net/lingyun/ultraui/android/components/UPBatch10PropsTest.kt`
- Modify: `ultra-ui/src/androidTest/kotlin/net/lingyun/ultraui/android/components/UPBatch10BehaviorTest.kt`

**Interfaces:**
- Produces: `UPCalendarTime`, `calendarInitialTimes`, `calendarTimeParts`, `updateCalendarTime`, and time-aware calendar event values.
- Consumes: `enableTime`, `timePrecision`, `defaultTime`, calendar mode, and boundary/all result mode.

- [ ] **Step 1: Write failing JVM tests for time parsing and formatting**

Use hand-derived fixtures for `hour`, `minute`, and `second` precision; malformed values must safely fall back to zeroed missing parts. Cover one time for single mode and separate start/end times for range boundary mode.

- [ ] **Step 2: Run the focused JVM test and verify RED**

Use the Task 1 JVM command. Expected: failures for missing time helpers.

- [ ] **Step 3: Implement minimum time helpers**

Normalize values to 24-hour ranges, expose only the precision-requested parts, and append time text only when `enableTime=true`. Keep range `all` date expansion date-only, matching the upstream time-panel restriction.

- [ ] **Step 4: Write a failing Compose time-panel test**

Render a single calendar with `enableTime=true`, confirm the `defaultTime`, change one visible time part, and assert the emitted value includes the updated normalized time.

- [ ] **Step 5: Run the targeted device test and verify RED**

Use the Task 2 device command. Expected: missing time controls or unchanged event value.

### Task 4: Render months, date metadata, time controls, and lunar labels

**Files:**
- Create: `ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/components/UPCalendarLunar.kt`
- Modify: `ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/components/UPCalendarComponents.kt:36`
- Modify: `ultra-ui/src/androidTest/kotlin/net/lingyun/ultraui/android/components/UPBatch10BehaviorTest.kt`
- Modify if required to preserve the current golden: `ultra-ui/src/screenshotTest/kotlin/net/lingyun/ultraui/android/components/Batch10Screenshots.kt:24`

**Interfaces:**
- Produces: `calendarLunarLabel(year, month, day)`, multi-month rendering, month switching, today navigation/highlight, custom top/bottom/dot content, range boundary labels, month watermark, prompt surface, and time controls.
- Consumes: every helper from Tasks 1-3 and the 19 target Props fields.

- [ ] **Step 1: Write failing lunar conversion tests**

Use known literal dates around Lunar New Year and a normal mid-month day. Include lower/upper unsupported-year cases that return an empty label instead of throwing.

- [ ] **Step 2: Run the focused JVM test and verify RED**

Use the Task 1 JVM command. Expected: failure because `calendarLunarLabel` is absent.

- [ ] **Step 3: Port the minimum upstream lunar algorithm**

Copy only the 1900-2100 year table and solar-to-lunar operations required to produce `IMonthCn`/`IDayCn`; expose a single internal label function and add no Android dependencies.

- [ ] **Step 4: Add failing Compose tests for the remaining visual contracts**

Assert observable tags/text for `monthNum`, `monthSwitch`, `monthFormat`, `rowHeight`, `showMark`, `showToday`, `todayColor`, `customList`, `startText`, `endText`, and `showLunar`. Each test must identify the production branch it protects rather than merely checking source text.

- [ ] **Step 5: Implement the Compose rendering slice**

Render one fixed-height scroll viewport for multi-month mode and a single month with arrows for switch mode. Preserve the old compact default cell height by treating upstream `rowHeight=56` as the native baseline and scaling from it. Add stable `upTestTag` values for month containers, metadata, marks, today navigation, prompts, and time controls.

- [ ] **Step 6: Run focused JVM and device tests until GREEN**

Run the Task 1 and Task 2 commands. Require all calendar tests to pass before refactoring.

- [ ] **Step 7: Refactor only after GREEN**

Extract small private composables for header, month grid, day cell, prompt, and time editor if needed; rerun both focused suites after each extraction.

### Task 5: Verify regressions and update compatibility evidence

**Files:**
- Modify: `docs/uview-plus-android-component-progress.md:48`

**Interfaces:**
- Consumes: completed calendar behavior and audit outputs.
- Produces: accurate component status, test evidence, unread-field count, and known-degradation notes.

- [ ] **Step 1: Run formatting and diff checks**

```bash
git diff --check
```

- [ ] **Step 2: Run all JVM tests and sample build**

```bash
./gradlew :ultra-ui:testDebugUnitTest :sample:assembleDebug --console=plain
```

- [ ] **Step 3: Run all device behavior tests**

```bash
./gradlew :ultra-ui:connectedDebugAndroidTest --console=plain
```

- [ ] **Step 4: Validate screenshots without updating goldens**

```bash
./gradlew :ultra-ui:validateDebugScreenshotTest --console=plain
```

- [ ] **Step 5: Run compatibility audits**

```bash
python3 tools/compare_uview_defaults.py
python3 tools/audit_status_claims.py --all
```

The status audit may still exit non-zero because other backlog remains; verify that `u-calendar` no longer appears in the unread-field list and that its documented status matches evidence.

- [ ] **Step 6: Update the progress document**

Record implemented semantics, exact new test totals, unread-field total, optimistic-status total, and any deliberate Android degradation. Do not claim completion for behavior not exercised by JVM or device tests.
