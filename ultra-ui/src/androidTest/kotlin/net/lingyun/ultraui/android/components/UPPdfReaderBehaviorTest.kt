package net.lingyun.ultraui.android.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * `u-pdf-reader`: hosts the pdf.js `WebView` surface. Only the container tag is asserted; the
 * viewer URL construction is covered by the pure `upPdfReaderViewerUrl` unit test.
 */
@RunWith(AndroidJUnit4::class)
class UPPdfReaderBehaviorTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun rendersPdfReaderContainer() {
        composeRule.setContent {
            UPPdfReader(props = UPPdfReaderProps(src = "https://example.com/a.pdf"))
        }

        composeRule.onNodeWithTag("up-pdf-reader").assertIsDisplayed()
    }
}
