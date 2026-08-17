package net.lingyun.ultraui.android.sample.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import net.lingyun.ultraui.android.components.UPIcon
import net.lingyun.ultraui.android.components.UPIconProps
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.sample.SampleScaffold

/** Recreates the pinned uview-plus icon page with its 193 upstream grid names. */
@Composable
public fun IconDemoPage(onBack: () -> Unit, modifier: Modifier = Modifier) {
    SampleScaffold(title = "图标", onBack = onBack, modifier = modifier) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(0.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp),
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Text(
                    text = "常用图标",
                    color = UPTheme.Main,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(horizontal = 8.dp, vertical = 12.dp),
                )
            }
            items(items = upstreamIconDemoNames, key = { it }) { name ->
                IconGridItem(name = name)
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                Text(
                    text = "文字标签",
                    color = UPTheme.Main,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(horizontal = 8.dp, vertical = 12.dp),
                )
            }
            items(items = iconLabelPositions, key = { it.first }) { (position, label) ->
                LabelPositionItem(position = position, label = label)
            }
        }
    }
}

@Composable
private fun IconGridItem(name: String) {
    androidx.compose.foundation.layout.Column(
        modifier = Modifier
            .height(116.dp)
            .border(width = 0.5.dp, color = UPTheme.Border)
            .background(Color.White)
            .clickable { },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        UPIcon(
            props = UPIconProps(
                name = name,
                size = 30,
                color = "#909399",
                stop = true,
            ),
            modifier = Modifier.padding(bottom = 10.dp),
        )
        Text(
            text = name,
            color = UPTheme.Tips,
            textAlign = TextAlign.Center,
            maxLines = 2,
            modifier = Modifier.padding(horizontal = 4.dp),
        )
    }
}

@Composable
private fun LabelPositionItem(position: String, label: String) {
    androidx.compose.foundation.layout.Column(
        modifier = Modifier
            .height(96.dp)
            .border(width = 0.5.dp, color = UPTheme.Border)
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        UPIcon(
            props = UPIconProps(
                name = "map",
                size = 24,
                color = "primary",
                label = label,
                labelPos = position,
            ),
        )
    }
}

private val iconLabelPositions: List<Pair<String, String>> = listOf(
    "right" to "右侧",
    "left" to "左侧",
    "top" to "上方",
    "bottom" to "下方",
)

/** Exact display order from pages/componentsA/icon/icon.nvue in the pinned uview-plus source. */
private val upstreamIconDemoNames: List<String> = listOf(
    "level",
    "woman",
    "man",
    "arrow-left-double",
    "arrow-right-double",
    "chat",
    "chat-fill",
    "red-packet",
    "red-packet-fill",
    "order",
    "checkbox-mark",
    "arrow-up-fill",
    "arrow-down-fill",
    "backspace",
    "photo",
    "photo-fill",
    "lock",
    "lock-fill",
    "lock-open",
    "lock-opened-fill",
    "hourglass",
    "hourglass-half-fill",
    "home",
    "home-fill",
    "fingerprint",
    "cut",
    "star",
    "star-fill",
    "share",
    "share-fill",
    "volume",
    "volume-fill",
    "volume-off",
    "volume-off-fill",
    "trash",
    "trash-fill",
    "rewind-right",
    "rewind-right-fill",
    "rewind-left",
    "rewind-left-fill",
    "shopping-cart",
    "shopping-cart-fill",
    "question",
    "question-circle",
    "question-circle-fill",
    "plus",
    "plus-circle",
    "plus-circle-fill",
    "tags",
    "tags-fill",
    "pause",
    "pause-circle",
    "pause-circle-fill",
    "play-circle",
    "play-circle-fill",
    "map",
    "map-fill",
    "phone",
    "phone-fill",
    "list",
    "list-dot",
    "man-delete",
    "man-add",
    "man-add-fill",
    "person-delete-fill",
    "info",
    "info-circle",
    "info-circle-fill",
    "minus",
    "minus-circle",
    "minus-circle-fill",
    "mic",
    "mic-off",
    "grid",
    "grid-fill",
    "eye",
    "eye-fill",
    "eye-off",
    "file-text",
    "file-text-fill",
    "edit-pen",
    "edit-pen-fill",
    "email",
    "email-fill",
    "download",
    "checkmark",
    "checkmark-circle",
    "checkmark-circle-fill",
    "clock",
    "clock-fill",
    "close",
    "close-circle",
    "close-circle-fill",
    "calendar",
    "calendar-fill",
    "car",
    "car-fill",
    "bell",
    "bell-fill",
    "bookmark",
    "bookmark-fill",
    "attach",
    "play-right",
    "play-right-fill",
    "play-left",
    "play-left-fill",
    "error",
    "error-circle",
    "error-circle-fill",
    "wifi",
    "wifi-off",
    "skip-back-left",
    "skip-forward-right",
    "search",
    "setting",
    "setting-fill",
    "more-dot-fill",
    "more-circle",
    "more-circle-fill",
    "bag",
    "bag-fill",
    "arrow-upward",
    "arrow-downward",
    "arrow-leftward",
    "arrow-rightward",
    "arrow-up",
    "arrow-down",
    "arrow-left",
    "arrow-right",
    "rmb",
    "rmb-circle",
    "rmb-circle-fill",
    "thumb-up",
    "thumb-up-fill",
    "thumb-down",
    "thumb-down-fill",
    "coupon",
    "coupon-fill",
    "kefu-ermai",
    "server-fill",
    "server-man",
    "scan",
    "warning",
    "warning-fill",
    "google",
    "google-circle-fill",
    "chrome-circle-fill",
    "ie",
    "IE-circle-fill",
    "github-circle-fill",
    "android-fill",
    "android-circle-fill",
    "apple-fill",
    "camera",
    "camera-fill",
    "pushpin",
    "pushpin-fill",
    "minus-square-fill",
    "plus-square-fill",
    "heart",
    "heart-fill",
    "reload",
    "account",
    "account-fill",
    "minus-people-fill",
    "plus-people-fill",
    "integral",
    "integral-fill",
    "zhihu",
    "zhihu-circle-fill",
    "gift",
    "gift-fill",
    "zhifubao",
    "zhifubao-circle-fill",
    "weixin-fill",
    "weixin-circle-fill",
    "twitter",
    "twitter-circle-fill",
    "taobao",
    "taobao-circle-fill",
    "weibo",
    "weibo-circle-fill",
    "qq-fill",
    "qq-circle-fill",
    "moments",
    "moments-circel-fill",
    "qzone",
    "qzone-circle-fill",
    "facebook",
    "facebook-circle-fill",
    "baidu",
    "baidu-circle-fill",
    "share-square",
)
