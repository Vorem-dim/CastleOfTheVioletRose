import korlibs.korge.scene.*
import korlibs.korge.view.*


class LevelInspector(private val settings: Settings): Scene() {

    override suspend fun SContainer.sceneInit() {

    }

    override suspend fun SContainer.sceneMain() {
        when(settings.currentLevel) {
            0 -> level0()
            1 -> level1()
            2 -> level2()
        }
    }

    private suspend fun level0() {

    }

    private suspend fun level1() {

    }

    private suspend fun level2() {

    }
}
