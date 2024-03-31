package uk.ac.soton.comp1206.game;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

/**
 * The Game class handles the main logic, state and properties of the TetrECS game. Methods to manipulate the game state
 * and to handle actions made by the player should take place inside this class.
 */
public class Game {


    private static final Logger logger = LogManager.getLogger(Game.class);

    /**
     * Number of rows
     */
    protected final int rows;

    /**
     * Number of columns
     */
    protected final int cols;

    /**
     * The grid model linked to the game
     */
    protected final Grid grid;

    GamePiece currentPiece;


    /**UI Properties*/

    private IntegerProperty score = new SimpleIntegerProperty(this, "score", 0);
    private IntegerProperty level = new SimpleIntegerProperty(this, "level", 0 );
    private IntegerProperty lives = new SimpleIntegerProperty(this, "lives", 3);
    private IntegerProperty multiplier = new SimpleIntegerProperty(this, "multiplier", 1);

    public IntegerProperty getScoreProperty(){
        return score;
    }
    /**Can return the object for binding*/
    public int getScoreValue(){
        return score.get();
    }
    /**Can return the value of the object*/
    public void setScore(int score) {
        this.score.set(score);
    }
    /**Can set the value of the score*/

    /**Now define the setters and getters for all other bindable properties*/
    public IntegerProperty getLevelProperty(){
        return level;
    }
    public int getLevelValue(){
        return level.get();
    }
    public void setLevel(int level) {
        this.level.set(level);
    }
    public IntegerProperty getLivesProperty(){
        return lives;
    }
    public int getLivesValue(){
        return lives.get();
    }
    public void setLives(int lives) {
        this.lives.set(lives);
    }
    public IntegerProperty getMultiplierProperty(){
        return multiplier;
    }
    public int getMultiplierValue(){
        return multiplier.get();
    }
    public void setMultiplier(int multiplier) {
        this.multiplier.set(multiplier);
    }






    /**
     * Create a new game with the specified rows and columns. Creates a corresponding grid model.
     *
     * @param cols number of columns
     * @param rows number of rows
     */
    public Game(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;

        //Create a new grid model to represent the game state
        this.grid = new Grid(cols, rows);
        currentPiece = spawnPiece();
    }

    /**
     * Start the game
     */
    public void start() {
        logger.info("Starting game");
        initialiseGame();
    }

    /**
     * Initialise a new game and set up anything that needs to be done at the start
     */
    public void initialiseGame() {
        logger.info("Initialising game");
    }

    /**
     * Handle what should happen when a particular block is clicked
     *
     * @param gameBlock the block that was clicked
     */
    public void blockClicked(GameBlock gameBlock) {
        //Get the position of this block
        int x = gameBlock.getX();
        int y = gameBlock.getY();

        //Get the new value for this block
        int previousValue = grid.get(x, y);
        int newValue = previousValue + 1;
        if (newValue > GamePiece.PIECES) {
            newValue = 0;
        }

        //Update the grid with the new value
        grid.set(x, y, newValue);

        if(grid.canPlayPiece(currentPiece, x, y)) {
            grid.playPiece(currentPiece, x, y);
        }
        nextPiece(spawnPiece());
        afterPiece();
    }

    /**
     * Get the grid model inside this game representing the game state of the board
     *
     * @return game grid model
     */
    public Grid getGrid() {
        return grid;
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


    //Where my code begins


    /**Spawn piece method
     *
     * @return GamePiece
     */
    public GamePiece spawnPiece() {
        Random random = new Random();
        int lowerBound = 0;
        int upperBound = GamePiece.PIECES;
        int pieceNumber = lowerBound + random.nextInt(upperBound - lowerBound);
        logger.info("spawnPiece was called and generated piece number" + pieceNumber);
        return GamePiece.createPiece(pieceNumber);
    }

    /**nextPiece method
     * @param nextPiece
     */

    public void nextPiece(GamePiece nextPiece) {
        logger.info("nextPiece method has been called with "+nextPiece);
        this.currentPiece = nextPiece;
    }

    /**afterPiece method*/

    public void afterPiece() {
        logger.info("afterPiece method has been called");
        int numberOfLinesCleared = 0;
        int numberOfBlocksCleared = 0;
        int numberOfRows = grid.getRows();
        int numberOfCols = grid.getCols();


        /**
         * First find the full columns
         * Loop through each element of the first row
         * Loop through each column of the first row
         * If any of the numbers in the column are empty, then the column is not full
         * Save position of the top row of which the column is free
         * */

        ArrayList<Integer> fullColumns = new ArrayList<Integer>();
        for (int i = 0; i < numberOfCols; i++) {
            boolean columnIsFull = true;
            for (int j = 0; j < numberOfRows; j++) {
            if(grid.get(i,j) == 0){
                columnIsFull = false;
            }
            }
            if(columnIsFull){fullColumns.add(i);} //
            numberOfLinesCleared++;
        }


        /**Now we need to do the same for the full rows*/
        ArrayList<Integer> fullRows = new ArrayList<Integer>();
        for (int j = 0; j < numberOfRows; j++) {
            boolean rowIsFull = true;
            for (int i = 0; i < numberOfCols; i++) {
                if(grid.get(i,j) == 0){
                    rowIsFull = false;
                }
            }
            if(rowIsFull){fullRows.add(j);}
            numberOfLinesCleared++;
        }

        /**Now clear the full columns if they are not already cleared and increment numberOfBlocksCleared*/
        Iterator<Integer> columnIterator = fullColumns.iterator();
        while(columnIterator.hasNext()){
            int horizontalPosition = columnIterator.next();
            for(int j = 0; j < numberOfRows; j++){
                if(grid.get(horizontalPosition,j) != 0) {
                    grid.set(horizontalPosition, j, 0);
                    numberOfBlocksCleared++;
                }
            }
        }

        /**Now clear the full rows if they are not already cleared and increment numberOfBlocksCleared*/
        Iterator<Integer> rowIterator = fullRows.iterator();
        while(rowIterator.hasNext()){
            int verticalPosition = rowIterator.next();
            for(int i = 0; i < numberOfCols; i++){
                if(grid.get(verticalPosition,i)!=0){
                    grid.set(verticalPosition, i, 0);
                    numberOfBlocksCleared++;
                }
            }
        }
        logger.info("afterPiece method has cleared "+numberOfLinesCleared+" lines and "+numberOfBlocksCleared+ " blocks.");
        score(numberOfLinesCleared, numberOfBlocksCleared);

        if(numberOfLinesCleared >0){
            setMultiplier(getMultiplierValue()+1);
        }
        else{
            setMultiplier(1);
        }
        /**Handle the increase of the multiplier*/

        double level = Math.floor((double) getScoreValue() /1000);
        int levelInt = (int)level;
        setLevel(levelInt);
    }



    public void score(int numberOfLines, int numberOfBlocks){
        int scoreToAdd = numberOfLines * numberOfBlocks * 10 * getMultiplierValue();
        setScore(getScoreValue()+scoreToAdd);
    }
}