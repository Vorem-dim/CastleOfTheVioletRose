import korlibs.image.atlas.*
import korlibs.korge.box2d.*
import korlibs.korge.view.*
import korlibs.time.*
import org.jbox2d.dynamics.*

class Hero(atlas: Array<Atlas>, container: Container) {
    var heroState: Sprite
    private val stand: Sprite
    private val death: Sprite
    private val run: Sprite
    private val hit: Sprite
    private val attack: Sprite
    private val runReversed: Sprite
    private val hitReversed: Sprite
    private val standReversed: Sprite
    private val deathReversed: Sprite
    private val attackReversed: Sprite
    var deathAnimation: Boolean = true
    private var spriteReversed: Boolean = false

    companion object {
        val spriteTime : Map<String, TimeSpan> = mapOf(
            "Stand" to .15.seconds,
            "Run" to .15.seconds,
            "Hit" to .15.seconds,
            "Death" to .1.seconds,
            "Attack" to .1.seconds,
        )
    }

    init {
        atlas[0].apply {
            attack = container.sprite(getSpriteAnimation("attack")).apply { removeFromParent() }
            stand = container.sprite(getSpriteAnimation("stand")).apply { alpha = .0; removeFromParent() }
            hit = container.sprite(getSpriteAnimation("hit")).apply { removeFromParent() }
            run = container.sprite(getSpriteAnimation("run")).apply { removeFromParent() }
            death = container.sprite(getSpriteAnimation("death")).apply {
                alpha = .0
                removeFromParent()
                onFrameChanged {
                    if (currentSpriteIndex == totalFrames - 1) {
                        if (deathAnimation) stopAnimation()
                        deathAnimation = !deathAnimation
                    }
                }
            }
        }
        atlas[1].apply {
            attackReversed = container.sprite(getSpriteAnimation("attack")).apply { removeFromParent() }
            standReversed = container.sprite(getSpriteAnimation("stand")).apply { removeFromParent() }
            hitReversed = container.sprite(getSpriteAnimation("hit")).apply { removeFromParent() }
            runReversed = container.sprite(getSpriteAnimation("run")).apply { removeFromParent() }
            deathReversed = container.sprite(getSpriteAnimation("death")).apply {
                removeFromParent()
                onFrameChanged {
                    if (currentSpriteIndex == totalFrames - 1) {
                        if (deathAnimation) stopAnimation()
                        deathAnimation = !deathAnimation
                    }
                }
            }
        }
        heroState = stand
    }

    fun spriteReverse(container: Container, state: String) {
        spriteReversed = !spriteReversed
        changeState(container, state)
    }

    fun changeState(container: Container, state: String) {
        val prevState = heroState
        prevState.apply { stopAnimation(); removeFromParent() }

        heroState = setHeroState(state)
        heroState.apply {
            addTo(container)
            pos = prevState.pos
            playAnimationLooped(spriteDisplayTime = spriteTime[state]!!)
        }
    }

    fun setHeroState(state: String): Sprite {
        return when(state) {
            "Stand" -> { if (spriteReversed) standReversed else stand }
            "Death" -> { if (spriteReversed) deathReversed else death }
            "Run" -> { if (spriteReversed) runReversed else run }
            "Hit" -> { if (spriteReversed) hitReversed else hit }
            "Attack" -> { if (spriteReversed) attackReversed else attack }
            else -> stand
        }
    }

    fun heroSpriteVisible() {
        stand.alpha = 1.0
        death.alpha = 1.0
    }

    fun reversed(): Boolean = spriteReversed
}
