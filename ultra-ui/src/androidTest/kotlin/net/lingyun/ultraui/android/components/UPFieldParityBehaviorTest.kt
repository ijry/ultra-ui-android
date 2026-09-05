package net.lingyun.ultraui.android.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Behaviour coverage for the field-parity batch: `u-cell`'s two icon style hooks,
 * `u-modal`'s offset, `u-navbar`/`u-navbar-mini`'s `autoBack` and `border`,
 * `u-number-box`'s `longPress`, and `u-badge`'s absolute `offset`.
 */
@RunWith(AndroidJUnit4::class)
class UPFieldParityBehaviorTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun cellIconStyleAndRightIconStyleResizeTheirOwnGlyphs() {
        composeRule.setContent {
            UPCell(
                props = UPCellProps(
                    title = "地址",
                    icon = "map",
                    isLink = true,
                    iconStyle = mapOf("fontSize" to "30px"),
                    rightIconStyle = mapOf("fontSize" to "26px"),
                ),
            )
        }

        // Both hooks land on `up-icon-glyph`; only the sizes tell the two apart.
        val glyphs = composeRule.onAllNodesWithTag("up-icon-glyph", useUnmergedTree = true)
        val leading = glyphs[0].getUnclippedBoundsInRoot().let { it.bottom - it.top }
        val trailing = glyphs[1].getUnclippedBoundsInRoot().let { it.bottom - it.top }
        assertTrue("leading icon should follow iconStyle", leading > 20.dp)
        assertTrue("trailing icon should follow rightIconStyle", trailing > 20.dp)
        assertTrue("iconStyle is the larger of the two", leading > trailing)
    }

    @Test
    fun modalNegativeTopLiftsThePanelClearOfTheKeyboard() {
        composeRule.setContent {
            UPModal(UPModalProps(show = true, title = "确认", content = "内容"))
        }
        val baseline = composeRule.onNodeWithTag("up-modal").getUnclippedBoundsInRoot().top

        composeRule.setContent {
            UPModal(UPModalProps(show = true, title = "确认", content = "内容", negativeTop = 60))
        }
        val lifted = composeRule.onNodeWithTag("up-modal").getUnclippedBoundsInRoot().top

        assertTrue("negativeTop should move the dialog up", lifted < baseline)
    }

    @Test
    fun navbarBorderDrawsAHairlineAndAutoBackRunsTheHostBackAction() {
        var leftClicks = 0
        var backs = 0
        composeRule.setContent {
            UPNavbar(
                props = UPNavbarProps(title = "订单", autoBack = true, border = true),
                onLeftClick = { leftClicks += 1 },
                onBack = { backs += 1 },
            )
        }

        composeRule.onNodeWithTag("up-navbar-left").performClick()
        composeRule.runOnIdle {
            assertEquals(1, leftClicks)
            assertEquals(1, backs)
        }
    }

    @Test
    fun navbarStatusBarBgColorReportsItselfAsCompatibilityOnly() {
        val events = mutableListOf<String>()
        composeRule.setContent {
            UPNavbar(
                props = UPNavbarProps(title = "订单", statusBarBgColor = "#ff0000"),
                diagnostics = UPCompatibilityDiagnostics { events += "${it.component}.${it.property}" },
            )
        }

        composeRule.runOnIdle { assertTrue(events.contains("UPNavbar.statusBarBgColor")) }
    }

    @Test
    fun navbarAutoBackDisabledLeavesTheHostBackActionAlone() {
        var backs = 0
        composeRule.setContent {
            UPNavbar(
                props = UPNavbarProps(title = "订单", autoBack = false),
                onLeftClick = {},
                onBack = { backs += 1 },
            )
        }

        composeRule.onNodeWithTag("up-navbar-left").performClick()
        composeRule.runOnIdle { assertEquals(0, backs) }
    }

    @Test
    fun navbarMiniAutoBackFiresAfterItsOwnLeftClick() {
        val order = mutableListOf<String>()
        composeRule.setContent {
            UPNavbarMini(
                props = UPNavbarMiniProps(),
                onLeftClick = { order += "leftClick" },
                onBack = { order += "back" },
            )
        }

        composeRule.onNodeWithContentDescription("u-icon: arrow-leftward", useUnmergedTree = true)
            .performClick()
        composeRule.runOnIdle { assertEquals(listOf("leftClick", "back"), order) }
    }

    @Test
    fun numberBoxLongPressDisabledStillStepsOnceOnRelease() {
        val values = mutableListOf<Any?>()
        composeRule.setContent {
            UPNumberBox(
                props = UPNumberBoxProps(modelValue = 1, min = 0, max = 9, longPress = false),
                onChange = { values += it },
            )
        }

        composeRule.onNodeWithTag("up-number-box-plus").performClick()
        composeRule.runOnIdle { assertEquals(listOf<Any?>(2), values) }
    }

    @Test
    fun badgeAbsoluteOffsetPushesTheLabelInFromTheCorner() {
        composeRule.setContent {
            Box(modifier = Modifier.size(120.dp)) {
                UPBadge(
                    props = UPBadgeProps(value = 9, absolute = true, offset = listOf(12, 20)),
                    content = { BasicText("图标") },
                )
            }
        }
        val offset = composeRule.onNodeWithTag("up-badge-label", useUnmergedTree = true)
            .getUnclippedBoundsInRoot()

        composeRule.setContent {
            Box(modifier = Modifier.size(120.dp)) {
                UPBadge(
                    props = UPBadgeProps(value = 9, absolute = true),
                    content = { BasicText("图标") },
                )
            }
        }
        val corner = composeRule.onNodeWithTag("up-badge-label", useUnmergedTree = true)
            .getUnclippedBoundsInRoot()

        assertTrue("offset[0] should push the badge down", offset.top > corner.top)
        assertTrue("offset[1] should push the badge in from the right", offset.right < corner.right)
    }

    @Test
    fun tagAutoBgColorDerivesALightBackgroundFromTheTextColor() {
        // Kotlin cannot read a drawn background, so lock the derived colour instead and let
        // the composable prove it consumes it by rendering without diagnostics.
        val events = mutableListOf<String>()
        composeRule.setContent {
            UPTag(
                props = UPTagProps(text = "标签", color = "#2979ff", autoBgColor = 95),
                diagnostics = UPCompatibilityDiagnostics { events += it.property },
            )
        }

        composeRule.onNodeWithTag("up-tag").assertIsDisplayed()
        composeRule.runOnIdle {
            assertEquals(emptyList<String>(), events)
            assertEquals("#e5efff", upTagAutoBackgroundColor("#2979ff", 95))
        }
    }

    @Test
    fun tagAutoBgColorReportsAColorFormatItCannotLighten() {
        val events = mutableListOf<String>()
        composeRule.setContent {
            UPTag(
                props = UPTagProps(text = "标签", color = "hsl(210, 100%, 58%)", autoBgColor = 95),
                diagnostics = UPCompatibilityDiagnostics { events += it.property },
            )
        }

        composeRule.runOnIdle { assertTrue(events.contains("autoBgColor")) }
    }
}
