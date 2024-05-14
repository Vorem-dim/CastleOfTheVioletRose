import korlibs.image.format.*
import korlibs.image.text.*
import korlibs.inject.*
import korlibs.korge.animate.*
import korlibs.korge.scene.*
import korlibs.korge.tests.*
import korlibs.korge.ui.*
import korlibs.korge.view.*
import korlibs.korge.view.align.*
import korlibs.math.geom.*
import korlibs.math.interpolation.*
import korlibs.time.*
import kotlin.test.*

class MySceneTest : ViewsForTesting() {
    private fun Injector.mapCommon() {
        mapSingleton {
            MainScreenDependency()
        }
        mapPrototype {
            MainScreen(get())
        }
    }

    private fun Injector.mapCommon2() {
        mapSingleton {
            MainScreenDependency()
        }
        mapPrototype {
            MainButton(get())
        }
    }

    private fun Injector.mapCommon3() {
        mapSingleton {
            MainScreenDependency()
        }
        mapPrototype {
            MainSettings(get())
        }
    }

    open class MainScreenDependency {
        val mainText: String = "Castle of the\nviolet rose"
        val continueText: String = "Tap to continue"

        val textButton = arrayOf("New game", "Continue", "Violet Rose", "Select hero", "Settings")

        val textParams = arrayOf("Turbo", "Music", "Sound")
    }

    class MainScreen(val dependency: MainScreenDependency): Scene() {
        lateinit var background: Image
            private set
        lateinit var textTitles: Array<Text>
            private set

        override suspend fun SContainer.sceneInit() {
            background = image(__KR.KRImages.mainScreen.__file.readBitmap()) {
                alpha = .0
                scale(.7, .8)
                centerOn(this@sceneInit)
            }

            textTitles = arrayOf(
                text("Castle of the\nviolet rose").also {
                    it.alignment = TextAlignment.CENTER
                    it.alpha = .0
                    it.centerOn(this)
                },
                text("Tap to continue").also {
                    it.alignment = TextAlignment.CENTER
                    it.alpha = .0
                    it.centerXOn(this)
                }
            )
            textTitles[1].y = textTitles[0].run { y + height }

            visibleScene(this, background, *textTitles)

            visibleViews(1.0, Easing.EASE_IN, background).awaitComplete()
            visibleViews(1.0, Easing.EASE_IN, *textTitles).awaitComplete()
        }

        private fun SContainer.visibleViews(alpha: Double, easing: Easing, vararg views: View): Animator {
            return animator { parallel { views.forEach { view -> alpha(view, alpha, 1.2.seconds, easing) } } }
        }

        private fun visibleScene(parent: Container, vararg views: View) { views.forEach { view -> view.addTo(parent) } }
    }

    class MainButton(val dependency: MainScreenDependency): Scene() {
        lateinit var buttonAreas: Array<RoundRect>
        lateinit var textSections: Array<Text>

        override suspend fun SContainer.sceneInit() {
            buttonAreas = Array(5) { index: Int ->
                RoundRect(Size(.3 * width, .1 * height), RectCorners(30,15,15, 30)).apply {
                    y = (.18 * (index - 2) + .45) * this@sceneInit.height
                    alpha = .0
                    addTo(this@sceneInit)
                    centerXOn(this@sceneInit)
                }
            }

            val text = arrayOf("New game", "Continue", "Violet Rose", "Select hero", "Settings")
            textSections = Array(5) { index ->
                text(text[index]).apply {
                    alpha = .0
                    zIndex = 10.0
                    addTo(this@sceneInit)
                    centerOn(buttonAreas[index])
                }
            }

            visibleViews(.5, Easing.EASE_IN, *buttonAreas)
            visibleViews(1.0, Easing.EASE_IN, *textSections)
        }

        private fun SContainer.visibleViews(alpha: Double, easing: Easing, vararg views: View): Animator {
            return animator { parallel { views.forEach { view -> alpha(view, alpha, 1.2.seconds, easing) } } }
        }
    }

    class MainSettings(val dependency: MainScreenDependency): Scene() {
        lateinit var textSettings: Array<Text>
        lateinit var params: Array<View>

        override suspend fun SContainer.sceneInit() {
            val text = arrayOf("Turbo", "Music", "Sound")
            textSettings = Array(text.size) { index ->
                text(text[index]).apply {
                    alpha = .0
                }
            }

            params = arrayOf(
                RoundRect(Size2D(.15 * width, 60), RectCorners(30, 15, 15, 30)).apply {
                    alpha = .0
                },
                uiSlider(1, .0, 1, .1, size = Size2D(.3 * width, 50)) {
                    showTooltip = false
                    alpha = .0
                },
                uiSlider(2, .0, 1, .1, size = Size2D(.3 * width, 50)) {
                    showTooltip = false
                    alpha = .0
                }
            )

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
            val turboText = text("ON").apply {
                addTo(this@sceneInit)
                centerOn(params[0])
            }

            visibleViews(1.0, Easing.EASE_IN, turboText, *textSettings, *params).awaitComplete()
        }

        private fun SContainer.visibleViews(alpha: Double, easing: Easing, vararg views: View): Animator {
            return animator { parallel { views.forEach { view -> alpha(view, alpha, 1.2.seconds, easing) } } }
        }

        private fun visibleScene(parent: Container, vararg views: View) { views.forEach { view -> view.addTo(parent) } }
    }

    @Test
    fun mainScreenTest() = sceneTest<MainScreen>(configureInjector = { mapCommon() }) {
        assertTrue(background.isVisibleToUser())
        assertTrue(textTitles[0].run { isVisibleToUser() && text == dependency.mainText })
        assertTrue(textTitles[1].run { isVisibleToUser() && text == dependency.continueText })
    }

    @Test
    fun mainScreenTest2() = sceneTest<MainButton>(configureInjector = { mapCommon2() }) {
        textSections.forEachIndexed { index, textView ->
            assertEquals(textView.text, dependency.textButton[index])
        }
    }

    @Test
    fun mainScreenTest3() = sceneTest<MainSettings>(configureInjector = { mapCommon3() }) {
        for (i in params.indices) {
            assertTrue(params[i].isVisibleToUser())
            textSettings[i].also {
                assertTrue(it.isVisibleToUser())
                assertEquals(it.text, dependency.textParams[i])
            }
        }
    }
}
