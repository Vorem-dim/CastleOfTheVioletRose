package heroes

import HeroEvent
import korlibs.image.atlas.*
import korlibs.io.async.*
import korlibs.korge.box2d.*
import korlibs.korge.view.*
import korlibs.math.geom.*
import korlibs.math.range.*
import korlibs.time.*
import kotlinx.coroutines.*
import org.jbox2d.dynamics.*
import kotlin.coroutines.*

open class Hero(atlas: Array<Atlas>, container: Container) {
    var deathAnimation: Boolean = true
    var inMidAir: Boolean = false
    var isFall: Boolean = false
    protected val heroStates: MutableList<Sprite> = mutableListOf()
    var spriteReversed: Boolean = false
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

    fun changeState(container: Container, state: String, direction: String = "", startFrame: Int = -1) {
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
            registerBodyWithFixture(
                gravityScale = 4,
                type = BodyType.DYNAMIC,
                friction = 1.0,
                fixedRotation = state == "Jump" || state == "Fall"
            )
            pos = heroStates[prevIndex].pos
            rotation = if (state == "Jump" || state == "Fall") Angle.Companion.fromDegrees(0) else heroStates[prevIndex].rotation
            playAnimationLooped(spriteDisplayTime = spriteTime[state]!!, startFrame = startFrame)
        }
    }

    fun heroSpriteVisible() {
        heroStates[1].alpha = 1.0
        heroStates[4].alpha = 1.0
    }

    fun setHeroState(state: String) { stateIndex = getHeroState(state) }

    fun getState() = heroStates[stateIndex]
}
