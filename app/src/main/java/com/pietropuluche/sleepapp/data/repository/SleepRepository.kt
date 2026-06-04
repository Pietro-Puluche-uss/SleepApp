package com.pietropuluche.sleepapp.data.repository

import com.pietropuluche.sleepapp.data.local.SleepStorage
import com.pietropuluche.sleepapp.data.model.ActiveSleepMode
import com.pietropuluche.sleepapp.data.model.SleepAchievement
import com.pietropuluche.sleepapp.data.model.SleepDashboard
import com.pietropuluche.sleepapp.data.model.SleepDayBar
import com.pietropuluche.sleepapp.data.model.SleepProfile
import com.pietropuluche.sleepapp.data.model.SleepSession
import com.pietropuluche.sleepapp.data.model.SleepSettings
import com.pietropuluche.sleepapp.data.model.SleepStats
import com.pietropuluche.sleepapp.data.model.asDayLabel
import java.time.LocalDate
import java.time.ZoneId
import kotlin.math.max
import kotlin.math.roundToInt

class SleepRepository(
    private val storage: SleepStorage,
    private val clockZone: ZoneId = ZoneId.systemDefault()
) {

    fun loadState(): SleepRepositorySnapshot {
        val settings = storage.loadSettings()
        val sessions = storage.loadSessions().sortedByDescending { it.startedAtMillis }
        val activeMode = storage.loadActiveMode()?.takeIf { it.plannedEndMillis > System.currentTimeMillis() }
        if (activeMode == null && storage.loadActiveMode() != null) {
            storage.saveActiveMode(null)
        }
        val profile = storage.loadProfile()
        return SleepRepositorySnapshot(
            settings = settings,
            sessions = sessions,
            activeMode = activeMode,
            profile = profile,
            dashboard = buildDashboard(settings, sessions),
            stats = buildStats(sessions),
            achievements = buildAchievements(sessions)
        )
    }

    fun saveSettings(settings: SleepSettings): SleepRepositorySnapshot {
        storage.saveSettings(settings)
        return loadState()
    }

    fun toggleBlockedApp(packageName: String, enabled: Boolean): SleepRepositorySnapshot {
        val current = storage.loadSettings()
        storage.saveSettings(
            current.copy(
                blockedApps = current.blockedApps.map {
                    if (it.packageName == packageName) it.copy(isBlocked = enabled) else it
                }
            )
        )
        return loadState()
    }

    fun startSleepMode(): SleepRepositorySnapshot {
        val settings = storage.loadSettings()
        val now = System.currentTimeMillis()
        val targetMillis = settings.goalMinutes.coerceAtLeast(1) * 60_000L
        storage.saveActiveMode(
            ActiveSleepMode(
                startedAtMillis = now,
                plannedEndMillis = now + targetMillis,
                targetMinutes = settings.goalMinutes.coerceAtLeast(1)
            )
        )
        return loadState()
    }

    fun extendSleepMode(minutes: Int): SleepRepositorySnapshot {
        val active = storage.loadActiveMode() ?: return loadState()
        storage.saveActiveMode(active.copy(plannedEndMillis = active.plannedEndMillis + minutes * 60_000L))
        return loadState()
    }

    fun registerMovement(weight: Int = 1): SleepRepositorySnapshot {
        val active = storage.loadActiveMode() ?: return loadState()
        val safeWeight = weight.coerceIn(1, 3)
        storage.saveActiveMode(active.copy(movementEvents = active.movementEvents + safeWeight))
        return loadState()
    }

    fun finishSleepMode(): SleepRepositorySnapshot {
        val active = storage.loadActiveMode() ?: return loadState()
        val now = System.currentTimeMillis()
        val sleptMinutes = ((now - active.startedAtMillis) / 60_000L).toInt().coerceAtLeast(1)
        val cappedSlept = sleptMinutes.coerceAtMost(active.targetMinutes + 90)
        val movementPenalty = active.movementEvents * 4
        val goalRatio = (cappedSlept.toFloat() / active.targetMinutes).coerceIn(0f, 1.15f)
        val quality = (goalRatio * 92).roundToInt().coerceIn(30, 96) - movementPenalty
        val points = max(0, 40 + (quality / 3) - movementPenalty)
        val session = SleepSession(
            id = now,
            date = LocalDate.now(clockZone).toString(),
            startedAtMillis = active.startedAtMillis,
            endedAtMillis = now,
            targetMinutes = active.targetMinutes,
            sleptMinutes = cappedSlept,
            phoneUseBeforeBedMinutes = estimatePhoneUseBeforeBed(active.movementEvents),
            movementEvents = active.movementEvents,
            qualityScore = quality.coerceIn(0, 100),
            pointsEarned = points
        )
        storage.saveSessions(listOf(session) + storage.loadSessions())
        storage.saveActiveMode(null)
        return loadState()
    }

    fun saveProfile(profile: SleepProfile): SleepRepositorySnapshot {
        storage.saveProfile(profile)
        return loadState()
    }

    private fun buildDashboard(settings: SleepSettings, sessions: List<SleepSession>): SleepDashboard {
        val recent = sessions.take(7)
        return SleepDashboard(
            nextSleepLabel = if (settings.sleepTimeMinutes > 0) settings.sleepTimeMinutes.asClock() else "00:00",
            lastQuality = sessions.firstOrNull()?.qualityScore ?: 0,
            averageQuality = recent.averageOf { it.qualityScore },
            averageSleepMinutes = recent.averageOf { it.sleptMinutes },
            currentStreak = calculateCurrentStreak(sessions),
            totalPoints = sessions.sumOf { it.pointsEarned },
            phoneUseBeforeBedMinutes = recent.averageOf { it.phoneUseBeforeBedMinutes }
        )
    }

    private fun buildStats(sessions: List<SleepSession>): SleepStats {
        val recent = sessions.take(7).reversed()
        return SleepStats(
            averageQuality = recent.averageOf { it.qualityScore },
            averageSleepMinutes = recent.averageOf { it.sleptMinutes },
            phoneUseBeforeBedMinutes = recent.averageOf { it.phoneUseBeforeBedMinutes },
            dayBars = recent.map {
                SleepDayBar(
                    dayLabel = it.date.asDayLabel(),
                    minutes = it.sleptMinutes,
                    quality = it.qualityScore
                )
            }
        )
    }

    private fun buildAchievements(sessions: List<SleepSession>): List<SleepAchievement> {
        val streak = calculateCurrentStreak(sessions)
        val totalPoints = sessions.sumOf { it.pointsEarned }
        val goodNights = sessions.count { it.qualityScore >= 80 }
        val lowPhoneUse = sessions.count { it.phoneUseBeforeBedMinutes <= 15 }
        return listOf(
            SleepAchievement(
                id = "first_night",
                title = "Primera noche",
                description = "Completa tu primera sesion de sueno.",
                progress = sessions.size.coerceAtMost(1),
                target = 1,
                icon = "moon",
                unlocked = sessions.isNotEmpty()
            ),
            SleepAchievement(
                id = "streak_7",
                title = "Racha tranquila",
                description = "Duerme a tiempo 7 noches seguidas.",
                progress = streak.coerceAtMost(7),
                target = 7,
                icon = "fire",
                unlocked = streak >= 7
            ),
            SleepAchievement(
                id = "quality_5",
                title = "Sueno profundo",
                description = "Consigue 5 noches con calidad mayor a 80%.",
                progress = goodNights.coerceAtMost(5),
                target = 5,
                icon = "star",
                unlocked = goodNights >= 5
            ),
            SleepAchievement(
                id = "points_1000",
                title = "Constancia",
                description = "Acumula 1000 puntos personales.",
                progress = totalPoints.coerceAtMost(1000),
                target = 1000,
                icon = "trophy",
                unlocked = totalPoints >= 1000
            ),
            SleepAchievement(
                id = "phone_break",
                title = "Menos pantalla",
                description = "Manten bajo el uso del celular antes de dormir.",
                progress = lowPhoneUse.coerceAtMost(5),
                target = 5,
                icon = "shield",
                unlocked = lowPhoneUse >= 5
            )
        )
    }

    private fun calculateCurrentStreak(sessions: List<SleepSession>): Int {
        if (sessions.isEmpty()) return 0
        val dates = sessions.mapNotNull { runCatching { LocalDate.parse(it.date) }.getOrNull() }.toSet()
        var streak = 0
        var cursor = LocalDate.now(clockZone)
        if (!dates.contains(cursor)) {
            cursor = cursor.minusDays(1)
        }
        while (dates.contains(cursor)) {
            streak++
            cursor = cursor.minusDays(1)
        }
        return streak
    }

    private fun estimatePhoneUseBeforeBed(movementEvents: Int): Int {
        return (6 + movementEvents * 3).coerceAtMost(90)
    }

    private fun List<SleepSession>.averageOf(selector: (SleepSession) -> Int): Int {
        if (isEmpty()) return 0
        return map(selector).average().roundToInt()
    }

    private fun Int.asClock(): String {
        val hours = this / 60
        val minutes = this % 60
        return "%02d:%02d".format(hours, minutes)
    }
}

data class SleepRepositorySnapshot(
    val settings: SleepSettings,
    val sessions: List<SleepSession>,
    val activeMode: ActiveSleepMode?,
    val profile: SleepProfile,
    val dashboard: SleepDashboard,
    val stats: SleepStats,
    val achievements: List<SleepAchievement>
)
