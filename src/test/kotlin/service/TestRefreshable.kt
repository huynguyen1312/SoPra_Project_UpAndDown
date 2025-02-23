package service

import entity.*

import gui.Refreshable

/**
 * [Refreshable] implementation that refreshes nothing, but remembers
 * if a refresh method has been called (since last [reset])
 */
class TestRefreshable: Refreshable {

    var winner: Player? = null
    var isDraw: Boolean? = null
    var stackID: Int? = null

    var refreshAfterStartNewGameCalled: Boolean = false


    var refreshAfterNextTurnCalled: Boolean = false


    var refreshAfterSwapCardsCalled: Boolean = false


    var refreshAfterPlayCardCalled: Boolean = false


    var refreshAfterDrawCardCalled: Boolean = false


    var refreshAfterPassCalled: Boolean = false


    var refreshAfterGameEndCalled: Boolean = false


    /**
     * resets all *Called properties to false
     */
    fun reset() {
        refreshAfterStartNewGameCalled = false
        refreshAfterNextTurnCalled = false
        refreshAfterGameEndCalled = false
        refreshAfterSwapCardsCalled = false
        refreshAfterPlayCardCalled = false
        refreshAfterDrawCardCalled = false
        refreshAfterPassCalled = false
    }

    override fun refreshAfterStartNewGame() {
        refreshAfterStartNewGameCalled = true
    }

    override fun refreshAfterNextTurn() {
        refreshAfterNextTurnCalled = true
    }

    override fun refreshAfterPlayCard(player: Player, stackID: Int) {
        refreshAfterPlayCardCalled = true
        this.stackID = stackID
    }

    override fun refreshAfterSwapCards() {
        refreshAfterSwapCardsCalled = true
    }

    override fun refreshAfterDrawCard(card: Card) {
        refreshAfterDrawCardCalled = true
    }

    override fun refreshAfterPass(player: Player) {
        refreshAfterPassCalled = true
    }
    override fun refreshAfterGameEnd(winner: Player, isDraw: Boolean) {
        refreshAfterGameEndCalled = true
        this.winner = winner
        this.isDraw = isDraw
    }
}