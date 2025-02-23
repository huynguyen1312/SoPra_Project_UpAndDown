package gui

import service.AbstractRefreshingService
import entity.Card
import entity.Player

/**
 * This interface provides a mechanism for the service layer classes to communicate
 * (usually to the view classes) that certain changes have been made to the entity
 * layer, so that the user interface can be updated accordingly.
 *
 * Default (empty) implementations are provided for all methods, so that implementing
 * UI classes only need to react to events relevant to them.
 *
 * @see AbstractRefreshingService
 *
 */
interface Refreshable {

    /**
     * Refreshes the UI or game state when a new game starts.
     * This function is called immediately after a new game is initialized.
     */
    fun refreshAfterStartNewGame() {}

    /**
     * Refreshes the UI or game state after the next turn is taken.
     * This function should be invoked when the turn switches to another player.
     */
    fun refreshAfterNextTurn() {}

    /**
     * Refreshes the UI or game state after a player swaps cards.
     */
    fun refreshAfterSwapCards(){}

    /**
    * Refreshes the UI or game state after a player plays a card.
    * @param player play the card.
    * @param stackID The stack where the card was played.
    */
    fun refreshAfterPlayCard(player: Player,stackID: Int){}

    /**
     * Refreshes the UI or game state after a player draws a card.
     * @param card The card was drawn
     */
    fun refreshAfterDrawCard(card: Card){}

    /**
     * Refreshes the UI or game state after a player passes their turn.
     * @param player The player who passed.
     */
    fun refreshAfterPass(player: Player){}

    /**
     * Refreshes the UI or game state after the game ends.
     * Called when the game reaches a conclusion, handling any necessary clean-up or final updates.
     */
    fun refreshAfterGameEnd(winner: Player, isDraw: Boolean) {}
    /**
     * Refreshes the UI or game state after the game restart.
     */
    fun refreshAfterGameRestart(){}
}
