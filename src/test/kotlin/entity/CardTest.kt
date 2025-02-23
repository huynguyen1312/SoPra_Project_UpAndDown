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
}
