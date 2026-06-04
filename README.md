# SleepApp

Aplicacion movil Android para configurar horarios de sueno, activar un modo de descanso, bloquear apps seleccionadas durante el horario y ver estadisticas personales.

## Tecnologia

- Kotlin
- Jetpack Compose
- Material 3
- Navigation Compose
- Almacenamiento local con `SharedPreferences` + Gson
- Servicio de accesibilidad para bloqueo inteligente
- Sensor acelerometro para detectar movimiento durante el modo sueno

## Funciones incluidas

- Pantalla de inicio con proximo horario, calidad, racha y puntos.
- Configuracion de hora de dormir, hora de despertar, dias activos y objetivo.
- Bloqueo inteligente local de apps comunes.
- Modo sueno activado con temporizador, extension de tiempo y deteccion de movimiento.
- Estadisticas con calidad promedio y grafico por dias.
- Retos, logros, racha actual y puntos acumulados.
- Perfil local con preferencias.

## Nota sobre almacenamiento

La app no usa API ni servidor. Los datos se guardan en el celular y ocupan poco espacio: configuracion, sesiones recientes, puntos, rachas y preferencias. No se puede hacer una app que no ocupe nada de espacio, pero este enfoque evita base de datos remota y mantiene el uso local ligero.

## Bloqueo de apps

Android no permite bloquear otras apps libremente sin permisos especiales. Esta version incluye un `AccessibilityService` local. Para que funcione, el usuario debe habilitar:

- Ajustes del sistema
- Accesibilidad
- Bloqueo inteligente de SleepApp

Cuando el modo sueno esta activo y se abre una app marcada como bloqueada, SleepApp redirige al usuario de vuelta a la app.

## Compilacion local

Usa JDK 21 y Android SDK configurado.

```powershell
.\gradlew.bat :app:assembleDebug
```

APK generado:

```text
app/build/outputs/apk/debug/app-debug.apk
```
