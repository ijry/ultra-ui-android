package net.lingyun.ultraui.android.components

import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UPLayerBehaviorTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun hiddenLayersDoNotCreateSemanticsNodes() {
        composeRule.setContent {
            UPOverlay(UPOverlayProps(show = false))
            UPPopup(UPPopupProps(show = false)) {}
            UPModal(UPModalProps(show = false))
            UPToast(UPToastProps(show = false))
        }

        composeRule.onNodeWithTag("up-overlay").assertDoesNotExist()
        composeRule.onNodeWithTag("up-popup").assertDoesNotExist()
        composeRule.onNodeWithTag("up-modal").assertDoesNotExist()
        composeRule.onNodeWithTag("up-toast").assertDoesNotExist()
    }

    @Test
    fun overlayAndPopupEmitDismissalContracts() {
        var overlayClicks = 0
        var updated: Boolean? = null
        var closes = 0
        composeRule.setContent {
            UPOverlay(UPOverlayProps(show = true), onClick = { overlayClicks += 1 })
            UPPopup(
                props = UPPopupProps(show = true, closeOnClickOverlay = true),
                onUpdateShow = { updated = it },
                onClose = { closes += 1 },
            ) {}
        }

        // Both full-screen layers intentionally overlap; invoke the semantic
        // action directly so this test checks each callback contract rather
        // than z-order hit testing.
        composeRule.onNodeWithTag("up-overlay").performSemanticsAction(SemanticsActions.OnClick) { it.invoke() }
        composeRule.onNodeWithTag("up-popup-overlay").performSemanticsAction(SemanticsActions.OnClick) { it.invoke() }
        composeRule.runOnIdle {
            assertEquals(1, overlayClicks)
            assertEquals(false, updated)
            assertEquals(1, closes)
        }
    }

    @Test
    fun modalButtonsAndCellReturnExpectedEvents() {
        var confirms = 0
        var cancels = 0
        var cellName: Any? = null
        composeRule.setContent {
            UPModal(
                props = UPModalProps(show = true, showCancelButton = true),
                onConfirm = { confirms += 1 },
                onCancel = { cancels += 1 },
            )
            UPCell(UPCellProps(title = "设置", name = "settings"), onClick = { cellName = it })
        }

        composeRule.onNodeWithTag("up-modal-confirm").performClick()
        composeRule.onNodeWithTag("up-modal-cancel").performClick()
        composeRule.onNodeWithTag("up-cell").performSemanticsAction(SemanticsActions.OnClick) { it.invoke() }
        composeRule.runOnIdle {
            assertEquals(1, confirms)
            assertEquals(1, cancels)
            assertEquals("settings", cellName)
        }
    }

    @Test
    fun toastWithPersistentDurationRemainsVisible() {
        composeRule.setContent {
            UPToast(UPToastProps(show = true, message = "正在保存", duration = -1))
        }

        composeRule.onNodeWithTag("up-toast").assertExists()
    }
}
