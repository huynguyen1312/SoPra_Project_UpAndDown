package service.GameServiceTest

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import service.RootService

/**
 * Unit test for verifying the `endTurn` functionality in the game service.
 *
 * This test ensures that:
 * - The game is properly initialized before each test.
 * - The `endTurn` function correctly resets the `passed` flag in the `UpAndDown` game.
 */
class EndTurnTest {
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
     * Tests whether the `endTurn` function correctly resets the `passed` flag.
     *
     * This test performs the following steps:
     * 1. Retrieves the current game instance.
     * 2. Manually sets the `passed` flag to `false`.
     * 3. Calls `endTurn` to check if the `passed` flag is reset to `true`.
     * 4. Asserts that the `passed` flag is indeed `true` after ending the turn.
     */
    @Test
    fun resetPassedFlagTest() {
        val game = checkNotNull(rootService.currentGame){ "Game should be initialized" }

        game.passed = false // Manually set passed to true
        rootService.gameService.endTurn()

        assertTrue(game.passed, "Game passed flag should be reset to true")
    }

}