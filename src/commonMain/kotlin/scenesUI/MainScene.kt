package scenesUI

import model.heroes.*
import model.Settings
import korlibs.audio.sound.*
import korlibs.event.*
import korlibs.image.atlas.*
import korlibs.image.font.*
import korlibs.image.format.*
import korlibs.image.text.*
import korlibs.io.async.launch
import korlibs.io.file.std.*
import korlibs.korge.animate.*
import korlibs.korge.input.*
import korlibs.korge.scene.*
import korlibs.korge.service.storage.*
import korlibs.korge.style.*
import korlibs.korge.ui.*
import korlibs.korge.view.*
import korlibs.korge.view.Circle
import korlibs.korge.view.align.*
import korlibs.math.geom.*
import korlibs.math.interpolation.*
import korlibs.time.*
import viewModel.*

class MainScene : Scene() {
    private lateinit var settings: SettingsViewModel
    private lateinit var background: Image

    override suspend fun SContainer.sceneInit() {
        views.storage.also { nativeStorage ->
            settings = SettingsViewModel(
                nativeStorage,
                Settings(
                    nativeStorage.getOrNull("MusicVolume")?.toDouble() ?: 1.0,
                    nativeStorage.getOrNull("SoundVolume")?.toDouble() ?: 1.0,
                    nativeStorage.getOrNull("getTurbo()")?.toBoolean() ?: false,
                    nativeStorage.getOrNull("HeroSelection")
                        ?.run { split(",").map { it.toBoolean() }.toMutableList() }
                        ?: mutableListOf(true, false, false, false),
                    nativeStorage.getOrNull("VioletRose")?.toInt() ?: 0,
                    mapOf(
                        "Knight" to Knight(
                            arrayOf(
                                resourcesVfs["sprites/heroes/knight.xml"].readAtlas(),
                                resourcesVfs["sprites/heroes/knight_reversed.xml"].readAtlas()
                            ), this@sceneInit
                        ),
                        "Anomaly" to Anomaly(
                            arrayOf(
                                resourcesVfs["sprites/heroes/anomaly.xml"].readAtlas(),
                                resourcesVfs["sprites/heroes/anomaly_reversed.xml"].readAtlas()
                            ), this@sceneInit
                        ),
                        "Thief" to Thief(
                            arrayOf(
                                resourcesVfs["sprites/heroes/thief.xml"].readAtlas(),
                                resourcesVfs["sprites/heroes/thief_reversed.xml"].readAtlas()
                            ), this@sceneInit
                        ),
                        "Violet Rose" to VioletRose(
                            arrayOf(
                                resourcesVfs["sprites/heroes/knight.xml"].readAtlas(),
                                resourcesVfs["sprites/heroes/knight_reversed.xml"].readAtlas()
                            ), this@sceneInit
                        )
                    ),
                    __KR.KRFonts.primary.__file.readFont(),
                    mapOf(
                        "MainSound" to __KR.KRAudio.mainSound.__file.readSound()
                    ),
                    mapOf(
                        "Select" to __KR.KRAudio.select.__file.readSound()
                    ),
                    nativeStorage.getOrNull("CurrentLevel")?.toInt() ?: 0
                )
            )
        }
    }

    override suspend fun SContainer.sceneMain() {
        settings.playSound("MainSound", PlaybackTimes.INFINITE)

        // Set background of menu
        background = image(__KR.KRImages.mainScreen.__file.readBitmap()) {
            alpha = .0
            scale(.7, .8)
            centerOn(this@sceneMain)
        }

        // Making TextView for section
        val textTitles = arrayOf(
            text("Castle of the\nviolet rose").also {
                settings.apply {
                    it.textSize = getTextSize("Header")
                    it.color = getColor("Violet")
                    it.font = getFont()
                }
                it.alignment = TextAlignment.CENTER
                it.alpha = .0
                it.centerOn(this)
            },
            text("Tap to continue").also {
                settings.apply {
                    it.textSize = getTextSize("SmallText")
                    it.color = getColor("DarkViolet")
                    it.font = getFont()
                }
                it.alignment = TextAlignment.CENTER
                it.alpha = .0
                it.centerXOn(this)
            }
        )
        textTitles[1].y = textTitles[0].run { y + height }

        visibleScene(this, background, *textTitles) // Add views on the scene

        // Display views
        visibleViews(1.0, Easing.EASE_IN, background).awaitComplete()
        visibleViews(1.0, Easing.EASE_IN, textTitles[0]).awaitComplete()

        // Wait user touch
        var userTouch = false
        while(!userTouch) {
            blinking(textTitles[1], 1.seconds).awaitComplete() // Blinking animation of text

            onEvent(MouseEvent.Type.DOWN) {
                userTouch = true
                launch { settings.playSound("Select") } // Turn on sound
            }
        }

        visibleViews(.0, Easing.EASE_IN, *textTitles).awaitComplete() // Vanish views\
        removeChildrenIf { _, child -> child != background }
        clearEvents()
        sceneMainButtons() // To main scene
    }

