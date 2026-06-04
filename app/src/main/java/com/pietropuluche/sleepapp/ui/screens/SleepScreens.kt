package com.pietropuluche.sleepapp.ui.screens

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.provider.Settings
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.pietropuluche.sleepapp.data.model.ActiveSleepMode
import com.pietropuluche.sleepapp.data.model.BlockedApp
import com.pietropuluche.sleepapp.data.model.SleepAchievement
import com.pietropuluche.sleepapp.data.model.SleepDayBar
import com.pietropuluche.sleepapp.data.model.SleepProfile
import com.pietropuluche.sleepapp.data.model.SleepSettings
import com.pietropuluche.sleepapp.data.model.SleepStats
import com.pietropuluche.sleepapp.data.model.asClockLabel
import com.pietropuluche.sleepapp.data.model.asDurationLabel
import com.pietropuluche.sleepapp.data.model.displayInitial
import com.pietropuluche.sleepapp.ui.common.InlineMessage
import com.pietropuluche.sleepapp.ui.common.MetricPill
import com.pietropuluche.sleepapp.ui.common.SleepCard
import com.pietropuluche.sleepapp.ui.theme.CalmTeal
import com.pietropuluche.sleepapp.ui.theme.DividerNight
import com.pietropuluche.sleepapp.ui.theme.DreamPurple
import com.pietropuluche.sleepapp.ui.theme.MoonCream
import com.pietropuluche.sleepapp.ui.theme.NightPanel
import com.pietropuluche.sleepapp.ui.theme.NightPanelSoft
import com.pietropuluche.sleepapp.ui.theme.RestBlue
import com.pietropuluche.sleepapp.ui.theme.SoftLilac
import com.pietropuluche.sleepapp.ui.theme.SuccessMint
import com.pietropuluche.sleepapp.ui.theme.TextPrimary
import com.pietropuluche.sleepapp.ui.theme.TextSecondary
import com.pietropuluche.sleepapp.ui.theme.WarningCoral
import com.pietropuluche.sleepapp.ui.viewmodel.SleepUiState
import kotlinx.coroutines.delay
import java.time.DayOfWeek
import java.util.Locale
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.math.sqrt

