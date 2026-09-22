package net.lingyun.ultraui.android.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPImageLoader
import net.lingyun.ultraui.android.core.UPImageLoaders
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.core.upTestTag

/**
 * Native Compose counterpart of uview-plus `u-no-network`.
 *
 * Upstream watches connectivity itself (`uni.onNetworkStatusChange`) and shows a full-screen
 * `u-overlay` with a `#fff` scrim whenever `isConnected` is false. Platform connectivity
 * observation on Android needs a `Context`/`ConnectivityManager` that the caller owns, so the
 * port lifts `isConnected` into the [connected] parameter and forwards the retry tap through
 * [onRetry]; the host re-checks the network and flips [connected] back, mirroring the upstream
 * `retry()` flow. When [connected] is true nothing renders, exactly like `:show="!isConnected"`.
 *
 * The upstream `APP-PLUS` "go to settings" row calls `plus.*` and only compiles on the App
 * runtime, so it has no Android counterpart and is intentionally omitted.
 */
@Composable
public fun UPNoNetwork(
    props: UPNoNetworkProps = UPNoNetworkProps(),
    connected: Boolean = true,
    modifier: Modifier = Modifier,
    onRetry: (() -> Unit)? = null,
    loader: UPImageLoader = UPImageLoaders.Android,
    diagnostics: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics.None,
) {
    if (connected) return
    UPOverlay(
        props = UPOverlayProps(show = true, zIndex = props.zIndex, customStyle = props.customStyle),
        modifier = modifier,
        diagnostics = diagnostics,
        content = {
        Column(
            modifier = Modifier.align(Alignment.Center).offsetUpNoNetwork().upTestTag("no-network"),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            // `<up-icon :name="image" size="150" imgMode="widthFit">`: the artwork is only
            // drawn when a source is supplied; upstream ships a base64 PNG, the port takes a
            // drawable path or URL from `image`.
            if (props.image.isNotEmpty()) {
                UPIcon(
                    UPIconProps(name = props.image, size = 150, imgMode = "widthFit"),
                    loader = loader,
                    diagnostics = diagnostics,
                )
            }
            // `.u-no-network__tips`: 14px tips colour, 15px above.
            BasicText(
                props.tips,
                modifier = Modifier.padding(top = 15.dp).upTestTag("no-network-tips"),
                style = TextStyle(color = UPTheme.Tips, fontSize = 14.sp),
            )
            // `.u-no-network__retry`: a mini plain primary button, 15px above.
            Column(modifier = Modifier.padding(top = 15.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                UPButton(
                    UPButtonProps(text = "重试", type = "primary", size = "mini", plain = true),
                    onClick = { onRetry?.invoke() },
                    modifier = Modifier.upTestTag("no-network-retry"),
                    diagnostics = diagnostics,
                )
            }
        }
        },
    )
}

/** `.u-no-network { margin-top: -100px }` nudges the stack above centre. */
private fun Modifier.offsetUpNoNetwork(): Modifier = this.padding(bottom = 100.dp)
