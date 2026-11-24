package com.sonidosfondo.bruma.audio

object AudioAssets {
    private val files = mapOf(
        "rain" to "rain.ogg",
        "storm" to "storm.ogg",
        "fire" to "fire.ogg",
        "wood-fire" to "wood-fire.ogg",
        "river" to "river.ogg",
        "ocean" to "ocean.ogg",
        "wind" to "wind.ogg",
        "fan" to "fan.ogg",
        "heater" to "heater.ogg",
        "ac" to "ac.ogg",
        "datacenter" to "datacenter.ogg",
        "extractor" to "extractor.ogg",
        "extractor-steady" to "extractor-steady.ogg",
        "cabin" to "cabin.mp3",
        "driving" to "driving.ogg",
        "brown" to "brown.mp3",
        "pink" to "pink.ogg",
        "white" to "white.ogg",
        "grey" to "grey.mp3",
        "car" to "car.ogg",
        "blizzard" to "blizzard.mp3",
    )

    fun fileFor(soundId: String): String? = files[soundId]
}
