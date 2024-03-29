package model

import model.heroes.Hero
import korlibs.audio.sound.*
import korlibs.image.color.*
import korlibs.image.font.*

data class Settings(
    // User model.Settings
    var musicVolume: Double,
    var soundVolume: Double,
    var turboMode: Boolean,
    val selectedHeroes: MutableList<Boolean>,
    var violetRose: Int,

    // Program settings
    val heroes: Map<String, Hero>,
    val textFont: Font,
    val music: Map<String, Sound>,
    val sound: Map<String, Sound>,
    var currentLevel: Int,
    val textSizes: Map<String, Double> = mapOf(
        "Header" to 80.0,
        "CommonText" to 40.0,
        "SmallText" to 30.0
    ),
    val colors: Map<String, RGBA> = mapOf(
        "Violet" to RGBA(127, 0, 255),
        "DarkViolet" to RGBA(57, 0, 185),
        "DarkGrey" to RGBA(70, 70, 70),
        "Black" to RGBA(0),
        "Cian" to RGBA(0, 150, 150)
    ),
)
