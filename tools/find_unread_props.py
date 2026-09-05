#!/usr/bin/env python3
"""Report UP*Props fields that no component ever reads.

A generated Props class is a contract: the backend sets a field and expects an
effect. A field the component never reads is a silent no-op — it type-checks, it
passes props tests, and no screenshot changes, so nothing catches it. Both the
u-tabbar-item and u-steps-item `iconSize` bugs had exactly this shape.

Resolution is deliberately generous. A field counts as read if `props.<field>` or
`.<field>` appears anywhere in the file defining its component's entry point, or —
when that entry forwards the whole `props` object onward — anywhere in the library.
That undercounts rather than overcounts, so what it does report is worth reading.

Fields inert on Android by design (uni-app / WeChat-only knobs kept for interface
compatibility) live in KNOWN_INERT with the reason.
"""

from __future__ import annotations

import argparse
import re
import sys
from collections import defaultdict
from pathlib import Path

REPO = Path(__file__).resolve().parent.parent
DEFAULT_MAIN = REPO / "ultra-ui/src/main/kotlin/net/lingyun/ultraui/android"

KNOWN_INERT: dict[str, str] = {
    "Button.lang": "wechat open-data only",
    "Button.sessionFrom": "wechat customer-service only",
    "Button.sendMessageTitle": "wechat share payload only",
    "Button.sendMessagePath": "wechat share payload only",
    "Button.sendMessageImg": "wechat share payload only",
    "Button.showMessageCard": "wechat share payload only",
    "Button.dataName": "wechat dataset attribute only",
    "Button.hoverStartTime": "uni-app hover timing; no Compose equivalent",
    "Button.hoverStayTime": "uni-app hover timing; no Compose equivalent",
    "Avatar.mpAvatar": "mini-program avatar node only",
    "Image.lazyLoad": "uni-app lazy-load attribute",
    "Image.showMenuByLongpress": "wechat long-press menu only",
    "Image.webp": "uni-app decoder hint; Android decodes webp natively",
    "Input.confirmHold": "uni-app keyboard retention flag",
    "Textarea.showConfirmBar": "uni-app keyboard confirm bar",
    "Toast.isTab": "uni-app tabbar-aware positioning",
    "Toast.params": "uni-app navigation payload passthrough",
    "List.showScrollbar": "nvue only per upstream (仅nvue有效)",
    "List.offsetAccuracy": "nvue only per upstream (仅nvue有效)",
    "List.enableFlex": "wechat mini-program only (仅微信小程序有效)",
    "List.enableBackToTop": "wechat mini-program only (只对微信小程序有效)",
    "Swiper.easingFunction": "wechat mini-program only (只对微信小程序有效)",
    # Keyboard/viewport plumbing owned by the uni-app runtime. On Android the platform
    # handles these via windowSoftInputMode and the IME itself, so the props stay inert.
    "Input.adjustPosition": "uni-app pushes the page up; Android uses windowSoftInputMode",
    "Input.autoBlur": "uni-app App 3.0.0+ only (仅App3.0.0+有效)",
    "Input.cursorSpacing": "uni-app keyboard spacing hint; no Compose equivalent",
    "NumberBox.cursorSpacing": "uni-app keyboard spacing hint; no Compose equivalent",
    "Input.disableDefaultPadding": "wechat + type=textarea only (仅微信小程序)",
    "Input.fixed": "mini-program position:fixed hint (微信/百度/字节/QQ)",
    "Input.holdKeyboard": "wechat mini-program only (微信小程序有效)",
    "Input.ignoreCompositionEvent": "uni-app IME composition passthrough",
    "Input.placeholderClass": "CSS class name; no Compose equivalent",
    "Textarea.adjustPosition": "uni-app pushes the page up; Android uses windowSoftInputMode",
    "Textarea.cursorSpacing": "uni-app keyboard spacing hint; no Compose equivalent",
    "Textarea.disableDefaultPadding": "wechat + type=textarea only (仅微信小程序)",
    "Textarea.fixed": "mini-program position:fixed hint (微信/百度/字节/QQ)",
    "Textarea.holdKeyboard": "wechat mini-program only (微信小程序有效)",
    "Textarea.ignoreCompositionEvent": "uni-app IME composition passthrough",
    "Textarea.placeholderClass": "CSS class name; no Compose equivalent",
    # uview's `stop` calls preventEvent to stop DOM bubbling. Compose click handlers do
    # not propagate to ancestors, so there is nothing to suppress.
    "Button.stop": "DOM event-bubbling guard; Compose clicks do not propagate",
    "Cell.stop": "DOM event-bubbling guard; Compose clicks do not propagate",
    "Icon.stop": "DOM event-bubbling guard; Compose clicks do not propagate",
    # uni-app navigation helpers: the component performs the route jump itself upstream.
    # On Android routing belongs to the host, so these stay as passthrough metadata.
    "Cell.url": "uni-app navigateTo target; routing belongs to the host app",
    "Cell.linkType": "uni-app navigation method; routing belongs to the host app",
    "NoticeBar.url": "uni-app navigateTo target; routing belongs to the host app",
    "NoticeBar.linkType": "uni-app navigation method; routing belongs to the host app",
    "Toast.url": "uni-app navigateTo target; routing belongs to the host app",
    "Toast.back": "uni-app navigateBack flag; routing belongs to the host app",
    # `scrolling` is uview's v-model flag for pausing an outer scroll-view mid-drag. The
    # Android side reports it via onUpdateScrolling; consuming it as an input would mean
    # driving the host's scroll container, which belongs to the host.
    "SwipeActionItem.scrolling": "outbound v-model flag; reported via onUpdateScrolling",
    # tooltip/popover render inline next to their trigger rather than in a window-level
    # overlay, so there is no stacking context to apply these to. Lifting them into a
    # real overlay is tracked as the window-level popup gap, not a per-field fix.
    "Tooltip.zIndex": "inline render; no window-level overlay to stack in",
    "Tooltip.overlay": "inline render; no window-level overlay to stack in",
    "Tooltip.singleton": "needs a window-level popup registry",
    "Tooltip.showToast": "copy feedback toast is the host app's call",
    "Tooltip.forcePosition": "absolute repositioning needs a window-level overlay",
    "Popover.zIndex": "inline render; no window-level overlay to stack in",
    "Popover.forcePosition": "absolute repositioning needs a window-level overlay",
    # `menu` has no counterpart in upstream u-dropdown/props.js; it is an Android-only
    # alias kept so older generated templates keep compiling.
    "Dropdown.menu": "Android-only compatibility alias; no upstream field",
    # Anchor stickiness needs the surrounding scroll container to report its offset back
    # to the list. Tracked with the same real-scroll-sticky gap as u-sticky.
    "IndexList.sticky": "anchor sticking needs scroll-position feedback from the host",
    # u-form declares borderBottom but only a dead propsChange computed exposes it;
    # u-form-item never reads the parent value, so the flag stays inert.
    "Form.borderBottom": "upstream u-form-item never reads the parent flag",
    # Declared upstream but never read there either: u-radio.vue has no `this.color`,
    # u-radio-group.vue/u-checkbox-group.vue never touch `name`/`label`, and neither
    # appears in the `parentData` keys the children copy from their group. Implementing
    # them on Android would invent behaviour uview-plus does not have.
    "Radio.color": "upstream u-radio.vue never reads the prop",
    "RadioGroup.label": "upstream u-radio-group.vue never renders the prop",
    "RadioGroup.name": "upstream u-radio-group.vue never reads the prop",
    "CheckboxGroup.name": "upstream u-checkbox-group.vue never reads the prop",
    "Sticky.index": "upstream u-sticky.vue never reads the prop; only a caller-side tag",
    "CollapseItem.cellCustomClass": "CSS class name; no Compose equivalent",
    "ActionSheet.index": "Android-only compatibility alias; upstream resolves items by position",
    "ActionSheet.openType": "wechat open-ability button only (getUserInfo/contact/launchApp)",
}


