package net.lingyun.ultraui.android.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * `up-choose`: renders one tag per option using `labelName`, and a tap emits update:modelValue
 * or custom-click depending on `customClick`.
 */
@RunWith(AndroidJUnit4::class)
class UPChooseBehaviorTest {
    @get:Rule val composeRule = createComposeRule()

    private val options = listOf(
        mapOf("title" to "北京", "value" to "bj"),
        mapOf("title" to "上海", "value" to "sh"),
        mapOf("title" to "广州", "value" to "gz"),
    )

    @Test
    fun tapSelectsAndEmitsModelValueIndex() {
        val updates = mutableListOf<Int>()
        composeRule.setContent {
            UPChoose(
                UPChooseProps(options = options, modelValue = 0),
                onUpdateModelValue = { updates += it },
            )
        }

        composeRule.onNodeWithText("北京").assertExists()
        composeRule.onNodeWithText("上海").assertExists()
        composeRule.onNodeWithText("广州").performClick()
        composeRule.runOnIdle { assertEquals(listOf(2), updates) }
    }

    @Test
    fun customClickEmitsCustomClickInsteadOfSelecting() {
        val custom = mutableListOf<Int>()
        val updates = mutableListOf<Int>()
        composeRule.setContent {
            UPChoose(
                UPChooseProps(options = options, customClick = true),
                onUpdateModelValue = { updates += it },
                onCustomClick = { custom += it },
            )
        }

        composeRule.onNodeWithText("上海").performClick()
        composeRule.runOnIdle {
            assertEquals(listOf(1), custom)
            assertEquals(emptyList<Int>(), updates)
        }
    }
}
