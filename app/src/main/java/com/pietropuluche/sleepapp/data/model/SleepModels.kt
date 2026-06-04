package com.pietropuluche.sleepapp.data.model

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

data class SleepSettings(
    val sleepTimeMinutes: Int = 0,
    val wakeTimeMinutes: Int = 0,
    val activeDays: Set<Int> = emptySet(),
    val goalMinutes: Int = 0,
    val blockedApps: List<BlockedApp> = defaultBlockedApps
)

data class BlockedApp(
    val packageName: String,
    val name: String,
    val shortName: String,
    val isBlocked: Boolean = true
)

data class SleepSession(
    val id: Long,
    val date: String,
    val startedAtMillis: Long,
    val endedAtMillis: Long,
    val targetMinutes: Int,
    val sleptMinutes: Int,
    val phoneUseBeforeBedMinutes: Int,
    val movementEvents: Int,
    val qualityScore: Int,
    val pointsEarned: Int
)

data class ActiveSleepMode(
    val startedAtMillis: Long,
    val plannedEndMillis: Long,
    val targetMinutes: Int,
    val movementEvents: Int = 0,
    val pointsAtRisk: Int = 30
)

data class SleepProfile(
    val displayName: String = "Tu perfil",
    val reminderEnabled: Boolean = true,
    val strictBlocking: Boolean = true
)

data class SleepDashboard(
    val nextSleepLabel: String,
    val lastQuality: Int,
    val averageQuality: Int,
    val averageSleepMinutes: Int,
    val currentStreak: Int,
    val totalPoints: Int,
    val phoneUseBeforeBedMinutes: Int
)

data class SleepAchievement(
    val id: String,
    val title: String,
    val description: String,
    val progress: Int,
    val target: Int,
    val icon: String,
    val unlocked: Boolean
)

data class SleepStats(
    val averageQuality: Int,
    val averageSleepMinutes: Int,
    val phoneUseBeforeBedMinutes: Int,
    val dayBars: List<SleepDayBar>
)

data class SleepDayBar(
    val dayLabel: String,
    val minutes: Int,
    val quality: Int
)

val defaultBlockedApps = listOf(
    BlockedApp("com.instagram.android", "Instagram", "IG"),
    BlockedApp("com.zhiliaoapp.musically", "TikTok", "TT"),
    BlockedApp("com.google.android.youtube", "YouTube", "YT"),
    BlockedApp("com.facebook.katana", "Facebook", "FB"),
    BlockedApp("com.twitter.android", "X / Twitter", "X"),
    BlockedApp("com.whatsapp", "WhatsApp", "WA"),
    BlockedApp("com.android.chrome", "Chrome", "CH")
)

fun SleepSettings.isConfigured(): Boolean {
    return goalMinutes > 0 && activeDays.isNotEmpty()
}

fun Int.asClockLabel(): String {
    val hours = (this / 60).floorMod(24)
    val minutes = this.floorMod(60)
    return "%02d:%02d".format(hours, minutes)
}

fun Int.asDurationLabel(): String {
    val hours = this / 60
    val minutes = this % 60
    return if (hours > 0) {
        "${hours}h ${minutes.toString().padStart(2, '0')}m"
    } else {
        "${minutes}m"
    }
}

fun String.asDayLabel(): String {
    return runCatching {
        val date = LocalDate.parse(this)
        date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.forLanguageTag("es")).replace(".", "")
    }.getOrDefault("--")
}

fun DayOfWeek.displayInitial(): String {
    return getDisplayName(TextStyle.SHORT, Locale.forLanguageTag("es")).take(1).uppercase(Locale.getDefault())
}

private fun Int.floorMod(other: Int): Int = Math.floorMod(this, other)