def load_sources(main: Path) -> dict[Path, str]:
    return {p: p.read_text(encoding="utf-8") for p in sorted(main.rglob("*.kt"))}


def find_props_classes(sources: dict[Path, str]) -> dict[str, list[str]]:
    classes = {}
    for src in sources.values():
        for match in re.finditer(r"public data class UP([A-Za-z0-9]+)Props\((.*?)\n\)", src, flags=re.S):
            classes[match.group(1)] = [
                m.group(1) for m in re.finditer(r"val\s+([A-Za-z0-9_]+)\s*:", match.group(2), flags=re.M)
            ]
    return classes


def find_entry_files(sources: dict[Path, str]) -> dict[str, set[Path]]:
    entries: dict[str, set[Path]] = defaultdict(set)
    pattern = re.compile(r"public fun (?:[A-Za-z]+Scope\.)?UP([A-Za-z0-9]+)\s*\(")
    for path, src in sources.items():
        for match in pattern.finditer(src):
            entries[match.group(1)].add(path)
    return entries


def strip_props_declarations(src: str) -> str:
    """Drop `data class UP*Props(...)` bodies so `val field:` lines aren't self-matches."""
    return re.sub(r"public data class UP[A-Za-z0-9]+Props\(.*?\n\)", "", src, flags=re.S)


