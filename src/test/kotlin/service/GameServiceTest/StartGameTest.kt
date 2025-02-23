package service.gameServiceTest

import entity.Player
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import service.RootService

/**
 * Test class for ensuring correct game initialization.
 *
 * This test ensures:
 * - A game starts with the correct number of players and initial setup.
 * - Invalid player names are rejected.
 */

class StartGameTest {
    private lateinit var rootService: RootService
    private lateinit var player1: Player
    private lateinit var player2: Player

    /**
     * Sets up the test environment before each test execution.
     * Initializes `rootService`, creates players, and sets up a game.
     */
    @BeforeEach
    fun setUp() {
        rootService = RootService()
        player1 = Player("Huy")
        player2 = Player("Duc")
    }

    /**
     * Check correct game initialisation
     */
    @Test
    fun correctInitiationOfGameTest() {

        rootService.gameService.startGame(listOf(player1.name, player2.name))

        val game = checkNotNull(rootService.currentGame) { "Game should be initialized" }

        assertNotNull(game, "Game is running")
        assertEquals("Huy", game.players[0].name, "Player 1 name should be correct")
        assertEquals("Duc", game.players[1].name, "Player 2 name should be correct")
        assertEquals(5, game.players[0].handCards.size, "Player 1 should have 5 cards in hand")
        assertEquals(5, game.players[1].handCards.size, "Player 2 should have 5 cards in hand")
        assertEquals(20, game.players[0].drawStack.size,
            "Player 1 should have 20 cards in draw stack")
        assertEquals(20, game.players[1].drawStack.size,
            "Player 2 should have 20 cards in draw stack")
        assertEquals(1, game.centerStack1.size, "Center stack 1 should have 1 card")
        assertEquals(1, game.centerStack2.size, "Center stack 2 should have 1 card")
    }

    /**
     * Check no blank name
     */
    @Test
    fun blankNameTest() {
        val exception = assertThrows<IllegalArgumentException> {
            rootService.gameService.startGame(listOf("", "Duc"))
        }
        assertTrue(exception.message!!.contains("Player names cannot be blank"))
    }

    /**
     * Check no two same name
     */
    @Test
    fun duplicateNameTest() {
        val exception = assertThrows<IllegalArgumentException> {
            rootService.gameService.startGame(listOf("Huy", "Huy"))
        }
        assertTrue(exception.message!!.contains("Player names cannot be the same"))
    }

    /**
     * Check result when the players name is way too long
     */
    @Test
    fun tooLongNameTest() {
        val exception = assertThrows<IllegalArgumentException> {
            rootService.gameService.startGame(listOf("ThisNameIsWayTooLongForTheGame", "Duc"))
        }
        assertTrue(exception.message!!.contains("Player names cannot be longer than 20 characters"))
    }

    /**
     * Check if all card from hand cards and cards in draw stack are unique
     */
    @Test
    fun allCardsAreUniqueTest() {
        rootService.gameService.startGame(listOf(player1.name, player2.name) )
        val game = checkNotNull(rootService.currentGame){ "Game should be initialized" }

        val player1DrawCards = game.players[0].drawStack
        val player1HandCards = game.players[0].handCards
        val player2DrawCards = game.players[1].drawStack
        val player2HandCards = game.players[1].handCards

        assertEquals(player1DrawCards.size, player1DrawCards.toSet().size,
            "Player 1 draw stack should have unique cards")
        assertEquals(player1HandCards.size, player1HandCards.toSet().size,
            "Player 1 hand cards should be unique cards")
        assertEquals(player2DrawCards.size, player2DrawCards.toSet().size,
            "Player 2 draw stack should have unique cards")
        assertEquals(player2HandCards.size, player2HandCards.toSet().size,
            "Player 2 hand cards should be unique cards")
    }

}

