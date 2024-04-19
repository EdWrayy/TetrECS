package uk.ac.soton.comp1206.scene;

import javafx.animation.Animation;
import javafx.animation.RotateTransition;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

import java.net.URL;

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
        var titleWrapper = new HBox();
        titleWrapper.setAlignment(Pos.CENTER);
        mainPane.setCenter(titleWrapper);

        URL imageUrl = getClass().getResource("/images/TetrECS.png");
        Image titleImage = new Image(imageUrl.toString());
        ImageView title = new ImageView(titleImage);
        title.setFitHeight(300);
        title.setFitWidth(700);
        title.setPreserveRatio(true);
        titleWrapper.getChildren().add(title);

        RotateTransition rotateTransition = new RotateTransition(Duration.seconds(3), titleWrapper);
        rotateTransition.setFromAngle(-10);
        rotateTransition.setToAngle(10);
        rotateTransition.setAutoReverse(true);
        rotateTransition.setCycleCount(Animation.INDEFINITE);


        rotateTransition.play();

        multimedia = new Multimedia();
        multimedia.playBackgroundMusic("src/main/resources/music/menu.mp3");
        logger.info("Menu Music Playing");

        VBox buttonLayout = new VBox(5); // 10 is the spacing between elements in the VBox
        buttonLayout.setPadding(new Insets(10,10,10,10));
        buttonLayout.setAlignment(Pos.CENTER); // Center align the VBox contents
        mainPane.setBottom(buttonLayout);

        //For now, let us just add a button that starts the game. I'm sure you'll do something way better.
        var playButton = new Button("Single Player");
        playButton.getStyleClass().add("menuItem");
        //Bind the button action to the startGame method in the menu
        playButton.setOnAction(this::startGame);
        playButton.setOnMouseEntered(e -> playButton.getStyleClass().add("menuItem:hover"));
        playButton.setOnMouseExited(e -> playButton.getStyleClass().add("menuItem"));

        var instructionsButton = new Button("How To Play");
        instructionsButton.setOnAction(this::showInstructions);
        instructionsButton.getStyleClass().add("menuItem");
        instructionsButton.setOnMouseEntered(e -> instructionsButton.getStyleClass().add("menuItem:hover"));
        instructionsButton.setOnMouseExited(e -> instructionsButton.getStyleClass().add("menuItem"));

        var settingsButton = new Button("Settings");
        settingsButton.setOnAction(this::settings);
        settingsButton.getStyleClass().add("menuItem");
        settingsButton.setOnMouseEntered(e -> settingsButton.getStyleClass().add("menuItem:hover"));
        settingsButton.setOnMouseExited(e -> settingsButton.getStyleClass().add("menuItem"));

        var multiPlayerButton = new Button("Multi Player");
        multiPlayerButton.getStyleClass().add("menuItem");
        multiPlayerButton.setOnAction(this::startMultiPlayer);
        multiPlayerButton.setOnMouseEntered(e ->multiPlayerButton.getStyleClass().add("menuItem:hover"));
        multiPlayerButton.setOnMouseExited(e -> multiPlayerButton.getStyleClass().add("menuItem"));

        var exitButton = new Button("Exit");
        exitButton.getStyleClass().add("menuItem");
        exitButton.setOnAction(this::exit);
        exitButton.setOnMouseEntered(e ->exitButton.getStyleClass().add("menuItem:hover"));
        exitButton.setOnMouseExited(e -> exitButton.getStyleClass().add("menuItem"));


        buttonLayout.getChildren().addAll(playButton, multiPlayerButton, instructionsButton, settingsButton, exitButton);


    }




    /**
     * Initialise the menu
     */
    @Override
    public void initialise() {
        if (scene != null) {
            scene.setOnKeyPressed(this::handleKeyPress);
        }
    }

    /**
     * Manage user input if necessary
     * @param keyEvent input
     */

    public void handleKeyPress(KeyEvent keyEvent){
        switch(keyEvent.getCode()) {
            case ESCAPE:
                logger.info("ESC pressed");
                break;
        }
    }

    /**
     * Handle when the Start Game button is pressed
     * @param event button click
     */
    private void startGame(ActionEvent event) {
        multimedia.stopMusic();
        gameWindow.startChallenge();
        logger.info("Menu Music Paused");
    }

    /**
     * Change to the instructions menu
     * @param event button click
     */
    private void showInstructions(ActionEvent event) {
        multimedia.stopMusic();
        gameWindow.startInstructions();
        logger.info("Menu Music Paused");
    }

    /**
     * Change to the lobby scene
     * @param event button click
     */
    private void startMultiPlayer(ActionEvent event){
        multimedia.stopMusic();
        gameWindow.startMultiPlayer();
        logger.info("Menu Music Paused");
    }

    /**
     * Terminate the programme
     * @param event button click
     */
    private void exit(ActionEvent event){
        System.exit(0);
    }

    /**
     * Change to the settings menu
     * @param event buttonClick
     */
    public void settings(ActionEvent event){
        multimedia.stopMusic();
        gameWindow.startSettings();
    }
}
