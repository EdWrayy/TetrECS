package uk.ac.soton.comp1206.game;

import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.event.DieMessageListener;
import uk.ac.soton.comp1206.event.RequestPieceListener;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.scene.MultiplayerScene;
import uk.ac.soton.comp1206.ui.GameWindow;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;


/**
 * The multiplayer version of the game
 * All pieces come from the server, so each player gets the same order of pieces
 * This is managed by a queue
 */


public class MultiplayerGame extends Game{


    /**
     * The queue which holds upcoming gamePieces in order
     */
    public Queue<GamePiece> piecesQueue;

    /**
     * The ArrayList which holds a players name and score in a pair
     */

    public ArrayList<Pair<String, Integer>> playersData= new ArrayList<Pair<String,Integer>>();

    RequestPieceListener requestPieceListener;

    DieMessageListener dieMessageListener;

    private static final Logger logger = LogManager.getLogger(MultiplayerGame.class);

    /**
     * Create a new game with the specified rows and columns. Creates a corresponding grid model.
     * Initializes the queue of pieces
     * @param cols number of columns
     * @param rows number of rows
     */
    public MultiplayerGame(int cols, int rows) {
        super(cols, rows);
        piecesQueue = new ConcurrentLinkedQueue<GamePiece>();
    }

    /**
     * Sets the request piece listener
     * @param requestPieceListener  will be a method reference in the multiplayerScene
     * */

    public void setRequestPieceListener(RequestPieceListener requestPieceListener){
        this.requestPieceListener = requestPieceListener;
    }

    /**
     * Sets the dieMessageListener
     * @param dieMessageListener  will be a method reference in the multiplayerScene
     * */

    public void setDieMessageListener(DieMessageListener dieMessageListener){
        this.dieMessageListener = dieMessageListener;
    }

    /**
     * Overrides the empty method in the Game class which is called after a piece is played or gameLoop is called
     * Calls the request piece listener
     * */
    @Override
    public void getPieceMultiplayer(){
        requestPieceListener.requestPiece();
        logger.info("Piece request sent to listener");
    }

    /**
     * Overrides the empty method in the Game class called when the game ends
     * Calls the dieMessageListener
     * */
    @Override
    public void sendDieMessage(){
        dieMessageListener.sendDieMessage();
    }

    /**
     * Creates a new piece with this value and enqueues it
     * Called by the multiplayerScene class when a new piece is received
     * @param pieceValue the value of a piece received from the communicator
     */
    public void enquePiece(int pieceValue){
        GamePiece newPiece = GamePiece.createPiece(pieceValue);
        piecesQueue.add(newPiece);
        logger.info("Enqueued piece "+pieceValue +" Queue size now: " +piecesQueue.size());
    }

    /**
     * Overrides the spawn piece method in the Game class
     * @return GamePiece the new gamepiece
     * Instead of the game generating a random piece when we need a new one, we take the one at the front of the queue
     * */
    @Override
    public GamePiece spawnPiece(){
        return piecesQueue.poll();
    }







}
