package net.lingyun.ultraui.android.components

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import net.lingyun.ultraui.android.core.UPImageLoaders
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UPContentBehaviorTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun imageFailureUsesErrorFallbackAndReportsIt() {
        var errors = 0
        composeRule.setContent {
            UPImage(
                props = UPImageProps(src = "missing", showError = true),
                loader = UPImageLoaders.Empty,
                onError = { errors += 1 },
            )
        }

        composeRule.onNodeWithTag("up-image-error", useUnmergedTree = true).assertExists()
        composeRule.waitUntil(timeoutMillis = 5_000) { errors == 1 }
        // The image root is clickable and therefore merges descendants. The
        // fallback marker intentionally remains available in the unmerged tree.
        composeRule.onNodeWithTag("up-image-error", useUnmergedTree = true).assertExists()
        composeRule.runOnIdle { assertEquals(1, errors) }
    }

    @Test
    fun avatarGroupShowsOverflowAndLoadmoreOnlyEmitsWhenActionable() {
        var showMore = 0
        var loadmore = 0
        composeRule.setContent {
            Column {
                UPAvatarGroup(
                    props = UPAvatarGroupProps(urls = listOf("a", "b", "c"), maxCount = 2, extraValue = "+1"),
                    onShowMore = { showMore += 1 },
                )
                UPLoadmore(UPLoadmoreProps(status = "loadmore"), onLoadmore = { loadmore += 1 })
            }
        }

        composeRule.onNodeWithTag("up-avatar-more").performClick()
        composeRule.onNodeWithTag("up-loadmore").performClick()
        composeRule.runOnIdle {
            assertEquals(1, showMore)
            assertEquals(1, loadmore)
        }
    }

    @Test
    fun loadingPageAndHiddenEmptyFollowShowFlags() {
        var loading by mutableStateOf(false)
        var emptyShown by mutableStateOf(false)
        composeRule.setContent {
            UPLoadingPage(UPLoadingPageProps(loading = loading))
            UPEmpty(UPEmptyProps(show = emptyShown, text = "暂无数据"))
        }
        composeRule.onNodeWithTag("up-loading-page").assertDoesNotExist()
        composeRule.onNodeWithTag("up-empty").assertDoesNotExist()

        composeRule.runOnIdle {
            loading = true
            emptyShown = true
        }
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("up-loading-page").assertExists()
        composeRule.onNodeWithTag("up-empty").assertExists()
    }
}
