package gui

import entity.Player
import service.RootService
import tools.aqua.bgw.core.BoardGameApplication
import tools.aqua.bgw.util.Font
import java.io.File
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip

/**
 * Implementation of the BGW [BoardGameApplication] for the game UpAndDown
 * @property rootService The root service to which this scene belongs
 * @property gameScene Scene for the actual game.
 * @property mainMenuScene Represents the menu of the 'currentGame'
 * @property resultMenuScene The result menu scene of the game.
 */
class UpAndDownApplication : BoardGameApplication("UpAndDown"), Refreshable {

    // Create a new instance of the root service
    private val rootService = RootService()

    // Create the game and menu scenes and pass them the root service
    private val gameScene = GameScene(rootService)
    private val mainMenuScene = MainMenuScene(rootService)
    private val resultMenuScene = ResultMenuScene(rootService)

    // Initialize the application by loading the fonts, adding refreshables and setting the initial scene
    init {
        // Load the "Staatliches" font from the resources
        loadFont("Staatliches-Regular.ttf", "Staatliches", Font.FontWeight.NORMAL)

        // Load the "JetBrains Mono" font from the resources
        loadFont("JetBrainsMono-ExtraBold.ttf", "JetBrains Mono ExtraBold", Font.FontWeight.EXTRA_BOLD)

        // Register refreshables for the application and every scene
        rootService.addRefreshables(
            this,
            mainMenuScene,
            resultMenuScene,
            gameScene
        )

        // Set the initial scene to the main menu
        this.showGameScene(gameScene)
        this.showMenuScene(mainMenuScene)
        // Play Music
        val resource = object {}.javaClass.getResource("/playback.wav")
        val audioFile = File(resource.toURI())
        val audioStream = AudioSystem.getAudioInputStream(audioFile)
        val clip = AudioSystem.getClip()
        clip.open(audioStream)
        clip.loop(Clip.LOOP_CONTINUOUSLY)
        clip.start()
    }

    /**
     * The refreshAfterGameStart method is called by the service layer after a game has started.
     * It hides the menu scene after a short delay.
     */
    override fun refreshAfterStartNewGame() {
        hideMenuScene(500)
    }

    /**
     * The refreshAfterGameEnd method is called by the service layer after a game has ended.
     * It shows the result menu scene.
     *
     * @param winner The player who won the game
     */
    override fun refreshAfterGameEnd(winner: Player, isDraw: Boolean) {
        showMenuScene(resultMenuScene)
    }
    /**
     * The refreshAfterGameRestart method is called by the service layer after a game has been restarted.
     * It shows the main menu scene.
     */
    override fun refreshAfterGameRestart() {
        showMenuScene(mainMenuScene)
    }
}