@Composable
fun HomeScreen(
    uiState: SleepUiState,
    onConfigureSleep: () -> Unit,
    onOpenSmartBlock: () -> Unit,
    onStartSleepMode: () -> Unit,
    onOpenSleepMode: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { Spacer(modifier = Modifier.height(6.dp)) }
        item {
            HomeHero(
                uiState = uiState,
                onStartSleepMode = onStartSleepMode,
                onOpenSleepMode = onOpenSleepMode
            )
        }
        item {
            InlineMessage(uiState.successMessage, isError = false)
            InlineMessage(uiState.errorMessage, isError = true)
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MetricPill(
                    label = "Calidad",
                    value = "${uiState.dashboard.lastQuality}%",
                    modifier = Modifier.weight(1f),
                    color = CalmTeal
                )
                MetricPill(
                    label = "Racha",
                    value = "${uiState.dashboard.currentStreak} noches",
                    modifier = Modifier.weight(1f),
                    color = MoonCream
                )
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                QuickActionCard(
                    title = "Configurar",
                    subtitle = "${uiState.settings.sleepTimeMinutes.asClockLabel()} - ${uiState.settings.wakeTimeMinutes.asClockLabel()}",
                    icon = Icons.Default.Settings,
                    color = RestBlue,
                    modifier = Modifier.weight(1f),
                    onClick = onConfigureSleep
                )
                QuickActionCard(
                    title = "Bloqueo",
                    subtitle = "${uiState.settings.blockedApps.count { it.isBlocked }} apps activas",
                    icon = Icons.Default.Lock,
                    color = DreamPurple,
                    modifier = Modifier.weight(1f),
                    onClick = onOpenSmartBlock
                )
            }
        }
        item {
            SleepCard(accent = CalmTeal) {
                SectionHeader("Hoy", "Puntos, pantalla y movimiento")
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    SmallStat(
                        label = "Puntos",
                        value = uiState.dashboard.totalPoints.toString(),
                        modifier = Modifier.weight(1f)
                    )
                    SmallStat(
                        label = "Antes de dormir",
                        value = uiState.dashboard.phoneUseBeforeBedMinutes.asDurationLabel(),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
        item {
            SleepCard {
                SectionHeader("Resumen semanal", "Horas registradas por dia")
                MiniBars(uiState.stats.dayBars)
            }
        }
        item { Spacer(modifier = Modifier.height(18.dp)) }
    }
}

@Composable
private fun HomeHero(
    uiState: SleepUiState,
    onStartSleepMode: () -> Unit,
    onOpenSleepMode: () -> Unit
) {
    val activeMode = uiState.activeMode
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = BorderStroke(1.dp, DreamPurple.copy(alpha = 0.45f))
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xFF20215D), Color(0xFF131832), Color(0xFF18313A))
                    )
                )
                .padding(20.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Buenas noches", style = MaterialTheme.typography.headlineMedium, color = TextPrimary, fontWeight = FontWeight.ExtraBold)
                        Text("Proximo sueno: ${uiState.dashboard.nextSleepLabel}", color = TextSecondary)
                    }
                    MoonBadge()
                }
                QualityRing(
                    quality = uiState.dashboard.lastQuality,
                    label = "Ultima calidad"
                )
                Button(
                    onClick = if (activeMode == null) onStartSleepMode else onOpenSleepMode,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DreamPurple,
                        contentColor = TextPrimary
                    )
                ) {
                    Icon(Icons.Default.DarkMode, contentDescription = null)
                    Text(
                        text = if (activeMode == null) "  Iniciar modo sueno" else "  Volver al modo sueno",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun ConfigureSleepScreen(
    settings: SleepSettings,
    successMessage: String,
    onSaveSettings: (SleepSettings) -> Unit,
    onOpenSmartBlock: () -> Unit
) {
    var sleepTime by rememberSaveable(settings.sleepTimeMinutes) { mutableStateOf(settings.sleepTimeMinutes) }
    var wakeTime by rememberSaveable(settings.wakeTimeMinutes) { mutableStateOf(settings.wakeTimeMinutes) }
    var goalMinutes by rememberSaveable(settings.goalMinutes) { mutableStateOf(settings.goalMinutes) }
    var activeDays by rememberSaveable(settings.activeDays) { mutableStateOf(settings.activeDays.toList()) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { ScreenTitle("Configurar mi sueno", "Horario, dias activos y meta personal") }
        item { InlineMessage(successMessage, isError = false) }
        item {
            TimeEditorCard(
                title = "Hora de dormir",
                value = sleepTime,
                onMinus = { sleepTime = adjustMinutes(sleepTime, -15) },
                onPlus = { sleepTime = adjustMinutes(sleepTime, 15) }
            )
        }
        item {
            TimeEditorCard(
                title = "Hora de despertar",
                value = wakeTime,
                onMinus = { wakeTime = adjustMinutes(wakeTime, -15) },
                onPlus = { wakeTime = adjustMinutes(wakeTime, 15) }
            )
        }
        item {
            SleepCard {
                SectionHeader("Dias activos", "El horario se aplica en estos dias")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DayOfWeek.values().forEach { day ->
                        val dayValue = day.value
                        val selected = activeDays.contains(dayValue)
                        DayChip(
                            label = day.displayInitial(),
                            selected = selected,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                activeDays = if (selected) {
                                    activeDays.filterNot { it == dayValue }
                                } else {
                                    (activeDays + dayValue).distinct().sorted()
                                }
                            }
                        )
                    }
                }
            }
        }
        item {
            SleepCard {
                SectionHeader("Objetivo de sueno", goalMinutes.asDurationLabel())
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    listOf(6, 7, 8, 9).forEach { hours ->
                        SelectablePill(
                            text = "${hours}h",
                            selected = goalMinutes == hours * 60,
                            modifier = Modifier.weight(1f),
                            onClick = { goalMinutes = hours * 60 }
                        )
                    }
                }
            }
        }
        item {
            OutlinedButton(
                onClick = onOpenSmartBlock,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, DreamPurple)
            ) {
                Icon(Icons.Default.Block, contentDescription = null, tint = DreamPurple)
                Text("  Bloqueo inteligente", color = TextPrimary, fontWeight = FontWeight.SemiBold)
            }
        }
        item {
            Button(
                onClick = {
                    onSaveSettings(
                        settings.copy(
                            sleepTimeMinutes = sleepTime,
                            wakeTimeMinutes = wakeTime,
                            goalMinutes = goalMinutes,
                            activeDays = activeDays.toSet()
                        )
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CalmTeal, contentColor = Color(0xFF041512))
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Text("  Guardar configuracion", fontWeight = FontWeight.Bold)
            }
        }
        item { Spacer(modifier = Modifier.height(18.dp)) }
    }
}

