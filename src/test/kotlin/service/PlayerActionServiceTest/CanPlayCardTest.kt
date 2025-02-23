package service.PlayerActionServiceTest

import entity.*
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import service.RootService

/**
 * Test class for verifying whether a player can play a given card.
 *
 * This test ensures that:
 * - A player can only play a card if it follows the game rules for valid moves.
 * - A card must match the center stack or follow specific game conditions.
 */
class CanPlayCardTest {
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
     * Tests whether specific cards are playable or not.
     *
     * - A player can play a card if it matches a valid move based on the center stack.
     * - If a card does not meet the game’s criteria, it should not be playable.
     */
    @Test
    fun possibleOrImpossibleToPlayCard() {
        // Setup center stack with a card
        game.centerStack1.add(Card(CardSuit.HEARTS, CardValue.TWO))

        assertTrue(rootService.playerActionService.canPlayCard(Card(CardSuit.HEARTS, CardValue.THREE), 0))
        assertTrue(rootService.playerActionService.canPlayCard(Card(CardSuit.HEARTS, CardValue.KING), 0))
        assertTrue(rootService.playerActionService.canPlayCard(Card(CardSuit.HEARTS, CardValue.ACE), 0))
        assertTrue(rootService.playerActionService.canPlayCard(Card(CardSuit.HEARTS, CardValue.FOUR), 0))
        assertFalse(rootService.playerActionService.canPlayCard(Card(CardSuit.HEARTS, CardValue.FIVE), 0))
        assertFalse(rootService.playerActionService.canPlayCard(Card(CardSuit.HEARTS, CardValue.SIX), 0))
        assertFalse(rootService.playerActionService.canPlayCard(Card(CardSuit.DIAMONDS, CardValue.SEVEN), 0))
        assertTrue(rootService.playerActionService.canPlayCard(Card(CardSuit.DIAMONDS, CardValue.ACE), 0))
    }
}