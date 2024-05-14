package model.heroes

import korlibs.image.atlas.*
import korlibs.korge.box2d.*
import korlibs.korge.view.*
import korlibs.math.geom.*
import korlibs.time.*
import org.jbox2d.dynamics.*

open class Hero(atlas: Array<Atlas>, container: Container) {
    var spriteReversed: Boolean = false
    var deathAnimation: Boolean = true
    var inMidAir: Boolean = false
    var isFall: Boolean = false
    protected val heroStates: MutableList<Sprite> = mutableListOf()
    private var stateIndex: Int = 1

    companion object {
        val TIME_ATTACK: TimeSpan = .1.seconds
        val TIME_DEATH: TimeSpan = .1.seconds
        val TIME_JUMP: TimeSpan = .1.seconds
        val TIME_FALL: TimeSpan = .1.seconds
        val TIME_IDLE: TimeSpan = .15.seconds
        val TIME_RUN: TimeSpan = .15.seconds
        val TIME_HIT: TimeSpan = .15.seconds
    }

    init {
        atlas.forEach {
            it.apply {
                heroStates.addAll(arrayOf(
                    container.sprite(getSpriteAnimation("attack"), Anchor.CENTER).apply { removeFromParent() },
                    container.sprite(getSpriteAnimation("idle"), Anchor.CENTER).apply { removeFromParent() },
                    container.sprite(getSpriteAnimation("hit"), Anchor.CENTER).apply { removeFromParent() },
                    container.sprite(getSpriteAnimation("run"), Anchor.CENTER).apply { removeFromParent() },
                    container.sprite(getSpriteAnimation("death"), Anchor.CENTER).apply {
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

    private fun getHeroTime(state: String): TimeSpan = when(state) {
        "Attack" -> TIME_ATTACK
        "Death" -> TIME_DEATH
        "Jump" -> TIME_JUMP
        "Fall" -> TIME_FALL
        "Idle" -> TIME_IDLE
        "Run" -> TIME_RUN
        "Hit" -> TIME_HIT
        else -> TIME_IDLE
    }

    private fun getHeroState(state: String): Int = when(state) {
        "Attack" -> { if (spriteReversed) 5 else 0 }
        "Idle" -> { if (spriteReversed) 6 else 1 }
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
            playAnimationLooped(spriteDisplayTime = getHeroTime(state), startFrame = startFrame)
        }
    }

    fun heroSpriteVisible() {
        heroStates[1].alpha = 1.0
        heroStates[4].alpha = 1.0
    }

    fun setHeroState(state: String) { stateIndex = getHeroState(state) }

    fun getState() = heroStates[stateIndex]
}
