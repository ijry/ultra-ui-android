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
 * `u-city-locate`: the located-city header, city selection emit and the locate tap.
 */
@RunWith(AndroidJUnit4::class)
class UPCityLocateBehaviorTest {
    @get:Rule val composeRule = createComposeRule()

    private val indexList = listOf("🔥", "A")
    private val cityList = listOf(
        listOf(mapOf("name" to "北京", "value" to "beijing")),
        listOf(mapOf("name" to "安庆", "value" to "anqing")),
    )

    @Test
    fun showsCurrentCityAndEmitsSelectedCity() {
        var selected: Map<String, UPRawValue>? = null
        composeRule.setContent {
            UPCityLocate(
                UPCityLocateProps(indexList = indexList, cityList = cityList, currentCity = "上海"),
                onSelectCity = { selected = it },
            )
        }

        composeRule.onNodeWithText("上海").assertExists()
        composeRule.onNodeWithText("安庆").performClick()
        composeRule.runOnIdle { assertEquals("安庆", selected?.get("name")) }
    }

    @Test
    fun tappingCurrentCityTriggersLocate() {
        var located = 0
        composeRule.setContent {
            UPCityLocate(
                UPCityLocateProps(indexList = indexList, cityList = cityList, currentCity = "上海"),
                onLocate = { located += 1 },
            )
        }

        composeRule.onNodeWithTag("up-city-locate-current").performClick()
        composeRule.runOnIdle { assertEquals(1, located) }
    }
}
