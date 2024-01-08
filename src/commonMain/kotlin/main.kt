import korlibs.audio.sound.*
import korlibs.time.*
import korlibs.korge.*
import korlibs.korge.scene.*
import korlibs.korge.view.*
import korlibs.image.color.*
import korlibs.image.font.*
import korlibs.image.format.*
import korlibs.image.text.*
import korlibs.korge.animate.*
import korlibs.korge.input.*
import korlibs.korge.view.align.*
import korlibs.math.interpolation.*

suspend fun main() = Korge() {
    val sceneContainer = sceneContainer()
    sceneContainer.changeTo { MainScene() }
}

class MainScene : Scene() {
    private lateinit var settings: Settings
    private lateinit var background: Image
    private lateinit var textTitles: Array<Text>
    override suspend fun SContainer.sceneInit() {
        settings = Settings(
            mapOf(
                "Primary" to KR.fonts.primary.__file.readFont(),
                "Secondary" to KR.fonts.secondary.__file.readFont()
            ),
            mapOf(
                "MainSound" to KR.audio.mainSound.__file.readMusic()
            ),
            mapOf(
                "Select" to KR.audio.select.__file.readMusic()
            )
        )
        settings.music.values.forEach { music -> music.volume = settings.musicVolume }
        settings.sound.values.forEach { sound -> sound.volume = settings.soundVolume }

        background = image(KR.images.mainScreen.__file.readBitmap()) {
            scale(.7, .8)
            alpha = 0.0
        }.centerOn(this)

        textTitles = arrayOf(
            text("Castle of the\nviolet rose").also {
                it.textSize = 80.0
                it.color = RGBA(127, 0, 255)
                it.font = settings.fonts["Primary"]!!
                it.alignment = TextAlignment.CENTER
                it.alpha = 0.0
                it.centerOn(this)
            },
            text("Tap to continue").also {
                it.textSize = 30.0
                it.color = RGBA(57, 0, 185)
                it.font = settings.fonts["Secondary"]!!
                it.alignment = TextAlignment.CENTER
                it.alpha = 0.0
                it.centerXOn(this)
            }
        )
        textTitles[1].y = textTitles[0].run { y + height }
    }
    override suspend fun SContainer.sceneMain() {
        settings.music["MainSound"]!!.play(PlaybackTimes.INFINITE)

        background.addTo(this)

        textTitles.forEach { text -> text.addTo(this) }

        animator {
            parallel { alpha(background, 1.0, TimeSpan.fromSeconds(2), Easing.EASE_IN) }
            parallel { alpha(textTitles[0], 1.0, TimeSpan.fromSeconds(2), Easing.EASE_IN) }
        }.awaitComplete()

        var userTouch = true
        while(userTouch) {
            val anim = blinking(textTitles[1], TimeSpan.fromSeconds(1)).awaitComplete()
            onClick {
                userTouch = false
                settings.sound["Select"]!!.play()
            }
        }

        animator {
            parallel { alpha(textTitles[1], 0.0, TimeSpan.fromSeconds(1), Easing.EASE_IN) }
            parallel { alpha(textTitles[0], 0.0, TimeSpan.fromSeconds(2), Easing.EASE_IN) }
        }.awaitComplete()
        textTitles.forEach { text -> text.removeFromParent() }
    }
    private fun SContainer.blinking(text: Text, time: TimeSpan): Animator {
        val blink = animator()
        if (text.alpha == 1.0)
            blink.sequence { alpha(text, 0.5, time, Easing.EASE_IN) }
        else
            blink.sequence { alpha(text, 1, time, Easing.EASE_IN) }
        return blink
    }
}
