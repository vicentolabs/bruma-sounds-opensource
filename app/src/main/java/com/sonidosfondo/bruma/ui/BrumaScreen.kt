package com.sonidosfondo.bruma.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.annotation.StringRes
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Air
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Cloud
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material.icons.rounded.Waves
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.input.KeyboardType
import androidx.core.content.ContextCompat
import com.sonidosfondo.bruma.R
import com.sonidosfondo.bruma.audio.AmbientAudioService
import com.sonidosfondo.bruma.audio.PlaybackState
import com.sonidosfondo.bruma.audio.PlaybackStore
import com.sonidosfondo.bruma.model.SceneCatalog
import com.sonidosfondo.bruma.model.ScenePreset
import com.sonidosfondo.bruma.model.AudioCredits
import com.sonidosfondo.bruma.model.SoundCatalog
import com.sonidosfondo.bruma.model.SoundCategory
import com.sonidosfondo.bruma.model.Soundscape
import kotlinx.coroutines.delay

private enum class SoundFilter(@StringRes val labelRes: Int) {
    ALL(R.string.filter_all),
    NATURE(R.string.filter_nature),
    NOISE(R.string.filter_noise),
    AMBIENCE(R.string.filter_ambience),
    FAVORITES(R.string.filter_favorites),
}

@Composable
fun BrumaScreen() {
    val context = LocalContext.current
    val state by PlaybackStore.state.collectAsState()
    var filter by remember { mutableStateOf(SoundFilter.ALL) }
    var showTimerDialog by remember { mutableStateOf(false) }
    var showCreditsDialog by remember { mutableStateOf(false) }

    val startPlayback = remember(context) { { beginPlayback(context) } }
    val notificationPermission = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { startPlayback() },
    )
    val requestPlayback = {
        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            startPlayback()
        }
    }

    val visibleSounds = SoundCatalog.sounds.filter { sound ->
        when (filter) {
            SoundFilter.ALL -> true
            SoundFilter.NATURE -> sound.category == SoundCategory.NATURE
            SoundFilter.NOISE -> sound.category == SoundCategory.NOISE
            SoundFilter.AMBIENCE -> sound.category == SoundCategory.AMBIENCE
            SoundFilter.FAVORITES -> sound.id in state.favoriteIds
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BrumaBackground)
            .statusBarsPadding()
            .navigationBarsPadding(),
        contentPadding = PaddingValues(start = 20.dp, top = 12.dp, end = 20.dp, bottom = 30.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp),
    ) {
        item { BrandHeader(isPlaying = state.isPlaying) }

        item {
            HeroCard(
                state = state,
                onPlayToggle = {
                    if (PlaybackStore.state.value.isPlaying) {
                        AmbientAudioService.pause(context)
                    } else {
                        requestPlayback()
                    }
                },
            )
        }

        if (state.activeSoundIds.isNotEmpty()) {
            item {
                MixPanel(
                    state = state,
                    onSoundVolumeChange = { id, volume ->
                        PlaybackStore.setSoundVolume(id, volume)
                        if (PlaybackStore.state.value.isPlaying) AmbientAudioService.sync(context)
                    },
                    onMasterVolumeChange = { volume ->
                        PlaybackStore.setMasterVolume(volume)
                        if (PlaybackStore.state.value.isPlaying) AmbientAudioService.sync(context)
                    },
                    onRemoveSound = { id ->
                        PlaybackStore.toggleSound(id)
                        if (PlaybackStore.state.value.isPlaying) AmbientAudioService.sync(context)
                    },
                    onTimerClick = { showTimerDialog = true },
                )
            }
        }

        item {
            SectionHeading(
                title = stringResource(R.string.section_find_sound_title),
                subtitle = stringResource(R.string.section_find_sound_subtitle),
            )
        }

        item {
            FilterRow(selected = filter, onSelected = { filter = it })
        }

        if (visibleSounds.isEmpty()) {
            item { EmptyFavoritesCard() }
        } else {
            items(visibleSounds.chunked(2)) { soundPair ->
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    soundPair.forEach { sound ->
                        SoundCard(
                            modifier = Modifier.weight(1f),
                            sound = sound,
                            selected = sound.id in state.activeSoundIds,
                            favorite = sound.id in state.favoriteIds,
                            onToggle = {
                                val current = PlaybackStore.state.value
                                val wasSelected = sound.id in current.activeSoundIds
                                PlaybackStore.toggleSound(sound.id)
                                val updated = PlaybackStore.state.value
                                if (updated.isPlaying) {
                                    AmbientAudioService.sync(context)
                                } else if (!wasSelected) {
                                    requestPlayback()
                                }
                            },
                            onAddToMix = {
                                val current = PlaybackStore.state.value
                                if (sound.id !in current.activeSoundIds) {
                                    PlaybackStore.setActiveSoundIds(current.activeSoundIds + sound.id)
                                }
                                if (current.isPlaying) {
                                    AmbientAudioService.sync(context)
                                } else {
                                    requestPlayback()
                                }
                            },
                            onFavorite = { PlaybackStore.toggleFavorite(sound.id) },
                        )
                    }
                    if (soundPair.size == 1) Spacer(Modifier.weight(1f))
                }
            }
        }

        item {
            SectionHeading(
                title = stringResource(R.string.section_scenes_title),
                subtitle = stringResource(R.string.section_scenes_subtitle),
            )
        }

        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(end = 4.dp),
            ) {
                items(SceneCatalog.scenes) { scene ->
                    SceneCard(
                        scene = scene,
                        onUse = {
                            PlaybackStore.setActiveSoundIds(scene.soundIds)
                            if (PlaybackStore.state.value.isPlaying) {
                                AmbientAudioService.sync(context)
                            } else {
                                requestPlayback()
                            }
                        },
                    )
                }
            }
        }

        item {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth().padding(top = 2.dp)) {
                Text(
                    text = stringResource(R.string.recordings_offline),
                    color = BrumaMuted.copy(alpha = 0.78f),
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                )
                TextButton(onClick = { showCreditsDialog = true }) {
                    Text(stringResource(R.string.audio_sources_licenses), color = BrumaAccent, fontSize = 12.sp)
                }
            }
        }
    }

    if (showTimerDialog) {
        TimerDialog(
            selectedMinutes = state.timerMinutes,
            onDismiss = { showTimerDialog = false },
            onApply = { minutes ->
                PlaybackStore.setTimerMinutes(minutes)
                if (PlaybackStore.state.value.isPlaying) AmbientAudioService.setTimer(context, minutes)
                showTimerDialog = false
            },
        )
    }

    if (showCreditsDialog) {
        AudioCreditsDialog(onDismiss = { showCreditsDialog = false })
    }
}

