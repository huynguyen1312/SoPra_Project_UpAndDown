package entity

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import kotlin.test.BeforeTest

/**
 * This [PlayerTest] gibt some solid test-cases for [player] two initialized [card1] and [card2]
 *
 * @property player who plays the game
 * @property card1 is 7 of ♥
 * @property card2 is A of ♠
 */
class PlayerTest {
    private lateinit var player: Player
    private val card1 = Card(CardSuit.HEARTS, CardValue.SEVEN)
    private val card2 = Card(CardSuit.SPADES, CardValue.ACE)

    /**
     * set up players name
     */
    @BeforeEach
    fun setUp() {
        player = Player("Huy")
    }

    /**
     * Check if two owns stacks from player are empty
     */
    @Test
    fun testPlayerInitialization(){
        assertEquals("Huy", player.name)
        assertTrue(player.handCards.isEmpty())
        assertTrue(player.drawStack.isEmpty())
    }

    /**
     * Check
     */
    @Test
    fun testAddCardToHand(){
        player.handCards.add(card1)
        assertFalse(player.handCards.isEmpty())
        assertEquals(1, player.handCards.size)
        assertEquals(card1, player.handCards.first())
    }

    /**
     * Check if the card was correct removed from handCards
     */
    @Test
    fun testRemoveCardFromHand(){
        player.handCards.add(card1)
        player.handCards.remove(card1)
        assertTrue(player.handCards.isEmpty())
    }

    /**
     * Check if card was correct add to the drawStack
     */
    @Test
    fun testAddCardToDrawStack() {
        player.drawStack.add(card2)
        assertFalse(player.drawStack.isEmpty())
        assertEquals(1, player.drawStack.size)
        assertEquals(card2, player.drawStack[0])
    }

    /**
     * Check if the card was correct removed from drawStack
     */
    @Test
    fun testRemoveCardFromDrawStack() {
        player.drawStack.add(card2)
        player.drawStack.remove(card2)
        assertTrue(player.drawStack.isEmpty())
    }

    /**
     * Check if [toString] show correct format
     */
    @Test
    fun testToStringMethod() {
        player.handCards.add(card1)
        player.drawStack.add(card2)
        val expectedString = "Huy: H[$card1] D[$card2]"
        assertEquals(expectedString, player.toString())
    }
}