    private suspend fun SContainer.sceneMainButtons() {
        // Making buttons for section and their location
        val buttonAreas = Array(5) { index: Int ->
            RoundRect(Size(.3 * width, .1 * height), RectCorners(30,15,15, 30)).apply {
                y = (.18 * (index - 2) + .45) * this@sceneMainButtons.height
                color = settings.getColor("Black")
                alpha = .0
                addTo(this@sceneMainButtons)
                centerXOn(this@sceneMainButtons)
            }
        }

        // Making TextView for section
        val text = arrayOf("New game", "Continue", "Violet Rose", "Select hero", "Settings")
        val textSections = Array(5) { index ->
            text(text[index]).apply {
                settings.apply {
                    textSize = getTextSize("CommonText")
                    color = if (violetRose() != 3 && index == 2) getColor("DarkGrey") else getColor("DarkViolet")
                    font = getFont()
                    alpha = 0.0
                }
                addTo(this@sceneMainButtons)
                centerOn(buttonAreas[index])
            }
        }

        // Display views
        visibleViews(.5, Easing.EASE_IN, *buttonAreas)
        visibleViews(1.0, Easing.EASE_IN, *textSections)

        suspend fun cleanScene(gameStart: Boolean = false) {
            // Vanish views
            if (gameStart)
                visibleViews(.0, Easing.EASE_IN, background, *textSections, *buttonAreas).awaitComplete()
            else
                visibleViews(.0, Easing.EASE_IN, *textSections, *buttonAreas).awaitComplete()
            removeChildrenIf { _, child -> gameStart || child != background } // Remove views from scene
        }

        buttonAreas[0].onClick {
            cleanScene(true)
            settings.resetCurrentLevel()
            sceneContainer.changeTo { LevelInspector(settings) } // Start new game
        }
        buttonAreas[1].onClick {
            cleanScene(true)
            sceneContainer.changeTo { LevelInspector(settings) } // Continue game
        }
        buttonAreas[2].onClick {
            if (settings.violetRose() == 3) {
                cleanScene()
            }
        }
        buttonAreas[3].onClick {
            cleanScene()
            sceneSelectHero(textSections[3]) // To scene of select model.heroes
        }
        buttonAreas[4].onClick {
            cleanScene()
            sceneSettings(textSections[4]) // To settings scene
        }
    }