@Composable
fun SmartBlockScreen(
    settings: SleepSettings,
    successMessage: String,
    onToggleBlockedApp: (String, Boolean) -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            HeaderWithClose("Bloqueo inteligente", onClose)
        }
        item { InlineMessage(successMessage, isError = false) }
        item {
            SleepCard(accent = DreamPurple) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(Icons.Default.Shield, contentDescription = null, tint = DreamPurple, modifier = Modifier.size(30.dp))
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Servicio local", color = TextPrimary, fontWeight = FontWeight.Bold)
                        Text("Activalo en Accesibilidad para redirigir apps bloqueadas durante el modo sueno.", color = TextSecondary)
                    }
                }
                Button(
                    onClick = { context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DreamPurple)
                ) {
                    Icon(Icons.Default.Accessibility, contentDescription = null)
                    Text("  Abrir Accesibilidad")
                }
            }
        }
        items(settings.blockedApps) { app ->
            BlockedAppRow(app = app, onToggle = onToggleBlockedApp)
        }
        item { Spacer(modifier = Modifier.height(18.dp)) }
    }
}

@Composable
fun SleepModeScreen(
    activeMode: ActiveSleepMode?,
    onFinishSleepMode: () -> Unit,
    onExtendSleepMode: (Int) -> Unit,
    onMovementDetected: (Float) -> Unit,
    onStartSleepMode: () -> Unit
) {
    var now by remember { mutableStateOf(System.currentTimeMillis()) }

    LaunchedEffect(activeMode?.startedAtMillis, activeMode?.plannedEndMillis) {
        while (activeMode != null) {
            now = System.currentTimeMillis()
            delay(1000)
        }
    }
    MovementSensorEffect(enabled = activeMode != null, onMovementDetected = onMovementDetected)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }
        if (activeMode == null) {
            item {
                SleepCard(accent = MoonCream) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        MoonBadge(size = 86)
                    }
                    Text("Modo sueno", style = MaterialTheme.typography.headlineMedium, color = TextPrimary, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                    Text("No hay una sesion activa.", color = TextSecondary, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                    Button(
                        onClick = onStartSleepMode,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DreamPurple)
                    ) {
                        Icon(Icons.Default.DarkMode, contentDescription = null)
                        Text("  Iniciar ahora", fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            val remainingMillis = (activeMode.plannedEndMillis - now).coerceAtLeast(0)
            val elapsedMillis = (now - activeMode.startedAtMillis).coerceAtLeast(0)
            val progress = (elapsedMillis.toFloat() / (activeMode.targetMinutes * 60_000f)).coerceIn(0f, 1f)
            item {
                MoonBadge(size = 110)
            }
            item {
                Text("Modo sueno activado", style = MaterialTheme.typography.headlineMedium, color = TextPrimary, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center)
                Text("Tu descanso es lo importante", color = TextSecondary, textAlign = TextAlign.Center)
            }
            item {
                SleepCard(accent = DreamPurple) {
                    SectionHeader("Tiempo restante", formatRemaining(remainingMillis))
                    ProgressTrack(progress = progress)
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        SmallStat("Movimientos", activeMode.movementEvents.toString(), Modifier.weight(1f), WarningCoral)
                        SmallStat("Puntos en riesgo", activeMode.pointsAtRisk.toString(), Modifier.weight(1f), MoonCream)
                    }
                }
            }
            item {
                Button(
                    onClick = onFinishSleepMode,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DreamPurple)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null)
                    Text("  Finalizar modo sueno", fontWeight = FontWeight.Bold)
                }
            }
            item {
                OutlinedButton(
                    onClick = { onExtendSleepMode(15) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.dp, CalmTeal)
                ) {
                    Icon(Icons.Default.Timer, contentDescription = null, tint = CalmTeal)
                    Text("  Extender 15 min", color = TextPrimary)
                }
            }
        }
        item { Spacer(modifier = Modifier.height(22.dp)) }
    }
}

