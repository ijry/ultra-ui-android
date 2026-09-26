#!/usr/bin/env python3
"""Report UP*Props fields that no component ever reads.

A generated Props class is a contract: the backend sets a field and expects an
effect. A field the component never reads is a silent no-op — it type-checks, it
passes props tests, and no screenshot changes, so nothing catches it. Both the
u-tabbar-item and u-steps-item `iconSize` bugs had exactly this shape.

A field counts as read when `props.<field>` (or a named argument forwarding it)
appears in any declaration that actually receives this component's Props type: its
composable, plus every helper, context class or support function typed against it.
Scoping to the type instead of widening to the whole library matters — the earlier
"whole library once props is forwarded" rule let `u-picker`'s `props.duration` stand
in for `u-select`'s and `u-popup`'s, hiding 48 genuinely unread fields.

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
    # `menu` has no counterpart in upstream u-dropdown/props.js; it is an Android-only
    # alias kept so older generated templates keep compiling.
    "Dropdown.menu": "Android-only compatibility alias; no upstream field",
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
    "CollapseItem.cellCustomClass": "CSS class name; no Compose equivalent",
    "ActionSheet.index": "Android-only compatibility alias; upstream resolves items by position",
    "ActionSheet.openType": "wechat open-ability button only (getUserInfo/contact/launchApp)",
    # Declared upstream but never read there either: u-list.vue has no `this.pagingEnabled`,
    # and `preLoadScreen` only feeds a `show` flag that u-list-item.vue computes and then
    # never binds to its template, so no item is ever actually skipped.
    "List.pagingEnabled": "upstream u-list.vue never reads the prop",
    "List.preLoadScreen": "upstream u-list-item.vue computes `show` from it but never binds it",
    # Same uni-app keyboard plumbing already registered for u-input/u-textarea: both are
    # forwarded straight to `<input>` upstream, and Android leaves them to the IME.
    "CodeInput.adjustPosition": "uni-app pushes the page up; Android uses windowSoftInputMode",
    "Search.adjustPosition": "uni-app pushes the page up; Android uses windowSoftInputMode",
    "Search.autoBlur": "uni-app App 3.0.0+ only (仅App3.0.0+有效)",
    # Declared in props.js but absent from the template: the parent u-dropdown closes the
    # menu through its own `closeOnClickMask`, and u-form-item only mentions `rightIcon`
    # in its doc comment.
    "DropdownItem.closeOnClickOverlay": "upstream u-dropdown-item.vue never reads it; the parent's closeOnClickMask closes the menu",
    "FormItem.rightIcon": "upstream u-form-item.vue declares it but never renders it",
    "Choose.valueName": "upstream up-choose emits the index, never reads valueName",
    "Coupon.circle": "upstream ternary is `circle ? 'circle' : 'circle'`; the shape is always circle",
    "LazyLoad.effect": "upstream hardcodes `ease-in-out` in the transition and never reads effect",
    "Guide.once": "local-storage show-once memory; persistence belongs to the host",
    "Guide.storageKey": "local-storage key for the show-once memory; persistence belongs to the host",
    "CityLocate.locationType": "geo coordinate system for uni.getLocation; the host owns the lookup",
    "TabsPro.contentMode": "upstream declares static/other but the template never branches on it",
    "Dragsort.columns": "only meaningful for the all/grid direction; the vertical port never reads it",
    "Dragsort.vibrate": "haptic feedback on drag; belongs to the host",
    "Waterfall.addTime": "staggered per-item append interval; the port lays out all at once",
    "Waterfall.idKey": "identifies items for upstream remove(id); callers edit modelValue instead",
    "VirtualList.buffer": "manual overscan hint; LazyColumn manages its own recycling window",
    "Qrcode.cid": "H5/App canvas element id; Android has no canvas node to bind",
    "Qrcode.onval": "auto-redraw-on-val-change flag; Compose recomposes on prop change automatically",
    "Qrcode.loadMake": "generate-on-mount flag; the port always renders synchronously when shown",
    "Qrcode.usingComponents": "H5 custom-component draw-delay hint; there is no async canvas draw on Android",
    "Qrcode.showLoading": "loading overlay while the canvas draws async; matrix generation is synchronous here",
    "Qrcode.loadingText": "text for the async-draw loading overlay; no such loading phase on Android",
    "Barcode.fontOptions": "upstream stores this font-style string but never applies it to the canvas",
    "Barcode.useCanvas": "the port always paints to a Compose canvas; the image-file branch needs host export",
    "Parse.errorImg": "custom error-placeholder image URL; the host UPImageLoader owns (remote) image loading",
    "Parse.loadingImg": "custom loading-placeholder image URL; the host UPImageLoader owns (remote) image loading",
    "Parse.pauseVideo": "auto-pause other videos on play; the port renders no video players",
    "Parse.setTitle": "sets the page <title>; no page-title concept on Android in-tree",
    "Parse.showImgMenu": "WeChat long-press image save menu; no equivalent on Android",
    "Parse.useAnchor": "in-page #anchor jump; the port renders a flat block list without anchors",
    "Parse.scrollTable": "per-table horizontal scroll layer; tables render inline without their own scroller",
    "Table2.lazy": "lazy tree child loading; the port renders the flat data list only",
    "Table2.treeProps": "tree children/hasChildren keys; tree expansion is not modelled",
    "Table2.defaultExpandAll": "tree expand-all; tree expansion is not modelled",
    "Table2.expandRowKeys": "initially expanded tree rows; tree expansion is not modelled",
    "Table2.mainCol": "column hosting the tree expand icon; tree expansion is not modelled",
    "Table2.expandWidth": "width of the tree expand-icon column; tree expansion is not modelled",
    "Table2.multiSort": "multi-column sort; the port sorts a single column at a time",
    "Table2.sortBy": "custom sort field/accessor; the port sorts by the column key",
    "Table2.filters": "per-column filter UI; not modelled",
    "Table2.showOverflowTooltip": "hover tooltip for overflowing cells; no hover on touch",
    "Upload.accept": "native media/file picker filter; opening the picker is a host concern",
    "Upload.extension": "picker file-extension filter; handled by the host picker",
    "Upload.capture": "album/camera picker source; handled by the host picker",
    "Upload.compressed": "video compression option for the native picker; host concern",
    "Upload.camera": "front/back camera for the native recorder; host concern",
    "Upload.maxDuration": "max video record duration for the native recorder; host concern",
    "Upload.sizeType": "original/compressed image size for the native picker; host concern",
    "Upload.multiple": "multi-select in the native picker; host concern",
    "Upload.maxSize": "per-file byte limit enforced while reading files; host concern",
    "Upload.useBeforeRead": "enables the beforeRead hook; file reading is a host concern",
    "Upload.autoDelete": "auto-remove a failed item after upload; host upload engine concern",
    "Upload.autoUpload": "auto-upload after selection; the upload engine is a host concern",
    "Upload.autoUploadApi": "auto-upload endpoint; the upload engine is a host concern",
    "Upload.autoUploadDriver": "auto-upload driver (local/oss/cos/kodo); host concern",
    "Upload.autoUploadAuthUrl": "auto-upload signing endpoint; host concern",
    "Upload.autoUploadHeader": "auto-upload request headers; host concern",
    "Upload.customAfterAutoUpload": "custom post-auto-upload handling; host concern",
    "Upload.getVideoThumb": "local video thumbnail extraction; host media concern",
    "Upload.videoPreviewObjectFit": "video preview object-fit; the port previews images, not video",
    # These sheets still render inline, so there is no full-screen scrim to tint, size or
    # dismiss. This is unfinished work rather than a platform limit: `u-tooltip` and
    # `u-popover` now use `androidx.compose.ui.window.Popup`, which proves the layer is
    # available. Lifting each sheet into it is a per-component change, not a per-field one,
    # so the fields stay registered here until their sheet moves.
    "Picker.closeOnClickOverlay": "still inline; no scrim to dismiss until the sheet moves into a Popup",
    "Picker.overlayOpacity": "still inline; no scrim to tint until the sheet moves into a Popup",
    "DatetimePicker.closeOnClickOverlay": "still inline; no scrim to dismiss until the sheet moves into a Popup",
    "Calendar.closeOnClickOverlay": "still inline; no scrim to dismiss until the sheet moves into a Popup",
    "Calendar.overlay": "still inline; no scrim to show until the sheet moves into a Popup",
    "Calendar.overlayOpacity": "still inline; no scrim to tint until the sheet moves into a Popup",
    "Calendar.overlayStyle": "still inline; no scrim to style until the sheet moves into a Popup",
    "Select.overlay": "still inline; no scrim to show until the panel moves into a Popup",
    "Select.overlayOpacity": "still inline; no scrim to tint until the panel moves into a Popup",
    "Select.overlayStyle": "still inline; no scrim to style until the panel moves into a Popup",
    # Declared in u-datetime-picker/props.js but never used by the component: the template
    # forwards its own `innerDefaultIndex` (computed from the value) rather than the prop,
    # and `loading` appears only in the doc comment — it is never handed to `<u-picker>`.
    "DatetimePicker.defaultIndex": "upstream forwards innerDefaultIndex, never the prop",
    "DatetimePicker.loading": "upstream u-datetime-picker.vue never passes it to u-picker",
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


DECLARATION_BOUNDARY = re.compile(
    r"\n(?=@Composable|@[A-Za-z]+\n|public fun |private fun |internal fun "
    r"|public class |private class |internal class |public data class "
    r"|internal data class |private data class |public object |internal object "
    r"|public val |internal val |private val )"
)


def typed_scopes(sources: dict[Path, str], name: str) -> str:
    """Every declaration that receives an UP<name>Props value.

    Several components share one file (UPSwiper and UPCountTo both live in
    UPStatusNumericComponents.kt), so the file as a whole is too wide: a sibling's
    `props.autoplay` would mask UPSwiper's own unread `autoplay`. Widening to the
    whole library the moment a component forwards `props` onward was worse still —
    it let one component's `duration` vouch for every other one's.

    Scoping by type keeps the forwarding case working (a helper declared as
    `fun foo(props: UPSelectProps)` is included) without borrowing evidence from
    unrelated components.
    """
    needle = f"UP{name}Props"
    typed = re.compile(rf":\s*{needle}\b")
    out = []
    for src in sources.values():
        if needle not in src:
            continue
        for chunk in DECLARATION_BOUNDARY.split(src):
            if typed.search(chunk):
                out.append(chunk)
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
        blob = typed_scopes(stripped, name)
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