    private suspend fun SContainer.sceneSelectHero(header: Text) {
        // Making TextView for section
        val text = arrayOf("Knight", "Anomaly", "Thief", "Violet Rose")
        val textSelect = Array(text.size) { index ->
            text(text[index]).apply {
                settings.apply {
                    textSize = getTextSize("CommonText")
                    color = getColor("DarkViolet")
                    font = getFont()
                    alpha = .0
                }
            }
        }

        // Header of section
        header.apply {
            settings.apply {
                y = .0
                textSize = getTextSize("Header")
                color = getColor("Violet")
                centerXOn(this@sceneSelectHero)
                addTo(this@sceneSelectHero)
            }
        }

        // Pedestals for model.heroes
        val heroAreas = Array(4) {
            Circle(90.0).apply {
                color = settings.getColor("Black")
                alpha = .0
            }
        }

        // Button to main scene
        val returnButton = RoundRect(Size2D(.2 * width, 60), RectCorners(30,15,15, 30)).apply {
            y = this@sceneSelectHero.height - height
            color = settings.getColor("Black")
            alpha = .0
            addTo(this@sceneSelectHero)
            centerXOn(this@sceneSelectHero)
        }

        // Making text to the button
        val buttonText = text("Back").apply {
            settings.apply {
                textSize = getTextSize("CommonText")
                color = getColor("DarkViolet")
                font = getFont()
                alpha = .0
                addTo(this@sceneSelectHero)
                centerOn(returnButton)
            }
        }

        // Location model.heroes pedestals and TextView
        uiVerticalStack(padding=20.0) {
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
            centerOn(this@sceneSelectHero)
        }

        // Making array of sprites and their location
        val heroSprites: Array<Sprite> = Array(text.size) { index: Int ->
            var parent: Container = this
            settings.run {
                if (index == getHeroes().size - 1 && violetRose() != 3) parent = heroAreas[index]
                getHero(text[index]).run {
                    setHeroState(if (getSelectedHeroes()[index]) "Idle" else "Death")
                    if (getSelectedHeroes()[index]) {
                        getState().run {
                            addTo(parent).centerOn(heroAreas[index])
                            this
                        }
                    } else
                        getState().run {
                            addTo(parent).centerOn(heroAreas[index])
                            this
                        }
                }
            }
        }

        // Display views
        visibleViews(.8, Easing.EASE_IN, returnButton, *heroAreas)
        visibleViews(1.0, Easing.EASE_IN, header, buttonText, *textSelect, *heroSprites).awaitComplete()

        // Animation of model.heroes
        for (i in heroSprites.indices) {
            if (settings.getSelectedHeroes()[i])
                heroSprites[i].playAnimationLooped(spriteDisplayTime = Hero.TIME_IDLE)
            else
                heroSprites[i].playAnimation(spriteDisplayTime = Hero.TIME_DEATH)
        }

        heroAreas.forEachIndexed { index: Int, heroArea: Circle ->
            heroArea.onClick {
                settings.apply {
                    if (!getSelectedHeroes()[index])
                        if (index != 3 || violetRose() == 3) {
                            var prevIndex = 0
                            for (i in getSelectedHeroes().indices)
                                if (getSelectedHeroes()[i]) {
                                    prevIndex = i
                                    break
                                }
                            getSelectedHeroes()[index] = true
                            getSelectedHeroes()[prevIndex] = false

                            getHero(text[index]).apply {
                                getState().playAnimation(spriteDisplayTime = Hero.TIME_DEATH, reversed = true)
                                delay(Hero.TIME_DEATH * (getState().totalFrames - 3))
                                deathAnimation = true
                            }

                            heroSprites[index].alpha = .0
                            heroSprites[prevIndex].alpha = .0
                            heroSprites[index].removeFromParent()
                            heroSprites[prevIndex].removeFromParent()

                            heroSprites[index] = with(getHero(text[index])) {
                                setHeroState("Idle")
                                getState()
                            }
                            heroSprites[prevIndex] = with(getHero(text[prevIndex])) {
                                setHeroState("Death")
                                getState()
                            }

                            heroSprites[index].apply {
                                alpha = 1.0
                                addTo(this@sceneSelectHero).centerOn(heroAreas[index])
                                playAnimationLooped(spriteDisplayTime = Hero.TIME_IDLE)
                            }

                            heroSprites[prevIndex].apply {
                                alpha = 1.0
                                addTo(this@sceneSelectHero).centerOn(heroAreas[prevIndex])
                                playAnimation(spriteDisplayTime = Hero.TIME_DEATH)
                            }
                        }
                    writeStorage("HeroSelection", getSelectedHeroes().joinToString(separator = ","))
                }
            }
        }

        returnButton.onClick {
            visibleViews(.0, Easing.EASE_IN, header, returnButton, buttonText, *textSelect, *heroAreas, *heroSprites).awaitComplete() // Vanish views

            // Stop animation of model.heroes
            heroSprites.forEach { hero: Sprite -> hero.playAnimation() }
            delay(.5.seconds)

            removeChildrenIf { _, child -> child != background } // Remove views from scene
            sceneMainButtons() // To main scene
        }
    }

