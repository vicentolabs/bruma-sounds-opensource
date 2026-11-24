package com.sonidosfondo.bruma

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.sonidosfondo.bruma.audio.AmbientAudioService
import com.sonidosfondo.bruma.audio.PlaybackStore
import com.sonidosfondo.bruma.ui.BrumaScreen
import com.sonidosfondo.bruma.ui.BrumaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        PlaybackStore.initialize(applicationContext)
        if (PlaybackStore.state.value.isPlaying) {
            AmbientAudioService.play(applicationContext)
        }

        setContent {
            BrumaTheme {
                BrumaScreen()
            }
        }
    }
}
