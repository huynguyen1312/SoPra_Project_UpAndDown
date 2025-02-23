package service.playerActionServiceTest

import entity.*
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import service.RootService

/**
 * Unit test for verifying the `canDrawCard` functionality in the PlayerActionService.
 *
 * This test ensures that:
 * - A player cannot draw a card if their draw stack is empty.
 * - A player can draw a card if their draw stack contains at least one card.
 * - A player cannot draw a card if they exceed the allowed hand size limit.
 */
class CanSwapCardTest {
    private lateinit var rootService: RootService // Root service instance used for testing

    // Player instances used for testing
    private lateinit var player1: Player
    private lateinit var player2: Player

    // Game instance used for testing
    private lateinit var game: UpAndDown

    /**
     * Sets up the test environment before each test execution.
     * Initializes `rootService`, creates players, and sets up a game.
     */
    @BeforeEach
    fun setUp() {
        rootService = RootService()
        player1 = Player("Huy")
        player2 = Player("Duc")
        rootService.currentGame = UpAndDown(player1, player2)
        game = checkNotNull(rootService.currentGame)
    }

    /**
     * Tests the `canDrawCard` method to determine whether a player is allowed to draw a card.
     *
     * Steps:
     * 1. If the player's draw stack is empty and the numbers of hand cards is smaller than 8,
     * they should not be able to draw a card.
     * 2. If at least one card is added to the draw stack, but the numbers of hand cards is still smaller than 8,
     * they should not be able to draw a card.
     * 3. f at least one card is added to the draw stack and the numbers of hand cards is greater than 8,
     * they should be able to draw a card.
     */
    @Test
    fun possibleOrImpossibleToSwapCard() {
        // Setup cards
        player1.handCards.add(Card(CardSuit.HEARTS, CardValue.TWO))
        player1.handCards.add(Card(CardSuit.SPADES, CardValue.ACE))
        player1.handCards.add(Card(CardSuit.CLUBS, CardValue.KING))
        player1.drawStack.isEmpty()

        assertFalse(rootService.playerActionService.canSwapCard())

        player1.handCards.add(Card(CardSuit.HEARTS, CardValue.FIVE))
        player1.handCards.add(Card(CardSuit.HEARTS, CardValue.SIX))
        player1.drawStack.add(Card(CardSuit.HEARTS, CardValue.THREE))

        assertFalse(rootService.playerActionService.canSwapCard())

        player1.handCards.add(Card(CardSuit.HEARTS, CardValue.SEVEN))
        player1.handCards.add(Card(CardSuit.HEARTS, CardValue.EIGHT))
        player1.handCards.add(Card(CardSuit.HEARTS, CardValue.NINE))

        assertTrue(rootService.playerActionService.canSwapCard())
    }
}
