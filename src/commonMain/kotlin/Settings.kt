import korlibs.audio.sound.*
import korlibs.image.color.*
import korlibs.image.font.*
import korlibs.time.*

data class Settings(
    // Program settings
    val fonts: Map<String, Font>,
    val music: Map<String, Sound>,
    val sound: Map<String, Sound>,
    val currentLevel: Int = 0,
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
    val heroes: Map<String, Hero>,
    val spriteTime : Array<TimeSpan> = arrayOf(.15.seconds, .15.seconds, .1.seconds, .15.seconds, .1.seconds),

    // User Settings
    var musicVolume: Double = 1.0,
    var soundVolume: Double = 1.0,
    var turboMode: Boolean,
    val selectedHeroes: Array<Boolean> = arrayOf(true, false, false, false),
    var violetRose: Int
)
