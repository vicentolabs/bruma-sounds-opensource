package com.sonidosfondo.bruma.model

data class AudioCredit(
    val sound: String,
    val recording: String,
    val creator: String,
    val sourceUrl: String,
    val license: String = "CC0 1.0",
)

object AudioCredits {
    val recordings = listOf(
        AudioCredit("Lluvia", "Rain on Puddle", "Joseph Sardin / BigSoundBank", "https://bigsoundbank.com/rain-on-puddle-s1290.html"),
        AudioCredit("Tormenta", "Rain and Thunder #2", "Joseph Sardin / BigSoundBank", "https://bigsoundbank.com/rain-and-storm-2-s0740.html"),
        AudioCredit("Chimenea", "Fireplace #1", "Joseph Sardin / BigSoundBank", "https://bigsoundbank.com/fireplace-1-s0030.html"),
        AudioCredit("Fuego de leña", "Big branching fire #2", "Joseph Sardin / BigSoundBank", "https://bigsoundbank.com/big-branching-fire-2-s0988.html"),
        AudioCredit("Río", "Small Stream", "Joseph Sardin / BigSoundBank", "https://bigsoundbank.com/small-stream-s0823.html"),
        AudioCredit("Océano", "Sea: Waves", "Joseph Sardin / BigSoundBank", "https://bigsoundbank.com/sea-waves-s0266.html"),
        AudioCredit("Viento", "Wind in the Trees", "Joseph Sardin / BigSoundBank", "https://bigsoundbank.com/forest-wind-in-the-trees-s0904.html"),
        AudioCredit("Ventisca", "Howling winter storm ambient sounds", "DBlover · Freesound #505999", "https://freesound.org/people/DBlover/sounds/505999/"),
        AudioCredit("Ventilador", "Computer 2 (Ventilation)", "Joseph Sardin / BigSoundBank", "https://bigsoundbank.com/computer-2-ventilation-s0126.html"),
        AudioCredit("Ventilador calefactor", "Air/water heat pump, front", "Joseph Sardin / BigSoundBank", "https://bigsoundbank.com/air-water-heat-pump-front-s1232.html"),
        AudioCredit("Aire acondicionado", "Air-conditioner", "Joseph Sardin / BigSoundBank", "https://bigsoundbank.com/air-conditioner-s1471.html"),
        AudioCredit("Centro de datos", "NAS, starting", "Joseph Sardin / BigSoundBank", "https://bigsoundbank.com/nas-starting-s1622.html"),
        AudioCredit("Extractor de cocina", "Kitchen extractor hood", "Denis Chardonnet / BigSoundBank", "https://bigsoundbank.com/kitchen-extractor-hood-s0309.html"),
        AudioCredit("Extractor fijo", "Electric hood #1 (speed 7/10)", "Joseph Sardin / BigSoundBank", "https://bigsoundbank.com/electric-hood-1-s0061.html"),
        AudioCredit("Cabina de avión", "Aircraft cabin", "csnmedia · Freesound #381174", "https://freesound.org/people/csnmedia/sounds/381174/"),
        AudioCredit("Conduciendo", "Car interior, 80 km/h", "Joseph Sardin & Axeline T. / BigSoundBank", "https://bigsoundbank.com/car-interior-80-km-h-s3098.html"),
        AudioCredit("Lluvia en el coche", "Thunder and Rain on Car Windshield", "Joseph Sardin / BigSoundBank", "https://bigsoundbank.com/storm-and-rain-on-car-windshield-s1296.html"),
        AudioCredit("Ruido marrón", "Softly Deep brownian noise", "Sadiquecat · Freesound #852712", "https://freesound.org/people/Sadiquecat/sounds/852712/"),
        AudioCredit("Ruido rosa", "Pink Noise", "Joseph Sardin / BigSoundBank", "https://bigsoundbank.com/pink-noise-s0432.html"),
        AudioCredit("Ruido blanco", "White Noise", "Joseph Sardin / BigSoundBank", "https://bigsoundbank.com/white-noise-s1037.html"),
        AudioCredit("Ruido gris", "gray noise.ogg", "qubodup · Freesound #186706", "https://freesound.org/people/qubodup/sounds/186706/"),
    )
}
