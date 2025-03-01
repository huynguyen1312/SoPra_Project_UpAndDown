package gui

import entity.Card
import entity.CardSuit
import entity.CardValue
import entity.Player
import service.RootService
import tools.aqua.bgw.components.ComponentView
import tools.aqua.bgw.components.container.CardStack
import tools.aqua.bgw.components.container.LinearLayout
import tools.aqua.bgw.components.gamecomponentviews.CardView
import tools.aqua.bgw.components.layoutviews.Pane
import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.Alignment
import tools.aqua.bgw.core.BoardGameScene
import tools.aqua.bgw.util.BidirectionalMap
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import java.awt.Color
import kotlin.concurrent.fixedRateTimer

/**
 * Scene for the actual game.
 *
 * @param rootService The root service to which this scene belongs
 * @property cards map from cards point to cardView
 * @property cardImageLoader Provides images for cards
 * @property overlayPane This pane is used to overlay the game scene with information or prompts for
 *                       wishing a suit, drawing cards or skipping a turn
 * @property currentPlayerHand View of current player's hand cards
 * @property currentPlayerName Current player's name
 * @property currentDrawStack Current player's draw stack
 * @property currentDrawStackCount Number of cards in current player's draw stack
 * @property opponentHand View of opponent player's hand cards
 * @property opponentName Opponent player's name
 * @property opponentDrawStack Opponent player's draw stack
 * @property opponentDrawStackCount Number of cards in opponent player's draw stack
 * @property playStack1 Left center play stack
 * @property playStack2 Right center play stack
 *
 * @property nextButton Button for activating new turn
 * @property drawButton Button for drawing new card from draw stack
 * @property swapButton Button for swapping cards from player's draw stack and hand cards
 * @property passButton Button for passing the current turn
 *
 *
 */