private fun beginPlayback(context: Context) {
    PlaybackStore.ensureDefaultMix()
    PlaybackStore.setPlaying(true)
    AmbientAudioService.play(context)
}

@Composable
private fun BrandHeader(isPlaying: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(Color(0xFF263C33)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Rounded.GraphicEq, contentDescription = null, tint = BrumaAccent, modifier = Modifier.size(25.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(
                text = "BRUMA",
                color = BrumaText,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.4.sp,
            )
            Text(
                text = stringResource(R.string.brand_tagline),
                color = BrumaMuted,
                fontSize = 9.sp,
                letterSpacing = 1.05.sp,
            )
        }
        Spacer(Modifier.weight(1f))
        if (isPlaying) {
            Row(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color(0xFF202C27))
                    .padding(horizontal = 11.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(Modifier.size(6.dp).clip(CircleShape).background(BrumaAccent))
                Spacer(Modifier.width(7.dp))
                Text(stringResource(R.string.status_playing), color = BrumaAccent, fontSize = 9.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.8.sp)
            }
        }
    }
}

@Composable
private fun HeroCard(state: PlaybackState, onPlayToggle: () -> Unit) {
    val selectedSounds = state.activeSoundIds.mapNotNull { SoundCatalog.byId[it] }
    val mixTitle = when (selectedSounds.size) {
        0 -> stringResource(R.string.hero_default_mix)
        1 -> stringResource(selectedSounds.first().titleRes)
        else -> stringResource(R.string.hero_mix_count, selectedSounds.size)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(252.dp)
            .clip(RoundedCornerShape(30.dp)),
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xFF355C4E), Color(0xFF25443E), Color(0xFF1B2B38)),
                    ),
                ),
        )
        Canvas(Modifier.fillMaxSize()) {
            drawCircle(
                color = BrumaAccent.copy(alpha = 0.075f),
                radius = size.width * 0.48f,
                center = Offset(size.width * 0.93f, size.height * 0.04f),
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.035f),
                radius = size.width * 0.34f,
                center = Offset(size.width * 0.98f, size.height * 0.02f),
                style = Stroke(width = 1.dp.toPx()),
            )
            val bars = listOf(14f, 24f, 38f, 50f, 34f, 22f, 42f, 27f, 16f)
            val gap = 8.dp.toPx()
            val barWidth = 4.dp.toPx()
            val totalWidth = bars.size * barWidth + (bars.size - 1) * gap
            val startX = size.width - totalWidth - 26.dp.toPx()
            bars.forEachIndexed { index, heightDp ->
                val barHeight = heightDp.dp.toPx()
                drawRoundRect(
                    color = Color.White.copy(alpha = 0.13f),
                    topLeft = Offset(startX + index * (barWidth + gap), size.height * 0.24f - barHeight / 2f),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(barWidth, barWidth),
                )
            }
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(22.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(7.dp).clip(CircleShape).background(BrumaAccent))
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.hero_eyebrow), color = BrumaAccent, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 1.45.sp)
            }

            Column(modifier = Modifier.fillMaxWidth(0.84f)) {
                Text(
                    text = stringResource(R.string.hero_headline),
                    color = BrumaText,
                    fontSize = 31.sp,
                    lineHeight = 35.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = (-0.65).sp,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.hero_description),
                    color = BrumaText.copy(alpha = 0.76f),
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(if (state.activeSoundIds.isEmpty()) R.string.hero_start_label else R.string.hero_mix_label),
                        color = BrumaText.copy(alpha = 0.62f),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.25.sp,
                    )
                    Spacer(Modifier.height(3.dp))
                    Text(mixTitle, color = BrumaText, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                IconButton(
                    onClick = onPlayToggle,
                    modifier = Modifier.size(54.dp).background(BrumaAccent, CircleShape),
                ) {
                    Icon(
                        imageVector = if (state.isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                        contentDescription = stringResource(if (state.isPlaying) R.string.action_pause_sounds else R.string.action_play_sounds),
                        tint = Color(0xFF1D2A1B),
                        modifier = Modifier.size(27.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun MixPanel(
    state: PlaybackState,
    onSoundVolumeChange: (String, Float) -> Unit,
    onMasterVolumeChange: (Float) -> Unit,
    onRemoveSound: (String) -> Unit,
    onTimerClick: () -> Unit,
) {
    var now by remember { mutableStateOf(System.currentTimeMillis()) }
    LaunchedEffect(state.timerEndAtMillis) {
        while (state.timerEndAtMillis > 0L) {
            now = System.currentTimeMillis()
            delay(1_000L)
        }
    }
    val timerLabel = if (state.timerEndAtMillis > 0L) {
        val seconds = (state.timerEndAtMillis - now).coerceAtLeast(0L) / 1_000L
        val hours = seconds / 3_600L
        val minutes = (seconds % 3_600L) / 60L
        when {
            hours > 0L && minutes > 0L -> stringResource(R.string.timer_format_hours_minutes, hours, minutes)
            hours > 0L -> stringResource(R.string.timer_format_hours, hours)
            minutes > 0L -> stringResource(R.string.timer_format_minutes, minutes)
            else -> pluralStringResource(R.plurals.timer_format_seconds, seconds.toInt(), seconds)
        }
    } else if (state.timerMinutes > 0) {
        stringResource(R.string.timer_starting_in, formatTimerDuration(state.timerMinutes.toLong()))
    } else {
        stringResource(R.string.timer_schedule)
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        color = BrumaSurface,
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.055f)),
    ) {
        Column(Modifier.padding(horizontal = 17.dp, vertical = 16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(stringResource(R.string.mix_current), color = BrumaMuted, style = MaterialTheme.typography.labelSmall)
                    Spacer(Modifier.height(3.dp))
                    Text(stringResource(R.string.mix_adjust_layers), color = BrumaText, style = MaterialTheme.typography.titleMedium)
                }
                TextButton(onClick = onTimerClick, contentPadding = PaddingValues(horizontal = 9.dp, vertical = 6.dp)) {
                    Icon(Icons.Rounded.Timer, contentDescription = null, tint = BrumaAccent, modifier = Modifier.size(17.dp))
                    Spacer(Modifier.width(5.dp))
                    Text(timerLabel, color = BrumaAccent, fontSize = 12.sp, maxLines = 1)
                }
            }

            Spacer(Modifier.height(8.dp))
            state.activeSoundIds.forEach { id ->
                val sound = SoundCatalog.byId[id] ?: return@forEach
                SoundVolumeRow(
                    sound = sound,
                    volume = state.soundVolumes[id] ?: sound.defaultVolume,
                    onVolumeChange = { onSoundVolumeChange(id, it) },
                    onRemove = { onRemoveSound(id) },
                )
            }

            HorizontalDivider(color = Color.White.copy(alpha = 0.075f), modifier = Modifier.padding(top = 8.dp, bottom = 11.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.AutoMirrored.Rounded.VolumeUp, contentDescription = null, tint = BrumaMuted, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.master_volume), color = BrumaText, fontSize = 13.sp, modifier = Modifier.weight(1f))
                Text("${(state.masterVolume * 100).toInt()}%", color = BrumaMuted, fontSize = 12.sp)
            }
            Slider(
                value = state.masterVolume,
                onValueChange = onMasterVolumeChange,
                valueRange = 0f..1f,
                modifier = Modifier.fillMaxWidth().height(34.dp),
                colors = SliderDefaults.colors(
                    thumbColor = BrumaAccent,
                    activeTrackColor = BrumaAccent,
                    inactiveTrackColor = Color(0xFF38443F),
                ),
            )
        }
    }
}

@Composable
private fun SoundVolumeRow(
    sound: Soundscape,
    volume: Float,
    onVolumeChange: (Float) -> Unit,
    onRemove: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.size(34.dp).clip(CircleShape).background(soundTint(sound.id).copy(alpha = 0.14f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(soundIcon(sound.id), contentDescription = null, tint = soundTint(sound.id), modifier = Modifier.size(17.dp))
        }
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(sound.titleRes), color = BrumaText, fontSize = 12.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                Text("${(volume * 100).toInt()}%", color = BrumaMuted, fontSize = 11.sp)
            }
            Slider(
                value = volume,
                onValueChange = onVolumeChange,
                valueRange = 0f..1f,
                modifier = Modifier.fillMaxWidth().height(28.dp),
                colors = SliderDefaults.colors(
                    thumbColor = soundTint(sound.id),
                    activeTrackColor = soundTint(sound.id),
                    inactiveTrackColor = Color(0xFF38443F),
                ),
            )
        }
        Spacer(Modifier.width(3.dp))
        val removeSoundDescription = stringResource(R.string.accessibility_remove_from_mix)
        IconButton(
            onClick = onRemove,
            modifier = Modifier.size(36.dp).semantics { contentDescription = removeSoundDescription },
        ) {
            Text("×", color = BrumaMuted, fontSize = 22.sp, lineHeight = 22.sp)
        }
    }
}

