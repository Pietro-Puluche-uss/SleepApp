package com.pietropuluche.sleepapp.data.local

import android.content.Context
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pietropuluche.sleepapp.data.model.ActiveSleepMode
import com.pietropuluche.sleepapp.data.model.SleepProfile
import com.pietropuluche.sleepapp.data.model.SleepSession
import com.pietropuluche.sleepapp.data.model.SleepSettings
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

class SleepStorage(context: Context) {

    private val prefs = context.applicationContext.getSharedPreferences("sleepapp_local", Context.MODE_PRIVATE)
    private val gson = Gson()

    init {
        migrateIfNeeded()
    }

    fun loadSettings(): SleepSettings {
        val raw = prefs.getString(KEY_SETTINGS, null) ?: return SleepSettings()
        return runCatching {
            gson.fromJson(raw, SleepSettings::class.java)
        }.getOrDefault(SleepSettings())
    }

    fun saveSettings(settings: SleepSettings) {
        prefs.edit { putString(KEY_SETTINGS, gson.toJson(settings)) }
    }

    fun loadSessions(): List<SleepSession> {
        val raw = prefs.getString(KEY_SESSIONS, null) ?: return emptyList()
        return runCatching {
            val type = object : TypeToken<List<SleepSession>>() {}.type
            gson.fromJson<List<SleepSession>>(raw, type)
        }.getOrDefault(emptyList())
    }

    fun saveSessions(sessions: List<SleepSession>) {
        prefs.edit { putString(KEY_SESSIONS, gson.toJson(sessions.take(MAX_SESSION_HISTORY))) }
    }

    fun loadActiveMode(): ActiveSleepMode? {
        val raw = prefs.getString(KEY_ACTIVE_MODE, null) ?: return null
        return runCatching {
            gson.fromJson(raw, ActiveSleepMode::class.java)
        }.getOrNull()
    }

    fun saveActiveMode(activeSleepMode: ActiveSleepMode?) {
        prefs.edit {
            if (activeSleepMode == null) {
                remove(KEY_ACTIVE_MODE)
            } else {
                putString(KEY_ACTIVE_MODE, gson.toJson(activeSleepMode))
            }
        }
    }

    fun loadProfile(): SleepProfile {
        val raw = prefs.getString(KEY_PROFILE, null) ?: return SleepProfile()
        return runCatching {
            gson.fromJson(raw, SleepProfile::class.java)
        }.getOrDefault(SleepProfile())
    }

    fun saveProfile(profile: SleepProfile) {
        prefs.edit { putString(KEY_PROFILE, gson.toJson(profile)) }
    }

    fun isPackageBlocked(packageName: String, nowMillis: Long = System.currentTimeMillis()): Boolean {
        val blockedApps = loadSettings().blockedApps
        if (blockedApps.none { it.packageName == packageName && it.isBlocked }) {
            return false
        }
        return isSleepProtectionActive(nowMillis)
    }

    fun isSleepProtectionActive(nowMillis: Long = System.currentTimeMillis()): Boolean {
        val active = loadActiveMode()
        if (active != null && active.plannedEndMillis > nowMillis) {
            return true
        }
        return isConfiguredSleepWindowActive(loadSettings(), nowMillis)
    }

    fun isConfiguredSleepWindowActive(
        settings: SleepSettings = loadSettings(),
        nowMillis: Long = System.currentTimeMillis()
    ): Boolean {
        if (settings.goalMinutes <= 0 || settings.activeDays.isEmpty()) {
            return false
        }
        val sleepTime = settings.sleepTimeMinutes
        val wakeTime = settings.wakeTimeMinutes
        if (sleepTime == wakeTime) return false

        val now = ZonedDateTime.ofInstant(Instant.ofEpochMilli(nowMillis), ZoneId.systemDefault())
        val currentMinutes = now.hour * 60 + now.minute
        val overnight = sleepTime > wakeTime
        val withinWindow = if (overnight) {
            currentMinutes >= sleepTime || currentMinutes < wakeTime
        } else {
            currentMinutes >= sleepTime && currentMinutes < wakeTime
        }
        if (!withinWindow) return false

        val scheduleDay = if (overnight && currentMinutes < wakeTime) {
            now.dayOfWeek.minus(1)
        } else {
            now.dayOfWeek
        }
        return settings.activeDays.contains(scheduleDay.value)
    }

    private fun migrateIfNeeded() {
        val currentVersion = prefs.getInt(KEY_SCHEMA_VERSION, 0)
        if (currentVersion >= CURRENT_SCHEMA_VERSION) return
        prefs.edit {
            clear()
            putInt(KEY_SCHEMA_VERSION, CURRENT_SCHEMA_VERSION)
        }
    }

    companion object {
        private const val CURRENT_SCHEMA_VERSION = 2
        private const val KEY_SCHEMA_VERSION = "schema_version"
        private const val KEY_SETTINGS = "settings"
        private const val KEY_SESSIONS = "sessions"
        private const val KEY_ACTIVE_MODE = "active_mode"
        private const val KEY_PROFILE = "profile"
        private const val MAX_SESSION_HISTORY = 90
    }
}
