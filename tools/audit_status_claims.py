#!/usr/bin/env python3
"""Cross-check each component's claimed status against the evidence behind it.

The progress table's own maintenance rules require evidence for each level:
"基础可用" needs a behaviour or screenshot test; "基本完成" additionally needs common
fields, events, controlled state and error/disabled regressions. Five consecutive
rounds of work found component-level defects hiding behind those labels, so this
scores every row against three measurable signals:

  * unread   — fields the component declares but never reads (find_unread_props)
  * device   — whether any androidTest exercises it
  * shot     — whether any screenshot preview renders it

A row is flagged when its label outruns its evidence. This does not prove a component
works; it finds labels that nothing backs up.
"""

from __future__ import annotations

import argparse
import re
import subprocess
import sys
from pathlib import Path

REPO = Path(__file__).resolve().parent.parent
DOC = REPO / "docs/uview-plus-android-component-progress.md"
ANDROID_TEST = REPO / "ultra-ui/src/androidTest/kotlin"
SHOT_TEST = REPO / "ultra-ui/src/screenshotTest/kotlin"
UNIT_TEST = REPO / "ultra-ui/src/test/kotlin"


def doc_rows() -> list[tuple[str, str, str]]:
    """(upstream tag, android api cell, status) for every numbered component row."""
    out = []
    for line in DOC.read_text(encoding="utf-8").splitlines():
        if not re.match(r"^\|\s*\d+\s*\|", line):
            continue
        cells = [c.strip() for c in line.split("|")]
        tag = cells[3].strip("`")
        out.append((tag, cells[4], cells[5]))
    return out


def component_names(api_cell: str) -> list[str]:
    """Extract UP* identifiers from an 'API / Props' cell."""
    return re.findall(r"`(UP[A-Za-z0-9]+)`", api_cell)


def unread_counts() -> dict[str, int]:
    """Run find_unread_props and map component base name -> unread field count."""
    script = REPO / "tools/find_unread_props.py"
    proc = subprocess.run([sys.executable, str(script)], capture_output=True, text=True)
    counts: dict[str, int] = {}
    for m in re.finditer(r"^\s+UP([A-Za-z0-9]+)Props: (.+)$", proc.stdout, re.M):
        counts[m.group(1)] = len(m.group(2).split(","))
    return counts


def corpus(root: Path) -> str:
    if not root.is_dir():
        return ""
    return "\n".join(p.read_text(encoding="utf-8") for p in root.rglob("*.kt"))


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--all", action="store_true", help="list every implemented row, not just flagged ones")
    args = parser.parse_args()

    device_src = corpus(ANDROID_TEST)
    shot_src = corpus(SHOT_TEST)
    unit_src = corpus(UNIT_TEST)
    unread = unread_counts()

    flagged, ok, skipped = [], [], 0
    for tag, api_cell, status in doc_rows():
        names = component_names(api_cell)
        if not names:
            skipped += 1
            continue
        base = names[0][2:]  # strip "UP"
        # Evidence: is the composable referenced from each test corpus?
        entry = names[0]
        has_device = bool(re.search(rf"\b{entry}\s*\(", device_src))
        has_shot = bool(re.search(rf"\b{entry}\s*\(", shot_src))
        has_unit = bool(re.search(rf"\b{entry}Props\s*\(", unit_src))
        n_unread = unread.get(base, 0)

        reasons = []
        if status == "基本完成":
            # The bar is common fields + events + controlled state + regressions.
            if not has_device:
                reasons.append("无真机行为测试")
            if n_unread:
                reasons.append(f"{n_unread} 个字段未读")
        elif status == "基础可用":
            # The bar is at least one behaviour or screenshot evidence.
            if not has_device and not has_shot:
                reasons.append("既无真机测试也无截图")
            if n_unread >= 6:
                reasons.append(f"{n_unread} 个字段未读")

        row = (tag, status, n_unread, has_device, has_shot, has_unit, reasons)
        (flagged if reasons else ok).append(row)

    def fmt(r):
        tag, status, n, d, s, u, reasons = r
        marks = f"{'真机' if d else '——'} {'截图' if s else '——'} {'单测' if u else '——'}"
        why = ("；".join(reasons)) if reasons else "证据齐备"
        return f"  {tag:22s} {status:6s} 未读{n:3d}  {marks}  {why}"

    print(f"已实现组件行 : {len(flagged) + len(ok)}（未开始 {skipped} 行跳过）")
    print(f"证据齐备     : {len(ok)}")
    print(f"标注偏乐观   : {len(flagged)}")
    print()
    if flagged:
        print("标注超出证据的行（按未读字段数排序）:")
        for r in sorted(flagged, key=lambda x: -x[2]):
            print(fmt(r))
    if args.all and ok:
        print("\n证据齐备的行:")
        for r in sorted(ok, key=lambda x: -x[2]):
            print(fmt(r))
    return 1 if flagged else 0


if __name__ == "__main__":
    sys.exit(main())
