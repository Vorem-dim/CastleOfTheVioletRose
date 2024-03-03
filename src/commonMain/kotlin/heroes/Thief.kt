package heroes

import korlibs.image.atlas.*
import korlibs.korge.view.*

class Thief(atlas: Array<Atlas>, container: Container): Hero(atlas, container) {

    init {
        atlas.forEach {
            it.apply {
                heroStates.addAll(arrayOf(
                    container.sprite(getSpriteAnimation("run0004")).apply { removeFromParent() },
                    container.sprite(getSpriteAnimation("run0006")).apply { removeFromParent() }
                ))
            }
        }
    }
}
