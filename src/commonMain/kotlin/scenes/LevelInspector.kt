package scenes

import Controller
import heroes.*
import Settings
import korlibs.image.format.*
import korlibs.korge.box2d.*
import korlibs.korge.scene.*
import korlibs.korge.view.*
import korlibs.math.geom.*
import korlibs.time.*
import kotlinx.coroutines.launch
import org.jbox2d.dynamics.*

class LevelInspector(private val settings: Settings): Scene() {
    private lateinit var hero: Hero
    private lateinit var controller: Controller

    override suspend fun SContainer.sceneInit() {
        for (i in settings.selectedHeroes.indices)
            if (settings.selectedHeroes[i]) {
                hero = when(i) {
                    0 -> settings.heroes["Knight"]!!
                    1 -> settings.heroes["Anomaly"]!!
                    2 -> settings.heroes["Thief"]!!
                    else -> settings.heroes["Violet Rose"]!!
                }
                break
            }
        hero.heroSpriteVisible()

        controller = Controller()
        controller.controlPanelCreate(this)
    }

    override suspend fun SContainer.sceneMain() {
        when(settings.currentLevel) {
            0 -> level0()
        }
    }

    private suspend fun SContainer.level0() {
        var screenIndex = 0
        val background: Array<Image> = arrayOf(
            image(__KR.KRImagesLevels.level00.__file.readBitmap()).scale(.65, .7),
            image(__KR.KRImagesLevels.level01.__file.readBitmap()).apply { removeFromParent() },
            image(__KR.KRImagesLevels.level02.__file.readBitmap()).apply { removeFromParent() },
            image(__KR.KRImagesLevels.level03.__file.readBitmap()).apply { scale(.7, .7); removeFromParent() }
        )

        val borders: Array<List<SolidRect>> = arrayOf(
            listOf(SolidRect(Size(width, 1.01)).position(0, 580)),
            listOf(
                SolidRect(Size(180, 1.01)).position(0, 510),
                SolidRect(Size(80, 1.01)).position(180, 510).rotation(Angle.fromDegrees(25)),
                SolidRect(Size(40, 1.01)).position(315, 445).rotation(Angle.fromDegrees(-25)),
                SolidRect(Size(60, 1.01)).position(350, 428),
                SolidRect(Size(500, 1.01)).position(410, 428).rotation(Angle.fromDegrees(15)),
                SolidRect(Size(400, 1.01)).position(893, 557).rotation(Angle.fromDegrees(5))
            ),
            listOf(
                SolidRect(Size(840, 1.01)).position(0, 580),
                SolidRect(Size(45, 1.01)).position(945, 515).rotation(Angle.fromDegrees(-10)),
                SolidRect(Size(25, 1.01)).position(989, 505).rotation(Angle.fromDegrees(-25)),
                SolidRect(Size(60, 1.01)).position(1010, 495).rotation(Angle.fromDegrees(-10)),
                SolidRect(Size(60, 1.01)).position(1069, 485)
            ),
            listOf(
                SolidRect(Size(1000, 1.01)).position(0, 580)
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

        hero.getState().apply {
            addTo(this@level0)
            y = borders[screenIndex][0].y - height
            registerBodyWithFixture(type = BodyType.DYNAMIC)
            playAnimationLooped(spriteDisplayTime = Hero.spriteTime["Stand"]!!)
        }

        addFixedUpdater(50.timesPerSecond) {
            val event = controller.events(input.touch.touches)

            launch { hero.heroActions(this@level0, event, borders[screenIndex]) }
        }
    }
}
