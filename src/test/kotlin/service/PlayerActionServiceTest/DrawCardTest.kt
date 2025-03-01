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
 * Test the functionality of [PlayerActionService.drawCard].
 */
class DrawCardTest {
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
        testRefreshable = TestRefreshable()
        gameService.addRefreshable(testRefreshable)
        rootService.addRefreshable(testRefreshable)
        playerActionService = PlayerActionService(rootService)
        playerActionService.addRefreshable(testRefreshable)
        gameService.startGame(listOf("Huy", "Duc"))
    }

    /**Test try to draw without a game running*/
    @Test
    fun `calling draw without a game`() {
        val rootService = RootService()
        val playerActionService = PlayerActionService(rootService)

        val ex = assertFailsWith<IllegalStateException> {
            playerActionService.drawCard()
        }

        assertTrue(ex.message?.contains("No game currently running.") == true)
    }
    /**
     * Verifies that the player can draw a card.
     */
    @Test
    fun `draw a Card with less then 10 hand cards and nonempty draw stack`() {
        val game = checkNotNull(rootService.currentGame)
        val currentPlayer = game.currentPlayer

        currentPlayer.handCards.clear()
        repeat(5) { currentPlayer.handCards.add(Card(CardSuit.HEARTS, CardValue.FIVE)) }
        currentPlayer.drawStack.clear()
        currentPlayer.drawStack.add(Card(CardSuit.CLUBS, CardValue.THREE))
        currentPlayer.drawStack.add(Card(CardSuit.DIAMONDS, CardValue.SEVEN))

        val initialHandSize = currentPlayer.handCards.size
        val initialDrawStackSize = currentPlayer.drawStack.size
        val initialPlayerIndex = game.players.indexOf(game.currentPlayer)

        playerActionService.drawCard()

        assertEquals((initialPlayerIndex + 1) % 2, game.players.indexOf(game.currentPlayer),
            "Turn should be passed to the next player.")
        assertTrue(testRefreshable.refreshAfterDrawCardCalled, "Refresh should be called.")
        assertEquals(initialHandSize + 1, currentPlayer.handCards.size, "Hand should have 1 more card.")
        assertEquals(initialDrawStackSize - 1, currentPlayer.drawStack.size, "Draw stack should have 1 less card.")
        assertFalse(game.passed, "Game passed should be false.")
    }

    /**
     * Verifies that the player can not draw a card with more than 9 hand cards.
     */
    @Test
    fun `draw a card with more then 9 hand cards`() {
        val game = checkNotNull(rootService.currentGame)
        val currentPlayer = game.currentPlayer
        testRefreshable.refreshAfterDrawCardCalled = false

        currentPlayer.handCards.clear()
        repeat(10) { currentPlayer.handCards.add(Card(CardSuit.HEARTS, CardValue.FIVE)) }
        currentPlayer.drawStack.clear()
        currentPlayer.drawStack.add(Card(CardSuit.CLUBS, CardValue.THREE))
        currentPlayer.drawStack.add(Card(CardSuit.DIAMONDS, CardValue.SEVEN))

        val initialHandSize = currentPlayer.handCards.size
        val initialDrawStackSize = currentPlayer.drawStack.size
        val initialPlayerIndex = game.players.indexOf(game.currentPlayer)

        playerActionService.drawCard()

        assertEquals(initialHandSize, currentPlayer.handCards.size, "Hand should have 0 more card.")
        assertEquals(initialPlayerIndex, game.players.indexOf(game.currentPlayer),
            "Turn should not be passed to the next player.")
        assertFalse(testRefreshable.refreshAfterDrawCardCalled, "Refresh should NOT be called.")
        assertEquals(initialDrawStackSize, currentPlayer.drawStack.size, "Draw stack should have 0 less card.")
    }

    /**
     * Verifies that the player can not draw a card from an empty draw stack.
     */
    @Test
    fun `draw a card with empty draw stack`() {
        val game = checkNotNull(rootService.currentGame)
        val currentPlayer = game.currentPlayer
        testRefreshable.refreshAfterDrawCardCalled = false

        currentPlayer.handCards.clear()
        repeat(5) { currentPlayer.handCards.add(Card(CardSuit.HEARTS, CardValue.FIVE)) }
        currentPlayer.drawStack.clear()

        val initialHandSize = currentPlayer.handCards.size
        val initialDrawStackSize = currentPlayer.drawStack.size
        val initialPlayerIndex = game.players.indexOf(game.currentPlayer)

        playerActionService.drawCard()

        assertFalse(testRefreshable.refreshAfterDrawCardCalled, "Refresh should NOT be called.")
        assertEquals(initialHandSize, currentPlayer.handCards.size, "Hand should have 0 more card.")
        assertEquals(initialDrawStackSize, currentPlayer.drawStack.size, "Draw stack should have 0 less card.")
        assertEquals(initialPlayerIndex, game.players.indexOf(game.currentPlayer),
            "Turn should not be passed to the next player.")
    }
}
