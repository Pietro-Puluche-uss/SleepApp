package com.pietropuluche.sleepapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.pietropuluche.sleepapp.data.model.ActiveSleepMode
import com.pietropuluche.sleepapp.data.model.SleepAchievement
import com.pietropuluche.sleepapp.data.model.SleepDashboard
import com.pietropuluche.sleepapp.data.model.SleepProfile
import com.pietropuluche.sleepapp.data.model.SleepSession
import com.pietropuluche.sleepapp.data.model.SleepSettings
import com.pietropuluche.sleepapp.data.model.SleepStats
import com.pietropuluche.sleepapp.data.repository.SleepRepository
import com.pietropuluche.sleepapp.data.repository.SleepRepositorySnapshot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class SleepUiState(
    val isLoading: Boolean = true,
    val settings: SleepSettings = SleepSettings(),
    val sessions: List<SleepSession> = emptyList(),
    val activeMode: ActiveSleepMode? = null,
    val profile: SleepProfile = SleepProfile(),
    val dashboard: SleepDashboard = SleepDashboard(
        nextSleepLabel = "22:30",
        lastQuality = 0,
        averageQuality = 0,
        averageSleepMinutes = 0,
        currentStreak = 0,
        totalPoints = 0,
        phoneUseBeforeBedMinutes = 0
    ),
    val stats: SleepStats = SleepStats(
        averageQuality = 0,
        averageSleepMinutes = 0,
        phoneUseBeforeBedMinutes = 0,
        dayBars = emptyList()
    ),
    val achievements: List<SleepAchievement> = emptyList(),
    val successMessage: String = "",
    val errorMessage: String = ""
)

class SleepViewModel(
    private val repository: SleepRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SleepUiState())
    val uiState: StateFlow<SleepUiState> = _uiState.asStateFlow()
    private var lastMovementAtMillis: Long = 0

    fun bootstrap() {
        applySnapshot(repository.loadState(), successMessage = "")
    }

    fun saveSettings(settings: SleepSettings) {
        applySnapshot(
            snapshot = repository.saveSettings(settings),
            successMessage = "Configuracion guardada"
        )
    }

    fun toggleBlockedApp(packageName: String, enabled: Boolean) {
        applySnapshot(
            snapshot = repository.toggleBlockedApp(packageName, enabled),
            successMessage = if (enabled) "App agregada al bloqueo" else "App liberada"
        )
    }

    fun startSleepMode() {
        applySnapshot(
            snapshot = repository.startSleepMode(),
            successMessage = "Modo sueno activado"
        )
    }

    fun extendSleepMode(minutes: Int) {
        applySnapshot(
            snapshot = repository.extendSleepMode(minutes),
            successMessage = "Tiempo extendido $minutes minutos"
        )
    }

    fun finishSleepMode() {
        applySnapshot(
            snapshot = repository.finishSleepMode(),
            successMessage = "Sesion de sueno registrada"
        )
    }

    fun registerMovementSample(force: Float) {
        val now = System.currentTimeMillis()
        if (force < MOVEMENT_THRESHOLD || now - lastMovementAtMillis < MOVEMENT_COOLDOWN_MILLIS) {
            return
        }
        lastMovementAtMillis = now
        applySnapshot(
            snapshot = repository.registerMovement(),
            successMessage = ""
        )
    }

    fun saveProfile(profile: SleepProfile) {
        applySnapshot(
            snapshot = repository.saveProfile(profile),
            successMessage = "Perfil actualizado"
        )
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(successMessage = "", errorMessage = "")
    }

    private fun applySnapshot(
        snapshot: SleepRepositorySnapshot,
        successMessage: String
    ) {
        _uiState.value = SleepUiState(
            isLoading = false,
            settings = snapshot.settings,
            sessions = snapshot.sessions,
            activeMode = snapshot.activeMode,
            profile = snapshot.profile,
            dashboard = snapshot.dashboard,
            stats = snapshot.stats,
            achievements = snapshot.achievements,
            successMessage = successMessage,
            errorMessage = ""
        )
    }

    companion object {
        private const val MOVEMENT_THRESHOLD = 16.5f
        private const val MOVEMENT_COOLDOWN_MILLIS = 12_000L
    }
}
