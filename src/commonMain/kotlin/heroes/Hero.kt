package heroes

import HeroEvent
import korlibs.image.atlas.*
import korlibs.io.async.*
import korlibs.korge.box2d.*
import korlibs.korge.view.*
import korlibs.time.*
import kotlinx.coroutines.*
import org.jbox2d.dynamics.*
import kotlin.coroutines.*

open class Hero(atlas: Array<Atlas>, container: Container) {
    var deathAnimation: Boolean = true
    private var inMidAir: Boolean = false
    private var isFall: Boolean = false
    protected val heroStates: MutableList<Sprite> = mutableListOf()
    private var spriteReversed: Boolean = false
    private var stateIndex: Int = 1

    companion object {
        val spriteTime : Map<String, TimeSpan> = mapOf(
            "Stand" to .15.seconds,
            "Run" to .15.seconds,
            "Hit" to .15.seconds,
            "Death" to .1.seconds,
            "Attack" to .1.seconds,
            "Jump" to .1.seconds,
            "Fall" to .1.seconds
        )
    }

    init {
        atlas.forEach {
            it.apply {
                heroStates.addAll(arrayOf(
                    container.sprite(getSpriteAnimation("attack")).apply { removeFromParent() },
                    container.sprite(getSpriteAnimation("stand")).apply { removeFromParent() },
                    container.sprite(getSpriteAnimation("hit")).apply { removeFromParent() },
                    container.sprite(getSpriteAnimation("run")).apply { removeFromParent() },
                    container.sprite(getSpriteAnimation("death")).apply {
                        removeFromParent()
                        onFrameChanged {
                            if (currentSpriteIndex == totalFrames - 1) {
                                if (deathAnimation) stopAnimation()
                                deathAnimation = !deathAnimation
                            }
                        }
                    }
                ))
            }
        }
        heroStates[1].alpha = .0
        heroStates[4].alpha = .0
    }

    private fun getHeroState(state: String): Int = when(state) {
        "Attack" -> { if (spriteReversed) 5 else 0 }
        "Stand" -> { if (spriteReversed) 6 else 1 }
        "Hit" -> { if (spriteReversed) 7 else 2 }
        "Run" -> { if (spriteReversed) 8 else 3 }
        "Death" -> { if (spriteReversed) 9 else 4 }
        "Jump" -> { if (spriteReversed) 12 else 10 }
        "Fall" -> { if (spriteReversed) 13 else 11 }
        else -> 1
    }

    private fun changeState(container: Container, state: String, direction: String = "", startFrame: Int = -1) {
        if (direction == "Left") spriteReversed = true
        else if (direction == "Right") spriteReversed = false

        if (stateIndex == getHeroState(state))
            return

        val prevIndex = stateIndex
        heroStates[prevIndex].apply {
            stopAnimation()
            removeFromParent()
        }

        stateIndex = getHeroState(state)
        heroStates[stateIndex].apply {
            addTo(container)
            registerBodyWithFixture(type = BodyType.DYNAMIC)
            pos = heroStates[prevIndex].pos
            playAnimationLooped(spriteDisplayTime = spriteTime[state]!!, startFrame = startFrame)
        }
    }

    suspend fun heroActions(container: Container, event: HeroEvent, borders: List<SolidRect>) {
        when(event) {
            HeroEvent.Attack -> if (!inMidAir && !isFall) changeState(container, "Attack", startFrame = 0)
            HeroEvent.Action -> changeState(container, "Action")
            HeroEvent.Right -> apply {
                if (!inMidAir && !isFall) {
                    changeState(container, "Run", direction = "Right")
                    if (!spriteReversed) getState().body?.linearVelocityX = 10f
                }
                else
                    getState().body?.linearVelocityX = 10f
            }
            HeroEvent.Left -> apply {
                if (!inMidAir && !isFall) {
                    changeState(container, "Run", direction = "Left")
                    if (spriteReversed) getState().body?.linearVelocityX = -10f
                }
                else
                    getState().body?.linearVelocityX = -10f
            }
            HeroEvent.Jump -> if (!inMidAir && !isFall) {
                changeState(container, "Jump")
                inMidAir = true
                launch(EmptyCoroutineContext) {
                    apply {
                        for (i in 0..20) {
                            getState().body?.gravityScale = -3f
                            delay(20)
                        }

                        inMidAir = false
                        isFall = true
                        changeState(container, "Fall")

                        var falling = true
                        do {
                            delay(20)
                            getState().body?.gravityScale = 2f
                            borders.forEach { border ->
                                if (getState().run { y + height + 1 } >= border.y) falling = false
                            }
                        } while (falling)

                        isFall = false
                        getState().body?.gravityScale = 1f
                    }
                }
            }
            HeroEvent.Idle -> if (!inMidAir && !isFall) changeState(container, "Stand")
        }
    }

    fun heroSpriteVisible() {
        heroStates[1].alpha = 1.0
        heroStates[4].alpha = 1.0
    }

    fun setHeroState(state: String) { stateIndex = getHeroState(state) }

    fun getState() = heroStates[stateIndex]
}
