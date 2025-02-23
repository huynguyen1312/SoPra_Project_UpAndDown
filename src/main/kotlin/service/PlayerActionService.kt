package service

import entity.*
import kotlin.math.*
/**
 * Service layer class that provides the logic for the two possible actions a player
 * can take in War: drawing from the left stack or drawing from right stack.
 */

class PlayerActionService(private val rootService: RootService): AbstractRefreshingService() {

    /**
     * Plays a card onto the specified stack.
     *
     * This function:
     * 1. Checks if the card can be played.
     * 2. Retrieves the correct stack based on `stackID`.
     * 3. Throws an `IllegalArgumentException` if an invalid `stackID` is provided.
     * 4. Adds the card to the chosen stack and removes it from the player's hand.
     * 5. Ends the turn after the move.
     * 6. Notifies observers to refresh the UI.
     *
     * @param card The card to be played.
     * @param stackID The ID of the stack (0 for `centerStack1`, 1 for `centerStack2`).
     * @throws IllegalStateException if no game is currently running.
     * @throws IllegalArgumentException if `stackID` is invalid.
     */
    fun playCard(card: Card, stackID: Int){
        val game = checkNotNull(rootService.currentGame){"No game currently running."}

        if (canPlayCard(card, stackID)) {// Retrieve the correct stack or throw an error for invalid stackID
            game.currentPlayer.handCards.remove(card)
            when (stackID) {
                0 -> game.centerStack1.add(card)
                1 -> game.centerStack2.add(card)
                else -> throw IllegalArgumentException("Invalid stackID: $stackID") // Handle invalid stackID
            }
            game.passed = false
            if (game.currentPlayer.handCards.isEmpty() && game.currentPlayer.drawStack.isEmpty()) {
                onAllRefreshables { refreshAfterPlayCard(game.currentPlayer, stackID) }
                rootService.gameService.endGame()
            } else {
                onAllRefreshables { refreshAfterPlayCard(game.currentPlayer, stackID) }
                rootService.gameService.endTurn()
            }
        }
        else return
    }

    /**
     * Draws a card from the draw stack and adds it to the player's hand.
     *
     * This function:
     * 1. Checks if the player can draw a card.
     * 2. Retrieves the current game and player.
     * 3. Removes the last card from the player's draw stack and adds it to their hand.
     * 4. Ends the turn after drawing a card.
     * 5. Notifies observers to refresh the UI.
     *
     * @throws IllegalStateException if no game is currently running.
     */
    fun drawCard(){
        val game = checkNotNull(rootService.currentGame){"No game currently running."}
        if(canDrawCard()){
            val player = game.currentPlayer
            val lastCard = player.drawStack.removeLast()
            player.handCards.add(lastCard)
            game.passed = false
            onAllRefreshables { refreshAfterDrawCard(lastCard) }
            rootService.gameService.endTurn()
        }
        else return
    }

    /**
    * Swaps all cards from the player's hand into their draw stack, then redraws five cards.
    *
    * This function:
    * 1. Checks if the player can swap cards.
    * 2. Moves all hand cards to the player's draw stack.
    * 3. Shuffles the draw stack.
    * 4. Draws five new cards from the draw stack back into the player's hand.
    * 5. Ends the turn after swapping.
    * 6. Notifies observers to refresh the UI.
    *
    * @throws IllegalStateException if no game is currently running.
    */
    fun swapCard(){
        val game = checkNotNull(rootService.currentGame){"No game currently running."}
        if(canSwapCard()){
            val player = game.currentPlayer
            player.drawStack.addAll(player.handCards)
            player.handCards.clear()
            player.drawStack.shuffle()
            player.handCards.addAll(player.drawStack.take(5))
            val newDrawStack = player.drawStack.drop(5)
            player.drawStack.clear()
            player.drawStack.addAll(newDrawStack)

            game.passed = false
            onAllRefreshables { refreshAfterSwapCards() }
            rootService.gameService.endTurn()
        }
        else return
    }

