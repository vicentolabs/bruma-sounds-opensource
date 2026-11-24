package com.sonidosfondo.bruma.audio

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.sonidosfondo.bruma.MainActivity
import com.sonidosfondo.bruma.R
import com.sonidosfondo.bruma.model.SoundCatalog

class AmbientAudioService : Service() {
    private lateinit var audioEngine: AudioEngine
    private val handler = Handler(Looper.getMainLooper())
    private var timerEndAtMillis = 0L

    private val timerTick = object : Runnable {
        override fun run() {
            val remaining = timerEndAtMillis - System.currentTimeMillis()
            if (remaining <= 0L) {
                finishWithFade()
                return
            }

            val fadeWindowMillis = 12_000L
            val fade = if (remaining < fadeWindowMillis) remaining.toFloat() / fadeWindowMillis else 1f
            if (remaining <= fadeWindowMillis) audioEngine.setFadeGain(fade)
            if (remaining <= fadeWindowMillis || remaining % 60_000L < 1_000L) updateNotification()

            val nextDelay = if (remaining > fadeWindowMillis + 60_000L) {
                (remaining % 60_000L).takeIf { it > 0L } ?: 60_000L
            } else {
                1_000L
            }
            handler.postDelayed(this, nextDelay)
        }
    }

    override fun onCreate() {
        super.onCreate()
        PlaybackStore.initialize(applicationContext)
        audioEngine = AudioEngine(applicationContext)
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(TAG, "onStartCommand action=${intent?.action} startId=$startId")
        when (intent?.action) {
            ACTION_PLAY, null -> startPlayback()
            ACTION_SYNC -> syncPlayback()
            ACTION_PAUSE, ACTION_STOP -> stopPlayback()
            ACTION_SET_TIMER -> {
                val minutes = intent?.getIntExtra(EXTRA_TIMER_MINUTES, 0) ?: 0
                PlaybackStore.setTimerMinutes(minutes)
                if (PlaybackStore.state.value.isPlaying) {
                    timerEndAtMillis = PlaybackStore.state.value.timerEndAtMillis
                    scheduleTimer()
                    updateNotification()
                }
            }
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        handler.removeCallbacks(timerTick)
        audioEngine.release()
        super.onDestroy()
    }

    private fun startPlayback() {
        PlaybackStore.ensureDefaultMix()
        PlaybackStore.setPlaying(true)
        val state = PlaybackStore.state.value
        Log.i(TAG, "Starting playback: sounds=${state.activeSoundIds}, timer=${state.timerMinutes}m")
        if (state.timerMinutes > 0 && state.timerEndAtMillis == 0L) {
            PlaybackStore.setTimerEndAtMillis(System.currentTimeMillis() + state.timerMinutes * 60_000L)
        }

        startAsForeground()
        runCatching { audioEngine.sync(PlaybackStore.state.value) }
            .onFailure { error ->
                Log.e(TAG, "No se pudo iniciar la mezcla de audio", error)
                PlaybackStore.setPlaying(false)
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
                return
            }
        Log.i(TAG, "Audio engine started successfully")

        timerEndAtMillis = PlaybackStore.state.value.timerEndAtMillis
        scheduleTimer()
        updateNotification()
    }

    private fun syncPlayback() {
        val state = PlaybackStore.state.value
        if (!state.isPlaying || state.activeSoundIds.isEmpty()) {
            stopPlayback()
            return
        }

        startAsForeground()
        runCatching { audioEngine.sync(state) }
            .onFailure { error ->
                Log.e(TAG, "No se pudo actualizar la mezcla de audio", error)
                PlaybackStore.setPlaying(false)
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
                return
            }
        timerEndAtMillis = state.timerEndAtMillis
        scheduleTimer()
        updateNotification()
    }

    private fun stopPlayback() {
        Log.w(TAG, "Stopping playback")
        handler.removeCallbacks(timerTick)
        timerEndAtMillis = 0L
        PlaybackStore.setPlaying(false)
        audioEngine.release()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun finishWithFade() {
        handler.removeCallbacks(timerTick)
        audioEngine.setFadeGain(0f)
        PlaybackStore.setPlaying(false)
        PlaybackStore.setTimerMinutes(0)
        audioEngine.release()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun scheduleTimer() {
        handler.removeCallbacks(timerTick)
        if (timerEndAtMillis <= 0L || !PlaybackStore.state.value.isPlaying) {
            audioEngine.setFadeGain(1f)
            return
        }
        if (timerEndAtMillis <= System.currentTimeMillis()) {
            finishWithFade()
        } else {
            audioEngine.setFadeGain(1f)
            val remaining = timerEndAtMillis - System.currentTimeMillis()
            val nextDelay = if (remaining > 72_000L) {
                (remaining % 60_000L).takeIf { it > 0L } ?: 60_000L
            } else {
                1_000L
            }
            handler.postDelayed(timerTick, nextDelay)
        }
    }

    private fun startAsForeground() {
        val notification = buildNotification()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK,
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    private fun buildNotification(): Notification {
        val state = PlaybackStore.state.value
        val activeNames = state.activeSoundIds
            .mapNotNull { SoundCatalog.byId[it]?.title }
            .joinToString(" · ")
            .ifBlank { "Sonidos ambientales" }
        val openApp = PendingIntent.getActivity(
            this,
            REQUEST_OPEN_APP,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val pause = servicePendingIntent(ACTION_PAUSE, REQUEST_PAUSE)
        val stop = servicePendingIntent(ACTION_STOP, REQUEST_STOP)

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_bruma)
            .setContentTitle("Bruma · Sonidos de fondo")
            .setContentText(activeNames)
            .setContentIntent(openApp)
            .setCategory(NotificationCompat.CATEGORY_TRANSPORT)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOngoing(state.isPlaying)
            .setOnlyAlertOnce(true)
            .addAction(android.R.drawable.ic_media_pause, "Pausar", pause)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Detener", stop)
            .build()
    }

    private fun servicePendingIntent(action: String, requestCode: Int): PendingIntent {
        val intent = Intent(this, AmbientAudioService::class.java).setAction(action)
        return PendingIntent.getService(
            this,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private fun updateNotification() {
        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        runCatching {
            NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, buildNotification())
        }
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Reproducción de Bruma",
            NotificationManager.IMPORTANCE_LOW,
        ).apply {
            description = "Controles para los sonidos ambientales en segundo plano"
            setShowBadge(false)
            setSound(null, null)
            enableVibration(false)
        }
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    companion object {
        private const val TAG = "BrumaAudioService"
        private const val CHANNEL_ID = "bruma_playback"
        private const val NOTIFICATION_ID = 7104
        private const val REQUEST_OPEN_APP = 1
        private const val REQUEST_PAUSE = 2
        private const val REQUEST_STOP = 3

        const val ACTION_PLAY = "com.sonidosfondo.bruma.action.PLAY"
        const val ACTION_SYNC = "com.sonidosfondo.bruma.action.SYNC"
        const val ACTION_PAUSE = "com.sonidosfondo.bruma.action.PAUSE"
        const val ACTION_STOP = "com.sonidosfondo.bruma.action.STOP"
        const val ACTION_SET_TIMER = "com.sonidosfondo.bruma.action.SET_TIMER"
        const val EXTRA_TIMER_MINUTES = "timer_minutes"

        fun play(context: Context) {
            val intent = Intent(context, AmbientAudioService::class.java).setAction(ACTION_PLAY)
            context.startForegroundService(intent)
        }

        fun sync(context: Context) {
            val intent = Intent(context, AmbientAudioService::class.java).setAction(ACTION_SYNC)
            context.startService(intent)
        }

        fun pause(context: Context) {
            val intent = Intent(context, AmbientAudioService::class.java).setAction(ACTION_PAUSE)
            context.startService(intent)
        }

        fun setTimer(context: Context, minutes: Int) {
            val intent = Intent(context, AmbientAudioService::class.java)
                .setAction(ACTION_SET_TIMER)
                .putExtra(EXTRA_TIMER_MINUTES, minutes)
            context.startService(intent)
        }
    }
}
