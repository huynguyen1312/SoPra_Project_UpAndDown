package service.PlayerActionServiceTest

import entity.*
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import service.RootService

/**
 * Test class for verifying the conditions under which a player can pass their turn.
 *
 * This test ensures that a player can only pass if they do not have a valid playable card
 * and their draw stack is empty.
 */
class CanPassTest {
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
     * Tests whether a player is allowed to pass or not.
     *
     * - A player can pass if they have no playable cards and no draw options.
     * - A player cannot pass if they have a playable card.
     */
    @Test
    fun possibleOrImpossibleToPass() {
        // Setup center stacks to define playable conditions
        game.centerStack1.add(Card(CardSuit.HEARTS, CardValue.TWO))
        game.centerStack2.add(Card(CardSuit.SPADES, CardValue.ACE))

        // Player1's hand has a card that is not playable
        player1.handCards.add(Card(CardSuit.CLUBS, CardValue.TEN))
        player1.drawStack.isEmpty()

        assertTrue(rootService.playerActionService.canPass())

        player1.handCards.add(Card(CardSuit.HEARTS, CardValue.FIVE))

        assertTrue(rootService.playerActionService.canPass())

        // Adding a playable card should prevent passing
        player1.drawStack.add(Card(CardSuit.HEARTS, CardValue.THREE))

        assertFalse(rootService.playerActionService.canPass())

    }
}