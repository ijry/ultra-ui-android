#!/usr/bin/env python3
"""Decode a screenshotTest reference PNG and report what it actually rendered.

This repository has no PIL/Pillow, and the agent-facing image viewer downscales a
945px-wide reference so aggressively that 12sp text disappears — which is how the
progress doc came to claim "the renderer draws neither text nor most fills". It does.
This script reads the pixels directly (stdlib ``zlib`` + PNG un-filtering) so a claim
about a reference image can be checked instead of eyeballed.

Usage:
  python3 tools/inspect_screenshot.py <png-or-substring> [--colors N] [--expect RRGGBB ...]
  python3 tools/inspect_screenshot.py <png-or-substring> --ascii Y0 Y1 X0 X1
  python3 tools/inspect_screenshot.py --list

``--expect`` turns the script into an assertion: it exits 1 when a colour the caller
claims the component paints is absent, so a visual claim can gate a commit.
"""

from __future__ import annotations

import argparse
import struct
import sys
import zlib
from collections import Counter
from pathlib import Path

REPO = Path(__file__).resolve().parent.parent
REFERENCE_ROOT = REPO / "ultra-ui/src/screenshotTestDebug/reference"


def png_chunks(data: bytes):
    if data[:8] != b"\x89PNG\r\n\x1a\n":
        raise ValueError("not a PNG")
    pos = 8
    while pos < len(data):
        (length,) = struct.unpack(">I", data[pos:pos + 4])
        ctype = data[pos + 4:pos + 8]
        yield ctype, data[pos + 8:pos + 8 + length]
        pos += 12 + length


def decode_rgba(path: Path) -> tuple[int, int, bytes]:
    """Return (width, height, RGBA bytes) for an 8-bit non-interlaced RGBA PNG."""
    idat = bytearray()
    header = None
    for ctype, body in png_chunks(path.read_bytes()):
        if ctype == b"IHDR":
            header = body
        elif ctype == b"IDAT":
            idat += body
    if header is None:
        raise ValueError("missing IHDR")
    width, height, depth, colour, _, _, interlace = struct.unpack(">IIBBBBB", header)
    if (depth, colour, interlace) != (8, 6, 0):
        raise ValueError(f"unsupported PNG: depth={depth} colourType={colour} interlace={interlace}")

    raw = zlib.decompress(bytes(idat))
    stride, bpp = width * 4, 4
    out = bytearray(stride * height)
    previous = bytearray(stride)
    offset = 0
    for y in range(height):
        filter_type = raw[offset]
        offset += 1
        line = bytearray(raw[offset:offset + stride])
        offset += stride
        if filter_type == 1:  # Sub
            for i in range(bpp, stride):
                line[i] = (line[i] + line[i - bpp]) & 0xFF
        elif filter_type == 2:  # Up
            for i in range(stride):
                line[i] = (line[i] + previous[i]) & 0xFF
        elif filter_type == 3:  # Average
            for i in range(stride):
                left = line[i - bpp] if i >= bpp else 0
                line[i] = (line[i] + ((left + previous[i]) >> 1)) & 0xFF
        elif filter_type == 4:  # Paeth
            for i in range(stride):
                left = line[i - bpp] if i >= bpp else 0
                up = previous[i]
                upleft = previous[i - bpp] if i >= bpp else 0
                pa, pb, pc = abs(up - upleft), abs(left - upleft), abs(left + up - 2 * upleft)
                predictor = left if (pa <= pb and pa <= pc) else (up if pb <= pc else upleft)
                line[i] = (line[i] + predictor) & 0xFF
        elif filter_type != 0:
            raise ValueError(f"unknown PNG filter {filter_type} on row {y}")
        out[y * stride:(y + 1) * stride] = line
        previous = line
    return width, height, bytes(out)


def histogram(width: int, height: int, pixels: bytes) -> Counter:
    counts: Counter = Counter()
    for i in range(0, len(pixels), 4):
        counts[pixels[i:i + 3].hex()] += 1
    return counts


def resolve(pattern: str) -> Path:
    candidate = Path(pattern)
    if candidate.is_file():
        return candidate
    matches = [p for p in sorted(REFERENCE_ROOT.rglob("*.png")) if pattern.lower() in p.name.lower()]
    if not matches:
        raise SystemExit(f"no reference image matches {pattern!r}")
    if len(matches) > 1:
        listing = "\n  ".join(p.name for p in matches)
        raise SystemExit(f"{pattern!r} is ambiguous:\n  {listing}")
    return matches[0]


def render_ascii(width: int, pixels: bytes, y0: int, y1: int, x0: int, x1: int) -> None:
    """Luminance ramp, so glyph shapes and edges stay legible in a terminal."""
    for y in range(y0, y1):
        row = []
        for x in range(x0, x1):
            i = (y * width + x) * 4
            lum = (pixels[i] * 299 + pixels[i + 1] * 587 + pixels[i + 2] * 114) // 1000
            row.append("#" if lum < 110 else "+" if lum < 200 else "-" if lum < 248 else " ")
        print("".join(row))


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("image", nargs="?", help="reference file path, or a substring of its name")
    parser.add_argument("--list", action="store_true", help="list every reference image and exit")
    parser.add_argument("--colors", type=int, default=10, help="how many top colours to print")
    parser.add_argument("--expect", nargs="*", default=[], metavar="RRGGBB", help="fail when a colour is absent")
    parser.add_argument("--ascii", nargs=4, type=int, metavar=("Y0", "Y1", "X0", "X1"), help="print a luminance map")
    args = parser.parse_args()

    if args.list:
        for path in sorted(REFERENCE_ROOT.rglob("*.png")):
            print(path.relative_to(REPO))
        return 0
    if not args.image:
        parser.error("pass an image (or --list)")

    path = resolve(args.image)
    width, height, pixels = decode_rgba(path)
    counts = histogram(width, height, pixels)
    total = width * height
    white = counts.get("ffffff", 0)
    # Colours used by only a handful of pixels are the tell-tale of glyph antialiasing;
    # a renderer that skipped text would leave flat fills and almost no such colours.
    antialiased = sum(n for colour, n in counts.items() if n <= 40)

    print(f"{path.name}")
    print(f"  {width}x{height}, {len(counts)} distinct colours")
    print(f"  non-white {(total - white) / total * 100:.2f}%, antialiased {antialiased / total * 100:.3f}%")
    print(f"  top {args.colors} colours:")
    for colour, n in counts.most_common(args.colors):
        print(f"    #{colour}  {n:>8}  {n / total * 100:6.2f}%")

    if args.ascii:
        y0, y1, x0, x1 = args.ascii
        print()
        render_ascii(width, pixels, max(0, y0), min(height, y1), max(0, x0), min(width, x1))

    missing = [c.lower().lstrip("#") for c in args.expect if c.lower().lstrip("#") not in counts]
    if missing:
        print("\nmissing expected colours: " + ", ".join(f"#{c}" for c in missing))
        return 1
    if args.expect:
        print("\nall expected colours present: " + ", ".join(f"#{c.lower().lstrip('#')}" for c in args.expect))
    return 0


if __name__ == "__main__":
    sys.exit(main())
