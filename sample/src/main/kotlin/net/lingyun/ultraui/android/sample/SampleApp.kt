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
            }
        }
    }
}
