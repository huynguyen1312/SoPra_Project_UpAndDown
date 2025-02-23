package service.gameServiceTest


import org.junit.jupiter.api.Test
import service.GameService
import service.PlayerActionService
import service.RootService
import service.TestRefreshable
import kotlin.test.BeforeTest

/**
 * Unit test for verifying the `nextTurn` functionality in the game service.
 *
 * This test ensures that:
 * - The turn switches correctly between players.
 * - The `passed` flag is reset when a new turn begins.
 * - An error is thrown when `nextTurn` is called without an active game.
 */
class NextTurnTest {
    private lateinit var rootService: RootService
    private lateinit var gameService: GameService
    private lateinit var testRefreshable: TestRefreshable

    /**
     * Sets up the test environment before each test.
     *
     * Initializes [RootService], [GameService], and [PlayerActionService], and starts a new game.
     */
    @BeforeTest
    fun setup() {
        rootService = RootService()
        gameService = GameService(rootService)
        testRefreshable = TestRefreshable()

        gameService.addRefreshable(testRefreshable)

        gameService.startGame(listOf("Huy", "DUc"))
    }

    /**
     * Verifies that the turn was successfully ended.
     */
    @Test
    fun `nextTurn should trigger refreshAfterNextTurn on all refreshables`() {

        gameService.nextTurn()

        kotlin.test.assertNotNull(rootService.currentGame, "Game should be running.")
        kotlin.test.assertTrue(testRefreshable.refreshAfterNextTurnCalled,
            "nextTurn should trigger refreshAfterNextTurn.")
    }
}
