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
        val spriteTime : Map<String, TimeSpan> = mapOf(
            "Idle" to .15.seconds,
            "Run" to .15.seconds,
            "Hit" to .2.seconds,
            "Death" to .2.seconds,
            "Attack" to .2.seconds
        )
    }

    init {
        atlas.forEach {
            it.apply {
                enemyStates.addAll(arrayOf(
                    container.sprite(getSpriteAnimation("attack")).apply { removeFromParent() },
                    container.sprite(getSpriteAnimation("idle")).apply { removeFromParent() },
                    container.sprite(getSpriteAnimation("hurt")).apply { removeFromParent() },
                    container.sprite(getSpriteAnimation("walk")).apply { removeFromParent() },
                    container.sprite(getSpriteAnimation("dead")).apply {
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

    private fun getEnemyState(state: String): Int = when(state) {
        "Attack" -> { if (spriteReversed) 5 else 0 }
        "Stand" -> { if (spriteReversed) 6 else 1 }
        "Hit" -> { if (spriteReversed) 7 else 2 }
        "Run" -> { if (spriteReversed) 8 else 3 }
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
                fixedRotation = state == "Jump" || state == "Fall"
            )
            pos = enemyStates[prevIndex].pos
            rotation = enemyStates[prevIndex].rotation
            playAnimationLooped(spriteDisplayTime = Enemy.spriteTime[state]!!, startFrame = startFrame)
        }
    }

    fun getEnemyState() = enemyStates[stateIndex]
}
