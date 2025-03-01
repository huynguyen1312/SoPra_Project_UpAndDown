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
 * Test the functionality of [PlayerActionService.playCard].
 */
class PlayCardTest {
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

    /**Test try to play a card without a game running*/
    @Test
    fun `calling play card without a game`() {
        val rootService = RootService()
        val playerActionService = PlayerActionService(rootService)

        val ex = assertFailsWith<IllegalStateException> {
            playerActionService.playCard(Card(CardSuit.CLUBS, CardValue.QUEEN), 1)
        }

        assertTrue(ex.message?.contains("No game currently running." ) == true)
    }
    /**
     * Verifies that the player can play a legal move.
     */
    @Test
    fun `play a valid card on play stack 1 one rank lower`() {
        val game = checkNotNull(rootService.currentGame)
        val player = game.currentPlayer
        game.passed = true

        game.centerStack1.clear()
        game.centerStack1.add(Card(CardSuit.CLUBS, CardValue.ACE))

        game.centerStack2.clear()
        game.centerStack2.add(Card(CardSuit.SPADES, CardValue.JACK))

        player.handCards.clear()
        player.handCards.add(Card(CardSuit.SPADES, CardValue.KING))
        player.handCards.add(Card(CardSuit.SPADES, CardValue.FIVE))

        playerActionService.playCard(Card(CardSuit.SPADES, CardValue.KING), 0)

        assertFalse(
            player.handCards.contains(Card(CardSuit.SPADES, CardValue.KING)),
            "Played card should not be in hand cards anymore."
        )
        assertFalse(game.passed, "Passed should be false.")
        assertTrue(testRefreshable.refreshAfterPlayCardCalled, "Refresh should be called.")
        assertEquals(Card(CardSuit.SPADES, CardValue.KING), game.centerStack1.last(), "Card should be on play stack.")
        assertNotSame(game.currentPlayer, player, "Current player should have switched.")
    }
    /**
     * Verifies that the player can play a legal move.
     */
    @Test
    fun `play a valid card on play stack 1 one rank higher`() {
        val game = checkNotNull(rootService.currentGame)
        val player = game.currentPlayer
        game.passed = true

        game.centerStack1.clear()
        game.centerStack1.add(Card(CardSuit.CLUBS, CardValue.QUEEN))

        game.centerStack2.clear()
        game.centerStack2.add(Card(CardSuit.SPADES, CardValue.JACK))

        player.handCards.clear()
        player.handCards.add(Card(CardSuit.SPADES, CardValue.KING))
        player.handCards.add(Card(CardSuit.SPADES, CardValue.FIVE))

        playerActionService.playCard(Card(CardSuit.SPADES, CardValue.KING), 0)

        assertFalse(
            player.handCards.contains(Card(CardSuit.SPADES, CardValue.KING)),
            "Played card should not be in hand cards anymore."
        )
        assertNotSame(game.currentPlayer, player, "Current player should have switched.")
        assertFalse(game.passed, "Passed should be false.")
        assertTrue(testRefreshable.refreshAfterPlayCardCalled, "Refresh should be called.")
        assertEquals(Card(CardSuit.SPADES, CardValue.KING), game.centerStack1.last(), "Card should be on play stack.")
    }

    /**
     * Verifies that the player can play a legal move on the other stack.
     */
    @Test
    fun `play a valid card on play stack 2 two ranks higher`() {
        val game = checkNotNull(rootService.currentGame)
        val player = game.currentPlayer
        game.passed = true

        game.centerStack1.clear()
        game.centerStack1.add(Card(CardSuit.CLUBS, CardValue.ACE))

        game.centerStack2.clear()
        game.centerStack2.add(Card(CardSuit.SPADES, CardValue.JACK))

        player.handCards.clear()
        player.handCards.add(Card(CardSuit.SPADES, CardValue.KING))
        player.handCards.add(Card(CardSuit.SPADES, CardValue.FIVE))

        playerActionService.playCard(Card(CardSuit.SPADES, CardValue.KING), 1)

        assertFalse(
            player.handCards.contains(Card(CardSuit.SPADES, CardValue.KING)),
            "Played card should not be in hand cards anymore."
        )
        assertNotSame(game.currentPlayer, player, "Current player should have switched.")
        assertFalse(game.passed, "Passed should be false.")
        assertTrue(testRefreshable.refreshAfterPlayCardCalled, "Refresh should be called.")
        assertEquals(Card(CardSuit.SPADES, CardValue.KING), game.centerStack2.last(), "Card should be on play stack.")
    }

