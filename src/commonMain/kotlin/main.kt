import korlibs.audio.sound.*
import korlibs.time.*
import korlibs.korge.*
import korlibs.korge.scene.*
import korlibs.korge.view.*
import korlibs.image.font.*
import korlibs.image.format.*
import korlibs.image.text.*
import korlibs.korge.animate.*
import korlibs.korge.input.*
import korlibs.korge.style.*
import korlibs.korge.ui.*
import korlibs.korge.view.align.*
import korlibs.math.geom.*
import korlibs.math.interpolation.*

suspend fun main() = Korge {
    val sceneContainer = sceneContainer()
    sceneContainer.changeTo { MainScene() }
}

class MainScene : Scene() {
    private lateinit var soundChannel: SoundChannel
    private lateinit var settings: Settings
    private lateinit var background: Image
    private lateinit var textTitles: Array<Text>
    private lateinit var buttons: Array<RoundRect>
    private lateinit var textButtons: Array<Text>
    private lateinit var textScenes: Array<Text>
    private lateinit var textSettings: Array<Text>
    private lateinit var returnButton: RoundRect

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
                it.color = settings.colors["MainText"]!!
                it.font = settings.fonts["Primary"]!!
                it.alignment = TextAlignment.CENTER
                it.alpha = 0.0
                it.centerOn(this)
            },
            text("Tap to continue").also {
                it.textSize = 30.0
                it.color = settings.colors["CommonText"]!!
                it.font = settings.fonts["Secondary"]!!
                it.alignment = TextAlignment.CENTER
                it.alpha = 0.0
                it.centerXOn(this)
            }
        )
        textTitles[1].y = textTitles[0].run { y + height }

        buttons = Array(5) { index: Int ->
            RoundRect(
                Size(.3 * this.width, .1 * this.height),
                RectCorners(30,15,15, 30)
            ).also {
                it.y = (.18 * (index - 2) + .45) * this.height
                it.color = settings.colors["Button"]!!
                it.alpha = .0
            }.centerXOn(this)
        }

        val text = arrayOf("New game", "Continue", "Violet Rose", "Select hero", "Settings")
        textButtons = Array(text.size) { index ->
            text(text[index]).also {
                it.textSize = 40.0
                it.color = if (settings.violetRose != 3 && index == 2)
                    settings.colors["Unavailable"]!!
                else
                    settings.colors["CommonText"]!!
                it.font = settings.fonts["Primary"]!!
                it.alpha = .0
            }
        }

        textScenes = Array(text.size) { index ->
            text(text[index]).also {
                it.textSize = 80.0
                it.color = settings.colors["MainText"]!!
                it.font = settings.fonts["Primary"]!!
                it.alpha = .0
            }.centerXOn(this)
        }

        val settingsText = arrayOf("Music", "Sound", "Back")
        textSettings = Array(settingsText.size) { index ->
            text(settingsText[index]).also {
                it.textSize = 60.0
                it.color = settings.colors["CommonText"]!!
                it.font = settings.fonts["Primary"]!!
                it.alpha = .0
            }
        }

        returnButton = RoundRect(
            Size(.2 * this.width, textSettings.last().height),
            RectCorners(30,15,15, 30)
        ).also {
            it.y = this.height - it.height
            it.color = settings.colors["Button"]!!
            it.alpha = .0
        }.centerXOn(this)
    }

    override suspend fun SContainer.sceneMain() {
        soundChannel = settings.music["MainSound"]!!.play(PlaybackTimes.INFINITE)

        val button = SolidRect(Size(width, height), settings.colors["Button"]!!)
        visibleScene(this, button, background, *textTitles)
        visibleViews(1.0, TimeSpan.fromSeconds(2), Easing.EASE_IN, background).awaitComplete()
        visibleViews(1.0, TimeSpan.fromSeconds(2), Easing.EASE_IN, textTitles[0]).awaitComplete()

        var userTouch = false
        while(!userTouch) {
            blinking(textTitles[1], TimeSpan.fromSeconds(1)).awaitComplete()
            button.onClick {
                userTouch = true
                settings.sound["Select"]!!.play()
            }
        }
        visibleViews(.0, TimeSpan.fromSeconds(1), Easing.EASE_IN, *textTitles).awaitComplete()
        cleanScene(button, *textTitles)
        sceneMainButtons()
    }

    private suspend fun SContainer.sceneMainButtons() {
        visibleScene(this, *buttons, *textButtons)
        textButtons.forEachIndexed { index, textButton -> textButton.centerOn(buttons[index]) }
        visibleViews(.5, TimeSpan.fromSeconds(1), Easing.EASE_IN, *buttons)
        visibleViews(1.0, TimeSpan.fromSeconds(1), Easing.EASE_IN, *textButtons)

        buttons[0].onClick {
            visibleViews(.0, TimeSpan.fromSeconds(1), Easing.EASE_IN, *textButtons, *buttons).awaitComplete()
            cleanScene(*buttons, *textButtons)
        }
        buttons[1].onClick {
            visibleViews(.0, TimeSpan.fromSeconds(1), Easing.EASE_IN, *textButtons, *buttons).awaitComplete()
            cleanScene(*buttons, *textButtons)
        }
        buttons[2].onClick {
            if (settings.violetRose == 3) {
                visibleViews(.0, TimeSpan.fromSeconds(1), Easing.EASE_IN, *textButtons, *buttons).awaitComplete()
                cleanScene(*buttons, *textButtons)
            }
        }
        buttons[3].onClick {
            visibleViews(.0, TimeSpan.fromSeconds(1), Easing.EASE_IN, *textButtons, *buttons).awaitComplete()
            cleanScene(*buttons, *textButtons)
        }
        buttons[4].onClick {
            visibleViews(.0, TimeSpan.fromSeconds(1), Easing.EASE_IN, *textButtons, *buttons).awaitComplete()
            cleanScene(*buttons, *textButtons)
            sceneSettings()
        }
    }

    private suspend fun SContainer.sceneSettings() {
        val sliders = arrayOf(
            uiSlider(settings.musicVolume, .0, 1, .1, size=Size(.3 * this.width, 50)) {
                showTooltip = false
                alpha = .0
                styles { uiSelectedColor = settings.colors["Slider"]!! }
                changed { volume ->
                    settings.musicVolume = volume
                    soundChannel.volume = volume
                }
            },
            uiSlider(settings.soundVolume, .0, 1, .1, size=Size(.3 * this.width, 50)) {
                showTooltip = false
                alpha = .0
                styles { uiSelectedColor = settings.colors["Slider"]!! }
                changed { volume ->
                    settings.soundVolume = volume
                    settings.sound["Select"]!!.volume = volume
                }
            }
        )

        val stack = uiVerticalStack(padding=20.0) {
            for (i in 0 until textSettings.size - 1) {
                uiHorizontalStack(padding=10.0) {
                    visibleScene(this, textSettings[i], sliders[i])
                    sliders[i].centerYOn(this)
                }
            }
        }.centerOn(this)

        visibleScene(this, textScenes[4], returnButton, textSettings.last())
        textSettings.last().centerOn(returnButton)
        visibleViews(.7, TimeSpan.fromSeconds(1), Easing.EASE_IN, returnButton)
        visibleViews(1.0, TimeSpan.fromSeconds(1), Easing.EASE_IN, textScenes[4], *textSettings, *sliders).awaitComplete()

        returnButton.onClick {
            visibleViews(.0, TimeSpan.fromSeconds(1), Easing.EASE_IN, textScenes[4], returnButton, *textSettings, *sliders).awaitComplete()
            cleanScene(stack, textScenes[4], returnButton, *textSettings)
            sceneMainButtons()
        }
    }

    private fun SContainer.blinking(text: Text, time: TimeSpan): Animator {
        val blink = animator()
        if (text.alpha == 1.0)
            blink.sequence { parallel { alpha(text, .5, time, Easing.EASE_IN) } }
        else
            blink.sequence { parallel { alpha(text, 1, time, Easing.EASE_IN) } }
        return blink
    }

    private fun visibleScene(parent: Container, vararg views: View) { views.forEach { view -> view.addTo(parent) } }

    private fun SContainer.visibleViews(alpha: Double, time: TimeSpan, easing: Easing, vararg views: View): Animator {
        return animator { parallel { views.forEach { view -> alpha(view, alpha, time, easing) } } }
    }

    private fun cleanScene(vararg views: View) { views.forEach { view -> view.removeFromParent() } }
}
