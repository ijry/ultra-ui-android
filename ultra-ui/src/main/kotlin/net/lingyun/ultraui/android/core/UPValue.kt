package net.lingyun.ultraui.android.core

/** A raw value emitted by a cross-platform source generator. */
public typealias UPRawValue = Any?

/** The `customStyle` representation accepted by uview-plus props. */
public typealias UPStyleInput = Any?

internal fun UPRawValue.asFiniteFloatOrNull(): Float? = when (this) {
    is Number -> toFloat().takeIf(Float::isFinite)
    is String -> trim().toFloatOrNull()?.takeIf(Float::isFinite)
    else -> null
}