    private suspend fun SContainer.sceneSettings(header: Text) {
        // Making TextView for section
        val text = arrayOf("Turbo", "Music", "Sound")
        val textSettings = Array(text.size) { index ->
            text(text[index]).apply {
                settings.apply {
                    textSize = getTextSize("CommonText")
                    color = getColor("DarkViolet")
                    font = getFont()
                }
                alpha = .0
            }
        }

        // Header of section
        header.apply {
            settings.apply {
                y = .0
                textSize = getTextSize("Header")
                color = getColor("Violet")
                addTo(this@sceneSettings)
                centerXOn(this@sceneSettings)
            }
        }

        // Button to main scene
        val returnButton = RoundRect(Size2D(.2 * width, 60), RectCorners(30,15,15,30)).apply {
            y = this@sceneSettings.height - height
            color = settings.getColor("Black")
            alpha = .0
            addTo(this@sceneSettings)
            centerXOn(this@sceneSettings)
        }

        // Making text to the button
        val buttonText = text("Back").apply {
            settings.apply {
                textSize = getTextSize("CommonText")
                color = getColor("DarkViolet")
                font = getFont()
                alpha = .0
                addTo(this@sceneSettings)
                centerOn(returnButton)
            }
        }

        // Params of settings section
        val params: Array<View> = arrayOf(
            RoundRect(Size2D(.15 * width, 60), RectCorners(30, 15, 15, 30)).apply {
                settings.apply {
                    alpha = .0
                    color = if(getTurbo()) getColor("DarkViolet") else getColor("DarkGrey")
                }
            },
            uiSlider(settings.getMusicVolume(), .0, 1, .1, size = Size2D(.3 * width, 50)) {
                showTooltip = false
                alpha = .0
                styles { uiSelectedColor = settings.getColor("Cian") }
                changed { volume ->
                    settings.apply {
                        setMusicVolume("MainSound", volume)
                        writeStorage("MusicVolume", volume.toString())
                    }
                }
            },
            uiSlider(settings.getSoundVolume(), .0, 1, .1, size = Size2D(.3 * width, 50)) {
                showTooltip = false
                alpha = .0
                styles { uiSelectedColor = settings.getColor("Cian") }
                changed { volume ->
                    settings.apply {
                        setSoundVolume("Select", volume)
                        writeStorage("SoundVolume", volume.toString())
                    }
                }
            }
        )

        // Location of params
        uiVerticalStack(padding = 40.0) {
            for (i in textSettings.indices) {
                uiHorizontalStack(padding = 40.0) {
                    visibleScene(this, textSettings[i], params[i])
                    textSettings[i].centerYOn(this)
                    params[i].centerYOn(this)
                }
            }
        }.centerOn(this)

        // Making text to button
        val turboText = text(if(settings.getTurbo()) "ON" else "OFF").apply {
            settings.apply {
                textSize = getTextSize("SmallText")
                font = getFont()
                color = if (getTurbo()) getColor("Cian") else getColor("DarkViolet")
                addTo(this@sceneSettings)
                centerOn(params[0])
            }
        }

        // Display views
        visibleViews(.8, Easing.EASE_IN, returnButton)
        visibleViews(1.0, Easing.EASE_IN, header, turboText, buttonText, *textSettings, *params).awaitComplete()

        // Change turbo mode
        params[0].onClick {
            settings.apply {
                setTurbo()
                params[0].colorMul = if(getTurbo()) getColor("DarkViolet") else getColor("DarkGrey")
                turboText.also {
                    it.color = if (getTurbo()) getColor("Cian") else getColor("DarkViolet")
                    it.text = if(getTurbo()) "ON" else "OFF"
                }
                writeStorage("TurboMode", getTurbo().toString())
            }
        }

        returnButton.onClick {
            visibleViews(.0, Easing.EASE_IN, header, returnButton, turboText, buttonText, *textSettings, *params).awaitComplete() // Vanish views
            removeChildrenIf { _, child -> child != background } // Remove views from scene
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
}

