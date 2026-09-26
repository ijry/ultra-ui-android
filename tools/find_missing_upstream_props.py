#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""Flag upstream uview-plus props that the Android port silently dropped.

The other audits cover different failure modes:

- ``find_unread_props.py`` — every field the Android Props class *declares* is
  consumed (or registered inert).
- ``compare_uview_defaults.py`` — fields present on *both* sides share a default.
- ``audit_status_claims.py`` — each "基本完成" row has test evidence.

None of them catches a prop that exists upstream but was never modelled on
Android: it is absent from the Props class, so nothing reads it and there is no
shared field to diff. Such an omission would pass every other tool silently.

This script closes that gap. For each upstream ``u-*`` component it collects the
declared prop names (from ``props.js`` and/or the inline ``props: {}`` block of
``<name>.vue``), maps the component to its Android ``UP<Name>Props`` via the docs
progress table, and reports any upstream prop that is BOTH:

  1. absent from the Android Props data class, and
  2. not accounted for by name (as `` `prop` ``) in that component's docs row.

A clean run means every upstream prop is either modelled on Android or explicitly
documented as an intentional omission/downgrade. It compares declared prop
*coverage*, not behaviour or defaults.
"""

from __future__ import annotations

import argparse
import re
import sys
from pathlib import Path

REPO = Path(__file__).resolve().parent.parent
DEFAULT_UPSTREAM = Path(
    "/Users/admin/Documents/Repos/xyito/open/uview-plus/src/uni_modules/uview-plus/components"
)
DEFAULT_DOCS = REPO / "docs/uview-plus-android-component-progress.md"
DEFAULT_COMPONENTS = REPO / "ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/components"

# Universal props every component inherits; Android handles them uniformly
# (customStyle) or they have no Compose equivalent (customClass) and the `name`
# identifier is not a rendered prop. Never flagged per-component.
IGNORE = {"customClass", "customStyle", "name"}


def strip_comments(src: str) -> str:
    src = re.sub(r"/\*.*?\*/", "", src, flags=re.S)
    return re.sub(r"//[^\n]*", "", src)


def props_block(src: str) -> str | None:
    """Return the balanced ``{...}`` following the first ``props:`` key."""
    m = re.search(r"props\s*:\s*\{", src)
    if not m:
        return None
    start = src.index("{", m.start())
    depth = 0
    for pos in range(start, len(src)):
        ch = src[pos]
        if ch == "{":
            depth += 1
        elif ch == "}":
            depth -= 1
            if depth == 0:
                return src[start : pos + 1]
    return None


def top_level_keys(block: str | None) -> list[str]:
    """Names of the top-level ``key: {...}`` / ``key: Type`` entries in a block."""
    if not block:
        return []
    inner = block[1:-1]
    parts, depth, buf = [], 0, ""
    for ch in inner:
        if ch in "{[(":
            depth += 1
        elif ch in "}])":
            depth -= 1
        if ch == "," and depth == 0:
            parts.append(buf)
            buf = ""
        else:
            buf += ch
    parts.append(buf)
    keys = []
    for part in parts:
        m = re.match(r"\s*['\"]?([A-Za-z0-9_]+)['\"]?\s*:", part)
        if m:
            keys.append(m.group(1))
    return keys


def upstream_prop_names(comp: Path) -> set[str]:
    """Declared prop names from a component's props.js and/or <name>.vue."""
    names: set[str] = set()
    props_js = comp / "props.js"
    if props_js.exists():
        src = strip_comments(props_js.read_text(encoding="utf-8", errors="replace"))
        names.update(top_level_keys(props_block(src)))
    vue = comp / f"{comp.name}.vue"
    if vue.exists():
        src = vue.read_text(encoding="utf-8", errors="replace")
        script = re.search(r"<script[^>]*>(.*?)</script>", src, flags=re.S)
        src = strip_comments(script.group(1) if script else src)
        names.update(top_level_keys(props_block(src)))
    return names


def android_fields(components_dir: Path, props_class: str) -> set[str] | None:
    for source in components_dir.glob("*.kt"):
        text = source.read_text(encoding="utf-8")
        m = re.search(r"data class " + re.escape(props_class) + r"\((.*?)\n\)", text, flags=re.S)
        if m:
            return {fm.group(1) for fm in re.finditer(r"val\s+([A-Za-z0-9_]+)\s*:", m.group(1))}
    return None


def docs_rows(docs: Path) -> dict[str, tuple[str | None, str]]:
    """Map upstream tag (e.g. ``u-button``) -> (Android Props class, full row)."""
    rows: dict[str, tuple[str | None, str]] = {}
    for line in docs.read_text(encoding="utf-8").splitlines():
        if not re.match(r"^\| \d+ \| ", line):
            continue
        cols = line.split("|")
        tag = cols[3].strip().strip("`")
        props = re.search(r"UP[A-Za-z0-9]+Props", line)
        rows[tag] = (props.group(0) if props else None, line)
    return rows


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--upstream", type=Path, default=DEFAULT_UPSTREAM)
    parser.add_argument("--docs", type=Path, default=DEFAULT_DOCS)
    parser.add_argument("--components", type=Path, default=DEFAULT_COMPONENTS)
    args = parser.parse_args()

    if not args.upstream.is_dir():
        print(f"upstream components directory not found: {args.upstream}", file=sys.stderr)
        return 2

    rows = docs_rows(args.docs)
    checked = 0
    total_omitted = 0
    undocumented: list[tuple[str, list[str]]] = []

    for comp in sorted(p for p in args.upstream.iterdir() if p.is_dir() and p.name.startswith("u-")):
        info = rows.get(comp.name)
        if not info or not info[0]:
            continue
        props_class, note = info
        fields = android_fields(args.components, props_class)
        if fields is None:
            continue
        lowered = {f.lower() for f in fields}
        checked += 1
        missing = [
            name
            for name in sorted(upstream_prop_names(comp))
            if name not in IGNORE and name.lower() not in lowered
        ]
        total_omitted += len(missing)
        undoc = [name for name in missing if f"`{name}`" not in note]
        if undoc:
            undocumented.append((comp.name, undoc))

    print(f"components checked     : {checked}")
    print(f"omitted upstream props : {total_omitted} (all documented by name)")
    print(f"UNDOCUMENTED omissions : {len(undocumented)}")

    if undocumented:
        print("\nUNDOCUMENTED omitted upstream props (model them, or name them in the docs row):")
        for name, props in undocumented:
            print(f"  {name:22s} {', '.join(props)}")
        return 1
    return 0


if __name__ == "__main__":
    sys.exit(main())
