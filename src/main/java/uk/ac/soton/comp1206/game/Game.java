package uk.ac.soton.comp1206.game;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.event.BlockClearedListener;
import uk.ac.soton.comp1206.event.FailToPlaceListener;
import uk.ac.soton.comp1206.event.NextPieceListener;
import uk.ac.soton.comp1206.scene.ChallengeScene;

import java.util.*;

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

    BlockClearedListener blockClearedListener;

    /**
     * Number of columns
     */
    protected final int cols;

    /**
     * The grid model linked to the game
     */
    protected final Grid grid;

    private FailToPlaceListener failToPlaceListener;

    private ObjectProperty<GamePiece> currentPiece = new SimpleObjectProperty<>();

    private ObjectProperty<GamePiece> nextPiece = new SimpleObjectProperty<>();

    /**Temporary piece to set as a piece when swapping*/
    GamePiece temporaryPiece;

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

    public GamePiece getCurrentPiece(){
        return currentPiece.get();
    }

    public GamePiece getNextPiece(){
        return nextPiece.get();
    }

    public ObjectProperty<GamePiece> getCurrentPieceObjectProperty(){
        return currentPiece;
    }
    public ObjectProperty<GamePiece> getNextPieceObjectProperty(){
        return nextPiece;
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
        currentPiece.set(spawnPiece());
        nextPiece.set(spawnPiece());
    }

    public void setFailToPlaceListener(FailToPlaceListener failToPlaceListener){
        this.failToPlaceListener = failToPlaceListener;
    }

    public void setBlockClearedListener(BlockClearedListener blockClearedListener){
        this.blockClearedListener = blockClearedListener;
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
        int blockValue = gameBlock.getValue();


        if(grid.canPlayPiece(getCurrentPiece(), x, y)) {
            grid.playPiece(getCurrentPiece(), x, y);
            afterPiece();
            nextPiece(spawnPiece());
            failToPlaceListener.failedToPlace(false);
        }
        else{failToPlaceListener.failedToPlace(true);}

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
     * @param newPiece
     */

    public void nextPiece(GamePiece newPiece) {
        logger.info("nextPiece method has been called with "+newPiece);
        this.currentPiece.set(getNextPiece());
        this.nextPiece.set(newPiece);
    }

    /**afterPiece method
     *
     */

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
        }

        /**Now clear the full columns if they are not already cleared and increment numberOfBlocksCleared and numberOfLinesCleared*/
        Iterator<Integer> columnIterator = fullColumns.iterator();
        while(columnIterator.hasNext()){
            numberOfLinesCleared++;
            int horizontalPosition = columnIterator.next();
            for(int j = 0; j < numberOfRows; j++){
                if(grid.get(horizontalPosition,j) != 0) {
                    blockClearedListener.blockCleared(horizontalPosition,j);
                    grid.set(horizontalPosition, j, 0);
                    numberOfBlocksCleared++;
                }
            }
        }



        /**Now clear the full rows if they are not already cleared and increment numberOfBlocksCleared*/
        Iterator<Integer> rowIterator = fullRows.iterator();
        while(rowIterator.hasNext()){
            numberOfLinesCleared++;
            int verticalPosition = rowIterator.next();
            for(int i = 0; i < numberOfCols; i++){
                if(grid.get(i, verticalPosition)!=0){
                    blockClearedListener.blockCleared(i, verticalPosition);
                    grid.set(i,verticalPosition, 0);
                    numberOfBlocksCleared++;
                }
            }
        }
        logger.info("afterPiece method has cleared "+numberOfLinesCleared+" lines and "+numberOfBlocksCleared+ " blocks.");
        score(numberOfLinesCleared, numberOfBlocksCleared);
        /**Handle the increase in score*/

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
        /**Handle the change level*/

    }



    public void score(int numberOfLines, int numberOfBlocks){
        int scoreToAdd = numberOfLines * numberOfBlocks * 10 * getMultiplierValue();
        setScore(getScoreValue()+scoreToAdd);
    }

    public void rotateCurrentPieceRight(){
        getCurrentPiece().rotate(1);
    }

    public void rotateCurrentPieceLeft(){
        getCurrentPiece().rotate(3);
    }

    public void swapPieces(){
        this.temporaryPiece = getCurrentPiece();
        this.currentPiece.set(getNextPiece());
        this.nextPiece.set(temporaryPiece);
    }
}