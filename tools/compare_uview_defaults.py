#!/usr/bin/env python3
"""Compare uview-plus upstream prop defaults against the Android port's defaults.

Upstream declares defaults either in ``components/u-<name>/<name>.js`` or, for the
components that never got one, as inline ``default:`` literals in ``props.js``. The
Android port mirrors them either as ``UP<Name>Defaults`` in ``core/UPConfig.kt`` or
as literals on the ``UP<Name>Props`` data class. This script diffs both shapes so a
default cannot drift silently the way ``u-tabbar-item``'s icon size did.

Only fields present on both sides with directly comparable literals are reported.
Computed upstream defaults, style maps and fields the port intentionally omits are
counted but never flagged, so a clean run means "no unexplained literal drift"
rather than "full behavioural parity" — it compares declared defaults, not rendered
geometry or event semantics. Run ``--list-unaudited`` for the components where not a
single field was comparable; those still need manual review against the upstream
``.vue``.
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
DEFAULT_CONFIG = REPO / "ultra-ui/src/main/kotlin/net/lingyun/ultraui/android/core/UPConfig.kt"

# Fields the port deliberately diverges on, with the reason it is not a defect.
# Keep this list short and justified: every entry is a documented downgrade.
ACCEPTED = {
    ("u-avatar", "colorIndex"): "randomBgColor is deterministic on Android for reproducible screenshots",
    ("u-image", "showMenuByLongpress"): "WeChat mini-program only; inert on Android",
    ("u-image", "width"): "upstream ships the number as a string",
    ("u-image", "height"): "upstream ships the number as a string",
    ("u-input", "disabledColor"): "upstream resolves '' via themeVar at render time",
    ("u-input", "color"): "upstream resolves '' via themeVar at render time",
    ("u-input", "fontSize"): "upstream ships the number as a px string",
    ("u-input", "placeholderClass"): "CSS class name; no Compose equivalent",
    ("u-input", "cursorSpacing"): "uni-app keyboard spacing hint; no Compose equivalent",
    ("u-textarea", "placeholderClass"): "CSS class name; no Compose equivalent",
    ("u-textarea", "cursor"): "'' means unset; Android encodes unset as -1",
    ("u-text", "lineHeight"): "'' means unset; Android encodes unset as 'normal'",
    ("u-calendar", "todayColor"): "upstream falls back to the theme color (month.vue: todayColor || color)",
    ("u-calendar", "monthFormat"): "upstream month.vue hardcodes YYYY年MM月; the Android default renders the same string",
    ("u-loading-page", "iconSize"): "upstream props.js resolves iconSize from fontSize (upstream bug, replicated)",
}


def split_top_level(text: str) -> list[str]:
    """Split on commas that sit outside nested brackets."""
    parts, depth, buf = [], 0, ""
    for ch in text:
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
    return parts


def upstream_defaults(path: Path) -> dict[str, str] | None:
    src = path.read_text(encoding="utf-8", errors="replace")
    src = re.sub(r"/\*.*?\*/", "", src, flags=re.S)
    src = re.sub(r"//[^\n]*", "", src)
    # Some files end `};` and some just `}`; tolerate both plus trailing whitespace.
    outer = re.search(r"export\s+default\s*\{(.*)\}\s*;?\s*$", src, flags=re.S)
    if not outer:
        return None
    inner = re.search(r"([A-Za-z0-9_]+)\s*:\s*\{(.*)\}\s*,?\s*$", outer.group(1), flags=re.S)
    if not inner:
        return None
    fields = {}
    for part in split_top_level(inner.group(2)):
        key, sep, value = part.partition(":")
        key = key.strip().strip("\"'")
        if sep and re.fullmatch(r"[A-Za-z0-9_]+", key):
            fields[key] = value.strip().rstrip(",").strip()
    return fields


def props_js_defaults(path: Path) -> dict[str, str] | None:
    """Read inline `default:` literals from a component's props.js.

    Components without a `<name>.js` defaults file (u-popover, u-tabs-item, ...)
    declare defaults directly in the props mixin. Entries that delegate to
    `defProps.<x>.<y>` are skipped — those resolve through a defaults file this
    component does not have, so there is no literal to compare.
    """
    src = path.read_text(encoding="utf-8", errors="replace")
    src = re.sub(r"/\*.*?\*/", "", src, flags=re.S)
    src = re.sub(r"//[^\n]*", "", src)
    fields = {}
    for match in re.finditer(r"([A-Za-z0-9_]+)\s*:\s*\{[^{}]*?default\s*:\s*([^\n,}]+)", src, flags=re.S):
        value = match.group(2).strip().rstrip(",").strip()
        if "defProps" in value or value.startswith("()"):
            continue
        fields[match.group(1)] = value
    return fields or None


def upstream_source(comp: Path) -> Path | None:
    """Locate a component's defaults file, tolerating upstream naming drift.

    Falls back to the sole non-props .js file so u-swiper-indicator's misspelled
    swipterIndicator.js is still picked up.
    """
    stem = comp.name[2:]
    camel = re.sub(r"-(\w)", lambda m: m.group(1).upper(), stem)
    for name in (stem, stem.replace("-", ""), camel):
        candidate = comp / f"{name}.js"
        if candidate.exists():
            return candidate
    extras = [p for p in comp.glob("*.js") if p.name != "props.js"]
    return extras[0] if len(extras) == 1 else None


def android_defaults(path: Path) -> dict[str, dict[str, str]]:
    src = path.read_text(encoding="utf-8")
    blocks = {}
    for match in re.finditer(r"public data class UP([A-Za-z0-9]+)Defaults\((.*?)\n\)", src, flags=re.S):
        fields = {
            m.group(1): m.group(2).rstrip(",").strip()
            for m in re.finditer(r"val\s+([A-Za-z0-9_]+)\s*:\s*[^=]+=\s*([^\n]+?),?\s*$", match.group(2), flags=re.M)
        }
        blocks[match.group(1).lower()] = fields
    return blocks


def props_defaults(components_dir: Path) -> dict[str, dict[str, str]]:
    """Read literal defaults straight from UP*Props classes.

    The 41 components that never got a UPConfig block still declare their defaults
    inline, so parsing the Props class is the only way to see them. Fields that
    delegate to UPConfig are dropped: android_defaults already covers those, and
    keeping them would double-report the same value.
    """
    blocks: dict[str, dict[str, str]] = {}
    for source in sorted(components_dir.glob("*.kt")):
        src = source.read_text(encoding="utf-8")
        for match in re.finditer(r"public data class UP([A-Za-z0-9]+)Props\((.*?)\n\)", src, flags=re.S):
            fields = {}
            for m in re.finditer(
                r"val\s+([A-Za-z0-9_]+)\s*:\s*[^=]+=\s*([^\n]+?),?\s*$", match.group(2), flags=re.M
            ):
                value = m.group(2).rstrip(",").strip()
                if "UPConfig." in value:
                    continue
                fields[m.group(1)] = value
            if fields:
                blocks.setdefault(match.group(1).lower(), {}).update(fields)
    return blocks


UNCOMPARABLE = object()


def normalize_js(raw: str):
    if raw.startswith("()") or "=>" in raw or raw.startswith("function"):
        return UNCOMPARABLE
    if raw in ("true", "false"):
        return raw == "true"
    if raw in ("null", "undefined"):
        return UNCOMPARABLE
    if re.fullmatch(r"-?\d+", raw):
        return int(raw)
    if re.fullmatch(r"-?\d*\.\d+", raw):
        return float(raw)
    quoted = re.fullmatch(r"['\"](.*)['\"]", raw, flags=re.S)
    return quoted.group(1) if quoted else UNCOMPARABLE


def normalize_kt(raw: str):
    if raw in ("true", "false"):
        return raw == "true"
    if raw == "null" or re.fullmatch(r"empty(List|Map)<[^>]*>\(\)", raw):
        return UNCOMPARABLE
    quoted = re.fullmatch(r'"(.*)"', raw, flags=re.S)
    if quoted:
        return quoted.group(1)
    if re.fullmatch(r"-?\d+", raw):
        return int(raw)
    if re.fullmatch(r"-?\d*\.\d+f?", raw):
        return float(raw.rstrip("f"))
    return UNCOMPARABLE


def equivalent(a, b) -> bool:
    if isinstance(a, bool) or isinstance(b, bool):
        return a is b
    if isinstance(a, (int, float)) and isinstance(b, (int, float)):
        return float(a) == float(b)
    return a == b


def uncovered_components(
    config: Path, components_dir: Path, upstream_dir: Path, checked: set[str]
) -> list[str]:
    """Ported Props classes this run never compared a single field for.

    Reasons vary: no upstream `<name>.js` defaults file, a name that does not map
    onto an upstream directory, or a Props class whose every field is a style map
    or callback. These still need manual comparison against the upstream `.vue`.
    """
    sources = "\n".join(p.read_text(encoding="utf-8") for p in sorted(components_dir.glob("*.kt")))
    props = {m.group(1) for m in re.finditer(r"public data class UP([A-Za-z0-9]+)Props\b", sources)}
    return sorted(p for p in props if p.lower() not in checked)


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--upstream", type=Path, default=DEFAULT_UPSTREAM, help="uview-plus components directory")
    parser.add_argument("--config", type=Path, default=DEFAULT_CONFIG, help="Android UPConfig.kt")
    parser.add_argument("--show-accepted", action="store_true", help="also list documented downgrades")
    parser.add_argument(
        "--list-unaudited",
        action="store_true",
        help="list ported components this script cannot check (defaults not in UPConfig)",
    )
    args = parser.parse_args()

    if not args.upstream.is_dir():
        print(f"upstream components directory not found: {args.upstream}", file=sys.stderr)
        return 2

    android = android_defaults(args.config)
    components_dir = args.config.parent.parent / "components"
    inline = props_defaults(components_dir)
    drift, accepted, compared, components = [], [], 0, 0
    checked: set[str] = set()

    for comp in sorted(p for p in args.upstream.iterdir() if p.is_dir() and p.name.startswith("u-")):
        stem = comp.name[2:]
        source = upstream_source(comp)
        if source is not None:
            upstream = upstream_defaults(source)
        else:
            # No defaults file: fall back to inline `default:` literals in props.js.
            props_js = comp / "props.js"
            upstream = props_js_defaults(props_js) if props_js.exists() else None
        key = stem.replace("-", "")
        # UPConfig wins where it exists; inline Props defaults fill in the rest.
        fields = {**inline.get(key, {}), **android.get(key, {})}
        if not upstream or not fields:
            continue
        components += 1
        for field, raw in upstream.items():
            if field not in fields:
                continue
            want, got = normalize_js(raw), normalize_kt(fields[field])
            if want is UNCOMPARABLE or got is UNCOMPARABLE:
                continue
            compared += 1
            checked.add(key)
            if equivalent(want, got):
                continue
            row = (comp.name, field, raw, fields[field])
            (accepted if (comp.name, field) in ACCEPTED else drift).append(row)

    uncovered = uncovered_components(args.config, components_dir, args.upstream, checked)
    print(f"components compared   : {components}")
    print(f"field values compared : {compared}")
    print(f"documented downgrades : {len(accepted)}")
    print(f"unexplained drift     : {len(drift)}")
    print(f"no field comparable   : {len(uncovered)} components (still need manual review)")

    if args.list_unaudited and uncovered:
        print("\nno field comparable (compare these against upstream by hand):")
        for i in range(0, len(uncovered), 6):
            print("  " + ", ".join(uncovered[i : i + 6]))

    if args.show_accepted and accepted:
        print("\ndocumented downgrades:")
        for name, field, want, got in accepted:
            print(f"  {name:20s} {field:24s} upstream={want:<26s} android={got}")
            print(f"    -> {ACCEPTED[(name, field)]}")

    if drift:
        print("\nUNEXPLAINED DRIFT (fix the port, or add a justified ACCEPTED entry):")
        for name, field, want, got in drift:
            print(f"  {name:20s} {field:24s} upstream={want:<26s} android={got}")
        return 1
    return 0


if __name__ == "__main__":
    sys.exit(main())
