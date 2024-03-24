package scenes

import Controller
import Enemy
import heroes.*
import Settings
import korlibs.image.atlas.*
import korlibs.image.color.*
import korlibs.image.format.*
import korlibs.io.file.std.*
import korlibs.korge.box2d.*
import korlibs.korge.scene.*
import korlibs.korge.view.*
import korlibs.korge.view.collision.*
import korlibs.math.geom.*
import korlibs.time.*
import kotlinx.coroutines.*
import org.jbox2d.common.*
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

        val enemies: Array<Array<Enemy>> = arrayOf(
            arrayOf(
                Enemy(arrayOf(
                    resourcesVfs["sprites/bosses/skeleton.xml"].readAtlas(),
                    resourcesVfs["sprites/bosses/skeleton_reversed.xml"].readAtlas()
                ), this)
            ),
            arrayOf(),
            arrayOf(),
            arrayOf()
        )

        enemies.forEachIndexed { index: Int, enemy: Array<Enemy> ->
            enemy.forEach { kind: Enemy ->
                kind.apply {
                    if (index == screenIndex) {
                        getEnemyState().apply {
                            addTo(this@level0)
                            position(500, borders[screenIndex][0].y - height) // !!!!
                            registerBodyWithFixture()
                            playAnimationLooped(spriteDisplayTime = Enemy.spriteTime["Idle"]!!)
                        }
                    }
                }
            }
        }

        hero.getState().apply {
            addTo(this@level0)
            pos = Point(0, borders[screenIndex][0].y - height)
            registerBodyWithFixture(gravityScale = 4, type = BodyType.DYNAMIC)
            playAnimationLooped(spriteDisplayTime = Hero.spriteTime["Stand"]!!)
        }

        addFixedUpdater(50.timesPerSecond) {
            val event = controller.events(input.touch.touches)

            launch {// Screen change
                if (hero.getState().run { width + x } > this@level0.width) {
                    if (screenIndex == borders.size - 1) {
                        hero.getState().apply { x = this@level0.width - width }
                    }
                    else if (hero.getState().run { .75 * width + x } > this@level0.width) {
                        borders[screenIndex].forEach { border -> border.removeFromParent() }
                        background[screenIndex++].removeFromParent()
                        background[screenIndex].addTo(this@level0)
                        borders[screenIndex].forEach { border -> border.apply { addTo(this@level0); registerBodyWithFixture() } }
                        hero.getState().apply {
                            removeFromParent()
                            pos = Point(0, borders[screenIndex][0].y - height)
                            rotation = Angle.fromDegrees(0)
                            addTo(this@level0)
                        }
                    }
                }
                else if (hero.getState().x < -.1) {
                    if (screenIndex == 0) {
                        hero.getState().x = -.1
                    }
                    else if (hero.getState().x < -.25) {
                        borders[screenIndex].forEach { border -> border.removeFromParent() }
                        background[screenIndex--].removeFromParent()
                        background[screenIndex].addTo(this@level0)
                        borders[screenIndex].forEach { border -> border.apply { addTo(this@level0); registerBodyWithFixture() } }
                        hero.getState().apply {
                            removeFromParent()
                            val border = borders[screenIndex].last()
                            pos = Point(border.run { x + width } - width, border.y - height)
                            rotation = Angle.fromDegrees(0)
                            addTo(this@level0)
                        }
                    }
                }
            }


            when(event) { // Action of the hero
                HeroEvent.Attack -> if (!hero.inMidAir && !hero.isFall) hero.changeState(this@level0, "Attack", startFrame = 0)
                HeroEvent.Action -> {}
                HeroEvent.Right -> apply {
                    if (!hero.inMidAir && !hero.isFall) {
                        hero.changeState(this@level0, "Run", direction = "Right")
                        if (!hero.spriteReversed) hero.getState().body?.linearVelocityX = 10f
                    }
                    else
                        hero.getState().body?.linearVelocityX = 10f
                }
                HeroEvent.Left -> apply {
                    if (!hero.inMidAir && !hero.isFall) {
                        hero.changeState(this@level0, "Run", direction = "Left")
                        if (hero.spriteReversed) hero.getState().body?.linearVelocityX = -10f
                    }
                    else
                        hero.getState().body?.linearVelocityX = -10f
                }
                HeroEvent.Jump -> if (!hero.inMidAir && !hero.isFall) {
                    hero.changeState(this@level0, "Jump")
                    hero.inMidAir = true
                    launch {
                        apply {
                            for (i in 0..20) {
                                hero.getState().body?.gravityScale = -4f
                                delay(20)
                            }

                            hero.inMidAir = false
                            hero.isFall = true
                            hero.changeState(this@level0, "Fall")

                            do {
                                delay(20)
                                borders[screenIndex].forEach { border ->
                                    if (border.y - hero.getState().run { height + y } < .8 && (.5 * hero.getState().run { x + width } >= border.x && .5 * hero.getState().run { x + width } <= border.run { x + width * cos(rotation) })) {
                                        hero.isFall = false
                                    }
                                }
                            } while (hero.isFall)
                        }
                    }
                }
                HeroEvent.Idle -> if (!hero.inMidAir && !hero.isFall) hero.changeState(this@level0, "Stand")
            }
        }
    }

}
