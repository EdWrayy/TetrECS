package uk.ac.soton.comp1206.scene;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.game.Settings;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SettingsScene extends BaseScene{


    private static final Logger logger = LogManager.getLogger(SettingsScene.class);
    
    Multimedia multimedia;
   
    /**
     * Create a new scene, passing in the GameWindow the scene will be displayed in
     *
     * @param gameWindow the game window
     */
    private Button musicButton;
    private Button soundButton;

    public SettingsScene(GameWindow gameWindow) {
        super(gameWindow);
    }

    @Override
    public void initialise() {
        scene.setOnKeyPressed(this::handleKeyPress);
    }

    @Override
    public void build() {
        root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());
        StackPane settingsPane = new StackPane();
        settingsPane.setMaxWidth(gameWindow.getWidth());
        settingsPane.setMaxHeight(gameWindow.getHeight());
        settingsPane.getStyleClass().add("menu-background");
        root.getChildren().add(settingsPane);
        var mainPane = new BorderPane();
        settingsPane.getChildren().add(mainPane);
        
        VBox settingsBox = new VBox();
        mainPane.setCenter(settingsBox);
        settingsBox.setAlignment(Pos.CENTER);
        
        musicButton = new Button("Toggle Music");
        soundButton = new Button("Toggle Sound");

        settingsBox.getChildren().addAll(musicButton,soundButton);

       musicButton.setOnAction(this::toggleMusic);
       musicButton.getStyleClass().add("menuItem");
       musicButton.setOnMouseEntered(e ->musicButton.getStyleClass().add("menuItem:hover"));
       musicButton.setOnMouseExited(e ->musicButton.getStyleClass().add("menuItem"));

       soundButton.setOnAction(this::toggleSound);
       soundButton.getStyleClass().add("menuItem");
       soundButton.setOnMouseEntered(e ->soundButton.getStyleClass().add("menuItem:hover"));
       soundButton.setOnMouseExited(e ->soundButton.getStyleClass().add("menuItem"));

       multimedia = new Multimedia();
       multimedia.playBackgroundMusic("src/main/resources/music/menu.mp3");

       if(Settings.soundActive){
           soundButton.setText("Toggle Sound: ON");
       }
       else{
           soundButton.setText("Toggle Sound: OFF");
       }

        if(Settings.musicActive){
            musicButton.setText("Toggle Music: ON");
        }
        else{
            musicButton.setText("Toggle Music: OFF");
        }

    }


    private void toggleMusic(ActionEvent event){
        if(Settings.musicActive) {
            Platform.runLater(() -> {
                multimedia.stopMusic();
                Settings.musicActive = false;
                musicButton.setText("Toggle Music: OFF");
            });

            try {
                FileWriter writer = new FileWriter("src/main/resources/IO/musicActive.txt");
                writer.write("false");
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            Settings.musicActive = true;
            musicButton.setText("Toggle Music: ON");
            multimedia.playBackgroundMusic("src/main/resources/music/menu.mp3");
            try {
                FileWriter writer = new FileWriter("src/main/resources/IO/musicActive.txt");
                writer.write("true");
                writer.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void toggleSound(ActionEvent event){
        if(Settings.soundActive) {
            Settings.soundActive = false;
            soundButton.setText("Toggle Sound: OFF");
            try {
                FileWriter writer = new FileWriter("src/main/resources/IO/soundActive.txt");
                writer.write("false");
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            Settings.soundActive = true;
            soundButton.setText("Toggle Sound: ON");
            try {
                FileWriter writer = new FileWriter("src/main/resources/IO/soundActive.txt");
                writer.write("true");
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void handleKeyPress(KeyEvent keyEvent) {
        logger.info("key pressed");
        switch(keyEvent.getCode()) {
            case ESCAPE:
                logger.info("ESC pressed");
                multimedia.stopMusic();
                gameWindow.startMenu();
                break;
        }
    }



}
