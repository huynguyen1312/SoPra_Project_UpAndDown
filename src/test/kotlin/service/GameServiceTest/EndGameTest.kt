package service.gameServiceTest

import entity.Card
import entity.CardSuit
import entity.CardValue
import service.GameService
import service.PlayerActionService
import service.RootService
import service.TestRefreshable
import kotlin.test.*

/**
 * Test class for verifying the functionality of [GameService.endGame].
 */
class EndGameTest {
    private lateinit var rootService: RootService
    private lateinit var gameService: GameService
    private lateinit var playerActionService: PlayerActionService
    private lateinit var testRefreshable: TestRefreshable
    /**
     * Sets up the test environment before each test.
     */
    @BeforeTest
    fun setup() {
        rootService = RootService()
        gameService = GameService(rootService)
        playerActionService = PlayerActionService(rootService)
        testRefreshable = TestRefreshable()
        gameService.addRefreshable(testRefreshable)
        gameService.startGame(listOf("Huy", "DUc"))
    }

    /**
     * Verifies that the game ended and the current player won.
     */
    @Test
    fun `end Game with current player winning`() {
        val game = checkNotNull(rootService.currentGame)

        game.players[0].drawStack.clear()
        game.players[1].drawStack.clear()

        game.players[0].handCards.clear()
        game.players[1].handCards.clear()
        repeat(2) { game.players[1].handCards.add(Card(CardSuit.DIAMONDS, CardValue.THREE)) }

        game.passed = false
        val currentIndex = game.players.indexOf(game.currentPlayer)
        gameService.endGame()

        assertTrue(testRefreshable.refreshAfterGameEndCalled, "Game should be ended.")
        assertEquals(currentIndex, game.players.indexOf(testRefreshable.winner),
            "Game should be ended with player 1 as the winner.")
        assertFalse(testRefreshable.isDraw ?: true, "Game should not be a draw.")
    }

    /**
     * Verifies that the gam ended and the opponent won.
     */
    @Test
    fun `end Game with opponent player winning`() {
        val game = checkNotNull(rootService.currentGame)

        game.players[0].drawStack.clear()
        game.players[1].drawStack.clear()

        game.players[0].handCards.clear()
        repeat(3) { game.players[0].handCards.add(Card(CardSuit.DIAMONDS, CardValue.THREE)) }
        game.players[1].handCards.clear()
        repeat(1) { game.players[1].handCards.add(Card(CardSuit.DIAMONDS, CardValue.THREE)) }

        game.passed = true
        gameService.endGame()

        assertTrue(testRefreshable.refreshAfterGameEndCalled, "Game should be ended.")
        assertEquals(1 , game.players.indexOf(testRefreshable.winner),
            "Game should be ended with player 2 as the winner.")
        assertFalse(testRefreshable.isDraw ?: true, "Game should not be a draw.")
    }
    /**
     * Verifies that the game ended with a draw.
     */
    @Test
    fun `end Game with a draw`() {
        val game = checkNotNull(rootService.currentGame)

        game.players[0].drawStack.clear()
        game.players[1].drawStack.clear()

        game.players[0].handCards.clear()
        repeat(3) { game.players[0].handCards.add(Card(CardSuit.DIAMONDS, CardValue.THREE)) }
        game.players[1].handCards.clear()
        repeat(3) { game.players[1].handCards.add(Card(CardSuit.DIAMONDS, CardValue.THREE)) }

        game.passed = true
        gameService.endGame()

        assertTrue(testRefreshable.refreshAfterGameEndCalled, "Game should be ended.")
        assertTrue(testRefreshable.isDraw ?: true, "Game should be a draw.")

    }
}
