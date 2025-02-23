package entity

/**
 * The `Card` data class represents a playing card with a suit and a value.
 *
 * @property suit The suit of the card (e.g., Hearts, Diamonds, Clubs, Spades).
 * @property value The value of the card (e.g., Ace, King, Queen, numerical values).
 */

data class Card(val suit: CardSuit, val value: CardValue) {
    /**
     * Returns a string representation of the card.
     *
     * @return A formatted string displaying the card's value and suit.
     */
    override fun toString(): String {
        return "$value of $suit"
    }
    /**
     * Compares this card with another card based on their value.
     *
     * @param other The other card to compare to.
     * @return result is closest distance between 2 cards values
     */
    fun compareTo(other: Card): Int {
        val diff = this.value.ordinal - other.value.ordinal
        val modDiff = (diff % 13 + 13) % 13
        return if (modDiff > 6) modDiff - 13 else modDiff
    }
}
