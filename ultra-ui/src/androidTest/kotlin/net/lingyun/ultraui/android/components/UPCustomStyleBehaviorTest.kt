package net.lingyun.ultraui.android.components

import androidx.compose.foundation.layout.Column
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import net.lingyun.ultraui.android.core.UPRawValue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * `customStyle` is part of every generated component's contract, but a component
 * that never reads it fails silently: the value type-checks, props tests pass and
 * no screenshot moves. These assertions pin the styling down to rendered geometry
 * for the components that used to drop it.
 */
@RunWith(AndroidJUnit4::class)
class UPCustomStyleBehaviorTest {
    @get:Rule val composeRule = createComposeRule()

    private val tallStyle: Map<String, UPRawValue> = mapOf("height" to "120px")

    @Test
    fun switchAppliesCustomStyleToItsRoot() {
        composeRule.setContent { UPSwitch(UPSwitchProps(modelValue = true, customStyle = tallStyle)) }
        composeRule.onNodeWithTag("up-switch").assertHeightIsAtLeast(120.dp)
    }

    @Test
    fun rateAppliesCustomStyleToItsRoot() {
        composeRule.setContent { UPRate(UPRateProps(modelValue = 3, customStyle = tallStyle)) }
        composeRule.onNodeWithTag("up-rate").assertHeightIsAtLeast(120.dp)
    }

    @Test
    fun avatarGroupAppliesCustomStyleToItsRoot() {
        composeRule.setContent {
            UPAvatarGroup(UPAvatarGroupProps(urls = listOf("a", "b"), customStyle = tallStyle))
        }
        composeRule.onNodeWithTag("up-avatar-group").assertHeightIsAtLeast(120.dp)
    }

    @Test
    fun collapseAppliesCustomStyleToItsRoot() {
        composeRule.setContent {
            UPCollapse(UPCollapseProps(customStyle = tallStyle)) { Column {} }
        }
        composeRule.onNodeWithTag("up-collapse").assertHeightIsAtLeast(120.dp)
    }

    @Test
    fun stickyOffsetsItsContentByOffsetTopPlusNavHeight() {
        // uview: stickyTop = getPx(offsetTop) + getPx(customNavHeight)
        composeRule.setContent {
            UPSticky(UPStickyProps(offsetTop = 40, customNavHeight = 30)) { UPGap(UPGapProps(height = 20)) }
        }
        composeRule.onNodeWithTag("up-sticky").assertHeightIsAtLeast(90.dp)
    }
}
