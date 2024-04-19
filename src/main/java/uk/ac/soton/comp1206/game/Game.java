package uk.ac.soton.comp1206.game;

import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.event.ActionEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.event.*;
import uk.ac.soton.comp1206.scene.ChallengeScene;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

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

    LifeLostListener lifeLostListener;

    /**
     * Number of columns
     */
    protected final int cols;

    /**
     * The grid model linked to the game
     */
    protected final Grid grid;

    private FailToPlaceListener failToPlaceListener;

    private GameLoopListener gameLoopListener;

    private GameOverListener gameOverListener;

    private ObjectProperty<GamePiece> currentPiece = new SimpleObjectProperty<>();

    private ObjectProperty<GamePiece> nextPiece = new SimpleObjectProperty<>();

    /**Temporary piece to set as a piece when swapping*/
    GamePiece temporaryPiece;

    private int timerDelay;

    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> gameLoopHandle = null;
    private ScheduledFuture<?> progressFuture;

    /**UI Properties*/

    private IntegerProperty score = new SimpleIntegerProperty(this, "score", 0);
    private IntegerProperty level = new SimpleIntegerProperty(this, "level", 0 );
    private IntegerProperty lives = new SimpleIntegerProperty(this, "lives", 3);
    private IntegerProperty multiplier = new SimpleIntegerProperty(this, "multiplier", 1);


    /**
     * Returns score property
     * @return IntegerProperty
     */

    public IntegerProperty getScoreProperty(){
        return score;
    }

    /**
     * Returns scoreValue
     * @return int score
     */
    public int getScoreValue(){
        return score.get();
    }

    /**
     * Sets score value
     * @param score score we are setting
     */
    public void setScore(int score) {
        this.score.set(score);
    }


    /**
     * Returns level property
     * @return integerProperty
     */
    public IntegerProperty getLevelProperty(){
        return level;
    }

    /**
     * Returns level value
     * @return int level
     */

    public int getLevelValue(){
        return level.get();
    }

    /**
     * Sets level
     * @param level level
     */
    public void setLevel(int level) {
        this.level.set(level);
    }

    /**
     * Returns livesproperty
     * @return lives
     */
    public IntegerProperty getLivesProperty(){
        return lives;
    }

    /**
     * Returns lives value
     * @return int lives
     */
    public int getLivesValue(){
        return lives.get();
    }

    /**
     * Sets lives
     * @param lives lives
     */
    public void setLives(int lives) {
        this.lives.set(lives);
    }

    /**
     * Returns multiplier property
     * @return integerProperty
     */
    public IntegerProperty getMultiplierProperty(){
        return multiplier;
    }

    /**
     * Returns multiplier value
     * @return int multiplier
     */
    public int getMultiplierValue(){
        return multiplier.get();
    }

    /**
     * Sets multiplier
     * @param multiplier value
     */
    public void setMultiplier(int multiplier) {
        this.multiplier.set(multiplier);
    }

    /**
     * Returns current GamePiece
     * @return GamePiece
     */
    public GamePiece getCurrentPiece(){
        return currentPiece.get();
    }

    /**
     * Returns next GamePiece
     * @return GamePiece
     */

    public GamePiece getNextPiece(){
        return nextPiece.get();
    }

    /**
     * Returns currentPieceObjectProperty
     * @return ObjectProperty
     */

    public ObjectProperty<GamePiece> getCurrentPieceObjectProperty(){
        return currentPiece;
    }

    /**
     * Returns nextPieceObjectProperty
     * @return ObjectProperty
     */
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
        this.timerDelay = 12000;


        //Create a new grid model to represent the game state
        this.grid = new Grid(cols, rows);

    }

    /**
     * Sets failed to place listener
     * @param failToPlaceListener listener
     */
    public void setFailToPlaceListener(FailToPlaceListener failToPlaceListener){
        this.failToPlaceListener = failToPlaceListener;
    }

    /**
     * Sets blockClearedListener
     * @param blockClearedListener listener
     */
    public void setBlockClearedListener(BlockClearedListener blockClearedListener){
        this.blockClearedListener = blockClearedListener;
    }

    /**
     * Sets gameLoopListener
     * @param gameLoopListener listener
     */

    public void setGameLoopListener(GameLoopListener gameLoopListener){
        this.gameLoopListener = gameLoopListener;
    }

    /**
     * Sets lifeLostListener
     * @param lifeLostListener listener
     */
    public void setLifeLostListener(LifeLostListener lifeLostListener){this.lifeLostListener = lifeLostListener;}

    /**
     * Sets gameOverListener
     * @param gameOverListener listener
     */
    public void setGameOverListener(GameOverListener gameOverListener){this.gameOverListener = gameOverListener;}

    /**
     * Start the game
     * There is a small delay here for the purpose of multiplayer, as sometimes it can take some time to receive pieces from the server.
     */
    public void start() {
        logger.info("Starting game");
        initialiseGame();
        try {
            Thread.sleep(400);
        }
        catch(Exception ignored){}
        currentPiece.set(spawnPiece());
        nextPiece.set(spawnPiece());
    }







    /**
     * Sets up the game loop
     */
    public void initialiseGame() {
        logger.info("Initialising game");
        final Runnable gameLoop = new Runnable()
        {
            public void run() {
                gameLoop();
                logger.info("Gameloop has been called");
            }
        };
        long initialDelay = 12000;
        long interval = getTimerDelay(); // The period between successive executions
        gameLoopHandle = scheduler.scheduleAtFixedRate(gameLoop, initialDelay, interval, TimeUnit.MILLISECONDS);
        startProgressUpdater();
    }


    /**
     * Called when time runs out or a piece is skipped.
     * Loses 1 life, resets the multiplayer and replaces the current piece with a new one.
     * Resets the loop again and calls the checkLives method as lives may now have run out
     * Calls getPieceMultiplayer(), which will request a new piece for multiplayer games.
     */
    public void gameLoop(){
        Platform.runLater(() -> {
            setLives(getLivesValue() - 1);
            lifeLostListener.lifeLost();
            setMultiplier(1);
            currentPiece.set(spawnPiece());
            logger.info("Time ran out, gameloop method has been called");
            if (progressFuture != null && !progressFuture.isCancelled()) {
                progressFuture.cancel(true);
            }
            startProgressUpdater();
            checkLives();
            getPieceMultiplayer();


        });
    }

    /**
     * Regularly calculates the current progress of the gameloop and saves it as a double called progress.
     * Progress will reduce overtime to a minimum of zero, at which point the updater stops until it is restarted by the gameloop method.
     */

    private void startProgressUpdater() {
        final long startTime = System.currentTimeMillis();
        progressFuture = scheduler.scheduleAtFixedRate(() -> {
            long elapsedTime = System.currentTimeMillis() - startTime;
            double progress = Math.max(0.0, (getTimerDelay() - elapsedTime) / (double) getTimerDelay());
            if (gameLoopListener != null) {
                gameLoopListener.progress(progress);
            }
            // Automatically stop the progress updater when time runs out
            if (progress <= 0.0) {
                if (progressFuture != null && !progressFuture.isCancelled()) {
                    progressFuture.cancel(false);
                }
            }
        }, 0, 100, TimeUnit.MILLISECONDS); // Update every 100 milliseconds
    }



    /**
     * Checks if the lives are now less than 0, if so it sets them back to zero just for visuals and ends the game
     * This stops the game loop and sends a die message for multiplayer games
     * The gameOverListener is also called to alert the challengeScene to switch to the scores scene
     */
    private void checkLives(){
        if(getLivesValue()<0){
            setLives(0);
            sendDieMessage(); //Purely for multiplayer
            stopLoop();
            logger.info("Lives below zero, loop stopped");
            //End game
            gameOverListener.gameOver(this);
        }
    }

    /**
     * Forcibly stops the loop for when the game is ended
     */
    public void stopLoop() {
        scheduler.shutdown(); // Shut down the scheduler
        try {
            if (!scheduler.awaitTermination(1, TimeUnit.SECONDS)) {
                scheduler.shutdownNow(); // Force shutdown if not terminated
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
        logger.info("Game loop has been stopped");
    }

    /**
     * Resets the timer for the gameloop
     */

    private void resetTimer(){
        gameLoopHandle.cancel(true);
        // Schedule a new game loop task with the updated delay
        gameLoopHandle = scheduler.scheduleAtFixedRate(this::gameLoop, 12000, getTimerDelay(), TimeUnit.MILLISECONDS);
        if (progressFuture != null && !progressFuture.isCancelled()) {
            progressFuture.cancel(false);
            startProgressUpdater();
        }
    }

    /**
     * Uses the specified formula to calculate how long the player should get to place each piece, based on their current level
     * It cannot get any lower than 2500m
     * @return int timer delay
     */

    public int getTimerDelay(){
        int newTimerDelay = 12000-(500*getLevelValue());
        if(newTimerDelay<=2500){
            this.timerDelay=2500;
            return this.timerDelay;
        }
        else{
            this.timerDelay = newTimerDelay;
            return this.timerDelay;
        }
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
            resetTimer();
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

    /**
     * nextPiece method
     * @param newPiece gamePiece
     */

    public void nextPiece(GamePiece newPiece) {
        logger.info("nextPiece method has been called with "+newPiece);
        this.currentPiece.set(getNextPiece());
        this.nextPiece.set(newPiece);
    }

    /**
     * Handles everything that can happen after a piece is played
     * First find the full columns
     * Loop through each element of the first row
     * Loop through each column of the first row
     * If any of the numbers in the column are empty, then the column is not full
     * Save position of the top row of which the column is free
     * Then do the same for the rows.
     * Then clear all the full rows and columns if they are not already clear, and increment the number of lines and blocks cleared.
     * Call the score method to find the increase in score
     * Increase the multiplier if necessary
     * Increase the level if necessary
     */

    public void afterPiece() {
        logger.info("afterPiece method has been called");
        int numberOfLinesCleared = 0;
        int numberOfBlocksCleared = 0;
        int numberOfRows = grid.getRows();
        int numberOfCols = grid.getCols();


        /**
         *
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


        if(numberOfLinesCleared >0){
            setMultiplier(getMultiplierValue()+1);
        }
        else{
            setMultiplier(1);
        }


        double level = Math.floor((double) getScoreValue() /1000);
        int levelInt = (int)level;
        setLevel(levelInt);

        getPieceMultiplayer();
    }



    /**
     * Uses an algorithm based off these variables and the multiplier to calculate the increase in score after a piece is played
     * @param numberOfLines number of lines cleared
     * @param numberOfBlocks number of blocks cleared
     */
    public void score(int numberOfLines, int numberOfBlocks){
        int scoreToAdd = numberOfLines * numberOfBlocks * 10 * getMultiplierValue();
        setScore(getScoreValue()+scoreToAdd);
    }

    /**
     * Rotates a piece once to the right
     */
    public void rotateCurrentPieceRight(){
        getCurrentPiece().rotate(1);
    }

    /**
     * Rotates a piece once to the left
     */
    public void rotateCurrentPieceLeft(){
        getCurrentPiece().rotate(3);
    }

    /**
     * Swaps the current and next piece
     */
    public void swapPieces(){
        this.temporaryPiece = getCurrentPiece();
        this.currentPiece.set(getNextPiece());
        this.nextPiece.set(temporaryPiece);
    }


    /**
     * Overriden in the multiplayerGame
     * Exists to request a new piece
     */
    public void getPieceMultiplayer(){}

    /**
     * Overriden in the multiplayerGame
     * Exists to send a die message to the server when the game ends
     */
    public void sendDieMessage(){}

}