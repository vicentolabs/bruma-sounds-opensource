package com.sonidosfondo.bruma.model

import androidx.annotation.StringRes
import com.sonidosfondo.bruma.R

enum class SoundCategory {
    NATURE,
    NOISE,
    AMBIENCE,
}

data class Soundscape(
    val id: String,
    @StringRes val titleRes: Int,
    @StringRes val descriptionRes: Int,
    val category: SoundCategory,
    val defaultVolume: Float = 0.68f,
)

object SoundCatalog {
    val sounds = listOf(
        Soundscape("rain", R.string.sound_rain_title, R.string.sound_rain_description, SoundCategory.NATURE),
        Soundscape("storm", R.string.sound_storm_title, R.string.sound_storm_description, SoundCategory.NATURE),
        Soundscape("fire", R.string.sound_fire_title, R.string.sound_fire_description, SoundCategory.NATURE),
        Soundscape("wood-fire", R.string.sound_wood_fire_title, R.string.sound_wood_fire_description, SoundCategory.NATURE),
        Soundscape("river", R.string.sound_river_title, R.string.sound_river_description, SoundCategory.NATURE),
        Soundscape("ocean", R.string.sound_ocean_title, R.string.sound_ocean_description, SoundCategory.NATURE),
        Soundscape("wind", R.string.sound_wind_title, R.string.sound_wind_description, SoundCategory.NATURE),
        Soundscape("blizzard", R.string.sound_blizzard_title, R.string.sound_blizzard_description, SoundCategory.NATURE),
        Soundscape("fan", R.string.sound_fan_title, R.string.sound_fan_description, SoundCategory.AMBIENCE),
        Soundscape("heater", R.string.sound_heater_title, R.string.sound_heater_description, SoundCategory.AMBIENCE),
        Soundscape("ac", R.string.sound_ac_title, R.string.sound_ac_description, SoundCategory.AMBIENCE),
        Soundscape("datacenter", R.string.sound_datacenter_title, R.string.sound_datacenter_description, SoundCategory.AMBIENCE),
        Soundscape("extractor", R.string.sound_extractor_title, R.string.sound_extractor_description, SoundCategory.AMBIENCE),
        Soundscape("extractor-steady", R.string.sound_extractor_steady_title, R.string.sound_extractor_steady_description, SoundCategory.AMBIENCE),
        Soundscape("cabin", R.string.sound_cabin_title, R.string.sound_cabin_description, SoundCategory.AMBIENCE),
        Soundscape("driving", R.string.sound_driving_title, R.string.sound_driving_description, SoundCategory.AMBIENCE),
        Soundscape("car", R.string.sound_car_title, R.string.sound_car_description, SoundCategory.AMBIENCE),
        Soundscape("brown", R.string.sound_brown_noise_title, R.string.sound_brown_noise_description, SoundCategory.NOISE),
        Soundscape("pink", R.string.sound_pink_noise_title, R.string.sound_pink_noise_description, SoundCategory.NOISE),
        Soundscape("white", R.string.sound_white_noise_title, R.string.sound_white_noise_description, SoundCategory.NOISE),
        Soundscape("grey", R.string.sound_grey_noise_title, R.string.sound_grey_noise_description, SoundCategory.NOISE),
    )

    val byId = sounds.associateBy(Soundscape::id)
}

data class ScenePreset(
    val id: String,
    @StringRes val titleRes: Int,
    @StringRes val descriptionRes: Int,
    val soundIds: List<String>,
)

object SceneCatalog {
    val scenes = listOf(
        ScenePreset(
            id = "flight",
            titleRes = R.string.scene_flight_title,
            descriptionRes = R.string.scene_flight_description,
            soundIds = listOf("cabin", "brown"),
        ),
        ScenePreset(
            id = "fire-rain",
            titleRes = R.string.scene_fire_rain_title,
            descriptionRes = R.string.scene_fire_rain_description,
            soundIds = listOf("fire", "rain", "storm"),
        ),
        ScenePreset(
            id = "storm-sea",
            titleRes = R.string.scene_storm_sea_title,
            descriptionRes = R.string.scene_storm_sea_description,
            soundIds = listOf("ocean", "storm"),
        ),
        ScenePreset(
            id = "rain-car",
            titleRes = R.string.scene_rain_car_title,
            descriptionRes = R.string.scene_rain_car_description,
            soundIds = listOf("car"),
        ),
        ScenePreset(
            id = "blizzard-night",
            titleRes = R.string.scene_blizzard_night_title,
            descriptionRes = R.string.scene_blizzard_night_description,
            soundIds = listOf("blizzard", "brown"),
        ),
        ScenePreset(
            id = "road-trip",
            titleRes = R.string.scene_road_trip_title,
            descriptionRes = R.string.scene_road_trip_description,
            soundIds = listOf("driving"),
        ),
        ScenePreset(
            id = "data-center",
            titleRes = R.string.scene_data_center_title,
            descriptionRes = R.string.scene_data_center_description,
            soundIds = listOf("datacenter"),
        ),
    )
}
