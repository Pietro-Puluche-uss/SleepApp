package com.pietropuluche.sleepapp.data.local

import android.content.Context
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pietropuluche.sleepapp.data.model.ActiveSleepMode
import com.pietropuluche.sleepapp.data.model.SleepProfile
import com.pietropuluche.sleepapp.data.model.SleepSession
import com.pietropuluche.sleepapp.data.model.SleepSettings

class SleepStorage(context: Context) {

    private val prefs = context.applicationContext.getSharedPreferences("sleepapp_local", Context.MODE_PRIVATE)
    private val gson = Gson()

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

    fun isPackageBlocked(packageName: String): Boolean {
        val active = loadActiveMode() ?: return false
        val isStillActive = active.plannedEndMillis > System.currentTimeMillis()
        if (!isStillActive) return false
        return loadSettings().blockedApps.any { it.packageName == packageName && it.isBlocked }
    }

    companion object {
        private const val KEY_SETTINGS = "settings"
        private const val KEY_SESSIONS = "sessions"
        private const val KEY_ACTIVE_MODE = "active_mode"
        private const val KEY_PROFILE = "profile"
        private const val MAX_SESSION_HISTORY = 90
    }
}
