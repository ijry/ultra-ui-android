package net.lingyun.ultraui.android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.tools.screenshot.PreviewTest
import net.lingyun.ultraui.android.core.UPImageLoaders
import net.lingyun.ultraui.android.core.UPRawValue

private val TextSlides: List<UPRawValue> = listOf("第一页", "第二页", "第三页")

private val ImageSlides: List<UPRawValue> = listOf(
    mapOf("url" to "https://cdn.test/banner-1.png", "title" to "带标题的图片轮播项，超出一行时省略"),
    mapOf("url" to "https://cdn.test/banner-2.png", "title" to "第二张"),
)

@PreviewTest
@Preview(
    name = "u-swiper text slides",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF,
    widthDp = 320,
    heightDp = 200,
)
@Composable
public fun UPSwiperTextSlidesScreenshot(): Unit = SwiperScreenshotSurface {
    UPSwiper(
        props = UPSwiperProps(
            list = TextSlides,
            autoplay = false,
            current = 1,
            indicator = true,
            indicatorMode = "dot",
            height = 120,
            radius = 12,
        ),
        loader = UPImageLoaders.Empty,
    )
}

@PreviewTest
@Preview(
    name = "u-swiper loading",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF,
    widthDp = 320,
    heightDp = 160,
)
@Composable
public fun UPSwiperLoadingScreenshot(): Unit = SwiperScreenshotSurface {
    UPSwiper(
        props = UPSwiperProps(list = TextSlides, autoplay = false, loading = true, height = 120, radius = 12),
        loader = UPImageLoaders.Empty,
    )
}

@PreviewTest
@Preview(
    name = "u-swiper title bar",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF,
    widthDp = 320,
    heightDp = 200,
)
@Composable
public fun UPSwiperTitleBarScreenshot(): Unit = SwiperScreenshotSurface {
    UPSwiper(
        props = UPSwiperProps(
            list = ImageSlides,
            autoplay = false,
            indicator = true,
            showTitle = true,
            height = 130,
            radius = 8,
        ),
        loader = UPImageLoaders.Empty,
    )
}

@PreviewTest
@Preview(
    name = "u-swiper peeking margins",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF,
    widthDp = 320,
    heightDp = 200,
)
@Composable
public fun UPSwiperPeekingMarginsScreenshot(): Unit = SwiperScreenshotSurface {
    UPSwiper(
        props = UPSwiperProps(
            list = TextSlides,
            autoplay = false,
            current = 1,
            indicator = true,
            previousMargin = 24,
            nextMargin = 24,
            height = 120,
            radius = 12,
            bgColor = "#e8eaec",
        ),
        loader = UPImageLoaders.Empty,
    )
}

@Composable
private fun SwiperScreenshotSurface(content: @Composable () -> Unit): Unit {
    Column(
        modifier = Modifier.fillMaxSize().background(Color.White).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
    ) {
        content()
    }
}
