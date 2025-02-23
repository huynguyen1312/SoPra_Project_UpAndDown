package service.gameServiceTest

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import service.GameService
import service.PlayerActionService
import service.RootService
import kotlin.test.BeforeTest

/**
 * Unit test for verifying the `endTurn` functionality in the game service.
 *
 * This test ensures that:
 * - The game is properly initialized before each test.
 * - The `endTurn` function correctly resets the `passed` flag in the `UpAndDown` game.
 */
class EndTurnTest {
    private lateinit var rootService: RootService
    private lateinit var gameService: GameService
    private lateinit var playerActionService: PlayerActionService

    /**
     * Sets up the test environment before each test.
     * Initializes [RootService], [GameService], and [PlayerActionService], and starts a new game.
     */
    @BeforeTest
    fun setup() {
        rootService = RootService()
        gameService = GameService(rootService)
        playerActionService = PlayerActionService(rootService)
        gameService.startGame(listOf("Huy", "DUc"))
    }

    /**
     * Verifies that the turn was ended and the player switched.
     */
    @Test
    fun `end the turn and switch the current player`() {
        val game = checkNotNull(rootService.currentGame)
        val currentIndex = game.players.indexOf(game.currentPlayer)

        gameService.endTurn()

        assertEquals((currentIndex + 1) % 2, game.players.indexOf(game.currentPlayer),
            "Current player should have changed.")
    }

    /**
     * Verifies that after two turns the same player is dran.
     */
    @Test
    fun `two end turns should not switch the first player`() {
        val game = checkNotNull(rootService.currentGame)
        val initialPlayer = game.currentPlayer

        gameService.endTurn()
        gameService.endTurn()

        assertEquals(initialPlayer, game.currentPlayer, "Current player should be the same.")
    }
}
