package uk.ac.soton.comp1206.component;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.game.Grid;
import uk.ac.soton.comp1206.scene.MenuScene;

public class InstructionsPieceBoard extends GameBoard {

    Grid grid;
    int cols;
    int rows;
    double width;
    double height;
    public InstructionsPieceBoard() {
        super(3,3,50,50);
        cols = 3;
        rows = 3;
        width = 50;
        height = 50;
        this.grid = new Grid(cols,rows);
        build();
    }

    /**displayPiece method
     * @Param GamePiece
     * @Return PieceBoard
     * Takes a game piece, and first clears the board, then iterates through the blocks, binding the new value of the gamepiece to the blocks in the pieceboard where necessary
     * Returns this pieceboard once that is done
     */
    private static final Logger logger = LogManager.getLogger(InstructionsPieceBoard.class);
    public InstructionsPieceBoard displayPiece(GamePiece gamePiece){
        int[][] blocks = gamePiece.getBlocks();
        SimpleIntegerProperty valueProperty = new SimpleIntegerProperty(gamePiece.getValue());
        SimpleIntegerProperty emptySquare = new SimpleIntegerProperty(0);

        for(int x = 0; x < blocks.length; x++) {
            for (int y = 0; y < blocks[x].length; y++){
                getBlock(x,y).bind(emptySquare);
            }
        }

        for(int x = 0; x < blocks.length; x++) {
            for (int y = 0; y < blocks[x].length; y++) {
                if(blocks[x][y] > 0){
                    getBlock(x,y).bind(valueProperty);
                }
            }
        }
        getBlock(1,1).addCenterRing();
        logger.info("Piece displayed");
        return this;
    }





}

