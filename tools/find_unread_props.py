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
        blob = "\n".join(stripped[f] for f in files)
        # If the entry hands the whole props object onward, the read may live anywhere.
        if re.search(r"\(\s*props\s*[,)]|=\s*props\b|\bprops\s*,", blob):
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
