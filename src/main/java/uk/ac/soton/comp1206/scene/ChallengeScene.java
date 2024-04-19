package uk.ac.soton.comp1206.scene;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBoard;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.event.BlockClearedListener;
import uk.ac.soton.comp1206.event.FailToPlaceListener;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.game.MultiplayerGame;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * The Single Player challenge scene. Holds the UI for the single player challenge mode in the game.
 */
public class ChallengeScene extends BaseScene{


    private static final Logger logger = LogManager.getLogger(MenuScene.class);
    /**
     * The game instance
     */
    protected Game game;

    Label scoreLabel = new Label();
    Label levelLabel = new Label();
    Label multiplierLabel = new Label();
    Label livesLabel = new Label();

    BorderPane mainPane;

    VBox pieceDisplays;

    Label highScoreLabel;



    Canvas timerBar;

    Multimedia multimedia;


    PieceBoard currentPieceDisplay = new PieceBoard();

    PieceBoard nextPieceDisplay = new PieceBoard();

    Label currentPieceLabel;

    Label nextPieceLabel;

    GameBoard board;

    HBox labels;


    int currentAimX;
    int currentAimY;
    /**
     * Create a new Single Player challenge scene
     *
     * @param gameWindow the Game Window
     */
    public ChallengeScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Challenge Scene");
    }

    /**
     * Build the Challenge window
     * Sets style and size of all visual components
     * Binds labels to the lives, multiplier, score and level values in the game scene
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());
        if(this.game == null) {
            setupGame();
        }

        root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

        var challengePane = new StackPane();
        challengePane.setMaxWidth(gameWindow.getWidth());
        challengePane.setMaxHeight(gameWindow.getHeight());
        challengePane.getStyleClass().add("challenge-background");
        root.getChildren().add(challengePane);
        mainPane = new BorderPane();
        challengePane.getChildren().add(mainPane);
        this.board = new GameBoard(game.getGrid(), gameWindow.getWidth() / 2, gameWindow.getWidth() / 2);
        board.getStyleClass().add("gameboard");
        mainPane.setCenter(board);

        //Handle block on gameboard grid being clicked
        game.setFailToPlaceListener(this::failedToPlace);
        game.setBlockClearedListener(this::blockCleared);
        game.setGameLoopListener(this::timerBar);
        game.setLifeLostListener(this::lifeLost);
        game.setGameOverListener(this::gameOver);
        board.setOnBlockClick(this::blockClicked);


        multimedia = new Multimedia();
        String musicFilePath = "src/main/resources/music/game.wav";
        multimedia.playBackgroundMusic(musicFilePath);
        logger.info("Challenge Music Playing");

        scoreLabel.textProperty().bind(Bindings.concat("Score:\n", game.getScoreProperty().asString()));
        scoreLabel.setPrefSize(160, 70);
        scoreLabel.getStyleClass().add("heading");

        levelLabel.textProperty().bind(Bindings.concat("Level:\n", game.getLevelProperty().asString()));
        levelLabel.setPrefSize(160, 70);
        levelLabel.getStyleClass().add("heading");

        multiplierLabel.textProperty().bind(Bindings.concat("Multiplier:\n", game.getMultiplierProperty().asString()));
        multiplierLabel.setPrefSize(160, 70);
        multiplierLabel.getStyleClass().add("heading");

        livesLabel.textProperty().bind(Bindings.concat("Lives:\n", game.getLivesProperty().asString()));
        livesLabel.setPrefSize(160, 70);
        livesLabel.getStyleClass().add("heading");

        int highScore;
        if(getHighScore() == -1){highScore = 5000;}
        else{
            highScore = getHighScore();
        }
        highScoreLabel = new Label("High Score:\n" + highScore);
        highScoreLabel.getStyleClass().add("heading");

        timerBar = new Canvas(800, 20);
        timerBar.getStyleClass().add("heading");


        game.getCurrentPieceObjectProperty().addListener((obs, oldPiece, newPiece) -> updateCurrentPieceDisplay(newPiece));
        game.getNextPieceObjectProperty().addListener((obs, oldPiece, newPiece) -> updateNextPieceDisplay(newPiece));


        mainPane.setCenter(board);
        labels = new HBox();
        labels.setAlignment(Pos.CENTER);
        labels.setPadding(new Insets(10, 10, 10, 10));
        labels.setSpacing(20);

        mainPane.setTop(labels);
        labels.getChildren().add(scoreLabel);
        labels.getChildren().add(levelLabel);
        labels.getChildren().add(multiplierLabel);
        labels.getChildren().add(livesLabel);

        mainPane.setBottom(timerBar);

        pieceDisplays = new VBox();
        pieceDisplays.setPadding(new Insets(10, 10, 10, 10));
        pieceDisplays.setSpacing(10);
        pieceDisplays.setAlignment(Pos.CENTER);
        mainPane.setRight(pieceDisplays);

        currentPieceDisplay = new PieceBoard();
        nextPieceDisplay = new PieceBoard();

        currentPieceLabel = new Label("Current Piece");
        currentPieceLabel.setPrefSize(160, 70);
        currentPieceLabel.getStyleClass().add("pieceDisplays");
        nextPieceLabel = new Label("Next Piece");
        nextPieceLabel.setPrefSize(160, 70);
        nextPieceLabel.getStyleClass().add("pieceDisplays");

        pieceDisplays.getChildren().add(currentPieceLabel);
        pieceDisplays.getChildren().add(currentPieceDisplay);
        pieceDisplays.getChildren().add(nextPieceLabel);
        pieceDisplays.getChildren().add(nextPieceDisplay);
        pieceDisplays.getChildren().add(highScoreLabel);



    }



    /**
     * Handle when a block is clicked
     *If left click, try to place
     * If right click, rotate piece
     * @param gameBlock the Game Block that was clocked
     */
    private void blockClicked(GameBlock gameBlock, MouseEvent event) {
        switch (event.getButton()) {
            case PRIMARY:
                game.blockClicked(gameBlock);
                break;
            case SECONDARY:
                game.rotateCurrentPieceRight();
                updateCurrentPieceDisplay(game.getCurrentPiece());
                multimedia.playAudio("src/main/resources/sounds/rotate.wav");
                break;
        }
    }


    /**
     * Setup the game object and model
     */
    public void setupGame() {
        logger.info("Starting a new challenge");

        //Start new game
        game = new Game(5, 5);
    }

    /**
     * Called from the multiplayerScene to make sure that the game field variable is a multiplayer game
     * @param game the multiplayer game setup in the multiplayerScene
     */
    public void setupGame(MultiplayerGame game) {
        logger.info("Starting a new challenge");

        //Start new game
        this.game = game;
    }

    /**
     * Initialise the scene and start the game
     * Set piece displays and currentAIM for keyboard
     */
    @Override
    public void initialise() {
        logger.info("Initialising Challenge");
        if (scene != null) {
            scene.setOnKeyPressed(this::handleKeyPress);
        }
        game.start();
        updateCurrentPieceDisplay(game.getCurrentPiece());
        updateNextPieceDisplay(game.getNextPiece());
        currentAimX = 0;
        currentAimY = 0;

    }

    /**
     * Handles what event should occur for each button pressed
     * @param keyEvent the key/button clicked
     */
    public void handleKeyPress(KeyEvent keyEvent) {
        switch (keyEvent.getCode()) {
            case ESCAPE:
                endGame(keyEvent);
                break;
            case ENTER, X:
                game.blockClicked(board.getBlock(currentAimX,currentAimY));
                break;
            case E,C,CLOSE_BRACKET:
                logger.info("Rotating current piece right");
                game.rotateCurrentPieceRight();
                updateCurrentPieceDisplay(game.getCurrentPiece());
                multimedia.playAudio("src/main/resources/sounds/rotate.wav");
                break;
            case Q,Z,OPEN_BRACKET:
                logger.info("Rotating current piece left");
                game.rotateCurrentPieceLeft();
                updateCurrentPieceDisplay(game.getCurrentPiece());
                multimedia.playAudio("src/main/resources/sounds/rotate.wav");
                break;
            case SPACE, R:
                game.swapPieces();
                multimedia.playAudio("src/main/resources/sounds/rotate.wav");
                break;
            case UP, W:
                currentAimMoveUp();
                break;
            case DOWN,S :
                currentAimMoveDown();
                break;
            case LEFT,A :
                currentAimMoveLeft();
                break;
            case RIGHT,D :
                currentAimMoveRight();
                break;
            case TAB :
               game.gameLoop();
               break;
            case T :
                openChat();
                break;
        }
    }



    /**
     * Overriden in the multiplayerScene which has a chat window
     */
    protected void openChat(){}

    /**
     * Moves the current block selected one to the right
     */
    private void currentAimMoveRight(){
        if(!(currentAimX >=4)){
            board.getBlock(currentAimX,currentAimY).paint();
            currentAimX++;
            changeHover();
        }
    }
    /**
     * Moves the current block selected one to the left
     */
    private void currentAimMoveLeft(){
        if(!(currentAimX <=0)){
            board.getBlock(currentAimX,currentAimY).paint();
            currentAimX--;
            changeHover();
        }
    }
    /**
     * Moves the current block selected once up
     */
    private void currentAimMoveUp(){
        if(!(currentAimY <=0)){
            board.getBlock(currentAimX,currentAimY).paint();
            currentAimY--;
            changeHover();
        }
    }
    /**
     * Moves the current block selected once down
     */
    private void currentAimMoveDown(){
        if(!(currentAimY >=4)){
            board.getBlock(currentAimX,currentAimY).paint();
            currentAimY++;
            changeHover();
        }
    }

    /**
     * Updates the hover animation showing the current aim
     */
    private void changeHover(){
        GameBlock gameBlock = board.getBlock(currentAimX,currentAimY);
        gameBlock.setHovering();
    }

    /**
     * Cleans up and switches to the scores scene when the game is over
     * @param game the game which just ended
     */
    public void gameOver(Game game){
        multimedia.stopMusic();
        scene.setOnKeyPressed(null);
        gameWindow.startScores(game);
        logger.info("Challenge Music Paused");
    }
    /**
     * Forcibly ends the game when ESC is pressed
     * @param keyEvent is the ESC key
     */
    private void endGame(KeyEvent keyEvent) {
        game.stopLoop();
        multimedia.stopMusic();
        gameWindow.startMenu();
    }


    /**
     * Updates the current piece display
     * @param piece current piece
     */
    protected void updateCurrentPieceDisplay(GamePiece piece) {
        currentPieceDisplay.displayPiece(piece);
    }

    /**
     * Updates the next piece display
     * @param piece next piece
     */
    protected void updateNextPieceDisplay(GamePiece piece) {
        nextPieceDisplay.displayPiece(piece);
    }



    /**
     * Handles a block being cleared, especially the animation
     * @param x coordinate of the block
     * @param y coordinate of the block
     */
    public void blockCleared(int x, int y) {
        board.clearBlock(x, y);
    }

    /**
     * Plays a life lost sound when called
     */
    public void lifeLost(){
        multimedia.playAudio("src/main/resources/sounds/lifelose.wav");
    }

    /**
     * Tries to read the current highest score on the machine
     * This is displayed on the UI
     * @return int highScore
     */
    private int getHighScore(){
        try {
            BufferedReader reader = Files.newBufferedReader(Paths.get("src/main/resources/IO/scores.txt"));
            int highScore = -1;
            String line;
            boolean firstLineRead = false;
            while ((line = reader.readLine()) != null && !firstLineRead) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    highScore = Integer.parseInt(parts[1].trim());
                    firstLineRead = true;
                }
            }
            return highScore;
        } catch (IOException e) {
           return -1;
        }
    }

    /**
     * Plays the place audio or buzzer audio depending on whether or not the block could be placed
     * @param failedToPlace T/F whether or not the block successfully placed after a click
     */
    public void failedToPlace(boolean failedToPlace) {
        if (failedToPlace) {
            multimedia.playAudio("src/main/resources/sounds/fail.wav");
        } else {
            multimedia.playAudio("src/main/resources/sounds/place.wav");
        }
    }

    /**
     * Draws the timer bar which changes length depending on how much time is left
     * It is painted more red the closer it gets to 0
     * @param progress the current progress of the timer
     */

    public void timerBar(double progress){
        Platform.runLater(() -> {
            GraphicsContext gc = timerBar.getGraphicsContext2D();
            gc.clearRect(0,0,900,200);
            Color fillColor = Color.rgb(250,(int)(250*progress),0);
            gc.setFill(fillColor);
            double fillAmount = progress*800;
            gc.fillRect(0,0, fillAmount, 20);
        });


    }


}



