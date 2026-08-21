#!/usr/bin/env python3
"""Compare uview-plus upstream prop defaults against the Android UPConfig defaults.

Every upstream component ships its defaults as ``export default { <key>: { ... } }``
in ``components/u-<name>/<name>.js``; the Android port mirrors them as
``UP<Name>Defaults`` data classes in ``core/UPConfig.kt``. This script diffs the two
so a default can never drift silently the way ``u-tabbar-item``'s icon size did.

Only fields present on both sides with directly comparable literals are reported.
Computed upstream defaults, style maps and fields the port intentionally omits are
counted but never flagged, so a clean run means "no unexplained literal drift"
rather than "full behavioural parity".

Coverage limit: this can only see components that route their defaults through
``UPConfig``. 41 of the 88 ported components (all of Batch 10 included) hardcode
literals directly in their ``UP*Props`` class, so they are invisible here and still
need manual comparison against the upstream ``.vue``/``props.js`` — that is exactly
how ``u-tabbar-item``'s icon size drifted unnoticed. Run with ``--list-unaudited``
to print them.
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
    outer = re.search(r"export\s+default\s*\{(.*)\}\s*$", src, flags=re.S)
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


def unaudited_components(config: Path, components_dir: Path) -> list[str]:
    """Ported Props classes with no UP*Defaults block, so this script cannot see them."""
    have = set(android_defaults(config))
    sources = "\n".join(p.read_text(encoding="utf-8") for p in sorted(components_dir.glob("*.kt")))
    props = {m.group(1) for m in re.finditer(r"public data class UP([A-Za-z0-9]+)Props\b", sources)}
    return sorted(p for p in props if p.lower() not in have)


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
    drift, accepted, compared, components = [], [], 0, 0

    for comp in sorted(p for p in args.upstream.iterdir() if p.is_dir() and p.name.startswith("u-")):
        stem = comp.name[2:]
        source = next(
            (c for c in (comp / f"{stem}.js", comp / f"{stem.replace('-', '')}.js") if c.exists()),
            None,
        )
        if source is None:
            continue
        upstream = upstream_defaults(source)
        fields = android.get(stem.replace("-", ""))
        if not upstream or fields is None:
            continue
        components += 1
        for field, raw in upstream.items():
            if field not in fields:
                continue
            want, got = normalize_js(raw), normalize_kt(fields[field])
            if want is UNCOMPARABLE or got is UNCOMPARABLE:
                continue
            compared += 1
            if equivalent(want, got):
                continue
            row = (comp.name, field, raw, fields[field])
            (accepted if (comp.name, field) in ACCEPTED else drift).append(row)

    print(f"components compared   : {components}")
    print(f"field values compared : {compared}")
    print(f"documented downgrades : {len(accepted)}")
    print(f"unexplained drift     : {len(drift)}")

    unaudited = unaudited_components(args.config, args.config.parent.parent / "components")
    print(f"not checkable here    : {len(unaudited)} components (defaults not routed through UPConfig)")

    if args.list_unaudited and unaudited:
        print("\nnot checkable here (compare these against upstream by hand):")
        for i in range(0, len(unaudited), 6):
            print("  " + ", ".join(unaudited[i : i + 6]))

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