    /**
     * Passes the turn to the next player.
     *
     * This function:
     * 1. Checks if the player is allowed to pass.
     * 2. Marks the game as "passed."
     * 3. Switches the current player to the next one.
     * 4. Notifies observers to refresh the UI.
     *
     * @throws IllegalStateException if no game is currently running.
     */
    fun pass(){
        val game = checkNotNull(rootService.currentGame){"No game currently running."}
        if (canPass()){
            if (game.passed) {
                rootService.gameService.endGame()
            }
            game.passed = true
            onAllRefreshables { refreshAfterPass(game.currentPlayer) }
            rootService.gameService.endTurn()
        }
        else return
    }

    /**
     * Determines whether a given card can be played on the specified stack.
     *
     * This function:
     * 1. Retrieves the current game state.
     * 2. Determines the stack based on the given `stackID`.
     * 3. Throws an `IllegalArgumentException` if an invalid `stackID` is provided.
     * 4. Checks if the card can be played based on the top card of the stack.
     * 5. A card can be played if:
     *    - It has a different suit from the top card and its value difference is within valid thresholds (1).
     *    - It has the same suit as the top card and its value difference is within valid thresholds (1 or 2).
     *
     * @param card The card the player wants to play.
     * @param stackID The ID of the stack (0 for `centerStack1`, 1 for `centerStack2`).
     * @return `true` if the card can be played, `false` otherwise.
     * @throws IllegalStateException if no game is currently running.
     * @throws IllegalArgumentException if an invalid `stackID` is provided.
     */
    fun canPlayCard(card: Card, stackID: Int): Boolean {
        val game = checkNotNull(rootService.currentGame)

        val playStack = when (stackID) {
            0 -> game.centerStack1
            1 -> game.centerStack2
            else -> throw IllegalArgumentException("Invalid stackID: $stackID") // Handle invalid stackID
        }

        val topCard = playStack.last()
        val compared = topCard.compareTo(card)

        return when {
            compared == -1 -> true // Can be played on a card exactly one rank lower
            compared == 1 -> true // Can be played on a card exactly one rank higher
            card.suit == topCard.suit && (compared == -2 || compared == 2) -> true
            // Can be played if same suit within 2 ranks
            else -> false
        }
    }
    /**
     * Determines whether the player can draw a card.
     *
     * This function:
     * 1. Retrieves the current game state and the current player.
     * 2. Checks if the player's draw stack is not empty.
     * 3. Ensures the player's hand size does not exceed 9 cards.
     *
     * @return `true` if the player can draw a card, `false` otherwise.
     * @throws IllegalStateException if no game is currently running.
     */
    fun canDrawCard(): Boolean{
        val game = checkNotNull(rootService.currentGame){"No game currently running."}
        val player = game.currentPlayer
        return player.drawStack.isNotEmpty() && player.handCards.size <= 9
    }

    /**
     * Determines whether the player can swap their hand cards with their draw stack.
     *
     * This function:
     * 1. Retrieves the current game state and the current player.
     * 2. Checks if the player has at least 8 cards in their hand.
     * 3. Ensures the player's draw stack is not empty.
     *
     * @return `true` if the player can swap cards, `false` otherwise.
     * @throws IllegalStateException if no game is currently running.
     */
    fun canSwapCard(): Boolean {
        val game = checkNotNull(rootService.currentGame){"No game currently running."}
        val player = game.currentPlayer
        return player.handCards.size >= 8 && player.drawStack.isNotEmpty()
    }

    /**
     * Determines whether the player can pass their turn.
     *
     * This function:
     * 1. Retrieves the current game state and the current player.
     * 2. Ensures the player cannot draw or swap cards.
     * 3. Checks if none of the player's hand cards can be played on either stack.
     *
     * @return `true` if the player can pass, `false` otherwise.
     * @throws IllegalStateException if no game is currently running.
     */
    fun canPass(): Boolean {
        val game = checkNotNull(rootService.currentGame){"No game currently running."}
        val player = game.currentPlayer
        return !canDrawCard() &&
               !canSwapCard() &&
               !player.handCards.any { card -> canPlayCard(card, stackID = 0) || canPlayCard(card, stackID = 1) }
    }
}
