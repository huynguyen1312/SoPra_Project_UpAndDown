package service

import entity.*
import gui.Refreshable

/**
 * Service layer class that provides the logic for actions not directly
 * related to a single player.
 */
class GameService(private val rootService: RootService): AbstractRefreshingService() {


    /**
     * Initializes and starts a new game of [UpAndDown].
     *
     * @param playerNames The list of the first and second players names. Must be non-empty, unique,
     * and at most 20 characters long.
     *
     * The function performs the following steps:
     * 1. Validates the player names based on length and uniqueness constraints.
     * 2. Creates a full deck of 52 unique cards.
     * 3. Splits the deck:
     *    - Each player receives an initial hand of 5 cards.
     *    - The remaining cards are split evenly into draw piles for each player.
     *    - The first card from each draw pile is placed in the center stack.
     * 4. Initializes the game state and assigns it to `rootService.currentGame`.
     * 5. Notifies all [Refreshable] to update the UI.
     *
     * Precondition:
     * - The deck must contain exactly 52 unique cards.
     *
     * Post-condition:
     * - Card piles were correctly split into players hand, draw piles and center stacks
     * - A new game is created and stored in `rootService.currentGame`.
     * - The UI is refreshed.
     */
    fun startGame(
        playerNames: List<String> = listOf("",""),
    ) {
        require(playerNames[0].isNotBlank() && playerNames[1].isNotBlank()) {"Player names cannot be blank."}
        require(playerNames[0] != playerNames[1]) {"Player names cannot be the same."}
        require(playerNames[0].length <= 20 && playerNames[1].length <= 20) {
            "Player names cannot be longer than 20 characters."
        }

        val player1 = Player(playerNames[0])
        val player2 = Player(playerNames[1])
        rootService.currentGame = UpAndDown(player1, player2)
        createdStartStack()
        onAllRefreshables{ refreshAfterStartNewGame() }
    }

    /**
     * Proceeds to the next turn in the game.
     *
     * This function performs the following actions:
     * 1. Ensures that a game is currently running.
     * 2. Notifies all observers that the turn has changed.
     *
     * Precondition:
     * - A game must be currently running.
     *
     * Post-condition:
     * - The game progresses to the next player's turn.
     * - UI or game state updates accordingly.
     *
     * @throws IllegalStateException if no game is currently running.
     */
    fun nextTurn() {
        checkNotNull(rootService.currentGame){"No game currently running."}

        // Notify any observers that the turn has changed
        onAllRefreshables { refreshAfterNextTurn() }

    }

    /**
     * Ends the current turn and marks it as passed.
     *
     * This function performs the following actions:
     * 1. Ensures that a game is currently running.
     * 2. Notifies all observers that the turn has ended.
     *
     * Precondition:
     * - A game must be currently running.
     *
     * Post-condition:
     * - The turn is marked as passed.
     * - UI or game state updates accordingly.
     *
     * @throws IllegalStateException if no game is currently running.
     */
    fun endTurn(){
        val game = checkNotNull(rootService.currentGame){"No game currently running."}
        val currentIndex = game.players.indexOf(game.currentPlayer)
        // Switch to the next player (toggle between 0 and 1)
        game.currentPlayer = if (currentIndex == 0) game.players[1] else game.players[0]
    }

    /**
     * Checks game-ending conditions and determines the winner.
     *
     * This function performs the following actions:
     * 1. Ensures that a game is currently running.
     * 2. Checks if either player's draw stack and hand are both empty.
     *    - If so, declares the other player as the winner.
     * 3. If neither player is out of cards, checks if consecutive passes have occurred.
     *    - If consecutive passes happen, the player with the most hand cards wins.
     *    - If both have the same number of cards, the game ends in a tie.
     *
     * Precondition:
     * - A game must be currently running.
     *
     * Post-condition:
     * - If a player runs out of cards, the other player is declared the winner.
     * - If the game ends due to consecutive passes, the player with more cards wins.
     * - If both players have the same number of cards, the game ends in a tie.
     *
     * @throws IllegalStateException if no game is currently running.
     */
    internal fun endGame() {
        val game = checkNotNull(rootService.currentGame){"No game currently running."}
        val currentPlayer = game.currentPlayer

        val currentIndex = game.players.indexOf(game.currentPlayer)
        val opponent = if (currentIndex == 0) game.players[1] else game.players[0]

        val isDraw = game.passed && (currentPlayer.handCards.size == opponent.handCards.size)

        val winner = when {
            game.passed && currentPlayer.handCards.size < opponent.handCards.size -> currentPlayer
            game.passed && currentPlayer.handCards.size > opponent.handCards.size -> opponent
            else -> game.currentPlayer
        }

        onAllRefreshables { refreshAfterGameEnd(winner, isDraw) }
    }

    /**
     * Creates a shuffled deck of 52 standard playing cards.
     *
     * This function:
     * 1. Generates a list of 52 `Card` objects, where each card consists of a suit and a value.
     * 2. Uses integer division and modulo operations to assign suits and values.
     * 3. Shuffles the deck before returning it.
     * 4. Splits the deck:
     *   - Each player receives an initial hand of 5 cards.
     *   - The remaining cards are split evenly into draw piles for each player.
     *   - The first card from each draw pile is placed in the center stack.
     */
    private fun createdStartStack() {
        val game = checkNotNull(rootService.currentGame)
        val player1 = game.player1
        val player2 = game.player2
        val stackOfAllCards = List(52) { index ->
            Card(
                CardSuit.entries[index / 13],
                CardValue.entries[index % 13]
            )
        }.shuffled()

        require(stackOfAllCards.size == 52) {"The stack of cards must contain exactly 52 cards."}

        val initialHandSize = 5

        player1.handCards = stackOfAllCards.take(initialHandSize).toMutableList()
        player2.handCards = stackOfAllCards.slice(initialHandSize until initialHandSize * 2).toMutableList()

        val remainingCards = stackOfAllCards.drop(initialHandSize * 2)

        val perDrawStack = stackOfAllCards.drop(initialHandSize * 2).size / 2

        player1.drawStack = remainingCards.take(perDrawStack).toMutableList()
        player2.drawStack = remainingCards.drop(perDrawStack).toMutableList()

        game.centerStack1.add(player1.drawStack.last())
        game.centerStack2.add(player2.drawStack.last())

        player1.drawStack.removeLast()
        player2.drawStack.removeLast()
    }
}
