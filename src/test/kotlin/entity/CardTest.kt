package entity

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

/**
 * Test cases for [Card]
 */
 class CardTest {

 /**
  * Check the right format of showed card
  */
 @Test
  fun testCorrectToStringFormat() {
   val card = Card(CardSuit.HEARTS, CardValue.ACE)
   assertEquals("A of ♥", card.toString())
  }

 /**
  * Check if card1 greater than card2
  */
  @Test
  fun testFirstCardGreaterThanSecondCard() {
   val card1 = Card(CardSuit.SPADES, CardValue.KING)
   val card2 = Card(CardSuit.CLUBS, CardValue.QUEEN)
   assertTrue(card1 > card2) // KING > QUEEN
  }

 /**
  * Check if card1 smaller than card2
  */
  @Test
  fun testFirstCardSmallerThanSecondCard() {
   val card1 = Card(CardSuit.HEARTS, CardValue.TWO)
   val card2 = Card(CardSuit.DIAMONDS, CardValue.FIVE)
   assertTrue(card1 < card2) // TWO < FIVE
  }
 /**
  * Check if card1 equal card2
  */
  @Test
  fun testEqualCardValues() {
   val card1 = Card(CardSuit.SPADES, CardValue.JACK)
   val card2 = Card(CardSuit.HEARTS, CardValue.JACK)
   assertEquals(0, card1.compareTo(card2)) // Beide sind JACK
  }
}