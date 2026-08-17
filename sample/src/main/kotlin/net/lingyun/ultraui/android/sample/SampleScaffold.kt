package net.lingyun.ultraui.android.sample

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import net.lingyun.ultraui.android.core.UPTheme

/** Shared white-page layout for each uview-plus demo screen. */
@Composable
public fun SampleScaffold(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(UPTheme.Background),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(androidx.compose.ui.graphics.Color.White)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "返回",
                color = UPTheme.Content,
                modifier = Modifier.clickable(role = Role.Button, onClick = onBack),
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = title, color = UPTheme.Main, fontWeight = FontWeight.SemiBold)
        }
        HorizontalDivider(color = UPTheme.Border)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 16.dp),
            content = content,
        )
    }
}

/** A deterministic local replacement for upstream HTTP image decoration. */
@Composable
public fun DemoPlaceholder(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.demo_placeholder),
        contentDescription = "示例占位图",
        modifier = modifier,
    )
}

/** A Chinese uview-style titled section used by component demo pages. */
@Composable
public fun DemoSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(androidx.compose.ui.graphics.Color.White),
    ) {
        Text(
            text = title,
            color = UPTheme.Main,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
        )
        HorizontalDivider(color = UPTheme.Border)
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            content = content,
        )
    }
}
