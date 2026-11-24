package com.sonidosfondo.bruma.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.PowerManager
import android.util.Log

/** Plays bundled real recordings as independent looping tracks without requesting audio focus. */
class AudioEngine(context: Context) {
    private val appContext = context.applicationContext
    private val tracks = mutableMapOf<String, MediaPlayer>()
    private var currentState = PlaybackState()
    private var fadeGain = 1f

    @Synchronized
    fun sync(state: PlaybackState) {
        currentState = state
        val wanted = if (state.isPlaying) state.activeSoundIds.toSet() else emptySet()

        (tracks.keys - wanted).forEach(::releaseTrack)

        wanted.forEach { soundId ->
            val player = tracks.getOrPut(soundId) { createPlayer(soundId) }
            applyVolume(soundId, player)
            if (!player.isPlaying) player.start()
        }
    }

    @Synchronized
    fun setFadeGain(gain: Float) {
        fadeGain = gain.coerceIn(0f, 1f)
        tracks.forEach { (soundId, player) -> applyVolume(soundId, player) }
    }

    @Synchronized
    fun release() {
        tracks.keys.toList().forEach(::releaseTrack)
    }

    private fun createPlayer(soundId: String): MediaPlayer {
        val assetName = AudioAssets.fileFor(soundId)
            ?: error("No hay una grabación local configurada para $soundId")
        val player = MediaPlayer()
        try {
            player.setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build(),
            )
            player.setWakeMode(appContext, PowerManager.PARTIAL_WAKE_LOCK)
            player.isLooping = true
            val asset = appContext.assets.openFd("audio/$assetName")
            try {
                player.setDataSource(asset.fileDescriptor, asset.startOffset, asset.length)
                player.prepare()
            } finally {
                asset.close()
            }
            return player
        } catch (error: Exception) {
            runCatching { player.release() }
            Log.e(TAG, "No se pudo cargar la grabación $assetName", error)
            throw error
        }
    }

    private fun applyVolume(soundId: String, player: MediaPlayer) {
        val trackVolume = currentState.soundVolumes[soundId] ?: 0.68f
        val volume = (currentState.masterVolume * trackVolume * fadeGain).coerceIn(0f, 1f)
        player.setVolume(volume, volume)
    }

    private fun releaseTrack(soundId: String) {
        val player = tracks.remove(soundId) ?: return
        runCatching { if (player.isPlaying) player.stop() }
        runCatching { player.release() }
    }

    private companion object {
        const val TAG = "BrumaAudioEngine"
    }
}
