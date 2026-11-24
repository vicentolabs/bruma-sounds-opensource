package com.sonidosfondo.bruma.model

data class AudioCredit(
    val soundId: String,
    val recording: String,
    val creator: String,
    val sourceUrl: String,
    val license: String = "CC0 1.0",
)

object AudioCredits {
    val recordings = listOf(
        AudioCredit("rain", "Rain on Puddle", "Joseph Sardin / BigSoundBank", "https://bigsoundbank.com/rain-on-puddle-s1290.html"),
        AudioCredit("storm", "Rain and Thunder #2", "Joseph Sardin / BigSoundBank", "https://bigsoundbank.com/rain-and-storm-2-s0740.html"),
        AudioCredit("fire", "Fireplace #1", "Joseph Sardin / BigSoundBank", "https://bigsoundbank.com/fireplace-1-s0030.html"),
        AudioCredit("wood-fire", "Big branching fire #2", "Joseph Sardin / BigSoundBank", "https://bigsoundbank.com/big-branching-fire-2-s0988.html"),
        AudioCredit("river", "Small Stream", "Joseph Sardin / BigSoundBank", "https://bigsoundbank.com/small-stream-s0823.html"),
        AudioCredit("ocean", "Sea: Waves", "Joseph Sardin / BigSoundBank", "https://bigsoundbank.com/sea-waves-s0266.html"),
        AudioCredit("wind", "Wind in the Trees", "Joseph Sardin / BigSoundBank", "https://bigsoundbank.com/forest-wind-in-the-trees-s0904.html"),
        AudioCredit("blizzard", "Howling winter storm ambient sounds", "DBlover · Freesound #505999", "https://freesound.org/people/DBlover/sounds/505999/"),
        AudioCredit("fan", "Computer 2 (Ventilation)", "Joseph Sardin / BigSoundBank", "https://bigsoundbank.com/computer-2-ventilation-s0126.html"),
        AudioCredit("heater", "Air/water heat pump, front", "Joseph Sardin / BigSoundBank", "https://bigsoundbank.com/air-water-heat-pump-front-s1232.html"),
        AudioCredit("ac", "Air-conditioner", "Joseph Sardin / BigSoundBank", "https://bigsoundbank.com/air-conditioner-s1471.html"),
        AudioCredit("datacenter", "NAS, starting", "Joseph Sardin / BigSoundBank", "https://bigsoundbank.com/nas-starting-s1622.html"),
        AudioCredit("extractor", "Kitchen extractor hood", "Denis Chardonnet / BigSoundBank", "https://bigsoundbank.com/kitchen-extractor-hood-s0309.html"),
        AudioCredit("extractor-steady", "Electric hood #1 (speed 7/10)", "Joseph Sardin / BigSoundBank", "https://bigsoundbank.com/electric-hood-1-s0061.html"),
        AudioCredit("cabin", "Aircraft cabin", "csnmedia · Freesound #381174", "https://freesound.org/people/csnmedia/sounds/381174/"),
        AudioCredit("driving", "Car interior, 80 km/h", "Joseph Sardin & Axeline T. / BigSoundBank", "https://bigsoundbank.com/car-interior-80-km-h-s3098.html"),
        AudioCredit("car", "Thunder and Rain on Car Windshield", "Joseph Sardin / BigSoundBank", "https://bigsoundbank.com/storm-and-rain-on-car-windshield-s1296.html"),
        AudioCredit("brown", "Softly Deep brownian noise", "Sadiquecat · Freesound #852712", "https://freesound.org/people/Sadiquecat/sounds/852712/"),
        AudioCredit("pink", "Pink Noise", "Joseph Sardin / BigSoundBank", "https://bigsoundbank.com/pink-noise-s0432.html"),
        AudioCredit("white", "White Noise", "Joseph Sardin / BigSoundBank", "https://bigsoundbank.com/white-noise-s1037.html"),
        AudioCredit("grey", "gray noise.ogg", "qubodup · Freesound #186706", "https://freesound.org/people/qubodup/sounds/186706/"),
    )
}
