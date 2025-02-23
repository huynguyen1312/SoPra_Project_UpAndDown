package entity

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach

/**
 * This test show whether the class [UpAndDown] correct performs base on some functional properties
 *
 * @property player1 the first [Player]
 * @property player2 the second [Player]
 * @property game the game [UpAndDown]
 */
class UpAndDownTest {

    private lateinit var player1: Player
    private lateinit var player2: Player
    private lateinit var game: UpAndDown

    /**
     * Set up initial name for two players and let them play UpAndDown
     */
    @BeforeEach
    fun setUp() {
        player1 = Player("Huy")
        player2 = Player("Duc")
        game = UpAndDown(player1, player2)
    }

    /**
     * Check if the list of players two players contains
     */
    @Test
    fun testTwoPlayerContainingList() {
        assertEquals(2, game.players.size)
        assertTrue(game.players.contains(player1))
        assertTrue(game.players.contains(player2))
    }

    /**
     * Check if the first player begins the game
     */
    @Test
    fun testInitialCurrentPlayer1() {
        assertEquals(player1, game.currentPlayer)
    }

    /**
     * Check if pass was false initialized
     */
    @Test
    fun testInitialFalsePass() {
        assertFalse(game.passed)
    }

    /**
     * Check if the initial leftCenterStack is empty
     */
    @Test
    fun testIsInitialEmptyLeftCenterStack() {
        assertTrue(game.centerStack1.isEmpty())
    }

    /**
     * Check if the initial rightCenterStack is empty
     */
    @Test
    fun testIsInitialEmptyRightCenterStack() {
        assertTrue(game.centerStack2.isEmpty())
    }
}
