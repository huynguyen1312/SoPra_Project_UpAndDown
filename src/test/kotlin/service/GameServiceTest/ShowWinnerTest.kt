package service.GameServiceTest

import entity.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import service.RootService

/**
 * Unit test for verifying the `showWinner` functionality in the game service.
 *
 * This test ensures that:
 * - The player with fewer hand cards is correctly identified as the winner.
 * - The player with more hand cards is correctly identified as the loser.
 * - Players with the same number of hand cards result in a tie.
 */
class ShowWinnerTest {
    private lateinit var rootService: RootService // Root service instance used for testing

    // Player instances used for testing
    private lateinit var player1: Player
    private lateinit var player2: Player
    private lateinit var player3: Player

    /**
     * Sets up the test environment before each test execution.
     * Initializes `rootService` and creates test players with predefined hand cards.
     */
    @BeforeEach
    fun setUp() {
        rootService = RootService()
        player1 = Player("Huy",handCards = listOf(Card(CardSuit.HEARTS, CardValue.ACE), Card(CardSuit.SPADES, CardValue.TWO)).toMutableList())
        player2 = Player("Jan",handCards = listOf(Card(CardSuit.CLUBS, CardValue.KING)).toMutableList())
        player3 = Player("Joost",handCards = listOf(Card(CardSuit.HEARTS, CardValue.KING)).toMutableList())
    }

    /**
     * Tests the `showWinner` method to determine the winner based on hand card count.
     *
     * Steps:
     * 1. Player with fewer hand cards should be identified as the winner.
     * 2. Player with more hand cards should be identified as the loser.
     * 3. If both players have the same number of hand cards, the result should be a tie.
     */
    @Test
    fun playerFewerHandCardWin() {
        assertEquals("Jan", rootService.gameService.showWinner(player1, player2), "Player with fewer hand cards should win")
        assertNotEquals("Huy", rootService.gameService.showWinner(player1, player2), "Player with more hand cards should lose")
        assertEquals("Tie", rootService.gameService.showWinner(player3, player2), "Player with same hand cards should tie")
    }
}