package net.lingyun.ultraui.android.components

import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPCompatibilityEvent
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.math.abs

@RunWith(AndroidJUnit4::class)
class UPTabbarBehaviorTest {
    @get:Rule val composeRule = createComposeRule()

    private fun node(tag: String) = composeRule.onNodeWithTag(tag, useUnmergedTree = true)

    private fun topOf(tag: String): Float =
        node(tag).fetchSemanticsNode().boundsInRoot.top

    @Test
    fun styleTypeCreatesStyleStructureAndPerStateBubbles() {
        composeRule.setContent {
            UPTabbar(
                props = UPTabbarProps(
                    value = "home",
                    styleType = "pill",
                    itemShape = "round",
                    activeBackgroundColor = "#ff0000",
                    inactiveBackgroundColor = "#00ff00",
                    safeAreaInsetBottom = false,
                ),
            ) {
                UPTabbarItem(UPTabbarItemProps(name = "home", icon = "home", text = "首页"))
                UPTabbarItem(UPTabbarItemProps(name = "settings", icon = "setting", text = "设置"))
            }
        }

        node("up-tabbar-style-pill").assertExists()
        node("up-tabbar-item-home-bubble").assertExists()
        node("up-tabbar-item-settings-bubble").assertExists()
        node("up-tabbar-item-home-bubble-shape-round").assertExists()
        node("up-tabbar-item-settings-bubble-shape-round").assertExists()
    }

    @Test
    fun underlineIndicatorBelongsOnlyToTheActiveItem() {
        composeRule.setContent {
            UPTabbar(
                props = UPTabbarProps(value = "home", styleType = "underline", safeAreaInsetBottom = false),
            ) {
                UPTabbarItem(UPTabbarItemProps(name = "home", icon = "home"))
                UPTabbarItem(UPTabbarItemProps(name = "settings", icon = "setting"))
            }
        }

        node("up-tabbar-item-home-underline").assertExists()
        node("up-tabbar-item-settings-underline").assertDoesNotExist()
    }

    @Test
    fun dotIndicatorBelongsOnlyToTheActiveItem() {
        composeRule.setContent {
            UPTabbar(
                props = UPTabbarProps(value = "settings", styleType = "dot", safeAreaInsetBottom = false),
            ) {
                UPTabbarItem(UPTabbarItemProps(name = "home", icon = "home"))
                UPTabbarItem(UPTabbarItemProps(name = "settings", icon = "setting"))
            }
        }

        node("up-tabbar-item-settings-dot").assertExists()
        node("up-tabbar-item-home-dot").assertDoesNotExist()
    }

    @Test
    fun liftAnimationMovesAndScalesOnlyTheActiveIcon() {
        composeRule.setContent {
            UPTabbar(
                props = UPTabbarProps(
                    value = "home",
                    animationType = "lift",
                    iconScale = 1.25,
                    safeAreaInsetBottom = false,
                ),
            ) {
                UPTabbarItem(UPTabbarItemProps(name = "home", icon = "home"))
                UPTabbarItem(UPTabbarItemProps(name = "settings", icon = "setting"))
            }
        }

        val activeTop = topOf("up-tabbar-item-home-icon")
        val inactiveTop = topOf("up-tabbar-item-settings-icon")
        assertTrue("active icon should be lifted", activeTop < inactiveTop)
        node("up-tabbar-item-home-icon-animation-lift").assertExists()
        node("up-tabbar-item-settings-icon-animation-lift").assertDoesNotExist()
    }

