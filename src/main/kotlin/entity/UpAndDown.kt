package entity

/**
 * The `UpAndDown` data class represents a two-player card game.
 *
 * @property player1 The first player in the game.
 * @property player2 The second player in the game.
 */
data class UpAndDown(
    val player1: Player,
    val player2: Player,
){
    /**
     * Represents a list of players participating in the game.
     */
    val players: List<Player> = listOf(player1, player2)
    /**
     * Represents the index of the player whose turn it currently is.
     */
    var currentPlayer = this.players[0]

    /**
     * Indicates whether the current player has passed their turn.
     */
    var passed: Boolean = false

    /**
     * Represents the stack of cards placed at the left and right center of the playing area.
     */
    val centerStack1: MutableList<Card> = mutableListOf()
    val centerStack2: MutableList<Card> = mutableListOf()
}