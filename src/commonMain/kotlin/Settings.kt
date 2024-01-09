import korlibs.audio.sound.*
import korlibs.image.color.*
import korlibs.image.font.*

data class Settings(
    // Program settings
    val fonts: Map<String, Font>,
    val music: Map<String, Sound>,
    val sound: Map<String, Sound>,
    val colors: Map<String, RGBA> = mapOf(
        "MainText" to RGBA(127, 0, 255),
        "CommonText" to RGBA(57, 0, 185),
        "Unavailable" to RGBA(70, 70, 70),
        "Button" to RGBA(0),
        "Slider" to RGBA(0, 150, 150)
    ),
    var violetRose: Int = 0,

    // User Settings
    var musicVolume: Double = 1.0,
    var soundVolume: Double = 1.0
    //var hero: ???
)
