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
 * Test the functionality of [PlayerActionService.swapCard].
 */
class SwapCardTest {
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

    /**Test try to swap without a game running*/
    @Test
    fun `calling swap without a game`() {
        val rootService = RootService()
        val playerActionService = PlayerActionService(rootService)
        val ex = assertFailsWith<IllegalStateException> {
            playerActionService.swapCard()
        }
        assertTrue(ex.message?.contains("No game currently running.") == true)
    }

    /**
     * Verifies that the player can swap cards.
     */
    @Test
    fun `swap cards with more then 8 hand cards and nonempty draw stack`() {
        val game = checkNotNull(rootService.currentGame)
        val currentPlayer = game.currentPlayer

        currentPlayer.handCards.clear()
        repeat(9) { currentPlayer.handCards.add(Card(CardSuit.HEARTS, CardValue.FIVE)) }
        currentPlayer.drawStack.clear()
        currentPlayer.drawStack.add(Card(CardSuit.CLUBS, CardValue.THREE))
        currentPlayer.drawStack.add(Card(CardSuit.DIAMONDS, CardValue.SEVEN))

        val initialHandSize = currentPlayer.handCards.size
        val initialDrawStackSize = currentPlayer.drawStack.size
        val initialPlayerIndex = game.players.indexOf(game.currentPlayer)

        playerActionService.swapCard()

        assertFalse(game.passed, "Game passed should be false.")
        assertEquals((initialPlayerIndex + 1) % 2, game.players.indexOf(game.currentPlayer),
            "Turn should be passed to the next player.")
        assertTrue(testRefreshable.refreshAfterSwapCardsCalled, "Refresh should be called.")
        assertEquals(5, currentPlayer.handCards.size, "Hand should have 5 cards.")
        assertEquals(
            initialDrawStackSize + initialHandSize - 5,
            currentPlayer.drawStack.size,
            "Draw stack should have 5 less card."
        )
    }

    /**
     * Verifies that the player can not swap cards with less than 8 hand cards.
     */
    @Test
    fun `try to swap cards with less than 8 hand cards`() {
        val game = checkNotNull(rootService.currentGame)
        val currentPlayer = game.currentPlayer
        testRefreshable.refreshAfterSwapCardsCalled = false

        currentPlayer.handCards.clear()
        repeat(7) { currentPlayer.handCards.add(Card(CardSuit.HEARTS, CardValue.FIVE)) }
        currentPlayer.drawStack.clear()
        currentPlayer.drawStack.add(Card(CardSuit.CLUBS, CardValue.THREE))
        currentPlayer.drawStack.add(Card(CardSuit.DIAMONDS, CardValue.SEVEN))

        val initialHandSize = currentPlayer.handCards.size
        val initialDrawStackSize = currentPlayer.drawStack.size
        val initialPlayerIndex = game.currentPlayer

        playerActionService.swapCard()

        assertEquals(initialPlayerIndex, game.currentPlayer, "Turn should not be passed to the next player.")
        assertFalse(testRefreshable.refreshAfterSwapCardsCalled, "Refresh should NOT be called.")
        assertEquals(initialHandSize, currentPlayer.handCards.size, "Hand should have 0 more card.")
        assertEquals(initialDrawStackSize, currentPlayer.drawStack.size, "Draw stack should have 0 less card.")
    }

    /**
     * Verifies that the player can not swap cards with an empty draw stack.
     */
    @Test
    fun `swap cards with empty draw stack`() {
        val game = checkNotNull(rootService.currentGame)
        val currentPlayer = game.currentPlayer
        testRefreshable.refreshAfterSwapCardsCalled = false

        currentPlayer.handCards.clear()
        repeat(5) { currentPlayer.handCards.add(Card(CardSuit.HEARTS, CardValue.FIVE)) }
        currentPlayer.drawStack.clear()

        val initialHandSize = currentPlayer.handCards.size
        val initialDrawStackSize = currentPlayer.drawStack.size
        val initialPlayerIndex = game.currentPlayer

        playerActionService.swapCard()

        assertEquals(initialDrawStackSize, currentPlayer.drawStack.size, "Draw stack should have 0 less card.")
        assertEquals(initialPlayerIndex, game.currentPlayer, "Turn should not be passed to the next player.")
        assertFalse(testRefreshable.refreshAfterSwapCardsCalled, "Refresh should NOT be called.")
        assertEquals(initialHandSize, currentPlayer.handCards.size, "Hand should have 0 more card.")

    }

}
