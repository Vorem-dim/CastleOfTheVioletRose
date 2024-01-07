import korlibs.audio.sound.*
import korlibs.image.bitmap.*
import korlibs.time.*
import korlibs.korge.*
import korlibs.korge.scene.*
import korlibs.korge.view.*
import korlibs.image.color.*
import korlibs.image.font.*
import korlibs.image.format.*
import korlibs.image.text.*
import korlibs.io.file.std.*
import korlibs.korge.animate.*
import korlibs.korge.view.align.*
import korlibs.math.interpolation.*

suspend fun main() = Korge() {
    val sceneContainer = sceneContainer()
    sceneContainer.changeTo { MainScene() }
}

class MainScene : Scene() {
    private lateinit var mainBackground: Bitmap
    private lateinit var fonts: Array<Font>
    private lateinit var mainSound: Sound
    override suspend fun SContainer.sceneInit() {
        mainBackground = resourcesVfs["images/main_screen.jpg"].readBitmap()
        fonts = arrayOf(
            resourcesVfs["fonts/primary.ttf"].readFont(),
            resourcesVfs["fonts/secondary.ttf"].readFont()
        )
        mainSound = resourcesVfs["audio/main_sound.mp3"].readMusic()
    }
    override suspend fun SContainer.sceneMain() {
        mainSound.play(PlaybackTimes.INFINITE)

        val image = image(mainBackground) {
            scale(.7, .8)
            alpha = 0.0
        }.centerOn(this)

        val text = text("Castle of the\nviolet rose").also {
            it.textSize = 80.0
            it.color = RGBA(127, 0, 255)
            it.font = fonts[0]
            it.alignment = TextAlignment.CENTER
            it.alpha = 0.0
            it.centerOn(this)
        }

        animator {
            parallel { alpha(image, 1.0, TimeSpan.fromSeconds(2), Easing.EASE_IN) }
            parallel { alpha(text, 1.0, TimeSpan.fromSeconds(5), Easing.EASE_IN) }
        }
    }
}
