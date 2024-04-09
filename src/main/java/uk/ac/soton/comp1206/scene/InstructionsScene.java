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
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

import java.net.URL;

public class InstructionsScene extends BaseScene{



    /**
     * Create a new scene, passing in the GameWindow the scene will be displayed in
     *
     * @param gameWindow the game window
     */
    public InstructionsScene(GameWindow gameWindow) {
        super(gameWindow);
    }


    @Override
    public void initialise() {
        Scene scene = root.getScene();
        if (scene!=null){
            scene.setOnKeyPressed(this::handleKeyPress);
        }
    }

    /**Setup everything for the instruction scene*/
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
        instructions.setSpacing(10);
        instructions.setPadding(new Insets(10,10,10,10));
        instructions.setAlignment(Pos.CENTER);
        mainPane.setTop(instructions);

        Label title = new Label("How to Play");
        title.getStyleClass().add("title");
        Label description = new Label("TetrECS is a fast paced gravity free block game where you must fit pieces \n into the grid before the timer runs out. If unsuccessful you will \n lose a life, lose 3 lives and you are out!" );
        description.getStyleClass().add("instructions");
        Label piecesLabel = new Label("Game Pieces");
        piecesLabel.getStyleClass().add("heading");
        URL imageUrl = getClass().getResource("/images/Instructions.png");
        Image instructionsImage = new Image(imageUrl.toString());
        ImageView instructionsImageView = new ImageView(instructionsImage);
        instructionsImageView.setFitHeight(400);
        instructionsImageView.setFitWidth(800);
        instructionsImageView.setPreserveRatio(true);




        GridPane piecesGrid = new GridPane();
        piecesGrid.setPadding(new Insets(10, 10, 10, 10));
        piecesGrid.setVgap(5);
        piecesGrid.setHgap(5);


        instructions.getChildren().add(title);
        instructions.getChildren().add(description);
        instructions.getChildren().add(instructionsImageView);
        instructions.getChildren().add(piecesLabel);
        instructions.getChildren().add(piecesGrid);



        /**Adding the different pieces to the gridpane*/
        PieceBoard pieceBoard0 = new PieceBoard();
        GamePiece gamePiece0 = GamePiece.createPiece(0);

        PieceBoard pieceBoard1 = new PieceBoard();
        GamePiece gamePiece1 = GamePiece.createPiece(1);

        PieceBoard pieceBoard2 = new PieceBoard();
        GamePiece gamePiece2 = GamePiece.createPiece(2);

        PieceBoard pieceBoard3 = new PieceBoard();
        GamePiece gamePiece3 = GamePiece.createPiece(3);

        PieceBoard pieceBoard4 = new PieceBoard();
        GamePiece gamePiece4 = GamePiece.createPiece(4);

        PieceBoard pieceBoard5 = new PieceBoard();
        GamePiece gamePiece5 = GamePiece.createPiece(5);

        PieceBoard pieceBoard6 = new PieceBoard();
        GamePiece gamePiece6 = GamePiece.createPiece(6);

        PieceBoard pieceBoard7 = new PieceBoard();
        GamePiece gamePiece7 = GamePiece.createPiece(7);

        PieceBoard pieceBoard8 = new PieceBoard();
        GamePiece gamePiece8 = GamePiece.createPiece(8);

        PieceBoard pieceBoard9 = new PieceBoard();
        GamePiece gamePiece9 = GamePiece.createPiece(9);

        PieceBoard pieceBoard10 = new PieceBoard();
        GamePiece gamePiece10 = GamePiece.createPiece(10);

        PieceBoard pieceBoard11 = new PieceBoard();
        GamePiece gamePiece11 = GamePiece.createPiece(11);

        PieceBoard pieceBoard12 = new PieceBoard();
        GamePiece gamePiece12 = GamePiece.createPiece(12);

        PieceBoard pieceBoard13 = new PieceBoard();
        GamePiece gamePiece13 = GamePiece.createPiece(13);

        PieceBoard pieceBoard14 = new PieceBoard();
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




    public void handleKeyPress(KeyEvent keyEvent){
        switch(keyEvent.getCode()) {
            case ESCAPE:
                showMenu(keyEvent);
                break;
        }
    }

    private void showMenu(KeyEvent event) {
        gameWindow.startMenu();
    }

}
