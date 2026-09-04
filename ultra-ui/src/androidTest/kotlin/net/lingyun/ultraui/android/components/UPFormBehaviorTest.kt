package net.lingyun.ultraui.android.components

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import net.lingyun.ultraui.android.core.UPRawValue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.math.abs

@RunWith(AndroidJUnit4::class)
class UPFormBehaviorTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun validateRendersTheRequiredMessageUntilClearValidate() {
        val controller = UPFormController()
        composeRule.setContent {
            UPForm(
                props = UPFormProps(
                    model = mapOf<String, UPRawValue>("name" to ""),
                    rules = mapOf<String, UPRawValue>(
                        "name" to UPFormRule(required = true, message = "请输入姓名"),
                    ),
                ),
                controller = controller,
            ) {
                UPFormItem(props = UPFormItemProps(label = "姓名", prop = "name"))
            }
        }

        composeRule.onNodeWithTag("up-form").assertExists()
        composeRule.onNodeWithTag("up-form-item-message", useUnmergedTree = true).assertDoesNotExist()

        composeRule.runOnIdle {
            assertEquals(listOf("请输入姓名"), controller.validate().map { it.message })
        }
        composeRule.onNodeWithTag("up-form-item-message", useUnmergedTree = true)
            .assertTextEquals("请输入姓名")

