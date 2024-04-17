package uk.ac.soton.comp1206.game;

import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.event.RequestPieceListener;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.scene.MultiplayerScene;
import uk.ac.soton.comp1206.ui.GameWindow;

import java.util.*;


public class MultiplayerGame extends Game{


    public Queue<GamePiece> piecesQueue;

    public ArrayList<Pair<String, Integer>> playersData= new ArrayList<Pair<String,Integer>>();

    RequestPieceListener requestPieceListener;

    private static final Logger logger = LogManager.getLogger(MultiplayerGame.class);

    /**
     * Create a new game with the specified rows and columns. Creates a corresponding grid model.
     *
     * @param cols number of columns
     * @param rows number of rows
     */
    public MultiplayerGame(int cols, int rows) {
        super(cols, rows);
        piecesQueue = new LinkedList<GamePiece>();
        Random random = new Random();
    }

    public void setRequestPieceListener(RequestPieceListener requestPieceListener){
        this.requestPieceListener = requestPieceListener;
    }

    @Override
    public void getPieceMultiplayer(){
        requestPieceListener.requestPiece();
        logger.info("Piece request sent to listener");
    }
    public void enquePiece(int pieceValue){
        GamePiece newPiece = GamePiece.createPiece(pieceValue);
        piecesQueue.add(newPiece);
        logger.info("Enqueued piece "+pieceValue);
    }

    @Override
    public GamePiece spawnPiece(){
        return piecesQueue.remove();
    }




}
