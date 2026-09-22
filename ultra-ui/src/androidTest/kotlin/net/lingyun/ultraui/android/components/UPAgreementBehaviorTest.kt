package net.lingyun.ultraui.android.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * `up-agreement`: the modal stays hidden until `showModal()`, confirm emits `confirm` and
 * closes, and the default body shows the agree label.
 */
@RunWith(AndroidJUnit4::class)
class UPAgreementBehaviorTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun showModalOpensTheGateAndConfirmClosesIt() {
        val controller = UPAgreementController()
        var confirmed = 0
        composeRule.setContent {
            UPAgreement(controller = controller, onConfirm = { confirmed += 1 })
        }

        // Hidden until showModal().
        composeRule.onNodeWithTag("up-modal").assertDoesNotExist()

        composeRule.runOnIdle { controller.showModal() }
        composeRule.onNodeWithTag("up-agreement-content").assertExists()
        composeRule.onNodeWithText("阅读并同意").assertExists()

        composeRule.onNodeWithTag("up-modal-confirm").performClick()
        composeRule.runOnIdle { assertEquals(1, confirmed) }
        composeRule.onNodeWithTag("up-modal").assertDoesNotExist()
    }

    @Test
    fun cancelClosesAndReportsThroughOnClose() {
        val controller = UPAgreementController()
        var closed = 0
        composeRule.setContent {
            UPAgreement(controller = controller, onClose = { closed += 1 })
        }

        composeRule.runOnIdle { controller.showModal() }
        composeRule.onNodeWithTag("up-modal-cancel").performClick()
        composeRule.runOnIdle { assertEquals(1, closed) }
        composeRule.onNodeWithTag("up-modal").assertDoesNotExist()
    }
}
