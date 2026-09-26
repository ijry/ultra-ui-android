package net.lingyun.ultraui.android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPRawValue
import net.lingyun.ultraui.android.core.report
import net.lingyun.ultraui.android.core.upIntOrDefault
import net.lingyun.ultraui.android.core.upTestTag

/** Colour palette for a novel-reader theme (background/text/muted/toolbar/active/disabled). */
internal data class UPNovelPalette(
    val background: Long,
    val text: Long,
    val muted: Long,
    val toolbar: Long,
    val active: Long,
    val disabled: Long,
)

/** Maps a reader theme name to its palette, mirroring `theme-vars.scss`; unknown falls back to day. */
internal fun upNovelReaderTheme(name: String): UPNovelPalette = when (name) {
    "paper" -> UPNovelPalette(0xFFF3EAD7, 0xFF51483D, 0xFF8F806D, 0xFFF7EFDF, 0xFF9B7653, 0xFFC7B9A3)
    "green" -> UPNovelPalette(0xFFE7F1E4, 0xFF3F5140, 0xFF708371, 0xFFEEF6EB, 0xFF4D8B55, 0xFFB6C7B4)
    "night" -> UPNovelPalette(0xFF202124, 0xFFD6D7DA, 0xFF9CA0A8, 0xFF292B30, 0xFF7DA7FF, 0xFF62656D)
    "dark" -> UPNovelPalette(0xFF111214, 0xFFE5E7EB, 0xFF9CA3AF, 0xFF1B1D21, 0xFF8AB4FF, 0xFF5F6368)
    else -> UPNovelPalette(0xFFF7F8FA, 0xFF303133, 0xFF909399, 0xFFFFFFFF, 0xFF2979FF, 0xFFC8C9CC)
}

/** Normalizes chapter content (String or list) into paragraphs, mirroring `content-normalizer`. */
internal fun upNovelParagraphs(content: UPRawValue): List<String> {
    val values: List<Any?> = when (content) {
        is List<*> -> content
        null -> listOf("")
        else -> listOf(content)
    }
    val out = ArrayList<String>()
    for (value in values) {
        (value?.toString() ?: "").split(Regex("\\r\\n|\\r|\\n")).forEach { out.add(it) }
    }
    return out
}

private fun UPRawValue.asFloatOr(default: Float): Float =
    (this as? Number)?.toFloat() ?: this?.toString()?.toFloatOrNull() ?: default

private fun contentWidthFraction(value: UPRawValue): Float {
    val s = value?.toString()?.trim() ?: return 0.92f
    return if (s.endsWith("%")) (s.dropLast(1).toFloatOrNull()?.div(100f) ?: 0.92f).coerceIn(0.3f, 1f) else 0.92f
}

private fun fontFamilyOf(name: String): FontFamily = when (name) {
    "serif" -> FontFamily.Serif
    "mono", "monospace" -> FontFamily.Monospace
    else -> FontFamily.Default
}

private val READER_THEMES = listOf("day", "paper", "green", "night", "dark")

/**
 * Native Compose counterpart of uview-plus `u-novel-reader` (bounded).
 *
 * Renders [`UPNovelReaderProps.currentChapter`] as themed, scrollable paragraphs with the merged
 * [defaultSettings]/[settings] (theme/fontSize/lineHeight/paragraphSpacing/contentWidth/fontFamily/
 * fontWeight). Tapping the page toggles the top (back via [showBack]/[backIcon] -> [onBack], chapter
 * title) and bottom toolbars (catalog, settings, prev/next chapter -> [onChapterRequest]); the
 * settings panel edits theme/font size/line height and emits [onSettingsChange]; the catalog lists
 * [chapters]. [loading]/[error] show their states ([onRetry]); scrolling reports a 0..1 progress via
 * [onProgressChange]; [controlsAutoHide] hides the toolbars after N ms.
 *
 * Downgrades (see docs): the page-mode pagination engine, and persistence/prefetch/bookmark storage,
 * are not modelled — `page` mode falls back to scrolling and those props are inert/host-managed.
 */