    /**
     * Verifies that the player can play a legal move on the second stack with two ranks lower.
     */
    @Test
    fun `play a valid card on play stack 2 two ranks lower`() {
        val game = checkNotNull(rootService.currentGame)
        val player = game.currentPlayer
        game.passed = true

        game.centerStack1.clear()
        game.centerStack1.add(Card(CardSuit.CLUBS, CardValue.ACE))

        game.centerStack2.clear()
        game.centerStack2.add(Card(CardSuit.SPADES, CardValue.TWO))

        player.handCards.clear()
        player.handCards.add(Card(CardSuit.SPADES, CardValue.KING))
        player.handCards.add(Card(CardSuit.SPADES, CardValue.FIVE))

        playerActionService.playCard(Card(CardSuit.SPADES, CardValue.KING), 1)

        assertFalse(
            player.handCards.contains(Card(CardSuit.SPADES, CardValue.KING)),
            "Played card should not be in hand cards anymore."
        )
        assertEquals(Card(CardSuit.SPADES, CardValue.KING), game.centerStack2.last(), "Card should be on play stack.")
        assertFalse(game.passed, "Passed should be false.")
        assertTrue(testRefreshable.refreshAfterPlayCardCalled, "Refresh should be called.")
        assertNotSame(game.currentPlayer, player, "Current player should have switched.")
    }


    /**
     * Verifies that the player can play his last card and the game should end.
     */
    @Test
    fun `play a valid last card`() {
        val game = checkNotNull(rootService.currentGame)
        val player = game.currentPlayer
        game.passed = true

        game.centerStack1.clear()
        game.centerStack1.add(Card(CardSuit.CLUBS, CardValue.ACE))

        game.centerStack2.clear()
        game.centerStack2.add(Card(CardSuit.SPADES, CardValue.JACK))

        player.handCards.clear()
        player.drawStack.clear()
        player.handCards.add(Card(CardSuit.SPADES, CardValue.KING))

        playerActionService.playCard(Card(CardSuit.SPADES, CardValue.KING), 0)

        assertFalse(
            player.handCards.contains(Card(CardSuit.SPADES, CardValue.KING)),
            "Played card should not be in hand cards anymore."
        )
        assertSame(game.currentPlayer, player, "Current player should NOT have switched.")
        assertFalse(game.passed, "Passed should be false.")
        assertTrue(testRefreshable.refreshAfterPlayCardCalled, "Refresh should be called.")
        assertEquals(Card(CardSuit.SPADES, CardValue.KING), game.centerStack1.last(), "Card should be on play stack.")
    }

    /**
     * Verifies that the player can not play an illegal move.
     */
    @Test
    fun `play an invalid card`() {
        val game = checkNotNull(rootService.currentGame)
        val player = game.currentPlayer
        game.passed = true
        testRefreshable.refreshAfterSwapCardsCalled = false

        game.centerStack1.clear()
        game.centerStack1.add(Card(CardSuit.CLUBS, CardValue.ACE))

        game.centerStack2.clear()
        game.centerStack2.add(Card(CardSuit.SPADES, CardValue.JACK))

        player.handCards.clear()
        player.handCards.add(Card(CardSuit.SPADES, CardValue.KING))
        player.handCards.add(Card(CardSuit.HEARTS, CardValue.FIVE))

        playerActionService.playCard(Card(CardSuit.HEARTS, CardValue.FIVE), 0)

        assertTrue(
            player.handCards.contains(Card(CardSuit.HEARTS, CardValue.FIVE)),
            "Played card should still be in hand cards anymore."
        )
        assertNotEquals(
            Card(CardSuit.HEARTS, CardValue.FIVE), game.centerStack1.last(), "Card should not be on play stack."
        )
        assertSame(game.currentPlayer, player, "Current player should NOT have switched.")
        assertTrue(game.passed, "Passed should still be true.")
        assertFalse(testRefreshable.refreshAfterPlayCardCalled, "Refresh should NOT be called.")
    }
}
