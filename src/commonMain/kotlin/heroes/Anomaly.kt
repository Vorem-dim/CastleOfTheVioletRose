package heroes

import korlibs.image.atlas.*
import korlibs.korge.view.*

class Anomaly(atlas: Array<Atlas>, container: Container): Hero(atlas, container) {
    init {
        atlas.forEach {
            it.apply {
                heroStates.addAll(arrayOf(
                    container.sprite(getSpriteAnimation("run0003")).apply { removeFromParent() },
                    container.sprite(getSpriteAnimation("death0000")).apply { removeFromParent() }
                ))
            }
        }
    }
}
