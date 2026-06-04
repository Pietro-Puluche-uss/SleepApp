package com.pietropuluche.sleepapp.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pietropuluche.sleepapp.ui.navigation.Route
import com.pietropuluche.sleepapp.ui.theme.CalmTeal
import com.pietropuluche.sleepapp.ui.theme.DividerNight
import com.pietropuluche.sleepapp.ui.theme.DreamPurple
import com.pietropuluche.sleepapp.ui.theme.NightBackground
import com.pietropuluche.sleepapp.ui.theme.NightPanel
import com.pietropuluche.sleepapp.ui.theme.NightPanelSoft
import com.pietropuluche.sleepapp.ui.theme.TextPrimary
import com.pietropuluche.sleepapp.ui.theme.TextSecondary
import com.pietropuluche.sleepapp.ui.theme.WarningCoral

data class BottomItem(
    val route: Route,
    val label: String,
    val icon: ImageVector
)

val sleepBottomItems = listOf(
    BottomItem(Route.Home, "Inicio", Icons.Default.Home),
    BottomItem(Route.ConfigureSleep, "Sueño", Icons.Default.Settings),
    BottomItem(Route.Stats, "Estad.", Icons.Default.BarChart),
    BottomItem(Route.Achievements, "Retos", Icons.Default.EmojiEvents),
    BottomItem(Route.Profile, "Perfil", Icons.Default.Person)
)

@Composable
fun SleepAppScaffold(
    currentRoute: String?,
    snackbarHostState: SnackbarHostState,
    showBottomBar: Boolean,
    onNavigate: (String) -> Unit,
    content: @Composable (Modifier) -> Unit
) {
    Scaffold(
        containerColor = NightBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(containerColor = NightPanel) {
                    sleepBottomItems.forEach { item ->
                        val isSelected = currentRoute == item.route.value ||
                            (currentRoute == Route.SmartBlock.value && item.route == Route.ConfigureSleep) ||
                            (currentRoute == Route.SleepMode.value && item.route == Route.Home)
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { onNavigate(item.route.value) },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = TextPrimary,
                                selectedTextColor = TextPrimary,
                                indicatorColor = DreamPurple,
                                unselectedIconColor = TextSecondary,
                                unselectedTextColor = TextSecondary
                            )
                        )
                    }
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        listOf(NightBackground, Color(0xFF0F1433), NightBackground)
                    )
                )
                .padding(padding)
        ) {
            content(Modifier)
        }
    }
}

@Composable
fun SleepCard(
    modifier: Modifier = Modifier,
    accent: Color? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = NightPanel),
        border = BorderStroke(1.dp, accent ?: DividerNight)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            content = content
        )
    }
}

@Composable
fun MetricPill(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    color: Color = CalmTeal
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = NightPanelSoft),
        border = BorderStroke(1.dp, color.copy(alpha = 0.35f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(value, style = MaterialTheme.typography.titleLarge, color = TextPrimary, fontWeight = FontWeight.Bold)
            Text(label, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }
    }
}

@Composable
fun InlineMessage(
    message: String,
    isError: Boolean
) {
    if (message.isBlank()) return
    val color = if (isError) WarningCoral else CalmTeal
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.16f)),
        border = BorderStroke(1.dp, color.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(color, RoundedCornerShape(50))
            )
            Text(message, color = TextPrimary, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
