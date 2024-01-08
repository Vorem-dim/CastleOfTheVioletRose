import korlibs.audio.sound.*
import korlibs.image.font.*

data class Settings(
    // Program settings
    val fonts: Map<String, Font>,
    val music: Map<String, Sound>,
    val sound: Map<String, Sound>,

    // User Settings
    var musicVolume: Double = 1.0,
    var soundVolume: Double = 1.0
    //var hero: ???
)
