import korlibs.audio.sound.*
import korlibs.image.atlas.*
import korlibs.time.*
import korlibs.korge.*
import korlibs.korge.scene.*
import korlibs.korge.view.*
import korlibs.image.font.*
import korlibs.image.format.*
import korlibs.image.text.*
import korlibs.io.file.std.*
import korlibs.korge.animate.*
import korlibs.korge.input.*
import korlibs.korge.style.*
import korlibs.korge.ui.*
import korlibs.korge.view.Circle
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

    override suspend fun SContainer.sceneInit() {
        // Set main parameters
        settings = Settings(
            mapOf(
                "Primary" to KR.fonts.primary.__file.readFont(),
                "Secondary" to KR.fonts.secondary.__file.readFont()
            ),
            mapOf(
                "MainSound" to KR.audio.mainSound.__file.readSound()
            ),
            mapOf(
                "Select" to KR.audio.select.__file.readSound()
            ),
            heroes = mapOf(
                "Knight" to Hero(resourcesVfs["sprites/heroes/knight.xml"].readAtlas(), this),
                "Anomaly" to Hero(resourcesVfs["sprites/heroes/anomaly.xml"].readAtlas(), this),
                "Thief" to Hero(resourcesVfs["sprites/heroes/thief.xml"].readAtlas(), this),
                "Violet Rose" to Hero(resourcesVfs["sprites/heroes/knight.xml"].readAtlas(), this) // coming soon
            )
        )

        // Set music and sound volume
        settings.music.values.forEach { music -> music.volume = settings.musicVolume }
        settings.sound.values.forEach { sound -> sound.volume = settings.soundVolume }

        // Set background of menu
        background = image(KR.images.mainScreen.__file.readBitmap()) {
            scale(.7, .8)
            alpha = .0
        }.centerOn(this)
    }

    override suspend fun SContainer.sceneMain() {
        soundChannel = settings.music["MainSound"]!!.play(PlaybackTimes.INFINITE) // Turn on music

        // Making TextView for section
        val textTitles = arrayOf(
            text("Castle of the\nviolet rose").also {
                settings.apply {
                    it.textSize = textSizes["Header"]!!
                    it.color = colors["MainText"]!!
                    it.font = fonts["Primary"]!!
                }
                it.alignment = TextAlignment.CENTER
                it.alpha = .0
                it.centerOn(this)
            },
            text("Tap to continue").also {
                settings.apply {
                    it.textSize = textSizes["SmallText"]!!
                    it.color = colors["CommonText"]!!
                    it.font = fonts["Secondary"]!!
                }
                it.alignment = TextAlignment.CENTER
                it.alpha = .0
                it.centerXOn(this)
            }
        )
        textTitles[1].y = textTitles[0].run { y + height }

        val button = SolidRect(Size(width, height), settings.colors["Button"]!!) // For screen click

        visibleScene(this, button, background, *textTitles) // Add views on the scene

        // Display views
        visibleViews(1.0, Easing.EASE_IN, background).awaitComplete()
        visibleViews(1.0, Easing.EASE_IN, textTitles[0]).awaitComplete()

        // Wait user touch
        var userTouch = false
        while(!userTouch) {
            blinking(textTitles[1], 1.seconds).awaitComplete() // Blinking animation of text
            button.onClick {
                userTouch = true
                settings.sound["Select"]!!.play() // Turn on sound
            }
        }

        visibleViews(.0, Easing.EASE_IN, *textTitles).awaitComplete() // Vanish views
        cleanScene(button, *textTitles) // Remove views from scene
        sceneMainButtons() // To main scene
    }

    private suspend fun SContainer.sceneMainButtons() {
        // Making TextView for section
        val text = arrayOf("New game", "Continue", "Violet Rose", "Select hero", "Settings")
        val textSections = Array(text.size) { index ->
            text(text[index]).apply {
                settings.apply {
                    textSize = textSizes["CommonText"]!!
                    color = if (violetRose != 3 && index == 2)
                        colors["Unavailable"]!!
                    else
                        colors["CommonText"]!!
                    font = fonts["Primary"]!!
                }
                alpha = .0
            }
        }

        // Making buttons for section and their location
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

        visibleScene(this, *buttonAreas, *textSections) // Add views on the scene
        textSections.forEachIndexed { index, textSection -> textSection.centerOn(buttonAreas[index]) } // Add text to buttons

        // Display views
        visibleViews(.5, Easing.EASE_IN, *buttonAreas)
        visibleViews(1.0, Easing.EASE_IN, *textSections)

        buttonAreas[0].onClick {
            visibleViews(.0, Easing.EASE_IN, *textSections, *buttonAreas).awaitComplete() // Vanish views
            cleanScene(*buttonAreas, *textSections) // Remove views from scene
        }
        buttonAreas[1].onClick {
            visibleViews(.0, Easing.EASE_IN, *textSections, *buttonAreas).awaitComplete() // Vanish views
            cleanScene(*buttonAreas, *textSections) // Remove views from scene
        }
        buttonAreas[2].onClick {
            if (settings.violetRose == 3) {
                visibleViews(.0, Easing.EASE_IN, *textSections, *buttonAreas).awaitComplete() // Vanish views
                cleanScene(*buttonAreas, *textSections) // Remove views from scene
            }
        }
        buttonAreas[3].onClick {
            visibleViews(.0, Easing.EASE_IN, *textSections, *buttonAreas).awaitComplete() // Vanish views
            cleanScene(*buttonAreas, *textSections) // Remove views from scene
            sceneSelectHero(textSections[3]) // To scene of select heroes
        }
        buttonAreas[4].onClick {
            visibleViews(.0, Easing.EASE_IN, *textSections, *buttonAreas).awaitComplete() // Vanish views
            cleanScene(*buttonAreas, *textSections) // Remove views from scene
            sceneSettings(textSections[4]) // To settings scene
        }
    }

    private suspend fun SContainer.sceneSelectHero(header: Text) {
        // Making TextView for section
        val text = arrayOf("Knight", "Anomaly", "Thief", "Violet Rose")
        val textSelect = Array(text.size) { index ->
            text(text[index]).apply {
                settings.apply {
                    textSize = textSizes["CommonText"]!!
                    color = colors["CommonText"]!!
                    font = fonts["Primary"]!!
                }
                alpha = .0
            }
        }

        // Header of section
        header.apply {
            y = .0
            settings.apply {
                textSize = textSizes["Header"]!!
                color = colors["MainText"]!!
            }
        }.centerXOn(this)

        // Pedestals for heroes
        val heroAreas = Array(4) {
            Circle(90.0).apply {
                color = settings.colors["Button"]!!
                alpha = .0
            }
        }

        // Button to main scene
        val returnButton = RoundRect(
            Size(.2 * this.width, 60),
            RectCorners(30,15,15, 30)
        ).also {
            it.y = this.height - it.height
            it.color = settings.colors["Button"]!!
            it.alpha = .0
        }.centerXOn(this)

        // Making text to the button
        val buttonText = text("Back").apply {
            settings.apply {
                textSize = textSizes["CommonText"]!!
                color = colors["CommonText"]!!
                font = fonts["Primary"]!!
            }
            alpha = .0
        }

        // Location heroes pedestals and TextView
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

        // Making array of sprites and their location
        val heroSprites: Array<Sprite> = Array(text.size) { index: Int ->
            var parent: Container = this
            settings.run {
                if (index == this.heroes.size - 1 && violetRose != 3) parent = heroAreas[index]
                this.heroes[text[index]]!!.run {
                    if (selectedHeroes[index]) {
                        stand.run {
                            addTo(parent).centerOn(heroAreas[index])
                            this
                        }
                    } else
                        death.run {
                            addTo(parent).centerOn(heroAreas[index])
                            this
                        }
                }
            }
        }

        visibleScene(this, header, returnButton, buttonText) // Add views on the scene
        buttonText.centerOn(returnButton) // Add text to the button

        // Display views
        visibleViews(.8, Easing.EASE_IN, returnButton, *heroAreas)
        visibleViews(1.0, Easing.EASE_IN, header, buttonText, *textSelect, *heroSprites).awaitComplete()

        // Animation of heroes
        for (i in heroSprites.indices) {
            if (settings.selectedHeroes[i])
                heroSprites[i].playAnimationLooped(spriteDisplayTime = settings.spriteTime[0])
            else
                heroSprites[i].playAnimation(spriteDisplayTime = settings.spriteTime[4])
        }

        heroAreas.forEachIndexed { index: Int, heroArea: Circle ->
            heroArea.onClick {
                settings.also { st ->
                    if (!st.selectedHeroes[index])
                        if (index != 3 || st.violetRose == 3) {
                            var prevIndex = 0
                            for (i in st.selectedHeroes.indices)
                                if (st.selectedHeroes[i]) {
                                    prevIndex = i
                                    break
                                }
                            st.selectedHeroes[index] = true
                            st.selectedHeroes[prevIndex] = false

                            st.heroes[text[index]]!!.apply {
                                death.playAnimation(spriteDisplayTime = st.spriteTime[4], reversed = true)
                                delay(st.spriteTime[4] * (death.totalFrames - 1))
                                deathAnimation = true
                            }

                            heroSprites[index].alpha = .0
                            heroSprites[prevIndex].alpha = .0
                            cleanScene(heroSprites[index], heroSprites[prevIndex])

                            heroSprites[index] = st.heroes[text[index]]!!.stand
                            heroSprites[prevIndex] = st.heroes[text[prevIndex]]!!.death

                            heroSprites[index].also {
                                it.alpha = 1.0
                                it.addTo(this).centerOn(heroAreas[index])
                                it.playAnimationLooped(spriteDisplayTime = st.spriteTime[0])
                            }

                            heroSprites[prevIndex].also {
                                it.alpha = 1.0
                                it.addTo(this).centerOn(heroAreas[prevIndex])
                                it.playAnimation(spriteDisplayTime = st.spriteTime[0])
                            }
                        }
                }
            }
        }

        returnButton.onClick {
            visibleViews(.0, Easing.EASE_IN, header, returnButton, buttonText, *textSelect, *heroAreas, *heroSprites).awaitComplete() // Vanish views

            // Stop animation of heroes
            heroSprites.forEach { hero: Sprite -> hero.playAnimation() }
            delay(.5.seconds)

            cleanScene(header, returnButton, buttonText, stack, *heroSprites) // Remove views from scene
            sceneMainButtons() // To main scene
        }
    }

    private suspend fun SContainer.sceneSettings(header: Text) {
        // Making TextView for section
        val text = arrayOf("Music", "Sound", "Back")
        val textSettings = Array(text.size) { index ->
            text(text[index]).apply {
                settings.apply {
                    textSize = textSizes["CommonText"]!!
                    color = colors["CommonText"]!!
                    font = fonts["Primary"]!!
                }
                alpha = .0
            }
        }

        // Header of section
        header.apply {
            y = 0.0
            settings.apply {
                textSize = textSizes["Header"]!!
                color = colors["MainText"]!!
            }
        }.centerXOn(this)

        // Button to main scene
        val returnButton = RoundRect(
            Size(.2 * this.width, 60),
            RectCorners(30,15,15, 30)
        ).also {
            it.y = this.height - it.height
            it.color = settings.colors["Button"]!!
            it.alpha = .0
        }.centerXOn(this)

        // Params of settings section
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

        // Location of params
        val stack = uiVerticalStack(padding=20.0) {
            for (i in 0 until textSettings.size - 1) {
                uiHorizontalStack(padding=10.0) {
                    visibleScene(this, textSettings[i], params[i])
                    params[i].centerYOn(this)
                }
            }
        }.centerOn(this)

        visibleScene(this, header, returnButton, textSettings.last()) // Add views on the scene
        textSettings.last().centerOn(returnButton) // Add text to the button

        // Display views
        visibleViews(.8, Easing.EASE_IN, returnButton)
        visibleViews(1.0, Easing.EASE_IN, header, *textSettings, *params).awaitComplete()

        returnButton.onClick {
            visibleViews(.0, Easing.EASE_IN, header, returnButton, *textSettings, *params).awaitComplete() // Vanish views
            cleanScene(stack, header, returnButton, *textSettings) // Remove views from scene
            sceneMainButtons() // To main scene
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

    private fun SContainer.visibleViews(alpha: Double, easing: Easing, vararg views: View): Animator {
        return animator { parallel { views.forEach { view -> alpha(view, alpha, 1.2.seconds, easing) } } }
    }

    private fun visibleScene(parent: Container, vararg views: View) { views.forEach { view -> view.addTo(parent) } }

    private fun cleanScene(vararg views: View) { views.forEach { view -> view.removeFromParent() } }
}
