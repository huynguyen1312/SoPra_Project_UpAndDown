package service.playerActionServiceTest

import entity.Card
import entity.CardSuit
import entity.CardValue
import service.GameService
import service.PlayerActionService
import service.RootService
import service.TestRefreshable
import kotlin.test.*

/**
 * Test the functionality of [PlayerActionService.pass].
 */
class PassTest {
    private lateinit var rootService: RootService
    private lateinit var gameService: GameService
    private lateinit var playerActionService: PlayerActionService
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
        playerActionService = PlayerActionService(rootService)

        testRefreshable = TestRefreshable()
        gameService.addRefreshable(testRefreshable)
        playerActionService.addRefreshable(testRefreshable)
        rootService.addRefreshable(testRefreshable)

        gameService.startGame(listOf("Huy", "Duc"))
    }

    /**Test try to pass without a game running*/
    @Test
    fun `calling pass without a game`() {
        val rootService = RootService()
        val playerActionService = PlayerActionService(rootService)
        val ex = assertFailsWith<IllegalStateException> {
            playerActionService.pass()
        }
        assertTrue(ex.message?.contains("No game currently running.") == true)
    }
    /**Test a simple pass*/
    @Test
    fun `player passes his turn`() {
        val game = checkNotNull(rootService.currentGame)
        game.players[0].drawStack.clear()
        game.players[1].drawStack.clear()
        game.centerStack1.clear()
        game.centerStack1.add(Card(CardSuit.HEARTS, CardValue.FIVE))
        game.centerStack2.clear()
        game.centerStack2.add(Card(CardSuit.CLUBS, CardValue.NINE))
        game.players[0].handCards.clear()
        repeat(3) { game.players[0].handCards.add(Card(CardSuit.SPADES, CardValue.TWO)) }
        game.players[1].handCards.clear()
        repeat(2) { game.players[1].handCards.add(Card(CardSuit.DIAMONDS, CardValue.THREE)) }
        game.passed = false
        playerActionService.pass()

        assertTrue(testRefreshable.refreshAfterPassCalled, "Refresh should be called.")
        assertTrue(game.players.indexOf(game.currentPlayer) == 1,
            "Current player should be the next player.")
        assertTrue(game.passed, "Game should know that last move was passed.")
    }

    /**
     * Verifies that two passes end the game and decided the winner by hand size.
     */
    @Test
    fun `two consecutive passes end game with winner determined by hand size`() {
        val game = checkNotNull(rootService.currentGame)
        game.players[0].drawStack.clear()
        game.players[1].drawStack.clear()
        game.centerStack1.clear()
        game.centerStack1.add(Card(CardSuit.HEARTS, CardValue.FIVE))
        game.centerStack2.clear()
        game.centerStack2.add(Card(CardSuit.CLUBS, CardValue.NINE))
        game.players[0].handCards.clear()
        repeat(3) { game.players[0].handCards.add(Card(CardSuit.SPADES, CardValue.TWO)) }
        game.players[1].handCards.clear()
        repeat(2) { game.players[1].handCards.add(Card(CardSuit.DIAMONDS, CardValue.THREE)) }
        game.passed = false
        playerActionService.pass()
        playerActionService.pass()

        assertEquals(1, game.players.indexOf(testRefreshable.winner),
            "Game should be ended with player 1 as the winner.")
        assertTrue(testRefreshable.refreshAfterSwapCardsCalled, "Game should be ended.")
        assertFalse(testRefreshable.isDraw ?: true, "Game should not be a draw.")
    }

    /**
     * Verifies that two passes end the game with a draw if the hand sizes are equal.
     */
    @Test
    fun `two consecutive passes end game with draw when hand sizes are equal`() {
        val game = checkNotNull(rootService.currentGame)

        game.players[0].drawStack.clear()
        game.players[1].drawStack.clear()
        game.centerStack1.clear()
        game.centerStack1.add(Card(CardSuit.HEARTS, CardValue.FIVE))
        game.centerStack2.clear()
        game.centerStack2.add(Card(CardSuit.CLUBS, CardValue.NINE))
        game.players[0].handCards.clear()
        repeat(3) { game.players[0].handCards.add(Card(CardSuit.SPADES, CardValue.TWO)) }
        game.players[1].handCards.clear()
        repeat(3) { game.players[1].handCards.add(Card(CardSuit.DIAMONDS, CardValue.THREE)) }

        game.passed = false

        playerActionService.pass()
        playerActionService.pass()

        assertTrue(testRefreshable.refreshAfterPassCalled, "Game should be ended.")
        assertEquals(1, game.players.indexOf(testRefreshable.winner),
            "Winner should be current player.")
        assertTrue(testRefreshable.isDraw ?: false, "Game should be a draw.")
    }
}
