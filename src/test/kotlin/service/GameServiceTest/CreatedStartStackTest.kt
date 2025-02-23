package service.GameServiceTest

import entity.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import service.RootService

/**
 * Unit test for verifying the deck creation logic in the `createdStartStack` function.
 *
 * This test ensures that:
 * - A deck of 52 unique cards is created.
 * - Each suit contains exactly 13 cards.
 * - Each card value appears exactly 4 times (one per suit).
 */
class CreatedStartStackTest {
    private lateinit var rootService: RootService // Root service instance used for testing

    /**
     * Sets up the test environment before each test execution.
     * Initializes the `rootService` instance.
     */
    @BeforeEach
    fun setUp() {
        rootService = RootService()
    }

    /**
     * Tests the `createdStartStack` function to ensure the deck is correctly generated.
     *
     * This test performs the following checks:
     * 1. Verifies that the generated deck contains exactly 52 cards.
     * 2. Ensures that all 52 cards are unique.
     * 3. Confirms that each suit contains exactly 13 cards.
     * 4. Validates that each card value appears exactly 4 times (once per suit).
     */
    @Test
    fun deckOfCardsCreatedTest() {
        /**
         * Generate the deck using the game service
         */
        val deck = rootService.gameService.createdStartStack()

        /**
         * Assert,
         * Ensure 52 cards are created,
         * Ensure all 52 cards are unique
         */
        assertEquals(52, deck.size)
        assertTrue(deck.distinct().size == 52)

        /**
         * Ensure each suit has 13 cards
         */
        val suitsCount = deck.groupBy { it.suit }.mapValues { it.value.size }
        CardSuit.values().forEach { suit ->
            assertEquals(13, suitsCount[suit])
        }

        /**
         * Ensure each value appears 4 times (one per suit)
         */
        val valuesCount = deck.groupBy { it.value }.mapValues { it.value.size }
        CardValue.values().forEach { value ->
            assertEquals(4, valuesCount[value])
        }
    }

}