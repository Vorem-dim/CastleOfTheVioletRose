import korlibs.audio.sound.*
import korlibs.image.atlas.*
import korlibs.image.color.*
import korlibs.time.*
import korlibs.korge.*
import korlibs.korge.scene.*
import korlibs.korge.view.*
import korlibs.image.font.*
import korlibs.image.format.*
import korlibs.image.text.*
import korlibs.io.file.std.*
import korlibs.korge.animate.*
import korlibs.korge.box2d.*
import korlibs.korge.input.*
import korlibs.korge.style.*
import korlibs.korge.ui.*
import korlibs.korge.view.Circle
import korlibs.korge.view.align.*
import korlibs.korge.view.collision.*
import korlibs.math.geom.*
import korlibs.math.interpolation.*
import org.jbox2d.dynamics.*


suspend fun main() = Korge {
    val sceneContainer = sceneContainer()
    sceneContainer.changeTo { MainScene() }
}

class MainScene : Scene() {
    private lateinit var soundChannel: SoundChannel
    private lateinit var settings: Settings
    private lateinit var background: Image

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
    }

    override suspend fun SContainer.sceneMain() {
        val textTitles = arrayOf(
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
        val text = arrayOf("New game", "Continue", "Violet Rose", "Select hero", "Settings")
        val textSections = Array(text.size) { index ->
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
        val buttonAreas = Array(5) { index: Int ->
            RoundRect(
                Size(.3 * this.width, .1 * this.height),
                RectCorners(30,15,15, 30)
            ).also {
                it.y = (.18 * (index - 2) + .45) * this.height
                it.color = settings.colors["Button"]!!
                it.alpha = .0
            }.centerXOn(this)
        }

        visibleScene(this, *buttonAreas, *textSections)
        textSections.forEachIndexed { index, textSection -> textSection.centerOn(buttonAreas[index]) }
        visibleViews(.5, TimeSpan.fromSeconds(1), Easing.EASE_IN, *buttonAreas)
        visibleViews(1.0, TimeSpan.fromSeconds(1), Easing.EASE_IN, *textSections)

        buttonAreas[0].onClick {
            visibleViews(.0, TimeSpan.fromSeconds(1), Easing.EASE_IN, *textSections, *buttonAreas).awaitComplete()
            cleanScene(*buttonAreas, *textSections)
        }
        buttonAreas[1].onClick {
            visibleViews(.0, TimeSpan.fromSeconds(1), Easing.EASE_IN, *textSections, *buttonAreas).awaitComplete()
            cleanScene(*buttonAreas, *textSections)
        }
        buttonAreas[2].onClick {
            if (settings.violetRose == 3) {
                visibleViews(.0, TimeSpan.fromSeconds(1), Easing.EASE_IN, *textSections, *buttonAreas).awaitComplete()
                cleanScene(*buttonAreas, *textSections)
            }
        }
        buttonAreas[3].onClick {
            visibleViews(.0, TimeSpan.fromSeconds(1), Easing.EASE_IN, *textSections, *buttonAreas).awaitComplete()
            cleanScene(*buttonAreas, *textSections)
            sceneSelectHero(textSections[3])
        }
        buttonAreas[4].onClick {
            visibleViews(.0, TimeSpan.fromSeconds(1), Easing.EASE_IN, *textSections, *buttonAreas).awaitComplete()
            cleanScene(*buttonAreas, *textSections)
            sceneSettings(textSections[4])
        }
    }

    private suspend fun SContainer.sceneSelectHero(header: Text) {
        val text = arrayOf("Knight", "Anomaly", "Thief", "Violet Rose", "Back")
        val textSelect = Array(text.size) { index ->
            text(text[index]).also {
                it.textSize = 50.0
                it.color = settings.colors["CommonText"]!!
                it.font = settings.fonts["Primary"]!!
                it.alpha = .0
            }
        }
        header.also {
            it.y = 0.0
            it.textSize = 80.0
            it.color = settings.colors["MainText"]!!
        }.centerXOn(this)

        val heroAreas = Array(4) {
            Circle(90.0).also {
                it.color = settings.colors["Button"]!!
                it.alpha = .0
            }
        }

        val returnButton = RoundRect(
            Size(.2 * this.width, 60),
            RectCorners(30,15,15, 30)
        ).also {
            it.y = this.height - it.height
            it.color = settings.colors["Button"]!!
            it.alpha = .0
        }.centerXOn(this)

        val stack = uiVerticalStack(padding=20.0) {
            uiHorizontalStack(padding=40.0) {
                for (i in 0 until heroAreas.size - 1) {
                    uiVerticalStack {
                        visibleScene(this, heroAreas[i], textSelect[i])
                        heroAreas[i].centerXOn(textSelect[i])
                    }
                }
            }
            uiVerticalStack {
                visibleScene(this, heroAreas.last(), textSelect[3])
                textSelect[3].centerXOn(textSelect[1])
                heroAreas.last().centerXOn(textSelect[3])
            }
        }.centerOn(this)

        visibleScene(this, header, returnButton, textSelect.last())
        textSelect.last().centerOn(returnButton)
        visibleViews(.8, TimeSpan.fromSeconds(1), Easing.EASE_IN, returnButton, *heroAreas)
        visibleViews(1.0, TimeSpan.fromSeconds(1), Easing.EASE_IN, header, *textSelect).awaitComplete()

        returnButton.onClick {
            visibleViews(.0, TimeSpan.fromSeconds(1), Easing.EASE_IN, header, returnButton, *textSelect, *heroAreas).awaitComplete()
            cleanScene(header, returnButton, *textSelect, stack)
            sceneMainButtons()
        }
    }

    private suspend fun SContainer.sceneSettings(header: Text) {
        val text = arrayOf("Music", "Sound", "Back")
        val textSettings = Array(text.size) { index ->
            text(text[index]).also {
                it.textSize = 60.0
                it.color = settings.colors["CommonText"]!!
                it.font = settings.fonts["Primary"]!!
                it.alpha = .0
            }
        }
        header.also {
            it.y = 0.0
            it.textSize = 80.0
            it.color = settings.colors["MainText"]!!
        }.centerXOn(this)

        val returnButton = RoundRect(
            Size(.2 * this.width, 60),
            RectCorners(30,15,15, 30)
        ).also {
            it.y = this.height - it.height
            it.color = settings.colors["Button"]!!
            it.alpha = .0
        }.centerXOn(this)

        val params = arrayOf(
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
                    visibleScene(this, textSettings[i], params[i])
                    params[i].centerYOn(this)
                }
            }
        }.centerOn(this)

        visibleScene(this, header, returnButton, textSettings.last())
        textSettings.last().centerOn(returnButton)
        visibleViews(.8, TimeSpan.fromSeconds(1), Easing.EASE_IN, returnButton)
        visibleViews(1.0, TimeSpan.fromSeconds(1), Easing.EASE_IN, header, *textSettings, *params).awaitComplete()

        returnButton.onClick {
            visibleViews(.0, TimeSpan.fromSeconds(1), Easing.EASE_IN, header, returnButton, *textSettings, *params).awaitComplete()
            cleanScene(stack, header, returnButton, *textSettings)
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
