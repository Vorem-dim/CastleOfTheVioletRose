import korlibs.image.atlas.*
import korlibs.korge.view.*

class Hero(atlas: Atlas, container: Container) {
    val stand: Sprite
    val run: Sprite
    val attack: Sprite
    val hit: Sprite
    val death: Sprite
    private var deathAnimation: Boolean = true
    init {
        atlas.apply {
            stand = container.sprite(getSpriteAnimation("stand")).apply { alpha = .0 }
            run = container.sprite(getSpriteAnimation("run")).apply { alpha = .0 }
            attack = container.sprite(getSpriteAnimation("attack")).apply { alpha = .0 }
            hit = container.sprite(getSpriteAnimation("hit")).apply { alpha = .0 }
            death = container.sprite(getSpriteAnimation("death")).apply {
                alpha = .0
                onFrameChanged {
                    if (currentSpriteIndex == totalFrames - 1) {
                        if (deathAnimation) stopAnimation()
                        deathAnimation = !deathAnimation
                    }
                }
            }
        }
    }
}
