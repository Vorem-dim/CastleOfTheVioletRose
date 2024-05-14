package model.heroes

import korlibs.image.atlas.*
import korlibs.korge.view.*
import korlibs.math.geom.*

class Thief(atlas: Array<Atlas>, container: Container): Hero(atlas, container) {

    init {
        atlas.forEach {
            it.apply {
                heroStates.addAll(arrayOf(
                    container.sprite(getSpriteAnimation("run0004"), Anchor.CENTER).apply { removeFromParent() },
                    container.sprite(getSpriteAnimation("run0006"), Anchor.CENTER).apply { removeFromParent() }
                ))
            }
        }
    }
}
