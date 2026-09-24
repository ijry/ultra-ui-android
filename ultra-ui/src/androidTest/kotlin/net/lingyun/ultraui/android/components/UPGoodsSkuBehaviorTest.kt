package net.lingyun.ultraui.android.components

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

/**
 * `up-goods-sku`: choosing every attribute updates price/stock and enables confirm.
 */
@RunWith(AndroidJUnit4::class)
class UPGoodsSkuBehaviorTest {
    @get:Rule val composeRule = createComposeRule()

    private val tree = listOf<Any?>(
        mapOf("label" to "颜色", "name" to "color", "children" to listOf<Any?>(mapOf("id" to "r", "name" to "红色"), mapOf("id" to "b", "name" to "蓝色"))),
        mapOf("label" to "尺寸", "name" to "size", "children" to listOf<Any?>(mapOf("id" to "s", "name" to "S码"))),
    )
    private val list = listOf<Any?>(
        mapOf("color" to "r", "size" to "s", "price" to 88, "stock" to 5),
    )

    @Test
    fun selectingAllAttributesEnablesConfirmAndReportsSku() {
        var confirmed: Triple<Map<String, UPRawValue>?, Int, String>? = null
        composeRule.setContent {
            UPGoodsSku(
                UPGoodsSkuProps(show = true, skuTree = tree, skuList = list, goodsInfo = mapOf("price" to 99, "stock" to 10)),
                onConfirm = { sku, num, text -> confirmed = Triple(sku, num, text) },
            )
        }

        composeRule.onNodeWithTag("up-goods-sku").assertExists()
        // Confirm before full selection does nothing.
        composeRule.onNodeWithTag("up-goods-sku-confirm").performClick()
        composeRule.runOnIdle { assertEquals(null, confirmed) }

        composeRule.onNodeWithTag("up-goods-sku-leaf-r").performClick()
        composeRule.onNodeWithTag("up-goods-sku-leaf-s").performClick()
        // Price now reflects the matched combination.
        composeRule.onNodeWithText("88").assertExists()

        composeRule.onNodeWithTag("up-goods-sku-confirm").performClick()
        composeRule.runOnIdle {
            assertEquals(88, confirmed?.first?.get("price"))
            assertEquals("红色, S码", confirmed?.third)
        }
    }
}
