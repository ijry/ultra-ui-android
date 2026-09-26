package net.lingyun.ultraui.android.sample

/** Route identifiers for the uview-plus-style demo app: a component index and per-component demo pages. */
public object SampleRoutes {
    public const val Index: String = "index"
    public const val Demo: String = "demo/{id}"
    public fun demoRoute(id: String): String = "demo/$id"
}
