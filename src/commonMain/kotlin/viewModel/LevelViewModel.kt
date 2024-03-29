package viewModel

import korlibs.event.*
import korlibs.image.atlas.*
import korlibs.io.file.std.*
import korlibs.korge.box2d.*
import korlibs.korge.view.*
import korlibs.math.geom.*
import kotlinx.coroutines.*
import model.*
import model.enemies.*
import model.heroes.*
import org.jbox2d.dynamics.*

class LevelViewModel(private val settings: SettingsViewModel) {
    private val controller = Controller()
    private lateinit var hero: Hero
    private lateinit var locationEnemies: Array<Array<Enemy>>

    suspend fun startGame(container: Container): Int {
        for (i in settings.getSelectedHeroes().indices)
            if (settings.getSelectedHeroes()[i]) {
                hero = when(i) {
                    0 -> settings.getHero("Knight")
                    1 -> settings.getHero("Anomaly")
                    2 -> settings.getHero("Thief")
                    else -> settings.getHero("Violet Rose")
                }
                break
            }
        hero.heroSpriteVisible()
        controller.controlPanelCreate(container)

        return settings.getCurrentLevel()
    }

    suspend fun startGameLevel(container: Container, heroPos: Point, vararg enemies: Array<Map<String, Point>>) {
        locationEnemies = Array(enemies.size) { indexLocation: Int ->
            enemies[indexLocation].run {
                Array(size) { index: Int ->
                    Enemy(
                        arrayOf(
                            resourcesVfs["sprites/enemies/${this[index].keys.first()}.xml"].readAtlas(),
                            resourcesVfs["sprites/enemies/${this[index].keys.first()}_reversed.xml"].readAtlas()
                        ),
                        container
                    ).apply {
                        getState().apply {
                            this@run[index].values.first().apply { position(x, y - height) }
                            if (indexLocation == 0) {
                                addTo(container)
                                registerBodyWithFixture(
                                    gravityScale = 4,
                                    type = BodyType.DYNAMIC,
                                    friction = 1.0,
                                    fixedRotation = true
                                )
                                playAnimationLooped(spriteDisplayTime = Enemy.TIME_IDLE)
                            }
                        }
                    }
                }
            }
        }

        hero.getState().apply {
            position(heroPos.x, heroPos.y - height)
            addTo(container)
            registerBodyWithFixture(gravityScale = 4, type = BodyType.DYNAMIC)
            playAnimationLooped(spriteDisplayTime = Hero.TIME_IDLE)
        }
    }

    suspend fun eventsHandler(container: Container, borders: List<SolidRect>, input: List<Touch>): String {
        var changeFrame = ""
        when(controller.events(input)) {
            HeroEvent.MOVE_RIGHT -> {
                if (!hero.inMidAir && !hero.isFall) {
                    hero.changeState(container, "Run", "Right")
                    hero.getState().apply {
                        if (width + x + 10f > container.width) {
                            x = container.width - width
                            changeFrame = "Next"
                        }
                        else if (!hero.spriteReversed) body?.linearVelocityX = 10f
                    }
                }
                else
                    hero.getState().body?.linearVelocityX = 10f
            }
            HeroEvent.MOVE_LEFT -> {
                if (!hero.inMidAir && !hero.isFall) {
                    hero.changeState(container, "Run", "Left")
                    hero.getState().apply {
                        if (x - 10f < 0) {
                            x = 0.0
                            changeFrame = "Previous"
                        }
                        else if (hero.spriteReversed) body?.linearVelocityX = -10f
                    }
                }
                else
                    hero.getState().body?.linearVelocityX = -10f
            }
            HeroEvent.ATTACK -> if (!hero.inMidAir && !hero.isFall)
                hero.changeState(container, "Attack", startFrame = 0)
            HeroEvent.ACTION -> TODO()
            HeroEvent.JUMP -> if (!hero.inMidAir && !hero.isFall) {
                hero.changeState(container, "Jump")
                hero.inMidAir = true

                apply {
                    for (i in 0..20) {
                        hero.getState().body?.gravityScale = -4f
                        delay(20)
                    }

                    hero.inMidAir = false
                    hero.isFall = true
                    hero.changeState(container, "Fall")

                    do {
                        delay(20)
                        borders.forEach { border ->
                            if (border.y - hero.getState().run { height + y } < .8 && (.5 * hero.getState()
                                    .run { x + width } >= border.x && .5 * hero.getState()
                                    .run { x + width } <= border.run { x + width * cos(rotation) })) {
                                hero.isFall = false
                            }
                        }
                    } while (hero.isFall)
                }
            }
            HeroEvent.IDLE -> if (!hero.inMidAir && !hero.isFall) hero.changeState(container, "Idle")
        }
        return changeFrame
    }

    fun heroToNewLocation(container: Container, heroPos: Point) {
        hero.getState().apply {
            removeFromParent()
            position(heroPos.x, heroPos.y - height)
            rotation(Angle.fromDegrees(0))
            addTo(container)
        }
    }
}
