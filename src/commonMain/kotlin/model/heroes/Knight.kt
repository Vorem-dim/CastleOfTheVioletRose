package model.heroes

import korlibs.image.atlas.*
import korlibs.korge.view.*
import korlibs.math.geom.*

class Knight(atlas: Array<Atlas>, container: Container): Hero(atlas, container) {
    init {
        atlas.forEach {
            it.apply {
                heroStates.addAll(arrayOf(
                    container.sprite(getSpriteAnimation("jump"), Anchor.CENTER).apply { removeFromParent() },
                    container.sprite(getSpriteAnimation("fall"), Anchor.CENTER).apply { removeFromParent() }
                ))
            }
        }
    }
}
