package net.lingyun.ultraui.android.core

import android.os.SystemClock
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.size
import androidx.compose.ui.composed
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Reports an unsupported string enum and returns a safe fallback. */
public fun upSafeEnum(
    value: String,
    allowed: Set<String>,
    fallback: String,
    diagnostics: UPCompatibilityDiagnostics,
    component: String,
    property: String,
): String {
    val normalized = value.trim().lowercase()
    if (normalized in allowed) return normalized
    diagnostics.report(component, property, value, "Unsupported value; using $fallback.")
    return fallback
}

/** Resolves a uview dimension against the current screen width. */
@Composable
public fun upDimension(value: UPRawValue, fallback: Dp = 0.dp): Dp =
    androidx.compose.ui.platform.LocalConfiguration.current.screenWidthDp
        .takeIf { it > 0 }
        ?.dp
        ?.let { UPUnit.toDp(value, it, fallback) }
        ?: fallback

/** Adds a stable semantic test tag used by behavior and screenshot tests. */
public fun Modifier.upTestTag(name: String): Modifier = testTag("up-$name")

/** Applies a click callback with optional uview-style monotonic throttling. */
public fun Modifier.upClickable(
    enabled: Boolean = true,
    throttleMillis: Long = 0L,
    role: Role? = Role.Button,
    onClick: () -> Unit,
): Modifier = composed {
    val lastClick = remember { longArrayOf(Long.MIN_VALUE) }
    clickable(
        enabled = enabled,
        role = role,
        onClick = {
            val now = SystemClock.elapsedRealtime()
            if (throttleMillis <= 0L || now - lastClick[0] >= throttleMillis) {
                lastClick[0] = now
                onClick()
            }
        },
    )
}

/** Adds width/height only when values are meaningful. */
public fun Modifier.upSize(width: Dp? = null, height: Dp? = null): Modifier = when {
    width != null && height != null -> size(width, height)
    width != null -> this.width(width)
    height != null -> this.height(height)
    else -> this
}

