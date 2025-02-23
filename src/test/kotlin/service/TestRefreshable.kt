package service

import entity.*

import gui.Refreshable

/**
 * [Refreshable] implementation that refreshes nothing, but remembers
 * if a refresh method has been called (since last [reset])
 */
class TestRefreshable: Refreshable {

    var refreshAfterStartNewGameCalled: Boolean = false
        private set

    var refreshAfterNextTurnCalled: Boolean = false
        private set

    var refreshAfterEndTurnCalled: Boolean = false
        private set

    var refreshAfterSwapCardsCalled: Boolean = false
        private set

    var refreshAfterPlayCardCalled: Boolean = false
        private set

    var refreshAfterDrawCardCalled: Boolean = false
        private set

    var refreshAfterPassCalled: Boolean = false
        private set

    var refreshAfterGameEndCalled: Boolean = false
        private set

    /**
     * resets all *Called properties to false
     */
    fun reset() {
        refreshAfterStartNewGameCalled = false
        refreshAfterNextTurnCalled = false
        refreshAfterEndTurnCalled = false
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

    override fun refreshAfterEndTurn() {
        refreshAfterEndTurnCalled = true
    }

    override fun refreshAfterSwapCards(player: Player) {
        refreshAfterSwapCardsCalled = true
    }

    override fun refreshAfterPlayCard(card: Card, stackID: Int) {
        refreshAfterPlayCardCalled = true
    }

    override fun refreshAfterDrawCard(player: Player) {
        refreshAfterDrawCardCalled = true
    }

    override fun refreshAfterPass(player: Player) {
        refreshAfterPassCalled = true
    }
    override fun refreshAfterGameEnd() {
        refreshAfterGameEndCalled = true
    }


}