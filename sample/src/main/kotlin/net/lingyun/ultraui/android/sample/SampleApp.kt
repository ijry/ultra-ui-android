package net.lingyun.ultraui.android.sample

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.sample.pages.FoundationDemoPage
import net.lingyun.ultraui.android.sample.pages.IconDemoPage
import net.lingyun.ultraui.android.sample.pages.InputSelectionDemoPage
import net.lingyun.ultraui.android.sample.pages.LayerContentDemoPage
import net.lingyun.ultraui.android.sample.pages.LayoutProgressDemoPage
import net.lingyun.ultraui.android.sample.pages.LoadingIconDemoPage
import net.lingyun.ultraui.android.sample.pages.NativeInteractionDemoPage

private val sampleColorScheme = lightColorScheme(
    primary = UPTheme.Primary,
    onPrimary = Color.White,
    background = Color.White,
    onBackground = UPTheme.Main,
    surface = Color.White,
    onSurface = UPTheme.Main,
    surfaceVariant = UPTheme.Background,
    onSurfaceVariant = UPTheme.Content,
)

/** Root of the deterministic UltraUI Android sample app. */
@Composable
public fun SampleApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    MaterialTheme(colorScheme = sampleColorScheme) {
        Surface(modifier = modifier.fillMaxSize(), color = Color.White) {
            NavHost(
                navController = navController,
                startDestination = SampleRoutes.Catalog,
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.safeDrawing),
            ) {
                composable(SampleRoutes.Catalog) {
                    SampleCatalog(onDestinationClick = { destination ->
                        navController.navigate(destination.route)
                    })
                }
                composable(SampleRoutes.Foundation) {
                    FoundationDemoPage(onBack = { navController.popBackStack() })
                }
                composable(SampleRoutes.LayerContent) {
                    LayerContentDemoPage(onBack = { navController.popBackStack() })
                }
                composable(SampleRoutes.InputSelection) {
                    InputSelectionDemoPage(onBack = { navController.popBackStack() })
                }
                composable(SampleRoutes.LayoutProgress) {
                    LayoutProgressDemoPage(onBack = { navController.popBackStack() })
                }
                composable(SampleRoutes.NativeInteraction) {
                    NativeInteractionDemoPage(onBack = { navController.popBackStack() })
                }
                composable(SampleRoutes.Icon) {
                    IconDemoPage(onBack = { navController.popBackStack() })
                }
                composable(SampleRoutes.LoadingIcon) {
                    LoadingIconDemoPage(onBack = { navController.popBackStack() })
                }
            }
        }
    }
}
