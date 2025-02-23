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
     * @param player1Name The name of the first player. Must be non-empty, unique, and at most 20 characters long.
     * @param player2Name The name of the second player. Must be non-empty, unique, and at most 20 characters long.
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
     * Postcondition:
     * - A new game is created and stored in `rootService.currentGame`.
     * - The UI is refreshed.
     */
    fun startGame(
        player1Name: String,
        player2Name: String
    ) {
        require(player1Name.isNotBlank() && player2Name.isNotBlank()) {"Player names cannot be blank."}
        require(player1Name != player2Name) {"Player names cannot be the same."}
        require(player1Name.length <= 20 && player2Name.length <= 20) {"Player names cannot be longer than 20 characters."}

        val allCards: List<Card> = createdStartStack()
        require(allCards.size == 52) {"The stack of cards must contain exactly 52 cards."}

        val initialHandSize = 5

        val player1HandCards : MutableList<Card> = allCards.take(initialHandSize).toMutableList()
        val player2HandCards : MutableList<Card> = allCards.slice(initialHandSize until initialHandSize * 2).toMutableList()

        val remainingCards = allCards.drop(initialHandSize * 2)

        val perDrawStack = remainingCards.size / 2

        val player1DrawStack : MutableList<Card> = remainingCards.take(perDrawStack).toMutableList()
        val player2DrawStack : MutableList<Card> = remainingCards.drop(perDrawStack).toMutableList()

        val firstCenterStack1Card = player1DrawStack.first()
        val firstCenterStack2Card = player2DrawStack.first()

        val game = UpAndDown(
            Player(player1Name,player1HandCards,player1DrawStack.drop(1).toMutableList()),
            Player(player2Name,player2HandCards,player2DrawStack.drop(1).toMutableList())
        )

        game.centerStack1.add(firstCenterStack1Card)
        game.centerStack2.add(firstCenterStack2Card)

        rootService.currentGame = game

        onAllRefreshables{ refreshAfterStartNewGame() }
    }

    /**
     * Proceeds to the next turn in the game.
     *
     * This function performs the following actions:
     * 1. Ensures that a game is currently running.
     * 2. Resets the `passed` flag to false.
     * 3. Determines the index of the current player.
     * 4. Switches to the next player (alternating between the two players).
     * 5. Notifies all observers that the turn has changed.
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
        val game = checkNotNull(rootService.currentGame){"No game currently running."}

        game.passed = false

        val currentIndex = game.players.indexOf(game.currentPlayer)

        // Switch to the next player (toggle between 0 and 1)
        game.currentPlayer = if (currentIndex == 0) game.players[1] else game.players[0]

        // Notify any observers that the turn has changed
        onAllRefreshables { refreshAfterNextTurn() }

    }
    /**
     * Ends the current turn and marks it as passed.
     *
     * This function performs the following actions:
     * 1. Ensures that a game is currently running.
     * 2. Sets the `passed` flag to true.
     * 3. Notifies all observers that the turn has ended.
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
        game.passed = true
        onAllRefreshables { refreshAfterEndTurn() }
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

        val player1 = game.player1
        val player2 = game.player2

        val drawStackPlayer1IsEmpty = player1.drawStack.isEmpty()
        val handCardPlayer1IsEmpty = player1.handCards.isEmpty()
        val drawStackPlayer2IsEmpty = player2.drawStack.isEmpty()
        val handCardPlayer2IsEmpty = player2.handCards.isEmpty()

        if (drawStackPlayer1IsEmpty && handCardPlayer1IsEmpty || drawStackPlayer2IsEmpty && handCardPlayer2IsEmpty){
            showWinner(player1,player2)
        }
        else if(checkConsecutivePasses()) {
            when {
                game.player1.handCards.size != game.player2.handCards.size -> showWinner(player1, player2)
                else -> println("It's a tie!")
            }
        }
        onAllRefreshables { refreshAfterGameEnd() }
    }

    /**
     * Checks if both players have passed consecutively.
     *
     * This function:
     * 1. Ensures that a game is currently running.
     * 2. Retrieves the `PlayerActionService` for the current and next player.
     * 3. Determines if both players are allowed to pass.
     *
     * @return `true` if both players can pass, indicating consecutive passes; otherwise, `false`.
     *
     * @throws IllegalStateException if no game is currently running.
     */
    internal fun checkConsecutivePasses(): Boolean {
        val game = checkNotNull(rootService.currentGame){"No game currently running."}
        var thisPlayerAction = rootService.playerActionService
        game.currentPlayer = game.players[(game.players.indexOf(game.currentPlayer) + 1) % 2]
        var nextPlayerAction = rootService.playerActionService
        return thisPlayerAction.canPass() && nextPlayerAction.canPass()
    }

    /**
     * Determines the winner based on the number of hand cards each player has.
     *
     * This function:
     * 1. Compares the number of hand cards of both players.
     * 2. Returns the name of the player with more cards.
     * 3. If both players have the same number of hand cards, returns "Tie".
     *
     * @param player1 The first player.
     * @param player2 The second player.
     * @return The name of the winning player or "Tie" if both have the same number of hand cards.
     */
    internal fun showWinner(player1: Player, player2: Player): String {
        if (player1.handCards.size > player2.handCards.size) return player2.name
        else if (player1.handCards.size < player2.handCards.size) return player1.name
        else return "Tie"
    }

    /**
     * Creates a shuffled deck of 52 standard playing cards.
     *
     * This function:
     * 1. Generates a list of 52 `Card` objects, where each card consists of a suit and a value.
     * 2. Uses integer division and modulo operations to assign suits and values.
     * 3. Shuffles the deck before returning it.
     *
     * @return A shuffled list of 52 unique playing cards.
     */
    internal fun createdStartStack() = List(52){
        index ->
        Card(
            CardSuit.values()[index/13],
            CardValue.values()[index%13]
        )
    }.shuffled()
}