@Composable
private fun SectionHeading(title: String, subtitle: String) {
    Column {
        Text(title, color = BrumaText, style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(4.dp))
        Text(subtitle, color = BrumaMuted, fontSize = 12.sp, lineHeight = 17.sp)
    }
}

@Composable
private fun FilterRow(selected: SoundFilter, onSelected: (SoundFilter) -> Unit) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(SoundFilter.entries) { filter ->
            FilterChip(
                selected = selected == filter,
                onClick = { onSelected(filter) },
                label = { Text(stringResource(filter.labelRes), fontSize = 12.sp) },
                shape = CircleShape,
                colors = FilterChipDefaults.filterChipColors(),
            )
        }
    }
}

@Composable
private fun SoundCard(
    modifier: Modifier,
    sound: Soundscape,
    selected: Boolean,
    favorite: Boolean,
    onToggle: () -> Unit,
    onAddToMix: () -> Unit,
    onFavorite: () -> Unit,
) {
    val tint = soundTint(sound.id)
    Surface(
        modifier = modifier.height(143.dp).clickable(onClick = onToggle),
        shape = RoundedCornerShape(23.dp),
        color = if (selected) Color(0xFF22352D) else BrumaSurface,
        border = BorderStroke(
            1.dp,
            if (selected) BrumaAccent.copy(alpha = 0.48f) else Color.White.copy(alpha = 0.055f),
        ),
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 13.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(39.dp).clip(CircleShape).background(tint.copy(alpha = 0.14f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(soundIcon(sound.id), contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
                }
                Spacer(Modifier.weight(1f))
                IconButton(onClick = onAddToMix, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = if (selected) Icons.Rounded.Check else Icons.Rounded.Add,
                        contentDescription = stringResource(if (selected) R.string.accessibility_sound_in_mix else R.string.accessibility_add_to_mix),
                        tint = if (selected) BrumaAccent else BrumaMuted.copy(alpha = 0.75f),
                        modifier = Modifier.size(18.dp),
                    )
                }
                IconButton(onClick = onFavorite, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = if (favorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                        contentDescription = stringResource(if (favorite) R.string.accessibility_remove_favorite else R.string.accessibility_add_favorite),
                        tint = if (favorite) Color(0xFFF09A91) else BrumaMuted.copy(alpha = 0.75f),
                        modifier = Modifier.size(17.dp),
                    )
                }
            }
            Column {
                Text(stringResource(sound.titleRes), color = BrumaText, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(3.dp))
                Text(
                    text = if (selected) stringResource(R.string.sound_in_mix) else stringResource(sound.descriptionRes),
                    color = if (selected) BrumaAccent.copy(alpha = 0.9f) else BrumaMuted,
                    fontSize = 10.sp,
                    lineHeight = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun EmptyFavoritesCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = BrumaSurface,
    ) {
        Text(
            text = stringResource(R.string.empty_favorites),
            color = BrumaMuted,
            fontSize = 13.sp,
            lineHeight = 19.sp,
            modifier = Modifier.padding(18.dp),
        )
    }
}

@Composable
private fun SceneCard(
    scene: ScenePreset,
    onUse: () -> Unit,
) {
    val icon = when (scene.id) {
        "flight" -> Icons.Rounded.Air
        "road-trip" -> Icons.Rounded.DirectionsCar
        "data-center" -> Icons.Rounded.GraphicEq
        "fire-rain" -> Icons.Rounded.LocalFireDepartment
        "storm-sea" -> Icons.Rounded.Waves
        "blizzard-night" -> Icons.Rounded.Cloud
        else -> Icons.Rounded.WaterDrop
    }
    val accent = when (scene.id) {
        "flight" -> Color(0xFFB7C9F4)
        "data-center" -> Color(0xFF9DAFB1)
        "fire-rain" -> Color(0xFFF3B184)
        "storm-sea" -> Color(0xFF8EC7D8)
        "blizzard-night" -> Color(0xFFAFC8F1)
        else -> Color(0xFF9CCDBA)
    }

    Surface(
        modifier = Modifier.width(258.dp).height(194.dp).clickable(onClick = onUse),
        shape = RoundedCornerShape(24.dp),
        color = BrumaSurface,
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.06f)),
    ) {
        Column(Modifier.fillMaxSize().padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(42.dp).clip(CircleShape).background(accent.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(21.dp))
                }
                Spacer(Modifier.weight(1f))
                Text(stringResource(R.string.scene_label), color = BrumaMuted, fontSize = 9.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 1.15.sp)
            }
            Spacer(Modifier.height(12.dp))
            Text(stringResource(scene.titleRes), color = BrumaText, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(3.dp))
            Text(stringResource(scene.descriptionRes), color = BrumaMuted, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.weight(1f))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(R.string.scene_local_mix), color = BrumaMuted.copy(alpha = 0.9f), fontSize = 9.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.8.sp, modifier = Modifier.weight(1f))
                Button(
                    onClick = onUse,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2A3B32), contentColor = BrumaAccent),
                ) {
                    Text(stringResource(R.string.action_use), fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
private fun TimerDialog(
    selectedMinutes: Int,
    onDismiss: () -> Unit,
    onApply: (Int) -> Unit,
) {
    val options = listOf(0, 15, 30, 45, 60, 90, 120)
    var selected by remember(selectedMinutes) { mutableStateOf(selectedMinutes) }
    var customMode by remember(selectedMinutes) { mutableStateOf(selectedMinutes !in options) }
    var hoursText by remember(selectedMinutes) { mutableStateOf((selectedMinutes / 60).toString()) }
    var minutesText by remember(selectedMinutes) { mutableStateOf((selectedMinutes % 60).toString()) }
    val customMinutes = run {
        val hours = hoursText.toLongOrNull()?.coerceAtLeast(0L) ?: 0L
        val minutes = minutesText.toLongOrNull()?.coerceAtLeast(0L) ?: 0L
        (hours * 60L + minutes).coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.timer_title), color = BrumaText) },
        text = {
            Column(modifier = Modifier.heightIn(max = 430.dp).verticalScroll(rememberScrollState())) {
                Text(stringResource(R.string.timer_description), color = BrumaMuted, fontSize = 13.sp, lineHeight = 19.sp)
                Spacer(Modifier.height(16.dp))
                options.chunked(3).forEach { rowOptions ->
                    Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                        rowOptions.forEach { minutes ->
                            FilterChip(
                                selected = !customMode && selected == minutes,
                                onClick = {
                                    selected = minutes
                                    customMode = false
                                    hoursText = (minutes / 60).toString()
                                    minutesText = (minutes % 60).toString()
                                },
                                label = {
                                    Text(
                                        if (minutes == 0) stringResource(R.string.timer_off)
                                        else stringResource(R.string.timer_short_minutes, minutes),
                                        fontSize = 12.sp,
                                    )
                                },
                                shape = CircleShape,
                                colors = FilterChipDefaults.filterChipColors(),
                            )
                        }
                    }
                }
                TextButton(
                    onClick = { customMode = !customMode },
                    contentPadding = PaddingValues(horizontal = 0.dp, vertical = 5.dp),
                ) {
                    Text(
                        stringResource(if (customMode) R.string.timer_use_quick_times else R.string.timer_customize),
                        color = BrumaAccent,
                        fontSize = 13.sp,
                    )
                }
                if (customMode) {
                    Text(stringResource(R.string.timer_choose_duration), color = BrumaMuted, fontSize = 12.sp, lineHeight = 17.sp)
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = hoursText,
                            onValueChange = { hoursText = it.filter(Char::isDigit).take(8) },
                            modifier = Modifier.weight(1f),
                            label = { Text(stringResource(R.string.timer_hours)) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        )
                        OutlinedTextField(
                            value = minutesText,
                            onValueChange = { minutesText = it.filter(Char::isDigit).take(8) },
                            modifier = Modifier.weight(1f),
                            label = { Text(stringResource(R.string.timer_minutes)) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        )
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = if (customMinutes == 0) {
                            stringResource(R.string.timer_disabled)
                        } else {
                            stringResource(R.string.timer_turns_off_in, formatTimerDuration(customMinutes.toLong()))
                        },
                        color = BrumaMuted,
                        fontSize = 11.sp,
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onApply(if (customMode) customMinutes else selected) },
                colors = ButtonDefaults.buttonColors(containerColor = BrumaAccent, contentColor = Color(0xFF1D2A1B)),
            ) {
                Text(stringResource(R.string.action_apply))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_cancel), color = BrumaMuted) }
        },
        containerColor = BrumaSurface,
        shape = RoundedCornerShape(26.dp),
    )
}

