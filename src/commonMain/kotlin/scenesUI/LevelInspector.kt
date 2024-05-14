package scenesUI

import korlibs.image.format.*
import korlibs.korge.box2d.*
import korlibs.korge.scene.*
import korlibs.korge.view.*
import korlibs.math.geom.*
import korlibs.time.*
import kotlinx.coroutines.*
import viewModel.*

class LevelInspector(settings: SettingsViewModel): Scene() {
    private val levelViewModel = LevelViewModel(settings)

    override suspend fun SContainer.sceneInit() {}

    override suspend fun SContainer.sceneMain() {
        when(levelViewModel.startGame(this)) {
            0 -> level0()
        }
    }

    private suspend fun SContainer.level0() {
        var screenIndex = 0
        val background: Array<Image> = arrayOf(
            image(__KR.KRImagesLevels.level00.__file.readBitmap()).apply { scale(.65, .7) },
            image(__KR.KRImagesLevels.level01.__file.readBitmap()).apply { removeFromParent() },
            image(__KR.KRImagesLevels.level02.__file.readBitmap()).apply { removeFromParent() },
            image(__KR.KRImagesLevels.level03.__file.readBitmap()).apply { scale(.7, .7); removeFromParent() }
        )

        val borders: Array<List<SolidRect>> = arrayOf(
            listOf(SolidRect(Size(width, .05)).position(0, 580)),
            listOf(
                SolidRect(Size(180, .05)).position(0, 510),
                SolidRect(Size(80, .05)).position(180, 510).rotation(Angle.fromDegrees(25)),
                SolidRect(Size(40, .05)).position(315, 445).rotation(Angle.fromDegrees(-25)),
                SolidRect(Size(60, .05)).position(350, 428),
                SolidRect(Size(500, .05)).position(410, 428).rotation(Angle.fromDegrees(15)),
                SolidRect(Size(400, .05)).position(893, 557.5).rotation(Angle.fromDegrees(5))
            ),
            listOf(
                SolidRect(Size(840, .05)).position(0, 580),
                SolidRect(Size(45, .05)).position(945, 515).rotation(Angle.fromDegrees(-10)),
                SolidRect(Size(25, .05)).position(989, 507).rotation(Angle.fromDegrees(-28)),
                SolidRect(Size(50, .05)).position(1011, 495.3).rotation(Angle.fromDegrees(-10)),
                SolidRect(Size(60, .05)).position(1060, 486.6).rotation(Angle.fromDegrees(2))
            ),
            listOf(
                SolidRect(Size(550, .05)).position(0, 560),
                SolidRect(Size(500, .05)).position(800, 600)
            )
        )

        borders.forEachIndexed { index: Int, border: List<SolidRect> ->
            border.forEach { borderPart: SolidRect ->
                borderPart.apply {
                    if (index == screenIndex) {
                        addTo(this@level0)
                        registerBodyWithFixture()
                    } else
                        removeFromParent()
                }
            }
        }

        val y = borders[screenIndex][0].y
        levelViewModel.startGameLevel(
            this,
            Point(0, y),
            arrayOf(
                mapOf("skeleton" to Point(500, y)),
                mapOf("skeleton" to Point(800, y))
            ),
            arrayOf(
                mapOf("skeleton" to Point(900, y - 20))
            ),
            arrayOf(
                mapOf("skeleton" to Point(200, y)),
                mapOf("skeleton" to Point(500, y))
            ),
            arrayOf(
                mapOf("skeleton" to Point(300, y)),
                mapOf("skeleton" to Point(900, y))
            )
        )

        addFixedUpdater(50.timesPerSecond) {
            launch {
                when(levelViewModel.eventsHandler(this@level0, borders[screenIndex], input.touch.touches)) {
                    "Next" -> {
                        if (screenIndex == borders.size - 1) return@launch
                        borders[screenIndex].forEach { border -> border.removeFromParent() }
                        background[screenIndex++].removeFromParent()
                        background[screenIndex].addTo(this@level0)
                        borders[screenIndex].forEach { border: SolidRect ->
                            border.apply { addTo(this@level0); registerBodyWithFixture() }
                        }
                        levelViewModel.heroToNewLocation(this@level0, borders[screenIndex][0], "Forward", screenIndex)
                    }
                    "Previous" -> {
                        if (screenIndex == 0) return@launch
                        borders[screenIndex].forEach { border -> border.removeFromParent() }
                        background[screenIndex--].removeFromParent()
                        background[screenIndex].addTo(this@level0)
                        borders[screenIndex].forEach { border: SolidRect ->
                            border.apply { addTo(this@level0); registerBodyWithFixture() }
                        }
                        levelViewModel.heroToNewLocation(
                            this@level0,
                            borders[screenIndex].last(),
                            "Back",
                            screenIndex
                        )
                    }
                }
            }

            launch {
                levelViewModel.enemyAI(this@level0, screenIndex)
                levelViewModel.hitEnemy()
            }
        }
    }

}
