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


    public void failedToPlace(boolean failedToPlace) {
        if (failedToPlace) {
            multimedia.playAudio("src/main/resources/sounds/fail.wav");
        } else {
            multimedia.playAudio("src/main/resources/sounds/place.wav");
        }
    }

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


    private static final Logger logger = LogManager.getLogger(MenuScene.class);
    protected Game game;

    Label scoreLabel = new Label();
    Label levelLabel = new Label();
    Label multiplierLabel = new Label();
    Label livesLabel = new Label();



    Canvas timerBar;

    Multimedia multimedia;


    PieceBoard currentPieceDisplay = new PieceBoard();

    PieceBoard nextPieceDisplay = new PieceBoard();

    Label currentPieceLabel;

    Label nextPieceLabel;

    GameBoard board;

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
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        setupGame();

        root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

        var challengePane = new StackPane();
        challengePane.setMaxWidth(gameWindow.getWidth());
        challengePane.setMaxHeight(gameWindow.getHeight());
        challengePane.getStyleClass().add("menu-background");
        root.getChildren().add(challengePane);

        var mainPane = new BorderPane();
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
        Label highScoreLabel = new Label("High Score:\n" + highScore);
        highScoreLabel.getStyleClass().add("heading");

        timerBar = new Canvas(800, 20);
        timerBar.getStyleClass().add("heading");


        game.getCurrentPieceObjectProperty().addListener((obs, oldPiece, newPiece) -> updateCurrentPieceDisplay(newPiece));
        game.getNextPieceObjectProperty().addListener((obs, oldPiece, newPiece) -> updateNextPieceDisplay(newPiece));


        mainPane.setCenter(board);
        HBox labels = new HBox();
        labels.setAlignment(Pos.CENTER);
        labels.setPadding(new Insets(10, 10, 10, 10));
        labels.setSpacing(20);

        mainPane.setTop(labels);
        labels.getChildren().add(scoreLabel);
        labels.getChildren().add(levelLabel);
        labels.getChildren().add(multiplierLabel);
        labels.getChildren().add(livesLabel);

        mainPane.setBottom(timerBar);

        VBox pieceDisplays = new VBox();
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
     *
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
     * Make sure the score, level, multiplier and lives labels are binded to the actual data.
     * Start playing the game music
     */
    public void setupGame() {
        logger.info("Starting a new challenge");

        //Start new game
        game = new Game(5, 5);
    }

    /**
     * Initialise the scene and start the game
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

    public void handleKeyPress(KeyEvent keyEvent) {
        switch (keyEvent.getCode()) {
            case ESCAPE:
                endGame(keyEvent);
                break;
            case ENTER, X:
                game.blockClicked(board.getBlock(currentAimX,currentAimY));
                break;
            case E:
                logger.info("Rotating current piece right");
                game.rotateCurrentPieceRight();
                updateCurrentPieceDisplay(game.getCurrentPiece());
                multimedia.playAudio("src/main/resources/sounds/rotate.wav");
                break;
            case Q:
                logger.info("Rotating current piece left");
                game.rotateCurrentPieceLeft();
                updateCurrentPieceDisplay(game.getCurrentPiece());
                multimedia.playAudio("src/main/resources/sounds/rotate.wav");
                break;
            case SPACE:
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
        }
    }

    private void currentAimMoveRight(){
        if(!(currentAimX >=4)){
            board.getBlock(currentAimX,currentAimY).paint();
            currentAimX++;
            changeHover();
        }
    }
    private void currentAimMoveLeft(){
        if(!(currentAimX <=0)){
            board.getBlock(currentAimX,currentAimY).paint();
            currentAimX--;
            changeHover();
        }
    }
    private void currentAimMoveUp(){
        if(!(currentAimY <=0)){
            board.getBlock(currentAimX,currentAimY).paint();
            currentAimY--;
            changeHover();
        }
    }
    private void currentAimMoveDown(){
        if(!(currentAimY >=4)){
            board.getBlock(currentAimX,currentAimY).paint();
            currentAimY++;
            changeHover();
        }
    }

    private void changeHover(){
        GameBlock gameBlock = board.getBlock(currentAimX,currentAimY);
        gameBlock.setHovering();
    }

    public void gameOver(Game game){
        multimedia.stopMusic();
        gameWindow.startScores(game);
        logger.info("Challenge Music Paused");
        scene.setOnKeyPressed(null);
    }
    private void endGame(KeyEvent keyEvent) {
        game.stopLoop();
        gameWindow.startMenu();
    }


    private void updateCurrentPieceDisplay(GamePiece piece) {
        currentPieceDisplay.displayPiece(piece);
    }

    private void updateNextPieceDisplay(GamePiece piece) {
        nextPieceDisplay.displayPiece(piece);
    }



    public void blockCleared(int x, int y) {
        GameBlock fadeBlock  = board.getBlock(x,y);
        board.clearBlock(x, y);
    }

    public void lifeLost(){
        multimedia.playAudio("src/main/resources/sounds/lifelose.wav");
    }

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



}



