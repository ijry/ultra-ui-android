package net.lingyun.ultraui.android.core

import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import java.io.File

/** Injectable, dependency-free image loading contract for generated Android views. */
public fun interface UPImageLoader {
    public suspend fun load(source: String): ImageBitmap?
}

/** Built-in deterministic image loaders. Neither loader performs network I/O. */
public object UPImageLoaders {
    /** Always returns `null`; useful for tests and offline placeholders. */
    public val Empty: UPImageLoader = UPImageLoader { null }

    /** Decodes local file paths and `file://` sources; unsupported sources fail safely. */
    public val Android: UPImageLoader = UPImageLoader { source ->
        val raw = source.trim()
        val path = when {
            raw.startsWith("file://", ignoreCase = true) -> raw.removePrefix("file://")
            raw.startsWith("/" ) -> raw
            else -> null
        } ?: return@UPImageLoader null
        runCatching { BitmapFactory.decodeFile(File(path).absolutePath)?.asImageBitmap() }.getOrNull()
    }
}
