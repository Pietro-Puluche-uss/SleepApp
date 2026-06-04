package com.pietropuluche.sleepapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.pietropuluche.sleepapp.data.local.SleepStorage
import com.pietropuluche.sleepapp.data.repository.SleepRepository
import com.pietropuluche.sleepapp.ui.common.SleepAppScaffold
import com.pietropuluche.sleepapp.ui.navigation.Route
import com.pietropuluche.sleepapp.ui.screens.AchievementsScreen
import com.pietropuluche.sleepapp.ui.screens.ConfigureSleepScreen
import com.pietropuluche.sleepapp.ui.screens.HomeScreen
import com.pietropuluche.sleepapp.ui.screens.ProfileScreen
import com.pietropuluche.sleepapp.ui.screens.SleepModeScreen
import com.pietropuluche.sleepapp.ui.screens.SmartBlockScreen
import com.pietropuluche.sleepapp.ui.screens.StatsScreen
import com.pietropuluche.sleepapp.ui.theme.SleepAppTheme
import com.pietropuluche.sleepapp.ui.viewmodel.SleepViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SleepAppTheme {
                val navController = rememberNavController()
                val snackbarHostState = remember { SnackbarHostState() }
                val storage = remember { SleepStorage(applicationContext) }
                val repository = remember { SleepRepository(storage) }
                val viewModel = remember { SleepViewModel(repository) }
                val uiState by viewModel.uiState.collectAsState()
                val backStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = backStackEntry?.destination?.route

                LaunchedEffect(Unit) {
                    viewModel.bootstrap()
                }

                LaunchedEffect(uiState.successMessage, uiState.errorMessage) {
                    val message = uiState.errorMessage.ifBlank { uiState.successMessage }
                    if (message.isNotBlank()) {
                        snackbarHostState.showSnackbar(message)
                        viewModel.clearMessages()
                    }
                }

                SleepAppScaffold(
                    currentRoute = currentRoute,
                    snackbarHostState = snackbarHostState,
                    showBottomBar = currentRoute in listOf(
                        Route.Home.value,
                        Route.ConfigureSleep.value,
                        Route.SmartBlock.value,
                        Route.SleepMode.value,
                        Route.Stats.value,
                        Route.Achievements.value,
                        Route.Profile.value
                    ),
                    onNavigate = { route ->
                        if (route != currentRoute) {
                            navController.navigate(route) {
                                launchSingleTop = true
                            }
                        }
                    }
                ) { modifier: Modifier ->
                    NavHost(
                        navController = navController,
                        startDestination = Route.Home.value,
                        modifier = modifier
                    ) {
                        composable(Route.Home.value) {
                            HomeScreen(
                                uiState = uiState,
                                onConfigureSleep = { navController.navigate(Route.ConfigureSleep.value) },
                                onOpenSmartBlock = { navController.navigate(Route.SmartBlock.value) },
                                onStartSleepMode = {
                                    viewModel.startSleepMode()
                                    navController.navigate(Route.SleepMode.value) {
                                        launchSingleTop = true
                                    }
                                },
                                onOpenSleepMode = { navController.navigate(Route.SleepMode.value) }
                            )
                        }
                        composable(Route.ConfigureSleep.value) {
                            ConfigureSleepScreen(
                                settings = uiState.settings,
                                successMessage = uiState.successMessage,
                                onSaveSettings = { viewModel.saveSettings(it) },
                                onOpenSmartBlock = { navController.navigate(Route.SmartBlock.value) }
                            )
                        }
                        composable(Route.SmartBlock.value) {
                            SmartBlockScreen(
                                settings = uiState.settings,
                                successMessage = uiState.successMessage,
                                onToggleBlockedApp = { packageName, enabled ->
                                    viewModel.toggleBlockedApp(packageName, enabled)
                                },
                                onClose = {
                                    if (!navController.popBackStack()) {
                                        navController.navigate(Route.ConfigureSleep.value) {
                                            launchSingleTop = true
                                        }
                                    }
                                }
                            )
                        }
                        composable(Route.SleepMode.value) {
                            SleepModeScreen(
                                activeMode = uiState.activeMode,
                                onFinishSleepMode = {
                                    viewModel.finishSleepMode()
                                    navController.navigate(Route.Stats.value) {
                                        launchSingleTop = true
                                    }
                                },
                                onExtendSleepMode = { viewModel.extendSleepMode(it) },
                                onMovementDetected = { force -> viewModel.registerMovementSample(force) },
                                onStartSleepMode = { viewModel.startSleepMode() }
                            )
                        }
                        composable(Route.Stats.value) {
                            StatsScreen(
                                stats = uiState.stats,
                                sessionsCount = uiState.sessions.size
                            )
                        }
                        composable(Route.Achievements.value) {
                            AchievementsScreen(
                                achievements = uiState.achievements,
                                currentStreak = uiState.dashboard.currentStreak,
                                totalPoints = uiState.dashboard.totalPoints
                            )
                        }
                        composable(Route.Profile.value) {
                            ProfileScreen(
                                profile = uiState.profile,
                                settings = uiState.settings,
                                totalPoints = uiState.dashboard.totalPoints,
                                successMessage = uiState.successMessage,
                                onSaveProfile = { viewModel.saveProfile(it) }
                            )
                        }
                    }
                }
            }
        }
    }
}