@Composable
public fun UPNovelReader(
    props: UPNovelReaderProps = UPNovelReaderProps(),
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    onChapterRequest: ((Map<String, UPRawValue>) -> Unit)? = null,
    onSettingsChange: ((Map<String, UPRawValue>) -> Unit)? = null,
    onProgressChange: ((Float) -> Unit)? = null,
    onRetry: (() -> Unit)? = null,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    val style = rememberUPResolvedStyle(props.customStyle, diagnostics, "UPNovelReader")
    val merged = remember(props.defaultSettings, props.settings) { props.defaultSettings + props.settings }

    var theme by remember(merged) { mutableStateOf(merged["theme"]?.toString() ?: "day") }
    var fontSize by remember(merged) { mutableIntStateOf(merged["fontSize"].upIntOrDefault(18)) }
    var lineHeight by remember(merged) { mutableFloatStateOf(merged["lineHeight"].asFloatOr(1.8f)) }
    val paragraphSpacing = merged["paragraphSpacing"].upIntOrDefault(16)
    val widthFraction = contentWidthFraction(merged["contentWidth"])
    val fontFamily = fontFamilyOf(merged["fontFamily"]?.toString() ?: "system")
    val fontWeight = FontWeight(merged["fontWeight"].upIntOrDefault(400).coerceIn(100, 900))

    val palette = upNovelReaderTheme(theme)
    var controlsVisible by remember { mutableStateOf(false) }
    var showCatalog by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    val scroll = rememberScrollState()

    LaunchedEffect(props.mode) {
        if (props.mode == "page") {
            diagnostics.report("UPNovelReader", "mode", props.mode, "page-mode pagination engine not ported; falling back to scroll")
        }
    }

    LaunchedEffect(scroll) {
        snapshotFlow { if (scroll.maxValue <= 0) 0f else scroll.value.toFloat() / scroll.maxValue }
            .collect { onProgressChange?.invoke(it) }
    }

    LaunchedEffect(controlsVisible, props.controlsAutoHide) {
        if (controlsVisible && props.controlsAutoHide > 0) {
            kotlinx.coroutines.delay(props.controlsAutoHide.toLong())
            controlsVisible = false
        }
    }

    fun emitSettings() {
        onSettingsChange?.invoke(
            mapOf(
                "theme" to theme,
                "fontSize" to fontSize,
                "lineHeight" to lineHeight,
                "paragraphSpacing" to paragraphSpacing,
                "contentWidth" to (merged["contentWidth"] ?: "92%"),
                "fontFamily" to (merged["fontFamily"] ?: "system"),
                "fontWeight" to fontWeight.weight,
            ),
        )
    }

    val chapter = props.currentChapter
    val paragraphs = remember(chapter) { upNovelParagraphs(chapter["content"]) }
    val title = chapter["title"].upStringValueOrEmpty()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(palette.background))
            .applyUPResolvedStyle(style)
            .upTestTag("novel-reader"),
    ) {
        when {
            props.loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                BasicText(text = "加载中…", style = TextStyle(color = Color(palette.muted), fontSize = 15.sp), modifier = Modifier.upTestTag("novel-reader-loading"))
            }
            props.error.isNotEmpty() -> Column(
                Modifier.fillMaxSize().upTestTag("novel-reader-error"),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                BasicText(text = props.error["message"].upStringValueOrEmpty().ifEmpty { "加载失败" }, style = TextStyle(color = Color(palette.text), fontSize = 15.sp))
                UPButton(props = UPButtonProps(text = "重试", type = "primary", size = "small"), onClick = { onRetry?.invoke() })
            }
            else -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scroll)
                    .pointerInput(Unit) { detectTapGestures { controlsVisible = !controlsVisible } }
                    .padding(vertical = 24.dp)
                    .fillMaxWidth(widthFraction)
                    .upTestTag("novel-reader-content"),
            ) {
                if (title.isNotEmpty()) {
                    BasicText(
                        text = title,
                        style = TextStyle(color = Color(palette.text), fontSize = (fontSize + 4).sp, fontWeight = FontWeight.Bold, fontFamily = fontFamily),
                        modifier = Modifier.padding(bottom = 12.dp),
                    )
                }
                paragraphs.forEach { para ->
                    BasicText(
                        text = para,
                        style = TextStyle(
                            color = Color(palette.text),
                            fontSize = fontSize.sp,
                            lineHeight = (fontSize * lineHeight).sp,
                            fontWeight = fontWeight,
                            fontFamily = fontFamily,
                        ),
                        modifier = Modifier.padding(bottom = paragraphSpacing.dp),
                    )
                }
            }
        }

        if (controlsVisible) {
            Row(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .background(Color(palette.toolbar))
                    .then(if (props.safeAreaInsetTop) Modifier.statusBarsPadding() else Modifier)
                    .padding(12.dp)
                    .upTestTag("novel-reader-topbar"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                if (props.showBack) {
                    UPIcon(props = UPIconProps(name = props.backIcon, color = "#" + palette.text.toString(16).takeLast(6), size = "22px"), onClick = { onBack?.invoke() })
                }
                BasicText(text = title, style = TextStyle(color = Color(palette.text), fontSize = 16.sp, fontWeight = FontWeight.Medium))
            }

            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Color(palette.toolbar))
                    .then(if (props.safeAreaInsetBottom) Modifier.navigationBarsPadding() else Modifier)
                    .padding(12.dp)
                    .upTestTag("novel-reader-bottombar"),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                UPButton(props = UPButtonProps(text = "上一章", size = "mini"), onClick = { requestChapterDelta(props, chapter, -1, onChapterRequest) })
                UPButton(props = UPButtonProps(text = "目录", size = "mini"), onClick = { showCatalog = true })
                UPButton(props = UPButtonProps(text = "设置", size = "mini"), onClick = { showSettings = true })
                UPButton(props = UPButtonProps(text = "下一章", size = "mini"), onClick = { requestChapterDelta(props, chapter, 1, onChapterRequest) })
            }
        }

        if (showCatalog) {
            UPOverlay(
                props = UPOverlayProps(show = true),
                onClick = { showCatalog = false },
                content = {
                    Box(
                        Modifier.fillMaxHeight().fillMaxWidth(0.7f).background(Color(palette.toolbar)).upTestTag("novel-reader-catalog"),
                    ) {
                        LazyColumn(Modifier.fillMaxSize()) {
                            items(props.chapters) { rawCh ->
                                val ch = rawCh.upStringKeyMapOrEmpty()
                                BasicText(
                                    text = ch["title"].upStringValueOrEmpty(),
                                    style = TextStyle(color = Color(palette.text), fontSize = 15.sp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .pointerInput(ch) { detectTapGestures { showCatalog = false; onChapterRequest?.invoke(ch) } }
                                        .padding(14.dp),
                                )
                            }
                        }
                    }
                },
            )
        }

        if (showSettings) {
            UPOverlay(
                props = UPOverlayProps(show = true),
                onClick = { showSettings = false },
                content = {
                    Column(
                        Modifier.align(Alignment.BottomCenter).fillMaxWidth().background(Color(palette.toolbar)).padding(16.dp).upTestTag("novel-reader-settings"),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            BasicText(text = "字号", style = TextStyle(color = Color(palette.text), fontSize = 14.sp))
                            UPButton(props = UPButtonProps(text = "A-", size = "mini"), onClick = { fontSize = (fontSize - 1).coerceAtLeast(12); emitSettings() })
                            BasicText(text = "$fontSize", style = TextStyle(color = Color(palette.text), fontSize = 14.sp))
                            UPButton(props = UPButtonProps(text = "A+", size = "mini"), onClick = { fontSize = (fontSize + 1).coerceAtMost(40); emitSettings() })
                            UPButton(props = UPButtonProps(text = "行距+", size = "mini"), onClick = { lineHeight = (lineHeight + 0.1f).coerceAtMost(3f); emitSettings() })
                            UPButton(props = UPButtonProps(text = "行距-", size = "mini"), onClick = { lineHeight = (lineHeight - 0.1f).coerceAtLeast(1f); emitSettings() })
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            READER_THEMES.forEach { t ->
                                val p = upNovelReaderTheme(t)
                                Box(
                                    Modifier
                                        .background(Color(p.background))
                                        .then(if (t == theme) Modifier.padding(1.dp) else Modifier)
                                        .pointerInput(t) { detectTapGestures { theme = t; emitSettings() } }
                                        .padding(horizontal = 14.dp, vertical = 10.dp)
                                        .upTestTag("novel-reader-theme-$t"),
                                ) {
                                    BasicText(text = t, style = TextStyle(color = Color(p.text), fontSize = 12.sp))
                                }
                            }
                        }
                    }
                },
            )
        }
    }
}

private fun requestChapterDelta(
    props: UPNovelReaderProps,
    chapter: Map<String, UPRawValue>,
    delta: Int,
    onChapterRequest: ((Map<String, UPRawValue>) -> Unit)?,
) {
    val currentIndex = chapter["index"].upIntOrDefault(0)
    val target = currentIndex + delta
    if (target < 0 || target >= props.chapters.size) return
    onChapterRequest?.invoke(props.chapters[target].upStringKeyMapOrEmpty())
}
