import korlibs.event.*
import korlibs.image.format.*
import korlibs.korge.annotations.*
import korlibs.korge.box2d.*
import korlibs.korge.input.*
import korlibs.korge.scene.*
import korlibs.korge.ui.*
import korlibs.korge.view.*
import korlibs.korge.view.align.*
import korlibs.korge.view.collision.*
import korlibs.math.geom.*
import korlibs.time.*
import org.jbox2d.dynamics.*


class LevelInspector(private val settings: Settings): Scene() {
    private lateinit var hero: Hero

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
    }

    override suspend fun SContainer.sceneMain() {
        when(settings.currentLevel) {
            0 -> level0()
            1 -> level1()
            2 -> level2()
        }
    }

    private suspend fun SContainer.level0() {
        val background: Array<Image> = arrayOf(
            image(KR.images.levels.level00.__file.readBitmap()).scale(.65, .7),
            image(KR.images.levels.level01.__file.readBitmap()).apply { removeFromParent() },
            image(KR.images.levels.level02.__file.readBitmap()).apply { removeFromParent() },
            image(KR.images.levels.level03.__file.readBitmap()).apply { scale(.7, .7); removeFromParent() }
        )

        SolidRect(Size(this.width, .01)).position(0, 580).addTo(this).registerBodyWithFixture()

        hero.heroState.addTo(this).position(0, 400).registerBodyWithFixture(type = BodyType.DYNAMIC)
        hero.heroState.playAnimationLooped(spriteDisplayTime = Hero.spriteTime["Stand"]!!)

        val btn1 = uiButton(size = Size(80, 80)).addTo(this)
        btn1.alignBottomToBottomOf(this)
        btn1.alignLeftToLeftOf(this)

        val btn2 = uiButton(size = Size(80, 80)).addTo(this)
        btn2.alignBottomToBottomOf(this)
        btn2.alignRightToRightOf(this)

        addUpdater {
            for (i in input.touch.touches.indices) {
                val touch = input.touch.touches[i]
                if (touch.isActive) {
                    val rect = SolidRect(1, 1).position(touch.x, touch.y)
                    if (btn1.collidesWith(rect)) {
                        if (!hero.reversed())
                            hero.spriteReverse(this, "Run")
                        else
                            hero.changeState(this, "Run")
                        hero.heroState.x -= 5
                        break
                    }
                    if (btn2.collidesWith(rect)) {
                        if (hero.reversed())
                            hero.spriteReverse(this, "Run")
                        else
                            hero.changeState(this, "Run")
                        hero.heroState.x += 5
                        break
                    }
                }
                else {
                    hero.changeState(this, "Stand")
                }
            }
        }
    }

    private suspend fun level1() {

    }

    private suspend fun level2() {

    }
}
