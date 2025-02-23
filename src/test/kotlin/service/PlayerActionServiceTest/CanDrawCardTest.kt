package service.PlayerActionServiceTest

import entity.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import service.RootService

/**
 * Test class for verifying the conditions under which a player can draw a card.
 *
 * This test ensures that a player can only draw a card if their draw stack is not empty
 * and they do not already hold the maximum number of cards allowed.
 */
class CanDrawCardTest {
    private lateinit var rootService: RootService
    private lateinit var player1: Player
    private lateinit var player2: Player
    private lateinit var game: UpAndDown

    /**
     * Sets up the test environment before each test case.
     * Initializes the game and assigns two players.
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
     * Tests the scenarios in which a player can or cannot draw a card.
     *
     * - A player with an empty draw stack should not be able to draw a card.
     * - A player with cards in the draw stack and the numbers of hand cards isn´t greater than 9, should be able to draw a card.
     * - A player with the maximum allowed cards should not be able to draw more.
     */
    @Test
    fun possibleOrImpossibleToDrawCard() {
        // Setup player1's hand with three cards
        player1.handCards.add(Card(CardSuit.HEARTS, CardValue.TWO))
        player1.handCards.add(Card(CardSuit.SPADES, CardValue.ACE))
        player1.handCards.add(Card(CardSuit.CLUBS, CardValue.KING))

        // Ensure draw stack is empty
        player1.drawStack.isEmpty()

        assertFalse(rootService.playerActionService.canDrawCard())

        // Add a card to the draw stack and verify the player can now draw
        player1.drawStack.add(Card(CardSuit.HEARTS, CardValue.THREE))

        assertTrue(rootService.playerActionService.canDrawCard())

        // Fill the player's hand to the maximum limit
        player1.handCards.add(Card(CardSuit.HEARTS, CardValue.FIVE))
        player1.handCards.add(Card(CardSuit.HEARTS, CardValue.SIX))
        player1.handCards.add(Card(CardSuit.HEARTS, CardValue.SEVEN))
        player1.handCards.add(Card(CardSuit.HEARTS, CardValue.EIGHT))
        player1.handCards.add(Card(CardSuit.HEARTS, CardValue.NINE))
        player1.handCards.add(Card(CardSuit.HEARTS, CardValue.TEN))

        assertTrue(rootService.playerActionService.canDrawCard())

        // Further adding more cards should prevent drawing
        player1.handCards.add(Card(CardSuit.HEARTS, CardValue.JACK))
        player1.handCards.add(Card(CardSuit.HEARTS, CardValue.QUEEN))
        player1.handCards.add(Card(CardSuit.HEARTS, CardValue.KING))

        assertFalse(rootService.playerActionService.canDrawCard())
    }
}