@Composable
private fun formatTimerDuration(totalMinutes: Long): String {
    val hours = totalMinutes / 60L
    val minutes = totalMinutes % 60L
    return when {
        hours > 0L && minutes > 0L -> stringResource(R.string.timer_format_hours_minutes, hours, minutes)
        hours > 0L -> stringResource(R.string.timer_format_hours, hours)
        else -> stringResource(R.string.timer_format_minutes, minutes)
    }
}

@Composable
private fun AudioCreditsDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.audio_credits_title), color = BrumaText) },
        text = {
            Column(
                modifier = Modifier.heightIn(max = 430.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(7.dp),
            ) {
                Text(
                    stringResource(R.string.audio_credits_description),
                    color = BrumaMuted,
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                )
                AudioCredits.recordings.forEach { credit ->
                    val soundName = SoundCatalog.byId[credit.soundId]?.let { stringResource(it.titleRes) } ?: credit.soundId
                    Column(modifier = Modifier.fillMaxWidth().padding(top = 5.dp)) {
                        Text(soundName, color = BrumaText, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Text("${credit.recording} · ${credit.creator} · ${credit.license}", color = BrumaMuted, fontSize = 11.sp, lineHeight = 15.sp)
                        TextButton(
                            onClick = {
                                runCatching {
                                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(credit.sourceUrl)))
                                }
                            },
                            contentPadding = PaddingValues(horizontal = 0.dp, vertical = 2.dp),
                        ) {
                            Text(stringResource(R.string.audio_credits_view_source), color = BrumaAccent, fontSize = 11.sp)
                        }
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_close), color = BrumaAccent) } },
        containerColor = BrumaSurface,
        shape = RoundedCornerShape(26.dp),
    )
}

