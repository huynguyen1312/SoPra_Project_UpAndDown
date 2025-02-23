package service.GameServiceTest


import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import service.RootService

/**
 * Unit test for verifying the `nextTurn` functionality in the game service.
 *
 * This test ensures that:
 * - The turn switches correctly between players.
 * - The `passed` flag is reset when a new turn begins.
 * - An error is thrown when `nextTurn` is called without an active game.
 */
class NextTurnTest {
    private lateinit var rootService: RootService // Root service instance used for testing

    /**
     * Sets up the test environment before each test execution.
     * Initializes the `rootService` and starts a game with two players.
     */
    @BeforeEach
    fun setUp() {
        rootService = RootService()
        rootService.gameService.startGame("Huy", "Duc") // Start a game before each test
    }

    /**
     * Tests whether calling `nextTurn` correctly switches the turn to the other player.
     *
     * Steps:
     * 1. Retrieves the current game instance.
     * 2. Stores the initial current player.
     * 3. Calls `nextTurn`.
     * 4. Retrieves the new current player.
     * 5. Asserts that the new player is different from the initial player.
     * 6. Ensures that the new player is one of the two game players.
     */
    @Test
    fun switchTurnTest() {
        val game = checkNotNull(rootService.currentGame){ "Game should be initialized" }

        val initialPlayer = game.currentPlayer

        rootService.gameService.nextTurn() // Call nextTurn

        val newPlayer = game.currentPlayer

        assertNotEquals(initialPlayer, newPlayer, "Turn should switch to the other player")
        assertTrue(newPlayer == game.players[1] || newPlayer == game.players[0], "New player should be one of the players")
    }

    /**
     * Tests whether the `passed` flag is reset when a new turn begins.
     *
     * Steps:
     * 1. Retrieves the current game instance.
     * 2. Manually sets the `passed` flag to `true`.
     * 3. Calls `nextTurn`.
     * 4. Asserts that the `passed` flag is reset to `false`.
     */
    @Test
    fun resetPassedFlagTest() {
        val game = checkNotNull(rootService.currentGame){ "Game should be initialized" }

        game.passed = true // Manually set passed to true
        rootService.gameService.nextTurn()

        assertFalse(game.passed, "Game passed flag should be reset to false")
    }

    /**
     * Tests whether calling `nextTurn` when no game is running throws an error.
     *
     * Steps:
     * 1. Sets `currentGame` to `null` to simulate no game running.
     * 2. Calls `nextTurn` and expects an `IllegalStateException`.
     * 3. Ensures the exception message contains the expected error text.
     */
    @Test
    fun noGameRunningTest() {
        rootService.currentGame = null

        val exception = assertThrows<IllegalStateException> {
            rootService.gameService.nextTurn()
        }

        assertTrue(exception.message!!.contains("No game currently running"), "Should throw error when no game is running")
    }
}