package net.lingyun.ultraui.android.core

/** A non-fatal compatibility downgrade or malformed generated value. */
public data class UPCompatibilityEvent(
    val component: String,
    val property: String,
    val value: UPRawValue,
    val reason: String,
)

/**
 * Optional reporting hook for values that uview-plus accepts but Android cannot
 * render or apply exactly. The default is intentionally silent and never throws.
 */
public fun interface UPCompatibilityDiagnostics {
    public fun report(event: UPCompatibilityEvent)

    public companion object {
        public val None: UPCompatibilityDiagnostics = UPCompatibilityDiagnostics { }
    }
}

internal fun UPCompatibilityDiagnostics.report(
    component: String,
    property: String,
    value: UPRawValue,
    reason: String,
) {
    report(
        UPCompatibilityEvent(
            component = component,
            property = property,
            value = value,
            reason = reason,
        ),
    )
}
