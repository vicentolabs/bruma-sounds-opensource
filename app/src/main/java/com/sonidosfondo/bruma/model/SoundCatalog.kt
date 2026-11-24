package com.sonidosfondo.bruma.model

enum class SoundCategory {
    NATURE,
    NOISE,
    AMBIENCE,
}

data class Soundscape(
    val id: String,
    val title: String,
    val description: String,
    val category: SoundCategory,
    val defaultVolume: Float = 0.68f,
)

object SoundCatalog {
    val sounds = listOf(
        Soundscape("rain", "Lluvia suave", "Gotas de lluvia sobre un charco", SoundCategory.NATURE),
        Soundscape("storm", "Tormenta", "Lluvia intensa y truenos lejanos", SoundCategory.NATURE),
        Soundscape("fire", "Chimenea", "Crepitar cálido de un fuego lento", SoundCategory.NATURE),
        Soundscape("wood-fire", "Fuego de leña", "Llamas y crepitar de una hoguera", SoundCategory.NATURE),
        Soundscape("river", "Río", "Corriente de agua entre piedras", SoundCategory.NATURE),
        Soundscape("ocean", "Océano", "Oleaje real y espuma en la costa", SoundCategory.NATURE),
        Soundscape("wind", "Viento", "Ráfagas entre los árboles", SoundCategory.NATURE),
        Soundscape("blizzard", "Ventisca", "Viento invernal y nieve azotando", SoundCategory.NATURE),
        Soundscape("fan", "Ventilador", "Zumbido constante y suave", SoundCategory.AMBIENCE),
        Soundscape("heater", "Ventilador calefactor", "Ventilación real de una bomba de calor", SoundCategory.AMBIENCE),
        Soundscape("ac", "Aire acondicionado", "Unidad portátil de climatización", SoundCategory.AMBIENCE),
        Soundscape("datacenter", "Centro de datos", "Servidores, discos y ventiladores", SoundCategory.AMBIENCE),
        Soundscape("extractor", "Extractor de cocina", "Campana extractora en funcionamiento", SoundCategory.AMBIENCE),
        Soundscape("extractor-steady", "Extractor fijo", "Campana a velocidad constante", SoundCategory.AMBIENCE),
        Soundscape("cabin", "Cabina de avión", "Rumor grave de un vuelo nocturno", SoundCategory.AMBIENCE),
        Soundscape("driving", "Conduciendo", "Motor y carretera desde el habitáculo", SoundCategory.AMBIENCE),
        Soundscape("car", "Lluvia en el coche", "Lluvia grabada desde el interior", SoundCategory.AMBIENCE),
        Soundscape("brown", "Ruido marrón", "Grave, profundo y uniforme", SoundCategory.NOISE),
        Soundscape("pink", "Ruido rosa", "Equilibrado para descansar o concentrarte", SoundCategory.NOISE),
        Soundscape("white", "Ruido blanco", "Hiss uniforme de banda ancha", SoundCategory.NOISE),
        Soundscape("grey", "Ruido gris", "Textura suave de balance tonal", SoundCategory.NOISE),
    )

    val byId = sounds.associateBy(Soundscape::id)
}

data class ScenePreset(
    val id: String,
    val title: String,
    val description: String,
    val soundIds: List<String>,
)

object SceneCatalog {
    val scenes = listOf(
        ScenePreset(
            id = "flight",
            title = "Noche en vuelo",
            description = "Cabina de avión + ruido marrón",
            soundIds = listOf("cabin", "brown"),
        ),
        ScenePreset(
            id = "fire-rain",
            title = "Lumbre bajo la lluvia",
            description = "Chimenea, lluvia y truenos",
            soundIds = listOf("fire", "rain", "storm"),
        ),
        ScenePreset(
            id = "storm-sea",
            title = "Tormenta en el mar",
            description = "Oleaje profundo + tormenta lejana",
            soundIds = listOf("ocean", "storm"),
        ),
        ScenePreset(
            id = "rain-car",
            title = "Lluvia en el coche",
            description = "Tormenta grabada desde el interior del coche",
            soundIds = listOf("car"),
        ),
        ScenePreset(
            id = "blizzard-night",
            title = "Noche de ventisca",
            description = "Viento helado + ruido marrón",
            soundIds = listOf("blizzard", "brown"),
        ),
        ScenePreset(
            id = "road-trip",
            title = "Trayecto en coche",
            description = "Motor real y carretera a 80 km/h",
            soundIds = listOf("driving"),
        ),
        ScenePreset(
            id = "data-center",
            title = "Centro de datos",
            description = "Discos y ventiladores de servidor",
            soundIds = listOf("datacenter"),
        ),
    )
}
