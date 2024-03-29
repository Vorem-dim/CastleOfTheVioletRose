package model.enemies

import korlibs.image.atlas.*
import korlibs.korge.box2d.*
import korlibs.korge.view.*
import korlibs.time.*
import org.jbox2d.dynamics.*

class Enemy(atlas: Array<Atlas>, container: Container) {
    private val enemyStates: MutableList<Sprite> = mutableListOf()
    private var spriteReversed: Boolean = false
    private var stateIndex: Int = 1

    companion object {
        val TIME_ATTACK: TimeSpan = .2.seconds
        val TIME_DEATH: TimeSpan = .2.seconds
        val TIME_IDLE: TimeSpan = .15.seconds
        val TIME_WALK: TimeSpan = .15.seconds
        val TIME_HIT: TimeSpan = .2.seconds
    }

    init {
        atlas.forEach {
            it.apply {
                enemyStates.addAll(arrayOf(
                    container.sprite(getSpriteAnimation("attack")).apply { removeFromParent() },
                    container.sprite(getSpriteAnimation("idle")).apply { removeFromParent() },
                    container.sprite(getSpriteAnimation("hit")).apply { removeFromParent() },
                    container.sprite(getSpriteAnimation("walk")).apply { removeFromParent() },
                    container.sprite(getSpriteAnimation("death")).apply {
                        removeFromParent()
                        onFrameChanged {
                            if (currentSpriteIndex == totalFrames - 1) {
                                stopAnimation()
                            }
                        }
                    }
                ))
            }
        }
    }

    private fun getEnemyTime(state: String): TimeSpan = when(state) {
        "Attack" -> TIME_ATTACK
        "Death" -> TIME_DEATH
        "Idle" -> TIME_IDLE
        "Run" -> TIME_WALK
        "Hit" -> TIME_HIT
        else -> TIME_IDLE
    }

    private fun getEnemyState(state: String): Int = when(state) {
        "Attack" -> { if (spriteReversed) 5 else 0 }
        "Idle" -> { if (spriteReversed) 6 else 1 }
        "Hit" -> { if (spriteReversed) 7 else 2 }
        "Walk" -> { if (spriteReversed) 8 else 3 }
        "Death" -> { if (spriteReversed) 9 else 4 }
        else -> 1
    }

    fun changeState(container: Container, state: String, direction: String = "", startFrame: Int = -1) {
        if (direction == "Left") spriteReversed = true
        else if (direction == "Right") spriteReversed = false

        if (stateIndex == getEnemyState(state))
            return

        val prevIndex = stateIndex
        enemyStates[prevIndex].apply {
            stopAnimation()
            removeFromParent()
        }

        stateIndex = getEnemyState(state)
        enemyStates[stateIndex].apply {
            addTo(container)
            registerBodyWithFixture(
                gravityScale = 4,
                type = BodyType.DYNAMIC,
                friction = 1.0,
                fixedRotation = true
            )
            pos = enemyStates[prevIndex].pos
            rotation = enemyStates[prevIndex].rotation
            playAnimationLooped(spriteDisplayTime = getEnemyTime(state), startFrame = startFrame)
        }
    }

    fun getState() = enemyStates[stateIndex]
}
