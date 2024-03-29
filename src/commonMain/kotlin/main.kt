import scenesUI.*
import korlibs.korge.*
import korlibs.korge.scene.*

suspend fun main() = Korge {
    val sceneContainer = sceneContainer()
    sceneContainer.changeTo { MainScene() }
}
