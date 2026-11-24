package com.sonidosfondo.bruma.audio

import android.content.Context
import android.content.SharedPreferences
import com.sonidosfondo.bruma.model.SoundCatalog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class PlaybackState(
    val activeSoundIds: List<String> = emptyList(),
    val soundVolumes: Map<String, Float> = SoundCatalog.sounds.associate { it.id to it.defaultVolume },
    val masterVolume: Float = 0.72f,
    val isPlaying: Boolean = false,
    val favoriteIds: Set<String> = emptySet(),
    val timerMinutes: Int = 0,
    val timerEndAtMillis: Long = 0L,
)

object PlaybackStore {
    private const val PREFS_NAME = "bruma_playback"
    private const val KEY_ACTIVE_IDS = "active_ids"
    private const val KEY_FAVORITES = "favorites"
    private const val KEY_MASTER_VOLUME = "master_volume"
    private const val KEY_IS_PLAYING = "is_playing"
    private const val KEY_TIMER_MINUTES = "timer_minutes"
    private const val KEY_TIMER_END = "timer_end"
    private const val KEY_VOLUME_PREFIX = "volume_"

    private var preferences: SharedPreferences? = null
    private val mutableState = MutableStateFlow(PlaybackState())
    val state = mutableState.asStateFlow()

    @Synchronized
    fun initialize(context: Context) {
        if (preferences != null) return

        val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        preferences = prefs
        val knownIds = SoundCatalog.byId.keys
        val activeIds = prefs.getString(KEY_ACTIVE_IDS, "")
            .orEmpty()
            .split(',')
            .filter { it in knownIds }
            .distinct()
        val favorites = prefs.getString(KEY_FAVORITES, "")
            .orEmpty()
            .split(',')
            .filter { it in knownIds }
            .toSet()
        val volumes = SoundCatalog.sounds.associate { sound ->
            sound.id to prefs.getFloat("$KEY_VOLUME_PREFIX${sound.id}", sound.defaultVolume).coerceIn(0f, 1f)
        }

        mutableState.value = PlaybackState(
            activeSoundIds = activeIds,
            soundVolumes = volumes,
            masterVolume = prefs.getFloat(KEY_MASTER_VOLUME, 0.72f).coerceIn(0f, 1f),
            isPlaying = prefs.getBoolean(KEY_IS_PLAYING, false),
            favoriteIds = favorites,
            timerMinutes = prefs.getInt(KEY_TIMER_MINUTES, 0).coerceAtLeast(0),
            timerEndAtMillis = prefs.getLong(KEY_TIMER_END, 0L),
        )
    }

    fun ensureDefaultMix() {
        val current = mutableState.value
        if (current.activeSoundIds.isEmpty()) {
            publish(current.copy(activeSoundIds = listOf("rain")))
        }
    }

    fun setActiveSoundIds(soundIds: List<String>) {
        val validIds = soundIds.filter { it in SoundCatalog.byId }.distinct()
        publish(mutableState.value.copy(activeSoundIds = validIds))
    }

    fun toggleSound(soundId: String) {
        if (soundId !in SoundCatalog.byId) return
        val current = mutableState.value
        val updated = if (soundId in current.activeSoundIds) {
            current.activeSoundIds - soundId
        } else {
            current.activeSoundIds + soundId
        }
        publish(current.copy(activeSoundIds = updated))
    }

    fun setSoundVolume(soundId: String, volume: Float) {
        val current = mutableState.value
        if (soundId !in SoundCatalog.byId) return
        publish(current.copy(soundVolumes = current.soundVolumes + (soundId to volume.coerceIn(0f, 1f))))
    }

    fun setMasterVolume(volume: Float) {
        publish(mutableState.value.copy(masterVolume = volume.coerceIn(0f, 1f)))
    }

    fun toggleFavorite(soundId: String) {
        if (soundId !in SoundCatalog.byId) return
        val current = mutableState.value
        val favorites = if (soundId in current.favoriteIds) {
            current.favoriteIds - soundId
        } else {
            current.favoriteIds + soundId
        }
        publish(current.copy(favoriteIds = favorites))
    }

    fun setPlaying(isPlaying: Boolean) {
        val current = mutableState.value
        publish(current.copy(isPlaying = isPlaying, timerEndAtMillis = if (isPlaying) current.timerEndAtMillis else 0L))
    }

    fun setTimerMinutes(minutes: Int) {
        val current = mutableState.value
        val safeMinutes = minutes.coerceAtLeast(0)
        val endAt = if (current.isPlaying && safeMinutes > 0) {
            System.currentTimeMillis() + safeMinutes * 60_000L
        } else {
            0L
        }
        publish(current.copy(timerMinutes = safeMinutes, timerEndAtMillis = endAt))
    }

    fun setTimerEndAtMillis(endAtMillis: Long) {
        publish(mutableState.value.copy(timerEndAtMillis = endAtMillis.coerceAtLeast(0L)))
    }

    private fun publish(newState: PlaybackState) {
        mutableState.value = newState
        val editor = preferences?.edit() ?: return
        editor.putString(KEY_ACTIVE_IDS, newState.activeSoundIds.joinToString(","))
        editor.putString(KEY_FAVORITES, newState.favoriteIds.joinToString(","))
        editor.putFloat(KEY_MASTER_VOLUME, newState.masterVolume)
        editor.putBoolean(KEY_IS_PLAYING, newState.isPlaying)
        editor.putInt(KEY_TIMER_MINUTES, newState.timerMinutes)
        editor.putLong(KEY_TIMER_END, newState.timerEndAtMillis)
        SoundCatalog.sounds.forEach { sound ->
            editor.putFloat("$KEY_VOLUME_PREFIX${sound.id}", newState.soundVolumes[sound.id] ?: sound.defaultVolume)
        }
        editor.apply()
    }
}
