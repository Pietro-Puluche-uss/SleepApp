package com.pietropuluche.sleepapp.ui.navigation

sealed class Route(val value: String) {
    data object Home : Route("home")
    data object ConfigureSleep : Route("configure_sleep")
    data object SmartBlock : Route("smart_block")
    data object SleepMode : Route("sleep_mode")
    data object Stats : Route("stats")
    data object Achievements : Route("achievements")
    data object Profile : Route("profile")
}