    @Test
    fun middleButtonUsesConfiguredLayersAndVerticalOffset() {
        composeRule.setContent {
            UPTabbar(
                props = UPTabbarProps(
                    value = "add",
                    styleType = "convex",
                    border = false,
                    safeAreaInsetBottom = false,
                ),
            ) {
                UPTabbarItem(
                    UPTabbarItemProps(
                        name = "add",
                        mode = "midButton",
                        icon = "plus",
                        midButtonBgColor = "#ffffff",
                        midButtonIconColor = "#ff0000",
                        midButtonBoxShadow = "0 4px 8px rgba(0,0,0,0.25)",
                        midButtonInnerBoxShadow = "0 1px 2px rgba(0,0,0,0.12)",
                        midButtonOffsetY = -18,
                    ),
                )
            }
        }

        node("up-tabbar-item-add-mid-button")
            .assertWidthIsEqualTo(64.dp)
            .assertHeightIsEqualTo(64.dp)
        node("up-tabbar-item-add-mid-button-inner")
            .assertWidthIsEqualTo(52.dp)
            .assertHeightIsEqualTo(52.dp)
        assertTrue(
            "middle button should be offset above its item",
            topOf("up-tabbar-item-add-mid-button") < topOf("up-tabbar-item-add"),
        )
    }

    @Test
    fun classHooksReportNativeCompatibilityDowngradeAndExposeState() {
        val events = mutableListOf<UPCompatibilityEvent>()
        val diagnostics = UPCompatibilityDiagnostics { events += it }

        composeRule.setContent {
            UPTabbar(
                props = UPTabbarProps(value = "home", safeAreaInsetBottom = false),
                diagnostics = diagnostics,
            ) {
                UPTabbarItem(
                    UPTabbarItemProps(name = "home", icon = "home", activeClass = "web-active"),
                )
                UPTabbarItem(
                    UPTabbarItemProps(name = "settings", icon = "setting", inactiveClass = "web-inactive"),
                )
            }
        }

        node("up-tabbar-item-home-content-active").assertExists()
        node("up-tabbar-item-settings-content-inactive").assertExists()
        composeRule.runOnIdle {
            assertTrue(events.any { it.property == "activeClass" && it.value == "web-active" })
            assertTrue(events.any { it.property == "inactiveClass" && it.value == "web-inactive" })
            assertTrue(events.filter { it.property.endsWith("Class") }.all { it.reason.contains("native", ignoreCase = true) })
        }
    }

    @Test
    fun defaultTabbarWrapsContentInsteadOfFillingParentHeight() {
        composeRule.setContent {
            Box(Modifier.height(200.dp)) {
                UPTabbar(UPTabbarProps(value = "home", safeAreaInsetBottom = false)) {
                    UPTabbarItem(UPTabbarItemProps(name = "home", icon = "home", text = "首页"))
                    UPTabbarItem(UPTabbarItemProps(name = "mine", icon = "account", text = "我的"))
                }
            }
        }

        val tabbarHeight = node("up-tabbar").fetchSemanticsNode().boundsInRoot.height
        with(composeRule.density) {
            assertTrue(
                "default tabbar should wrap its content",
                tabbarHeight < 100.dp.toPx(),
            )
        }
    }

    @Test
    fun defaultTabbarCentersIconsWithinEachItem() {
        composeRule.setContent {
            UPTabbar(UPTabbarProps(value = "home", safeAreaInsetBottom = false)) {
                UPTabbarItem(UPTabbarItemProps(name = "home", icon = "home", text = "首页"))
                UPTabbarItem(UPTabbarItemProps(name = "mine", icon = "account", text = "我的"))
            }
        }

        val textBounds = node("up-tabbar-item-home-text-active").fetchSemanticsNode().boundsInRoot
        val iconBounds = composeRule
            .onNodeWithContentDescription("u-icon: home", useUnmergedTree = true)
            .fetchSemanticsNode()
            .boundsInRoot
        val itemCenter = (textBounds.left + textBounds.right) / 2f
        val iconCenter = (iconBounds.left + iconBounds.right) / 2f
        with(composeRule.density) {
            assertTrue(
                "icon should align with its label: label=$textBounds icon=$iconBounds",
                abs(itemCenter - iconCenter) < 2.dp.toPx(),
            )
        }
    }
}
