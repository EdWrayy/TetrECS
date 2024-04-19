package uk.ac.soton.comp1206.scene;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.ImageInput;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.InstructionsPieceBoard;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

import java.net.URL;

/**
 * A simple UI layout which describes how to play the game
 * Not interactive at all
 */


public class InstructionsScene extends BaseScene{

    Multimedia multimedia;

    private static final Logger logger = LogManager.getLogger(InstructionsScene.class);

    /**
     * Create a new scene, passing in the GameWindow the scene will be displayed in
     *
     * @param gameWindow the game window
     */
    public InstructionsScene(GameWindow gameWindow) {
        super(gameWindow);
    }


    /**
     * Sets handleKeyPress
     */
    @Override
    public void initialise() {
        Scene scene = root.getScene();
        if (scene!=null){
            scene.setOnKeyPressed(this::handleKeyPress);
        }
    }

    /**
     * Setup everything for the instruction scene
     * Mainly just Labels and images
     * There is a dynamically generated array of pieceboards showing all the possible pieces
     */
    @Override
    public void build() {
        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());
        var instructionsPane = new StackPane();
        instructionsPane.setMaxWidth(gameWindow.getWidth());
        instructionsPane.setMaxHeight(gameWindow.getHeight());
        instructionsPane.getStyleClass().add("menu-background");
        root.getChildren().add(instructionsPane);

        var mainPane = new BorderPane();
        instructionsPane.getChildren().add(mainPane);

        VBox instructions = new VBox();
        instructions.setSpacing(5);
        instructions.setPadding(new Insets(5,5,5,5));
        instructions.setAlignment(Pos.CENTER);
        mainPane.setTop(instructions);

        multimedia = new Multimedia();
        multimedia.playBackgroundMusic("src/main/resources/music/menu.mp3");
        logger.info("Instructions Music Playing");

        Label title = new Label("How to Play");
        title.getStyleClass().add("title");
        Label description = new Label("TetrECS is a fast paced gravity free block game where you must fit pieces into the grid before the timer runs out. \n If unsuccessful you will lose a life, lose 3 lives and you are out!" );
        description.getStyleClass().add("instructions");
        Label piecesLabel = new Label("Game Pieces");
        piecesLabel.getStyleClass().add("heading");
        URL imageUrl = getClass().getResource("/images/Instructions.png");
        Image instructionsImage = new Image(imageUrl.toString());
        ImageView instructionsImageView = new ImageView(instructionsImage);
        instructionsImageView.setFitHeight(290);
        instructionsImageView.setFitWidth(600);
        instructionsImageView.setPreserveRatio(true);




        GridPane piecesGrid = new GridPane();
        piecesGrid.setAlignment(Pos.CENTER);
        piecesGrid.setPadding(new Insets(10, 10, 10, 10));
        piecesGrid.setVgap(2);
        piecesGrid.setHgap(2);


        instructions.getChildren().add(title);
        instructions.getChildren().add(description);
        instructions.getChildren().add(instructionsImageView);
        instructions.getChildren().add(piecesLabel);
        instructions.getChildren().add(piecesGrid);



        /**Adding the different pieces to the gridpane*/
        InstructionsPieceBoard pieceBoard0 = new InstructionsPieceBoard();
        GamePiece gamePiece0 = GamePiece.createPiece(0);

        InstructionsPieceBoard pieceBoard1 = new InstructionsPieceBoard();
        GamePiece gamePiece1 = GamePiece.createPiece(1);

        InstructionsPieceBoard pieceBoard2 = new InstructionsPieceBoard();
        GamePiece gamePiece2 = GamePiece.createPiece(2);

        InstructionsPieceBoard pieceBoard3 = new InstructionsPieceBoard();
        GamePiece gamePiece3 = GamePiece.createPiece(3);

        InstructionsPieceBoard pieceBoard4 = new InstructionsPieceBoard();
        GamePiece gamePiece4 = GamePiece.createPiece(4);

        InstructionsPieceBoard pieceBoard5 = new InstructionsPieceBoard();
        GamePiece gamePiece5 = GamePiece.createPiece(5);

        InstructionsPieceBoard pieceBoard6 = new InstructionsPieceBoard();
        GamePiece gamePiece6 = GamePiece.createPiece(6);

        InstructionsPieceBoard pieceBoard7 = new InstructionsPieceBoard();
        GamePiece gamePiece7 = GamePiece.createPiece(7);

        InstructionsPieceBoard pieceBoard8 = new InstructionsPieceBoard();
        GamePiece gamePiece8 = GamePiece.createPiece(8);

        InstructionsPieceBoard pieceBoard9 = new InstructionsPieceBoard();
        GamePiece gamePiece9 = GamePiece.createPiece(9);

        InstructionsPieceBoard pieceBoard10 = new InstructionsPieceBoard();
        GamePiece gamePiece10 = GamePiece.createPiece(10);

        InstructionsPieceBoard pieceBoard11 = new InstructionsPieceBoard();
        GamePiece gamePiece11 = GamePiece.createPiece(11);

        InstructionsPieceBoard pieceBoard12 = new InstructionsPieceBoard();
        GamePiece gamePiece12 = GamePiece.createPiece(12);

        InstructionsPieceBoard pieceBoard13 = new InstructionsPieceBoard();
        GamePiece gamePiece13 = GamePiece.createPiece(13);

        InstructionsPieceBoard pieceBoard14 = new InstructionsPieceBoard();
        GamePiece gamePiece14 = GamePiece.createPiece(14);


        piecesGrid.add(pieceBoard0.displayPiece(gamePiece0), 0, 0);
        piecesGrid.add(pieceBoard1.displayPiece(gamePiece1), 1, 0);
        piecesGrid.add(pieceBoard2.displayPiece(gamePiece2), 2, 0);
        piecesGrid.add(pieceBoard3.displayPiece(gamePiece3), 3, 0);
        piecesGrid.add(pieceBoard4.displayPiece(gamePiece4), 4, 0);
        piecesGrid.add(pieceBoard5.displayPiece(gamePiece5), 0, 1);
        piecesGrid.add(pieceBoard6.displayPiece(gamePiece6), 1, 1);
        piecesGrid.add(pieceBoard7.displayPiece(gamePiece7), 2, 1);
        piecesGrid.add(pieceBoard8.displayPiece(gamePiece8), 3, 1);
        piecesGrid.add(pieceBoard9.displayPiece(gamePiece9), 4, 1);
        piecesGrid.add(pieceBoard10.displayPiece(gamePiece10), 0, 2);
        piecesGrid.add(pieceBoard11.displayPiece(gamePiece11), 1, 2);
        piecesGrid.add(pieceBoard12.displayPiece(gamePiece12), 2, 2);
        piecesGrid.add(pieceBoard13.displayPiece(gamePiece13), 3, 2);
        piecesGrid.add(pieceBoard14.displayPiece(gamePiece14), 4, 2);


    }


    /**
     * If the user presses ESC we exit and load the menu screen
     * @param keyEvent keyboard input
     */
    public void handleKeyPress(KeyEvent keyEvent){
        switch(keyEvent.getCode()) {
            case ESCAPE:
                showMenu(keyEvent);
                break;
        }
    }

    /**
     * Handles switching back to the menu scene
     * @param event ESC key
     */
    private void showMenu(KeyEvent event) {
        multimedia.stopMusic();
        gameWindow.startMenu();
        logger.info("Instructions Music Stopped");
    }

}
