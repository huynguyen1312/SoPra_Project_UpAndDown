package entity

/**
 * The `Player` class represents a player in a card game.
 *
 * @property name The name of the player.
 */
class Player(
    val name: String,
    var handCards: MutableList<Card> = mutableListOf(),
    var drawStack: MutableList<Card> = mutableListOf()
){

    /**
     * Overrides the `toString()` method to return a textual representation of the player.
     * Displays the player's name, hand cards, and draw stack.
     *
     * @return A formatted string containing player information.
     */
    override fun toString(): String =
        "$name: H$handCards D$drawStack"
}