class GameScene(private val rootService: RootService) :
    BoardGameScene(1920, 1080, background = ColorVisual(Color(0xEDEFEF))), Refreshable {
    /**
     * BidirectionalMap is a data structure that allows to map keys to values in both directions
     * In this case, it is used to map Card objects to CardView objects
     */
    private val cards: BidirectionalMap<Card, CardView> = BidirectionalMap()

    private val cardImageLoader = CardImageLoader()

    private fun createdFullCardsStack(){
        // Initializes the CardView objects for all cards in the deck and adds them to the BidirectionalMap "cards"
        cards.clear()

        CardValue.entries.forEach { value ->
            CardSuit.entries.forEach { suit ->
                cards[Card(suit, value)] = CardView(
                    posX = 0,
                    posY = 0,
                    width = 162,
                    height = 250,
                    front = cardImageLoader.frontImageFor(suit, value),
                    back = cardImageLoader.backImage
                )
            }
        }
    }

    /**
     * This pane is used to overlay the game scene with information or prompts for
     * wishing a suit, drawing cards or skipping a turn
     */
    private val overlayPane = Pane<ComponentView>(
        posX = 0,
        posY = 0,
        width = 1920,
        height = 1080,
        visual = ColorVisual(Color(12, 32, 39, 240))
    ).apply {
        isVisible = false
    }

    /**
     * This LinearLayout is used to display the player's hand
     */
    private val currentPlayerHand = LinearLayout<CardView>(
        posX = 0,
        posY = 1080 - 275,
        width = 1920,
        height = 200,
        alignment = Alignment.CENTER,
        spacing = -60
    )

    /**
     * This Label is used to display the current player's name
     */
    private val currentPlayerName = Label(
        posX = 1920 - 250,
        posY = 1080 - 100,
        width = 200,
        height = 50,
        text = "Spieler",
        alignment = Alignment.CENTER,
        visual = ColorVisual(Color(0x0C2027)),
        font = Font(22, Color(0xFFFFFFF), "JetBrains Mono ExtraBold")
    ).apply {
        isVisible = true
    }

    /**
     * This LinearLayout is used to display the opponent's hand
     */
    private val opponentHand = LinearLayout<CardView>(
        posX = 0,
        posY = 50,
        width = 1920,
        height = 200,
        alignment = Alignment.TOP_CENTER,
        spacing = -60
    ).apply {
        rotation = 180.0
    }

    /**
     * This Label is used to display the current player's name
     */
    private val opponentName = Label(
        posX = 150 ,
        posY = 125,
        width = 200,
        height = 50,
        text = "Spieler",
        alignment = Alignment.CENTER,
        visual = ColorVisual(Color(0x0C2027)),
        font = Font(22, Color(0xFFFFFFF), "JetBrains Mono ExtraBold")
    ).apply {
        rotation = 180.0
        isVisible = true
    }

    /**
     * This CardStack is used to display the top card of the play stack
     */
    private val playStack1 = CardStack<CardView>(
        posX = 1920 / 2 - 81 + 150,
        posY = 1080 / 2 - 155,
        width = 162,
        height = 250,
        alignment = Alignment.TOP_CENTER
    ).apply {
        // A dropAcceptor function checks if the dragged card is valid to be played
        // and consumes the dragged element if true
        dropAcceptor = { dragEvent ->
            when (dragEvent.draggedComponent) {
                // If the dragged component is a CardView, the dropAcceptor checks if the card is valid
                is CardView -> {
                    // The card is looked up in the BidirectionalMap "cards" and checked
                    val card = cards.backward(dragEvent.draggedComponent as CardView)
                    // If the card is valid, the card can be dropped and played
                    // Otherwise, the card is not accepted and the dragEvent is consumed (dragged element is reset)
                    rootService.playerActionService.canPlayCard(card, 0)
                }
                else -> {
                    displayErrorMessage("Kannst diese Karte nicht spielen")
                    false
                }
            }
        }

        // The onDragDropped event handler plays the card if it is valid
        onDragDropped = { event ->
            // Get the CardView from the drag event and look up
            // the corresponding Card in the BidirectionalMap "cards"
            val cardView = event.draggedComponent as CardView
            val card = cards.backward(cardView)

            // Play the card
            rootService.playerActionService.playCard(card,0)
        }
    }

    /**
     * This CardStack is used to display the draw stack
     */
    private val playStack2 = CardStack<CardView>(
        posX = 1920 / 2 - 81 - 150,
        posY = 1080 / 2 - 155,
        width = 162,
        height = 250,
        alignment = Alignment.TOP_CENTER
    ).apply {
        // A dropAcceptor function checks if the dragged card is valid to be played
        // and consumes the dragged element if true
        dropAcceptor = { dragEvent ->
            when (dragEvent.draggedComponent) {
                // If the dragged component is a CardView, the dropAcceptor checks if the card is valid
                is CardView -> {
                    // The card is looked up in the BidirectionalMap "cards" and checked
                    val card = cards.backward(dragEvent.draggedComponent as CardView)
                    // If the card is valid, the card can be dropped and played
                    // Otherwise, the card is not accepted and the dragEvent is consumed (dragged element is reset)
                    rootService.playerActionService.canPlayCard(card, 1)
                }
                else -> {
                    displayErrorMessage("Kannst diese Karte nicht spielen")
                    false
                }
            }
        }

        // The onDragDropped event handler plays the card if it is valid
        onDragDropped = { event ->
            // Get the CardView from the drag event and look up
            // the corresponding Card in the BidirectionalMap "cards"
            val cardView = event.draggedComponent as CardView
            val card = cards.backward(cardView)

            // Play the card
            rootService.playerActionService.playCard(card,1)
        }
    }
    /**
     * Draw stack from 1st player
     */
    private val currentDrawStack = CardView(
        posX = 200,
        posY = 700,
        width = 162,
        height = 250,
        front = cardImageLoader.backImage
    )

    /**
     * Displays the remaining cards from 1st player's draw stack  in the game.
     */
    private val currentDrawStackCount = Label(
        posX = 200,
        posY = 850,
        width = 162,
        height = 250,
        text = "",  // Example count
        font = Font(18, Color(0x000000), "JetBrains Mono ExtraBold")
    )

    /**
     * Draw stack from 2nd player
     */
    private val opponentDrawStack = CardView(
        posX = 1550,
        posY = 150,
        width = 162,
        height = 250,
        front = cardImageLoader.backImage
    ).apply{
        rotation = 180.0
    }

    /**
     * Displays the remaining cards from 2nd player's draw stack in the game.
     */
    private val opponentDrawStackCount = Label(
        posX = 1550,
        posY = 0,
        width = 162,
        height = 250,
        text = "",  // Example count
        font = Font(18, Color(0x000000), "JetBrains Mono ExtraBold")
    ).apply {
        rotation = 180.0
    }

    /**
     * Button for activating new turn
     */
    private val nextButton = Button(
        text = "Aufdecken",
        width = 200,
        height = 40,
        posX = 1700,
        posY = 540,
        font = Font(22, Color(0xFFFFFFF), "JetBrains Mono ExtraBold"),
        visual = ColorVisual(Color(0x49585D))
    ).apply{
        rotation = 270.0
        onMouseClicked = {
            rootService.gameService.nextTurn()
        }
    }
    /**
     * Displays an error message if the player tries to do something that is not allowed
     */
    private val errorMessage = Button(
        width = 450,
        height = 100,
        posX = 49,
        posY = 400,
        font = Font(size = 24),
        visual = ColorVisual(Color(0xEDEFEF))
    ).apply {
        onMouseClicked = {
            this.isVisible = false
        }
        fixedRateTimer("hideError", false, 0, 5000) {
            this@apply.isVisible = false
        }
    }
    /**
     * Displays an error message.
     */
    private fun displayErrorMessage(message: String) {
        errorMessage.text = message
        errorMessage.isVisible = true
    }
    /**
     * Creates a button for drawing cards in the game.
     */
    private val drawButton = Button(
        text = "Ziehen",
        width = 200,
        height = 40,
        posX = 20,
        posY = 800,
        font = Font(22, Color(0xFFFFFFF), "JetBrains Mono ExtraBold"),
        visual = ColorVisual(Color(0x49585D))
    ).apply{
        rotation = 270.0
        onMouseClicked = {
            if (rootService.playerActionService.canDrawCard() && !this@GameScene.nextButton.isVisible) {
                rootService.playerActionService.drawCard()
            } else {
                displayErrorMessage("Kannst nicht Karte ziehen")
            }
        }
        isWrapText = true
    }

    /**
     * Creates a button for swapping cards in the game.
     */
    private val swapButton = Button(
        text = "Ersetzen",
        width = 200,
        height = 40,
        posX = 180,
        posY = 600,
        font = Font(22, Color(0xFFFFFFF), "JetBrains Mono ExtraBold"),
        visual = ColorVisual(Color(0x49585D))
    ).apply{
        onMouseClicked = {
            if (rootService.playerActionService.canSwapCard() && !this@GameScene.nextButton.isVisible) {
                rootService.playerActionService.swapCard()
            } else {
                displayErrorMessage("Kannst nicht ersetzen")
            }
        }
        isWrapText = true
    }

    private val passButton = Button(
        text = "Passen",
        width = 200,
        height = 40,
        posX = 1700,
        posY = 540,
        font = Font(22, Color(0xFFFFFFF), "JetBrains Mono ExtraBold"),
        visual = ColorVisual(Color(0x49585D))
    ).apply{
        rotation = 270.0
        onMouseClicked = {
            if (rootService.playerActionService.canPass() && !this@GameScene.nextButton.isVisible) {
                rootService.playerActionService.pass()
            } else {
                displayErrorMessage("Kannst nicht passen")
            }
        }
        isVisible = false
    }
    /**
     * Initialize the scene and all components to it
     */
    init {
        // Add all components to the scene
        addComponents(
            currentPlayerHand,
            currentPlayerName,
            opponentHand,
            opponentName,
            playStack1,
            playStack2,
            currentDrawStack,
            currentDrawStackCount,
            opponentDrawStack,
            opponentDrawStackCount,
            drawButton,
            swapButton,
            passButton,
            overlayPane,
            errorMessage,
            nextButton
        )
    }

    /**
     *   The refreshAfterGameStart method is called by the service layer after a new game has been started.
     *   It clears the playStack and adds the top card of the play stack to it,
     *   by looking up the corresponding CardView in the BidirectionalMap "cards".
     */
    override fun refreshAfterStartNewGame() {
        // Get the current game from the rootService and return if no game is currently active
        val game = checkNotNull(rootService.currentGame)

        // Initializes the CardView objects for all cards in the deck and adds them to the BidirectionalMap "cards"
        createdFullCardsStack()

        // Clear the playStack and add the top card of the play stack to it
        playStack1.clear()
        playStack2.clear()
        playStack1.add(cards[game.centerStack1.last()] as CardView)
        playStack2.add(cards[game.centerStack2.last()] as CardView)
        // Show the front of the top card of the play stack
        playStack1.peek().showFront()
        playStack2.peek().showFront()

        // Show player's names
        currentPlayerName.text = game.player1.name
        opponentName.text = game.player2.name

        // Generate hand cards for 2 players and show hand cards from current player
        currentPlayerHand.clear()
        opponentHand.clear()
        game.player1.handCards.forEach { card ->
            currentPlayerHand.add(cards[card] as CardView)
        }
        game.player2.handCards.forEach { card ->
            opponentHand.add(cards[card] as CardView)
        }

        currentDrawStackCount.text = "${game.player1.drawStack.size} remaining"
        opponentDrawStackCount.text = "${game.player2.drawStack.size} remaining"

        //show nextTurn Button
        nextButton.isVisible = true
        swapButton.isVisible = false
        drawButton.isVisible = false

        //reset Error Message
        errorMessage.isVisible = false
    }

    /**
     * Refreshes the game state after transitioning to the next turn.
     * This function updates the UI elements and player interactions accordingly.
     *
     * - Hides the `nextButton` to prevent skipping ahead.
     * - Reveals all cards in the `currentPlayerHand` and makes them draggable.
     * - Checks if the player can pass their turn and displays the `passButton` if allowed.
     * - Ensures the `swapButton` is visible for card swapping.
     */
    override fun refreshAfterNextTurn() {
        //initialize turn
        nextButton.isVisible = false
        currentPlayerHand.forEach{
                cardView -> cardView.showFront()
                cardView.isDraggable = true
        }
        if (rootService.playerActionService.canPass()) {
            passButton.isVisible = true
        }
        swapButton.isVisible = true
        drawButton.isVisible = true
    }

    /**
     * Handles the transition between player turns in the game.
     *
     * This function performs several key tasks:
     * 1. Pauses briefly to allow players to see the last played card.
     * 2. Hides the current player's hand by flipping their cards and making them non-draggable.
     * 3. Updates button visibility, disabling the pass and swap buttons while enabling the next turn button.
     * 4. Switches hands between players, ensuring the new active player sees their updated hand.
     *
     * Behavior:
     * - Flips all cards in `currentPlayerHand` to their back side.
     * - Disables card dragging.
     * - Updates UI buttons to reflect turn transition.
     * - Swaps hands and  names between players, ensuring the new player sees their name and cards in
     * correct direction.
     * - Makes all new cards in hand visible.
     *
     * @throws IllegalStateException if no game is currently active.
     */
    private fun endTurn() {
        val game = checkNotNull(rootService.currentGame){"No game currently running."}
        //wait for a moment to see the played card
        Thread.sleep(1000)
        //flip hand
        currentPlayerHand.forEach{
                cardView -> cardView.showBack()
                cardView.isDraggable = false
        }
        //make all buttons invisible
        passButton.isVisible = false
        swapButton.isVisible = false
        drawButton.isVisible = false
        nextButton.isVisible = true

        currentPlayerHand.clear()
        opponentHand.clear()

        //rerender Hands from players to swap
        val currentPlayer = game.currentPlayer
        val opponentPlayer = game.players[(game.players.indexOf(currentPlayer)+1)%2]

        opponentPlayer.handCards.forEach{
            card -> currentPlayerHand.add(cards[card] as CardView)
        }
        currentPlayer.handCards.forEach{
            card -> opponentHand.add(cards[card] as CardView)
        }
        currentPlayerName.text = opponentPlayer.name
        opponentName.text = currentPlayer.name
        currentDrawStackCount.text = "${opponentPlayer.drawStack.size} remaining"
        opponentDrawStackCount.text = "${currentPlayer.drawStack.size} remaining"
    }

    /**
     * Updates the game state after a player plays a card.
     *
     * @param player The player who played the card.
     * @param stackID The stack where the card was placed (0 for `playStack1`, 1 for `playStack2`).
     *
     * Behavior:
     * - Retrieves the current game instance.
     * - Determines the correct stack based on `stackID`.
     * - Moves the last card from the corresponding `centerStack` to the appropriate `playStack`.
     * - Ensures the played card is displayed on the top of the stack.
     * - Removes the played card from the `currentPlayerHand`.
     */
    override fun refreshAfterPlayCard(player: Player, stackID: Int) {
        // Get the current game from the rootService and return if no game is currently active
        val game = checkNotNull(rootService.currentGame)
        when(stackID){
            0 -> {
                playStack1.push(cards[game.centerStack1.last()] as CardView)
                playStack1.peek().showFront()
                currentPlayerHand.remove(cards[game.centerStack1.last()] as CardView)
            }
            1 -> {
                playStack2.push(cards[game.centerStack2.last()] as CardView)
                playStack2.peek().showFront()
                currentPlayerHand.remove(cards[game.centerStack2.last()] as CardView)
            }
        }
        endTurn()
    }

    /**
     * Updates the game state after a player swaps their cards.
     *
     * @throws IllegalStateException if there is no active game in `rootService`.
     *
     * Behavior:
     * - Retrieves the current game instance from `rootService`.
     * - Clears the `currentPlayerHand` to prepare for updated cards.
     * - Iterates through `handCards` of the `currentPlayer` and adds them to `currentPlayerHand`.
     * - Updates the `currentDrawStackCount` text to reflect the remaining draw pile size.
     * - Ensures that all cards in `currentPlayerHand` are displayed face-up.
     */
    override fun refreshAfterSwapCards() {
        // Get the current game from the rootService and return if no game is currently active
        val game = checkNotNull(rootService.currentGame)
        //update Hand
        currentPlayerHand.clear()

        //update Hand
        game.currentPlayer.handCards.forEach { card ->
            currentPlayerHand.add(cards[card] as CardView)
        }
        currentDrawStackCount.text = "${game.currentPlayer.drawStack.size} remaining"

        // Show the front of all cards in current player's hand
        currentPlayerHand.forEach{
                cardView -> cardView.showFront()
        }
        endTurn()
    }

    /**
     * Updates the game state after a player draws a card.
     *
     * @param card The `Card` that was drawn by the player.
     * @throws IllegalStateException if no active game exists in `rootService`.
     *
     * Behavior:
     * - Retrieves the current game instance from `rootService`.
     * - Finds the corresponding `CardView` for the drawn card from the `cards` mapping.
     * - Displays the front of the drawn card.
     * - Adds the drawn `CardView` to `currentPlayerHand`.
     * - Updates the `currentDrawStackCount` text to reflect the remaining draw pile size.
     */
    override fun refreshAfterDrawCard(card: Card) {
        // Get the current game from the rootService and return if no game is currently active
        val game = checkNotNull(rootService.currentGame)

        //Look up the corresponding CardView in the BidirectionalMap "cards" and add hover effects
        val cardView = cards[card] as CardView
        cardView.showFront()

        //update Hand
        currentPlayerHand.add(cardView)

        currentDrawStackCount.text = "${game.currentPlayer.drawStack.size} remaining"

        endTurn()
    }

    /**
     * Handles the game state update after a player chooses to pass their turn.
     *
     * @param player The `Player` who has chosen to pass.
     *
     * Behavior:
     * - Ends the current player's turn by calling `endTurn()`.
     */
    override fun refreshAfterPass(player: Player) {
        endTurn()
    }
}
