package heroes

import korlibs.image.atlas.*
import korlibs.korge.view.*

class VioletRose(atlas: Array<Atlas>, container: Container): Hero(atlas, container) {
    init {
        atlas.forEach {
            it.apply {
                heroStates.addAll(arrayOf(
                    container.sprite(getSpriteAnimation("jump")).apply { removeFromParent() },
                    container.sprite(getSpriteAnimation("fall")).apply { removeFromParent() }
                ))
            }
        }
    }
}
