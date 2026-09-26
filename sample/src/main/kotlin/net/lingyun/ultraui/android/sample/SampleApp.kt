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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import net.lingyun.ultraui.android.core.UPTheme
import net.lingyun.ultraui.android.sample.catalog.ComponentDemoScreen
import net.lingyun.ultraui.android.sample.catalog.ComponentIndexPage

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

/**
 * Root of the UltraUI Android sample app — a 1:1 reproduction of the uview-plus demo app:
 * a searchable component index home that navigates to per-component demo pages.
 */
@Composable
public fun SampleApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    MaterialTheme(colorScheme = sampleColorScheme) {
        Surface(modifier = modifier.fillMaxSize(), color = Color.White) {
            NavHost(
                navController = navController,
                startDestination = SampleRoutes.Index,
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.safeDrawing),
            ) {
                composable(SampleRoutes.Index) {
                    ComponentIndexPage(
                        onOpen = { id -> navController.navigate(SampleRoutes.demoRoute(id)) },
                    )
                }
                composable(
                    route = SampleRoutes.Demo,
                    arguments = listOf(navArgument("id") { type = NavType.StringType }),
                ) { backStackEntry ->
                    val id = backStackEntry.arguments?.getString("id").orEmpty()
                    ComponentDemoScreen(id = id, onBack = { navController.popBackStack() })
                }
            }
        }
    }
}