@Composable
fun StatsScreen(
    stats: SleepStats,
    sessionsCount: Int
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { ScreenTitle("Estadisticas", "Calidad, horas y uso del celular") }
        item {
            SleepCard(accent = CalmTeal) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    QualityRing(stats.averageQuality, "Promedio", Modifier.weight(1f))
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        SmallStat("Horas promedio", stats.averageSleepMinutes.asDurationLabel(), color = RestBlue)
                        SmallStat("Antes de dormir", stats.phoneUseBeforeBedMinutes.asDurationLabel(), color = MoonCream)
                    }
                }
            }
        }
        item {
            SleepCard {
                SectionHeader("Horario de sueno", "$sessionsCount sesiones locales")
                SleepBars(stats.dayBars)
            }
        }
        item {
            SleepCard(accent = RestBlue) {
                SectionHeader("Lectura rapida", "Tu semana reciente")
                Text(statsQualityMessage(stats.averageQuality), color = TextSecondary, style = MaterialTheme.typography.bodyLarge)
            }
        }
        item { Spacer(modifier = Modifier.height(18.dp)) }
    }
}

@Composable
fun AchievementsScreen(
    achievements: List<SleepAchievement>,
    currentStreak: Int,
    totalPoints: Int
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { ScreenTitle("Retos y logros", "Metas personales y racha actual") }
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                border = BorderStroke(1.dp, MoonCream.copy(alpha = 0.45f))
            ) {
                Row(
                    modifier = Modifier
                        .background(Brush.linearGradient(listOf(Color(0xFF382787), Color(0xFF1B2450))))
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Racha actual", color = TextSecondary)
                        Text("$currentStreak noches", color = TextPrimary, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
                        Text("$totalPoints puntos acumulados", color = SoftLilac)
                    }
                    Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = MoonCream, modifier = Modifier.size(54.dp))
                }
            }
        }
        items(achievements) { achievement ->
            AchievementRow(achievement)
        }
        item { Spacer(modifier = Modifier.height(18.dp)) }
    }
}

@Composable
fun ProfileScreen(
    profile: SleepProfile,
    settings: SleepSettings,
    totalPoints: Int,
    successMessage: String,
    onSaveProfile: (SleepProfile) -> Unit
) {
    var displayName by rememberSaveable(profile.displayName) { mutableStateOf(profile.displayName) }
    var reminders by rememberSaveable(profile.reminderEnabled) { mutableStateOf(profile.reminderEnabled) }
    var strictBlocking by rememberSaveable(profile.strictBlocking) { mutableStateOf(profile.strictBlocking) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { ScreenTitle("Perfil", "Preferencias locales de SleepApp") }
        item { InlineMessage(successMessage, isError = false) }
        item {
            SleepCard(accent = DreamPurple) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .background(DreamPurple, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(displayName.take(1).uppercase(Locale.getDefault()).ifBlank { "S" }, color = TextPrimary, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(displayName.ifBlank { "Tu perfil" }, color = TextPrimary, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                        Text("${settings.goalMinutes.asDurationLabel()} de meta diaria", color = TextSecondary)
                        Text("$totalPoints puntos", color = CalmTeal)
                    }
                }
            }
        }
        item {
            SleepCard {
                SectionHeader("Datos personales", "Solo se guardan en este celular")
                OutlinedTextField(
                    value = displayName,
                    onValueChange = { displayName = it },
                    label = { Text("Nombre visible") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                )
            }
        }
        item {
            SleepCard {
                PreferenceSwitch(
                    title = "Recordatorios",
                    subtitle = "Aviso antes del horario de sueno",
                    checked = reminders,
                    onCheckedChange = { reminders = it }
                )
                Divider(color = DividerNight)
                PreferenceSwitch(
                    title = "Bloqueo estricto",
                    subtitle = "Mantener bloqueo activo durante todo el horario",
                    checked = strictBlocking,
                    onCheckedChange = { strictBlocking = it }
                )
            }
        }
        item {
            Button(
                onClick = {
                    onSaveProfile(
                        profile.copy(
                            displayName = displayName.ifBlank { "Tu perfil" },
                            reminderEnabled = reminders,
                            strictBlocking = strictBlocking
                        )
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CalmTeal, contentColor = Color(0xFF041512))
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Text("  Guardar perfil", fontWeight = FontWeight.Bold)
            }
        }
        item { Spacer(modifier = Modifier.height(18.dp)) }
    }
}

@Composable
private fun ScreenTitle(title: String, subtitle: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(title, style = MaterialTheme.typography.headlineMedium, color = TextPrimary, fontWeight = FontWeight.ExtraBold)
        Text(subtitle, color = TextSecondary)
    }
}

@Composable
private fun HeaderWithClose(title: String, onClose: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title, style = MaterialTheme.typography.headlineSmall, color = TextPrimary, fontWeight = FontWeight.ExtraBold)
        IconButton(onClick = onClose) {
            Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = TextSecondary)
        }
    }
}