def reads_field(blob: str, field: str) -> bool:
    escaped = re.escape(field)
    # props.field / it.field / item.field, or a named argument forwarding it on.
    return bool(
        re.search(rf"\.{escaped}\b", blob) or re.search(rf"\b{escaped}\s*=\s*[^,)\n]", blob)
    )


def component_bodies(sources: dict[Path, str], name: str) -> str:
    """Extract just the UP<name> composable bodies, not the whole file.

    Several components share one file (UPSwiper and UPCountTo both live in
    UPStatusNumericComponents.kt). Searching the file as a whole let a sibling's
    `props.autoplay` mask UPSwiper's own unread `autoplay`, so scope the text to the
    function that actually receives this component's props.
    """
    out = []
    pattern = re.compile(
        rf"public fun (?:[A-Za-z]+Scope\.)?UP{re.escape(name)}\s*\(", re.M
    )
    for src in sources.values():
        for match in pattern.finditer(src):
            start = match.start()
            # Walk to the matching close of the parameter list, then take the body up to
            # the next top-level declaration.
            rest = src[start:]
            nxt = re.search(r"\n@Composable|\npublic fun |\nprivate fun |\ninternal fun ", rest[1:])
            out.append(rest[: nxt.start() + 1] if nxt else rest)
    return "\n".join(out)


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--main", type=Path, default=DEFAULT_MAIN, help="library main source root")
    parser.add_argument("--show-inert", action="store_true", help="also list documented inert fields")
    args = parser.parse_args()

    if not args.main.is_dir():
        print(f"source root not found: {args.main}", file=sys.stderr)
        return 2

    sources = load_sources(args.main)
    stripped = {p: strip_props_declarations(s) for p, s in sources.items()}
    whole_library = "\n".join(stripped.values())
    classes = find_props_classes(sources)
    entries = find_entry_files(sources)

    unread: dict[str, list[str]] = defaultdict(list)
    inert: list[tuple[str, str]] = []
    missing_entry: list[str] = []

    for name, fields in sorted(classes.items()):
        files = entries.get(name)
        if not files:
            missing_entry.append(name)
            continue
        blob = component_bodies(stripped, name)
        # Widen to the whole library only when the component really forwards its entire
        # props object onward. `= props` alone is too loose: it also matches
        # `current = props.current`, which is a field read, not a forward.
        if re.search(r"\(\s*props\s*[,)]|=\s*props\s*[,)\n]|\bprops\s*,\s*\w", blob):
            blob = whole_library
        for field in fields:
            if reads_field(blob, field):
                continue
            key = f"{name}.{field}"
            if key in KNOWN_INERT:
                inert.append((key, KNOWN_INERT[key]))
            else:
                unread[name].append(field)

    total = sum(len(v) for v in unread.values())
    print(f"Props classes analysed : {len(classes)}")
    print(f"without an entry point : {len(missing_entry)}")
    print(f"documented inert       : {len(inert)}")
    print(f"UNREAD fields          : {total}")

    if args.show_inert and inert:
        print("\ndocumented inert (kept for interface compatibility):")
        for key, reason in sorted(inert):
            print(f"  {key:38s} {reason}")

    if unread:
        print("\nfields no component reads (each is a silent no-op):")
        for name in sorted(unread):
            print(f"  UP{name}Props: {', '.join(sorted(unread[name]))}")
        return 1
    return 0


if __name__ == "__main__":
    sys.exit(main())
