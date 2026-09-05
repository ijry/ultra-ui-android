package net.lingyun.ultraui.android.screenshots

import java.io.File
import javax.imageio.ImageIO

/**
 * Reads the `screenshotTest` reference PNGs back on the JVM so a visual claim can be
 * asserted instead of eyeballed.
 *
 * `validateDebugScreenshotTest` only answers "did the rendering change since the last
 * `update`" — it cannot say *what* was drawn, so a reference that never had text in it
 * would keep passing forever. Decoding the committed PNG here closes that gap without a
 * device: `javax.imageio` is part of the host JDK these unit tests already run on.
 */
internal class UPScreenshotReference private constructor(
    val name: String,
    val width: Int,
    val height: Int,
    private val argb: IntArray,
) {
    /** `#rrggbb` at a pixel, alpha dropped because every reference is fully opaque. */
    fun colorAt(x: Int, y: Int): String {
        require(x in 0 until width && y in 0 until height) { "($x, $y) outside ${width}x$height" }
        return "%06x".format(argb[y * width + x] and 0xFFFFFF)
    }

    fun colorCounts(): Map<String, Int> {
        val counts = HashMap<String, Int>()
        for (pixel in argb) {
            val key = "%06x".format(pixel and 0xFFFFFF)
            counts[key] = (counts[key] ?: 0) + 1
        }
        return counts
    }

    fun countOf(color: String): Int = colorCounts()[color.normalizedHex()] ?: 0

    fun contains(color: String): Boolean = countOf(color) > 0

    /** Rows where [color] appears, collapsed into contiguous bands. */
    fun rowBandsOf(color: String): List<IntRange> {
        val wanted = color.normalizedHex()
        val rows = ArrayList<Int>()
        for (y in 0 until height) {
            for (x in 0 until width) {
                if ("%06x".format(argb[y * width + x] and 0xFFFFFF) == wanted) {
                    rows += y
                    break
                }
            }
        }
        return rows.toBands()
    }

    /** Columns where [color] appears within [rows], collapsed into contiguous bands. */
    fun columnBandsOf(color: String, rows: IntRange): List<IntRange> {
        val wanted = color.normalizedHex()
        val columns = ArrayList<Int>()
        for (x in 0 until width) {
            for (y in rows) {
                if (y in 0 until height && "%06x".format(argb[y * width + x] and 0xFFFFFF) == wanted) {
                    columns += x
                    break
                }
            }
        }
        return columns.toBands()
    }

    /**
     * Widest horizontal run of [color] on row [y], tolerating gaps up to [mergeGap] px.
     *
     * A filled bar is rarely one unbroken run: label glyphs punch holes through it, and
     * the 1px item borders of `mode="subsection"` show up as their own hairline runs.
     * Merging across small gaps recovers the bar itself while still ignoring the borders.
     */
    fun widestRunInRow(color: String, y: Int, mergeGap: Int = 40): IntRange? {
        val wanted = color.normalizedHex()
        val runs = ArrayList<IntRange>()
        var start: Int? = null
        for (x in 0 until width) {
            val hit = "%06x".format(argb[y * width + x] and 0xFFFFFF) == wanted
            if (hit && start == null) {
                start = x
            } else if (!hit && start != null) {
                runs += start..(x - 1)
                start = null
            }
        }
        if (start != null) runs += start..(width - 1)
        if (runs.isEmpty()) return null

        val merged = ArrayList<IntRange>()
        var current = runs.first()
        for (run in runs.drop(1)) {
            current = if (run.first - current.last <= mergeGap) current.first..run.last else {
                merged += current
                run
            }
        }
        merged += current
        return merged.maxBy { it.last - it.first }
    }

    /**
     * Share of pixels whose colour is used by at most [rare] pixels in the whole image.
     * Glyph edges and rounded corners produce hundreds of such one-off blends, so a
     * renderer that silently skipped text would leave flat fills and score near zero.
     */
    fun antialiasedFraction(rare: Int = 40): Double {
        val counts = colorCounts()
        val blended = counts.values.filter { it <= rare }.sum()
        return blended.toDouble() / (width * height)
    }

    private fun List<Int>.toBands(): List<IntRange> {
        if (isEmpty()) return emptyList()
        val bands = ArrayList<IntRange>()
        var start = first()
        var previous = first()
        for (value in drop(1)) {
            if (value != previous + 1) {
                bands += start..previous
                start = value
            }
            previous = value
        }
        bands += start..previous
        return bands
    }

    internal companion object {
        /**
         * Locates the committed reference PNG whose file name contains [nameFragment].
         * The screenshot plugin bakes a preview hash into the file name, so matching on a
         * fragment keeps the tests readable and survives an unrelated re-render.
         */
        fun load(nameFragment: String): UPScreenshotReference {
            val matches = referenceRoot().walkTopDown()
                .filter { it.isFile && it.extension == "png" }
                .filter { it.name.contains(nameFragment, ignoreCase = true) }
                .sortedBy { it.name }
                .toList()
            require(matches.isNotEmpty()) { "no reference PNG matches \"$nameFragment\"" }
            require(matches.size == 1) {
                "\"$nameFragment\" is ambiguous: " + matches.joinToString { it.name }
            }
            val file = matches.single()
            val image = requireNotNull(ImageIO.read(file)) { "could not decode ${file.name}" }
            val width = image.width
            val height = image.height
            val argb = IntArray(width * height)
            image.getRGB(0, 0, width, height, argb, 0, width)
            return UPScreenshotReference(file.name, width, height, argb)
        }

        /** Walks up from the test working directory, which differs between Gradle and IDE runs. */
        private fun referenceRoot(): File {
            var directory: File? = File(System.getProperty("user.dir") ?: ".").absoluteFile
            while (directory != null) {
                for (relative in RELATIVE_ROOTS) {
                    val candidate = File(directory, relative)
                    if (candidate.isDirectory) return candidate
                }
                directory = directory.parentFile
            }
            error("could not locate the screenshotTest reference directory")
        }

        private val RELATIVE_ROOTS = listOf(
            "src/screenshotTestDebug/reference",
            "ultra-ui/src/screenshotTestDebug/reference",
        )

        private fun String.normalizedHex(): String = removePrefix("#").lowercase()
    }
}
