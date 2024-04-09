package uk.ac.soton.comp1206.scene;

import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * The main menu of the game. Provides a gateway to the rest of the game.
 */
public class MenuScene extends BaseScene {

    Multimedia multimedia;

    private static final Logger logger = LogManager.getLogger(MenuScene.class);

    /**
     * Create a new menu scene
     * @param gameWindow the Game Window this will be displayed in
     */
    public MenuScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Menu Scene");
    }

    /**
     * Build the menu layout
     * Starts playing the menu music
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

        var menuPane = new StackPane();
        menuPane.setMaxWidth(gameWindow.getWidth());
        menuPane.setMaxHeight(gameWindow.getHeight());
        menuPane.getStyleClass().add("menu-background");
        root.getChildren().add(menuPane);

        var mainPane = new BorderPane();
        menuPane.getChildren().add(mainPane);

        //Awful title
        var title = new Text("TetrECS");
        title.getStyleClass().add("title");
        mainPane.setTop(title);

        multimedia = new Multimedia();
        multimedia.playBackgroundMusic("src/main/resources/music/menu.mp3");

        VBox buttonLayout = new VBox(10); // 10 is the spacing between elements in the VBox
        buttonLayout.setAlignment(Pos.CENTER); // Center align the VBox contents
        mainPane.setCenter(buttonLayout);

        //For now, let us just add a button that starts the game. I'm sure you'll do something way better.
        var playButton = new Button("Play");
        playButton.getStyleClass().add("heading");
        //Bind the button action to the startGame method in the menu
        playButton.setOnAction(this::startGame);

        var instructionsButton = new Button("Instructions");
        instructionsButton.setOnAction(this::showInstructions);
        instructionsButton.getStyleClass().add("heading");


        buttonLayout.getChildren().addAll(playButton, instructionsButton);
    }



    /**
     * Initialise the menu
     */
    @Override
    public void initialise() {

    }

    /**
     * Handle when the Start Game button is pressed
     *
     * @param event event
     */
    private void startGame(ActionEvent event) {
        gameWindow.startChallenge();
    }

    private void showInstructions(ActionEvent event) {
        gameWindow.startInstructions();
    }

}