private fun soundIcon(soundId: String): ImageVector = when (soundId) {
    "rain" -> Icons.Rounded.WaterDrop
    "storm" -> Icons.Rounded.Cloud
    "fire", "wood-fire" -> Icons.Rounded.LocalFireDepartment
    "river", "ocean" -> Icons.Rounded.Waves
    "wind", "fan", "cabin", "heater", "ac", "extractor", "extractor-steady" -> Icons.Rounded.Air
    "driving" -> Icons.Rounded.DirectionsCar
    "datacenter" -> Icons.Rounded.GraphicEq
    "blizzard" -> Icons.Rounded.Cloud
    "car" -> Icons.Rounded.WaterDrop
    else -> Icons.Rounded.GraphicEq
}

private fun soundTint(soundId: String): Color = when (soundId) {
    "rain" -> Color(0xFF8BC8DC)
    "storm" -> Color(0xFF91A9D6)
    "fire", "wood-fire" -> Color(0xFFF0A674)
    "river" -> Color(0xFF82CBB7)
    "ocean" -> Color(0xFF78B9D5)
    "wind", "fan", "cabin", "heater", "ac" -> Color(0xFFBBC6D9)
    "datacenter" -> Color(0xFF9DAFB1)
    "extractor" -> Color(0xFFB6C2BA)
    "extractor-steady" -> Color(0xFFC2CCC6)
    "blizzard" -> Color(0xFFAFC8F1)
    "driving" -> Color(0xFFB7C4D9)
    "car" -> Color(0xFF8BC8DC)
    "brown" -> Color(0xFFC89B72)
    "pink" -> Color(0xFFE49EBC)
    "white" -> Color(0xFFE4E5D9)
    "grey" -> Color(0xFFB2B9BD)
    else -> BrumaAccent
}
