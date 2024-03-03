import korlibs.event.*
import korlibs.image.color.*
import korlibs.image.format.*
import korlibs.korge.view.*
import korlibs.korge.view.align.*
import korlibs.korge.view.collision.*

class Controller {
    private val buttonStates: Array<Boolean> = Array(5) { false }
    private val buttonAreas: Array<Circle> = Array(5) {
        Circle(50.0).apply {
            color = RGBA(230, 230, 230)
            alpha = .7
        }
    }
    private lateinit var buttonImages: Array<Image>

    suspend fun controlPanelCreate(container: Container) {
        buttonImages = with(container) {
            arrayOf(
                image(KR.images.control.arrowLeft.__file.readBitmap()),
                image(KR.images.control.arrowRight.__file.readBitmap()),
                image(KR.images.control.arrowJump.__file.readBitmap()),
                image(KR.images.control.sword.__file.readBitmap()),
                image(KR.images.control.action.__file.readBitmap())
            )
        }

        for (i in buttonAreas.indices) {
            buttonAreas[i].apply {
                alignBottomToBottomOf(container)
                when(i) {
                    0 -> alignLeftToLeftOf(container)
                    1 -> alignLeftToRightOf(buttonAreas[0], 10)
                    2 -> alignRightToRightOf(container)
                    3 -> alignRightToRightOf(container, 10 + 2 * buttonAreas[2].radius)
                    4 -> alignRightToRightOf(container, 2 * (10 + 2 * buttonAreas[2].radius))
                }
                zIndex = 10.0
                addTo(container)
            }

            buttonImages[i].apply {
                scale = .125
                zIndex = 11.0
                centerOn(buttonAreas[i])
            }
        }
    }

    fun events(touches: List<Touch>): HeroEvent {
        touches.forEach { touch: Touch ->
            if (touch.isActive)
                for (i in buttonAreas.indices)
                    if (buttonAreas[i].collidesWith(Circle(1.0).position(touch.p)))
                        buttonStates[i] = true

        }

        buttonStates.forEachIndexed { index, state ->
            if (state) {
                buttonAreas[index].alpha = .4
                buttonImages[index].alpha = .7
            }
            else {
                buttonAreas[index].alpha = .7
                buttonImages[index].alpha = 1.0
            }
        }

        val event = if (buttonStates[4])
            HeroEvent.Action
        else if (buttonStates[3])
            HeroEvent.Attack
        else if (buttonStates[2])
            HeroEvent.Jump
        else if (buttonStates[1])
            HeroEvent.Right
        else if (buttonStates[0])
            HeroEvent.Left
        else
            HeroEvent.Idle

        for (i in buttonStates.indices)
            buttonStates[i] = false

        return event
    }
}
