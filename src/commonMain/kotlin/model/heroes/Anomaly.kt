package model.heroes

import korlibs.image.atlas.*
import korlibs.korge.view.*
import korlibs.math.geom.*

class Anomaly(atlas: Array<Atlas>, container: Container): Hero(atlas, container) {
    init {
        atlas.forEach {
            it.apply {
                heroStates.addAll(arrayOf(
                    container.sprite(getSpriteAnimation("run0003"), Anchor.CENTER).apply { removeFromParent() },
                    container.sprite(getSpriteAnimation("death0000"), Anchor.CENTER).apply { removeFromParent() }
                ))
            }
        }
    }
}
