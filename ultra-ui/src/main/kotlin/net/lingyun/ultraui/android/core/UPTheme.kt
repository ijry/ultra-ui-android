package net.lingyun.ultraui.android.core

import androidx.compose.ui.graphics.Color

/** uview-plus JavaScript configuration colors, preserved for generated source. */
public object UPTheme {
    public val Primary: Color = Color(0xFF2979FF)
    public val Warning: Color = Color(0xFFFF9900)
    public val Success: Color = Color(0xFF19BE6B)
    public val Error: Color = Color(0xFFFA3534)
    public val Info: Color = Color(0xFF909399)
    public val Main: Color = Color(0xFF303133)
    public val Content: Color = Color(0xFF606266)
    public val Tips: Color = Color(0xFF909399)
    public val Light: Color = Color(0xFFC0C4CC)
    public val Border: Color = Color(0xFFE4E7ED)
    public val Background: Color = Color(0xFFF3F4F6)
    public val Disabled: Color = Color(0xFFC8C9CC)

    private val namedColors: Map<String, Color> = mapOf(
        "primary" to Primary,
        "warning" to Warning,
        "success" to Success,
        "error" to Error,
        "info" to Info,
        "main" to Main,
        "content" to Content,
        "tips" to Tips,
        "light" to Light,
        "border" to Border,
        "background" to Background,
        "disabled" to Disabled,
        "main-color" to Main,
        "content-color" to Content,
        "tips-color" to Tips,
        "light-color" to Light,
        "border-color" to Border,
        "bg-color" to Background,
        "disabled-color" to Disabled,
    )

    /** Resolves uview theme aliases such as `u-primary` and `up-content-color`. */
    public fun colorFor(value: String): Color? {
        val normalized = value.trim().lowercase()
            .removePrefix("--")
            .removePrefix("up-")
            .removePrefix("u-")
        return namedColors[normalized]
    }
}