@Composable
private fun SectionHeader(title: String, subtitle: String) {
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(title, style = MaterialTheme.typography.titleMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
        Text(subtitle, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
    }
}

@Composable
private fun QuickActionCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = NightPanel),
        border = BorderStroke(1.dp, color.copy(alpha = 0.35f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(28.dp))
            Text(title, color = TextPrimary, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(subtitle, color = TextSecondary, style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun SmallStat(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    color: Color = CalmTeal
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NightPanelSoft),
        border = BorderStroke(1.dp, color.copy(alpha = 0.28f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(value, color = TextPrimary, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(label, color = TextSecondary, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun MoonBadge(size: Int = 58) {
    Box(
        modifier = Modifier
            .size(size.dp)
            .background(MoonCream.copy(alpha = 0.16f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(Icons.Default.DarkMode, contentDescription = null, tint = MoonCream, modifier = Modifier.size((size * 0.55).dp))
    }
}

@Composable
private fun QualityRing(
    quality: Int,
    label: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 116.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(112.dp)) {
            val stroke = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
            drawArc(
                color = DividerNight,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = stroke
            )
            drawArc(
                color = if (quality >= 80) CalmTeal else if (quality >= 65) MoonCream else WarningCoral,
                startAngle = -90f,
                sweepAngle = 360f * (quality / 100f).coerceIn(0f, 1f),
                useCenter = false,
                style = stroke
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("$quality%", color = TextPrimary, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
            Text(label, color = TextSecondary, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun TimeEditorCard(
    title: String,
    value: Int,
    onMinus: () -> Unit,
    onPlus: () -> Unit
) {
    SleepCard {
        SectionHeader(title, "Ajusta en intervalos de 15 minutos")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onMinus) {
                Icon(Icons.Default.Remove, contentDescription = "Restar", tint = TextPrimary)
            }
            Text(value.asClockLabel(), color = TextPrimary, style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.ExtraBold)
            IconButton(onClick = onPlus) {
                Icon(Icons.Default.Add, contentDescription = "Sumar", tint = TextPrimary)
            }
        }
    }
}

@Composable
private fun DayChip(
    label: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = if (selected) DreamPurple else NightPanelSoft)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 11.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(label, color = TextPrimary, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun SelectablePill(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = if (selected) CalmTeal else NightPanelSoft)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text, color = if (selected) Color(0xFF041512) else TextPrimary, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun BlockedAppRow(
    app: BlockedApp,
    onToggle: (String, Boolean) -> Unit
) {
    SleepCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(DreamPurple.copy(alpha = 0.2f), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(app.shortName, color = TextPrimary, fontWeight = FontWeight.Bold)
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(app.name, color = TextPrimary, fontWeight = FontWeight.Bold)
                Text(app.packageName, color = TextSecondary, style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Switch(
                checked = app.isBlocked,
                onCheckedChange = { onToggle(app.packageName, it) },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = TextPrimary,
                    checkedTrackColor = DreamPurple,
                    uncheckedThumbColor = TextSecondary,
                    uncheckedTrackColor = NightPanelSoft
                )
            )
        }
    }
}

@Composable
private fun MovementSensorEffect(
    enabled: Boolean,
    onMovementDetected: (Float) -> Unit
) {
    val context = LocalContext.current
    DisposableEffect(enabled) {
        if (!enabled) {
            return@DisposableEffect onDispose { }
        }
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                val x = event.values.getOrNull(0) ?: 0f
                val y = event.values.getOrNull(1) ?: 0f
                val z = event.values.getOrNull(2) ?: 0f
                val magnitude = sqrt(x * x + y * y + z * z)
                val force = abs(magnitude - 9.81f)
                if (force > 6.7f) {
                    onMovementDetected(force)
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
        }
        if (accelerometer != null) {
            sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        }
        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }
}

@Composable
private fun ProgressTrack(progress: Float) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(18.dp)
    ) {
        val radius = size.height / 2
        drawRoundRect(
            color = DividerNight,
            size = size,
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(radius, radius)
        )
        drawRoundRect(
            color = CalmTeal,
            size = Size(size.width * progress.coerceIn(0f, 1f), size.height),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(radius, radius)
        )
    }
}

@Composable
private fun MiniBars(dayBars: List<SleepDayBar>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        dayBars.forEach { bar ->
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height((bar.minutes / 540f * 64).coerceIn(12f, 64f).dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (bar.quality >= 80) CalmTeal else RestBlue)
                )
                Text(bar.dayLabel, color = TextSecondary, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun SleepBars(dayBars: List<SleepDayBar>) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        dayBars.forEach { bar ->
            val progress = (bar.minutes / 540f).coerceIn(0.08f, 1f)
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(bar.dayLabel.uppercase(Locale.getDefault()), color = TextSecondary, fontWeight = FontWeight.Bold)
                    Text("${bar.minutes.asDurationLabel()}  ${bar.quality}%", color = TextPrimary)
                }
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(14.dp)
                ) {
                    val radius = size.height / 2
                    drawRoundRect(
                        color = DividerNight,
                        size = size,
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(radius, radius)
                    )
                    drawRoundRect(
                        color = if (bar.quality >= 80) CalmTeal else if (bar.quality >= 70) RestBlue else WarningCoral,
                        size = Size(size.width * progress, size.height),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(radius, radius)
                    )
                }
            }
        }
    }
}

@Composable
private fun AchievementRow(achievement: SleepAchievement) {
    val color = if (achievement.unlocked) MoonCream else DividerNight
    SleepCard(accent = color) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(color.copy(alpha = if (achievement.unlocked) 0.22f else 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (achievement.icon) {
                        "fire" -> Icons.Default.LocalFireDepartment
                        "star" -> Icons.Default.Star
                        "shield" -> Icons.Default.Shield
                        else -> Icons.Default.EmojiEvents
                    },
                    contentDescription = null,
                    tint = if (achievement.unlocked) MoonCream else TextSecondary
                )
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(achievement.title, color = TextPrimary, fontWeight = FontWeight.Bold)
                    Text("${achievement.progress}/${achievement.target}", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                }
                Text(achievement.description, color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                ProgressTrack(achievement.progress.toFloat() / max(1, achievement.target))
            }
        }
    }
}

@Composable
private fun PreferenceSwitch(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(title, color = TextPrimary, fontWeight = FontWeight.Bold)
            Text(subtitle, color = TextSecondary, style = MaterialTheme.typography.bodySmall)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

private fun adjustMinutes(value: Int, delta: Int): Int {
    return Math.floorMod(value + delta, 24 * 60)
}

private fun formatRemaining(remainingMillis: Long): String {
    val totalSeconds = (remainingMillis / 1000).coerceAtLeast(0)
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d:%02d".format(hours, minutes, seconds)
}

private fun statsQualityMessage(quality: Int): String {
    return when {
        quality >= 82 -> "Tu calidad de sueno va muy bien. Mantener el celular quieto y reducir pantalla antes de dormir esta sumando puntos."
        quality >= 70 -> "Tu descanso esta en zona buena. Ajustar la hora de dormir y bajar el uso previo del celular puede mejorar la calidad."
        else -> "Tu semana muestra margen de mejora. El modo sueno y el bloqueo inteligente ayudan a proteger el horario configurado."
    }
}