        composeRule.runOnIdle { controller.clearValidate() }
        composeRule.onNodeWithTag("up-form-item-message", useUnmergedTree = true).assertDoesNotExist()
    }

    @Test
    fun validateReportsTheTrailingPathSegmentAsTheFieldName() {
        val controller = UPFormController()
        composeRule.setContent {
            UPForm(
                props = UPFormProps(
                    model = mapOf<String, UPRawValue>(
                        "userInfo" to mapOf<String, UPRawValue>("name" to ""),
                    ),
                    rules = mapOf<String, UPRawValue>(
                        "userInfo.name" to UPFormRule(required = true, message = "请输入姓名"),
                    ),
                ),
                controller = controller,
            ) {
                UPFormItem(props = UPFormItemProps(label = "姓名", prop = "userInfo.name"))
            }
        }

        composeRule.runOnIdle {
            val error = controller.validate().single()
            assertEquals("name", error.field)
            assertEquals("userInfo.name", error.prop)
            assertEquals("请输入姓名", error.message)
        }
    }

    @Test
    fun itemRulesWinOverTheFormLevelEntry() {
        val controller = UPFormController()
        composeRule.setContent {
            UPForm(
                props = UPFormProps(
                    model = mapOf<String, UPRawValue>("name" to ""),
                    rules = mapOf<String, UPRawValue>(
                        "name" to UPFormRule(required = true, message = "表单级规则"),
                    ),
                ),
                controller = controller,
            ) {
                UPFormItem(
                    props = UPFormItemProps(
                        label = "姓名",
                        prop = "name",
                        rules = listOf<UPRawValue>(UPFormRule(required = true, message = "条目级规则")),
                    ),
                )
            }
        }

        composeRule.runOnIdle { assertEquals("条目级规则", controller.validate().single().message) }
        composeRule.onNodeWithTag("up-form-item-message", useUnmergedTree = true)
            .assertTextEquals("条目级规则")
    }

    @Test
    fun itemRulesAloneValidateWithoutAnyFormLevelRule() {
        val controller = UPFormController()
        composeRule.setContent {
            UPForm(
                props = UPFormProps(model = mapOf<String, UPRawValue>("name" to "")),
                controller = controller,
            ) {
                UPFormItem(
                    props = UPFormItemProps(
                        label = "姓名",
                        prop = "name",
                        rules = listOf<UPRawValue>(UPFormRule(required = true, message = "仅条目级规则")),
                    ),
                )
            }
        }

        composeRule.runOnIdle { assertEquals("仅条目级规则", controller.validate().single().message) }
    }

    @Test
    fun aNamedEventOnlyRunsRulesThatDeclareTheTrigger() {
        val controller = UPFormController()
        composeRule.setContent {
            UPForm(
                props = UPFormProps(
                    model = mapOf<String, UPRawValue>("name" to ""),
                    rules = mapOf<String, UPRawValue>(
                        "name" to UPFormRule(
                            required = true,
                            message = "请输入姓名",
                            trigger = listOf("change"),
                        ),
                    ),
                ),
                controller = controller,
            ) {
                UPFormItem(props = UPFormItemProps(label = "姓名", prop = "name"))
            }
        }

        composeRule.runOnIdle {
            assertTrue(controller.validateField(listOf("name"), "blur").isEmpty())
        }
        composeRule.onNodeWithTag("up-form-item-message", useUnmergedTree = true).assertDoesNotExist()

        composeRule.runOnIdle {
            val messages = controller.validateField(listOf("name"), "change").map { it.message }
            assertEquals(listOf("请输入姓名"), messages)
        }
        composeRule.onNodeWithTag("up-form-item-message", useUnmergedTree = true)
            .assertTextEquals("请输入姓名")
    }

    @Test
    fun toastErrorTypeForwardsTheFirstMessageInsteadOfRenderingIt() {
        val controller = UPFormController()
        val toasts = mutableListOf<String>()
        composeRule.setContent {
            UPForm(
                props = UPFormProps(
                    model = mapOf<String, UPRawValue>("name" to "", "note" to ""),
                    rules = mapOf<String, UPRawValue>(
                        "name" to UPFormRule(required = true, message = "请输入姓名"),
                        "note" to UPFormRule(required = true, message = "请输入备注"),
                    ),
                    errorType = "toast",
                ),
                controller = controller,
                onToast = { toasts += it },
            ) {
                UPFormItem(props = UPFormItemProps(label = "姓名", prop = "name"))
                UPFormItem(props = UPFormItemProps(label = "备注", prop = "note"))
            }
        }

        composeRule.runOnIdle { assertEquals(2, controller.validate().size) }
        composeRule.onAllNodesWithTag("up-form-item-message", useUnmergedTree = true)
            .assertCountEquals(0)
        composeRule.runOnIdle { assertEquals(listOf("请输入姓名"), toasts) }
    }

    @Test
    fun labelPositionDrivesTheMessageIndent() {
        val controller = UPFormController()
        composeRule.setContent {
            UPForm(
                props = UPFormProps(
                    model = mapOf<String, UPRawValue>("name" to "", "note" to ""),
                    rules = mapOf<String, UPRawValue>(
                        "name" to UPFormRule(required = true, message = "请输入内容"),
                        "note" to UPFormRule(required = true, message = "请输入内容"),
                    ),
                    labelWidth = 100,
                ),
                controller = controller,
            ) {
                UPFormItem(props = UPFormItemProps(label = "姓名", prop = "name"))
                UPFormItem(props = UPFormItemProps(label = "备注", prop = "note", labelPosition = "top"))
            }
        }

        composeRule.runOnIdle { assertEquals(2, controller.validate().size) }
        val messages = composeRule.onAllNodesWithTag("up-form-item-message", useUnmergedTree = true)
        messages.assertCountEquals(2)
        // `padding(start = labelWidth)` shifts the whole text box, so comparing the
        // right edge of the identical string keeps the assertion independent of how
        // the test framework reports leading padding.
        val indented = messages[0].getUnclippedBoundsInRoot().right
        val flush = messages[1].getUnclippedBoundsInRoot().right
        assertTrue(
            "indented=$indented flush=$flush",
            abs((indented - flush).value - 100f) < 2f,
        )
    }

    @Test
    fun itemLabelWidthOverridesTheFormLevelWidth() {
        composeRule.setContent {
            UPForm(props = UPFormProps(labelWidth = 100)) {
                UPFormItem(props = UPFormItemProps(label = "姓名", prop = "name", labelWidth = "60px"))
                UPFormItem(props = UPFormItemProps(label = "备注", prop = "note"))
            }
        }

        val labels = composeRule.onAllNodesWithTag("up-form-item-label", useUnmergedTree = true)
        labels.assertCountEquals(2)
        labels[0].assertWidthIsEqualTo(60.dp)
        labels[1].assertWidthIsEqualTo(100.dp)
    }

    @Test
    fun theRequiredStarNeverShiftsTheItemContent() {
        composeRule.setContent {
            UPForm(props = UPFormProps(labelWidth = 100)) {
                UPFormItem(props = UPFormItemProps(label = "姓名", prop = "name", required = true)) {
                    BasicText("必填项", modifier = Modifier.testTag("required-value"))
                }
                UPFormItem(props = UPFormItemProps(label = "备注", prop = "note")) {
                    BasicText("普通项", modifier = Modifier.testTag("plain-value"))
                }
            }
        }

        val labels = composeRule.onAllNodesWithTag("up-form-item-label", useUnmergedTree = true)
        labels[0].assertWidthIsEqualTo(100.dp)
        // `position: absolute; left: -9px`, so the star hangs outside the label box
        // instead of pushing the label or the body to the right.
        val star = composeRule.onNodeWithTag("up-form-item-required", useUnmergedTree = true)
            .getUnclippedBoundsInRoot()
        val label = labels[0].getUnclippedBoundsInRoot()
        assertTrue("star=$star label=$label", star.left <= label.left)
        val required = composeRule.onNodeWithTag("required-value", useUnmergedTree = true)
            .getUnclippedBoundsInRoot()
        val plain = composeRule.onNodeWithTag("plain-value", useUnmergedTree = true)
            .getUnclippedBoundsInRoot()
        assertTrue("required=$required plain=$plain", abs((required.left - plain.left).value) < 1f)
    }

    @Test
    fun onlyATruthyBorderBottomRendersTheSeparator() {
        composeRule.setContent {
            // `u-form` owns a `borderBottom` prop that defaults to true, but
            // `u-form-item` never reads it, so the second item stays borderless.
            UPForm(props = UPFormProps()) {
                UPFormItem(props = UPFormItemProps(label = "姓名", prop = "name", borderBottom = true))
                UPFormItem(props = UPFormItemProps(label = "备注", prop = "note"))
            }
        }

        composeRule.onAllNodesWithTag("up-line", useUnmergedTree = true).assertCountEquals(1)
    }

    @Test
    fun theItemBodyForwardsTaps() {
        var clicks = 0
        composeRule.setContent {
            UPForm(props = UPFormProps()) {
                UPFormItem(
                    props = UPFormItemProps(label = "姓名", prop = "name"),
                    onClick = { clicks += 1 },
                )
            }
        }

        composeRule.onNodeWithTag("up-form-item-body").performClick()
        composeRule.runOnIdle { assertEquals(1, clicks) }
    }

    @Test
    fun theLabelSlotReplacesTheLeftBlockAndTheRightSlotTrailsTheBody() {
        composeRule.setContent {
            UPForm(props = UPFormProps(labelWidth = 100)) {
                UPFormItem(
                    props = UPFormItemProps(label = "姓名", prop = "name", required = true),
                    labelContent = { BasicText("自定义标签", modifier = Modifier.testTag("custom-label")) },
                    rightContent = { BasicText("右侧", modifier = Modifier.testTag("custom-right")) },
                ) {
                    BasicText("输入区", modifier = Modifier.testTag("body-value"))
                }
            }
        }

        composeRule.onNodeWithTag("custom-label", useUnmergedTree = true).assertExists()
        composeRule.onNodeWithTag("up-form-item-right", useUnmergedTree = true).assertExists()
        // The upstream `label` slot swallows the required star and the left icon.
        composeRule.onNodeWithTag("up-form-item-required", useUnmergedTree = true).assertDoesNotExist()
        val body = composeRule.onNodeWithTag("body-value", useUnmergedTree = true)
            .getUnclippedBoundsInRoot()
        val right = composeRule.onNodeWithTag("custom-right", useUnmergedTree = true)
            .getUnclippedBoundsInRoot()
        assertTrue("body=$body right=$right", right.left >= body.right)
    }

    @Test
    fun theErrorSlotReplacesTheGeneratedMessageNode() {
        val controller = UPFormController()
        composeRule.setContent {
            UPForm(
                props = UPFormProps(
                    model = mapOf<String, UPRawValue>("name" to ""),
                    rules = mapOf<String, UPRawValue>(
                        "name" to UPFormRule(required = true, message = "请输入姓名"),
                    ),
                ),
                controller = controller,
            ) {
                UPFormItem(
                    props = UPFormItemProps(label = "姓名", prop = "name"),
                    errorContent = { BasicText("[$it]", modifier = Modifier.testTag("custom-error")) },
                )
            }
        }

        composeRule.onNodeWithTag("custom-error", useUnmergedTree = true).assertTextEquals("[]")
        composeRule.runOnIdle { controller.validate() }
        composeRule.onNodeWithTag("custom-error", useUnmergedTree = true).assertTextEquals("[请输入姓名]")
        composeRule.onNodeWithTag("up-form-item-message", useUnmergedTree = true).assertDoesNotExist()
    }

    @Test
    fun resetFieldsRestoresTheFirstObservedModelForRegisteredPropsOnly() {
        val controller = UPFormController()
        val model = mutableStateOf(mapOf<String, UPRawValue>("name" to "初始姓名", "note" to "初始备注"))
        composeRule.setContent {
            UPForm(
                props = UPFormProps(model = model.value),
                controller = controller,
                onUpdateModel = { model.value = it },
            ) {
                UPFormItem(props = UPFormItemProps(label = "姓名", prop = "name")) {
                    BasicText("${model.value["name"]}", modifier = Modifier.testTag("name-value"))
                }
            }
        }

        composeRule.runOnIdle {
            model.value = mapOf<String, UPRawValue>("name" to "改后姓名", "note" to "改后备注")
        }
        composeRule.onNodeWithTag("name-value", useUnmergedTree = true).assertTextEquals("改后姓名")

        composeRule.runOnIdle { controller.resetFields() }
        composeRule.onNodeWithTag("name-value", useUnmergedTree = true).assertTextEquals("初始姓名")
        // `resetModel()` walks the registered children, so an unregistered key keeps
        // whatever the caller last published.
        composeRule.runOnIdle { assertEquals("改后备注", model.value["note"]) }
    }

    @Test
    fun resetFieldClearsTheMessageWhileResetFieldsKeepsIt() {
        val controller = UPFormController()
        val model = mutableStateOf(mapOf<String, UPRawValue>("name" to ""))
        composeRule.setContent {
            UPForm(
                props = UPFormProps(
                    model = model.value,
                    rules = mapOf<String, UPRawValue>(
                        "name" to UPFormRule(required = true, message = "请输入姓名"),
                    ),
                ),
                controller = controller,
                onUpdateModel = { model.value = it },
            ) {
                UPFormItem(props = UPFormItemProps(label = "姓名", prop = "name"))
            }
        }

        composeRule.runOnIdle { controller.validate() }
        composeRule.onNodeWithTag("up-form-item-message", useUnmergedTree = true)
            .assertTextEquals("请输入姓名")

        // `resetFields()` delegates to `resetModel()`, which restores values without
        // clearing the error text; only the per item `resetField()` does both.
        composeRule.runOnIdle { controller.resetFields() }
        composeRule.onNodeWithTag("up-form-item-message", useUnmergedTree = true)
            .assertTextEquals("请输入姓名")

        composeRule.runOnIdle { controller.resetField("name") }
        composeRule.onNodeWithTag("up-form-item-message", useUnmergedTree = true).assertDoesNotExist()
    }

    @Test
    fun setRulesReplacesTheFormRulesAndIgnoresAnEmptyMap() {
        val controller = UPFormController()
        composeRule.setContent {
            UPForm(
                props = UPFormProps(model = mapOf<String, UPRawValue>("name" to "")),
                controller = controller,
            ) {
                UPFormItem(props = UPFormItemProps(label = "姓名", prop = "name"))
            }
        }

        composeRule.runOnIdle { assertTrue(controller.validate().isEmpty()) }
        composeRule.runOnIdle {
            controller.setRules(
                mapOf<String, UPRawValue>("name" to UPFormRule(required = true, message = "动态规则")),
            )
        }
        composeRule.runOnIdle { assertEquals("动态规则", controller.validate().single().message) }
        composeRule.onNodeWithTag("up-form-item-message", useUnmergedTree = true)
            .assertTextEquals("动态规则")

        // An empty map is ignored upstream, so the dynamic rules survive.
        composeRule.runOnIdle { controller.setRules(emptyMap()) }
        composeRule.runOnIdle { assertEquals("动态规则", controller.validate().single().message) }
    }
}
