package net.lingyun.ultraui.android.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import net.lingyun.ultraui.android.core.UPRawValue
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UPNativeInteractionBehaviorTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun alertClickAndCloseEmitControlledVisibilityEvents() {
        val updates = mutableListOf<Boolean>()
        var clicks = 0
        var closes = 0
        composeRule.setContent {
            UPAlert(
                props = UPAlertProps(
                    title = "系统提示",
                    description = "请检查网络",
                    closable = true,
                    modelValue = true,
                ),
                onUpdateModelValue = { updates += it },
                onClick = { clicks += 1 },
                onClose = { closes += 1 },
            )
        }

        composeRule.onNodeWithTag("up-alert").performClick()
        composeRule.onNodeWithTag("up-alert-close").performClick()
        composeRule.runOnIdle {
            assertEquals(1, clicks)
            assertEquals(1, closes)
            assertEquals(listOf(false), updates)
        }
    }

    @Test
    fun actionSheetSelectsRawActionAndOverlayCanClose() {
        val selected = mutableListOf<UPRawValue>()
        val updates = mutableListOf<Boolean>()
        var closes = 0
        val actions = listOf(
            mapOf("name" to "拍照", "subname" to "使用相机"),
            mapOf("name" to "相册", "disabled" to true),
        )
        composeRule.setContent {
            UPActionSheet(
                props = UPActionSheetProps(show = true, actions = actions),
                onSelect = { selected += it },
                onUpdateShow = { updates += it },
                onClose = { closes += 1 },
            )
        }

        composeRule.onNodeWithTag("up-action-sheet-item-0").performClick()
        composeRule.onNodeWithTag("up-action-sheet-overlay").performClick()
        composeRule.runOnIdle {
            assertEquals(listOf(actions[0]), selected)
            assertEquals(listOf(false, false), updates)
            assertEquals(2, closes)
        }
    }

    @Test
    fun notifyRendersMessageAndBackTopOnlyAppearsAfterThreshold() {
        var notifyClicks = 0
        var notifyCloses = 0
        var backTopClicks = 0
        var scrollTop by mutableStateOf(399)
        composeRule.setContent {
            Column {
                UPNotify(
                    props = UPNotifyProps(message = "保存成功", duration = -1),
                    onClick = { notifyClicks += 1 },
                    onClose = { notifyCloses += 1 },
                )
                UPBackTop(
                    props = UPBackTopProps(scrollTop = scrollTop),
                    onClick = { backTopClicks += 1 },
                )
            }
        }
        composeRule.onNodeWithTag("up-notify").performClick()
        composeRule.onNodeWithTag("up-notify-close").performClick()
        composeRule.onNodeWithTag("up-back-top").assertDoesNotExist()
        composeRule.runOnIdle {
            assertEquals(1, notifyClicks)
            assertEquals(1, notifyCloses)
            assertEquals(0, backTopClicks)
        }

        composeRule.runOnIdle { scrollTop = 401 }
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("up-back-top").performClick()
        composeRule.runOnIdle { assertEquals(1, backTopClicks) }
    }

    @Test
    fun cardEmitsItsIndexAndCollapseSupportsAccordionAndChangePayload() {
        var cardIndex: UPRawValue = null
        var openValue: UPRawValue = listOf("first")
        val changes = mutableListOf<UPRawValue>()
        val modelUpdates = mutableListOf<UPRawValue>()
        composeRule.setContent {
            Column {
                UPCard(
                    props = UPCardProps(title = "卡片", index = 7),
                    onClick = { cardIndex = it },
                ) {
                    BasicText("卡片内容")
                }
                UPCollapse(
                    props = UPCollapseProps(modelValue = openValue, accordion = true),
                    onChange = { next ->
                        changes += next
                    },
                    onUpdateModelValue = { next ->
                        modelUpdates += next
                        openValue = next
                    },
                ) {
                    UPCollapseItem(
                        props = UPCollapseItemProps(name = "first", title = "第一项"),
                    ) { BasicText("第一项内容") }
                    UPCollapseItem(
                        props = UPCollapseItemProps(name = "second", title = "第二项"),
                    ) { BasicText("第二项内容") }
                }
            }
        }

        composeRule.onNodeWithTag("up-card").performClick()
        composeRule.onNodeWithTag("up-collapse-item-second-header").performClick()
        composeRule.runOnIdle {
            assertEquals(7, cardIndex)
            assertEquals(
                listOf(
                    listOf(
                        mapOf("name" to "first", "status" to "close"),
                        mapOf("name" to "second", "status" to "open"),
                    ),
                ),
                changes,
            )
            assertEquals(listOf("second"), modelUpdates)
            assertEquals("second", openValue)
        }
    }

    @Test
    fun dropdownSelectsOptionAndNoticeBarEmitsClickAndClose() {
        var selected: UPRawValue = null
        var dropdownClosed = 0
        var noticeClickIndex: Int? = null
        var noticeCloses = 0
        val options = listOf(
            mapOf("label" to "全部", "value" to "all"),
            mapOf("label" to "已完成", "value" to "done"),
        )
        composeRule.setContent {
            Column {
                UPDropdown(
                    props = UPDropdownProps(),
                    onUpdateOpen = { open -> if (!open) dropdownClosed += 1 },
                ) {
                    UPDropdownItem(
                        props = UPDropdownItemProps(
                            title = "状态",
                            options = options,
                            modelValue = "",
                        ),
                        onUpdateModelValue = { selected = it },
                    )
                }
                UPNoticeBar(
                    props = UPNoticeBarProps(text = listOf("有新消息"), mode = "closable"),
                    onClick = { noticeClickIndex = it },
                    onClose = { noticeCloses += 1 },
                )
            }
        }

        composeRule.onNodeWithTag("up-dropdown-title-0").performClick()
        composeRule.onNodeWithTag("up-dropdown-option-0-1").performClick()
        composeRule.onNodeWithTag("up-notice-bar").performClick()
        composeRule.onNodeWithTag("up-notice-bar-close").performClick()
        composeRule.runOnIdle {
            assertEquals("done", selected)
            assertEquals(1, dropdownClosed)
            assertEquals(0, noticeClickIndex)
            assertEquals(1, noticeCloses)
        }
    }

    @Test
    fun invalidEnumsFallbackWithoutThrowing() {
        composeRule.setContent {
            Column {
                UPAlert(UPAlertProps(type = "not-a-type", effect = "not-an-effect"))
                UPDropdown(UPDropdownProps(menuIcon = "not-an-icon")) {
                    UPDropdownItem(UPDropdownItemProps(title = "菜单"))
                }
                UPNoticeBar(UPNoticeBarProps(direction = "diagonal", mode = "unknown", text = "兼容"))
            }
        }

        composeRule.onNodeWithTag("up-alert").assertExists()
        composeRule.onNodeWithTag("up-dropdown").assertExists()
        composeRule.onNodeWithTag("up-notice-bar").assertExists()
    }
}
