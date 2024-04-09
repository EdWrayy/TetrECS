package uk.ac.soton.comp1206.scene;

import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
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


    private static final Logger logger = LogManager.getLogger(MenuScene.class);
    protected Game game;

    Label scoreLabel = new Label();
    Label levelLabel = new Label();
    Label multiplierLabel = new Label();
    Label livesLabel = new Label();

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
        board.setOnBlockClick(this::blockClicked);


        multimedia = new Multimedia();
        String musicFilePath = "src/main/resources/music/game.wav";
        multimedia.playBackgroundMusic(musicFilePath);

        scoreLabel.textProperty().bind(Bindings.concat("Score:\n", game.getScoreProperty().asString()));
        scoreLabel.setPrefSize(130, 70);
        scoreLabel.getStyleClass().add("score");

        levelLabel.textProperty().bind(Bindings.concat("Level:\n", game.getLevelProperty().asString()));
        levelLabel.setPrefSize(130, 70);
        levelLabel.getStyleClass().add("level");

        multiplierLabel.textProperty().bind(Bindings.concat("Multiplier:\n", game.getMultiplierProperty().asString()));
        multiplierLabel.setPrefSize(130, 70);
        multiplierLabel.getStyleClass().add("score");

        livesLabel.textProperty().bind(Bindings.concat("Lives:\n", game.getLivesProperty().asString()));
        livesLabel.setPrefSize(130, 70);
        livesLabel.getStyleClass().add("lives");

        game.getCurrentPieceObjectProperty().addListener((obs, oldPiece, newPiece) -> updateCurrentPieceDisplay(newPiece));
        game.getNextPieceObjectProperty().addListener((obs, oldPiece, newPiece) -> updateNextPieceDisplay(newPiece));


        mainPane.setCenter(board);
        HBox labels = new HBox();
        labels.setPadding(new Insets(10, 10, 10, 10));
        labels.setSpacing(10);

        mainPane.setTop(labels);
        labels.getChildren().add(scoreLabel);
        labels.getChildren().add(levelLabel);
        labels.getChildren().add(multiplierLabel);
        labels.getChildren().add(livesLabel);

        VBox pieceDisplays = new VBox();
        pieceDisplays.setPadding(new Insets(10, 10, 10, 10));
        pieceDisplays.setSpacing(10);
        mainPane.setRight(pieceDisplays);

        currentPieceDisplay = new PieceBoard();
        nextPieceDisplay = new PieceBoard();

        currentPieceLabel = new Label("Current Piece");
        currentPieceLabel.getStyleClass().add("lives");
        nextPieceLabel = new Label("Next Piece");
        nextPieceLabel.getStyleClass().add("lives");

        pieceDisplays.getChildren().add(currentPieceLabel);
        pieceDisplays.getChildren().add(currentPieceDisplay);
        pieceDisplays.getChildren().add(nextPieceLabel);
        pieceDisplays.getChildren().add(nextPieceDisplay);



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
                showMenu(keyEvent);
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


    private void showMenu(KeyEvent keyEvent) {
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
}



