package gui

import entity.Player
import service.RootService
import tools.aqua.bgw.components.layoutviews.Pane
import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.components.uicomponents.UIComponent
import tools.aqua.bgw.core.Alignment
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import java.awt.Color


/**
 * The result menu scene of the game.
 *
 * @param rootService The root service to which this scene belongs
 * @property contentPane This pane is used to hold all components of the scene and easily center them on the screen
 * @property label This label is used to display the tie game, the name of the winner
 * @property restartButton This button is used to restart the game
 */
class ResultMenuScene(val rootService: RootService) : MenuScene(1920, 1080), Refreshable {

    private val contentPane = Pane<UIComponent>(
        width = 700,
        height = 500,
        posX = 1920 / 2 - 700 / 2,
        posY = 1080 / 2 - 500 / 2,
        visual = ColorVisual(Color(0x0C2027))
    )


    private val label = Label(
        text = "",
        width = 600,
        height = 200,
        posX = 50,
        posY = 150,
        alignment = Alignment.CENTER,
        font = Font(45, Color(0xFFFFFFF), "JetBrains Mono ExtraBold"),
        visual = ColorVisual(Color(0x49585D))
    )

    private val restartButton = Button(
        text = "NEUSTART",
        width = 280,
        height = 60,
        posX = 700 / 2 - 280 / 2,
        posY = 390,
        font = Font(22, Color(0xFFFFFFF), "JetBrains Mono ExtraBold"),
        visual = ColorVisual(Color(0x49585D))
    ).apply{
        // When the button is clicked, restart the game
        onMouseClicked = {
            // Access the onAllRefreshables method of the game service to call the refreshAfterGameRestart method
            rootService.gameService.onAllRefreshables { refreshAfterGameRestart() }
        }
    }

    // Initialize the scene by setting the background color and adding all components to the content pane
    init {
        background = ColorVisual(Color(12, 32, 39, 240))
        contentPane.addAll(label, restartButton)
        addComponents(contentPane)
    }

    /**
     * The refreshAfterGameEnd method is called by the service layer after a game has ended.
     * It sets the name of the winner.
     *
     * @param winner The [Player] who has won the game
     */
    override fun refreshAfterGameEnd(winner: Player, isDraw: Boolean) {
        if (isDraw) {
            label.text = "UNENTSCHIEDEN!"
        }
        else {
            label.text = "${winner.name} hat gewonnen!"
        }

    }
}
