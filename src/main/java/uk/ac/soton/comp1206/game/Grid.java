package uk.ac.soton.comp1206.game;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.CookieHandler;

/**
 * The Grid is a model which holds the state of a game board. It is made up of a set of Integer values arranged in a 2D
 * arrow, with rows and columns.
 * Each value inside the Grid is an IntegerProperty can be bound to enable modification and display of the contents of
 * the grid.
 * The Grid contains functions related to modifying the model, for example, placing a piece inside the grid.
 * The Grid should be linked to a GameBoard for it's display.
 */
public class Grid {

    private static final Logger logger = LogManager.getLogger(Grid.class);
    /**
     * The number of columns in this grid
     */
    private final int cols;

    /**
     * The number of rows in this grid
     */
    private final int rows;

    /**
     * The grid is a 2D arrow with rows and columns of SimpleIntegerProperties.
     */
    private final SimpleIntegerProperty[][] grid;

    /**
     * Create a new Grid with the specified number of columns and rows and initialise them
     *
     * @param cols number of columns
     * @param rows number of rows
     */
    public Grid(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;

        //Create the grid itself
        grid = new SimpleIntegerProperty[cols][rows];

        //Add a SimpleIntegerProperty to every block in the grid
        for (var y = 0; y < rows; y++) {
            for (var x = 0; x < cols; x++) {
                grid[x][y] = new SimpleIntegerProperty(0);
            }
        }
    }

    /**
     * Get the Integer property contained inside the grid at a given row and column index. Can be used for binding.
     *
     * @param x column
     * @param y row
     * @return the IntegerProperty at the given x and y in this grid
     */
    public IntegerProperty getGridProperty(int x, int y) {
        return grid[x][y];
    }

    /**
     * Update the value at the given x and y index within the grid
     *
     * @param x     column
     * @param y     row
     * @param value the new value
     */
    public void set(int x, int y, int value) {
        grid[x][y].set(value);
    }

    /**
     * Get the value represented at the given x and y index within the grid
     *
     * @param x column
     * @param y row
     * @return the value
     */
    public int get(int x, int y) {
        try {
            //Get the value held in the property at the x and y index provided
            return grid[x][y].get();
        } catch (ArrayIndexOutOfBoundsException e) {
            //No such index
            return -1;
        }
    }

    /**
     * Get the number of columns in this game
     *
     * @return number of columns
     */
    public int getCols() {
        return cols;
    }

    /**
     * Get the number of rows in this game
     *
     * @return number of rows
     */
    public int getRows() {
        return rows;
    }


    /**
     * Check if the squares around the block makeup are full or not to see if we can place the block
     * Also check if any squares will be out of bounds
     * @param gamePiece GamePiece
     * @param xCoordinate coord
     * @param yCoordinate coord
     * @return T/F if we can play it or not
     */
    public boolean canPlayPiece(GamePiece gamePiece, int xCoordinate, int yCoordinate) {
        logger.info("canPlayPiece was called with parameters " + gamePiece);
        boolean canPlayPiece = true;
        int[][] blockMakeUp = gamePiece.getBlocks();
        for (int i = 0; i < blockMakeUp.length; i++) { // Loop through each row
            for (int j = 0; j < blockMakeUp[i].length; j++) { // Loop through each column of the row
                if ((blockMakeUp[i][j] > 0 && get(i+(xCoordinate-1), j+(yCoordinate-1)) > 0) ||
                        (blockMakeUp[i][j] >0 && get(i+(xCoordinate-1),j+(yCoordinate-1)) ==-1))
                {
                    canPlayPiece = false;
                }
            }
        }
        logger.info("canPlayPiece returned the value " + canPlayPiece);
        return canPlayPiece;
    }

    /**
     * PlayPiece
     * If we can play the piece, then change all the surrounding values to the value of that piece and call the afterpiece method
     * @param gamePiece GamePiece we are placing
     * @param xCoordinate coord
     * @param yCoordinate coord
     *
     *
     */

    public void playPiece(GamePiece gamePiece, int xCoordinate, int yCoordinate) {
        logger.info("playPiece method was called on "+gamePiece+ " with centre X: "+xCoordinate+" Y: "+yCoordinate);
        int[][] blockMakeUp = gamePiece.getBlocks();
        int pieceValue = gamePiece.getValue();
        for (int i = 0; i < blockMakeUp.length; i++) { // Loop through each row
            for (int j = 0; j < blockMakeUp[i].length; j++) { // Loop through each column of the row
                if (blockMakeUp[i][j] > 0) {
                    set(i+(xCoordinate-1), j+(yCoordinate-1), pieceValue);
                    logger.info("playPiece method has placed a block at X: "+i+(xCoordinate-1) + " Y: "+j+(yCoordinate-1));
                }
            }
        }
    }
    /**Play piece method
     * @Param GamePiece
     * @Param xCoordinate
     * @Param yCoordinate
     * Aligns the centre of the block to the chosen coordinate of the grid and iterates through the grid changing all the values to the piece value.
     * */

}